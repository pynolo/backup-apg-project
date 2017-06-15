package it.giunti.apg.client.widgets.tables;

import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.services.LoggingService;
import it.giunti.apg.client.services.LoggingServiceAsync;
import it.giunti.apg.shared.AppConstants;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

public class LogTable extends FlexTable {

	private static final String LOG_END_STRING = "FINE: operazione conclusa";
	private final DataModel<String> model;
	private WaitPanel waitPanel = null;
	private List<String> logList = new ArrayList<String>();
	private int idx = 0;
	
	private AsyncCallback<List<String>> callback = new AsyncCallback<List<String>>() {
		@Override
		public void onFailure(Throwable caught) {
			UiSingleton.get().addError(caught);
		}
		@Override
		public void onSuccess(List<String> result) {
			boolean logEnd = false;
			for (int i=0; i<result.size();i++) {
				logList.add(result.get(i));
				if (result.get(i) != null) {
					if (result.get(i).contains(AppConstants.MSG_LOG_END)) {
						//Log ultimato
						addTableRow(LOG_END_STRING);
						waitPanel.clear();
						logEnd = true;
					} else {
						//Il log non è ultimato
						addTableRow(result.get(i));
					}
				}
			}
			if (!logEnd) {
				//Se il log non è finito resta in ascolto
				downloadLog();
			}
		}
	};
	
	public LogTable(DataModel<String> model) {
		this.model=model;
		waitPanel = new WaitPanel();
		this.setWidget(0, 0, waitPanel);
		downloadLog();
	}
	
	private void addTableRow(String logLine) {
		this.setHTML(idx, 0, logLine);
		this.setWidget(idx+1, 0, waitPanel);
		idx++;
	}
	
	private void downloadLog() {
		model.find(idx, 0, callback);
	}
	
	
	//Inner Classes
	
	
	
	public static class LogModel implements DataModel<String> {
		private final LoggingServiceAsync loggingService = GWT.create(LoggingService.class);
		private Integer logId =null;
		
		public LogModel(Integer logId) {
			this.logId = logId;
		}
		
		@Override
		public void find(int offset, int pageSize,
				AsyncCallback<List<String>> callback) {
			loggingService.receiveLogLines(logId, offset, callback);
		}
	}
	
	public class WaitPanel extends HorizontalPanel {
		public WaitPanel() {
			Image waitImg = new Image("img/ajax-loader-small.gif");
			HTML label = new HTML("&nbsp;<i>attendere...</i>");
			this.add(waitImg);
			this.add(label);
		}
	}
}
