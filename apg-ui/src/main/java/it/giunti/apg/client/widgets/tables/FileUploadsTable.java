package it.giunti.apg.client.widgets.tables;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineHTML;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.services.UtilService;
import it.giunti.apg.client.services.UtilServiceAsync;
import it.giunti.apg.shared.model.FileUploads;

public class FileUploadsTable extends PagingTable<FileUploads> implements IRefreshable {
	
	private static final int TABLE_ROWS = 50;
	private static final DateTimeFormat DTF = DateTimeFormat.getFormat("dd/MM/yyyy HH:mm");
	private static final UtilServiceAsync utilService = GWT.create(UtilService.class);
	
	boolean isAdmin = false;
	
	private AsyncCallback<List<FileUploads>> callback = new AsyncCallback<List<FileUploads>>() {
		@Override
		public void onFailure(Throwable caught) {
			setTableRows(new ArrayList<FileUploads>());
			//WaitSingleton.get().stop();
		}
		@Override
		public void onSuccess(List<FileUploads> result) {
			setTableRows(result);
			//WaitSingleton.get().stop();
		}
	};
	
	public FileUploadsTable(DataModel<FileUploads> model, boolean isAdmin) {
		super(model, TABLE_ROWS);
		this.isAdmin = isAdmin;
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
	protected void addTableRow(int rowNum, FileUploads rowObj) {
		// Set the data in the current row
		//DATA
		getInnerTable().setHTML(rowNum, 0, DTF.format(rowObj.getDataCreazione()));
		//NOME
		HTML message = new HTML("<b>"+rowObj.getFileName()+"</b>");
		getInnerTable().setWidget(rowNum, 1, message);
		//Utente
		getInnerTable().setHTML(rowNum, 2, "<i>("+rowObj.getIdUtente()+")</i>");
		// DELETE
		if (isAdmin) {
			final FileUploads fRowObj = rowObj;
			InlineHTML trashImg = new InlineHTML(ClientConstants.ICON_DELETE);
			trashImg.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent arg0) {
					confirmAndDelete(fRowObj.getId());
				}
			});
			getInnerTable().setWidget(rowNum, 3, trashImg);
		}
	}
	
	@Override
	protected void addHeader() {
		// Set the data in the current row
		getInnerTable().setHTML(0, 0, "Data");
		getInnerTable().setHTML(0, 1, "Nome");
		getInnerTable().setHTML(0, 2, "Utente");
	}
	
	@Override
	protected void onEmptyResult() {}
	
	private void confirmAndDelete(Integer idFileUpload) {
		boolean confirm = Window.confirm("Vuoi veramente cancellare il file caricato?");
		if (confirm) {
			delete(idFileUpload);
		}
	}
	public void delete(Integer idFileUpload) {
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
		utilService.deleteFileUpload(idFileUpload, delCallback);
	}
	
	
	
	//Inner classes
	
	
	
	public static class FileUploadsModel implements DataModel<FileUploads> {
		
		public FileUploadsModel() {
		}
		
		@Override
		public void find(int offset, int pageSize,
				AsyncCallback<List<FileUploads>> callback) {
			//WaitSingleton.get().start();
			utilService.findFileUploadsStripped(callback);
		}
	}
	
}
