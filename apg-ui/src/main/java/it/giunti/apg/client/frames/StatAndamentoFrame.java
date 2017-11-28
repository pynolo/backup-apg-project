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
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.StatAbbonati;
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
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.AnnotatedTimeLine;

public class StatAndamentoFrame extends FramePanel implements IAuthenticatedWidget {
	
	public static final String CHART_TITLE = "Andamento abbonati";
	public static final String CHART_WIDTH = "600px";
	public static final String CHART_HEIGHT = "400px";
	private static final long MESE = 1000L * 60L * 60L * 24L * 30L;
	
	private StatServiceAsync statService = GWT.create(StatService.class);
	
	private Utenti utente = null;
	
	private Integer idPeriodico = null;
	private FlowPanel panelTa = null;
	private VerticalPanel statPanel = null;
	private PeriodiciSelect periodiciList = null;
	private VerticalPanel chartPanel = null;
	
	// METHODS
	
	public StatAndamentoFrame(UriParameters params) {
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
			this.add(panelTa, "Andamento abbonati");
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
				params.triggerUri(UriManager.STAT_ANDAMENTO);
			}
		});
		periodiciList.setEnabled(true);
		panelTa.add(periodiciList);
		statPanel = new VerticalPanel();
		panelTa.add(statPanel);
		chartPanel = new VerticalPanel();
		panelTa.add(chartPanel);
		changePeriodico();
	}
	
	private void drawStat(StatAbbonati item) {
		if (item.getTiratura() == null) item.setTiratura(0);
		if (item.getNuovi() == null) item.setNuovi(0);
		if (item.getDisdette() == null) item.setDisdette(0);
		if (item.getMorosiAnnoPrec() == null) item.setMorosiAnnoPrec(0);
		if (item.getPagati() == null) item.setPagati(0);
		if (item.getMorosiAttuali() == null) item.setMorosiAttuali(0);
		if (item.getOmaggi() == null) item.setOmaggi(0);
		NumberFormat df = NumberFormat.getFormat("#0");
		FlexTable table = new FlexTable();
		int r=0;
		table.setHTML(r, 0, "<h3>Statistica abbonamenti al "+
				ClientConstants.FORMAT_DAY.format(item.getDataCreazione())+"</h3>");
		table.getFlexCellFormatter().setColSpan(r, 0, 2);
		r++;
		table.setHTML(r, 0, "Prossima tiratura");
		table.setHTML(r, 1, "<b>"+df.format(item.getTiratura())+"</b>");
		//r++;
		//table.setHTML(r, 0, "Nuovi*");
		//table.setHTML(r, 1, "<b>"+df.format(item.getNuovi())+"</b>");
		r++;
		table.setHTML(r, 0, "Quote pagate");
		table.setHTML(r, 1, "<b>"+df.format(item.getPagati())+"</b>");
		r++;
		table.setHTML(r, 0, "Morosi (quote non pagate)");
		table.setHTML(r, 1, "<b>"+df.format(item.getMorosiAttuali())+"</b>");
		//r++;
		//table.setHTML(r, 0, "Morosi anno precedente*");
		//table.setHTML(r, 1, "<b>"+df.format(item.getMorosiAnnoPrec())+"</b>");
		r++;
		table.setHTML(r, 0, "Omaggi");
		table.setHTML(r, 1, "<b>"+df.format(item.getOmaggi())+"</b>");
		//r++;
		//table.setHTML(r, 0, "Disdette*");
		//table.setHTML(r, 1, "<b>"+df.format(item.getDisdette())+"</b>");
		//r++;
		//table.setHTML(r, 0, "<i>* algoritmi non ancora concordati</i>");
		r++;
		table.setHTML(r, 0, "&nbsp;");
		statPanel.add(table);
	}
	
	private void changePeriodico() {
		chartPanel.clear();
		try {
			if (periodiciList != null) {
				if (periodiciList.getSelectedValueString() != null) {
					idPeriodico = periodiciList.getSelectedValueInt();
				}
				loadStat(idPeriodico);
				loadChartData(chartPanel, idPeriodico);
			}
		} catch (NumberFormatException e) {	}
	}
	
	private AbstractDataTable createTiraturaTable(List<StatAbbonati> saData) {
		DataTable data = DataTable.create();
		data.addColumn(AbstractDataTable.ColumnType.DATE, "Data");// 0
		data.addColumn(AbstractDataTable.ColumnType.NUMBER, "Prossima tiratura");// 1
		data.addColumn(AbstractDataTable.ColumnType.NUMBER, "Quote pagate");// 2
		data.addRows(saData.size());
		for (int i = 0; i < saData.size(); i++) {
			if (saData.get(i).getDataCreazione() != null) data.setValue(i, 0, saData.get(i).getDataCreazione());
			if (saData.get(i).getTiratura() != null) data.setValue(i, 1, saData.get(i).getTiratura());
			if (saData.get(i).getPagati() != null) data.setValue(i, 2, saData.get(i).getPagati());
		}
		return data;
	}

	//private AbstractDataTable createNuoviTable(List<StatAbbonati> saData) {
	//	DataTable data = DataTable.create();
	//	data.addColumn(AbstractDataTable.ColumnType.DATE, "Data");// 0
	//	data.addColumn(AbstractDataTable.ColumnType.NUMBER, "Nuovi");// 1
	//	data.addRows(saData.size());
	//	for (int i = 0; i < saData.size(); i++) {
	//		if (saData.get(i).getDataCreazione() != null) data.setValue(i, 0, saData.get(i).getDataCreazione());
	//		if (saData.get(i).getNuovi() != null) data.setValue(i, 1, saData.get(i).getNuovi());
	//	}
	//	return data;
	//}
	
	//private AbstractDataTable createDisdetteTable(List<StatAbbonati> saData) {
	//	DataTable data = DataTable.create();
	//	data.addColumn(AbstractDataTable.ColumnType.DATE, "Data");// 0
	//	data.addColumn(AbstractDataTable.ColumnType.NUMBER, "Disdette");// 1
	//	data.addRows(saData.size());
	//	for (int i = 0; i < saData.size(); i++) {
	//		if (saData.get(i).getDataCreazione() != null) data.setValue(i, 0, saData.get(i).getDataCreazione());
	//		if (saData.get(i).getDisdette() != null) data.setValue(i, 1, saData.get(i).getDisdette());
	//	}
	//	return data;
	//}
	
	private AbstractDataTable createMorosiTable(List<StatAbbonati> saData) {
		DataTable data = DataTable.create();
		data.addColumn(AbstractDataTable.ColumnType.DATE, "Data");// 0
		data.addColumn(AbstractDataTable.ColumnType.NUMBER, "Morosi");// 1
		//data.addColumn(AbstractDataTable.ColumnType.NUMBER, "Morosi anno prec.");// 2
		data.addRows(saData.size());
		for (int i = 0; i < saData.size(); i++) {
			if (saData.get(i).getDataCreazione() != null) data.setValue(i, 0, saData.get(i).getDataCreazione());
			if (saData.get(i).getMorosiAttuali() != null) data.setValue(i, 1, saData.get(i).getMorosiAttuali());
			//if (saData.get(i).getMorosiAnnoPrec() != null) data.setValue(i, 2, saData.get(i).getMorosiAnnoPrec());
		}
		return data;
	}
	
