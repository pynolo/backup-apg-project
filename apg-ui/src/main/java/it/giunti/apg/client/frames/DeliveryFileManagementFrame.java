package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Utenti;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DeliveryFileManagementFrame extends FramePanel implements IAuthenticatedWidget {
	
	private static final int WIDTH = 600;
	private static final int HEIGHT = 950;
	
	private boolean isAdmin = false;
	private VerticalPanel panel = null;
	
	public DeliveryFileManagementFrame(UriParameters params) {
		AuthSingleton.get().queueForAuthentication(this);
	}
	
	@Override
	public void onSuccessfulAuthentication(Utenti utente) {
		init(utente);
	}
	
	private void init(Utenti utente) {
		// Editing rights
		int ruolo = utente.getRuolo().getId();
		isAdmin = (ruolo >= AppConstants.RUOLO_ADMIN);
		// UI
		if (isAdmin) {
			panel = new VerticalPanel();
			this.add(panel, "Operazioni su etichette");
			drawIFrame();
		}
	}
	
	private void drawIFrame() {
		HTML iFrameHtml = new HTML("<iframe " +
				"src='"+AppConstants.URL_APG_AUTOMATION_DELIVERY+"' " +
				"width='"+WIDTH+"' " +
				"height='"+HEIGHT+"' " +
				"align='top' " +
				"marginwidth='0' marginheight='0' scrolling='auto' " +
				"frameborder='0' border='0' cellspacing='0' ></iframe>");
		panel.add(iFrameHtml);
	}
}
