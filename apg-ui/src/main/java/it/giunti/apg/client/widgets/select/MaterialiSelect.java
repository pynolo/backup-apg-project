package it.giunti.apg.client.widgets.select;

import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.ArticoliService;
import it.giunti.apg.client.services.ArticoliServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Articoli;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ArticoliSelect extends EntitySelect<Articoli> {

	private Date startDt = AppConstants.DEFAULT_DATE;
	private Date finishDt = AppConstants.DEFAULT_DATE;
	private boolean createChangeEvent = false;
	private boolean includeEmptyItem = false;
	
	public ArticoliSelect(Integer selectedId, Date startDt, Date finishDt, boolean createChangeEvent, boolean includeEmptyItem) {
		super(selectedId);
		this.includeEmptyItem = includeEmptyItem;
		reload(selectedId, startDt, finishDt, createChangeEvent);
	}

	public void reload(Integer selectedId, Date startDt, Date finishDt, boolean createChangeEvent) {
		this.setSelectedId(selectedId);
		this.createChangeEvent = createChangeEvent;
		if (!this.startDt.equals(startDt) || !this.finishDt.equals(finishDt)) {
			this.startDt = startDt;
			this.finishDt = finishDt;
			loadEntityList();
		} else {
			drawListBox(this.getEntityList());
		}
	}

	@Override
	protected void drawListBox(List<Articoli> entityList) {
		this.clear();
		//this.setVisibleItemCount(1);
		if (includeEmptyItem) this.addItem(AppConstants.SELECT_EMPTY_LABEL, AppConstants.SELECT_EMPTY_VALUE_STRING);
		if (entityList != null) {
			for (int i=0; i<entityList.size(); i++) {
				Articoli r = entityList.get(i);
				String descr = "";
				if (r.getCodiceInterno() != null) {
					if (r.getCodiceInterno().length() > 0) {
						descr += "["+r.getCodiceInterno() + "] ";
					}
				}
				descr += r.getTitoloNumero();
				//if ((r.getGiornoLimite() != null) && (r.getMeseLimite() != null)) {
				//	descr += " ["+r.getGiornoLimite()+" ";
				//	descr += ClientConstants.MESI[r.getMeseLimite()]+"]";
				//}
				this.addItem(descr, r.getId().toString());
			}
		}
		showSelectedValue();
		if (createChangeEvent) this.createChangeEvent();
	}
	
	@Override
	protected void loadEntityList() {
		ArticoliServiceAsync articoliService = GWT.create(ArticoliService.class);
		AsyncCallback<List<Articoli>> callback = new AsyncCallback<List<Articoli>>() {
			@Override
			public void onFailure(Throwable caught) {
				drawListBox(null);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(List<Articoli> result) {
				setEntityList(result);
				drawListBox(result);
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		articoliService.findArticoliByDateInterval(startDt, finishDt, callback);
	}
	
}
