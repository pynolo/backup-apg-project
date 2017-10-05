package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.CookieSingleton;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.UriManager;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.LoggingService;
import it.giunti.apg.client.services.LoggingServiceAsync;
import it.giunti.apg.client.widgets.DownloadIFrame;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.client.widgets.RinnoviMassiviPanel;
import it.giunti.apg.client.widgets.select.PeriodiciSelect;
import it.giunti.apg.client.widgets.tables.DataModel;
import it.giunti.apg.client.widgets.tables.LogTable;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.Utenti;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class RinnoviMassiviFrame extends FramePanel implements IAuthenticatedWidget {

	private static final String TITLE_FORM = "Rinnovo massivo";
	private static final String TITLE_LOG = "Rapporto";
	
	private Integer idPeriodico = null;
	private PeriodiciSelect periodiciList = null;
	private VerticalPanel panelLog = null;
	private Utenti utente = null;
	
	public RinnoviMassiviFrame(UriParameters params) {
		super();
		if (params == null) {
			params = new UriParameters();
		}
		idPeriodico = ValueUtil.stoi(params.getValue(AppConstants.PARAM_ID_PERIODICO));
		if (idPeriodico == null) idPeriodico = ValueUtil.stoi(CookieSingleton.get().getCookie(ClientConstants.COOKIE_LAST_PERIODICO));
		if (idPeriodico == null) idPeriodico = UiSingleton.get().getDefaultIdPeriodico(utente);
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
		if (ruolo >= AppConstants.RUOLO_ADMIN) {
			draw();
		}
	}
	
	private void draw() {
		this.clear();
		VerticalPanel vPanel = new VerticalPanel();
		RinnoviPanel rinnoviForm = new RinnoviPanel(idPeriodico);
		HorizontalPanel topPanel = new HorizontalPanel();
		// Periodico
		topPanel.add(new InlineHTML("Periodico&nbsp;"));
		periodiciList = new PeriodiciSelect(idPeriodico, DateUtil.now(), false, false, utente);
		periodiciList.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				idPeriodico = periodiciList.getSelectedValueInt();
				CookieSingleton.get().setCookie(ClientConstants.COOKIE_LAST_PERIODICO, idPeriodico+"");
				UriParameters params = new UriParameters();
				params.add(AppConstants.PARAM_ID_PERIODICO, idPeriodico);
				params.triggerUri(UriManager.RINNOVI_MASSIVI);
			}
		});
		periodiciList.setEnabled(true);
		topPanel.add(periodiciList);
		vPanel.add(topPanel);
		vPanel.add(rinnoviForm);
		this.add(vPanel, TITLE_FORM);
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
	
	private void createRapporto(RinnoviPanel form, String idUtente) {
		LoggingServiceAsync loggingService = GWT.create(LoggingService.class);
		final RinnoviPanel fForm = form;
		String titolo = TITLE_FORM;
		AsyncCallback<Integer> callback = new AsyncCallback<Integer>() {
			@Override
			public void onFailure(Throwable e) {
				WaitSingleton.get().stop();
				UiSingleton.get().addError(e);
			}
			@Override
			public void onSuccess(Integer result) {
				WaitSingleton.get().stop();
				showLogTable(result);
				fForm.onSubmit(result);
			}
		};
		WaitSingleton.get().start();
		loggingService.createRapporto(titolo, idUtente, callback);
	}
	
	
	// Inner Classes
	
	public class RinnoviPanel extends FlowPanel {
		private Hidden periodicoHid;
		private Hidden rapportoHid;
		private Hidden utenteHid;
		
		public RinnoviPanel(Integer idPeriodico) {
			super();
			final RinnoviPanel thisForm = this;
			periodicoHid = new Hidden();
			periodicoHid.setValue(idPeriodico+"");
			
			FlowPanel holder = new FlowPanel();
			holder.add(new InlineHTML("Trasformazioni programmate per il rinnovo massivo:<br/>"));
			//Tabella trasformazioni
			final RinnoviMassiviPanel rmp = new RinnoviMassiviPanel(idPeriodico);
			holder.add(rmp);
			//Icona aggiungi riga
			InlineHTML newImg = new InlineHTML(ClientConstants.ICON_ADD);
			newImg.setTitle("Nuova riga");
			newImg.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent arg0) {
					rmp.addNewRow();
				}
			});
			holder.add(newImg);
			
			HorizontalPanel buttonPanel = new HorizontalPanel();
			//Bottone salva
			Button saveButton = new Button("Salva");
			saveButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					rmp.save();
				}
			});
			buttonPanel.add(saveButton);
			//Separatore
			buttonPanel.add(new Image("img/separator.gif"));
			//Bottone avvia rinnovo massivo
			Button startButton = new Button("Avvia rinnovo massivo");
			startButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					avviaRinnovo(thisForm);
				}
			});
			buttonPanel.add(startButton);
			holder.add(buttonPanel);
			//Avviso
			buttonPanel.add(new InlineHTML("&nbsp;"+ClientConstants.ICON_WARNING+"&nbsp;"+
						"I nuovi <b>fascicoli</b> e i nuovi <b>listini</b> devono essere gi&agrave; pronti"));
			
			//Hidden values
			utenteHid = new Hidden(AppConstants.PARAM_ID_UTENTE);
			utenteHid.setValue(AuthSingleton.get().getUtente().getId());
			holder.add(utenteHid);
			rapportoHid = new Hidden(AppConstants.PARAM_ID_RAPPORTO);
			rapportoHid.setValue("0");
			holder.add(rapportoHid);
			
			thisForm.add(holder);
		}
		
		private void avviaRinnovo(RinnoviPanel form) {
			boolean confirm1 = Window.confirm(
					"Vuoi veramente avviare il rinnovo massivo?\r\n"+
					"La configurazione deve essere salvata prima di proseguire.");
			if (confirm1) {
				boolean confirm2 = Window.confirm("E' un'operazione molto pericolosa e IRREVERSIBILE, vuoi veramente rinnovare?");
				if (confirm2) {
					createRapporto(form, AuthSingleton.get().getUtente().getId());
				}
			}
		}
		
		public void onSubmit(int idRapporto) {
			rapportoHid.setValue(idRapporto+"");
			String servletURL = GWT.getModuleBaseURL()+AppConstants.SERVLET_RINNOVO_MASSIVO + 
					"?" + AppConstants.PARAM_ID_UTENTE + "=" + utenteHid.getValue() +
					"&" + AppConstants.PARAM_ID_PERIODICO + "=" + periodicoHid.getValue() +
					"&" + AppConstants.PARAM_ID_RAPPORTO + "=" + rapportoHid.getValue();
			new DownloadIFrame(servletURL);
			showLogTable(idRapporto);
		}
	}

}
