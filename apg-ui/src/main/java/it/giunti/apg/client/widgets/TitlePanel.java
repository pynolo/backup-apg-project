package it.giunti.apg.client.widgets;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Widget;

public class TitlePanel extends FlowPanel {

	String title = null;
	FlowPanel body = null;
	
	public TitlePanel(String title) {
		this.title = title;
		this.setStyleName("panel panel-default");//panel-info
		FlowPanel heading = new FlowPanel();
		super.add(heading);
		heading.setStyleName("panel-heading");
		heading.add(new InlineHTML("<b>"+this.title+"</b>"));
		body = new FlowPanel();
		body.setStyleName("panel-body");
		super.add(body);
	}

	@Override
	public void clear() {
		body.clear();
	}

	@Override
	public void add(Widget w) {
		body.add(w);
	}

	@Override
	public boolean remove(Widget w) {
		return body.remove(w);
	}
	
}
