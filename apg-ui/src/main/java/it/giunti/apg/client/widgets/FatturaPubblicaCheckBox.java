package it.giunti.apg.client.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;

public class FatturaPubblicaCheckBox extends FlowPanel {

	private boolean value = false;
	private boolean enabled = true;
	private Anchor pubblicaLabel = null;
	private Anchor nonPubblicaLabel = null;
	
	public FatturaPubblicaCheckBox() {
		super();
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
	
	private void toggle() {
		value = !value;
		save();
		refresh();
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
	
	//SAVE
	
	
}