//	private AbstractDataTable createDataTable(List<StatAbbonati> saData) {
//	DataTable data = DataTable.create();
//	data.addColumn(AbstractDataTable.ColumnType.DATE, "Data");// 0
//	data.addColumn(AbstractDataTable.ColumnType.NUMBER, "In vigore");// 1
//	data.addColumn(AbstractDataTable.ColumnType.NUMBER, "Nuovi");// 2
//	data.addColumn(AbstractDataTable.ColumnType.NUMBER, "Disdette");// 3
//	data.addColumn(AbstractDataTable.ColumnType.NUMBER, "Morosi anno prec.");// 4
//	data.addColumn(AbstractDataTable.ColumnType.NUMBER, "Pagati");// 5
//	data.addColumn(AbstractDataTable.ColumnType.NUMBER, "Morosi");// 6
//	data.addColumn(AbstractDataTable.ColumnType.NUMBER, "Omaggi");// 7
//	data.addRows(saData.size());
//	for (int i = 0; i < saData.size(); i++) {
//		if (saData.get(i).getDataCreazione() != null) data.setValue(i, 0, saData.get(i).getDataCreazione());
//		if (saData.get(i).getInVigore() != null) data.setValue(i, 1, saData.get(i).getInVigore());
//		if (saData.get(i).getNuovi() != null) data.setValue(i, 2, saData.get(i).getNuovi());
//		if (saData.get(i).getDisdette() != null) data.setValue(i, 3, saData.get(i).getDisdette());
//		if (saData.get(i).getMorosiAnnoPrec() != null) data.setValue(i, 4, saData.get(i).getMorosiAnnoPrec());
//		if (saData.get(i).getPagati() != null) data.setValue(i, 5, saData.get(i).getPagati());
//		if (saData.get(i).getMorosiAttuali() != null) data.setValue(i, 6, saData.get(i).getMorosiAttuali());
//		if (saData.get(i).getOmaggi() != null) data.setValue(i, 7, saData.get(i).getOmaggi());
//	}
//	return data;
//}
	
	private AnnotatedTimeLine.Options createOptions() {
		AnnotatedTimeLine.Options options = AnnotatedTimeLine.Options.create();
		options.set("displayExactValues", true);
		options.set("displayRangeSelector", true);
		options.set("displayZoomButtons", false);
		options.set("fill", 10D);
		options.set("scaleType", "maximized");
		options.set("thickness", 1D);
		return options;
	}
	
	
	
	//Async methods
	
	

	private void loadStat(Integer idPeriodico) {
		AsyncCallback<StatAbbonati> callback = new AsyncCallback<StatAbbonati>() {
			@Override
			public void onFailure(Throwable e) {
				UiSingleton.get().addError(e);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(StatAbbonati result) {
				drawStat(result);
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		statService.findLastStatAbbonati(idPeriodico, callback);
	}
	

	private void loadChartData(Panel chartPanel, Integer idPeriodico) {
		final Panel fChartPanel = chartPanel;
		AsyncCallback<List<StatAbbonati>> callback = new AsyncCallback<List<StatAbbonati>>() {
			@Override
			public void onFailure(Throwable e) {
				UiSingleton.get().addError(e);
				WaitSingleton.get().stop();
			}

			@Override
			public void onSuccess(List<StatAbbonati> result) {
				loadChartPanel(fChartPanel, result);
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		long dataFineLong = DateUtil.now().getTime();
		long dataInizioLong = dataFineLong - MESE*48;
		statService.findStatAbbonatiBetweenDates(idPeriodico,
				new Date(dataInizioLong),
				new Date(dataFineLong),
				callback);
	}

	private void loadChartPanel(Panel chartPanel, List<StatAbbonati> saData) {
		final Panel fChartPanel = chartPanel;
		final List<StatAbbonati> fTaData = saData;
		// Create a callback to be called when the visualization API
		// has been loaded.
		Runnable onLoadCallback = new Runnable() {
			public void run() {
				// Create chart visualization.
				fChartPanel.add(new HTML("<b>Andamento tiratura</b>"));
				AnnotatedTimeLine inVigoreChart = new AnnotatedTimeLine(createTiraturaTable(fTaData),
						createOptions(), CHART_WIDTH, CHART_HEIGHT);
				fChartPanel.add(inVigoreChart);
				//fChartPanel.add(new HTML("<br/><b>Andamento nuovi</b>"));
				//AnnotatedTimeLine nuoviChart = new AnnotatedTimeLine(createNuoviTable(fTaData),
				//		createOptions(), CHART_WIDTH, CHART_HEIGHT);
				//fChartPanel.add(nuoviChart);
				fChartPanel.add(new HTML("<br/><b>Andamento quote non pagate</b>"));
				AnnotatedTimeLine morosiChart = new AnnotatedTimeLine(createMorosiTable(fTaData),
						createOptions(), CHART_WIDTH, CHART_HEIGHT);
				fChartPanel.add(morosiChart);
				//fChartPanel.add(new HTML("<br/><b>Andamento disdette</b>"));
				//AnnotatedTimeLine disdetteChart = new AnnotatedTimeLine(createDisdetteTable(fTaData),
				//		createOptions(), CHART_WIDTH, CHART_HEIGHT);
				//fChartPanel.add(disdetteChart);
			}
		};
		// Load the visualization api, passing the onLoadCallback to be called
		// when loading is done.
		VisualizationUtils.loadVisualizationApi(onLoadCallback,
				AnnotatedTimeLine.PACKAGE);
	}
}
