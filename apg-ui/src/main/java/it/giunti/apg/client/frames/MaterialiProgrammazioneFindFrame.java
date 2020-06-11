package it.giunti.apg.client.frames;

import java.util.Date;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.datepicker.client.DateBox;

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
import it.giunti.apg.client.widgets.tables.MaterialiProgrammazioneTable;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.MaterialiProgrammazione;
import it.giunti.apg.shared.model.Ruoli;
import it.giunti.apg.shared.model.Utenti;

public class MaterialiProgrammazioneFindFrame extends FramePanel implements IAuthenticatedWidget {
	
	private Integer idPeriodico = null;
	
	private Ruoli role = null;
	private boolean isOperator = false;
	//private boolean isEditor = false;
	private boolean isAdmin = false;
	private Utenti utente = null;
	
	private Date date = null;
	private FlowPanel fPanel = null;
	private PeriodiciSelect periodiciList = null;
	private DateBox extractionDate = null;
	private MaterialiProgrammazioneTable mpTable = null;
		
	// METHODS
	
	public MaterialiProgrammazioneFindFrame(UriParameters params) {
		super();
		if (params == null) {
			params = new UriParameters();
		}
		idPeriodico = ValueUtil.stoi(params.getValue(AppConstants.PARAM_ID_PERIODICO));
		if (idPeriodico == null) {
			idPeriodico = ValueUtil.stoi(CookieSingleton.get().getCookie(ClientConstants.COOKIE_LAST_PERIODICO));
		}
		date =  params.getDateValue(AppConstants.PARAM_DATE);
		if (date == null) {
			long start = DateUtil.now().getTime();
			start = start - AppConstants.MONTH * 12;
			date = new Date(start);//se la data non è definita prende 3 mesi fa
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
		role = utente.getRuolo();
		isOperator = (role.getId() >= AppConstants.RUOLO_OPERATOR);
		//isEditor = (role.getId() >= AppConstants.RUOLO_EDITOR);
		isAdmin = (role.getId() >= AppConstants.RUOLO_ADMIN);
		// UI
		if (isOperator) {
			draw();
		}
	}
	
	private void draw() {
		this.clear();
		fPanel = new FlowPanel();
		this.add(fPanel, "Fascicoli");

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
				if (extractionDate != null) params.add(AppConstants.PARAM_DATE, extractionDate.getValue());
				params.triggerUri(UriManager.MATERIALI_PROGRAMMAZIONE_FIND);
			}
		});
		periodiciList.setEnabled(true);
		topPanel.add(periodiciList);
		//Data estrazione
		topPanel.add(new InlineHTML("&nbsp;A partire dal&nbsp;"));
		extractionDate = new DateBox();
		extractionDate.setFormat(ClientConstants.BOX_FORMAT_DAY);
		extractionDate.setValue(date);
		extractionDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				UriParameters params = new UriParameters();
				params.add(AppConstants.PARAM_DATE, extractionDate.getValue());
				params.add(AppConstants.PARAM_ID_PERIODICO, idPeriodico);
				params.triggerUri(UriManager.MATERIALI_PROGRAMMAZIONE_FIND);
			}
		});
		topPanel.add(extractionDate);
		fPanel.add(topPanel);
		
		if (isAdmin) {
			//Fascicolo
			Anchor createFasButton = new Anchor(ClientConstants.ICON_ADD+"Crea fascicolo", true);
			createFasButton.addMouseDownHandler(new MouseDownHandler() {
				@Override
				public void onMouseDown(MouseDownEvent event) {
					idPeriodico = periodiciList.getSelectedValueInt();
					new MaterialiProgrammazionePopUp().initByPeriodico(idPeriodico, mpTable);
				}
			});
			fPanel.add(createFasButton);
		}
		changePeriodico();
	}
	
	
	private void changePeriodico() {
		if (mpTable != null) {
			fPanel.remove(mpTable);
			mpTable=null;
		}
		long start = date.getTime();
		long finish = start + AppConstants.MONTH * 120;
		DataModel<MaterialiProgrammazione> model = 
				new MaterialiProgrammazioneTable.MaterialiProgrammazioneByPeriodicoModel(idPeriodico, start, finish);
		mpTable = new MaterialiProgrammazioneTable(model, role);
		fPanel.add(mpTable);
	}
	
}
