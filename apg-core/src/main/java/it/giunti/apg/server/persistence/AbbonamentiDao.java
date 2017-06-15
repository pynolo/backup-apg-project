package it.giunti.apg.server.persistence;

import it.giunti.apg.shared.model.Abbonamenti;

import java.io.Serializable;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class AbbonamentiDao implements BaseDao<Abbonamenti> {

	@Override
	public void update(Session ses, Abbonamenti instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
		EditLogDao.writeEditLog(ses, Abbonamenti.class, instance.getId(), instance.getIdUtente());
	}

	@Override
	public Serializable save(Session ses, Abbonamenti transientInstance)
			throws HibernateException {
		Integer id = (Integer)GenericDao.saveGeneric(ses, transientInstance);
		EditLogDao.writeEditLog(ses, Abbonamenti.class, id, transientInstance.getIdUtente());
		return id;
	}

	@Override
	public void delete(Session ses, Abbonamenti instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}
	
	@SuppressWarnings("unchecked")
	public Abbonamenti findAbbonamentiByCodice(Session ses, String codice) throws HibernateException {
		QueryFactory qf = new QueryFactory(ses, "from Abbonamenti a");
		qf.addWhere("a.codiceAbbonamento = :p1");
		qf.addParam("p1", codice);
		qf.addOrder("a.dataCreazione desc");
		Query q = qf.getQuery();
		List<Abbonamenti> abbList = (List<Abbonamenti>) q.list();
		if (abbList.size()>0) {
			return abbList.get(0);
		} else {
			return null;
		}
	}
	
	//@SuppressWarnings("unchecked")
	//public List<Abbonamenti> findAbbonamentiByAbbonato(Session ses, Integer idAbbonato) throws HibernateException {
	//	QueryFactory qf = new QueryFactory(ses, "from Abbonamenti a");
	//	qf.addWhere("a.abbonato.id = :p1");
	//	qf.addParam("p1", idAbbonato);
	//	qf.addOrder("a.dataCreazione desc");
	//	Query q = qf.getQuery();
	//	List<Abbonamenti> abbList = (List<Abbonamenti>) q.list();
	//	return abbList;
	//}
	//
	//@SuppressWarnings("unchecked")
	//public List<Abbonamenti> findAbbonamentiByPagante(Session ses, Integer idPagante) throws HibernateException {
	//	QueryFactory qf = new QueryFactory(ses, "from Abbonamenti a");
	//	qf.addWhere("a.pagante.id = :p1");
	//	qf.addParam("p1", idPagante);
	//	qf.addOrder("a.dataCreazione desc");
	//	Query q = qf.getQuery();
	//	List<Abbonamenti> abbList = (List<Abbonamenti>) q.list();
	//	return abbList;
	//}
	//
	//@SuppressWarnings("unchecked")
	//public List<Abbonamenti> findAbbonamentiByAgente(Session ses, Integer idAgente) throws HibernateException {
	//	QueryFactory qf = new QueryFactory(ses, "from Abbonamenti a");
	//	qf.addWhere("a.agente.id = :p1");
	//	qf.addParam("p1", idAgente);
	//	qf.addOrder("a.dataCreazione desc");
	//	Query q = qf.getQuery();
	//	List<Abbonamenti> abbList = (List<Abbonamenti>) q.list();
	//	return abbList;
	//}
	
	@SuppressWarnings("unchecked")
	public Boolean findCodiceAbbonamento(Session ses, String codiceAbbonamento) throws HibernateException {
		QueryFactory qf = new QueryFactory(ses, "select codiceAbbonamento from Abbonamenti a");
		qf.addWhere("a.codiceAbbonamento = :p1");
		qf.addParam("p1", codiceAbbonamento);
		Query q = qf.getQuery();
		List<Object> abbList = (List<Object>) q.list();
		if (abbList != null) {
			if (abbList.size() > 0) {
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public Boolean findCodiceAbbonamentoIfDifferentAbbonato(Session ses,
			String codiceAbbonamento, Integer idAbbonato) throws HibernateException {
		QueryFactory qf = new QueryFactory(ses, "select ia.abbonamento.codiceAbbonamento from IstanzeAbbonamenti ia");
		qf.addWhere("ia.abbonamento.codiceAbbonamento = :p1");
		qf.addParam("p1", codiceAbbonamento);
		qf.addWhere("ia.abbonato.id != :i1");
		qf.addParam("i1", idAbbonato);
		Query q = qf.getQuery();
		List<Object> abbList = (List<Object>) q.list();
		if (abbList != null) {
			if (abbList.size() > 0) {
				return true;
			}
		}
		return false;
	}
	
//	@SuppressWarnings("unchecked")
//	public List<String> findAdesioniSuggestions(Session ses, String adesionePrefix) throws HibernateException {
//		String queryString = "select distinct ia.adesione from IstanzeAbbonamenti ia " +
//				"where upper(ia.adesione) like :p1 ";
//		Query q = ses.createQuery(queryString);
//		q.setString("p1", adesionePrefix.toUpperCase()+"%");
//		List<String> result = (List<String>)q.list();
//		return result;
//	}

}
