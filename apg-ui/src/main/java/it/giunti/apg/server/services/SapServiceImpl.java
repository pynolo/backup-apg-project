package it.giunti.apg.server.services;

import it.giunti.apg.client.services.SapService;
import it.giunti.apg.core.persistence.EvasioniArticoliDao;
import it.giunti.apg.core.persistence.EvasioniFascicoliDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.OrdiniLogisticaDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.model.EvasioniArticoli;
import it.giunti.apg.shared.model.EvasioniFascicoli;
import it.giunti.apg.shared.model.OrdiniLogistica;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class SapServiceImpl extends RemoteServiceServlet implements SapService  {
	private static final long serialVersionUID = 1501017124140920250L;
	
	private static final Logger LOG = LoggerFactory.getLogger(SapServiceImpl.class);

	@Override
	public OrdiniLogistica findOrdineById(Integer idOrdine)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		OrdiniLogistica result = null;
		try {
			result = GenericDao.findById(ses, OrdiniLogistica.class, idOrdine);
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
	public List<OrdiniLogistica> findOrdini(boolean showAnnullati, int offset, int pageSize)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<OrdiniLogistica> result = null;
		try {
			result = new OrdiniLogisticaDao().findOrdini(ses, showAnnullati, offset, pageSize);
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
	public List<EvasioniFascicoli> findEvasioniFascicoliByOrdine(
			String numeroOrdine) throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<EvasioniFascicoli> result = null;
		try {
			result = new EvasioniFascicoliDao().findByNumeroOrdine(ses, numeroOrdine);
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
	public List<EvasioniArticoli> findEvasioniArticoliByOrdine(String numeroOrdine)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<EvasioniArticoli> result = null;
		try {
			result = new EvasioniArticoliDao().findByNumeroOrdine(ses, numeroOrdine);
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
