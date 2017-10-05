package it.giunti.apg.server.services;

import it.giunti.apg.client.services.StatService;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.ListiniDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.core.persistence.StatAbbonatiDao;
import it.giunti.apg.core.persistence.StatInvioDao;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.StatData;
import it.giunti.apg.shared.model.Listini;
import it.giunti.apg.shared.model.Periodici;
import it.giunti.apg.shared.model.StatAbbonati;
import it.giunti.apg.shared.model.StatInvio;
import it.giunti.apg.shared.model.TipiAbbonamento;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class StatServiceImpl extends RemoteServiceServlet implements StatService {
	private static final long serialVersionUID = 9016920119218731566L;

	private static final Logger LOG = LoggerFactory.getLogger(StatServiceImpl.class);
	
	@Override
	public List<StatData<Periodici>> statTiraturaPeriodici() throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<StatData<Periodici>> result = new ArrayList<StatData<Periodici>>();
		StatAbbonatiDao saDao = new StatAbbonatiDao();
		try {
			List<Periodici> list = GenericDao.findByClass(ses, Periodici.class, "id");
			for (Periodici p:list) {
				Integer tiratura = saDao.findTiraturaByPeriodico(ses, p.getId());
				if (tiratura > 1) {
					StatData<Periodici> data = new StatData<Periodici>(p, tiratura);
					result.add(data);
				}
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
		throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	}

	@Override
	public List<StatData<TipiAbbonamento>> statTipiAbbPeriodico(Date date, Integer idPeriodico) throws BusinessException, EmptyResultException{
		Session ses = SessionFactory.getSession();
		List<StatData<TipiAbbonamento>> result = new ArrayList<StatData<TipiAbbonamento>>();
		ListiniDao lstDao = new ListiniDao();
		Date today = DateUtil.now();
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.YEAR, 3);
		Date fromDate = cal.getTime();
		try {
			List<Listini> lstList = lstDao.findListiniByPeriodicoDate(ses,
					idPeriodico, fromDate, null, 0, Integer.MAX_VALUE);
			for (Listini lst:lstList) {
				Integer count = lstDao.countProssimaTiraturaByListino(ses, today, lst);
				StatData<TipiAbbonamento> data = new StatData<TipiAbbonamento>(lst.getTipoAbbonamento(), count);
				result.add(data);
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
		throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	}

	@Override
	public List<StatAbbonati> findStatAbbonatiBetweenDates(Integer idPeriodico,
			Date dataInizio, Date dataFine) throws BusinessException,
			EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<StatAbbonati> result = null;
		try {
			result = new StatAbbonatiDao().findByDates(ses, idPeriodico, dataInizio, dataFine);
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
	public StatAbbonati findLastStatAbbonati(Integer idPeriodico) throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		StatAbbonati result = new StatAbbonati();
		try {
			result = new StatAbbonatiDao().findLast(ses, idPeriodico);
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
	public List<StatInvio> findLastStatInvio(Integer idPeriodico)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<StatInvio> result = null;
		try {
			result = new StatInvioDao().findLastStatInvio(ses, idPeriodico);
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
	public List<List<StatInvio>> findStatInvio(Integer idPeriodico)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<List<StatInvio>> result = null;
		try {
			result = new StatInvioDao().findOrderedStatInvio(ses, idPeriodico);
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

}
