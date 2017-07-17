package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.CookieSingleton;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.widgets.AnagraficheSearchBox;
import it.giunti.apg.client.widgets.DownloadIFrame;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.client.widgets.select.AdesioniSelect;
import it.giunti.apg.client.widgets.select.BooleanSelect;
import it.giunti.apg.client.widgets.select.FascicoliSelect;
import it.giunti.apg.client.widgets.select.PeriodiciSelect;
import it.giunti.apg.client.widgets.select.TipiDisdettaSelect;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.Utenti;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DateBox;

public class QueryIstanzeFrame extends FramePanel implements IAuthenticatedWidget {
	
	private static final String BOX_WIDTH = "20em";
	
	private long dtStart = new Date().getTime() - (3*AppConstants.YEAR);
	private long dtFinish = new Date().getTime() + (AppConstants.YEAR);
	private Integer idPeriodico = null;
	private Utenti utente = null;
	
	private UriParameters params = null;
	private SimplePanel contentPanel = null;

	private AnagraficheSearchBox paganteSearchBox = null;
	private AnagraficheSearchBox promotoreSearchBox = null;
	
	private PeriodiciSelect periodiciList = null;
	private TextBox tipiAbbTxt = null;
	private TextBox opzioniTxt = null;
	private FascicoliSelect fasList = null;
	private DateBox dniGe = null;
	private DateBox dniLe = null;
	private DateBox dnfGe = null;
	private DateBox dnfLe = null;
	private DateBox creGe = null;
	private DateBox creLe = null;
	private DateBox inactiveAtDt = null;
	private AdesioniSelect adeList = null;
	private BooleanSelect pagatoList = null;
	private BooleanSelect fatturaDifferitaList = null;
	private BooleanSelect disdettaList = null;
	private BooleanSelect bloccatoList = null;
	private TipiDisdettaSelect tipiDisdList = null;
	
	public QueryIstanzeFrame(UriParameters params) {
		super();
		if (params != null) {
			this.params = params;
		} else {
			this.params = new UriParameters();
		}
		contentPanel = new SimplePanel();
		this.add(contentPanel, "Query istanze");
		this.setWidth("100%");
		AuthSingleton.get().queueForAuthentication(this);
	}
	
	@Override
	public void onSuccessfulAuthentication(Utenti utente) {
		this.utente = utente;
		init(utente);
	}
	
	private void init(Utenti utente) {
		idPeriodico = ValueUtil.stoi(params.getValue(AppConstants.PARAM_ID_PERIODICO));
		if (idPeriodico == null) {
			idPeriodico = ValueUtil.stoi(CookieSingleton.get().getCookie(ClientConstants.COOKIE_LAST_PERIODICO));
			if (idPeriodico == null) idPeriodico=UiSingleton.get().getDefaultIdPeriodico(utente);
		}
		// Editing rights
		int ruolo = utente.getRuolo().getId();
		// UI
		if (ruolo >= AppConstants.RUOLO_OPERATOR) {
			draw();
		}
	}
	
