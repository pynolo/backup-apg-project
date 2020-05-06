package it.giunti.apg.automation.jobs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.type.DateType;
import org.hibernate.type.DoubleType;
import org.hibernate.type.StringType;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.giunti.apg.automation.AutomationConstants;
import it.giunti.apg.automation.business.DateBusiness;
import it.giunti.apg.automation.business.EntityBusiness;
import it.giunti.apg.automation.report.FatturaBean;
import it.giunti.apg.automation.report.FattureDataSource;
import it.giunti.apg.core.PropertyReader;
import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.VisualLogger;
import it.giunti.apg.core.business.AvvisiBusiness;
import it.giunti.apg.core.persistence.FattureDao;
import it.giunti.apg.core.persistence.FattureStampeDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.model.Fatture;
import it.giunti.apg.shared.model.FattureStampe;
import it.giunti.apg.shared.model.Pagamenti;
import it.giunti.apg.shared.model.Societa;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class FatturePagamentiJob implements Job {
	
	//private static final long serialVersionUID = 4394668127625471725L;
	static private Logger LOG = LoggerFactory.getLogger(FatturePagamentiJob.class);
	
	static private String REPORT_TITLE = "Creazione PDF fatture";
	
	@Override
	public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
		LOG.info("Started job '"+jobCtx.getJobDetail().getKey().getName()+"'");

		//param: idSocietaList
		String idSocietaList = (String) jobCtx.getMergedJobDataMap().get("idSocietaList");
		if (idSocietaList == null) throw new JobExecutionException("idSocietaList non definito");
		if (idSocietaList.equals("")) throw new JobExecutionException("idSocietaList non definito");
		String[] idSocietaArray = idSocietaList.split(AppConstants.STRING_SEPARATOR);

		//File suffix
		String suffix = PropertyReader.getApgStatus();
		if (suffix == null) suffix = AppConstants.APG_DEV;
		if (suffix.length() == 0) suffix = AppConstants.APG_DEV;
		if (AppConstants.APG_PROD.equalsIgnoreCase(suffix)) suffix = "";
		
		////param: dailyLimit
		//String dailyLimitString = (String) jobCtx.getMergedJobDataMap().get("dailyLimit");
		//Integer dailyLimit = null;
		//if (dailyLimitString != null) dailyLimit = Integer.parseInt(dailyLimitString);

		//Rimosso al 01/01/2020
		//boolean prod = ConfigUtil.isApgProd();
		//boolean debug = false;

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
  			//Rimosso al 01/01/2020
  			//String debugString = new ConfigDao().findValore(ses, "FattureFxeFxsJob_debug");
  			//if (debugString != null) debug = debugString.equalsIgnoreCase("true");
  			
  			Date today = DateUtil.now();
			// today = ServerConstants.FORMAT_DAY.parse("05/05/2014");
  			Date startDt = DateBusiness.daysAgoStart(today, AppConstants.FATTURE_NEW_YEAR_DELAY_DAYS);
			Date finishDt = DateBusiness.dayEnd(today);
			//if (prod) {
			//	startDt = DateBusiness.yearStart(today);
			//	finishDt = DateBusiness.dayEnd(today);
			//} else {
			//	startDt = DateBusiness.previousMonthStart(today);
			//	finishDt = DateBusiness.dayEnd(today);
			//}

				
			/* ** CREAZIONE FATTURE+ARTICOLI E, dopo, STAMPE (a partire dai bean) ** */
			
			//Rimosso al 01/01/2020
			//FtpConfig ftpConfigDebug = ConfigUtil.loadFtpFattureRegistri(ses, true);
			Set<Societa> societaSet = new HashSet<Societa>();
			for (String idSocieta:idSocietaArray) {
				Societa s = GenericDao.findById(ses, Societa.class, idSocieta);
				societaSet.add(s);
			}
			EntityBusiness.periodiciFromUidArray(ses, idSocietaArray);
			List<Fatture> fattureFinalList = new ArrayList<Fatture>();
			FattureDao fDao = new FattureDao();
			//Ciclo su tutte le societa
			for (Societa societa:societaSet) {
				
				VisualLogger.get().addHtmlInfoLine(idRapporto, "<b>"+societa.getId()+"</b>: stampa PDF e caricamento");
				VisualLogger.get().addHtmlInfoLine(idRapporto, "Ricerca fatture per cui produrre stampe");
				List<Fatture> printableList = fDao.findNotYetPrintedBySocietaData(ses, 
						societa.getId(), startDt, finishDt, false);
				
				List<FatturaBean> beanList = new ArrayList<FatturaBean>();
				if (printableList.size() > 0) {
					//Data source per jasperReports
					VisualLogger.get().addHtmlInfoLine(idRapporto, "Inizializzazione dei dati del report");
					FattureDataSource.initDataSource(idRapporto, printableList);
					beanList = FattureDataSource.createBeanCollection(ses);
				}
				VisualLogger.get().addHtmlInfoLine(idRapporto, beanList.size()+" fatture da stampare in PDF");
				//Produzione dei dati PDF
				if (beanList.size() > 0) {
					//Rendering
					VisualLogger.get().addHtmlInfoLine(idRapporto, "Rendering delle fatture");
					persistStampe(idRapporto, ses, beanList);//Crea PDF & visual logs
					//Rimosso al 01/01/2020
					//List<FattureStampe> stampeList = persistStampe(idRapporto, ses, beanList);//Crea PDF & visual logs
					VisualLogger.get().addHtmlInfoLine(idRapporto, "<b>"+beanList.size()+" fatture create</b>");
					
					//FTP
					//Rimosso al 01/01/2020
					//FtpConfig ftpConfig = ConfigUtil.loadFtpPdfBySocieta(ses, societa.getId());
					//uploadStampe(idRapporto, stampeList, suffix, ftpConfig, ftpConfigDebug, prod, debug);
					fattureFinalList.addAll(printableList);
					VisualLogger.get().addHtmlInfoLine(idRapporto, "Totale parziale: "+fattureFinalList.size()+" stampe PDF");
					if (avviso.length() > 0) avviso +=", ";
					avviso +=societa.getNome()+" ("+beanList.size()+")";
				} else {
					VisualLogger.get().addHtmlInfoLine(idRapporto, "Nessun file PDF creato.");
				}

			}
			
			
			/* ** CREAZIONE FILE ACCOMPAGNAMENTO ** */
			
			//Rimosso al 24/04/2020
			//if (fattureFinalList != null) {
			//	VisualLogger.get().addHtmlInfoLine(idRapporto, fattureFinalList.size()+" fatture da inserire nel file di accompagnamento");
			//	if (fattureFinalList.size() > 0) {
			//		List<UploadContent> ftpContentList = createAccompagnamentoFiles(idRapporto, ses,
			//				fattureFinalList, suffix);
			//		uploadFiles(idRapporto, ses, ftpContentList);
			//	}
			//}
			
			trn.commit();
	  	} catch (HibernateException e) {
			trn.rollback();
			VisualLogger.get().addHtmlErrorLine(idRapporto, "ERROR: "+e.getMessage());
			throw new JobExecutionException(e);
		} catch (BusinessException e) {
			trn.rollback();
			VisualLogger.get().addHtmlErrorLine(idRapporto, "ERROR: "+e.getMessage());
			throw new JobExecutionException(e);
	  	} catch (IOException e) {
			trn.rollback();
			VisualLogger.get().addHtmlErrorLine(idRapporto, "ERROR: "+e.getMessage());
			throw new JobExecutionException(e);
	  	} catch (JRException e) {
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

//	private void uploadStampe(int idRapporto, List<FattureStampe> stampeList,
//			String suffix, FtpConfig ftpConfig, FtpConfig ftpConfigDebug, boolean prod, boolean debug)
//			throws BusinessException {
//		try {
//			// FTP
//			if (prod) VisualLogger.get().addHtmlInfoLine(idRapporto, "Caricamento su ftp://"+ftpConfig.getHost()+"/"+ftpConfig.getDir()+" in corso");
//			for (FattureStampe stampa:stampeList) {
//				//Creazione file
//				File sfTmpFile = File.createTempFile("stampeFatture", ".pdf");
//				sfTmpFile.deleteOnExit();
//				byte b[]=stampa.getContent();
//				FileOutputStream fos = new FileOutputStream(sfTmpFile);
//				fos.write(b);
//			    fos.close();
//				//Upload FTP
//				//VisualLogger.get().addHtmlInfoLine(idRapporto, "ftp://"+ftpDestUsername+"@"+ftpDestHost+"/"+remoteNameAndDir);
//				if (prod) {
//					String debugRemoteNameAndDir = ftpConfig.getDir()+"/"+stampa.getFileName()+suffix;
//					FtpBusiness.upload(ftpConfig.getHost(), ftpConfig.getPort(),
//							ftpConfig.getUsername(), ftpConfig.getPassword(),
//							debugRemoteNameAndDir, sfTmpFile);
//				} 
//				if (!prod || debug){
//					String remoteNameAndDir = ftpConfigDebug.getDir()+"/"+
//							stampa.getFileName()+suffix;
//					FtpBusiness.upload(ftpConfigDebug.getHost(), ftpConfigDebug.getPort(),
//							ftpConfigDebug.getUsername(), ftpConfigDebug.getPassword(),
//							remoteNameAndDir, sfTmpFile);
//				}
//				sfTmpFile.delete();
//			}
//			VisualLogger.get().addHtmlInfoLine(idRapporto, "Caricamento FTP "+stampeList.size()+" fatture OK");
//		} catch (IOException e) {
//			LOG.error(e.getMessage(), e);
//			VisualLogger.get().addHtmlErrorLine(idRapporto, e.getMessage());
//			throw new BusinessException(e.getMessage(), e);
//		}
//	}
	
	private List<FattureStampe> persistStampe(Integer idRapporto,
			Session ses, List<FatturaBean> beanList)
			throws BusinessException, JRException, IOException {
		Locale locale = new Locale("it", "IT");
		FattureDao fDao = new FattureDao();
		FattureStampeDao faDao = new FattureStampeDao();
		List<FattureStampe> stampeList = new ArrayList<FattureStampe>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(JRParameter.REPORT_LOCALE, locale);
		paramMap.put("SUBREPORT_DIR", AutomationConstants.REPORT_RESOURCES_PATH+"/");
		VisualLogger.get().addHtmlInfoLine(idRapporto, "Creazione dei pdf delle fatture e salvataggio su database");
		int c = 0;
		//Produce i singoli report per Istanza
		for (FatturaBean bean:beanList) {
			List<FatturaBean> singleItemCollection = new ArrayList<FatturaBean>();
			singleItemCollection.add(bean);
			//Attenzione, viene creato un singolo file per tutta la collection
			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(singleItemCollection);
			//Creo l'input stream della matrice del report
			InputStream reportIs = getClass().getResourceAsStream(AutomationConstants.REPORT_TEMPLATE_FATTURE);
			if (reportIs == null) throw new IOException("Could not find report file "+AutomationConstants.REPORT_TEMPLATE_FATTURE);
			//Creazione report fondendo dati e struttura
			JasperPrint print = JasperFillManager.fillReport(reportIs, paramMap, jrds);
			//Pulizia pagine bianche
			List<JRPrintPage> pages = print.getPages();
			for (Iterator<JRPrintPage> i=pages.iterator(); i.hasNext();) {
				JRPrintPage page = (JRPrintPage)i.next();
				if (page.getElements().size() == 0) i.remove();
	        }
			byte [] pdfStream = JasperExportManager.exportReportToPdf(print);
			//Persist STAMPA
			FattureStampe stampa = new FattureStampe();
			stampa.setFileName(bean.getFileName());
			stampa.setMimeType("application/pdf");
			stampa.setContent(pdfStream);
			stampa.setDataCreazione(DateUtil.now());
			faDao.save(ses, stampa);
			stampeList.add(stampa);
			//Add a reference from fattura
			Fatture fattura = bean.getFattura();
			fattura.setIdFatturaStampa(stampa.getId());
			fDao.update(ses, fattura);
			//LOG
			String codAbbo = "";
			if (bean.getIstanzaAbbonamento() != null) {
				codAbbo = " <b>"+bean.getIstanzaAbbonamento().getAbbonamento().getCodiceAbbonamento()+"</b> "+
						"["+bean.getIstanzaAbbonamento().getId()+"]";
			}
			String pagDesc = (bean.getIdTipoPagamento() == null) ? "[nessuno]" : bean.getIdTipoPagamento();
			VisualLogger.get().addHtmlInfoLine(idRapporto,
					"<b>"+bean.getFatturaNumero()+"</b>"+
					" "+ServerConstants.FORMAT_DAY.format(bean.getFatturaData())+
					" imp &euro;"+ServerConstants.FORMAT_CURRENCY.format(bean.getTotaleImponibile())+
					" iva &euro;"+ServerConstants.FORMAT_CURRENCY.format(bean.getTotaleIva())+
					" TOT &euro;"+ServerConstants.FORMAT_CURRENCY.format(bean.getTotaleFinale())+
					codAbbo+" pag:"+pagDesc);
			c++;
			if (c % ServerConstants.FATTURE_PAGE_SIZE == 0) ses.flush();
		}
		VisualLogger.get().addHtmlInfoLine(idRapporto, "Terminata la creazione e salvataggio dei pdf");
		return stampeList;
	}
	
	
	// File accompagnamento
	
	//Rimosso al 24/04/2020
	//private List<UploadContent> createAccompagnamentoFiles(int idRapporto, 
	//		Session ses, List<Fatture> fattureListToFilter, String fileSuffix) 
	//		throws IOException {
	//	List<UploadContent> ftpContentList = new ArrayList<UploadContent>();
	//	Map<String, List<Fatture>> fattMap = new HashMap<String, List<Fatture>>();
	//	//Group fatture by societa & year
	//	for (Fatture fatt:fattureListToFilter) {
	//		if (!FattureBusiness.isFittizia(fatt)) {
	//			String key = fatt.getIdSocieta()+"-"+ServerConstants.FORMAT_YEAR.format(fatt.getDataFattura());
	//			List<Fatture> fl = fattMap.get(key);
	//			if (fl == null) fl = new ArrayList<Fatture>();
	//			fl.add(fatt);
	//			fattMap.put(key,fl);
	//		}
	//	}
	//	//Create a file from each list
	//	for (String key:fattMap.keySet()) {
	//		List<Fatture> fl = fattMap.get(key);
	//		//Order list by number
	//		Collections.sort(fl, new Comparator<Fatture>() {
	//			@Override
	//			public int compare(Fatture arg0, Fatture arg1) {
	//				return arg0.getNumeroFattura().compareTo(arg1.getNumeroFattura());
	//			}
	//		});
	//		//Create files
	//		if (fl.size() > 0) {
	//			Societa societa = GenericDao.findById(ses, Societa.class, fl.get(0).getIdSocieta());
	//			VisualLogger.get().addHtmlInfoLine(idRapporto, "Fatture PDF per "+societa.getNome()+": "+fl.size()+" (delle "+fattureListToFilter.size()+" di oggi)");
	//			File corFile = FattureTxtBusiness.createAccompagnamentoPdfFile(ses, fl, societa);
	//			VisualLogger.get().addHtmlInfoLine(idRapporto, "Creazione del <b>file di accompagnamento PDF "
	//					+societa.getNome()+" esercizio "+
	//					ServerConstants.FORMAT_YEAR.format(fl.get(0).getDataFattura())+"</b>");
	//			String fileName = societa.getCodiceSocieta()+"_datixarchi_"+
	//					ServerConstants.FORMAT_FILE_NAME_TIMESTAMP.format(DateUtil.now())+
	//					fileSuffix+"."+societa.getPrefissoFatture();
	//			UploadContent uploadContent = new UploadContent();
	//			uploadContent.societa = societa;
	//			uploadContent.fileName = fileName;
	//			uploadContent.file = corFile;
	//			ftpContentList.add(uploadContent);
	//		}
	//	}
	//	return ftpContentList;
	//}
	
	//private void uploadFiles(int idRapporto, Session ses, List<UploadContent> uploadContentList) 
	//		throws IOException, BusinessException {
	//	for (UploadContent uploadContent:uploadContentList) {
	//		File corFile = uploadContent.file;
	//		FtpConfig ftpConfig = ConfigUtil.loadFtpPdfBySocieta(ses, uploadContent.societa.getId());
	//		
	//		String corRemoteNameAndDir = ftpConfig.getDir()+"/"+uploadContent.fileName;
	//		VisualLogger.get().addHtmlInfoLine(idRapporto, "ftp://"+ftpConfig.getUsername()+
	//				"@"+ftpConfig.getHost()+"/"+corRemoteNameAndDir);
	//		FtpBusiness.upload(ftpConfig.getHost(), ftpConfig.getPort(), ftpConfig.getUsername(), 
	//				ftpConfig.getPassword(), corRemoteNameAndDir, corFile);
	//		VisualLogger.get().addHtmlInfoLine(idRapporto,
	//				"Caricamento FTP di <b>"+uploadContent.fileName+"</b> terminato");
	//	}
	//}
		
	@SuppressWarnings("unchecked")
	public static List<Pagamenti> findPagamentiDaFatturare(Session ses, String idSocieta, 
			Date fromDate, Date toDate, Integer dailyLimit, int idRapporto)
			throws HibernateException, EmptyResultException {
		List<Pagamenti> finalList = new ArrayList<Pagamenti>();
		List<Pagamenti> pList = null;
		if (dailyLimit == null) dailyLimit = Integer.MAX_VALUE;
		int size = (dailyLimit < ServerConstants.FATTURE_PAGE_SIZE) ? dailyLimit : ServerConstants.FATTURE_PAGE_SIZE;
		int offset = 0;
		do {
			String iaHql = "from Pagamenti pag where " +
					"pag.importo > :d1 and "+
					"pag.idFattura is null and "+
					"pag.istanzaAbbonamento.abbonamento.periodico.idSocieta = :s1 and " + //Societa
					"pag.istanzaAbbonamento.listino.fatturaInibita = :b2 and "+//false
					"pag.dataAccredito >= :dt1 and " +
					"pag.dataAccredito <= :dt2 " +
					"order by pag.dataAccredito asc ";
			Query iaQ = ses.createQuery(iaHql);
			iaQ.setParameter("d1", 0d, DoubleType.INSTANCE);
			iaQ.setParameter("dt1", fromDate, DateType.INSTANCE);
			iaQ.setParameter("dt2", toDate, DateType.INSTANCE);
			iaQ.setParameter("s1", idSocieta, StringType.INSTANCE);
			//iaQ.setParameter("dt3", fromDate, DateType.INSTANCE);
			//iaQ.setParameter("dt4", toDate, DateType.INSTANCE);
			iaQ.setParameter("b2", Boolean.FALSE); //pagato
			iaQ.setFirstResult(offset);
			iaQ.setMaxResults(size);
			pList = iaQ.list();
			if (pList.size() > 0) {
				finalList.addAll(pList);
				offset += pList.size();
				VisualLogger.get().addHtmlInfoLine(idRapporto, "Trovati "+finalList.size()+" pagamenti per "+idSocieta);
			}
			ses.flush();
		} while ((pList.size() > 0) && (finalList.size() < dailyLimit));
		VisualLogger.get().addHtmlInfoLine(idRapporto, "Query terminata: "+finalList.size()+" pagamenti per "+idSocieta);
		
		if (finalList.size() == 0) throw new EmptyResultException("Nessun pagamento da fatturare");
		return finalList;
	}


	
	//Inner classes
	
	
	
	public static class UploadContent {
		
		public Societa societa;
		
		public String fileName;
		
		public File file;
		
	}
	
}
