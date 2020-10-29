package it.giunti.apg.client.widgets.select;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.services.AbbonamentiService;
import it.giunti.apg.client.services.AbbonamentiServiceAsync;
import it.giunti.apg.client.services.AnagraficheService;
import it.giunti.apg.client.services.AnagraficheServiceAsync;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;

public class AnagraficaDestinatarioSelect extends Select {
	
	private final AbbonamentiServiceAsync abbonamentiService = GWT.create(AbbonamentiService.class);
	private final AnagraficheServiceAsync anagraficheService = GWT.create(AnagraficheService.class);
	
	private Integer idAbbonamento = null;
	private Integer idAnagrafica = null;
	private IstanzeAbbonamenti ia = null;
	
	public AnagraficaDestinatarioSelect(Integer selectedId, Integer idAbbonamento) {
		super(selectedId);
		this.idAnagrafica = selectedId;
		this.idAbbonamento = idAbbonamento;
		if (idAbbonamento != null) {
			loadIstanza();
		} else {
			loadAnagrafica();
		}
	}
	
	private void drawAnagraficheByAbbonamento() {
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
	
	private void drawAnagrafica(Anagrafiche anag) {
		this.clear();
		this.addItem(anag.getIndirizzoPrincipale().getCognomeRagioneSociale()+
					" "+anag.getIndirizzoPrincipale().getNome(), anag.getId()+"");
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
				drawAnagraficheByAbbonamento();
			}
		};
		//Load istanza
		abbonamentiService.findLastIstanzaByAbbonamento(idAbbonamento, callback);
	}
	
	private void loadAnagrafica() {
		AsyncCallback<Anagrafiche> callback = new AsyncCallback<Anagrafiche>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
			}
			@Override
			public void onSuccess(Anagrafiche result) {
				drawAnagrafica(result);
			}
		};
		//Load istanza
		anagraficheService.findById(idAnagrafica, callback);
	}
}
