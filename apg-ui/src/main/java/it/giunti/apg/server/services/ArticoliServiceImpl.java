package it.giunti.apg.server.services;

import it.giunti.apg.client.services.ArticoliService;
import it.giunti.apg.server.SerializationUtil;
import it.giunti.apg.server.persistence.ArticoliDao;
import it.giunti.apg.server.persistence.ArticoliListiniDao;
import it.giunti.apg.server.persistence.ArticoliOpzioniDao;
import it.giunti.apg.server.persistence.EvasioniArticoliDao;
import it.giunti.apg.server.persistence.GenericDao;
import it.giunti.apg.server.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.server.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.Articoli;
import it.giunti.apg.shared.model.ArticoliListini;
import it.giunti.apg.shared.model.ArticoliOpzioni;
import it.giunti.apg.shared.model.EvasioniArticoli;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Listini;
import it.giunti.apg.shared.model.Opzioni;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ArticoliServiceImpl extends RemoteServiceServlet implements ArticoliService {
	private static final long serialVersionUID = 5100726070121536041L;

	private static final Logger LOG = LoggerFactory.getLogger(ArticoliServiceImpl.class);
	
	@Override
	public Articoli createArticolo() throws BusinessException {
		Articoli result = new Articoli();
		result.setIdTipoAnagraficaSap(AppConstants.ANAGRAFICA_SAP_GE_LIBRO);
		result.setInAttesa(false);
		return result;
	}

	@Override
	public Articoli findArticoloById(Integer idArticolo) throws BusinessException,
			EmptyResultException {
		Session ses = SessionFactory.getSession();
		Articoli result = null;
		try {
			result = GenericDao.findById(ses, Articoli.class, idArticolo);
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
	public Integer saveOrUpdateArticolo(Articoli item) throws BusinessException {
		Session ses = SessionFactory.getSession();
		Integer idReg = null;
		Transaction trx = ses.beginTransaction();
		ArticoliDao articoliDao = new ArticoliDao();
		try {
			if (item.getId() != null) {
				articoliDao.update(ses, item);
				idReg = item.getId();
			} else {
				//salva
				idReg = (Integer) articoliDao.save(ses, item);
			}
			trx.commit();
		} catch (HibernateException e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return idReg;
	}

	@Override
	public List<Articoli> findArticoliByDate(Date validDt, int offset, int pageSize)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<Articoli> result = null;
		try {
			result = new ArticoliDao().findByDate(ses, validDt, offset, pageSize);
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
	public List<Articoli> findArticoliByDateInterval(Date startDt, Date finishDt)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<Articoli> result = null;
		try {
			result = new ArticoliDao().findByDateInterval(ses, startDt, finishDt);
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
	

	// EvasioniFascicoli
	
	
	
	@Override
	public List<EvasioniArticoli> findEvasioniArticoliByIstanza(Integer idIstanza)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<EvasioniArticoli> result = null;
		try {
			result = new EvasioniArticoliDao().findByIstanza(ses, idIstanza);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (result != null) {
			if (result.size() > 0) {
				return SerializationUtil.makeSerializable(result);
			}
		}
		throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	}

	@Override
	public List<EvasioniArticoli> findEvasioniArticoliByAnagrafica(Integer idAnagrafica)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<EvasioniArticoli> result = null;
		try {
			result = new EvasioniArticoliDao().findByAnagrafica(ses, idAnagrafica);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (result != null) {
			if (result.size() > 0) {
				return SerializationUtil.makeSerializable(result);
			}
		}
		throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	}
	
	@Override
	public EvasioniArticoli findEvasioniArticoliById(Integer idEd)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		EvasioniArticoli result = null;
		try {
			result = GenericDao.findById(ses, EvasioniArticoli.class, idEd);
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
	public EvasioniArticoli createEmptyEvasioneArticoloFromIstanza(Integer idIstanza,
			String idTipoDestinatario, String idUtente)
			throws BusinessException {
		Session ses = SessionFactory.getSession();
		EvasioniArticoli result = null;
		try {
			IstanzeAbbonamenti ia = GenericDao.findById(ses, IstanzeAbbonamenti.class, idIstanza);
			result = new EvasioniArticoliDao().createEmptyEvasioniArticoliFromIstanza(ses, ia, idTipoDestinatario, idUtente);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}


	@Override
	public EvasioniArticoli createEvasioneArticoloFromAnagrafica(Integer idAnagrafica,
			Integer copie, String idTipoDestinatario, String idUtente)
			throws BusinessException {
		Session ses = SessionFactory.getSession();
		EvasioniArticoli result = null;
		try {
			result = new EvasioniArticoliDao().createEvasioniArticoliFromAnagrafica(ses, 
					idAnagrafica, copie, idTipoDestinatario, idUtente);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	@Override
	public Integer createEvasioneArticoloWithCodAbbo(String codAbbo,
			Integer idArticolo, String idTipoDestinatario, String idUtente) throws BusinessException {
		if ((idArticolo == null) || (codAbbo == null) || (idTipoDestinatario == null))
			throw new BusinessException("Dati insufficienti ad abbinare un articolo");
		IstanzeAbbonamenti ia = null;
		Session ses = SessionFactory.getSession();
		try {
			ia = new IstanzeAbbonamentiDao().findUltimaIstanzaByCodice(ses, codAbbo);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		EvasioniArticoli ed = new EvasioniArticoli();
		ed.setDataCreazione(new Date());
		ed.setIdTipoDestinatario(idTipoDestinatario);
		ed.setNote("");
		ed.setPrenotazioneIstanzaFutura(false);
		ed.setIdArticoloT(idArticolo.toString());
		ed.setIdUtente(idUtente);
		if (ia == null) {
			throw new BusinessException("Non e' possibile abbinare il articolo all'abbonamento "+codAbbo);
		} else {
			fillEvasioneArticoloWithIstanza(ed, ia);
			return saveOrUpdateEvasioneArticolo(ed);
		}
	}
	
	@Override
	public Integer saveOrUpdateEvasioneArticolo(EvasioniArticoli item)
			throws BusinessException {
		Session ses = SessionFactory.getSession();
		Integer idReg = null;
		Transaction trx = ses.beginTransaction();
		EvasioniArticoliDao edDao = new EvasioniArticoliDao();
		try {
			if (item.getIdIstanzaAbbonamento() != null) {
				Integer idIa = item.getIdIstanzaAbbonamento();
				IstanzeAbbonamenti ia = GenericDao.findById(ses, IstanzeAbbonamenti.class, idIa);
				if (ia != null) {
					fillEvasioneArticoloWithIstanza(item, ia);
				}
			}
			Integer idArticolo = ValueUtil.stoi(item.getIdArticoloT());
			Articoli articolo = GenericDao.findById(ses, Articoli.class, idArticolo);
			item.setArticolo(articolo);
			//Salvataggio effettivo
			if (item.getId() != null) {
				edDao.update(ses, item);
				idReg = item.getId();
			} else {
				//salva
				idReg = (Integer) edDao.save(ses, item);
			}
			trx.commit();
		} catch (HibernateException e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return idReg;
	}


	private void fillEvasioneArticoloWithIstanza(EvasioniArticoli ed, IstanzeAbbonamenti ia) 
			throws BusinessException {
		//Abbonamento
		ed.setIdAbbonamento(ia.getAbbonamento().getId());
		ed.setIdIstanzaAbbonamento(ia.getId());
		ed.setCopie(ia.getCopie());
		//Anagrafica
		ed.setIdAnagrafica(ia.getAbbonato().getId());
		if (ed.getIdTipoDestinatario().equals(AppConstants.DEST_PAGANTE)) {
			if (ia.getPagante() != null) {
				ed.setIdAnagrafica(ia.getPagante().getId());
			} else {
				throw new BusinessException("Impossibile salvare il articolo: non e' definito un pagante a cui inviarlo");
			}
		}
		if (ed.getIdTipoDestinatario().equals(AppConstants.DEST_PROMOTORE)) {
			if (ia.getPromotore() != null) {
				ed.setIdAnagrafica(ia.getPromotore().getId());
			} else {
				throw new BusinessException("Impossibile salvare il articolo: non e' definito un promotore a cui inviarlo");
			}
		}
	}
	
	@Override
	public List<EvasioniArticoli> deleteEvasioneArticolo(Integer idEvasioneArticolo)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		Transaction trx = ses.beginTransaction();
		Integer idIstanza = null;
		try {
			EvasioniArticoli ed = GenericDao.findById(ses, EvasioniArticoli.class, idEvasioneArticolo);
			idIstanza = ed.getIdIstanzaAbbonamento();
			new EvasioniArticoliDao().delete(ses, ed);
			trx.commit();
		} catch (HibernateException e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		List<EvasioniArticoli> result = new ArrayList<EvasioniArticoli>();
		if (idIstanza != null) result = findEvasioniArticoliByIstanza(idIstanza);
		return result;
	}

	//@Override
	//public Integer reattachArticoliToInstanza(Integer idIstanza)
	//		throws PagamentiException {
	//	Integer result = null;
	//	Session ses = SessionFactory.getSession();
	//	Transaction trx = ses.beginTransaction();
	//	try {
	//		IstanzeAbbonamenti ia = GenericDao.findById(ses, IstanzeAbbonamenti.class, idIstanza);
	//		result = new EvasioniArticoliDao().reattachArticoliToInstanza(ses, ia);
	//		trx.commit();
	//	} catch (HibernateException e) {
	//		trx.rollback();
	//		LOG.error(e.getMessage(), e);
	//		throw new PagamentiException(e.getMessage(), e);
	//	} finally {
	//		ses.close();
	//	}
	//	return result;
	//}


	
	// ArticoliListini
	
	
	
	@Override
	public ArticoliListini findArticoloListinoById(Integer idArticoloListino)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		ArticoliListini result = null;
		try {
			result = GenericDao.findById(ses, ArticoliListini.class, idArticoloListino);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (result != null) {
			return SerializationUtil.makeSerializable(result);
		}
		throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	}

	@Override
	public Integer saveOrUpdateArticoloListino(ArticoliListini item)
			throws BusinessException {
		Session ses = SessionFactory.getSession();
		Integer idReg = null;
		Transaction trx = ses.beginTransaction();
		ArticoliListiniDao alDao = new ArticoliListiniDao();
		try {
			Articoli articolo = GenericDao.findById(ses, Articoli.class, item.getIdArticoliT());
			item.setArticolo(articolo);
			if (item.getId() != null) {
				alDao.update(ses, item);
				idReg = item.getId();
			} else {
				//salva
				idReg = (Integer) alDao.save(ses, item);
			}
			trx.commit();
		} catch (HibernateException e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return idReg;
	}

	@Override
	public ArticoliListini createArticoloListino(Integer idListino) throws BusinessException {
		ArticoliListini result = new ArticoliListini();
		Session ses = SessionFactory.getSession();
		try {
			result.setIdTipoDestinatario(AppConstants.DEST_BENEFICIARIO);
			Listini listino = GenericDao.findById(ses, Listini.class, idListino);
			result.setListino(listino);
			result.setGiornoLimitePagamento(null);
			result.setMeseLimitePagamento(null);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return SerializationUtil.makeSerializable(result);
	}

	@Override
	public List<ArticoliListini> deleteArticoloListino(Integer idArticoloListino) throws BusinessException, EmptyResultException {
		Integer idListino = null;
		Session ses = SessionFactory.getSession();
		Transaction trx = ses.beginTransaction();
		try {
			ArticoliListini al = GenericDao.findById(ses, ArticoliListini.class, idArticoloListino);
			idListino = al.getListino().getId();
			new ArticoliListiniDao().delete(ses, al);
			trx.commit();
		} catch (HibernateException e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return findArticoliListini(idListino);
	}
	
	@Override
	public List<ArticoliListini> findArticoliListini(Integer idListino)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<ArticoliListini> result = null;
		try {
			result = new ArticoliListiniDao().findByListino(ses, idListino);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (result != null) {
			if (result.size() > 0) {
				return SerializationUtil.makeSerializable(result);
			}
		}
		throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	}

	@Override
	public List<ArticoliListini> findArticoliListiniByPeriodicoDate(
			Integer idPeriodico, Date date)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<ArticoliListini> result = null;
		try {
			result = new ArticoliListiniDao().findByPeriodicoDate(ses, idPeriodico, date);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (result != null) {
			if (result.size() > 0) {
				return SerializationUtil.makeSerializable(result);
			}
		}
		throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	}
	
	@Override
	public Map<ArticoliListini, Integer> findPendingArticoliListiniCount()
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		Map<ArticoliListini, Integer> result = null;
		try {
			result = new ArticoliListiniDao().findPendingArticoliListiniCount(ses);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (result != null) {
			if (result.size() > 0) {
				for (ArticoliListini al:result.keySet()) SerializationUtil.makeSerializable(al);
				return result;
			}
		}
		throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	}
	
	
	// ArticoliOpzioni
	
	
	
	@Override
	public ArticoliOpzioni findArticoloOpzioneById(Integer idArticoloOpzione)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		ArticoliOpzioni result = null;
		try {
			result = GenericDao.findById(ses, ArticoliOpzioni.class, idArticoloOpzione);
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
	public Integer saveOrUpdateArticoloOpzione(ArticoliOpzioni item)
			throws BusinessException {
		Session ses = SessionFactory.getSession();
		Integer idReg = null;
		Transaction trx = ses.beginTransaction();
		ArticoliOpzioniDao alDao = new ArticoliOpzioniDao();
		try {
			Articoli articolo = GenericDao.findById(ses, Articoli.class, item.getIdArticoliT());
			item.setArticolo(articolo);
			if (item.getId() != null) {
				alDao.update(ses, item);
				idReg = item.getId();
			} else {
				//salva
				idReg = (Integer) alDao.save(ses, item);
			}
			trx.commit();
		} catch (HibernateException e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return idReg;
	}

	@Override
	public ArticoliOpzioni createArticoloOpzione(Integer idOpzione) throws BusinessException {
		ArticoliOpzioni result = new ArticoliOpzioni();
		Session ses = SessionFactory.getSession();
		try {
			Opzioni opzione = GenericDao.findById(ses, Opzioni.class, idOpzione);
			result.setOpzione(opzione);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}

	@Override
	public List<ArticoliOpzioni> deleteArticoloOpzione(Integer idArticoloOpzione) throws BusinessException, EmptyResultException {
		Integer idOpzione = null;
		Session ses = SessionFactory.getSession();
		Transaction trx = ses.beginTransaction();
		try {
			ArticoliOpzioni ao = GenericDao.findById(ses, ArticoliOpzioni.class, idArticoloOpzione);
			idOpzione = ao.getOpzione().getId();
			new ArticoliOpzioniDao().delete(ses, ao);
			trx.commit();
		} catch (HibernateException e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return findArticoliOpzioni(idOpzione);
	}
	
	@Override
	public List<ArticoliOpzioni> findArticoliOpzioni(Integer idOpzione)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<ArticoliOpzioni> result = null;
		try {
			result = new ArticoliOpzioniDao().findByOpzione(ses, idOpzione);
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
	public List<ArticoliOpzioni> findArticoliOpzioniByPeriodicoDate(
			Integer idPeriodico, Date date) throws BusinessException,
			EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<ArticoliOpzioni> result = null;
		try {
			result = new ArticoliOpzioniDao().findByPeriodicoDate(ses, idPeriodico, date);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (result != null) {
			if (result.size() > 0) {
				return SerializationUtil.makeSerializable(result);
			}
		}
		throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	}

	@Override
	public Map<ArticoliOpzioni, Integer> findPendingArticoliOpzioniCount()
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		Map<ArticoliOpzioni, Integer> result = null;
		try {
			result = new ArticoliOpzioniDao().findPendingArticoliOpzioniCount(ses);
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
