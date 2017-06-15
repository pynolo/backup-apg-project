package it.giunti.apg.updater;

import it.giunti.apg.server.ServerConstants;
import it.giunti.apg.server.persistence.EvasioniFascicoliDao;
import it.giunti.apg.server.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.EvasioniFascicoli;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.type.DateType;
import org.hibernate.type.StringType;

public class AttachMissingFascicoli {
	
	private static Date DEFAULT_DATE;
	static {
		try {
			DEFAULT_DATE = new SimpleDateFormat("yyyy-MM-dd").parse("1970-01-01");
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public static void attachMissingFascicoli(boolean markAsSent, String letteraPeriodico) throws BusinessException {
		Date now = new Date();
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		EvasioniFascicoliDao efDao = new EvasioniFascicoliDao();
		try {
			List<IstanzeAbbonamenti> iaInizialiList = findIstanzeMissingFascicoli(ses, letteraPeriodico);
			List<IstanzeAbbonamenti> iaIntermediList = checkFascicoliMancantiByPeriodico(ses, letteraPeriodico);
			List<IstanzeAbbonamenti> iaList = new ArrayList<IstanzeAbbonamenti>();
			iaList.addAll(iaInizialiList);
			iaList.addAll(iaIntermediList);
			int fasCount = 0;
			for (IstanzeAbbonamenti ia:iaList) {
				List<EvasioniFascicoli> efList = efDao.enqueueMissingArretratiByStatus(ses,
						ia,
						now,
						ia.getIdUtente());
				if (markAsSent) {
					for (EvasioniFascicoli ef:efList) {
						ef.setDataInvio(DEFAULT_DATE);
						efDao.update(ses, ef);
					}
				}
				String logString = ia.getAbbonamento().getCodiceAbbonamento()+" "+
						ia.getListino().getTipoAbbonamento().getCodice();
				for (EvasioniFascicoli ef:efList) {
					logString += " "+
							ef.getFascicolo().getTitoloNumero()+"("+
							ServerConstants.FORMAT_DAY.format(ef.getFascicolo().getDataInizio())+")";
					fasCount++;
				}
				logString += "; ";
				System.out.println(logString);
			}
			trn.commit();
			System.out.println("Aggiunti "+fasCount+" fascicoli");
		} catch (HibernateException e) {
			trn.rollback();
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	private static List<IstanzeAbbonamenti> findIstanzeMissingFascicoli(Session ses, String letteraPeriodico) {
		String qs = "from IstanzeAbbonamenti ia where "+
				"ia.abbonamento.codiceAbbonamento like :s1 and "+
				"ia.invioBloccato = :b1 and "+
				"ia.listino.cartaceo = :b2 and "+
				"ia.fascicoloInizio.dataEstrazione is not null and "+
				"ia.id not in (select ef.idIstanzaAbbonamento from EvasioniFascicoli ef where ia.fascicoloInizio.id=ef.fascicolo.id)";
		Query q = ses.createQuery(qs);
		q.setParameter("s1", letteraPeriodico+"%");
		q.setParameter("b1", Boolean.FALSE);
		q.setParameter("b2", Boolean.TRUE);
		List<IstanzeAbbonamenti> iaList = q.list();
		return iaList;
	}
	
	@SuppressWarnings("unchecked")
	private static List<IstanzeAbbonamenti> checkFascicoliMancantiByPeriodico(Session ses,
			String letteraPeriodico) throws BusinessException {
		Date now = new Date();
		String hql = "from IstanzeAbbonamenti ia where " +
				"ia.abbonamento.periodico.lettera = :s1 and " +
				"ia.fascicoloFine.dataNominale < :dt1 and " +
				"ia.fascicoloFine.dataEstrazione is not null and " +
				"ia.invioBloccato = :b1 and " + // is false
				"ia.fascicoliSpediti < ia.fascicoliTotali and " +
				"ia.listino.cartaceo = :b2 and " + //is cartaceo
					"(ia.pagato = :b3 or " +
					"ia.inFatturazione = :b4 or " +
					"ia.listino.fatturaDifferita = :b5 or " +
					"ia.listino.prezzo <= :d1) " +
				"order by ia.abbonamento.codiceAbbonamento";
		Query q = ses.createQuery(hql);
		q.setParameter("s1", letteraPeriodico, StringType.INSTANCE);
		q.setParameter("dt1", now, DateType.INSTANCE);
		q.setParameter("b1", Boolean.FALSE);
		q.setParameter("b2", Boolean.TRUE);
		q.setParameter("b3", Boolean.TRUE);
		q.setParameter("b4", Boolean.TRUE);
		q.setParameter("b5", Boolean.TRUE);
		q.setParameter("d1", AppConstants.SOGLIA);
		List<IstanzeAbbonamenti> iaList = (List<IstanzeAbbonamenti>) q.list();
		return iaList;
	}
}
