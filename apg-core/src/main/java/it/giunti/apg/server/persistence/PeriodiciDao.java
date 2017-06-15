package it.giunti.apg.server.persistence;

import it.giunti.apg.shared.model.Periodici;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.DateType;

public class PeriodiciDao implements BaseDao<Periodici> {

	@Override
	public void update(Session ses, Periodici instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
	}

	@Override
	public Serializable save(Session ses, Periodici transientInstance)
			throws HibernateException {
		return GenericDao.saveGeneric(ses, transientInstance);
	}

	@Override
	public void delete(Session ses, Periodici instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}
	
	@SuppressWarnings("unchecked")
	public List<Periodici> findByDate(Session ses, Date dt) throws HibernateException {
		String hql = "from Periodici p where " +
			"p.dataInizio <= :d1 and " +
			"(p.dataFine >= :d2 or p.dataFine is null) " +
			"order by p.nome";
		Query q = ses.createQuery(hql);
		q.setDate("d1", dt);
		q.setDate("d2", dt);
		List<Periodici> list = (List<Periodici>) q.list();
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<Periodici> findByDateOrId(Session ses, Integer selectedId, Date dt) throws HibernateException {
		String hql = "from Periodici p where " +
				"( "+
					"p.dataInizio <= :dt1 and " +
					"(p.dataFine >= :dt2 or p.dataFine is null) "+
				")";
		if (selectedId != null) hql += "or (p.id = :id1) ";
		hql += "order by p.nome ";
		Query q = ses.createQuery(hql);
		q.setParameter("dt1", dt, DateType.INSTANCE);
		q.setParameter("dt2", dt, DateType.INSTANCE);
		if (selectedId != null) q.setParameter("id1", selectedId);
		List<Periodici> list = (List<Periodici>) q.list();
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public Periodici findByUid(Session ses, String uid) throws HibernateException {
		String hql = "from Periodici p where " +
			"p.uid = :s1 " +
			"order by p.dataInizio desc";
		Query q = ses.createQuery(hql);
		q.setString("s1", uid);
		List<Periodici> list = (List<Periodici>) q.list();
		if (list != null) {
			if (list.size() > 0) {
				return list.get(0);
			}
		}
		return null;
	}
}
