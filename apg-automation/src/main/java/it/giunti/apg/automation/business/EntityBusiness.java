package it.giunti.apg.automation.business;

import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Periodici;
import it.giunti.apg.shared.model.Listini;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;

public class EntityBusiness {

	@SuppressWarnings("unchecked")
	public static <T> T findEntityById(Integer objectId, Class<T> findClass) throws BusinessException {
		Session ses = SessionFactory.getSession();
		T entity = null;
		try {
			entity = (T)ses.get(findClass, objectId);
		} catch (HibernateException e) {
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return entity;
	}

	public static List<Periodici> periodiciFromUidArray(String[] uidArray) throws BusinessException {
		List<Periodici> result = null;
		Session ses = SessionFactory.getSession();
		try {
			result = periodiciFromUidArray(ses, uidArray);
		} catch (HibernateException e) {
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	public static List<Periodici> periodiciFromUidArray(Session ses, String[] uidArray) throws HibernateException {
		List<Periodici> result = new ArrayList<Periodici>();
		Date today = new Date();
		String qs = "from Periodici p where " +
				"p.uid = :s1 and " +
				"p.dataInizio <= :dt1 and " +
				"(p.dataFine is null or p.dataFine >= :dt2)";
		for (String s:uidArray) {
			Query q = ses.createQuery(qs);
			q.setParameter("s1", s, StringType.INSTANCE);
			q.setParameter("dt1", today, DateType.INSTANCE);
			q.setParameter("dt2", today, DateType.INSTANCE);
			List<Periodici> list = GenericDao.findByProperty(ses, Periodici.class, "uid", s.trim());
			if (list != null) {
				if (list.size() > 0) {
					result.addAll(list);
				}
			}
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Listini> listiniFromCodice(Session ses, Integer idPeriodico, String[] codiciArray) {
		List<Listini> result = new ArrayList<Listini>();
		String qs = "from Listini lst where " +
				"lst.tipoAbbonamento.periodico.id = :id1 and " +
				"lst.tipoAbbonamento.codice like :s1";
		for (String s:codiciArray) {
			Query q = ses.createQuery(qs);
			q.setParameter("id1", idPeriodico, IntegerType.INSTANCE);
			q.setParameter("s1", s.trim(), StringType.INSTANCE);
			List<Listini> list = (List<Listini>) q.list();
			if (list != null) {
				if (list.size() > 0) {
					result.addAll(list);
				}
			}
		}
		return result;
	}
	
}
