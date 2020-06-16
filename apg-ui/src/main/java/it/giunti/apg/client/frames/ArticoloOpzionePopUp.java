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

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.MaterialiService;
import it.giunti.apg.client.services.MaterialiServiceAsync;
import it.giunti.apg.client.widgets.MaterialiPanel;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.model.ArticoliOpzioni;
import it.giunti.apg.shared.model.Utenti;

public class ArticoloOpzionePopUp extends PopupPanel implements IAuthenticatedWidget {

	private final MaterialiServiceAsync matService = GWT.create(MaterialiService.class);
	
	private FlexTable table = new FlexTable();
	private Integer idArticoloOpzione = null;
	private Integer idOpzione = null;
	private ArticoliOpzioni item = null;
	private IRefreshable parent = null;
	private boolean isOperator = false;
	private boolean isEditor = false;
	
	private MaterialiPanel materialiPanel = null;
	
	public ArticoloOpzionePopUp(Integer idArticoloOpzione, Integer idOpzione, IRefreshable parent) {
		super(false);
		this.idArticoloOpzione=idArticoloOpzione;
		this.idOpzione=idOpzione;
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
			loadArticoloOpzione();
		}
	}
	
	private void drawArticolo() {
		int r=0;
		
		HTML titleHtml = new HTML("Materiale abbinato all'opzione");
		titleHtml.setStyleName("frame-title");
		table.setWidget(r, 0, titleHtml);
		table.getFlexCellFormatter().setColSpan(r, 0, 5);
		r++;
		
		//Materiale
		table.setHTML(r, 0, "Materiale");
		materialiPanel = new MaterialiPanel(item.getMateriale().getId(), 30, isEditor);
		table.setWidget(r, 1, materialiPanel);
		table.getFlexCellFormatter().setColSpan(r, 1, 4);
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
		if (idArticoloOpzione.equals(AppConstants.NEW_ITEM_ID)) {
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
				idArticoloOpzione = result;
				parent.refresh();
				close();
				UiSingleton.get().addInfo(AppConstants.MSG_SAVE_OK);
				WaitSingleton.get().stop();
			}
		};
		//Salvataggio
		item.setMaterialeCmT(materialiPanel.getCodiceMeccanografico());
		//item.setUtente(AuthSingleton.get().getUtente());
		
		WaitSingleton.get().start();
		matService.saveOrUpdateArticoloOpzione(item, callback);
	}

	private void loadArticoloOpzione() {
		AsyncCallback<ArticoliOpzioni> callback = new AsyncCallback<ArticoliOpzioni>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(ArticoliOpzioni result) {
				item = result;
				drawArticolo();
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		//look for item with id only if id is defined
		if (idArticoloOpzione.intValue() != AppConstants.NEW_ITEM_ID) {
			matService.findArticoloOpzioneById(idArticoloOpzione, callback);
		} else {
			//is new abbonamento
			matService.createArticoloOpzione(idOpzione, callback);
		}
	}
	
}
