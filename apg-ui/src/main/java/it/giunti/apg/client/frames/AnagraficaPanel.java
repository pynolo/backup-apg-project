package it.giunti.apg.client.frames;

import java.util.Date;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.widgets.CodFiscText;
import it.giunti.apg.client.widgets.ConsensoPanel;
import it.giunti.apg.client.widgets.DateOnlyBox;
import it.giunti.apg.client.widgets.LocalitaCapPanel;
import it.giunti.apg.client.widgets.NoteArea;
import it.giunti.apg.client.widgets.PartitaIvaText;
import it.giunti.apg.client.widgets.select.NazioniSelect;
import it.giunti.apg.client.widgets.select.ProfessioniSelect;
import it.giunti.apg.client.widgets.select.SessoSelect;
import it.giunti.apg.client.widgets.select.TipiAnagraficaSelect;
import it.giunti.apg.client.widgets.select.TitoliStudioSelect;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.Indirizzi;
import it.giunti.apg.shared.model.Utenti;

public class AnagraficaPanel extends FlowPanel implements BlurHandler {

	private static final String BOX_WIDTH = "20em";
	//private static final int PROFESSIONE_DEFAULT = 1; //="altra professione"
	
	private Anagrafiche anag = null;
	private AnagraficheSuggPanel suggPanel = null;
	private boolean suggestionToForm = true;
	private boolean isOperator = false;
	//private boolean isEditor = false;
	private boolean isAdmin = false;
	//private boolean isSuper = false;
	private String lastSearchString = "";
	
	private TipiAnagraficaSelect tipoAnagraficaList = null;
	private TextBox titoloText = null;
	private SessoSelect sessoList = null;
	private TextBox ragSocText = null;
	private TextBox nomeText = null;
	private TextBox indirizzoText = null;
	private TextBox pressoText = null;
	private LocalitaCapPanel localitaCapPanel = null;
	private NazioniSelect nazioniList = null;
	private DateOnlyBox nascitaDate = null;
	private CodFiscText codFisText = null;
	private PartitaIvaText partIvaText = null;
	private TextBox codiceDestText = null;
	private CheckBox paCheck = null;
	private TextBox cufText = null;
	private TextBox telCasaText = null;
	private TextBox telMobileText = null;
	private TextBox emailPrimText = null;
	private TextBox emailPecText = null;
	private ProfessioniSelect professioniList = null;
	private TitoliStudioSelect titoliStudioList = null;
	private ConsensoPanel consensoPanel = null;
	private CheckBox adoCheck = null;
	private NoteArea noteArea = null;
	
