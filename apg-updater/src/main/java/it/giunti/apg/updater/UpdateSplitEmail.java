package it.giunti.apg.updater;

import it.giunti.apg.core.persistence.AnagraficheDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.Anagrafiche;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateSplitEmail {

	private static final Logger LOG = LoggerFactory.getLogger(UpdateUltimaDellaSerie.class);
	
	private static int PAGE_SIZE = 500;
	
	private static AnagraficheDao anaDao = new AnagraficheDao();
	
	@SuppressWarnings("unchecked")
	public static void updateAbbonamenti() 
			throws BusinessException, IOException {
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		List<Anagrafiche> aList = new ArrayList<Anagrafiche>();
		int offset = 0;
		int errorCount = 0;
		String hql = "from Anagrafiche a where " +
				"a.emailPrimaria like :s1 or "+
				"a.emailPrimaria like :s2 "+
				"order by a.id";
		try {
			do {
				Query q = ses.createQuery(hql);
				q.setParameter("s1", "%,%");
				q.setParameter("s2", "%;%");
				q.setFirstResult(offset);
				q.setMaxResults(PAGE_SIZE);
				aList = (List<Anagrafiche>) q.list();
				for (Anagrafiche a:aList) {
					errorCount += splitEmail(ses, a);
				}
				offset += aList.size();
				LOG.info(offset+" email estratte. Errori: "+errorCount);
				ses.flush();
				ses.clear();
			} while (aList.size() == PAGE_SIZE);
			trn.commit();
		} catch (HibernateException e) {
			trn.rollback();
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
	}

	private static int splitEmail(Session ses, Anagrafiche a) {
		String emailString = a.getEmailPrimaria();
		String email[] = emailString.split(",|;");
		String primaria = email[0].trim();
		if (!ValueUtil.isValidEmail(primaria)) {
			emailString = StringUtils.replace(emailString, ",", ".");
			email = emailString.split(",|;");
			primaria = email[0].trim();
			if (!ValueUtil.isValidEmail(primaria)) {
				LOG.info("ERRORE "+a.getUid()+": "+a.getEmailPrimaria());
				return 1;
			}
		}
		String secondaria = "";
		for (int i=1; i<email.length; i++) {
			secondaria += email[i].trim()+" ";
		}
		a.setEmailPrimaria(primaria);
		a.setEmailSecondaria(secondaria);
		LOG.info("OK "+a.getUid()+": "+a.getEmailPrimaria()+" - "+a.getEmailSecondaria());
		anaDao.update(ses, a);
		return 0;
	}

}
