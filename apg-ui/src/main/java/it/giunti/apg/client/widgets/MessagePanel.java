package it.giunti.apg.client.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHTML;

public class MessagePanel extends FlowPanel {
	
	public static final int INFO = 0;
	public static final int WARN = 1;
	public static final int ERROR = 2;
	
	public static final String MSG_IMG_HTML = "<img src='img/icon16/dialog-information.png' style='vertical-align:middle' />&nbsp;";
	public static final int INFO_SHOW_TIME = 5000;
	
	public MessagePanel() { }
	
	public void addInfo(String info) {
		this.add(new MessageInstance(this, info, INFO));
		
	}
	
	public void addWarning(String warning) {
		this.add(new MessageInstance(this, warning, WARN));
		
	}
	
	public void addError(Throwable e) {
		this.add(new MessageInstance(this, e.getLocalizedMessage(), ERROR));

	}
	
	public void removeMessage(MessageInstance inst ) {
		this.remove(inst);
	}
	
	//Gestione messaggi a scomparsa
	
	public void addAutohideInfo(String message) {
		final InlineHTML msgLabel = new InlineHTML(MSG_IMG_HTML + message);
		msgLabel.setStyleName("message-info");
		this.add(msgLabel);
		Timer t = new Timer() {
			public void run() {
				removeTempMessage(msgLabel);
			}
		};
		t.schedule(INFO_SHOW_TIME);
	}
	
	public void removeTempMessage(InlineHTML msgLabel ) {
		this.remove(msgLabel);
	}
	
	//Inner classes
	
	private class MessageInstance extends FlowPanel {
		
		public MessageInstance(MessagePanel p, String msg, int type) {
			final MessagePanel parent=p;
			final MessageInstance thisInstance = this;
			
			if (type == INFO) {
				addAutohideInfo(msg);
			} else {
				Button ok = new Button(" OK ");
				String style = null;
				//if (type == INFO) {
				//	msg = "<img src='img/icon16/dialog-information.png' style='vertical-align:middle' />&nbsp;" + msg;
				//	style = "message-info";
				//}
				if (type == WARN) {
					msg = "<img src='img/icon16/dialog-warning.png' style='vertical-align:middle' />&nbsp;" + msg;
					style = "message-warn";
				}
				if (type == ERROR) {
					msg = "<img src='img/icon16/dialog-error.png' style='vertical-align:middle' />&nbsp;" + msg;
					style = "message-error";
				}
				InlineHTML label = new InlineHTML(msg);
				label.setStyleName(style);
				
				this.add(label);
				this.add(ok);
				ok.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						parent.removeMessage(thisInstance);
					}
				});
			}
		}
	}
}
