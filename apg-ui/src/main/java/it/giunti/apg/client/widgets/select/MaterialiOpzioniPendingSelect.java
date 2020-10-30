package it.giunti.apg.client.widgets.select;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.MaterialiService;
import it.giunti.apg.client.services.MaterialiServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.MaterialiOpzioni;

public class MaterialiOpzioniPendingSelect extends Select {

	private List<MaterialiOpzioni> moList;
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
		if (moList != null) {
			for (MaterialiOpzioni mo:moList) {
				String descr = mo.getOpzione().getPeriodico().getNome()+" - "+
						mo.getOpzione().getNome()+" - "+
						mo.getMateriale().getCodiceMeccanografico()+" "+
						mo.getMateriale().getTitolo();
				this.addItem(descr, mo.getId().toString());
			}
		}
		showSelectedValue();
		if (createChangeEvent) this.createChangeEvent();
	}
	
	protected void loadEntityList() {
		MaterialiServiceAsync matService = GWT.create(MaterialiService.class);
		AsyncCallback<List<MaterialiOpzioni>> callback = new AsyncCallback<List<MaterialiOpzioni>>() {
			@Override
			public void onFailure(Throwable caught) {
				moList = new ArrayList<MaterialiOpzioni>();
				drawListBox();
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(List<MaterialiOpzioni> result) {
				moList = result;
				drawListBox();
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		matService.findPendingMaterialiOpzioni(callback);
	}
	
}
