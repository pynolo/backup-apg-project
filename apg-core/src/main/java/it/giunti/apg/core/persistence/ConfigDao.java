package it.giunti.apg.core.persistence;

import it.giunti.apg.shared.model.Config;

import java.io.Serializable;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.StringType;

public class ConfigDao implements BaseDao<Config> {


	@Override
	public void update(Session ses, Config instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
	}

	@Override
	public Serializable save(Session ses, Config transientInstance)
			throws HibernateException {
		Serializable key = ses.save(transientInstance);
		return key;
	}
	
	@Override
	public void delete(Session ses, Config instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}
	
	@SuppressWarnings("unchecked")
	public String findValore(Session ses, String id) throws HibernateException {
		String hql = "from Config where id = :id1";
		Query q = ses.createQuery(hql);
		q.setParameter("id1", id, StringType.INSTANCE);
		List<Config> cList = q.list();
		if (cList.size() > 0) {
			return cList.get(0).getValore();
		} else {
			return null;
		}
	}
	
}
