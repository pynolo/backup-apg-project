package it.giunti.apg.server.persistence;

import it.giunti.apg.shared.model.OpzioniListini;

import java.io.Serializable;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class OpzioniListiniDao implements BaseDao<OpzioniListini> {

	@Override
	public void update(Session ses, OpzioniListini instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
	}

	@Override
	public Serializable save(Session ses, OpzioniListini transientInstance)
			throws HibernateException {
		return GenericDao.saveGeneric(ses, transientInstance);
	}

	@Override
	public void delete(Session ses, OpzioniListini instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}
	
	@SuppressWarnings("unchecked")
	public List<OpzioniListini> findOpzioniListiniByListino(Session ses,
			Integer idListino) throws HibernateException {
		QueryFactory qf = new QueryFactory(ses, "from OpzioniListini ol");
		qf.addWhere("ol.listino.id = :id1");
		qf.addParam("id1", idListino);
		qf.addOrder("ol.id asc");
		Query q = qf.getQuery();
		List<OpzioniListini> olList = (List<OpzioniListini>) q.list();
		return olList;
	}

}
