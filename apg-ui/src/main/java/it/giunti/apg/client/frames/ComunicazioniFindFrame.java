package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.CookieSingleton;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.UriManager;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.widgets.DateOnlyBox;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.client.widgets.select.PeriodiciSelect;
import it.giunti.apg.client.widgets.tables.ComunicazioniTable;
import it.giunti.apg.client.widgets.tables.DataModel;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.Comunicazioni;
import it.giunti.apg.shared.model.Utenti;

import java.util.Date;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineHTML;

public class ComunicazioniFindFrame extends FramePanel implements IAuthenticatedWidget {
	
	private Integer idPeriodico = null;
	private Date date = null;
	private boolean isEditor = false;
	private boolean isAdmin = false;
	private Utenti utente = null;
	
	private FlowPanel topPanel = null;
	private PeriodiciSelect periodiciList = null;
	private DateOnlyBox dateBox = null;
	private ComunicazioniTable bTable = null;
	
	// METHODS
	
	public ComunicazioniFindFrame(UriParameters params) {
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
		this.utente = utente;
		init(utente);
	}
	
	private void init(Utenti utente) {
		if (idPeriodico == null) idPeriodico=UiSingleton.get().getDefaultIdPeriodico(utente);
		// Editing rights
		int ruolo = utente.getRuolo().getId();
		isEditor = (ruolo >= AppConstants.RUOLO_EDITOR);
		isAdmin = (ruolo >= AppConstants.RUOLO_ADMIN);
		// UI
		if (isEditor) {
			draw();
		}
	}
	
	private void draw() {
		topPanel = new FlowPanel();
		this.add(topPanel, "Comunicazioni");

		// Periodico
		topPanel.add(new HTML("Periodico&nbsp;"));
		periodiciList = new PeriodiciSelect(idPeriodico, new Date(), false, false, utente);
		periodiciList.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				idPeriodico = periodiciList.getSelectedValueInt();
				CookieSingleton.get().setCookie(ClientConstants.COOKIE_LAST_PERIODICO, idPeriodico+"");
				UriParameters params = new UriParameters();
				params.add(AppConstants.PARAM_ID_PERIODICO, idPeriodico);
				params.add(AppConstants.PARAM_DATE, date);
				params.triggerUri(UriManager.COMUNICAZIONI_FIND);
			}
		});
		periodiciList.setEnabled(true);
		topPanel.add(periodiciList);
		//Data
		topPanel.add(new InlineHTML("&nbsp;Valide in data "));
		dateBox = new DateOnlyBox();
		dateBox.setFormat(ClientConstants.BOX_FORMAT_DAY);
		dateBox.setValue(date);
		dateBox.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> arg0) {
				date = dateBox.getValue();
				UriParameters params = new UriParameters();
				params.add(AppConstants.PARAM_ID_PERIODICO, idPeriodico);
				params.add(AppConstants.PARAM_DATE, date);
				params.triggerUri(UriManager.COMUNICAZIONI_FIND);
			}
		});
		topPanel.add(dateBox);
		
		if (isAdmin) {
			topPanel.add(new InlineHTML("<br/>"));
			UriParameters params = new UriParameters();
			params.add(AppConstants.PARAM_ID, AppConstants.NEW_ITEM_ID);
			Hyperlink nuovoComunicazioneLink = params.getHyperlink(
					ClientConstants.ICON_ADD+"Crea comunicazione", UriManager.COMUNICAZIONE);
			topPanel.add(nuovoComunicazioneLink);
		}
		
		changePeriodico();
	}
	
	
	private void changePeriodico() {
		if (bTable != null) {
			topPanel.remove(bTable);
			bTable=null;
		}
		DataModel<Comunicazioni> model = new ComunicazioniTable.ComunicazioniByPeriodicoModel(idPeriodico, date);
		bTable = new ComunicazioniTable(model);
		topPanel.add(bTable);
	}
	
}
