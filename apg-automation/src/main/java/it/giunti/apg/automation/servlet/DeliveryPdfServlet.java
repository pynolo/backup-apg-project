package it.giunti.apg.automation.servlet;

import it.giunti.apg.automation.AutomationConstants;
import it.giunti.apg.automation.report.Talloncino;
import it.giunti.apg.core.DateUtil;
import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.business.CsvReader;
import it.giunti.apg.core.persistence.PeriodiciDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Periodici;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
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

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeliveryPdfServlet extends HttpServlet {
	private static final long serialVersionUID = -4270720982800937099L;

	private static final Logger LOG = LoggerFactory.getLogger(DeliveryPdfServlet.class);
	
	// FEFF because this is the Unicode char represented by the UTF-8 byte order mark (EF BB BF).
    public static final String UTF8_BOM = "\uFEFF";
    public static final char SEP = ';';
    
	private static final String REPORT_FILE_NAME = "/tallonciniReport.jasper";
	private static final String REPORT_OUTPUT_FILE_NAME = "talloncini";
	private static final String STAMP_IMAGE_PATH = "/img/stamp_periodico.jpg";
	//private static final int ROW_LENGTH = 425;
	
	public DeliveryPdfServlet() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	//@SuppressWarnings("unchecked")
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		List<File> fileList = new ArrayList<File>();
		// process only multipart requests
		if (ServletFileUpload.isMultipartContent(req)) {
			// Create a factory for disk-based file items
			FileItemFactory factory = new DiskFileItemFactory();
			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);
			// Parse the request
			try {
				List<FileItem> items = (List<FileItem>) upload.parseRequest(req);
				for (FileItem item : items) {
					// process only file upload - discard other form item types
					if (item.isFormField()) {
						////is a form field, not a file
						//String paramName = item.getFieldName();
						//String paramValue = item.getString();
						//if (paramName != null) {
						//	if (paramName.equals(AppConstants.PARAM_ID_UTENTE)) {
						//		idUtente = paramValue;
						//	}
						//	if (paramName.equals(AppConstants.PARAM_ID_RAPPORTO)) {
						//		idRapporto = ValueUtil.stoi(paramValue);
						//	}
						//}
					} else {
						//Is a file
						String fileName = item.getName();
						// get only the file name not whole path
						if (fileName != null) {
							fileName = FilenameUtils.getName(fileName);
						}
						File uploadedFile = new File(ServerConstants.UPLOAD_DIRECTORY, fileName);
						uploadedFile.deleteOnExit();
						if (uploadedFile.exists()) {
							uploadedFile.delete();
						}
						uploadedFile.createNewFile();
						item.write(uploadedFile);
						//resp.setStatus(HttpServletResponse.SC_CREATED);
						//resp.getWriter().print("The file was created successfully.");
						//resp.flushBuffer();
						
						//Processa il contenuto del file
						fileList.add(uploadedFile);
					}
				}
			} catch (Exception e) {
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"An error occurred while creating the file : " + e.getMessage());
			}
		} else {
			resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
					"Request contents type is not opzorted by the servlet.");
		}
		
		prepareResponse(resp, fileList);

	}
	
	private void prepareResponse(HttpServletResponse resp, List<File> fileList) throws ServletException {
		//Business logic after file upload
		for (File f:fileList) {
			try {
				createReportFromFile(resp, f);
			} catch (BusinessException e) {
				LOG.error(e.getMessage(), e);
				throw new ServletException(e);
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
				throw new ServletException(e);
			}
		}
	}
	
	private List<Talloncino> convertFileToList(Session ses, File f) throws IOException, BusinessException {
		List<Talloncino> tList = new ArrayList<Talloncino>();
		try {
			InputStreamReader reader = new InputStreamReader(new FileInputStream(f));
			CsvReader delivery = new CsvReader(reader, SEP);
			//delivery.readHeaders();
			while (delivery.readRecord()) {
				Talloncino t = new Talloncino();
				Integer copie = 0;
				try {
					copie = Integer.parseInt(delivery.get(11));
				} catch (NumberFormatException e) { }
				if (copie == 0) copie = 1;
				String codice = delivery.get(1);
				String codiceAbbonato = "Codice Abbonato: "+codice;
				String titoloPersonale = delivery.get(3);
				if (copie == 1) {
					//Copia singola
					t.setAvvisoFileName("");
					t.setTitolo1(codiceAbbonato);
					t.setTitolo2(titoloPersonale);
					//ATTENZIONE Sotto i valori titolo1 titolo2 vengono cambiati in base alla nazione!!!
				} else {
					//Più copie
					t.setAvvisoFileName(AutomationConstants.REPORT_RESOURCES_PATH+AutomationConstants.IMG_DESTINATARIO_UNICO);
					t.setTitolo1("");
					t.setTitolo2(codiceAbbonato);
				}
				t.setCopie(copie);
				t.setDescrFascicolo(delivery.get(13)+" "+delivery.get(19));//descrPeriodico+cm
				String cap = delivery.get(2);
				t.setCap(cap);
				String nazione = delivery.get(17);
				t.setNazione(nazione);
				String indirizzoFormattato = delivery.get(4);// cognomeNome;
				String presso = delivery.get(5);
				if (!presso.equals("")) {
					indirizzoFormattato +="\r\n"+presso;
				}
				String indirizzo = delivery.get(6);
				String localita = delivery.get(7);
				String provincia = delivery.get(8);
				indirizzoFormattato += "\r\n"+indirizzo;
				if (!cap.equals("00000") && !cap.equals("0000")) {
					localita = cap + " " + localita;
				}
				if (!provincia.equals("EE")) {
					localita += " " + provincia;
				}
				indirizzoFormattato += "\r\n"+localita;
				t.setStampFileName(AutomationConstants.REPORT_RESOURCES_PATH+AutomationConstants.IMG_STAMP_PERIODICO);
				//Nazione
				if (!nazione.equals("")) {
					indirizzoFormattato += "\r\n                             "+nazione;
					t.setStampFileName(AutomationConstants.REPORT_RESOURCES_PATH+AutomationConstants.IMG_STAMP_ECONOMY);
					t.setTitolo1("");
					t.setTitolo2(codiceAbbonato);
				}
				t.setIndirizzoFormattato(indirizzoFormattato);
				
				String logoFileName = AutomationConstants.REPORT_RESOURCES_PATH+logoFromLettera(ses, codice.substring(0,1));
				t.setLogoFileName(logoFileName);	
				
				tList.add(t);
			}
			delivery.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return tList;
	}
	
	
	private String logoFromLettera(Session ses, String letteraPeriodico) throws BusinessException {
		try {
			Periodici periodico = new PeriodiciDao().findByUid(ses, letteraPeriodico);
			if (periodico == null) throw new BusinessException("Nessun periodico corrisponde alla lettera '"+letteraPeriodico+"'");
			if (periodico.getIdTipoPeriodico().equals(AppConstants.PERIODICO_SCOLASTICO)) {
				return AutomationConstants.IMG_PERIODICO_LOGO_SCOLASTICO;
			} else {
				return AutomationConstants.IMG_PERIODICO_LOGO_VARIA;
			}
		} catch (HibernateException e) {
			throw new BusinessException(e.getMessage(), e);
		}
	}
	
    public static String removeUTF8BOM(String s) {
        if (s.startsWith(UTF8_BOM)) {
            s = s.substring(1);
        }
        return s;
    }
    
	private void createReportFromFile(HttpServletResponse resp, File f) throws BusinessException, IOException {
		Session ses = SessionFactory.getSession();
		Date date = DateUtil.now();
		try {
			List<Talloncino> tList = convertFileToList(ses, f);
			String nomeFascicolo = "nessun fascicolo";
			if (tList != null) {
				if (tList.size() > 0) {
					nomeFascicolo = tList.get(0).getDescrFascicolo().toLowerCase();
				}
			}
			//Ottiene gli altri parametri dall'entity Orders
			ReportWork work = new ReportWork(resp, tList, nomeFascicolo, date);
			ses.doWork(work);//L'oggetto Work serve ad usare la Connection JDBC restando nella sessione Hibernate
		} catch (BusinessException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			ses.close();
		}
	}
	
	//private void exportReportFtp(File reportFile, String nomePeriodico, Date date) throws IOException {
	//	String remoteFileName = ServerConstants.FORMAT_FILE_NAME_TIMESTAMP.format(date)+
	//			" Report doni "+nomePeriodico+".pdf";
	//	new FtpUtil().completeFileTransfer(reportFile, remoteFileName);
	//}
	
	private void exportReportHttp(HttpServletResponse resp, File reportFile, String nomePeriodico,
			Date date) {
		String remoteFileName = ServerConstants.FORMAT_FILE_NAME_TIMESTAMP.format(date)+
				" Report "+REPORT_OUTPUT_FILE_NAME+" "+nomePeriodico+".pdf";
		try {
			//Try to send via http
			int length = 0;
			ServletOutputStream op = resp.getOutputStream();
			resp.setContentType("application/octet-stream");
			resp.setHeader( "Content-Disposition", "attachment; filename=\"" + remoteFileName + "\"" );
			// Stream to the requester
			byte[] bbuf = new byte[2048];
			DataInputStream in = new DataInputStream(new FileInputStream(reportFile));
			while ((in != null) && ((length = in.read(bbuf)) != -1)) {
			    op.write(bbuf,0,length);
			}
			in.close();
			op.flush();
			op.close();
			resp.setContentLength( (int)reportFile.length() );
		} catch (Exception e) { 
			LOG.error(e.getMessage(), e);
		}
	}
	
	
	//Inner classes
	
	
	
	public class ReportWork implements Work {
		
		private Map<String, Object> paramMap = new HashMap<String, Object>();
		private HttpServletResponse resp = null;
		private String nomeFascicolo = null;
		private List<Talloncino> pList = null;
		private Date date = null;
		
		public ReportWork(HttpServletResponse resp, List<Talloncino> pList, String nomeFascicolo, Date date) throws HibernateException {
			this.resp = resp;
			this.nomeFascicolo = nomeFascicolo;
			this.pList = pList;
			this.date = date;
			Locale locale = new Locale("it", "IT");
			paramMap.put(JRParameter.REPORT_LOCALE, locale);
			paramMap.put("REPORT_STAMP_IMAGE", AutomationConstants.REPORT_RESOURCES_PATH+STAMP_IMAGE_PATH);
		}

		@Override
		public void execute(Connection con) throws SQLException {
			File f = null;
			try {
				f = File.createTempFile(REPORT_OUTPUT_FILE_NAME+" "+nomeFascicolo+" ", ".pdf");
				f.deleteOnExit();
				String reportFileName = AutomationConstants.REPORT_RESOURCES_PATH+REPORT_FILE_NAME;
				InputStream reportIs = getClass().getResourceAsStream(reportFileName);
				if (reportIs == null) throw new SQLException("Could not find file "+reportFileName);
				JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(pList);
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
				exportReportHttp(resp, f, nomeFascicolo, date);
			} catch (IOException e) {
				throw new SQLException(e);
			} catch (JRException e) {
				throw new SQLException(e);
			}
		}

	}
	
}
