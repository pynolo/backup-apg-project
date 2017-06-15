package it.giunti.apg.client.widgets;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;

public class PasswordBoxEnter extends PasswordTextBox implements KeyPressHandler {

	private FormPanel form = null;
	
	public PasswordBoxEnter(FormPanel form) {
		super();
		this.addKeyPressHandler(this);
		this.form = form;
	}

	public PasswordBoxEnter(FormPanel form, Element element) {
		super(element);
		this.addKeyPressHandler(this);
		this.form = form;
	}
	
	@Override
	public void onKeyPress(KeyPressEvent event) {
		int keyCode = event.getNativeEvent().getKeyCode();
		if (keyCode == KeyCodes.KEY_ENTER) form.submit();
	}
	
}
