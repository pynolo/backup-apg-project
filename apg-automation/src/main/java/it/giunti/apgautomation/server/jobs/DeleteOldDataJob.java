package it.giunti.apgautomation.server.jobs;

import it.giunti.apg.server.Mailer;
import it.giunti.apg.server.ServerConstants;
import it.giunti.apg.server.business.FtpConfig;
import it.giunti.apg.server.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.ValueUtil;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPListParseEngine;
import org.apache.commons.net.ftp.FTPReply;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.type.DateType;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DeleteOldDataJob implements Job {
	
	static private Logger LOG = LoggerFactory.getLogger(DeleteOldDataJob.class);
	static private final String EOL = "\r\n";
	
	private String report = null;
	private FtpConfig ftpConfig = new FtpConfig(); 
	
	@Override
	public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
		LOG.info("Started job '"+jobCtx.getJobDetail().getKey().getName()+"'");
		//param: backwardDays
		String backwardDaysString = (String) jobCtx.getMergedJobDataMap().get("backwardDays");
		Integer backwardDays = ValueUtil.stoi(backwardDaysString);
		if (backwardDays == null) throw new JobExecutionException("Non sono definiti i giorni di aggiornamento");
		//param: emailRecipients
		String emailRecipients = (String) jobCtx.getMergedJobDataMap().get("emailRecipients");
		if (emailRecipients == null) throw new JobExecutionException("emailRecipients non definito");
		if (emailRecipients.equals("")) throw new JobExecutionException("emailRecipients non definito");
		String[] recipientArray = emailRecipients.split(AppConstants.STRING_SEPARATOR);

		//JOB
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DAY_OF_MONTH, (-1)*backwardDays);
		Date startDt = cal.getTime();
		report = "";
		int rowCount = 0; 
		int fileCount = 0;
		try {
			rowCount = deleteLogRows(startDt);
			fileCount = deleteFtpFiles(startDt);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new JobExecutionException(e.getMessage(), e);
		}
		//Spedisce il report
		if ((fileCount > 0) || (rowCount > 0)) {
			String subject = "[APG - "+jobCtx.getJobDetail().getKey().getName()+"] "+fileCount+" file rimossi, "+rowCount+" righe rimosse";
			sendReport(subject, recipientArray, startDt);
		}
		LOG.info(fileCount+" file rimossi, "+rowCount+" righe rimosse");
		LOG.info("Ended job '"+jobCtx.getJobDetail().getKey().getName()+"'");
	}
	
	public int deleteFtpFiles(Date startDt) throws IOException {
		FTPClient client = new FTPClient();
		int count = 0;
		try {
			client.connect(ftpConfig.getHost());
			report += client.getReplyString()+EOL;
			// After connection attempt, you should check the reply code to verify success.
			int reply = client.getReplyCode();
			if(!FTPReply.isPositiveCompletion(reply)) {
				client.disconnect();
				String msg = "FTP server refused connection.";
				report += msg+EOL;
		    	throw new IOException(msg);
			}
			//attempts login
			client.login(ftpConfig.getUsername(), ftpConfig.getPassword());
			report += client.getReplyString()+EOL;
			
			//Delete process
			count = recursiveDelete(startDt, client);
			
			//logount
			client.logout();
		} catch (IOException e) {
			throw e;
		} finally {
			if(client.isConnected()) {
				try {
					client.disconnect();
				} catch(IOException ioe) {
					throw ioe;
				}
			}
		}
		return count;
	}
	
	private int deleteLogRows(Date startDt) throws BusinessException {
		Session ses = SessionFactory.getSession();
		Transaction trx = ses.beginTransaction();
		int count = 0;
		try {
			//Rapporti
			String rapportiHql = "delete from Rapporti where " +
					"dataModifica < :dt1";
			Query rapportiQ = ses.createQuery(rapportiHql);
			rapportiQ.setParameter("dt1", startDt, DateType.INSTANCE);
			int rapportiCount = rapportiQ.executeUpdate();
			report += "Eliminate "+rapportiCount+" righe dalla tabella 'rapporti'"+EOL;
			count += rapportiCount;
			//Avvisi
			String avvisiHql = "delete from Avvisi where " +
					"data < :dt1";
			Query avvisiQ = ses.createQuery(avvisiHql);
			avvisiQ.setParameter("dt1", startDt, DateType.INSTANCE);
			int avvisiCount = avvisiQ.executeUpdate();
			report += "Eliminate "+avvisiCount+" righe dalla tabella 'avvisi'"+EOL;
			count += avvisiCount;
			//Log Editing
			String logEditHql = "delete from LogEditing where " +
					"logDatetime < :dt1";
			Query logEditQ = ses.createQuery(logEditHql);
			logEditQ.setParameter("dt1", startDt, DateType.INSTANCE);
			int logEditCount = logEditQ.executeUpdate();
			report += "Eliminate "+logEditCount+" righe dalla tabella 'log_editing'"+EOL;
			count += logEditCount;
			//Log Ws
			String logWsHql = "delete from LogWs where " +
					"logDatetime < :dt1";
			Query logWsQ = ses.createQuery(logWsHql);
			logWsQ.setParameter("dt1", startDt, DateType.INSTANCE);
			int logWsCount = logWsQ.executeUpdate();
			report += "Eliminate "+logWsCount+" righe dalla tabella 'log_ws'"+EOL;
			count += logWsCount;
			//Stampe Fatture
			String stampeHql = "delete from FattureStampe sf where " +
					"sf.dataCreazione < :dt1";
			Query stampeQ = ses.createQuery(stampeHql);
			stampeQ.setParameter("dt1", startDt, DateType.INSTANCE);
			int stampeCount = stampeQ.executeUpdate();
			report += "Rimossi "+stampeCount+" file pdf dalla tabella 'fatture_stampe'"+EOL;
			count += stampeCount;
			trx.commit();
			report += EOL;
		} catch (HibernateException e) {
			trx.rollback();
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return count;			
	}
	
	private int recursiveDelete(Date startDt, FTPClient client) throws IOException {
		int count = 0;
		FTPListParseEngine listEngine = client.initiateListParsing();
		FTPFile[] fileList = listEngine.getFiles();
		for (FTPFile f:fileList) {
			if (f.isDirectory()) {
				if (!f.getName().equals(".") && !f.getName().equals("..")) {
					//Change directory
					client.changeWorkingDirectory(f.getName());
					report += client.getReplyString()+EOL;
					count += recursiveDelete(startDt, client);
					client.changeWorkingDirectory("..");
					report += client.getReplyString()+EOL;
				}
			} else {
				if (f.isFile()) {
					//is a file
					Date fileDate = f.getTimestamp().getTime();
					if (fileDate.before(startDt)) {
						//is an old file
						client.deleteFile(f.getName());
						report += "delete "+f.getName()+EOL;
						count++;
						report += client.getReplyString()+EOL;
					}
				}
			}
		}
		return count;
	}
	
	private void sendReport(String subject, String[] recipients, Date startDt) {
		String message = "Sono stati eliminati da " +
				ftpConfig.getHost() +
				" i file piu' vecchi del " + 
				ServerConstants.FORMAT_DAY.format(startDt) + EOL +
				report;
		try {
			LOG.info(message);
			//Mailer.postMail(ServerConstants.SMTP_HOST,
			//		ServerConstants.SMTP_USER,
			//		ServerConstants.SMTP_PASSWORD,
			//		ServerConstants.SMTP_FROM,
			//		recipients, subject, message);
			Mailer.postMail(ServerConstants.SMTP_HOST,
					ServerConstants.SMTP_FROM,
					recipients, subject, message, false);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
}
