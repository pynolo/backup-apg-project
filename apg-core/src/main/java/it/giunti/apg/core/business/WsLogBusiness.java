package it.giunti.apg.core.business;

import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.core.persistence.WsLogDao;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.LogWs;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WsLogBusiness {

	private static final Logger LOG = LoggerFactory.getLogger(WsLogBusiness.class);
	
	public static Integer writeWsLog(String service, String operation,
			String parameters, String result) throws BusinessException {
		//Salvataggio
		Session ses = SessionFactory.getSession();
		Integer id = null;
		Transaction trx = ses.beginTransaction();
		try {
			writeWsLog(ses, service, operation, parameters, result);
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
	
	public static Integer writeWsLog(Session ses, String service, String operation,
			String parameters, String result) throws HibernateException {
		LogWs wl = new LogWs();
		wl.setService(service);
		wl.setOperation(operation);
		wl.setParameters(parameters);
		wl.setResult(result);
		wl.setLogDatetime(DateUtil.now());
		Integer id = (Integer) new WsLogDao().save(ses, wl);
		return id;
	}
}
