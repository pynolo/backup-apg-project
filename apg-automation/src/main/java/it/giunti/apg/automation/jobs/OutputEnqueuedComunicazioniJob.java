package it.giunti.apg.automation.jobs;

import it.giunti.apg.automation.AutomationConstants;
import it.giunti.apg.automation.business.EntityBusiness;
import it.giunti.apg.automation.business.ReportUtil;
import it.giunti.apg.automation.report.BollettiniEcDataSource;
import it.giunti.apg.automation.report.Bollettino;
import it.giunti.apg.core.PropertyReader;
import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.VisualLogger;
import it.giunti.apg.core.business.AvvisiBusiness;
import it.giunti.apg.core.business.FileFormatComunicazioni;
import it.giunti.apg.core.business.OutputComunicazioniBusiness;
import it.giunti.apg.core.business.SortBusiness;
import it.giunti.apg.core.persistence.EvasioniComunicazioniDao;
import it.giunti.apg.core.persistence.FascicoliDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.FileException;
import it.giunti.apg.shared.model.EvasioniComunicazioni;
import it.giunti.apg.shared.model.Fascicoli;
import it.giunti.apg.shared.model.Periodici;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.jdbc.Work;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OutputEnqueuedComunicazioniJob implements Job {
	
	static private Logger LOG = LoggerFactory.getLogger(OutputEnqueuedComunicazioniJob.class);

	private static final String JOB_LABEL = "Estrazione automatica comunicazioni";
	private static final int PAGE_SIZE = 250;
	
	/** Cicla sui periodici specificati dai parametri.
	 * Per ciascun periodico cicla sui tipi media specificati dai parametri.
	 * Per ciascun tipo media crea un file (report pdf o txt) in base al tipo di comunicazione.
	 * Le comunicazioni per bollettini per evento(+manuali) vanno sempre in PDF.
	 * Tutte le altre escono come file txt.
	 */
	@Override
	public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
		String jobName = jobCtx.getJobDetail().getKey().getName();
		LOG.info("Started job '"+jobName+"'");
		Date now = new Date();
		//param: letterePeriodici
		String letterePeriodici = (String) jobCtx.getMergedJobDataMap().get("letterePeriodici");
		if (letterePeriodici == null) throw new JobExecutionException("letterePeriodici non definito");
		if (letterePeriodici.equals("")) throw new JobExecutionException("letterePeriodici non definito");
		String[] lettereArray = letterePeriodici.split(AppConstants.STRING_SEPARATOR);
		//param: idTipoMediaList
		List<String> idTipoMediaList = new ArrayList<String>();
		String idTipoMediaListParam = (String) jobCtx.getMergedJobDataMap().get("idTipoMediaList");
		if (idTipoMediaListParam != null) {
			String[] array = idTipoMediaListParam.split(AppConstants.STRING_SEPARATOR);
			for (String s:array) {
				idTipoMediaList.add(s);
			}
		}
		//param: includeComEventi
		boolean includeComEventi = false;
		String includeComEventiParam = (String) jobCtx.getMergedJobDataMap().get("includeComEventi");
		if (includeComEventiParam != null) {
			if (includeComEventiParam.equals("true")) includeComEventi=true;
		}
		//param: includeComFascicoli
		boolean includeComFascicoli = false;
		String includeComFascicoliParam = (String) jobCtx.getMergedJobDataMap().get("includeComFascicoli");
		if (includeComFascicoliParam != null) {
			if (includeComFascicoliParam.equals("true")) includeComFascicoli=true;
		}
		//param: ftpSubDir
		String ftpSubDir = (String) jobCtx.getMergedJobDataMap().get("ftpSubDir");
		if (ftpSubDir == null) throw new JobExecutionException("ftpSubDir non definito");
		//param: suffix
		String suffix = PropertyReader.getApgStatus();
		if (suffix == null) suffix = AppConstants.APG_DEV;
		if (suffix.length() == 0) suffix = AppConstants.APG_DEV;
		if (AppConstants.APG_PROD.equalsIgnoreCase(suffix)) suffix = "";
		//JOB
		Integer idRapporto;
		try {
			idRapporto = VisualLogger.get().createRapporto(
					JOB_LABEL,
					ServerConstants.DEFAULT_SYSTEM_USER);
		} catch (BusinessException e) {
			LOG.error(e.getMessage(), e);
			throw new JobExecutionException(e);
		}
		String avviso = "";
		// ESTRAZIONE
		try {
			String processedPeriodici = "";
			//Ciclo su tutti i periodici
			List<Periodici> periodici = EntityBusiness.periodiciFromUidArray(lettereArray);
			for (Periodici periodico:periodici) {
				//Genera uno per uno tutti i file relativi al periodico
				//per i vari tipi di comunicazioni accodate
				processedPeriodici = reportByPeriodico(
						periodico, suffix, idTipoMediaList,
						includeComEventi, includeComFascicoli,
						now, ftpSubDir, idRapporto);
			}
			//Avviso
			if (processedPeriodici.length() > 0) {
				avviso = "Estrazione comunicazioni accodate per: "+processedPeriodici;
				AvvisiBusiness.writeAvviso(avviso, false, ServerConstants.DEFAULT_SYSTEM_USER);
			}
		} catch (BusinessException | NumberFormatException e) {
			VisualLogger.get().addHtmlErrorLine(idRapporto, "ERROR: "+e.getMessage(), e);
			throw new JobExecutionException(e);
		} finally {
			VisualLogger.get().setLogTitle(idRapporto, JOB_LABEL);
			try {
				VisualLogger.get().closeAndSaveRapporto(idRapporto);
			} catch (BusinessException e) {
				LOG.error(e.getMessage(), e);
				throw new JobExecutionException(e);
			}
		}
		LOG.info("Ended job '"+jobName+"'");
	}
	
	
	private static String reportByPeriodico(Periodici periodico,
			String suffix, List<String> idTipoMediaList,
			boolean includeComEventi, boolean includeComFascicoli,
			Date dt, String ftpSubDir, int idRapporto) 
			throws BusinessException {
		String processedPeriodici = "";
		String nomePeriodico = periodico.getNome()+suffix;
		EvasioniComunicazioniDao ecDao = new EvasioniComunicazioniDao();
		FascicoliDao fasDao = new FascicoliDao();
		
		for (String idTipoMedia:idTipoMediaList) {
			Session ses = SessionFactory.getSession();
			Transaction trn = ses.beginTransaction();
			try {
				VisualLogger.get().addHtmlInfoLine(idRapporto, "Estrazione "+AppConstants.COMUN_MEDIA_DESC_PLUR.get(idTipoMedia)+
						" per "+periodico.getNome());
				
				if (includeComEventi) {
					//Comunicazioni per EVENTO
					List<EvasioniComunicazioni> perEventoList = new ArrayList<EvasioniComunicazioni>();
					LOG.debug("Estrazione "+idTipoMedia+" attivaz. alla creazione");
					List<EvasioniComunicazioni> creazioneList = ecDao.findEnqueuedComunicazioniByMediaAttivazione(
							ses, periodico.getId(), idTipoMedia,
							AppConstants.COMUN_ATTIVAZ_ALLA_CREAZIONE, idRapporto);
					perEventoList.addAll(creazioneList);
					LOG.debug("Estrazione "+idTipoMedia+" attivaz. al pagamento");
					List<EvasioniComunicazioni> pagamentoList = ecDao.findEnqueuedComunicazioniByMediaAttivazione(
							ses, periodico.getId(), idTipoMedia,
							AppConstants.COMUN_ATTIVAZ_AL_PAGAMENTO, idRapporto);
					perEventoList.addAll(pagamentoList);
					LOG.debug("Estrazione "+idTipoMedia+" manuali");
					List<EvasioniComunicazioni> manualiList = ecDao.findEvasioniComunicazioniManuali(
							ses, periodico.getId(), idTipoMedia, idRapporto);
					perEventoList.addAll(manualiList);
					//Dagli eventi crea un report Jasperreports se BOLLETTINO
					if (idTipoMedia.equals(AppConstants.COMUN_MEDIA_BOLLETTINO) /*|| 
							idTipoMedia.equals(AppConstants.COMUN_MEDIA_NDD) */) {
						//OUTPUT PDF
						processedPeriodici += outputBollettiniPdf(ses,
								perEventoList, idTipoMedia,
								nomePeriodico, periodico.getIdSocieta(), dt,
								ftpSubDir, idRapporto);
					}
					//Dagli eventi crea una lista testuale se LETTERA
					if (idTipoMedia.equals(AppConstants.COMUN_MEDIA_LETTERA)) {
						//OUTPUT TXT
						processedPeriodici += outputTxt(ses, perEventoList,
								idTipoMedia, nomePeriodico, periodico.getIdSocieta(), dt, ftpSubDir, idRapporto);
					}
				}
				
				if (includeComFascicoli) {
					//Comunicazioni per FASCICOLO
					List<Fascicoli> fasList = fasDao.findByEnqueuedComunicazioniPeriodicoMedia(
							ses, periodico.getId(), idTipoMedia);
					for (Fascicoli fas:fasList) {
						//Query paginata per fascicolo
						List<EvasioniComunicazioni> perFascicoloList = new ArrayList<EvasioniComunicazioni>();
						int offset = 0;
						int size = 0;
						do {
							List<EvasioniComunicazioni> list =ecDao.findEnqueuedComunicazioniByFascicolo(ses,
									fas.getId(), idTipoMedia,
									offset, PAGE_SIZE, idRapporto);
							size = list.size();
							offset += size;
							perFascicoloList.addAll(list);
						} while (size > 0);
						String idSocieta = fas.getPeriodico().getIdSocieta();
						//Dai fascicoli sono sempre estratte SOLO liste testuali
						if (idTipoMedia.equals(AppConstants.COMUN_MEDIA_BOLLETTINO) || 
								/*idTipoMedia.equals(AppConstants.COMUN_MEDIA_NDD) ||*/
								idTipoMedia.equals(AppConstants.COMUN_MEDIA_LETTERA)) {
							//OUTPUT TXT
							VisualLogger.get().addHtmlInfoLine(idRapporto, "Estrazione "+
									AppConstants.COMUN_MEDIA_DESC_PLUR.get(idTipoMedia)+
									" per "+fas.getTitoloNumero()+" "+periodico.getNome());
							processedPeriodici += outputTxt(ses, perFascicoloList,
									idTipoMedia,
									fas.getTitoloNumero()+" "+nomePeriodico, idSocieta,
									dt, ftpSubDir, idRapporto);
						}
					}
				}
				
				trn.commit();
			} catch (HibernateException e) {
				trn.rollback();
				VisualLogger.get().addHtmlErrorLine(idRapporto, e.getMessage(), e);
				throw new BusinessException(e.getMessage(), e);
			} catch (FileException e) {
				trn.rollback();
				VisualLogger.get().addHtmlErrorLine(idRapporto, e.getMessage(), e);
				throw new BusinessException(e.getMessage(), e);
			} catch (IOException e) {
				trn.rollback();
				VisualLogger.get().addHtmlErrorLine(idRapporto, e.getMessage(), e);
				throw new BusinessException(e.getMessage(), e);
			} finally {
				ses.close();
			}
		}
		return processedPeriodici;
	}
	
	
	private static String outputBollettiniPdf(Session ses,
			List<EvasioniComunicazioni> ecList, String idTipoMedia,
			String nomePeriodico, String idSocieta, Date dt, String ftpSubDir, int idRapporto)
			throws BusinessException {
		String elencoPeriodiciString = "";
		//Creazione report
		String fileDescription = AppConstants.COMUN_MEDIA_DESC_PLUR.get(idTipoMedia);
		if (ecList.size() == 0) {
			VisualLogger.get().addHtmlInfoLine(idRapporto, "<i>Non ci sono "+
					fileDescription+" da spedire</i>");
		} else {
			//Ordinamento
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Ordinamento per cap");
			new SortBusiness().sortEvasioniComunicazioni(ecList);
			//Data source
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Creazione data source");
			BollettiniEcDataSource.initDataSource(ecList);
			List<Bollettino> bolCollection = BollettiniEcDataSource.createBeanCollection(ses);
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Disegno il report");
			createBollettiniReport(ses, bolCollection, idTipoMedia,
					nomePeriodico, idSocieta, dt, ftpSubDir, idRapporto);
			//marca come stampati
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Scrittura su DB in corso");
			OutputComunicazioniBusiness.writeEvasioniComunicazioniOnDb(ses,
					ecList, dt, idRapporto, ServerConstants.DEFAULT_SYSTEM_USER);
			if (bolCollection.size() > 0) elencoPeriodiciString += "'"+nomePeriodico+"' ";
			VisualLogger.get().addHtmlInfoLine(idRapporto, "<b>"+ecList.size()
					+" "+fileDescription+" di "+nomePeriodico+" marcati come estratti</b>");
		}
		return elencoPeriodiciString;
	}
	private static void createBollettiniReport(Session ses,
			List<Bollettino> bolCollection, String idTipoMedia,
			String nomePeriodico, String idSocieta, Date dt, String ftpSubDir,
			int idRapporto) throws HibernateException {
		//Raggruppa per tipi di report, se necessario
		Map<String, List<Bollettino>> reportFileMap = new HashMap<String, List<Bollettino>>();
		for (Bollettino bol:bolCollection) {
			String key = bol.getReportFilePath();
			List<Bollettino> list = reportFileMap.get(key);
			if (list == null) {
				list = new ArrayList<Bollettino>();
				reportFileMap.put(key, list);
			}
			list.add(bol);
		}
		//Vede quanti tipi di report devono essere stampati e cicla
		Set<String> keySet = reportFileMap.keySet();
		int count = 1;
		for (String key:keySet) {
			List<Bollettino> list = reportFileMap.get(key);
			String descr = nomePeriodico;
			if (keySet.size() > 1) descr += " ("+count+")";
			ReportBollettiniWork work = new ReportBollettiniWork(
					ses, list, idTipoMedia, descr, idSocieta, dt, ftpSubDir, idRapporto);
			ses.doWork(work);//L'oggetto Work serve ad usare la Connection JDBC restando nella sessione Hibernate
			count++;
		}
	}
	
	
	private static String outputTxt(Session ses,
			List<EvasioniComunicazioni> ecList, String idTipoMedia,
			String nomePeriodico, String idSocieta, Date dt, 
			String ftpSubDir, int idRapporto)
			throws IOException, FileException, BusinessException {
		String elencoPeriodiciString = "";
		if (ecList.size() == 0) {
			VisualLogger.get().addHtmlInfoLine(idRapporto, "<i>Non ci sono "+
					AppConstants.COMUN_MEDIA_DESC_PLUR.get(idTipoMedia)+" da spedire</i>");
		} else {
			//Ordinamento secondo cap e nazione
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Ordinamento per cap e nazione");
			new SortBusiness().sortEvasioniComunicazioni(ecList);
			//Creazione file
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Formattazione dati");
			File f = File.createTempFile("destinatariCom"+nomePeriodico, ".csv");
			f.deleteOnExit();
			FileFormatComunicazioni.formatEvasioniComunicazioni(ses, f, ecList, idRapporto);
			//Invia file all'ftp
			String fileDescription = AppConstants.COMUN_MEDIA_DESC_PLUR.get(idTipoMedia);
			ReportUtil.exportReportToFtp(ses, idRapporto, f, ftpSubDir, fileDescription, nomePeriodico, idSocieta, "csv", dt);
			//marca come stampati
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Scrittura su DB in corso");
			OutputComunicazioniBusiness.writeEvasioniComunicazioniOnDb(ses,
					ecList, dt, idRapporto, ServerConstants.DEFAULT_SYSTEM_USER);
			if (ecList.size() > 0) elencoPeriodiciString += "'"+nomePeriodico+"' ";
			VisualLogger.get().addHtmlInfoLine(idRapporto, "<b>"+ecList.size()
					+" "+fileDescription+" relative a "+nomePeriodico+" marcate come estratte</b>");
		}
		return elencoPeriodiciString;
	}
	
	
	
	
	//Inner classes
	
	
	
	
	public static class ReportBollettiniWork implements Work {
		
		private Map<String, Object> paramMap = new HashMap<String, Object>();
		private Session ses = null;
		private Integer idRapporto = null;
		private String descrPeriodico = null;
		private String idSocieta = null;
		private List<Bollettino> bolCollection = null;
		private String idTipoMedia = null;
		private Date dt = null;
		private String ftpSubDir = null;
		
		public ReportBollettiniWork(Session ses, List<Bollettino> bolCollection,
				String idTipoMedia, String descrPeriodico, String idSocieta,
				Date dt, String ftpSubDir, int idRapporto) throws HibernateException {
			this.ses = ses;
			this.idRapporto = idRapporto;
			this.descrPeriodico = descrPeriodico;
			this.idTipoMedia = idTipoMedia;
			this.bolCollection = bolCollection;
			this.idSocieta = idSocieta;
			this.dt = dt;
			this.ftpSubDir = ftpSubDir;
			Locale locale = new Locale("it", "IT");
			paramMap.put(JRParameter.REPORT_LOCALE, locale);
			paramMap.put("euroImageUrl",
					AutomationConstants.REPORT_RESOURCES_PATH+AutomationConstants.IMG_EURO);
		}

		@Override
		public void execute(Connection con) throws SQLException {
			File f = null;
			String fileNamePrefix = AppConstants.COMUN_MEDIA_DESC_PLUR.get(idTipoMedia)+" ";
			try {
				f = File.createTempFile(fileNamePrefix+descrPeriodico, ".pdf");
				f.deleteOnExit();
				String reportFilePath = bolCollection.get(0).getReportFilePath();
				InputStream reportIs = getClass().getResourceAsStream(reportFilePath);
				if (reportIs == null) throw new IOException("Could not find report file "+reportFilePath);
				JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(bolCollection);
				JasperPrint print = JasperFillManager.fillReport(reportIs, paramMap, jrds);
				//Pulizia pagine bianche
				List<JRPrintPage> pages = print.getPages();
				for (Iterator<JRPrintPage> i=pages.iterator(); i.hasNext();) {
					JRPrintPage page = (JRPrintPage)i.next();
					if (page.getElements().size() == 0) i.remove();
		        }
				//Esporta in file
				JasperExportManager.exportReportToPdfFile(print, f.getAbsolutePath());
				LOG.info("report file: "+f.getAbsolutePath());
				ReportUtil.exportReportToFtp(ses, idRapporto, f, ftpSubDir, 
						fileNamePrefix.trim(), descrPeriodico, idSocieta, "pdf", dt);
			} catch (IOException|BusinessException e) {
				LOG.error(e.getMessage(), e);
				e.printStackTrace();
				throw new SQLException(e);
			} catch (JRException e) {
				LOG.error(e.getMessage(), e);
				e.printStackTrace();
				throw new SQLException(e);
			}
		}

	}
}
