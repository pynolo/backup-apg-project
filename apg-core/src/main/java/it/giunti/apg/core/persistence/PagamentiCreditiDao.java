package it.giunti.apg.core.persistence;

import it.giunti.apg.core.business.PagamentiMatchBusiness;
import it.giunti.apg.shared.model.PagamentiCrediti;

import java.io.Serializable;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.BooleanType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;

public class PagamentiCreditiDao implements BaseDao<PagamentiCrediti> {
	
	@Override
	public void update(Session ses, PagamentiCrediti instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
	}

	@Override
	public Serializable save(Session ses, PagamentiCrediti transientInstance)
			throws HibernateException {
		Integer id = (Integer)GenericDao.saveGeneric(ses, transientInstance);
		return id;
	}

	@Override
	public void delete(Session ses, PagamentiCrediti instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}
	
	@SuppressWarnings("unchecked")
	public List<PagamentiCrediti> findByAnagrafica(Session ses, 
			Integer idAnagrafica, Boolean fatturati) throws HibernateException {
		QueryFactory qf = new QueryFactory(ses, "from PagamentiCrediti pc");
		qf.addWhere("pc.idAnagrafica = :id1");
		qf.addParam("id1", idAnagrafica);
		if (fatturati != null) {
			if (fatturati) {
				qf.addWhere("pc.fatturaImpiego is not null");
			} else {
				qf.addWhere("pc.fatturaImpiego is null");
			}
		}
		qf.addOrder("pc.id asc");
		Query q = qf.getQuery();
		List<PagamentiCrediti> pList = (List<PagamentiCrediti>) q.list();
		return pList;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<PagamentiCrediti> findByAnagraficaSocieta(Session ses, 
			Integer idAnagrafica, String idSocieta, Boolean stornati, Boolean fatturati) throws HibernateException {
		String hql = "from PagamentiCrediti pc where ";
		if (stornati != null) hql += "pc.stornatoDaOrigine = :b1 and ";
		if (fatturati != null) {
			if (fatturati) {
				hql += "pc.fatturaImpiego is not null and ";
			} else {
				hql += "pc.fatturaImpiego is null and ";
			}
		}
		hql += "pc.idAnagrafica = :id1 and "+
			"pc.idSocieta = :s1 "+
			"order by pc.id asc";
		Query q = ses.createQuery(hql);
		if (stornati != null) q.setParameter("b1", stornati, BooleanType.INSTANCE);
		q.setParameter("id1", idAnagrafica, IntegerType.INSTANCE);
		q.setParameter("s1", idSocieta, StringType.INSTANCE);
		List<PagamentiCrediti> pList = (List<PagamentiCrediti>) q.list();
		return pList;
	}
	
	@SuppressWarnings("unchecked")
	public List<PagamentiCrediti> findByIstanza(Session ses, 
			Integer idIstanzaAbbonamento) throws HibernateException {
		QueryFactory qf = new QueryFactory(ses, "from PagamentiCrediti pc");
		qf.addWhere("pc.idIstanzaAbbonamento = :id1");
		qf.addParam("id1", idIstanzaAbbonamento);
		qf.addOrder("pc.id asc");
		Query q = qf.getQuery();
		List<PagamentiCrediti> pList = (List<PagamentiCrediti>) q.list();
		return pList;
	}
	
	public Double getCreditoByAnagraficaSocieta(Session ses, 
			Integer idAnagrafica, String idSocieta, Boolean stornati, Boolean fatturati)
			throws HibernateException {
		List<PagamentiCrediti> pcList = findByAnagraficaSocieta(ses, 
				idAnagrafica, idSocieta, stornati, fatturati);
		Double result = PagamentiMatchBusiness.getTotalAmount(null, pcList);
		return result;
	}

	
	@SuppressWarnings("unchecked")
	public List<PagamentiCrediti> findByFatturaOrigine(Session ses, 
			Integer idFattura) throws HibernateException {
		String hql = "from PagamentiCrediti pc where "+
				"pc.fatturaOrigine.id = :id1 "+
				"order by pc.dataCreazione desc";
		Query q = ses.createQuery(hql);
		q.setParameter("id1", idFattura, IntegerType.INSTANCE);
		List<PagamentiCrediti> pcList = (List<PagamentiCrediti>) q.list();
		return pcList;
	}
	
	@SuppressWarnings("unchecked")
	public List<PagamentiCrediti> findByFatturaImpiego(Session ses, 
			Integer idFattura) throws HibernateException {
		String hql = "from PagamentiCrediti pc where "+
				"pc.fatturaImpiego.id = :id1 "+
				"order by pc.dataCreazione desc";
		Query q = ses.createQuery(hql);
		q.setParameter("id1", idFattura, IntegerType.INSTANCE);
		List<PagamentiCrediti> pcList = (List<PagamentiCrediti>) q.list();
		return pcList;
	}
	
	@SuppressWarnings("unchecked")
	public Double sumCreditiByIstanza(Session ses, Integer idIstanza) throws HibernateException {
		if (idIstanza == null) return 0D;
		Double result = 0D;
		String qs = "from PagamentiCrediti as pc where " +
			"pc.idIstanzaAbbonamento = :id1";
		Query q = ses.createQuery(qs);
		q.setInteger("id1", idIstanza);
		List<PagamentiCrediti> list = (List<PagamentiCrediti>) q.list();
		if (list != null) {
			if (list.size() > 0) {
				result = PagamentiMatchBusiness.getTotalAmount(null, list);
			}
		}
		return result;
	}
}
