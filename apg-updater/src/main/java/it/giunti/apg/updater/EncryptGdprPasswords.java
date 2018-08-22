package it.giunti.apg.updater;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import it.giunti.apg.core.business.Md5PasswordEncoder;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.core.persistence.UtentiDao;
import it.giunti.apg.core.persistence.UtentiPasswordDao;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.Utenti;
import it.giunti.apg.shared.model.UtentiPassword;

public class EncryptGdprPasswords {

	@SuppressWarnings("unchecked")
	public static void execute() throws BusinessException {
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			String sql = "select u.id, u.password from utenti u where u.password is not null";
			SQLQuery sqlQ = ses.createSQLQuery(sql);
			List<Object[]> list = (List<Object[]>) sqlQ.list();
			for (Object[] objArray:list) {
				String id = (String) objArray[0];
				String password = (String) objArray[1];
				if (password.length() > 0) {
					addNewPassword(ses, id, password, true);
				}
			}
			trn.commit();
		} catch (HibernateException e) {
			trn.rollback();
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
	}
	
	public static void addNewPassword(Session ses, String idUtente, String password, boolean askReset)
			throws HibernateException, BusinessException {
		UtentiPassword up = null;
		if (password != null) {
			//New password
			up = new UtentiPassword();
			up.setDataCreazione(DateUtil.now());
			up.setIdUtente(idUtente);
			String md5Password = Md5PasswordEncoder.encode(password);
			up.setPasswordMd5(md5Password);
			new UtentiPasswordDao().save(ses, up);
			//Password reset
			Utenti utente = GenericDao.findById(ses, Utenti.class, idUtente);
			utente.setPasswordReset(askReset);
			new UtentiDao().update(ses, utente);
		}
	}
}
