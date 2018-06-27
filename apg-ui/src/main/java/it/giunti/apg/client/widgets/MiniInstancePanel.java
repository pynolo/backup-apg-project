package it.giunti.apg.client.widgets;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;

import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.services.AbbonamentiService;
import it.giunti.apg.client.services.AbbonamentiServiceAsync;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;

public class MiniInstancePanel extends HorizontalPanel {
	
	private final AbbonamentiServiceAsync abbonamentiService = GWT.create(AbbonamentiService.class);
	
	private Integer idAnagrafica = null;
	private String idSocieta = null;
	private boolean soloPagate = false;
	private boolean soloScadute = false;
	
	public MiniInstancePanel(Integer idAnagrafica, boolean soloPagate, boolean soloScadute) {
		this.idAnagrafica = idAnagrafica;
		this.idSocieta = null;
		this.soloPagate = soloPagate;
		this.soloScadute = soloScadute;
		loadLastInstances();
	}
	
	public MiniInstancePanel(Integer idAnagrafica, String idSocieta, boolean soloPagate, boolean soloScadute) {
		this.idAnagrafica = idAnagrafica;
		this.idSocieta = idSocieta;
		this.soloPagate = soloPagate;
		this.soloScadute = soloScadute;
		loadLastInstances();
	}
	
	private void draw(List<IstanzeAbbonamenti> iaList) {
		for (IstanzeAbbonamenti ia:iaList) {
			MiniInstanceLabel mil = new MiniInstanceLabel(ia, true);
			this.add(mil);
		}
	}
	
	private void loadLastInstances() { 
		AsyncCallback<List<IstanzeAbbonamenti>> callback = new AsyncCallback<List<IstanzeAbbonamenti>>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
			}
			@Override
			public void onSuccess(List<IstanzeAbbonamenti> result) {
				draw(result);
			}
		};
		abbonamentiService.findLastIstanzePagateByAnagraficaSocieta(idAnagrafica, idSocieta, soloPagate, soloScadute, callback);
	}
}
