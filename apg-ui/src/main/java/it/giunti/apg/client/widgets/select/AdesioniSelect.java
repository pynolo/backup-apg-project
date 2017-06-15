package it.giunti.apg.client.widgets.select;

import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.LookupService;
import it.giunti.apg.client.services.LookupServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Adesioni;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AdesioniSelect extends EntitySelect<Adesioni> {
	
	public AdesioniSelect(Integer selectedId) {
		super(selectedId);
		reload(selectedId);
	}

	public void reload(Integer selectedId) {
		this.setSelectedId(selectedId);
		loadEntityList();
	}
	
	@Override
	protected void drawListBox(List<Adesioni> entityList) {
		this.clear();
		//this.setVisibleItemCount(1);
		//Disegna la lista delle professioni selezionando quello con selectedId
		this.addItem(AppConstants.SELECT_EMPTY_LABEL, AppConstants.SELECT_EMPTY_VALUE_STRING);
		for (int i=0; i<entityList.size(); i++) {
			Adesioni p = entityList.get(i);
			this.addItem(p.getCodice(), p.getId().toString());
		}
		showSelectedValue();
	}
	
	protected void loadEntityList() {
		LookupServiceAsync lookupService = GWT.create(LookupService.class);
		AsyncCallback<List<Adesioni>> callback = new AsyncCallback<List<Adesioni>>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(List<Adesioni> result) {
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
		lookupService.findAdesioni(null, 0, Integer.MAX_VALUE, callback);
	}
	
}
