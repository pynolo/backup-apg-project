package it.giunti.apg.updater;

import it.giunti.apg.server.persistence.AnagraficheDao;
import it.giunti.apg.server.persistence.ContatoriDao;
import it.giunti.apg.server.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Anagrafiche;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class UpdateAnagraficaCodice {

	private static int PAGE_SIZE = 500;
	private static AnagraficheDao anagDao = new AnagraficheDao();
	private static ContatoriDao contDao = new ContatoriDao();
	
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
					updateCodice(ses, a);
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
	
	private static void updateCodice(Session ses, Anagrafiche anag) 
			throws HibernateException, IOException {
		String codice = contDao.generateUidCliente(ses);
		anag.setUid(codice);
		anagDao.update(ses, anag);
	}

}
