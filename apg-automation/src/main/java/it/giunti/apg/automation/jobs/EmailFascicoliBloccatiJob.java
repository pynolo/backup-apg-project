package it.giunti.apg.automation.jobs;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.giunti.apg.core.Mailer;
import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.persistence.MaterialiProgrammazioneDao;
import it.giunti.apg.core.persistence.OpzioniDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.MaterialiProgrammazione;
import it.giunti.apg.shared.model.Opzioni;

public class EmailFascicoliBloccatiJob implements Job {
	
	static private Logger LOG = LoggerFactory.getLogger(EmailFascicoliBloccatiJob.class);

	@Override
	public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
		LOG.info("Started job '"+jobCtx.getJobDetail().getKey().getName()+"'");
		//param: emailRecipients
		String emailRecipients = (String) jobCtx.getMergedJobDataMap().get("emailRecipients");
		if (emailRecipients == null) throw new JobExecutionException("emailRecipients non definito");
		if (emailRecipients.equals("")) throw new JobExecutionException("emailRecipients non definito");
		String[] recipientArray = emailRecipients.split(AppConstants.STRING_SEPARATOR);
		
		try {
			//Avvisa se ci sono fascicoli con etichette separate
			sendEtichetteFascicoliInfo(recipientArray);
		} catch (BusinessException e) {
			LOG.error(e.getMessage(), e);
			throw new JobExecutionException(e);
		}
		
		LOG.info("Ended job '"+jobCtx.getJobDetail().getKey().getName()+"'");
	}
	
	private void sendEtichetteFascicoliInfo(String[] recipientArray)
			throws BusinessException {
		//JOB
		Date today = DateUtil.now();
		OpzioniDao opzDao = new OpzioniDao();
		MaterialiProgrammazioneDao mpDao = new MaterialiProgrammazioneDao();
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.YEAR, -2);
		long startDt = cal.getTime().getTime();
		cal.add(Calendar.YEAR, +3);
		long finishDt = cal.getTime().getTime();
		String body = "";
		Session ses = SessionFactory.getSession();
		try {
			//Cerca fascicoli con etichetta separata e li notifica
			List<Opzioni> opzList = opzDao.findByDate(ses, today);
			for (Opzioni opz:opzList) {
				List<MaterialiProgrammazione> fascicoliList = mpDao.findByOpzione(ses, 
						opz.getId(), null, startDt, finishDt, true, 0, Integer.MAX_VALUE);
				for (MaterialiProgrammazione mp:fascicoliList) {
					if (mp.getMateriale().getInAttesa() && 
							(mp.getDataEstrazione() != null)) {
						body += "Il fascicolo '"+mp.getMateriale().getCodiceMeccanografico()+"' ";
						body += "di '"+opz.getNome()+"' ";
						body += "estratto il "+ServerConstants.FORMAT_DAY.format(mp.getDataEstrazione())+" ";
						body += "e' IN ATTESA e non sara' incluso negli ordini degli arretrati. \r\n";
						body += "Modificare l'impostazione 'Arretrato in attesa' per sbloccarlo. \r\n";
						body += "\r\n";
					}
				}
			}
			//Spedisce il report
			if (body.length() > 1) {
				body = "PROMEMORIA:\r\n\r\n"+body;
				sendReport("[APG] Avviso arretrati in attesa",
						recipientArray, body);
			}
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
	}
	
	private void sendReport(String subject, String[] recipients,
			String messageBody) {
		try {
			//Mailer.postMail(ServerConstants.SMTP_HOST,
			//		ServerConstants.SMTP_USER,
			//		ServerConstants.SMTP_PASSWORD,
			//		ServerConstants.SMTP_FROM,
			//		recipients, subject, messageBody);
			Mailer.postMail(ServerConstants.SMTP_HOST,
					ServerConstants.SMTP_FROM,
					recipients, subject, messageBody, false);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

}
