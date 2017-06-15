package it.giunti.apg.client.widgets.select;

import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.AuthService;
import it.giunti.apg.client.services.AuthServiceAsync;
import it.giunti.apg.shared.model.Ruoli;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class RuoliSelect extends EntitySelect<Ruoli> {
	
	public RuoliSelect(Integer selectedId) {
		super(selectedId);
		reload(selectedId);
	}

	public void reload(Integer selectedId) {
		this.setSelectedId(selectedId);
		loadEntityList();
	}
	
	@Override
	protected void drawListBox(List<Ruoli> entityList) {
		this.clear();
		//this.setVisibleItemCount(1);
		//Disegna la lista dei ruoli selezionando quello con selectedId
		for (int i=0; i<entityList.size(); i++) {
			this.addItem(entityList.get(i).getDescrizione(),
					entityList.get(i).getId().toString());
		}
		showSelectedValue();
	}
	
	protected void loadEntityList() {
		AuthServiceAsync authService = GWT.create(AuthService.class);
		AsyncCallback<List<Ruoli>> callback = new AsyncCallback<List<Ruoli>>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(List<Ruoli> result) {
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
		authService.findRuoli(callback);
	}
	
}
