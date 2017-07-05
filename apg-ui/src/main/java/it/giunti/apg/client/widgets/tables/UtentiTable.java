package it.giunti.apg.client.widgets.tables;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.frames.UtentePopUp;
import it.giunti.apg.client.services.AuthService;
import it.giunti.apg.client.services.AuthServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Utenti;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;

public class UtentiTable extends PagingTable<Utenti> implements IRefreshable {
	
	private static final int TABLE_ROWS = AppConstants.TABLE_ROWS_DEFAULT;
	private static final int LOGGED_MAX_MILLIS = 180000;//3 minutes
	
	private boolean isAdmin = false;
	
	private AsyncCallback<List<Utenti>> callback = new AsyncCallback<List<Utenti>>() {
		@Override
		public void onFailure(Throwable caught) {
			setTableRows(new ArrayList<Utenti>());
			//WaitSingleton.get().stop();
		}
		@Override
		public void onSuccess(List<Utenti> result) {
			setTableRows(result);
			//WaitSingleton.get().stop();
		}
	};
	
	public UtentiTable(DataModel<Utenti> model, Utenti utente) {
		super(model, TABLE_ROWS);
		int ruolo = utente.getRuolo().getId();
		isAdmin = (ruolo >= AppConstants.RUOLO_ADMIN);
		drawPage(0);
	}

	@Override
	public void drawPage(int page) {
		clearInnerTable();
		getInnerTable().setHTML(0, 0, ClientConstants.LABEL_LOADING);
		getModel().find(page*TABLE_ROWS,
				TABLE_ROWS,
				callback);
	}
	
	@Override
	public void refresh() {
		drawPage(0);
	}
	
	@Override
	protected void addTableRow(int rowNum, Utenti rowObj) {
		final Utenti rowFinal = rowObj;
		final UtentiTable utentiTable = this;
		// Set the data in the current row
		HTML semaforo = new HTML();
		boolean isOffline = true;
		if (rowObj.getHeartbeat() != null) {
			isOffline = (new Date().getTime() > rowObj.getHeartbeat().getTime()+LOGGED_MAX_MILLIS);
		}
		if (isOffline) {
			//Offline
			semaforo.setHTML("<i class='text-muted fa fa-user-circle-o' aria-hidden='true'></i>");
			String title = "Offline ";
			if (rowObj.getHeartbeat() != null) title +="dal "+ClientConstants.FORMAT_DATETIME.format(rowObj.getHeartbeat());
			semaforo.setTitle(title);
		} else {
			//Online
			semaforo.setHTML("<i class='text-success fa fa-user-circle' aria-hidden='true'></i>");
			semaforo.setTitle("Online");
		}
		getInnerTable().setWidget(rowNum, 0, semaforo);
		//username
		if (isAdmin) {
			String linkText = "<b>"+rowObj.getId()+"</b>";
			Anchor rowLink = new Anchor(linkText, true);
			rowLink.addMouseDownHandler(new MouseDownHandler() {
				@Override
				public void onMouseDown(MouseDownEvent event) {
					new UtentePopUp(rowFinal.getId(), utentiTable);
				}
			});
			getInnerTable().setWidget(rowNum, 1, rowLink);
		} else {
			getInnerTable().setHTML(rowNum, 1, "<b>"+rowObj.getId()+"</b>");
		}
		//descrizione
		getInnerTable().setHTML(rowNum, 2, rowObj.getDescrizione());
		//ruolo
		String ruoloLabel = "";
		if (rowObj.getRuolo().getId() == AppConstants.RUOLO_BLOCKED) ruoloLabel += ClientConstants.ICON_AMBULANCE + "&nbsp;";
		if (rowObj.getRuolo().getId() == AppConstants.RUOLO_OPERATOR) ruoloLabel += ClientConstants.ICON_USER_OPERATOR + "&nbsp;";
		if (rowObj.getRuolo().getId() == AppConstants.RUOLO_EDITOR) ruoloLabel += ClientConstants.ICON_USER_EDITOR + "&nbsp;";
		if (rowObj.getRuolo().getId() == AppConstants.RUOLO_ADMIN) ruoloLabel += ClientConstants.ICON_USER_ADMIN + "&nbsp;";
		if (rowObj.getRuolo().getId() == AppConstants.RUOLO_SUPER) ruoloLabel += ClientConstants.ICON_USER_ADMIN + "&nbsp;";
		ruoloLabel += rowObj.getRuolo().getDescrizione();
		getInnerTable().setHTML(rowNum, 3, ruoloLabel);
		//nel dominio?
		if (rowObj.getPassword().equals("")) {
			Image checkImg = new Image("img/check-green.png");
			getInnerTable().setWidget(rowNum, 4, checkImg);
		} else {
			//Image checkImg = new Image("img/fail-red.png");
			//getInnerTable().setWidget(rowNum, 3, checkImg);
			getInnerTable().setHTML(rowNum, 4, "&nbsp;");
		}
		//Restrizioni
		getInnerTable().setHTML(rowNum, 5, rowObj.getPeriodiciUidRestriction());
	}

	@Override
	protected void addHeader() {
		// Set the data in the current row
		getInnerTable().setHTML(0, 1, "Nome utente");
		getInnerTable().setHTML(0, 2, "Descrizione");
		getInnerTable().setHTML(0, 3, "Ruolo");
		getInnerTable().setHTML(0, 4, "Aziendale");
		getInnerTable().setHTML(0, 5, "Restrizioni");
	}
	
	@Override
	protected void onEmptyResult() {}
	
	//private void confirmAndDelete(String idUtente) {
	//	boolean confirm = Window.confirm("Vuoi veramente cancellare l'utente?");
	//	if (confirm) {
	//		delete(idUtente);
	//	}
	//}
	//
	//public void delete(String idUtente) {
	//	AuthServiceAsync authService = GWT.create(AuthService.class);
	//	//WaitSingleton.get().start();
	//	authService.delete(idUtente, callback);
	//}
	
	
	
	
	//Inner classes
	
	
	
	public static class UtentiModel implements DataModel<Utenti> {
		private final AuthServiceAsync authService = GWT.create(AuthService.class);
		boolean showBlocked = false;
		
		public UtentiModel(boolean showBlocked) {
			this.showBlocked = showBlocked;
		}

		@Override
		public void find(int offset, int pageSize, AsyncCallback<List<Utenti>> callback) {
			//WaitSingleton.get().start();
			authService.findUtenti(showBlocked, offset, pageSize, callback);
		}
	}
	
}
