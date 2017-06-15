package it.giunti.apg.client.widgets.tables;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.frames.OrdineLogisticaPopup;
import it.giunti.apg.client.services.SapService;
import it.giunti.apg.client.services.SapServiceAsync;
import it.giunti.apg.client.widgets.DestinatarioPanel;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.OrdiniLogistica;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;

public class OrdiniLogisticaTable extends PagingTable<OrdiniLogistica> {
	private static final SapServiceAsync sapService = GWT.create(SapService.class);
	
	private static final int TABLE_ROWS = AppConstants.TABLE_ROWS_DEFAULT;
	
	//private static final DateTimeFormat FORMAT_LIMIT = DateTimeFormat.getFormat("dd MMMM");
	
//	private IRefreshable parent = null;
//	private boolean isOperator = false;
	
	private AsyncCallback<List<OrdiniLogistica>> callback = new AsyncCallback<List<OrdiniLogistica>>() {
		@Override
		public void onFailure(Throwable caught) {
			setTableRows(new ArrayList<OrdiniLogistica>());
		}
		@Override
		public void onSuccess(List<OrdiniLogistica> result) {
			setTableRows(result);
		}
	};
	
	public OrdiniLogisticaTable(DataModel<OrdiniLogistica> model) {
		super(model, TABLE_ROWS);
//		this.parent = parent;
//		isOperator = (userRole.getId().intValue() >= AppConstants.RUOLO_OPERATOR);
		drawPage(0);
	}

	//@Override
	//public void refresh() {
	//	drawPage(0);
	//	if (parent != null) {
	//		parent.refresh();
	//	}
	//}
	
	@Override
	public void drawPage(int page) {
		clearInnerTable();
		getInnerTable().setHTML(0, 0, ClientConstants.LABEL_LOADING);
		getModel().find(page*TABLE_ROWS,
				TABLE_ROWS,
				callback);
	}
	
	@Override
	protected void addTableRow(int rowNum, OrdiniLogistica rowObj) {
		//final OrdiniLogisticaTable table = this;
		final OrdiniLogistica rowFinal = rowObj;
		// Set the data in the current row
		String linkText = "<b>"+rowObj.getNumeroOrdine()+"</b>";
		Anchor rowLink = new Anchor(linkText, true);
		rowLink.addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				@SuppressWarnings("unused")
				OrdineLogisticaPopup popup = new OrdineLogisticaPopup(rowFinal);
			}
		});
		getInnerTable().setWidget(rowNum, 0, rowLink);
		//Data inserimento
		getInnerTable().setHTML(rowNum, 1, ClientConstants.FORMAT_DAY.format(rowObj.getDataInserimento()));
		//Data stato
		String dataInvio = "In corso&nbsp;";
		if (rowObj.getDataChiusura() != null) {
			dataInvio = "Chiuso il "+ClientConstants.FORMAT_DAY.format(rowObj.getDataChiusura());
		}
		if (rowObj.getDataRifiuto() != null) {
			dataInvio = "Annullato interamente il "+ClientConstants.FORMAT_DAY.format(rowObj.getDataRifiuto());
		}
		getInnerTable().setHTML(rowNum, 2, dataInvio);
		//Anagrafica
		DestinatarioPanel destPanel = new DestinatarioPanel(rowObj.getIdAnagrafica(), true);
		getInnerTable().setWidget(rowNum, 3, destPanel);
		//Note
		String note = "";
		if (rowObj.getNote() != null) note += rowObj.getNote();
		//if (note.length()>NOTE_LENGTH) {
		//	note = note.substring(0, NOTE_LENGTH)+"&hellip;";
		//}
		
		getInnerTable().setHTML(rowNum, 4, note);
	}
	
	@Override
	protected void addHeader() {
		// Set the data in the current row
		getInnerTable().setHTML(0, 0, "Ordine");
		getInnerTable().setHTML(0, 1, "Creazione");
		getInnerTable().setHTML(0, 2, "Stato");
		getInnerTable().setHTML(0, 3, "Anagrafica");
		getInnerTable().setHTML(0, 4, "Note");
	}
	
	@Override
	protected void onEmptyResult() {}
	
	
	//Inner classes
	
	
	public static class OrdiniLogisticaModel implements DataModel<OrdiniLogistica> {
		private boolean showAnnullati = false;
		
		public OrdiniLogisticaModel(boolean showAnnullati) {
			this.showAnnullati = showAnnullati;
		}
		
		@Override
		public void find(int offset, int pageSize,
				AsyncCallback<List<OrdiniLogistica>> callback) {
			sapService.findOrdini(showAnnullati, offset, pageSize, callback);
		}
	}
	
}
