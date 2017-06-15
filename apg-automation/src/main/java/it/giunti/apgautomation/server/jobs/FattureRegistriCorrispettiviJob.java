package it.giunti.apgautomation.server.jobs;

import it.giunti.apg.server.ConfigUtil;
import it.giunti.apg.server.PropertyReader;
import it.giunti.apg.server.ServerConstants;
import it.giunti.apg.server.VisualLogger;
import it.giunti.apg.server.business.AvvisiBusiness;
import it.giunti.apg.server.business.FtpBusiness;
import it.giunti.apg.server.business.FtpConfig;
import it.giunti.apg.server.persistence.ConfigDao;
import it.giunti.apg.server.persistence.FattureDao;
import it.giunti.apg.server.persistence.GenericDao;
import it.giunti.apg.server.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Fatture;
import it.giunti.apg.shared.model.Periodici;
import it.giunti.apg.shared.model.Societa;
import it.giunti.apgautomation.server.business.DateBusiness;
import it.giunti.apgautomation.server.business.EntityBusiness;
import it.giunti.apgautomation.server.business.FattureRegistroMensileBusiness;
import it.giunti.apgautomation.server.business.FattureTxtBusiness;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FattureRegistriCorrispettiviJob implements Job {
	
	//private static final long serialVersionUID = 4394668127625471725L;
	static private Logger LOG = LoggerFactory.getLogger(FattureRegistriCorrispettiviJob.class);
	
	static private String REPORT_TITLE = "Creazione registro mensile dei corrispettivi giornalieri";
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyMM");
	
	@Override
	public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
		LOG.info("Started job '"+jobCtx.getJobDetail().getKey().getName()+"'");
		//param: letterePeriodici
		String letterePeriodici = (String) jobCtx.getMergedJobDataMap().get("letterePeriodici");
		if (letterePeriodici == null) throw new JobExecutionException("letterePeriodici non definito");
		if (letterePeriodici.equals("")) throw new JobExecutionException("letterePeriodici non definito");
		String[] lettereArray = letterePeriodici.split(AppConstants.STRING_SEPARATOR);
		//File suffix
		String suffix = PropertyReader.getApgStatus();
		if (suffix == null) suffix = AppConstants.APG_DEV;
		if (suffix.length() == 0) suffix = AppConstants.APG_DEV;
		if (AppConstants.APG_PROD.equalsIgnoreCase(suffix)) suffix = "";
		//param: makeDailyFile
		boolean makeDailyFile = true;
		String makeDailyFileString = (String) jobCtx.getMergedJobDataMap().get("makeDailyFile");
		if (makeDailyFileString != null) {
			if (makeDailyFileString.equals("false")) {
				makeDailyFile = false;
			}
		}
		//param: makeMonthlyFile
		boolean makeMonthlyFile = true;
		String makeMonthlyFileString = (String) jobCtx.getMergedJobDataMap().get("makeMonthlyFile");
		if (makeMonthlyFileString != null) {
			if (makeMonthlyFileString.equals("false")) {
				makeMonthlyFile = false;
			}
		}
		//param: produzione
		boolean prod = ConfigUtil.isApgProd();
		//param: debug
		boolean debug = false;
		
		//JOB
		Integer idRapporto;
		Date monthStart;
		Date monthEnd;
		List<Periodici> periodiciList;
		try {
			idRapporto = VisualLogger.get().createRapporto(
					REPORT_TITLE,
					ServerConstants.DEFAULT_SYSTEM_USER);
			Date now = new Date();
			if (prod) {
				monthStart = DateBusiness.previousMonthStart(now);
				monthEnd = DateBusiness.previousMonthEnd(now);
			} else {
				monthStart = DateBusiness.oneMonthAgoStart(now);
				monthEnd = now;	
			}
			periodiciList = EntityBusiness.periodiciFromUidArray(lettereArray);
		} catch (BusinessException e) {
			LOG.error(e.getMessage(), e);
			throw new JobExecutionException(e);
		}
		
		try {
			createRegistri(makeDailyFile, makeMonthlyFile, suffix, periodiciList,
					monthStart, monthEnd, idRapporto, prod, debug);
		} catch (BusinessException e) {
			throw new JobExecutionException(e.getMessage(), e);
		}
		LOG.info("Ended job '"+jobCtx.getJobDetail().getKey().getName()+"'");
	}
	
	public static void createRegistri(boolean makeDailyFile, boolean makeMonthlyFile,
			String suffix, List<Periodici> periodiciList,
			Date monthStart, Date monthEnd,
			Integer idRapporto, boolean prod, boolean debug) throws BusinessException {
		//Hibernate Session
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			String debugString = new ConfigDao().findValore(ses, "FattureRegistriCorrispettiviJob_debug");
  			if (debugString != null) debug = debugString.equalsIgnoreCase("true");
			
			FtpConfig ftpConfig = ConfigUtil.loadFtpFattureRegistri(ses, false);
			FtpConfig ftpConfigDebug = ConfigUtil.loadFtpFattureRegistri(ses, true);
			/* ** CREAZIONE CORRISPETTIVI QUOTIDIANI ** */
			if (makeDailyFile) {
				if (prod) {
					uploadRegCorGiornalieriFile(idRapporto, ses, periodiciList, 
							suffix, monthStart, monthEnd, ftpConfig);
				}
				if (!prod || debug) {
					//Carica sempre una copia di sicurezza
					uploadRegCorGiornalieriFile(idRapporto, ses, periodiciList, 
							suffix, monthStart, monthEnd, ftpConfigDebug);
				}
			}
			
			/* ** CREAZIONE CORRISPETTIVI MENSILI ** */
			if (makeMonthlyFile) {
				Set<String> idSocietaSet = new HashSet<String>();
				for (Periodici p:periodiciList) idSocietaSet.add(p.getIdSocieta());
				//Ciclo per societa'
				for (String idSocieta:idSocietaSet) {
					if (prod) {
						uploadRegCorMensileFile(idRapporto, ses, idSocieta, periodiciList, 
								suffix, monthStart, monthEnd, ftpConfig);
					}
					if (!prod || debug) {
						uploadRegCorMensileFile(idRapporto, ses, idSocieta, periodiciList, 
								suffix, monthStart, monthEnd, ftpConfigDebug);
					}
				}
			}
			trn.commit();
		} catch (BusinessException e) {
			trn.rollback();
			LOG.error(e.getMessage(), e);
			VisualLogger.get().addHtmlErrorLine(idRapporto, e.getMessage());
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
			try {
				VisualLogger.get().closeAndSaveRapporto(idRapporto);
				String avviso = "";
				//Avviso
				if (avviso.length() > 0) {
					avviso = "Creazione file dei corrispettivi "+avviso;
					AvvisiBusiness.writeAvviso(avviso, false, ServerConstants.DEFAULT_SYSTEM_USER);
				}
			} catch (BusinessException e) {
				LOG.error(e.getMessage(), e);
				throw new BusinessException(e.getMessage(), e);
			}
		}

	}

	
	// Corrispettivi giornalieri
	

	private static void uploadRegCorGiornalieriFile(Integer idRapporto, Session ses,
			List<Periodici> periodiciList, String suffix, Date startDt, Date finishDt,
			FtpConfig ftpConfig)
			throws BusinessException {
		try {
			//Ciclo sui periodici
			for (Periodici p:periodiciList) {
				VisualLogger.get().addHtmlInfoLine(idRapporto, "Ricerca delle fatture di '"+p.getNome()+"' dal "+
						ServerConstants.FORMAT_TIMESTAMP.format(startDt)+" al "+
						ServerConstants.FORMAT_TIMESTAMP.format(finishDt));
				Societa societa = GenericDao.findById(ses, Societa.class, p.getIdSocieta());
				//Il periodico Ã¨ del periodico selezionato
				List<Fatture> fattureList = new FattureDao().
						findByPeriodicoData(ses, p.getId(), startDt, finishDt, false);
				VisualLogger.get().addHtmlInfoLine(idRapporto, "Fatture PDF per "+p.getNome()+": "+fattureList.size());
				//Registro dei corrispettivi
				if (fattureList.size() > 0) {
					VisualLogger.get().addHtmlInfoLine(idRapporto,
							"Creazione del <b>registro corrispettivi di "+
							p.getNome()+"</b>");
					File regFile = FattureTxtBusiness.createRegCorGiornalieroFile(ses, fattureList, p);
					String remoteNameAndDir = ftpConfig.getDir()+"/"+societa.getCodiceSocieta()+
							"_registrazioni_"+p.getUid()+"_"+
							ServerConstants.FORMAT_DAY_SQL.format(finishDt)+suffix+".txt";
					VisualLogger.get().addHtmlInfoLine(idRapporto, "ftp://"+ftpConfig.getUsername()+"@"+ftpConfig.getHost()+"/"+remoteNameAndDir);
					FtpBusiness.upload(ftpConfig.getHost(), ftpConfig.getPort(), ftpConfig.getUsername(), ftpConfig.getPassword(),
							remoteNameAndDir, regFile);
					regFile.delete();
					VisualLogger.get().addHtmlInfoLine(idRapporto, "Caricamento FTP del <b>registro corrispettivi di "+
							p.getUid()+"</b> su "+ftpConfig.getHost()+"/"+remoteNameAndDir+" OK");
				}
			}
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} catch (MalformedURLException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}
	}
	
	
	// Corrispettivi mensili
	
	
	private static void uploadRegCorMensileFile(Integer idRapporto, Session ses, String idSocieta,
			List<Periodici> periodiciList, String suffix, Date startDt, Date finishDt,
			FtpConfig ftpConfig)
			throws BusinessException {
		try {
			Societa societa = GenericDao.findById(ses, Societa.class, idSocieta);
			VisualLogger.get().addHtmlInfoLine(idRapporto,
					"Ricerca delle <b>fatture "+societa.getNome()+"</b> dal "+
					ServerConstants.FORMAT_TIMESTAMP.format(startDt)+" al "+
					ServerConstants.FORMAT_TIMESTAMP.format(finishDt));
			//Registro mensile dei corrispettivi
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Creazione del <b>registro mensile corrispettivi di "+
					societa.getNome()+"</b>");
			File regFile = FattureRegistroMensileBusiness.createRegistroMensileFile(ses,
					startDt, finishDt, idSocieta);
			if (regFile != null) {
				String remoteNameAndDir = ftpConfig.getDir()+"/"+societa.getCodiceSocieta()+
						"_registromensile_"+SDF.format(finishDt)+suffix+".txt";
				VisualLogger.get().addHtmlInfoLine(idRapporto, "ftp://"+ftpConfig.getUsername()+"@"+ftpConfig.getHost()+"/"+remoteNameAndDir);
				FtpBusiness.upload(ftpConfig.getHost(), ftpConfig.getPort(), ftpConfig.getUsername(), ftpConfig.getPassword(),
						remoteNameAndDir, regFile);
				regFile.delete();
				VisualLogger.get().addHtmlInfoLine(idRapporto,
						"Caricamento FTP del <b>registro corrispettivi di "+
						societa.getNome()+"</b> su "+ftpConfig.getHost()+"/"+remoteNameAndDir+" OK");
			} else {
				VisualLogger.get().addHtmlInfoLine(idRapporto,
						"Non ci sono dati da inserire nel <b>registro corrispettivi di "+
						societa.getNome()+"</b>");
			}
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}
	}
	
}
