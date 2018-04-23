package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.CookieSingleton;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.UriManager;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.client.widgets.select.Select;
import it.giunti.apg.client.widgets.select.SocietaSelect;
import it.giunti.apg.client.widgets.tables.DataModel;
import it.giunti.apg.client.widgets.tables.IstanzeAbbonamentiTable;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Utenti;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AbbonamentiConCreditoFindFrame extends FramePanel
		implements IRefreshable, IAuthenticatedWidget {
	
	private String idSocieta = null;
	private Boolean regalo = false;
	private VerticalPanel panel = null;
	private VerticalPanel abbPanel = null;
	private IstanzeAbbonamentiTable abbTable = null;
	private SocietaSelect societaList = null;
	
	public AbbonamentiConCreditoFindFrame(UriParameters params) {
		super();
		if (params == null) {
			params = new UriParameters();
		}
		//ID_SOCIETA
		idSocieta = params.getValue(AppConstants.PARAM_ID_SOCIETA);
		if (idSocieta == null) {
			idSocieta = CookieSingleton.get().getCookie(ClientConstants.COOKIE_LAST_SOCIETA);
		}
		if (idSocieta == null) idSocieta = AppConstants.DEFAULT_SOCIETA;
		if (idSocieta.equals("")) idSocieta = AppConstants.DEFAULT_SOCIETA;
		//REGALO
		String regalo$ = params.getValue(AppConstants.PARAM_ID_ANAGRAFICA);
		if (regalo$ == null) {
			regalo = false;
		} else {
			regalo = regalo$.equals("true");
		}
		this.setWidth("100%");
		AuthSingleton.get().queueForAuthentication(this);
	}
	
	@Override
	public void onSuccessfulAuthentication(Utenti utente) {
		init(utente);
	}
	
	private void init(Utenti utente) {
		int ruolo = utente.getRuolo().getId();
		// UI
		if (ruolo >= AppConstants.RUOLO_OPERATOR) {
			draw();
		}
	}
	
	private void draw() {
		panel = new VerticalPanel();
		this.add(panel, "Crediti da assegnare");
		panel.clear();
		//Pannello abbonamenti
		abbPanel = new VerticalPanel();
		//Societa
		FlowPanel topPanel = new FlowPanel();
		topPanel.add(new HTML("Societ&agrave;&nbsp;"));
		societaList = new SocietaSelect(idSocieta);
		societaList.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				idSocieta = societaList.getSelectedValue();
				CookieSingleton.get().setCookie(ClientConstants.COOKIE_LAST_SOCIETA, idSocieta);
				UriParameters params = new UriParameters();
				params.add(AppConstants.PARAM_ID_SOCIETA, idSocieta);
				params.add(AppConstants.PARAM_ID_ANAGRAFICA, regalo.toString());
				params.triggerUri(UriManager.ABBONAMENTI_CREDITI_FIND);
			}
		});
		societaList.setEnabled(true);
		topPanel.add(societaList);
		//Regalo
		topPanel.add(new HTML("&nbsp;&nbsp;&nbsp;Istanza abbonamento&nbsp;"));
		final Select regaloSelect = new Select(regalo.toString());
		regaloSelect.addItem("Pagata dall'abbonato", "false");
		regaloSelect.addItem("Pagante diverso da abbonato", "true");
		regaloSelect.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				String regalo$ = regaloSelect.getSelectedValue();
				UriParameters params = new UriParameters();
				params.add(AppConstants.PARAM_ID_SOCIETA, idSocieta);
				params.add(AppConstants.PARAM_ID_ANAGRAFICA, regalo$.toString());
				params.triggerUri(UriManager.ABBONAMENTI_CREDITI_FIND);
			}
		});
		regaloSelect.showSelectedValue();
		topPanel.add(regaloSelect);
		abbPanel.add(topPanel);
		//Tabella
		DataModel<IstanzeAbbonamenti> model = new IstanzeAbbonamentiTable.IstanzeConCredito(idSocieta, regalo);
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
