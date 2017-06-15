package it.giunti.apg.client.widgets.tables;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.frames.ArticoloPopUp;
import it.giunti.apg.client.services.ArticoliService;
import it.giunti.apg.client.services.ArticoliServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Articoli;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;

public class ArticoliTable extends PagingTable<Articoli> implements IRefreshable {
	
	private static final int TABLE_ROWS = AppConstants.TABLE_ROWS_DEFAULT;
	private boolean isEditor = false;
	
	private AsyncCallback<List<Articoli>> callback = new AsyncCallback<List<Articoli>>() {
		@Override
		public void onFailure(Throwable caught) {
			setTableRows(new ArrayList<Articoli>());
			//WaitSingleton.get().stop();
		}
		@Override
		public void onSuccess(List<Articoli> result) {
			setTableRows(result);
			//WaitSingleton.get().stop();
		}
	};
	
	public ArticoliTable(DataModel<Articoli> model, boolean isEditor) {
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
	protected void addTableRow(int rowNum, Articoli rowObj) {
		final Articoli rowFinal = rowObj;
		final ArticoliTable articoliTable = this;
		// Set the data in the current row
		//Codice Meccanografico
		String codice = "";
		if (rowObj.getCodiceMeccanografico() != null) codice = rowObj.getCodiceMeccanografico();
		if (isEditor) {
			Anchor rowLink = new Anchor("<b>"+codice+"</b>", true);
			rowLink.addMouseDownHandler(new MouseDownHandler() {
				@Override
				public void onMouseDown(MouseDownEvent event) {
					new ArticoloPopUp(rowFinal.getId(), articoliTable);
				}
			});
			getInnerTable().setWidget(rowNum, 0, rowLink);
		} else {
			getInnerTable().setHTML(rowNum, 0, "<b>"+codice+"</b>");
		}
		//Descrizione
		String titolo = "";
		if (rowObj.getCodiceInterno() != null) {
			if (!rowObj.getCodiceInterno().equals("")) {
				titolo += "["+rowObj.getCodiceInterno()+"] ";
			}
		}
		if (rowObj.getTitoloNumero() != null) titolo += "<b>"+rowObj.getTitoloNumero()+"</b> ";
		if (rowObj.getCartaceo()) titolo += " "+ClientConstants.ICON_CARTACEO;
		if (rowObj.getDigitale()) titolo += " "+ClientConstants.ICON_APP;
		if (rowObj.getAutore() != null) {
			if (!rowObj.getAutore().equals("")) {
				if (titolo.length() > 0) titolo += "- ";
				titolo += rowObj.getAutore();
			}
		}
		getInnerTable().setHTML(rowNum, 1, titolo);
		//Disponibilità opzione
		String disp = "";
		if (rowObj.getDataInizio() != null) disp += " <i>dal "+ClientConstants.FORMAT_DAY.format(rowObj.getDataInizio())+"</i> ";
		if (rowObj.getDataFine() != null) disp += " <i>fino al "+ClientConstants.FORMAT_DAY.format(rowObj.getDataFine())+"</i> ";
		getInnerTable().setHTML(rowNum, 2, disp);
		//Invio sospeso
		String inAttesa = "NO";
		if (rowObj.getInAttesa()) inAttesa = "<b>SI</b>";
		getInnerTable().setHTML(rowNum, 3, inAttesa+"&nbsp;");
		//Articolo
		getInnerTable().setHTML(rowNum, 4, "<i>"+
				AppConstants.ANAGRAFICA_SAP_DESC.get(rowObj.getIdTipoAnagraficaSap())+"</i>");
	}
	
	@Override
	protected void addHeader() {
		// Set the data in the current row
		getInnerTable().setHTML(0, 0, "CM");
		getInnerTable().setHTML(0, 1, "Descrizione");
		getInnerTable().setHTML(0, 2, "Disponibilit&agrave;");
		getInnerTable().setHTML(0, 3, "In&nbsp;attesa");
		getInnerTable().setHTML(0, 4, "Anagrafica SAP");
	}
	
	@Override
	protected void onEmptyResult() {}
	
	
	
	//Inner classes
	
	
	
	public static class ArticoliModel implements DataModel<Articoli> {
		private ArticoliServiceAsync articoliService = GWT.create(ArticoliService.class);
		private Date validDt = null;
		
		public ArticoliModel(Date validDt) {
			this.validDt=validDt;
		}

		@Override
		public void find(int offset, int pageSize, AsyncCallback<List<Articoli>> callback) {
			//WaitSingleton.get().start();
			articoliService.findArticoliByDate(validDt, offset, pageSize, callback);
		}
	}
	
}