	public AnagraficaPanel(Anagrafiche anag, AnagraficheSuggPanel suggPanel,
			boolean suggestionToForm, Utenti utente) {
		this.anag = anag;
		this.suggPanel = suggPanel;
		this.suggestionToForm = suggestionToForm;
		isOperator = (utente.getRuolo().getId() >= AppConstants.RUOLO_OPERATOR);
		//isEditor = (utente.getRuolo().getId() >= AppConstants.RUOLO_EDITOR);
		isAdmin = (utente.getRuolo().getId() >= AppConstants.RUOLO_ADMIN);
		//isSuper = (utente.getRuolo().getId() >= AppConstants.RUOLO_SUPER);
		if (this.anag == null) {
			this.anag = new Anagrafiche();
			this.anag.setIndirizzoPrincipale(new Indirizzi());
			this.anag.setIndirizzoFatturazione(new Indirizzi());
		}
		draw();
	}
	
	
	public void draw() {
		FlexTable table = new FlexTable();
		int r=0;
		String idNazione = AppConstants.DEFAULT_ID_NAZIONE_ITALIA;
		if (anag.getIndirizzoPrincipale() != null) {
			if (anag.getIndirizzoPrincipale().getNazione() != null) 
					idNazione = anag.getIndirizzoPrincipale().getNazione().getId();
		}
		//Consenso
		boolean consentEnabled = (anag.getId() == null);
		consensoPanel = new ConsensoPanel(
				anag.getConsensoMarketing(), anag.getConsensoProfilazione(),
				anag.getDataAggiornamentoConsenso(), consentEnabled);
		table.setWidget(r, 0, consensoPanel);
		table.getFlexCellFormatter().setColSpan(r, 0, 6);
		r++;
		
		if (anag.getUid() != null) {
			//Codice cliente
			table.setHTML(r, 0, "UID cliente");
			table.setHTML(r, 1, "<b>["+anag.getUid()+"]</b>");
			r++;
		}
		
		//Tipo Anagrafica
		table.setHTML(r, 0, "Anagrafica");
		tipoAnagraficaList = new TipiAnagraficaSelect(anag.getIdTipoAnagrafica());
		table.setWidget(r, 1, tipoAnagraficaList);
		r++;

		table.getFlexCellFormatter().setColSpan(r, 0, 6);
		table.setHTML(r, 0, "<b>Indirizzo principale</b>");
		r++;
		
		//Titolo
		table.setHTML(r, 0, "Titolo");
		titoloText = new TextBox();
		titoloText.setValue(anag.getIndirizzoPrincipale().getTitolo());
		titoloText.setMaxLength(6);
		titoloText.setWidth("5em");
		titoloText.setFocus(true);
		titoloText.setEnabled(isOperator);
		table.setWidget(r, 1, titoloText);
		r++;
		
		// RagSoc
		table.setHTML(r, 0, "Cognome/Rag.soc."+ClientConstants.MANDATORY);
		ragSocText = new TextBox();
		ragSocText.setValue(anag.getIndirizzoPrincipale().getCognomeRagioneSociale());
		ragSocText.setMaxLength(30);
		ragSocText.setWidth(BOX_WIDTH);
		ragSocText.addBlurHandler(this);
		ragSocText.setEnabled(isOperator);
		table.setWidget(r, 1, ragSocText);
		r++;
		
		// nome
		table.setHTML(r, 0, "Nome");
		nomeText = new TextBox();
		nomeText.setValue(anag.getIndirizzoPrincipale().getNome());
		nomeText.setMaxLength(25);
		nomeText.setWidth(BOX_WIDTH);
		nomeText.addBlurHandler(this);
		nomeText.setEnabled(isOperator);
		table.setWidget(r, 1, nomeText);
		r++;
		
		//Presso
		table.setHTML(r, 0, "Presso");
		pressoText = new TextBox();
		pressoText.setValue("");
		if (anag.getIndirizzoPrincipale() != null) {
			pressoText.setValue(anag.getIndirizzoPrincipale().getPresso());
		}
		pressoText.setWidth(BOX_WIDTH);
		pressoText.setMaxLength(28);
		pressoText.addBlurHandler(this);
		pressoText.setEnabled(isOperator);
		table.setWidget(r, 1, pressoText);
		r++;
		
		//Nazione
		table.setHTML(r, 0, "Nazione"+ClientConstants.MANDATORY);
		nazioniList = new NazioniSelect(idNazione);
		if (anag.getIndirizzoPrincipale() != null) {
			if (anag.getIndirizzoPrincipale().getNazione() != null) {
				nazioniList = new NazioniSelect(anag.getIndirizzoPrincipale().getNazione().getId());
			}
		}
		nazioniList.addBlurHandler(this);
		nazioniList.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				String idNazione = nazioniList.getSelectedValueString();
				if (idNazione != null) {
					localitaCapPanel.setIdNazione(idNazione);
					codFisText.setIdNazione(idNazione);
					partIvaText.setIdNazione(idNazione);
				}
			}
		});
		nazioniList.setEnabled(isOperator);
		table.setWidget(r, 1, nazioniList);
		r++;
		
		//Indirizzo
		table.setHTML(r, 0, "Indirizzo"+ClientConstants.MANDATORY);
		indirizzoText = new TextBox();
		indirizzoText.setValue("");
		if (anag.getIndirizzoPrincipale() != null)
			indirizzoText.setValue(anag.getIndirizzoPrincipale().getIndirizzo());
		indirizzoText.setWidth(BOX_WIDTH);
		indirizzoText.setMaxLength(36);
		indirizzoText.addBlurHandler(this);
		indirizzoText.setEnabled(isOperator);
		table.setWidget(r, 1, indirizzoText);
		r++;

		//Localita
		table.setHTML(r, 0, "Localit&agrave;"+ClientConstants.MANDATORY);
		localitaCapPanel = null;
		if (anag.getIndirizzoPrincipale() != null) {
			if (anag.getIndirizzoPrincipale().getNazione() != null) {
				localitaCapPanel = new LocalitaCapPanel(
						anag.getIndirizzoPrincipale().getLocalita(),
						anag.getIndirizzoPrincipale().getProvincia(),
						anag.getIndirizzoPrincipale().getCap(),
						anag.getIndirizzoPrincipale().getNazione().getId());
			}
		}
		if (localitaCapPanel == null) {
			localitaCapPanel = new LocalitaCapPanel("", "", "", AppConstants.DEFAULT_ID_NAZIONE_ITALIA);
		}
		localitaCapPanel.setIdNazione(idNazione);
		if (anag.getIndirizzoPrincipale() != null) {
			if (anag.getIndirizzoPrincipale().getNazione() != null) {
				localitaCapPanel.setIdNazione(anag.getIndirizzoPrincipale().getNazione().getId());
			}
		}
		localitaCapPanel.setEnabled(isOperator);
		table.getFlexCellFormatter().setColSpan(r, 1, 5);
		table.setWidget(r, 1, localitaCapPanel);
		//Verifica localita
		if (localitaCapPanel.getLocalitaCap().length() > 0 &&
				localitaCapPanel.getLocalitaName().length() > 0 &&
				localitaCapPanel.getLocalitaProv() != null) {
			localitaCapPanel.verifyStoredLocalita();
			if (localitaCapPanel.isEmpty()) {
				UiSingleton.get().addWarning("La localita' e' errata o incompleta");
			}
		}
		r++;

		table.getFlexCellFormatter().setColSpan(r, 0, 6);
		table.setHTML(r, 0, "<b>Dettagli personali</b>");
		r++;
		
		//Sesso
		table.setHTML(r, 0, "Sesso");
		sessoList = new SessoSelect(anag.getSesso());
		sessoList.setEnabled(isOperator);
		table.setWidget(r, 1, sessoList);
		r++;
		
		//Cod Fiscale
		table.setHTML(r, 0, "Codice fisc.");
		codFisText = new CodFiscText(idNazione);
		codFisText.setValue(anag.getCodiceFiscale());
		codFisText.setMaxLength(16);
		codFisText.setWidth(BOX_WIDTH);
		codFisText.setEnabled(isOperator);
		codFisText.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent arg0) {
				if (codFisText.getValue().length() == 0)
					UiSingleton.get().addWarning("Il codice fiscale e' vuoto");
			}
		});
		table.setWidget(r, 1, codFisText);
		r++;
		
		//Partita IVA
		table.setHTML(r, 0, "Partita IVA");
		partIvaText = new PartitaIvaText(idNazione);
		partIvaText.setValue(anag.getPartitaIva());
		partIvaText.setWidth(BOX_WIDTH);
		partIvaText.setMaxLength(16);
		partIvaText.setEnabled(isOperator);
		table.setWidget(r, 1, partIvaText);
		r++;
		
		//Data nascita
		table.setHTML(r, 0, "Data di nascita");
		nascitaDate = new DateOnlyBox();
		nascitaDate.setFormat(ClientConstants.BOX_FORMAT_DAY);
		nascitaDate.setValue(anag.getDataNascita());
		nascitaDate.setEnabled(isOperator);
		table.setWidget(r, 1, nascitaDate);
		r++;
		
		//Tel Casa
		table.setHTML(r, 0, "Telefono fisso");
		telCasaText = new TextBox();
		telCasaText.setValue(anag.getTelCasa());
		telCasaText.setMaxLength(32);
		telCasaText.setWidth(BOX_WIDTH);
		telCasaText.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent arg0) {
				if (!ValueUtil.isValidTelephone(telCasaText.getValue()))
						UiSingleton.get().addInfo("Il formato del telefono non è corretto");
			}
		});
		telCasaText.setEnabled(isOperator);
		table.setWidget(r, 1, telCasaText);
		r++;
		
		//Tel Mobile
		table.setHTML(r, 0, "Cellulare");
		telMobileText = new TextBox();
		telMobileText.setValue(anag.getTelMobile());
		telMobileText.setWidth(BOX_WIDTH);
		telMobileText.setMaxLength(32);
		telMobileText.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent arg0) {
				if (!ValueUtil.isValidTelephone(telMobileText.getValue()))
						UiSingleton.get().addInfo("Il formato del cellulare non è corretto");
			}
		});
		telMobileText.setEnabled(isOperator);
		table.setWidget(r, 1, telMobileText);
		r++;
		
		//Email 1
		table.setHTML(r, 0, "Email principale");
		emailPrimText = new TextBox();
		emailPrimText.setValue(anag.getEmailPrimaria());
		emailPrimText.setWidth(BOX_WIDTH);
		emailPrimText.setMaxLength(64);
		emailPrimText.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent arg0) {
				if (!ValueUtil.isValidEmail(emailPrimText.getValue()))
						UiSingleton.get().addInfo("Il formato dell'email primaria non è corretto");
			}
		});
		emailPrimText.setEnabled(isOperator);
		table.setWidget(r, 1, emailPrimText);
		r++;
		//Email PEC
		table.setHTML(r, 0, "PEC");
		emailPecText = new TextBox();
		emailPecText.setValue(anag.getEmailPec());
		emailPecText.setWidth(BOX_WIDTH);
		emailPecText.setMaxLength(64);
		emailPecText.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent arg0) {
				if (!ValueUtil.isValidEmail(emailPecText.getValue()))
						UiSingleton.get().addInfo("Il formato dell'PEC non è corretto");
			}
		});
		emailPecText.setEnabled(isOperator);
		table.setWidget(r, 1, emailPecText);
		r++;
		
		//Codice Destinatario
		table.setHTML(r, 0, "Codice dest./intermediario");
		codiceDestText = new TextBox();
		codiceDestText.setValue(anag.getCodiceDestinatario());
		codiceDestText.setWidth(BOX_WIDTH);
		codiceDestText.setEnabled(isOperator);
		codiceDestText.setMaxLength(8);
		table.setWidget(r, 1, codiceDestText);
		r++;
		
		//PA - Pubblica amministrazione
		HTML paLabel = new HTML("Pubblica amministrazione");
		paLabel.setTitle("PA");
		table.setWidget(r, 0, paLabel);
		paCheck = new CheckBox();
		paCheck.setValue(anag.getPa());
		paCheck.setEnabled(isOperator);
		table.setWidget(r, 1, paCheck);
		r++;
		
		//CUF - Codice Unico Ufficio per PA
		HTML cufLabel = new HTML("CUF");
		cufLabel.setTitle("Codice Unico Ufficio per PA");
		table.setWidget(r, 0, cufLabel);
		cufText = new TextBox();
		cufText.setValue(anag.getCuf());
		cufText.setWidth(BOX_WIDTH);
		cufText.setEnabled(isOperator);
		cufText.setMaxLength(8);
		table.setWidget(r, 1, cufText);
		r++;
		
		//Professione
		table.setHTML(r, 0, "Professione");
		if (anag.getProfessione() == null) {
			professioniList = new ProfessioniSelect(null);
		} else {
			professioniList = new ProfessioniSelect(anag.getProfessione().getId());
		}
		professioniList.setEnabled(isOperator);
		table.setWidget(r, 1, professioniList);
		r++;
		
		//Titolo di studio
		table.setHTML(r, 0, "Titolo di studio");
		if (anag.getTitoloStudio() == null) {
			titoliStudioList = new TitoliStudioSelect(null);
		} else {
			titoliStudioList = new TitoliStudioSelect(anag.getTitoloStudio().getId());
		}
		titoliStudioList.setEnabled(isOperator);
		table.setWidget(r, 1, titoliStudioList);
		r++;
		
		//Adottatario - read only
		HTML adoLabel = new HTML("Adottatario");
		adoLabel.setTitle("Adottatario");
		table.setWidget(r, 0, adoLabel);
		adoCheck = new CheckBox();
		adoCheck.setValue(anag.getAdottatario());
		adoCheck.setEnabled(isAdmin);
		table.setWidget(r, 1, adoCheck);
		r++;
		
		//Note
		table.setHTML(r, 0, "Note");
		noteArea = new NoteArea(2048);
		noteArea.setValue(anag.getNote());
		noteArea.setWidth("95%");
		noteArea.setHeight("3em");
		noteArea.setEnabled(isOperator);
		table.getFlexCellFormatter().setColSpan(r, 1, 4);
		table.setWidget(r, 1, noteArea);
		r++;
		
		this.add(table);
	}
	
	public Anagrafiche getValue() throws ValidationException {
		//Validazione
		if (ragSocText.getValue().trim().length() == 0) throw
			new ValidationException("Il cognome/ragione sociale e' obbligatorio");
		if (indirizzoText.getValue().trim().length() == 0) throw
			new ValidationException("L'indirizzo e' obbligatorio");
		localitaCapPanel.setIdNazione(nazioniList.getSelectedValueString());
		if (localitaCapPanel.isEmpty()) throw
			new ValidationException("La localita' e' errata o incompleta");
		if (!ValueUtil.isValidEmail(emailPrimText.getValue().trim())) throw
			new ValidationException("L'indirizzo email non e' valido");
		//if (!ValueUtil.isValidCodFisc(codFisText.getValue())) throw
		//	new ValidationException("Il codice fiscale non e' valido");

		//Assegnazione data
		Date today = DateUtil.now();
		if (anag == null) anag = new Anagrafiche();
		if (anag.getIndirizzoPrincipale() == null) 
			anag.setIndirizzoPrincipale(new Indirizzi());
		if (anag.getIndirizzoFatturazione() == null)
			anag.setIndirizzoFatturazione(new Indirizzi());
		anag.setIdProfessioneT(professioniList.getSelectedValueString());
		anag.setIdTitoloStudioT(titoliStudioList.getSelectedValueString());
		anag.setDataNascita(nascitaDate.getValue());
		anag.setSesso(sessoList.getSelectedValueString());
		anag.setCodiceFiscale(codFisText.getValue().toUpperCase().trim());
		anag.setPartitaIva(partIvaText.getValue().toUpperCase().trim());
		anag.setCodiceDestinatario(codiceDestText.getValue().trim());
		anag.setPa(paCheck.getValue());
		anag.setCuf(cufText.getValue().trim());
		anag.setTelCasa(telCasaText.getValue().trim());
		anag.setTelMobile(telMobileText.getValue().trim());
		anag.setEmailPrimaria(emailPrimText.getValue().toLowerCase().trim());
		anag.setEmailPec(emailPecText.getValue().toLowerCase().trim());
		anag.setIdTipoAnagrafica(tipoAnagraficaList.getSelectedValueString());
		anag.setAdottatario(adoCheck.getValue());
		anag.setNote(noteArea.getValue().trim());
		anag.setDataModifica(today);
		anag.setCodiceSap("");
		if (consensoPanel.getEnabled()) {
			anag.setConsensoTos(true);
			anag.setConsensoMarketing(consensoPanel.getMarketing());
			anag.setConsensoProfilazione(consensoPanel.getProfilazione());
			anag.setDataAggiornamentoConsenso(today);
		}
		anag.setNecessitaVerifica(false);
		if (anag.getDataCreazione() == null) anag.setDataCreazione(today);
		anag.setIdUtente(AuthSingleton.get().getUtente().getId());
		
		anag.getIndirizzoPrincipale().setTitolo(titoloText.getValue().trim());
		anag.getIndirizzoPrincipale().setCognomeRagioneSociale(ragSocText.getValue().trim());
		anag.getIndirizzoPrincipale().setNome(nomeText.getValue().trim());
		anag.getIndirizzoPrincipale().setCap(localitaCapPanel.getLocalitaCap().trim());
		anag.getIndirizzoPrincipale().setIndirizzo(indirizzoText.getValue().trim());
		anag.getIndirizzoPrincipale().setLocalita(localitaCapPanel.getLocalitaName().trim());
		anag.getIndirizzoPrincipale().setIdNazioneT(nazioniList.getSelectedValueString());
		anag.getIndirizzoPrincipale().setPresso(pressoText.getValue().trim());
		anag.getIndirizzoPrincipale().setProvincia(localitaCapPanel.getLocalitaProv());
		anag.getIndirizzoPrincipale().setDataModifica(today);
		anag.getIndirizzoPrincipale().setIdUtente(AuthSingleton.get().getUtente().getId());
		
		anag.getIndirizzoFatturazione().setTitolo("");
		anag.getIndirizzoFatturazione().setCognomeRagioneSociale("");
		anag.getIndirizzoFatturazione().setNome("");
		anag.getIndirizzoFatturazione().setCap("");
		anag.getIndirizzoFatturazione().setIndirizzo("");
		anag.getIndirizzoFatturazione().setLocalita("");
		anag.getIndirizzoFatturazione().setIdNazioneT(nazioniList.getSelectedValueString());
		anag.getIndirizzoFatturazione().setPresso("");
		anag.getIndirizzoFatturazione().setProvincia(null);
		anag.getIndirizzoFatturazione().setDataModifica(today);
		anag.getIndirizzoFatturazione().setIdUtente(AuthSingleton.get().getUtente().getId());
		return anag;
	}
	
	
	@Override
	public void onBlur(BlurEvent arg0) {
		//localitaCapPanel.verifyStoredLocalita();
		//if (localitaCapPanel.isEmpty()) {
		//	UiSingleton.get().addWarning("La localita' e' incompleta");
		//}
		String cap = localitaCapPanel.getLocalitaCap();
		String localita = localitaCapPanel.getLocalitaName();
		if (cap.length() < 5) cap = "";
		String searchString = ragSocText.getValue() + nomeText.getValue() +
				pressoText.getValue() + indirizzoText.getValue() +
				localita + cap;
		if (!searchString.equalsIgnoreCase(lastSearchString) && (suggPanel != null)) {
			lastSearchString = searchString;
			suggPanel.findSuggestions(ragSocText.getValue(),
					nomeText.getValue(),
					pressoText.getValue(),
					indirizzoText.getValue(),
					localita, cap,
					suggestionToForm);
		}
	}
	
}
