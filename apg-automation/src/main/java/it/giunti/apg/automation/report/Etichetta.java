package it.giunti.apg.automation.report;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import it.giunti.apg.automation.AutomationConstants;
import it.giunti.apg.core.business.FascicoliGroupBean;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Abbonamenti;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.Materiali;
import it.giunti.apg.shared.model.MaterialiSpedizione;

public class Etichetta {
	
	private String _codiceAbbonato;
	private String _logoFileName;
	private String _stampFileName;
	private Integer _copie;
	private String _elencoCm;
	private String _titolo;
	private String _indirizzoFormattato;
	private Date _dataStampa;
	
	public Etichetta() {}
	
	public Etichetta(Session ses, FascicoliGroupBean fg, String logoFileName,
			String stampFileName, Date dataStampa) throws BusinessException {
		loadMaterialiSpedizioni(ses, fg, logoFileName, stampFileName, dataStampa);
	}
	
	public Etichetta(Session ses, Anagrafiche anag, Materiali dono, String codAbb, Integer copie,
			String logoFileName, String stampFileName, Date dataStampa) throws BusinessException {
		loadDono(ses, anag, dono, codAbb, copie, logoFileName, stampFileName, dataStampa);
	}
	
	private void loadMaterialiSpedizioni(Session ses, FascicoliGroupBean fg,
			String logoFileName, String stampFileName, Date dataStampa) throws BusinessException {
		Abbonamenti abb = fg.getAbbonamento();
		if (fg.getMaterialiSpedizioneList().size() > 0) {
			MaterialiSpedizione ms0 = fg.getMaterialiSpedizioneList().get(0);
			Anagrafiche anag = GenericDao.findById(ses, Anagrafiche.class, ms0.getIdAnagrafica());
			_codiceAbbonato = abb.getCodiceAbbonamento();
			_logoFileName = AutomationConstants.REPORT_RESOURCES_PATH+logoFileName;
			_stampFileName = AutomationConstants.REPORT_RESOURCES_PATH+stampFileName;
			_copie = ms0.getCopie();
			_titolo = anag.getIndirizzoPrincipale().getTitolo();
			String elencoCm = "";
			for (MaterialiSpedizione numero:fg.getMaterialiSpedizioneList()) {
				if (elencoCm.length() > 0) elencoCm += AutomationConstants.SEPARATORE_CM_ETICHETTA;
				elencoCm += numero.getMateriale().getCodiceMeccanografico();
			}
			_elencoCm = elencoCm;
			_indirizzoFormattato = formatIndirizzo(anag);
			_dataStampa = dataStampa;
		}
	}
	
	private void loadDono(Session ses, Anagrafiche anag, Materiali dono, String codAbb,
			Integer copie, String logoFileName, String stampFileName, Date dataStampa) throws BusinessException {
		_codiceAbbonato=codAbb;
		_logoFileName = AutomationConstants.REPORT_RESOURCES_PATH+logoFileName;
		_stampFileName = AutomationConstants.REPORT_RESOURCES_PATH+stampFileName;
		_copie = copie;
		_titolo = anag.getIndirizzoPrincipale().getTitolo();
		_elencoCm = StringUtils.replace(
				 dono.getCodiceMeccanografico(),
				 AppConstants.STRING_SEPARATOR, AutomationConstants.SEPARATORE_CM_ETICHETTA);
		_indirizzoFormattato = formatIndirizzo(anag);
		_dataStampa = dataStampa;
	}
	
	private String formatIndirizzo(Anagrafiche anag) {
		//Ragione sociale
		String indirizzoFormattato = anag.getIndirizzoPrincipale().getCognomeRagioneSociale();
		if (anag.getIndirizzoPrincipale().getNome() != null) indirizzoFormattato += " " +anag.getIndirizzoPrincipale().getNome();
		//Presso
		if (anag.getIndirizzoPrincipale().getPresso() != null) {
			if (!anag.getIndirizzoPrincipale().getPresso().equals("")) {
				indirizzoFormattato +="\r\n"+anag.getIndirizzoPrincipale().getPresso();
			}
		}
		//Indirizzo stradale
		indirizzoFormattato += "\r\n"+anag.getIndirizzoPrincipale().getIndirizzo();
		//Localita
		String localita = "";
		if (anag.getIndirizzoPrincipale().getCap() != null) {
			if (!anag.getIndirizzoPrincipale().getCap().contains("0000")) {
				localita += anag.getIndirizzoPrincipale().getCap()+ " ";
			}
		}
		if (anag.getIndirizzoPrincipale().getLocalita() != null) localita += anag.getIndirizzoPrincipale().getLocalita()+ " ";
		String prov = anag.getIndirizzoPrincipale().getProvincia();
		if (prov != null) {
			if (!localita.equals(AppConstants.SELECT_EMPTY_LABEL)) {
				localita += anag.getIndirizzoPrincipale().getProvincia();
			}
		}
		indirizzoFormattato += "\r\n"+localita;
		//Nazione
		if (!anag.getIndirizzoPrincipale().getNazione().getId().equals(AppConstants.DEFAULT_ID_NAZIONE_ITALIA)) {
			indirizzoFormattato += "\r\n            "+
						anag.getIndirizzoPrincipale().getNazione().getNomeNazione().toUpperCase();
		}
		return indirizzoFormattato;
	}
	
	
	public String getElencoCm() {
		return _elencoCm;
	}
	public void setElencoCm(String elencoCm) {
		this._elencoCm = elencoCm;
	}

	public String getLogoFileName() {
		return _logoFileName;
	}

	public void setLogoFileName(String logoFileName) {
		this._logoFileName = logoFileName;
	}

	public String getStampFileName() {
		return _stampFileName;
	}

	public void setStampFileName(String _stampFileName) {
		this._stampFileName = _stampFileName;
	}

	public String getCodiceAbbonato() {
		return _codiceAbbonato;
	}

	public void setCodiceAbbonato(String codiceAbbonato) {
		this._codiceAbbonato = codiceAbbonato;
	}

	public Integer getCopie() {
		return _copie;
	}

	public void setCopie(Integer copie) {
		this._copie = copie;
	}

	public String getTitolo() {
		return _titolo;
	}

	public void setTitolo(String titolo) {
		this._titolo = titolo;
	}

	public String getIndirizzoFormattato() {
		return _indirizzoFormattato;
	}

	public void setIndirizzoFormattato(String indirizzoFormattato) {
		this._indirizzoFormattato = indirizzoFormattato;
	}

	public Date getDataStampa() {
		return _dataStampa;
	}

	public void setDataStampa(Date dataStampa) {
		this._dataStampa = dataStampa;
	}
	
}
