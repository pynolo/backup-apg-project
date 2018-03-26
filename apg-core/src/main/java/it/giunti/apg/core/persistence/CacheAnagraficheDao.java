package it.giunti.apg.core.persistence;

import it.giunti.apg.shared.model.CacheAnagrafiche;

import java.io.Serializable;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.IntegerType;

public class CacheAnagraficheDao implements BaseDao<CacheAnagrafiche> {


	@Override
	public void update(Session ses, CacheAnagrafiche instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getIdAnagrafica(), instance);
	}

	@Override
	public Serializable save(Session ses, CacheAnagrafiche transientInstance)
			throws HibernateException {
		Serializable key = ses.save(transientInstance);
		return key;
	}
	
	@Override
	public void delete(Session ses, CacheAnagrafiche instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getIdAnagrafica(), instance);
	}
	
	@SuppressWarnings("unchecked")
	public CacheAnagrafiche findByAnagrafica(Session ses, Integer idAnagrafica)
			throws HibernateException {
		String qs = "from CacheAnagrafiche as ca where " +
				"ca.idAnagrafica = :id1 ";
		Query q = ses.createQuery(qs);
		q.setParameter("id1", idAnagrafica, IntegerType.INSTANCE);
		List<CacheAnagrafiche> caList= (List<CacheAnagrafiche>) q.list();
		if (caList.size() == 1) {
			return caList.get(0);
		}
		if (caList.size() > 1){
			throw new HibernateException(caList.size()+" CacheAnagrafiche with id="+idAnagrafica);
		}
		return null;
	}
	
}
