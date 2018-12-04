package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.CookieSingleton;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.UriManager;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.AbbonamentiService;
import it.giunti.apg.client.services.AbbonamentiServiceAsync;
import it.giunti.apg.client.services.FascicoliService;
import it.giunti.apg.client.services.FascicoliServiceAsync;
import it.giunti.apg.client.widgets.AnagraficheSearchBox;
import it.giunti.apg.client.widgets.ArticoliListiniPanel;
import it.giunti.apg.client.widgets.BloccatoCheckBox;
import it.giunti.apg.client.widgets.DateOnlyBox;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.client.widgets.NoteArea;
import it.giunti.apg.client.widgets.OpzioniIstanzaPanel;
import it.giunti.apg.client.widgets.PagatoCheckBox;
import it.giunti.apg.client.widgets.SubPanel;
import it.giunti.apg.client.widgets.TitlePanel;
import it.giunti.apg.client.widgets.VersioningPanel;
import it.giunti.apg.client.widgets.select.AdesioniSelect;
import it.giunti.apg.client.widgets.select.FascicoliSelect;
import it.giunti.apg.client.widgets.select.ListiniSelect;
import it.giunti.apg.client.widgets.select.PeriodiciSelect;
import it.giunti.apg.client.widgets.select.TipiDisdettaSelect;
import it.giunti.apg.client.widgets.select.TipiPagamentoSelect;
import it.giunti.apg.client.widgets.select.TipiSpedizioneSelect;
import it.giunti.apg.client.widgets.tables.DataModel;
import it.giunti.apg.client.widgets.tables.EvasioniArticoliTable;
import it.giunti.apg.client.widgets.tables.EvasioniArticoliTable.EvasioniArticoliByIstanzaModel;
import it.giunti.apg.client.widgets.tables.EvasioniComunicazioniTable;
import it.giunti.apg.client.widgets.tables.EvasioniFascicoliTable;
import it.giunti.apg.client.widgets.tables.FattureTable;
import it.giunti.apg.client.widgets.tables.IstanzeAbbonamentiTable;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.IstanzeStatusUtil;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.EvasioniComunicazioni;
import it.giunti.apg.shared.model.EvasioniFascicoli;
import it.giunti.apg.shared.model.Fatture;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Pagamenti;
import it.giunti.apg.shared.model.Ruoli;
import it.giunti.apg.shared.model.Utenti;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AbbonamentoFrame extends FramePanel
		implements IRefreshable, IAuthenticatedWidget {
	
	private final AbbonamentiServiceAsync abbonamentiService = GWT.create(AbbonamentiService.class);
	private final FascicoliServiceAsync fascicoliService = GWT.create(FascicoliService.class);
	
	//private static final String BOX_WIDTH = "20em";
	
	private static final String TITLE_ABBONAMENTO = "Abbonamento";
	private static final String TITLE_FATTURE = "Fatture correlate";// <img src='img/icon22/emblem-money.png' style='vertical-align:middle' />";
	private static final String TITLE_ARTICOLI = "Articoli";// <img src='img/icon22/mail-notification.png' style='vertical-align:middle' />";
	private static final String TITLE_FASCICOLI = "Fascicoli";// <img src='img/icon22/gnome-colors-applications-office.png' style='vertical-align:middle' />";
	private static final String TITLE_COMUNICAZIONI = "Comunicazioni";// <img src='img/icon22/mail-notification.png' style='vertical-align:middle' />";
	private static final String TITLE_STORICO = "Storico";// <img src='img/icon22/text-x-generic.png' style='vertical-align:middle' />";
	private static final String NUMERI_EMPTY_LABEL = "<i>[da calcolare]</i>";
	
	private FramePanel stack = null;
	
	private Integer idIstanza = null;
	private Integer idAnagrafica = null;
	private IstanzeAbbonamenti item = null;
	private Ruoli userRole = null;
	private boolean isSuper = false;
	private boolean isEditor = false;
	private boolean isAdmin = false;
	private boolean isOperator = false;
	private long startDt;
	private long finishDt;
	private Utenti utente;
	
	private VerticalPanel panelAbb = null;
	private AnagraficheSearchBox abbonatoSearchBox = null;
	private AnagraficheSearchBox paganteSearchBox = null;
	private AnagraficheSearchBox promotoreSearchBox = null;
	private HTML statusWarningHtml = null;
	private TextBox codAbboText = null;
	private PeriodiciSelect periodiciList = null;
	private TextBox copieText = null;
	private ListiniSelect listiniList = null;
	private OpzioniIstanzaPanel opzioniIstanzaPanel = null;
	private ArticoliListiniPanel artListPanel = null;
	private FascicoliSelect fasInizioList = null;
	private FascicoliSelect fasFineList = null;
	private DateOnlyBox disdettaDate = null;
	private TipiDisdettaSelect tipoDisdettaList = null;
	private HTML numeriHtml = null;
	//private AdesioniSuggestBox adesioniSuggest = null;
	private AdesioniSelect adesioniList = null;
	private NoteArea noteArea = null;
	private PagatoCheckBox pagatoCheck = null;
	private CheckBox fatturaDifferitaCheck = null;
	private TitlePanel fatturaPanel = null;
	private TextBox fatturaNumText = null;
	private DateOnlyBox fatturaDate = null;
	private TextBox fatturaImportoText = null;
	private CheckBox fatturaPagataCheck = null;
	private BloccatoCheckBox bloccatoCheck = null;
	private TipiSpedizioneSelect tipoSpedizioneList = null;
	private ButtonPanel buttonPanel = null;
	
	private TextBox initialPaymentAmountText = null;
	private DateOnlyBox initialPaymentDate = null;
	private TipiPagamentoSelect initialPaymentTypeList = null;
	private TextBox initialPaymentNoteText = null;
	
	private SubPanel panelFatt = null;
	private HorizontalPanel pagButtonHolder = null;
	private HorizontalPanel pagWarningHolder = null;
	//private PagamentiTable pagTable = null;
	//private CreditiTable credTable = null;
	private FattureTable fattTable = null;
	
	private SubPanel panelEvasioniArticoli = null;
	private EvasioniArticoliTable edTable = null;
	
	private SubPanel panelEvasioniFascicoli = null;
	private EvasioniFascicoliTable efTable = null;
	
	private SubPanel panelComunicazioni = null;
	private EvasioniComunicazioniTable ecTable = null;
	
	private SubPanel panelStorico = null;
	
	// Gerarchia aggiornamento onChange: Periodico > FascicoloInizio > Listino > Opzioni+FascicoloFine
	
	// METHODS
	
	public AbbonamentoFrame(UriParameters params) {
		super();
		if (params == null) {
			params = new UriParameters();
		}
		idAnagrafica = params.getIntValue(AppConstants.PARAM_ID_ANAGRAFICA);
		Integer value = params.getIntValue(AppConstants.PARAM_ID);
		if (value != null) {
			idIstanza = value;
			AuthSingleton.get().queueForAuthentication(this);
		}
	}
	
	@Override
	public void onSuccessfulAuthentication(Utenti utente) {
		this.utente = utente;
		init(utente);
	}
	
	private void init(Utenti utente) {
		// Editing rights
		userRole = utente.getRuolo();
		isOperator = (userRole.getId() >= AppConstants.RUOLO_OPERATOR);
		isEditor = (userRole.getId() >= AppConstants.RUOLO_EDITOR);
		isAdmin = (userRole.getId() >= AppConstants.RUOLO_ADMIN);
		isSuper = (userRole.getId() >= AppConstants.RUOLO_SUPER);
		// UI
		if (isOperator) {
			refresh();
		}
	}
	
	@Override
	public void refresh() {
		this.clear();
		stack = new FramePanel();
		panelAbb = new VerticalPanel();
		stack.add(panelAbb, TITLE_ABBONAMENTO);
		this.add(stack);
		loadIstanzaAbbonamento();
	}

	
	/** This method empties the ContentPanel and redraws the 'item' data
	 * @param item
	 */
	private void drawAbbonamento() {
		//Periodici rights
		if (utente.getPeriodiciUidRestriction() != null) {
			if (utente.getPeriodiciUidRestriction().length() > 0 &&
				!utente.getPeriodiciUidRestriction().contains(
					item.getAbbonamento().getPeriodico().getUid())) {
				//PeriodiciUidRestriction deve essere popolato e non contenere la UID periodico
				new UriParameters().triggerUri(UriManager.ERRORE);
			}
		}
		//DRAW
		boolean isNewIstanza = (item.getId() == null);
		boolean isEditable = isNewIstanza;
		setBrowserWindowTitle(item.getAbbonamento().getCodiceAbbonamento());
		final IstanzeAbbonamenti item = this.item;
		//boolean isTransient = (item.getId() == null);
		long fasStartTime = item.getFascicoloInizio().getDataInizio().getTime();
		startDt = fasStartTime - AppConstants.MONTH * 13;
		finishDt = fasStartTime + AppConstants.MONTH * 61;
		// clean form
		panelAbb.clear();
		FlexTable table = new FlexTable();
		panelAbb.add(table);
		int r=0;
		
		//Necessita verifica?
		if (item.getNecessitaVerifica()) {
			HTML html = new HTML(ClientConstants.ICON_HAND_RIGHT+" <b>Questo abbonamento deve essere verificato</b>");
			html.setStyleName("message-info");
			table.setWidget(r, 0, html);
			table.getFlexCellFormatter().setColSpan(r, 0, 5);
			r++;
		}
		
		//Caption con anagrafiche
		VerticalPanel anagPanel = new VerticalPanel();
		anagPanel.setStyleName("grey-panel");
		anagPanel.setWidth("100%");
		abbonatoSearchBox = new AnagraficheSearchBox("Abbonato"+ClientConstants.MANDATORY,
				item.getAbbonato(),
				isOperator);
		anagPanel.add(abbonatoSearchBox);
		paganteSearchBox = new AnagraficheSearchBox("Pagante",
				item.getPagante(),
				isOperator);
		anagPanel.add(paganteSearchBox);
		promotoreSearchBox = new AnagraficheSearchBox("Promotore",
				item.getPromotore(),
				isOperator);
		anagPanel.add(promotoreSearchBox);
		table.setWidget(r, 0, anagPanel);
		table.getFlexCellFormatter().setColSpan(r, 0, 5);
		r++;
		
		statusWarningHtml = new HTML("");
		table.setWidget(r, 0, statusWarningHtml);
		table.getFlexCellFormatter().setColSpan(r, 0, 5);
		loadStatusMessage(item);
		r++;
		
		// Periodico
		table.setHTML(r, 0, "Periodico");
		if (isOperator && isNewIstanza) {
			periodiciList = new PeriodiciSelect(item.getAbbonamento().getPeriodico().getId(), 
					item.getFascicoloInizio().getDataInizio(), false, true, utente);
			periodiciList.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					onPeriodicoChange();
				}
			});
			periodiciList.setEnabled(isOperator && (item.getAbbonamento().getId() == null));//solo se nuovo
			table.setWidget(r, 1, periodiciList);
		} else {
			table.setHTML(r, 1, "<b>"+item.getAbbonamento().getPeriodico().getNome()+
				" ["+item.getAbbonamento().getPeriodico().getUid()+"]</b>");
		}
		// Codice
		table.setHTML(r, 3, "Codice abbonamento"+ClientConstants.MANDATORY);
		String codIstanza = (item.getId() != null) ? codIstanza = " &nbsp;<i>UID ["+item.getId()+"]</i>" : "";
		if (isAdmin && item.getId() != null) {
			codAbboText = new TextBox();
			codAbboText.setValue(item.getAbbonamento().getCodiceAbbonamento());
			codAbboText.setEnabled(true);
			codAbboText.setMaxLength(7);
			codAbboText.setFocus(true);
			codAbboText.setWidth("8em");
			codAbboText.addBlurHandler(new BlurHandler() {
				@Override
				public void onBlur(BlurEvent arg0) {
					verifyCodiceAbbonamento(codAbboText.getValue());
				}
			});
			HorizontalPanel codicePanel = new HorizontalPanel();
			codicePanel.add(codAbboText);
			codicePanel.add(new InlineHTML(codIstanza));
			table.setWidget(r, 4, codicePanel);
		} else {
			String descr = (item.getId() != null) ? 
					descr = item.getAbbonamento().getCodiceAbbonamento() : "[generato automaticamente]";
			table.setHTML(r, 4, "<b>"+descr+"</b> "+codIstanza);
		}
		r++;

		// TipoAbb
		table.setHTML(r,0, "Tipo abbonamento");
		listiniList = new ListiniSelect(
					item.getListino().getId(),
					item.getAbbonamento().getPeriodico().getId(),
					item.getFascicoloInizio().getDataInizio(),
					false, true, false, isEditable);
		listiniList.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				onListinoChange();
			}
		});
		table.setWidget(r, 1, listiniList);
		// Copie
		table.setHTML(r, 3, "Copie"+ClientConstants.MANDATORY);
		copieText = new TextBox();
		copieText.setValue(item.getCopie()+"");
		copieText.setEnabled(isOperator && isEditable);
		copieText.setMaxLength(3);
		copieText.setWidth("3em");
		table.setWidget(r, 4, copieText);
		r++;
		
		// FascicoloInizio
		table.setHTML(r, 0, "Inizio");
		fasInizioList = new FascicoliSelect(item.getFascicoloInizio().getId(),
				item.getAbbonamento().getPeriodico().getId(),
				startDt, finishDt, false, false, true, false, false);
		fasInizioList.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				onFascicoloInizioChange();
			}
		});
		if (isOperator) {
			table.setWidget(r, 1, fasInizioList);
		} else {
			table.setHTML(r, 1, "<b>"+item.getFascicoloInizio().getTitoloNumero() + "&nbsp;(" +
					item.getFascicoloInizio().getDataCop() + " " +
					ClientConstants.FORMAT_YEAR.format(item.getFascicoloInizio().getDataInizio())+")</b>");
		}
		// FacicoloFine
		table.setHTML(r, 3, "Fine");
		fasFineList = new FascicoliSelect(item.getFascicoloFine().getId(),
				item.getAbbonamento().getPeriodico().getId(),
				startDt, finishDt, false, false, true, false, false);
		fasFineList.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				onFascicoloFineChange();
			}
		});
		if (isOperator) {
			table.setWidget(r, 4, fasFineList);
		} else {
			table.setHTML(r, 4, "<b>"+item.getFascicoloFine().getTitoloNumero() + "&nbsp;(" +
					item.getFascicoloFine().getDataCop() + " " +
					ClientConstants.FORMAT_YEAR.format(item.getFascicoloFine().getDataInizio())+")</b>");
		}
		r++;
		
		//Opzioni
		opzioniIstanzaPanel = new OpzioniIstanzaPanel(
				item.getListino().getTipoAbbonamento().getPeriodico().getId(),
				item.getFascicoloInizio().getId(),
				item.getOpzioniIstanzeAbbonamentiSet(),
				item.getListino().getOpzioniListiniSet(),
				"Opzioni "+ClientConstants.ICON_OPZIONI);
		opzioniIstanzaPanel.setVisible(false);
		opzioniIstanzaPanel.setEnabled(isOperator && isEditable);
		table.setWidget(r, 0, opzioniIstanzaPanel);
		table.getFlexCellFormatter().setColSpan(r, 0, 5);
		r++;
		
		//Articoli regalo
		artListPanel = new ArticoliListiniPanel(
				item.getListino().getArticoliListiniSet(),
				"Articoli inclusi");
		table.setWidget(r, 0, artListPanel);
		table.getFlexCellFormatter().setColSpan(r, 0, 5);
		r++;
		
		//TipoSpedizione
		table.setHTML(r,0, "Tipo spedizione");
		tipoSpedizioneList = new TipiSpedizioneSelect(
					item.getAbbonamento().getIdTipoSpedizione());
		tipoSpedizioneList.setEnabled(isEditor);
		table.setWidget(r, 1, tipoSpedizioneList);
		// Tag
		table.setHTML(r, 3, "Adesione");
		Integer idAdesione = null;
		if (item.getAdesione() != null) idAdesione = item.getAdesione().getId();
		adesioniList = new AdesioniSelect(idAdesione);
		adesioniList.setEnabled(isOperator);
		table.setWidget(r, 4, adesioniList);
		//adesioniSuggest = new AdesioniSuggestBox();
		//adesioniSuggest.setValue(item.getAdesioneTxt());
		//adesioniSuggest.setWidth("15em");
		//if (isEditor) {
		//	table.setWidget(r, 4, adesioniSuggest);
		//} else {
		//	table.setHTML(r, 4, item.getAdesioneTxt());
		//}
		r++;
		
		//Pagato
		table.setHTML(r, 0, "Pagamento");
		Integer idPagante = item.getAbbonato().getId();
		if (item.getPagante() != null) idPagante = item.getPagante().getId();
		pagatoCheck = new PagatoCheckBox(item, idPagante, item.getAbbonamento().getPeriodico().getIdSocieta());
		//pagatoCheck.setEnabled(isEditor);//Disabilitato per tutti
		table.setWidget(r, 1, pagatoCheck);
		//Pagato tramita fattura gestita dall'amministrazione
		InlineHTML fattDifferitaLabel = new InlineHTML("Fatt. pag. differito");
		table.setWidget(r, 3, fattDifferitaLabel);
		fatturaDifferitaCheck = new CheckBox();
		fatturaDifferitaCheck.setEnabled(isEditor);
		boolean fatturato = IstanzeStatusUtil.isFatturatoOppureOmaggio(item);
		if (fatturato) fattDifferitaLabel.setHTML("<b>Fatt. pagamento differito</b>");
		fatturaDifferitaCheck.setValue(item.getFatturaDifferita());
		fatturaDifferitaCheck.setEnabled(!item.getListino().getFatturaDifferita());
		fatturaDifferitaCheck.setVisible(!item.getListino().getFatturaDifferita());
		table.setWidget(r, 4, fatturaDifferitaCheck);
		r++;
		
		//Fattura Panel
		fatturaPanel = getFatturaPanel();
		table.setWidget(r, 0, fatturaPanel);
		fatturaDifferitaCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> arg0) {
				toggleFatturaFeatures();
			}
		});
		toggleFatturaFeatures();
		table.getFlexCellFormatter().setColSpan(r, 0, 5);
		r++;
		
		//Data disdetta
		table.setHTML(r, 0, "Prenot. disdetta ");
		HorizontalPanel disdettaPanel = new HorizontalPanel();
		disdettaDate = new DateOnlyBox();
		disdettaDate.setFormat(ClientConstants.BOX_FORMAT_DAY);
		disdettaDate.setValue(item.getDataDisdetta());
		disdettaDate.setWidth("7em");
		disdettaDate.setEnabled(isOperator);
		if (isOperator) {
			disdettaPanel.add(disdettaDate);
		} else {
			disdettaPanel.add(new InlineHTML(ClientConstants.FORMAT_DAY.format(item.getDataDisdetta())));
		}
		//Invio Bloccato
		disdettaPanel.add(new InlineHTML("&nbsp;"));
		bloccatoCheck = new BloccatoCheckBox();
		bloccatoCheck.setValue(item.getInvioBloccato());
		bloccatoCheck.setEnabled(isOperator);
		disdettaPanel.add(bloccatoCheck);
		table.setWidget(r, 1, disdettaPanel);
		//Tipo disdetta
		table.setHTML(r, 3, "Motivo");
		tipoDisdettaList = new TipiDisdettaSelect(item.getIdTipoDisdetta());
		tipoDisdettaList.setEnabled(isOperator);
		table.setWidget(r, 4, tipoDisdettaList);
		r++;
		
		//Data Creazione
		table.setHTML(r, 0, "Creazione");
		String creaz = "<b>"+
				ClientConstants.FORMAT_MONTH.format(item.getAbbonamento().getDataCreazione())+"</b>";
		if (!item.getDataCreazione().equals(item.getAbbonamento().getDataCreazione()) ) {
			creaz += ClientConstants.SPAN_SMALL_START+" (istanza del "+
				ClientConstants.FORMAT_MONTH.format(item.getDataCreazione())+")"+
				ClientConstants.SPAN_STOP;
		}
		table.setHTML(r, 1, creaz);
		//Copie restanti
		table.setHTML(r, 3, "Numeri da spedire");
		numeriHtml = new HTML();
		updateNumeriLabel();
		table.setWidget(r, 4, numeriHtml);
		r++;
		
		//Note
		table.setHTML(r, 0, "Note");
		noteArea = new NoteArea(2048);
		noteArea.setValue(item.getNote());
		noteArea.setWidth("95%");
		noteArea.setHeight("3em");
		noteArea.setEnabled(isOperator);
		table.getFlexCellFormatter().setColSpan(r, 1, 4);
		table.setWidget(r, 1, noteArea);
		r++;
		
		//Pagamento alla creazione
		if (idIstanza.intValue() == AppConstants.NEW_ITEM_ID) {
			TitlePanel paymentPanel = getPaymentPanel();
			table.setWidget(r, 0, paymentPanel);
			table.getFlexCellFormatter().setColSpan(r, 0, 5);
			r++;
		}
		
		//Button panel
		buttonPanel = new ButtonPanel(this);
		table.setWidget(r,0,buttonPanel);
		table.getFlexCellFormatter().setColSpan(r, 0, 6);//Span su 5 colonne
		
		
		if (item.getId() != null) {
			//riga
			panelAbb.add(new HTML("<hr />"));
			//PANNELLO PAGAMENTI
			panelFatt = new SubPanel(TITLE_FATTURE);
			panelAbb.add(panelFatt);
			//PANNELLO FASCICOLI
			panelEvasioniFascicoli = new SubPanel(TITLE_FASCICOLI);
			panelAbb.add(panelEvasioniFascicoli);
			//PANNELLO ARTICOLI
			panelEvasioniArticoli = new SubPanel(TITLE_ARTICOLI);
			panelAbb.add(panelEvasioniArticoli);
			//PANNELLO COMUNICAZIONI
			panelComunicazioni = new SubPanel(TITLE_COMUNICAZIONI);
			panelAbb.add(panelComunicazioni);
			//PANNELLO STORICO
			panelStorico = new SubPanel(TITLE_STORICO);
			panelAbb.add(panelStorico);
			//PANNELLO VERSIONAMENTO
			VersioningPanel versionPanel = new VersioningPanel(
					"IstanzeAbbonamenti", item.getId(), item.getIdUtente(), item.getDataModifica());
			panelAbb.add(versionPanel);
		}
	}
	
		
	private TitlePanel getFatturaPanel() {
		TitlePanel panel = new TitlePanel("Dati della fattura a pagamento differito");
		HorizontalPanel holder = new HorizontalPanel();
		holder.add(new HTML("Numero&nbsp;"));
		fatturaNumText = new TextBox();
		fatturaNumText.setEnabled(isEditor);
		fatturaNumText.setWidth("8em");
		fatturaNumText.setMaxLength(32);
		fatturaNumText.setValue(item.getFatturaNumero());
		holder.add(fatturaNumText);
		holder.add(new HTML("&nbsp;&nbsp;Data&nbsp;"));
		fatturaDate = new DateOnlyBox();
		fatturaDate.setFormat(ClientConstants.BOX_FORMAT_DAY);
		fatturaDate.setEnabled(isEditor);
		fatturaDate.setWidth("8em");
		fatturaDate.setValue(item.getFatturaData());
		holder.add(fatturaDate);
		holder.add(new HTML("&nbsp;&nbsp;Importo&nbsp;"));
		fatturaImportoText = new TextBox();
		fatturaImportoText.setEnabled(isEditor);
		fatturaImportoText.setWidth("6em");
		if (item.getFatturaImporto() != null) {
			fatturaImportoText.setValue(ClientConstants.FORMAT_CURRENCY.format(item.getFatturaImporto()));
		}
		holder.add(fatturaImportoText);
		holder.add(new HTML("&nbsp;&nbsp;Pagata"));
		fatturaPagataCheck = new CheckBox();
		fatturaPagataCheck.setEnabled(isEditor);
		fatturaPagataCheck.setValue(item.getFatturaPagata());
		holder.add(fatturaPagataCheck);
		panel.add(holder);
		return panel;
	}
	
	private TitlePanel getPaymentPanel() {
		TitlePanel panel = new TitlePanel("Pagamento iniziale (immediatamente fatturato)");
		HorizontalPanel holder = new HorizontalPanel();
		holder.add(new HTML("Importo&nbsp;"));
		initialPaymentAmountText = new TextBox();
		initialPaymentAmountText.setEnabled(isOperator);
		initialPaymentAmountText.setMaxLength(10);
		initialPaymentAmountText.setWidth("6em");
		holder.add(initialPaymentAmountText);
		holder.add(new HTML("&nbsp;&nbsp;Data&nbsp;"));
		initialPaymentDate = new DateOnlyBox();
		initialPaymentDate.setFormat(ClientConstants.BOX_FORMAT_DAY);
		initialPaymentDate.setEnabled(isOperator);
		initialPaymentDate.setWidth("8em");
		holder.add(initialPaymentDate);
		holder.add(new HTML("&nbsp;&nbsp;Tipo&nbsp;"));
		initialPaymentTypeList = new TipiPagamentoSelect(AppConstants.PAGAMENTO_DEFAULT);
		initialPaymentTypeList.setEnabled(isOperator);
		holder.add(initialPaymentTypeList);
		panel.add(holder);
		holder.add(new HTML("&nbsp;&nbsp;Note&nbsp;"));
		initialPaymentNoteText = new TextBox();
		initialPaymentNoteText.setEnabled(isOperator);
		initialPaymentNoteText.setMaxLength(250);
		initialPaymentNoteText.setWidth("12em");
		holder.add(initialPaymentNoteText);
		return panel;
	}
	
	private void toggleFatturaFeatures() {
		if ((fatturaDifferitaCheck != null) && (fatturaPanel != null)) {
			boolean visible = fatturaDifferitaCheck.getValue() || item.getListino().getFatturaDifferita();
			//Si vede solo se è vero che è fatturato
			if (fatturaPanel != null)
				fatturaPanel.setVisible(visible);
			if (pagWarningHolder != null)
				pagWarningHolder.setVisible(visible);
			if (pagButtonHolder != null)
				pagButtonHolder.setVisible(!visible);
			fatturaDifferitaCheck.setEnabled(!item.getListino().getFatturaDifferita());
			if (item.getListino().getFatturaDifferita()) fatturaDifferitaCheck.setValue(false);
		}
	}
	
	private void drawFatture() {
		if (panelFatt != null) {
			panelFatt.clear();
			if (item.getId() != null) {
				if (item.getId().intValue() != AppConstants.NEW_ITEM_ID) {
					//DataModel<Pagamenti> pagModel = new PagamentiTable.PagamentiIstanzaModel(item);
					//pagTable = new PagamentiTable(pagModel, userRole, this);
					//DataModel<PagamentiCrediti> credModel = new CreditiTable.CreditiIstanzaModel(item.getId());
					//credTable = new CreditiTable(credModel, userRole, this);
					DataModel<Fatture> fattModel = new FattureTable.FattureByIstanzaModel(item.getId());
					fattTable = new FattureTable(fattModel, utente, this);
					VerticalPanel holder = new VerticalPanel();
					panelFatt.add(holder);
					
					//Warning holder
					pagWarningHolder = new HorizontalPanel();
					InlineHTML pagWarning = new InlineHTML("Prevista fattura a pagamento differito");
					pagWarningHolder.add(pagWarning);
					holder.add(pagWarningHolder);
					
					//Button holder
					pagButtonHolder = new HorizontalPanel();
					holder.add(pagButtonHolder);
					Anchor createAnchor = new Anchor(ClientConstants.ICON_ADD+"Nuovo pagamento", true);
					createAnchor.setVisible(isOperator);
					if (isOperator) {
						createAnchor.addMouseDownHandler(new MouseDownHandler() {
							@Override
							public void onMouseDown(MouseDownEvent event) {
								showPagamentiPopUp();
							}
						});
					}
					holder.add(fattTable);
					//holder.add(pagTable);
					//holder.add(credTable);
					if (item.getId() == null) {
						createAnchor.setVisible(false);
					} else if (item.getId().intValue() == AppConstants.NEW_ITEM_ID) {
						createAnchor.setVisible(false);
					}
					pagButtonHolder.add(createAnchor);
					pagButtonHolder.add(new InlineHTML("&nbsp;<i>(Sar&agrave; visualizzato sulla scheda del pagante)</i>"));
					if ((item.getDataSaldo() != null) && !item.getListino().getFatturaDifferita() &&
							!item.getListino().getFatturaDifferita()) {
						InlineHTML saldoLabel = new InlineHTML("&nbsp;&nbsp;&nbsp;<i>Data saldo: "+
								ClientConstants.FORMAT_DAY.format(item.getDataSaldo())+"</i>");
						pagButtonHolder.add(saldoLabel);
					}
					
					toggleFatturaFeatures();
				}
			}
			//panelPag.add(new InlineHTML("<br/>"));
		}
	}
	private void showPagamentiPopUp() {
		Integer idAnag = ValueUtil.stoi(paganteSearchBox.getIdValue());
		if (idAnag == null) idAnag = ValueUtil.stoi(abbonatoSearchBox.getIdValue());
		new PagamentoPopUp(this, idAnag, item.getAbbonamento().getCodiceAbbonamento(),
				item.getFascicoloInizio().getPeriodico().getIdSocieta());
	}
	
	private void drawEvasioniArticoli() {
		if (panelEvasioniArticoli != null) {
			panelEvasioniArticoli.clear();
			panelEvasioniArticoli.setTitle(TITLE_ARTICOLI);
			if (item.getId() != null) {
				if (item.getId().intValue() != AppConstants.NEW_ITEM_ID) {
					EvasioniArticoliByIstanzaModel model = new EvasioniArticoliTable.EvasioniArticoliByIstanzaModel(idIstanza);
					edTable = new EvasioniArticoliTable(model, userRole, this, false);
					FlowPanel holder = new FlowPanel();
					panelEvasioniArticoli.add(holder);
					if (item.getDataSaldo() == null) {
						holder.add(new InlineHTML("Gli articoli saranno spediti dopo il saldo.<br />"));
					}
					Anchor nuovoLink = null;
					if (isOperator) {
						nuovoLink = new Anchor(ClientConstants.ICON_ADD+"Abbina articolo", true);
						holder.add(nuovoLink);
					}
					holder.add(edTable);
					if(isOperator) {
						nuovoLink.addMouseDownHandler(new MouseDownHandler() {
							@Override
							public void onMouseDown(MouseDownEvent event) {
								EvasioneArticoloPopUp popup = new EvasioneArticoloPopUp();
								popup.initByIstanzaAbbonamento(item.getId(),false, true, edTable);
							}
						});
					}
				}
			}
			//panelEvasioniArticoli.add(new InlineHTML("<br/>"));
		}
	}
	
	private void drawEvasioniFascicoli() {
		if (panelEvasioniFascicoli != null) {
			panelEvasioniFascicoli.clear();
			panelEvasioniFascicoli.setTitle(TITLE_FASCICOLI);
			if (item.getId() != null) {
				if (item.getId().intValue() != AppConstants.NEW_ITEM_ID) {
					DataModel<EvasioniFascicoli> efModel = new EvasioniFascicoliTable.EvasioniFascicoliByIstanzaModel(item.getId());
					efTable = new EvasioniFascicoliTable(efModel, item.getFascicoloInizio().getDataInizio(),
							userRole, this, false);
					VerticalPanel holder = new VerticalPanel();
					HorizontalPanel buttonHolder = new HorizontalPanel();
					// Bottone ARRETRATO
					Anchor arretratoAnchor = new Anchor(ClientConstants.ICON_ADD+"Aggiungi arretrato", true);
					arretratoAnchor.setVisible(isOperator);
					if (isOperator) {
						arretratoAnchor.addClickHandler(new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								aggiungiArretrato();
							}
						});
					}
					buttonHolder.add(arretratoAnchor);
					// Bottone tutti ARRETRATI
	//				Button arreInizioButton = new Button("Aggiungi arretrati da inizio");
	//				arreInizioButton.setEnabled(editable);
	//				if (editable) {
	//					arreInizioButton.addClickHandler(new ClickHandler() {
	//						@Override
	//						public void onClick(ClickEvent event) {
	//							generaTuttiArretrati(idIstanza, efTable);
	//						}
	//					});
	//				}
	//				buttonHolder.add(arreInizioButton);
					holder.add(buttonHolder);
					if (item.getId() == null) {
						arretratoAnchor.setVisible(false);
	//					arreInizioButton.setEnabled(false);
					} else if (item.getId().intValue() == AppConstants.NEW_ITEM_ID) {
						arretratoAnchor.setVisible(false);
	//					arreInizioButton.setEnabled(false);
					}
					holder.add(efTable);
					panelEvasioniFascicoli.add(holder);
				}
			}
			//panelEvasioniFascicoli.add(new InlineHTML("<br/>"));
		}
	}
	
	private void drawEvasioniComunicazioni() {
		if (panelComunicazioni != null) {
			panelComunicazioni.clear();
			panelComunicazioni.setTitle(TITLE_COMUNICAZIONI);
			if (item.getId() != null) {
				if (item.getId().intValue() != AppConstants.NEW_ITEM_ID) {
					VerticalPanel holder = new VerticalPanel();
					HorizontalPanel buttonPanel = new HorizontalPanel();
					Anchor bollettinoAnchor = new Anchor(ClientConstants.ICON_ADD+"Invia bollettino", true);
					bollettinoAnchor.setVisible(isOperator);
					if (isOperator) {
						bollettinoAnchor.addClickHandler(new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								aggiungiEvasioneComunicazione(AppConstants.COMUN_MEDIA_BOLLETTINO);
							}
						});
					}
					buttonPanel.add(bollettinoAnchor);
					buttonPanel.add(new InlineHTML("&nbsp;&nbsp;"));
					holder.add(buttonPanel);
					DataModel<EvasioniComunicazioni> model = new EvasioniComunicazioniTable.EvasioniComunicazioniModel(item.getId());
					ecTable = new EvasioniComunicazioniTable(model, userRole);
					holder.add(ecTable);
					panelComunicazioni.add(holder);
				}
			}
			//panelComunicazioni.add(new InlineHTML("<br/>"));
		}
	}
	
	private void drawStorico(Integer idAbbonamento) {
		if (panelStorico != null) {
			panelStorico.clear();
			panelStorico.setTitle(TITLE_STORICO);
			if (item.getId() != null) {
				if (item.getId().intValue() != AppConstants.NEW_ITEM_ID) {
					DataModel<IstanzeAbbonamenti> model = new IstanzeAbbonamentiTable.StoricoIstanzeModel(idAbbonamento);
					IstanzeAbbonamentiTable iaTable = new IstanzeAbbonamentiTable(model, false, null);
					panelStorico.add(iaTable);
				}
			}
			//panelStorico.add(new InlineHTML("<br/>"));
		}
	}
	
	private void aggiungiArretrato() {
		Integer idPeriodico = item.getListino().getTipoAbbonamento().getPeriodico().getId();
		EvasioneFascicoloPopUp popUp = new EvasioneFascicoloPopUp();
		popUp.initByPeriodicoIstanza(idPeriodico, item.getId(),
				item.getFascicoloInizio().getDataInizio(), efTable);
	}
	
	private void aggiungiEvasioneComunicazione(String idTipoMedia) {
		EvasioneComunicazionePopUp popUp = new EvasioneComunicazionePopUp();
		popUp.initByIstanzaAbbonamento(item.getId(), idTipoMedia, ecTable);
	}
	
	
	
	// METODI ON_CHANGE DI AGGIORNAMENTO UI
	
	
	
	private void onPeriodicoChange() {
		Integer idPeriodico = periodiciList.getSelectedValueInt();
		CookieSingleton.get().setCookie(ClientConstants.COOKIE_LAST_PERIODICO, idPeriodico+"");
		AsyncCallback<IstanzeAbbonamenti> callback = new AsyncCallback<IstanzeAbbonamenti>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
			}
			@Override
			public void onSuccess(IstanzeAbbonamenti result) {
				item=result;
				//NO MORE refreshAbbonamentoCode(result.getAbbonamento().getPeriodico().getId());
				fasInizioList.reload(
						result.getFascicoloInizio().getId(),
						result.getListino().getTipoAbbonamento().getPeriodico().getId(),
						startDt, finishDt, false, false, true, false, false);// NON scatena onChange
				listiniList.reload(item.getListino().getId(),
						item.getFascicoloInizio().getPeriodico().getId(),
						item.getFascicoloInizio().getId(),
						false); // NON scatena onChange
				opzioniIstanzaPanel.onListinoChange(
						item.getListino().getTipoAbbonamento().getPeriodico().getId(),
						item.getFascicoloInizio().getId(),
						item.getListino().getOpzioniListiniSet());
				fasFineList.reload(
						item.getFascicoloFine().getId(),
						item.getListino().getTipoAbbonamento().getPeriodico().getId(),
						startDt, finishDt, false, false, true, false, false); // NON scatena onChange()
				artListPanel.changeListino(item.getListino().getArticoliListiniSet());
				updateNumeriLabel();
			}
		};
		abbonamentiService.changePeriodico(item, idPeriodico,
				item.getListino().getTipoAbbonamento().getCodice(), callback);
	}

	public void onFascicoloInizioChange() {
		Integer idFascicolo = fasInizioList.getSelectedValueInt();
		AsyncCallback<IstanzeAbbonamenti> callback = new AsyncCallback<IstanzeAbbonamenti>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
			}
			@Override
			public void onSuccess(IstanzeAbbonamenti result) {
				item=result;
				listiniList.reload(item.getListino().getId(),
						item.getFascicoloInizio().getPeriodico().getId(),
						item.getFascicoloInizio().getId(),
						false); // NON scatena onChange
				opzioniIstanzaPanel.onListinoChange(
						item.getListino().getTipoAbbonamento().getPeriodico().getId(),
						item.getFascicoloInizio().getId(),
						item.getListino().getOpzioniListiniSet());
				fasFineList.reload(
						item.getFascicoloFine().getId(),
						item.getListino().getTipoAbbonamento().getPeriodico().getId(),
						startDt, finishDt, false, false, true, false, false); // NON scatena onChange()
				artListPanel.changeListino(item.getListino().getArticoliListiniSet());
				updateNumeriLabel();
			}
		};
		abbonamentiService.changeFascicoloInizio(item,
				idFascicolo,
				item.getListino().getTipoAbbonamento().getCodice(), callback);
	}
	
	public void onFascicoloFineChange() {
		if ((fasInizioList.getSelectedValueString() != null) &&
				(periodiciList.getSelectedValueString() != null)) {
			//Chiame il metodo asincrono per il conto fascicoli
			updateNumeriLabel();
		}
	}
	
	private void onListinoChange() {
		Integer idListino = listiniList.getSelectedValueInt();
		AsyncCallback<IstanzeAbbonamenti> callback = new AsyncCallback<IstanzeAbbonamenti>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
			}
			@Override
			public void onSuccess(IstanzeAbbonamenti result) {
				item=result;
				opzioniIstanzaPanel.onListinoChange(
						item.getListino().getTipoAbbonamento().getPeriodico().getId(),
						item.getFascicoloInizio().getId(),
						item.getListino().getOpzioniListiniSet());
				fasInizioList.reload(
						item.getFascicoloInizio().getId(),
						item.getListino().getTipoAbbonamento().getPeriodico().getId(),
						startDt, finishDt, false, false, true, false, false); // NON scatena onChange()
				fasFineList.reload(
						item.getFascicoloFine().getId(),
						item.getListino().getTipoAbbonamento().getPeriodico().getId(),
						startDt, finishDt, false, false, true, false, false); // NON scatena onChange()
				artListPanel.changeListino(item.getListino().getArticoliListiniSet());
				updateNumeriLabel();
			}
		};
		abbonamentiService.changeListino(item,
				idListino, callback);
	}
	
	private void updateNumeriLabel() {
		if (item.getListino().getCartaceo()) {
			//Cartaceo
			AsyncCallback<Integer> callback = new AsyncCallback<Integer>() {
				@Override
				public void onFailure(Throwable caught) {
					UiSingleton.get().addError(caught);
				}
				@Override
				public void onSuccess(Integer result) {
						numeriHtml.setHTML("<b>"+result+"</b>");
				}
			};
			if (idIstanza.intValue() != AppConstants.NEW_ITEM_ID) {
				fascicoliService.countFascicoliDaSpedire(idIstanza, callback);
			} else {
				numeriHtml.setHTML(NUMERI_EMPTY_LABEL);
			}
		} else {
			numeriHtml.setHTML("<i>non cartaceo</i>");
		}
	}
	
	
	
	
	// METODI ASINCRONI
	
	
	
	
	private void loadIstanzaAbbonamento() {
		AsyncCallback<IstanzeAbbonamenti> callback = new AsyncCallback<IstanzeAbbonamenti>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(IstanzeAbbonamenti result) {
				item = result;
				if (result.getAbbonato() != null) {
					idAnagrafica = result.getAbbonato().getId();
				}
				drawAbbonamento();
				WaitSingleton.get().stop();
				drawStorico(result.getAbbonamento().getId());
				drawFatture();
				drawEvasioniArticoli();
				drawEvasioniFascicoli();
				drawEvasioniComunicazioni();
			}
		};
		
		WaitSingleton.get().start();
		//look for item with id only if id is defined
		if (idIstanza.intValue() != AppConstants.NEW_ITEM_ID) {
			abbonamentiService.findIstanzeById(idIstanza, callback);
		} else {
			//is new abbonamento
			Integer idPeriodico = ValueUtil.stoi(CookieSingleton.get().getCookie(ClientConstants.COOKIE_LAST_PERIODICO));
			if (idPeriodico == null) idPeriodico = UiSingleton.get().getDefaultIdPeriodico(utente);
			abbonamentiService.createAbbonamentoAndIstanza(idAnagrafica, null, null, idPeriodico, callback);
		}
	}

	private void verifyCodiceAndSave() throws ValidationException {
		if (codAbboText != null) {
			//Verifica codice abbonamento e salva
			final String codiceAbbonamento = codAbboText.getValue();
			if (abbonatoSearchBox.getIdValue() == null) throw new ValidationException("L'abbonato non e' definito");
			if (abbonatoSearchBox.getIdValue().equals("") ||
					abbonatoSearchBox.getIdValue().equals(AppConstants.NEW_ITEM_ID+"")) {
				throw new ValidationException("L'abbonato non e' definito");
			}
			Integer idAbbonato = Integer.parseInt(abbonatoSearchBox.getIdValue());
			AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
				@Override
				public void onFailure(Throwable caught) {
					UiSingleton.get().addError(caught);
				}
				@Override
				public void onSuccess(Boolean exists) {
					try {
						if (!exists) {
							//Salva tranquillamente
							saveOrUpdate(false);
						} else {
							//Chiede se cambiare codice
							boolean confirm = Window.confirm(
									codiceAbbonamento+" e' gia' assegnato ad un altro cliente.\n" +
									"Vuoi che APG risolva il problema?\n" +
									"Scegli 'OK' per generare un nuovo codice per questo abbonamento.\n" +
									"Scegli 'Annulla' per lasciare il codice "+codiceAbbonamento);
							saveOrUpdate(confirm);
						}
					} catch (ValidationException e) {
						UiSingleton.get().addWarning(e.getMessage());
					}
				}
			};
			abbonamentiService.findCodiceAbbonamentoIfDifferentAbbonato(
					codiceAbbonamento, idAbbonato, callback);
		} else {
			//salva o modifica ma assegna codice solo se mancante
			if (item.getAbbonamento().getCodiceAbbonamento() == null)
				item.getAbbonamento().setCodiceAbbonamento("");
			boolean assignNewCodiceAbbonamento = (item.getAbbonamento().getCodiceAbbonamento().length() == 0);
			saveOrUpdate(assignNewCodiceAbbonamento);
		}
	}
	
	private void saveOrUpdate(boolean assignNewCodiceAbbonamento) throws ValidationException {
		AsyncCallback<Integer> callback = new AsyncCallback<Integer>() {
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
			public void onSuccess(Integer result) {
				idIstanza = (Integer)result;
				UiSingleton.get().addInfo(AppConstants.MSG_SAVE_OK);
				WaitSingleton.get().stop();
				//generaTuttiArretrati();
				verifyTotaleNumeri();
				verifyPagante();
				verifyMacroarea();
				UriParameters params = new UriParameters();
				params.add(AppConstants.PARAM_ID_ANAGRAFICA, idAnagrafica);
				params.add(AppConstants.PARAM_ID, idIstanza);
				params.triggerUri(UriManager.ABBONAMENTO);
			}
		};
		
		//Validazione residua (parte e' stata fatta prima di chiamare save)
		Integer copie = null;
		try {
			copie = Integer.valueOf(copieText.getValue().trim());
		} catch (NumberFormatException e1) {
			throw new ValidationException("Errore nel numero di copie");
		}
		if (copie < 1) throw new ValidationException("Errore nel numero di copie");
//		String descFasInizio = fasInizioList.getItemText(fasInizioList.getSelectedIndex());
//		String descFasFine = fasFineList.getItemText(fasFineList.getSelectedIndex());
//		if (descFasInizio.compareToIgnoreCase(descFasFine) >= 0)  throw new ValidationException("Inizio e fine sono invertiti");
		
		Double importoFatt = null;
		if (fatturaImportoText.getValue() != null) {
			if (!fatturaImportoText.getValue().trim().equals("")) {
				try {
					importoFatt = ClientConstants.FORMAT_CURRENCY.parse(fatturaImportoText.getValue().trim());
				} catch (NumberFormatException e1) {
					throw new ValidationException("Valore non valido nell'importo fattura");
				}
			}
		}
		Integer idTipoDisdetta = null;
		try {
			idTipoDisdetta = tipoDisdettaList.getSelectedValueInt();
		} catch (NumberFormatException e1) {
			idTipoDisdetta = null;
		}
		if (abbonatoSearchBox.getIdValue() == null) {
			throw new ValidationException("Deve essere specificata l'anagrafica dell'abbonato");
		}
		//Assegnazione
		Date today = DateUtil.now();
		item.setCopie(copie);
		item.setIdFascicoloInizioT(fasInizioList.getValue(fasInizioList.getSelectedIndex()));
		item.setIdFascicoloFineT(fasFineList.getValue(fasFineList.getSelectedIndex()));
		item.setInvioBloccato(bloccatoCheck.getValue());
		item.setNote(noteArea.getValue());
		//item.setPagato(pagatoCheck.getValue());
		if (item.getListino() != null) {
			if (!item.getListino().getFatturaDifferita()) {
				item.setFatturaDifferita(fatturaDifferitaCheck.getValue());
			}
		}
		item.setFatturaData(fatturaDate.getValue());
		item.setFatturaImporto(importoFatt);
		item.setFatturaNumero(fatturaNumText.getValue().trim());
		item.setFatturaPagata(fatturaPagataCheck.getValue());
		item.setIdListinoT(listiniList.getSelectedValueString());
		item.setDataModifica(today);
		item.setIdUtente(AuthSingleton.get().getUtente().getId());
		item.setDataDisdetta(disdettaDate.getValue());
		item.setIdTipoDisdetta(idTipoDisdetta);
		item.setIdPromotoreT(promotoreSearchBox.getIdValue());
		item.setIdAbbonatoT(abbonatoSearchBox.getIdValue());
		item.setIdPaganteT(paganteSearchBox.getIdValue());
		//item.setAdesioneTxt(adesioniSuggest.getValue());
		item.setIdAdesioneT(adesioniList.getSelectedValueString());
		
		if (codAbboText != null) {
			item.getAbbonamento().setCodiceAbbonamento(codAbboText.getValue().trim());
		}
		if (periodiciList != null)
			item.getAbbonamento().setIdPeriodicoT(periodiciList.getSelectedValueString());
		item.getAbbonamento().setIdTipoSpedizione(tipoSpedizioneList.getSelectedValueString());
		item.getAbbonamento().setDataModifica(today);
		item.getAbbonamento().setIdUtente(AuthSingleton.get().getUtente().getId());

		item.setIdOpzioniIstanzeAbbonamentiSetT(opzioniIstanzaPanel.getValue());
		
		if (initialPaymentAmountText != null) {
			if (initialPaymentAmountText.getValue() != null) {
				if (initialPaymentAmountText.getValue().trim().length() > 0) {
					if (initialPaymentDate.getValue() == null) {
						throw new ValidationException("Il pagamento iniziale non e' stato salvato perche' manca la data");
					} else {
						//C'è pagamento iniziale
						Pagamenti pagamento = new Pagamenti();
						try {
							pagamento.setImporto(ClientConstants.FORMAT_CURRENCY.parse(initialPaymentAmountText.getValue()));
						} catch (NumberFormatException e) {
							throw new ValidationException("Importo non corretto");
						}
						String idTipo = initialPaymentTypeList.getSelectedValueString();
						pagamento.setIdTipoPagamento(idTipo);
						pagamento.setDataPagamento(initialPaymentDate.getValue());
						pagamento.setDataAccredito(initialPaymentDate.getValue());
						pagamento.setDataModifica(today);
						pagamento.setDataCreazione(today);
						pagamento.setNote(initialPaymentNoteText.getValue().trim());
						pagamento.setIdUtente(AuthSingleton.get().getUtente().getId());
						WaitSingleton.get().start();
						abbonamentiService.saveWithPayment(item, pagamento, callback);
						return;
					}
				}
			}
		}
		item.setNecessitaVerifica(false);
		WaitSingleton.get().start();
		//Salvataggio solo abbonamento o senza pagamento iniziale
		if (item.getId() == null) {
			abbonamentiService.save(item, callback);
		} else {
			abbonamentiService.update(item, assignNewCodiceAbbonamento, callback);
		}
	}

	
	//private void refreshAbbonamentoCode(Integer periodiciId) {
	//	if (codAbboText == null) {
	//		//Non deve accadere se l'abbonamento o l'istanza già esistono
	//		return;
	//	}
	//	AsyncCallback<String> callback = new AsyncCallback<String>() {
	//		@Override
	//		public void onFailure(Throwable caught) {
	//			UiSingleton.get().addError(caught);
	//			WaitSingleton.get().stop();
	//		}
	//		@Override
	//		public void onSuccess(String result) {			
	//			codAbboText.setValue((String)result);
	//			WaitSingleton.get().stop();
	//		}
	//	};
	//	WaitSingleton.get().start();
	//	abbonamentiService.createCodiceAbbonamento(periodiciId, callback);
	//}

		
	private void rinnova(Integer idOldIstanza) {
		AsyncCallback<IstanzeAbbonamenti> callback = new AsyncCallback<IstanzeAbbonamenti>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(IstanzeAbbonamenti result) {
				item = result;
				WaitSingleton.get().stop();
				UriParameters params = new UriParameters();
				params.add(AppConstants.PARAM_ID_ANAGRAFICA, idAnagrafica);
				params.add(AppConstants.PARAM_ID, item.getId());
				params.triggerUri(UriManager.ABBONAMENTO);

			}
		};
		
		//look for item with id only if id is defined
		if (idIstanza.intValue() != AppConstants.NEW_ITEM_ID) {
			WaitSingleton.get().start();
			idIstanza = AppConstants.NEW_ITEM_ID;
			abbonamentiService.makeBasicRenewal(idOldIstanza, AuthSingleton.get().getUtente().getId(), callback);
		}
	}
	
	private void rigenera(Integer idOldIstanza) {
		AsyncCallback<IstanzeAbbonamenti> callback = new AsyncCallback<IstanzeAbbonamenti>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(IstanzeAbbonamenti result) {
				item = result;
				WaitSingleton.get().stop();
				UriParameters params = new UriParameters();
				params.add(AppConstants.PARAM_ID_ANAGRAFICA, idAnagrafica);
				params.add(AppConstants.PARAM_ID, item.getId());
				params.triggerUri(UriManager.ABBONAMENTO);
			}
		};
		
		//look for item with id only if id is defined
		if (idIstanza.intValue() != AppConstants.NEW_ITEM_ID) {
			WaitSingleton.get().start();
			idIstanza = AppConstants.NEW_ITEM_ID;
			abbonamentiService.makeBasicRegeneration(idOldIstanza, AuthSingleton.get().getUtente().getId(), callback);
		}
	}
	
