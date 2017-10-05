package it.giunti.apg.automation.jobs;

import it.giunti.apg.automation.AutomationConstants;
import it.giunti.apg.automation.business.DateBusiness;
import it.giunti.apg.automation.business.EntityBusiness;
import it.giunti.apg.automation.business.FattureTxtBusiness;
import it.giunti.apg.automation.report.FatturaBean;
import it.giunti.apg.automation.report.FattureDataSource;
import it.giunti.apg.core.ConfigUtil;
import it.giunti.apg.core.PropertyReader;
import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.VisualLogger;
import it.giunti.apg.core.business.AvvisiBusiness;
import it.giunti.apg.core.business.FattureBusiness;
import it.giunti.apg.core.business.FtpBusiness;
import it.giunti.apg.core.business.FtpConfig;
import it.giunti.apg.core.persistence.ConfigDao;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
  			String debugString = new ConfigDao().findValore(ses, "FattureFxeFxsJob_debug");
  			if (debugString != null) debug = debugString.equalsIgnoreCase("true");
  			
  			Date today = DateUtil.now();
			// today = ServerConstants.FORMAT_DAY.parse("05/05/2014");
  			Date startDt = null;
			Date finishDt = null;
  			if (prod) {
				startDt = DateBusiness.yearStart(today);
				finishDt = DateBusiness.dayEnd(today);
  			} else {
				startDt = DateBusiness.previousMonthStart(today);
				finishDt = DateBusiness.dayEnd(today);
			}

				
			/* ** CREAZIONE FATTURE+ARTICOLI E, dopo, STAMPE (a partire dai bean) ** */
			
			
			FtpConfig ftpConfigDebug = ConfigUtil.loadFtpFattureRegistri(ses, true);
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
					List<FattureStampe> stampeList = persistStampe(idRapporto, ses, beanList);//Crea PDF & visual logs
					VisualLogger.get().addHtmlInfoLine(idRapporto, "<b>"+beanList.size()+" fatture create</b>");
					
					//FTP
					FtpConfig ftpConfig = ConfigUtil.loadFtpPdfBySocieta(ses, societa.getId());
					uploadStampe(idRapporto, stampeList, suffix, ftpConfig, ftpConfigDebug, prod, debug);
					fattureFinalList.addAll(printableList);
					VisualLogger.get().addHtmlInfoLine(idRapporto, "Totale parziale: "+fattureFinalList.size()+" stampe PDF");
					if (avviso.length() > 0) avviso +=", ";
					avviso +=societa.getNome()+" ("+beanList.size()+")";
				} else {
					VisualLogger.get().addHtmlInfoLine(idRapporto, "Nessun file PDF creato.");
				}

			}
			
			
			/* ** CREAZIONE FILE ACCOMPAGNAMENTO ** */
			
			
			if (fattureFinalList != null) {
				VisualLogger.get().addHtmlInfoLine(idRapporto, fattureFinalList.size()+" fatture da inserire nel file di accompagnamento");
				if (fattureFinalList.size() > 0) {
					//Ciclo per società
					for (Societa societa:societaSet) {
						if (prod) {
							FtpConfig ftpConfig = ConfigUtil.loadFtpPdfBySocieta(ses, societa.getId());
							uploadAccompagnamentoPdfFile(idRapporto, ses, societa.getId(), suffix,
									fattureFinalList, finishDt, ftpConfig);
						}
						if (!prod || debug) {
							uploadAccompagnamentoPdfFile(idRapporto, ses, societa.getId(), suffix,
									fattureFinalList, finishDt, ftpConfigDebug);
						}
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

	private void uploadStampe(int idRapporto, List<FattureStampe> stampeList,
			String suffix, FtpConfig ftpConfig, FtpConfig ftpConfigDebug, boolean prod, boolean debug)
			throws BusinessException {
		try {
			// FTP
			if (prod) VisualLogger.get().addHtmlInfoLine(idRapporto, "Caricamento su ftp://"+ftpConfig.getHost()+"/"+ftpConfig.getDir()+" in corso");
			for (FattureStampe stampa:stampeList) {
				//Creazione file
				File sfTmpFile = File.createTempFile("stampeFatture", ".pdf");
				sfTmpFile.deleteOnExit();
				byte b[]=stampa.getContent();
				FileOutputStream fos = new FileOutputStream(sfTmpFile);
				fos.write(b);
			    fos.close();
				//Upload FTP
				//VisualLogger.get().addHtmlInfoLine(idRapporto, "ftp://"+ftpDestUsername+"@"+ftpDestHost+"/"+remoteNameAndDir);
				if (prod) {
					String debugRemoteNameAndDir = ftpConfig.getDir()+"/"+stampa.getFileName()+suffix;
					FtpBusiness.upload(ftpConfig.getHost(), ftpConfig.getPort(),
							ftpConfig.getUsername(), ftpConfig.getPassword(),
							debugRemoteNameAndDir, sfTmpFile);
				} 
				if (!prod || debug){
					String remoteNameAndDir = ftpConfigDebug.getDir()+"/"+
							stampa.getFileName()+suffix;
					FtpBusiness.upload(ftpConfigDebug.getHost(), ftpConfigDebug.getPort(),
							ftpConfigDebug.getUsername(), ftpConfigDebug.getPassword(),
							remoteNameAndDir, sfTmpFile);
				}
				sfTmpFile.delete();
			}
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Caricamento FTP "+stampeList.size()+" fatture OK");
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			VisualLogger.get().addHtmlErrorLine(idRapporto, e.getMessage());
			throw new BusinessException(e.getMessage(), e);
		}
	}
	
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
	
	
	private void uploadAccompagnamentoPdfFile(int idRapporto, Session ses,
			String filteringIdSocieta, String suffix, List<Fatture> fattureListToFilter, Date today,
			FtpConfig ftpConfig) 
			throws BusinessException {
		try {
			Societa societa = GenericDao.findById(ses, Societa.class, filteringIdSocieta);
			//Filter list by societa
			List<Fatture> fattureFilteredList = new ArrayList<Fatture>();
			for (Fatture fatt:fattureListToFilter) {
				if (fatt.getIdSocieta().equals(filteringIdSocieta) &&
						!FattureBusiness.isFittizia(fatt)) {
					fattureFilteredList.add(fatt);
				}
			}
			//We have a filtered list now
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Fatture PDF per "+societa.getNome()+": "+fattureFilteredList.size()+" (delle "+fattureListToFilter.size()+" di oggi)");
			if (fattureFilteredList.size() > 0) {
				//File accompagnamento
				VisualLogger.get().addHtmlInfoLine(idRapporto, "Creazione del <b>file di accompagnamento PDF "
						+societa.getNome()+"</b>");
				File corFile = FattureTxtBusiness.createAccompagnamentoPdfFile(ses, fattureFilteredList, societa);
				String corRemoteNameAndDir = ftpConfig.getDir()+"/"+societa.getCodiceSocieta()+
						"_datixarchi_"+ServerConstants.FORMAT_FILE_NAME_TIMESTAMP.format(DateUtil.now())+suffix+"."+societa.getPrefissoFatture();
				VisualLogger.get().addHtmlInfoLine(idRapporto, "ftp://"+ftpConfig.getUsername()+"@"+ftpConfig.getHost()+"/"+corRemoteNameAndDir);
				FtpBusiness.upload(ftpConfig.getHost(), ftpConfig.getPort(), ftpConfig.getUsername(), ftpConfig.getPassword(),
						corRemoteNameAndDir, corFile);
				VisualLogger.get().addHtmlInfoLine(idRapporto, "Caricamento FTP del <b>file di accompagnamento per "+
						societa.getNome()+"</b> terminato");
				//File carta docente
				VisualLogger.get().addHtmlInfoLine(idRapporto, "Creazione del <b>file carta docente per "
						+societa.getNome()+"</b>");
				try {
					File cdoFile = FattureTxtBusiness.createCartaDocenteFile(ses, fattureFilteredList, societa);
					String cdoRemoteNameAndDir = ftpConfig.getDir()+"/"+societa.getCodiceSocieta()+
							"_cartadocente_"+ServerConstants.FORMAT_FILE_NAME_TIMESTAMP.format(DateUtil.now())+suffix+".csv";
					VisualLogger.get().addHtmlInfoLine(idRapporto, "ftp://"+ftpConfig.getUsername()+"@"+ftpConfig.getHost()+"/"+cdoRemoteNameAndDir);
					FtpBusiness.upload(ftpConfig.getHost(), ftpConfig.getPort(), ftpConfig.getUsername(), ftpConfig.getPassword(),
							cdoRemoteNameAndDir, cdoFile);
					VisualLogger.get().addHtmlInfoLine(idRapporto, "Caricamento FTP del <b>file carta docente per "+
							societa.getNome()+"</b> terminato");
				} catch (EmptyResultException e) {
					VisualLogger.get().addHtmlInfoLine(idRapporto, "Nessuna fattura relativa a <b>carta docente per "+
							societa.getNome()+"</b>, file non generato");
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
	
//	@SuppressWarnings("unchecked")
//	public static List<IstanzeAbbonamenti> findIstanzeSenzaFattura(Session ses, String idSocieta, 
//			Date fromDate, Date toDate, Integer dailyLimit, int idRapporto)
//			throws HibernateException, EmptyResultException {
//		List<IstanzeAbbonamenti> finalList = new ArrayList<IstanzeAbbonamenti>();
//		List<Pagamenti> pList = null;
//		if (dailyLimit == null) dailyLimit = Integer.MAX_VALUE;
//		int size = (dailyLimit < ServerConstants.FATTURE_PAGE_SIZE) ? dailyLimit : ServerConstants.FATTURE_PAGE_SIZE;
//		int offset = 0;
//		do {
//			String iaHql = "from Pagamenti pag where " +
//					"pag.istanzaAbbonamento.pagato = :b1 and " + //Abbonamento pagato
//					"pag.istanzaAbbonamento.abbonamento.periodico.idSocieta = :s1 and " + //Societa
//					"pag.istanzaAbbonamento.listino.fatturaInibita = :b2 and "+//false
//					"pag.dataAccredito >= :dt1 and " +
//					"pag.dataAccredito <= :dt2 and " +
//					"pag.istanzaAbbonamento.idFattura is null and "+
//					"pag.istanzaAbbonamento.dataSaldo >= :dt3 and "+
//					"pag.istanzaAbbonamento.dataSaldo <= :dt4 and "+
//					"pag.dataAccredito = (select max(p2.dataAccredito) "+
//						"from Pagamenti p2 where p2.istanzaAbbonamento = pag.istanzaAbbonamento) "+
//					"order by pag.dataAccredito asc ";
//			Query iaQ = ses.createQuery(iaHql);
//			iaQ.setParameter("b1", Boolean.TRUE); //pagato
//			iaQ.setParameter("dt1", fromDate, DateType.INSTANCE);
//			iaQ.setParameter("dt2", toDate, DateType.INSTANCE);
//			iaQ.setParameter("s1", idSocieta, StringType.INSTANCE);
//			iaQ.setParameter("dt3", fromDate, DateType.INSTANCE);
//			iaQ.setParameter("dt4", toDate, DateType.INSTANCE);
//			iaQ.setParameter("b2", Boolean.FALSE); //pagato
//			iaQ.setFirstResult(offset);
//			iaQ.setMaxResults(size);
//			pList = iaQ.list();
//			if (pList.size() > 0) {
//				for (Pagamenti p:pList) finalList.add(p.getIstanzaAbbonamento());
//				offset += pList.size();
//				VisualLogger.get().addHtmlInfoLine(idRapporto, "Trovate "+finalList.size()+" istanze "+idSocieta);
//			}
//			ses.flush();
//		} while ((pList.size() > 0) && (finalList.size() < dailyLimit));
//		VisualLogger.get().addHtmlInfoLine(idRapporto, "Query terminata: "+finalList.size()+" istanze "+idSocieta);
//		
//		if (finalList.size() == 0) throw new EmptyResultException("Nessuna istanza da fatturare");
//		return finalList;
//	}

}
