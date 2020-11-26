package it.giunti.apg.automation.jobs;

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

import it.giunti.apg.automation.sap.StatoodvSapServiceBusiness;
import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.VisualLogger;
import it.giunti.apg.core.persistence.AvvisiDao;
import it.giunti.apg.core.persistence.OrdiniLogisticaDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.Avvisi;
import it.giunti.apg.shared.model.OrdiniLogistica;

public class SapOrdiniVerifyJob implements Job {
	
	//private static final long serialVersionUID = 4394668127625471725L;
	static private Logger LOG = LoggerFactory.getLogger(SapOrdiniVerifyJob.class);
	
	@Override
	public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
		LOG.info("Started job '"+jobCtx.getJobDetail().getKey().getName()+"'");
		//param: backwardDays
		String backwardDaysString = (String) jobCtx.getMergedJobDataMap().get("backwardDays");
		Integer backwardDays = ValueUtil.stoi(backwardDaysString);
		if (backwardDays == null) throw new JobExecutionException("Non sono definiti i giorni della finestra temporale");	
		
		//param: user
		String wsUser = (String) jobCtx.getMergedJobDataMap().get("user");
		if (wsUser == null) throw new JobExecutionException("'user' non definito");
		if (wsUser.equals("")) throw new JobExecutionException("'user' non definito");
		//param: pass
		String wsPass = (String) jobCtx.getMergedJobDataMap().get("pass");
		if (wsPass == null) throw new JobExecutionException("'pass' non definito");
		if (wsPass.equals("")) throw new JobExecutionException("'pass' non definito");

		//JOB
		int idRapporto;
		try {
			idRapporto = VisualLogger.get().createRapporto(
					jobCtx.getJobDetail().getKey().getName(),
					ServerConstants.DEFAULT_SYSTEM_USER);
		} catch (BusinessException e) {
			LOG.error(e.getMessage(), e);
			throw new JobExecutionException(e);
		}
		
		try {
			Calendar cal = new GregorianCalendar();
			cal.add(Calendar.DAY_OF_MONTH, -1*backwardDays);
			Date startDate = cal.getTime();

			VisualLogger.get().addHtmlInfoLine(idRapporto, "Verifica ordini arretrati evasi su SAP");
			verifyOrdiniSap(wsUser, wsPass, startDate, idRapporto);
		} catch (BusinessException e) {
			VisualLogger.get().addHtmlErrorLine(idRapporto, "ERROR: "+e.getMessage(), e);
			throw new JobExecutionException(e);
		} catch (Exception e) { //SAP lancia eccezioni non dichiarate
			VisualLogger.get().addHtmlErrorLine(idRapporto, "ERROR: "+e.getMessage(), e);
			throw new JobExecutionException(e);
		} finally {
			String titolo = "Verifica ordini arretrati evasi su SAP";
			VisualLogger.get().setLogTitle(idRapporto, titolo);
			try {
				VisualLogger.get().closeAndSaveRapporto(idRapporto);
			} catch (BusinessException e) {
				LOG.error(e.getMessage(), e);
				throw new JobExecutionException(e);
			}
		}
		LOG.info("Ended job '"+jobCtx.getJobDetail().getKey().getName()+"'");
	}
	
	private void verifyOrdiniSap(String wsUser, String wsPass,
			Date startDate, int idRapporto)
			throws BusinessException {
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			//Estrae i fascicoli da ordinare
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Ricerca ordini da verificare");
			List<OrdiniLogistica> efList = new OrdiniLogisticaDao()
				.findNuoviByDataInserimento(ses, startDate, 0, Integer.MAX_VALUE);
			//interroga sap sullo stato degli ordini e li aggiorna di conseguenza
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Ordini da riscontrare su SAP: "+efList.size());
			if (efList.size() > 0) {
				Date today = DateUtil.now();
				String avvisoString = StatoodvSapServiceBusiness
					.verifyAndUpdateOrders(ses, wsUser, wsPass, efList, today, idRapporto);
				if (avvisoString.length() > 1) {
					Avvisi avviso = new Avvisi();
					avviso.setData(today);
					avviso.setImportante(false);
					avviso.setMessaggio(avvisoString);
					avviso.setIdUtente(ServerConstants.DEFAULT_SYSTEM_USER);
					new AvvisiDao().save(ses, avviso);
				}
			}
			trn.commit();
		} catch (HibernateException e) {
			trn.rollback();
			VisualLogger.get().addHtmlErrorLine(idRapporto, e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
	}
	
}
