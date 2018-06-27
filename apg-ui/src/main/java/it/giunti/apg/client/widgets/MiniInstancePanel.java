package it.giunti.apg.client.widgets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private boolean soloNonBloccati = false;
	
	public MiniInstancePanel(Integer idAnagrafica, boolean soloPagate, boolean soloScadute, boolean soloNonBloccati) {
		this.idAnagrafica = idAnagrafica;
		this.idSocieta = null;
		this.soloPagate = soloPagate;
		this.soloScadute = soloScadute;
		this.soloNonBloccati = soloNonBloccati;
		loadLastInstances();
	}
	
	public MiniInstancePanel(Integer idAnagrafica, String idSocieta, boolean soloPagate, boolean soloScadute, boolean soloNonBloccati) {
		this.idAnagrafica = idAnagrafica;
		this.idSocieta = idSocieta;
		this.soloPagate = soloPagate;
		this.soloScadute = soloScadute;
		this.soloNonBloccati = soloNonBloccati;
		loadLastInstances();
	}
	
	private void draw(List<IstanzeAbbonamenti> iaList) {
		//Filtro: solo le istanze con ID maggiore
		Map<String, IstanzeAbbonamenti> iaMap = new HashMap<String, IstanzeAbbonamenti>();
		for (IstanzeAbbonamenti ia:iaList) {
			if (!soloNonBloccati || !ia.getInvioBloccato()) {
				IstanzeAbbonamenti found = iaMap.get(ia.getFascicoloInizio().getPeriodico().getUid());
				if (found != null) {
					if (ia.getId() > found.getId()) 
						iaMap.put(ia.getFascicoloInizio().getPeriodico().getUid(), ia);
				} else {
					iaMap.put(ia.getFascicoloInizio().getPeriodico().getUid(), ia);
				}
			}
		}
		//Stampa
		for (IstanzeAbbonamenti ia:iaMap.values()) {
			if (ia.getFascicoloInizio().getPeriodico().getDataFine() == null) {
				MiniInstanceLabel mil = new MiniInstanceLabel(ia, true);
				this.add(mil);
			}
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
