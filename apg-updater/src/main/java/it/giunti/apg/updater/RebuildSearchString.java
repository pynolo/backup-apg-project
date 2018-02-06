package it.giunti.apg.updater;

import it.giunti.apg.core.business.SearchBusiness;
import it.giunti.apg.core.persistence.AnagraficheDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Anagrafiche;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class RebuildSearchString {

	private static int PAGE_SIZE = 1000;
	private static AnagraficheDao anagDao = new AnagraficheDao();
	private static DecimalFormat df = new DecimalFormat("0.00");
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@SuppressWarnings("unchecked")
	public static void update() 
			throws BusinessException, IOException {
		
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		List<Anagrafiche> aList = new ArrayList<Anagrafiche>();
		Integer offset = 0;
		try {
			String hql = "select count(id) from Anagrafiche";
			Object result = ses.createQuery(hql).uniqueResult();
			Long totalAnag = (Long) result;
			System.out.println("Totale anagrafiche: "+totalAnag);
			Date dtStart = new Date();
			//Update Anagrafiche
			hql = "from Anagrafiche a order by a.id";
			do {
				Query q = ses.createQuery(hql);
				q.setFirstResult(offset);
				q.setMaxResults(PAGE_SIZE);
				aList = (List<Anagrafiche>) q.list();
				for (Anagrafiche a:aList) {
					a.setSearchString(SearchBusiness.buildAnagraficheSearchString(a));
					anagDao.updateUnlogged(ses, a);
				}
				offset += aList.size();
				Double perc = 100*(offset.doubleValue()/totalAnag.doubleValue());
				System.out.println("Aggiornate "+offset+" anagrafiche ("+df.format(perc)+"%) "+
						"fine stimata "+stimaFine(dtStart, offset, totalAnag));
				//ses.flush();
				//ses.clear();
				trn.commit();
				trn = ses.beginTransaction();
			} while (aList.size() == PAGE_SIZE);
			//trn.commit();
		} catch (HibernateException e) {
			trn.rollback();
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
	}
	
	
	private static String stimaFine(Date dtInizio, Integer offset, Long total) {
		Date now = new Date();
		Long elapsed = now.getTime()-dtInizio.getTime();
		Double forecastDouble = elapsed.doubleValue()*total.doubleValue()/offset.doubleValue();
		Long forecastTime = forecastDouble.longValue() + dtInizio.getTime();
		Date forecastDt = new Date(forecastTime);
		return sdf.format(forecastDt);
	}
	
}
