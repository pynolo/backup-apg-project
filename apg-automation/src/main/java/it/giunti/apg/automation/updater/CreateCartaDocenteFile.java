package it.giunti.apg.automation.updater;

import it.giunti.apg.automation.jobs.FattureRegistriCorrispettiviJob;
import it.giunti.apg.core.ConfigUtil;
import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.business.FtpConfig;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;

import java.text.ParseException;
import java.util.Date;

import org.hibernate.Session;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateCartaDocenteFile {
	
	static private Logger LOG = LoggerFactory.getLogger(CreateCartaDocenteFile.class);
	static private String ID_SOCIETA = AppConstants.SOCIETA_GIUNTI_EDITORE;
	//static private boolean prod = true;//TODO
	//static private boolean debug = true;//TODO
	
	public static void execute() throws JobExecutionException {
		//LOG.info("Started job '"+jobCtx.getJobDetail().getKey().getName()+"'");
		//boolean makeDailyFile = true;
		//boolean makeMonthlyFile = true;
		String suffix = "";
		//String letterePeriodici = "A;B;D;M;N;Q;W";
		//String[] lettereArray = letterePeriodici.split(AppConstants.STRING_SEPARATOR);
		Date monthStart;
		Date monthEnd;
		
		Session ses = SessionFactory.getSession();
		try {
			FtpConfig ftpConfigDebug = ConfigUtil.loadFtpFattureRegistri(ses, true);
			
			//Dicembre
			monthStart = ServerConstants.FORMAT_DATETIME.parse("01/12/2017 00:01");
			monthEnd = ServerConstants.FORMAT_DATETIME.parse("31/12/2017 23:59");
			FattureRegistriCorrispettiviJob.uploadCartadocenteFile(null, ses,
					ID_SOCIETA, suffix, monthStart, monthEnd, ftpConfigDebug);
		} catch (BusinessException | ParseException e) {
			LOG.error(e.getMessage(), e);
		} finally {
			ses.close();
		}
		
		
		//LOG.info("Ended job '"+jobCtx.getJobDetail().getKey().getName()+"'");
	}
}
