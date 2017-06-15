package it.giunti.apg.client.widgets;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * Il SubPanel è un pannello con titolo.
 * Il titolo diventa visibile solo dopo che è stato aggiunto (add) almeno un widget
 * @author paolo
 *
 */
public class SubPanel extends FlowPanel {

	private HTML titleHtml = new HTML();
	
	public SubPanel() {
		super();
		titleHtml.setVisible(false);
		super.add(titleHtml);
	}
	
	public SubPanel(String htmlTitle) {
		super();
		titleHtml.setVisible(false);
		super.add(titleHtml);
		setTitle(htmlTitle);
	}
	
	public void setTitle(String html) {
		titleHtml.setHTML(html);
		titleHtml.setStyleName("section-title");
	}
	
	@Override
	public void clear() {
		super.clear();
		titleHtml.setVisible(false);
		super.add(titleHtml);
	}

	@Override
	public void add(Widget w) {
		super.add(w);
		titleHtml.setVisible(true);
	}
	
}
