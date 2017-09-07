package it.giunti.apg.automation.business;

import it.giunti.apg.automation.AutomationConstants;
import it.giunti.apg.automation.report.FatturaBean;
import it.giunti.apg.automation.report.FattureDataSource;
import it.giunti.apg.core.DateUtil;
import it.giunti.apg.shared.BusinessException;
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

import org.hibernate.Session;

public class FatturePdfBusiness {

	public static File createTempPdfFile(Session ses, FattureStampe fatturaStampa) 
			throws BusinessException, JRException, IOException {
		//Build the .pdf file
		File sfTmpFile = File.createTempFile("invoice", ".pdf");
		sfTmpFile.deleteOnExit();
		byte b[]=fatturaStampa.getContent();
		FileOutputStream fos = new FileOutputStream(sfTmpFile);
		fos.write(b);
	    fos.close();
	    return sfTmpFile;
	}
	
	public static FattureStampe createTransientStampaFattura(Session ses, Fatture fattura) 
			throws BusinessException, JRException, IOException {
		//Data source per jasperReports & create PDF prints
		List<Fatture> fattureList = new ArrayList<Fatture>();
		fattureList.add(fattura);
		FattureDataSource.initDataSource(null, fattureList);
		List<FatturaBean> beanList = FattureDataSource.createBeanCollection(ses);
		FatturaBean bean = beanList.get(0);
		byte [] pdfStream = new FatturePdfBusiness().createTransientPdf(bean);
		FattureStampe stampa = new FattureStampe();
		stampa.setFileName(bean.getFileName());
		stampa.setMimeType("application/pdf");
		stampa.setContent(pdfStream);
		stampa.setDataCreazione(DateUtil.now());
		return stampa;
	}
	
	private byte[] createTransientPdf(FatturaBean bean) 
			throws JRException, IOException {
		Locale locale = new Locale("it", "IT");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(JRParameter.REPORT_LOCALE, locale);
		paramMap.put("SUBREPORT_DIR", AutomationConstants.REPORT_RESOURCES_PATH+"/");
		//Produce i singoli report per Istanza

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
		//Esporta in byte array
		byte [] pdfStream = JasperExportManager.exportReportToPdf(print);
		return pdfStream;
	}
}
