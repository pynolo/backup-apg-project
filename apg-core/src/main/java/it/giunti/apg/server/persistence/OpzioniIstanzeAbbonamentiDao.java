package it.giunti.apg.server.persistence;

import it.giunti.apg.shared.model.OpzioniIstanzeAbbonamenti;

import java.io.Serializable;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class OpzioniIstanzeAbbonamentiDao implements BaseDao<OpzioniIstanzeAbbonamenti> {

	@Override
	public void update(Session ses, OpzioniIstanzeAbbonamenti instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
	}

	@Override
	public Serializable save(Session ses, OpzioniIstanzeAbbonamenti transientInstance)
			throws HibernateException {
		return GenericDao.saveGeneric(ses, transientInstance);
	}

	@Override
	public void delete(Session ses, OpzioniIstanzeAbbonamenti instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}
	
	@SuppressWarnings("unchecked")
	public List<OpzioniIstanzeAbbonamenti> findOpzioniByIstanzaAbbonamento(Session ses,
			Integer idIstanzaAbbonamento) throws HibernateException {
		QueryFactory qf = new QueryFactory(ses, "from OpzioniIstanzeAbbonamenti oia");
		qf.addWhere("oia.istanza.id = :id1");
		qf.addParam("id1", idIstanzaAbbonamento);
		qf.addOrder("oia.id asc");
		Query q = qf.getQuery();
		List<OpzioniIstanzeAbbonamenti> oiaList = (List<OpzioniIstanzeAbbonamenti>) q.list();
		return oiaList;
	}
}
