package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.StatService;
import it.giunti.apg.client.services.StatServiceAsync;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.StatData;
import it.giunti.apg.shared.model.Periodici;
import it.giunti.apg.shared.model.Utenti;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart;

public class StatPeriodiciFrame extends FramePanel implements
		IAuthenticatedWidget {

	public static final String CHART_TITLE = "Tiratura per periodico";
	public static final int CHART_WIDTH = 750;
	public static final int CHART_HEIGHT = 450;

	private Date date;

	private VerticalPanel panel = null;

	public StatPeriodiciFrame(UriParameters params) {
		super();
		if (params == null) {
			params = new UriParameters();
		}
		date = params.getDateValue(AppConstants.PARAM_DATE);
		if (date == null) date = new Date();
		AuthSingleton.get().queueForAuthentication(this);
	}

	@Override
	public void onSuccessfulAuthentication(Utenti utente) {
		// Editing rights
		int ruolo = utente.getRuolo().getId();
		// UI
		if (ruolo >= AppConstants.RUOLO_EDITOR) {
			panel = new VerticalPanel();
			this.add(panel, "Periodici");
			drawStack(date);
		}
	}

	private void drawStack(Date extractionDt) {
		panel.clear();
		SimplePanel chartPanel = new SimplePanel();
		panel.add(chartPanel);
		loadChartData(chartPanel);
	}

	private void loadChartData(Panel chartPanel) {
		final Panel fChartPanel = chartPanel;
		StatServiceAsync statService = GWT.create(StatService.class);
		AsyncCallback<List<StatData<Periodici>>> callback = new AsyncCallback<List<StatData<Periodici>>>() {
			@Override
			public void onFailure(Throwable e) {
				UiSingleton.get().addError(e);
				WaitSingleton.get().stop();
			}

			@Override
			public void onSuccess(List<StatData<Periodici>> result) {
				loadChartPanel(fChartPanel, result);
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		statService.statTiraturaPeriodici(callback);
	}

	private void loadChartPanel(Panel chartPanel, List<StatData<Periodici>> periodiciData) {
		final Panel fChartPanel = chartPanel;
		final List<StatData<Periodici>> fPeriodiciData = periodiciData;
		// Create a callback to be called when the visualization API
		// has been loaded.
		Runnable onLoadCallback = new Runnable() {
			public void run() {
				// Create a pie chart visualization.
				PieChart pie = new PieChart(createDataTable(fPeriodiciData),
						createOptions());
				fChartPanel.add(pie);
			}
		};
		// Load the visualization api, passing the onLoadCallback to be called
		// when loading is done.
		VisualizationUtils.loadVisualizationApi(onLoadCallback,
				PieChart.PACKAGE);
	}

	private AbstractDataTable createDataTable(List<StatData<Periodici>> periodiciData) {
		DataTable data = DataTable.create();
		data.addColumn(AbstractDataTable.ColumnType.STRING, "Periodico");
		data.addColumn(AbstractDataTable.ColumnType.NUMBER, "Tiratura");
		data.addRows(periodiciData.size());
		for (int i = 0; i < periodiciData.size(); i++) {
			String label = periodiciData.get(i).getName().getNome();
			data.setValue(i, 0, label);
			data.setValue(i, 1, periodiciData.get(i).getValue());
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
