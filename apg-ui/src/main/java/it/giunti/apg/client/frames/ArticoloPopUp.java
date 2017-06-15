package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.ArticoliService;
import it.giunti.apg.client.services.ArticoliServiceAsync;
import it.giunti.apg.client.widgets.select.TipiAnagraficaSapSelect;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.model.Articoli;
import it.giunti.apg.shared.model.Utenti;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DateBox;

public class ArticoloPopUp extends PopupPanel implements IAuthenticatedWidget {

	private final ArticoliServiceAsync articoliService = GWT.create(ArticoliService.class);
	
	private FlexTable table = new FlexTable();
	private Integer idArticolo = null;
	private Articoli item = null;
	private IRefreshable parent = null;
	private boolean isOperator = false;
	private boolean isEditor = false;
	
	private TextBox codiceText = null;
	private TextBox meccText = null;
	private TextBox titoloText = null;
	private TextBox autoreText = null;
	private TipiAnagraficaSapSelect tipoAnagraficaSap = null;
	private CheckBox cartaceoCheck = null;
	private CheckBox digitaleCheck = null;
	private DateBox beginDate = null;
	private DateBox endDate = null;
	private CheckBox attesaCheck = null;
	
	public ArticoloPopUp(Integer idArticolo, IRefreshable parent) {
		super(false);
		this.idArticolo=idArticolo;
		this.parent=parent;
		AuthSingleton.get().queueForAuthentication(this);
	}
	
	@Override
	public void onSuccessfulAuthentication(Utenti utente) {
		init(utente);
	}
	
	private void init(Utenti utente) {
		int ruolo = utente.getRuolo().getId();
		// Editing rights
		isOperator = (ruolo >= AppConstants.RUOLO_OPERATOR);
		isEditor = (ruolo >= AppConstants.RUOLO_EDITOR);
		//UI
		if (isOperator) {
			this.setModal(true);
			this.setGlassEnabled(true);
			this.add(table);
			loadArticolo();
		}
	}
	
