package it.giunti.apg.client.frames;

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

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.MaterialiService;
import it.giunti.apg.client.services.MaterialiServiceAsync;
import it.giunti.apg.client.widgets.DateSafeBox;
import it.giunti.apg.client.widgets.select.TipiAnagraficaSapSelect;
import it.giunti.apg.client.widgets.select.TipiMaterialeSelect;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.model.Materiali;
import it.giunti.apg.shared.model.Utenti;

public class MaterialiPopUp extends PopupPanel implements IAuthenticatedWidget {

	private final MaterialiServiceAsync matService = GWT.create(MaterialiService.class);
	
	private FlexTable table = new FlexTable();
	private Integer idMat = null;
	private Materiali item = null;
	private IRefreshable parent = null;
	private boolean isOperator = false;
	private boolean isEditor = false;
	
	private TipiMaterialeSelect tipoMaterialeList = null;
	private TextBox meccText = null;
	private TextBox titoloText = null;
	private TextBox autoreText = null;
	private TipiAnagraficaSapSelect tipoAnagraficaSap = null;
	private DateSafeBox limiteDate = null;
	private CheckBox attesaCheck = null;
	
	public MaterialiPopUp(Integer idMat, IRefreshable parent) {
		super(false);
		this.idMat=idMat;
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
			loadMateriale();
		}
	}
	
	private void drawMateriale() {
		int r=0;
		
		HTML titleHtml = new HTML("Materiale");
		titleHtml.setStyleName("frame-title");
		table.setWidget(r, 0, titleHtml);
		table.getFlexCellFormatter().setColSpan(r, 0, 5);
		r++;
		
		//Titolo
		table.setHTML(r, 0, "Titolo"+ClientConstants.MANDATORY);
		titoloText = new TextBox();
		titoloText.setValue(item.getTitolo());
		titoloText.setMaxLength(64);
		titoloText.setEnabled(isEditor);
		table.setWidget(r, 1, titoloText);
		//Autore
		table.setHTML(r, 3, "Descrizione");
		autoreText = new TextBox();
		autoreText.setValue(item.getSottotitolo());
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
		r++;
		
		//Tipo Materiale
		table.setHTML(r, 0, "Tipo materiale");
		tipoMaterialeList = new TipiMaterialeSelect(item.getIdTipoMateriale());
		tipoMaterialeList.setEnabled(isEditor);
		table.setWidget(r, 1, tipoMaterialeList);
		//Tipo Anagrafica SAP
		table.setHTML(r, 3, "Anagrafica SAP");
		tipoAnagraficaSap = new TipiAnagraficaSapSelect(item.getIdTipoAnagraficaSap());
		tipoAnagraficaSap.setEnabled(isEditor);
		table.setWidget(r, 4, tipoAnagraficaSap);
		r++;

		//Data fine visibilità
		table.setHTML(r, 0, "Visibile fino a");
		limiteDate = new DateSafeBox();
		limiteDate.setFormat(ClientConstants.BOX_FORMAT_DAY);
		limiteDate.setValue(item.getDataLimiteVisibilita());
		if (isEditor) {
			table.setWidget(r, 1, limiteDate);
		} else {
			table.setHTML(r, 1, ClientConstants.FORMAT_DAY.format(item.getDataLimiteVisibilita()));
		}
		//In attesa
		table.setHTML(r, 3, "In attesa");
		attesaCheck = new CheckBox();
		attesaCheck.setValue(item.getInAttesa());
		attesaCheck.setEnabled(isEditor);
		table.setWidget(r, 4, attesaCheck);
		r++;
		
		HorizontalPanel buttonPanel = new HorizontalPanel();
		// Bottone SALVA
		Button submitButton = new Button(ClientConstants.ICON_SAVE+" Salva", new ClickHandler() {
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
		if (idMat.equals(AppConstants.NEW_ITEM_ID)) {
			submitButton.setHTML(ClientConstants.ICON_SAVE+" Crea");
		}
		submitButton.setEnabled(isEditor);
		buttonPanel.add(submitButton);
		
		// Bottone ANNULLA
		Button cancelButton = new Button(ClientConstants.ICON_CANCEL+" Annulla", new ClickHandler() {
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
				idMat = result;
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
		//if (limiteDate.getValue() == null) {
		//	throw new ValidationException("La fine visibilita' non non puo' essere vuota");
		//}
		//Salvataggio
		item.setSottotitolo(autoreText.getValue());
		String cm = "";
		if (meccText.getValue() != null) cm = meccText.getValue().toUpperCase();
		item.setCodiceMeccanografico(cm);
		item.setTitolo(titoloText.getValue());
		item.setIdTipoMateriale(tipoMaterialeList.getSelectedValue());
		item.setIdTipoAnagraficaSap(tipoAnagraficaSap.getSelectedValueString());
		item.setDataLimiteVisibilita(limiteDate.getValue());
		item.setInAttesa(attesaCheck.getValue());
		
		WaitSingleton.get().start();
		matService.saveOrUpdateMateriale(item, callback);
	}

	private void loadMateriale() {
		AsyncCallback<Materiali> callback = new AsyncCallback<Materiali>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(Materiali result) {
				item = result;
				drawMateriale();
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		//look for item with id only if id is defined
		if (idMat.intValue() != AppConstants.NEW_ITEM_ID) {
			matService.findMaterialeById(idMat, callback);
		} else {
			//is new abbonamento
			matService.createMateriale(AppConstants.MATERIALE_FASCICOLO,
					AppConstants.ANAGRAFICA_SAP_GE_FASCICOLO, callback);
		}
	}
	
}
