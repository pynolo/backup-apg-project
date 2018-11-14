package it.giunti.apg.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.UriManager;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.services.AnagraficheService;
import it.giunti.apg.client.services.AnagraficheServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Anagrafiche;

public class AnagraficaLink extends HorizontalPanel {

	private final AnagraficheServiceAsync anagraficheService = GWT.create(AnagraficheService.class);
	
	private Integer idAnagrafica;
	private boolean conStradario;
	
	public AnagraficaLink(Integer idAnagrafica, boolean conStradario) {
		this.idAnagrafica=idAnagrafica;
		this.conStradario=conStradario;
		loadAnagrafica();
	}
	
	private void draw(Anagrafiche anag) {
		//Nome
		String linkText = anag.getIndirizzoPrincipale().getCognomeRagioneSociale();
		if (anag.getIndirizzoPrincipale().getNome() != null) {
			linkText += " " + anag.getIndirizzoPrincipale().getNome();
		}
		linkText = "<b>"+linkText+"</b> - ";
		//Indirizzo
		if (conStradario)
			linkText += anag.getIndirizzoPrincipale().getIndirizzo() + " ";
		if (anag.getIndirizzoPrincipale().getCap() != null)
			linkText += anag.getIndirizzoPrincipale().getCap()+ " ";
		if (anag.getIndirizzoPrincipale().getLocalita() != null)
			linkText += anag.getIndirizzoPrincipale().getLocalita()+ " ";
		if (anag.getIndirizzoPrincipale().getProvincia() != null)
			linkText += "("+anag.getIndirizzoPrincipale().getProvincia()+") ";
		linkText += "<b>["+anag.getUid()+"]</b> ";
		//Link
		UriParameters params = new UriParameters();
		params.add(AppConstants.PARAM_ID, anag.getId());
		Hyperlink rowLink = null;
		if (anag.getNecessitaVerifica() || (anag.getIdAnagraficaDaAggiornare() != null)) {
			rowLink = params.getHyperlink(ClientConstants.ICON_HAND_RIGHT+" "+linkText, UriManager.ANAGRAFICHE_MERGE);
		} else {
			rowLink = params.getHyperlink(linkText, UriManager.ANAGRAFICHE_MERGE);
		}
		this.add(rowLink);
	}
	
	public void loadAnagrafica() {
		AsyncCallback<Anagrafiche> callback = new AsyncCallback<Anagrafiche>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addInfo(caught.getLocalizedMessage());
			}
			@Override
			public void onSuccess(Anagrafiche result) {
				draw(result);
			}
		};
		anagraficheService.findById(idAnagrafica, callback);
	}
	
}
