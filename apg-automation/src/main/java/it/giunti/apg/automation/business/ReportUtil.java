package it.giunti.apg.automation.business;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.VisualLogger;
import it.giunti.apg.core.business.FtpUtil;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportUtil {
	
	static private Logger LOG = LoggerFactory.getLogger(ReportUtil.class);
	
	public static void exportReportToFtp(Integer idRapporto, File reportFile, String ftpSubDir,
			String contentDescription, String nomePeriodico, String idSocieta, String estensione, Date date)
			throws IOException, BusinessException {
		Session ses = SessionFactory.getSession();
		try {
			exportReportToFtp(ses, idRapporto, reportFile, ftpSubDir, 
					contentDescription, nomePeriodico, idSocieta, estensione, date);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
	}
	
	public static void exportReportToFtp(Session ses, Integer idRapporto, File reportFile, String ftpSubDir,
			String contentDescription, String nomePeriodico, String idSocieta, String estensione, Date date)
			throws IOException, BusinessException {
		String remoteFileName = ServerConstants.FORMAT_FILE_NAME_TIMESTAMP.format(date)+
				" Report "+contentDescription+" "+nomePeriodico+"."+estensione;
		VisualLogger.get().addHtmlInfoLine(idRapporto, "Trasferimento FTP in corso");
		FtpUtil ftpUtil = new FtpUtil(ses, idSocieta);
		boolean dirExists = true;
		if (ftpSubDir != null) {
			if (ftpSubDir.length() > 0) {
				dirExists = ftpUtil.checkDirectoryExists(ftpSubDir);
			}
		}
		if (!dirExists) {
			VisualLogger.get().addHtmlInfoLine(idRapporto, "La cartella '"+ftpSubDir+"' non esiste. I file saranno posizionati nella cartella base.");
			ftpSubDir = "";
		}
		String ftpHost = ftpUtil.fileTransfer(reportFile, ftpSubDir, remoteFileName);
		VisualLogger.get().addHtmlInfoLine(idRapporto, "Report trasferito correttamente su "+ftpHost);
	}
	
}
