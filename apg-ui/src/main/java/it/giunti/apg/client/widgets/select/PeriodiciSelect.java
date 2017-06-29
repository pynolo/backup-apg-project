package it.giunti.apg.client.widgets.select;

import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.LookupService;
import it.giunti.apg.client.services.LookupServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Periodici;
import it.giunti.apg.shared.model.Utenti;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class PeriodiciSelect extends EntitySelect<Periodici> {

	private Integer selectedId = null;
	private Date extractionDt = null;
	private Boolean includeEmptyItem = null;
	private Boolean createChangeEvent = null;
	private Utenti user = null;

	public PeriodiciSelect(Integer selectedId, Date extractionDt,
			boolean includeEmptyItem, boolean createChangeEvent, Utenti user) {
		super(selectedId);
		this.selectedId = selectedId;
		this.includeEmptyItem = includeEmptyItem;
		this.user = user;
		reload(selectedId, extractionDt, createChangeEvent);
	}
	
	public void reload(Integer selectedId, Date extractionDt,
			boolean createChangeEvent) {
		this.setSelectedId(selectedId);
		this.selectedId = selectedId;
		this.extractionDt = extractionDt;
		this.createChangeEvent = createChangeEvent;
		loadEntityList();
	}
	
	@Override
	protected void drawListBox(List<Periodici> periodici) {
		this.clear();
		String restriction = "";
		if (user != null) 
			if (user.getPeriodiciUidRestriction() != null) 
				restriction = user.getPeriodiciUidRestriction();
		if (includeEmptyItem) this.addItem(AppConstants.SELECT_EMPTY_LABEL, AppConstants.SELECT_EMPTY_VALUE_STRING);
		for (Periodici p:periodici) {
			boolean show = true;
			if (restriction.length() > 0) show = restriction.contains(p.getUid());
			if (show && extractionDt.after(p.getDataInizio()) && 
					((p.getDataFine() == null) || extractionDt.before(p.getDataFine())) ) {
				String label = p.getNome() + " ["+p.getUid()+"]";
				this.addItem(label, p.getId()+"");
			}
		}
		showSelectedValue();
		if (createChangeEvent) this.createChangeEvent();
	}
	
	@Override
	protected void loadEntityList() {
		LookupServiceAsync lookupService = GWT.create(LookupService.class);
		AsyncCallback<List<Periodici>> callback = new AsyncCallback<List<Periodici>>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(List<Periodici> result) {
				setEntityList(result);
				drawListBox(result);
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		lookupService.findPeriodici(selectedId, extractionDt, callback);
	}

}
