package it.giunti.apg.client.widgets.select;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.MaterialiService;
import it.giunti.apg.client.services.MaterialiServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.MaterialiListini;

public class MaterialiListiniPendingSelect extends Select {

	private List<MaterialiListini> mlList;
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
		if (mlList != null) {
			for (MaterialiListini ml:mlList) {
				String descr = ml.getListino().getTipoAbbonamento().getPeriodico().getNome()+ " - "+
						ml.getListino().getTipoAbbonamento().getCodice()+" "+
						ml.getListino().getTipoAbbonamento().getNome()+" - "+
						ml.getMateriale().getCodiceMeccanografico()+" "+
						ml.getMateriale().getTitolo();
				this.addItem(descr, ml.getId().toString());
			}
		}
		showSelectedValue();
		if (createChangeEvent) this.createChangeEvent();
	}
	
	protected void loadEntityList() {
		MaterialiServiceAsync matService = GWT.create(MaterialiService.class);
		AsyncCallback<List<MaterialiListini>> callback = new AsyncCallback<List<MaterialiListini>>() {
			@Override
			public void onFailure(Throwable caught) {
				mlList = new ArrayList<MaterialiListini>();
				drawListBox();
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(List<MaterialiListini> result) {
				mlList = result;
				drawListBox();
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		matService.findPendingMaterialiListini(callback);
	}
	
}
