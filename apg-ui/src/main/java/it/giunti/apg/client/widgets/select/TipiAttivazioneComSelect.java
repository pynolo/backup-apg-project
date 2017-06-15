package it.giunti.apg.client.widgets.select;

import it.giunti.apg.shared.AppConstants;

public class TipiAttivazioneComSelect extends Select {
	
	public TipiAttivazioneComSelect(String selectedId) {
		super(selectedId);
		draw();
	}
	
	private void draw() {
		this.clear();
		//this.setVisibleItemCount(1);
		for (String key:AppConstants.COMUN_ATTIVAZ_DESC.keySet()) {
			String descr = AppConstants.COMUN_ATTIVAZ_DESC.get(key);
			this.addItem(descr, key);
		}
		showSelectedValue();
	}
		
}
