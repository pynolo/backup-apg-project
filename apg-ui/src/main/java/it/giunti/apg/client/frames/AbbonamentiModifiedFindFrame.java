package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.CookieSingleton;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.UriManager;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.client.widgets.select.PeriodiciSelect;
import it.giunti.apg.client.widgets.tables.DataModel;
import it.giunti.apg.client.widgets.tables.IstanzeAbbonamentiTable;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Utenti;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AbbonamentiModifiedFindFrame extends FramePanel
		implements IRefreshable, IAuthenticatedWidget {
	
	private Integer idPeriodico = null;
	private Utenti utente = null;
	
	private VerticalPanel panel = null;
	private VerticalPanel abbPanel = null;
	private IstanzeAbbonamentiTable abbTable = null;
	private PeriodiciSelect periodiciList = null;
	
	public AbbonamentiModifiedFindFrame(UriParameters params) {
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
		this.utente=utente;
		init(utente);
	}
	
	private void init(Utenti utente) {
		if (idPeriodico == null) idPeriodico=UiSingleton.get().getDefaultIdPeriodico(utente);
		int ruolo = utente.getRuolo().getId();
		// UI
		if (ruolo >= AppConstants.RUOLO_OPERATOR) {
			draw();
		}
	}
	
	private void draw() {
		panel = new VerticalPanel();
		this.add(panel, "Abbonamenti modificati");
		panel.clear();
		//Pannello abbonamenti
		abbPanel = new VerticalPanel();
		// Periodico
		FlowPanel topPanel = new FlowPanel();
		topPanel.add(new HTML("Periodico&nbsp;"));
		periodiciList = new PeriodiciSelect(idPeriodico, DateUtil.now(), false, false, utente);
		periodiciList.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				idPeriodico = periodiciList.getSelectedValueInt();
				CookieSingleton.get().setCookie(ClientConstants.COOKIE_LAST_PERIODICO, idPeriodico+"");
				UriParameters params = new UriParameters();
				params.add(AppConstants.PARAM_ID_PERIODICO, idPeriodico);
				params.triggerUri(UriManager.ABBONAMENTI_MODIFIED_FIND);
			}
		});
		periodiciList.setEnabled(true);
		topPanel.add(periodiciList);
		abbPanel.add(topPanel);
		//Tabella
		DataModel<IstanzeAbbonamenti> model = new IstanzeAbbonamentiTable.LastModifiedModel(idPeriodico);
		abbTable = new IstanzeAbbonamentiTable(model,false, this);
		abbPanel.add(abbTable);
		panel.add(abbPanel);
	}
	
	@Override
	public void refresh() {
		//if (abbTable != null) {
		//	if (abbTable.isEmpty()) {
		//		abbPanel.setVisible(false);
		//	}
		//}
	}
}
