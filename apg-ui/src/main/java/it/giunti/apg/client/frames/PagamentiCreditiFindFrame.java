package it.giunti.apg.client.frames;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

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
import it.giunti.apg.client.widgets.tables.PagamentiCreditiTable;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.PagamentiCrediti;
import it.giunti.apg.shared.model.Utenti;

public class PagamentiCreditiFindFrame extends FramePanel
		implements IRefreshable, IAuthenticatedWidget {
	
	private static String FILTRO_DA_PAGARE = "daPagare";
	private static String FILTRO_SCADUTI = "scaduti";
	private static String FILTRO_TUTTI = "tutti";
	
	private String idSocieta = null;
	private String filtro = null;
	private VerticalPanel panel = null;
	private VerticalPanel abbPanel = null;
	private PagamentiCreditiTable pcTable = null;
	private SocietaSelect societaList = null;
	
	public PagamentiCreditiFindFrame(UriParameters params) {
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
		//FILTRO
		String filtro$ = params.getValue(AppConstants.PARAM_QUICKSEARCH);
		if (filtro$ == null) filtro$ = "";
		if (filtro$.equals(FILTRO_DA_PAGARE)) filtro = FILTRO_DA_PAGARE;
		if (filtro$.equals(FILTRO_SCADUTI)) filtro = FILTRO_SCADUTI;
		if (filtro$.equals(FILTRO_TUTTI)) filtro = FILTRO_TUTTI;
		if (filtro == null) filtro = FILTRO_DA_PAGARE;
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
		this.add(panel, "Elenco crediti");
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
				params.add(AppConstants.PARAM_QUICKSEARCH, filtro);
				params.triggerUri(UriManager.PAGAMENTI_CREDITI_FIND);
			}
		});
		societaList.setEnabled(true);
		topPanel.add(societaList);
		//Filtro
		topPanel.add(new HTML("&nbsp;&nbsp;&nbsp;Filtro:&nbsp;"));
		final Select regaloSelect = new Select(filtro);
		regaloSelect.addItem("Solo con istanza da pagare", FILTRO_DA_PAGARE);
		regaloSelect.addItem("Solo con istanza scaduta", FILTRO_SCADUTI);
		regaloSelect.addItem("Mostra tutti", FILTRO_TUTTI);
		regaloSelect.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				String filtro = regaloSelect.getSelectedValue();
				UriParameters params = new UriParameters();
				params.add(AppConstants.PARAM_ID_SOCIETA, idSocieta);
				params.add(AppConstants.PARAM_QUICKSEARCH, filtro);
				params.triggerUri(UriManager.PAGAMENTI_CREDITI_FIND);
			}
		});
		regaloSelect.showSelectedValue();
		topPanel.add(regaloSelect);
		abbPanel.add(topPanel);
		//Tabella
		boolean soloConIstanzeDaPagare = false;
		boolean soloConIstanzeScadute = false;
		if (filtro.equals(FILTRO_DA_PAGARE)) soloConIstanzeDaPagare = true;
		if (filtro.equals(FILTRO_SCADUTI)) soloConIstanzeScadute = true;
		DataModel<PagamentiCrediti> model = new PagamentiCreditiTable.CreditiSocietaModel(idSocieta, soloConIstanzeDaPagare, soloConIstanzeScadute);
		pcTable = new PagamentiCreditiTable(model, soloConIstanzeDaPagare, soloConIstanzeScadute);
		abbPanel.add(pcTable);
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
