package it.giunti.apg.automation.report;

import it.giunti.apg.automation.AutomationConstants;
import it.giunti.apg.core.business.IndirizziBusiness;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.AliquoteIva;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.Fatture;
import it.giunti.apg.shared.model.FattureArticoli;
import it.giunti.apg.shared.model.Indirizzi;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Nazioni;
import it.giunti.apg.shared.model.Societa;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FatturaBean {
	
	private static final String NOTA_FATTURA_PAGATA = "FATTURA PAGATA";
	private static final String NOTA_CARTA_DOCENTE = "PAGATA CON CARTA DEL DOCENTE";
	private static final String NOTA_RIMBORSO = "DOCUMENTO IN CORSO DI RIMBORSO";
	
	private String bannerImgFile;
	private String societaRagSoc;
	private String societaBox1;
	private String societaBox2;
	private String societaBox3;
	private String recipientAddress;
	private String tipoDocumentoDesc;
	private Date fatturaData;
	private String fatturaNumero;
	private String fileName;
	private String codFisc;
	private String pIva;
	private String note;
	private Double totaleImponibile;
	private Double totaleIva;
	private Double totaleFinale;
	private String notaDocumento;
	
	private List<FatturaArticoloBean> artBeanList;
	private Fatture fattura;
	private String idTipoPagamento;
	private IstanzeAbbonamenti istanzaAbbonamento;
	
	public FatturaBean(String logoFileName, Fatture fattura,
			List<FattureArticoli> faList, String idTipoPagamento,
			Anagrafiche pagante, Societa societa) throws BusinessException {
		createFatturaBean(logoFileName, fattura, faList, idTipoPagamento, pagante, societa, null);
	}
	
	public FatturaBean(String logoFileName, Fatture fattura,
			List<FattureArticoli> faList, String idTipoPagamento,
			Anagrafiche pagante, Societa societa, IstanzeAbbonamenti ia) throws BusinessException {
		createFatturaBean(logoFileName, fattura, faList, idTipoPagamento, pagante, societa, ia);
	}
	
	private void createFatturaBean(String logoFileName, Fatture fattura,
			List<FattureArticoli> faList, String idTipoPagamento,
			Anagrafiche pagante, Societa societa, IstanzeAbbonamenti ia) throws BusinessException {
		this.fattura = fattura;
		this.idTipoPagamento = idTipoPagamento;
		this.istanzaAbbonamento = ia;
//		IstanzeAbbonamenti ia = GenericDao.findById(ses, IstanzeAbbonamenti.class, fattura.getIdIstanza());
//		Anagrafiche pagante = GenericDao.findById(ses, Anagrafiche.class, fattura.getIdAnagrafica());
//		Societa societa = GenericDao.findById(ses, Societa.class, fattura.getIdSocieta());
		this.codFisc = "Codice fiscale: "+AutomationConstants.LABEL_NON_DISPONIBILE;
		if (pagante.getCodiceFiscale() != null) {
			if (pagante.getCodiceFiscale().length() > 0) this.codFisc = "Codice fiscale: " + pagante.getCodiceFiscale();
		}
		this.pIva = " ";
		if (pagante.getPartitaIva() != null) {
			if (pagante.getPartitaIva().length() > 0) this.pIva = "Partita IVA: " + pagante.getPartitaIva();
		}
		String indirizzo = formatIndirizzo(pagante);
		this.bannerImgFile = logoFileName;
		this.fatturaData = fattura.getDataFattura();
		this.fatturaNumero = fattura.getNumeroFattura();
		this.fileName = fattura.getNumeroFattura()+".pdf";
		this.recipientAddress = indirizzo;
		this.societaRagSoc = societa.getNome();
		this.societaBox1 = societa.getTestoFattura1();
		this.societaBox2 = societa.getTestoFattura2();
		String datiFatturazione = "";
		if (societa.getPartitaIva() != null) {
			if (societa.getPartitaIva().length() > 0) {
				datiFatturazione += "Partita IVA "+societa.getPartitaIva()+"\r\n";
			}
		}
		if (societa.getCodiceFiscale() != null) {
			if (societa.getCodiceFiscale().length() > 0) {
				datiFatturazione += "Codice Fiscale "+societa.getCodiceFiscale()+"\r\n";
			}
		}
		if (this.societaBox2 == null) this.societaBox2 = "";
		if (this.societaBox2.length() == 0) {
			this.societaBox2 = datiFatturazione;
		} else {
			this.societaBox2 += "\r\n"+datiFatturazione;
		}
		//this.societaBox3 = datiFatturazione;
		this.tipoDocumentoDesc = AppConstants.DOCUMENTO_DESC.get(fattura.getIdTipoDocumento())+" num.";
		if (fattura.getIdTipoDocumento().equalsIgnoreCase(AppConstants.DOCUMENTO_FATTURA)) {
			this.notaDocumento = NOTA_FATTURA_PAGATA;
			if (idTipoPagamento != null) {
				if (idTipoPagamento.equals(AppConstants.PAGAMENTO_CARTA_DOCENTE)) {
					this.notaDocumento = NOTA_CARTA_DOCENTE;
				}
			}
		} else {
			this.notaDocumento = NOTA_RIMBORSO;
		}
		this.note = buildNote(pagante);
		this.totaleImponibile = 0D;
		this.totaleIva = 0D;
		this.totaleFinale = 0D;
		// ** Dettaglio articoli **
		artBeanList = new ArrayList<FatturaBean.FatturaArticoloBean>();
		for (FattureArticoli articoli:faList) {
			this.totaleImponibile += articoli.getImportoImpUnit()*articoli.getQuantita();
			this.totaleIva += articoli.getImportoIvaUnit()*articoli.getQuantita();
			this.totaleFinale += articoli.getImportoTotUnit()*articoli.getQuantita();
			this.artBeanList.add(createArticoloBeanFromFatturaArticolo(articoli, fattura.getTipoIva()));
		}
	}
	
	private String formatIndirizzo(Anagrafiche anag) {
		Indirizzi indFatt = anag.getIndirizzoPrincipale();
		if (IndirizziBusiness.isFilledUp(anag.getIndirizzoFatturazione()))
			indFatt = anag.getIndirizzoFatturazione();
		//Ragione sociale
		String indirizzoFormattato = indFatt.getCognomeRagioneSociale();
		if (indFatt.getNome() != null) indirizzoFormattato += " " +indFatt.getNome();
		//Presso
		if (indFatt.getPresso() != null) {
			if (!indFatt.getPresso().equals("")) {
				indirizzoFormattato +="\r\n"+indFatt.getPresso();
			}
		}
		//Indirizzo stradale
		indirizzoFormattato += "\r\n"+indFatt.getIndirizzo();
		//Localita
		String localita = "";
		if (indFatt.getCap() != null) {
			if (!indFatt.getCap().contains("0000")) {
				localita += indFatt.getCap()+ " ";
			}
		}
		if (indFatt.getLocalita() != null) localita += indFatt.getLocalita()+ " ";
		String prov = indFatt.getProvincia();
		if (prov != null) {
			if (!localita.equals(AppConstants.SELECT_EMPTY_LABEL)) {
				localita += indFatt.getProvincia();
			}
		}
		indirizzoFormattato += "\r\n"+localita;
		//Nazione
		if (!indFatt.getNazione().getId().equals(AppConstants.DEFAULT_ID_NAZIONE_ITALIA)) {
			indirizzoFormattato += "\r\n            "+
					indFatt.getNazione().getNomeNazione().toUpperCase();
		}
		return indirizzoFormattato;
	}


	private String buildNote(Anagrafiche pagante) {
		Indirizzi indFatt = pagante.getIndirizzoPrincipale();
		if (IndirizziBusiness.isFilledUp(pagante.getIndirizzoFatturazione()))
			indFatt = pagante.getIndirizzoFatturazione();
		Nazioni naz = indFatt.getNazione();
		boolean hasIva = false;
		if (pagante.getPartitaIva() != null) {
			if (pagante.getPartitaIva().length() > 0) hasIva = true;
		}
		if (naz.getId().equals(AppConstants.DEFAULT_ID_NAZIONE_ITALIA)) {
			return "";
		}
		if (naz.getUe()) {
			if (hasIva) {
				return "V.f.c.IVA art.7 ter (D) - Subject to reverse charge art. 196 Dir. 2006/112/EC";
			} else {
				return "";
			}
		}
		//ELSE (extra UE)
		return "V.f.c.IVA art.7 ter (F)";
	}
	
	private static FatturaBean.FatturaArticoloBean createArticoloBeanFromFatturaArticolo(FattureArticoli fa, String tipoIva) 
			throws BusinessException {
		FatturaBean.FatturaArticoloBean result = new FatturaBean.FatturaArticoloBean();
		result.setDesc(fa.getDescrizione());
		result.setQuantita(fa.getQuantita());
		result.setPrezzoImpUnit(fa.getImportoImpUnit());
		String ivaDescr = "";
		String ivaCodice = "";
		if (!fa.getResto() && fa.getAliquotaIva() != null &&
				!fa.getIvaScorporata()) {
			ivaDescr = fa.getAliquotaIva().getDescr();
			ivaCodice = ValueUtil.getCodiceIva(fa.getAliquotaIva(), tipoIva);
		}
		if (fa.getIvaScorporata()) ivaDescr = AppConstants.DEFAULT_IVA_SCORPORATA_DESCR;
		result.setIvaDesc(ivaDescr);
		result.setIvaCodice(ivaCodice);
		result.setAliquota(fa.getAliquotaIva());
		result.setPrezzoFinale(fa.getImportoTotUnit()*fa.getQuantita());
		return result;
	}
	
	public String getBannerImgFile() {
		return bannerImgFile;
	}

	public void setBannerImgFile(String bannerImgFile) {
		this.bannerImgFile = bannerImgFile;
	}

	public String getSocietaRagSoc() {
		return societaRagSoc;
	}

	public void setSocietaRagSoc(String societaRagSoc) {
		this.societaRagSoc = societaRagSoc;
	}

	public String getSocietaBox1() {
		return societaBox1;
	}

	public void setSocietaBox1(String societaBox1) {
		this.societaBox1 = societaBox1;
	}

	public String getSocietaBox2() {
		return societaBox2;
	}

	public void setSocietaBox2(String societaBox2) {
		this.societaBox2 = societaBox2;
	}

	public String getSocietaBox3() {
		return societaBox3;
	}

	public void setSocietaBox3(String societaBox3) {
		this.societaBox3 = societaBox3;
	}

	public String getRecipientAddress() {
		return recipientAddress;
	}

	public void setRecipientAddress(String recipientAddress) {
		this.recipientAddress = recipientAddress;
	}
	
	public String getTipoDocumentoDesc() {
		return tipoDocumentoDesc;
	}

	public void setTipoDocumentoDesc(String tipoDocumentoDesc) {
		this.tipoDocumentoDesc = tipoDocumentoDesc;
	}

	public Date getFatturaData() {
		return fatturaData;
	}

	public void setFatturaData(Date fatturaData) {
		this.fatturaData = fatturaData;
	}

	public String getFatturaNumero() {
		return fatturaNumero;
	}

	public void setFatturaNumero(String fatturaNumero) {
		this.fatturaNumero = fatturaNumero;
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	//public void setFatturaNumero(String companyPrefix, Date date, Integer numero) 
	//		throws BusinessException {
	//	String nf = FattureBusiness.buildNumeroFattura(companyPrefix, date, numero);
	//	this.fatturaNumero = nf;
	//	this.fileName = nf + ".pdf";
	//}

	public String getCodFisc() {
		return codFisc;
	}

	public void setCodFisc(String codFisc) {
		this.codFisc = codFisc;
	}

	public String getpIva() {
		return pIva;
	}

	public void setpIva(String pIva) {
		this.pIva = pIva;
	}

	public List<FatturaArticoloBean> getArticoliList() {
		return artBeanList;
	}

	public void setArticoliList(List<FatturaArticoloBean> articoliList) {
		this.artBeanList = articoliList;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Double getTotaleImponibile() {
		return totaleImponibile;
	}

	public void setTotaleImponibile(Double totaleImponibile) {
		this.totaleImponibile = totaleImponibile;
	}

	public Double getTotaleIva() {
		return totaleIva;
	}

	public void setTotaleIva(Double totaleIva) {
		this.totaleIva = totaleIva;
	}

	public Double getTotaleFinale() {
		return totaleFinale;
	}

	public void setTotaleFinale(Double totaleFinale) {
		this.totaleFinale = totaleFinale;
	}
	
	public String getNotaDocumento() {
		return notaDocumento;
	}

	public void setNotaDocumento(String notaDocumento) {
		this.notaDocumento = notaDocumento;
	}

	public Fatture getFattura() {
		return fattura;
	}

	public void setFattura(Fatture fattura) {
		this.fattura = fattura;
	}

	public IstanzeAbbonamenti getIstanzaAbbonamento() {
		return istanzaAbbonamento;
	}

	public void setIstanzaAbbonamento(IstanzeAbbonamenti istanzaAbbonamento) {
		this.istanzaAbbonamento = istanzaAbbonamento;
	}

	public String getIdTipoPagamento() {
		return idTipoPagamento;
	}

	public void setIdTipoPagamento(String idTipoPagamento) {
		this.idTipoPagamento = idTipoPagamento;
	}
	
	

	
	//*** Inner Classes ***




	public static class FatturaArticoloBean {
		private String desc;
		private Double prezzoImpUnit;
		private Integer quantita;
		private String ivaDesc;
		private String ivaCodice;
		private AliquoteIva aliquota;
		private Double prezzoFinale;
		
		public String getDesc() {
			return desc;
		}
		public void setDesc(String desc) {
			this.desc = desc;
		}
		public Double getPrezzoImpUnit() {
			return prezzoImpUnit;
		}
		public void setPrezzoImpUnit(Double prezzoImpUnit) {
			this.prezzoImpUnit = prezzoImpUnit;
		}
		public Integer getQuantita() {
			return quantita;
		}
		public void setQuantita(Integer quantita) {
			this.quantita = quantita;
		}
		public String getIvaDesc() {
			return ivaDesc;
		}
		public void setIvaDesc(String ivaDesc) {
			this.ivaDesc = ivaDesc;
		}
		public String getIvaCodice() {
			return ivaCodice;
		}
		public void setIvaCodice(String ivaCodice) {
			this.ivaCodice = ivaCodice;
		}
		public AliquoteIva getAliquota() {
			return aliquota;
		}
		public void setAliquota(AliquoteIva aliquota) {
			this.aliquota = aliquota;
		}
		public Double getPrezzoFinale() {
			return prezzoFinale;
		}
		public void setPrezzoFinale(Double prezzoFinale) {
			this.prezzoFinale = prezzoFinale;
		}
	}
	
}
