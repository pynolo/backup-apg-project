package it.giunti.apg.core.persistence;

import it.giunti.apg.shared.model.CacheCrm;

import java.io.Serializable;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.IntegerType;

public class CacheCrmDao implements BaseDao<CacheCrm> {


	@Override
	public void update(Session ses, CacheCrm instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getIdAnagrafica(), instance);
	}

	@Override
	public Serializable save(Session ses, CacheCrm transientInstance)
			throws HibernateException {
		Serializable key = ses.save(transientInstance);
		return key;
	}
	
	@Override
	public void delete(Session ses, CacheCrm instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getIdAnagrafica(), instance);
	}
	
	@SuppressWarnings("unchecked")
	public CacheCrm findByAnagrafica(Session ses, Integer idAnagrafica)
			throws HibernateException {
		String qs = "from CacheCrm as ca where " +
				"ca.idAnagrafica = :id1 ";
		Query q = ses.createQuery(qs);
		q.setParameter("id1", idAnagrafica, IntegerType.INSTANCE);
		List<CacheCrm> caList= (List<CacheCrm>) q.list();
		if (caList.size() == 1) {
			return caList.get(0);
		}
		if (caList.size() > 1){
			throw new HibernateException(caList.size()+" CacheCrm with id="+idAnagrafica);
		}
		return null;
	}
	
}
