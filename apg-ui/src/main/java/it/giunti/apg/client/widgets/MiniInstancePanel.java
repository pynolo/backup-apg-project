package it.giunti.apg.client.widgets;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.services.AbbonamentiService;
import it.giunti.apg.client.services.AbbonamentiServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;

public class MiniInstancePanel extends FlowPanel {
	
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
			////Filtro: solo le istanze con ID maggiore
			//Map<String, IstanzeAbbonamenti> iaMap = new HashMap<String, IstanzeAbbonamenti>();
			//for (IstanzeAbbonamenti ia:iaList) {
			//	if (!soloNonBloccati || !ia.getInvioBloccato()) {
			//		IstanzeAbbonamenti found = iaMap.get(ia.getFascicoloInizio().getPeriodico().getUid());
			//		if (found != null) {
			//			if (ia.getId() > found.getId()) 
			//				iaMap.put(ia.getFascicoloInizio().getPeriodico().getUid(), ia);
			//		} else {
			//			iaMap.put(ia.getFascicoloInizio().getPeriodico().getUid(), ia);
			//		}
			//	}
			//}
		//Stampa
		Long now = DateUtil.now().getTime();
		for (IstanzeAbbonamenti ia:iaList) {
			Long start = ia.getFascicoloInizio().getDataInizio().getTime();
			if (now-start < AppConstants.YEAR*ClientConstants.INSTANCE_SHOW_YEARS) {//Mostra solo ultimi anni
				if (ia.getFascicoloInizio().getPeriodico().getDataFine() == null) {
					if (!soloNonBloccati || !ia.getInvioBloccato()) {
						MiniInstanceLabel mil = new MiniInstanceLabel(ia, true);
						this.add(mil);
					}
				}
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
		abbonamentiService.findLastIstanzeByAnagraficaSocieta(idAnagrafica, idSocieta, soloPagate, soloScadute, callback);
	}
}
