package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.CookieSingleton;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.ComunicazioniService;
import it.giunti.apg.client.services.ComunicazioniServiceAsync;
import it.giunti.apg.client.services.FascicoliService;
import it.giunti.apg.client.services.FascicoliServiceAsync;
import it.giunti.apg.client.services.LoggingService;
import it.giunti.apg.client.services.LoggingServiceAsync;
import it.giunti.apg.client.widgets.DownloadIFrame;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.client.widgets.tables.DataModel;
import it.giunti.apg.client.widgets.tables.LogTable;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.Comunicazioni;
import it.giunti.apg.shared.model.Fascicoli;
import it.giunti.apg.shared.model.Utenti;

import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.VerticalPanel;

public class OutputEnqueuedEmailFrame extends FramePanel implements IAuthenticatedWidget {

	private static final String TITLE_FORM = "Email in coda";
	private static final String TITLE_LOG = "Rapporto";
	
	private Integer idPeriodico = null;
	private Utenti utente = null;
	
	private FlowPanel mainPanel = null;
	private VerticalPanel panelLog = null;
	
	
	public OutputEnqueuedEmailFrame(UriParameters params) {
		super();
		AuthSingleton.get().queueForAuthentication(this);
	}
	
	@Override
	public void onSuccessfulAuthentication(Utenti utente) {
		init(utente);
	}
	
	private void init(Utenti utente) {
		this.utente = utente;
		// Editing rights
		int ruolo = utente.getRuolo().getId();
		// UI
		if (ruolo >= AppConstants.RUOLO_OPERATOR) {
			draw();
		}
	}
	
