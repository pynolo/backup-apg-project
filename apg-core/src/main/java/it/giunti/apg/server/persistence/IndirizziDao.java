package it.giunti.apg.server.persistence;

import it.giunti.apg.shared.model.Indirizzi;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.Session;

public class IndirizziDao implements BaseDao<Indirizzi> {

	@Override
	public void update(Session ses, Indirizzi instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
	}

	@Override
	public Serializable save(Session ses, Indirizzi transientInstance)
			throws HibernateException {
		Integer id = (Integer)GenericDao.saveGeneric(ses, transientInstance);
		return id;
	}

	@Override
	public void delete(Session ses, Indirizzi instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}
	
}
