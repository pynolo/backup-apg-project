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
	private boolean enabled = false;
	private Integer idFattura = null;
	
	private final PagamentiServiceAsync pagamentiService = GWT.create(PagamentiService.class);
	
	public FatturaPubblicaCheckBox(Integer idFattura, boolean pubblica) {
		super();
		this.idFattura=idFattura;
		this.value=pubblica;
		refresh();
	}
		
	private void refresh() {
		this.clear();
		String style = "";
		if (!enabled) style = "style='color: #999999'"; 
		Anchor toggleAnchor = new Anchor();
		if (value) {
			toggleAnchor.setHTML("<i class='fa fa-eye' aria-hidden='true' "+style+"></i>");
		} else {
			toggleAnchor.setHTML("<i class='fa fa-eye-slash' aria-hidden='true' "+style+"></i>");
		}
		this.add(toggleAnchor);
		if (enabled) toggleAnchor.addClickHandler(new ToggleHandler());
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
			refresh();
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
