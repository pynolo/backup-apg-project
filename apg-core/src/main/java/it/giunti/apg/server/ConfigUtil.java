package it.giunti.apg.server;

import it.giunti.apg.server.business.FtpConfig;
import it.giunti.apg.server.persistence.ConfigDao;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;

import org.hibernate.Session;

public class ConfigUtil {
	private static final ConfigDao cDao = new ConfigDao();
	
	public static boolean isApgSviluppo() {
		String status = PropertyReader.getApgStatus();
		if (status == null) status = AppConstants.APG_DEV;
		if (status.length() == 0) status = AppConstants.APG_DEV;
		return AppConstants.APG_DEV.equalsIgnoreCase(status);
	}
	
	public static boolean isApgTest() {
		String status = PropertyReader.getApgStatus();
		if (status == null) status = AppConstants.APG_DEV;
		if (status.length() == 0) status = AppConstants.APG_DEV;
		return AppConstants.APG_TEST.equalsIgnoreCase(status);
	}
	
	public static boolean isApgProd() {
		String status = PropertyReader.getApgStatus();
		if (status == null) status = AppConstants.APG_DEV;
		if (status.length() == 0) status = AppConstants.APG_DEV;
		return AppConstants.APG_PROD.equalsIgnoreCase(status);
	}
	
	public static String getOrderPrefix(Session ses) {
		String prefix = cDao.findValore(ses, "orderPrefix");
		if (prefix == null) return "";
		return prefix;
	}
	
	public static FtpConfig loadFtpPdfBySocieta(Session ses, String idSocieta) throws BusinessException {
		FtpConfig ftpParams = null;
		if (AppConstants.SOCIETA_GIUNTI_EDITORE.equals(idSocieta)) {
			ftpParams = new FtpConfig(
					cDao.findValore(ses, "ftpFxeHost"),
					cDao.findValore(ses, "ftpFxePort"),
					cDao.findValore(ses, "ftpFxeUsername"),
					cDao.findValore(ses, "ftpFxePassword"),
					cDao.findValore(ses, "ftpFxeDir"));
		}
		if (AppConstants.SOCIETA_GIUNTI_SCUOLA.equals(idSocieta)) {
			ftpParams = new FtpConfig(
					cDao.findValore(ses, "ftpFxsHost"),
					cDao.findValore(ses, "ftpFxsPort"),
					cDao.findValore(ses, "ftpFxsUsername"),
					cDao.findValore(ses, "ftpFxsPassword"),
					cDao.findValore(ses, "ftpFxsDir"));
		}
		if (AppConstants.SOCIETA_GIUNTI_OS.equals(idSocieta)) {
			ftpParams = new FtpConfig(
					cDao.findValore(ses, "ftpFxyHost"),
					cDao.findValore(ses, "ftpFxyPort"),
					cDao.findValore(ses, "ftpFxyUsername"),
					cDao.findValore(ses, "ftpFxyPassword"),
					cDao.findValore(ses, "ftpFxyDir"));
		}
		if (ftpParams == null) throw new BusinessException("Non e' stata definita alcuna destinazione FTP");
		return ftpParams;
	}
	
	public static FtpConfig loadFtpFattureRegistri(Session ses, boolean debug) throws BusinessException {
		FtpConfig ftpParams;
		if (!debug) {
			ftpParams = new FtpConfig(
					cDao.findValore(ses, "ftpFatRegHost"),
					cDao.findValore(ses, "ftpFatRegPort"),
					cDao.findValore(ses, "ftpFatRegUsername"),
					cDao.findValore(ses, "ftpFatRegPassword"),
					cDao.findValore(ses, "ftpFatRegDir"));
		} else {
			ftpParams = new FtpConfig(
					cDao.findValore(ses, "ftpFatDbgHost"),
					cDao.findValore(ses, "ftpFatDbgPort"),
					cDao.findValore(ses, "ftpFatDbgUsername"),
					cDao.findValore(ses, "ftpFatDbgPassword"),
					cDao.findValore(ses, "ftpFatDbgDir"));
		}
		return ftpParams;
	}
	
	public static FtpConfig loadFtpBySocieta(Session ses, String idSocieta) throws BusinessException {
		FtpConfig ftpParams = null;
		if (AppConstants.SOCIETA_GIUNTI_EDITORE.equals(idSocieta)) {
			ftpParams = new FtpConfig(
					cDao.findValore(ses, "ftpGeHost"),
					cDao.findValore(ses, "ftpGePort"),
					cDao.findValore(ses, "ftpGeUsername"),
					cDao.findValore(ses, "ftpGePassword"),
					cDao.findValore(ses, "ftpGeDir"));
		}
		if (AppConstants.SOCIETA_GIUNTI_SCUOLA.equals(idSocieta)) {
			ftpParams = new FtpConfig(
					cDao.findValore(ses, "ftpGsHost"),
					cDao.findValore(ses, "ftpGsPort"),
					cDao.findValore(ses, "ftpGsUsername"),
					cDao.findValore(ses, "ftpGsPassword"),
					cDao.findValore(ses, "ftpGsDir"));
		}
		if (AppConstants.SOCIETA_GIUNTI_OS.equals(idSocieta)) {
			ftpParams = new FtpConfig(
					cDao.findValore(ses, "ftpOsHost"),
					cDao.findValore(ses, "ftpOsPort"),
					cDao.findValore(ses, "ftpOsUsername"),
					cDao.findValore(ses, "ftpOsPassword"),
					cDao.findValore(ses, "ftpOsDir"));
		}
		if (ftpParams == null) throw new BusinessException("Non e' stata definita alcuna destinazione FTP");
		return ftpParams;
	}
}
