package it.giunti.apg.client.widgets.select;

import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.LookupService;
import it.giunti.apg.client.services.LookupServiceAsync;
import it.giunti.apg.shared.model.Macroaree;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MacroareeSelect extends EntitySelect<Macroaree> {
	
	public MacroareeSelect(Integer selectedId) {
		super(selectedId);
		reload(selectedId);
	}

	public void reload(Integer selectedId) {
		this.setSelectedId(selectedId);
		loadEntityList();
	}
	
	@Override
	protected void drawListBox(List<Macroaree> entityList) {
		this.clear();
		//this.setVisibleItemCount(1);
		//Disegna la lista delle Macroaree selezionando quello con selectedId
		for (int i=0; i<entityList.size(); i++) {
			Macroaree p = entityList.get(i);
			this.addItem(p.getNome(), p.getId().toString());
		}
		showSelectedValue();
	}
	
	protected void loadEntityList() {
		LookupServiceAsync lookupService = GWT.create(LookupService.class);
		AsyncCallback<List<Macroaree>> callback = new AsyncCallback<List<Macroaree>>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(List<Macroaree> result) {
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
		lookupService.findMacroaree(callback);
	}
	
}
