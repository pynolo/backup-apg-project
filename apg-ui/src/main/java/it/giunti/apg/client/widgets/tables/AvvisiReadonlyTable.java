package it.giunti.apg.client.widgets.tables;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.services.LoggingService;
import it.giunti.apg.client.services.LoggingServiceAsync;
import it.giunti.apg.shared.model.Avvisi;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;

public class AvvisiReadonlyTable extends PagingTable<Avvisi> implements IRefreshable {
	
	private static final int TABLE_ROWS = 25;
	private static final DateTimeFormat DTF_DAY = DateTimeFormat.getFormat("dd/MM/yyyy");
	private static final DateTimeFormat DTF_HOUR = DateTimeFormat.getFormat("HH:mm");
	private static final LoggingServiceAsync loggingService = GWT.create(LoggingService.class);
	
	private AsyncCallback<List<Avvisi>> callback = new AsyncCallback<List<Avvisi>>() {
		@Override
		public void onFailure(Throwable caught) {
			getInnerTable().clear();
			InlineHTML label = new InlineHTML("Nessun avviso recente");
			label.setStyleName("avviso-text");
			getInnerTable().setWidget(0, 0, label);
		}
		@Override
		public void onSuccess(List<Avvisi> result) {
			setTableRows(result);
			//WaitSingleton.get().stop();
		}
	};
	
	public AvvisiReadonlyTable(DataModel<Avvisi> model) {
		super(model, TABLE_ROWS);
		drawPage(0);
	}

	@Override
	public void drawPage(int page) {
		clearInnerTable();
		getInnerTable().setHTML(0, 0, ClientConstants.LABEL_LOADING);
		getModel().find(page*TABLE_ROWS,
				TABLE_ROWS,
				callback);
	}
	
	public void refresh() {
		drawPage(0);
	}
	
	@Override
	protected void addTableRow(int rowNum, Avvisi rowObj) {
		// Data-ora
		InlineHTML data = new InlineHTML(
				DTF_DAY.format(rowObj.getData()) + "&nbsp;" +
				DTF_HOUR.format(rowObj.getData()) );
		data.setStyleName("avviso-date");
		getInnerTable().setWidget(rowNum, 0, data);
		getInnerTable().getColumnFormatter().setWidth(0, "10%");
		// Avviso
		HorizontalPanel rowPanel = new HorizontalPanel();
		if (rowObj.getImportante()) {
			InlineHTML warn = new InlineHTML(ClientConstants.ICON_WARNING);
			warn.setStyleName("align-left");
			rowPanel.add(warn);
		}
		InlineHTML messaggio = new InlineHTML(rowObj.getMessaggio());
		if (rowObj.getImportante()) {
			messaggio.setStyleName("avviso-important");
		} else {
			messaggio.setStyleName("avviso-text");
		}
		rowPanel.add(messaggio);
		getInnerTable().setWidget(rowNum, 1, rowPanel);
	}
	
	@Override
	protected void addHeader() {
		getInnerTable().setWidth("60em");
		getInnerTable().setHTML(0, 0, "Data");
		getInnerTable().setHTML(0, 1, "Avviso");
	}
	
	@Override
	protected void onEmptyResult() {}
	
	
	//Inner classes
	
	
	
	public static class AvvisiModel implements DataModel<Avvisi> {
		
		public AvvisiModel() {
		}
		
		@Override
		public void find(int offset, int pageSize, AsyncCallback<List<Avvisi>> callback) {
			//WaitSingleton.get().start();
			loggingService.findLastAvvisi(offset, pageSize, callback);
		}
	}
	
}
