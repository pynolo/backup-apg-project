package it.giunti.apg.client.widgets.tables;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.InlineHTML;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.frames.AdesionePopUp;
import it.giunti.apg.client.services.LookupService;
import it.giunti.apg.client.services.LookupServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Adesioni;
import it.giunti.apg.shared.model.Ruoli;

public class AdesioniTable extends PagingTable<Adesioni> implements IRefreshable {
	
	private static final LookupServiceAsync lookupService = GWT.create(LookupService.class);
	
	private static final int TABLE_ROWS = 50;//ClientConstants.TABLE_ROWS_DEFAULT;
	private boolean isAdmin = false;
	
	private AsyncCallback<List<Adesioni>> callback = new AsyncCallback<List<Adesioni>>() {
		@Override
		public void onFailure(Throwable caught) {
			setTableRows(new ArrayList<Adesioni>());
			//WaitSingleton.get().stop();
		}
		@Override
		public void onSuccess(List<Adesioni> result) {
			setTableRows(result);
			//WaitSingleton.get().stop();
		}
	};
	
	public AdesioniTable(DataModel<Adesioni> model, Ruoli role) {
		super(model, TABLE_ROWS);
		this.isAdmin = (role.getId() >= AppConstants.RUOLO_ADMIN);
		drawPage(0);
	}

	@Override
	public void refresh() {
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
	protected void addTableRow(int rowNum, Adesioni rowObj) {
		// Set the data in the current row
		final Adesioni fRowObj = rowObj;
		final AdesioniTable table = this;
		
		//Adesione
		String linkText = "";
		if (rowObj.getCodice() != null) {
			linkText += rowObj.getCodice()+" ";
		}
		Anchor adesioneAnchor = new Anchor(linkText, true);
		if (isAdmin) {
			linkText = "<b>"+linkText+"</b>";
			adesioneAnchor.setHTML(linkText);
			adesioneAnchor.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent arg0) {
					@SuppressWarnings("unused")
					AdesionePopUp popUp = new AdesionePopUp(fRowObj.getId(), table);
				}
			});
		}
		getInnerTable().setWidget(rowNum, 0, adesioneAnchor);
		//delete
		if (isAdmin) {
			InlineHTML trashImg = new InlineHTML(ClientConstants.ICON_DELETE);
			trashImg.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent arg0) {
					confirmAndDelete(fRowObj);
				}
			});
			getInnerTable().setWidget(rowNum, 1, trashImg);
		}
	}
	
	@Override
	protected void addHeader() {
		// Set the data in the current row
		getInnerTable().setHTML(0, 0, "Adesione");
		getInnerTable().setHTML(0, 1, "Descrizione");
	}
	
	@Override
	protected void onEmptyResult() {}
	
	private void confirmAndDelete(Adesioni adesione) {
		boolean confirm = Window.confirm("Vuoi veramente eliminare l'adesione?");
		if (confirm) {
			delete(adesione.getCodice());
		}
	}
	public void delete(String codiceAdesione) {
		AsyncCallback<Boolean> deleteCallback = new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
			}
			@Override
			public void onSuccess(Boolean result) {
				refresh();
			}
		};
		//WaitSingleton.get().start();
		lookupService.deleteAdesione(codiceAdesione, deleteCallback);
	}
	
	
	
	//Inner classes
	
	
	
	public static class AdesioniModel implements DataModel<Adesioni> {
		private String prefixString = null;
		
		public AdesioniModel(String prefixString) {
			this.prefixString=prefixString;
		}
		
		@Override
		public void find(int offset, int pageSize,
				AsyncCallback<List<Adesioni>> callback) {
			//WaitSingleton.get().start();
			lookupService.findAdesioni(prefixString, offset, pageSize, callback);
		}
	}
	
}
