package it.giunti.apg.client.widgets;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.TextArea;

public class NoteArea extends TextArea implements ChangeHandler {

	private int maxSize = 0;
	
	public NoteArea(int maxSize) {
		super();
		this.maxSize = maxSize;
		this.addChangeHandler(this);
	}
	
	@Override
	public void onChange(ChangeEvent event) {
		String value = getValue();
		if (value != null) {
			if (value.length() > maxSize) {
				setValue(value.substring(0, maxSize));
			}
		}
	}

}
