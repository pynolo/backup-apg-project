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
import it.giunti.apg.client.frames.MaterialeOpzionePopUp;
import it.giunti.apg.client.services.MaterialiService;
import it.giunti.apg.client.services.MaterialiServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.MaterialiOpzioni;
import it.giunti.apg.shared.model.Materiali;
import it.giunti.apg.shared.model.Ruoli;

public class MaterialiOpzioniTable extends PagingTable<MaterialiOpzioni> implements IRefreshable {
	private static final MaterialiServiceAsync matService = GWT.create(MaterialiService.class);
	
	private static final int TABLE_ROWS = AppConstants.TABLE_ROWS_DEFAULT;
	
	private boolean isAdmin = false;
	//private boolean isSuper = false;
	
	private AsyncCallback<List<MaterialiOpzioni>> callback = new AsyncCallback<List<MaterialiOpzioni>>() {
		@Override
		public void onFailure(Throwable caught) {
			setTableRows(new ArrayList<MaterialiOpzioni>());
			//WaitSingleton.get().stop();
		}
		@Override
		public void onSuccess(List<MaterialiOpzioni> result) {
			setTableRows(result);
			//WaitSingleton.get().stop();
		}
	};
	
	public MaterialiOpzioniTable(DataModel<MaterialiOpzioni> model, Ruoli userRole) {
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
	protected void addTableRow(int rowNum, MaterialiOpzioni rowObj) {
		final MaterialiOpzioni rowFinal = rowObj;
		Materiali mat = rowObj.getMateriale();
		final MaterialiOpzioniTable articoliTable = this;
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
				new MaterialeOpzionePopUp(rowFinal.getId(), rowFinal.getOpzione().getId(), articoliTable);
			}
		});
		getInnerTable().setWidget(rowNum, 0, rowLink);
		//Disponibilità articolo
//		String disp = "";
//		if (art.getDataInizio() != null) disp += " <i>dal "+ClientConstants.FORMAT_DAY.format(art.getDataInizio())+"</i> ";
//		if (art.getDataFine() != null) disp += " <i>fino al "+ClientConstants.FORMAT_DAY.format(art.getDataFine())+"</i> ";
//		getInnerTable().setHTML(rowNum, 1, disp);
		//Codice meccanografico
		String codice = "";
		if (mat.getCodiceMeccanografico() != null) codice = "<b>"+mat.getCodiceMeccanografico()+"</b>";
		getInnerTable().setHTML(rowNum, 2, codice);
		//Tipo
		getInnerTable().setHTML(rowNum, 3, "<i>"+
				AppConstants.ANAGRAFICA_SAP_DESC.get(mat.getIdTipoAnagraficaSap())+"</i>");
		//Ultima estrazione
		String ultimaEstr = "--";
		if (rowObj.getDataEstrazione() != null) ultimaEstr = ClientConstants.FORMAT_DAY.format(rowObj.getDataEstrazione());
		getInnerTable().setHTML(rowNum, 4, ultimaEstr);
		//delete
		if (isAdmin) {
			InlineHTML trashImg = new InlineHTML(ClientConstants.ICON_DELETE);
			trashImg.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent arg0) {
					confirmAndDelete(rowFinal.getId());
				}
			});
			getInnerTable().setWidget(rowNum, 5, trashImg);
		}
	}
	
	@Override
	protected void addHeader() {
		// Set the data in the current row
		getInnerTable().setHTML(0, 0, "Titolo");
		//getInnerTable().setHTML(0, 1, "Disponibilit&agrave;");
		getInnerTable().setHTML(0, 2, "CM");
		getInnerTable().setHTML(0, 3, "Anagrafica SAP");
		getInnerTable().setHTML(0, 4, "Estrazione");
	}
	
	@Override
	protected void onEmptyResult() {}
	
	private void confirmAndDelete(Integer idMaterialeListino) {
		boolean confirm = Window.confirm("Vuoi veramente eliminare il articolo dall'elenco?");
		if (confirm) {
			delete(idMaterialeListino);
		}
	}
	
	public void delete(Integer idMaterialeListino) {
		matService.deleteMaterialeOpzione(idMaterialeListino, callback);
	}
	
	
	//Inner classes
	
	
	
	public static class MaterialiOpzioniModel implements DataModel<MaterialiOpzioni> {
		private MaterialiServiceAsync articoliService = GWT.create(MaterialiService.class);
		private Integer idOpzione = null;
		
		public MaterialiOpzioniModel(Integer idOpzione) {
			this.idOpzione=idOpzione;
		}

		@Override
		public void find(int offset, int pageSize, AsyncCallback<List<MaterialiOpzioni>> callback) {
			//WaitSingleton.get().start();
			articoliService.findMaterialiOpzioni(idOpzione, callback);
		}
	}
}
