package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.UriManager;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.OpzioniService;
import it.giunti.apg.client.services.OpzioniServiceAsync;
import it.giunti.apg.client.widgets.DateOnlyBox;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.client.widgets.SubPanel;
import it.giunti.apg.client.widgets.VersioningPanel;
import it.giunti.apg.client.widgets.select.AliquoteIvaSelect;
import it.giunti.apg.client.widgets.select.PeriodiciSelect;
import it.giunti.apg.client.widgets.select.TagSelectPanel;
import it.giunti.apg.client.widgets.tables.ArticoliOpzioniTable;
import it.giunti.apg.client.widgets.tables.DataModel;
import it.giunti.apg.client.widgets.tables.FascicoliTable;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.model.ArticoliOpzioni;
import it.giunti.apg.shared.model.Fascicoli;
import it.giunti.apg.shared.model.Opzioni;
import it.giunti.apg.shared.model.Ruoli;
import it.giunti.apg.shared.model.Utenti;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class OpzioneFrame extends FramePanel implements IAuthenticatedWidget {
	
	private final OpzioniServiceAsync opzioniService = GWT.create(OpzioniService.class);
	
	private static final String BOX_WIDTH = "20em";
	private static final String TITLE_OPZIONE = "Opzione";
	private static final String TITLE_ARTICOLI = "Articoli da spedire";
	private static final String TITLE_FASCICOLI = "Fascicoli abbinati";
	
	private VerticalPanel dataPanel = null;
	private Integer idOpzione = null;
	private Opzioni item = null;
	private Ruoli ruolo = null;
	//private boolean isOperator = false;
	private boolean isEditor = false;
	private boolean isAdmin = false;
	private boolean isSuper = false;
	private Utenti utente = null;
	
	private TextBox nomeText = null;
	private PeriodiciSelect periodicoSel = null;
	
	private TextBox codiceText = null;
	private TagSelectPanel tagSelect = null;
	
	private TextBox prezzoText = null;
	private AliquoteIvaSelect ivaSel = null;
	
	private CheckBox cartaceoCheck = null;
	private CheckBox digitaleCheck = null;
	
	private DateOnlyBox beginDate = null;
	private DateOnlyBox endDate = null;
	
	private TextBox noteText = null;
	
	private SubPanel panelArticoli = null;
	
	private SubPanel panelFascicoli = null;
	
	// METHODS
	
	public OpzioneFrame(UriParameters params) {
		super();
		if (params == null) {
			params = new UriParameters();
		}
		Integer value = params.getIntValue(AppConstants.PARAM_ID);
		if (value != null) {
			idOpzione = value;
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
		ruolo = utente.getRuolo();
		//isOperator = (ruolo.getId() >= AppConstants.RUOLO_OPERATOR);
		isEditor = (ruolo.getId() >= AppConstants.RUOLO_EDITOR);
		isAdmin = (ruolo.getId() >= AppConstants.RUOLO_ADMIN);
		isSuper = (ruolo.getId() >= AppConstants.RUOLO_SUPER);
		// UI
		if (isEditor) {
			dataPanel = new VerticalPanel();
			this.add(dataPanel, TITLE_OPZIONE);
			loadOpzione();
		}
	}


	private void draw() {
		// clean form
		dataPanel.clear();
		FlexTable table = new FlexTable();
		dataPanel.add(table);
		int r=0;
				
		//Nome
		table.setHTML(r, 0, "Nome"+ClientConstants.MANDATORY);
		nomeText = new TextBox();
		nomeText.setValue(item.getNome());
		nomeText.setMaxLength(64);
		nomeText.setEnabled(isAdmin);
		table.setWidget(r, 1, nomeText);
		//Periodico
		table.setHTML(r, 3, "Periodico");
		periodicoSel = new PeriodiciSelect(item.getPeriodico().getId(),
				item.getDataInizio(), false, false, utente);
		periodicoSel.setEnabled((isAdmin&&item.getId() == null) || isSuper);
		periodicoSel.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				changeUid();
			}
		});
		table.setWidget(r, 4, periodicoSel);
		r++;
		
		//Codice
		table.setHTML(r, 0, "UID"+ClientConstants.MANDATORY);
		codiceText = new TextBox();
		codiceText.setValue(item.getUid());
		codiceText.setMaxLength(8);
		codiceText.setEnabled(isSuper);
		table.setWidget(r, 1, codiceText);
		//Tag
		table.setHTML(r, 3, "Tag");
		tagSelect = new TagSelectPanel(item.getTag());
		tagSelect.setEnabled(isAdmin);
		table.setWidget(r, 4, tagSelect);
		r++;
				
		//Prezzo
		table.setHTML(r, 0, "Prezzo"+ClientConstants.MANDATORY);
		prezzoText = new TextBox();
		prezzoText.setValue(ClientConstants.FORMAT_CURRENCY.format(item.getPrezzo()));
		prezzoText.setMaxLength(12);
		prezzoText.setEnabled(isAdmin);
		table.setWidget(r, 1, prezzoText);
		//Aliquota IVA
		table.setHTML(r, 3, "Aliquota IVA");
		ivaSel = new AliquoteIvaSelect(item.getAliquotaIva().getId(), item.getDataInizio());
		ivaSel.setEnabled(isAdmin);
		table.setWidget(r, 4, ivaSel);
		r++;
		
		//Cartaceo
		table.setHTML(r, 0, "Cartaceo "+ClientConstants.ICON_CARTACEO);
		cartaceoCheck = new CheckBox();
		cartaceoCheck.setValue(item.getCartaceo());
		cartaceoCheck.setEnabled(isAdmin);
		table.setWidget(r, 1, cartaceoCheck);
		//Digitale
		table.setHTML(r, 3, "Digitale "+ClientConstants.ICON_APP);
		digitaleCheck = new CheckBox();
		digitaleCheck.setValue(item.getDigitale());
		digitaleCheck.setEnabled(isAdmin);
		table.setWidget(r, 4, digitaleCheck);
		r++;
		
		//Data inizio
		table.setHTML(r, 0, "Inizio"+ClientConstants.MANDATORY);
		beginDate = new DateOnlyBox();
		beginDate.setFormat(ClientConstants.BOX_FORMAT_DAY);
		beginDate.setValue(item.getDataInizio());
		if (isAdmin) {
			table.setWidget(r, 1, beginDate);
		} else {
			table.setHTML(r, 1, ClientConstants.FORMAT_DAY.format(item.getDataInizio()));
		}
		//Data fine
		table.setHTML(r, 3, "Fine");
		endDate = new DateOnlyBox();
		endDate.setFormat(ClientConstants.BOX_FORMAT_DAY);
		endDate.setValue(item.getDataFine());
		if (isAdmin) {
			table.setWidget(r, 4, endDate);
		} else {
			if (item.getDataFine() != null) {
				table.setHTML(r, 4, ClientConstants.FORMAT_DAY.format(item.getDataFine()));
			}
		}
		r++;
		
		//Note
		table.setHTML(r, 0, "Note");
		noteText = new TextBox();
		noteText.setValue(item.getNote());
		noteText.setMaxLength(250);
		noteText.setWidth(BOX_WIDTH);
		noteText.setEnabled(isAdmin);
		table.getFlexCellFormatter().setColSpan(r, 1, 4);
		table.setWidget(r, 1, noteText);
		r++;
		
		dataPanel.add(getButtonPanel(isAdmin));
		
		//Articoli
		panelArticoli = new SubPanel(TITLE_ARTICOLI);
		dataPanel.add(panelArticoli);
		drawArticoli(item.getId());
		
		//Fascicoli
		panelFascicoli = new SubPanel(TITLE_FASCICOLI);
		dataPanel.add(panelFascicoli);
		drawFascicoli(item.getId());
		
		//PANNELLO VERSIONAMENTO
		if (item.getId() != null) {
			VersioningPanel versionPanel = new VersioningPanel(
					"Opzioni", item.getId(), item.getIdUtente(), item.getDataModifica());
			dataPanel.add(versionPanel);
		}
	}
	
	
	private HorizontalPanel getButtonPanel(boolean editable) {
		HorizontalPanel buttonPanel = new HorizontalPanel();
		if (isEditor) {
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
			if (idOpzione.equals(AppConstants.NEW_ITEM_ID)) {
				submitButton.setText("Crea");
			}
			submitButton.setEnabled(editable);
			buttonPanel.add(submitButton);
		}
		return buttonPanel;
	}
	
	private void drawArticoli(Integer idOpzione) {
		panelArticoli.setTitle(TITLE_ARTICOLI);
		panelArticoli.clear();
		if (idOpzione != null) {
			DataModel<ArticoliOpzioni> model = new ArticoliOpzioniTable.ArticoliOpzioniModel(idOpzione);
			ArticoliOpzioniTable aoTable = new ArticoliOpzioniTable(model, ruolo);
			FlowPanel holder = new FlowPanel();
			panelArticoli.add(holder);
			Anchor nuovoLink = null;
			if (isAdmin) {
				nuovoLink = new Anchor(ClientConstants.ICON_ADD+"Abbina articolo", true);
				holder.add(nuovoLink);
			}
			holder.add(aoTable);
			if(isAdmin) {
				final Integer fIdOpzione = idOpzione;
				final ArticoliOpzioniTable fAoTable = aoTable;
				nuovoLink.addMouseDownHandler(new MouseDownHandler() {
					@Override
					public void onMouseDown(MouseDownEvent event) {
						new ArticoloOpzionePopUp(AppConstants.NEW_ITEM_ID, fIdOpzione, fAoTable);
					}
				});
			}
		}
	}
	
	private void drawFascicoli(Integer idOpzione) {
		panelFascicoli.setTitle(TITLE_FASCICOLI);
		panelFascicoli.clear();
		if (idOpzione != null) {
			DataModel<Fascicoli> model = new FascicoliTable.FascicoliByOpzioneModel(idOpzione);
			FascicoliTable aoTable = new FascicoliTable(model, ruolo);
			FlowPanel holder = new FlowPanel();
			panelFascicoli.add(holder);
			HTML tip = new HTML("I fascicoli devono essere aggiunti da <b>Impostazioni > Fascicoli</b>");
			holder.add(tip);
			holder.add(aoTable);
		}
	}
	
	
	
	
	// METODI ASINCRONI
	
	
	
	private void loadOpzione() {
		AsyncCallback<Opzioni> callback = new AsyncCallback<Opzioni>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(Opzioni result) {
				item = result;
				WaitSingleton.get().stop();
				draw();
			}
		};
		
		//look for item with id only if id is defined
		if (idOpzione.intValue() != AppConstants.NEW_ITEM_ID) {
			WaitSingleton.get().start();
			opzioniService.findOpzioneById(idOpzione, callback);
		} else {
			//is new opzione
			WaitSingleton.get().start();
			Integer idPeriodiciDefault = UiSingleton.get().getDefaultIdPeriodico(utente);
			opzioniService.createOpzione(idPeriodiciDefault, callback);
		}
	}

	private void saveData() throws ValidationException {
		AsyncCallback<Integer> callback = new AsyncCallback<Integer>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(Integer result) {			
				idOpzione = result;
				UiSingleton.get().addInfo(AppConstants.MSG_SAVE_OK);
				WaitSingleton.get().stop();
				UriParameters params = new UriParameters();
				params.add(AppConstants.PARAM_ID, idOpzione);
				params.triggerUri(UriManager.OPZIONE);
			}
		};
		//Validazione
		if (nomeText.getValue().equals("")) {
			throw new ValidationException("Il nome non puo' essere vuoto");
		}
		if (prezzoText.getValue().equals("")) {
			throw new ValidationException("Il prezzo non puo' essere vuoto");
		}
		Double prezzo;
		try {
			prezzo = ClientConstants.FORMAT_CURRENCY.parse(prezzoText.getValue());
		} catch (NumberFormatException e) {
			throw new ValidationException("Il prezzo non e' valido");
		}
		if (codiceText.getValue().equals("")) {
			throw new ValidationException("Lo UID non puo' essere vuoto");
		}
		if (nomeText.getValue().equals("")) {
			throw new ValidationException("Il titolo non puo' essere vuoto");
		}
		if (beginDate.getValue() == null) {
			throw new ValidationException("La data iniziale non puo' essere vuota");
		}
		if (endDate.getValue() != null) {
			if (beginDate.getValue().equals(endDate.getValue()) ||
					beginDate.getValue().after(endDate.getValue())) {
				throw new ValidationException("La data iniziale deve essere antecedente a quella finale");
			}
		}
		//Salvataggio
		item.setNome(nomeText.getValue());
		item.setIdPeriodicoT(periodicoSel.getSelectedValueInt());
		item.setUid(codiceText.getValue());
		item.setTag(tagSelect.getTagValues());
		item.setPrezzo(prezzo);
		item.setIdAliquotaIvaT(ivaSel.getSelectedValueInt());
		item.setCartaceo(cartaceoCheck.getValue());
		item.setDigitale(digitaleCheck.getValue());
		item.setDataInizio(beginDate.getValue());
		item.setDataFine(endDate.getValue());
		item.setNote(noteText.getValue());
		item.setDataModifica(new Date());
		item.setIdUtente(AuthSingleton.get().getUtente().getId());
		
		WaitSingleton.get().start();
		opzioniService.saveOrUpdateOpzione(item, callback);
	}

	private void changeUid() {
		AsyncCallback<String> callback = new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(String newUid) {
				codiceText.setValue(newUid);
				WaitSingleton.get().stop();
				draw();
			}
		};
		WaitSingleton.get().start();
		int selectedValue = periodicoSel.getSelectedValueInt();
		opzioniService.createNewUid(selectedValue, callback);
	}
	
}
