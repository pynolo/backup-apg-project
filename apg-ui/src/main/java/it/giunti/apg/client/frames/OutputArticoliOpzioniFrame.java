package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.LoggingService;
import it.giunti.apg.client.services.LoggingServiceAsync;
import it.giunti.apg.client.widgets.DownloadIFrame;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.client.widgets.select.ArticoliOpzioniPendingSelect;
import it.giunti.apg.client.widgets.tables.DataModel;
import it.giunti.apg.client.widgets.tables.LogTable;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Utenti;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class OutputArticoliOpzioniFrame extends FramePanel implements IAuthenticatedWidget {
	
	private static final String TITLE_FORM = "Invio materiali per opzione";
	private static final String TITLE_LOG = "Rapporto";

	private SimplePanel panelForm = null;
	private VerticalPanel panelLog = null;
	private ArticoliOpzioniPendingSelect artOpzList = null;
	private CheckBox scriviDbCheck = null;
	
	
	public OutputArticoliOpzioniFrame(UriParameters params) {
		super();
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
		if (ruolo >= AppConstants.RUOLO_EDITOR) {
			draw();
		}
	}
	
	private void draw() {
		//Form Panel
		EstrazionePanel estrazionePanel = new EstrazionePanel();
		panelForm = new SimplePanel();
		panelForm.add(estrazionePanel);
		this.add(panelForm, TITLE_FORM);
		//Log Panel
		panelLog = new VerticalPanel();
		this.add(panelLog, TITLE_LOG);
	}
		
	private void showLogTable(int idRapporto) {
		DataModel<String> model = new LogTable.LogModel(idRapporto);
		LogTable logTable = new LogTable(model);
		panelLog.clear();
		panelLog.add(logTable);
		this.showWidget(1);
	}
		
	
	
	//Async methods
	
	
	
	private void createRapporto(EstrazionePanel form, String idUtente) {
		LoggingServiceAsync loggingService = GWT.create(LoggingService.class);
		final EstrazionePanel fForm = form;
		String titolo = TITLE_FORM + " " + artOpzList.getSelectedValueDescription();
		AsyncCallback<Integer> callback = new AsyncCallback<Integer>() {
			@Override
			public void onFailure(Throwable e) {
				WaitSingleton.get().stop();
				UiSingleton.get().addError(e);
			}
			@Override
			public void onSuccess(Integer result) {
				WaitSingleton.get().stop();
				fForm.submit(result);
			}
		};
		WaitSingleton.get().start();
		loggingService.createRapporto(titolo, idUtente, callback);
	}
	
	
	
	//Inner classes
	
	

	private class EstrazionePanel extends SimplePanel {
		private Hidden utenteHid;
		private Hidden rapportoHid;
		
		public EstrazionePanel() {
			final EstrazionePanel thisForm = this;
			//RestrictionsPanel
			VerticalPanel restrictionPanel = new VerticalPanel();
			this.add(restrictionPanel);
			//this.setMethod(FormPanel.METHOD_POST);
			//this.setAction(GWT.getModuleBaseURL()+AppConstants.SERVLET_OUTPUT_FASCICOLI);
			
			//Periodico panel
			FlowPanel periodicoPanel = new FlowPanel();
			// Elenco
			periodicoPanel.add(new HTML("&nbsp;Da&nbsp;inviare&nbsp;"));
			artOpzList = new ArticoliOpzioniPendingSelect(false, false);
			artOpzList.setName(AppConstants.PARAM_ID);
			periodicoPanel.add(artOpzList);
			restrictionPanel.add(periodicoPanel);
			
			//Scrivi su DB
			FlowPanel scriviDbPanel = new FlowPanel();
			scriviDbPanel.add(new HTML("Memorizza l'estrazione"));
			scriviDbCheck = new CheckBox();
			scriviDbCheck.setName(AppConstants.PARAM_SCRIVI_DB);
			scriviDbPanel.add(scriviDbCheck);
			restrictionPanel.add(scriviDbPanel);
			
			//Bottone
			FlowPanel buttonPanel = new FlowPanel();
			Button submitButton = new Button("Estrai", new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					createRapporto(thisForm, AuthSingleton.get().getUtente().getId());
				}
			});
			buttonPanel.add(submitButton);
			// Hidden
			utenteHid = new Hidden(AppConstants.PARAM_ID_UTENTE);
			utenteHid.setValue(AuthSingleton.get().getUtente().getId());
			buttonPanel.add(utenteHid);
			rapportoHid = new Hidden(AppConstants.PARAM_ID_RAPPORTO);
			rapportoHid.setValue("0");
			buttonPanel.add(rapportoHid);
			restrictionPanel.add(buttonPanel);
		}
		
		public void submit(int idRapporto) {
			rapportoHid.setValue(idRapporto+"");
			//submit();
			String servletURL = GWT.getModuleBaseURL()+AppConstants.SERVLET_OUTPUT_ARTICOLI_OPZIONI + 
					"?" + AppConstants.PARAM_ID + "=" + artOpzList.getSelectedValueString() +
					"&" + AppConstants.PARAM_SCRIVI_DB + "=" + scriviDbCheck.getValue() +
					"&" + AppConstants.PARAM_ID_UTENTE + "=" + utenteHid.getValue() +
					"&" + AppConstants.PARAM_ID_RAPPORTO + "=" + rapportoHid.getValue();
			new DownloadIFrame(servletURL);
			showLogTable(idRapporto);
		}
	}

}
