package it.giunti.apg.client.widgets.select;

import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.LookupService;
import it.giunti.apg.client.services.LookupServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Province;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ProvinceSelect extends EntitySelect<Province> {

	public ProvinceSelect(String selectedId) {
		super(selectedId);
		reload(selectedId);
	}
	
	public void reload(String selectedId) {
		this.setSelectedId(selectedId);
		loadEntityList();
	}
	
	@Override
	protected void drawListBox(List<Province> province) {
		this.clear();
		//this.setVisibleItemCount(1);
		this.addItem(AppConstants.SELECT_EMPTY_LABEL, AppConstants.SELECT_EMPTY_LABEL);
		for (int i=0; i<province.size(); i++) {
			this.addItem(province.get(i).getId(),
					province.get(i).getId());
		}
		showSelectedValue();
	}
	
	@Override
	protected void loadEntityList() {
		LookupServiceAsync lookupService = GWT.create(LookupService.class);
		AsyncCallback<List<Province>> callback = new AsyncCallback<List<Province>>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(List<Province> result) {
				setEntityList(result);
				drawListBox(result);
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		lookupService.findProvince(callback);
	}
	
}
