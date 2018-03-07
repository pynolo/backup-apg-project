package it.giunti.apg.core.persistence;

import it.giunti.apg.shared.model.CacheCrm;

import java.io.Serializable;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class CacheCrmDao implements BaseDao<CacheCrm> {


	@Override
	public void update(Session ses, CacheCrm instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getIdCustomer(), instance);
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
		GenericDao.deleteGeneric(ses, instance.getIdCustomer(), instance);
	}
	
	@SuppressWarnings("unchecked")
	public CacheCrm findByAnagraficheUid(Session ses, String uid)
			throws HibernateException {
		String qs = "from CacheCrm as cc where " +
				"cc.idCustomer like :s1 ";
		Query q = ses.createQuery(qs);
		q.setString("s1", uid.trim());
		List<CacheCrm> ccList= (List<CacheCrm>) q.list();
		if (ccList.size() == 1) {
			return ccList.get(0);
		}
		if (ccList.size() > 1){
			throw new HibernateException(ccList.size()+" CacheCrm with uid="+uid);
		}
		return null;
	}
	
}
