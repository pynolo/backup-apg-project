package it.giunti.apg.client.widgets.tables;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.UriManager;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.services.LoggingService;
import it.giunti.apg.client.services.LoggingServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Rapporti;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineHTML;

public class RapportiTable extends PagingTable<Rapporti> {
	
	private static final int TABLE_ROWS = AppConstants.TABLE_ROWS_DEFAULT;
	private static final DateTimeFormat DTF = DateTimeFormat.getFormat("dd/MM/yyyy HH:mm");
	
	private AsyncCallback<List<Rapporti>> callback = new AsyncCallback<List<Rapporti>>() {
		@Override
		public void onFailure(Throwable caught) {
			setTableRows(new ArrayList<Rapporti>());
		}
		@Override
		public void onSuccess(List<Rapporti> result) {
			setTableRows(result);
		}
	};
	
	public RapportiTable(DataModel<Rapporti> model) {
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
	
	@Override
	protected void addTableRow(int rowNum, Rapporti rowObj) {
		// TIMESTAMP
		String linkText = DTF.format(rowObj.getDataModifica());
		linkText = "<b>"+linkText+"</b> ";
		UriParameters params = new UriParameters();
		params.add(AppConstants.PARAM_ID, rowObj.getId());
		if (rowObj.getTerminato()) {
			Hyperlink rowLink = params.getHyperlink(linkText, UriManager.RAPPORTO);
			getInnerTable().setWidget(rowNum, 0, rowLink);
		} else {
			InlineHTML row = new InlineHTML(linkText);
			getInnerTable().setWidget(rowNum, 0, row);
		}
		// TITOLO
		String titolo = rowObj.getTitolo()+" ";
		if (!rowObj.getTerminato()) {
			if (rowObj.getDataModifica().getTime()+8*AppConstants.HOUR < new Date().getTime()) {
				//MODIFICATO DA PIU' DI 1 GIORNO
				titolo += ClientConstants.ICON_AMBULANCE+" ";
			} else {
				//MODIFICATO DA POCHE ORE
				titolo += ClientConstants.ICON_LOADING_SMALL+" ";
			}
		}
		if (rowObj.getErrore()) titolo += ClientConstants.ICON_AMBULANCE+" ";
		getInnerTable().setHTML(rowNum, 1, titolo);
	}
	
	@Override
	protected void addHeader() {
		// Set the data in the current row
		getInnerTable().setHTML(0, 0, "Data");
		getInnerTable().setHTML(0, 1, "Contenuto");
	}
	
	@Override
	protected void onEmptyResult() {}
	
	
	
	//Inner classes
	
	
	
	public static class RapportiModel implements DataModel<Rapporti> {
		private final LoggingServiceAsync loggingService = GWT.create(LoggingService.class);
		private Date extractionDt = null;
		
		public RapportiModel(Date extractionDt) {
			this.extractionDt=extractionDt;
		}
		
		@Override
		public void find(int offset, int pageSize,
				AsyncCallback<List<Rapporti>> callback) {
			loggingService.findRapportiStripped(extractionDt, offset, pageSize, callback);
		}
	}
	
}
