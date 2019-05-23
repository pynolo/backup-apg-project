package it.giunti.apg.client.widgets.select;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.AbbonamentiService;
import it.giunti.apg.client.services.AbbonamentiServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Adesioni;

public class AdesioniSelect extends Select {
	
	private String oldAdesione;
	private List<String> adesioniList = new ArrayList<String>();
	
	public AdesioniSelect(String adesione) {
		super(adesione);
		this.oldAdesione=adesione;
		loadEntityList();
	}
	
	protected void drawListBox() {
		this.clear();
		//this.setVisibleItemCount(1);
		//Disegna la lista delle professioni selezionando quello con selectedId
		this.addItem(AppConstants.SELECT_EMPTY_LABEL, "");
		if (oldAdesione != null) {
			if (oldAdesione.length() > 0) this.addItem(oldAdesione, oldAdesione);
		}
		for (int i=0; i<adesioniList.size(); i++) {
			if (!adesioniList.get(i).equals(oldAdesione)) {
				this.addItem(adesioniList.get(i), adesioniList.get(i));
			}
		}
		showSelectedValue();
	}
	
	protected void loadEntityList() {
		AbbonamentiServiceAsync abboService = GWT.create(AbbonamentiService.class);
		AsyncCallback<List<Adesioni>> callback = new AsyncCallback<List<Adesioni>>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(List<Adesioni> result) {
				if (result.size() > 0) {
					adesioniList.clear();
					for (Adesioni ade:result) {
						adesioniList.add(ade.getCodice());
					}
				}
				try {
					drawListBox();
				} catch (Exception e) {
					UiSingleton.get().addWarning(e.getMessage());
				}
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		abboService.findAdesioni(null, 0, Integer.MAX_VALUE, callback);
	}
	
}
