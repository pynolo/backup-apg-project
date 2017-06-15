package it.giunti.apg.client.widgets.select;

import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.LookupService;
import it.giunti.apg.client.services.LookupServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.TitoliStudio;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class TitoliStudioSelect extends EntitySelect<TitoliStudio> {
	
	public TitoliStudioSelect(Integer selectedId) {
		super(selectedId);
		reload(selectedId);
	}

	public void reload(Integer selectedId) {
		this.setSelectedId(selectedId);
		loadEntityList();
	}
	
	@Override
	protected void drawListBox(List<TitoliStudio> entityList) {
		this.clear();
		//this.setVisibleItemCount(1);
		//Disegna la lista dei TitoliStudio selezionando quello con selectedId
		this.addItem(AppConstants.SELECT_EMPTY_LABEL, AppConstants.SELECT_EMPTY_VALUE_STRING);
		for (int i=0; i<entityList.size(); i++) {
			TitoliStudio p = entityList.get(i);
			this.addItem(p.getNome(), p.getId().toString());
		}
		showSelectedValue();
	}
	
	protected void loadEntityList() {
		LookupServiceAsync lookupService = GWT.create(LookupService.class);
		AsyncCallback<List<TitoliStudio>> callback = new AsyncCallback<List<TitoliStudio>>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(List<TitoliStudio> result) {
				setEntityList(result);
				try {
					drawListBox(getEntityList());
				} catch (Exception e) {
					UiSingleton.get().addWarning(e.getMessage());
				}
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		lookupService.findTitoliStudio(callback);
	}
	
}
