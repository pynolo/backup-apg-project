package it.giunti.apg.server.servlet;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.VisualLogger;
import it.giunti.apg.core.business.AvvisiBusiness;
import it.giunti.apg.core.business.FileFormatArticoli;
import it.giunti.apg.core.business.FtpUtil;
import it.giunti.apg.core.business.OutputArticoliBusiness;
import it.giunti.apg.core.business.OutputInvioBusiness;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.FileException;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.ArticoliOpzioni;
import it.giunti.apg.shared.model.EvasioniArticoli;

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

public class OutputArticoliOpzioniServlet extends HttpServlet {
	private static final long serialVersionUID = 214268513523766204L;
	
	//private static final Logger LOG = LoggerFactory.getLogger(OutputArticoliOpzioniServlet.class);
	private static final int PAGE_SIZE = 500;
	private static final String TRUE = "true";
	
	public OutputArticoliOpzioniServlet() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Integer idRapporto = ValueUtil.stoi(req.getParameter(AppConstants.PARAM_ID_RAPPORTO));
		String idUtente = req.getParameter(AppConstants.PARAM_ID_UTENTE); 
		Integer idArticoloOpzione = ValueUtil.stoi(req.getParameter(AppConstants.PARAM_ID));
		String scriviDbParam = req.getParameter(AppConstants.PARAM_SCRIVI_DB);
		VisualLogger.get().addHtmlInfoLine(idRapporto, "Estrazione destinatari in corso");
		boolean scriviDb = false;
		if (scriviDbParam != null) {
			if (scriviDbParam.equals(TRUE)) {
				scriviDb=true;
			}
		}
		if ((idUtente != null) &&  (idArticoloOpzione != null) && (idRapporto != null)) {
			if (idUtente.length()>0) {
				prepareResponse(resp, idArticoloOpzione,
						idRapporto, idUtente, scriviDb);
			}
		}
		try {
			VisualLogger.get().closeAndSaveRapporto(idRapporto);
		} catch (BusinessException e) {
			throw new ServletException(e);
		}
	}
	
	private void prepareResponse(HttpServletResponse resp, Integer idArticoloOpzione,
			int idRapporto, String idUtente, boolean writeToDb) {
		Date now = new Date();
		try {
			ArticoliOpzioni artOpz = OutputInvioBusiness.findEntityById(ArticoliOpzioni.class, idArticoloOpzione, idRapporto);
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Estrazione articoli in coda (pu&ograve; durare a lungo)");
			// Extract enqueued EvasioniArticoli
			List<EvasioniArticoli> eaList = new ArrayList<EvasioniArticoli>();
			int offset = 0;
			int size = 0;
			do {
				List<EvasioniArticoli> list = OutputArticoliBusiness
					.findPendingEvasioniArticoliOpzioni(idArticoloOpzione, offset, PAGE_SIZE, idRapporto);
				eaList.addAll(list);
				size = list.size();
				offset += size;
				VisualLogger.get().addHtmlInfoLine(idRapporto, "Estratti "+offset+" abbonamenti...");
			} while (size == PAGE_SIZE);
			//Titolo log
			String avviso = "Evasione articolo "+artOpz.getArticolo().getCodiceMeccanografico()+" abbinato all'opzione "+
					"["+artOpz.getOpzione().getUid()+"] "+artOpz.getOpzione().getNome()+" di "+
					artOpz.getOpzione().getPeriodico().getNome();
			VisualLogger.get().setLogTitle(idRapporto, avviso);
			// Prepare the filename
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Creazione file");
			File f = File.createTempFile("exportArticoliOpzioni"+idArticoloOpzione, ".csv");
			f.deleteOnExit();
			String timestamp = ServerConstants.FORMAT_FILE_NAME_TIMESTAMP.format(now);
			String fileName = timestamp + " Articolo " +
					artOpz.getArticolo().getCodiceMeccanografico()+
					" ["+artOpz.getOpzione().getUid()+"].csv";
			//Formatta
			FileFormatArticoli.formatArticoliDaSpedire(f, eaList, idRapporto);
			
			//send file via Ftp and write on db
			if (writeToDb) {
				VisualLogger.get().addHtmlInfoLine(idRapporto, "FTP del file");
				String idSocieta = artOpz.getOpzione().getPeriodico().getIdSocieta();
				String ftpHost = new FtpUtil(idSocieta).fileTransfer(f, null, fileName);
				VisualLogger.get().addHtmlInfoLine(idRapporto, "File trasferito su "+ftpHost);
				OutputArticoliBusiness.writeEvasioniArticoliOnDb(eaList, now, idRapporto);
				OutputArticoliBusiness.updateDataEstrazioneArticoloOpzione(idArticoloOpzione, idRapporto, idUtente);
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