package it.giunti.apg.automation.jobs;

import it.giunti.apg.automation.business.ComunicazioniEventBusiness;
import it.giunti.apg.core.DateUtil;
import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.VisualLogger;
import it.giunti.apg.core.persistence.ComunicazioniDao;
import it.giunti.apg.core.persistence.EvasioniComunicazioniDao;
import it.giunti.apg.core.persistence.FascicoliDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Comunicazioni;
import it.giunti.apg.shared.model.EvasioniComunicazioni;
import it.giunti.apg.shared.model.Fascicoli;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnqueueComunicazioniJob implements Job {
	
	static private Logger LOG = LoggerFactory.getLogger(EnqueueComunicazioniJob.class);

	@Override
	public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
		String jobName = jobCtx.getJobDetail().getKey().getName();
		int idRapporto;
		try {
			idRapporto = VisualLogger.get().createRapporto("Creazione comunicazioni in coda", ServerConstants.DEFAULT_SYSTEM_USER);
		} catch (BusinessException e) {
			throw new JobExecutionException(e.getMessage(), e);
		}
		LOG.info("Started job '"+jobName+"'");
		Session ses = SessionFactory.getSession();
		Transaction trx = ses.beginTransaction();
		try {
			enqueueComunicazioni(ses, idRapporto);
			trx.commit();
		} catch (Exception e) {
			LOG.info("ERROR in job '"+jobName+"'");
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new JobExecutionException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		try {
			VisualLogger.get().closeAndSaveRapporto(idRapporto);
		} catch (BusinessException e) {
			throw new JobExecutionException(e.getMessage(), e);
		}
		LOG.info("Ended job '"+jobName+"'");
	}
	
	private void enqueueComunicazioni(Session ses, int idRapporto) throws BusinessException {
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DAY_OF_MONTH, -1*AppConstants.COMUN_ROLLBACK_DAYS);
		Date fromDay = cal.getTime();
		Date now = DateUtil.now();
		ComunicazioniDao comDao = new ComunicazioniDao();
		EvasioniComunicazioniDao ecDao = new EvasioniComunicazioniDao();
		FascicoliDao fasDao = new FascicoliDao();
		
		try {
			List<EvasioniComunicazioni> ecList = new ArrayList<EvasioniComunicazioni>();
			List<Comunicazioni> comunicazioniList = comDao.findByDataPeriodico(ses, now);

			//Crea le comunicazioni relative ad eventi asincroni come creazione e pagamento di istanze
			VisualLogger.get().addHtmlInfoLine(idRapporto, "<b>Attivazione da eventi asincroni</b> (creazione, pagamento...)");
			for (Comunicazioni com:comunicazioniList) {
				if (com.getIdTipoAttivazione().equals(AppConstants.COMUN_ATTIVAZ_ALLA_CREAZIONE)) {
					List<EvasioniComunicazioni> daContattareList = 
							ComunicazioniEventBusiness.createMissingEvasioniComOnCreation(ses, 
									com, fromDay, now, ServerConstants.DEFAULT_SYSTEM_USER, idRapporto);
					ecList.addAll(daContattareList);
				}
				if (com.getIdTipoAttivazione().equals(AppConstants.COMUN_ATTIVAZ_AL_PAGAMENTO)) {
					List<EvasioniComunicazioni> daContattareList =
							ComunicazioniEventBusiness.createMissingEvasioniComOnPayment(ses,
									com, fromDay, now, ServerConstants.DEFAULT_SYSTEM_USER, idRapporto);
					ecList.addAll(daContattareList);
				}
			}
			//Rimuove chi ha un bollettino o NDD manuale!
			removeIfComunicazioneManuale(ses, ecList);
			if (ecList.size() == 0) VisualLogger.get().addHtmlInfoLine(idRapporto, "Nessuna comunicazione di questo tipo da inviare");
			
			//Crea le comunicazioni MANCANTI relative ai fascicoli spediti di recente per cui
			//comunicazioni_inviate è false
			VisualLogger.get().addHtmlInfoLine(idRapporto, "<b>Attivazione da invio fascicoli</b> (creazione, pagamento...)");
			List<Fascicoli> fascicoliList = fasDao.findByComunicazioniMancanti(ses, false);
			List<EvasioniComunicazioni> daInvioList =
					ComunicazioniEventBusiness.createMissingEvasioniComunicazioniByFascicoli(ses,
							fascicoliList, comunicazioniList, now, ServerConstants.DEFAULT_SYSTEM_USER, idRapporto);
			ecList.addAll(daInvioList);
			if (daInvioList.size() == 0) VisualLogger.get().addHtmlInfoLine(idRapporto, "Nessuna comunicazione di questo tipo da inviare");
			
			//SAVE EvasioniComunicazioni
			for (EvasioniComunicazioni ec:ecList) {
				ecDao.save(ses, ec);
			}
			//UPDATE Fascicoli
			for (Fascicoli fas:fascicoliList) {
				fas.setComunicazioniInviate(true);
				fasDao.update(ses, fas);
				VisualLogger.get().addHtmlInfoLine(idRapporto, "Fascicolo <b>"+
						fas.getTitoloNumero()+" "+fas.getPeriodico().getNome()+"</b> marcato: comunicazioni inviate");
			}
		} catch (HibernateException e) {
			throw new BusinessException(e.getMessage(), e);
		}
		
	}
	
	private void removeIfComunicazioneManuale(Session ses, List<EvasioniComunicazioni> ecTransientList) {
		EvasioniComunicazioniDao ecDao = new EvasioniComunicazioniDao();
		for (EvasioniComunicazioni ec:ecTransientList) {
			List<EvasioniComunicazioni> existingList =
					ecDao.findByIstanza(ses, ec.getIstanzaAbbonamento().getId());
			if (existingList != null) {
				if (existingList.size() > 0) {
					//Esistono delle comunicazioni previste o spedite
					for (EvasioniComunicazioni exEc:existingList) {
						if (exEc.getDataEstrazione() == null && 
								(exEc.getComunicazione() == null) ) {
							//Se esiste già una comunicazione manuale non spedita allora annulla quella automatica
							ec.setEliminato(true);
						}
					}
				}
			}
		}
	}
}
