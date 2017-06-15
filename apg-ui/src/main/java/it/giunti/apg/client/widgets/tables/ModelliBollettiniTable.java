package it.giunti.apg.client.widgets.tables;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.UriManager;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.services.ComunicazioniService;
import it.giunti.apg.client.services.ComunicazioniServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.ModelliBollettini;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Hyperlink;

public class ModelliBollettiniTable extends PagingTable<ModelliBollettini> {

	private static final ComunicazioniServiceAsync comService = GWT.create(ComunicazioniService.class);
	
	private static final int TABLE_ROWS = AppConstants.TABLE_ROWS_DEFAULT;
	private static final int TESTO_BANDELLA_PREVIEW = 45;
	
	private AsyncCallback<List<ModelliBollettini>> callback = new AsyncCallback<List<ModelliBollettini>>() {
		@Override
		public void onFailure(Throwable caught) {
			setTableRows(new ArrayList<ModelliBollettini>());
			//WaitSingleton.get().stop();
		}
		@Override
		public void onSuccess(List<ModelliBollettini> result) {
			setTableRows(result);
			//WaitSingleton.get().stop();
		}
	};
	
	public ModelliBollettiniTable(DataModel<ModelliBollettini> model) {
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
	protected void addTableRow(int rowNum, ModelliBollettini rowObj) {
		// Set the data in the current row
		String linkText = "<b>"+rowObj.getDescr()+"</b>";
		UriParameters params = new UriParameters();
		params.add(AppConstants.PARAM_ID, rowObj.getId());
		Hyperlink rowLink = params.getHyperlink(linkText, UriManager.MODELLI_BOLLETTINI);
		getInnerTable().setWidget(rowNum, 0, rowLink);
		//Periodico
		if (rowObj.getPeriodico() != null) {
			String lettera = rowObj.getPeriodico().getUid();
			if (rowObj.getPredefinitoPeriodico()) {
				getInnerTable().setHTML(rowNum, 1, "<b>"+lettera+"</b> (default)");
			} else {
				getInnerTable().setHTML(rowNum, 1, lettera);
			}
		}
		//Modello
		getInnerTable().setHTML(rowNum, 2, rowObj.getCodiceModello());
		//Testo
		String testoBandella = "";
		if (rowObj.getTestoBandella() != null) {
			if (rowObj.getTestoBandella().length() <= TESTO_BANDELLA_PREVIEW) {
				testoBandella = rowObj.getTestoBandella();
			} else {
				testoBandella = rowObj.getTestoBandella().substring(0,TESTO_BANDELLA_PREVIEW-2)+"...";
			}
		}
		getInnerTable().setHTML(rowNum, 3, testoBandella);
	}
	
	@Override
	protected void addHeader() {
		// Set the data in the current row
		getInnerTable().setHTML(0, 0, "Descrizione");
		getInnerTable().setHTML(0, 1, "Periodico");
		getInnerTable().setHTML(0, 2, "Modello");
		getInnerTable().setHTML(0, 3, "Testo");
	}
	
	@Override
	protected void onEmptyResult() {}
	
	
	
	
	//Inner classes
	
	
	
	public static class ModelliBollettiniByPeriodicoModel implements DataModel<ModelliBollettini> {
		private Integer idPeriodico = null;
		
		public ModelliBollettiniByPeriodicoModel(Integer idPeriodico) {
			this.idPeriodico = idPeriodico;
		}
		
		@Override
		public void find(int offset, int pageSize,
				AsyncCallback<List<ModelliBollettini>> callback) {
			//WaitSingleton.get().start();
			comService.findModelliBollettiniByPeriodico(idPeriodico, offset, pageSize, callback);
		}
	}

	
}
