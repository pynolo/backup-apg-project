package it.giunti.apg.core.persistence;

import it.giunti.apg.shared.model.Avvisi;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class AvvisiDao implements BaseDao<Avvisi> {


	@Override
	public void update(Session ses, Avvisi instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
	}

	@Override
	public Serializable save(Session ses, Avvisi transientInstance)
			throws HibernateException {
		Serializable key = ses.save(transientInstance);
		return key;
	}
	
	@Override
	public void delete(Session ses, Avvisi instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}
	
	@SuppressWarnings("unchecked")
	public List<Avvisi> findLastAvvisi(Session ses, int offset, int size) throws HibernateException {
		//ricerca dell'ultima istanza
		String hql = "from Avvisi as n " +
				"order by n.data desc";
		Query q = ses.createQuery(hql);
		q.setFirstResult(offset);
        q.setMaxResults(size);
		List<Avvisi> nList = (List<Avvisi>) q.list();
		return nList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Avvisi> findLastAvvisiByGiorniTipo(Session ses, int giorniAntecendenti) throws HibernateException {
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DAY_OF_MONTH, (-1)*giorniAntecendenti);
		//ricerca dell'ultima istanza
		String hql = "from Avvisi as n " +
				"where n.data >= :d1 " +
				"order by n.importante desc, n.data desc";
		Query q = ses.createQuery(hql);
		q.setTimestamp("d1", cal.getTime());
		List<Avvisi> nList = (List<Avvisi>) q.list();
		return nList;
	}
	
	@SuppressWarnings("unchecked")
	public Avvisi findMaintenanceAfterDate(Session ses, Date dt) throws HibernateException {
		//ricerca dell'ultima istanza
		String hql = "from Avvisi as a where "+
				"a.dataManutenzione >= :dt1 "+
				"order by a.id desc";
		Query q = ses.createQuery(hql);
		q.setParameter(":dt1", dt);
		List<Avvisi> aList = (List<Avvisi>) q.list();
		Avvisi result = null;
		if (aList.size() >= 1) {
			result = aList.get(0);
		}
		return result;
	}
	
	//public Integer save(Session ses, Date date, boolean importante, String message,
	//		Date maintenanceDt, String idUtente) throws HibernateException  {
	//	Avvisi avviso = new Avvisi();
	//	avviso.setData(date);
	//	avviso.setImportante(importante);
	//	avviso.setMessaggio(message);
	//	avviso.setDataManutenzione(maintenanceDt);
	//	avviso.setIdUtente(idUtente);
	//	Integer idNotizia = (Integer) this.save(ses, avviso);
	//	return idNotizia;
	//}
}
