package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.LoggingService;
import it.giunti.apg.client.services.LoggingServiceAsync;
import it.giunti.apg.client.widgets.DateOnlyBox;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.client.widgets.tables.AvvisiTable;
import it.giunti.apg.client.widgets.tables.DataModel;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.model.Avvisi;
import it.giunti.apg.shared.model.Utenti;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DateBox;

public class AvvisiFindFrame extends FramePanel implements IAuthenticatedWidget {
	
	private VerticalPanel panel = null;
	private CheckBox importantCheck = null;
	private TextArea msgText = null;
	private DateOnlyBox maintenanceDt = null;
	private DateBox startTimeText = null;
	private DateBox finishTimeText = null;
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
		
		FlexTable msgHolder = new FlexTable();
		//Messaggio
		msgHolder.setHTML(0,0,"Messaggio <i>(max 1000 caratteri)</i>&nbsp;");
		msgText = new TextArea();
		msgText.setSize("15em", "3em");
		msgText.setWidth("35em");
		msgHolder.setWidget(1,0,msgText);
		//Tipo
		msgHolder.setHTML(0,1,"In evidenza&nbsp;");
		importantCheck = new CheckBox();
		msgHolder.setWidget(1,1,importantCheck);
		//Manutenzione
		msgHolder.setHTML(0, 2, "Data manutenzione&nbsp;");
		maintenanceDt = new DateOnlyBox();
		maintenanceDt.setFormat(ClientConstants.BOX_FORMAT_DAY);
		maintenanceDt.setWidth("7em");
		msgHolder.setWidget(1, 2, maintenanceDt);
		//Inizio
		msgHolder.setHTML(0, 3, "inizio&nbsp;");
		startTimeText = new DateBox();
		startTimeText.setFormat(ClientConstants.BOX_FORMAT_TIME);
		startTimeText.setWidth("4em");
		msgHolder.setWidget(1, 3, startTimeText);
		//Fine
		msgHolder.setHTML(0, 4, "fine");
		finishTimeText = new DateBox();
		finishTimeText.setFormat(ClientConstants.BOX_FORMAT_TIME);
		finishTimeText.setWidth("4em");
		msgHolder.setWidget(1, 4, finishTimeText);
		//Submit
		Button submitButton = new Button("Crea", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				try {
					createAvviso();
				} catch (ValidationException e) {
					UiSingleton.get().addError(e);
				}
			}
		});
		msgHolder.setWidget(1, 5, submitButton);
		DataModel<Avvisi> model = new AvvisiTable.AvvisiModel();
		notizieTable = new AvvisiTable(model);
		panel.add(msgHolder);
		panel.add(notizieTable);
	}

	private void createAvviso() throws ValidationException {
		final LoggingServiceAsync loggingService = GWT.create(LoggingService.class);
		Avvisi avviso = new Avvisi();
		avviso.setData(DateUtil.now());
		avviso.setIdUtente(AuthSingleton.get().getUtente().getId());
		avviso.setImportante(importantCheck.getValue());
		String msg = msgText.getValue();
		msg = msg.replaceAll("\n", "<br/>");//a capo
		msg = msg.replaceAll("\r", "<br/>");//a capo
		if (msg.length() > 1024) msg = msg.substring(0,1024);
		avviso.setMessaggio(msg);
		avviso.setDataManutenzione(maintenanceDt.getValue());
		avviso.setOraInizio(startTimeText.getValue());
		avviso.setOraFine(finishTimeText.getValue());
		if (avviso.getDataManutenzione() != null && avviso.getOraInizio() == null) {
			throw new ValidationException("L'ora di inizio è obbligatoria");
		}
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
		loggingService.saveAvviso(avviso, callback);
	}
	
}
