package it.giunti.apg.client;

import it.giunti.apg.client.widgets.GlassPanel;

public class WaitSingleton {

	private static WaitSingleton instance = null;
	private static GlassPanel glassPanel;
	private static int countWaitInProgress;
	
	private WaitSingleton() {
		glassPanel = new GlassPanel();
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
    		glassPanel.show();
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
