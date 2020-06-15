package it.giunti.apg.client.widgets.select;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.MaterialiService;
import it.giunti.apg.client.services.MaterialiServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Materiali;

public class MaterialiSelect extends EntitySelect<Materiali> {

	private Date visibleFromDt = AppConstants.DEFAULT_DATE;
	private boolean createChangeEvent = false;
	private boolean includeEmptyItem = false;
	
	public MaterialiSelect(Integer selectedId, Date visibleFromDt, boolean createChangeEvent, boolean includeEmptyItem) {
		super(selectedId);
		this.createChangeEvent = createChangeEvent;
		this.includeEmptyItem = includeEmptyItem;
		reload(selectedId, visibleFromDt);
	}

	public void reload(Integer selectedId, Date newVisibleFromDate) {
		this.setSelectedId(selectedId);
		if (!this.visibleFromDt.equals(newVisibleFromDate)) {
			this.visibleFromDt = newVisibleFromDate;
			loadEntityList();
		} else {
			drawListBox(this.getEntityList());
		}
	}

	@Override
	protected void drawListBox(List<Materiali> entityList) {
		this.clear();
		//this.setVisibleItemCount(1);
		if (includeEmptyItem) this.addItem(AppConstants.SELECT_EMPTY_LABEL, AppConstants.SELECT_EMPTY_VALUE_STRING);
		if (entityList != null) {
			for (int i=0; i<entityList.size(); i++) {
				Materiali r = entityList.get(i);
				String descr = "";
				if (r.getCodiceMeccanografico() != null) {
					if (r.getCodiceMeccanografico().length() > 0) {
						descr += r.getCodiceMeccanografico() + " - ";
					}
				}
				descr += r.getTitolo();
				//if ((r.getGiornoLimite() != null) && (r.getMeseLimite() != null)) {
				//	descr += " ["+r.getGiornoLimite()+" ";
				//	descr += ClientConstants.MESI[r.getMeseLimite()]+"]";
				//}
				this.addItem(descr, r.getCodiceMeccanografico());
			}
		}
		showSelectedValue();
		if (createChangeEvent) this.createChangeEvent();
	}
	
	@Override
	protected void loadEntityList() {
		MaterialiServiceAsync matService = GWT.create(MaterialiService.class);
		AsyncCallback<List<Materiali>> callback = new AsyncCallback<List<Materiali>>() {
			@Override
			public void onFailure(Throwable caught) {
				drawListBox(null);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(List<Materiali> result) {
				setEntityList(result);
				drawListBox(result);
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		matService.findMaterialiByDate(visibleFromDt, 0, Integer.MAX_VALUE, callback);
	}
	
}
