package it.giunti.apg.server.persistence;

import it.giunti.apg.shared.model.ModelliEmail;

import java.io.Serializable;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class ModelliEmailDao implements BaseDao<ModelliEmail> {

	@Override
	public void update(Session ses, ModelliEmail instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
	}

	@Override
	public Serializable save(Session ses, ModelliEmail transientInstance)
			throws HibernateException {
		return GenericDao.saveGeneric(ses, transientInstance);
	}

	@Override
	public void delete(Session ses, ModelliEmail instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}
	
	@SuppressWarnings("unchecked")
	public List<ModelliEmail> findModelliEmail(
			Session ses,
			Integer offset, Integer size) throws HibernateException {
		String qs = "from ModelliEmail me " +
				"order by me.descr asc ";
		Query q = ses.createQuery(qs);
		q.setFirstResult(offset);
	    q.setMaxResults(size);
		List<ModelliEmail> meList = (List<ModelliEmail>) q.list();
		return meList;
	}
}
