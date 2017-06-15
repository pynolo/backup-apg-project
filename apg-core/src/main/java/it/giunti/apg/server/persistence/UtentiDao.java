package it.giunti.apg.server.persistence;

import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Utenti;

import java.io.Serializable;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class UtentiDao implements BaseDao<Utenti> {

	@Override
	public void update(Session ses, Utenti instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
	}

	@Override
	public Serializable save(Session ses, Utenti transientInstance)
			throws HibernateException {
		return GenericDao.saveGeneric(ses, transientInstance);
	}

	@Override
	public void delete(Session ses, Utenti instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}
	
	//@SuppressWarnings("unchecked")
	public Utenti findUtenteByUserName(Session ses, String userName) throws HibernateException {
		Utenti result = (Utenti)ses.get(Utenti.class, userName);
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Utenti> findUtenti(Session ses, boolean showBlocked, int offset, int size) throws HibernateException {
		//ricerca dell'ultima istanza
		String hql = "from Utenti as u " +
				"where u.ruolo > :i1 " +
				"order by u.id asc";
		Query q = ses.createQuery(hql);
		if (showBlocked) {
			q.setInteger("i1", AppConstants.RUOLO_BLOCKED-1);
		} else {
			q.setInteger("i1", AppConstants.RUOLO_BLOCKED);
		}
		q.setFirstResult(offset);
        q.setMaxResults(size);
		List<Utenti> uList = (List<Utenti>) q.list();
		return uList;
	}
}
