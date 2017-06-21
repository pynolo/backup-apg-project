package it.giunti.apg.updater;

import it.giunti.apg.core.persistence.EvasioniFascicoliDao;
import it.giunti.apg.core.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;

import java.io.IOException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReattachOldGracing {
	
	private static final Logger LOG = LoggerFactory.getLogger(ReattachOldGracing.class);
	
	private static EvasioniFascicoliDao efDao = new EvasioniFascicoliDao();
	private static IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
	
	@SuppressWarnings("unchecked")
	public static void reattachOldGracing()
			throws BusinessException, IOException {
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			LOG.info("Estrazione abbonamenti da verificare");
			String hql = "from IstanzeAbbonamenti ia where " +
					"ia.invioBloccato = :b1 and " +//FALSE
					"ia.listino.cartaceo = :b2 and " +//TRUE
					"ia.fascicoloInizio.dataEstrazione is not null and " +
					"ia.fascicoloInizio.id not in (" +
						"select ef.fascicolo.id from EvasioniFascicoli ef where " +
						"ef.idIstanzaAbbonamento=ia.id and " +
						"ef.fascicolo.id=ia.fascicoloInizio.id " +
						") " +
					"order by ia.id asc ";
			Query q = ses.createQuery(hql);
			q.setParameter("b1", Boolean.FALSE);
			q.setParameter("b2", Boolean.TRUE);
			List<IstanzeAbbonamenti> iaList = q.list();
			LOG.info("Totale abbonamenti da verificare: "+iaList.size());
			int count = 0;
			for (IstanzeAbbonamenti ia:iaList) {
				efDao.reattachEvasioniFascicoliToIstanza(ses, ia);
				updateCountFascicoli(ses, ia);
				count++;
				if (count%100 == 0) LOG.info("Verificati "+count+"/"+iaList.size());
			}
			trn.commit();
			LOG.info("Termine verifica: "+count+"/"+iaList.size());
		} catch (HibernateException e) {
			trn.rollback();
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		
	}
	
	private static void updateCountFascicoli(Session ses, IstanzeAbbonamenti ia) 
			throws HibernateException {
		int newSpediti = efDao.countFascicoliSpediti(ses, ia.getId());
		if (ia.getFascicoliSpediti() != newSpediti) {
			ia.setFascicoliSpediti(newSpediti);
			iaDao.update(ses, ia);
		}
	}
}
