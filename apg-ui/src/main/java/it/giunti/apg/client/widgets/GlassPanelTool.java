package it.giunti.apg.client.widgets;

import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.PopupPanel;

import it.giunti.apg.client.ClientConstants;

public class GlassPanelTool {
	
	public static final String MODE_SHORT = "short";
	public static final String MODE_LONG = "long";
    
	private static final String STYLE = "glass-panel";
	
	private String glassMode = null;
	private WaitPopup waitPopup =null;
	
	public GlassPanelTool() {
		this.glassMode=MODE_SHORT;
	}
	
	public void show(String mode) {
		glassMode = MODE_SHORT;
    	if (mode != null) glassMode = mode;
		
    	waitPopup = new WaitPopup(glassMode);
    	waitPopup.show();
	}
	
	public void hide() {
		if (waitPopup != null) {
			waitPopup.hide();
		}
	}
	
	
	//Inner classes
	

	private static class WaitPopup extends PopupPanel {

		private String mode = MODE_LONG;
		
		public WaitPopup(String mode) {
			super(false,true);
	    	this.mode=mode;
	    	
	    	InlineHTML waitImg = new InlineHTML();
	    	waitImg.setHTML(ClientConstants.ICON_LOADING_SHORT);
	    	if (this.mode.equals(MODE_LONG)) {
	   			waitImg.setHTML(ClientConstants.ICON_LOADING_LONG);
	    	}
			// PopupPanel is a SimplePanel, so you have to set it's widget property to
			// whatever you want its contents to be.
			setWidget(waitImg);
			setGlassStyleName(STYLE);
			center();
			setStyleName("nostyle");
		}
	}
}
