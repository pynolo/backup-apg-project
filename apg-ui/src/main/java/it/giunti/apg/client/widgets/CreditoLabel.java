package it.giunti.apg.client.widgets;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.services.PagamentiService;
import it.giunti.apg.client.services.PagamentiServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.InlineHTML;

public class CreditoLabel extends InlineHTML {

	private final PagamentiServiceAsync pagamentiService = GWT.create(PagamentiService.class);
	
	private Integer idPagante = null;
	private String idSocieta = null;
	
	public CreditoLabel(Integer idPagante, String idSocieta) {
		setStyleName("message-info");
		setVisible(false);
		this.idPagante = idPagante;
		this.idSocieta = idSocieta;
		loadCredito();
	}
		
	private void loadCredito() { 
		AsyncCallback<Double> callback = new AsyncCallback<Double>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
			}
			@Override
			public void onSuccess(Double credito) {
				setVisible(false);
				if (credito != null) {
					if (credito > 0d) {
						setHTML("&euro;"+ClientConstants.FORMAT_CURRENCY.format(credito));
						setVisible(true);
						setTitle("Credito: "+ClientConstants.FORMAT_CURRENCY.format(credito));
					}
				}
			}
		};
		pagamentiService.getCreditoByAnagraficaSocieta(idPagante, idSocieta, null, false, callback);
	}
	
}
