package it.giunti.apg.client.widgets.select;

import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.LookupService;
import it.giunti.apg.client.services.LookupServiceAsync;
import it.giunti.apg.shared.model.AliquoteIva;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AliquoteIvaSelect extends EntitySelect<AliquoteIva> {

	private Date selectionDate = null;
	
	public AliquoteIvaSelect(Integer selectedId, Date selectionDate) {
		super(selectedId);
		this.selectionDate=selectionDate;
		reload(selectedId);
	}
	
	public void reload(Integer selectedId) {
		this.setSelectedId(selectedId);
		loadEntityList();
	}
	
	@Override
	protected void drawListBox(List<AliquoteIva> ivaList) {
		this.clear();
		//this.setVisibleItemCount(1);
		for (int i=0; i<ivaList.size(); i++) {
			this.addItem(ivaList.get(i).getDescr(),
					ivaList.get(i).getId().toString());
		}
		showSelectedValue();
	}
	
	@Override
	protected void loadEntityList() {
		LookupServiceAsync lookupService = GWT.create(LookupService.class);
		AsyncCallback<List<AliquoteIva>> callback = new AsyncCallback<List<AliquoteIva>>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(List<AliquoteIva> result) {
				setEntityList(result);
				drawListBox(result);
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		lookupService.findAliquoteIva(selectionDate, callback);
	}
	
}
