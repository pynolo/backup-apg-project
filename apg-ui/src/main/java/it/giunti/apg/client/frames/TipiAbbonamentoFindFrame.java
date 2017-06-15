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
import it.giunti.apg.client.widgets.tables.ListiniTable;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.Listini;
import it.giunti.apg.shared.model.Utenti;

import java.util.Date;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.datepicker.client.DateBox;

public class TipiAbbonamentoFindFrame extends FramePanel implements IAuthenticatedWidget {
		
	private Integer idPeriodico = null;
	private Date date = null;
	private boolean isOperator = false;
	private boolean isEditor = false;
	//private boolean isAdmin = false;
	private boolean isSuper = false;
	private Utenti utente = null;
	
	private FlowPanel topPanel = null;
	private DateBox dateBox = null;
	private PeriodiciSelect periodiciList = null;
	private ListiniTable lstTable = null;
		
	// METHODS
	
	public TipiAbbonamentoFindFrame(UriParameters params) {
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
		isOperator = (ruolo >= AppConstants.RUOLO_OPERATOR);
		isEditor = (ruolo >= AppConstants.RUOLO_EDITOR);
		//isAdmin = (ruolo >= AppConstants.RUOLO_ADMIN);
		isSuper = (ruolo >= AppConstants.RUOLO_SUPER);
		// UI
		if (isOperator) {
			topPanel = new FlowPanel();
			this.add(topPanel, "Tipi abbonamento");
			drawResults();
		}
	}
	
	private void drawResults() {
		// Periodico
		InlineHTML periodicoLabel = new InlineHTML("Periodico&nbsp;");
		topPanel.add(periodicoLabel);
		periodiciList = new PeriodiciSelect(idPeriodico, new Date(), false, false, utente);
		periodiciList.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				idPeriodico = periodiciList.getSelectedValueInt();
				CookieSingleton.get().setCookie(ClientConstants.COOKIE_LAST_PERIODICO, idPeriodico+"");
				UriParameters params = new UriParameters();
				params.add(AppConstants.PARAM_ID_PERIODICO, idPeriodico);
				params.add(AppConstants.PARAM_DATE, date);
				params.triggerUri(UriManager.TIPI_ABBONAMENTO_FIND);
			}
		});
		periodiciList.setEnabled(true);
		topPanel.add(periodiciList);
		//Data
		topPanel.add(new InlineHTML("&nbsp;Validi in data "));
		dateBox = new DateBox();
		dateBox.setFormat(ClientConstants.BOX_FORMAT_DAY);
		dateBox.setValue(date);
		dateBox.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> arg0) {
				date = dateBox.getValue();
				UriParameters params = new UriParameters();
				params.add(AppConstants.PARAM_ID_PERIODICO, idPeriodico);
				params.add(AppConstants.PARAM_DATE, date);
				params.triggerUri(UriManager.TIPI_ABBONAMENTO_FIND);
			}
		});
		topPanel.add(dateBox);
		if (isSuper) {
			topPanel.add(new InlineHTML("<br/>"));
			UriParameters params = new UriParameters();
			params.add(AppConstants.PARAM_ID, AppConstants.NEW_ITEM_ID);
			Hyperlink nuovoTaLink = params.getHyperlink(
					ClientConstants.ICON_ADD+"Crea tipo abbonamento", UriManager.LISTINO);
			topPanel.add(nuovoTaLink);
		}
		
		changePeriodico();
	}
	
	
	private void changePeriodico() {
		if (lstTable != null) {
			topPanel.remove(lstTable);
			lstTable=null;
		}
		DataModel<Listini> model = new ListiniTable.ListiniByPeriodicoDateModel(idPeriodico, dateBox.getValue());
		lstTable = new ListiniTable(model, isEditor);
		topPanel.add(lstTable);
	}
	
}
