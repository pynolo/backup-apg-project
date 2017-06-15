package it.giunti.apg.server.persistence;

import it.giunti.apg.shared.model.Rapporti;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.DateType;

public class RapportiDao implements BaseDao<Rapporti> {

	@Override
	public void update(Session ses, Rapporti instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
	}

	@Override
	public Serializable save(Session ses, Rapporti transientInstance)
			throws HibernateException {
		return GenericDao.saveGeneric(ses, transientInstance);
	}

	@Override
	public void delete(Session ses, Rapporti instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}
	
	@SuppressWarnings("unchecked")
	public List<Rapporti> findRapportiStripped(Session ses, Date extractionDt,
			int offset, int size) throws HibernateException {
		Calendar cal = new GregorianCalendar();
		cal.setTime(extractionDt);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		Date dt = cal.getTime();
		//ricerca dell'ultima istanza
		String hql = "from Rapporti as r where " +
				"r.dataModifica < :dt1 "+
				"order by r.dataModifica desc";
		Query q = ses.createQuery(hql);
		q.setParameter("dt1", dt, DateType.INSTANCE);
		q.setFirstResult(offset);
        q.setMaxResults(size);
		List<Rapporti> rList = (List<Rapporti>) q.list();
		for (Rapporti r:rList) {
			r.setTesto(null);
		}
		return rList;
	}
}
