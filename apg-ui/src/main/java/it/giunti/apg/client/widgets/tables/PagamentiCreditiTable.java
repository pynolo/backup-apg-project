package it.giunti.apg.client.widgets.tables;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.InlineHTML;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.services.PagamentiService;
import it.giunti.apg.client.services.PagamentiServiceAsync;
import it.giunti.apg.client.widgets.AnagraficaLink;
import it.giunti.apg.client.widgets.FatturaStampaLink;
import it.giunti.apg.client.widgets.MiniInstancePanel;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.PagamentiCrediti;

public class PagamentiCreditiTable extends PagingTable<PagamentiCrediti> implements IRefreshable {
	
	private static final int TABLE_ROWS = AppConstants.TABLE_ROWS_DEFAULT;
	
	private static final PagamentiServiceAsync pagamentiService = GWT.create(PagamentiService.class);
	
	private boolean soloConIstanzeDaPagare = false;
	private boolean soloConIstanzeScadute = false;
	 
	private AsyncCallback<List<PagamentiCrediti>> callback = new AsyncCallback<List<PagamentiCrediti>>() {
		@Override
		public void onFailure(Throwable caught) {
			setTableRows(new ArrayList<PagamentiCrediti>());
		}
		@Override
		public void onSuccess(List<PagamentiCrediti> result) {
			setTableRows(result);
		}
	};
	
	public PagamentiCreditiTable(DataModel<PagamentiCrediti> model, boolean soloConIstanzeDaPagare, boolean soloConIstanzeScadute) {
		super(model, TABLE_ROWS);
		this.soloConIstanzeDaPagare = soloConIstanzeDaPagare;
		this.soloConIstanzeScadute = soloConIstanzeScadute;
		//isSuper = (userRole.getId().intValue() >= AppConstants.RUOLO_SUPER);
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
		//if (parent != null) {
		//	parent.refresh();
		//}
		drawPage(0);
	}
	
	@Override
	protected void addTableRow(int rowNum, PagamentiCrediti rowObj) {
		//Fattura
		if (rowObj.getFatturaOrigine() != null) {
			FatturaStampaLink link = new FatturaStampaLink(rowObj.getFatturaOrigine().getId(), true);
			getInnerTable().setWidget(rowNum, 0, link);
		} else {
			getInnerTable().setHTML(rowNum, 0, "--");
		}
		//Importo
		InlineHTML importoLabel = new InlineHTML(
				"<b>&euro;"+ClientConstants.FORMAT_CURRENCY.format(rowObj.getImporto())+"</b>");
		getInnerTable().setWidget(rowNum, 1, importoLabel);
		//Anagrafica
		AnagraficaLink anaLink = new AnagraficaLink(rowObj.getIdAnagrafica(), false);
		getInnerTable().setWidget(rowNum, 2, anaLink);
		//Data creazione credito
		String creazione = ClientConstants.FORMAT_DAY.format(rowObj.getDataCreazione());
		if (rowObj.getIdUtente() != null) {
			if (rowObj.getIdUtente().length() > 0) creazione += " <i>("+rowObj.getIdUtente()+")</i>";
		}
		getInnerTable().setHTML(rowNum, 3, creazione);
		//Stato abb
		MiniInstancePanel mip = new MiniInstancePanel(rowObj.getIdAnagrafica(), rowObj.getIdSocieta(),
				soloConIstanzeDaPagare, soloConIstanzeScadute, true, false);
		getInnerTable().setWidget(rowNum, 4, mip);
	}
	
	@Override
	protected void addHeader() {
		// Set the data in the current row
		getInnerTable().setHTML(0, 0, "Origine");
		getInnerTable().setHTML(0, 1, "Importo");
		getInnerTable().setHTML(0, 2, "Anagrafica");
		getInnerTable().setHTML(0, 3, "Creazione");
		getInnerTable().setHTML(0, 4, "Istanze");
	}
	
	@Override
	protected void onEmptyResult() {}
	
	
	
	//Inner classes

	
	
	public static class CreditiSocietaModel implements DataModel<PagamentiCrediti> {
		private String idSocieta = null;
		private boolean soloConIstanzeDaPagare = false;
		private boolean soloConIstanzeScadute = false;
		
		public CreditiSocietaModel(String idSocieta, boolean soloConIstanzeDaPagare, boolean soloConIstanzeScadute) {
			this.idSocieta=idSocieta;
			this.soloConIstanzeDaPagare = soloConIstanzeDaPagare;
			this.soloConIstanzeScadute = soloConIstanzeScadute;
		}

		@Override
		public void find(int offset, int pageSize, AsyncCallback<List<PagamentiCrediti>> callback) {
			pagamentiService.findCreditiBySocieta(idSocieta, soloConIstanzeDaPagare, soloConIstanzeScadute, offset, pageSize, callback);
		}
	}
	
}
