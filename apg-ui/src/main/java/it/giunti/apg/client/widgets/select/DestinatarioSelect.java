package it.giunti.apg.client.widgets.select;

import it.giunti.apg.shared.AppConstants;

public class DestinatarioSelect extends Select {
	
	public DestinatarioSelect(String selectedId) {
		super(selectedId);
		draw();
	}
	
	private void draw() {
		this.clear();
		//this.setVisibleItemCount(1);
		for (String key:AppConstants.DEST_DESC.keySet()) {
			this.addItem(AppConstants.DEST_DESC.get(key), key);
		}
		showSelectedValue();
	}
	
}