package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.LoggingService;
import it.giunti.apg.client.services.LoggingServiceAsync;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Rapporti;
import it.giunti.apg.shared.model.Utenti;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class RapportoFrame extends FramePanel implements IAuthenticatedWidget {
	
//	private boolean editable = false;
	private static final DateTimeFormat DTF = DateTimeFormat.getFormat("dd/MM/yyyy HH:mm");
	
	private Rapporti item = null;
	private Integer idRapporto = null;
	private VerticalPanel panel = null;
	
	public RapportoFrame(UriParameters params) {
		super();
		if (params == null) {
			params = new UriParameters();
		}
		idRapporto = params.getIntValue(AppConstants.PARAM_ID);
		AuthSingleton.get().queueForAuthentication(this);
	}
	
	@Override
	public void onSuccessfulAuthentication(Utenti utente) {
		init(utente);
	}
	
	private void init(Utenti utente) {
		// Editing rights
		int ruolo = utente.getRuolo().getId();
//		if (ruolo >= AppConstants.RUOLO_EDITOR) {
//			editable=true;
//		}
		// UI
		if (ruolo >= AppConstants.RUOLO_EDITOR) {
			loadRapporto();
		}
	}
	
	private void draw() {
		panel = new VerticalPanel();
		this.add(panel, "Rapporto");
		HTML title = new HTML("<h3>"+ DTF.format(item.getDataModifica()) + " - " +
				item.getTitolo() + "</h3>");
		panel.add(title);
		HTML body = new HTML(item.getTesto());
		panel.add(body);
	}


	// METODI ASINCRONI
	
	
	private void loadRapporto() {
		LoggingServiceAsync loggingService = GWT.create(LoggingService.class);
		AsyncCallback<Rapporti> callback = new AsyncCallback<Rapporti>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(Rapporti result) {
				item = result;
				draw();
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		loggingService.findRapportoById(idRapporto, callback);
	}
}
