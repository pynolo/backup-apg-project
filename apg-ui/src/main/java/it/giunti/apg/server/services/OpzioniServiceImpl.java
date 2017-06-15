package it.giunti.apg.server.services;

import it.giunti.apg.client.services.OpzioniService;
import it.giunti.apg.server.persistence.AliquoteIvaDao;
import it.giunti.apg.server.persistence.GenericDao;
import it.giunti.apg.server.persistence.OpzioniDao;
import it.giunti.apg.server.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.model.AliquoteIva;
import it.giunti.apg.shared.model.Fascicoli;
import it.giunti.apg.shared.model.Opzioni;
import it.giunti.apg.shared.model.Periodici;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class OpzioniServiceImpl extends RemoteServiceServlet implements
		OpzioniService {
	private static final long serialVersionUID = 1585191638678662526L;

	private static final Logger LOG = LoggerFactory.getLogger(OpzioniServiceImpl.class);

	@Override
	public Opzioni findOpzioneById(Integer idOpzione) throws BusinessException,
			EmptyResultException {
		Session ses = SessionFactory.getSession();
		Opzioni result = null;
		try {
			result = GenericDao.findById(ses, Opzioni.class, idOpzione);
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
	public Opzioni createOpzione(Integer idPeriodico) throws BusinessException {
		Session ses = SessionFactory.getSession();
		Opzioni result = null;
		try {
			Date today = new Date();
			result = new Opzioni();
			result.setCartaceo(false);
			result.setDigitale(false);
			result.setUid("");
			result.setDataInizio(today);
			result.setDataFine(null);
			result.setDataModifica(today);
			result.setPrezzo(0D);
			// Periodico
			Periodici periodico = GenericDao.findById(ses, Periodici.class,
					idPeriodico);
			result.setPeriodico(periodico);
			// IVA
			AliquoteIva iva = new AliquoteIvaDao()
					.findDefaultAliquotaIvaByDate(ses,
							AppConstants.DEFAULT_ALIQUOTA_IVA, new Date());
			result.setAliquotaIva(iva);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}

	@Override
	public Integer saveOrUpdateOpzione(Opzioni item) throws BusinessException {
		Session ses = SessionFactory.getSession();
		Integer idOpz = null;
		Transaction trx = ses.beginTransaction();
		try {
			Periodici periodico = null;
			if (item.getIdPeriodicoT() != null) {
				periodico = GenericDao.findById(ses, Periodici.class,
						item.getIdPeriodicoT());
			}
			item.setPeriodico(periodico);
			AliquoteIva iva = null;
			if (item.getIdAliquotaIvaT() != null) {
				iva = GenericDao.findById(ses, AliquoteIva.class,
						item.getIdAliquotaIvaT());
			}
			item.setAliquotaIva(iva);
			if (item.getDataModifica() == null)
				item.setDataModifica(new Date());
			if (item.getId() != null) {
				new OpzioniDao().update(ses, item);
				idOpz = item.getId();
			} else {
				// salva
				idOpz = (Integer) new OpzioniDao().save(ses, item);
			}
			trx.commit();
		} catch (HibernateException e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return idOpz;
	}

	@Override
	public List<Opzioni> findOpzioni(Long startDt, Long finishDt)
			throws BusinessException {
		Session ses = SessionFactory.getSession();
		List<Opzioni> result = null;
		try {
			result = new OpzioniDao().findByDate(ses, startDt, finishDt);
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
		return null;
	}

	@Override
	public List<Opzioni> findOpzioni(Date extractionDt)
			throws BusinessException {
		Session ses = SessionFactory.getSession();
		List<Opzioni> result = null;
		try {
			result = new OpzioniDao().findByDate(ses, extractionDt);
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
		return null;
	}

	@Override
	public List<Opzioni> findOpzioni(Integer idPeriodico, Date extractionDt,
			Boolean soloCartacei) throws BusinessException {
		Session ses = SessionFactory.getSession();
		List<Opzioni> result = null;
		try {
			result = new OpzioniDao().findByPeriodicoDate(ses, idPeriodico,
					extractionDt, soloCartacei);
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
		return null;
	}

	@Override
	public List<Opzioni> findOpzioni(Integer idPeriodico, Date startDt,
			Date finishDt, Boolean soloCartacei) throws BusinessException {
		Session ses = SessionFactory.getSession();
		List<Opzioni> result = null;
		try {
			result = new OpzioniDao().findByPeriodicoDate(ses, idPeriodico,
					startDt, finishDt, soloCartacei);
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
		return null;
	}

	@Override
	public List<Opzioni> findOpzioni(Integer idPeriodico, Integer idFascicolo)
			throws BusinessException {
		Session ses = SessionFactory.getSession();
		List<Opzioni> result = null;
		try {
			Fascicoli fas = GenericDao.findById(ses, Fascicoli.class,
					idFascicolo);
			result = new OpzioniDao().findByPeriodicoDate(ses, idPeriodico,
					fas.getDataInizio(), false);
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
		return null;
	}

	@Override
	public List<Opzioni> findOpzioniByListino(Integer idListino,
			Integer idFascicolo) throws BusinessException {
		Session ses = SessionFactory.getSession();
		List<Opzioni> result = new ArrayList<Opzioni>();
		try {
			result = new OpzioniDao().findOpzioniByListino(ses, idListino);
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
		return null;
	}

	@Override
	public List<Opzioni> findOpzioniFacoltativeByListino(Integer idListino, Integer idFascicolo)
			throws BusinessException {
		Session ses = SessionFactory.getSession();
		OpzioniDao opzDao = new OpzioniDao();
		List<Opzioni> result = new ArrayList<Opzioni>();
		try {
			Fascicoli fas = GenericDao.findById(ses, Fascicoli.class, idFascicolo);
			List<Opzioni> allOpzioniList = opzDao.findByPeriodicoDate(ses, fas.getPeriodico().getId(),
					fas.getDataInizio(), false);
			List<Opzioni> mandatoryList = opzDao.findOpzioniByListino(ses, idListino);
			for (Opzioni opz:allOpzioniList) {
				if (!mandatoryList.contains(opz)) result.add(opz);
			}
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
		return null;
	}

}
