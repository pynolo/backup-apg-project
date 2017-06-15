package it.giunti.apg.server.persistence;

import it.giunti.apg.server.ServerConstants;
import it.giunti.apg.shared.model.Articoli;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class ArticoliDao implements BaseDao<Articoli> {

	@Override
	public void update(Session ses, Articoli instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
		EditLogDao.writeEditLog(ses, Articoli.class, instance.getId(), instance.getIdUtente());
	}

	@Override
	public Serializable save(Session ses, Articoli transientInstance)
			throws HibernateException {
		Integer id = (Integer)GenericDao.saveGeneric(ses, transientInstance);
		EditLogDao.writeEditLog(ses, Articoli.class, id, transientInstance.getIdUtente());
		return id;
	}

	@Override
	public void delete(Session ses, Articoli instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}
	
	@SuppressWarnings("unchecked")
	public List<Articoli> findByDate(Session ses, Date extractionDt, int offset, int pageSize)
			throws HibernateException {
		String qs = "from Articoli as d where " +
				"((d.dataInizio is null) or (d.dataInizio <= :d1)) and " +
				"((d.dataFine is null) or (d.dataFine >= :d2)) " +
				"order by d.titoloNumero ";
		Query q = ses.createQuery(qs);
		q.setDate("d1", extractionDt);
		q.setDate("d2", extractionDt);
		q.setFirstResult(offset);
		q.setMaxResults(pageSize);
		List<Articoli> dList= (List<Articoli>) q.list();
		return dList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Articoli> findByDateInterval(Session ses, Date startDt, Date finishDt)
			throws HibernateException {
		if (startDt == null) startDt = ServerConstants.DATE_FAR_PAST;
		if (finishDt == null) finishDt = ServerConstants.DATE_FAR_FUTURE;
		String qs = "from Articoli as d where " +
				"((d.dataInizio is null) or (d.dataInizio <= :d2)) and " +
				"((d.dataFine is null) or (d.dataFine >= :d1)) " +
				"order by d.titoloNumero ";
		Query q = ses.createQuery(qs);
		q.setDate("d1", startDt);
		q.setDate("d2", finishDt);
		List<Articoli> dList= (List<Articoli>) q.list();
		return dList;
	}
	
	@SuppressWarnings("unchecked")
	public Articoli findByCm(Session ses, String codiceMeccanografico)
			throws HibernateException {
		String qs = "from Articoli as d where " +
				"d.codiceMeccanografico like :s1 " +
				"order by d.id ";
		Query q = ses.createQuery(qs);
		q.setString("s1", codiceMeccanografico);
		List<Articoli> dList= (List<Articoli>) q.list();
		if (dList != null) {
			if (dList.size() == 1) {
				return dList.get(0);
			} else {
				throw new HibernateException(dList.size()+" Articoli with codiceMeccanografico="+codiceMeccanografico);
			}
		} else {
			throw new HibernateException("No Articoli with codiceMeccanografico="+codiceMeccanografico);
		}
	}
	
}
