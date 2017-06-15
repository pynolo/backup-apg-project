package it.giunti.apg.server.business;

import it.giunti.apg.server.VisualLogger;
import it.giunti.apg.server.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.FileException;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.EvasioniComunicazioni;
import it.giunti.apg.shared.model.Indirizzi;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.hibernate.HibernateException;
import org.hibernate.Session;

public class FileFormatComunicazioni {

	//private static final Logger LOG = LoggerFactory.getLogger(FileFormatComunicazioni.class);
	private static final SimpleDateFormat SDF_BOLLETTINI = new SimpleDateFormat("dd/MM/yyyy");
	private static final String SEP = ";";
	private static final String SEP_ESCAPE = ",";
	private static final String EOL = "\r\n";
	private static final int LOG_INTERVAL = 500;
	
	public static void formatEvasioniComunicazioni(File file, List<EvasioniComunicazioni> ecList, int idRapporto)
			throws BusinessException, FileException {
		Locale.setDefault(Locale.ITALIAN);
		//la lista è in ordine di abbonamento e fascicolo
		if (file != null) {
			Session ses = SessionFactory.getSession();
			try {
				//Preparazione file
				formatEvasioniComunicazioni(ses, file, ecList, idRapporto);
			} catch(HibernateException e)	{
				VisualLogger.get().addHtmlErrorLine(idRapporto, e.getMessage(), e);
				throw new BusinessException(e.getMessage(), e);
			} catch(IOException e)	{
				VisualLogger.get().addHtmlErrorLine(idRapporto, e.getMessage(), e);
				throw new FileException(e.getMessage(), e);
			} finally {
				ses.close();
			}
		} else {
			VisualLogger.get().addHtmlErrorLine(idRapporto, "Errore nell'apertura del file");
		}
	}
	
	public static void formatEvasioniComunicazioni(Session ses,
			File file, List<EvasioniComunicazioni> ecList, int idRapporto)
					throws BusinessException, IOException, FileException, FileNotFoundException {
		Locale.setDefault(Locale.ITALIAN);
		//Preparazione file
		FileOutputStream fos = new FileOutputStream(file);
		OutputStreamWriter fileWriter = new OutputStreamWriter(fos, AppConstants.CHARSET);
		
		String fileData = createComunicazioniHeader();
		fileData += createComunicazioniFileContent(ses, ecList, new Date(), idRapporto);
		fileWriter.append(fileData);
		fileWriter.close();
		VisualLogger.get().addHtmlInfoLine(idRapporto, "Scrittura file completa");
	}
	
	public static String formatBollettinoText(String text, int lineWidth) {
		String result = "";
		if (text == null) return result;
		if (text.length() <= 2) return result;
		
		text = text.replaceAll("\\r\\n", "££");
		text = text.replaceAll("\\r", "££");
		text = text.replaceAll("\\n", "££");
		text = text.replaceAll("\\s\\s", "__");
		text = text.replaceAll("££", " § ");
		String[] words = text.split("\\s");
		String line1 = "";
		String line2 = "";
		for (String word:words) {
			if (word.length()>0) {
				line2 = line1;
				if (line1.length() > 0) line1 += " ";
				line1 += word;
				if (word.contains("§")) {
					result += line2 + "\r\n";
					line1 = "";
				}
				if (line1.length() > lineWidth) {
					result += line2 + "\r\n";
					line1 = word;
				}
			}
		}
		result += line1 + "\r\n";
		return result;
	}

	
	//Formattazione bollettini
	
