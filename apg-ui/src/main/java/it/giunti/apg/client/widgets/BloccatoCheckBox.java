package it.giunti.apg.client.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;

public class BloccatoCheckBox extends FlowPanel {

	private boolean value = false;
	private boolean enabled = true;
	private Anchor nonBloccatoLabel = null;
	private Image bloccatoImg = null;
	
	public BloccatoCheckBox() {
		super();
		draw();
		refresh();
	}
	
	private void draw() {
		bloccatoImg = new Image("img/bloccato.png");
		
		nonBloccatoLabel = new Anchor();
		nonBloccatoLabel.setHTML("non&nbsp;bloccato");
		if (enabled) {
			bloccatoImg.addClickHandler(new ToggleHandler());
			nonBloccatoLabel.addClickHandler(new ToggleHandler());
		}
	}
	
	private void toggle() {
		value = !value;
		refresh();
	}
	
	private void refresh() {
		this.clear();
		if (value) {
			this.add(bloccatoImg);
		} else {
			this.add(nonBloccatoLabel);
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
	
}
