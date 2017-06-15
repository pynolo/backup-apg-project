package it.giunti.apg.client.widgets.select;

import it.giunti.apg.shared.AppConstants;

public class SocietaSelect extends Select {
	
	public SocietaSelect(String selectedId) {
		super(selectedId);
		draw();
	}
	
	private void draw() {
		this.clear();
		//this.setVisibleItemCount(1);
		for (String key:AppConstants.SOCIETA_DESC.keySet()) {
			this.addItem(AppConstants.SOCIETA_DESC.get(key), key);
		}
		showSelectedValue();
	}
	
}
