package it.giunti.apg.client.widgets.select;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.MaterialiService;
import it.giunti.apg.client.services.MaterialiServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.MaterialiProgrammazione;

public class MaterialiProgrammazioneSelect extends EntitySelect<MaterialiProgrammazione> {

	private Integer selectedId = null;
	private Integer idPeriodico = null;
	private Long boxStartDt = null;
	private Long boxFinishDt = null;
	private Boolean includeOpzioni = null;
	private Boolean longDescription = null;
	private Boolean includeSent = null;
	private Boolean createChangeEvent = null;
	private Boolean showEmptyItem = null;
	
	public MaterialiProgrammazioneSelect() {
		super(AppConstants.NEW_ITEM_ID);
	}
	
	public MaterialiProgrammazioneSelect(Integer selectedId, Integer idPeriodico, 
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
	protected void drawListBox(List<MaterialiProgrammazione> entityList) {
		this.clear();
		//this.setVisibleItemCount(1);
		if (showEmptyItem) {
			this.addItem(AppConstants.SELECT_EMPTY_LABEL, AppConstants.SELECT_EMPTY_VALUE_STRING);
		}
		//Disegna la lista dei fascicoli selezionando quello con selectedId
		for (MaterialiProgrammazione mp:entityList) {
			String fasDesc = mp.getPeriodico().getUid()+" ";
			if (mp.getOpzione() == null) {
				fasDesc += "fascicolo "+mp.getMateriale().getTitolo();
			} else {
				//Se opzione
				fasDesc += "opz. ["+mp.getOpzione().getUid()+"] "+mp.getOpzione().getNome();
				if (mp.getMateriale().getTitolo() != null) fasDesc += " "+mp.getMateriale().getTitolo();
			}
			fasDesc += " - "+mp.getMateriale().getSottotitolo()+" "+
					ClientConstants.FORMAT_YEAR.format(mp.getDataNominale());
			if ((mp.getDataNominale() != null) && longDescription) {
				fasDesc += " (estr."+
						ClientConstants.FORMAT_DAY.format(mp.getDataNominale())+")";
			}
			//String fasName = fas.getTitoloNumero() +" ("+ ClientConstants.FORMAT_DAY.format(fas.getDataInizio())+")";
			Integer mpId = mp.getId();
			boolean toSend = false;
			if (mp.getDataEstrazione() == null) {
				toSend = true;
			} else {
				if (DateUtil.now().getTime() < mp.getDataEstrazione().getTime()+AppConstants.DAY) {
					toSend = true;
				}
			}
			if (toSend || (!toSend && includeSent))
			this.addItem(fasDesc, mpId.toString());
		}
		showSelectedValue();
		if (createChangeEvent) this.createChangeEvent();
	}
	
	protected void loadEntityList() {
		MaterialiServiceAsync matService = GWT.create(MaterialiService.class);
		AsyncCallback<List<MaterialiProgrammazione>> callback = new AsyncCallback<List<MaterialiProgrammazione>>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addWarning("Non ci sono fascicoli disponibili per questa rivista");
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(List<MaterialiProgrammazione> result) {
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
		matService.findMaterialiProgrammazioneByPeriodico(idPeriodico, selectedId, boxStartDt, boxFinishDt, includeOpzioni, true, 0, Integer.MAX_VALUE, callback);
	}
	
}
