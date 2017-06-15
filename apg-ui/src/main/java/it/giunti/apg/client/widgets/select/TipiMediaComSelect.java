package it.giunti.apg.client.widgets.select;

import it.giunti.apg.shared.AppConstants;

public class TipiMediaComSelect extends Select {
	
	public TipiMediaComSelect(String selectedId) {
		super(selectedId);
		draw();
	}
	
	private void draw() {
		this.clear();
		//this.setVisibleItemCount(1);
		for (String key:AppConstants.COMUN_MEDIA_DESC.keySet()) {
			String descr = AppConstants.COMUN_MEDIA_DESC.get(key);
			this.addItem(descr, key);
		}
		showSelectedValue();
	}
		
}
