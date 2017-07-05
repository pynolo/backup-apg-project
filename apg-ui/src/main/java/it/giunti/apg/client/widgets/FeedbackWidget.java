package it.giunti.apg.client.widgets;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.CookieSingleton;
import it.giunti.apg.client.frames.MaintenancePopUp;
import it.giunti.apg.client.services.AuthService;
import it.giunti.apg.client.services.AuthServiceAsync;
import it.giunti.apg.client.services.LoggingService;
import it.giunti.apg.client.services.LoggingServiceAsync;
import it.giunti.apg.shared.model.Avvisi;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.InlineHTML;

public class FeedbackWidget extends InlineHTML {
	
	private static LoggingServiceAsync loggingService = GWT.create(LoggingService.class);
	private static AuthServiceAsync authService = GWT.create(AuthService.class);
	
	private static final int REFRESH_INTERVAL = 60000;//millisec
	private String idUtente;
	
	public FeedbackWidget(String idUtente) {
		this.idUtente = idUtente;
		attemptFeedback();
		
		// Setup timer to refresh list automatically.
		Timer refreshTimer = new Timer() {
			@Override
			public void run() {
				attemptFeedback();
			}
		};
		refreshTimer.scheduleRepeating(REFRESH_INTERVAL);
	}
	
	private final void attemptFeedback() {
		//Verifica manutenzione
		checkMaintenance();
		//Segnale heartbeat
		sendHeartbeat(idUtente);
	}
	
	public static void checkMaintenance() {
		AsyncCallback<Avvisi> callback = new AsyncCallback<Avvisi>() {
			@Override
			public void onFailure(Throwable caught) {
				//do nothing // UiSingleton.get().addError(caught);
			}
			@Override
			public void onSuccess(Avvisi avviso) {
				if (avviso != null) {
					Integer id = 0;
					String idS = CookieSingleton.get().getCookie(ClientConstants.COOKIE_LAST_ID_MAINTENANCE);
					if (idS != null) {
						try {
							id = Integer.parseInt(idS);
						} catch (NumberFormatException e) {}
					}
					if (avviso.getId() > id) {
						new MaintenancePopUp(avviso);
					}
				}
			}
		};
		loggingService.checkMaintenance(callback);
	}
	
	public static void sendHeartbeat(String idUtente) {
		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				//do nothing // UiSingleton.get().addError(caught);
			}
			@Override
			public void onSuccess(Boolean done) {
				//do nothing
			}
		};
		authService.sendHeartbeat(idUtente, callback);
	}
}
