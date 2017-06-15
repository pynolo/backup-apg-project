package it.giunti.apg.server.persistence;

import it.giunti.apg.shared.model.FattureArticoli;

import java.io.Serializable;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.IntegerType;

public class FattureArticoliDao implements BaseDao<FattureArticoli> {

	@Override
	public void update(Session ses, FattureArticoli instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
	}

	@Override
	public Serializable save(Session ses, FattureArticoli transientInstance)
			throws HibernateException {
		return GenericDao.saveGeneric(ses, transientInstance);
	}

	@Override
	public void delete(Session ses, FattureArticoli instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}
	
	@SuppressWarnings("unchecked")
	public List<FattureArticoli> findByFattura(Session ses, Integer idFattura) throws HibernateException {
		//Query
		String qs = "from FattureArticoli fa where " +
				"fa.idFattura = :i1 " +
				"order by fa.id asc ";
		Query q = ses.createQuery(qs);
		q.setParameter("i1", idFattura, IntegerType.INSTANCE);
		List<FattureArticoli> faList = (List<FattureArticoli>) q.list();
		return faList;
	}
	
}
