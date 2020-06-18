package it.giunti.apg.client.widgets.select;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.MaterialiService;
import it.giunti.apg.client.services.MaterialiServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.MaterialiListini;

public class MaterialiListiniPendingSelect extends Select {

	private Map<MaterialiListini, Integer> entityMap;
	private boolean createChangeEvent = false;
	private boolean includeEmptyItem = false;
	
	public MaterialiListiniPendingSelect(boolean createChangeEvent, boolean includeEmptyItem) {
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
			for (MaterialiListini al:entityMap.keySet()) {
				String descr = al.getListino().getTipoAbbonamento().getPeriodico().getNome()+ " - "+
						al.getListino().getTipoAbbonamento().getCodice()+" "+
						al.getListino().getTipoAbbonamento().getNome()+" - "+
						al.getMateriale().getCodiceMeccanografico()+" "+
						al.getMateriale().getTitolo();
				descr += " ("+entityMap.get(al)+" copie stimate)";
				this.addItem(descr, al.getId().toString());
			}
		}
		showSelectedValue();
		if (createChangeEvent) this.createChangeEvent();
	}
	
	protected void loadEntityList() {
		MaterialiServiceAsync matService = GWT.create(MaterialiService.class);
		AsyncCallback<Map<MaterialiListini, Integer>> callback = new AsyncCallback<Map<MaterialiListini, Integer>>() {
			@Override
			public void onFailure(Throwable caught) {
				entityMap = new HashMap<MaterialiListini, Integer>();
				drawListBox();
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(Map<MaterialiListini, Integer> result) {
				entityMap = result;
				drawListBox();
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		matService.findPendingMaterialiListiniCount(callback);
	}
	
}
