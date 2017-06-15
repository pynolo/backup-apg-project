package it.giunti.apg.server.persistence;

import it.giunti.apg.shared.model.StatAbbonati;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class StatAbbonatiDao implements BaseDao<StatAbbonati> {

	@Override
	public void update(Session ses, StatAbbonati instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
	}

	@Override
	public Serializable save(Session ses, StatAbbonati transientInstance)
			throws HibernateException {
		return GenericDao.saveGeneric(ses, transientInstance);
	}

	@Override
	public void delete(Session ses, StatAbbonati instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}
	
	@SuppressWarnings("unchecked")
	public List<StatAbbonati> findByDates(Session ses, Integer idPeriodico,
			Date dataInizio , Date dataFine) throws HibernateException {
		//ricerca dell'ultima istanza
		String hql = "from StatAbbonati as sa where " +
				"sa.periodico.id = :i1 and " +
				"sa.dataCreazione >= :d1 and " +
				"sa.dataCreazione <= :d2 " +
				"order by sa.dataCreazione asc";
		Query q = ses.createQuery(hql);
		q.setInteger("i1", idPeriodico);
		q.setTimestamp("d1", dataInizio);
		q.setTimestamp("d2", dataFine);
		List<StatAbbonati> saList = (List<StatAbbonati>) q.list();
		return saList;
	}
	
	@SuppressWarnings("unchecked")
	public Integer findTiraturaByPeriodico(Session ses, Integer idPeriodico) throws HibernateException {
		String qs = "from StatAbbonati sa where sa.periodico.id = :i1 " +
				"order by sa.dataCreazione desc";
		Query q = ses.createQuery(qs);
		q.setMaxResults(1);
		q.setInteger("i1", idPeriodico);
		List<StatAbbonati> list = (List<StatAbbonati>) q.list();
		if (list != null) {
			if (list.size() > 0) {
				StatAbbonati sa = (StatAbbonati) list.get(0);
				return sa.getTiratura();
			}
		}
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	public StatAbbonati findLast(Session ses, Integer idPeriodico) throws HibernateException {
		//ricerca dell'ultima istanza
		String hql = "from StatAbbonati as sa where " +
				"sa.periodico.id = :i1 " +
				"order by sa.dataCreazione desc";
		Query q = ses.createQuery(hql);
		q.setInteger("i1", idPeriodico);
		q.setMaxResults(1);
		List<StatAbbonati> saList = (List<StatAbbonati>) q.list();
		if (saList != null) {
			if (saList.size() > 0) {
				return saList.get(0);
			}
		}
		return null;
	}

}
