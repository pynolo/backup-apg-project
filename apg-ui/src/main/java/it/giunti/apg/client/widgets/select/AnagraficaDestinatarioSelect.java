package it.giunti.apg.client.widgets.select;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.services.AbbonamentiService;
import it.giunti.apg.client.services.AbbonamentiServiceAsync;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;

public class AnagraficaDestinatarioSelect extends Select {
	
	private final AbbonamentiServiceAsync abbonamentiService = GWT.create(AbbonamentiService.class);
	
	private Integer idAbbonamento = null;
	private IstanzeAbbonamenti ia = null;
	
	public AnagraficaDestinatarioSelect(Integer selectedId, Integer idAbbonamento) {
		super(selectedId);
		loadIstanza();
	}
	
	private void draw() {
		this.clear();
		if (ia != null) {
			this.addItem("[Beneficiario] "+ia.getAbbonato().getIndirizzoPrincipale().getCognomeRagioneSociale()+
					" "+ia.getAbbonato().getIndirizzoPrincipale().getNome(), ia.getAbbonato().getId()+"");
			if (ia.getPagante() != null) {
				this.addItem("[Pagante] "+ia.getPagante().getIndirizzoPrincipale().getCognomeRagioneSociale()+
						" "+ia.getPagante().getIndirizzoPrincipale().getNome(), ia.getPagante().getId()+"");
			}
			if (ia.getPromotore() != null) {
				this.addItem("[Promotore] "+ia.getPromotore().getIndirizzoPrincipale().getCognomeRagioneSociale()+
						" "+ia.getPromotore().getIndirizzoPrincipale().getNome(), ia.getPromotore().getId()+"");
			}
		}
		showSelectedValue();
	}
	
	private void loadIstanza() {
		AsyncCallback<IstanzeAbbonamenti> callback = new AsyncCallback<IstanzeAbbonamenti>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
			}
			@Override
			public void onSuccess(IstanzeAbbonamenti result) {
				ia = result;
				draw();
			}
		};
		//Load istanza
		abbonamentiService.findLastIstanzaByAbbonamento(idAbbonamento, callback);
	}
}
