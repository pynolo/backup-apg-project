package it.giunti.apg.client.widgets.select;

import it.giunti.apg.shared.AppConstants;

public class BooleanSelect extends Select {
	
	public BooleanSelect(String selectedId) {
		super(selectedId);
		draw();
	}
	
	private void draw() {
		this.clear();
		//this.setVisibleItemCount(1);
		this.addItem(AppConstants.SELECT_EMPTY_LABEL);
		this.addItem("SI", AppConstants.BOOLEAN_TRUE);
		this.addItem("NO", AppConstants.BOOLEAN_FALSE);
		showSelectedValue();
	}

	@Override
	public void showSelectedValue() {
		String selected = getSelectedId();
		if (AppConstants.SELECT_EMPTY_LABEL.equals(selected)) this.setSelectedIndex(0);
		if (AppConstants.BOOLEAN_TRUE.equals(selected)) this.setSelectedIndex(1);
		if (AppConstants.BOOLEAN_FALSE.equals(selected)) this.setSelectedIndex(2);
	}
	
}
