package it.giunti.apg.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;

import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.services.PagamentiService;
import it.giunti.apg.client.services.PagamentiServiceAsync;

public class FatturaPubblicaCheckBox extends FlowPanel {

	private boolean value = false;
	private boolean enabled = true;
	private Integer idFattura = null;
	private Anchor pubblicaLabel = null;
	private Anchor nonPubblicaLabel = null;
	
	private final PagamentiServiceAsync pagamentiService = GWT.create(PagamentiService.class);
	
	public FatturaPubblicaCheckBox(Integer idFattura, boolean pubblica) {
		super();
		this.idFattura=idFattura;
		this.value=pubblica;
		draw();
		refresh();
	}
	
	private void draw() {
		String style = "";
		if (!enabled) style = "style='color: #999999'"; 
		pubblicaLabel = new Anchor();
		pubblicaLabel.setHTML("<i class='fa fa-eye' aria-hidden='true' "+style+"></i>");
		nonPubblicaLabel = new Anchor();
		nonPubblicaLabel.setHTML("<i class='fa fa-eye-slash' aria-hidden='true' "+style+"></i>");
		if (enabled) {
			pubblicaLabel.addClickHandler(new ToggleHandler());
			nonPubblicaLabel.addClickHandler(new ToggleHandler());
		}
	}
		
	private void refresh() {
		this.clear();
		if (value) {
			this.add(pubblicaLabel);
		} else {
			this.add(nonPubblicaLabel);
		}
	}
	

	public boolean getValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
		refresh();
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		if (enabled != this.enabled) {
			this.enabled = enabled;
			draw();
		}
	}
	
	
	//Inner classes
	
	
	private class ToggleHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			toggle();
		}
	}
	
	
	//UPDATE (toggle)
	
	
	private void toggle() {
		boolean newValue = !value;
		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
			}
			@Override
			public void onSuccess(Boolean result) {
				value = result;
				refresh();
			}
		};
		pagamentiService.setFatturaPubblica(idFattura, newValue, callback);
	}
}