	private void drawArticolo() {
		int r=0;
		
		HTML titleHtml = new HTML("Articolo");
		titleHtml.setStyleName("frame-title");
		table.setWidget(r, 0, titleHtml);
		table.getFlexCellFormatter().setColSpan(r, 0, 5);
		r++;
		
		//Titolo
		table.setHTML(r, 0, "Titolo"+ClientConstants.MANDATORY);
		titoloText = new TextBox();
		titoloText.setValue(item.getTitoloNumero());
		titoloText.setMaxLength(64);
		titoloText.setEnabled(isEditor);
		table.setWidget(r, 1, titoloText);
		//Autore
		table.setHTML(r, 3, "Autore");
		autoreText = new TextBox();
		autoreText.setValue(item.getAutore());
		autoreText.setMaxLength(64);
		autoreText.setEnabled(isEditor);
		table.setWidget(r, 4, autoreText);
		r++;
		
		//Meccanografico
		table.setHTML(r, 0, "Meccanografico"+ClientConstants.MANDATORY);
		meccText = new TextBox();
		meccText.setValue(item.getCodiceMeccanografico());
		meccText.setMaxLength(6);
		meccText.setEnabled(isEditor);
		table.setWidget(r, 1, meccText);
		//Codice
		table.setHTML(r, 3, "Codice interno");
		codiceText = new TextBox();
		codiceText.setValue(item.getCodiceInterno());
		codiceText.setMaxLength(16);
		codiceText.setEnabled(isEditor);
		table.setWidget(r, 4, codiceText);
		r++;
		
		//Tipo Anagrafica SAP
		table.setHTML(r, 0, "Anagrafica SAP");
		tipoAnagraficaSap = new TipiAnagraficaSapSelect(item.getIdTipoAnagraficaSap());
		tipoAnagraficaSap.setEnabled(isEditor);
		table.setWidget(r, 1, tipoAnagraficaSap);
		//In attesa
		table.setHTML(r, 3, "In attesa");
		attesaCheck = new CheckBox();
		attesaCheck.setValue(item.getInAttesa());
		attesaCheck.setEnabled(isEditor);
		table.setWidget(r, 4, attesaCheck);
		r++;
		
		//Cartaceo
		table.setHTML(r, 0, "Cartaceo "+ClientConstants.ICON_CARTACEO);
		cartaceoCheck = new CheckBox();
		cartaceoCheck.setValue(item.getCartaceo());
		cartaceoCheck.setEnabled(isEditor);
		table.setWidget(r, 1, cartaceoCheck);
		//Digitale
		table.setHTML(r, 3, "Digitale "+ClientConstants.ICON_APP);
		digitaleCheck = new CheckBox();
		digitaleCheck.setValue(item.getDigitale());
		digitaleCheck.setEnabled(isEditor);
		table.setWidget(r, 4, digitaleCheck);
		r++;
				
		//Data inizio
		table.setHTML(r, 0, "Inizio"+ClientConstants.MANDATORY);
		beginDate = new DateBox();
		beginDate.setFormat(ClientConstants.BOX_FORMAT_DAY);
		beginDate.setValue(item.getDataInizio());
		if (isEditor) {
			table.setWidget(r, 1, beginDate);
		} else {
			table.setHTML(r, 1, ClientConstants.FORMAT_DAY.format(item.getDataInizio()));
		}
		//Data fine
		table.setHTML(r, 3, "Fine");
		endDate = new DateBox();
		endDate.setFormat(ClientConstants.BOX_FORMAT_DAY);
		endDate.setValue(item.getDataFine());
		if (isEditor) {
			table.setWidget(r, 4, endDate);
		} else {
			if (item.getDataFine() != null) {
				table.setHTML(r, 4, ClientConstants.FORMAT_DAY.format(item.getDataFine()));
			}
		}
		r++;
		
		HorizontalPanel buttonPanel = new HorizontalPanel();
		// Bottone SALVA
		Button submitButton = new Button("Salva", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				try {
					saveData();
					close();
				} catch (Exception e) {
					UiSingleton.get().addError(e);
				}
			}
		});
		if (idArticolo.equals(AppConstants.NEW_ITEM_ID)) {
			submitButton.setText("Crea");
		}
		submitButton.setEnabled(isEditor);
		buttonPanel.add(submitButton);
		
		// Bottone ANNULLA
		Button cancelButton = new Button("Annulla", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				close();
			}
		});
		buttonPanel.add(cancelButton);
		
		table.setWidget(r,0,buttonPanel);
		table.getFlexCellFormatter().setColSpan(r, 0, 5);
		
		this.center();
		this.show();
	}
	
	private void close() {
		this.hide();
	}
	
	
	
	// METODI ASINCRONI
	
	
	
	private void saveData() throws ValidationException {
		AsyncCallback<Integer> callback = new AsyncCallback<Integer>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				close();
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(Integer result) {			
				idArticolo = result;
				parent.refresh();
				close();
				UiSingleton.get().addInfo(AppConstants.MSG_SAVE_OK);
				WaitSingleton.get().stop();
			}
		};
		//Validazione
		if (meccText.getValue() == null) {
			throw new ValidationException("Il CM non e' valido");
		} else {
			if (meccText.getValue().length() < 5) {
				throw new ValidationException("Il CM non e' valido");
			}
		}
		if (titoloText.getValue() == null) {
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
		item.setDataModifica(new Date());
		item.setAutore(autoreText.getValue());
		item.setCodiceInterno(codiceText.getValue());
		String cm = "";
		if (meccText.getValue() != null) cm = meccText.getValue().toUpperCase();
		item.setCodiceMeccanografico(cm);
		item.setTitoloNumero(titoloText.getValue());
		item.setIdTipoAnagraficaSap(tipoAnagraficaSap.getSelectedValueString());
		item.setCartaceo(cartaceoCheck.getValue());
		item.setDigitale(digitaleCheck.getValue());
		item.setDataInizio(beginDate.getValue());
		item.setDataFine(endDate.getValue());
		item.setInAttesa(attesaCheck.getValue());
		item.setIdUtente(AuthSingleton.get().getUtente().getId());
		
		WaitSingleton.get().start();
		articoliService.saveOrUpdateArticolo(item, callback);
	}

	private void loadArticolo() {
		AsyncCallback<Articoli> callback = new AsyncCallback<Articoli>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(Articoli result) {
				item = result;
				drawArticolo();
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		//look for item with id only if id is defined
		if (idArticolo.intValue() != AppConstants.NEW_ITEM_ID) {
			articoliService.findArticoloById(idArticolo, callback);
		} else {
			//is new abbonamento
			articoliService.createArticolo(callback);
		}
	}
	
}
