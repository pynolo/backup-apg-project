package it.giunti.apg.client.widgets.select;

import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.ArticoliService;
import it.giunti.apg.client.services.ArticoliServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.ArticoliOpzioni;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ArticoliOpzioniPendingSelect extends Select {

	private Map<ArticoliOpzioni, Integer> entityMap;
	private boolean createChangeEvent = false;
	private boolean includeEmptyItem = false;
	
	public ArticoliOpzioniPendingSelect(boolean createChangeEvent, boolean includeEmptyItem) {
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
			for (ArticoliOpzioni ao:entityMap.keySet()) {
				String descr = ao.getOpzione().getPeriodico().getNome()+" - "+
						ao.getOpzione().getNome()+" - "+
						ao.getArticolo().getCodiceMeccanografico()+" "+
						ao.getArticolo().getTitoloNumero();
				descr += " ("+entityMap.get(ao)+" copie stimate)";
				this.addItem(descr, ao.getId().toString());
			}
		}
		showSelectedValue();
		if (createChangeEvent) this.createChangeEvent();
	}
	
	protected void loadEntityList() {
		ArticoliServiceAsync articoliService = GWT.create(ArticoliService.class);
		AsyncCallback<Map<ArticoliOpzioni, Integer>> callback = new AsyncCallback<Map<ArticoliOpzioni, Integer>>() {
			@Override
			public void onFailure(Throwable caught) {
				entityMap = new HashMap<ArticoliOpzioni, Integer>();
				drawListBox();
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(Map<ArticoliOpzioni, Integer> result) {
				entityMap = result;
				drawListBox();
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		articoliService.findPendingArticoliOpzioniCount(callback);
	}
	
}
