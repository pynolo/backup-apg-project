package it.giunti.apg.automation.servlet;

import it.giunti.apg.automation.report.BollettiniIaDataSource;
import it.giunti.apg.automation.report.Bollettino;
import it.giunti.apg.core.business.PagamentiMatchBusiness;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.PagamentiCreditiDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

public class CreateBollettinoServlet extends HttpServlet {
	private static final long serialVersionUID = 510346861934804813L;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}
	
	//Process the HTTP Get request
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		//param: numeroFattura
		String idSubscriptionS = request.getParameter(AppConstants.PARAM_ID);
		if (idSubscriptionS == null) throw new ServletException("No subscription id supplied");
		if (idSubscriptionS.equals("")) throw new ServletException("No subscription id supplied");
		Integer idSubscription;
		try {
			idSubscription = Integer.parseInt(idSubscriptionS);
		} catch (NumberFormatException e1) {
			throw new ServletException("Invalid subscription id");
		}
		
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			IstanzeAbbonamenti ia = GenericDao.findById(ses, IstanzeAbbonamenti.class, idSubscription);
			if (ia == null) 
				throw new ServletException("Subscription not found");
			if (ia.getFatturaDifferita() || ia.getListino().getFatturaDifferita() || ia.getPagato())
				throw new ServletException("Subscription is paid");
			//draw the bill
			Double dovuto = PagamentiMatchBusiness.getMissingAmount(ses, ia.getId());
			Double pagato = new PagamentiCreditiDao()
					.getCreditoByAnagraficaSocieta(ses, ia.getId(),
							ia.getAbbonamento().getPeriodico().getIdSocieta(), null, false);
			Double importo = dovuto-pagato;
			if (importo < AppConstants.SOGLIA)
					throw new ServletException("Subscription is paid");
			List<IstanzeAbbonamenti> iaList = new ArrayList<IstanzeAbbonamenti>();
			iaList.add(ia);
			BollettiniIaDataSource.initDataSource(iaList);
			List<Bollettino> beanList = BollettiniIaDataSource.createBeanCollection(ses);
			byte[] pdfStream = drawBollettino(beanList.get(0));
			//Build the .pdf file
			String fileName = ia.getAbbonamento().getCodiceAbbonamento()+".pdf";
			//File sfTmpFile = File.createTempFile("bollettino_", "_"+fileName);
			//sfTmpFile.deleteOnExit();
			//FileOutputStream fos = new FileOutputStream(sfTmpFile);
			//fos.write(pdfStream);
			//fos.close();
			//Return .pdf to HTTP outputstream
			PrintWriter out = new PrintWriter(response.getOutputStream());
			ServletOutputStream binout = response.getOutputStream();
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment;filename="+fileName);
			byte[] sfBytes = pdfStream;
			binout.write(sfBytes);
			out.close();
			trn.commit();
		} catch (HibernateException | JRException | BusinessException e) {
			trn.rollback();
			throw new ServletException(e.getMessage());
		} catch (IOException e) {
			trn.rollback();
			throw new ServletException(e.getMessage());
		} finally {
			ses.close();
		}
	}
	
	private byte[] drawBollettino(Bollettino bollettino)
			throws BusinessException, JRException, IOException {
		Locale locale = new Locale("it", "IT");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(JRParameter.REPORT_LOCALE, locale);
		List<Bollettino> singleItemCollection = new ArrayList<Bollettino>();
		singleItemCollection.add(bollettino);
		//Attenzione, viene creato un singolo file per tutta la collection
		JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(singleItemCollection);
		//Creo l'input stream della matrice del report
		InputStream reportIs = getClass().getResourceAsStream(
				bollettino.getReportFilePath());
		if (reportIs == null) throw new IOException("Could not find report file "+bollettino.getReportFilePath());
		//Creazione report fondendo dati e struttura
		JasperPrint print = JasperFillManager.fillReport(reportIs, paramMap, jrds);
		//Pulizia pagine bianche
		List<JRPrintPage> pages = print.getPages();
		for (Iterator<JRPrintPage> i=pages.iterator(); i.hasNext();) {
			JRPrintPage page = (JRPrintPage)i.next();
			if (page.getElements().size() == 0) i.remove();
        }
		//Esporta in byte array
		byte[] pdfStream = JasperExportManager.exportReportToPdf(print);
		return pdfStream;
	}
	
}
