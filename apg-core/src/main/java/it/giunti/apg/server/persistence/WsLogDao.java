package it.giunti.apg.server.persistence;

import it.giunti.apg.shared.model.LogWs;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.Session;

public class WsLogDao implements BaseDao<LogWs> {


	@Override
	public void update(Session ses, LogWs instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
	}

	@Override
	public Serializable save(Session ses, LogWs transientInstance)
			throws HibernateException {
		Serializable key = ses.save(transientInstance);
		return key;
	}
	
	@Override
	public void delete(Session ses, LogWs instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}
	
}
