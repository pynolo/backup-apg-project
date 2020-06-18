package it.giunti.apg.client.widgets.select;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.MaterialiService;
import it.giunti.apg.client.services.MaterialiServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.MaterialiOpzioni;

public class MaterialiOpzioniPendingSelect extends Select {

	private Map<MaterialiOpzioni, Integer> entityMap;
	private boolean createChangeEvent = false;
	private boolean includeEmptyItem = false;
	
	public MaterialiOpzioniPendingSelect(boolean createChangeEvent, boolean includeEmptyItem) {
		super("");
		this.includeEmptyItem = includeEmptyItem;
		reload(createChangeEvent);
	}

	public void reload(boolean createChangeEvent) {
		this.createChangeEvent = createChangeEvent;
		loadEntityList();
	}

	protected void drawListBox() {
		this.clear();
		if (includeEmptyItem) this.addItem(AppConstants.SELECT_EMPTY_LABEL, AppConstants.SELECT_EMPTY_VALUE_STRING);
		if (entityMap != null) {
			for (MaterialiOpzioni ao:entityMap.keySet()) {
				String descr = ao.getOpzione().getPeriodico().getNome()+" - "+
						ao.getOpzione().getNome()+" - "+
						ao.getMateriale().getCodiceMeccanografico()+" "+
						ao.getMateriale().getTitolo();
				descr += " ("+entityMap.get(ao)+" copie stimate)";
				this.addItem(descr, ao.getId().toString());
			}
		}
		showSelectedValue();
		if (createChangeEvent) this.createChangeEvent();
	}
	
	protected void loadEntityList() {
		MaterialiServiceAsync matService = GWT.create(MaterialiService.class);
		AsyncCallback<Map<MaterialiOpzioni, Integer>> callback = new AsyncCallback<Map<MaterialiOpzioni, Integer>>() {
			@Override
			public void onFailure(Throwable caught) {
				entityMap = new HashMap<MaterialiOpzioni, Integer>();
				drawListBox();
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(Map<MaterialiOpzioni, Integer> result) {
				entityMap = result;
				drawListBox();
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		matService.findPendingMaterialiOpzioniCount(callback);
	}
	
}
