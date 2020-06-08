package it.giunti.apg.server.services;

import java.util.ArrayList;
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

import it.giunti.apg.client.services.MaterialiService;
import it.giunti.apg.core.SerializationUtil;
import it.giunti.apg.core.persistence.ArticoliListiniDao;
import it.giunti.apg.core.persistence.ArticoliOpzioniDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.core.persistence.MaterialiDao;
import it.giunti.apg.core.persistence.MaterialiProgrammazioneDao;
import it.giunti.apg.core.persistence.MaterialiSpedizioneDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.model.ArticoliListini;
import it.giunti.apg.shared.model.ArticoliOpzioni;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Listini;
import it.giunti.apg.shared.model.Materiali;
import it.giunti.apg.shared.model.MaterialiProgrammazione;
import it.giunti.apg.shared.model.MaterialiSpedizione;
import it.giunti.apg.shared.model.Opzioni;
import it.giunti.apg.shared.model.Periodici;

public class MaterialiServiceImpl extends RemoteServiceServlet implements MaterialiService {
	private static final long serialVersionUID = 7708772890945641609L;
	private static final Logger LOG = LoggerFactory.getLogger(MaterialiServiceImpl.class);
	
	// Materiali
	
	@Override
	public Materiali createMateriale(String tipoMateriale, String tipoAnagraficaSap) throws BusinessException {
		Materiali result = new Materiali();
		result.setIdTipoMateriale(tipoMateriale);
		result.setIdTipoAnagraficaSap(tipoAnagraficaSap);
		result.setInAttesa(false);
		return result;
	}
	
	@Override
	public Materiali createMaterialeArticolo() throws BusinessException {
		Materiali result = new Materiali();
		result.setIdTipoMateriale(AppConstants.MATERIALE_ARTICOLO_LIBRO);
		result.setIdTipoAnagraficaSap(AppConstants.ANAGRAFICA_SAP_GE_LIBRO);
		result.setInAttesa(false);
		return result;
	}
	
	@Override
	public Materiali createMaterialeFascicoloGe() throws BusinessException {
		Materiali result = new Materiali();
		result.setIdTipoMateriale(AppConstants.MATERIALE_FASCICOLO);
		result.setIdTipoAnagraficaSap(AppConstants.ANAGRAFICA_SAP_GE_FASCICOLO);
		result.setInAttesa(true);
		return result;
	}
	
