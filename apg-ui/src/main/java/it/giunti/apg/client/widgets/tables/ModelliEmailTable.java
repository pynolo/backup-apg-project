package it.giunti.apg.client.widgets.tables;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.UriManager;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.services.ComunicazioniService;
import it.giunti.apg.client.services.ComunicazioniServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.ModelliEmail;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Hyperlink;

public class ModelliEmailTable extends PagingTable<ModelliEmail> {

	private static final ComunicazioniServiceAsync comService = GWT.create(ComunicazioniService.class);
	
	private static final int TABLE_ROWS = AppConstants.TABLE_ROWS_DEFAULT;
	
	private AsyncCallback<List<ModelliEmail>> callback = new AsyncCallback<List<ModelliEmail>>() {
		@Override
		public void onFailure(Throwable caught) {
			setTableRows(new ArrayList<ModelliEmail>());
			//WaitSingleton.get().stop();
		}
		@Override
		public void onSuccess(List<ModelliEmail> result) {
			setTableRows(result);
			//WaitSingleton.get().stop();
		}
	};
	
	public ModelliEmailTable(DataModel<ModelliEmail> model) {
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
	protected void addTableRow(int rowNum, ModelliEmail rowObj) {
		// Set the data in the current row
		String linkText = "<b>"+rowObj.getDescr()+"</b>";
		UriParameters params = new UriParameters();
		params.add(AppConstants.PARAM_ID, rowObj.getId());
		Hyperlink rowLink = params.getHyperlink(linkText, UriManager.MODELLI_EMAIL);
		getInnerTable().setWidget(rowNum, 0, rowLink);
		//Oggetto
		if (rowObj.getOggetto() != null) {
			String oggetto = rowObj.getOggetto();
			getInnerTable().setHTML(rowNum, 1, oggetto);
		}
		//NomeMittente
		if (rowObj.getNomeMittente() != null) {
			String nome = rowObj.getNomeMittente();
			getInnerTable().setHTML(rowNum, 2, nome);
		}
	}
	
	@Override
	protected void addHeader() {
		// Set the data in the current row
		getInnerTable().setHTML(0, 0, "Descrizione");
		getInnerTable().setHTML(0, 1, "Oggetto");
		getInnerTable().setHTML(0, 2, "Nome mittente");
	}
	
	@Override
	protected void onEmptyResult() {}
	
	
	
	
	//Inner classes
	
	
	
	public static class ModelliEmailModel implements DataModel<ModelliEmail> {
		
		public ModelliEmailModel() {
		}
		
		@Override
		public void find(int offset, int pageSize,
				AsyncCallback<List<ModelliEmail>> callback) {
			//WaitSingleton.get().start();
			comService.findModelliEmail(offset, pageSize, callback);
		}
	}

	
}
