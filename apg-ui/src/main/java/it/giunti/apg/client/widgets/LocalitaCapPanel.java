package it.giunti.apg.client.widgets;

import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.services.AnagraficheService;
import it.giunti.apg.client.services.AnagraficheServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Localita;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;

public class LocalitaCapPanel extends HorizontalPanel {

	private final AnagraficheServiceAsync anagraficheService = GWT.create(AnagraficheService.class);
	
	private static final String BOX_WIDTH = "20em";
	private SuggestBox locSuggest = null;
	private MultiWordSuggestOracle locOracle = null;
	private SuggestBox.DefaultSuggestionDisplay locSuggDisplay = null;
	
	private String localitaName;
	private String localitaProv;
	private String localitaCap;
	private String idNazione;
	
	public LocalitaCapPanel(String localitaName, String localitaProv, String localitaCap) {
		if (localitaProv == null) localitaProv = "";
		this.localitaName=localitaName;
		this.localitaProv=localitaProv;
		this.localitaCap=localitaCap;
		this.idNazione=AppConstants.DEFAULT_ID_NAZIONE_ITALIA;
		draw();
		verifyStoredLocalita();
	}
	
	public String getLocalitaName() {
		storeLocalitaFromValue();
		return localitaName;
	}

	public String getLocalitaProv() {
		storeLocalitaFromValue();
		if (localitaProv.length() < 2) {
			return null;
		} else {
			return localitaProv;
		}
	}

	public String getLocalitaCap() {
		storeLocalitaFromValue();
		return localitaCap;
	}

	public void setEnabled(boolean enabled) {
		locSuggest.setEnabled(enabled);
	}
	
	public void setIdNazione(String idNazione) {
		this.idNazione=idNazione;
	}
	
	private void draw() {
		locOracle = new MultiWordSuggestOracle();
		locSuggDisplay = new SuggestBox.DefaultSuggestionDisplay();
		locSuggest = new SuggestBox(locOracle, new TextBox(), locSuggDisplay);
		locSuggest.setWidth(BOX_WIDTH);
		locSuggest.setValue(valueFromLocalita(localitaName, localitaProv, localitaCap));
		locSuggest.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent arg0) {
				loadCapSuggestions();
			}
		});
		//locSuggest.addValueChangeHandler(new ValueChangeHandler<String>() {
		//	@Override
		//	public void onValueChange(ValueChangeEvent<String> event) {
		//		storeLocalitaFromValue(event.getValue());
		//		verifyStoredLocalita();
		//	}
		//});
		this.add(locSuggest);
	}
	
	private String valueFromLocalita(String localitaName, String localitaProv, String localitaCap) {
		String s = "";
		if (localitaName != null) s = localitaName;
		if (isItalia()) {
			if (localitaProv == null) localitaProv = "";
			if (localitaCap == null) localitaCap = "";
			if (localitaProv.length() > 0) {
				s += " ("+localitaProv+") "+localitaCap;
			} else {
				if (localitaCap.length() > 0) s += " () "+localitaCap;
			}
		}
		return s;
	}
	
	private void storeLocalitaFromValue() {
		String value = locSuggest.getValue();
		if (isItalia()) {
			int beginProv = value.indexOf("(");
			int endProv = value.indexOf(")");
			if (beginProv > 0) {
				this.localitaName = value.substring(0, beginProv).trim();
			} else {
				this.localitaName = value.trim();
			}
			if (endProv >= 0) {
				this.localitaCap = value.substring(endProv+1).trim();
				this.localitaProv = "";
				if (beginProv > 0) {
					this.localitaProv = value.substring(beginProv+1,endProv).trim();
				}
			} else {
				this.localitaCap = "";
				this.localitaProv = "";
				if (beginProv > 0) {
					this.localitaProv = value.substring(beginProv+1).trim();
				}
			}
		} else {
			this.localitaName = value.trim();
			this.localitaProv = "";
			this.localitaCap = "";
		}
	}
	
	private List<String> suggestionsFromLocalita(List<Localita> localitaList) {
		List<String> list = new ArrayList<String>();
		if (isItalia()) {
			for (Localita l:localitaList) {
				String s = valueFromLocalita(l.getNome(), l.getIdProvincia(), l.getCap());
				list.add(s);
			}
		}
		return list;
	}

	public void verifyStoredLocalita() {
		if (isItalia() && !isEmpty()) {
			AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
				@Override
				public void onFailure(Throwable caught) {
					UiSingleton.get().addInfo(caught.getLocalizedMessage());
				}
				@Override
				public void onSuccess(Boolean result) {
					if (result) {
						locSuggest.removeStyleName("label-warn");
					} else {
						locSuggest.addStyleName("label-warn");
					}
				}
			};
			anagraficheService.verifyLocalitaItalia(localitaName, localitaProv, localitaCap, callback);
		}
	}
	
	private boolean isItalia() {
		if (idNazione.equals(AppConstants.DEFAULT_ID_NAZIONE_ITALIA)) {
			return true;
		} else return false;
	}
	
	public boolean isEmpty() {
		Boolean full = false;
		storeLocalitaFromValue();
		if (isItalia()) {
			full = (localitaName.length() > 1) && (localitaProv.length() == 2) && (localitaCap.length() == 5);
		} else {
			full = (localitaName.length() > 2);
		}
		return !full;
	}
	
	
	//Async methods
	
	
	private void loadCapSuggestions() {
		storeLocalitaFromValue();
		if (localitaName == null) return;
		if (localitaName.length() < 3) {
			locOracle.clear();
			locSuggDisplay.hideSuggestions();
		} else {
			AsyncCallback<List<Localita>> callback = new AsyncCallback<List<Localita>>() {
				@Override
				public void onFailure(Throwable caught) {
					UiSingleton.get().addInfo(caught.getLocalizedMessage());
				}
				@Override
				public void onSuccess(List<Localita> result) {
					locOracle.clear();
					List<String> suggestionStrings = suggestionsFromLocalita(result);
					locOracle.addAll(suggestionStrings);
					if (!locSuggDisplay.isSuggestionListShowing()) {
						locSuggest.showSuggestionList();
					}
				}
			};
			anagraficheService.findLocalitaCapSuggestions(localitaName, localitaProv, localitaCap, callback);
		}
	}
	
}
