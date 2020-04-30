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
	
	@SuppressWarnings("unchecked")
	public Anagrafiche selectById(Integer id) {
		Query query = entityManager.createQuery(
				"from Anagrafiche as ana where "+
				"ana.id = :s1")
				.setParameter("s1", id);
		List<Anagrafiche> list = (List<Anagrafiche>) query.getResultList();
		if (list != null) {
			if (list.size() > 0) return list.get(0);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> findIdByUpdateTimestamp(Date startTimestamp, int firstResult, int maxResult) {
		Query query = entityManager.createQuery(
				"select ana.id from Anagrafiche as ana where "+
				"ana.updateTimestamp >= :s1 "+
				"order by ana.id")
				.setParameter("s1", startTimestamp, TemporalType.TIMESTAMP);
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);
		List<Integer> list = (List<Integer>) query.getResultList();
		return list;
	}
}
