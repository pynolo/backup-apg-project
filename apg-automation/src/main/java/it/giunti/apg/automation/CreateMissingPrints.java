package it.giunti.apg.automation;

import it.giunti.apg.automation.report.FatturaBean;
import it.giunti.apg.automation.report.FattureDataSource;
import it.giunti.apg.core.ConfigUtil;
import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.VisualLogger;
import it.giunti.apg.core.business.FtpBusiness;
import it.giunti.apg.core.business.FtpConfig;
import it.giunti.apg.core.persistence.FattureDao;
import it.giunti.apg.core.persistence.FattureStampeDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.Fatture;
import it.giunti.apg.shared.model.FattureStampe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import org.hibernate.type.StringType;

public class CreateMissingPrints {

	public static void main(String[] args) {
		CreateMissingPrints instance = new CreateMissingPrints();
		try {
			instance.createPdf("GE", "FXE7006984", "FXE7006986");
			instance.createPdf("GS", "FXS7043941", "FXS7044000");
		} catch (BusinessException e) {
			e.printStackTrace();
		}
	}
	
	private void createPdf(String idSocieta, 
			String numeroFatturaBegin, String numeroFatturaEnd) 
					throws BusinessException {
		Integer idRapporto = null;
		
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			FtpConfig ftpConfigDebug = ConfigUtil.loadFtpFattureRegistri(ses, true);
			List<Fatture> printableList = findNotYetPrinted(ses, 
					idSocieta, numeroFatturaBegin, numeroFatturaEnd);
			
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
				uploadStampe(stampeList, ftpConfigDebug);
			} else {
				VisualLogger.get().addHtmlInfoLine(idRapporto, "Nessun file PDF creato.");
			}
			trn.commit();
	  	} catch (HibernateException e) {
			trn.rollback();
			VisualLogger.get().addHtmlErrorLine(idRapporto, "ERROR: "+e.getMessage());
			throw new BusinessException(e.getMessage(), e);
	  	} catch (IOException e) {
			trn.rollback();
			VisualLogger.get().addHtmlErrorLine(idRapporto, "ERROR: "+e.getMessage());
			throw new BusinessException(e.getMessage(), e);
	  	} catch (JRException e) {
			trn.rollback();
			VisualLogger.get().addHtmlErrorLine(idRapporto, "ERROR: "+e.getMessage());
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
			VisualLogger.get().closeAndSaveRapporto(idRapporto);
		}
	}
	
	
	
	
	
	@SuppressWarnings("unchecked")
	public List<Fatture> findNotYetPrinted(Session ses,
			String idSocieta, String numeroFatturaBegin, String numeroFatturaEnd) throws HibernateException {
		String qs = "from Fatture f where " +
				"f.idSocieta = :id1 and " + //societa
				"f.numeroFattura >= :s1 and " +
				"f.numeroFattura <= :s2 ";
		qs += "order by f.id ";
		Query q = ses.createQuery(qs);
		q.setParameter("id1", idSocieta, StringType.INSTANCE);
		q.setParameter("s1", numeroFatturaBegin, StringType.INSTANCE);
		q.setParameter("s2", numeroFatturaEnd, StringType.INSTANCE);
		List<Fatture> result = q.list();
		return result;
	}
	
	private void uploadStampe(List<FattureStampe> stampeList, FtpConfig ftpConfigDebug)
			throws BusinessException {
		try {
			// FTP
			for (FattureStampe stampa:stampeList) {
				//Creazione file
				File sfTmpFile = File.createTempFile("stampeFatture", ".pdf");
				sfTmpFile.deleteOnExit();
				byte b[]=stampa.getContent();
				FileOutputStream fos = new FileOutputStream(sfTmpFile);
				fos.write(b);
			    fos.close();
				//Upload FTP
				String remoteNameAndDir = ftpConfigDebug.getDir()+"/"+
						stampa.getFileName()+".pdf";
				FtpBusiness.upload(ftpConfigDebug.getHost(), ftpConfigDebug.getPort(),
						ftpConfigDebug.getUsername(), ftpConfigDebug.getPassword(),
						remoteNameAndDir, sfTmpFile);
				sfTmpFile.delete();
			}
		} catch (IOException e) {
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
}
