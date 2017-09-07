package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.widgets.CodFiscText;
import it.giunti.apg.client.widgets.LocalitaCapPanel;
import it.giunti.apg.client.widgets.select.NazioniSelect;
import it.giunti.apg.client.widgets.select.ProfessioniSelect;
import it.giunti.apg.client.widgets.select.SessoSelect;
import it.giunti.apg.client.widgets.select.TipiAnagraficaSelect;
import it.giunti.apg.client.widgets.select.TitoliStudioSelect;
import it.giunti.apg.core.DateUtil;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.Indirizzi;

import java.util.Date;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextBox;

public class QuickAnagPanel extends FlowPanel implements BlurHandler {

	private static final String BOX_WIDTH = "20em";
	//private static final int PROFESSIONE_DEFAULT = 1; //="altra professione"
	
	private Anagrafiche anag = null;
	private QuickSuggPanel suggPanel = null;
	private boolean suggestionToForm = true;
	private boolean enabled;
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
	private CodFiscText codFisText = null;
	private TextBox partIvaText = null;
	private TextBox telCasaText = null;
	private TextBox telMobileText = null;
	private TextBox emailPrimText = null;
	private TextBox emailSecText = null;
	private ProfessioniSelect professioniList = null;
	private TitoliStudioSelect titoliStudioList = null;
	private TextBox noteArea = null;
	
