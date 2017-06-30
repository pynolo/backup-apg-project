package it.giunti.apg.client.widgets;

import it.giunti.apg.client.UiSingleton;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class FramePanel extends FlowPanel implements ClickHandler {

	private List<TitleLabel> titleList = new ArrayList<TitleLabel>();
	private List<Widget> widgetList = new ArrayList<Widget>();
	
	public FramePanel() {
		super();
		setBrowserWindowTitle(null);
		UiSingleton.get().checkMaintenance();
	}
	
	public void add(Widget widget, String html) {
		TitleLabel title = new TitleLabel(html);
		title.addClickHandler(this);
		titleList.add(title);
		this.add(title);
		widgetList.add(widget);
		widget.setStyleName("frame-content");
		widget.setVisible(widgetList.size() == 1);
		this.add(widget);
	}
	
	public void showWidget(int index) {
		for (int i = 0; i<titleList.size(); i++) {
			if (index == i) {
				widgetList.get(i).setVisible(true);
			} else {
				widgetList.get(i).setVisible(false);
			}
		}
	}
	

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() instanceof TitleLabel) {
			TitleLabel clicked = (TitleLabel) event.getSource();
			for (int i = 0; i<titleList.size(); i++) {
				if (titleList.get(i).equals(clicked)) {
					widgetList.get(i).setVisible(true);
				} else {
					widgetList.get(i).setVisible(false);
				}
			}
		}
	}
	
	public void setBrowserWindowTitle(String windowTitle) {
		String title = UiSingleton.get().getApgTitle();
		if (windowTitle != null) {
			title += " " + windowTitle;
		}
	    if (Document.get() != null) {
	        Document.get().setTitle(title);
	    }
	}
	
	
	
	//Inner classes
	


	public class TitleLabel extends HTML {
		public TitleLabel(String html) {
			super(html);
			this.setStyleName("frame-title");
		}
	}
	
}
