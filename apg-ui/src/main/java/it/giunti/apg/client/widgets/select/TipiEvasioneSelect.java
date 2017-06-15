package it.giunti.apg.client.widgets.select;

import it.giunti.apg.shared.AppConstants;

public class TipiEvasioneSelect extends Select {
	
	public TipiEvasioneSelect(String selectedId) {
		super(selectedId);
		draw();
	}
	
	private void draw() {
		this.clear();
		//this.setVisibleItemCount(1);
		for (String key:AppConstants.EVASIONE_FAS_DESC.keySet()) {
			this.addItem(AppConstants.EVASIONE_FAS_DESC.get(key), key);
		}
		showSelectedValue();
	}
	
}
