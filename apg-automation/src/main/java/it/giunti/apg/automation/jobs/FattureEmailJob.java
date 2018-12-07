package it.giunti.apg.automation.jobs;

import it.giunti.apg.automation.AutomationConstants;
import it.giunti.apg.core.LocalMailer;
import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.VisualLogger;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.Fatture;
import it.giunti.apg.shared.model.FattureStampe;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.mail.MessagingException;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.type.DateType;
import org.hibernate.type.StringType;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FattureEmailJob implements Job {
	
	static private Logger LOG = LoggerFactory.getLogger(FattureEmailJob.class);
	
	static private String REPORT_TITLE = "Invio fatture via email";

	//private int countOmaggi = 0;
	//Rimosso perché se il calcolo parte dai 6 mesi precedenti allora
	//sono contati negli omaggi anche quelli dell'anno prima
	
	@Override
	public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
		LOG.info("Started job '"+jobCtx.getJobDetail().getKey().getName()+"'");
		//param: backwardDays
		String backwardDaysString = (String) jobCtx.getMergedJobDataMap().get("backwardDays");
		Integer backwardDays = ValueUtil.stoi(backwardDaysString);
		if (backwardDays == null) throw new JobExecutionException("Non sono definiti i giorni di aggiornamento");
		//param: excludeEmptyCodFisc
		boolean excludeEmptyCodFisc = false;
		String excludeEmptyCodFiscParam = (String) jobCtx.getMergedJobDataMap().get("excludeEmptyCodFisc");
		if (excludeEmptyCodFiscParam != null) {
			if (excludeEmptyCodFiscParam.equals("true")) excludeEmptyCodFisc = true;
		}
		//param: testEmailRecipient
		String testEmailRecipient = (String) jobCtx.getMergedJobDataMap().get("testEmailRecipient");
		//param: test (test or dev environment?)
		boolean test = true;
		String testParam = (String) jobCtx.getMergedJobDataMap().get("test");
		if (testParam != null) {
			if (testParam.equals("false")) test = false;
		}
		//String suffix = PropertyReader.getApgStatus();
		//if (AppConstants.APG_PROD.equalsIgnoreCase(suffix)) test = false;
		
		//JOB
		int idRapporto;
		try {
			idRapporto = VisualLogger.get().createRapporto(
					REPORT_TITLE,
					ServerConstants.DEFAULT_SYSTEM_USER);
		} catch (BusinessException e) {
			LOG.error(e.getMessage(), e);
			throw new JobExecutionException(e);
		}
		
		// Find Stampe fatture created in the last month where
		// dataEmail=null and email != null
		Session ses = SessionFactory.getSession();
		List<EmailFatture> efList = new ArrayList<FattureEmailJob.EmailFatture>();
		try {
			Calendar cal = new GregorianCalendar();
			cal.add(Calendar.DAY_OF_MONTH, -1*backwardDays);
			Date mailingStart = cal.getTime();
			efList = findPendingEmails(ses, mailingStart, excludeEmptyCodFisc);
			VisualLogger.get().addHtmlInfoLine(idRapporto,
					"Fatture da spedire: <b>"+efList.size()+"</b> ");
		} catch (BusinessException e) {
			VisualLogger.get().addHtmlErrorLine(idRapporto, e.getClass().getSimpleName(), e);
			LOG.error(e.getMessage(), e);
			throw new JobExecutionException(e);
		} finally {
			ses.close();
		}
		
		//Invio delle singole email
		int countSent = 0;
		int countError = 0;
		for (EmailFatture ef:efList) {
			String[] recipients =  { testEmailRecipient };
			if (!test) recipients = ef.recipients;
			ses = SessionFactory.getSession();
			Transaction trn = ses.beginTransaction();
			try {
				LocalMailer.postMail(ServerConstants.SMTP_HOST,
						ServerConstants.SMTP_FROM,
						recipients, ef.subject, ef.htmlBody, true, ef.attachment);
				updateStampaFatture(ses, ef);
				deleteFattureAttachment(ef);
				trn.commit();
				countSent++;
				VisualLogger.get().addHtmlInfoLine(idRapporto,
						"Spedita fattura "+ef.numFattura+" a: <b>"+recipients[0]+"</b> ");
			} catch (MessagingException e) {
				trn.rollback();
				VisualLogger.get().addHtmlInfoLine(idRapporto,
						"Impossibile spedire fattura "+ef.numFattura+" a <b>"+recipients[0]+"</b>: "+
						e.getClass().getSimpleName());
				countError++;
			} catch (BusinessException e) {
				trn.rollback();
				VisualLogger.get().addHtmlErrorLine(idRapporto, e.getClass().getSimpleName(), e);
				LOG.error(e.getMessage(), e);
				throw new JobExecutionException(e);
			} finally {
				ses.close();
			}
		}
		
		//Chiusura rapporto
		VisualLogger.get().addHtmlInfoLine(idRapporto,"Fatture spedite: <b>"+countSent+"</b> ");
		VisualLogger.get().addHtmlInfoLine(idRapporto,"Errori di spedizione: <b>"+countError+"</b> ");
		try {
			VisualLogger.get().setLogTitle(idRapporto, REPORT_TITLE+
					" (spedite "+countSent+", errori "+countError+")");
			VisualLogger.get().closeAndSaveRapporto(idRapporto);
		} catch (BusinessException e) {
			LOG.error(e.getMessage(), e);
			throw new JobExecutionException(e);
		}
		LOG.info("Ended job '"+jobCtx.getJobDetail().getKey().getName()+"'");
	}
	
	private List<EmailFatture> findPendingEmails(Session ses, Date fromDt, boolean excludeEmptyCodFisc) 
			throws BusinessException {
		List<EmailFatture> result = new ArrayList<FattureEmailJob.EmailFatture>();
		try {
			String hql = "select sf, an "+
					"from Fatture sf, Anagrafiche an where "+
					"sf.idAnagrafica = an.id and "+
					"sf.dataEmail is null and ";
			if (excludeEmptyCodFisc) hql += "an.codiceFiscale is not null and "+
					"an.codiceFiscale not like :s1 and ";// s1 is empty string
			hql += "an.emailPrimaria is not null and "+
					"an.emailPrimaria not like :s2 and "+ //s2 is empty string
					"sf.dataFattura >= :dt1 "+
					"order by sf.id ";
			Query q = ses.createQuery(hql);
			if (excludeEmptyCodFisc) q.setParameter("s1", "", StringType.INSTANCE);
			q.setParameter("s2", "", StringType.INSTANCE);
			q.setParameter("dt1", fromDt, DateType.INSTANCE);
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.list();
			for (Object[] obj:list) {
				Fatture sf = (Fatture) obj[0];
				Anagrafiche an = (Anagrafiche) obj[1];
				EmailFatture ef = createEmailFatture(ses, sf, an);
				result.add(ef);
			}
		} catch (HibernateException e) {
			throw new BusinessException(e.getMessage(), e);
		}
		return result;
	}
	
	private EmailFatture createEmailFatture(Session ses,
			Fatture fattura, Anagrafiche an) throws BusinessException {
		EmailFatture result = new EmailFatture();
		String[] recipients = { an.getEmailPrimaria() };
		//Periodici periodico = null;
		//if (sf.getIdPeriodico() != null) periodico = GenericDao.findById(ses, Periodici.class, sf.getIdPeriodico());
		try {
			FattureStampe fs = GenericDao.findById(ses, FattureStampe.class, fattura.getIdFatturaStampa());
			if (fs == null) throw new BusinessException("No pdf data attached");
			File file = File.createTempFile("fatt_"+fattura.getNumeroFattura()+"_",".pdf");
			file.deleteOnExit();
			InputStream is = new ByteArrayInputStream(fs.getContent());
			FileOutputStream opt = new FileOutputStream(file);
			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = is.read(bytes)) != -1) {
				opt.write(bytes, 0, read);
			}
			opt.close();
			is.close();
			//Creazione EmailFatture
			result.subject = AutomationConstants.EMAIL_FATTURE_SUBJECT;
			result.htmlBody = AutomationConstants.EMAIL_FATTURE_HTML_BODY;
			result.attachment = file;
			result.recipients = recipients;
			result.idFattura = fattura.getId();
			result.numFattura = fattura.getNumeroFattura();
		} catch (FileNotFoundException e) {
			throw new BusinessException(e.getMessage(), e);
		} catch (IOException e) {
			throw new BusinessException(e.getMessage(), e);
		}
		return result;
	}
	
	private void deleteFattureAttachment(EmailFatture ef) throws BusinessException {
		try {
			File file = ef.attachment;
			file.delete();
		} catch (Exception e) {
			throw new BusinessException(e.getMessage(), e);
		}
	}
	
	private void updateStampaFatture(Session ses, EmailFatture ef) throws BusinessException {
		try {
			Fatture fattura = GenericDao.findById(ses, Fatture.class, ef.idFattura);
			fattura.setDataEmail(DateUtil.now());
			GenericDao.updateGeneric(ses, fattura.getId(), fattura);
		} catch (HibernateException e) {
			throw new BusinessException(e.getMessage(), e);
		}
	}
	
	
	// Inner Classes
	
	
	public static class EmailFatture {
		public String subject = null;
		public String htmlBody = null;
		public String[] recipients;
		public File attachment;
		public Integer idFattura;
		public String numFattura;
	}
}
