package it.giunti.apg.export.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.springframework.stereotype.Repository;

import it.giunti.apg.export.model.IstanzeAbbonamenti;

@Repository("istanzeAbbonamentiDao")
public class IstanzeAbbonamentiDao {

	@PersistenceContext
	private EntityManager entityManager;
	
	@SuppressWarnings("unchecked")
	public IstanzeAbbonamenti selectById(Integer id) {
		Query query = entityManager.createQuery(
				"from IstanzeAbbonamenti as ia where "+
				"ia.id = :s1")
				.setParameter("s1", id);
		List<IstanzeAbbonamenti> list = (List<IstanzeAbbonamenti>) query.getResultList();
		if (list != null) {
			if (list.size() > 0) return list.get(0);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public IstanzeAbbonamenti selectLastByIdAbbonato(Integer id) {
		Query query = entityManager.createQuery(
				"from IstanzeAbbonamenti as ia where "+
				"ia.idAbbonato = :s1 and "+
				"ia.ultimaDellaSerie = :b1 ")
				.setParameter("s1", id)
				.setParameter("b1", Boolean.TRUE);
		List<IstanzeAbbonamenti> list = (List<IstanzeAbbonamenti>) query.getResultList();
		if (list != null) {
			if (list.size() > 0) return list.get(0);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public IstanzeAbbonamenti selectLastByIdPagante(Integer id) {
		Query query = entityManager.createQuery(
				"from IstanzeAbbonamenti as ia where "+
				"ia.idPagante = :s1 and "+
				"ia.ultimaDellaSerie = :b1 ") //first result = last abbonamento
				.setParameter("s1", id)
				.setParameter("b1", Boolean.TRUE);
		List<IstanzeAbbonamenti> list = (List<IstanzeAbbonamenti>) query.getResultList();
		if (list != null) {
			if (list.size() > 0) return list.get(0);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> findIdAbbonatoByUpdateTimestamp(Date startTimestamp, int firstResult, int maxResult) {
		Query query = entityManager.createQuery(
				"select ia.idAbbonato from IstanzeAbbonamenti as ia where "+
				"ia.updateTimestamp >= :s1 "+
				"order by ia.id")
				.setParameter("s1", startTimestamp, TemporalType.TIMESTAMP);
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);
		List<Integer> list = (List<Integer>) query.getResultList();
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> findIdPaganteByUpdateTimestamp(Date startTimestamp, int firstResult, int maxResult) {
		Query query = entityManager.createQuery(
				"select ia.idPagante from IstanzeAbbonamenti as ia where "+
				"ia.idPagante is not null and "+
				"ia.updateTimestamp >= :s1 "+
				"order by ia.id")
				.setParameter("s1", startTimestamp, TemporalType.TIMESTAMP);
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);
		List<Integer> list = (List<Integer>) query.getResultList();
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> findExpiringSinceTimestamp(Date startTimestamp, int firstResult, int maxResult) {
		Query query = entityManager.createQuery(
				"select ia.idAbbonato from IstanzeAbbonamenti as ia where "+
				"ia.fascicoloFine.dataFine >= :s1 "+
				"order by ia.id")
				.setParameter("s1", startTimestamp, TemporalType.TIMESTAMP);
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);
		List<Integer> list = (List<Integer>) query.getResultList();
		return list;
	}
}
