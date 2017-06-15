package it.giunti.apgautomation.server.report;

import it.giunti.apg.server.business.FileFormatCommon;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.EvasioniComunicazioni;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.ModelliBollettini;
import it.giunti.apgautomation.server.AutomationConstants;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Locale;

public class Bollettino  {
	
	private static final DecimalFormat DF = new DecimalFormat("00000000.00");
	private static final String TESTO_BENEFICIARIO_REGALO = 
			"Richiesta quota abbonamento regalo a favore di:";
	
	private String reportFilePath = "";
	private String codiceModello = null;
	private String autorizzazione = "";
	private Double importo = null;
	private String importoCl = null;
	private String logoVerticalPath = null;
	private String logoSmallPath = null;
	private String logoEuroPath = null;
	private String anagCodiceFiscale = null;
	private String anagPartitaIva = null;
	private String anagTitolo = "";
	private String anagIndirizzoFormattato = "";
	private String periodicoCc = null;
	private String periodicoCc12 = null;
	private String periodicoIban = null;
	private String periodicoNome = null;
	private String periodicoIndirizzo = null;
	private String quintoCampo = "";
	private String barcodeValue = null;
	private InputStream barcode128cInputStream = null;
	private InputStream dataMatrixInputStream = null;
	private String codiceAbbonamento = "";
	private String testoBandella = "";
	private String testoRegalo = "";
	

	public Bollettino(ModelliBollettini modello, EvasioniComunicazioni ec) {
		IstanzeAbbonamenti ia = ec.getIstanzaAbbonamento();
		Double importo = ec.getImportoStampato();
		init(ia, importo, modello);
	}

	public Bollettino(IstanzeAbbonamenti ia, Double importo, ModelliBollettini modello) {
		init(ia, importo, modello);
	}
	
