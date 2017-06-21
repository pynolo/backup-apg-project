package it.giunti.apg.server.services;

import it.giunti.apg.client.services.ComunicazioniService;
import it.giunti.apg.core.SerializationUtil;
import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.business.ComunicazioniBusiness;
import it.giunti.apg.core.business.FileFormatComunicazioni;
import it.giunti.apg.core.persistence.ComunicazioniDao;
import it.giunti.apg.core.persistence.EvasioniComunicazioniDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.ModelliBollettiniDao;
import it.giunti.apg.core.persistence.ModelliEmailDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.EmailConstants;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.model.Comunicazioni;
import it.giunti.apg.shared.model.EvasioniComunicazioni;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.ModelliBollettini;
import it.giunti.apg.shared.model.ModelliEmail;
import it.giunti.apg.shared.model.OpzioniIstanzeAbbonamenti;
import it.giunti.apg.shared.model.OpzioniListini;
import it.giunti.apg.shared.model.Periodici;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ComunicazioniServiceImpl extends RemoteServiceServlet implements ComunicazioniService  {
	private static final long serialVersionUID = 5100726070121536041L;

	private static final Logger LOG = LoggerFactory.getLogger(ComunicazioniServiceImpl.class);
	
	@Override
	public Comunicazioni createComunicazione(Integer idPeriodico) throws BusinessException {
		Comunicazioni result = new Comunicazioni();
		Session ses = SessionFactory.getSession();
		try {
			Periodici periodico = GenericDao.findById(ses, Periodici.class, idPeriodico);
			result.setPeriodico(periodico);
			result.setSoloNonPagati(false);
			result.setSoloPiuCopie(false);
			result.setSoloUnaCopia(false);
			result.setNumeriDaInizioOFine(0);
			result.setIdBandella(0);
			result.setIdTipoAttivazione(AppConstants.COMUN_ATTIVAZ_PER_STATUS);
			result.setIdTipoMedia(AppConstants.COMUN_MEDIA_BOLLETTINO);
			result.setIdTipoDestinatario(AppConstants.DEST_PAGANTE);
			result.setTipiAbbonamentoList("");
			Calendar cal = new GregorianCalendar();
			cal.add(Calendar.MONTH, -3);
			result.setDataInizio(cal.getTime());
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}

	@Override
	public Comunicazioni findComunicazioneById(Integer idBandella)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		Comunicazioni result = null;
		try {
			result = GenericDao.findById(ses, Comunicazioni.class, idBandella);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (result != null) {
			return result;
		}
		throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	}

	@Override
	public List<Comunicazioni> findComunicazioniByPeriodico(Integer idPeriodico,
			Date dt, int offset, int pageSize) throws BusinessException,
			EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<Comunicazioni> result = null;
		try {
			result = new ComunicazioniDao().findComunicazioniByPeriodico(ses, idPeriodico, dt, offset, pageSize);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (result != null) {
			if (result.size() > 0) {
				return result;
			}
		}
		throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	}

	@Override
	public List<Comunicazioni> findComunicazioniByTipoAbb(Integer idTipoAbb,
			Date dt, int offset, int pageSize) throws BusinessException,
			EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<Comunicazioni> result = null;
		try {
			result = new ComunicazioniDao().findComunicazioniByTipoAbb(ses, idTipoAbb, dt, offset, pageSize);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (result != null) {
			if (result.size() > 0) {
				return result;
			}
		}
		throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	}
	
	@Override
	public Integer saveOrUpdateComunicazione(Comunicazioni item) throws BusinessException {
		Session ses = SessionFactory.getSession();
		Integer idBnd = null;
		Transaction trx = ses.beginTransaction();
		try {
			Integer idPeriodico = Integer.valueOf(item.getIdPeriodicoT());
			Periodici periodico = GenericDao.findById(ses, Periodici.class, idPeriodico);
			item.setPeriodico(periodico);
			//Modello bollettino
			item.setModelloBollettino(null);
			if (item.getIdModelloBollettinoT() != null) {
				if (!item.getIdModelloBollettinoT().equals(AppConstants.SELECT_EMPTY_LABEL)) {
					Integer idBolMod = Integer.valueOf(item.getIdModelloBollettinoT());
					ModelliBollettini bolMod = GenericDao.findById(ses, ModelliBollettini.class, idBolMod);
					item.setModelloBollettino(bolMod);
				}
			}
			//Modello email
			item.setModelloEmail(null);
			if (item.getIdModelloEmailT() != null) {
				if (!item.getIdModelloEmailT().equals(AppConstants.SELECT_EMPTY_LABEL)) {
					Integer idModEmail = Integer.valueOf(item.getIdModelloEmailT());
					ModelliEmail modEmail = GenericDao.findById(ses, ModelliEmail.class, idModEmail);
					item.setModelloEmail(modEmail);
				}
			}
			//Update or save
			if (item.getId() != null) {
				new ComunicazioniDao().update(ses, item);
				idBnd = item.getId();
			} else {
				//salva
				idBnd = (Integer) new ComunicazioniDao().save(ses, item);
			}
			trx.commit();
		} catch (HibernateException e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return idBnd;
	}
	
	@Override
	public List<Comunicazioni> deleteComunicazione(Integer idCom, int pageSize) throws BusinessException, EmptyResultException {
		Integer idPeriodico = null;
		Session ses = SessionFactory.getSession();
		Transaction trx = ses.beginTransaction();
		try {
			Comunicazioni b = GenericDao.findById(ses, Comunicazioni.class, idCom);
			idPeriodico = b.getPeriodico().getId();
			new ComunicazioniDao().delete(ses, b);
			trx.commit();
		} catch (HibernateException e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return findComunicazioniByPeriodico(idPeriodico, new Date(), 0, pageSize);
	}

	@Override
	public List<EvasioniComunicazioni> findEvasioniComunicazioniByIstanza(
			Integer idIstanza) throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<EvasioniComunicazioni> result = null;
		try {
			result = new EvasioniComunicazioniDao().findByIstanza(ses, idIstanza);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (result != null) {
			if (result.size() > 0) {
				result = SerializationUtil.makeSerializable(result);
				return result;
			}
		}
		throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	}
	

	@Override
	public List<EvasioniComunicazioni> deleteEvasioneComunicazione(
			Integer idIstanza, Integer idEvasioneComunicazione)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		Transaction trx = ses.beginTransaction();
		try {
			new EvasioniComunicazioniDao().delete(ses, idEvasioneComunicazione);
			trx.commit();
		} catch (HibernateException e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return findEvasioniComunicazioniByIstanza(idIstanza);
	}

	@Override
	public String getTipiAbbStringFromComunicazione(Integer idCom) throws BusinessException {
		Session ses = SessionFactory.getSession();
		String result = "";
		try {
			result = ComunicazioniBusiness.getTipiAbbStringFromComunicazione(ses, idCom);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
//	@Override
//	public String getTipiAbbStringFromComunicazione(Integer idCom) throws PagamentiException {
//		Session ses = SessionFactory.getSession();
//		String result = "";
//		try {
//			Comunicazioni com = GenericDao.findById(ses, Comunicazioni.class, idCom);
//			String[] idArray = com.getTipiAbbonamentoList().split(AppConstants.COMUN_TIPI_ABB_SEPARATOR);
//			for (String idString:idArray) {
//				Integer id = Integer.parseInt(idString);
//				TipiAbbonamento ta = GenericDao.findById(ses, TipiAbbonamento.class, id);
//				if (ta != null) {
//					result += ta.getCodice()+" ";
//				} else {
//					throw new PagamentiException("La comunicazione '"+com.getTitolo()+"' è abbinata ad un tipo abb. non esistente");
//				}
//			}
//		} catch (HibernateException e) {
//			LOG.error(e.getMessage(), e);
//			throw new PagamentiException(e.getMessage(), e);
//		} finally {
//			ses.close();
//		}
//		return result;
//	}


	@Override
	public Integer saveOrUpdateEvasioneComunicazione(
			EvasioniComunicazioni item)
			throws BusinessException {
		Session ses = SessionFactory.getSession();
		Integer idEc = null;
		Transaction trx = ses.beginTransaction();
		try {
			//verify
			if (item.getMessaggio() != null) {
				if (item.getMessaggio().length() > AppConstants.BOLLETTINO_MESSAGE_MAX_LENGTH) {
					int excess = item.getMessaggio().length() - AppConstants.BOLLETTINO_MESSAGE_MAX_LENGTH;
					throw new BusinessException("Il messaggio ha "+excess+
							" caratteri piu' del massimo consentito");
				}
			}
			//save/update
			Integer idIa = Integer.valueOf(item.getIdIstanzaAbbonamentoT());
			IstanzeAbbonamenti ia = GenericDao.findById(ses, IstanzeAbbonamenti.class, idIa);
			item.setIstanzaAbbonamento(ia);
			if (item.getId() != null) {
				new EvasioniComunicazioniDao().update(ses, item);
				idEc = item.getId();
			} else {
				//salva
				idEc = (Integer) new EvasioniComunicazioniDao().save(ses, item);
			}
			trx.commit();
		} catch (HibernateException e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return idEc;
	}
	
	@Override
	public Boolean enqueueEvasioneComunicazione(Integer idIstanza, String idUtente,
			String idTipoMedia, String idTipoDestinatario, Boolean richiestaRinnovo, String messaggio) throws BusinessException {
		EvasioniComunicazioni result = new EvasioniComunicazioni();
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		//ComunicazioniDao comDao = new ComunicazioniDao();
		try {
			IstanzeAbbonamenti ia = GenericDao.findById(ses, IstanzeAbbonamenti.class, idIstanza);
			//Comunicazioni comBol = comDao.findComunicazioneBollettino(ses);
			result.setComunicazione(null);
			result.setFascicolo(null);
			result.setIstanzaAbbonamento(ia);
			result.setDataCreazione(new Date());
			result.setDataModifica(new Date());
			result.setDataEstrazione(null);
			result.setIdUtente(idUtente);
			result.setEliminato(false);
			result.setEstrattoComeAnnullato(false);
			result.setIdTipoMedia(idTipoMedia);
			result.setIdTipoDestinatario(idTipoDestinatario);
			result.setRichiestaRinnovo(richiestaRinnovo);
			result.setMessaggio(messaggio);
			result.setNote(null);
			ses.save(result);
			trn.commit();
		} catch (HibernateException e) {
			trn.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return true;
	}
	
	@Override
	public EvasioniComunicazioni createEvasioneComunicazione(Integer idIstanza,
			String idTipoMedia) throws BusinessException {
		EvasioniComunicazioni result = new EvasioniComunicazioni();
		Session ses = SessionFactory.getSession();
		//ComunicazioniDao comDao = new ComunicazioniDao();
		try {
			IstanzeAbbonamenti ia = GenericDao.findById(ses, IstanzeAbbonamenti.class, idIstanza);
			String messaggio = "Richiesta di pagamento a saldo quota di abbonamento\r\n"+
					ia.getAbbonamento().getPeriodico().getNome()+
					" €"+ServerConstants.FORMAT_CURRENCY.format(ia.getListino().getPrezzo())+"\r\n";
			if (ia.getOpzioniIstanzeAbbonamentiSet() != null) {
				if (ia.getOpzioniIstanzeAbbonamentiSet().size() > 0) {
					String opzMessage = "";
					for (OpzioniIstanzeAbbonamenti oia:ia.getOpzioniIstanzeAbbonamentiSet()) {
						boolean mandatory = false;
						for (OpzioniListini ol:ia.getListino().getOpzioniListiniSet()) {
							if (oia.getOpzione().getId() == ol.getOpzione().getId()) mandatory = true;
						}
						if (!mandatory) opzMessage += oia.getOpzione().getNome() +
								" €"+ServerConstants.FORMAT_CURRENCY.format(oia.getOpzione().getPrezzo())+"\r\n";
					}
					if (opzMessage.length() > 0) messaggio += "Opzioni richieste:\r\n"+opzMessage;
				}
			}
			
			//Comunicazioni comBol = comDao.findComunicazioneBollettino(ses);
			result.setComunicazione(null);
			result.setFascicolo(null);
			result.setIstanzaAbbonamento(ia);
			result.setDataCreazione(new Date());
			result.setDataModifica(new Date());
			result.setDataEstrazione(null);
			result.setEliminato(false);
			result.setEstrattoComeAnnullato(false);
			result.setIdTipoMedia(idTipoMedia);
			result.setIdTipoDestinatario(AppConstants.DEST_PAGANTE);
			result.setRichiestaRinnovo(false);
			result.setMessaggio(messaggio);
			result.setNote(null);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		result = SerializationUtil.makeSerializable(result);
		return result;
	}

	@Override
	public EvasioniComunicazioni findEvasioneComunicazioneById(Integer idEvasioniCom)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<EvasioniComunicazioni> resultList = null;
		try {
			resultList = GenericDao.findByProperty(ses, EvasioniComunicazioni.class, "id", idEvasioniCom);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (resultList != null) {
			if (resultList.size() > 0) {
			EvasioniComunicazioni result =
					SerializationUtil.makeSerializable(resultList.get(0));
			return result;
			}
		}
		throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	}
	
	
	
	//ModelliBollettini
	
	
	
	@Override
	public List<ModelliBollettini> findModelliBollettini(int offset,
			int pageSize) throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<ModelliBollettini> result = null;
		try {
			result = new ModelliBollettiniDao().findModelliBollettini(ses, offset, pageSize);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (result != null) {
			if (result.size() > 0) {
				return result;
			}
		}
		throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	}

	@Override
	public List<ModelliBollettini> findModelliBollettiniByPeriodico(
			Integer idPeriodico, int offset, int pageSize)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<ModelliBollettini> result = null;
		try {
			result = new ModelliBollettiniDao().findModelliBollettiniByPeriodico(ses,
					idPeriodico, offset, pageSize);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (result != null) {
			if (result.size() > 0) {
				return result;
			}
		}
		throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	}

	@Override
	public ModelliBollettini findModelliBollettiniById(Integer idBolMod)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		ModelliBollettini result = null;
		try {
			result = GenericDao.findById(ses, ModelliBollettini.class, idBolMod);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (result != null) {
			return result;
		}
		throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	}

	@Override
	public Integer saveOrUpdateModelliBollettini(ModelliBollettini item)
			throws BusinessException {
		Session ses = SessionFactory.getSession();
		Integer idBm = null;
		Transaction trx = ses.beginTransaction();
		try {
			Integer idPeriodico = Integer.valueOf(item.getIdPeriodicoT());
			Periodici periodico = GenericDao.findById(ses, Periodici.class, idPeriodico);
			item.setPeriodico(periodico);
			item.setAutorizzazione(item.getAutorizzazione().toUpperCase());
			ModelliBollettiniDao bmDao = new ModelliBollettiniDao();
			if (item.getId() != null) {
				bmDao.update(ses, item);
				idBm = item.getId();
			} else {
				//salva
				idBm = (Integer) bmDao.save(ses, item);
			}
			trx.commit();
		} catch (HibernateException e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return idBm;
	}

	@Override
	public ModelliBollettini createModelliBollettini(Integer idPeriodico)
			throws BusinessException {
		ModelliBollettini result = new ModelliBollettini();
		Session ses = SessionFactory.getSession();
		try {
			new ModelliBollettiniDao().createModelliBollettini(ses, idPeriodico, "");
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}

	@Override
	public List<ModelliBollettini> deleteModelliBollettini(Integer idBolMod,
			int pageSize) throws BusinessException, EmptyResultException {
		Integer idPeriodico = null;
		Session ses = SessionFactory.getSession();
		Transaction trx = ses.beginTransaction();
		try {
			ModelliBollettini b = GenericDao.findById(ses, ModelliBollettini.class, idBolMod);
			idPeriodico = b.getPeriodico().getId();
			new ModelliBollettiniDao().delete(ses, b);
			trx.commit();
		} catch (HibernateException e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return findModelliBollettiniByPeriodico(idPeriodico, 0, pageSize);
	}

	@Override
	public String formatBollettinoText(String text, int lineWidth) {
		String result = FileFormatComunicazioni.formatBollettinoText(text, lineWidth);
		return result;
	}

	@Override
	public List<ModelliEmail> findModelliEmail(int offset, int pageSize)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<ModelliEmail> result = null;
		try {
			result = new ModelliEmailDao().findModelliEmail(ses, offset, pageSize);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (result != null) {
			if (result.size() > 0) {
				return result;
			}
		}
		throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	}

	@Override
	public ModelliEmail findModelliEmailById(Integer idMe)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		ModelliEmail result = null;
		try {
			result = GenericDao.findById(ses, ModelliEmail.class, idMe);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (result != null) {
			return result;
		}
		throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	}

	@Override
	public Integer saveOrUpdateModelliEmail(ModelliEmail item)
			throws BusinessException {
		Session ses = SessionFactory.getSession();
		Integer idMe = null;
		Transaction trx = ses.beginTransaction();
		try {
			ModelliEmailDao meDao = new ModelliEmailDao();
			if (item.getId() != null) {
				meDao.update(ses, item);
				idMe = item.getId();
			} else {
				//salva
				idMe = (Integer) meDao.save(ses, item);
			}
			trx.commit();
		} catch (HibernateException e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return idMe;
	}

	@Override
	public ModelliEmail createModelliEmail() throws BusinessException {
		ModelliEmail result = new ModelliEmail();
		result.setDescr(EmailConstants.DEFAULT_DESCRIPTION);
		result.setOggetto(EmailConstants.DEFAULT_SUBJECT);
		result.setNomeMittente(EmailConstants.DEFAULT_FROM_NAME);
		return result;
	}

	@Override
	public List<ModelliEmail> deleteModelliEmail(Integer idModEmail,
			int pageSize) throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		Transaction trx = ses.beginTransaction();
		try {
			ModelliEmail me = GenericDao.findById(ses, ModelliEmail.class, idModEmail);
			new ModelliEmailDao().delete(ses, me);
			trx.commit();
		} catch (HibernateException e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return findModelliEmail(0, pageSize);
	}

	@Override
	public Map<Comunicazioni, Integer> findComunicazioniByEnqueuedMedia(
			String idTipoMedia) throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		Map<Comunicazioni, Integer> result = null;
		try {
			result = new ComunicazioniDao().findAsyncComunicazioniByEnqueuedMedia(ses, idTipoMedia);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (result != null) {
			if (result.size() > 0) {
				return result;
			}
		}
		throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	}

}
