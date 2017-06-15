package it.giunti.apg.server.persistence;

import it.giunti.apg.shared.model.Nazioni;

import java.io.Serializable;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class NazioniDao implements BaseDao<Nazioni> {

	@Override
	public void update(Session ses, Nazioni instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
	}

	@Override
	public Serializable save(Session ses, Nazioni transientInstance)
			throws HibernateException {
		return GenericDao.saveGeneric(ses, transientInstance);
	}

	@Override
	public void delete(Session ses, Nazioni instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}
	
	@SuppressWarnings("unchecked")
	public Nazioni findByName(Session ses, String name) throws HibernateException {
		QueryFactory qf = new QueryFactory(ses, "from Nazioni c");
		qf.addWhere("c.nomeNazione = :p1");
		qf.addParam("p1", name);
		Query q = qf.getQuery();
		List<Nazioni> list = (List<Nazioni>) q.list();
		if (list != null) {
			if (list.size() > 0) {
				return list.get(0);
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Nazioni findBySiglaNazione(Session ses, String siglaNazione) throws HibernateException {
		QueryFactory qf = new QueryFactory(ses, "from Nazioni c");
		qf.addWhere("c.siglaNazione = :p1");
		qf.addParam("p1", siglaNazione);
		Query q = qf.getQuery();
		List<Nazioni> list = (List<Nazioni>) q.list();
		if (list != null) {
			if (list.size() > 0) {
				return list.get(0);
			}
		}
		return null;
	}
	
}
