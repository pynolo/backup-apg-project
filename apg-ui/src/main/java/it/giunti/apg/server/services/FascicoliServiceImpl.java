package it.giunti.apg.server.services;

import it.giunti.apg.client.services.FascicoliService;
import it.giunti.apg.core.OpzioniUtil;
import it.giunti.apg.core.persistence.EvasioniFascicoliDao;
import it.giunti.apg.core.persistence.FascicoliDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.EvasioniFascicoli;
import it.giunti.apg.shared.model.Fascicoli;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Opzioni;
import it.giunti.apg.shared.model.Periodici;

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

public class FascicoliServiceImpl extends RemoteServiceServlet implements FascicoliService  {
	private static final long serialVersionUID = -8104049988180421660L;
	
	private static final Logger LOG = LoggerFactory.getLogger(FascicoliServiceImpl.class);
	
	@Override
	public List<Fascicoli> findFascicoliByPeriodico(Integer idPeriodico, long startDt, long finishDt,
			boolean includeOpzioni, boolean orderAsc,
			int offset, int pageSize) throws BusinessException, EmptyResultException {
		if(idPeriodico == null) return new ArrayList<Fascicoli>();
		Session ses = SessionFactory.getSession();
		List<Fascicoli> result = null;
		try {
			result = new FascicoliDao().findFascicoliByPeriodico(ses, idPeriodico, null,
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
	public List<Fascicoli> findFascicoliByPeriodico(Integer idPeriodico,
			Integer selectedId, long startDt, long finishDt,
			boolean includeOpzioni, boolean orderAsc, int offset, int pageSize)
			throws BusinessException, EmptyResultException {
		if(idPeriodico == null) return new ArrayList<Fascicoli>();
		Session ses = SessionFactory.getSession();
		List<Fascicoli> result = null;
		try {
			result = new FascicoliDao().findFascicoliByPeriodico(ses, idPeriodico, selectedId,
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
	public List<Fascicoli> findFascicoliByOpzione(Integer idOpzione,
			boolean orderAsc, int offset, int pageSize)
			throws BusinessException, EmptyResultException {
		if(idOpzione == null) return new ArrayList<Fascicoli>();
		Session ses = SessionFactory.getSession();
		List<Fascicoli> result = null;
		try {
			result = new FascicoliDao().findFascicoliByOpzione(ses, idOpzione,
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
	public EvasioniFascicoli createEvasioneFascicoloForIstanza(Integer idIstanza, String idTipoEvasione)
			throws BusinessException {
		Session ses = SessionFactory.getSession();
		EvasioniFascicoli result = null;
		IstanzeAbbonamenti istanza = (IstanzeAbbonamenti) ses.get(IstanzeAbbonamenti.class, idIstanza);
		try {
			Date today = DateUtil.now();
			result = new EvasioniFascicoli();
			result.setDataCreazione(today);
			result.setDataModifica(today);
			result.setDataInvio(null);
			result.setDataOrdine(null);
			result.setFascicolo(null);
			result.setIdTipoEvasione(idTipoEvasione);
			result.setNote("");
			if (istanza != null) {
				result.setIdAbbonamento(istanza.getAbbonamento().getId());
				result.setIdIstanzaAbbonamento(istanza.getId());
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
	public EvasioniFascicoli createEvasioneFascicoloForAnagrafica(Integer idAnagrafica, Integer copie, String idTipoEvasione)
			throws BusinessException {
		Session ses = SessionFactory.getSession();
		EvasioniFascicoli result = null;
		try {
			Date today = DateUtil.now();
			result = new EvasioniFascicoli();
			result.setDataCreazione(today);
			result.setDataModifica(today);
			result.setDataInvio(null);
			result.setDataOrdine(null);
			result.setFascicolo(null);
			result.setIdAbbonamento(null);
			result.setIdIstanzaAbbonamento(null);
			result.setIdAnagrafica(idAnagrafica);
			result.setIdTipoEvasione(idTipoEvasione);
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
	public EvasioniFascicoli findEvasioneFascicoloById(
			Integer idEvasioneFascicolo) throws BusinessException,
			EmptyResultException {
		Session ses = SessionFactory.getSession();
		EvasioniFascicoli result = null;
		try {
			result = GenericDao.findById(ses, EvasioniFascicoli.class, idEvasioneFascicolo);
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
	public List<EvasioniFascicoli> findEvasioniFascicoliByIstanza(Integer idIstanza) throws BusinessException, EmptyResultException {
		if (idIstanza == null) return null;
		Session ses = SessionFactory.getSession();
		List<EvasioniFascicoli> result = null;
		try {
			IstanzeAbbonamenti istanza = (IstanzeAbbonamenti) ses.get(IstanzeAbbonamenti.class, idIstanza);
			result = new EvasioniFascicoliDao().findByIstanza(ses, istanza);
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
	public Integer saveOrUpdate(EvasioniFascicoli item)
			throws BusinessException {
		Session ses = SessionFactory.getSession();
		EvasioniFascicoliDao efDao = new EvasioniFascicoliDao();
		Integer idEf = null;
		Transaction trx = ses.beginTransaction();
		try {
			if (item.getIdFascicoliT() != null) {
				Integer id = Integer.valueOf(item.getIdFascicoliT());
				Fascicoli fasc = GenericDao.findById(ses, Fascicoli.class, id);
				item.setFascicolo(fasc);
			}
			if (item.getId() != null) {
				efDao.update(ses, item);
				idEf = item.getId();
			} else {
				//salva
				idEf = (Integer) efDao.save(ses, item);
			}
			efDao.updateFascicoliSpediti(ses, item.getIdIstanzaAbbonamento());
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
	public List<EvasioniFascicoli> deleteEvasioneFascicolo(Integer idIstanza,
			Integer idEvasioneFascicolo) throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		EvasioniFascicoliDao efDao = new EvasioniFascicoliDao();
		Transaction trx = ses.beginTransaction();
		try {
			EvasioniFascicoli ef = GenericDao.findById(ses, EvasioniFascicoli.class, idEvasioneFascicolo);
			efDao.delete(ses, ef);
			efDao.updateFascicoliSpediti(ses, idIstanza);
			trx.commit();
		} catch (HibernateException e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return findEvasioniFascicoliByIstanza(idIstanza);
	}

	@Override
	public List<EvasioniFascicoli> createMassiveArretrati(Integer idIa, Date today, String idUtente)
			throws BusinessException {
		List<EvasioniFascicoli> result = null;
		Session ses = SessionFactory.getSession();
		EvasioniFascicoliDao efDao = new EvasioniFascicoliDao();
		Transaction trx = ses.beginTransaction();
		try {
			IstanzeAbbonamenti ia = GenericDao.findById(ses, IstanzeAbbonamenti.class, idIa);
			result = efDao.enqueueMissingArretratiByStatus(ses, ia, today, idUtente);
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
	public List<EvasioniFascicoli> createMassiveArretrati(String codiceAbbonamento, Date today, String idUtente) throws BusinessException {
		List<EvasioniFascicoli> result = null;
		Session ses = SessionFactory.getSession();
		IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
		EvasioniFascicoliDao efDao = new EvasioniFascicoliDao();
		Transaction trx = ses.beginTransaction();
		try {
			IstanzeAbbonamenti ia = iaDao.findUltimaIstanzaByCodice(ses, codiceAbbonamento);
			result = efDao.enqueueMissingArretratiByStatus(ses, ia, today, idUtente);
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
	public Fascicoli findFascicoloByPeriodicoDataInizio(
			Integer idPeriodico, Date date) throws BusinessException {
		Session ses = SessionFactory.getSession();
		Fascicoli result = null;
		try {
			result = new FascicoliDao().findFascicoloByPeriodicoDataInizio(ses, idPeriodico, date);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	@Override
	public Fascicoli findPrimoFascicoloNonSpedito(Integer idPeriodico, Date date, Boolean includeAllegati)
			throws BusinessException {
		Session ses = SessionFactory.getSession();
		Fascicoli result = null;
		try {
			result = new FascicoliDao().findPrimoFascicoloNonSpedito(ses, idPeriodico, date, includeAllegati);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	

	@Override
	public Map<Fascicoli, Integer> findFascicoliByEnqueuedMedia(String idTipoMedia)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		Map<Fascicoli, Integer> result = null;
		try {
			result = new FascicoliDao().findByEnqueuedComunicazioniMedia(ses, idTipoMedia);
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

//	@Override
//	public Fascicoli findFascicoliAfterFascicolo(Integer idOldFascicolo, Integer fascicoliCount)
//			throws PagamentiException {
//		Session ses = SessionFactory.getSession();
//		Fascicoli result = null;
//		try {
//			result = new FascicoliDao().findFascicoliAfterFascicolo(ses, idOldFascicolo, fascicoliCount);
//		} catch (HibernateException e) {
//			LOG.error(e.getMessage(), e);
//			throw new PagamentiException(e.getMessage(), e);
//		} finally {
//			ses.close();
//		}
//		return result;
//	}
//	
//	@Override
//	public Fascicoli findFascicoliBeforeFascicolo(Integer idOldFascicolo, Integer fascicoliCount)
//			throws PagamentiException {
//		Session ses = SessionFactory.getSession();
//		Fascicoli result = null;
//		try {
//			result = new FascicoliDao().findFascicoliBeforeFascicolo(ses, idOldFascicolo, fascicoliCount);
//		} catch (HibernateException e) {
//			LOG.error(e.getMessage(), e);
//			throw new PagamentiException(e.getMessage(), e);
//		} finally {
//			ses.close();
//		}
//		return result;
//	}

	//@Override
	//public List<Fascicoli> findFascicoliBetweenFascicoli(String idPeriodicoString,
	//		String idFasInizio, String idFasFine) throws PagamentiException {
	//	Session ses = SessionFactory.getSession();
	//	List<Fascicoli> result = null;
	//	try {
	//		Fascicoli fasInizio = (Fascicoli) ses.get(Fascicoli.class, Integer.parseInt(idFasInizio));
	//		Fascicoli fasFine = (Fascicoli) ses.get(Fascicoli.class, Integer.parseInt(idFasFine));
	//		Integer idPeriodico = ValueUtil.stoi(idPeriodicoString);
	//		if ((fasInizio != null) && (fasFine != null)) {
	//			result = new FascicoliDao().findFascicoliBetweenDates(ses, idPeriodico,
	//					fasInizio.getDataNominale(), fasFine.getDataNominale());
	//		} else {
	//			throw new PagamentiException("I fascicoli di inizio o fine intervallo non esistono");
	//		}
	//	} catch (HibernateException e) {
	//		LOG.error(e.getMessage(), e);
	//		throw new PagamentiException(e.getMessage(), e);
	//	} finally {
	//		ses.close();
	//	}
	//	return result;
	//}

	@Override
	public Integer countFascicoliBetweenFascicoli(String idPeriodicoString,
			String idFasInizio, String idFasFine) throws BusinessException {
		Session ses = SessionFactory.getSession();
		Integer result = null;
		try {
			Fascicoli fasInizio = (Fascicoli) ses.get(Fascicoli.class, Integer.parseInt(idFasInizio));
			Fascicoli fasFine = (Fascicoli) ses.get(Fascicoli.class, Integer.parseInt(idFasFine));
			Integer idPeriodico = ValueUtil.stoi(idPeriodicoString);
			if ((fasInizio != null) && (fasFine != null)) {
				List<Fascicoli> fList = new FascicoliDao().findFascicoliBetweenDates(ses, idPeriodico,
						fasInizio.getDataInizio(), fasFine.getDataInizio());
				int totFascicoli = 0;
				for (Fascicoli fas:fList) {
					totFascicoli += fas.getFascicoliAccorpati();
				}
				result = totFascicoli;
			} else {
				throw new BusinessException("I fascicoli di inizio o fine intervallo non esistono");
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
	public Integer countFascicoliSpediti(Integer idIstanza)
			throws BusinessException {
		if (idIstanza != null) {
			if (idIstanza.intValue() > 0) {
				Session ses = SessionFactory.getSession();
				Integer result = null;
				try {
					result = new EvasioniFascicoliDao().countFascicoliSpediti(ses, idIstanza);
				} catch (HibernateException e) {
					LOG.error(e.getMessage(), e);
					throw new BusinessException(e.getMessage(), e);
				} finally {
					ses.close();
				}
				return result;
			}
		}
		return 0;
	}

	@Override
	public Integer countFascicoliTotali(Integer idIstanza)
			throws BusinessException {
		if (idIstanza != null) {
			if (idIstanza.intValue() > 0) {
				Session ses = SessionFactory.getSession();
				Integer result = null;
				try {
					result = new EvasioniFascicoliDao().countFascicoliTotali(ses, idIstanza);
				} catch (HibernateException e) {
					LOG.error(e.getMessage(), e);
					throw new BusinessException(e.getMessage(), e);
				} finally {
					ses.close();
				}
				return result;
			}
		}
		return 0;
	}
	
	@Override
	public Integer countFascicoliDaSpedire(Integer idIstanza)
			throws BusinessException {
		if (idIstanza != null) {
			if (idIstanza.intValue() > 0) {
				Session ses = SessionFactory.getSession();
				Integer result = null;
				try {
					result = new EvasioniFascicoliDao().countFascicoliDaSpedire(ses, idIstanza);
				} catch (HibernateException e) {
					LOG.error(e.getMessage(), e);
					throw new BusinessException(e.getMessage(), e);
				} finally {
					ses.close();
				}
				return result;
			}
		}
		return 0;
	}
	
	@Override
	public Boolean verifyFascicoloWithinIstanza(Integer idIstanza, Integer idEvasioneFascicolo)
			throws BusinessException {
		Session ses = SessionFactory.getSession();
		EvasioniFascicoli ef = null;
		IstanzeAbbonamenti ia = null;
		boolean result = true;
		try {
			ef = GenericDao.findById(ses, EvasioniFascicoli.class, idEvasioneFascicolo);
			ia = GenericDao.findById(ses, IstanzeAbbonamenti.class, idIstanza);
			if ((ia == null) || (ef == null)) throw new BusinessException("Nessuna corrispondenza trovata");
			Date dataFascicolo = ef.getFascicolo().getDataInizio();
			if (ia.getFascicoloInizio().getDataInizio().after(dataFascicolo)) result = false;
			if (ia.getFascicoloFine().getDataFine().before(dataFascicolo)) result = false;
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	@Override
	public Fascicoli findFascicoloById(Integer idFas) throws BusinessException,
			EmptyResultException {
		Session ses = SessionFactory.getSession();
		Fascicoli result = null;
		try {
			result = GenericDao.findById(ses, Fascicoli.class, idFas);
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
	public Fascicoli createFascicolo(Integer idPeriodico, Boolean isOpzione) throws BusinessException {
		Session ses = SessionFactory.getSession();
		Fascicoli f = new Fascicoli();
		f.setFascicoliAccorpati(1);
		try {
			Date today = DateUtil.now();
			Periodici p = GenericDao.findById(ses, Periodici.class, idPeriodico);
			f.setPeriodico(p);
			f.setDataInizio(today);
			f.setIdTipoAnagraficaSap(AppConstants.ANAGRAFICA_SAP_GE_LIBRO);
			//f.setIdSocieta(p.getIdSocieta());
			if (isOpzione) {
				f.setFascicoliAccorpati(0);
			} else {
				f.setFascicoliAccorpati(1);
			}
			f.setInAttesa(true);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return f;
	}

	@Override
	public Integer saveOrUpdate(Fascicoli item) throws BusinessException {
		Session ses = SessionFactory.getSession();
		Integer idFas = null;
		Transaction trx = ses.beginTransaction();
		FascicoliDao fasDao = new FascicoliDao();
		try {
			if (item.getIdPeriodicoT() != null) {
				Integer id = Integer.valueOf(item.getIdPeriodicoT());
				Periodici p = GenericDao.findById(ses, Periodici.class, id);
				item.setPeriodico(p);
			}
			if (item.getIdOpzioneT() != null) {
				Integer id = Integer.valueOf(item.getIdOpzioneT());
				Opzioni s = GenericDao.findById(ses, Opzioni.class, id);
				item.setOpzione(s);
			}
			if (item.getOpzione() != null) {
				item.setFascicoliAccorpati(0);
			} //else {
			//	item.setFascicoliAccorpati(1);
			//}
			//if (item.getIdFascicoloAbbinatoT() != null) {
			//	Integer id = Integer.valueOf(item.getIdFascicoloAbbinatoT());
			//	Fascicoli f = GenericDao.findById(ses, Fascicoli.class, id);
			//	item.setFascicoloAbbinato(f);
			//}
			if (item.getId() != null) {
				fasDao.update(ses, item);
				idFas = item.getId();
			} else {
				//salva
				idFas = (Integer) fasDao.save(ses, item);
			}
			trx.commit();
		} catch (Exception e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return idFas;
	}

	@Override
	public String getOpzioniDescr(String opzioniList) throws BusinessException {
		Session ses = SessionFactory.getSession();
		String result = "";
		try {
			OpzioniUtil sUtil = new OpzioniUtil(opzioniList);
			List<Integer> sList = sUtil.getOpzioniIdList();
			for (Integer idSup:sList) {
				Opzioni sup = GenericDao.findById(ses, Opzioni.class, idSup);
				if (sup == null) throw new BusinessException("Non esiste opzione con id="+idSup);
				if (result.length() > 0) result += ", ";
				result += sup.getNome();
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}


	@Override
	public List<Fascicoli> deleteFascicolo(Integer idFas)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		FascicoliDao fasDao = new FascicoliDao();
		Transaction trx = ses.beginTransaction();
		Integer idPeriodico = null;
		try {
			Fascicoli f = GenericDao.findById(ses, Fascicoli.class, idFas);
			idPeriodico = f.getPeriodico().getId();
			fasDao.delete(ses, f);
			trx.commit();
		} catch (HibernateException e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		long now = DateUtil.now().getTime();
		long startDt = now - AppConstants.MONTH * 24;
		long finishDt = now + AppConstants.MONTH * 120;
		return findFascicoliByPeriodico(idPeriodico, startDt, finishDt, true, true, 0, 50);
	}



}
