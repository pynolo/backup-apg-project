package it.giunti.apg.client.widgets.select;

import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.LookupService;
import it.giunti.apg.client.services.LookupServiceAsync;
import it.giunti.apg.shared.model.Nazioni;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class NazioniSelect extends EntitySelect<Nazioni> {

	public NazioniSelect(String selectedId) {
		super(selectedId);
		reload(selectedId);
	}
	
	public void reload(String selectedId) {
		this.setSelectedId(selectedId);
		loadEntityList();
	}
	
	@Override
	protected void drawListBox(List<Nazioni> nazioni) {
		this.clear();
		//this.setVisibleItemCount(1);
		for (int i=0; i<nazioni.size(); i++) {
			this.addItem(nazioni.get(i).getNomeNazione(),
					nazioni.get(i).getId());
		}
		showSelectedValue();
	}
	
	@Override
	protected void loadEntityList() {
		LookupServiceAsync lookupService = GWT.create(LookupService.class);
		AsyncCallback<List<Nazioni>> callback = new AsyncCallback<List<Nazioni>>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(List<Nazioni> result) {
				setEntityList(result);
				drawListBox(result);
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		lookupService.findNazioni(callback);
	}
	
}
