package it.giunti.apg.client.widgets;

import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.services.TipiAbbService;
import it.giunti.apg.client.services.TipiAbbServiceAsync;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;

public class TipiAbbonamentoRinnovoLabel extends HTML {

	public TipiAbbonamentoRinnovoLabel(Integer idListino) {
		super();
		TipiAbbServiceAsync tipiAbbService = GWT.create(TipiAbbService.class);
		AsyncCallback<List<String>> callback = new AsyncCallback<List<String>>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
			}
			@Override
			public void onSuccess(List<String> result) {
				draw(result);
			}
		};
		tipiAbbService.findCodiceTipiAbbonamentoRinnovoByListino(idListino, callback);
	}
	
	private void draw(List<String> codici) {
		String result = "";
		for (String codice:codici) {
			if (result.length() != 0) result += ", ";
			result += codice;
		}
		this.setHTML("<b>"+result+"</b>");
	}
}
