package it.giunti.apg.automation.jobs;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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

import it.giunti.apg.automation.sap.CustomDestinationDataProvider;
import it.giunti.apg.automation.sap.RfcConnectionException;
import it.giunti.apg.automation.sap.ZrfcFattElEsterne;
import it.giunti.apg.automation.sap.ZrfcFattElEsterne.ErrRow;
import it.giunti.apg.automation.sap.ZrfcFattElEsterneBusiness;
import it.giunti.apg.core.persistence.ContatoriDao;
import it.giunti.apg.core.persistence.FattureDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.Fatture;

public class SapFattureElettronicheJob implements Job {

	static private Logger LOG = LoggerFactory.getLogger(SapFattureElettronicheJob.class);
	
	private FattureDao fattDao = new FattureDao();
	private ContatoriDao contDao = new ContatoriDao();
	
	@Override
	public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
		LOG.info("Started job '"+jobCtx.getJobDetail().getKey().getName()+"'");
		
		//param: backwardDays
		String backwardDaysString = (String) jobCtx.getMergedJobDataMap().get("backwardDays");
		Integer backwardDays = ValueUtil.stoi(backwardDaysString);
		if (backwardDays == null) throw new JobExecutionException("Non sono definiti i giorni della finestra temporale");	
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
		Calendar cal = new GregorianCalendar();
		Date now = DateUtil.now();
		cal.setTime(now);
		cal.add(Calendar.DAY_OF_MONTH, -1*backwardDays);
		Date daysAgo = cal.getTime();
		
		Session ses = SessionFactory.getSession();
		Integer idInvio = null;
		List<Fatture> fattList = null;
		JCoDestination sapDestination = null;
		try {
			//Destination SAP
			sapDestination = new CustomDestinationDataProvider(
					ashost, gwhost, sysnr, client, user, passwd, lang)
					.getDestination();
			//Fatture da inviare
			fattList = fattDao.findByInvioSap(ses, daysAgo);
			//Ordina le fatture per numero
			Collections.sort(fattList, new Comparator<Fatture>() {
				@Override
				public int compare(Fatture o1, Fatture o2) {
					int comparation = o1.getNumeroFattura().compareTo(o2.getNumeroFattura());
					return comparation;
				}
			});
		} catch (HibernateException | BusinessException e) {
			LOG.error(e.getMessage(), e);
			throw new JobExecutionException(e);
		} finally {
			ses.close();
		}
		
		if (fattList.size() == 0) {
			LOG.info("Nessuna fattura da inviare a SAP / fatturazione elettronica");
		} else {
			// Extract fatture: today and yesterday
			int idx = 0;
	  		boolean noErrors = true;
	  		boolean cyclesLeft = true;
			List<ZrfcFattElEsterne.ErrRow> errList = null;
			do {
				Fatture fatt = fattList.get(idx);
				ses = SessionFactory.getSession();
		  		Transaction trn = ses.beginTransaction();
		  		String errorMessage = "";
		  		try {
					//Carica e aggiorna idInvio
					idInvio = contDao.loadProgressivo(ses, AppConstants.CONTATORE_ID_INVIO_PREFIX);
					if (idInvio == null) idInvio = 0;
					idInvio++;
					contDao.updateProgressivo(ses, idInvio, AppConstants.CONTATORE_ID_INVIO_PREFIX);

					//Chiama SAP (scrivendo il log FattureInvioSap)
		  			try {
						errList = ZrfcFattElEsterneBusiness.sendFattura(ses, sapDestination, fatt, idInvio);
					} catch (RfcConnectionException | BusinessException e) {
						//Sono errori di connessione o è stata passato codice iva = null
						errorMessage += e.getMessage()+"\r\n";
						LOG.error(e.getMessage(), e);
					}
		  			if (errList.size() == 0) {
		  				//Fattura inviata
		  				fatt.setDataInvioSap(now);
		  				fattDao.update(ses, fatt);
		  			} else {
		  				//errore invio
		  				errorMessage += "Errore invio fattura elettronica "+fatt.getNumeroFattura()+"\r\n";
		  				for (ErrRow er:errList) errorMessage +=  "["+er.tabname+"]["+er.fieldname+"] "+er.message+"\r\n";
		  			}
		  			trn.commit();
			  	} catch (HibernateException e) {
					trn.rollback();
					LOG.error(e.getMessage(), e);
					throw new JobExecutionException(e);
				} finally {
					ses.close();
				}
		  		//Errori logici bloccano il ciclo ma non la transazione
		  		if (errorMessage.length() > 0) {
		  			LOG.error(errorMessage);
		  			throw new JobExecutionException(errorMessage);
		  		}
		  		idx++;
		  		noErrors = (errList.size() == 0);
		  		cyclesLeft = (idx < fattList.size());
			} while (noErrors && cyclesLeft);//Ripete se nessun errore
		}
		
		LOG.info("Ended job '"+jobCtx.getJobDetail().getKey().getName()+"'");
	}

}
