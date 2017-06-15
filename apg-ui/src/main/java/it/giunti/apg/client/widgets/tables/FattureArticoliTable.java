package it.giunti.apg.client.widgets.tables;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.services.PagamentiService;
import it.giunti.apg.client.services.PagamentiServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.FattureArticoli;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FattureArticoliTable extends PagingTable<FattureArticoli> implements IRefreshable {
	private static final PagamentiServiceAsync paymentService = GWT.create(PagamentiService.class);

	private static final int TABLE_ROWS = 200;
	private IRefreshable parent = null;
	
	private AsyncCallback<List<FattureArticoli>> callback = new AsyncCallback<List<FattureArticoli>>() {
		@Override
		public void onFailure(Throwable caught) {
			setTableRows(new ArrayList<FattureArticoli>());
			//WaitSingleton.get().stop();
		}
		@Override
		public void onSuccess(List<FattureArticoli> result) {
			setTableRows(result);
			//WaitSingleton.get().stop();
		}
	};
	
	public FattureArticoliTable(DataModel<FattureArticoli> model, IRefreshable parent) {
		super(model, TABLE_ROWS);
		this.parent = parent;
		drawPage(0);
	}

	@Override
	public void refresh() {
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
	protected void addTableRow(int rowNum, FattureArticoli rowObj) {
		//Descrizione
		String descr = ValueUtil.newLineToBr(rowObj.getDescrizione());
		getInnerTable().setHTML(rowNum, 0, "<b>"+descr+"</b>&nbsp;");
		//Importo IVA esclusa
		getInnerTable().setHTML(rowNum, 1, "&euro;"+
				ClientConstants.FORMAT_CURRENCY.format(rowObj.getImportoImpUnit())+"&nbsp;");
		//Quantita
		getInnerTable().setHTML(rowNum, 2, rowObj.getQuantita()+"&nbsp;");
		//IVA
		if (!rowObj.getResto()) {
			if (rowObj.getIvaScorporata()) {
				getInnerTable().setHTML(rowNum, 3, AppConstants.DEFAULT_IVA_SCORPORATA_DESCR+"&nbsp;");
			} else {
				getInnerTable().setHTML(rowNum, 3, rowObj.getAliquotaIva().getDescr()+"&nbsp;");
			}
		}
		//Totale
		Double totale = rowObj.getImportoTotUnit()*rowObj.getQuantita();
		getInnerTable().setHTML(rowNum, 4, "&euro;"+
				ClientConstants.FORMAT_CURRENCY.format(totale));
		parent.refresh();
	}
	
	@Override
	protected void addHeader() {
		// Set the data in the current row
		getInnerTable().setHTML(0, 0, "Descrizione");
		getInnerTable().setHTML(0, 1, "Imponibile unit.");
		getInnerTable().setHTML(0, 2, "Quantità");
		getInnerTable().setHTML(0, 3, "IVA");
		getInnerTable().setHTML(0, 4, "Totale");
	}
	
	@Override
	protected void onEmptyResult() {}

	
	
	//Inner classes
	
	
	
	public static class FattureArticoliByFatturaModel implements DataModel<FattureArticoli> {
		private Integer idFattura = null;
		
		public FattureArticoliByFatturaModel(Integer idFattura) {
			this.idFattura=idFattura;
		}
		
		@Override
		public void find(int offset, int pageSize,
				AsyncCallback<List<FattureArticoli>> callback) {
			paymentService.findFattureArticoliByIdFattura(idFattura, callback);
		}
	}
	
}
