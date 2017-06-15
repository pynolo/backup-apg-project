package it.giunti.apg.client.widgets.tables;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.services.LookupService;
import it.giunti.apg.client.services.LookupServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Periodici;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class PeriodiciTable extends PagingTable<Periodici> {
	
	private static final int TABLE_ROWS = AppConstants.TABLE_ROWS_DEFAULT;
	
	private AsyncCallback<List<Periodici>> callback = new AsyncCallback<List<Periodici>>() {
		@Override
		public void onFailure(Throwable caught) {
			setTableRows(new ArrayList<Periodici>());
			//WaitSingleton.get().stop();
		}
		@Override
		public void onSuccess(List<Periodici> result) {
			setTableRows(result);
			//WaitSingleton.get().stop();
		}
	};
	
	public PeriodiciTable(DataModel<Periodici> model) {
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
	protected void addTableRow(int rowNum, Periodici rowObj) {
		
		// Nome
		getInnerTable().setHTML(rowNum, 0, rowObj.getNome());
		// Fascicoli/anno
		String numeri = "--";
		if (rowObj.getNumeriAnnuali() != null) numeri =rowObj.getNumeriAnnuali().toString();
		getInnerTable().setHTML(rowNum, 1, numeri);
		// CC
		getInnerTable().setHTML(rowNum, 2, rowObj.getNumeroCc());
		// IBAN
		if (rowObj.getIban() != null)
			getInnerTable().setHTML(rowNum, 3, rowObj.getIban()+"&nbsp;");
		//Disponibilità opzione
		String disp = "";
		if (rowObj.getDataInizio() != null) disp += " <i>dal "+ClientConstants.FORMAT_DAY.format(rowObj.getDataInizio())+"</i> ";
		if (rowObj.getDataFine() != null) disp += " <i>fino al "+ClientConstants.FORMAT_DAY.format(rowObj.getDataFine())+"</i> ";
		getInnerTable().setHTML(rowNum, 4, disp);
		// Società
		getInnerTable().setHTML(rowNum, 5, "<b>"+rowObj.getIdSocieta()+"</b>");
		// Tipo periodico
		getInnerTable().setHTML(rowNum, 6,
				AppConstants.PERIODICO_DESC.get(rowObj.getIdTipoPeriodico()));
		// Proprietà
		String caratteristiche = "";
		if (rowObj.getTag() != null) {
			if (rowObj.getTag().length() > 0) {
				if (caratteristiche.length() > 0) caratteristiche += ", ";
				caratteristiche += "tag: <b>"+rowObj.getTag()+"</b>";
			}
		}
		getInnerTable().setHTML(rowNum, 7, caratteristiche);
		// UID
		getInnerTable().setHTML(rowNum, 8, "<b>["+rowObj.getUid()+"]</b>&nbsp;");
	}
	
	@Override
	protected void addHeader() {
		// Set the data in the current row
		getInnerTable().setHTML(0, 0, "Periodico");
		getInnerTable().setHTML(0, 1, "Annualità");
		getInnerTable().setHTML(0, 2, "CC");
		getInnerTable().setHTML(0, 3, "IBAN");
		getInnerTable().setHTML(0, 4, "Disponibilit&agrave;");
		getInnerTable().setHTML(0, 5, "Societ&agrave;");
		getInnerTable().setHTML(0, 6, "Tipo invio");
		getInnerTable().setHTML(0, 7, "Propriet&agrave;");
		getInnerTable().setHTML(0, 8, "UID");
	}
	
	@Override
	protected void onEmptyResult() {}
	
	
	
	//Inner classes
	
	
	
	public static class PeriodiciModel implements DataModel<Periodici> {
		private final LookupServiceAsync lookupService = GWT.create(LookupService.class);
		private Date extractionDt = null;
		
		public PeriodiciModel(Date extractionDt) {
			this.extractionDt=extractionDt;
		}
		
		@Override
		public void find(int offset, int pageSize,
				AsyncCallback<List<Periodici>> callback) {
			//WaitSingleton.get().start();
			lookupService.findPeriodici(extractionDt, callback);
		}
	}
	
}