	public static String createComunicazioniHeader() {
		String line = "";
		line += "progressivo"+SEP;
		line += "codiceAbbonamento"+SEP;
		line += "cap"+SEP;
		line += "titolo"+SEP;
		line += "cognome"+SEP;
		line += "presso"+SEP;
		line += "indirizzo"+SEP;
		line += "localita"+SEP;
		line += "provincia"+SEP;
		line += "nazione"+SEP;
		line += "tipoAbbonamento"+SEP;
		line += "importo"+SEP;
		line += "bandella"+SEP;//
		line += "data"+SEP;//
		line += "periodico"+SEP;
		line += "quintoCampo"+SEP;
		line += "autorizzazione"+SEP;//Autorizzazione
		line += "contoCorrente"+SEP;
		line += "ultimoNumero"+SEP;
		line += "importoAlt"+SEP;
		line += "titoloRegalo"+SEP;
		line += "cognomeRegalo"+SEP;
		line += "pressoRegalo"+SEP;
		line += "indirizzoRegalo"+SEP;
		line += "capRegalo"+SEP;
		line += "localitaRegalo"+SEP;
		line += "provinciaRegalo"+SEP;
		line += "nazioneRegalo"+SEP;
		line += EOL;
		return line;
	}
	
	private static String createComunicazioniFileContent(Session ses,
			List<EvasioniComunicazioni> ecList, Date date, int idRapporto)
			throws HibernateException {
		String result = "";
		//formatta ciascuna EvasioneComunicazione
		int progressivo = 1;
		for (EvasioniComunicazioni ec:ecList) {
			//Stampa solo se EC non eliminata
			if (!ec.getEliminato()){
				//crea la linea
				String line = createComunicazioneLine(progressivo, ec, date);
				progressivo++;
				result += line;
				if (progressivo % LOG_INTERVAL == 0)
					VisualLogger.get().addHtmlInfoLine(idRapporto, "Formattate "+progressivo+" linee");
			}
		}
		VisualLogger.get().addHtmlInfoLine(idRapporto, "Formattate "+progressivo+" linee");
		return result;
	}
	
