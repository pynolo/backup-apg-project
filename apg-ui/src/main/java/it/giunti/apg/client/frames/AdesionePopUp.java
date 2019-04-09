package it.giunti.apg.client.frames;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
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
import it.giunti.apg.client.services.LookupService;
import it.giunti.apg.client.services.LookupServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.model.Adesioni;
import it.giunti.apg.shared.model.Utenti;

public class AdesionePopUp extends PopupPanel implements IAuthenticatedWidget {

	private final LookupServiceAsync lookupService = GWT.create(LookupService.class);
	
	private FlexTable table = new FlexTable();
	private Integer idAdesione = null;
	private Adesioni item = null;
	private IRefreshable parent = null;
	private boolean isAdmin = false;
	
	private TextBox codiceText = null;
	
	public AdesionePopUp(Integer idAdesione, IRefreshable parent) {
		super(false);
		this.idAdesione=idAdesione;
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
		isAdmin = (ruolo >= AppConstants.RUOLO_ADMIN);
		//UI
		if (isAdmin) {
			this.setModal(true);
			this.setGlassEnabled(true);
			this.add(table);
			loadAdesione();
		}
	}
	
	private void draw() {
		int r=0;
		
		HTML titleHtml = new HTML("Adesione");
		titleHtml.setStyleName("frame-title");
		table.setWidget(r, 0, titleHtml);
		table.getFlexCellFormatter().setColSpan(r, 0, 5);
		r++;
		
		//Adesione
		table.setHTML(r, 0, "Adesione"+ClientConstants.MANDATORY);
		codiceText = new TextBox();
		codiceText.setValue(item.getCodice());
		codiceText.setMaxLength(64);
		codiceText.setEnabled(isAdmin);
		table.setWidget(r, 1, codiceText);
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
		if (idAdesione.equals(AppConstants.NEW_ITEM_ID)) {
			submitButton.setHTML(ClientConstants.ICON_SAVE+" Crea");
		}
		submitButton.setEnabled(isAdmin);
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
				idAdesione = result;
				parent.refresh();
				close();
				UiSingleton.get().addInfo(AppConstants.MSG_SAVE_OK);
				WaitSingleton.get().stop();
			}
		};
		//Validazione
		String val = codiceText.getValue();
		if (val == null) val = "";
		if (val.length() == 0) {
			throw new ValidationException("L'adesione non puo' essere vuota");
		}
		//Salvataggio
		item.setCodice(val);
		
		WaitSingleton.get().start();
		lookupService.saveOrUpdateAdesione(item, callback);
	}

	private void loadAdesione() {
		AsyncCallback<Adesioni> callback = new AsyncCallback<Adesioni>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(Adesioni result) {
				item = result;
				draw();
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		//look for item with id only if id is defined
		if (idAdesione.intValue() != AppConstants.NEW_ITEM_ID) {
			lookupService.findAdesioneById(idAdesione, callback);
		} else {
			//is new adesione
			lookupService.createAdesione(callback);
		}
	}
	
}
