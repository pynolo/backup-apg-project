package it.giunti.apg.core.business;

import it.giunti.apg.core.persistence.ContatoriDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContatoriBusiness {

	private static final Logger LOG = LoggerFactory.getLogger(ContatoriBusiness.class);
	
	public static synchronized String generateUidCliente() throws BusinessException {
		//Se non ha codice cliente lo crea e associa
		Session ses = SessionFactory.getSession();
		Transaction trx = ses.beginTransaction();
		String uid = null;
		try {
			uid = new ContatoriDao().generateUidCliente(ses);
			trx.commit();
		} catch (HibernateException e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return uid;
	}
	
}
