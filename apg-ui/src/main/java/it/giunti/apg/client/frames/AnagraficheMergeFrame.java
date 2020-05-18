package it.giunti.apg.client.frames;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.UriManager;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.AnagraficheService;
import it.giunti.apg.client.services.AnagraficheServiceAsync;
import it.giunti.apg.client.widgets.CodFiscText;
import it.giunti.apg.client.widgets.DateOnlyBox;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.client.widgets.LocalitaCapPanel;
import it.giunti.apg.client.widgets.NoteArea;
import it.giunti.apg.client.widgets.PartitaIvaText;
import it.giunti.apg.client.widgets.select.NazioniSelect;
import it.giunti.apg.client.widgets.select.ProfessioniSelect;
import it.giunti.apg.client.widgets.select.SessoSelect;
import it.giunti.apg.client.widgets.select.TipiAnagraficaSelect;
import it.giunti.apg.client.widgets.select.TitoliStudioSelect;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.Indirizzi;
import it.giunti.apg.shared.model.Ruoli;
import it.giunti.apg.shared.model.Utenti;

public class AnagraficheMergeFrame extends FramePanel implements IAuthenticatedWidget, IRefreshable {
	private final AnagraficheServiceAsync anagraficheService = GWT.create(AnagraficheService.class);
	
	private static final String TITLE_ANAGRAFICA = "Unione anagrafiche";
	
	private static final String BOX_WIDTH = "20em";
	
	private UriParameters params;
	private Integer idAnagrafica = null;
	private Integer idOptionalSecondAnagrafica = null;
	private Anagrafiche anag1 = null;
	private Anagrafiche anag2 = null;
	private Anagrafiche anag3 = null;
	private Ruoli userRole = null;
	//private boolean isSuper = false;
	//private boolean isEditor = false;
	private boolean isOperator = false;
	
	private FlowPanel panelAna = null;
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
	private PartitaIvaText partIvaText = null;
	private TextBox codiceDestText = null;
	private CheckBox paCheck = null;
	private TextBox cufText = null;
	private DateOnlyBox nascitaDate = null;
	private TextBox telCasaText = null;
	private TextBox telMobileText = null;
	private TextBox emailPrimText = null;
	private TextBox emailPecText = null;
	private ProfessioniSelect professioniList = null;
	private TitoliStudioSelect titoliStudioList = null;
	private NoteArea noteArea = null;
	private TextBox giuntiCardText = null;
	private TextBox sapText = null;
	private CheckBox consentTos = null;
	private CheckBox consentMarketing = null;
	private CheckBox consentProfilazione = null;
	private DateOnlyBox consentDate = null;
	private TextBox titoloFattText = null;
	private TextBox ragSocFattText = null;
	private TextBox nomeFattText = null;
	private TextBox indirizzoFattText = null;
	private TextBox pressoFattText = null;
	private LocalitaCapPanel localitaFattCapPanel = null;
	private NazioniSelect nazioniFattList = null;
	
	public AnagraficheMergeFrame(UriParameters params) {
		super();
		if (params != null) {
			this.params = params;
		} else {
			this.params = new UriParameters();
		}
		Integer value = this.params.getIntValue(AppConstants.PARAM_ID);
		Integer optionalSecondValue = this.params.getIntValue(AppConstants.PARAM_ID_ANAGRAFICA);
		if (value != null) {
			idAnagrafica = value;
			idOptionalSecondAnagrafica = optionalSecondValue;
			AuthSingleton.get().queueForAuthentication(this);
		}
	}
	
	@Override
	public void onSuccessfulAuthentication(Utenti utente) {
		init(utente);
	}
	
	private void init(Utenti utente) {
		// Editing rights
		userRole = utente.getRuolo();
		isOperator = (userRole.getId() >= AppConstants.RUOLO_OPERATOR);
		//isEditor = (userRole.getId() >= AppConstants.RUOLO_EDITOR);
		//isSuper = (userRole.getId() >= AppConstants.RUOLO_SUPER);
		// UI
		if (isOperator) {
			panelAna = new FlowPanel();
			this.add(panelAna, TITLE_ANAGRAFICA);
			this.setWidth("100%");
			loadAnagrafiche();
		}
	}
	
	@Override
	public void refresh() {
		// Auto-generated method stub
	}

