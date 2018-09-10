package it.giunti.apg.client.widgets.tables;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.services.PagamentiService;
import it.giunti.apg.client.services.PagamentiServiceAsync;
import it.giunti.apg.client.widgets.FatturaStampaLink;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.PagamentiCrediti;
import it.giunti.apg.shared.model.Ruoli;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.InlineHTML;

public class CreditiTable extends PagingTable<PagamentiCrediti> implements IRefreshable {
	
	private static final int TABLE_ROWS = AppConstants.TABLE_ROWS_DEFAULT;
	private static final int NOTE_LENGTH = 25;
	private IRefreshable parent = null;
	//private boolean isSuper = false;
	
	private static final PagamentiServiceAsync pagamentiService = GWT.create(PagamentiService.class);
	
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
	
	public CreditiTable(DataModel<PagamentiCrediti> model, Ruoli userRole, IRefreshable parent) {
		super(model, TABLE_ROWS);
		this.parent=parent;
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
		if (parent != null) {
			parent.refresh();
		}
		drawPage(0);
	}
	
	@Override
	protected void addTableRow(int rowNum, PagamentiCrediti rowObj) {
		//Importo
		InlineHTML importoLabel = new InlineHTML(
				"<b>&euro;"+ClientConstants.FORMAT_CURRENCY.format(rowObj.getImporto())+"</b>");
		getInnerTable().setWidget(rowNum, 0, importoLabel);
		//Societa
		getInnerTable().setHTML(rowNum, 1, "<b>"+rowObj.getIdSocieta()+"</b>");
		//Fattura
		if (rowObj.getFatturaOrigine() != null) {
			FatturaStampaLink link = new FatturaStampaLink(rowObj.getFatturaOrigine().getId(), parent);
			getInnerTable().setWidget(rowNum, 2, link);
		} else {
			getInnerTable().setHTML(rowNum, 2, "--");
		}
		//Attribuzione
		if (rowObj.getFatturaImpiego() != null) {
			FatturaStampaLink link = new FatturaStampaLink(rowObj.getFatturaImpiego().getId(), parent);
			getInnerTable().setWidget(rowNum, 3, link);
		} else {
			if (rowObj.getStornatoDaOrigine()) {
				getInnerTable().setHTML(rowNum, 3, "Disponibile");
			} else {
				getInnerTable().setHTML(rowNum, 3, "<b>Da stornare</b>");
			}
		}
		//Data pagamento
		getInnerTable().setHTML(rowNum, 4, ClientConstants.FORMAT_DAY.format(rowObj.getDataCreazione()));
		//Note
		String note = rowObj.getNote();
		if (note != null) {
			if (note.length()>NOTE_LENGTH) {
				note = note.substring(0, NOTE_LENGTH)+"...";
			}
		} else {
			note = "";
		}
		getInnerTable().setHTML(rowNum, 5, note);
		////delete
		//if (isSuper) {
		//	InlineHTML trashImg = new InlineHTML(ClientConstants.ICON_DELETE);
		//	trashImg.addClickHandler(new ClickHandler() {
		//		@Override
		//		public void onClick(ClickEvent arg0) {
		//			confirmAndDelete(rowFinal.getId());
		//		}
		//	});
		//	getInnerTable().setWidget(rowNum, 6, trashImg);
		//}
	}
	
	@Override
	protected void addHeader() {
		// Set the data in the current row
		getInnerTable().setHTML(0, 0, "Importo");
		getInnerTable().setHTML(0, 1, "Societa");
		getInnerTable().setHTML(0, 2, "Origine");
		getInnerTable().setHTML(0, 3, "Attribuzione");
		getInnerTable().setHTML(0, 4, "Creazione");
		getInnerTable().setHTML(0, 5, "Note");
	}
	
	@Override
	protected void onEmptyResult() {}
	
	//private void confirmAndDelete(Integer idPag) {
	//	boolean confirm = Window.confirm("Vuoi veramente eliminare il pagamento dall'elenco?");
	//	if (confirm) {
	//		delete(idPag);
	//	}
	//}
	
	//public void delete(Integer idPag) {
	//	//WaitSingleton.get().start();
	//	pagamentiService.deletePagamento(idPag, callback);
	//}
	
	
	
	//Inner classes

	
	
	//public static class CreditiAnagraficaSocietaModel_ implements DataModel<PagamentiCrediti> {
	//	private Anagrafiche anagrafica = null;
	//	private String idSocieta = null;
	//	
	//	public CreditiAnagraficaSocietaModel_(Anagrafiche anagrafica, String idSocieta) {
	//		this.anagrafica=anagrafica;
	//		this.idSocieta=idSocieta;
	//	}
	//
	//	@Override
	//	public void find(int offset, int pageSize, AsyncCallback<List<PagamentiCrediti>> callback) {
	//		pagamentiService.findCreditiByAnagraficaSocieta(anagrafica.getId(), idSocieta, null, callback);
	//	}
	//}
	
	public static class CreditiAnagraficaModel implements DataModel<PagamentiCrediti> {
		private Anagrafiche anagrafica = null;
		
		public CreditiAnagraficaModel(Anagrafiche anagrafica) {
			this.anagrafica=anagrafica;
		}

		@Override
		public void find(int offset, int pageSize, AsyncCallback<List<PagamentiCrediti>> callback) {
			pagamentiService.findCreditiByAnagrafica(anagrafica.getId(), callback);
		}
	}
	
	public static class CreditiIstanzaModel implements DataModel<PagamentiCrediti> {
		private Integer idIa = null;
		
		public CreditiIstanzaModel(Integer idIstanzaAbbonamento) {
			this.idIa=idIstanzaAbbonamento;
		}

		@Override
		public void find(int offset, int pageSize, AsyncCallback<List<PagamentiCrediti>> callback) {
			pagamentiService.findCreditiByIstanza(idIa, callback);
		}
	}
	
}
