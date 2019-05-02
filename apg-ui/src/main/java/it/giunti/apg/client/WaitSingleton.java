package it.giunti.apg.client;

import it.giunti.apg.client.widgets.GlassPanelTool;

public class WaitSingleton {

	public static final String MODE_SHORT = GlassPanelTool.MODE_SHORT;
	public static final String MODE_LONG = GlassPanelTool.MODE_LONG;
	
	private static WaitSingleton instance = null;
	private static GlassPanelTool glassPanel = null;
	private static int countWaitInProgress;
	
	private WaitSingleton() {
		//Tolta la possibilità di sbloccare
		//glassPanel.addClickHandler(new ClickHandler() {
		//	@Override
		//	public void onClick(ClickEvent event) {
		//		unlock();
		//	}
		//});
		countWaitInProgress = 0;
	}
	
	public static WaitSingleton get() {
		if (instance == null) {
			instance = new WaitSingleton();
		}
		return instance;
	}
	
	public void start() {
    	if (countWaitInProgress == 0) {
    		glassPanel = new GlassPanelTool();
    		glassPanel.show(GlassPanelTool.MODE_SHORT);
    	}    	
    	countWaitInProgress +=1;
	}
	
	public void start(String waitMode) {
    	if (countWaitInProgress == 0) {
    		glassPanel = new GlassPanelTool();
    		glassPanel.show(waitMode);
    	}    	
    	countWaitInProgress +=1;
	}
	
	public void stop() {
    	if (countWaitInProgress == 1) {
    		glassPanel.hide();
    	}
    	countWaitInProgress -=1;
    	if (countWaitInProgress < 0) countWaitInProgress = 0;
	}
	
	//private void unlock() {
	//	glassPanel.hide();
	//	countWaitInProgress = 0;
	//}
}
