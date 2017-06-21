package it.giunti.apg.core.business;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.EvasioniArticoli;
import it.giunti.apg.shared.model.EvasioniFascicoli;
import it.giunti.apg.shared.model.Indirizzi;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

public class FileFormatCommon {
    
	//private static final Logger LOG = LoggerFactory.getLogger(FileFormatCommon.class);
	
	public static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	//public static final DecimalFormat df = new DecimalFormat("0.00");
	//public static final DecimalFormat df4_2 = new DecimalFormat("0000.00");
	//public static final DecimalFormat df3 = new DecimalFormat("000");
	//public static final String YEAR2 = sdf.format(new Date()).substring(8);
	
	private static final String SEP = ";";
	private static final String SEP_ESCAPE = ",";
	
	public static String evasioneArticoloToBuffer(Session ses, Integer progressivo,
			EvasioniArticoli ea, int idRapporto) {
		IstanzeAbbonamenti ia = GenericDao.findById(ses, IstanzeAbbonamenti.class,
				ea.getIdIstanzaAbbonamento());
		Anagrafiche anagSpedizione = GenericDao.findById(ses, Anagrafiche.class, ea.getIdAnagrafica());
		if (anagSpedizione != null) {
			ia.setAbbonato(anagSpedizione);
			ia.setPagante(null);
		}
		return createInvioLine(progressivo, ia,
				ea.getArticolo().getCodiceMeccanografico(),
				ia.getFascicoloFine().getDataFine(), new Date());
	}
	
	public static final String createInvioLine(Integer progressivo, IstanzeAbbonamenti ia,
			String cm, Date dataScadenza, Date dataInvio) {
		Anagrafiche anag = ia.getAbbonato();
		Anagrafiche paga = ia.getPagante();
		if (paga == null) {
			paga = new Anagrafiche();
			paga.setIndirizzoPrincipale(new Indirizzi());
		}
		String anagNome = anag.getIndirizzoPrincipale().getCognomeRagioneSociale().trim();
		if (anag.getIndirizzoPrincipale().getNome() != null) anagNome += " " +anag.getIndirizzoPrincipale().getNome().trim();
		String pagaNome = "";
		if (paga.getIndirizzoPrincipale().getCognomeRagioneSociale() != null) pagaNome += paga.getIndirizzoPrincipale().getCognomeRagioneSociale().trim();
		if (paga.getIndirizzoPrincipale().getNome() != null) pagaNome += " " +paga.getIndirizzoPrincipale().getNome().trim();
		Double importo = 0D;
		Double importoAlt = 0D;
		Integer copie = ia.getCopie();
		//if (copie == 1) copie = 0;
		String vCampoAutCcp = "";
		String nazioneUfficioRecapito = anag.getIndirizzoPrincipale().getNazione().getNomeNazione();
		if (nazioneUfficioRecapito.equalsIgnoreCase("italia")) {
			nazioneUfficioRecapito = "";
		}
		String fineStradale = "";
		String stradale = "";
		String omaggio = "";
		String scadenza = "";
		if (ia.getListino().getStampaScrittaOmaggio()) {
			omaggio = ServerConstants.INVIO_OMAGGIO;
		} else {
			if (dataScadenza != null) {
				scadenza = ServerConstants.INVIO_SCADENZA +
						ServerConstants.INVIO_SDF_FASCICOLI.format(dataScadenza);
			}
		}
		String fascia = "";
		String aziendaArticolo = "";
		String locAziendaArticolo = "";
		if (ia.getListino().getStampaDonatore()) {
			aziendaArticolo = ServerConstants.INVIO_COPIA_OFFERTA+pagaNome;
			locAziendaArticolo = paga.getIndirizzoPrincipale().getLocalita();
		}
		return createIndirizzarioString(progressivo,
				ia.getAbbonamento().getCodiceAbbonamento(),
				anag.getIndirizzoPrincipale().getCap(), 
				anag.getIndirizzoPrincipale().getTitolo(),
				anagNome,
				anag.getIndirizzoPrincipale().getPresso(),
				anag.getIndirizzoPrincipale().getIndirizzo(),
				anag.getIndirizzoPrincipale().getLocalita(),
				anag.getIndirizzoPrincipale().getProvincia(),
				ia.getListino().getTipoAbbonamento().getCodice(),
				importo,
				copie,
				dataInvio,
				ia.getAbbonamento().getPeriodico().getNome(),
				vCampoAutCcp,
				fineStradale,
				importoAlt,
				nazioneUfficioRecapito,
				stradale,
				cm,
				omaggio,
				scadenza,
				fascia,
				aziendaArticolo,
				locAziendaArticolo);
	}
	private final static String createIndirizzarioString(Integer progressivo, String codiceAbbonamento, String cap,
			String titolo, String cognome, String presso,
			String indirizzo, String localita, String provincia,
			String tipoAbbonamento, Double importo, Integer copie,
			Date data, String periodicoDescr, String vCampoAutCcp,
			String fineStradale, Double importoAlt, String nazioneUfficioRecapito,
			String stradale, String cm, String omaggio,
			String scadenza, String fascia, String aziendaArticolo,
			String locAziendaArticolo) {
		String line = "";
		line += progressivo+SEP;// 0
		line += codiceAbbonamento+SEP;// 1
		if (cap == null) cap = "";
		if (cap.contains("0000")) cap = "";
		line += FileFormatCommon.escape(cap, SEP, SEP_ESCAPE)+SEP;// 2
		line += FileFormatCommon.escape(titolo, SEP, SEP_ESCAPE)+SEP;// 3
		line += FileFormatCommon.escape(cognome, SEP, SEP_ESCAPE)+SEP;// 4
		line += FileFormatCommon.escape(presso, SEP, SEP_ESCAPE)+SEP;// 5
		line += FileFormatCommon.escape(indirizzo, SEP, SEP_ESCAPE)+SEP;// 6
		line += FileFormatCommon.escape(localita, SEP, SEP_ESCAPE)+SEP;// 7
		if (provincia == null) provincia = "";
		line += FileFormatCommon.escape(provincia, SEP, SEP_ESCAPE)+SEP;// 8
		line += tipoAbbonamento+SEP;// 9
		line += FileFormatCommon.formatCurrency(importo)+SEP;// 10
		line += copie+SEP;// 11
		line += ServerConstants.INVIO_SDF_FASCICOLI.format(data)+SEP;// 12
		line += FileFormatCommon.escape(periodicoDescr, SEP, SEP_ESCAPE)+SEP;// 13
		line += FileFormatCommon.escape(vCampoAutCcp, SEP, SEP_ESCAPE)+SEP;// 14vuoto
		line += FileFormatCommon.escape(fineStradale, SEP, SEP_ESCAPE)+SEP;// 15
		line += FileFormatCommon.formatCurrency(importoAlt)+SEP;// 16
		line += FileFormatCommon.escape(nazioneUfficioRecapito, SEP, SEP_ESCAPE)+SEP;// 17
		line += FileFormatCommon.escape(stradale, SEP, SEP_ESCAPE)+SEP;// 18
		line += FileFormatCommon.escape(cm, SEP, SEP_ESCAPE)+SEP;// 19
		line += FileFormatCommon.escape(omaggio, SEP, SEP_ESCAPE)+SEP;
		line += FileFormatCommon.escape(scadenza, SEP, SEP_ESCAPE)+SEP;
		line += FileFormatCommon.escape(fascia, SEP, SEP_ESCAPE)+SEP;
		line += FileFormatCommon.escape(aziendaArticolo, SEP, SEP_ESCAPE)+SEP;
		line += FileFormatCommon.escape(locAziendaArticolo, SEP, SEP_ESCAPE)+SEP;
		line += ServerConstants.INVIO_EOL;
		return line;
	}
	
