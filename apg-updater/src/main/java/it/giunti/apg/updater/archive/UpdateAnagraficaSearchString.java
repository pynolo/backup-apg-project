package it.giunti.apg.updater.archive;

import it.giunti.apg.core.business.SearchBusiness;
import it.giunti.apg.core.persistence.AnagraficheDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Anagrafiche;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class UpdateAnagraficaSearchString {

	private static int PAGE_SIZE = 500;
	private static AnagraficheDao anagDao = new AnagraficheDao();
	
	@SuppressWarnings("unchecked")
	public static void updateAnagraficaCodice() 
			throws BusinessException, IOException {
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		List<Anagrafiche> aList = new ArrayList<Anagrafiche>();
		int offset = 0;
		String hql = "from Anagrafiche a order by a.id";
		try {
			do {
				Query q = ses.createQuery(hql);
				q.setFirstResult(offset);
				q.setMaxResults(PAGE_SIZE);
				aList = (List<Anagrafiche>) q.list();
				for (Anagrafiche a:aList) {
					updateSearchString(ses, a);
				}
				offset += aList.size();
				System.out.println("Aggiornate "+offset+" anagrafiche");
				ses.flush();
				ses.clear();
			} while (aList.size() == PAGE_SIZE);
			trn.commit();
		} catch (HibernateException e) {
			trn.rollback();
			throw new BusinessException(e.getMessage(), e);
		} catch (IOException e) {
			trn.rollback();
			throw new IOException(e.getMessage(), e);
		} finally {
			ses.close();
		}
	}
	
	private static void updateSearchString(Session ses, Anagrafiche anag) 
			throws HibernateException, IOException {
		String searchString = SearchBusiness.buildAnagraficheSearchString(anag);
		anag.setSearchString(searchString);;
		anagDao.update(ses, anag);
	}

}