	private void draw() {
		idPeriodico = ValueUtil.stoi(CookieSingleton.get().getCookie(ClientConstants.COOKIE_LAST_PERIODICO));
		if (idPeriodico == null) idPeriodico=UiSingleton.get().getDefaultIdPeriodico(utente);
		mainPanel = new FlowPanel();
		//Introduction
		HTML html = new HTML("Elenco delle comunicazioni in attesa di invio.<br /><br />");
		mainPanel.add(html);
		//Form Panel
		EstrazionePanel estrazionePanel = new EstrazionePanel(AppConstants.COMUN_MEDIA_EMAIL);
		mainPanel.add(estrazionePanel);
		this.add(mainPanel, TITLE_FORM);
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
	

	
	
	//Inner Classes
	
	
	
	
	private class EstrazionePanel extends FormPanel {
		private FlexTable estrazioneTable = null;
		private Hidden utenteHid;
		private Hidden rapportoHid;
		int row = 0;
		
		public EstrazionePanel(String idTipoMedia) {
			super();
			this.add(new HTML(ClientConstants.LABEL_LOADING));
			init(idTipoMedia);
		}
		
		private void init(String idTipoMedia) {
			estrazioneTable = new FlexTable();
			this.clear();
			this.add(estrazioneTable);
			// Hidden
			utenteHid = new Hidden(AppConstants.PARAM_ID_UTENTE);
			utenteHid.setValue(AuthSingleton.get().getUtente().getId());
			estrazioneTable.setWidget(row, 0, utenteHid);
			rapportoHid = new Hidden(AppConstants.PARAM_ID_RAPPORTO);
			rapportoHid.setValue("0");
			estrazioneTable.setWidget(row, 1, rapportoHid);
			row++;
			estrazioneTable.setHTML(row, 0, ClientConstants.LABEL_EMPTY_RESULT);
			loadFascicoliList(idTipoMedia);
			loadAsyncList(idTipoMedia);
		}
		
		private void addFascicoliToForm(Map<Fascicoli, Integer> fasMap, String idTipoMedia) {
			final EstrazionePanel thisForm = this;
			Set<Fascicoli> fasSet = fasMap.keySet();
			for (Fascicoli fas:fasSet) {
				final Fascicoli fFas = fas;
				final String fIdTipoMedia = idTipoMedia;
				int quantita = fasMap.get(fas);
				estrazioneTable.setHTML(row, 0, ClientConstants.ICON_EMAIL);
				String descr = "<b>"+AppConstants.COMUN_MEDIA_DESC.get(idTipoMedia)+" - "+
						fas.getPeriodico().getNome()+"</b>";
				descr += "<br />"+fas.getTitoloNumero()+" "+fas.getDataCop()+" "+
						ClientConstants.FORMAT_YEAR.format(fas.getDataInizio());
				descr += "<br />Quantit&agrave; stimata: <b>"+quantita+"</b>";
				estrazioneTable.setHTML(row, 1, descr);
				Button fasButton = new Button("Invia");
				fasButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						outputEvasioniByFascicolo(thisForm, fFas, fIdTipoMedia, false, AuthSingleton.get().getUtente().getId());
					}
				});
				estrazioneTable.setWidget(row, 2, fasButton);
				Button testButton = new Button("Test");
				testButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						outputEvasioniByFascicolo(thisForm, fFas, fIdTipoMedia, true, AuthSingleton.get().getUtente().getId());
					}
				});
				estrazioneTable.setWidget(row, 3, testButton);
				row++;
			}
			applyDataRowStyles();
		}
		
		private void addComunicazioniToForm(Map<Comunicazioni, Integer> comMap, String idTipoMedia) {
			final EstrazionePanel thisForm = this;
			Set<Comunicazioni> comSet = comMap.keySet();
			for (Comunicazioni com:comSet) {
				final Comunicazioni fCom = com;
				final String fIdTipoMedia = idTipoMedia;
				int quantita = comMap.get(com);
				estrazioneTable.setHTML(row, 0, ClientConstants.ICON_EMAIL);
				String descr = "<b>"+AppConstants.COMUN_MEDIA_DESC.get(idTipoMedia)+" - "+
						com.getPeriodico().getNome()+"</b><br />"+
						com.getTitolo();
				if (com.getModelloEmail() != null)
						descr += "<br /><i>Modello: " + com.getModelloEmail().getDescr()+"</i>";
				if (com.getModelloBollettino() != null)
						descr += "<br /><i>Modello: " + com.getModelloBollettino().getDescr()+"</i>";
				descr += "<br />Quantit&agrave; stimata: <b>"+quantita+"</b>";
				estrazioneTable.setHTML(row, 1, descr);
				Button comButton = new Button("Invia");
				comButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						outputEvasioniByComunicazione(thisForm, fCom, fIdTipoMedia, false, AuthSingleton.get().getUtente().getId());
					}
				});
				estrazioneTable.setWidget(row, 2, comButton);
				Button testButton = new Button("Test");
				testButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						outputEvasioniByComunicazione(thisForm, fCom, fIdTipoMedia, true, AuthSingleton.get().getUtente().getId());
					}
				});
				estrazioneTable.setWidget(row, 3, testButton);
				row++;
			}
			applyDataRowStyles();
		}
		
		private void applyDataRowStyles() {
			HTMLTable.RowFormatter rf = estrazioneTable.getRowFormatter();
			for (int row = 1; row < estrazioneTable.getRowCount(); ++row) {
				if ((row % 2) != 0) {
					rf.addStyleName(row, "apg-row-even");
				} else {
					rf.addStyleName(row, "apg-row-odd");
				}
			}
		}
		
		public void submitByFascicolo(Fascicoli fas, String idTipoMedia, Boolean test, int idRapporto) {
			rapportoHid.setValue(idRapporto+"");
			String servletURL = GWT.getModuleBaseURL()+AppConstants.SERVLET_OUTPUT_ENQUEUED_EMAILS + 
					"?" + AppConstants.PARAM_ID_TIPO_MEDIA + "=" + idTipoMedia +
					"&" + AppConstants.PARAM_ID_FASCICOLO + "=" + fas.getId() +
					"&" + AppConstants.PARAM_ID_UTENTE + "=" + utenteHid.getValue() +
					"&" + AppConstants.PARAM_ID_RAPPORTO + "=" + rapportoHid.getValue() +
					"&" + AppConstants.PARAM_TEST + "=" + test.toString();
			new DownloadIFrame(servletURL);
			showLogTable(idRapporto);
		}
		
		public void submitByComunicazione(Comunicazioni com, String idTipoMedia, Boolean test, int idRapporto) {
			rapportoHid.setValue(idRapporto+"");
			String servletURL = GWT.getModuleBaseURL()+AppConstants.SERVLET_OUTPUT_ENQUEUED_EMAILS + 
					"?" + AppConstants.PARAM_ID_TIPO_MEDIA + "=" + idTipoMedia +
					"&" + AppConstants.PARAM_ID_COMUNICAZIONE + "=" + com.getId() +
					"&" + AppConstants.PARAM_ID_UTENTE + "=" + utenteHid.getValue() +
					"&" + AppConstants.PARAM_ID_RAPPORTO + "=" + rapportoHid.getValue() +
					"&" + AppConstants.PARAM_TEST + "=" + test.toString();
			new DownloadIFrame(servletURL);
			showLogTable(idRapporto);
		}
		

		//Async methods
		
		
		private void loadFascicoliList(String idTipoMedia) {
			final String fIdTipoMedia = idTipoMedia;
			FascicoliServiceAsync fasService = GWT.create(FascicoliService.class);
			AsyncCallback<Map<Fascicoli, Integer>> callback = new AsyncCallback<Map<Fascicoli, Integer>>() {
				@Override
				public void onFailure(Throwable caught) {
					WaitSingleton.get().stop();
					if (!(caught instanceof EmptyResultException)) {
						UiSingleton.get().addError(caught);
					}
				}
				@Override
				public void onSuccess(Map<Fascicoli, Integer> result) {
					WaitSingleton.get().stop();
					addFascicoliToForm(result, fIdTipoMedia);
				}
			};
			WaitSingleton.get().start();
			fasService.findFascicoliByEnqueuedMedia(idTipoMedia, callback);
		}
		
		private void loadAsyncList(String idTipoMedia) {
			final String fIdTipoMedia = idTipoMedia;
			ComunicazioniServiceAsync comService = GWT.create(ComunicazioniService.class);
			AsyncCallback<Map<Comunicazioni, Integer>> callback = new AsyncCallback<Map<Comunicazioni, Integer>>() {
				@Override
				public void onFailure(Throwable caught) {
					WaitSingleton.get().stop();
					if (!(caught instanceof EmptyResultException)) {
						UiSingleton.get().addError(caught);
					}
				}
				@Override
				public void onSuccess(Map<Comunicazioni, Integer> result) {
					WaitSingleton.get().stop();
					addComunicazioniToForm(result, fIdTipoMedia);
				}
			};
			WaitSingleton.get().start();
			comService.findComunicazioniByEnqueuedMedia(idTipoMedia, callback);
		}
		
		private void outputEvasioniByFascicolo(EstrazionePanel form, Fascicoli fas,
				String idTipoMedia, boolean test, String idUtente) {
			boolean confirm = Window.confirm("Come misura di sicurezza e' necessario confermare l'invio " +
					"delle email legate al fascicolo "+ fas.getTitoloNumero()+" di "+
					fas.getPeriodico().getNome());
			if (confirm) {
				LoggingServiceAsync loggingService = GWT.create(LoggingService.class);
				final Fascicoli fFas = fas;
				final String fIdTipoMedia = idTipoMedia;
				final EstrazionePanel fForm = form;
				final boolean fTest = test;
				String titolo = TITLE_FORM + " fascicolo " + fas.getTitoloNumero()+" "+
						fas.getPeriodico().getNome();
				AsyncCallback<Integer> callback = new AsyncCallback<Integer>() {
					@Override
					public void onFailure(Throwable e) {
						WaitSingleton.get().stop();
						UiSingleton.get().addError(e);
					}
					@Override
					public void onSuccess(Integer result) {
						WaitSingleton.get().stop();
						fForm.submitByFascicolo(fFas, fIdTipoMedia, fTest, result);
					}
				};
				WaitSingleton.get().start();
				loggingService.createRapporto(titolo, idUtente, callback);
			}
		}
		
		private void outputEvasioniByComunicazione(EstrazionePanel form, Comunicazioni com,
				String idTipoMedia, boolean test, String idUtente) {
			String confirmString = "Come misura di sicurezza e' necessario confermare l'invio " +
					"delle email legate alla comunicazione '"+ com.getTitolo()+"' per "+
					com.getPeriodico().getNome();
			if (test) confirmString = "Vuoi inviare una email di TEST all'amministratore?";
			boolean confirm = Window.confirm(confirmString);
			if (confirm) {
				LoggingServiceAsync loggingService = GWT.create(LoggingService.class);
				final Comunicazioni fCom = com;
				final String fIdTipoMedia = idTipoMedia;
				final EstrazionePanel fForm = form;
				final boolean fTest = test;
				String titolo = TITLE_FORM + " comunicazione '" + com.getTitolo()+"' "+
						com.getPeriodico().getNome();
				AsyncCallback<Integer> callback = new AsyncCallback<Integer>() {
					@Override
					public void onFailure(Throwable e) {
						WaitSingleton.get().stop();
						UiSingleton.get().addError(e);
					}
					@Override
					public void onSuccess(Integer result) {
						WaitSingleton.get().stop();
						fForm.submitByComunicazione(fCom, fIdTipoMedia, fTest, result);
					}
				};
				WaitSingleton.get().start();
				loggingService.createRapporto(titolo, idUtente, callback);
			}
		}
	}
}
