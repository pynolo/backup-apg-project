package it.giunti.apg.client.widgets;

import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.LookupService;
import it.giunti.apg.client.services.LookupServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.model.RinnoviMassivi;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.VerticalPanel;

public class RinnoviMassiviPanel extends VerticalPanel {
	
	private static final LookupServiceAsync lookupService = GWT.create(LookupService.class);
	
	private int idPeriodico;
	private List<RinnoviMassiviRowPanel> rowPanelList = new ArrayList<RinnoviMassiviRowPanel>();
	
	public RinnoviMassiviPanel(Integer idPeriodico) {
		super();
		this.idPeriodico = idPeriodico;
		load();
	}

	private void draw(List<RinnoviMassivi> rmList) {
		this.clear();
		rowPanelList.clear();
		for (RinnoviMassivi rm:rmList) {
			RinnoviMassiviRowPanel row = new RinnoviMassiviRowPanel(rm, idPeriodico, this);
			this.add(row);
			rowPanelList.add(row);
			applyRowStyles(rowPanelList.size(), row);
		}
	}
	
	public void deleteRow(RinnoviMassiviRowPanel row) {
		this.remove(row);
		rowPanelList.remove(row);
		delete(row.getRinnovoMassivo().getId());
	}
	
	public void addNewRow() {
		RinnoviMassiviRowPanel row = new RinnoviMassiviRowPanel(null, idPeriodico, this);
		this.add(row);
		rowPanelList.add(row);
		applyRowStyles(rowPanelList.size(), row);
	}
	
	private void applyRowStyles(int rowNum, RinnoviMassiviRowPanel row) {
		if ((rowNum % 2) != 0) {
			row.setStyleName("apg-row-odd");
		} else {
			row.setStyleName("apg-row-even");
		}
	}
	
	private void load() {
		AsyncCallback<List<RinnoviMassivi>> callback = new AsyncCallback<List<RinnoviMassivi>>() {
			@Override
			public void onFailure(Throwable caught) {
				WaitSingleton.get().stop();
				if (caught instanceof EmptyResultException) {
					UiSingleton.get().addWarning("Per il periodico scelto non sono previsti rinnovi massivi");
				} else {
					UiSingleton.get().addError(caught);
				}
			}

			@Override
			public void onSuccess(List<RinnoviMassivi> result) {
				WaitSingleton.get().stop();
				draw(result);
			}
		};
		WaitSingleton.get().start();
		lookupService.findRinnoviMassivi(idPeriodico, callback);
	}
	
	public void save() {
		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				WaitSingleton.get().stop();
				UiSingleton.get().addError(caught);
			}

			@Override
			public void onSuccess(Boolean result) {
				WaitSingleton.get().stop();
				UiSingleton.get().addInfo(AppConstants.MSG_SAVE_OK);
				load();
			}
		};
		List<RinnoviMassivi> rinnoviMassiviList = new ArrayList<RinnoviMassivi>();
		for(RinnoviMassiviRowPanel row:rowPanelList) {
			rinnoviMassiviList.add(row.getRinnovoMassivo());
		}
		WaitSingleton.get().start();
		lookupService.saveOrUpdateRinnoviMassiviList(rinnoviMassiviList, callback);
	}
	
	public void delete(Integer idRinnovoMassivo) {
		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				WaitSingleton.get().stop();
				UiSingleton.get().addError(caught);
			}

			@Override
			public void onSuccess(Boolean result) {
				WaitSingleton.get().stop();
			}
		};
		if (idRinnovoMassivo != null) {
			WaitSingleton.get().start();
			lookupService.deleteRinnovoMassivo(idRinnovoMassivo, callback);
		}
	}
}