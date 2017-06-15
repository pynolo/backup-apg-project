package it.giunti.apgautomation.server.jobs;

import it.giunti.apg.server.Mailer;
import it.giunti.apg.server.ServerConstants;
import it.giunti.apg.server.persistence.FascicoliDao;
import it.giunti.apg.server.persistence.PeriodiciDao;
import it.giunti.apg.server.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Fascicoli;
import it.giunti.apg.shared.model.Periodici;

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
		Date today = new Date();
		PeriodiciDao perDao = new PeriodiciDao();
		FascicoliDao fasDao = new FascicoliDao();
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.YEAR, -2);
		long startDt = cal.getTime().getTime();
		cal.add(Calendar.YEAR, +3);
		long finishDt = cal.getTime().getTime();
		String body = "";
		Session ses = SessionFactory.getSession();
		try {
			//Cerca fascicoli con etichetta separata e li notifica
			List<Periodici> perList = perDao.findByDate(ses, today);
			for (Periodici p:perList) {
				List<Fascicoli> fascicoliList = fasDao.findFascicoliByPeriodico(ses, p.getId(), null,
						startDt, finishDt,
						false, true, 0, Integer.MAX_VALUE);
				for (Fascicoli fas:fascicoliList) {
					if (fas.getInAttesa() && 
							(fas.getDataEstrazione() != null)) {
						body += "Il fascicolo '"+fas.getTitoloNumero()+"' ";
						body += "di '"+p.getNome()+"' ";
						body += "estratto il "+ServerConstants.FORMAT_DAY.format(fas.getDataEstrazione())+" ";
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
