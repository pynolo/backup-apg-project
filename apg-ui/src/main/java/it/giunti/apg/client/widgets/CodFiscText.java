package it.giunti.apg.client.widgets;

import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.ValueUtil;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.user.client.ui.TextBox;

public class CodFiscText extends TextBox implements BlurHandler {

	private String idNazione = AppConstants.DEFAULT_ID_NAZIONE_ITALIA;
	
	public CodFiscText(String idNazione) {
		super();
		setIdNazione(idNazione);
		this.addBlurHandler(this);
	}
	
	@Override
	public void onBlur(BlurEvent event) {
		boolean valid = true;
		if (this.getValue() != null) {
			if (this.getValue().length() > 0) {
				valid = ValueUtil.isValidCodFisc(this.getValue(), idNazione);
			}
		}
		if (!valid) {
			UiSingleton.get().addWarning("Il codice fiscale non &egrave; corretto");
		}
	}

	@Override
	public String getValue() {
		return super.getValue().toUpperCase().trim();
	}

	public void setIdNazione(String idNazione) {
		if (idNazione != null) this.idNazione = idNazione;
	}
}
