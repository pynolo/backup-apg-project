package it.giunti.apg.client.widgets.select;

import it.giunti.apg.shared.AppConstants;

public class TipiPagamentoSelect extends Select {
	
	public TipiPagamentoSelect(String selectedId) {
		super(selectedId);
		draw();
	}
	
	private void draw() {
		this.clear();
		//this.setVisibleItemCount(1);
		for (String key:AppConstants.PAGAMENTO_DESC.keySet()) {
			this.addItem(AppConstants.PAGAMENTO_DESC.get(key), key);
		}
		showSelectedValue();
	}
		
}
