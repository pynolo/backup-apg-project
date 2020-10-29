package it.giunti.apg.client.widgets.tables;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.frames.MaterialiPopUp;
import it.giunti.apg.client.services.MaterialiService;
import it.giunti.apg.client.services.MaterialiServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Materiali;

public class MaterialiTable extends PagingTable<Materiali> implements IRefreshable {
	
	private static final int TABLE_ROWS = AppConstants.TABLE_ROWS_DEFAULT;
	private boolean isEditor = false;
	
	private AsyncCallback<List<Materiali>> callback = new AsyncCallback<List<Materiali>>() {
		@Override
		public void onFailure(Throwable caught) {
			setTableRows(new ArrayList<Materiali>());
			//WaitSingleton.get().stop();
		}
		@Override
		public void onSuccess(List<Materiali> result) {
			setTableRows(result);
			//WaitSingleton.get().stop();
		}
	};
	
	public MaterialiTable(DataModel<Materiali> model, boolean isEditor) {
		super(model, TABLE_ROWS);
		this.isEditor=isEditor;
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
	protected void addTableRow(int rowNum, Materiali rowObj) {
		final Materiali rowFinal = rowObj;
		final MaterialiTable matTable = this;
		// Set the data in the current row
		//Codice Meccanografico
		String codice = "";
		if (rowObj.getCodiceMeccanografico() != null) codice = rowObj.getCodiceMeccanografico();
		if (isEditor) {
			Anchor rowLink = new Anchor("<b>"+codice+"</b>", true);
			rowLink.addMouseDownHandler(new MouseDownHandler() {
				@Override
				public void onMouseDown(MouseDownEvent event) {
					new MaterialiPopUp(rowFinal.getId(), matTable);
				}
			});
			getInnerTable().setWidget(rowNum, 0, rowLink);
		} else {
			getInnerTable().setHTML(rowNum, 0, "<b>"+codice+"</b>");
		}
		//Descrizione
		String titolo = "";
		if (rowObj.getTitolo() != null) titolo += "<b>"+rowObj.getTitolo()+"</b> ";
		if (rowObj.getSottotitolo() != null) {
			if (!rowObj.getSottotitolo().equals("")) {
				if (titolo.length() > 0) titolo += "- ";
				titolo += rowObj.getSottotitolo();
			}
		}
		getInnerTable().setHTML(rowNum, 1, titolo);
		//Tipo
		String tipo = "";
		if (rowObj.getIdTipoMateriale() != null) 
			tipo += AppConstants.MATERIALE_DESC.get(rowObj.getIdTipoMateriale());
		getInnerTable().setHTML(rowNum, 2, tipo);
		//Disponibilità opzione
		String disp = "";
		if (rowObj.getDataLimiteVisibilita() != null) disp += " <i>fino al "+ClientConstants.FORMAT_DAY.format(rowObj.getDataLimiteVisibilita())+"</i> ";
		getInnerTable().setHTML(rowNum, 3, disp);
		//Invio sospeso
		String inAttesa = "NO";
		if (rowObj.getInAttesa()) inAttesa = "<b>SI</b>";
		getInnerTable().setHTML(rowNum, 4, inAttesa+"&nbsp;");
		//Articolo
		getInnerTable().setHTML(rowNum, 5, "<i>"+
				AppConstants.ANAGRAFICA_SAP_DESC.get(rowObj.getIdTipoAnagraficaSap())+"</i>");
	}
	
	@Override
	protected void addHeader() {
		// Set the data in the current row
		getInnerTable().setHTML(0, 0, "CM");
		getInnerTable().setHTML(0, 1, "Titolo");
		getInnerTable().setHTML(0, 2, "Tipo");
		getInnerTable().setHTML(0, 3, "Visibile&nbsp;");
		getInnerTable().setHTML(0, 4, "In&nbsp;attesa");
		getInnerTable().setHTML(0, 5, "Anagrafica SAP");
	}
	
	@Override
	protected void onEmptyResult() {}
	
	
	
	//Inner classes
	
	
	
	public static class MaterialiModel implements DataModel<Materiali> {
		private MaterialiServiceAsync matService = GWT.create(MaterialiService.class);
		private String search = null;
		private Date extractionDt = null;
		
		public MaterialiModel(String search, Date extractionDt) {
			this.search = search;
			this.extractionDt = extractionDt;
		}

		@Override
		public void find(int offset, int pageSize, AsyncCallback<List<Materiali>> callback) {
			//WaitSingleton.get().start();
			matService.findMaterialiByStringAndDate(search, extractionDt, offset, pageSize, callback);
		}
	}
	
}
