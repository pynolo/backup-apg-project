package it.giunti.apg.client.frames;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.services.LookupService;
import it.giunti.apg.client.services.LookupServiceAsync;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Utenti;

public class ConfigFrame extends FramePanel implements IAuthenticatedWidget {
	
	private static final int WIDTH = 600;
	private static final int HEIGHT = 100;
	
	private boolean isAdmin = false;
	private VerticalPanel panel = null;
	private HTML versionHtml = new HTML();
	
	public ConfigFrame(UriParameters params) {
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
			this.add(panel, "Verifica della configurazione");
			loadVersion();
			drawContent();
		}
	}
	
	private void drawContent() {
		HTML versionTitleHtml = new HTML("Versione Software");
		versionTitleHtml.setStyleName("section-title");
		panel.add(versionHtml);
		
		HTML uiIframeTitleHtml = new HTML("Configurazione User Interface");
		uiIframeTitleHtml.setStyleName("section-title");
		panel.add(uiIframeTitleHtml);
		HTML uiIframeHtml = new HTML("<iframe " +
				"src='"+AppConstants.URL_APG_UI_CONFIG+"' " +
				"width='"+WIDTH+"' " +
				"height='"+HEIGHT+"' " +
				"align='top' " +
				"marginwidth='0' marginheight='0' scrolling='auto' " +
				"frameborder='0' border='1' cellspacing='0' ></iframe>");
		panel.add(uiIframeHtml);

		HTML wsIframeTitleHtml = new HTML("Configurazione API / Web Service");
		wsIframeTitleHtml.setStyleName("section-title");
		panel.add(wsIframeTitleHtml);
		HTML wsIframeHtml = new HTML("<iframe " +
				"src='"+AppConstants.URL_APG_WS_CONFIG+"' " +
				"width='"+WIDTH+"' " +
				"height='"+HEIGHT+"' " +
				"align='top' " +
				"marginwidth='0' marginheight='0' scrolling='auto' " +
				"frameborder='0' border='1' cellspacing='0' ></iframe>");
		panel.add(wsIframeHtml);
		
		HTML automationIframeTitleHtml = new HTML("Configurazione Automazione");
		automationIframeTitleHtml.setStyleName("section-title");
		panel.add(automationIframeTitleHtml);
		HTML automationIframeHtml = new HTML("<iframe " +
				"src='"+AppConstants.URL_APG_AUTOMATION_CONFIG+"' " +
				"width='"+WIDTH+"' " +
				"height='"+HEIGHT+"' " +
				"align='top' " +
				"marginwidth='0' marginheight='0' scrolling='auto' " +
				"frameborder='0' border='1' cellspacing='0' ></iframe>");
		panel.add(automationIframeHtml);
	}
	
	private void loadVersion() {
		LookupServiceAsync lookupService = GWT.create(LookupService.class);
		
		AsyncCallback<String> callback = new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				versionHtml.setHTML(ClientConstants.APG_DEFAULT_TITLE+
						": Impossibile verificare la versione in uso!");
				UiSingleton.get().addError(caught);
			}
			@Override
			public void onSuccess(String version) {
				versionHtml.setHTML(ClientConstants.APG_DEFAULT_TITLE+
						": <b>"+version+"</b>");
			}
		};
		lookupService.getApgVersion(callback);
	}
}
