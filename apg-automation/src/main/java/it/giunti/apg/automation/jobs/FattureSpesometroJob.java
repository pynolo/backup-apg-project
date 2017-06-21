package it.giunti.apg.automation.jobs;

import it.giunti.apg.automation.business.DateBusiness;
import it.giunti.apg.automation.business.FattureTxtBusiness;
import it.giunti.apg.core.ConfigUtil;
import it.giunti.apg.core.PropertyReader;
import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.VisualLogger;
import it.giunti.apg.core.business.AvvisiBusiness;
import it.giunti.apg.core.business.FtpBusiness;
import it.giunti.apg.core.business.FtpConfig;
import it.giunti.apg.core.persistence.ConfigDao;
import it.giunti.apg.core.persistence.FattureDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Fatture;
import it.giunti.apg.shared.model.Societa;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FattureSpesometroJob implements Job {

	//private static final long serialVersionUID = 4394668127625471725L;
	static private Logger LOG = LoggerFactory.getLogger(FattureSpesometroJob.class);
	
	static private String REPORT_TITLE = "Creazione file spesometro con riepilogo fatture";
	
	@Override
	public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
		LOG.info("Started job '"+jobCtx.getJobDetail().getKey().getName()+"'");

		//File suffix
		String filenameSuffix = PropertyReader.getApgStatus();
		if (filenameSuffix == null) filenameSuffix = AppConstants.APG_DEV;
		if (filenameSuffix.length() == 0) filenameSuffix = AppConstants.APG_DEV;
		if (AppConstants.APG_PROD.equalsIgnoreCase(filenameSuffix)) filenameSuffix = "";
		//param: monthPeriod
		int monthPeriod = 12;
		String monthPeriodString = (String) jobCtx.getMergedJobDataMap().get("monthPeriod");
		if (monthPeriodString != null) {
			try {
				monthPeriod = Integer.parseInt(monthPeriodString);
			} catch (NumberFormatException e) {
				throw new JobExecutionException(monthPeriodString+" is not an integer", e);
			}
		}
		//param: produzione
		boolean prod = ConfigUtil.isApgProd();
		//param: debug
		boolean debug = false;
		
		//JOB
		Integer idRapporto;
		try {
			idRapporto = VisualLogger.get().createRapporto(
					REPORT_TITLE,
					ServerConstants.DEFAULT_SYSTEM_USER);
		} catch (BusinessException e) {
			LOG.error(e.getMessage(), e);
			throw new JobExecutionException(e);
		}
		String avviso = "";
		
		// Extract fatture
		Session ses = SessionFactory.getSession();
  		Transaction trn = ses.beginTransaction();
  		try {
  			//debug string
  			String debugString = new ConfigDao().findValore(ses, "FattureSpesometroJob_debug");
  			if (debugString != null) debug = debugString.equalsIgnoreCase("true");
  			
  			Date now = new Date();
  			//Date yearStart = DateBusiness.previousYearStart(now);
  			//Date yearFinish = DateBusiness.previousYearEnd(now);
  			Date periodStart = DateBusiness.previousYearlyPeriodStart(now, monthPeriod);
  			Date periodFinish = DateBusiness.previousYearlyPeriodEnd(now, monthPeriod);
  			VisualLogger.get().addHtmlInfoLine(idRapporto, "Creazione spesometro da "+
  					ServerConstants.FORMAT_DAY.format(periodStart)+" a "+
  					ServerConstants.FORMAT_DAY.format(periodFinish));
			FattureDao fDao = new FattureDao();
			
			List<Societa> societaList = GenericDao.findByClass(ses, Societa.class, "id");
			//List<Societa> societaList = new ArrayList<Societa>();
			//societaList.add(GenericDao.findById(ses, Societa.class, "GS"));//Solo GS
			for (Societa societa:societaList) {
				List<Fatture> fattureList = new ArrayList<Fatture>();
				List<Fatture> pagedList = new ArrayList<Fatture>();
				do {
					pagedList = fDao.findBySocietaData(ses, societa.getId(),
							periodStart, periodFinish, false, fattureList.size(), 250);
					fattureList.addAll(pagedList);
					ses.flush();
					ses.clear();
					VisualLogger.get().addHtmlInfoLine(idRapporto, "Caricate "+fattureList.size()+" fatture "+
							societa.getNome());
				} while (pagedList.size() > 0);
				VisualLogger.get().addHtmlInfoLine(idRapporto, fattureList.size()+" fatture "+
						societa.getNome()+" da inserire nel file spesometro");
				if (fattureList.size() > 0) {
					if (prod) {
						FtpConfig ftpConfig = ConfigUtil.loadFtpPdfBySocieta(ses, societa.getId());
						uploadSpesometroFile(idRapporto, ses, filenameSuffix,
								societa, fattureList, periodFinish, ftpConfig);
					}
					if (!prod || debug) {
						FtpConfig ftpConfigDebug = ConfigUtil.loadFtpFattureRegistri(ses, true);
						uploadSpesometroFile(idRapporto, ses, filenameSuffix,
								societa, fattureList, periodFinish, ftpConfigDebug);
					}
				}
			}
			
			trn.commit();
	  	} catch (HibernateException e) {
			trn.rollback();
			VisualLogger.get().addHtmlErrorLine(idRapporto, "ERROR: "+e.getMessage());
			throw new JobExecutionException(e);
		} catch (BusinessException e) {
			trn.rollback();
			VisualLogger.get().addHtmlErrorLine(idRapporto, "ERROR: "+e.getMessage());
			throw new JobExecutionException(e);
		} finally {
			ses.close();
			try {
				VisualLogger.get().closeAndSaveRapporto(idRapporto);
			} catch (BusinessException e) {
				LOG.error(e.getMessage(), e);
				throw new JobExecutionException(e);
			}
		}
		//Avviso FATTURE
		if (avviso.length() > 0) {
			avviso = "Estrazione fatture per "+avviso;
			try {
				AvvisiBusiness.writeAvviso(avviso, false, ServerConstants.DEFAULT_SYSTEM_USER);
			} catch (BusinessException e) {
				trn.rollback();
				VisualLogger.get().addHtmlErrorLine(idRapporto, "WARNING: "+e.getMessage());
			}
		}
		LOG.info("Ended job '"+jobCtx.getJobDetail().getKey().getName()+"'");
	}

	
	// File accompagnamento
	
	
	private void uploadSpesometroFile(int idRapporto, Session ses,
			String filenameSuffix, Societa societa, List<Fatture> fattureList, Date finishDate,
			FtpConfig ftpConfig) 
			throws BusinessException {
		try {
			//We have a filtered list now
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Societa' "+societa.getNome()+": "+fattureList.size());
			if (fattureList.size() > 0) {
				File corFile = FattureTxtBusiness.createSpesometroFile(ses, fattureList, societa);
				String remoteNameAndDir = ftpConfig.getDir()+"/"+societa.getCodiceSocieta()+
						"_spesometro_"+ServerConstants.FORMAT_FILE_NAME_TIMESTAMP.format(finishDate)+filenameSuffix+".txt";
				VisualLogger.get().addHtmlInfoLine(idRapporto, "ftp://"+ftpConfig.getUsername()+"@"+ftpConfig.getHost()+"/"+remoteNameAndDir);
				FtpBusiness.upload(ftpConfig.getHost(), ftpConfig.getPort(), ftpConfig.getUsername(), ftpConfig.getPassword(),
						remoteNameAndDir, corFile);
				VisualLogger.get().addHtmlInfoLine(idRapporto, "Caricamento FTP del <b>file spesometro "+
						societa.getNome()+"</b> OK");
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

}
