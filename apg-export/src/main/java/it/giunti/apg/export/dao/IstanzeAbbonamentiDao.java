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
	
	public IstanzeAbbonamenti selectById(Integer id) {
		IstanzeAbbonamenti ia = entityManager.find(IstanzeAbbonamenti.class, id);
		return ia;
	}
	
	@SuppressWarnings("unchecked")
	public List<IstanzeAbbonamenti> selectLastByIdAbbonato(Integer id) {
		Query query = entityManager.createQuery(
				"from IstanzeAbbonamenti as ia where "+
				"ia.idAbbonato = :s1 and "+
				"ia.ultimaDellaSerie = :b1 ")
				.setParameter("s1", id)
				.setParameter("b1", Boolean.TRUE);
		List<IstanzeAbbonamenti> list = (List<IstanzeAbbonamenti>) query.getResultList();
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<IstanzeAbbonamenti> selectLastByIdPagante(Integer id) {
		Query query = entityManager.createQuery(
				"from IstanzeAbbonamenti as ia where "+
				"ia.idPagante = :s1 and "+
				"ia.ultimaDellaSerie = :b1 ") //first result = last abbonamento
				.setParameter("s1", id)
				.setParameter("b1", Boolean.TRUE);
		List<IstanzeAbbonamenti> list = (List<IstanzeAbbonamenti>) query.getResultList();
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> findIdAbbonatoByTimestamp(Date beginTimestamp, Date endTimestamp, int firstResult, int maxResult) {
		Query query = entityManager.createQuery(
				"select distinct ia.idAbbonato from IstanzeAbbonamenti as ia where "+
				"ia.ultimaDellaSerie = :b1 and "+
				"ia.updateTimestamp > :ts1 and "+
				"ia.updateTimestamp <= :ts2 "+
				"order by ia.idAbbonato asc")
				.setParameter("b1", Boolean.TRUE)
				.setParameter("ts1", beginTimestamp, TemporalType.TIMESTAMP)
				.setParameter("ts2", endTimestamp, TemporalType.TIMESTAMP);
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);
		List<Integer> list = (List<Integer>) query.getResultList();
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> findIdPaganteByTimestamp(Date beginTimestamp, Date endTimestamp, int firstResult, int maxResult) {
		Query query = entityManager.createQuery(
				"select distinct ia.idPagante from IstanzeAbbonamenti as ia where "+
				"ia.idPagante is not null and "+
				"ia.ultimaDellaSerie = :b1 and "+
				"ia.updateTimestamp > :ts1 and "+
				"ia.updateTimestamp <= :ts2 "+
				"order by ia.idPagante asc")
				.setParameter("b1", Boolean.TRUE)
				.setParameter("ts1", beginTimestamp, TemporalType.TIMESTAMP)
				.setParameter("ts2", endTimestamp, TemporalType.TIMESTAMP);
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);
		List<Integer> list = (List<Integer>) query.getResultList();
		return list;
	}
	
	//@SuppressWarnings("unchecked")
	//public List<Integer> findExpiringSinceTimestamp(Date startTimestamp, int firstResult, int maxResult) {
	//	Query query = entityManager.createQuery(
	//			"select ia.idAbbonato from IstanzeAbbonamenti as ia where "+
	//			"ia.fascicoloFine.dataFine >= :s1 "+
	//			"order by ia.updateTimestamp asc")
	//			.setParameter("s1", startTimestamp, TemporalType.TIMESTAMP);
	//	query.setFirstResult(firstResult);
	//	query.setMaxResults(maxResult);
	//	List<Integer> list = (List<Integer>) query.getResultList();
	//	return list;
	//}
	
	@SuppressWarnings("unchecked")
	public Date findLastUpdateTimestamp() {
		Query query = entityManager.createQuery(
				"select max(ia.updateTimestamp) from IstanzeAbbonamenti as ia");
		List<Date> list = (List<Date>) query.getResultList();
		if (list != null) {
			if (list.size() > 0) return list.get(0);
		}
		return null;
	}
}
