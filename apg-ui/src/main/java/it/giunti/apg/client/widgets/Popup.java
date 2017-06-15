package it.giunti.apg.client.widgets;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Popup extends PopupPanel {
	
	public Popup(String message) {
		// PopupPanel's constructor takes 'auto-hide' as its boolean parameter.
		// If this is set, the panel closes itself automatically when the user
		// clicks outside of it.
		super(true);
		// PopupPanel is a SimplePanel, so you have to set it's widget property
		// to
		// whatever you want its contents to be.
		VerticalPanel vp = new VerticalPanel();
		vp.add(new HTML("<img src='img/icon16/dialog-warning.png' style='vertical-align:middle' /> <b>Attenzione</b>"));
		vp.add(new HTML(message));
		setWidget(vp);
	}

	public Popup(Throwable e) {
		super(true);
		VerticalPanel vp = new VerticalPanel();
		vp.add(new HTML("<img src='img/icon16/dialog-error.png' style='vertical-align:middle' /> <b>Errore</b>"));
		vp.add(new HTML(e.getMessage()));
		setWidget(vp);
	}

	public void centerAndShow() {
		final PopupPanel pop = this;
		pop.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
			public void setPosition(int offsetWidth, int offsetHeight) {
				int left = (Window.getClientWidth() - offsetWidth) / 2;
				int top = (Window.getClientHeight() - offsetHeight) / 2;
				pop.setPopupPosition(left, top);
			}
		});
	}
	
}
