package it.giunti.apg.server.servlet;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.VisualLogger;
import it.giunti.apg.core.business.AvvisiBusiness;
import it.giunti.apg.core.business.FileFormatInvio;
import it.giunti.apg.core.business.FtpUtil;
import it.giunti.apg.core.business.OutputInvioBusiness;
import it.giunti.apg.core.business.SortBusiness;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.FileException;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.MaterialiProgrammazione;

public class OutputInvioServlet extends HttpServlet {
	private static final long serialVersionUID = 1291685282395393946L;
	
	//private static final Logger LOG = LoggerFactory.getLogger(OutputInvioServlet.class);
	private static final String TRUE = "true";
	
	public OutputInvioServlet() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Integer idRapporto = ValueUtil.stoi(req.getParameter(AppConstants.PARAM_ID_RAPPORTO));
		String idUtente = req.getParameter(AppConstants.PARAM_ID_UTENTE); 
		Integer idPeriodico = ValueUtil.stoi(req.getParameter(AppConstants.PARAM_ID_PERIODICO)); 
		Integer idMaterialeProgrammazione = ValueUtil.stoi(req.getParameter(AppConstants.PARAM_ID_MATERIALE_PROGRAMMAZIONE));
		String copie = req.getParameter(AppConstants.PARAM_INCLUDI_COPIE);
		String italia = req.getParameter(AppConstants.PARAM_INCLUDI_ITALIA);
		String scriviDbParam = req.getParameter(AppConstants.PARAM_SCRIVI_DB);
		//String scriviDataEstrazioneParam = req.getParameter(AppConstants.PARAM_SCRIVI_DATA_ESTRAZIONE);
		VisualLogger.get().addHtmlInfoLine(idRapporto, "Estrazione destinatari in corso");
		boolean scriviDb = false;
		if (scriviDbParam != null) {
			if (scriviDbParam.equals(TRUE)) {
				scriviDb=true;
			}
		}
		//boolean scriviDataEstrazione = false;
		//if (scriviDataEstrazioneParam != null) {
		//	if (scriviDataEstrazioneParam.equals(TRUE)) {
		//		scriviDataEstrazione=true;
		//	}
		//}
		if ((idUtente != null) && (idPeriodico != null) && (idMaterialeProgrammazione != null) && (idRapporto != null)) {
			if (idUtente.length()>0) {
				prepareResponse(resp, idPeriodico, idMaterialeProgrammazione,
						copie, italia, idRapporto, idUtente, scriviDb);
			}
		}
		try {
			VisualLogger.get().closeAndSaveRapporto(idRapporto);
		} catch (BusinessException e) {
			throw new ServletException(e);
		}
	}
	
	private void prepareResponse(HttpServletResponse resp, 
			Integer idPeriodico, Integer idMaterialeProgrammazione,
			String copie, String italia,
			int idRapporto, String idUtente, boolean writeToDb) {
		try {
			MaterialiProgrammazione mp = OutputInvioBusiness.findEntityById(
					MaterialiProgrammazione.class, idMaterialeProgrammazione, idRapporto);
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Estrazione aventi diritto");
			// Extract invii
			List<IstanzeAbbonamenti> iaList = OutputInvioBusiness
					.extractIstanzeRiceventiMateriale(idPeriodico, idMaterialeProgrammazione, copie, italia, idRapporto);
			//Titolo log
			String avviso = "Evasione fascicolo "+mp.getMateriale().getTitolo()+" '"+
					iaList.get(0).getListino().getTipoAbbonamento().getPeriodico().getNome()+"'";
			if (AppConstants.INCLUDI_INSIEME_INTERNO.equals(copie)) {
				avviso += " una copia";
			}
			if (AppConstants.INCLUDI_INSIEME_ESTERNO.equals(copie)) {
				avviso += " piu' copie";
			}
			if (AppConstants.INCLUDI_INSIEME_INTERNO.equals(italia)) {
				avviso += " Italia";
			}
			if (AppConstants.INCLUDI_INSIEME_ESTERNO.equals(italia)) {
				avviso += " estero";
			}
			if (mp.getOpzione() != null) {
				avviso += " con opz. ["+mp.getOpzione().getUid()+"] "+
						mp.getOpzione().getNome();
			}
			VisualLogger.get().setLogTitle(idRapporto, avviso);
			//Ordinamento
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Ordinamento per copie, cap e nazione");
			new SortBusiness().sortIstanzeAbbonamenti(iaList);
			// Prepare the filename
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Creazione file");
			File f = File.createTempFile("exportInvio"+idPeriodico, ".csv");
			f.deleteOnExit();
			String timestamp = ServerConstants.FORMAT_FILE_NAME_TIMESTAMP.format(DateUtil.now());
			String fileName = timestamp + " Invio " +
					mp.getMateriale().getTitolo()+" "+
					mp.getPeriodico().getNome()+" ";
			String dettaglioNome = "";
			if (AppConstants.INCLUDI_INSIEME_INTERNO.equals(copie)) {
				dettaglioNome += "Una copia";
			}
			if (AppConstants.INCLUDI_INSIEME_ESTERNO.equals(copie)) {
				if (dettaglioNome.length() > 0) dettaglioNome += " - ";
				dettaglioNome += "Piu copie";
			}
			if (AppConstants.INCLUDI_INSIEME_INTERNO.equals(italia)) {
				if (dettaglioNome.length() > 0) dettaglioNome += " - ";
				dettaglioNome += "Italia";
			}
			if (AppConstants.INCLUDI_INSIEME_ESTERNO.equals(italia)) {
				if (dettaglioNome.length() > 0) dettaglioNome += " - ";
				dettaglioNome += "Estero";
			}
			if (mp.getOpzione() != null) {
				if (dettaglioNome.length() > 0) dettaglioNome += " - ";
				dettaglioNome += "Con opz ["+mp.getOpzione().getUid()+"] "+
						mp.getOpzione().getNome();
			}
			if (dettaglioNome.length() > 0) fileName += "("+dettaglioNome+")";
			fileName +=".csv";
			//Formatta
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Formattazione dati");
			FileFormatInvio.formatInviiRegolari(f, iaList, mp.getMateriale(), idRapporto);
			
			//send file via Ftp and write on db
			if (writeToDb) {
				VisualLogger.get().addHtmlInfoLine(idRapporto, "FTP del file");
				String idSocieta = mp.getPeriodico().getIdSocieta();
				String ftpHost = new FtpUtil(idSocieta).fileTransfer(f, null, fileName);
				VisualLogger.get().addHtmlInfoLine(idRapporto, "File trasferito su "+ftpHost);
				OutputInvioBusiness.writeMaterialiSpedizioneOnDb(iaList, idMaterialeProgrammazione,
						copie, italia, idRapporto, idUtente);
				OutputInvioBusiness.writeDataSpedizione(idMaterialeProgrammazione, 
						mp.getPeriodico().getId(), idRapporto);
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
		} catch (EmptyResultException e) { 
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Nessun dato da estrarre - "+e.getMessage());
		} catch (IOException e) {
			VisualLogger.get().addHtmlErrorLine(idRapporto, "IO ERROR: "+e.getMessage(), e);
		} catch (BusinessException e) {
			VisualLogger.get().addHtmlErrorLine(idRapporto, "DB ERROR: "+e.getMessage(), e);
		} catch (FileException e) {
			VisualLogger.get().addHtmlErrorLine(idRapporto, "FILESYSTEM ERROR: "+e.getMessage(), e);
		}

	}
}