package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.ArticoliService;
import it.giunti.apg.client.services.ArticoliServiceAsync;
import it.giunti.apg.client.widgets.select.ArticoliSelect;
import it.giunti.apg.client.widgets.select.DestinatarioSelect;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.model.ArticoliListini;
import it.giunti.apg.shared.model.Utenti;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;

public class ArticoloListinoPopUp extends PopupPanel implements IAuthenticatedWidget {

	private final ArticoliServiceAsync articoliService = GWT.create(ArticoliService.class);
	
	private FlexTable table = new FlexTable();
	private Integer idArticoloListino = null;
	private Integer idListino = null;
	private ArticoliListini item = null;
	private IRefreshable parent = null;
	private boolean isOperator = false;
	private boolean isEditor = false;
	
	private ArticoliSelect articoloList = null;
	private DestinatarioSelect destArticoloList = null;
	private TextBox giornoLimiteText = null;
	private ListBox meseLimiteList = null;
	
	public ArticoloListinoPopUp(Integer idArticoloListino, Integer idListino, IRefreshable parent) {
		super(false);
		this.idArticoloListino=idArticoloListino;
		this.idListino=idListino;
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
			loadArticoloListino();
		}
	}
	
	private void drawArticolo() {
		int r=0;
		
		HTML titleHtml = new HTML("Articolo");
		titleHtml.setStyleName("frame-title");
		table.setWidget(r, 0, titleHtml);
		table.getFlexCellFormatter().setColSpan(r, 0, 5);
		r++;
		
		//Articolo
		table.setHTML(r, 0, "Articolo");
		if (item.getArticolo() != null) { 
			articoloList = new ArticoliSelect(item.getArticolo().getId(),
				item.getListino().getDataInizio(),
				item.getListino().getDataFine(), false, false);
		} else {
			articoloList = new ArticoliSelect(null,
					item.getListino().getDataInizio(),
					item.getListino().getDataFine(), false, false);
		}
		table.setWidget(r, 1, articoloList);
		table.getFlexCellFormatter().setColSpan(r, 1, 4);
		r++;
		
		//Giorno limite
		table.setHTML(r, 0, "Giorno limite pagamento");
		giornoLimiteText = new TextBox();
		if (item.getGiornoLimitePagamento() != null) {
			giornoLimiteText.setValue(
					ClientConstants.FORMAT_INTEGER.format(item.getGiornoLimitePagamento()) );
		}
		giornoLimiteText.setEnabled(isEditor);
		giornoLimiteText.setWidth("2em");
		giornoLimiteText.setMaxLength(2);
		table.setWidget(r, 1, giornoLimiteText);
		//Mese limite
		table.setHTML(r, 3, "Mese limite pagamento");
		meseLimiteList = new ListBox();
		meseLimiteList.addItem("[nessuno]", "");
		for (int i=1;i<13;i++) {
			meseLimiteList.addItem(ClientConstants.MESI[i], i+"");
		}
		if (item.getMeseLimitePagamento() != null) {
			meseLimiteList.setSelectedIndex(item.getMeseLimitePagamento());
		}
		meseLimiteList.setEnabled(isEditor);
		table.setWidget(r, 4, meseLimiteList);
		r++;
		
		//Data inizio
		table.setHTML(r, 0, "Destinatario");
		destArticoloList = new DestinatarioSelect(item.getIdTipoDestinatario());
		table.setWidget(r, 1, destArticoloList);
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
		if (idArticoloListino.equals(AppConstants.NEW_ITEM_ID)) {
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
				idArticoloListino = result;
				parent.refresh();
				close();
				UiSingleton.get().addInfo(AppConstants.MSG_SAVE_OK);
				WaitSingleton.get().stop();
			}
		};
		//Salvataggio
		item.setIdArticoliT(articoloList.getSelectedValueInt());
		try {
			int giornoLimite = Integer.parseInt(giornoLimiteText.getValue());
			item.setGiornoLimitePagamento(giornoLimite);
		} catch (NumberFormatException e) {
			item.setGiornoLimitePagamento(null);
		}
		try {
			int meseLimite = Integer.parseInt(meseLimiteList.getValue(meseLimiteList.getSelectedIndex()));
			item.setMeseLimitePagamento(meseLimite);
		} catch (NumberFormatException e) {
			item.setMeseLimitePagamento(null);
		}
		item.setIdTipoDestinatario(destArticoloList.getSelectedValueString());
		//item.setUtente(AuthSingleton.get().getUtente());
		
		WaitSingleton.get().start();
		articoliService.saveOrUpdateArticoloListino(item, callback);
	}

	private void loadArticoloListino() {
		AsyncCallback<ArticoliListini> callback = new AsyncCallback<ArticoliListini>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(ArticoliListini result) {
				item = result;
				drawArticolo();
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		//look for item with id only if id is defined
		if (idArticoloListino.intValue() != AppConstants.NEW_ITEM_ID) {
			articoliService.findArticoloListinoById(idArticoloListino, callback);
		} else {
			//is new abbonamento
			articoliService.createArticoloListino(idListino, callback);
		}
	}
	
}
