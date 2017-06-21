package it.giunti.apg.updater;

import it.giunti.apg.core.persistence.EvasioniFascicoliDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.EvasioniFascicoli;
import it.giunti.apg.shared.model.Fascicoli;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;

import java.io.IOException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateStartIstanze {
	
	private static final Logger LOG = LoggerFactory.getLogger(UpdateStartIstanze.class);
	
	private static final Integer ID_PERIODICO = 4;//4=Psicologia Scuola
	private static final String TIPO_ABB = "TV";
	
	private static EvasioniFascicoliDao efDao = new EvasioniFascicoliDao();
	private static IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
	
	@SuppressWarnings("unchecked")
	public static void update()
			throws BusinessException, IOException {
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			Fascicoli fasInizio = GenericDao.findById(ses, Fascicoli.class, 205);// numero D 31
			Fascicoli fasFine = GenericDao.findById(ses, Fascicoli.class, 210);// numero D 36
			LOG.info("Estrazione abbonamenti da verificare");
			String hql = "from IstanzeAbbonamenti ia where " +
					"ia.abbonamento.periodico.id = :i1 and " +//Lettera D
					"ia.listino.tipoAbbonamento.codice like :s1 " +//TV
					"order by ia.id asc ";
			Query q = ses.createQuery(hql);
			q.setParameter("i1", ID_PERIODICO, IntegerType.INSTANCE);
			q.setParameter("s1", TIPO_ABB, StringType.INSTANCE);
			List<IstanzeAbbonamenti> iaList = q.list();
			LOG.info("Totale abbonamenti da verificare: "+iaList.size());
			int count = 0;
			for (IstanzeAbbonamenti ia:iaList) {
				List<EvasioniFascicoli> efList = efDao.findByAbbonamento(ses, ia.getAbbonamento());
				if (efList.size() > 0) {
					//Elimina evasione fascicolo 204 (D 30)
					for (EvasioniFascicoli ef:efList) {
						if (ef.getFascicolo().getId() == 204) {
							efDao.delete(ses, ef);
						}
					}
					//Sposta inizio e fine abbonamento e ricalcola il totale fascicoli
					ia.setFascicoloInizio(fasInizio);
					ia.setFascicoloFine(fasFine);
					//NON SERVE efDao.reattachEvasioniFascicoliToIstanza(ses, ia);
					updateCountFascicoli(ses, ia);
					iaDao.update(ses, ia);
				} else {
					LOG.warn("Anomalia: abbonamento "+ia.getAbbonamento().getCodiceAbbonamento()+
							" ha "+efList.size()+" fascicoli");
				}
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
