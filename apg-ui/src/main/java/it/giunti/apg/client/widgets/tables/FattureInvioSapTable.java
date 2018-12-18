package it.giunti.apg.client.widgets.tables;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.services.LoggingService;
import it.giunti.apg.client.services.LoggingServiceAsync;
import it.giunti.apg.client.widgets.FatturaStampaLink;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.FattureInvioSap;
import it.giunti.apg.shared.model.Ruoli;

public class FattureInvioSapTable extends PagingTable<FattureInvioSap> {
	
	private static final LoggingServiceAsync loggingService = GWT.create(LoggingService.class);
	
	private static final int TABLE_ROWS = 50;
	private boolean isOperator = false;
	
	private AsyncCallback<List<FattureInvioSap>> callback = new AsyncCallback<List<FattureInvioSap>>() {
		@Override
		public void onFailure(Throwable caught) {
			setTableRows(new ArrayList<FattureInvioSap>());
			//WaitSingleton.get().stop();
		}
		@Override
		public void onSuccess(List<FattureInvioSap> result) {
			setTableRows(result);
			//WaitSingleton.get().stop();
		}
	};
	
	public FattureInvioSapTable(DataModel<FattureInvioSap> model, Ruoli role) {
		super(model, TABLE_ROWS);
		this.isOperator = (role.getId() >= AppConstants.RUOLO_OPERATOR);
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
	protected void addTableRow(int rowNum, FattureInvioSap rowObj) {
		// Set the data in the current row
		//n° invio
		getInnerTable().setHTML(rowNum, 0, "<b>"+rowObj.getIdInvio()+"</b> del "+
				ClientConstants.FORMAT_DATETIME.format(rowObj.getDataCreazione()));
		//Fattura
		HorizontalPanel fPanel = new HorizontalPanel();
		if (isOperator) {
			FatturaStampaLink fsLink = new FatturaStampaLink(rowObj.getIdFattura());
			fPanel.add(fsLink);
		} else {
			fPanel.add(new InlineHTML(rowObj.getNumeroFattura()));
		}
		getInnerTable().setWidget(rowNum, 1, fPanel);
		//Risposta
		String errDesc = "";
		if (rowObj.getErrTable() != null) errDesc += rowObj.getErrTable()+" - ";
		if (rowObj.getErrField() != null) errDesc += rowObj.getErrField()+" - ";
		if (rowObj.getErrMessage() != null) errDesc += rowObj.getErrMessage();
		HTML rispostaHtml = new HTML("OK");
		if (errDesc.length() > 0) {
			rispostaHtml.setHTML(errDesc);
			rispostaHtml.setStyleName("text-danger");
		}
		getInnerTable().setWidget(rowNum, 2, rispostaHtml);
	}
	
	@Override
	protected void addHeader() {
		// Set the data in the current row
		getInnerTable().setHTML(0, 0, "Invio");
		getInnerTable().setHTML(0, 1, "Fattura");
		getInnerTable().setHTML(0, 2, "Risposta");
	}
	
	@Override
	protected void onEmptyResult() {}
	
	
	
	//Inner classes
	
	
	
	public static class FattureInvioSapModel implements DataModel<FattureInvioSap> {
		private Long startDt = null;
		private Long finishDt = null;
		private boolean errorOnly = false;
		
		public FattureInvioSapModel(long startDt, long finishDt, boolean errorOnly) {
			this.startDt = startDt;
			this.finishDt = finishDt;
			this.errorOnly = errorOnly;
		}
		
		@Override
		public void find(int offset, int pageSize,
				AsyncCallback<List<FattureInvioSap>> callback) {
			//WaitSingleton.get().start();
			loggingService.findFattureInvioSap(startDt, finishDt, errorOnly, offset, pageSize, callback);
		}
	}
	
}
