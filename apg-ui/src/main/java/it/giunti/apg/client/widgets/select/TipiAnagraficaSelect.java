package it.giunti.apg.client.widgets.select;

import it.giunti.apg.shared.AppConstants;

public class TipiAnagraficaSelect extends Select {
	
	public TipiAnagraficaSelect(String selectedId) {
		super(selectedId);
		draw();
	}
	
	private void draw() {
		this.clear();
		//this.setVisibleItemCount(1);
		for (String key:AppConstants.ANAG_DESC.keySet()) {
			this.addItem(AppConstants.ANAG_DESC.get(key), key);
		}
		showSelectedValue();
	}
		
}
