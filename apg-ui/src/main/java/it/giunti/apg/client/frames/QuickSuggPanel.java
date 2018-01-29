package it.giunti.apg.client.frames;

import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.services.AnagraficheService;
import it.giunti.apg.client.services.AnagraficheServiceAsync;
import it.giunti.apg.client.widgets.AnagraficheSuggestionPanel;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Anagrafiche;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

public class QuickSuggPanel extends ScrollPanel {
	
	private static final int MAX_RESULTS = 20;
	private static final String MAX_WIDTH = "24em";
	private static final String MAX_HEIGHT = "29em";
	
	private final AnagraficheServiceAsync anagraficheService = GWT.create(AnagraficheService.class);

	private IRefreshable parent = null;
	private FlowPanel contentPanel = null;
	private List<Anagrafiche> anagList = null;
	private Anagrafiche selectedAnag = null;
	
	public QuickSuggPanel(IRefreshable parent) {
		this.parent = parent;
		this.setSize(MAX_WIDTH, MAX_HEIGHT);
		contentPanel = new FlowPanel();
		this.add(contentPanel);
	}
	
	private void drawSuggestions(boolean suggestionToForm) {
		if (anagList != null) {
			contentPanel.clear();
			for (Anagrafiche anag:anagList) {
				AnagraficheSuggestionPanel asp = new AnagraficheSuggestionPanel(anag, this, suggestionToForm);
				contentPanel.add(asp);
			}
		}
	}
	
	public Anagrafiche getValue() {
		return selectedAnag;
	}
	
	public void onSuggestionClick(Anagrafiche anag) {
		selectedAnag = anag;
		parent.refresh();
	}
	
	public void clearSelection() {
		selectedAnag = null;
		parent.refresh();
	}
	
	public void findSuggestions(String cognome, String nome,
			String presso, String indirizzo, String localita, String cap, boolean suggestionToForm) {
		final boolean fSuggestionToForm=suggestionToForm;
		AsyncCallback<List<Anagrafiche>> callback = new AsyncCallback<List<Anagrafiche>>() {
			@Override
			public void onFailure(Throwable caught) {
				if (caught instanceof BusinessException) {
					UiSingleton.get().addError(caught);
				} else {//instanceof EmptyResultException
					anagList = new ArrayList<Anagrafiche>();
					drawSuggestions(fSuggestionToForm);
				}
			}
			@Override
			public void onSuccess(List<Anagrafiche> result) {
				anagList = result;
				drawSuggestions(fSuggestionToForm);
			}
		};
		cognome += "*";
		if (nome.length() > 0) {
			nome += "*";
		} else {
			nome = null;
		}
		if (presso.length() > 0) {
			presso += "*";
		} else {
			presso = null;
		}
		if (indirizzo.length() > 0) {
			indirizzo += "*";
		} else {
			indirizzo = null;
		}
		if (cap.length() == 0) cap = null;
		if (localita.length() > 0) {
			localita += "*";
		} else {
			localita = null;
		}
		anagraficheService.findByProperties(
				null, cognome, nome, //codAnag, ragSoc, nome,
				presso, indirizzo, cap, localita, //presso, indirizzo, cap, loc,
				null, null, null,//prov, email, cfiva, 
				null, null, null,//idPeriodico, tipoAbb, numFat,
				0, MAX_RESULTS, callback);//offset, size, callback
	}
}
