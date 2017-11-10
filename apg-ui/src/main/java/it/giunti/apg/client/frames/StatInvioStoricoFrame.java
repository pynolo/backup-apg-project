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
import it.giunti.apg.shared.model.StatInvio;
import it.giunti.apg.shared.model.TipiAbbonamento;
import it.giunti.apg.shared.model.Utenti;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.AnnotatedTimeLine;

public class StatInvioStoricoFrame extends FramePanel implements IAuthenticatedWidget {
	
	public static final String CHART_TITLE = "Storico degli invii";
	public static final String CHART_WIDTH = "600px";
	public static final String CHART_HEIGHT = "400px";
	
	private StatServiceAsync statService = GWT.create(StatService.class);
	
	private Integer idPeriodico = null;
	private List<List<StatInvio>> statMatrix = null;
	private List<TipiAbbonamento> tipiCompleteList = null;
	private Utenti utente = null;
	
	private FlowPanel panelTa = null;
	private PeriodiciSelect periodiciList = null;
	private TipiAbbonamentoTable tipiCompleteTable = null;
	private VerticalPanel chartPanel = null;
	
	// METHODS
	
	public StatInvioStoricoFrame(UriParameters params) {
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
			this.add(panelTa, "Statistiche: andamento degli invii");
			drawFrame();
		}
	}
	
	private void drawFrame() {
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
				params.triggerUri(UriManager.STAT_INVIO_STORICO);
			}
		});
		periodiciList.setEnabled(true);
		panelTa.add(periodiciList);
		tipiCompleteTable = new TipiAbbonamentoTable();
		panelTa.add(tipiCompleteTable);
		chartPanel = new VerticalPanel();
		panelTa.add(chartPanel);
		loadChartData(idPeriodico);
	}
		
	//private void changePeriodico() {
	//	chartPanel.clear();
	//	try {
	//		if (periodiciList != null) {
	//			if (periodiciList.getSelectedValueString() != null) {
	//				Integer idPeriodico = periodiciList.getSelectedValueInt();
	//				loadChartData(idPeriodico);
	//			}
	//		}
	//	} catch (NumberFormatException e) {	}
	//}
	
	private void fillTipiCompleteList() {
		tipiCompleteList = new ArrayList<TipiAbbonamento>();
		//Creazione elenco di TUTTI i tipi abbonamento
		for (List<StatInvio> invioList:statMatrix) {
			for (StatInvio si:invioList) {
				TipiAbbonamento ta = si.getTipoAbbonamento();
				if (!tipiCompleteList.contains(ta)) {
					tipiCompleteList.add(ta);
				}
			}
		}
		//Ordina i tipi
		Collections.sort(tipiCompleteList, new TipiAbbonamentoComparator());
	}
	
	private AbstractDataTable createTiraturaTable() {
		final DataTable data = DataTable.create();
		//Prepara le colonne dei tipi abbonamento
		data.addColumn(AbstractDataTable.ColumnType.DATE, "Data");// 0
		List<TipiAbbonamento> selectedTipi = tipiCompleteTable.getSelectedTipiList();
		if (selectedTipi.size() == 0) {
			//Nessun tipo scelto
			data.addColumn(AbstractDataTable.ColumnType.NUMBER,"Scegliere dei Tipi Abbonamento");
		} else {
			//La scelta dei tipi non è vuota
			for (TipiAbbonamento ta:selectedTipi) {
				data.addColumn(AbstractDataTable.ColumnType.NUMBER,
						ta.getCodice());//+" "+ta.getNome());// 1
			}
			//Prepara le righe
			data.addRows(statMatrix.size());
			int r = 0;
			for (List<StatInvio> invioList:statMatrix) {
				//Riga con i dati dell'invio: crea la mappa coi dati per posizionarli
				//correttamente nella tabella nella colonna del rispettivo tipo abbonamento
				Date dataInvio = null;
				Map<TipiAbbonamento,StatInvio> taMap = new HashMap<TipiAbbonamento, StatInvio>(); 
				for (StatInvio si:invioList) {
					TipiAbbonamento key = si.getTipoAbbonamento();
					taMap.put(key, si);
					dataInvio = si.getDataCreazione();
				}
				//Posiziona i dati nella colonna corretta
				data.setValue(r, 0, dataInvio);
				int col = 1;
				for (TipiAbbonamento ta:selectedTipi) {
					StatInvio si = taMap.get(ta);
					if (si != null) {
						data.setValue(r, col, si.getQuantita());
					} else {
						data.setValue(r, col, 0);
					}
					col++;
				}
				r++;
			}
		}
		return data;
	}
	
	
	// Charts methods
	
	
	private void loadChartPanel() {
		// Create a callback to be called when the visualization API
		// has been loaded.
		Runnable onLoadCallback = new Runnable() {
			public void run() {
				chartPanel.clear();
				// Create chart visualization.
				chartPanel.add(new HTML("<b>Andamento per tipo abbonamento</b>"));
				AnnotatedTimeLine tipiChart = new AnnotatedTimeLine(createTiraturaTable(),
						createAnnotatedTimeLineOptions(), CHART_WIDTH, CHART_HEIGHT);
				chartPanel.add(tipiChart);
			}
		};
		// Load the visualization api, passing the onLoadCallback to be called
		// when loading is done.
		VisualizationUtils.loadVisualizationApi(onLoadCallback,
				AnnotatedTimeLine.PACKAGE);
	}
	
	private AnnotatedTimeLine.Options createAnnotatedTimeLineOptions() {
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




	private void loadChartData(Integer idPeriodico) {
		AsyncCallback<List<List<StatInvio>>> callback = new AsyncCallback<List<List<StatInvio>>>() {
			@Override
			public void onFailure(Throwable e) {
				UiSingleton.get().addError(e);
				WaitSingleton.get().stop();
			}

			@Override
			public void onSuccess(List<List<StatInvio>> result) {
				statMatrix = result;
				fillTipiCompleteList();
				tipiCompleteTable.draw(tipiCompleteList);
				loadChartPanel();
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		statService.findStatInvio(idPeriodico, callback);
	}

	
	
	
	//Inner classes
	
	
	
	
	public static class TipiAbbonamentoComparator implements Comparator<TipiAbbonamento> {
		@Override
		public int compare(TipiAbbonamento o1, TipiAbbonamento o2) {
			String ta1 = o1.getCodice()+o2.getNome();
			String ta2 = o2.getCodice()+o2.getNome();
			return ta1.compareTo(ta2);
		}
	}

	public class TipiAbbonamentoTable extends FlexTable {
		private Map<TipiAbbonamento, CheckBox> tipiMap = null;
		
		public void draw(List<TipiAbbonamento> tipiCompleteList) {
			this.clear();
			drawAndFillMap(tipiCompleteList);
		}
		
		private void drawAndFillMap(List<TipiAbbonamento> tipiCompleteList) {
			if (tipiCompleteList == null) return;
			if (tipiCompleteList.size() == 0) {
				this.setHTML(0, 0, "Nessuna statistica da visualizzare");
				return;
			}
			tipiMap = new HashMap<TipiAbbonamento, CheckBox>();
			int r=0;
			for (TipiAbbonamento ta:tipiCompleteList) {
				CheckBox taCheck = new CheckBox();
				taCheck.setValue(false);
				tipiMap.put(ta, taCheck);
				String description = "<b>"+ta.getCodice()+"</b> "+ta.getNome();
				this.setWidget(r, 0, taCheck);
				this.setHTML(r, 1, description);
				r++;
			}
			Button updateButton = new Button("Aggiorna il grafico");
			updateButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					loadChartPanel();
				}
			});
			this.setWidget(r, 1, updateButton);
		}
		
		public List<TipiAbbonamento> getSelectedTipiList() {
			List<TipiAbbonamento> checkList = new ArrayList<TipiAbbonamento>();
			for(TipiAbbonamento ta:tipiMap.keySet()) {
				CheckBox cb = tipiMap.get(ta);
				if (cb.getValue()) {
					checkList.add(ta);
				}
			}
			return checkList;
		}
	}
}
