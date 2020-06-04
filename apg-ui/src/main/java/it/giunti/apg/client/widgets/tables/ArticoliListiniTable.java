package it.giunti.apg.client.widgets.tables;

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

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.frames.ArticoloListinoPopUp;
import it.giunti.apg.client.services.MaterialiService;
import it.giunti.apg.client.services.MaterialiServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.ArticoliListini;
import it.giunti.apg.shared.model.Materiali;
import it.giunti.apg.shared.model.Ruoli;

public class ArticoliListiniTable extends PagingTable<ArticoliListini> implements IRefreshable {
	private static final MaterialiServiceAsync matService = GWT.create(MaterialiService.class);
	
	private static final int TABLE_ROWS = AppConstants.TABLE_ROWS_DEFAULT;
	
	private boolean isAdmin = false;
	//private boolean isSuper = false;
	
	private AsyncCallback<List<ArticoliListini>> callback = new AsyncCallback<List<ArticoliListini>>() {
		@Override
		public void onFailure(Throwable caught) {
			setTableRows(new ArrayList<ArticoliListini>());
			//WaitSingleton.get().stop();
		}
		@Override
		public void onSuccess(List<ArticoliListini> result) {
			setTableRows(result);
			//WaitSingleton.get().stop();
		}
	};
	
	public ArticoliListiniTable(DataModel<ArticoliListini> model, Ruoli userRole) {
		super(model, TABLE_ROWS);
		isAdmin = (userRole.getId().intValue() >= AppConstants.RUOLO_OPERATOR);
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
		drawPage(0);
	}
	
	@Override
	protected void addTableRow(int rowNum, ArticoliListini rowObj) {
		final ArticoliListini rowFinal = rowObj;
		Materiali mat = rowObj.getMateriale();
		final ArticoliListiniTable articoliTable = this;
		// Set the data in the current row
		String sigla = "<b>"+mat.getCodiceMeccanografico()+"</b> ";
		String titolo = "";
		if (mat.getTitolo() != null) titolo = mat.getTitolo();
		if (mat.getSottotitolo() != null) {
			if (!mat.getSottotitolo().equals("")) {
				if (titolo.length() > 0) titolo += " - ";
				titolo += mat.getSottotitolo();
			}
		}
		Anchor rowLink = new Anchor(sigla+titolo, true);
		rowLink.addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				new ArticoloListinoPopUp(rowFinal.getId(), rowFinal.getListino().getId(), articoliTable);
			}
		});
		getInnerTable().setWidget(rowNum, 0, rowLink);
//		//Disponibilità articolo
//		String disp = "";
//		if (art.getDataInizio() != null) disp += " <i>dal "+ClientConstants.FORMAT_DAY.format(art.getDataInizio())+"</i> ";
//		if (art.getDataFine() != null) disp += " <i>fino al "+ClientConstants.FORMAT_DAY.format(art.getDataFine())+"</i> ";
//		getInnerTable().setHTML(rowNum, 1, disp);
		//Codice meccanografico
		String codice = "";
		if (mat.getCodiceMeccanografico() != null) codice = "<b>["+mat.getCodiceMeccanografico()+"]</b>";
		getInnerTable().setHTML(rowNum, 2, codice);
		//Limite
		String limite = "";
		if ((rowObj.getGiornoLimitePagamento() != null) && (rowObj.getMeseLimitePagamento() != null)) {
			limite += "<b>"+rowObj.getGiornoLimitePagamento()+" ";
			limite += ClientConstants.MESI[rowObj.getMeseLimitePagamento()]+"</b>";
		}
		getInnerTable().setHTML(rowNum, 3, limite);
		//Tipo
		getInnerTable().setHTML(rowNum, 4, "<i>"+
				AppConstants.ANAGRAFICA_SAP_DESC.get(mat.getIdTipoAnagraficaSap())+"</i>");
		//Ultima estrazione
		String ultimaEstr = "--";
		if (rowObj.getDataEstrazione() != null) ultimaEstr = 
				"<b>"+ClientConstants.FORMAT_DAY.format(rowObj.getDataEstrazione())+"</b>";
		getInnerTable().setHTML(rowNum, 5, ultimaEstr);
		//delete
		if (isAdmin) {
			InlineHTML trashImg = new InlineHTML(ClientConstants.ICON_DELETE);
			trashImg.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent arg0) {
					confirmAndDelete(rowFinal.getId());
				}
			});
			getInnerTable().setWidget(rowNum, 6, trashImg);
		}
	}
	
	@Override
	protected void addHeader() {
		// Set the data in the current row
		getInnerTable().setHTML(0, 0, "Titolo");
		//getInnerTable().setHTML(0, 1, "Disponibilit&agrave;");
		getInnerTable().setHTML(0, 2, "CM");
		getInnerTable().setHTML(0, 3, "Limite pag.");
		getInnerTable().setHTML(0, 4, "Anagrafica SAP");
		getInnerTable().setHTML(0, 5, "Estrazione");
	}
	
	@Override
	protected void onEmptyResult() {}
	
	private void confirmAndDelete(Integer idArticoloListino) {
		boolean confirm = Window.confirm("Vuoi veramente eliminare il articolo dall'elenco?");
		if (confirm) {
			delete(idArticoloListino);
		}
	}
	
	public void delete(Integer idArticoloListino) {
		matService.deleteArticoloListino(idArticoloListino, callback);
	}
	
	
	//Inner classes
	
	
	
	public static class ArticoliListiniModel implements DataModel<ArticoliListini> {
		private MaterialiServiceAsync matService = GWT.create(MaterialiService.class);
		private Integer idListino = null;
		
		public ArticoliListiniModel(Integer idListino) {
			this.idListino=idListino;
		}

		@Override
		public void find(int offset, int pageSize, AsyncCallback<List<ArticoliListini>> callback) {
			//WaitSingleton.get().start();
			matService.findArticoliListini(idListino, callback);
		}
	}
}
