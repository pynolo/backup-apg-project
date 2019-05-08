package it.giunti.apg.client.frames;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.services.LookupService;
import it.giunti.apg.client.services.LookupServiceAsync;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Utenti;

public class InstallFrame extends FramePanel implements IAuthenticatedWidget {
	
	private static final int WIDTH = 600;
	private static final int HEIGHT = 100;
	
	private boolean isAdmin = false;
	private VerticalPanel panel = null;
	private HTML versionHtml = new HTML();
	private HTML statusHtml = new HTML();
	
	public InstallFrame(UriParameters params) {
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
			this.add(panel, "Installazione");
			loadVersion();
			loadStatus();
			drawContent();
		}
	}
	
	private void drawContent() {
		HTML versionTitleHtml = new HTML("Versione Software");
		versionTitleHtml.setStyleName("section-title");
		panel.add(versionTitleHtml);
		panel.add(versionHtml);
		panel.add(statusHtml);
		
		HTML uiIframeTitleHtml = new HTML("User Interface");
		uiIframeTitleHtml.setStyleName("section-title");
		panel.add(uiIframeTitleHtml);
		HTML uiIframeHtml = new HTML("<iframe " +
				"src='"+AppConstants.URL_APG_UI_INSTALL_PAGE+"' " +
				"width='"+WIDTH+"' " +
				"height='"+HEIGHT+"' " +
				"align='top' " +
				"marginwidth='0' marginheight='0' scrolling='auto' " +
				"frameborder='0' border='1' cellspacing='0' ></iframe>");
		panel.add(uiIframeHtml);

		HTML wsIframeTitleHtml = new HTML("API / Web Service");
		wsIframeTitleHtml.setStyleName("section-title");
		panel.add(wsIframeTitleHtml);
		HTML wsIframeHtml = new HTML("<iframe " +
				"src='"+AppConstants.URL_APG_WS_INSTALL_PAGE+"' " +
				"width='"+WIDTH+"' " +
				"height='"+HEIGHT+"' " +
				"align='top' " +
				"marginwidth='0' marginheight='0' scrolling='auto' " +
				"frameborder='0' border='1' cellspacing='0' ></iframe>");
		panel.add(wsIframeHtml);
		
		HTML automationIframeTitleHtml = new HTML("Automazione");
		automationIframeTitleHtml.setStyleName("section-title");
		panel.add(automationIframeTitleHtml);
		HTML automationIframeHtml = new HTML("<iframe " +
				"src='"+AppConstants.URL_APG_AUTOMATION_INSTALL_PAGE+"' " +
				"width='"+WIDTH+"' " +
				"height='500' " +
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
				versionHtml.setHTML("<b>APG version</b>: Impossibile verificare la versione in uso!");
				UiSingleton.get().addError(caught);
			}
			@Override
			public void onSuccess(String version) {
				versionHtml.setHTML("<b>APG version</b>: "+version+"");
			}
		};
		lookupService.getApgVersion(callback);
	}
	
	private void loadStatus() {
		LookupServiceAsync lookupService = GWT.create(LookupService.class);
		
		AsyncCallback<String> callback = new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				statusHtml.setHTML("<b>Status</b>: Impossibile verificare lo status!");
				UiSingleton.get().addError(caught);
			}
			@Override
			public void onSuccess(String status) {
				statusHtml.setHTML("<b>Status</b>: "+status+"");
			}
		};
		lookupService.getApgStatus(callback);
	}
}
