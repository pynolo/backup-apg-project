package it.giunti.apg.server.persistence;

import it.giunti.apg.shared.model.AliquoteIva;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.DateType;

public class AliquoteIvaDao implements BaseDao<AliquoteIva> {

	@Override
	public void update(Session ses, AliquoteIva instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
	}

	@Override
	public Serializable save(Session ses, AliquoteIva transientInstance)
			throws HibernateException {
		return GenericDao.saveGeneric(ses, transientInstance);
	}

	@Override
	public void delete(Session ses, AliquoteIva instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}
	
	public List<AliquoteIva> findByDate(Session ses, Date extractionDt)
			throws HibernateException {
		long extrDt = extractionDt.getTime();
		return findByDate(ses, extrDt, extrDt);
	}
	
	@SuppressWarnings("unchecked")
	public List<AliquoteIva> findByDate(Session ses, long startDt, long finishDt)
			throws HibernateException {
		String qs = "from AliquoteIva as ai where " +
				"((ai.dataInizio is null) or (ai.dataInizio <= :d2)) and " +
				"((ai.dataFine is null) or (ai.dataFine >= :d1)) " +
				"order by ai.descr ";
		Query q = ses.createQuery(qs);
		q.setDate("d1", new Date(startDt));
		q.setDate("d2", new Date(finishDt));
		List<AliquoteIva> aiList = (List<AliquoteIva>) q.list();
		return aiList;
	}

	@SuppressWarnings("unchecked")
	public AliquoteIva findDefaultAliquotaIvaByDate(Session ses,
			String codiceAliquota, Date date) throws HibernateException {
		String qs = "from AliquoteIva as ai where " +
					"((ai.dataInizio is null) or (ai.dataInizio <= :d2)) and " +
					"((ai.dataFine is null) or (ai.dataFine >= :d1)) and " +
					"(ai.codiceExtraUe = :s1 or ai.codiceItaPvt = :s2 or ai.codiceItaSoc = :s3 or codiceUePvt = :s4 or codiceUeSoc = :s5)";
		Query q = ses.createQuery(qs);
		q.setParameter("d1", date, DateType.INSTANCE);
		q.setParameter("d2", date, DateType.INSTANCE);
		q.setString("s1", codiceAliquota);
		q.setString("s2", codiceAliquota);
		q.setString("s3", codiceAliquota);
		q.setString("s4", codiceAliquota);
		q.setString("s5", codiceAliquota);
		List<AliquoteIva> list = (List<AliquoteIva>) q.list();
		if (list != null) {
			if (list.size()>0) {
				return list.get(0);
			}
		}
		return null;
	}
}
