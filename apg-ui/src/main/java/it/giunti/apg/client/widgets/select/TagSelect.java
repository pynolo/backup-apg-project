package it.giunti.apg.client.widgets.select;

import it.giunti.apg.shared.AppConstants;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;

public class TagSelect extends Select implements BlurHandler {
	
	TagSelectPanel parent = null;

	public TagSelect(String selectedId) {
		super(selectedId);
		this.addBlurHandler(this);
		draw();
	}
	
	public TagSelect(String selectedId, TagSelectPanel parent) {
		super(selectedId);
		this.addBlurHandler(this);
		this.parent = parent;
		draw();
	}
	
	private void draw() {
		this.clear();
		//this.setVisibleItemCount(1);
		this.addItem("", "");
		for (String tag:AppConstants.TAG_DESC) {
			this.addItem(tag, tag);
		}
		showSelectedValue();
	}

	@Override
	public void onBlur(BlurEvent event) {
		if (parent != null) {
			parent.pack();
		}
	}
}
