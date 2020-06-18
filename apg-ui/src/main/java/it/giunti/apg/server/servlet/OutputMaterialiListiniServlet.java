package it.giunti.apg.server.servlet;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.VisualLogger;
import it.giunti.apg.core.business.AvvisiBusiness;
import it.giunti.apg.core.business.FileFormatArticoli;
import it.giunti.apg.core.business.FtpUtil;
import it.giunti.apg.core.business.OutputMaterialiBusiness;
import it.giunti.apg.core.business.OutputInvioBusiness;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.FileException;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.MaterialiListini;
import it.giunti.apg.shared.model.MaterialiSpedizione;

public class OutputMaterialiListiniServlet extends HttpServlet {
	private static final long serialVersionUID = 731183754176574773L;
	
	//private static final Logger LOG = LoggerFactory.getLogger(OutputMaterialiListiniServlet.class);
	private static final int PAGE_SIZE = 500;
	private static final String TRUE = "true";
	
	public OutputMaterialiListiniServlet() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Integer idRapporto = ValueUtil.stoi(req.getParameter(AppConstants.PARAM_ID_RAPPORTO));
		String idUtente = req.getParameter(AppConstants.PARAM_ID_UTENTE); 
		Integer idMaterialeListino = ValueUtil.stoi(req.getParameter(AppConstants.PARAM_ID));
		String scriviDbParam = req.getParameter(AppConstants.PARAM_SCRIVI_DB);
		VisualLogger.get().addHtmlInfoLine(idRapporto, "Estrazione destinatari in corso");
		boolean scriviDb = false;
		if (scriviDbParam != null) {
			if (scriviDbParam.equals(TRUE)) {
				scriviDb=true;
			}
		}
		if ((idUtente != null) && (idMaterialeListino != null) && (idRapporto != null)) {
			if (idUtente.length()>0) {
				prepareResponse(resp, idMaterialeListino,
						idRapporto, idUtente, scriviDb);
			}
		}
		try {
			VisualLogger.get().closeAndSaveRapporto(idRapporto);
		} catch (BusinessException e) {
			throw new ServletException(e);
		}
	}
	
	private void prepareResponse(HttpServletResponse resp, Integer idMaterialeListino,
			int idRapporto, String idUtente, boolean writeToDb) {
		Date now = DateUtil.now();
		try {
			MaterialiListini artLsn = OutputInvioBusiness.findEntityById(MaterialiListini.class, idMaterialeListino, idRapporto);
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Estrazione articoli in coda (pu&ograve; durare a lungo)");
			// Extract enqueued EvasioniArticoli
			List<MaterialiSpedizione> msList = new ArrayList<MaterialiSpedizione>();
			int offset = 0;
			int size = 0;
			do {
				List<MaterialiSpedizione> list = OutputMaterialiBusiness
					.findPendingMaterialiSpedizioneListini(idMaterialeListino, now, offset, PAGE_SIZE, idRapporto);
				msList.addAll(list);
				size = list.size();
				offset += size;
				VisualLogger.get().addHtmlInfoLine(idRapporto, "Estratti "+offset+" abbonamenti...");
			} while (size == PAGE_SIZE);
			//Filtraggio pagati dopo il limite
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Filtraggio delle date di scadenza in corso");
			int size1 = msList.size();
			msList = OutputMaterialiBusiness.filterMaterialiListiniByScadenza(msList);
			int diff = size1-msList.size();
			if (diff > 0) VisualLogger.get().addHtmlInfoLine(idRapporto, "Filtrati "+diff+" destinatari per scadenza");
			//Titolo log
			String avviso = "Evasione articolo "+artLsn.getMateriale().getCodiceMeccanografico()+" abbinato al tipo "+
					artLsn.getListino().getTipoAbbonamento().getCodice()+" "+
					artLsn.getListino().getTipoAbbonamento().getNome()+ " di "+
					artLsn.getListino().getTipoAbbonamento().getPeriodico().getNome();
			VisualLogger.get().setLogTitle(idRapporto, avviso);
			// Prepare the filename
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Creazione file");
			File f = File.createTempFile("exportMaterialiListini"+idMaterialeListino, ".csv");
			f.deleteOnExit();
			String timestamp = ServerConstants.FORMAT_FILE_NAME_TIMESTAMP.format(now);
			String fileName = timestamp + " Articolo " +
					artLsn.getMateriale().getCodiceMeccanografico()+" "+
					artLsn.getListino().getTipoAbbonamento().getCodice()+" "+
					artLsn.getListino().getTipoAbbonamento().getPeriodico().getUid()+
					".csv";
			//Formatta
			FileFormatArticoli.formatArticoliDaSpedire(f, msList, idRapporto);
			
			//send file via Ftp and write on db
			if (writeToDb) {
				VisualLogger.get().addHtmlInfoLine(idRapporto, "FTP del file");
				String idSocieta = artLsn.getListino().getTipoAbbonamento().getPeriodico().getIdSocieta();
				String ftpHost = new FtpUtil(idSocieta).fileTransfer(f, null, fileName);
				VisualLogger.get().addHtmlInfoLine(idRapporto, "File trasferito su "+ftpHost);
				OutputMaterialiBusiness.writeMaterialiSpedizioneOnDb(msList, now, idRapporto);
				OutputMaterialiBusiness.updateDataEstrazioneMaterialeListino(idMaterialeListino, idRapporto, idUtente);
				AvvisiBusiness.writeAvviso(avviso, false, idUtente);
			} else {
				//Durante i test il file è inviato in HTTP
				try {
					int BUFSIZE = 2048;
					//Try to send via http
					int length = 0;
					ServletOutputStream op = resp.getOutputStream();
					resp.setContentType("application/octet-stream");
					String header = "attachment; filename=\""+fileName+"\"";
					resp.setHeader("Content-Disposition", header);
					// Stream to the requester
					byte[] bbuf = new byte[BUFSIZE];
					DataInputStream in = new DataInputStream(new FileInputStream(f));
					VisualLogger.get().addHtmlInfoLine(idRapporto, "Invio file via http");
					while ((in != null) && ((length = in.read(bbuf)) != -1)) {
					    op.write(bbuf,0,length);
					}
					in.close();
					op.flush();
					op.close();
					resp.setContentLength( (int)f.length() );
					VisualLogger.get().addHtmlInfoLine(idRapporto, "File inviato via http");
				} catch (Exception e) { 
					VisualLogger.get().addHtmlErrorLine(idRapporto, "Http file transfer error: "+e.getMessage(), e);
				}
			}
		//} catch (EmptyResultException e) { 
		//	VisualLogger.get().addHtmlInfoLine(idRapporto, "Nessun dato da estrarre - "+e.getMessage());
		} catch (IOException e) {
			VisualLogger.get().addHtmlErrorLine(idRapporto, "IO ERROR: "+e.getMessage(), e);
		} catch (BusinessException e) {
			VisualLogger.get().addHtmlErrorLine(idRapporto, "DB ERROR: "+e.getMessage(), e);
		} catch (FileException e) {
			VisualLogger.get().addHtmlErrorLine(idRapporto, "FILESYSTEM ERROR: "+e.getMessage(), e);
		}

	}
}