	@Override
	public Materiali findById(Integer idMateriale) throws BusinessException,
			EmptyResultException {
		Session ses = SessionFactory.getSession();
		Materiali result = null;
		try {
			result = GenericDao.findById(ses, Materiali.class, idMateriale);
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
	public Integer saveOrUpdateMateriale(Materiali item) throws BusinessException {
		Session ses = SessionFactory.getSession();
		Integer idReg = null;
		Transaction trx = ses.beginTransaction();
		MaterialiDao matDao = new MaterialiDao();
		try {
			if (item.getId() != null) {
				matDao.update(ses, item);
				idReg = item.getId();
			} else {
				//salva
				idReg = (Integer) matDao.save(ses, item);
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
	public Boolean deleteMateriale(Integer idMateriale) throws BusinessException {
		Session ses = SessionFactory.getSession();
		MaterialiDao fasDao = new MaterialiDao();
		Transaction trx = ses.beginTransaction();
		try {
			Materiali f = GenericDao.findById(ses, Materiali.class, idMateriale);
			fasDao.delete(ses, f);
			trx.commit();
		} catch (HibernateException e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return true;
	}
	
	@Override
	public List<Materiali> findMaterialiByDate(Date validDt, int offset, int pageSize)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<Materiali> result = null;
		try {
			result = new MaterialiDao().findByDate(ses, validDt, offset, pageSize);
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
	
	


	// MaterialiProgrammazione
	
	@Override
	public MaterialiProgrammazione createMaterialeProgrammazione(Materiali mat, Integer idPeriodico) throws BusinessException {
		Session ses = SessionFactory.getSession();
		MaterialiProgrammazione mp = new MaterialiProgrammazione();
		try {
			Calendar cal = new GregorianCalendar();
			cal.setTime(DateUtil.now());
			cal.set(Calendar.DAY_OF_MONTH, 1);
			Date monthStart = cal.getTime();
			Periodici p = GenericDao.findById(ses, Periodici.class, idPeriodico);
			mp.setPeriodico(p);
			mp.setDataNominale(monthStart);
			mp.setMateriale(mat);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return mp;
	}

	@Override
	public Boolean deleteMaterialiProgrammazione(Integer idMaterialiProgrammazione)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		MaterialiProgrammazioneDao mpDao = new MaterialiProgrammazioneDao();
		Transaction trx = ses.beginTransaction();
		try {
			MaterialiProgrammazione mp = GenericDao.findById(ses, MaterialiProgrammazione.class, idMaterialiProgrammazione);
			mpDao.delete(ses, mp);
			trx.commit();
		} catch (HibernateException e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return true;
	}
	
	@Override
	public List<MaterialiProgrammazione> findMaterialiProgrammazioneByPeriodico(Integer idPeriodico, long startDt, long finishDt,
			boolean includeOpzioni, boolean orderAsc, int offset, int pageSize) throws BusinessException, EmptyResultException {
		if(idPeriodico == null) return new ArrayList<MaterialiProgrammazione>();
		Session ses = SessionFactory.getSession();
		List<MaterialiProgrammazione> result = null;
		try {
			result = new MaterialiProgrammazioneDao().findByPeriodico(ses, idPeriodico, null,
					startDt, finishDt, includeOpzioni, orderAsc, offset, pageSize);
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
	public List<MaterialiProgrammazione> findMaterialiProgrammazioneByPeriodico(Integer idPeriodico,
			Integer selectedId, long startDt, long finishDt, boolean includeOpzioni, boolean orderAsc, int offset, int pageSize)
			throws BusinessException, EmptyResultException {
		if(idPeriodico == null) return new ArrayList<MaterialiProgrammazione>();
		Session ses = SessionFactory.getSession();
		List<MaterialiProgrammazione> result = null;
		try {
			result = new MaterialiProgrammazioneDao().findByPeriodico(ses, idPeriodico, selectedId,
					startDt, finishDt, includeOpzioni, orderAsc, offset, pageSize);
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
	public List<MaterialiProgrammazione> findMaterialiProgrammazioneByOpzione(Integer idOpzione,
			boolean orderAsc, int offset, int pageSize) throws BusinessException, EmptyResultException {
		if(idOpzione == null) return new ArrayList<MaterialiProgrammazione>();
		Session ses = SessionFactory.getSession();
		List<MaterialiProgrammazione> result = null;
		try {
			result = new MaterialiProgrammazioneDao().findByOpzione(ses, idOpzione,
					orderAsc, offset, pageSize);
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
	public MaterialiProgrammazione findMaterialeProgrammazioneByPeriodicoDataInizio(
			Integer idPeriodico, Date date) throws BusinessException {
		Session ses = SessionFactory.getSession();
		MaterialiProgrammazione result = null;
		try {
			result = new MaterialiProgrammazioneDao().findFascicoloByPeriodicoDataInizio(ses, idPeriodico, date);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	@Override
	public MaterialiProgrammazione findPrimoFascicoloNonSpedito(Integer idPeriodico, Date date) throws BusinessException {
		Session ses = SessionFactory.getSession();
		MaterialiProgrammazione result = null;
		try {
			result = new MaterialiProgrammazioneDao().findPrimoFascicoloNonSpedito(ses, idPeriodico, date);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	@Override
	public Map<MaterialiProgrammazione, Integer> findFascicoliByEnqueuedMedia(String idTipoMedia)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		Map<MaterialiProgrammazione, Integer> result = null;
		try {
			result = new MaterialiProgrammazioneDao().findByEnqueuedComunicazioniMedia(ses, idTipoMedia);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (result != null) {
			if (result.size() > 0) return result;
		}
		throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	}
	
	@Override
	public Boolean verifyMaterialiProgrammazioneWithinIstanza(Integer idIstanza, Integer idMatProg)
			throws BusinessException {
		Session ses = SessionFactory.getSession();
		MaterialiProgrammazione mp = null;
		IstanzeAbbonamenti ia = null;
		boolean result = true;
		try {
			mp = GenericDao.findById(ses, MaterialiProgrammazione.class, idMatProg);
			ia = GenericDao.findById(ses, IstanzeAbbonamenti.class, idIstanza);
			if ((ia == null) || (mp == null)) throw new BusinessException("Nessuna corrispondenza trovata");
			Date dataFascicolo = mp.getDataNominale();
			if (ia.getDataInizio().after(dataFascicolo)) result = false;
			if (ia.getDataFine().before(dataFascicolo)) result = false;
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	
	// MaterialiSpedizione
	
	
	@Override
	public MaterialiSpedizione createMaterialiSpedizioneForAbbonamento(Integer idIstanza)
			throws BusinessException {
		Session ses = SessionFactory.getSession();
		MaterialiSpedizione result = null;
		IstanzeAbbonamenti istanza = (IstanzeAbbonamenti) ses.get(IstanzeAbbonamenti.class, idIstanza);
		try {
			Date today = DateUtil.now();
			result = new MaterialiSpedizione();
			result.setDataCreazione(today);
			result.setDataInvio(null);
			result.setDataOrdine(null);
			result.setMateriale(null);
			result.setNote("");
			if (istanza != null) {
				result.setIdAbbonamento(istanza.getAbbonamento().getId());
				result.setIdAnagrafica(istanza.getAbbonato().getId());
				result.setCopie(istanza.getCopie());
			}
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	@Override
	public MaterialiSpedizione createMaterialiSpedizioneForAnagrafica(Integer idAnagrafica, Integer copie)
			throws BusinessException {
		Session ses = SessionFactory.getSession();
		MaterialiSpedizione result = null;
		try {
			Date today = DateUtil.now();
			result = new MaterialiSpedizione();
			result.setDataCreazione(today);
			result.setDataInvio(null);
			result.setDataOrdine(null);
			result.setMateriale(null);
			result.setIdAbbonamento(null);
			result.setIdAnagrafica(idAnagrafica);
			result.setNote("");
			result.setCopie(copie);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	@Override
	public Integer createMaterialiSpedizioneForCodAbboAndAnagrafica(String codAbbo, Integer idMateriale,
			Integer idAnagrafica) throws BusinessException {
		if ((idMateriale == null) || (codAbbo == null))
			throw new BusinessException("Dati insufficienti ad abbinare un materiale");
		IstanzeAbbonamenti ia = null;
		Materiali mat = null;
		Session ses = SessionFactory.getSession();
		try {
			ia = new IstanzeAbbonamentiDao().findUltimaIstanzaByCodice(ses, codAbbo);
			mat = GenericDao.findById(ses, Materiali.class, idMateriale);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		MaterialiSpedizione ms = new MaterialiSpedizione();
		ms.setDataCreazione(DateUtil.now());
		ms.setNote("");
		ms.setPrenotazioneIstanzaFutura(false);
		ms.setMateriale(mat);
		if (ia == null) {
			throw new BusinessException("Non e' possibile abbinare il articolo all'abbonamento "+codAbbo);
		} else {
			ms.setIdAbbonamento(ia.getAbbonamento().getId());
			ms.setCopie(ia.getCopie());
			ms.setIdAnagrafica(idAnagrafica);
			return saveOrUpdateMaterialiSpedizione(ms);
		}
	}
	
	@Override
	public MaterialiSpedizione findMaterialiSpedizioneById(Integer idMatSped) 
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		MaterialiSpedizione result = null;
		try {
			result = GenericDao.findById(ses, MaterialiSpedizione.class, idMatSped);
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
	public List<MaterialiSpedizione> findMaterialiSpedizioneByIstanza(Integer idIstanza) throws BusinessException, EmptyResultException {
		if (idIstanza == null) return null;
		Session ses = SessionFactory.getSession();
		List<MaterialiSpedizione> result = null;
		try {
			IstanzeAbbonamenti istanza = (IstanzeAbbonamenti) ses.get(IstanzeAbbonamenti.class, idIstanza);
			result = new MaterialiSpedizioneDao().findByIstanza(ses, istanza);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (result != null) {
			if (result.size() > 0) return result;
		}
		throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	}
	
	@Override
	public List<MaterialiSpedizione> findMaterialiSpedizioneByAnagrafica(Integer idAnagrafica)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<MaterialiSpedizione> result = null;
		try {
			result = new MaterialiSpedizioneDao().findByAnagrafica(ses, idAnagrafica);
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
	public Integer saveOrUpdateMaterialiSpedizione(MaterialiSpedizione item) throws BusinessException {
		Session ses = SessionFactory.getSession();
		MaterialiSpedizioneDao efDao = new MaterialiSpedizioneDao();
		Integer idEf = null;
		Transaction trx = ses.beginTransaction();
		try {
			if (item.getMaterialeCmT() != null) {
				Materiali mat = new MaterialiDao().findByCodiceMeccanografico(ses, item.getMaterialeCmT());
				item.setMateriale(mat);
			}
			if (item.getId() != null) {
				efDao.update(ses, item);
				idEf = item.getId();
			} else {
				//salva
				idEf = (Integer) efDao.save(ses, item);
			}
			trx.commit();
		} catch (Exception e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return idEf;
	}

	@Override
	public Boolean deleteMaterialiSpedizione(Integer idMatSped) 
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		MaterialiSpedizioneDao msDao = new MaterialiSpedizioneDao();
		Transaction trx = ses.beginTransaction();
		try {
			MaterialiSpedizione ms = GenericDao.findById(ses, MaterialiSpedizione.class, idMatSped);
			msDao.delete(ses, ms);
			trx.commit();
		} catch (HibernateException e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return true;
	}
	
	
	
	@Override
	public List<MaterialiSpedizione> createAllArretrati(Integer idIa, Date today) throws BusinessException {
		List<MaterialiSpedizione> result = null;
		Session ses = SessionFactory.getSession();
		MaterialiSpedizioneDao maDao = new MaterialiSpedizioneDao();
		Transaction trx = ses.beginTransaction();
		try {
			IstanzeAbbonamenti ia = GenericDao.findById(ses, IstanzeAbbonamenti.class, idIa);
			result = maDao.enqueueMissingArretratiByStatus(ses, ia);
			trx.commit();
		} catch (Exception e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}

	@Override
	public List<MaterialiSpedizione> createAllArretrati(String codiceAbbonamento, Date today) throws BusinessException {
		List<MaterialiSpedizione> result = null;
		Session ses = SessionFactory.getSession();
		IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
		MaterialiSpedizioneDao msDao = new MaterialiSpedizioneDao();
		Transaction trx = ses.beginTransaction();
		try {
			IstanzeAbbonamenti ia = iaDao.findUltimaIstanzaByCodice(ses, codiceAbbonamento);
			result = msDao.enqueueMissingArretratiByStatus(ses, ia);
			trx.commit();
		} catch (Exception e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}

	
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
			Materiali mat = new MaterialiDao().findByCodiceMeccanografico(ses, item.getMaterialeCmT());
			item.setMateriale(mat);
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
			Materiali Mat = new MaterialiDao().findByCodiceMeccanografico(ses, item.getMaterialeCmT());
			item.setMateriale(Mat);
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