	/** This method empties the ContentPanel and redraws the 'item' data
	 * @param item
	 */
	private void drawAnagrafiche() {
		boolean enabled = isOperator;
		FlexTable table = new FlexTable();
		panelAna.add(table);
		int r=0;
		
		table.setHTML(r, 1, "<b>Anagrafica 1</b>");
		table.setHTML(r, 3, "<b>Anagrafica 2</b>");
		table.setHTML(r, 5, "<b>Risultato</b>");
		r++;
		
		if (anag3.getUid() != null) {
			//Codice cliente
			table.setHTML(r, 0, "Codice cliente");
			table.setHTML(r, 1, anag1.getUid());
			table.setHTML(r, 3, anag2.getUid()+" (sar&agrave; sovrascritto)");
			table.setHTML(r, 5, "<b>"+anag3.getUid()+"</b>");
			r++;
		}
		
		//Tipo Anagrafica
		table.setHTML(r, 0, "Anagrafica");
		TipiAnagraficaSelect tipoAnagraficaList1 =
				new TipiAnagraficaSelect(anag1.getIdTipoAnagrafica());
		tipoAnagraficaList1.setEnabled(false);
		table.setWidget(r, 1, tipoAnagraficaList1);
		TipiAnagraficaSelect tipoAnagraficaList2 =
				new TipiAnagraficaSelect(anag2.getIdTipoAnagrafica());
		tipoAnagraficaList2.setEnabled(false);
		table.setWidget(r, 3, tipoAnagraficaList2);
		tipoAnagraficaList = new TipiAnagraficaSelect(anag3.getIdTipoAnagrafica());
		tipoAnagraficaList.setEnabled(enabled);
		table.setWidget(r, 5, tipoAnagraficaList);
		r++;
		
		table.setHTML(r, 0, "<b>Indirizzo principale</b>");
		table.getFlexCellFormatter().setColSpan(r, 0, 6);
		r++;
		
		//Titolo
		table.setHTML(r, 0, "Titolo");
		table.setHTML(r, 1, anag1.getIndirizzoPrincipale().getTitolo());
		table.setHTML(r, 3, anag2.getIndirizzoPrincipale().getTitolo());
		titoloText = new TextBox();
		titoloText.setValue(anag3.getIndirizzoPrincipale().getTitolo());
		titoloText.setMaxLength(6);
		titoloText.setWidth("5em");
		titoloText.setFocus(true);
		titoloText.setWidth(BOX_WIDTH);
		titoloText.setEnabled(enabled);
		table.setWidget(r, 5, titoloText);
		r++;

		// RagSoc
		table.setHTML(r, 0, "Cognome/Rag.soc."+ClientConstants.MANDATORY);
		table.setHTML(r, 1, anag1.getIndirizzoPrincipale().getCognomeRagioneSociale());
		table.setHTML(r, 3, anag2.getIndirizzoPrincipale().getCognomeRagioneSociale());
		ragSocText = new TextBox();
		ragSocText.setValue(anag3.getIndirizzoPrincipale().getCognomeRagioneSociale());
		ragSocText.setMaxLength(30);
		ragSocText.setWidth(BOX_WIDTH);
		ragSocText.setEnabled(enabled);
		table.setWidget(r, 5, ragSocText);
		r++;
		
		// nome
		table.setHTML(r, 0, "Nome");
		table.setHTML(r, 1, anag1.getIndirizzoPrincipale().getNome());
		table.setHTML(r, 3, anag2.getIndirizzoPrincipale().getNome());
		nomeText = new TextBox();
		nomeText.setValue(anag3.getIndirizzoPrincipale().getNome());
		nomeText.setMaxLength(25);
		nomeText.setWidth(BOX_WIDTH);
		nomeText.setEnabled(enabled);
		table.setWidget(r, 5, nomeText);
		r++;

		//Presso
		table.setHTML(r, 0, "Presso");
		table.setHTML(r, 1, anag1.getIndirizzoPrincipale().getPresso());
		table.setHTML(r, 3, anag2.getIndirizzoPrincipale().getPresso());
		pressoText = new TextBox();
		pressoText.setValue("");
		if (anag3.getIndirizzoPrincipale() != null) {
			pressoText.setValue(anag3.getIndirizzoPrincipale().getPresso());
		}
		pressoText.setWidth(BOX_WIDTH);
		pressoText.setMaxLength(28);
		pressoText.setEnabled(enabled);
		table.setWidget(r, 5, pressoText);
		r++;
		
		//Nazione
		table.setHTML(r, 0, "Nazione"+ClientConstants.MANDATORY);
		if (anag1.getIndirizzoPrincipale().getNazione() != null)
			table.setHTML(r, 1, anag1.getIndirizzoPrincipale().getNazione().getNomeNazione());
		if (anag2.getIndirizzoPrincipale().getNazione() != null)
			table.setHTML(r, 3, anag2.getIndirizzoPrincipale().getNazione().getNomeNazione());
		if (anag3.getIndirizzoPrincipale() != null) {
			nazioniList = new NazioniSelect(anag3.getIndirizzoPrincipale().getNazione().getId());
		} else {
			nazioniList = new NazioniSelect(AppConstants.DEFAULT_ID_NAZIONE_ITALIA);
		}
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
		nazioniList.setEnabled(enabled);
		table.setWidget(r, 5, nazioniList);
		r++;
		
		//Indirizzo
		table.setHTML(r, 0, "Indirizzo"+ClientConstants.MANDATORY);
		table.setHTML(r, 1, anag1.getIndirizzoPrincipale().getIndirizzo());
		table.setHTML(r, 3, anag2.getIndirizzoPrincipale().getIndirizzo());
		indirizzoText = new TextBox();
		indirizzoText.setValue("");
		if (anag3.getIndirizzoPrincipale() != null)
			indirizzoText.setValue(anag3.getIndirizzoPrincipale().getIndirizzo());
		indirizzoText.setWidth(BOX_WIDTH);
		indirizzoText.setMaxLength(36);
		indirizzoText.setEnabled(enabled);
		table.setWidget(r, 5, indirizzoText);
		r++;

		//Localita
		table.setHTML(r, 0, "Localit&agrave;"+ClientConstants.MANDATORY);
		String loc1 = "";
		if (anag1.getIndirizzoPrincipale().getLocalita() != null) loc1 += anag1.getIndirizzoPrincipale().getLocalita()+" ";
		if (anag1.getIndirizzoPrincipale().getProvincia() != null) loc1 += "("+anag1.getIndirizzoPrincipale().getProvincia()+") ";
		if (anag1.getIndirizzoPrincipale().getCap() !=null) loc1 += anag1.getIndirizzoPrincipale().getCap();
		table.setHTML(r, 1, loc1);
		String loc2 = "";
		if (anag2.getIndirizzoPrincipale().getLocalita() != null) loc2 += anag2.getIndirizzoPrincipale().getLocalita()+" ";
		if (anag2.getIndirizzoPrincipale().getProvincia() != null) loc2 += "("+anag2.getIndirizzoPrincipale().getProvincia()+") ";
		if (anag2.getIndirizzoPrincipale().getCap() !=null) loc2 += anag2.getIndirizzoPrincipale().getCap();
		table.setHTML(r, 3, loc2);
		if (anag3.getIndirizzoPrincipale() != null) {
			localitaCapPanel = new LocalitaCapPanel(
					anag3.getIndirizzoPrincipale().getLocalita(),
					anag3.getIndirizzoPrincipale().getProvincia(),
					anag3.getIndirizzoPrincipale().getCap());
		} else {
			localitaCapPanel = new LocalitaCapPanel("", "", "");
		}
		if (anag3.getIndirizzoPrincipale() != null) {
			if (anag3.getIndirizzoPrincipale().getNazione() != null) {
				localitaCapPanel.setIdNazione(anag3.getIndirizzoPrincipale().getNazione().getId());
			} else {
				localitaCapPanel.setIdNazione(AppConstants.DEFAULT_ID_NAZIONE_ITALIA);
			}
		} else {
			localitaCapPanel.setIdNazione(AppConstants.DEFAULT_ID_NAZIONE_ITALIA);
		}
		localitaCapPanel.setEnabled(enabled);
		//table.getFlexCellFormatter().setColSpan(r, 1, 5);
		table.setWidget(r, 5, localitaCapPanel);
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
		
		table.setHTML(r, 0, "<b>Dettagli personali</b>");
		table.getFlexCellFormatter().setColSpan(r, 0, 6);
		r++;
		
		//Sesso
		table.setHTML(r, 0, "Sesso");
		table.setHTML(r, 1, anag1.getSesso());
		table.setHTML(r, 3, anag2.getSesso());
		sessoList = new SessoSelect(anag3.getSesso());
		sessoList.setEnabled(enabled);
		table.setWidget(r, 5, sessoList);
		r++;
		
		//Cod Fiscale
		table.setHTML(r, 0, "Codice fisc.");
		table.setHTML(r, 1, anag1.getCodiceFiscale());
		table.setHTML(r, 3, anag2.getCodiceFiscale());
		String idNazione = AppConstants.DEFAULT_ID_NAZIONE_ITALIA;
		if (anag3.getIndirizzoPrincipale().getNazione().getId() != null)
			idNazione = anag3.getIndirizzoPrincipale().getNazione().getId();
		codFisText = new CodFiscText(idNazione);
		codFisText.setValue(anag3.getCodiceFiscale());
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
		table.setWidget(r, 5, codFisText);
		r++;
		
		//Partita IVA
		table.setHTML(r, 0, "Partita IVA");
		table.setHTML(r, 1, anag1.getPartitaIva());
		table.setHTML(r, 3, anag2.getPartitaIva());
		partIvaText = new PartitaIvaText(idNazione);
		partIvaText.setValue(anag3.getPartitaIva());
		partIvaText.setWidth(BOX_WIDTH);
		partIvaText.setEnabled(enabled);
		partIvaText.setMaxLength(16);
		table.setWidget(r, 5, partIvaText);
		r++;
		
		//Data nascita
		table.setHTML(r, 0, "Data nascita");
		if (anag1.getDataNascita() != null)
			table.setHTML(r, 1, ClientConstants.FORMAT_DAY.format(anag1.getDataNascita()));
		if (anag2.getDataNascita() != null)
			table.setHTML(r, 3, ClientConstants.FORMAT_DAY.format(anag2.getDataNascita()));
		nascitaDate = new DateOnlyBox();
		nascitaDate.setFormat(ClientConstants.BOX_FORMAT_DAY);
		nascitaDate.setValue(anag3.getDataNascita());
		nascitaDate.setEnabled(enabled);
		table.setWidget(r, 5, nascitaDate);
		r++;
		
		//Tel Casa
		table.setHTML(r, 0, "Telefono fisso");
		table.setHTML(r, 1, anag1.getTelCasa());
		table.setHTML(r, 3, anag2.getTelCasa());
		telCasaText = new TextBox();
		telCasaText.setValue(anag3.getTelCasa());
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
		table.setWidget(r, 5, telCasaText);
		r++;
		
		//Tel Mobile
		table.setHTML(r, 0, "Cellulare");
		table.setHTML(r, 1, anag1.getTelMobile());
		table.setHTML(r, 3, anag2.getTelMobile());
		telMobileText = new TextBox();
		telMobileText.setValue(anag3.getTelMobile());
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
		table.setWidget(r, 5, telMobileText);
		r++;
		
		//Email 1
		table.setHTML(r, 0, "Email principale");
		table.setHTML(r, 1, anag1.getEmailPrimaria());
		table.setHTML(r, 3, anag2.getEmailPrimaria());
		emailPrimText = new TextBox();
		emailPrimText.setValue(anag3.getEmailPrimaria());
		emailPrimText.setWidth(BOX_WIDTH);
		emailPrimText.setMaxLength(64);
		emailPrimText.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent arg0) {
				if (!ValueUtil.isValidEmail(emailPrimText.getValue()))
						UiSingleton.get().addInfo("Il formato dell'email primaria non è corretto");
			}
		});
		emailPrimText.setEnabled(enabled);
		table.setWidget(r, 5, emailPrimText);
		r++;
		
		//Email PEC
		table.setHTML(r, 0, "PEC");
		table.setHTML(r, 1, anag1.getEmailPec());
		table.setHTML(r, 3, anag2.getEmailPec());
		emailPecText = new TextBox();
		emailPecText.setValue(anag3.getEmailPec());
		emailPecText.setWidth(BOX_WIDTH);
		emailPecText.setMaxLength(64);
		emailPecText.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent arg0) {
				if (!ValueUtil.isValidEmail(emailPecText.getValue()))
						UiSingleton.get().addInfo("Il formato dell'PEC non è corretto");
			}
		});
		emailPecText.setEnabled(enabled);
		table.setWidget(r, 5, emailPecText);
		r++;

		//Codice Destinatario
		table.setHTML(r, 0, "Codice dest./intermediario");
		table.setHTML(r, 1, anag1.getCodiceDestinatario());
		table.setHTML(r, 3, anag2.getCodiceDestinatario());
		codiceDestText = new TextBox();
		codiceDestText.setValue(anag3.getCodiceDestinatario());
		codiceDestText.setWidth(BOX_WIDTH);
		codiceDestText.setEnabled(enabled);
		codiceDestText.setMaxLength(8);
		table.setWidget(r, 5, codiceDestText);
		r++;
		
		//Pubblica amministrazione
		table.setHTML(r, 0, "Pubblica amministrazione");
		String pa1 = ClientConstants.ICON_UNCHECKED;
		String pa2 = ClientConstants.ICON_UNCHECKED;
		if (anag1.getPa()) pa1 = ClientConstants.ICON_CHECKED;
		if (anag2.getPa()) pa2 = ClientConstants.ICON_CHECKED;
		table.setHTML(r, 1, pa1);	
		table.setHTML(r, 3, pa2);
		paCheck = new CheckBox();
		paCheck.setValue(anag3.getPa());
		paCheck.setEnabled(isOperator);
		table.setWidget(r, 5, paCheck);
		r++;
		
		//CUF
		table.setHTML(r, 0, "CUF");
		table.setHTML(r, 1, anag1.getCuf());
		table.setHTML(r, 3, anag2.getCuf());
		cufText = new TextBox();
		cufText.setValue(anag3.getCuf());
		cufText.setWidth(BOX_WIDTH);
		cufText.setEnabled(enabled);
		cufText.setMaxLength(8);
		table.setWidget(r, 5, cufText);
		r++;
		
		//Professione
		table.setHTML(r, 0, "Professione");
		if (anag1.getProfessione() != null) {
			ProfessioniSelect professioniList1 =
					new ProfessioniSelect(anag1.getProfessione().getId());
			professioniList1.setEnabled(false);
			table.setWidget(r, 1, professioniList1);
		}
		if (anag2.getProfessione() != null) {
			ProfessioniSelect professioniList2 =
					new ProfessioniSelect(anag2.getProfessione().getId());
			professioniList2.setEnabled(false);
			table.setWidget(r, 3, professioniList2);
		}
		if (anag3.getProfessione() == null) {
			professioniList = new ProfessioniSelect(null);
		} else {
			professioniList = new ProfessioniSelect(anag3.getProfessione().getId());
		}
		professioniList.setEnabled(enabled);
		table.setWidget(r, 5, professioniList);
		r++;
		
		//Titolo di studio
		table.setHTML(r, 0, "Titolo di studio");
		if (anag1.getTitoloStudio() != null) {
			TitoliStudioSelect titoliStudioList1 =
					new TitoliStudioSelect(anag1.getTitoloStudio().getId());
			titoliStudioList1.setEnabled(false);
			table.setWidget(r, 1, titoliStudioList1);
		}
		if (anag2.getTitoloStudio() != null) {
			TitoliStudioSelect titoliStudioList2 =
					new TitoliStudioSelect(anag2.getTitoloStudio().getId());
			titoliStudioList2.setEnabled(false);
			table.setWidget(r, 3, titoliStudioList2);
		}
		if (anag3.getTitoloStudio() == null) {
			titoliStudioList = new TitoliStudioSelect(null);
		} else {
			titoliStudioList = new TitoliStudioSelect(anag3.getTitoloStudio().getId());
		}
		titoliStudioList.setEnabled(enabled);
		table.setWidget(r, 5, titoliStudioList);
		r++;
		
		//Note
		table.setHTML(r, 0, "Note");
		table.setHTML(r, 1, anag1.getNote());
		table.setHTML(r, 3, anag2.getNote());
		noteArea = new NoteArea(2048);
		noteArea.setValue(anag3.getNote());
		noteArea.setHeight("3em");
		noteArea.setEnabled(enabled);
		table.setWidget(r, 5, noteArea);
		r++;
		
		//GiuntiCardClub
		table.setHTML(r, 0, "Giunti Card Club");
		table.setHTML(r, 1, anag1.getGiuntiCardClub());
		table.setHTML(r, 3, anag2.getGiuntiCardClub());
		giuntiCardText = new TextBox();
		giuntiCardText.setValue(anag3.getGiuntiCardClub());
		giuntiCardText.setWidth(BOX_WIDTH);
		giuntiCardText.setEnabled(enabled);
		giuntiCardText.setMaxLength(16);
		table.setWidget(r, 5, giuntiCardText);
		r++;
		
		//SAP
		table.setHTML(r, 0, "Codice SAP");
		table.setHTML(r, 1, anag1.getCodiceSap());
		table.setHTML(r, 3, anag2.getCodiceSap());
		sapText = new TextBox();
		sapText.setValue(anag3.getCodiceSap());
		sapText.setWidth(BOX_WIDTH);
		sapText.setEnabled(enabled);
		sapText.setMaxLength(64);
		table.setWidget(r, 5, sapText);
		r++;
		
		//Privacy TOS
		table.setHTML(r, 0, "Consenso termini d'uso");
		String consTos1 = ClientConstants.ICON_UNCHECKED;
		String consTos2 = ClientConstants.ICON_UNCHECKED;
		if (anag1.getConsensoTos()) consTos1 = ClientConstants.ICON_CHECKED;
		if (anag2.getConsensoTos()) consTos2 = ClientConstants.ICON_CHECKED;
		table.setHTML(r, 1, consTos1);	
		table.setHTML(r, 3, consTos2);
		consentTos = new CheckBox();
		consentTos.setValue(anag3.getConsensoTos());
		consentTos.setEnabled(isOperator);
		table.setWidget(r, 5, consentTos);
		r++;
		//Privacy marketing
		table.setHTML(r, 0, "Privacy marketing");
		String consMkt1 = ClientConstants.ICON_UNCHECKED;
		String consMkt2 = ClientConstants.ICON_UNCHECKED;
		if (anag1.getConsensoMarketing()) consMkt1 = ClientConstants.ICON_CHECKED;
		if (anag2.getConsensoMarketing()) consMkt2 = ClientConstants.ICON_CHECKED;
		table.setHTML(r, 1, consMkt1);	
		table.setHTML(r, 3, consMkt2);
		consentMarketing = new CheckBox();
		consentMarketing.setValue(anag3.getConsensoMarketing());
		consentMarketing.setEnabled(isOperator);
		table.setWidget(r, 5, consentMarketing);
		r++;
		//Privacy profilazione
		table.setHTML(r, 0, "Privacy profilazione");
		String consPrf1 = ClientConstants.ICON_UNCHECKED;
		String consPrf2 = ClientConstants.ICON_UNCHECKED;
		if (anag1.getConsensoProfilazione()) consPrf1 = ClientConstants.ICON_CHECKED;
		if (anag2.getConsensoProfilazione()) consPrf2 = ClientConstants.ICON_CHECKED;
		table.setHTML(r, 1, consPrf1);	
		table.setHTML(r, 3, consPrf2);
		consentProfilazione = new CheckBox();
		consentProfilazione.setValue(anag3.getConsensoProfilazione());
		consentProfilazione.setEnabled(isOperator);
		table.setWidget(r, 5, consentProfilazione);
		r++;
				
		//Data Aggiornamento Consenso
		table.setHTML(r, 0, "Aggiornamento consenso");
		if (anag1.getDataAggiornamentoConsenso() != null) 
			table.setHTML(r, 1, ClientConstants.FORMAT_DAY.format(anag1.getDataAggiornamentoConsenso()));
		if (anag2.getDataAggiornamentoConsenso() != null) 
			table.setHTML(r, 3, ClientConstants.FORMAT_DAY.format(anag2.getDataAggiornamentoConsenso()));
		consentDate = new DateOnlyBox();
		consentDate.setFormat(ClientConstants.BOX_FORMAT_DAY);
		consentDate.setWidth(BOX_WIDTH);
		consentDate.setValue(anag3.getDataAggiornamentoConsenso());
		consentDate.setEnabled(isOperator);
		table.setWidget(r, 5, consentDate);
		r++;
		
		table.setHTML(r, 0, "<b>Indirizzo di fatturazione</b>");
		table.getFlexCellFormatter().setColSpan(r, 0, 6);
		r++;
		
		//TitoloFatt
		table.setHTML(r, 0, "Titolo");
		table.setHTML(r, 1, anag1.getIndirizzoFatturazione().getTitolo());
		table.setHTML(r, 3, anag2.getIndirizzoFatturazione().getTitolo());
		titoloFattText = new TextBox();
		titoloFattText.setValue(anag3.getIndirizzoFatturazione().getTitolo());
		titoloFattText.setMaxLength(6);
		titoloFattText.setWidth("5em");
		titoloFattText.setFocus(true);
		titoloFattText.setWidth(BOX_WIDTH);
		titoloFattText.setEnabled(enabled);
		table.setWidget(r, 5, titoloFattText);
		r++;
		
		// RagSocFatt
		table.setHTML(r, 0, "Cognome/Rag.soc.");
		table.setHTML(r, 1, anag1.getIndirizzoFatturazione().getCognomeRagioneSociale());
		table.setHTML(r, 3, anag2.getIndirizzoFatturazione().getCognomeRagioneSociale());
		ragSocFattText = new TextBox();
		ragSocFattText.setValue(anag3.getIndirizzoFatturazione().getCognomeRagioneSociale());
		ragSocFattText.setMaxLength(30);
		ragSocFattText.setWidth(BOX_WIDTH);
		ragSocFattText.setEnabled(enabled);
		table.setWidget(r, 5, ragSocFattText);
		r++;
		
		// nomeFatt
		table.setHTML(r, 0, "Nome");
		table.setHTML(r, 1, anag1.getIndirizzoFatturazione().getNome());
		table.setHTML(r, 3, anag2.getIndirizzoFatturazione().getNome());
		nomeFattText = new TextBox();
		nomeFattText.setValue(anag3.getIndirizzoFatturazione().getNome());
		nomeFattText.setMaxLength(25);
		nomeFattText.setWidth(BOX_WIDTH);
		nomeFattText.setEnabled(enabled);
		table.setWidget(r, 5, nomeFattText);
		r++;
		
		//PressoFatt
		table.setHTML(r, 0, "Presso");
		table.setHTML(r, 1, anag1.getIndirizzoFatturazione().getPresso());
		table.setHTML(r, 3, anag2.getIndirizzoFatturazione().getPresso());
		pressoFattText = new TextBox();
		pressoFattText.setValue(anag3.getIndirizzoFatturazione().getPresso());
		pressoFattText.setWidth(BOX_WIDTH);
		pressoFattText.setMaxLength(30);
		pressoFattText.setEnabled(isOperator);
		table.setWidget(r, 5, pressoFattText);
		r++;
		
		//NazioneFatt
		table.setHTML(r, 0, "Nazione");
		if (anag1.getIndirizzoFatturazione().getNazione() != null) 
			table.setHTML(r, 1, anag1.getIndirizzoFatturazione().getNazione().getNomeNazione());
		if (anag2.getIndirizzoFatturazione().getNazione() != null)
			table.setHTML(r, 3, anag2.getIndirizzoFatturazione().getNazione().getNomeNazione());
		nazioniFattList = new NazioniSelect(anag3.getIndirizzoFatturazione().getNazione().getId());
		nazioniFattList.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				String idNazione = nazioniFattList.getSelectedValueString();
				if (idNazione != null) localitaFattCapPanel.setIdNazione(idNazione);
			}
		});
		nazioniFattList.setEnabled(isOperator);
		table.setWidget(r, 5, nazioniFattList);
		r++;
		
		//IndirizzoFatt
		table.setHTML(r, 0, "Indirizzo");
		table.setHTML(r, 1, anag1.getIndirizzoFatturazione().getIndirizzo());
		table.setHTML(r, 3, anag2.getIndirizzoFatturazione().getIndirizzo());
		indirizzoFattText = new TextBox();
		indirizzoFattText.setValue(anag3.getIndirizzoFatturazione().getIndirizzo());
		indirizzoFattText.setMaxLength(60);
		indirizzoFattText.setWidth(BOX_WIDTH);
		indirizzoFattText.setEnabled(isOperator);
		table.setWidget(r, 5, indirizzoFattText);
		r++;
		
		//LocalitaFatt
		table.setHTML(r, 0, "Localit&agrave;");
		String locf1 = "";
		if (anag1.getIndirizzoFatturazione().getLocalita() != null) locf1 += anag1.getIndirizzoFatturazione().getLocalita()+" ";
		if (anag1.getIndirizzoFatturazione().getProvincia() != null) locf1 += "("+anag1.getIndirizzoFatturazione().getProvincia()+") ";
		if (anag1.getIndirizzoFatturazione().getCap() !=null) locf1 += anag1.getIndirizzoFatturazione().getCap();
		table.setHTML(r, 1, locf1);
		String locf2 = "";
		if (anag2.getIndirizzoFatturazione().getLocalita() != null) locf2 += anag2.getIndirizzoFatturazione().getLocalita()+" ";
		if (anag2.getIndirizzoFatturazione().getProvincia() != null) locf2 += "("+anag2.getIndirizzoFatturazione().getProvincia()+") ";
		if (anag2.getIndirizzoFatturazione().getCap() !=null) locf2 += anag2.getIndirizzoFatturazione().getCap();
		table.setHTML(r, 3, locf2);
		if (anag3.getIndirizzoFatturazione() != null) {
			localitaFattCapPanel = new LocalitaCapPanel(
					anag3.getIndirizzoFatturazione().getLocalita(),
					anag3.getIndirizzoFatturazione().getProvincia(),
					anag3.getIndirizzoFatturazione().getCap());
		} else {
			localitaFattCapPanel = new LocalitaCapPanel("", "", "");
		}
		if (anag3.getIndirizzoFatturazione() != null) {
			localitaFattCapPanel.setIdNazione(anag3.getIndirizzoFatturazione().getNazione().getId());
		} else {
			localitaFattCapPanel.setIdNazione(AppConstants.DEFAULT_ID_NAZIONE_ITALIA);
		}
		localitaFattCapPanel.setEnabled(enabled);
		//table.getFlexCellFormatter().setColSpan(r, 1, 5);
		table.setWidget(r, 5, localitaFattCapPanel);
		//Verifica localita
		if (localitaFattCapPanel.getLocalitaCap().length() > 0 &&
				localitaFattCapPanel.getLocalitaName().length() > 0 &&
				localitaFattCapPanel.getLocalitaProv() != null) {
			localitaFattCapPanel.verifyStoredLocalita();
			if (localitaFattCapPanel.isEmpty()) {
				UiSingleton.get().addWarning("La localita' di fatturazione e' errata o incompleta");
			}
		}
		r++;
		
		//PANNELLO BOTTONI
		HorizontalPanel buttonPanel = getButtonPanel();
		table.setWidget(r,0,buttonPanel);
		table.getFlexCellFormatter().setColSpan(r, 0, 6);//Span su 5 colonne
		r++;
	}
	
	private HorizontalPanel getButtonPanel() {
		HorizontalPanel buttonPanel = new HorizontalPanel();
		// Bottone SALVA
		Button mergeButton = new Button(ClientConstants.ICON_MERGE+"&nbsp;Unisci irreversibilmente");
		mergeButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				try {
					saveData();
				} catch (ValidationException e) {
					UiSingleton.get().addError(e);
				} catch (BusinessException e) {
					UiSingleton.get().addError(e);
				}
			}
		});
		mergeButton.setEnabled(isOperator);
		buttonPanel.add(mergeButton);
		// Separator
		buttonPanel.add(new Image("img/separator.gif"));
		// Bottone SEPARA
		Button splitButton = new Button(ClientConstants.ICON_SPLIT+"&nbsp;Annulla: separa le anagrafiche");
		splitButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				try {
					splitData();
				} catch (ValidationException e) {
					UiSingleton.get().addError(e);
				} catch (BusinessException e) {
					UiSingleton.get().addError(e);
				}
			}
		});
		splitButton.setEnabled(isOperator);
		buttonPanel.add(splitButton);
		
		return buttonPanel;
	}
	
	
	/***** ASYNC SERVICES *****/
	
	
	private void loadAnagrafiche() {
		AsyncCallback<List<Anagrafiche>> callback = new AsyncCallback<List<Anagrafiche>>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(List<Anagrafiche> anaList) {
				anag1 = anaList.get(0);
				anag2 = anaList.get(1);
				anag3 = anaList.get(2);
				WaitSingleton.get().stop();
				if ((anag2 == null) && (anag3 == null)) {
					UriParameters params = new UriParameters();
					params.add(AppConstants.PARAM_ID, anag1.getId());
					params.triggerUri(UriManager.ANAGRAFICA);
				} else {
					drawAnagrafiche();
				} 				
			}
		};
		WaitSingleton.get().start();
		if (idOptionalSecondAnagrafica == null) {
			anagraficheService.findMergeArray(idAnagrafica, callback);
		} else {
			anagraficheService.findMergeArray(idAnagrafica, idOptionalSecondAnagrafica, callback);
		}
	}
	
	private void saveData() throws ValidationException, BusinessException {
		AsyncCallback<Anagrafiche> callback = new AsyncCallback<Anagrafiche>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(Anagrafiche result) {			
				idAnagrafica = result.getId();
				//loadAnagrafiche();
				UiSingleton.get().addInfo(AppConstants.MSG_SAVE_OK);
				WaitSingleton.get().stop();
				UriParameters params = new UriParameters();
				params.add(AppConstants.PARAM_ID, idAnagrafica);
				params.triggerUri(UriManager.ANAGRAFICA);
			}
		};
		//scrittura
		Date today = DateUtil.now();
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
		if (anag3.getIndirizzoPrincipale() == null) 
			anag3.setIndirizzoPrincipale(new Indirizzi());
		if (anag3.getIndirizzoFatturazione() == null)
			anag3.setIndirizzoFatturazione(new Indirizzi());
		anag3.setIdProfessioneT(professioniList.getSelectedValueString());
		anag3.setIdTitoloStudioT(titoliStudioList.getSelectedValueString());
		anag3.setSesso(sessoList.getSelectedValueString());
		anag3.setCodiceFiscale(codFisText.getValue().toUpperCase().trim());
		anag3.setPartitaIva(partIvaText.getValue().toUpperCase().trim());
		anag3.setCodiceDestinatario(codiceDestText.getValue().trim());
		anag3.setPa(paCheck.getValue());
		anag3.setCuf(cufText.getValue().trim());
		anag3.setDataNascita(nascitaDate.getValue());
		anag3.setTelCasa(telCasaText.getValue().trim());
		anag3.setTelMobile(telMobileText.getValue().trim());
		anag3.setEmailPrimaria(emailPrimText.getValue().toLowerCase().trim());
		anag3.setEmailPec(emailPecText.getValue().toLowerCase().trim());
		anag3.setIdTipoAnagrafica(tipoAnagraficaList.getSelectedValueString());
		anag3.setNote(noteArea.getValue().trim());
		anag3.setDataModifica(today);
		anag3.setIdUtente(AuthSingleton.get().getUtente().getId());
		
		anag3.getIndirizzoPrincipale().setTitolo(titoloText.getValue().trim());
		anag3.getIndirizzoPrincipale().setCognomeRagioneSociale(ragSocText.getValue().trim());
		anag3.getIndirizzoPrincipale().setNome(nomeText.getValue().trim());
		anag3.getIndirizzoPrincipale().setCap(localitaCapPanel.getLocalitaCap().trim());
		anag3.getIndirizzoPrincipale().setIndirizzo(indirizzoText.getValue().trim());
		anag3.getIndirizzoPrincipale().setLocalita(localitaCapPanel.getLocalitaName().trim());
		anag3.getIndirizzoPrincipale().setIdNazioneT(nazioniList.getSelectedValueString());
		anag3.getIndirizzoPrincipale().setPresso(pressoText.getValue().trim());
		anag3.getIndirizzoPrincipale().setProvincia(localitaCapPanel.getLocalitaProv());
		anag3.getIndirizzoPrincipale().setDataModifica(today);
		anag3.getIndirizzoPrincipale().setIdUtente(AuthSingleton.get().getUtente().getId());
		
		localitaFattCapPanel.setIdNazione(nazioniFattList.getSelectedValueString());
		anag3.getIndirizzoFatturazione().setTitolo(titoloFattText.getValue().trim());
		anag3.getIndirizzoFatturazione().setCognomeRagioneSociale(ragSocFattText.getValue().trim());
		anag3.getIndirizzoFatturazione().setNome(nomeFattText.getValue().trim());
		anag3.getIndirizzoFatturazione().setIdNazioneT(nazioniFattList.getSelectedValueString());
		anag3.getIndirizzoFatturazione().setCap(localitaFattCapPanel.getLocalitaCap().trim());
		anag3.getIndirizzoFatturazione().setIndirizzo(indirizzoFattText.getValue().trim());
		anag3.getIndirizzoFatturazione().setLocalita(localitaFattCapPanel.getLocalitaName().trim());
		anag3.getIndirizzoFatturazione().setPresso(pressoFattText.getValue().trim());
		anag3.getIndirizzoFatturazione().setProvincia(localitaFattCapPanel.getLocalitaProv());
		anag3.getIndirizzoFatturazione().setDataModifica(today);
		anag3.getIndirizzoFatturazione().setIdUtente(AuthSingleton.get().getUtente().getId());
		
		anag3.setGiuntiCardClub(giuntiCardText.getValue().trim());
		anag3.setCodiceSap(sapText.getValue().trim());
		anag3.setConsensoTos(consentTos.getValue());
		anag3.setConsensoMarketing(consentMarketing.getValue());
		anag3.setConsensoProfilazione(consentProfilazione.getValue());
		anag3.setDataAggiornamentoConsenso(consentDate.getValue());

		WaitSingleton.get().start(WaitSingleton.MODE_LONG);
		anagraficheService.merge(anag1, anag2, anag3, callback);
	}
	
	private void splitData() throws ValidationException, BusinessException {
		AsyncCallback<Anagrafiche> callback = new AsyncCallback<Anagrafiche>() {
			@Override
			public void onFailure(Throwable caught) {
				if (caught instanceof ValidationException) {
					UiSingleton.get().addWarning(caught.getMessage());
				} else {
					UiSingleton.get().addError(caught);
				}
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(Anagrafiche anag2) {			
				idAnagrafica = anag2.getId();
				//loadAnagrafiche();
				UiSingleton.get().addInfo(AppConstants.MSG_SAVE_OK);
				WaitSingleton.get().stop();
				UriParameters params = new UriParameters();
				params.add(AppConstants.PARAM_ID, idAnagrafica);
				params.triggerUri(UriManager.ANAGRAFICHE_MERGE);
			}
		};
		WaitSingleton.get().start(WaitSingleton.MODE_LONG);
		anagraficheService.splitMerge(anag1, anag2, callback);
	}
}
