package it.giunti.apgautomation.server.report;


public class Talloncino {
	
	private String logoFileName;
	private Integer copie;
	private String descrFascicolo;
	private String avvisoFileName;
	private String stampFileName;
	private String titolo1;
	private String titolo2;
	private String indirizzoFormattato;
	private String cap;
	private String nazione;
	
	public Talloncino() {}
	
	//public Talloncino(Session ses, String fileRow) throws BusinessException {
	//	String letteraPeriodico = fileRow.substring(6, 7);
	//	System.out.println(fileRow);
	//	logoFileName = AutomationConstants.REPORT_RESOURCES_PATH+logoFromLettera(ses, letteraPeriodico);
	//	String codice = fileRow.substring(6, 13).trim();
	//	String codiceAbbonato = "Codice Abbonato: "+codice;
	//	String copieString = fileRow.substring(157, 160).trim();
	//	copie = Integer.valueOf(copieString);
	//	String titoloPersonale = fileRow.substring(18, 24).trim();
	//	if (copie == 0) copie = 1;
	//	if (copie == 1) {
	//		//Copia singola
	//		avvisoFileName = "";
	//		titolo1 = codiceAbbonato;
	//		titolo2 = titoloPersonale;
	//		//ATTENZIONE Sotto i valori titolo1 titolo2 vengono cambiati in base alla nazione!!!
	//	} else {
	//		//Più copie
	//		avvisoFileName = AutomationConstants.REPORT_RESOURCES_PATH+AutomationConstants.IMG_DESTINATARIO_UNICO;
	//		titolo1 = "";
	//		titolo2 = codiceAbbonato;
	//	}
	//	String descrPeriodico = fileRow.substring(170, 200).trim();
	//	String numFascicolo = fileRow.substring(294, 302).trim();
	//	descrFascicolo = descrPeriodico+" "+numFascicolo;
	//	
	//	String cognomeNome = fileRow.substring(24, 54).trim();
	//	String presso = fileRow.substring(54, 82).trim();
	//	String indirizzo = fileRow.substring(82, 118).trim();
	//	String localita = fileRow.substring(118, 144).trim();
	//	this.cap = fileRow.substring(13, 18).trim();
	//	String provincia = fileRow.substring(144, 148).trim();
	//	this.nazione = fileRow.substring(238, 278).trim();
	//	indirizzoFormattato = cognomeNome;
	//	if (!presso.equals("")) {
	//		indirizzoFormattato +="\r\n"+presso;
	//	}
	//	indirizzoFormattato += "\r\n"+indirizzo;
	//	if (!cap.equals("00000") && !cap.equals("0000")) {
	//		localita = cap + " " + localita;
	//	}
	//	if (!provincia.equals("EE")) {
	//		localita += " " + provincia;
	//	}
	//	indirizzoFormattato += "\r\n"+localita;
	//	stampFileName = AutomationConstants.REPORT_RESOURCES_PATH+AutomationConstants.IMG_STAMP_PERIODICO;
	//	//Nazione
	//	if (!nazione.equals("")) {
	//		indirizzoFormattato += "\r\n                             "+nazione;
	//		stampFileName = AutomationConstants.REPORT_RESOURCES_PATH+AutomationConstants.IMG_STAMP_ECONOMY;
	//		titolo1 = "";
	//		titolo2 = codiceAbbonato;
	//	}
	//}	
	
	public String getLogoFileName() {
		return logoFileName;
	}

	public void setLogoFileName(String logoFileName) {
		this.logoFileName = logoFileName;
	}
	
	public String getDescrFascicolo() {
		return descrFascicolo;
	}
	
	public void setDescrFascicolo(String descrFascicolo) {
		this.descrFascicolo = descrFascicolo;
	}

	public Integer getCopie() {
		return copie;
	}

	public void setCopie(Integer copie) {
		this.copie = copie;
	}

	public String getAvvisoFileName() {
		return avvisoFileName;
	}

	public void setAvvisoFileName(String avvisoFileName) {
		this.avvisoFileName = avvisoFileName;
	}

	public String getStampFileName() {
		return stampFileName;
	}

	public void setStampFileName(String stampFileName) {
		this.stampFileName = stampFileName;
	}

	public String getTitolo1() {
		return titolo1;
	}

	public void setTitolo1(String titolo1) {
		this.titolo1 = titolo1;
	}

	public String getTitolo2() {
		return titolo2;
	}

	public void setTitolo2(String titolo2) {
		this.titolo2 = titolo2;
	}

	public String getIndirizzoFormattato() {
		return indirizzoFormattato;
	}

	public void setIndirizzoFormattato(String indirizzoFormattato) {
		this.indirizzoFormattato = indirizzoFormattato;
	}

	public String getCap() {
		return cap;
	}

	public void setCap(String cap) {
		this.cap = cap;
	}

	public String getNazione() {
		return nazione;
	}

	public void setNazione(String nazione) {
		this.nazione = nazione;
	}
	
}
