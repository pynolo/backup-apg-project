package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.CookieSingleton;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.UriManager;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.ComunicazioniService;
import it.giunti.apg.client.services.ComunicazioniServiceAsync;
import it.giunti.apg.client.services.TipiAbbService;
import it.giunti.apg.client.services.TipiAbbServiceAsync;
import it.giunti.apg.client.widgets.DateOnlyBox;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.client.widgets.ProtectedMultiListBox;
import it.giunti.apg.client.widgets.VersioningPanel;
import it.giunti.apg.client.widgets.select.DestinatarioSelect;
import it.giunti.apg.client.widgets.select.FascicoliSelect;
import it.giunti.apg.client.widgets.select.PeriodiciSelect;
import it.giunti.apg.client.widgets.select.TagSelect;
import it.giunti.apg.client.widgets.select.TipiAttivazioneComSelect;
import it.giunti.apg.client.widgets.select.TipiMediaComSelect;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.Comunicazioni;
import it.giunti.apg.shared.model.ModelliBollettini;
import it.giunti.apg.shared.model.ModelliEmail;
import it.giunti.apg.shared.model.TipiAbbonamento;
import it.giunti.apg.shared.model.Utenti;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;

public class ComunicazioneFrame extends FramePanel implements IAuthenticatedWidget {
	
	private final ComunicazioniServiceAsync comService = GWT.create(ComunicazioniService.class);
	private final TipiAbbServiceAsync tipiAbbService = GWT.create(TipiAbbService.class);
	
	private static final String TITLE_COMUNICAZIONE = "Comunicazione";
	private static final String LABEL_NUMERI_INIZIO = "Fascicoli dall'inizio";
	private static final String LABEL_NUMERI_FINE = "Fascicoli dalla fine";
	
	private Integer idPeriodico = null;
	private Integer idComunicazione = null;
	private Comunicazioni item = null;
	private boolean isEditor = false;
	private boolean isAdmin = false;
	//private boolean isSuper = false;
	private Utenti utente = null;
	
	private SimplePanel dataPanel = null;
	private TextBox titoloText = null;
	private PeriodiciSelect periodiciList = null;
	
	private TipiMediaComSelect tipiMediaList = null;
	private TipiAttivazioneComSelect tipiAttivazioneList = null;
	private CheckBox soloConPaganteCheck = null;
	private CheckBox soloSenzaPaganteCheck = null;
	private CheckBox soloUnaCopiaCheck = null;
	private CheckBox soloPiuCopieCheck = null;
	private CheckBox soloNonPagatiCheck = null;
	private CheckBox soloUnaIstanzaCheck = null;
	private CheckBox soloMolteIstanzeCheck = null;
	private TagSelect tagOpzione = null;
	private FascicoliSelect fasList = null;
	
	private CheckBox prezzoAltCheck = null;
	private CheckBox prezzoVuotoCheck = null;
	private CheckBox rinnovoCheck = null;
	private HTML labelNumeri = null;
	private TextBox numDaInizioOFineText = null;
	private DestinatarioSelect tipiDestinatarioList = null;
	private ProtectedMultiListBox tipiAbbonamentoList = null;
	
	private TextBox idBandellaText = null;
	private InlineHTML modelliPanelLabel = null;
	private SimplePanel modelliPanel = null;
	private ListBox modelliBollettiniList = null;
	private ListBox modelliEmailList = null;
	private DateOnlyBox inizioDate = null;
	private DateOnlyBox fineDate = null;
	
	// METHODS
	
	public ComunicazioneFrame(UriParameters params) {
		super();
		if (params == null) {
			params = new UriParameters();
		}
		idComunicazione = params.getIntValue(AppConstants.PARAM_ID);
		AuthSingleton.get().queueForAuthentication(this);
	}
	
	@Override
	public void onSuccessfulAuthentication(Utenti utente) {
		this.utente = utente;
		init(utente);
	}
	
