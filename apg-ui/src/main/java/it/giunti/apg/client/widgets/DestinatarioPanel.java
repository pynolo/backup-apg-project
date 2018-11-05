package it.giunti.apg.client.widgets;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.UriManager;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.services.AnagraficheService;
import it.giunti.apg.client.services.AnagraficheServiceAsync;
import it.giunti.apg.shared.AppConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.SimplePanel;

public class DestinatarioPanel extends SimplePanel {
	private static final AnagraficheServiceAsync anagraficheService = GWT.create(AnagraficheService.class);
	private Integer idAnagrafica = null;
	private boolean isHyperlink = true;
	
	public DestinatarioPanel(Integer idAnagrafica, boolean isHyperlink) {
		super();
		this.idAnagrafica = idAnagrafica;
		this.isHyperlink = isHyperlink;
		add(new InlineHTML(ClientConstants.LABEL_LOADING));
		load();
	}
	
	private void load() {
		AsyncCallback<String> callback = new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
			}
			@Override
			public void onSuccess(String result) {
				clear();
				if (isHyperlink) {
					UriParameters params = new UriParameters();
					params.add(AppConstants.PARAM_ID, idAnagrafica);
					Hyperlink rowLink = params.getHyperlink(result, UriManager.ANAGRAFICHE_MERGE);
					add(rowLink);
				} else {
					InlineHTML label = new InlineHTML(result);
					add(label);
				}
			}
		};
		anagraficheService.findDescriptionById(idAnagrafica, callback);
	}
}
