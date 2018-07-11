package it.giunti.apg.core.persistence;

import java.io.Serializable;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import it.giunti.apg.core.business.Md5PasswordEncoder;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.UtentiPassword;

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
			throws HibernateException, BusinessException {
		UtentiPassword up = null;
		if (password != null) {
			if (password.length() >= AppConstants.MIN_PASSWORD_LENGTH) {
				up = new UtentiPassword();
				up.setDataCreazione(DateUtil.now());
				up.setIdUtente(idUtente);
				String md5Password = Md5PasswordEncoder.encode(password);
				up.setPasswordMd5(md5Password);
				new UtentiPasswordDao().save(ses, up);
			}
		}
		if (up == null) throw new BusinessException("La password è inferiore a "+AppConstants.MIN_PASSWORD_LENGTH+" caratteri");
	}

	@SuppressWarnings("unchecked")
	public boolean checkPassword(Session ses, String idUtente, String password)
			throws HibernateException, BusinessException {
		Boolean result = null;
		//Calcola MD5
		String newPasswordMd5 = null;
		if (password != null) {
			if (password.length() >= AppConstants.MIN_PASSWORD_LENGTH) {
				newPasswordMd5 = Md5PasswordEncoder.encode(password);
			}
		}
		if (newPasswordMd5 == null) throw new BusinessException("La password è inferiore a "+AppConstants.MIN_PASSWORD_LENGTH+" caratteri");
		//ricerca dell'ultima istanza
		String hql = "from UtentiPassword as up " +
				"where up.idUtente = :id1 " +
				"order by up.dataCreazione desc";
		Query q = ses.createQuery(hql);
		q.setFirstResult(0);
        q.setMaxResults(1);
		List<UtentiPassword> upList = (List<UtentiPassword>) q.list();
		if (upList != null) {
			if (upList.size() > 0) {
				//Recupera la password MD5 su DB
				String dbPasswordMd5 = upList.get(0).getPasswordMd5();
				result = (newPasswordMd5.equals(dbPasswordMd5));
			}
		}
		if (result != null) {
			return result;
		} else {
			throw new BusinessException("Impossibile confrontare la password");
		}
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
