package it.giunti.apg.core.persistence;

import it.giunti.apg.shared.model.FattureStampe;

import java.io.Serializable;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class FattureStampeDao implements BaseDao<FattureStampe> {

	@Override
	public void update(Session ses, FattureStampe instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
	}

	@Override
	public Serializable save(Session ses, FattureStampe transientInstance)
			throws HibernateException {
		return GenericDao.saveGeneric(ses, transientInstance);
	}

	@Override
	public void delete(Session ses, FattureStampe instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}
	
	public boolean isFatturaStampa(Session ses, Integer idFatturaStampa) {
		String hql = "select fs.id from FattureStampe fs where "+
				"fs.id = :id1";
		Query q = ses.createQuery(hql);
		q.setParameter("id1", idFatturaStampa);
		@SuppressWarnings("rawtypes")
		List list = q.list();
		if (list == null) return false;
		if (list.size() == 0) return false;
		return true;
	}
	
}
