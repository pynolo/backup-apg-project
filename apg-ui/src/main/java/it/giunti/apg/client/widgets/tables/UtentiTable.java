package it.giunti.apg.client.widgets.tables;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.frames.UtentePopUp;
import it.giunti.apg.client.services.AuthService;
import it.giunti.apg.client.services.AuthServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.Utenti;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;

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
			isOffline = (DateUtil.now().getTime() > rowObj.getHeartbeat().getTime()+LOGGED_MAX_MILLIS);
		}
		if (isOffline) {
			//Offline
			semaforo.setHTML("<i class='text-muted fa fa-user-circle-o' aria-hidden='true'></i>");
			semaforo.setTitle("Offline");
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
		//ruolo
		String ruoloLabel = "";
		if (rowObj.getRuolo().getId() == AppConstants.RUOLO_BLOCKED) ruoloLabel = ClientConstants.ICON_AMBULANCE + "&nbsp;";
		if (rowObj.getRuolo().getId() == AppConstants.RUOLO_OPERATOR) ruoloLabel = ClientConstants.ICON_USER_OPERATOR + "&nbsp;";
		if (rowObj.getRuolo().getId() == AppConstants.RUOLO_EDITOR) ruoloLabel = ClientConstants.ICON_USER_EDITOR + "&nbsp;";
		if (rowObj.getRuolo().getId() == AppConstants.RUOLO_ADMIN) ruoloLabel = ClientConstants.ICON_USER_ADMIN + "&nbsp;";
		if (rowObj.getRuolo().getId() == AppConstants.RUOLO_SUPER) ruoloLabel = ClientConstants.ICON_USER_SUPER + "&nbsp;";
		ruoloLabel += rowObj.getRuolo().getDescrizione();
		getInnerTable().setHTML(rowNum, 2, ruoloLabel);
		//Tipo utenza
		if (rowObj.getAziendale()) {
			getInnerTable().setHTML(rowNum, 3, "<i class='fa fa-check-square-o' aria-hidden='true'></i> Intranet");
		} else {
			getInnerTable().setHTML(rowNum, 3, "<span class='text-muted'><i class='fa fa-globe' aria-hidden='true'></i> Esterna</span>");
		}
		//Restrizioni
		getInnerTable().setHTML(rowNum, 4, rowObj.getPeriodiciUidRestriction());
		//Ultima attività
		String heartbeat = "--";
		if (rowObj.getHeartbeat() != null) heartbeat = ClientConstants.FORMAT_DATETIME.format(rowObj.getHeartbeat());
		getInnerTable().setHTML(rowNum, 5, heartbeat);
		//Note
		getInnerTable().setHTML(rowNum, 6, rowObj.getDescrizione());		
	}

	@Override
	protected void addHeader() {
		// Set the data in the current row
		getInnerTable().setHTML(0, 1, "Nome utente&nbsp;");
		getInnerTable().setHTML(0, 2, "Ruolo&nbsp;");
		getInnerTable().setHTML(0, 3, "Tipo utenza&nbsp;");
		getInnerTable().setHTML(0, 4, "Restrizioni&nbsp;");
		getInnerTable().setHTML(0, 5, "Ultima attivit&agrave;&nbsp;");
		getInnerTable().setHTML(0, 6, "Note");
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
