package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.CookieSingleton;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.client.widgets.tables.DataModel;
import it.giunti.apg.client.widgets.tables.OrdiniLogisticaTable;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.OrdiniLogistica;
import it.giunti.apg.shared.model.Utenti;

import java.util.Date;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;

public class OrdiniLogisticaFindFrame extends FramePanel implements IAuthenticatedWidget {
	
	private Integer idPeriodico = null;
	private Date date = null;
	private boolean isEditor = false;
	//private boolean isAdmin = false;
	private boolean showAnnullati = false;
	
	private FlowPanel mainPanel = null;
	private OrdiniLogisticaTable olTable = null;
	private CheckBox showAnnullatiBox = null;
	
	// METHODS
	
	public OrdiniLogisticaFindFrame(UriParameters params) {
		super();
		if (params == null) {
			params = new UriParameters();
		}
		date = params.getDateValue(AppConstants.PARAM_DATE);
		if (date == null) date = new Date();
		idPeriodico = ValueUtil.stoi(params.getValue(AppConstants.PARAM_ID_PERIODICO));
		if (idPeriodico == null) {
			idPeriodico = ValueUtil.stoi(CookieSingleton.get().getCookie(ClientConstants.COOKIE_LAST_PERIODICO));
		}
		this.setWidth("100%");
		AuthSingleton.get().queueForAuthentication(this);
	}
	
	@Override
	public void onSuccessfulAuthentication(Utenti utente) {
		init(utente);
	}
	
	private void init(Utenti utente) {
		if (idPeriodico == null) idPeriodico=UiSingleton.get().getDefaultIdPeriodico(utente);
		// Editing rights
		int ruolo = utente.getRuolo().getId();
		isEditor = (ruolo >= AppConstants.RUOLO_EDITOR);
		//isAdmin = (ruolo >= AppConstants.RUOLO_ADMIN);
		// UI
		if (isEditor) {
			draw();
		}
	}
	
	private void draw() {
		mainPanel = new FlowPanel();
		this.add(mainPanel, "Ordini");
		
		DataModel<OrdiniLogistica> model = new OrdiniLogisticaTable.OrdiniLogisticaModel(showAnnullati);
		olTable = new OrdiniLogisticaTable(model);
		//Box annullati
		showAnnullatiBox = new CheckBox("Mostra anche gli ordini annullati");
		showAnnullatiBox.setValue(showAnnullati);
		showAnnullatiBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> arg0) {
				showAnnullati = showAnnullatiBox.getValue();
				olTable.setModel(new OrdiniLogisticaTable.OrdiniLogisticaModel(showAnnullati));
			}
		});
		mainPanel.add(showAnnullatiBox);
		//Tabella
		mainPanel.add(olTable);
	}
	
}
