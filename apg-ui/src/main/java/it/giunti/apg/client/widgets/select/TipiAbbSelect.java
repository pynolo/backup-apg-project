package it.giunti.apg.client.widgets.select;

import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.TipiAbbService;
import it.giunti.apg.client.services.TipiAbbServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.TipiAbbonamento;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class TipiAbbSelect extends EntitySelect<TipiAbbonamento> {

	private Integer selectedId = null;
	private Integer idPeriodico = null;
	private Date beginDt = null;
	//private Date endDt = null;
	private Boolean includeEmptyItem = null;
	private Boolean createChangeEvent = null;

	public TipiAbbSelect(Integer selectedId, Integer idPeriodico, Date beginDt, /*Date endDt,*/
			boolean includeEmptyItem, boolean createChangeEvent) {
		super(selectedId);
		this.selectedId = selectedId;
		this.includeEmptyItem = includeEmptyItem;
		reload(selectedId, idPeriodico, beginDt, /*endDt,*/ createChangeEvent);
	}
	
	public void reload(Integer selectedId, Integer idPeriodico, Date beginDt, /*Date endDt,*/
			boolean createChangeEvent) {
		this.setSelectedId(selectedId);
		this.selectedId = selectedId;
		this.idPeriodico = idPeriodico;
		this.beginDt = beginDt;
		//this.endDt = endDt;
		this.createChangeEvent = createChangeEvent;
		loadEntityList();
	}

	@Override
	protected void drawListBox(List<TipiAbbonamento> entityList) {
		this.clear();
		//this.setVisibleItemCount(1);
		if (includeEmptyItem) this.addItem(AppConstants.SELECT_EMPTY_LABEL);
		if (entityList != null) {
			for (int i=0; i<entityList.size(); i++) {
				TipiAbbonamento ta = entityList.get(i);
				String taName = ta.getCodice() +" "+ta.getNome();
				this.addItem(taName, ta.getId().toString());
			}
		}
		showSelectedValue();
		if(createChangeEvent) createChangeEvent();
	}
	
	@Override
	protected void loadEntityList() {
		TipiAbbServiceAsync tipiAbbService = GWT.create(TipiAbbService.class);
		AsyncCallback<List<TipiAbbonamento>> callback = new AsyncCallback<List<TipiAbbonamento>>() {
			@Override
			public void onFailure(Throwable caught) {
				//UiSingleton.get().addInfo("Non ci sono tipi abbonamento disponibili per questa rivista");
				drawListBox(null);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(List<TipiAbbonamento> result) {
				//tipiAbbListinoCache = result;
				drawListBox(result);
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		tipiAbbService.findTipiAbbonamentoByPeriodicoDate(idPeriodico, selectedId, beginDt, /*endDt,*/ callback);
	}
	
}
