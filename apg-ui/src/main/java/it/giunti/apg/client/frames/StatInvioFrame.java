package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.CookieSingleton;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.UriManager;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.StatService;
import it.giunti.apg.client.services.StatServiceAsync;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.client.widgets.select.PeriodiciSelect;
import it.giunti.apg.core.DateUtil;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.Fascicoli;
import it.giunti.apg.shared.model.StatInvio;
import it.giunti.apg.shared.model.Utenti;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart;

public class StatInvioFrame extends FramePanel implements IAuthenticatedWidget {
	

	private static final String PIE_CHART_TITLE = "Tipi abbonamento";
	private static final int PIE_CHART_WIDTH = 750;
	private static final int PIE_CHART_HEIGHT = 450;
	
	private StatServiceAsync statService = GWT.create(StatService.class);
	
	private Utenti utente = null;
	
	private Integer idPeriodico = null;
	private FlowPanel panelTa = null;
	private FlowPanel statPanel = null;
	private SimplePanel piePanel = null;
	private PeriodiciSelect periodiciList = null;
	
	// METHODS
	
	public StatInvioFrame(UriParameters params) {
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
		// Editing rights
		int ruolo = utente.getRuolo().getId();
		// UI
		if (ruolo >= AppConstants.RUOLO_EDITOR) {
			panelTa = new FlowPanel();
			this.add(panelTa, "Statistiche: ultimo invio");
			drawChart();
		}
	}
	
	private void drawChart() {
		if (idPeriodico == null) idPeriodico=UiSingleton.get().getDefaultIdPeriodico(utente);
		// Periodico
		panelTa.add(new HTML("Periodico&nbsp;"));
		periodiciList = new PeriodiciSelect(idPeriodico, DateUtil.now(), false, false, utente);
		periodiciList.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				idPeriodico = periodiciList.getSelectedValueInt();
				CookieSingleton.get().setCookie(ClientConstants.COOKIE_LAST_PERIODICO, idPeriodico+"");
				UriParameters params = new UriParameters();
				params.add(AppConstants.PARAM_ID_PERIODICO, idPeriodico);
				params.triggerUri(UriManager.STAT_INVIO);
			}
		});
		periodiciList.setEnabled(true);
		panelTa.add(periodiciList);
		statPanel = new FlowPanel();
		panelTa.add(statPanel);
		piePanel = new SimplePanel();
		panelTa.add(piePanel);
		loadLastStatInvio(idPeriodico);
	}
	
	private void drawLastStat(List<StatInvio> invioList) {
		if (invioList == null) return;
		if (invioList.size() == 0) return;
		Date statDate = invioList.get(0).getDataCreazione();
		Fascicoli statFas = invioList.get(0).getFascicolo();
		NumberFormat df = NumberFormat.getFormat("#0");
		FlexTable table = new FlexTable();
		int r=0;
		table.setHTML(r, 0, "<h3>Distribuzione per il fascicolo "+
				statFas.getTitoloNumero()+" (invio del "+
				ClientConstants.FORMAT_DAY.format(statDate)+")</h3>");
		table.getFlexCellFormatter().setColSpan(r, 0, 2);
		r++;
		
		for (StatInvio stat:invioList) {
			table.setHTML(r, 0, df.format(stat.getQuantita()));
			table.setHTML(r, 1, "<b>"+stat.getTipoAbbonamento().getCodice()+"</b> "+
					stat.getTipoAbbonamento().getNome());
			r++;
		}
		statPanel.add(table);
	}
	
	//private void changePeriodico() {
	//	piePanel.clear();
	//	try {
	//		if (periodiciList != null) {
	//			if (periodiciList.getSelectedValueInt() != null) {
	//				Integer idPeriodico = periodiciList.getSelectedValueInt();
	//				loadLastStatInvio(idPeriodico);
	//			}
	//		}
	//	} catch (NumberFormatException e) {	}
	//}
	
	
	
	// Chart methods

	
	
	private void drawPieChart(List<StatInvio> siData) {
		final List<StatInvio> fSiData = siData;
		// Create a callback to be called when the visualization API
		// has been loaded.
		Runnable onLoadCallback = new Runnable() {
			public void run() {
				// Create a pie chart visualization.
				PieChart pie = new PieChart(createPieDataTable(fSiData),
						createPieChartOptions());
				piePanel.add(pie);
			}
		};
		// Load the visualization api, passing the onLoadCallback to be called
		// when loading is done.
		VisualizationUtils.loadVisualizationApi(onLoadCallback,
				PieChart.PACKAGE);
	}

	private AbstractDataTable createPieDataTable(List<StatInvio> siData) {
		DataTable data = DataTable.create();
		data.addColumn(AbstractDataTable.ColumnType.STRING, "Tipo Abbonamento");
		data.addColumn(AbstractDataTable.ColumnType.NUMBER, "Tiratura");
		data.addRows(siData.size());
		for (int i = 0; i < siData.size(); i++) {
			String labelTipo = siData.get(i).getTipoAbbonamento().getCodice()+ " " +
					siData.get(i).getTipoAbbonamento().getNome();
			data.setValue(i, 0, labelTipo);
			data.setValue(i, 1, siData.get(i).getQuantita());
		}
		return data;
	}

	private PieChart.PieOptions createPieChartOptions() {
		PieChart.PieOptions options = PieChart.PieOptions.create();
		options.set3D(true);
		options.setWidth(PIE_CHART_WIDTH);
		options.setHeight(PIE_CHART_HEIGHT);
		options.setTitle(PIE_CHART_TITLE);
		return options;
	}
	
	
	//Async methods
	
	

	private void loadLastStatInvio(Integer idPeriodico) {
		AsyncCallback<List<StatInvio>> callback = new AsyncCallback<List<StatInvio>>() {
			@Override
			public void onFailure(Throwable e) {
				UiSingleton.get().addError(e);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(List<StatInvio> result) {
				drawLastStat(result);
				drawPieChart(result);
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		statService.findLastStatInvio(idPeriodico, callback);
	}

}