	public final static String createInvioHeader() {
		String line = "";
		line += "progressivo"+SEP;
		line += "codiceAbbonamento"+SEP;// 1
		line += "cap"+SEP;// 2
		line += "titolo"+SEP;// 3
		line += "cognome"+SEP;// 4
		line += "presso"+SEP;// 5
		line += "indirizzo"+SEP;// 6
		line += "localita"+SEP;// 7
		line += "provincia"+SEP;// 8
		line += "tipoAbbonamento"+SEP;// 9
		line += "importo"+SEP;// 10
		line += "copie"+SEP;// 11
		line += "data"+SEP;// 12
		line += "periodicoDescr"+SEP;// 13
		line += "vCampoAutCcp"+SEP;// 14vuoto
		line += "fineStradale"+SEP;// 15
		line += "importoAlt"+SEP;// 16
		line += "nazioneUfficioRecapito"+SEP;// 17
		line += "stradale"+SEP;// 18
		line += "cm"+SEP;// 19
		line += "omaggio"+SEP;
		line += "scadenza"+SEP;
		line += "fascia"+SEP;
		line += "aziendaArticolo"+SEP;
		line += "locAziendaArticolo"+SEP;
		line += ServerConstants.INVIO_EOL;
		return line;
	}
	
//	private final static String createIndirizzarioString(Integer progressivo, String codiceAbbonamento, String cap,
//			String titolo, String cognome, String presso,
//			String indirizzo, String localita, String provincia,
//			String tipoAbbonamento, Double importo, Integer copie,
//			Date data, String periodicoDescr, String vCampoAutCcp,
//			String fineStradale, Double importoAlt, String nazioneUfficioRecapito,
//			String stradale, String titoloNumero, String omaggio,
//			String scadenza, String fascia, String aziendaArticolo,
//			String locAziendaArticolo) {
//		String line = "";
//		line += FileFormatCommon.formatInteger(6, progressivo);
//		line += FileFormatCommon.formatString(7, codiceAbbonamento);
//		if (cap == null) cap = "";
//		if (cap.contains("0000")) cap = "";
//		line += FileFormatCommon.formatString(5, cap);
//		line += FileFormatCommon.formatString(6, titolo);
//		line += FileFormatCommon.formatString(30, cognome);
//		line += FileFormatCommon.formatString(28, presso);
//		line += FileFormatCommon.formatString(36, indirizzo);
//		line += FileFormatCommon.formatString(26, localita);
//		if (provincia == null) provincia = "";
//		//if (provincia.equalsIgnoreCase("ee")) provincia = "";
//		line += FileFormatCommon.formatString(4, provincia);
//		line += FileFormatCommon.formatString(2, tipoAbbonamento);
//		line += FileFormatCommon.formatCurrency(7, importo);
//		line += FileFormatCommon.formatInteger(3, copie);
//		line += ServerConstants.INVIO_SDF_FASCICOLI.format(data);//10 char
//		line += FileFormatCommon.formatString(30, periodicoDescr);
//		line += FileFormatCommon.formatString(28, vCampoAutCcp);
//		line += FileFormatCommon.formatString(3, fineStradale);
//		line += FileFormatCommon.formatCurrency(7, importoAlt);
//		line += FileFormatCommon.formatString(40, nazioneUfficioRecapito);
//		line += FileFormatCommon.formatString(16, stradale);
//		line += FileFormatCommon.formatString(8, titoloNumero);
//		line += FileFormatCommon.formatString(20, omaggio);
//		line += FileFormatCommon.formatString(32, scadenza);
//		line += FileFormatCommon.formatString(1, fascia);
//		line += FileFormatCommon.formatString(48, aziendaArticolo);
//		line += FileFormatCommon.formatString(20, locAziendaArticolo);
//		line += ServerConstants.INVIO_EOL;
//		return line;
//	}
		
