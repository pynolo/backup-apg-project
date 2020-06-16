package it.giunti.apg.core.business;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.ListiniDao;
import it.giunti.apg.core.persistence.MaterialiProgrammazioneDao;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Listini;
import it.giunti.apg.shared.model.MaterialiProgrammazione;
import it.giunti.apg.shared.model.Periodici;

public class FascicoliBusiness {

	public static IstanzeAbbonamenti changePeriodico(Session ses,IstanzeAbbonamenti istanzaT /*transient*/, 
			Integer idPeriodico, String siglaTipoAbbonamento) throws HibernateException {
		if (istanzaT == null) return null;
		MaterialiProgrammazioneDao mpDao = new MaterialiProgrammazioneDao();

		Periodici p = GenericDao.findById(ses, Periodici.class, idPeriodico);
		istanzaT.getAbbonamento().setPeriodico(p);
		
		//Cambia il fascicolo iniziale e verifica (iterativamente) mese fisso di inizio
		Date dataNominaleOld = DateUtil.now();//istanzaT.getFascicoloInizio().getDataNominale();
		Listini lst = null;
		int iterations = 0;
		MaterialiProgrammazione fascicoloInizio = mpDao.findFascicoloByPeriodicoDataInizio(ses, idPeriodico, dataNominaleOld);
		do {
			if (fascicoloInizio == null) {
				Calendar cal = new GregorianCalendar();
				cal.setTime(dataNominaleOld);
				cal.add(Calendar.YEAR, -1);
				fascicoloInizio = mpDao.findFascicoloByPeriodicoDataInizio(ses,
						idPeriodico,
						cal.getTime());
			}
			istanzaT.setDataInizio(fascicoloInizio.getDataNominale());
			
			//marca il cambiamento di listino solo se l'istanza è nuova il listino è davvero cambiato
			lst = new ListiniDao().findDefaultListinoByInizio(ses, idPeriodico,
					siglaTipoAbbonamento,
					fascicoloInizio.getDataNominale());
			istanzaT.setListino(lst);
			
			if (lst.getMeseInizio() != null) {
				fascicoloInizio = mpDao.changeFascicoloToMatchStartingMonth(ses,
						lst/*, fascicoloInizio*/);
				iterations++;
			} else {
				fascicoloInizio = mpDao.findFascicoloByPeriodicoDataInizio(ses,
						idPeriodico, dataNominaleOld);
				iterations = 2;
			}
			istanzaT.setDataInizio(fascicoloInizio.getDataNominale());
		} while (iterations < 2);
		
		//marca il cambiamento di listino solo se l'istanza è nuova il listino è davvero cambiato
		if (istanzaT.getId() == null || (!istanzaT.getListino().equals(lst))) { 
			istanzaT.setListino(lst);
			istanzaT.setDataCambioTipo(DateUtil.now());
		}
		
		//Cambia data fine
		setupDataFine(istanzaT);
		
		return istanzaT;
	}
	
	/**
	 * 1) acquisisce data
	 * 2) verifcare 1° uscita precedente
	 * 3) esiste uscita entro 6 mesi precedenti?
	 *  - si: la data viene spostata alla data nominale del fascicolo
	 *  - no: assegna il primo giorno del mese
	 * 4) sulla interfaccia mostro suggerimento sul fascicolo corrispondente alla data
	 */
	public static IstanzeAbbonamenti setupDataInizio(Session ses, IstanzeAbbonamenti istanzaT, Date dataInizio, String siglaTipoAbbonamento)
			throws HibernateException {
		if (istanzaT == null) return null;
		// 2) prima uscita precedente alla data
		MaterialiProgrammazione uscita = new MaterialiProgrammazioneDao()
				.findFascicoloByPeriodicoDataInizio(ses, 
						istanzaT.getAbbonamento().getPeriodico().getId(), dataInizio);
		Date normalizedDate = null;
		if (uscita != null) {
			Date sixMonthsAgo = new Date(DateUtil.now().getTime()-6*AppConstants.MONTH);
			if (uscita.getDataNominale().after(sixMonthsAgo)) {
				normalizedDate = uscita.getDataNominale();
			}
		}
		if (normalizedDate == null) {
			Calendar cal = new GregorianCalendar();
			cal.setTime(dataInizio);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			normalizedDate = cal.getTime();
		}
		dataInizio = normalizedDate;
		istanzaT.setDataInizio(normalizedDate);
		
		//marca il cambiamento di listino solo se l'istanza è nuova il listino è davvero cambiato
		Listini lst = new ListiniDao().findDefaultListinoByInizio(ses,
				istanzaT.getListino().getTipoAbbonamento().getPeriodico().getId(),
				siglaTipoAbbonamento, dataInizio);
		istanzaT.setListino(lst);
		
		//Cambia fascicolo finale
		setupDataFine(istanzaT);
		
		//marca il cambiamento di listino solo se l'istanza è nuova il listino è davvero cambiato
		if (istanzaT.getId() == null || (!istanzaT.getListino().equals(lst))) { 
			istanzaT.setListino(lst);
			istanzaT.setDataCambioTipo(DateUtil.now());
		}
		return istanzaT;
	}
	
//	public static IstanzeAbbonamenti changeFascicoloInizio(Session ses, IstanzeAbbonamenti istanzaT /*transient*/, 
//			Integer idFascicolo, String siglaTipoAbbonamento) throws HibernateException {
//		if (istanzaT == null) return null;
//		
//		Fascicoli fascicoloInizio = GenericDao.findById(ses, Fascicoli.class, idFascicolo);
//		istanzaT.setFascicoloInizio(fascicoloInizio);
//		
//		//marca il cambiamento di listino solo se l'istanza è nuova il listino è davvero cambiato
//		Listini lst = new ListiniDao().findDefaultListinoByFascicoloInizio(ses,
//				fascicoloInizio.getPeriodico().getId(),
//				siglaTipoAbbonamento,
//				fascicoloInizio.getId());
//		istanzaT.setListino(lst);
//		
//		//Cambia fascicolo finale
//		setupFascicoloFine(ses, istanzaT);
//		
//		//marca il cambiamento di listino solo se l'istanza è nuova il listino è davvero cambiato
//		if (istanzaT.getId() == null || (!istanzaT.getListino().equals(lst))) { 
//			istanzaT.setListino(lst);
//			istanzaT.setDataCambioTipo(DateUtil.now());
//			istanzaT.setFascicoliTotali(lst.getNumFascicoli());
//		}
//		return istanzaT;
//	}
	
	
//	public static void setupFascicoloFine(Session ses, IstanzeAbbonamenti ia) 
//			throws HibernateException {
//		
//		Fascicoli fascicoloFine = new FascicoliDao().findFascicoliAfterFascicolo(ses,
//				ia.getFascicoloInizio(),
//				ia.getListino().getNumFascicoli()-1);
//		ia.setFascicoloFine(fascicoloFine);
//	}

	public static void setupDataFine(IstanzeAbbonamenti ia) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(ia.getDataInizio());
		cal.add(Calendar.MONTH, ia.getListino().getDurataMesi());
		cal.add(Calendar.DAY_OF_MONTH, -1);
		ia.setDataFine(cal.getTime());
	}
}
