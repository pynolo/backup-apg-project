package it.giunti.apg.client.widgets.tables;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.UriManager;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.services.OpzioniService;
import it.giunti.apg.client.services.OpzioniServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Opzioni;
import it.giunti.apg.shared.model.Utenti;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Hyperlink;

public class OpzioniTable extends PagingTable<Opzioni> implements IRefreshable {
	
	private static final int TABLE_ROWS = AppConstants.TABLE_ROWS_DEFAULT;
	
	private Utenti utente = null;
	private boolean isEditor = false;
	
	private AsyncCallback<List<Opzioni>> callback = new AsyncCallback<List<Opzioni>>() {
		@Override
		public void onFailure(Throwable caught) {
			setTableRows(new ArrayList<Opzioni>());
			//WaitSingleton.get().stop();
		}
		@Override
		public void onSuccess(List<Opzioni> result) {
			setTableRows(result);
			//WaitSingleton.get().stop();
		}
	};
	
	public OpzioniTable(DataModel<Opzioni> model, Utenti utente) {
		super(model, TABLE_ROWS);
		this.utente = utente;
		isEditor = (this.utente.getRuolo().getId() >= AppConstants.RUOLO_EDITOR);
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
	public void refresh() {
		drawPage(0);
	}
	
	@Override
	protected void addTableRow(int rowNum, Opzioni rowObj) {
		// Set the data in the current row
		String titolo = ClientConstants.ICON_OPZIONE+"&nbsp;"+
				"<b>"+rowObj.getNome()+"</b>";
		if (rowObj.getCartaceo()) titolo += " "+ClientConstants.ICON_CARTACEO;
		if (rowObj.getDigitale()) titolo += " "+ClientConstants.ICON_APP;
		if (isEditor) {
			UriParameters params1 = new UriParameters();
			params1.add(AppConstants.PARAM_ID, rowObj.getId());
			Hyperlink rowLink = params1.getHyperlink(titolo, UriManager.OPZIONE);
			getInnerTable().setWidget(rowNum, 0, rowLink);
		} else {
			getInnerTable().setHTML(rowNum, 0, "<b>"+titolo+"</b>");
		}
		//Periodico
		String nome = rowObj.getPeriodico().getNome();
		getInnerTable().setHTML(rowNum, 1, nome);
		//Prezzo
		String prezzo = "&euro;"+ClientConstants.FORMAT_CURRENCY.format(rowObj.getPrezzo());
		if (rowObj.getAliquotaIva().getValore() > 0) prezzo += " iva&nbsp;"+
				rowObj.getAliquotaIva().getDescr();
		getInnerTable().setHTML(rowNum, 2, prezzo);
		//Disponibilità opzione
		String disp = "";
		if (rowObj.getDataInizio() != null) disp += " <i>dal "+ClientConstants.FORMAT_DAY.format(rowObj.getDataInizio())+"</i> ";
		if (rowObj.getDataFine() != null) disp += " <i>fino al "+ClientConstants.FORMAT_DAY.format(rowObj.getDataFine())+"</i> ";
		getInnerTable().setHTML(rowNum, 3, disp);
		//UID
		String codice = rowObj.getUid();
		getInnerTable().setHTML(rowNum, 4, "<b>["+codice+"]</b>");
	}
	
	@Override
	protected void addHeader() {
		// Set the data in the current row
		getInnerTable().setHTML(0, 0, "Nome");
		getInnerTable().setHTML(0, 1, "Periodico");
		getInnerTable().setHTML(0, 2, "Prezzo");
		getInnerTable().setHTML(0, 3, "Disponibilit&agrave;");
		getInnerTable().setHTML(0, 4, "UID");
	}
	
	@Override
	protected void onEmptyResult() {}
	
	
	
	//Inner classes
	
	
	
	public static class OpzioniModel implements DataModel<Opzioni> {
		private final OpzioniServiceAsync opzioniService = GWT.create(OpzioniService.class);
		private Date extractionDt = null;
		
		public OpzioniModel(Date extractionDt) {
			this.extractionDt=extractionDt;
		}
		
		@Override
		public void find(int offset, int pageSize,
				AsyncCallback<List<Opzioni>> callback) {
			//WaitSingleton.get().start();
			opzioniService.findOpzioni(extractionDt, callback);
		}
	}

	
}
