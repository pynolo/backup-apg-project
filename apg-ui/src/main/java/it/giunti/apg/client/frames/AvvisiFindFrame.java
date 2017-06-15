package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.LoggingService;
import it.giunti.apg.client.services.LoggingServiceAsync;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.client.widgets.tables.AvvisiTable;
import it.giunti.apg.client.widgets.tables.DataModel;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Avvisi;
import it.giunti.apg.shared.model.Utenti;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AvvisiFindFrame extends FramePanel implements IAuthenticatedWidget {
		
	private VerticalPanel panel = null;
	private CheckBox importantCheck = null;
	private TextBox msgText = null;
	private AvvisiTable notizieTable = null;
	
	public AvvisiFindFrame(UriParameters params) {
		super();
		if (params == null) {
			params = new UriParameters();
		}
		AuthSingleton.get().queueForAuthentication(this);
	}
	
	@Override
	public void onSuccessfulAuthentication(Utenti utente) {
		init(utente);
	}
	
	private void init(Utenti utente) {
		// Editing rights
		int ruolo = utente.getRuolo().getId();
		// UI
		if (ruolo >= AppConstants.RUOLO_ADMIN) {
			draw();
		}
	}

	private void draw() {
		panel = new VerticalPanel();
		this.add(panel, "Avvisi");
		
		HorizontalPanel msgHolder = new HorizontalPanel();
		//Messaggio
		msgHolder.add(new HTML("Messaggio:"));
		msgText = new TextBox();
		msgText.setMaxLength(255);
		msgText.setWidth("35em");
		msgHolder.add(msgText);
		//Tipo
		msgHolder.add(new HTML("&nbsp;In evidenza:"));
		importantCheck = new CheckBox();
		msgHolder.add(importantCheck);
		//Submit
		Button submitButton = new Button("Crea", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				createAvviso(importantCheck.getValue(),
						msgText.getValue(), AuthSingleton.get().getUtente());
			}
		});
		msgHolder.add(submitButton);
		DataModel<Avvisi> model = new AvvisiTable.AvvisiModel();
		notizieTable = new AvvisiTable(model);
		panel.add(msgHolder);
		panel.add(notizieTable);
	}

	private void createAvviso(boolean importante, String message, Utenti utente) {
		final LoggingServiceAsync loggingService = GWT.create(LoggingService.class);
		AsyncCallback<Integer> callback = new AsyncCallback<Integer>() {
			@Override
			public void onFailure(Throwable e) {
				UiSingleton.get().addError(e);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(Integer arg0) {
				notizieTable.refresh();
				msgText.setValue("");
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		loggingService.saveAvviso(message, importante, utente.getId(), callback);
	}
}