	private static String createComunicazioneLine(Integer progressivo, EvasioniComunicazioni ec, Date date) {
		IstanzeAbbonamenti ia = ec.getIstanzaAbbonamento();
		String codiceTipoAbbonamento = ia.getListino().getTipoAbbonamento().getCodice();
		Anagrafiche anagSpedizione = null;
		Anagrafiche anagNotifica = null;
		//Se non è definito un pagante, la comunicazione va all'abbonato
		if (ia.getPagante() == null) {
			anagSpedizione = ia.getAbbonato();
			anagNotifica = new Anagrafiche();//quindi destinatario abb.regalo è vuoto
			anagNotifica.setIndirizzoPrincipale(new Indirizzi());
		} else {
			//Il pagante è definito
			if (ec.getIdTipoMedia().equals(AppConstants.COMUN_MEDIA_LETTERA) /*||
					ec.getIdTipoMedia().equals(AppConstants.COMUN_MEDIA_EMAIL)*/) {
				//Lettera abb regalo
				anagNotifica = ia.getPagante();
				anagSpedizione = ia.getAbbonato();
			}
			if (ec.getIdTipoMedia().equals(AppConstants.COMUN_MEDIA_BOLLETTINO) /*||
					ec.getIdTipoMedia().equals(AppConstants.COMUN_MEDIA_NDD)*/) {
				//Bollettino o ndd di abb regalo
				anagSpedizione = ia.getPagante();
				anagNotifica = ia.getAbbonato();
			}
		}
		String anagNome = anagSpedizione.getIndirizzoPrincipale().getCognomeRagioneSociale().trim();
		if (anagSpedizione.getIndirizzoPrincipale().getNome() != null) anagNome += " " +anagSpedizione.getIndirizzoPrincipale().getNome().trim();
		String pagaNome = "";
		if (anagNotifica.getIndirizzoPrincipale().getCognomeRagioneSociale() != null) pagaNome += anagNotifica.getIndirizzoPrincipale().getCognomeRagioneSociale().trim();
		if (anagNotifica.getIndirizzoPrincipale().getNome() != null) pagaNome += " " +anagNotifica.getIndirizzoPrincipale().getNome().trim();
		Double importo = ec.getImportoStampato();
		Double importoAlt = ec.getImportoAlternativoStampato();
		Integer idBandella = 0;
		if (ec.getComunicazione() != null) idBandella = ec.getComunicazione().getIdBandella();
		String quintoCampo = FileFormatCommon.getQuintoCampo(
				ia.getAbbonamento().getCodiceAbbonamento(),
				ia.getAbbonamento().getPeriodico().getNumeroCc());
		String ultimoNumero = ia.getFascicoloFine().getTitoloNumero();
		ultimoNumero = ultimoNumero.substring(ultimoNumero.indexOf('-')+1);
		String nomeNazione = "";
		if (anagNotifica.getIndirizzoPrincipale().getNazione() != null) {
			nomeNazione = anagNotifica.getIndirizzoPrincipale().getNazione().getNomeNazione();
		}
		return createComunicazioneString(progressivo,
				ia.getAbbonamento().getCodiceAbbonamento(),
				anagSpedizione.getIndirizzoPrincipale().getCap(), 
				anagSpedizione.getIndirizzoPrincipale().getTitolo(),
				anagNome,
				anagSpedizione.getIndirizzoPrincipale().getPresso(),
				anagSpedizione.getIndirizzoPrincipale().getIndirizzo(),
				anagSpedizione.getIndirizzoPrincipale().getLocalita(),
				anagSpedizione.getIndirizzoPrincipale().getProvincia(),
				anagSpedizione.getIndirizzoPrincipale().getNazione().getNomeNazione(),
				codiceTipoAbbonamento,
				importo,
				idBandella,
				date,
				ia.getAbbonamento().getPeriodico().getNome(),
				quintoCampo,
				ia.getAbbonamento().getPeriodico().getNumeroCc(),
				ultimoNumero,
				importoAlt,
				anagNotifica.getIndirizzoPrincipale().getTitolo(),
				pagaNome,
				anagNotifica.getIndirizzoPrincipale().getPresso(),
				anagNotifica.getIndirizzoPrincipale().getIndirizzo(),
				anagNotifica.getIndirizzoPrincipale().getCap(),
				anagNotifica.getIndirizzoPrincipale().getLocalita(),
				anagNotifica.getIndirizzoPrincipale().getProvincia(),
				nomeNazione);
		
	}
	private static String createComunicazioneString(Integer progressivo, String codiceAbbonamento, String cap,
			String titolo, String cognome, String presso,
			String indirizzo, String localita, String provincia,
			String nazione, String tipoAbbonamento, Double importo,
			Integer numeroTesto, Date data, String periodicoDescr,
			String quintoCampo, String ccp,
			String ultimoNumero, Double importoAlt, String titoloRegalo,
			String cognomeRegalo, String pressoRegalo, String indirizzoRegalo,
			String capRegalo, String localitaRegalo, String provinciaRegalo,
			String nazioneRegalo) {
		String line = "";
		line += progressivo+SEP;
		line += codiceAbbonamento+SEP;
		//line += FileFormatCommon.formatInteger(6, progressivo);
		//line += FileFormatCommon.formatString(7, codiceAbbonamento);
		if (cap == null) cap = "";
		if (cap.contains("0000")) cap = "";
		line += FileFormatCommon.escape(cap, SEP, SEP_ESCAPE)+SEP;
		line += FileFormatCommon.escape(titolo, SEP, SEP_ESCAPE)+SEP;
		line += FileFormatCommon.escape(cognome, SEP, SEP_ESCAPE)+SEP;
		line += FileFormatCommon.escape(presso, SEP, SEP_ESCAPE)+SEP;
		line += FileFormatCommon.escape(indirizzo, SEP, SEP_ESCAPE)+SEP;
		line += FileFormatCommon.escape(localita, SEP, SEP_ESCAPE)+SEP;
		//line += FileFormatCommon.formatString(5, cap);
		//line += FileFormatCommon.formatString(6, titolo);
		//line += FileFormatCommon.formatString(30, cognome);
		//line += FileFormatCommon.formatString(28, presso);
		//line += FileFormatCommon.formatString(36, indirizzo);
		//line += FileFormatCommon.formatString(26, localita);
		line += FileFormatCommon.escape(provincia, SEP, SEP_ESCAPE)+SEP;
		////if (provincia.equalsIgnoreCase("ee")) provincia = "";
		//line += FileFormatCommon.formatString(4, provincia);
		if (nazione.equalsIgnoreCase("italia")) nazione = "";
		line += FileFormatCommon.escape(nazione, SEP, SEP_ESCAPE)+SEP;
		line += tipoAbbonamento+SEP;
		//line += FileFormatCommon.formatString(21, nazione);
		//line += FileFormatCommon.formatString(2, tipoAbbonamento);
		if (importo != null) {
			line += FileFormatCommon.formatCurrency(importo)+SEP;//
		} else {
			//line += "       ";
			line += SEP;
		}
		line += FileFormatCommon.formatInteger(3, numeroTesto)+SEP;//
		line += SDF_BOLLETTINI.format(data)+SEP;//
		line += FileFormatCommon.escape(periodicoDescr, SEP, SEP_ESCAPE)+SEP;
		line += quintoCampo+SEP;
		line += FileFormatCommon.escape("  ", SEP, SEP_ESCAPE)+SEP;//Autorizzazione
		line += FileFormatCommon.escape(ccp, SEP, SEP_ESCAPE)+SEP;
		//line += FileFormatCommon.formatString(30, periodicoDescr);
		//line += FileFormatCommon.formatString(18, quintoCampo);
		//line += FileFormatCommon.formatString(2, autorizzazione);
		//line += FileFormatCommon.formatString(8, ccp);
		Integer ultimoNumeroInt = 0;
		try {
			ultimoNumeroInt = Integer.parseInt(ultimoNumero);
		} catch (NumberFormatException e) {}
		line += ultimoNumeroInt+SEP;
		//line += FileFormatCommon.formatInteger(3, ultimoNumeroInt);
		if (importoAlt != null) {
			line += FileFormatCommon.formatCurrency(importoAlt)+SEP;//
		} else {
			//line += "       ";
			line += SEP;
		}
		line += FileFormatCommon.escape(titoloRegalo, SEP, SEP_ESCAPE)+SEP;
		line += FileFormatCommon.escape(cognomeRegalo, SEP, SEP_ESCAPE)+SEP;
		line += FileFormatCommon.escape(pressoRegalo, SEP, SEP_ESCAPE)+SEP;
		line += FileFormatCommon.escape(indirizzoRegalo, SEP, SEP_ESCAPE)+SEP;
		//line += FileFormatCommon.formatString(6, titoloRegalo);
		//line += FileFormatCommon.formatString(25, cognomeRegalo);
		//line += FileFormatCommon.formatString(27, pressoRegalo);
		//line += FileFormatCommon.formatString(36, indirizzoRegalo);
		if (capRegalo == null) capRegalo = "";
		line += FileFormatCommon.escape(capRegalo, SEP, SEP_ESCAPE)+SEP;
		line += FileFormatCommon.escape(localitaRegalo, SEP, SEP_ESCAPE)+SEP;
		//line += FileFormatCommon.formatString(5, capRegalo);
		//line += FileFormatCommon.formatString(26, localitaRegalo);
		if (provinciaRegalo == null) provinciaRegalo = "";
		line += FileFormatCommon.escape(provinciaRegalo, SEP, SEP_ESCAPE)+SEP;
		//line += FileFormatCommon.formatString(4, provinciaRegalo);
		if (nazioneRegalo.equalsIgnoreCase("italia")) nazioneRegalo = "";
		line += FileFormatCommon.escape(nazioneRegalo, SEP, SEP_ESCAPE)+SEP;
		line += EOL;
		return line;
	}
	
	///** ATTENZIONE questo metodo deve essere
	// * incluso in una transazione */
	//public static void markComunicazioneAsEliminato(Session ses, EvasioniComunicazioni ec, Date date) 
	//		throws HibernateException {
	//	if (ec.getId() != null) {
	//		ec.setEliminato(true);
	//		ec.setDataStampa(date);
	//		new EvasioniComunicazioniDao().update(ses, ec);
	//	} else {
	//		// quando succede?
	//	}
	//}
}
