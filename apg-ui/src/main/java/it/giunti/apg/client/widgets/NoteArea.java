package it.giunti.apg.client.widgets;

import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.TextArea;

public class NoteArea extends TextArea implements ValueChangeHandler<String>, KeyPressHandler {

	private int maxSize = 0;
	
	public NoteArea(int maxSize) {
		super();
		this.maxSize = maxSize;
		this.addValueChangeHandler(this);
		this.addKeyPressHandler(this);
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		trimValue();
	}

	@Override
	public void onKeyPress(KeyPressEvent event) {
		trimValue();
	}
	
	private void trimValue() {
		String value = this.getValue();
		if (value != null) {
			if (value.length() > maxSize) {
				value = value.substring(0, maxSize);
				setValue(value, false);
			}
		}
	}

}
