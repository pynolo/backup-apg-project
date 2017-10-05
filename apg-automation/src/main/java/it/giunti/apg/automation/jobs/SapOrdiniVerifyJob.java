package it.giunti.apg.automation.jobs;

import it.giunti.apg.automation.sap.CustomDestinationDataProvider;
import it.giunti.apg.automation.sap.ZrfcApgOrdiniEvasiBusiness;
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

import com.sap.conn.jco.JCoDestination;

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
		//param: backwardDays
		String expirationDaysString = (String) jobCtx.getMergedJobDataMap().get("expirationDays");
		Integer expirationDays = ValueUtil.stoi(expirationDaysString);
		if (expirationDays == null) throw new JobExecutionException("Non sono definiti i giorni dopo cui annullare l'ordine");	
		//param: JCO_ASHOST
		String ashost = (String) jobCtx.getMergedJobDataMap().get("JCO_ASHOST");
		if (ashost == null) throw new JobExecutionException("JCO_ASHOST non definito");
		if (ashost.equals("")) throw new JobExecutionException("JCO_ASHOST non definito");
		//param: JCO_GWHOST
		String gwhost = (String) jobCtx.getMergedJobDataMap().get("JCO_GWHOST");
		if (gwhost == null) throw new JobExecutionException("JCO_GWHOST non definito");
		if (gwhost.equals("")) throw new JobExecutionException("JCO_GWHOST non definito");
		//param: JCO_SYSNR
		String sysnr = (String) jobCtx.getMergedJobDataMap().get("JCO_SYSNR");
		if (sysnr == null) throw new JobExecutionException("JCO_SYSNR non definito");
		if (sysnr.equals("")) throw new JobExecutionException("JCO_SYSNR non definito");
		//param: JCO_CLIENT
		String client = (String) jobCtx.getMergedJobDataMap().get("JCO_CLIENT");
		if (client == null) throw new JobExecutionException("JCO_CLIENT non definito");
		if (client.equals("")) throw new JobExecutionException("JCO_CLIENT non definito");
		//param: JCO_USER
		String user = (String) jobCtx.getMergedJobDataMap().get("JCO_USER");
		if (user == null) throw new JobExecutionException("JCO_USER non definito");
		if (user.equals("")) throw new JobExecutionException("JCO_USER non definito");
		//param: JCO_PASSWD
		String passwd = (String) jobCtx.getMergedJobDataMap().get("JCO_PASSWD");
		if (passwd == null) throw new JobExecutionException("JCO_PASSWD non definito");
		if (passwd.equals("")) throw new JobExecutionException("JCO_PASSWD non definito");
		//param: JCO_LANG
		String lang = (String) jobCtx.getMergedJobDataMap().get("JCO_LANG");
		if (lang == null) throw new JobExecutionException("JCO_LANG non definito");
		if (lang.equals("")) throw new JobExecutionException("JCO_LANG non definito");

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
			//Destination SAP
			JCoDestination sapDestination =
					new CustomDestinationDataProvider(ashost, gwhost, sysnr, client, user, passwd, lang)
					.getDestination();
			
			Calendar cal = new GregorianCalendar();
			cal.add(Calendar.DAY_OF_MONTH, -1*backwardDays);
			Date startDate = cal.getTime();
			cal.setTime(DateUtil.now());
			cal.add(Calendar.DAY_OF_MONTH, -1*expirationDays);
			Date expirationDate = cal.getTime();
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Verifica ordini arretrati evasi su SAP");
			verifyOrdiniSap(sapDestination, startDate, expirationDate, idRapporto);
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
	
	private void verifyOrdiniSap(JCoDestination sapDestination,
			Date startDate, Date expirationDate, int idRapporto)
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
				String avvisoString = ZrfcApgOrdiniEvasiBusiness
					.verifyAndUpdateOrders(ses, sapDestination, efList, expirationDate, today, idRapporto);
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
