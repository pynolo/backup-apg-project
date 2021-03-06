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
import it.giunti.apg.client.widgets.tables.PagamentiCorrezioniTable;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.Pagamenti;
import it.giunti.apg.shared.model.Utenti;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PagamentiCorrezioniFrame extends FramePanel implements IAuthenticatedWidget {

	//private static final long MESE = 1000L * 60L * 60L * 24L * 30L;
	
	private Integer idPeriodico = null;
	private Utenti utente = null;
	
	private VerticalPanel mainPanel = null;
	private PeriodiciSelect periodiciList = null;
	private PagamentiCorrezioniTable pcTable = null;
	
	
	public PagamentiCorrezioniFrame(UriParameters params) {
		super();
		if (params == null) {
			params = new UriParameters();
		}
		idPeriodico = ValueUtil.stoi(params.getValue(AppConstants.PARAM_ID_PERIODICO));
		if (idPeriodico == null) {
			idPeriodico = ValueUtil.stoi(CookieSingleton.get().getCookie(ClientConstants.COOKIE_LAST_PERIODICO));
		}
		AuthSingleton.get().queueForAuthentication(this);
	}
	
	@Override
	public void onSuccessfulAuthentication(Utenti utente) {
		this.utente = utente;
		// UI
		if (utente.getRuolo().getId() >= AppConstants.RUOLO_EDITOR) {
			draw();
		}
	}
	
	private void draw() {
		if (idPeriodico == null) idPeriodico=UiSingleton.get().getDefaultIdPeriodico(utente);
		mainPanel = new VerticalPanel();
		this.add(mainPanel, "Pagamenti: elenco errori");
		
		VerticalPanel formPanel = new VerticalPanel();
		FlowPanel topPanel = new FlowPanel();
		// Periodico
		InlineHTML periodicoLabel = new InlineHTML("Periodico&nbsp;");
		topPanel.add(periodicoLabel);
		periodiciList = new PeriodiciSelect(idPeriodico, DateUtil.now(), false, false, utente);
		periodiciList.setEnabled(true);
		periodiciList.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent arg0) {
				idPeriodico = periodiciList.getSelectedValueInt();
				CookieSingleton.get().setCookie(ClientConstants.COOKIE_LAST_PERIODICO, idPeriodico+"");
				UriParameters params = new UriParameters();
				params.add(AppConstants.PARAM_ID_PERIODICO, idPeriodico);
				params.triggerUri(UriManager.PAGAMENTI_CORREZIONE);
			}
		});
		topPanel.add(periodiciList);
		formPanel.add(topPanel);
		mainPanel.add(formPanel);
		
		DataModel<Pagamenti> model = new PagamentiCorrezioniTable
				.PagamentiConErroreModel(idPeriodico);
		pcTable = new PagamentiCorrezioniTable(null, model, utente);
		mainPanel.add(pcTable);
		mainPanel.add(legenda());
	}
	
	private FlexTable legenda() {
		FlexTable table = new FlexTable();
		table.setStyleName("grey-panel");
		table.setHTML(0, 0, "<b>Legenda</b>");
		table.getFlexCellFormatter().setColSpan(0, 0, 2);
		InlineHTML riattivaImg = new InlineHTML(ClientConstants.ICON_EURO);
		table.setWidget(1,0, riattivaImg);
		table.setHTML(1, 1, "Fattura come saldo");
		InlineHTML creditoImg = new InlineHTML(ClientConstants.ICON_DATABASE);
		table.setWidget(2,0, creditoImg);
		table.setHTML(2, 1, "Fattura come anticipo");
		InlineHTML eliminaImg = new InlineHTML(ClientConstants.ICON_DELETE);
		table.setWidget(3,0, eliminaImg);
		table.setHTML(3, 1, "Elimina il pagamento");		
		//InlineHTML abbinaImg = new InlineHTML(ClientConstants.ICON_ADD);
		//table.setWidget(1, 0, abbinaImg);
		//table.setHTML(1, 1, "Abbina all'istanza pi&ugrave; recente dell'abbonamento indicato");
		//InlineHTML rinnovaImg = new InlineHTML(ClientConstants.ICON_RINNOVA);
		//table.setWidget(2,0, rinnovaImg);
		//table.setHTML(2, 1, "Rinnova l'abbonamento indicato e abbina il pagamento");
		//InlineHTML riattivaImg = new InlineHTML(ClientConstants.ICON_RIGENERA);
		//table.setWidget(3,0, riattivaImg);
		//table.setHTML(3, 1, "Rigenera l'abbonamento indicato dal numero corrente e abbina il pagamento");
		//InlineHTML creditoImg = new InlineHTML(ClientConstants.ICON_SAVE);
		//table.setWidget(4,0, creditoImg);
		//table.setHTML(4, 1, "Memorizza nei crediti del pagante");
		//InlineHTML eliminaImg = new InlineHTML(ClientConstants.ICON_DELETE);
		//table.setWidget(5,0, eliminaImg);
		//table.setHTML(5, 1, "Elimina completamente il pagamento");
		return table;
	}

}
