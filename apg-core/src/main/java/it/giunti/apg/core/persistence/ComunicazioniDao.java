package it.giunti.apg.core.persistence;

import it.giunti.apg.shared.model.Comunicazioni;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class ComunicazioniDao implements BaseDao<Comunicazioni>  {

	@Override
	public void update(Session ses, Comunicazioni instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
		EditLogDao.writeEditLog(ses, Comunicazioni.class, instance.getId(), instance.getIdUtente());
	}

	@Override
	public Serializable save(Session ses, Comunicazioni transientInstance)
			throws HibernateException {
		Integer id = (Integer)GenericDao.saveGeneric(ses, transientInstance);
		EditLogDao.writeEditLog(ses, Comunicazioni.class, id, transientInstance.getIdUtente());
		return id;
	}

	@Override
	public void delete(Session ses, Comunicazioni instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}
	
	@SuppressWarnings("unchecked")
	public List<Comunicazioni> findComunicazioniByPeriodico(Session ses,
			Integer idPeriodico, Date dt, 
			Integer offset, Integer size) throws HibernateException {
		String qs = "from Comunicazioni c " +
				"where c.periodico.id = :p1 and " +
				"(c.dataInizio is null or c.dataInizio <= :d1) and " +
				"(c.dataFine is null or c.dataFine >= :d2) " +
				"order by c.titolo asc ";
		Query q = ses.createQuery(qs);
		q.setInteger("p1", idPeriodico);
		q.setDate("d1", dt);
		q.setDate("d2", dt);
		q.setFirstResult(offset);
	    q.setMaxResults(size);
		List<Comunicazioni> cList = (List<Comunicazioni>) q.list();
		return cList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Comunicazioni> findComunicazioniByTipoAbb(Session ses,
			Integer idTipoAbb, Date dt, 
			Integer offset, Integer size) throws HibernateException {
		String qs = "from Comunicazioni c where " +
				"(c.tipiAbbonamentoList = :s1 or " +
					"c.tipiAbbonamentoList like :s2 or " +
					"c.tipiAbbonamentoList like :s3 or " +
					"c.tipiAbbonamentoList like :s4) and " +
				"(c.dataInizio is null or c.dataInizio <= :d1) and " +
				"(c.dataFine is null or c.dataFine >= :d2) " +
				"order by c.titolo asc ";
		Query q = ses.createQuery(qs);
		q.setString("s1", idTipoAbb+"");
		q.setString("s2", idTipoAbb+",%");
		q.setString("s3", "%,"+idTipoAbb);
		q.setString("s4", "%,"+idTipoAbb+",%");
		q.setDate("d1", dt);
		q.setDate("d2", dt);
		q.setFirstResult(offset);
	    q.setMaxResults(size);
		List<Comunicazioni> cList = (List<Comunicazioni>) q.list();
		return cList;
	}
	
	@SuppressWarnings("unchecked")
	public Map<Comunicazioni, Integer> findAsyncComunicazioniByEnqueuedMedia(Session ses,
			String idTipoMedia) throws HibernateException {
		String qs = "select ec.comunicazione, count(ec.id) from EvasioniComunicazioni ec where " +
				"(ec.dataEstrazione is null) and " + //non ancora estratti
				"(ec.eliminato = :b1) and " + //non eliminati
				"(ec.fascicolo is null) and " + //comunicazioni asincrone
				"(ec.idTipoMedia = :s1) " +
				"group by ec.comunicazione "+
				"order by ec.comunicazione.id asc ";
		Query q = ses.createQuery(qs);
		q.setParameter("b1", Boolean.FALSE);
		q.setParameter("b2", Boolean.FALSE);
		q.setParameter("s1", idTipoMedia);
		List<Object[]> coupleList = (List<Object[]>) q.list();
		Map<Comunicazioni, Integer> comMap = new HashMap<Comunicazioni, Integer>();
		for (Object[] couple:coupleList) {
			Integer qty = ((Long)couple[1]).intValue();
			comMap.put((Comunicazioni)couple[0], qty);
		}
		return comMap;
	}
	
	@SuppressWarnings("unchecked")
	public List<Comunicazioni> findComunicazioniByPeriodicoNull(Session ses) throws HibernateException {
		String qs = "from Comunicazioni c " +
				"where c.periodico is null " +
				"order by c.titolo asc ";
		Query q = ses.createQuery(qs);
		List<Comunicazioni> cList = (List<Comunicazioni>) q.list();
		return cList;
	}
	
	/**
	 * Trova le comunicazioni valide in data odierna per i periodici validi in data odierna
	 * @param ses
	 * @param dt
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Comunicazioni> findByDataPeriodico(Session ses, Date dtNow) throws HibernateException {
		String hql = "from Comunicazioni c where " +
			"(c.periodico.dataInizio <= :d1 or c.periodico.dataInizio is null) and " +
			"(c.periodico.dataFine >= :d2 or c.periodico.dataFine is null) and " +
			"(c.dataInizio <= :d3 or c.dataInizio is null) and " +
			"(c.dataFine >= :d4 or c.dataFine is null) ";
		Query q = ses.createQuery(hql);
		q.setDate("d1", dtNow);
		q.setDate("d2", dtNow);
		q.setDate("d3", dtNow);
		q.setDate("d4", dtNow);
		List<Comunicazioni> list = (List<Comunicazioni>) q.list();
		return list;
	}
	
}