	private void draw() {
		FlexTable table = new FlexTable();
		contentPanel.add(table);
		int r = 0;
		
		// Periodico
		table.setHTML(r, 0, "Periodico");
		periodiciList = new PeriodiciSelect(idPeriodico, new Date(), false, true, utente);
		periodiciList.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				onPeriodicoChange();
			}
		});
		table.setWidget(r, 1, periodiciList);
		r++;
		
		// Anagrafiche
		VerticalPanel anagPanel = new VerticalPanel();
		anagPanel.setStyleName("grey-panel");
		anagPanel.setWidth("100%");
		paganteSearchBox = new AnagraficheSearchBox("Pagante", null, true);
		anagPanel.add(paganteSearchBox);
		promotoreSearchBox = new AnagraficheSearchBox("Promotore", null, true);
		anagPanel.add(promotoreSearchBox);
		table.setWidget(r, 0, anagPanel);
		table.getFlexCellFormatter().setColSpan(r, 0, 5);
		r++;
		
		// Tipi Abbonamento
		table.setHTML(r, 0, "Tipi Abbonamento (;)");
		tipiAbbTxt = new TextBox();
		tipiAbbTxt.setWidth(BOX_WIDTH);
		tipiAbbTxt.setValue("");
		table.setWidget(r, 1, tipiAbbTxt);
		// Opzioni
		table.setHTML(r, 3, "Opzioni (;) "+ClientConstants.ICON_DANGER);
		opzioniTxt = new TextBox();
		opzioniTxt.setWidth(BOX_WIDTH);
		opzioniTxt.setValue("");
		table.setWidget(r, 4, opzioniTxt);
		r++;
		
		// Fascicolo
		table.setHTML(r, 0, "Fascicolo ricevuto "+ClientConstants.ICON_DANGER);
		fasList = new FascicoliSelect(null, idPeriodico,
				dtStart, dtFinish, false, false, true, false, true);
		table.setWidget(r, 1, fasList);
		table.getFlexCellFormatter().setColSpan(r, 1, 4);
		r++;
		
		// Filtro date
		table.setHTML(r, 0, "Filtro date");
		FlexTable dateTable = new FlexTable();
		table.setWidget(r, 1, dateTable);
		table.getFlexCellFormatter().setColSpan(r, 1, 4);
		dniGe = new DateBox();
		dniGe.setFormat(ClientConstants.BOX_FORMAT_DAY);
		dateTable.setWidget(0, 0, dniGe);
		dateTable.setHTML(0, 1, " <b>&le;</b> inizio istanza <b>&le;</b> ");
		dniLe = new DateBox();
		dniLe.setFormat(ClientConstants.BOX_FORMAT_DAY);
		dateTable.setWidget(0, 2, dniLe);
		dnfGe = new DateBox();
		dnfGe.setFormat(ClientConstants.BOX_FORMAT_DAY);
		dateTable.setWidget(1, 0, dnfGe);
		dateTable.setHTML(1, 1, " <b>&le;</b> fine istanza <b>&le;</b> ");
		dnfLe = new DateBox();
		dnfLe.setFormat(ClientConstants.BOX_FORMAT_DAY);
		dateTable.setWidget(1, 2, dnfLe);
		creGe = new DateBox();
		creGe.setFormat(ClientConstants.BOX_FORMAT_DAY);
		dateTable.setWidget(2, 0, creGe);
		dateTable.setHTML(2, 1, " <b>&le;</b> data creazione <b>&le;</b> ");
		creLe = new DateBox();
		creLe.setFormat(ClientConstants.BOX_FORMAT_DAY);
		dateTable.setWidget(2, 2, creLe);
		r++;
		
		// Non attivo in data
		table.setHTML(r, 0, "Senza istanza valida in data "+ClientConstants.ICON_DANGER);
		inactiveAtDt = new DateBox();
		inactiveAtDt.setFormat(ClientConstants.BOX_FORMAT_DAY);
		table.setWidget(r, 1, inactiveAtDt);
		r++;
		
		// Adesione
		table.setHTML(r, 0, "Adesione");
		adeList = new AdesioniSelect(null);
		table.setWidget(r, 1, adeList);
		r++;
		
		// Pagato
		table.setHTML(r, 0, "Pagato");
		pagatoList = new BooleanSelect(null);
		table.setWidget(r, 1, pagatoList);
		// Fatturato
		table.setHTML(r, 3, "Fattura a pagamento differito");
		fatturaDifferitaList = new BooleanSelect(null);
		table.setWidget(r, 4, fatturaDifferitaList);
		r++;
		
		// Disdettato
		table.setHTML(r, 0, "Con disdetta");
		disdettaList = new BooleanSelect(null);
		table.setWidget(r, 1, disdettaList);
		// Bloccato
		table.setHTML(r, 3, "Bloccato");
		bloccatoList = new BooleanSelect(null);
		table.setWidget(r, 4, bloccatoList);
		r++;
		
		//tipo abbonamento
		table.setHTML(r, 0, "Motivo disdetta");
		tipiDisdList = new TipiDisdettaSelect(null);
		table.setWidget(r, 1, tipiDisdList);
		r++;
		
		//bottone
		table.setWidget(r, 0, new Button("Cerca", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				try {
					submit();
				} catch (ValidationException e) {
					UiSingleton.get().addError(e);
				}
			}
		}));
		r++;
		
		table.setHTML(r, 0, "<br />"+
				"&Egrave; necessario riempire almeno uno dei tre intervalli nel <i>Filtro date</i>.<br />"+
				"Le condizioni sono tutte in <i>AND</i> tra loro e almeno 2 devono essere compilate per lanciare la ricerca.<br />"+
				"Le voci con "+ClientConstants.ICON_DANGER+" sono impegnative e potrebbero richiere molto tempo.");
		table.getFlexCellFormatter().setColSpan(r, 0, 5);
	}
	
	public void submit() throws ValidationException {
		//Validation
		boolean valid = ((dniGe.getValue() != null) && (dniLe.getValue() != null)) ||
				((dnfGe.getValue() != null) && (dnfLe.getValue() != null)) ||
				((creGe.getValue() != null) && (creLe.getValue() != null));
		if (!valid) throw new ValidationException("&Egrave; necessario "+
				"riempire almeno uno dei tre intervalli nel Filtro Date");
		//Value map
		Map<String, String> params = new HashMap<String, String>();
		if (paganteSearchBox.getIdValue() != null)
			params.put("idPagante", paganteSearchBox.getIdValue());
		if (promotoreSearchBox.getIdValue() != null)
			params.put("idPromotore", promotoreSearchBox.getIdValue());
		if (periodiciList.getSelectedValueInt() != null)
			if (periodiciList.getSelectedValueInt() > 0)
				params.put("idPeriodico", periodiciList.getSelectedValueString());
		if (tipiAbbTxt.getValue().length() > 0)
			params.put("tipiAbbonamento", tipiAbbTxt.getValue());
		if (opzioniTxt.getValue().length() > 0)
			params.put("opzioni", opzioniTxt.getValue());
		if (fasList.getSelectedValueInt() != null)
			if (fasList.getSelectedValueInt() > 0)
				params.put("idFascicolo", fasList.getSelectedValueString());
		if (dniGe.getValue() != null)
			params.put("dniGe", ClientConstants.FORMAT_DAY_SQL.format(dniGe.getValue()));
		if (dniLe.getValue() != null)
			params.put("dniLe", ClientConstants.FORMAT_DAY_SQL.format(dniLe.getValue()));
		if (dnfGe.getValue() != null)
			params.put("dnfGe", ClientConstants.FORMAT_DAY_SQL.format(dnfGe.getValue()));
		if (dnfLe.getValue() != null)
			params.put("dnfLe", ClientConstants.FORMAT_DAY_SQL.format(dnfLe.getValue()));
		if (creGe.getValue() != null)
			params.put("creGe", ClientConstants.FORMAT_DAY_SQL.format(creGe.getValue()));
		if (creLe.getValue() != null)
			params.put("creLe", ClientConstants.FORMAT_DAY_SQL.format(creLe.getValue()));
		if (inactiveAtDt.getValue() != null)
			params.put("inactiveAtDt", ClientConstants.FORMAT_DAY_SQL.format(inactiveAtDt.getValue()));
		if (adeList.getSelectedValueInt() !=null)
			if (adeList.getSelectedValueInt() > 0)
				params.put("idAdesione", adeList.getSelectedValueString());
		if (!pagatoList.getSelectedValueString().equals(AppConstants.SELECT_EMPTY_LABEL))
				params.put("pagato", pagatoList.getSelectedValueString());
		if (!fatturaDifferitaList.getSelectedValueString().equals(AppConstants.SELECT_EMPTY_LABEL))
				params.put("fatturato", fatturaDifferitaList.getSelectedValueString());
		if (!disdettaList.getSelectedValueString().equals(AppConstants.SELECT_EMPTY_LABEL))
				params.put("disdetta", disdettaList.getSelectedValueString());
		if (!bloccatoList.getSelectedValueString().equals(AppConstants.SELECT_EMPTY_LABEL))
				params.put("bloccato", bloccatoList.getSelectedValueString());
		if (tipiDisdList.getSelectedValueInt() != null)
			if (tipiDisdList.getSelectedValueInt() > 0)
				params.put("idTipoDisdetta", tipiDisdList.getSelectedValueString());
		String paramString = "";
		for (String key:params.keySet()) {
			if (paramString.length() > 0) paramString += "&";
			paramString += key+"="+params.get(key);
		}
		String servletURL = GWT.getModuleBaseURL()+AppConstants.SERVLET_QUERY_ISTANZE+"?"+paramString;
		new DownloadIFrame(servletURL);
	}
	
	private void onPeriodicoChange() {
		Integer idPeriodico = periodiciList.getSelectedValueInt();
		CookieSingleton.get().setCookie(ClientConstants.COOKIE_LAST_PERIODICO, idPeriodico+"");
		if (idPeriodico != null) {
			if (idPeriodico > 0) {
				fasList.reload(null, idPeriodico, dtStart, dtFinish, false, false, true, false, true);
			}
		}
	}

}
