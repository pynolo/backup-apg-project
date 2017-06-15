package it.giunti.apg.client.widgets.select;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.OpzioniService;
import it.giunti.apg.client.services.OpzioniServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Opzioni;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class OpzioniSelect extends EntitySelect<Opzioni> {

	private OpzioniServiceAsync opzioniService = GWT.create(OpzioniService.class);
	
	private Integer idPeriodico = null;
	private Date extractionDt = null;
	private Date startDt = null;
	private Date finishDt = null;
	private boolean includeEmptyItem = false;
	private boolean soloCartacei = false;
	
	public OpzioniSelect(Integer selectedId, Integer idPeriodico, Date extractionDt,
			boolean includeEmptyItem, boolean soloCartacei) {
		super(selectedId);
		this.includeEmptyItem = includeEmptyItem;
		this.soloCartacei = soloCartacei;
		reload(selectedId, idPeriodico, extractionDt);
	}
	
	public OpzioniSelect(Integer selectedId, Integer idPeriodico, Date startDt, Date finishDt,
			boolean includeEmptyItem, boolean soloCartacei) {
		super(selectedId);
		this.includeEmptyItem = includeEmptyItem;
		this.soloCartacei = soloCartacei;
		reload(selectedId, idPeriodico, startDt, finishDt);
	}
	
	public void reload(Integer selectedId, Integer idPeriodico,
			Date extractionDt) {
		this.setSelectedId(selectedId);
		this.idPeriodico = idPeriodico;
		this.extractionDt = extractionDt;
		loadEntityList();
	}

	public void reload(Integer selectedId, Integer idPeriodico,
			Date startDt, Date finishDt) {
		this.setSelectedId(selectedId);
		this.idPeriodico = idPeriodico;
		this.startDt = startDt;
		this.finishDt = finishDt;
		loadEntityList();
	}
	
	@Override
	protected void drawListBox(List<Opzioni> entityList) {
		this.clear();
		//this.setVisibleItemCount(1);
		if (includeEmptyItem || (entityList == null)) this.addItem(AppConstants.SELECT_EMPTY_LABEL, AppConstants.SELECT_EMPTY_VALUE_STRING);
		if (entityList != null) {
			for (int i=0; i<entityList.size(); i++) {
				Opzioni s = entityList.get(i);
				String label = "["+s.getUid()+"] "+s.getNome();
				if (s.getDataFine() != null) label +=
						" ("+ClientConstants.FORMAT_YEAR.format(s.getDataInizio())+")";
				this.addItem(label, s.getId().toString());
			}
		}
		showSelectedValue();
	}
	
	@Override
	protected void loadEntityList() {
		AsyncCallback<List<Opzioni>> callback = new AsyncCallback<List<Opzioni>>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(List<Opzioni> result) {
				setEntityList(result);
				try {
					drawListBox(getEntityList());
				} catch (Exception e) {
					UiSingleton.get().addWarning(e.getMessage());
				}
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		if (extractionDt != null) {
			opzioniService.findOpzioni(idPeriodico, extractionDt, soloCartacei, callback);
		} else {
			opzioniService.findOpzioni(idPeriodico, startDt, finishDt, soloCartacei, callback);
		}
	}
	
}