	public QuickAnagPanel(Anagrafiche anag, QuickSuggPanel suggPanel,
			boolean suggestionToForm, boolean enabled) {
		this.anag = anag;
		this.suggPanel = suggPanel;
		this.suggestionToForm = suggestionToForm;
		this.enabled = enabled;
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
		titoloText.setEnabled(enabled);
		table.setWidget(r, 1, titoloText);
		r++;
		
		// RagSoc
		table.setHTML(r, 0, "Cognome/Rag.soc."+ClientConstants.MANDATORY);
		ragSocText = new TextBox();
		ragSocText.setValue(anag.getIndirizzoPrincipale().getCognomeRagioneSociale());
		ragSocText.setMaxLength(30);
		ragSocText.setWidth(BOX_WIDTH);
		ragSocText.addBlurHandler(this);
		ragSocText.setEnabled(enabled);
		table.setWidget(r, 1, ragSocText);
		r++;
		
		// nome
		table.setHTML(r, 0, "Nome");
		nomeText = new TextBox();
		nomeText.setValue(anag.getIndirizzoPrincipale().getNome());
		nomeText.setMaxLength(25);
		nomeText.setWidth(BOX_WIDTH);
		nomeText.addBlurHandler(this);
		nomeText.setEnabled(enabled);
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
		pressoText.setEnabled(enabled);
		table.setWidget(r, 1, pressoText);
		r++;
		
		//Nazione
		table.setHTML(r, 0, "Nazione"+ClientConstants.MANDATORY);
		nazioniList = new NazioniSelect(AppConstants.DEFAULT_ID_NAZIONE_ITALIA);
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
				if (idNazione != null) localitaCapPanel.setIdNazione(idNazione);
			}
		});
		nazioniList.setEnabled(enabled);
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
		indirizzoText.setEnabled(enabled);
		table.setWidget(r, 1, indirizzoText);
		r++;

		//Localita
		table.setHTML(r, 0, "Localit&agrave;"+ClientConstants.MANDATORY);
		if (anag.getIndirizzoPrincipale() != null) {
			localitaCapPanel = new LocalitaCapPanel(
					anag.getIndirizzoPrincipale().getLocalita(),
					anag.getIndirizzoPrincipale().getProvincia(),
					anag.getIndirizzoPrincipale().getCap());
		} else {
			localitaCapPanel = new LocalitaCapPanel("", "", "");
		}
		localitaCapPanel.setIdNazione(AppConstants.DEFAULT_ID_NAZIONE_ITALIA);
		if (anag.getIndirizzoPrincipale() != null) {
			if (anag.getIndirizzoPrincipale().getNazione() != null) {
				localitaCapPanel.setIdNazione(anag.getIndirizzoPrincipale().getNazione().getId());
			}
		}
		localitaCapPanel.setEnabled(enabled);
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
		sessoList.setEnabled(enabled);
		table.setWidget(r, 1, sessoList);
		r++;
		
		//Cod Fiscale
		table.setHTML(r, 0, "Codice fisc.");
		codFisText = new CodFiscText();
		codFisText.setValue(anag.getCodiceFiscale());
		codFisText.setMaxLength(16);
		codFisText.setWidth(BOX_WIDTH);
		codFisText.setEnabled(enabled);
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
		partIvaText = new TextBox();
		partIvaText.setValue(anag.getPartitaIva());
		partIvaText.setWidth(BOX_WIDTH);
		partIvaText.setEnabled(enabled);
		partIvaText.setMaxLength(16);
		partIvaText.setEnabled(enabled);
		table.setWidget(r, 1, partIvaText);
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
		telCasaText.setEnabled(enabled);
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
		telMobileText.setEnabled(enabled);
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
						UiSingleton.get().addInfo("Il formato dell'email non è corretto");
			}
		});
		emailPrimText.setEnabled(enabled);
		table.setWidget(r, 1, emailPrimText);
		r++;
		//Email 2
		table.setHTML(r, 0, "Email secondaria");
		emailSecText = new TextBox();
		emailSecText.setValue(anag.getEmailSecondaria());
		emailSecText.setWidth(BOX_WIDTH);
		emailSecText.setMaxLength(64);
		emailSecText.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent arg0) {
				if (!ValueUtil.isValidEmail(emailSecText.getValue()))
						UiSingleton.get().addInfo("Il formato dell'email non è corretto");
			}
		});
		emailSecText.setEnabled(enabled);
		table.setWidget(r, 1, emailSecText);
		r++;
		
		//Professione
		table.setHTML(r, 0, "Professione");
		if (anag.getProfessione() == null) {
			professioniList = new ProfessioniSelect(null);
		} else {
			professioniList = new ProfessioniSelect(anag.getProfessione().getId());
		}
		professioniList.setEnabled(enabled);
		table.setWidget(r, 1, professioniList);
		r++;
		
		//Titolo di studio
		table.setHTML(r, 0, "Titolo di studio");
		if (anag.getTitoloStudio() == null) {
			titoliStudioList = new TitoliStudioSelect(null);
		} else {
			titoliStudioList = new TitoliStudioSelect(anag.getTitoloStudio().getId());
		}
		titoliStudioList.setEnabled(enabled);
		table.setWidget(r, 1, titoliStudioList);
		r++;
		
		//Note
		table.setHTML(r, 0, "Note");
		noteArea = new TextBox();
		noteArea.setValue(anag.getNote());
		noteArea.setWidth("95%");
		noteArea.setMaxLength(250);
		noteArea.setEnabled(enabled);
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
		//Assegnamento
		Date today = DateUtil.now();
		if (anag == null) anag = new Anagrafiche();
		if (anag.getIndirizzoPrincipale() == null) 
			anag.setIndirizzoPrincipale(new Indirizzi());
		if (anag.getIndirizzoFatturazione() == null)
			anag.setIndirizzoFatturazione(new Indirizzi());
		anag.setIdProfessioneT(professioniList.getSelectedValueString());
		anag.setIdTitoloStudioT(titoliStudioList.getSelectedValueString());
		anag.setSesso(sessoList.getSelectedValueString());
		anag.setCodiceFiscale(codFisText.getValue().toUpperCase().trim());
		anag.setPartitaIva(partIvaText.getValue().toUpperCase().trim());
		anag.setTelCasa(telCasaText.getValue().trim());
		anag.setTelMobile(telMobileText.getValue().trim());
		anag.setEmailPrimaria(emailPrimText.getValue().trim());
		anag.setEmailSecondaria(emailSecText.getValue().trim());
		anag.setIdTipoAnagrafica(tipoAnagraficaList.getSelectedValueString());
		anag.setNote(noteArea.getValue().trim());
		anag.setDataModifica(today);
		anag.setCodiceSap("");
		anag.setConsensoCommerciale(true);
		anag.setConsensoDati(true);
		anag.setNecessitaVerifica(false);
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
