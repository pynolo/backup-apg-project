package it.giunti.apg.client.widgets.select;

import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.ArticoliService;
import it.giunti.apg.client.services.ArticoliServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.ArticoliListini;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ArticoliListiniPendingSelect extends Select {

	private Map<ArticoliListini, Integer> entityMap;
	private boolean createChangeEvent = false;
	private boolean includeEmptyItem = false;
	
	public ArticoliListiniPendingSelect(boolean createChangeEvent, boolean includeEmptyItem) {
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
			for (ArticoliListini al:entityMap.keySet()) {
				String descr = al.getListino().getTipoAbbonamento().getPeriodico().getNome()+ " - "+
						al.getListino().getTipoAbbonamento().getCodice()+" "+
						al.getListino().getTipoAbbonamento().getNome()+" - "+
						al.getArticolo().getCodiceMeccanografico()+" "+
						al.getArticolo().getTitoloNumero();
				descr += " ("+entityMap.get(al)+" copie stimate)";
				this.addItem(descr, al.getId().toString());
			}
		}
		showSelectedValue();
		if (createChangeEvent) this.createChangeEvent();
	}
	
	protected void loadEntityList() {
		ArticoliServiceAsync articoliService = GWT.create(ArticoliService.class);
		AsyncCallback<Map<ArticoliListini, Integer>> callback = new AsyncCallback<Map<ArticoliListini, Integer>>() {
			@Override
			public void onFailure(Throwable caught) {
				entityMap = new HashMap<ArticoliListini, Integer>();
				drawListBox();
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(Map<ArticoliListini, Integer> result) {
				entityMap = result;
				drawListBox();
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		articoliService.findPendingArticoliListiniCount(callback);
	}
	
}
