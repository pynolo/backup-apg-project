package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.CookieSingleton;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.UriManager;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.client.widgets.select.PeriodiciSelect;
import it.giunti.apg.client.widgets.tables.DataModel;
import it.giunti.apg.client.widgets.tables.ModelliBollettiniTable;
import it.giunti.apg.core.DateUtil;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.ModelliBollettini;
import it.giunti.apg.shared.model.Utenti;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineHTML;

public class ModelliBollettiniFindFrame extends FramePanel implements IAuthenticatedWidget {
	
	private Integer idPeriodico = null;
	private boolean isSuper = false;
	private boolean isAdmin = false;
	private Utenti utente = null;
	
	private FlowPanel topPanel = null;
	private PeriodiciSelect periodiciList = null;
	private ModelliBollettiniTable bTable = null;
	
	// METHODS
	
	public ModelliBollettiniFindFrame(UriParameters params) {
		super();
		if (params == null) {
			params = new UriParameters();
		}
		idPeriodico = ValueUtil.stoi(params.getValue(AppConstants.PARAM_ID_PERIODICO));
		if (idPeriodico == null) {
			idPeriodico = ValueUtil.stoi(CookieSingleton.get().getCookie(ClientConstants.COOKIE_LAST_PERIODICO));
		}
		this.setWidth("100%");
		AuthSingleton.get().queueForAuthentication(this);
	}
	
	@Override
	public void onSuccessfulAuthentication(Utenti utente) {
		this.utente = utente;
		init(utente);
	}
	
	private void init(Utenti utente) {
		if (idPeriodico == null) idPeriodico=UiSingleton.get().getDefaultIdPeriodico(utente);
		// Editing rights
		int ruolo = utente.getRuolo().getId();
		isAdmin = (ruolo >= AppConstants.RUOLO_ADMIN);
		isSuper = (ruolo >= AppConstants.RUOLO_SUPER);
		// UI
		if (isAdmin) {
			draw();
		}
	}
	
	private void draw() {
		topPanel = new FlowPanel();
		this.add(topPanel, "Modelli per bollettini");

		// Periodico
		topPanel.add(new HTML("Periodico&nbsp;"));
		periodiciList = new PeriodiciSelect(idPeriodico, DateUtil.now(), false, false, utente);
		periodiciList.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				idPeriodico = periodiciList.getSelectedValueInt();
				CookieSingleton.get().setCookie(ClientConstants.COOKIE_LAST_PERIODICO, idPeriodico+"");
				UriParameters params = new UriParameters();
				params.add(AppConstants.PARAM_ID_PERIODICO, idPeriodico);
				params.triggerUri(UriManager.MODELLI_BOLLETTINI_FIND);
			}
		});
		periodiciList.setEnabled(true);
		topPanel.add(periodiciList);
		
		if (isSuper) {
			topPanel.add(new InlineHTML("<br/>"));
			UriParameters params = new UriParameters();
			params.add(AppConstants.PARAM_ID, AppConstants.NEW_ITEM_ID);
			Hyperlink nuovoModelloLink = params.getHyperlink(
					ClientConstants.ICON_ADD+"Crea modello", UriManager.MODELLI_BOLLETTINI);
			topPanel.add(nuovoModelloLink);
		}
		
		changePeriodico();
	}
	
	
	private void changePeriodico() {
		if (bTable != null) {
			topPanel.remove(bTable);
			bTable=null;
		}
		DataModel<ModelliBollettini> model = new ModelliBollettiniTable.ModelliBollettiniByPeriodicoModel(idPeriodico);
		bTable = new ModelliBollettiniTable(model);
		topPanel.add(bTable);
	}
	
}
