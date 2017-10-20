package it.giunti.apg.client.widgets;

import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.shared.ValueUtil;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.user.client.ui.TextBox;

public class PIvaText extends TextBox implements BlurHandler {

	public PIvaText() {
		super();
		this.addBlurHandler(this);
	}
	
	@Override
	public void onBlur(BlurEvent event) {
		boolean valid = ValueUtil.isValidOrEmptyPIva(this.getValue());
		if (!valid) {
			UiSingleton.get().addWarning("La partita IVA non &egrave; corretta");
		}
	}

	@Override
	public String getValue() {
		return super.getValue().toUpperCase().trim();
	}

}
