package it.giunti.apg.core.persistence;

import it.giunti.apg.core.business.DefaultPasswordEncoder;
import it.giunti.apg.shared.model.UtentiPassword;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class UtentiPasswordDao implements BaseDao<UtentiPassword> {

	@Override
	public void update(Session ses, UtentiPassword instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
	}

	@Override
	public Serializable save(Session ses, UtentiPassword transientInstance)
			throws HibernateException {
		return GenericDao.saveGeneric(ses, transientInstance);
	}

	@Override
	public void delete(Session ses, UtentiPassword instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}
	
	public void addNewPassword(Session ses, String idUtente, String password)
			throws HibernateException {
		String md5 = null;
		if (null == password) throw new HibernateException("Password is null");
		DefaultPasswordEncoder dpe = new DefaultPasswordEncoder("MD5");
		md5 = dpe.encode(password);
		//Save new password
		UtentiPassword up = new UtentiPassword();
		up.setDataCreazione(new Date());
		up.setIdUtente(idUtente);
		up.setPasswordMd5(md5);
		new UtentiPasswordDao().save(ses, up);
	}

	@SuppressWarnings("unchecked")
	public List<UtentiPassword> findLastByUtente(Session ses, String idUtente, int historySize)
			throws HibernateException {
		//ricerca dell'ultima istanza
		String hql = "from UtentiPassword as up " +
				"where up.idUtente = :id1 " +
				"order by up.dataCreazione desc";
		Query q = ses.createQuery(hql);
		q.setFirstResult(0);
        q.setMaxResults(historySize);
		List<UtentiPassword> upList = (List<UtentiPassword>) q.list();
		return upList;
	}
	
}
