package it.giunti.apg.server.services;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import it.giunti.apg.client.services.UtilService;
import it.giunti.apg.core.PropertyReader;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.RinnoviMassiviDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.model.RinnoviMassivi;

public class UtilServiceImpl extends RemoteServiceServlet implements UtilService {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8020867457648219678L;
	private static final Logger LOG = LoggerFactory.getLogger(UtilServiceImpl.class);

	@Override
	public String getApgTitle() throws EmptyResultException {
		return PropertyReader.getApgTitle();
	}
	
	@Override
	public String getApgStatus() throws EmptyResultException {
		return PropertyReader.getApgStatus();
	}
	
	@Override
	public String getApgMenuImage() throws EmptyResultException {
		return PropertyReader.getApgMenuImage();
	}
	
	@Override
	public String getApgLoginImage() throws EmptyResultException {
		return PropertyReader.getApgLoginImage();
	}
	
	@Override
	public String getApgVersion() throws EmptyResultException {
		return PropertyReader.getApgVersion();
	}


	// Rinnovi massivi
	
	
	@Override
	public List<RinnoviMassivi> findRinnoviMassivi(Integer idPeriodico) throws BusinessException,
			EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<RinnoviMassivi> result = null;
		try {
			result = new RinnoviMassiviDao().findByPeriodico(ses, idPeriodico);
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
	public Boolean saveOrUpdateRinnoviMassiviList(
			List<RinnoviMassivi> rinnoviMassiviList) throws BusinessException {
		Session ses = SessionFactory.getSession();
		Transaction trx = ses.beginTransaction();
		RinnoviMassiviDao rmDao = new RinnoviMassiviDao();
		try {
			for (RinnoviMassivi rm:rinnoviMassiviList) {
				RinnoviMassivi persistent = null;
				if (rm.getId() != null) {
					persistent = GenericDao.findById(ses, RinnoviMassivi.class, rm.getId());
				}
				if (persistent != null) {
					rmDao.update(ses, rm);
				} else {
					rmDao.save(ses, rm);
				}
			}
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
	public Boolean deleteRinnovoMassivo(Integer idRinnovoMassivo)
			throws BusinessException {
		Session ses = SessionFactory.getSession();
		Transaction trx = ses.beginTransaction();
		try {
			RinnoviMassivi persistent = null;
			if (idRinnovoMassivo != null) {
				persistent = GenericDao.findById(ses, RinnoviMassivi.class, idRinnovoMassivo);
			}
			if (persistent != null) {
				new RinnoviMassiviDao().delete(ses, persistent);
			}
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


}
