package it.giunti.apg.client.widgets;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.services.PagamentiService;
import it.giunti.apg.client.services.PagamentiServiceAsync;
import it.giunti.apg.shared.IstanzeStatusUtil;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PagatoCheckBox extends FlowPanel implements IRefreshable {

	private final PagamentiServiceAsync pagamentiService = GWT.create(PagamentiService.class);
	
	private static final String PAGAMENTO_NON_NECESSARIO_LABEL = "<i>(pagamento non necessario)</i>";
	private static final String PAGATO = "pagato";
	private static final String NON_PAGATO = "non&nbsp;pagato";
		
	//private final CheckBox pagatoCheck = new CheckBox();
	private final InlineHTML pagatoLabel = new InlineHTML();
	private final InlineHTML mancanteLabel = new InlineHTML();
	private final InlineHTML dovutoLabel = new InlineHTML();
	private final InlineHTML creditoLabel = new InlineHTML();
	private IstanzeAbbonamenti istanza = null;
	private Integer idPagante = null;
	private String idSocieta = null;
	//private Boolean persistedPagatoValue = Boolean.FALSE;
	
	public PagatoCheckBox(IstanzeAbbonamenti ia, Integer idPagante, String idSocieta) {
		VerticalPanel vertBox = new VerticalPanel();
		HorizontalPanel descBox = new HorizontalPanel();
		descBox.add(pagatoLabel);
		descBox.setStyleName("compact-table");
		mancanteLabel.setStyleName("pagato-box");
		mancanteLabel.setVisible(false);
		descBox.add(mancanteLabel);
		descBox.setStyleName("compact-table");
		dovutoLabel.setStyleName("pagato-box");
		dovutoLabel.setVisible(false);
		descBox.add(dovutoLabel);
		descBox.setStyleName("compact-table");
		vertBox.add(descBox);
		vertBox.setStyleName("compact-table");
		creditoLabel.setStyleName("label-apg-info");
		creditoLabel.setVisible(false);
		vertBox.add(creditoLabel);
		vertBox.setStyleName("compact-table");
		this.add(vertBox);
		this.istanza = ia;
		this.idPagante = idPagante;
		this.idSocieta = idSocieta;
		refresh();
	}
	
	public void refresh() {
		if (istanza != null) {
			if (istanza.getId() != null) {
				boolean visible = !IstanzeStatusUtil.isFatturatoOppureOmaggio(istanza);
				refreshPagato(visible);
				refreshImportoMancante(visible);
				refreshImportoTotale(visible);
				refreshCredito();
			}
		}
	}
	
	private void refreshImportoMancante(boolean visible) { 
		AsyncCallback<Double> callback = new AsyncCallback<Double>() {
			@Override
			public void onFailure(Throwable caught) {
				if (!(caught instanceof ValidationException)) {
					UiSingleton.get().addError(caught);
				} else { }
			}
			@Override
			public void onSuccess(Double result) {
				if (result != null) {
					if (result > 0D) {
						String labelHtml = "<b>dovuto&nbsp;&euro;";
						labelHtml += ClientConstants.FORMAT_CURRENCY.format(result);
						labelHtml += "</b>";
						mancanteLabel.setHTML(labelHtml);
						mancanteLabel.setVisible(true);
					} else {
						mancanteLabel.setVisible(false);
					}
				}
			}
		};
		if (visible) {
			pagamentiService.getImportoMancante(istanza.getId(), callback);
		}
		mancanteLabel.setVisible(visible);
	}

	private void refreshImportoTotale(boolean visible) { 
		AsyncCallback<Double> callback = new AsyncCallback<Double>() {
			@Override
			public void onFailure(Throwable caught) {
				if (!(caught instanceof ValidationException)) {
					UiSingleton.get().addError(caught);
				} else {
					dovutoLabel.setVisible(true);
					dovutoLabel.setHTML("&nbsp;"+PAGAMENTO_NON_NECESSARIO_LABEL);
				}
			}
			@Override
			public void onSuccess(Double result) {
				String labelHtml = "&nbsp;da&nbsp;listino&nbsp;&euro;";
				if (result == null) {
					labelHtml += "0";
				} else {
					labelHtml += ClientConstants.FORMAT_CURRENCY.format(result);
				}
				dovutoLabel.setVisible(true);
				dovutoLabel.setHTML(labelHtml);
			}
		};
		if (visible) {
			pagamentiService.getImportoTotale(istanza.getId(), callback);
		}
		dovutoLabel.setVisible(visible);
	}
	
	private void refreshPagato(boolean visible) { 
		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
			}
			@Override
			public void onSuccess(Boolean result) {
				if (result) {
					pagatoLabel.setHTML(PAGATO);
					pagatoLabel.setStyleName("label-apg-warn");
				} else {
					pagatoLabel.setHTML(NON_PAGATO);
					pagatoLabel.setStyleName("label-apg-error");
				}
			}
		};
		if (visible) {
			pagamentiService.isPagato(istanza.getId(), callback);
		}
		pagatoLabel.setVisible(visible);
	}
	
	private void refreshCredito() { 
		AsyncCallback<Double> callback = new AsyncCallback<Double>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
			}
			@Override
			public void onSuccess(Double credito) {
				creditoLabel.setVisible(false);
				if (credito != null) {
					if (credito > 0d) {
						creditoLabel.setHTML(ClientConstants.ICON_IMPORTANT+
								"Credito residuo "+idSocieta+": &euro;"+
								ClientConstants.FORMAT_CURRENCY.format(credito));
						creditoLabel.setVisible(true);
					}
				}
			}
		};
		pagamentiService.getCreditoByAnagraficaSocieta(idPagante, idSocieta, null, false, callback);
	}
	
//	private void clickPagatoCheck() {
//		String msg = "Attenzione: il valore di 'Pagato' va cambiato solamente se si e' consapevoli " +
//				"delle conseguenze sull'invio dei numeri, opzioni e bollettini.\r\n" +
//				"Vuoi davvero cambiare il valore?\r\n" +
//				"(Scegli 'Annulla' per recuperare l'ultimo valore salvato)";
//		boolean confirm = Window.confirm(msg);
//		if (!confirm) {
//			pagatoCheck.setValue(persistedPagatoValue);
//		}
//	}
}
