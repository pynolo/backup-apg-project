package it.giunti.apg.server.services;

import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import it.giunti.apg.client.services.LoggingService;
import it.giunti.apg.core.VisualLogger;
import it.giunti.apg.core.persistence.AvvisiDao;
import it.giunti.apg.core.persistence.FattureInvioSapDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.LogEditingDao;
import it.giunti.apg.core.persistence.RapportiDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.model.Avvisi;
import it.giunti.apg.shared.model.FattureInvioSap;
import it.giunti.apg.shared.model.LogEditing;
import it.giunti.apg.shared.model.Rapporti;

public class LoggingServiceImpl extends RemoteServiceServlet implements LoggingService  {
	private static final long serialVersionUID = -6601009225301501995L;
	
	private static final Logger LOG = LoggerFactory.getLogger(LoggingServiceImpl.class);
	private static final int SLEEP_SECONDS = 2;

	@Override
	public List<String> receiveLogLines(int idRapporto, int expectedLine) throws EmptyResultException {
		sleepSomeSeconds(SLEEP_SECONDS);
		List<String> result = VisualLogger.get().getLogFromLine(idRapporto, expectedLine);
		if (result == null) throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
		return result;
	}
	
	private void sleepSomeSeconds(int sec) {
		try {
			Thread.sleep(sec * 1000);
		} catch (InterruptedException e) {
			//e.printStackTrace();
		}
	}

	@Override
	public Integer createRapporto(String titolo, String idUtente) throws BusinessException, EmptyResultException {
		Integer idRapporto = VisualLogger.get().createRapporto(titolo, idUtente);
		return idRapporto;
	}
	

	@Override
	public List<Rapporti> findRapportiStripped(Date extractionDt, int offset, int size) throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<Rapporti> result = null;
		try {
			result = new RapportiDao().findRapportiStripped(ses, extractionDt, offset, size);
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
	
	

	//Avvisi
	
	
	
	@Override
	public List<Avvisi> findLastAvvisi(int offset, int size) throws BusinessException,
			EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<Avvisi> result = null;
		try {
			result = new AvvisiDao().findLastAvvisi(ses, offset, size);
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
	public List<Avvisi> findLastAvvisiByGiorniTipo(int giorniAntecedenti) throws BusinessException {
		Session ses = SessionFactory.getSession();
		List<Avvisi> result = null;
		try {
			result = new AvvisiDao().findLastAvvisiByGiorniTipo(ses, giorniAntecedenti);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	@Override
	public Integer saveAvviso(Avvisi avviso)
			throws BusinessException {
		Session ses = SessionFactory.getSession();
		Integer id = null;
		Transaction trx = ses.beginTransaction();
		try {
			id = (Integer) new AvvisiDao().save(ses, avviso);
			trx.commit();
		} catch (HibernateException e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return id;
	}

	@Override
	public List<Avvisi> deleteAvviso(Integer idAvviso, int pageSize)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		AvvisiDao avvDao = new AvvisiDao();
		Transaction trx = ses.beginTransaction();
		try {
			Avvisi n = GenericDao.findById(ses, Avvisi.class, idAvviso);
			avvDao.delete(ses, n);
			trx.commit();
		} catch (HibernateException e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return findLastAvvisi(0, pageSize);
	}

	@Override
	public Boolean updateImportanza(Integer idAvviso, boolean importante)
			throws BusinessException {
		boolean result = false;
		Session ses = SessionFactory.getSession();
		Transaction trx = ses.beginTransaction();
		AvvisiDao avvDao = new AvvisiDao();
		try {
			Avvisi avviso = GenericDao.findById(ses, Avvisi.class, idAvviso);
			avviso.setImportante(importante);
			avvDao.update(ses, avviso);
			trx.commit();
			result = true;
		} catch (HibernateException e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	@Override
	public Avvisi checkMaintenance() throws BusinessException {
		Session ses = SessionFactory.getSession();
		Avvisi result = null;
		try {
			List<Avvisi> aList = new AvvisiDao()
					.findMaintenanceAfterDate(ses, DateUtil.now());
			if (aList.size() > 0) result = aList.get(0);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}

	@Override
	public Rapporti findRapportoById(Integer idRapporto)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		Rapporti result = null;
		try {
			result = GenericDao.findById(ses, Rapporti.class, idRapporto);
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

	// LogEditing
	
	@Override
	public List<LogEditing> findEditLogs(String classSimpleName, Integer entityId)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<LogEditing> result = null;
		try {
			result = new LogEditingDao().findByClassNameAndId(ses, classSimpleName, entityId);
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
	public List<FattureInvioSap> findFattureInvioSap(Long startDt, Long finishDt, int offset, int pageSize)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<FattureInvioSap> result = null;
		try {
			result = new FattureInvioSapDao().findFattureInvioSap(ses, 
					startDt, finishDt, offset, pageSize);
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
