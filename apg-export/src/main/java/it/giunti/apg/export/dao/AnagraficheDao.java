package it.giunti.apg.export.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.springframework.stereotype.Repository;

import it.giunti.apg.export.model.Anagrafiche;

@Repository("anagraficheDao")
public class AnagraficheDao {

	@PersistenceContext
	private EntityManager entityManager;
	
	public Anagrafiche selectById(Integer id) {
		Anagrafiche result = entityManager.find(Anagrafiche.class, id);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> findIdTimestampByTimestamp(Date beginTimestamp, Date endTimestamp, int firstResult, int maxResult) {
		Query query = entityManager.createQuery(
				"select ana.id, ana.updateTimestamp from Anagrafiche as ana where "+
				"ana.updateTimestamp > :ts1 and "+
				"ana.updateTimestamp <= :ts2 "+
				"order by ana.id asc")
				.setParameter("ts1", beginTimestamp, TemporalType.TIMESTAMP)
				.setParameter("ts2", endTimestamp, TemporalType.TIMESTAMP);
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);
		List<Object[]> list = (List<Object[]>) query.getResultList();
		return list;
	}
	
	//@SuppressWarnings("unchecked")
	//public Long countIdByTimestamp(Date beginTimestamp, Date endTimestamp) {
	//	Query query = entityManager.createQuery(
	//			"select count(distinct ana.id) from Anagrafiche as ana where "+
	//			"ana.updateTimestamp > :ts1 and "+
	//			"ana.updateTimestamp <= :ts2 ")
	//			.setParameter("ts1", beginTimestamp, TemporalType.TIMESTAMP)
	//			.setParameter("ts2", endTimestamp, TemporalType.TIMESTAMP);
	//	List<Long> list = (List<Long>) query.getResultList();
	//	if (list != null) {
	//		if (list.size() > 0) return list.get(0);
	//	}
	//	return null;
	//}
	
	@SuppressWarnings("unchecked")
	public Date findLastUpdateTimestamp() {
		Query query = entityManager.createQuery(
				"select max(ana.updateTimestamp) from Anagrafiche as ana where ana.updateTimestamp is not null");
		List<Date> list = (List<Date>) query.getResultList();
		if (list != null) {
			if (list.size() > 0) return list.get(0);
		}
		return null;
	}
}
