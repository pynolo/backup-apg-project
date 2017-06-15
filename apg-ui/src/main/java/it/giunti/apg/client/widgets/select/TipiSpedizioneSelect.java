package it.giunti.apg.client.widgets.select;

import it.giunti.apg.shared.AppConstants;

public class TipiSpedizioneSelect extends Select {
	
	public TipiSpedizioneSelect(String selectedId) {
		super(selectedId);
		draw();
	}
	
	private void draw() {
		this.clear();
		//this.setVisibleItemCount(1);
		for (String key:AppConstants.SPEDIZIONE_DESC.keySet()) {
			String descr = AppConstants.SPEDIZIONE_DESC.get(key);
			this.addItem(descr, key);
		}
		showSelectedValue();
	}

}