//	private void generaTuttiArretrati() {
//		final EvasioniFascicoliTable fascicoliTable = efTable;
//		AsyncCallback<List<EvasioniFascicoli>> callback = new AsyncCallback<List<EvasioniFascicoli>>() {
//			@Override
//			public void onFailure(Throwable caught) {
//				UiSingleton.get().addError(caught);
//				WaitSingleton.get().stop();
//			}
//			@Override
//			public void onSuccess(List<EvasioniFascicoli> result) {
//				//il risultato è scartato
//				if (fascicoliTable != null) {
//					fascicoliTable.refresh();
//				}
//				WaitSingleton.get().stop();
//			}
//		};
//		if (idIstanza.intValue() != AppConstants.NEW_ITEM_ID) {
//			WaitSingleton.get().start();
//			Utenti utente = AuthSingleton.get().getUtente();
//			Date endDt = DateUtil.now();
//			fascicoliService.createMassiveArretrati(idIstanza, endDt, utente, callback);
//		}
//	}
	
	private void verifyCodiceAbbonamento(String codiceAbbonamento) {
		verifyCodiceAbbonamento(codiceAbbonamento, null);
	}
	private void verifyCodiceAbbonamento(String codiceAbbonamento, Integer idAbbonato) {
		final String fCa = codiceAbbonamento;
		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
			}
			@Override
			public void onSuccess(Boolean result) {
				if (result) UiSingleton.get().addWarning("Il codice "+fCa+" e' gia' assegnato ad un altro cliente!");
			}
		};
		if (codiceAbbonamento == null) codiceAbbonamento = "";
		if (codiceAbbonamento.equals("")) UiSingleton.get().addWarning("Il codice abbonamento non puo' essere vuoto!");
		if (codiceAbbonamento.length() != 7) UiSingleton.get().addWarning("Il codice abbonamento deve essere di 7 caratteri");
		if (item.getAbbonamento().getCodiceAbbonamento().equals(codiceAbbonamento)) return;
		if (idAbbonato == null) {
			abbonamentiService.findCodiceAbbonamento(codiceAbbonamento, callback);
		} else {
			abbonamentiService.findCodiceAbbonamentoIfDifferentAbbonato(codiceAbbonamento, idAbbonato, callback);
		}
	}
	
	private void verifyTotaleNumeri() {
		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				if (caught instanceof ValidationException) {
					UiSingleton.get().addWarning(caught.getMessage());
				} else {
					UiSingleton.get().addError(caught);
				}
			}
			@Override
			public void onSuccess(Boolean corrisponde) {
				//Tutto corrisponde
			}
		};
		abbonamentiService.verifyTotaleNumeri(idIstanza, callback);
	}
	
	private void verifyPagante() {
		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				if (caught instanceof ValidationException) {
					UiSingleton.get().addWarning(caught.getMessage());
				} else {
					UiSingleton.get().addError(caught);
				}
			}
			@Override
			public void onSuccess(Boolean corrisponde) {
				//Tutto corrisponde
			}
		};
		abbonamentiService.verifyPagante(idIstanza, callback);
	}
	
	private void verifyMacroarea() {
		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				if (caught instanceof ValidationException) {
					UiSingleton.get().addWarning(caught.getMessage());
				} else {
					UiSingleton.get().addError(caught);
				}
			}
			@Override
			public void onSuccess(Boolean corrisponde) {
				//Tutto corrisponde
			}
		};
		abbonamentiService.verifyMacroarea(idIstanza, callback);
	}
	
	private void loadStatusMessage(IstanzeAbbonamenti ia) {
		AsyncCallback<String> callback = new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
			}
			@Override
			public void onSuccess(String result) {
				if (result.length() > 0) {
					String statusText = ClientConstants.ICON_IMPORTANT +
							"&nbsp;Stato attuale: "+result;
					statusWarningHtml.setHTML(statusText);
					statusWarningHtml.setStyleName("message-warn");
				}
			}
		};
		abbonamentiService.getStatusMessage(idIstanza, callback);
	}
	
	private void deleteAbbonamento() {
		final Anagrafiche anag = item.getAbbonato();
		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
			}
			@Override
			public void onSuccess(Boolean result) {
				//Visualizza l'anagrafica
				UriParameters params = new UriParameters();
				params.add(AppConstants.PARAM_ID, anag.getId());
				params.triggerUri(UriManager.ANAGRAFICHE_MERGE);
			}
		};
		
		boolean confirm = Window.confirm("Vuoi veramente cancellare questa istanza inclusi TUTTI I PAGAMENTI, I FASCICOLI e LE COMUNICAZIONI?");
		if (confirm) {
			abbonamentiService.deleteIstanza(idIstanza, callback);
		}
	}
	
	
	
	// inner classes
	

	
	private class ButtonPanel extends HorizontalPanel {
		private HorizontalPanel rinnovaPanel;
		private HorizontalPanel rigeneraPanel;
		
		public ButtonPanel(IRefreshable parent) {
			super();
			final IRefreshable fParent = parent;
			// Bottone SALVA
			Button submitButton = new Button(ClientConstants.ICON_SAVE+" Salva", new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					try {
						verifyCodiceAndSave();
					} catch (ValidationException e) {
						UiSingleton.get().addWarning(e.getMessage());
					}
				}
			});
			if (idIstanza.equals(AppConstants.NEW_ITEM_ID)) {
				submitButton.setHTML(ClientConstants.ICON_SAVE+" Crea");
			}
			this.add(submitButton);
			//Rinnovo
			if (!idIstanza.equals(AppConstants.NEW_ITEM_ID)) {
				// Bottone RINNOVA
				rinnovaPanel = new HorizontalPanel();
				rinnovaPanel.add(new Image("img/separator.gif"));
				Button rinnovaButton = new Button(ClientConstants.ICON_RINNOVA+"&nbsp;Rinnova");
				rinnovaButton.setVisible(isOperator);
				if (isOperator) {
					rinnovaButton.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							boolean confirm = Window.confirm("Vuoi veramente rinnovare l'abbonamento?");
							if (confirm) {
								rinnova(idIstanza);
							}
						}
					});
				}
				rinnovaPanel.add(rinnovaButton);
				this.add(rinnovaPanel);
				
				// Bottone Rigenera
				rigeneraPanel = new HorizontalPanel();
				rigeneraPanel.add(new Image("img/separator.gif"));
				Button rigeneraButton = new Button(ClientConstants.ICON_RIGENERA+"&nbsp;Rigenera");
				rigeneraButton.setVisible(isOperator);
				if (isOperator) {
					rigeneraButton.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							boolean confirm = Window.confirm("Rigenerare un abbonamento significa creare una nuova istanza che " +
									"avra' come fascicolo iniziale il prossimo numero in uscita. \r\n" +
									"\r\n" +
									"Vuoi veramente rigenerare l'abbonamento?");
							if (confirm) {
								rigenera(idIstanza);
							}
						}
					});
				}
				rigeneraPanel.add(rigeneraButton);
				this.add(rigeneraPanel);

				this.add(new Image("img/separator.gif"));
				//Bottone Ridefinisci offerta e pagamento
				Button creditoButton = new Button(ClientConstants.ICON_CHECKED+" Cambia offerta e pagamento");
				creditoButton.setVisible(isOperator);
				if (isOperator) {
					creditoButton.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							new FatturazionePopUp(item, fParent);
						}
					});
				}
				this.add(creditoButton);
				
				// Bottone elimina
				if (isSuper && !idAnagrafica.equals(AppConstants.NEW_ITEM_ID)) {
					this.add(new Image("img/separator.gif"));
					Button deleteAbbButton = new Button(ClientConstants.ICON_DELETE+"&nbsp;Elimina completamente!");
					deleteAbbButton.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							deleteAbbonamento();
						}
					});
					this.add(deleteAbbButton);
				}
			}
			refresh();
		}
		
		public void refresh() {
			refreshRinnovaButton();
			refreshRigeneraButton();
			//if (pagTable != null) pagTable.refresh();
			//if (credTable != null) credTable.refresh();
		}
		
		private void refreshRinnovaButton() {
			AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
				@Override
				public void onFailure(Throwable caught) {
					UiSingleton.get().addError(caught);
				}
				@Override
				public void onSuccess(Boolean result) {
					boolean rinnovabile = (isOperator && result) || isSuper;
					if (rinnovaPanel != null) {
						rinnovaPanel.setVisible(rinnovabile);
					}
				}
			};
			abbonamentiService.isRenewable(idIstanza, callback);
		}
		
		private void refreshRigeneraButton() {
			AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
				@Override
				public void onFailure(Throwable caught) {
					UiSingleton.get().addError(caught);
				}
				@Override
				public void onSuccess(Boolean result) {
					boolean rigenerabile = (isOperator && result) || isSuper;
					if (rigeneraPanel != null) {
						rigeneraPanel.setVisible(rigenerabile);
					}
				}
			};
			abbonamentiService.isRegenerable(idIstanza, callback);
		}
	}
	
}
