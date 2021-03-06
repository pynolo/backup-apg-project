package it.giunti.apg.client.frames;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.services.UtilService;
import it.giunti.apg.client.services.UtilServiceAsync;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Utenti;

public class InstallFrame extends FramePanel implements IAuthenticatedWidget {
	
	UtilServiceAsync utilService = GWT.create(UtilService.class);
	private boolean isAdmin = false;
	private VerticalPanel panel = null;
	private HTML versionHtml = new HTML();
	private HTML statusHtml = new HTML();
	private HTML apgUiHtml = new HTML();
	private HTML apgWsHtml = new HTML();
	private HTML apgAutomationHtml = new HTML();
	
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
			loadApgUiInstallInfo();
			loadApgWsInstallInfo();
			loadApgAutomationInstallInfo();
			drawContent();
		}
	}
	
	private void drawContent() {
		HTML versionTitleHtml = new HTML("Versione Software");
		versionTitleHtml.setStyleName("section-title");
		panel.add(versionTitleHtml);
		panel.add(versionHtml);
		panel.add(statusHtml);
		panel.add(new HTML("<b>LDAP host</b>: "+ServerConstants.LDAP_HOST));
		panel.add(new HTML("<b>SMTP host</b>: "+ServerConstants.SMTP_HOST));
		panel.add(new HTML("<b>Email provider endpoint</b>: "+ServerConstants.PROVIDER_EMAIL_ENDPOINT));
		
		HTML uiTitleHtml = new HTML("User Interface");
		uiTitleHtml.setStyleName("section-title");
		panel.add(uiTitleHtml);
		panel.add(apgUiHtml);

		HTML wsTitleHtml = new HTML("API / Web Service");
		wsTitleHtml.setStyleName("section-title");
		panel.add(wsTitleHtml);
		panel.add(apgWsHtml);
		
		HTML automationTitleHtml = new HTML("Automazione");
		automationTitleHtml.setStyleName("section-title");
		panel.add(automationTitleHtml);
		panel.add(apgAutomationHtml);
	}
	
	private void loadVersion() {
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
		utilService.getApgVersion(callback);
	}
	
	private void loadStatus() {
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
		utilService.getApgStatus(callback);
	}
	
	private void loadApgUiInstallInfo() {
		AsyncCallback<String> callback = new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				apgUiHtml.setHTML("Impossibile ottenere informazioni");
				apgUiHtml.setStyleName("message-warn");
			}
			@Override
			public void onSuccess(String installInfo) {
				apgUiHtml.setHTML(installInfo);
			}
		};
		utilService.getApguiInstallInfo(GWT.getModuleBaseURL(), callback);
	}
	
	private void loadApgWsInstallInfo() {
		AsyncCallback<String> callback = new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				apgWsHtml.setHTML("Impossibile ottenere informazioni");
				apgWsHtml.setStyleName("message-warn");
			}
			@Override
			public void onSuccess(String installInfo) {
				apgWsHtml.setHTML(installInfo);
			}
		};
		utilService.getApgwsInstallInfo(GWT.getModuleBaseURL(), callback);
	}
	
	private void loadApgAutomationInstallInfo() {
		AsyncCallback<String> callback = new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				apgAutomationHtml.setHTML("Impossibile ottenere informazioni");
				apgAutomationHtml.setStyleName("message-warn");
			}
			@Override
			public void onSuccess(String installInfo) {
				apgAutomationHtml.setHTML(installInfo);
			}
		};
		utilService.getApgautomationInstallInfo(GWT.getModuleBaseURL(), callback);
	}
}