	public void init(IstanzeAbbonamenti ia, Double importo, ModelliBollettini modello) {
		/** Viene SEMPRE spedito solo al pagante */
		Anagrafiche dest = ia.getPagante();
		if (dest == null) {
			//non c'è pagante => non è un abb.regalo
			dest = ia.getAbbonato();
		} else {
			//c'è un pagante => imposto il testo del regalo
			Anagrafiche beneficiario = ia.getAbbonato();
			testoRegalo = TESTO_BENEFICIARIO_REGALO+"\r\n";
			String benefRagSoc = beneficiario.getIndirizzoPrincipale().getCognomeRagioneSociale();
			if (beneficiario.getIndirizzoPrincipale().getNome() != null) 
					benefRagSoc += " " + beneficiario.getIndirizzoPrincipale().getNome();
			String benefPresso = beneficiario.getIndirizzoPrincipale().getPresso();
			String benefIndirizzo = beneficiario.getIndirizzoPrincipale().getIndirizzo();
			String benefLocalita = beneficiario.getIndirizzoPrincipale().getLocalita();
			String benefProvincia = beneficiario.getIndirizzoPrincipale().getProvincia();
			if (benefProvincia == null) benefProvincia = "";
			String benefCap = beneficiario.getIndirizzoPrincipale().getCap();
			testoRegalo += benefRagSoc+" - ";
			if (benefPresso != null) {
				if (benefPresso.length() > 0) {
					testoRegalo += benefPresso+" - ";
				}
			}
			testoRegalo += benefIndirizzo+" - ";
			if (benefCap != null) testoRegalo += benefCap+" ";
			if (benefLocalita != null) testoRegalo += benefLocalita+" ";
			if (benefProvincia != null) testoRegalo += "("+benefProvincia+")";
		}
		this.testoBandella = modello.getTestoBandella();
		this.autorizzazione = modello.getAutorizzazione();
		this.codiceModello = modello.getCodiceModello();
		this.reportFilePath = modello.getReportFilePath();
		this.logoVerticalPath = modello.getLogoVerticalPath();
		this.logoSmallPath = modello.getLogoSmallPath();
		this.logoEuroPath = AutomationConstants.REPORT_RESOURCES_PATH+AutomationConstants.IMG_EURO;
		this.anagTitolo = "";
		if (dest.getIndirizzoPrincipale().getTitolo() != null) 
			this.anagTitolo = dest.getIndirizzoPrincipale().getTitolo().toUpperCase();
		String destRagSoc = dest.getIndirizzoPrincipale().getCognomeRagioneSociale().toUpperCase();
		if (dest.getIndirizzoPrincipale().getNome() != null) 
				destRagSoc += " " + dest.getIndirizzoPrincipale().getNome().toUpperCase();
		String destPresso = dest.getIndirizzoPrincipale().getPresso();
		String destIndirizzo = dest.getIndirizzoPrincipale().getIndirizzo();
		String destLocalita = dest.getIndirizzoPrincipale().getLocalita();
		String destProvincia = dest.getIndirizzoPrincipale().getProvincia();
		if (destProvincia == null) destProvincia = "";
		String destCap = dest.getIndirizzoPrincipale().getCap();
		this.anagIndirizzoFormattato = destRagSoc+"\r\n";
		if (destPresso != null) {
			if (destPresso.length() > 0) {
				this.anagIndirizzoFormattato += destPresso.toUpperCase()+"\r\n";
			}
		}
		this.anagIndirizzoFormattato += destIndirizzo.toUpperCase()+"\r\n";
		if (destCap != null) anagIndirizzoFormattato += destCap.toUpperCase()+" ";
		if (destLocalita != null) anagIndirizzoFormattato += destLocalita.toUpperCase()+" ";
		if (destProvincia != null) anagIndirizzoFormattato += "("+destProvincia.toUpperCase()+")";
		this.quintoCampo = FileFormatCommon.getQuintoCampo(
				ia.getAbbonamento().getCodiceAbbonamento(),
				ia.getAbbonamento().getPeriodico().getNumeroCc());
		this.codiceAbbonamento = ia.getAbbonamento().getCodiceAbbonamento();
		//Nazione
		if (!dest.getIndirizzoPrincipale().getNazione().getId().equals(AppConstants.DEFAULT_ID_NAZIONE_ITALIA)) {
			anagIndirizzoFormattato += "\r\n            "+
						dest.getIndirizzoPrincipale().getNazione().getNomeNazione().toUpperCase();
		} else {
			anagIndirizzoFormattato += "\r\n";
		}
		//Periodico
		this.periodicoCc = ia.getAbbonamento().getPeriodico().getNumeroCc();
		this.periodicoCc12 = "000000000000"+this.periodicoCc;
		this.periodicoCc12 = this.periodicoCc12.substring(this.periodicoCc12.length()-12);
		this.periodicoIban = ia.getAbbonamento().getPeriodico().getIban().toUpperCase();
		this.periodicoNome = ia.getAbbonamento().getPeriodico().getNome().toUpperCase();
		this.periodicoIndirizzo = ia.getAbbonamento().getPeriodico().getIndirizzo().toUpperCase();
		//importo
		this.importo = importo;
		this.importoCl = formatImportoCl(importo);
		//Codice fiscale
		this.anagCodiceFiscale = "";
		if (dest.getCodiceFiscale() != null) this.anagCodiceFiscale = dest.getCodiceFiscale().toUpperCase();
		//Partita iva
		this.anagPartitaIva = "";
		if (dest.getPartitaIva() != null) this.anagPartitaIva = dest.getPartitaIva().toUpperCase();
		//barcode
		this.barcodeValue = formatBarcode(this.quintoCampo, periodicoCc, importoCl, codiceModello);
		
		//DataMatrix
		try {
			byte[] dataMatrix = BarcodeUtil.getDataMatrixFile(barcodeValue);
			this.dataMatrixInputStream = new ByteArrayInputStream(dataMatrix);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//Barcode128c
		try {
			byte[] barcode128c = BarcodeUtil.getBarcode128cFile(barcodeValue);
			this.barcode128cInputStream = new ByteArrayInputStream(barcode128c);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String formatImportoCl(Double importo) {
		Locale.setDefault(Locale.ITALY);
		//Importo CL (sul piè di pagina del bollettino)
		String importoCl = DF.format(importo);
		importoCl = importoCl.replaceFirst("\\,", "\\+");
		importoCl = importoCl.replaceFirst("\\.", "\\+");
		return importoCl;
	}
	
	private String formatBarcode(String quintoCampo, String periodicoCc,
			String importoCl, String codiceModello) {
		String result = "18";//lunghezza V campo
		result += quintoCampo;
		result += "12";//lunghezza cc
		String cc = "000000000000"+periodicoCc;
		cc = cc.substring(cc.length()-12, cc.length());
		result += cc;
		result += "10";//lunghezza importo (8 cifre + 2 decimali senza separatore)
		String importo10 = "0000000000"+importoCl.replaceFirst("\\+", "");
		importo10 = importo10.substring(importo10.length()-10, importo10.length());
		result += importo10;
		result += "3"; //lunghezza tipo documento
		result += codiceModello;
		return result;
	}
	
	// GETTERS
	
	public String getReportFilePath() {
		return reportFilePath;
	}
	
	public Double getImporto() {
		return importo;
	}

	public String getImportoCl() {
		return importoCl;
	}

	public String getAnagCodiceFiscale() {
		return anagCodiceFiscale;
	}

	public String getAnagPartitaIva() {
		return anagPartitaIva;
	}
	
	public String getAnagTitolo() {
		return anagTitolo;
	}

	public String getAnagIndirizzoFormattato() {
		return anagIndirizzoFormattato;
	}

	public String getQuintoCampo() {
		return quintoCampo;
	}

	public String getCodiceAbbonamento() {
		return codiceAbbonamento;
	}

	public String getAutorizzazione() {
		return autorizzazione;
	}
	
	public String getCodiceModello() {
		return codiceModello;
	}
	
	public String getLogoVerticalPath() {
		return logoVerticalPath;
	}
	
	public String getLogoSmallPath() {
		return logoSmallPath;
	}
	
	public String getLogoEuroPath() {
		return logoEuroPath;
	}
	
	public String getTestoBandella() {
		return testoBandella;
	}
	
	public String getTestoRegalo() {
		return testoRegalo;
	}
	
	public String getPeriodicoIndirizzo() {
		return periodicoIndirizzo;
	}
	
	public String getPeriodicoNome() {
		return periodicoNome;
	}
	
	public String getPeriodicoCc() {
		return periodicoCc;
	}
	
	public String getPeriodicoCc12() {
		return periodicoCc12;
	}
	
	public String getPeriodicoIban() {
		return periodicoIban;
	}

	public String getBarcodeValue() {
		return barcodeValue;
	}

	public InputStream getBarcode128cInputStream() {
		return barcode128cInputStream;
	}

	public InputStream getDataMatrixInputStream() {
		return dataMatrixInputStream;
	}

}