	public static String escape(String text, String separator, String replacement) {
		String s = "";
		if (text != null) {
			s = StringUtils.replace(text, separator, replacement);
			s = StringUtils.replace(text, "\"", "'");
		}
		return s;
	}
	
	public static String formatString(int i,String s) {
		if (s == null) {
			s = new String();
		}
		s=s.trim().toUpperCase();
		if (s.length()==i) return s;
		if (s.length()>i) {
			return s.substring(0, i);
		} else {
			for (int c=s.length(); c<i; c++) {
				s=s.concat(" ");
			}
		}
		return s;
	}
	
	//Il numero è allineato a destra con zero iniziali
	public static String formatInteger(int i,Integer num) throws NumberFormatException {
		if (num == null) {
			num = 0;
		}
		String s = num.toString();
		if (s.length()==i) return s;
		if (s.length()>i) {
			throw new NumberFormatException("Integer "+num+" doesn't fit in "+i+" characters");
		} else {
			for (int c=s.length(); c<i; c++) {
				s="0"+s;
			}
		}
		return s;
	}
	
	public static String formatCurrency(Double num) throws NumberFormatException {
		Locale.setDefault(Locale.ITALIAN);
		DecimalFormat df = new DecimalFormat("0.00");
		if (num == null) {
			num = 0D;
		}
		String s = df.format(num);
		//if (s.length()==i) return s;
		//if (s.length()>i) {
		//	throw new NumberFormatException("Double "+s+" doesn't fit in "+i+" characters");
		//} else {
		//	for (int c=s.length(); c<i; c++) {
		//		s="0"+s;
		//	}
		//}
		return s;
	}
	
	public static String getQuintoCampo(String codiceAbbonamento, String ccpPeriodico) {
		String campo = "";
		DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
		campo += df.format(Calendar.getInstance().getTime());
		campo += codiceAbbonamento.substring(1);
		String cc = "00000000"+ccpPeriodico;
		campo += cc.substring(cc.length()-8);//le 8 cifre meno significative del ccp
		//Codice di controllo (2 char)
		Double campoD = Double.parseDouble(campo);
		Double numA = Math.floor(campoD/93D);
		Double numB = numA*93D;
		Double ctrl = campoD - numB;
		String ctrlString = formatInteger(2, ctrl.intValue());
		campo += ctrlString;
		return campo;
	}
	
	/** nuova versione del metodo che accoda gli arretrati come num.Meccanografici
	 * 
	 * @param arreList
	 * @param idRapporto
	 * @return
	 */
	public static List<String> findIntervals(List<EvasioniFascicoli> arreList, int fieldLength, int idRapporto) {
		List<String> result = new ArrayList<String>();
		String string = new String();
		for (EvasioniFascicoli ef:arreList) {
			String nm = ef.getFascicolo().getCodiceMeccanografico().trim();
			if (string.length()+nm.length() > fieldLength) {
				result.add(string);
				string = new String();
			}
			if (string.length() > 0) string += AppConstants.STRING_SEPARATOR;
			string += nm;
		}
		result.add(string);
		return result;
	}

}
