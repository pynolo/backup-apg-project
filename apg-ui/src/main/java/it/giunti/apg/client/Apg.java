package it.giunti.apg.client;

import it.giunti.apg.client.services.LookupService;
import it.giunti.apg.client.services.LookupServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Apg implements EntryPoint {

	private final LookupServiceAsync lookupService = GWT.create(LookupService.class);
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		hardReloadOnVersionChange();
	}

	private void hardReloadOnVersionChange() {
		AsyncCallback<String> callback = new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				//LAUNCH APG:
				//GWT.setUncaughtExceptionHandler(new BrowserException());
				UiSingleton.get().drawUi();
				//Loading info
				UiSingleton.get().addWarning("Impossibile verificare la versione di APG in uso!");
				UiSingleton.get().addError(caught);
			}
			@Override
			public void onSuccess(String version) {
				String cookieVersion = CookieSingleton.get().getCookie(ClientConstants.COOKIE_VERSION);
				if (cookieVersion == null) cookieVersion="";
				if (!cookieVersion.equals(version)) {
					CookieSingleton.get().setCookie(ClientConstants.COOKIE_VERSION, version);
					UriManager.hardReload();
				}
				//LAUNCH APG:
				//GWT.setUncaughtExceptionHandler(new BrowserException());
				UiSingleton.get().drawUi();
				//Loading info
				UiSingleton.get().addInfo("APG versione "+version);
			}
		};
		lookupService.getApgVersion(callback);
	}
	
}
