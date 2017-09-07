package it.giunti.apg.client.widgets.select;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.FascicoliService;
import it.giunti.apg.client.services.FascicoliServiceAsync;
import it.giunti.apg.core.DateUtil;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Fascicoli;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FascicoliSelect extends EntitySelect<Fascicoli> {

	private Integer selectedId = null;
	private Integer idPeriodico = null;
	private Long boxStartDt = null;
	private Long boxFinishDt = null;
	private Boolean includeOpzioni = null;
	private Boolean longDescription = null;
	private Boolean includeSent = null;
	private Boolean createChangeEvent = null;
	private Boolean showEmptyItem = null;
	
	public FascicoliSelect() {
		super(AppConstants.NEW_ITEM_ID);
	}
	
	public FascicoliSelect(Integer selectedId, Integer idPeriodico, 
			long boxStartDt, long boxFinishDt, boolean includeOpzioni, boolean longDescription,
			 boolean includeSent, boolean createChangeEvent, boolean showEmptyItem) {
		super(selectedId);
		reload(selectedId, idPeriodico, boxStartDt, boxFinishDt, includeOpzioni, longDescription, includeSent, createChangeEvent, showEmptyItem);
	}

	public void reload(Integer selectedId, Integer idPeriodico, 
			long boxStartDt, long boxFinishDt, boolean includeOpzioni,
			boolean longDescription, boolean includeSent,
			boolean createChangeEvent, boolean showEmptyItem) {
		this.setSelectedId(selectedId);
		this.selectedId = selectedId;
		this.idPeriodico = idPeriodico;
		this.boxStartDt = boxStartDt;
		this.boxFinishDt = boxFinishDt;
		this.includeOpzioni = includeOpzioni;
		this.longDescription = longDescription;
		this.includeSent = includeSent;
		this.createChangeEvent = createChangeEvent;
		this.showEmptyItem = showEmptyItem;
		loadEntityList();
	}
	
	@Override
	protected void drawListBox(List<Fascicoli> entityList) {
		this.clear();
		//this.setVisibleItemCount(1);
		if (showEmptyItem) {
			this.addItem(AppConstants.SELECT_EMPTY_LABEL, AppConstants.SELECT_EMPTY_VALUE_STRING);
		}
		//Disegna la lista dei fascicoli selezionando quello con selectedId
		for (Fascicoli fas:entityList) {
			String fasDesc = fas.getPeriodico().getUid()+" ";
			if (fas.getOpzione() == null) {
				if (fas.getFascicoliAccorpati() > 0) {
					//Se fascicolo
					fasDesc += "fascicolo "+fas.getTitoloNumero();
				} else {
					//Se allegato
					fasDesc += "alleg. "+fas.getTitoloNumero();
				}
			} else {
				//Se opzione
				fasDesc += "opz. ["+fas.getOpzione().getUid()+"] "+fas.getOpzione().getNome();
				if (fas.getTitoloNumero() != null) fasDesc += " "+fas.getTitoloNumero();
			}
			fasDesc += " - "+fas.getDataCop()+" "+
					ClientConstants.FORMAT_YEAR.format(fas.getDataInizio());
			if ((fas.getDataEstrazione() != null) && longDescription) {
				fasDesc += " (estr."+
						ClientConstants.FORMAT_DAY.format(fas.getDataEstrazione())+")";
			}
			//String fasName = fas.getTitoloNumero() +" ("+ ClientConstants.FORMAT_DAY.format(fas.getDataInizio())+")";
			Integer fasId = fas.getId();
			boolean toSend = false;
			if (fas.getDataEstrazione() == null) {
				toSend = true;
			} else {
				if (DateUtil.now().getTime() < fas.getDataEstrazione().getTime()+AppConstants.DAY) {
					toSend = true;
				}
			}
			if (toSend || (!toSend && includeSent))
			this.addItem(fasDesc, fasId.toString());
		}
		showSelectedValue();
		if (createChangeEvent) this.createChangeEvent();
	}
	
	protected void loadEntityList() {
		FascicoliServiceAsync fascicoliService = GWT.create(FascicoliService.class);
		AsyncCallback<List<Fascicoli>> callback = new AsyncCallback<List<Fascicoli>>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addWarning("Non ci sono fascicoli disponibili per questa rivista");
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(List<Fascicoli> result) {
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
		fascicoliService.findFascicoliByPeriodico(idPeriodico, selectedId, boxStartDt, boxFinishDt, includeOpzioni, true, 0, Integer.MAX_VALUE, callback);
	}
	
}