	private void init(Utenti utente) {
		idPeriodico = ValueUtil.stoi(CookieSingleton.get().getCookie(ClientConstants.COOKIE_LAST_PERIODICO));
		if (idPeriodico == null) idPeriodico=UiSingleton.get().getDefaultIdPeriodico(utente);
		// Editing rights
		int ruolo = utente.getRuolo().getId();
		isEditor = (ruolo >= AppConstants.RUOLO_EDITOR);
		isAdmin = (ruolo >= AppConstants.RUOLO_ADMIN);
		//isSuper = (ruolo >= AppConstants.RUOLO_SUPER);
		// UI
		if (isEditor) {
			dataPanel = new SimplePanel();
			this.add(dataPanel, TITLE_COMUNICAZIONE);
			loadComunicazione();
		}
	}
	

	
	/** This method empties the ContentPanel and redraws the 'item' data
	 * @param item
	 */
	private void drawComunicazione() {
		// clean form
		dataPanel.clear();
		FlexTable table = new FlexTable();
		dataPanel.add(table);
		int r=0;
		
		// titolo
		table.setHTML(r, 0, "Descrizione"+ClientConstants.MANDATORY);
		titoloText = new TextBox();
		titoloText.setValue(item.getTitolo());
		titoloText.setEnabled(isAdmin);
		titoloText.setMaxLength(128);
		titoloText.setWidth("18em");
		table.setWidget(r, 1, titoloText);
		//table.getFlexCellFormatter().setColSpan(r, 1, 4);
		//tipi media
		table.setHTML(r, 3, "Media");
		tipiMediaList = new TipiMediaComSelect(item.getIdTipoMedia());
		tipiMediaList.setEnabled(isAdmin);
		tipiMediaList.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				refreshModelliPanel();
			}
		});
		table.setWidget(r, 4, tipiMediaList);
		r++;
		
		//tipi attivazione
		table.setHTML(r, 0, "Attivazione");
		tipiAttivazioneList = new TipiAttivazioneComSelect(item.getIdTipoAttivazione());
		tipiAttivazioneList.setEnabled(isAdmin);
		tipiAttivazioneList.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent arg0) {
				refreshNumeriLabel();
			}
		});
		table.setWidget(r, 1, tipiAttivazioneList);
		//Numeri da inizio o fine
		labelNumeri = new HTML(LABEL_NUMERI_INIZIO+ClientConstants.MANDATORY);
		table.setWidget(r, 3, labelNumeri);
		numDaInizioOFineText = new TextBox();
		numDaInizioOFineText.setValue(item.getNumeriDaInizioOFine()+"");
		numDaInizioOFineText.setWidth("4em");
		numDaInizioOFineText.setEnabled(isAdmin);
		table.setWidget(r, 4, numDaInizioOFineText);
		refreshNumeriLabel();
		r++;
		
		// Periodico
		table.setHTML(r, 0, "Periodico");
		periodiciList = new PeriodiciSelect(item.getPeriodico().getId(), DateUtil.now(), false, true, utente);
		periodiciList.setEnabled(isAdmin);
		periodiciList.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent arg0) {
				loadTipiAbbonamentiList();
				refreshModelliBollettiniList();
			}
		});
		table.setWidget(r, 1, periodiciList);
		//tipi destinatario
		table.setHTML(r, 3, "Destinatario");
		tipiDestinatarioList = new DestinatarioSelect(item.getIdTipoDestinatario());
		tipiDestinatarioList.setEnabled(isAdmin);
		table.setWidget(r, 4, tipiDestinatarioList);
		r++;
		
		
		
		//tipiAbbonamento
		table.setHTML(r, 0, "Tipi abbonamento"+ClientConstants.MANDATORY);
		//table.getFlexCellFormatter().setRowSpan(r, 3, 11);
		tipiAbbonamentoList = new ProtectedMultiListBox();
		tipiAbbonamentoList.setVisibleItemCount(16);
		tipiAbbonamentoList.setEnabled(isAdmin);
		table.setWidget(r, 1, tipiAbbonamentoList);
		//table.getFlexCellFormatter().setRowSpan(r, 4, 11);
		r++;
		
		table.setHTML(r, 0, "<br/><b>Direttive per il contenuto del messaggio:</b>");
		table.getFlexCellFormatter().setColSpan(r, 0, 2);
		r++;
		table.setHTML(r, 0, "&Egrave; un invito al rinnovo");
		rinnovoCheck = new CheckBox();
		rinnovoCheck.setValue(item.getRichiestaRinnovo());
		rinnovoCheck.setEnabled(isAdmin);
		table.setWidget(r, 1, rinnovoCheck);
		r++;
		table.setHTML(r, 0, "Stampa prezzo alternativo");
		prezzoAltCheck = new CheckBox();
		prezzoAltCheck.setValue(item.getMostraPrezzoAlternativo());
		prezzoAltCheck.setEnabled(isAdmin);
		table.setWidget(r, 1, prezzoAltCheck);
		r++;
		table.setHTML(r, 0, "Stampa prezzo vuoto");
		prezzoVuotoCheck = new CheckBox();
		prezzoVuotoCheck.setValue(item.getBollettinoSenzaImporto());
		prezzoVuotoCheck.setEnabled(isAdmin);
		table.setWidget(r, 1, prezzoVuotoCheck);
		r++;
		
		//Testo
		table.setHTML(r, 0, "Num. bandella <i>(se necessario)</i>");
		idBandellaText = new TextBox();
		idBandellaText.setValue(item.getIdBandella()+"");
		idBandellaText.setWidth("6em");
		idBandellaText.setEnabled(isAdmin);
		table.setWidget(r, 1, idBandellaText);
		r++;
		// Modello
		modelliPanelLabel = new InlineHTML("Modello");
		table.setWidget(r, 0, modelliPanelLabel);
		modelliPanel = new SimplePanel();
		modelliBollettiniList = new ListBox();
		modelliEmailList = new ListBox();
		refreshModelliPanel();
		table.setWidget(r, 1, modelliPanel);
		r++;
				
		//Restrizioni
		table.setHTML(r, 0, "<br/><b>Restrizioni sull'abbonamento:</b>");
		table.getFlexCellFormatter().setColSpan(r, 0, 2);
		r++;
		table.setHTML(r, 0, "Solo senza pagante (non regalo)");
		soloSenzaPaganteCheck = new CheckBox();
		soloSenzaPaganteCheck.setValue(item.getSoloSenzaPagante());
		soloSenzaPaganteCheck.setEnabled(isAdmin);
		table.setWidget(r, 1, soloSenzaPaganteCheck);
		table.setHTML(r, 3, "Solo con pagante (regalo)");
		soloConPaganteCheck = new CheckBox();
		soloConPaganteCheck.setValue(item.getSoloConPagante());
		soloConPaganteCheck.setEnabled(isAdmin);
		table.setWidget(r, 4, soloConPaganteCheck);
		r++;
		table.setHTML(r, 0, "Solo per una copia");
		soloUnaCopiaCheck = new CheckBox();
		soloUnaCopiaCheck.setValue(item.getSoloUnaCopia());
		soloUnaCopiaCheck.setEnabled(isAdmin);
		table.setWidget(r, 1, soloUnaCopiaCheck);
		table.setHTML(r, 3, "Solo per pi&ugrave; copie");
		soloPiuCopieCheck = new CheckBox();
		soloPiuCopieCheck.setValue(item.getSoloPiuCopie());
		soloPiuCopieCheck.setEnabled(isAdmin);
		table.setWidget(r, 4, soloPiuCopieCheck);
		r++;
		table.setHTML(r, 0, "Solo con una istanza");
		soloUnaIstanzaCheck = new CheckBox();
		soloUnaIstanzaCheck.setValue(item.getSoloUnaIstanza());
		soloUnaIstanzaCheck.setEnabled(isAdmin);
		table.setWidget(r, 1, soloUnaIstanzaCheck);
		table.setHTML(r, 3, "Solo con pi&ugrave; istanze");
		soloMolteIstanzeCheck = new CheckBox();
		soloMolteIstanzeCheck.setValue(item.getSoloMolteIstanze());
		soloMolteIstanzeCheck.setEnabled(isAdmin);
		table.setWidget(r, 4, soloMolteIstanzeCheck);
		r++;
		table.setHTML(r, 0, "Solo abb. in corso non pagato");
		soloNonPagatiCheck = new CheckBox();
		soloNonPagatiCheck.setValue(item.getSoloNonPagati());
		soloNonPagatiCheck.setEnabled(isAdmin);
		table.setWidget(r, 1, soloNonPagatiCheck);
		r++;
		table.setHTML(r, 0, "Tag del opzione abbinato");
		tagOpzione = new TagSelect(item.getTagOpzione());
		tagOpzione.setEnabled(isAdmin);
		table.setWidget(r, 1, tagOpzione);
		r++;
		table.setHTML(r, 0, "Solo con fascicolo iniziale");
		fasList = new FascicoliSelect(item.getIdFascicoloInizio(),
				item.getPeriodico().getId(),
				item.getDataInizio().getTime()-AppConstants.YEAR,
				Long.MAX_VALUE,
				false, false, true, false, true);
		fasList.setEnabled(isAdmin);
		table.setWidget(r, 1, fasList);
		r++;
		
		//Validità
		table.setHTML(r, 0, "<br/><b>Validit&agrave;:</b> ");
		table.getFlexCellFormatter().setColSpan(r, 0, 2);
		r++;
		// DataInizio
		table.setHTML(r, 0, "Valido da");
		inizioDate = new DateOnlyBox();
		inizioDate.setFormat(ClientConstants.BOX_FORMAT_DAY);
		inizioDate.setValue(item.getDataInizio());
		if (isAdmin) {
			table.setWidget(r, 1, inizioDate);
		} else if (item.getDataInizio() != null) {
				table.setHTML(r, 1, ClientConstants.FORMAT_DAY.format(item.getDataInizio()));
		}
		// DataFine
		table.setHTML(r, 3, "Fino a");
		fineDate = new DateOnlyBox();
		fineDate.setFormat(ClientConstants.BOX_FORMAT_DAY);
		fineDate.setValue(item.getDataFine());
		if (isAdmin) {
			table.setWidget(r, 4, fineDate);
		} else {
			String scadenza = "--";
			if (item.getDataFine() != null) {
				scadenza = ClientConstants.FORMAT_DAY.format(item.getDataFine());
			}
			table.setHTML(r, 4, scadenza);
		}
		r++;
		//Note sulla validita'
		table.setHTML(r, 0, "La validit&agrave; indica i giorni in cui verr&agrave; creata "+
				"questa comunicazione. I destinatari saranno coloro per cui la condizione "+
				"di attivazione si &egrave; verificata da non pi&ugrave; di "+
				AppConstants.COMUN_ROLLBACK_DAYS+" giorni.");
		table.getFlexCellFormatter().setColSpan(r, 0, 6);
		r++;
		
		HorizontalPanel buttonPanel = getButtonPanel();
		table.setWidget(r,0,buttonPanel);
		table.getFlexCellFormatter().setColSpan(r, 0, 6);//Span su 5 colonne
		r++;
		
		if (item.getId() != null) {
			//PANNELLO VERSIONAMENTO
			VersioningPanel versionPanel = new VersioningPanel(
					"Comunicazioni", item.getId(), item.getIdUtente(), item.getDataModifica());
			table.setWidget(r, 0, versionPanel);
			table.getFlexCellFormatter().setColSpan(r, 0, 6);
		}
	}
	
	private HorizontalPanel getButtonPanel() {
		HorizontalPanel buttonPanel = new HorizontalPanel();
		// Bottone SALVA
		Button submitButton = new Button("Salva", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				try {
					saveData();
				} catch (ValidationException e) {
					UiSingleton.get().addWarning(e.getMessage());
				}
			}
		});
		if (idComunicazione.equals(AppConstants.NEW_ITEM_ID)) {
			submitButton.setText("Crea");
		}
		submitButton.setEnabled(isAdmin);
		buttonPanel.add(submitButton);

		return buttonPanel;
	}
	
	private void refreshNumeriLabel() {
		String tipoAtt = tipiAttivazioneList.getSelectedValueString();
		boolean visible = true;
		if (tipoAtt.equals(AppConstants.COMUN_ATTIVAZ_DA_INIZIO)) {
			labelNumeri.setHTML(LABEL_NUMERI_INIZIO+ClientConstants.MANDATORY);
		} else {
			if (tipoAtt.equals(AppConstants.COMUN_ATTIVAZ_DA_FINE)) {
				labelNumeri.setHTML(LABEL_NUMERI_FINE+ClientConstants.MANDATORY);
			} else {
				numDaInizioOFineText.setValue("0");
				visible = false;
			}
		}
		labelNumeri.setVisible(visible);
		numDaInizioOFineText.setVisible(visible);
	}
	
	private void refreshModelliBollettiniList() {
		String media = tipiMediaList.getSelectedValueString();
		if (media.equals(AppConstants.COMUN_MEDIA_BOLLETTINO) /*||
				media.equals(AppConstants.COMUN_MEDIA_NDD)*/) {
			//Bollettino
			modelliBollettiniList.setEnabled(isAdmin);
			Integer idModello = null;
			if (item.getModelloBollettino() != null) idModello = item.getModelloBollettino().getId();
			if (periodiciList.getSelectedValueInt() != null) {
				String idPeriodicoString = periodiciList.getSelectedValueString();
				Integer idPeriodico = null;
				if (idPeriodicoString != null) {
					if (!idPeriodicoString.equals("")) {
						idPeriodico = Integer.parseInt(idPeriodicoString);
					}
				}
				loadModelliBollettiniList(idModello, idPeriodico, modelliBollettiniList);
			}
		} else {
			//Se non è bollettino o ndd disattiva e mette il valore vuoto
			modelliBollettiniList.setEnabled(false);
			modelliBollettiniList.clear();
			modelliBollettiniList.addItem(AppConstants.SELECT_EMPTY_LABEL);
		}
	}
	
	private void refreshModelliEmailList() {
		String media = tipiMediaList.getSelectedValueString();
		if (media.equals(AppConstants.COMUN_MEDIA_EMAIL)) {
			//Bollettino
			modelliEmailList.setEnabled(isAdmin);
			Integer idModello = null;
			if (item.getModelloEmail() != null) idModello = item.getModelloEmail().getId();
			loadModelliEmailList(idModello, modelliEmailList);
		} else {
			//Se non è bollettino o ndd disattiva e mette il valore vuoto
			modelliEmailList.setEnabled(false);
			modelliEmailList.clear();
			modelliEmailList.addItem(AppConstants.SELECT_EMPTY_LABEL);
		}
	}
	
	private void refreshModelliPanel() {
		String media = tipiMediaList.getSelectedValueString();
		modelliPanelLabel.setHTML("");
		modelliPanel.clear();
		if (media.equals(AppConstants.COMUN_MEDIA_EMAIL)) {
			modelliPanelLabel.setHTML("Modello per email");
			modelliPanel.add(modelliEmailList);
			refreshModelliEmailList();
		}
		if (media.equals(AppConstants.COMUN_MEDIA_BOLLETTINO) /*||
				media.equals(AppConstants.COMUN_MEDIA_NDD) */) {
			modelliPanelLabel.setHTML("Modello bollettino <i>(se automatico)</i>");
			modelliPanel.add(modelliBollettiniList);
			refreshModelliBollettiniList();
		}
	}
	
	private void drawModelliBollettiniBox(Integer selectedId, ListBox listBox, List<ModelliBollettini> list) {
		listBox.clear();
		//listBox.setVisibleItemCount(1);
		listBox.addItem(AppConstants.SELECT_EMPTY_LABEL);
		for (int i=0; i<list.size(); i++) {
			ModelliBollettini p = list.get(i);
			listBox.addItem(p.getDescr(), p.getId().toString());
			if (p.getId().equals(selectedId)) {
				listBox.setSelectedIndex(i+1);
			}
		}
	}

	private void drawModelliEmailBox(Integer selectedId, ListBox listBox, List<ModelliEmail> list) {
		listBox.clear();
		//listBox.setVisibleItemCount(1);
		listBox.addItem(AppConstants.SELECT_EMPTY_LABEL);
		for (int i=0; i<list.size(); i++) {
			ModelliEmail p = list.get(i);
			listBox.addItem(p.getDescr(), p.getId().toString());
			if (p.getId().equals(selectedId)) {
				listBox.setSelectedIndex(i+1);
			}
		}
	}
	
	
	
	// METODI ASINCRONI
	

	private void loadComunicazione() {
		AsyncCallback<Comunicazioni> callback = new AsyncCallback<Comunicazioni>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(Comunicazioni result) {
				item = result;
				drawComunicazione();
				loadTipiAbbonamentiList();
				WaitSingleton.get().stop();
			}
		};
		
		//look for item with id only if id is defined
		if (idComunicazione.intValue() != AppConstants.NEW_ITEM_ID) {
			WaitSingleton.get().start();
			comService.findComunicazioneById(idComunicazione, callback);
		} else {
			//is new bandella
			WaitSingleton.get().start();
			comService.createComunicazione(idPeriodico, callback);
		}
	}
	
	private void loadModelliBollettiniList(Integer selectedId, Integer idPeriodico, ListBox bolModList) {
		ComunicazioniServiceAsync comService = GWT.create(ComunicazioniService.class);
		final Integer id = selectedId;
		final ListBox listBox = bolModList;
		AsyncCallback<List<ModelliBollettini>> callback = new AsyncCallback<List<ModelliBollettini>>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(List<ModelliBollettini> result) {
				drawModelliBollettiniBox(id, listBox, result);
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		comService.findModelliBollettiniByPeriodico(idPeriodico, 0, Integer.MAX_VALUE, callback);
	}
	
	private void loadModelliEmailList(Integer selectedId, ListBox modEmailList) {
		ComunicazioniServiceAsync comService = GWT.create(ComunicazioniService.class);
		final Integer id = selectedId;
		final ListBox listBox = modEmailList;
		AsyncCallback<List<ModelliEmail>> callback = new AsyncCallback<List<ModelliEmail>>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(List<ModelliEmail> result) {
				drawModelliEmailBox(id, listBox, result);
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		comService.findModelliEmail(0, Integer.MAX_VALUE, callback);
	}
	
	private void saveData() throws ValidationException {
		//Conferma
		boolean confirm = Window.confirm("Vuoi veramente modificare le impostazioni della comunicazione?");
		if (!confirm) return;
		
		AsyncCallback<Integer> callback = new AsyncCallback<Integer>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(Integer result) {			
				idComunicazione = result;
				UiSingleton.get().addInfo(AppConstants.MSG_SAVE_OK);
				WaitSingleton.get().stop();
				UriParameters params = new UriParameters();
				params.add(AppConstants.PARAM_ID, idComunicazione);
				params.triggerUri(UriManager.COMUNICAZIONE);
			}
		};
		//Validazione
		String val = titoloText.getValue();
		if (val == null) throw new ValidationException("Descrizione mancante");
		if (val.length()==0) throw new ValidationException("Descrizione mancante");
		Integer numeri = null;
		try {
			numeri = Integer.parseInt(numDaInizioOFineText.getValue());
		} catch (NumberFormatException e) {
			throw new ValidationException("Numeri da inizio o fine non validi");
		}
		String tipiAbbonamentoString = listBoxToString(tipiAbbonamentoList);
		//if (tipiAbbonamentoString.length() < 1) {
		//	throw new ValidationException("Nessun tipo abbonamento selezionato");
		//}
		Integer idBandella;
		try {
			idBandella = Integer.parseInt(idBandellaText.getValue());
		} catch (NumberFormatException e) {
			throw new ValidationException("Numero bandella non valido");
		}
		//Assegnamento
		String idPer = periodiciList.getSelectedValueString();
		item.setIdPeriodicoT(idPer);
		item.setTitolo(val);
		item.setIdTipoMedia(tipiMediaList.getSelectedValueString());
		item.setIdTipoAttivazione(tipiAttivazioneList.getSelectedValueString());
		item.setIdTipoDestinatario(tipiDestinatarioList.getSelectedValueString());
		item.setNumeriDaInizioOFine(numeri);
		item.setSoloConPagante(soloConPaganteCheck.getValue());
		item.setSoloSenzaPagante(soloSenzaPaganteCheck.getValue());
		item.setSoloNonPagati(soloNonPagatiCheck.getValue());
		item.setSoloPiuCopie(soloPiuCopieCheck.getValue());
		item.setSoloUnaCopia(soloUnaCopiaCheck.getValue());
		item.setSoloUnaIstanza(soloUnaIstanzaCheck.getValue());
		item.setSoloMolteIstanze(soloMolteIstanzeCheck.getValue());
		item.setTagOpzione(tagOpzione.getSelectedValueString());
		item.setIdFascicoloInizio(null);
		Integer idFasInizio = fasList.getSelectedValueInt();
		if (idFasInizio != null) {
			if (idFasInizio > 0) item.setIdFascicoloInizio(idFasInizio);
		}
		item.setMostraPrezzoAlternativo(prezzoAltCheck.getValue());
		item.setBollettinoSenzaImporto(prezzoVuotoCheck.getValue());
		item.setRichiestaRinnovo(rinnovoCheck.getValue());
		item.setTipiAbbonamentoList(tipiAbbonamentoString);
		item.setIdBandella(idBandella);
		if (modelliBollettiniList.getItemCount() > 0) {
			item.setIdModelloBollettinoT(modelliBollettiniList.getValue(modelliBollettiniList.getSelectedIndex()));
		}
		if (modelliEmailList.getItemCount() > 0) {
			item.setIdModelloEmailT(modelliEmailList.getValue(modelliEmailList.getSelectedIndex()));
		}
		item.setDataInizio(inizioDate.getValue());
		item.setDataFine(fineDate.getValue());
		item.setDataModifica(DateUtil.now());
		item.setIdUtente(AuthSingleton.get().getUtente().getId());

		WaitSingleton.get().start();
		comService.saveOrUpdateComunicazione(item, callback);
	}

	
	public String listBoxToString(ProtectedMultiListBox listBox) {
		String result = "";
		for (int i=0; i<listBox.getItemCount(); i++) {
			if (listBox.isItemSelected(i)) {
				if (result.length()>0) result += AppConstants.COMUN_TIPI_ABB_SEPARATOR;
				result += listBox.getValue(i);
			}
		}
		return result;
	}
	
	public void stringToListBoxSelection(ProtectedMultiListBox listBox, String valueString) {
		String[] idArray = valueString.split(AppConstants.COMUN_TIPI_ABB_SEPARATOR);
		for (int i=0; i<listBox.getItemCount(); i++) {
			for (String id:idArray) {
				if (listBox.getValue(i).equals(id)) {
					listBox.setItemSelected(i, true);
				}
			}
		}
	}
	
	
	

	//tipiAbbonamentiList methods
	
	private void drawTipiAbbonamentiList(List<TipiAbbonamento> taList) {
		tipiAbbonamentoList.clear();
		for (int i=0; i<taList.size(); i++) {
			TipiAbbonamento ta = taList.get(i);
			String nome = ta.getNome();
			if (ta.getCodice() != null) {
				if (ta.getCodice().length() > 0) {
					nome = ta.getCodice()+" - "+nome+" (modif. "+
							ClientConstants.FORMAT_DAY.format(ta.getDataModifica())+
							")";
				}
			}
			tipiAbbonamentoList.addItem(nome,
					ta.getId().toString());
		}
		stringToListBoxSelection(tipiAbbonamentoList, item.getTipiAbbonamentoList());
	}
	private void loadTipiAbbonamentiList() {
		if (periodiciList != null) {
			if (periodiciList.getSelectedValueInt() != null) {
				AsyncCallback<List<TipiAbbonamento>> callback = new AsyncCallback<List<TipiAbbonamento>>() {
					@Override
					public void onFailure(Throwable caught) {
						UiSingleton.get().addError(caught);
						WaitSingleton.get().stop();
					}
					@Override
					public void onSuccess(List<TipiAbbonamento> result) {
						drawTipiAbbonamentiList(result);
						refreshModelliBollettiniList();
						WaitSingleton.get().stop();
					}
				};
				WaitSingleton.get().start();
				Integer idPeriodico = periodiciList.getSelectedValueInt();
				CookieSingleton.get().setCookie(ClientConstants.COOKIE_LAST_PERIODICO, idPeriodico+"");
				tipiAbbService.findTipiAbbonamentoByPeriodico(idPeriodico,
						null, callback);
			}
		}
	}
	
}
