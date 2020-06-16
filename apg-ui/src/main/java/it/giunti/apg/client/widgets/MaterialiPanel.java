package it.giunti.apg.client.widgets;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBox;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.services.MaterialiService;
import it.giunti.apg.client.services.MaterialiServiceAsync;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.model.Materiali;

public class MaterialiPanel extends HorizontalPanel {

	private final MaterialiServiceAsync matService = GWT.create(MaterialiService.class);
	
	private static final String BOX_WIDTH = "8em";
	private SuggestBox matSuggest = null;
	private MultiWordSuggestOracle matOracle = null;
	private SuggestBox.DefaultSuggestionDisplay matSuggDisplay = null;
	private InlineHTML descrHtml = null;
	
	private Integer idMateriale;
	private Integer pageSize;
	private boolean isEnabled;
	private Materiali item;
	private String cm = "";
	private boolean cmFound = false;
	
	public MaterialiPanel(Integer idMat, int pageSize, boolean isEnabled) {
		this.idMateriale = idMat;
		this.pageSize = pageSize;
		this.isEnabled = isEnabled;
		if (idMat != null) {
			if (idMat > 0) {
				loadMateriale();
			} else {
				draw();
			}
		} else {
			draw();
		}
	}
	
	public String getCodiceMeccanografico() throws ValidationException {
		cm = null;
		if (matSuggest != null) {
			if (matSuggest.getValue() != null) {
				cm = matSuggest.getValue();
				if (cm.length() == 6) {
					if (cmFound) {
						return cm;
					}
				}
			}
		}
		throw new ValidationException("Il codice meccanografico non e' valido");
	}

	private void draw() {
		if (isEnabled) {
			matOracle = new MultiWordSuggestOracle();
			matSuggDisplay = new SuggestBox.DefaultSuggestionDisplay();
			matSuggest = new SuggestBox(matOracle, new TextBox(), matSuggDisplay);
			matSuggest.setWidth(BOX_WIDTH);
			matSuggest.setValue(item.getCodiceMeccanografico());
			matSuggest.addKeyUpHandler(new KeyUpHandler() {
				@Override
				public void onKeyUp(KeyUpEvent event) {
					loadMatSuggestions();
					updateDescription();
				}
			});
			matSuggest.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
				@Override
				public void onSelection(SelectionEvent<Suggestion> event) {
					updateDescription();
				}
			});
			this.add(matSuggest);
		} else {
			this.add(new InlineHTML("<b>"+item.getCodiceMeccanografico()+"</b>"));
		}
		descrHtml = new InlineHTML();
		this.add(descrHtml);
		updateDescription();
	}

//	public void verifyStoredLocalita() {
//		if (isItalia() && !isEmpty()) {
//			AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
//				@Override
//				public void onFailure(Throwable caught) {
//					UiSingleton.get().addInfo(caught.getLocalizedMessage());
//				}
//				@Override
//				public void onSuccess(Boolean result) {
//					if (result) {
//						locSuggest.removeStyleName("label-warn");
//					} else {
//						locSuggest.addStyleName("label-warn");
//					}
//				}
//			};
//			anagraficheService.verifyLocalitaItalia(localitaName, localitaProv, localitaCap, callback);
//		}
//	}
	
	
	//Async methods
	
	
	private void loadMatSuggestions() {
		cm = matSuggest.getValue();
		if (this.cm.length() > 1) {
			AsyncCallback<List<Materiali>> callback = new AsyncCallback<List<Materiali>>() {
				@Override
				public void onFailure(Throwable caught) {
					UiSingleton.get().addInfo(caught.getMessage());
				}
				@Override
				public void onSuccess(List<Materiali> result) {
					matOracle.clear();
					for (Materiali mat:result) 
						matOracle.add(mat.getCodiceMeccanografico());
					if (!matSuggDisplay.isSuggestionListShowing()) {
						matSuggest.showSuggestionList();
					}
				}
			};
			matService.findSuggestionsByCodiceMeccanografico(this.cm, this.pageSize, callback);
		} else {
			matOracle.clear();
			matSuggDisplay.hideSuggestions();
		}
	}
	
	private void loadMateriale() {
		AsyncCallback<Materiali> callback = new AsyncCallback<Materiali>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addInfo(caught.getMessage());
			}
			@Override
			public void onSuccess(Materiali result) {
				item = result;
				cm = result.getCodiceMeccanografico();
				draw();
			}
		};
		matService.findMaterialeById(idMateriale, callback);
	}
	
	private void updateDescription() {
		cmFound = false;
		descrHtml.setHTML("");
		String searchString = matSuggest.getValue();
		AsyncCallback<List<Materiali>> callback = new AsyncCallback<List<Materiali>>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addInfo(caught.getMessage());
			}
			@Override
			public void onSuccess(List<Materiali> result) {
				if (result != null) {
					if (result.size() > 0) {
						cmFound = true;
						Materiali mat = result.get(0);
						String s = "";
						if (mat.getTitolo() != null) s += " "+mat.getTitolo();
						if (mat.getSottotitolo() != null) s += " "+mat.getSottotitolo();
						descrHtml.setHTML(s);
					}
				}
			}
		};
		if (searchString != null) {
			if (searchString.length() == 6) {
				descrHtml.setHTML(ClientConstants.ICON_LOADING_SMALL);
				matService.findSuggestionsByCodiceMeccanografico(searchString, 1, callback);
			}
		}
	}
}
