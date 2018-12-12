package it.giunti.apg.client.widgets.tables;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.frames.PagamentoPopUp;
import it.giunti.apg.client.services.PagamentiService;
import it.giunti.apg.client.services.PagamentiServiceAsync;
import it.giunti.apg.client.widgets.FatturaStampaLink;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Pagamenti;
import it.giunti.apg.shared.model.Ruoli;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.InlineHTML;

public class PagamentiTable extends PagingTable<Pagamenti> implements IRefreshable {
	
	private static final int TABLE_ROWS = AppConstants.TABLE_ROWS_DEFAULT;
	private static final int NOTE_LENGTH = 25;
	private IRefreshable parent = null;
	private boolean isSuper = false;
	
	private static final PagamentiServiceAsync pagamentiService = GWT.create(PagamentiService.class);
	
	private AsyncCallback<List<Pagamenti>> callback = new AsyncCallback<List<Pagamenti>>() {
		@Override
		public void onFailure(Throwable caught) {
			setTableRows(new ArrayList<Pagamenti>());
		}
		@Override
		public void onSuccess(List<Pagamenti> result) {
			setTableRows(result);
		}
	};
	
	public PagamentiTable(DataModel<Pagamenti> model, Ruoli userRole, IRefreshable parent) {
		super(model, TABLE_ROWS);
		this.parent=parent;
		isSuper = (userRole.getId().intValue() >= AppConstants.RUOLO_SUPER);
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
	protected void addTableRow(int rowNum, Pagamenti rowObj) {
		final Pagamenti fRowObj = rowObj;
		final PagamentiTable fTable = this;
		//Importo
		String linkText = "<b>&euro;"+ClientConstants.FORMAT_CURRENCY.format(rowObj.getImporto())+"</b>";
		Anchor rowLink = new Anchor(linkText, true);
		rowLink.addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				new PagamentoPopUp(fTable, fRowObj.getId(), null);
			}
		});
		getInnerTable().setWidget(rowNum, 0, rowLink);
		//Societa
		getInnerTable().setHTML(rowNum, 1, "<b>"+rowObj.getIdSocieta()+"</b>");
		//Fattura
		if (rowObj.getIdFattura() != null) {
			FatturaStampaLink link = new FatturaStampaLink(rowObj.getIdFattura());
			getInnerTable().setWidget(rowNum, 2, link);
		} else {
			getInnerTable().setHTML(rowNum, 2, "--");
		}
		//Attribuzione
		//String codiceAbbonamento = "Anticipo";
		//if (rowObj.getIstanzaAbbonamento() != null) {
		//	codiceAbbonamento = rowObj.getIstanzaAbbonamento().getAbbonamento().getCodiceAbbonamento();
		//} else {
		//	if (rowObj.getCodiceAbbonamentoBollettino() != null) {
		//		if (rowObj.getCodiceAbbonamentoBollettino().length() > 0) {
		//			codiceAbbonamento = " (per "+rowObj.getCodiceAbbonamentoBollettino()+")";
		//		}
		//	}
		//}
		//getInnerTable().setHTML(rowNum, 3, codiceAbbonamento);
		//Data pagamento
		getInnerTable().setHTML(rowNum, 4, ClientConstants.FORMAT_DAY.format(rowObj.getDataPagamento()));
		//Data registrazione
		getInnerTable().setHTML(rowNum, 5, ClientConstants.FORMAT_DAY.format(rowObj.getDataAccredito()));
		//Tipo
		getInnerTable().setHTML(rowNum, 6, AppConstants.PAGAMENTO_DESC.get(rowObj.getIdTipoPagamento()));
		//TRN
		getInnerTable().setHTML(rowNum, 7, rowObj.getTrn());
		//Note
		String note = rowObj.getNote();
		if (note != null) {
			if (note.length()>NOTE_LENGTH) {
				note = note.substring(0, NOTE_LENGTH)+"...";
			}
		} else {
			note = "";
		}
		getInnerTable().setHTML(rowNum, 8, note+" <i>("+rowObj.getIdUtente()+")</i>");
		//delete
		if (isSuper && (rowObj.getIdFattura() == null)) {
			InlineHTML trashImg = new InlineHTML(ClientConstants.ICON_DELETE);
			trashImg.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent arg0) {
					confirmAndDelete(fRowObj.getId());
				}
			});
			getInnerTable().setWidget(rowNum, 9, trashImg);
		}
	}
	
	@Override
	protected void addHeader() {
		// Set the data in the current row
		getInnerTable().setHTML(0, 0, "Importo");
		getInnerTable().setHTML(0, 1, "Societa");
		getInnerTable().setHTML(0, 2, "Fattura");
		//getInnerTable().setHTML(0, 3, "Attribuzione");
		getInnerTable().setHTML(0, 4, "Pagamento");
		getInnerTable().setHTML(0, 5, "Registrazione");
		getInnerTable().setHTML(0, 6, "Tipo");
		getInnerTable().setHTML(0, 7, "TRN");
		getInnerTable().setHTML(0, 8, "Note");
	}
	
	@Override
	protected void onEmptyResult() {}
	
	private void confirmAndDelete(Integer idPag) {
		boolean confirm = Window.confirm("Vuoi veramente eliminare il pagamento dall'elenco?");
		if (confirm) {
			delete(idPag);
		}
	}
	
	public void delete(Integer idPag) {
		AsyncCallback<Boolean> delCallback = new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
			}

			@Override
			public void onSuccess(Boolean result) {
				refresh();
			}
		};
		pagamentiService.deletePagamento(idPag, delCallback);
	}
	
	
	
	//Inner classes
	
	
	
	public static class PagamentiIstanzaModel implements DataModel<Pagamenti> {
		private IstanzeAbbonamenti istanza = null;
		
		public PagamentiIstanzaModel(IstanzeAbbonamenti istanza) {
			this.istanza=istanza;
		}

		@Override
		public void find(int offset, int pageSize, AsyncCallback<List<Pagamenti>> callback) {
			pagamentiService.findPagamentiByIstanzaAbbonamento(istanza, callback);
		}
	}

	public static class PagamentiAnagraficaModel implements DataModel<Pagamenti> {
		private Integer idAnagrafica = null;
		
		public PagamentiAnagraficaModel(Integer idAnagrafica) {
			this.idAnagrafica=idAnagrafica;
		}

		@Override
		public void find(int offset, int pageSize, AsyncCallback<List<Pagamenti>> callback) {
			pagamentiService.findPagamentiByAnagrafica(idAnagrafica, callback);
		}
	}
}
