package it.giunti.apg.client.widgets.select;

import it.giunti.apg.shared.AppConstants;

public class SessoSelect extends Select {
	
	public SessoSelect(String selectedId) {
		super(selectedId);
		draw();
	}
	
	private void draw() {
		this.clear();
		//this.setVisibleItemCount(1);
		this.addItem(AppConstants.SELECT_EMPTY_LABEL);
		this.addItem(AppConstants.SESSO_M);
		this.addItem(AppConstants.SESSO_F);
		showSelectedValue();
	}

	@Override
	public void showSelectedValue() {
		String selected = getSelectedId();
		if (AppConstants.SELECT_EMPTY_LABEL.equals(selected)) this.setSelectedIndex(0);
		if (AppConstants.SESSO_M.equals(selected)) this.setSelectedIndex(1);
		if (AppConstants.SESSO_F.equals(selected)) this.setSelectedIndex(2);
	}
	
}
