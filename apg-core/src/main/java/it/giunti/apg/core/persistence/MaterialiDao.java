package it.giunti.apg.core.persistence;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.StringType;

import it.giunti.apg.shared.model.Materiali;

public class MaterialiDao implements BaseDao<Materiali> {

	@Override
	public void update(Session ses, Materiali instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
	}

	@Override
	public Serializable save(Session ses, Materiali transientInstance)
			throws HibernateException {
		Integer id = (Integer) GenericDao.saveGeneric(ses, transientInstance);
		return id;
	}

	@Override
	public void delete(Session ses, Materiali instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}
	
	@SuppressWarnings("unchecked")
	public Materiali findByCodiceMeccanografico(Session ses, String cm)
			throws HibernateException {
		String qs = "from Materiali f where " +
				"f.codiceMeccanografico = :s1 " +
				"order by f.id desc ";
		Query q = ses.createQuery(qs);
		q.setParameter("s1", cm, StringType.INSTANCE);
		List<Materiali> cList = (List<Materiali>) q.list();
		if (cList != null) {
			if (cList.size()==1) {
				return cList.get(0);
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Materiali> findSuggestionsByCodiceMeccanografico(Session ses, String searchString, int pageSize)
			throws HibernateException {
		String qs = "from Materiali f where " +
				"f.codiceMeccanografico like :s1 " +
				"order by f.id desc ";
		Query q = ses.createQuery(qs);
		q.setMaxResults(pageSize);
		q.setParameter("s1", searchString+"%", StringType.INSTANCE);
		List<Materiali> cList = (List<Materiali>) q.list();
		return cList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Materiali> findByDate(Session ses, Date extractionDt, int offset, int pageSize)
			throws HibernateException {
		String qs = "from Materiali as mat where " +
				"mat.dataLimiteVisibilita >= :dt1 or mat.dataLimiteVisibilita is null " +
				"order by mat.titolo ";
		Query q = ses.createQuery(qs);
		q.setDate("dt1", extractionDt);
		q.setFirstResult(offset);
		q.setMaxResults(pageSize);
		List<Materiali> dList= (List<Materiali>) q.list();
		return dList;
	}

}
