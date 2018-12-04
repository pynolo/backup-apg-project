package it.giunti.apg.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HorizontalPanel;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.frames.FatturaPopUp;
import it.giunti.apg.client.services.PagamentiService;
import it.giunti.apg.client.services.PagamentiServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Fatture;

public class FatturaStampaLink extends HorizontalPanel {

	private static final PagamentiServiceAsync paymentService = GWT.create(PagamentiService.class);
	private Integer idFattura = null;
	private IRefreshable parent = null;
	
	public FatturaStampaLink(Integer idFattura) {
		this.idFattura = idFattura;
		if (idFattura > 0) loadFatturaStampa();
	}
	
	private void draw(Fatture fattura) {
		if (fattura != null) {
			if (fattura.getIdFatturaStampa() != null) {
				Anchor fattAnchor = new Anchor(ClientConstants.ICON_FATTURA_CORRISPETTIVO, true);
				fattAnchor.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent arg0) {
						String servletUrl = AppConstants.URL_APG_AUTOMATION_FATTURA_STAMPA + 
								"?" + AppConstants.PARAM_ID + "=" + idFattura;
						Window.open(servletUrl, "", "");
					}
				});
				this.add(fattAnchor);
			}
			Anchor numeroFatturaLink = new Anchor();
			if (fattura.getIdTipoDocumento().equalsIgnoreCase(AppConstants.DOCUMENTO_FATTURA)) {
				numeroFatturaLink.setHTML("<b>"+fattura.getNumeroFattura()+"</b>");
			} else {
				numeroFatturaLink.setHTML("<i>"+fattura.getNumeroFattura()+"</i>");
			}
			if (fattura.getIdUtente() != null) {
				if (fattura.getIdUtente().length() > 0) numeroFatturaLink.setTitle("Inserita da "+fattura.getIdUtente());
			}
			numeroFatturaLink.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					new FatturaPopUp(idFattura, parent);
				}
			});
			this.add(numeroFatturaLink);
		}
	}
	
	private void loadFatturaStampa() {
		AsyncCallback<Fatture> callback = new AsyncCallback<Fatture>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addInfo("Fatture: "+caught.getMessage());
			}
			@Override
			public void onSuccess(Fatture result) {
				draw(result);
			}
		};
		paymentService.findFatturaById(idFattura, callback);
	}
	
}
