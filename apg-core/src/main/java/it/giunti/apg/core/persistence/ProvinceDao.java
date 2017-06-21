package it.giunti.apg.core.persistence;

import it.giunti.apg.shared.model.Province;

import java.io.Serializable;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class ProvinceDao implements BaseDao<Province> {

	@Override
	public void update(Session ses, Province instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
	}

	@Override
	public Serializable save(Session ses, Province transientInstance)
			throws HibernateException {
		return GenericDao.saveGeneric(ses, transientInstance);
	}

	@Override
	public void delete(Session ses, Province instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}
	
	@SuppressWarnings("unchecked")
	public Province findByName(Session ses, String name) throws HibernateException {
		QueryFactory qf = new QueryFactory(ses, "from Province c");
		qf.addWhere("c.nomeProvincia = :p1");
		qf.addParam("p1", name);
		Query q = qf.getQuery();
		List<Province> list = (List<Province>) q.list();
		if (list != null) {
			if (list.size() > 0) {
				return list.get(0);
			}
		}
		return null;
	}
	
}
