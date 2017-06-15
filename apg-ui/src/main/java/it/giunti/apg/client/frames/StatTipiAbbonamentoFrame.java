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
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.StatData;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.TipiAbbonamento;
import it.giunti.apg.shared.model.Utenti;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart;

public class StatTipiAbbonamentoFrame extends FramePanel implements IAuthenticatedWidget {
	
	public static final String CHART_TITLE = "Abbonamenti per tipo abbonamento";
	public static final int CHART_WIDTH = 750;
	public static final int CHART_HEIGHT = 450;
	
	private Integer idPeriodico = null;
	private Utenti utente = null;
	
	private FlowPanel panelTa = null;
	private PeriodiciSelect periodiciList = null;
	private SimplePanel chartPanel = null;
	
	// METHODS
	
	public StatTipiAbbonamentoFrame(UriParameters params) {
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
			this.add(panelTa, "Tipi abbonamento");
			drawResults();
		}
	}
	
	private void drawResults() {
		if (idPeriodico == null) idPeriodico=UiSingleton.get().getDefaultIdPeriodico(utente);
		// Periodico
		panelTa.add(new HTML("Periodico&nbsp;"));
		periodiciList = new PeriodiciSelect(idPeriodico, new Date(), false, false, utente);
		periodiciList.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				idPeriodico = periodiciList.getSelectedValueInt();
				CookieSingleton.get().setCookie(ClientConstants.COOKIE_LAST_PERIODICO, idPeriodico+"");
				UriParameters params = new UriParameters();
				params.add(AppConstants.PARAM_ID_PERIODICO, idPeriodico);
				params.triggerUri(UriManager.STAT_TIPI_ABBONAMENTO);
			}
		});
		periodiciList.setEnabled(true);
		panelTa.add(periodiciList);
		chartPanel = new SimplePanel();
		panelTa.add(chartPanel);
		changePeriodico();
	}
	
	
	private void changePeriodico() {
		chartPanel.clear();
		try {
			if (periodiciList != null) {
				if (periodiciList.getSelectedValueString() != null) {
					idPeriodico = periodiciList.getSelectedValueInt();
				}
				loadChartData(chartPanel, idPeriodico);
			}
		} catch (NumberFormatException e) {
		} catch (IndexOutOfBoundsException e) {}
	}
	

	private void loadChartData(Panel chartPanel, Integer idPeriodico) {
		final Panel fChartPanel = chartPanel;
		StatServiceAsync statService = GWT.create(StatService.class);
		AsyncCallback<List<StatData<TipiAbbonamento>>> callback = new AsyncCallback<List<StatData<TipiAbbonamento>>>() {
			@Override
			public void onFailure(Throwable e) {
				UiSingleton.get().addError(e);
				WaitSingleton.get().stop();
			}

			@Override
			public void onSuccess(List<StatData<TipiAbbonamento>> result) {
				loadChartPanel(fChartPanel, result);
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		statService.statTipiAbbPeriodico(new Date(), idPeriodico, callback);
	}

	private void loadChartPanel(Panel chartPanel, List<StatData<TipiAbbonamento>> taData) {
		final Panel fChartPanel = chartPanel;
		final List<StatData<TipiAbbonamento>> fTaData = taData;
		// Create a callback to be called when the visualization API
		// has been loaded.
		Runnable onLoadCallback = new Runnable() {
			public void run() {
				// Create a pie chart visualization.
				PieChart pie = new PieChart(createDataTable(fTaData),
						createOptions());
				fChartPanel.add(pie);
			}
		};
		// Load the visualization api, passing the onLoadCallback to be called
		// when loading is done.
		VisualizationUtils.loadVisualizationApi(onLoadCallback,
				PieChart.PACKAGE);
	}

	private AbstractDataTable createDataTable(List<StatData<TipiAbbonamento>> taData) {
		DataTable data = DataTable.create();
		data.addColumn(AbstractDataTable.ColumnType.STRING, "Tipo Abbonamento");
		data.addColumn(AbstractDataTable.ColumnType.NUMBER, "Abbonati");
		data.addRows(taData.size());
		for (int i = 0; i < taData.size(); i++) {
			String label = ((TipiAbbonamento) taData.get(i).getName())
					.getNome();//+" ("+taData.get(i).getValue()+")";
			data.setValue(i, 0, label);
			data.setValue(i, 1, taData.get(i).getValue());
		}
		return data;
	}

	private PieChart.PieOptions createOptions() {
		PieChart.PieOptions options = PieChart.PieOptions.create();
		options.set3D(true);
		options.setWidth(CHART_WIDTH);
		options.setHeight(CHART_HEIGHT);
		options.setTitle(CHART_TITLE);
		return options;
	}
}
