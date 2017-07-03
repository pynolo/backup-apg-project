package it.giunti.apg.client.widgets.tables;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.services.LoggingService;
import it.giunti.apg.client.services.LoggingServiceAsync;
import it.giunti.apg.shared.model.Avvisi;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineHTML;

public class AvvisiTable extends PagingTable<Avvisi> implements IRefreshable {
	
	private static final int TABLE_ROWS = 50;
	private static final DateTimeFormat DTF = DateTimeFormat.getFormat("dd/MM/yyyy HH:mm");
	private static final LoggingServiceAsync loggingService = GWT.create(LoggingService.class);
	
	private AsyncCallback<List<Avvisi>> callback = new AsyncCallback<List<Avvisi>>() {
		@Override
		public void onFailure(Throwable caught) {
			setTableRows(new ArrayList<Avvisi>());
			//WaitSingleton.get().stop();
		}
		@Override
		public void onSuccess(List<Avvisi> result) {
			setTableRows(result);
			//WaitSingleton.get().stop();
		}
	};
	
	public AvvisiTable(DataModel<Avvisi> model) {
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
	
	public void refresh() {
		drawPage(0);
	}
	
	@Override
	protected void addTableRow(int rowNum, Avvisi rowObj) {
		// Set the data in the current row
		final Avvisi fRowObj = rowObj;
		getInnerTable().setHTML(rowNum, 0, DTF.format(rowObj.getData()));
		final CheckBox importanceCheck = new CheckBox();
		importanceCheck.setValue(rowObj.getImportante());
		importanceCheck.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				updateImportanza(fRowObj.getId(), importanceCheck);
			}
		});
		getInnerTable().setWidget(rowNum, 1, importanceCheck);
		//Message
		String msg = rowObj.getMessaggio();
		if (rowObj.getDataManutenzione() != null) {
			msg += "<br/><b>Manutenzione "+ClientConstants.FORMAT_DAY.format(rowObj.getDataManutenzione())+" ";
			if (rowObj.getOraInizio() != null) {
				msg += "Orario: <i>"+ClientConstants.FORMAT_TIME.format(rowObj.getOraInizio())+"</i> ";
				if (rowObj.getOraFine() != null)
						msg += "- <i>"+ClientConstants.FORMAT_TIME.format(rowObj.getOraFine())+"</i>";
			}
			msg += "</b>";
		}
		HTML message = new HTML(msg);
		getInnerTable().setWidget(rowNum, 2, message);
		InlineHTML trashImg = new InlineHTML(ClientConstants.ICON_DELETE);
		trashImg.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				confirmAndDelete(fRowObj.getId());
			}
		});
		getInnerTable().setWidget(rowNum, 3, trashImg);
	}
	
	@Override
	protected void addHeader() {
		// Set the data in the current row
		getInnerTable().setHTML(0, 0, "Data");
		getInnerTable().setHTML(0, 1, "In evidenza");
		getInnerTable().setHTML(0, 2, "Messaggio");
	}
	
	@Override
	protected void onEmptyResult() {}
	
	private void confirmAndDelete(Integer idAvviso) {
		boolean confirm = Window.confirm("Vuoi veramente cancellare l'avviso?");
		if (confirm) {
			delete(idAvviso);
		}
	}
	
	public void delete(Integer idAvviso) {
		//WaitSingleton.get().start();
		loggingService.deleteAvviso(idAvviso, TABLE_ROWS, callback);
	}
	
	private void updateImportanza(Integer idAvviso, CheckBox importanteCheck) {
		//final CheckBox fImportanteCheck = importanteCheck;
		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
			}
			@Override
			public void onSuccess(Boolean result) {
				//fImportanteCheck.setValue(result);
				UiSingleton.get().addInfo("Importanza avviso aggiornata");
			}
		};
		loggingService.updateImportanza(idAvviso, importanteCheck.getValue(), callback);
	}
	
	
	
	//Inner classes
	
	
	
	public static class AvvisiModel implements DataModel<Avvisi> {
		
		public AvvisiModel() {
		}
		
		@Override
		public void find(int offset, int pageSize,
				AsyncCallback<List<Avvisi>> callback) {
			//WaitSingleton.get().start();
			loggingService.findLastAvvisi(offset, pageSize, callback);
		}
	}
	
}
