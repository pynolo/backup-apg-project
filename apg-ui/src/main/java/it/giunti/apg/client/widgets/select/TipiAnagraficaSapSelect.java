package it.giunti.apg.client.widgets.select;

import it.giunti.apg.shared.AppConstants;

public class TipiAnagraficaSapSelect extends Select {
	
	public TipiAnagraficaSapSelect(String selectedId) {
		super(selectedId);
		draw();
	}
	
	private void draw() {
		this.clear();
		//this.setVisibleItemCount(1);
		for (String key:AppConstants.ANAGRAFICA_SAP_DESC.keySet()) {
			this.addItem(AppConstants.ANAGRAFICA_SAP_DESC.get(key), key);
		}
		showSelectedValue();
	}
	
}
