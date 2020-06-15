package it.giunti.apg.client.widgets;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;

import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.services.MaterialiService;
import it.giunti.apg.client.services.MaterialiServiceAsync;
import it.giunti.apg.shared.model.Materiali;

public class MaterialiPanel extends HorizontalPanel {

	private final MaterialiServiceAsync matService = GWT.create(MaterialiService.class);
	
	private static final String BOX_WIDTH = "20em";
	private SuggestBox matSuggest = null;
	private MultiWordSuggestOracle matOracle = null;
	private SuggestBox.DefaultSuggestionDisplay matSuggDisplay = null;
	
	private Integer idMateriale;
	private Integer pageSize;
	private boolean isEnabled;
	private Materiali item;
	private String cm = "";
	
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
	
	public String getCodiceMeccanografico() {
		storeCmFromValue();
		return cm;
	}

	private void draw() {
		if (isEnabled) {
			matOracle = new MultiWordSuggestOracle();
			matSuggDisplay = new SuggestBox.DefaultSuggestionDisplay();
			matSuggest = new SuggestBox(matOracle, new TextBox(), matSuggDisplay);
			matSuggest.setWidth(BOX_WIDTH);
			matSuggest.setValue(getDescriptionFromMateriale(item));
			matSuggest.addKeyUpHandler(new KeyUpHandler() {
				@Override
				public void onKeyUp(KeyUpEvent arg0) {
					loadMatSuggestions();
				}
			});
			this.add(matSuggest);
		} else {
			this.add(new InlineHTML("<b>"+getDescriptionFromMateriale(item)+"</b>"));
		}
	}
	
	private void storeCmFromValue() {
		String value = matSuggest.getValue();
		int endCm = value.indexOf(" ");
		if (endCm >= 0) {
			this.cm = value.substring(0, endCm).trim();
		} else {
			this.cm = value;
		}
	}
	
	private String getDescriptionFromMateriale(Materiali mat) {
		if (mat == null) return "";
		String s = mat.getCodiceMeccanografico();
		if (mat.getTitolo() != null) s += " "+mat.getTitolo();
		if (mat.getSottotitolo() != null) s += " "+mat.getSottotitolo();
		return s;
	}
	
	private List<String> getDescriptionsFromMateriali(List<Materiali> matList) {
		List<String> list = new ArrayList<String>();
		for (Materiali mat:matList) {
			String s = getDescriptionFromMateriale(mat);
			list.add(s);
		}
		return list;
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
	
	public boolean isEmpty() {
		storeCmFromValue();
		return (cm.length() != 6);
	}
	
	
	//Async methods
	
	
	private void loadMatSuggestions() {
		storeCmFromValue();
		if (this.cm.length() > 1) {
			AsyncCallback<List<Materiali>> callback = new AsyncCallback<List<Materiali>>() {
				@Override
				public void onFailure(Throwable caught) {
					UiSingleton.get().addInfo(caught.getMessage());
				}
				@Override
				public void onSuccess(List<Materiali> result) {
					matOracle.clear();
					List<String> suggestionStrings = getDescriptionsFromMateriali(result);
					matOracle.addAll(suggestionStrings);
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
				draw();
			}
		};
		matService.findMaterialeById(idMateriale, callback);
	}
}
