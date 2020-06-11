package it.giunti.apg.client.widgets.select;

import it.giunti.apg.shared.AppConstants;

public class TipiMaterialeSelect extends Select {
	
	public TipiMaterialeSelect(String selectedId) {
		super(selectedId);
		draw();
	}
	
	private void draw() {
		this.clear();
		//this.setVisibleItemCount(1);
		for (String key:AppConstants.MATERIALE_DESC.keySet()) {
			this.addItem(AppConstants.MATERIALE_DESC.get(key), key);
		}
		showSelectedValue();
	}
	
}