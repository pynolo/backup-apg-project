package it.giunti.apg.updater;

import it.giunti.apg.core.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Abbonamenti;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class UpdateUltimaDellaSerie {
	private static int PAGE_SIZE = 500;
	private static IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
	
	@SuppressWarnings("unchecked")
	public static void updateAbbonamenti() 
			throws BusinessException, IOException {
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		List<Abbonamenti> aList = new ArrayList<Abbonamenti>();
		int offset = 0;
		String hql = "from Abbonamenti a order by a.id";
		try {
			do {
				Query q = ses.createQuery(hql);
				q.setFirstResult(offset);
				q.setMaxResults(PAGE_SIZE);
				aList = (List<Abbonamenti>) q.list();
				for (Abbonamenti a:aList) {
					iaDao.markUltimaDellaSerie(ses, a);
				}
				offset += aList.size();
				System.out.println("Aggiornati "+offset+" abbonamenti");
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
	
}
