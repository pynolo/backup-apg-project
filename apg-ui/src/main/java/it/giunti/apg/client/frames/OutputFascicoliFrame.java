package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.CookieSingleton;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.LoggingService;
import it.giunti.apg.client.services.LoggingServiceAsync;
import it.giunti.apg.client.widgets.DownloadIFrame;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.client.widgets.select.FascicoliSelect;
import it.giunti.apg.client.widgets.select.PeriodiciSelect;
import it.giunti.apg.client.widgets.tables.DataModel;
import it.giunti.apg.client.widgets.tables.LogTable;
import it.giunti.apg.core.DateUtil;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.Utenti;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class OutputFascicoliFrame extends FramePanel implements IAuthenticatedWidget {
	
	private static final String TITLE_FORM = "Invio fascicoli";
	private static final String TITLE_LOG = "Rapporto";

	private Integer idPeriodico = null;
	private Utenti utente = null;
	
	private SimplePanel panelForm = null;
	private VerticalPanel panelLog = null;
	private FascicoliSelect fascicoliList = null;
	private PeriodiciSelect periodiciList = null;
	private ListBox copieList = null;
	private ListBox italiaList = null;
	//private ListBox conOpzioneList = null;
	//private OpzioniSelect opzioniList = null;
	
	private CheckBox scriviDbCheck = null;
	//private CheckBox scriviDataEstrazioneCheck = null;
	
	public OutputFascicoliFrame(UriParameters params) {
		super();
		AuthSingleton.get().queueForAuthentication(this);
	}
	
	@Override
	public void onSuccessfulAuthentication(Utenti utente) {
		this.utente = utente;
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
		idPeriodico = ValueUtil.stoi(CookieSingleton.get().getCookie(ClientConstants.COOKIE_LAST_PERIODICO));
		if (idPeriodico == null) idPeriodico=UiSingleton.get().getDefaultIdPeriodico(utente);
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
	
	private void updateFascicoliList() {
		if (periodiciList != null) {
			if (periodiciList.getItemCount() > 0) {
				if (periodiciList.getSelectedValueString() != null) {
					idPeriodico = periodiciList.getSelectedValueInt();
					CookieSingleton.get().setCookie(ClientConstants.COOKIE_LAST_PERIODICO, idPeriodico+"");
				}
			}
		}
		if (fascicoliList == null) {
			fascicoliList = new FascicoliSelect();
			//fascicoliList.setVisibleItemCount(1);
		}
		long now = DateUtil.now().getTime();
		long startDt = now - AppConstants.MONTH * 6;
		long finishDt = now + AppConstants.MONTH * 4;
		fascicoliList.reload(
				AppConstants.NEW_ITEM_ID,
				idPeriodico, startDt, finishDt, true, true, false, true, false);
	}
	
	//private void updateOpzioniList() {
	//	if (periodiciList == null) return;
	//	if (periodiciList.getItemCount() < 1) return;
	//	if (periodiciList.getSelectedValueString() == null) return;
	//	Integer idPeriodico = periodiciList.getSelectedValueInt();
	//	Date today = DateUtil.now();
	//	Date startDt = new Date(today.getTime() - (AppConstants.MONTH * 18));
	//	Date finishDt = new Date(today.getTime() + (AppConstants.MONTH * 6));
	//	opzioniList.reload(null, idPeriodico, startDt, finishDt);
	//}
	
	
	
	//Async methods
	
	
	

	private void createRapporto(EstrazionePanel form, String idUtente) {
		LoggingServiceAsync loggingService = GWT.create(LoggingService.class);
		final EstrazionePanel fForm = form;
		String titolo = TITLE_FORM + " " + 
				periodiciList.getSelectedValueDescription();
		titolo += " " + fascicoliList.getSelectedValueDescription();
		if (!copieList.getValue(copieList.getSelectedIndex()).equals(AppConstants.INCLUDI_TUTTI)) {
			titolo += " " + copieList.getItemText(copieList.getSelectedIndex());
		}
		if (!italiaList.getValue(italiaList.getSelectedIndex()).equals(AppConstants.INCLUDI_TUTTI)) {
			titolo += " " + italiaList.getItemText(italiaList.getSelectedIndex());
		}
		//if (opzioniList.isEnabled()) {
		//	if (!opzioniList.getValue(opzioniList.getSelectedIndex()).equals(AppConstants.INCLUDI_TUTTI)) {
		//		titolo += " " + opzioniList.getItemText(opzioniList.getSelectedIndex());
		//	}
		//}
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
			periodicoPanel.add(new HTML("Periodico&nbsp;"));
			periodiciList = new PeriodiciSelect(idPeriodico, DateUtil.now(), false, true, utente);
			periodiciList.setName(AppConstants.PARAM_ID_PERIODICO);
			periodiciList.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent arg0) {
					updateFascicoliList();
					//updateOpzioniList();
				}
			});
			periodicoPanel.add(periodiciList);
			// Codice
			periodicoPanel.add(new HTML("&nbsp;Da&nbsp;inviare&nbsp;"));
			updateFascicoliList();
			fascicoliList.setName(AppConstants.PARAM_ID_FASCICOLO);
			periodicoPanel.add(fascicoliList);
			restrictionPanel.add(periodicoPanel);
			
			//copia singola
			FlowPanel copiaSingolaPanel = new FlowPanel();
			copiaSingolaPanel.add(new HTML("Restrizione sulle copie:&nbsp;"));
			copieList = new ListBox();
			copieList.addItem("includi tutti", AppConstants.INCLUDI_TUTTI);
			copieList.addItem("solo una copia", AppConstants.INCLUDI_INSIEME_INTERNO);
			copieList.addItem("solo piu' copie", AppConstants.INCLUDI_INSIEME_ESTERNO);
			copieList.setSelectedIndex(0);
			copieList.setName(AppConstants.PARAM_INCLUDI_COPIE);
			copiaSingolaPanel.add(copieList);
			restrictionPanel.add(copiaSingolaPanel);
						
			//copia singola
			FlowPanel esteriPanel = new FlowPanel();
			esteriPanel.add(new HTML("Restrizione sulla nazione:&nbsp;"));
			italiaList = new ListBox();
			italiaList.addItem("includi tutti", AppConstants.INCLUDI_TUTTI);
			italiaList.addItem("solo Italia", AppConstants.INCLUDI_INSIEME_INTERNO);
			italiaList.addItem("solo estero", AppConstants.INCLUDI_INSIEME_ESTERNO);
			italiaList.setSelectedIndex(0);
			italiaList.setName(AppConstants.PARAM_INCLUDI_ITALIA);
			esteriPanel.add(italiaList);
			restrictionPanel.add(esteriPanel);
			
			////copia singola
			//FlowPanel conOpzionePanel = new FlowPanel();
			//conOpzionePanel.add(new HTML("Restrizione sul opzione&nbsp;"));
			//Date startDt = DateUtil.now();
			//Date finishDt = new Date(startDt.getTime() - (AppConstants.MONTH * 18));
			//opzioniList = new OpzioniSelect(null, idPeriodico, startDt, finishDt, false, true);
			//opzioniList.setName(AppConstants.PARAM_ID_OPZIONE);
			//conOpzionePanel.add(opzioniList);
			//conOpzionePanel.add(new HTML(":&nbsp;"));
			//conOpzioneList = new ListBox();
			//conOpzioneList.addItem("non filtrare per opzione", AppConstants.INCLUDI_TUTTI);
			//conOpzioneList.addItem("solo con opzione", AppConstants.INCLUDI_INSIEME_INTERNO);
			//conOpzioneList.addItem("solo senza opzione", AppConstants.INCLUDI_INSIEME_ESTERNO);
			//conOpzioneList.setSelectedIndex(0);
			//conOpzioneList.setName(AppConstants.PARAM_INCLUDI_SUPPLEMENTO);
			//conOpzionePanel.add(conOpzioneList);
			//restrictionPanel.add(conOpzionePanel);
			
			//Scrivi su DB
			FlowPanel scriviDbPanel = new FlowPanel();
			scriviDbPanel.add(new HTML("Memorizza l'estrazione"));
			scriviDbCheck = new CheckBox();
			scriviDbCheck.setName(AppConstants.PARAM_SCRIVI_DB);
			scriviDbPanel.add(scriviDbCheck);
			restrictionPanel.add(scriviDbPanel);
			
			//Instructions
			restrictionPanel.add(new HTML("<br /><i>L'estrazione dei destinatari di un <b>fascicolo</b> " +
					"o <b>allegato</b> &egrave; limitata ai " +
					"tipi abbonamento cartacei "+ClientConstants.ICON_CARTACEO+"<br />" +
					"Ma l'estrazione dei destinatari di un'<b>opzione</b> " +
					"comprende tutti i tipi abbonamento.</i>"));
			
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
			String servletURL = GWT.getModuleBaseURL()+AppConstants.SERVLET_OUTPUT_FASCICOLI + 
					"?" + AppConstants.PARAM_ID_PERIODICO + "=" + periodiciList.getSelectedValueString() +
					"&" + AppConstants.PARAM_ID_FASCICOLO + "=" + fascicoliList.getSelectedValueString() +
					"&" + AppConstants.PARAM_INCLUDI_COPIE + "=" + copieList.getValue(copieList.getSelectedIndex()) +
					"&" + AppConstants.PARAM_INCLUDI_ITALIA + "=" + italiaList.getValue(italiaList.getSelectedIndex()) +
					//"&" + AppConstants.PARAM_ID_OPZIONE + "=" + opzioniList.getValue(opzioniList.getSelectedIndex()) +
					//"&" + AppConstants.PARAM_INCLUDI_SUPPLEMENTO + "=" +conOpzioneList.getValue(conOpzioneList.getSelectedIndex()) +
					"&" + AppConstants.PARAM_SCRIVI_DB + "=" + scriviDbCheck.getValue() +
					//"&" + AppConstants.PARAM_SCRIVI_DATA_ESTRAZIONE + "=" + scriviDataEstrazioneCheck.getValue() +
					"&" + AppConstants.PARAM_ID_UTENTE + "=" + utenteHid.getValue() +
					"&" + AppConstants.PARAM_ID_RAPPORTO + "=" + rapportoHid.getValue();
			new DownloadIFrame(servletURL);
			showLogTable(idRapporto);
		}
	}

}
