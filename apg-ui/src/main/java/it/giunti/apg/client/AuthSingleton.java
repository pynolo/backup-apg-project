package it.giunti.apg.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import it.giunti.apg.client.frames.LoginPopUp;
import it.giunti.apg.client.frames.PasswordChangePopUp;
import it.giunti.apg.client.services.AuthService;
import it.giunti.apg.client.services.AuthServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.Utenti;

public class AuthSingleton {

	private final AuthServiceAsync authService = GWT.create(AuthService.class);
		
	private static AuthSingleton instance = null;
	private Utenti utente = null;
	private List<IAuthenticatedWidget> widgetList = null;

	private AuthSingleton() {
		reloadOnExpiredLogin();
		widgetList = new ArrayList<IAuthenticatedWidget>();
	}
	
	public static final AuthSingleton get() {
		if (instance == null) {
			instance = new AuthSingleton();
		}
		return instance;
	}

	public void logout(IAuthenticatedWidget widget) {
		utente = null;
		deleteCookie();
		queueForAuthentication(widget);
	}
	
	public Utenti getUtente() {
		return utente;
	}
	
	public void queueForAuthentication(IAuthenticatedWidget widget) {
		if (utente != null) {
			widget.onSuccessfulAuthentication(utente);
		} else {
			widgetList.add(widget);
			if (widgetList.size() <= 1) {
				authenticateByCookieOrPopUp(widgetList);
			}
		}
	}
	
	private void unlockWidgets(Utenti utente, List<IAuthenticatedWidget> widgetList) {
		for (IAuthenticatedWidget widget:widgetList) {
			widget.onSuccessfulAuthentication(utente);
		}
		widgetList.clear();
	}
	
	public void passwordChange(String msg, Utenti utente) {
		new PasswordChangePopUp(msg, utente, this);
	}
	
	//Cookie
	private void authenticateByCookieOrPopUp(List<IAuthenticatedWidget> widgetList) {
		reloadOnExpiredLogin();
		String userName = CookieSingleton.get().getCookie(ClientConstants.COOKIE_USERNAME);
		String password = CookieSingleton.get().getCookie(ClientConstants.COOKIE_PASSWORD);
		processCredentials(userName, password, widgetList);
	}
	private void saveCookie(String userName, String password) {
		CookieSingleton.get().setCookie(ClientConstants.COOKIE_USERNAME, userName);
		CookieSingleton.get().setCookie(ClientConstants.COOKIE_PASSWORD, password);
	}
	private void deleteCookie() {
		CookieSingleton.get().removeCookie(ClientConstants.COOKIE_USERNAME);
		CookieSingleton.get().removeCookie(ClientConstants.COOKIE_PASSWORD);
	}
	
	private void reloadOnExpiredLogin() {
		long thisLogin = DateUtil.now().getTime();
		String lastLoginString = CookieSingleton.get().getCookie(ClientConstants.COOKIE_LAST_LOGIN);
		if (lastLoginString != null) {
			long lastLogin;
			try {
				lastLogin = Long.parseLong(lastLoginString);
			} catch (NumberFormatException e) {
				lastLogin = thisLogin;
			}
			if (thisLogin-lastLogin > ClientConstants.LOGIN_EXPIRATION_TIME) {
				CookieSingleton.get().setCookie(ClientConstants.COOKIE_LAST_LOGIN, thisLogin+"");
				UriManager.hardReload();
			}
		} else {
			CookieSingleton.get().setCookie(ClientConstants.COOKIE_LAST_LOGIN, thisLogin+"");
			UriManager.hardReload();
		}
		CookieSingleton.get().setCookie(ClientConstants.COOKIE_LAST_LOGIN, thisLogin+"");
	}
	
	
	
	// METODI ASINCRONI


	public void processCredentials(String userName, String password,
			List<IAuthenticatedWidget> widgetList) {
		final String fUserName = userName;
		final String fPassword = password;
		final List<IAuthenticatedWidget> fWidgetList = widgetList;
		final AuthSingleton authSingleton = this;
		AsyncCallback<Utenti> callback = new AsyncCallback<Utenti>() {
			@Override
			public void onFailure(Throwable caught) {
				WaitSingleton.get().stop();
				String message = caught.getMessage();
				if (message == null) message = "Autenticazione fallita";
				if (message.equals("")) {
					message = "Autenticazione fallita";
				} else {
					if (message.equals(AppConstants.AUTH_EMPTY_CREDENTIALS)) message = "";
				}
				new LoginPopUp(message, fWidgetList, authSingleton);
			}
			@Override
			public void onSuccess(Utenti result) {
				utente = result;
				saveCookie(fUserName, fPassword);
				unlockWidgets(result, fWidgetList);
				WaitSingleton.get().stop();
				if (result.getPasswordReset()) passwordChange(
						"E' necessario un cambio password per continuare", result);
			}
		};
		try {
			if ((userName != null) && (password != null)) {
				WaitSingleton.get().start();
				authService.authenticate(fUserName, fPassword, callback);
			} else {
				new LoginPopUp("", fWidgetList, authSingleton);
			}
		} catch (Exception e) {
			//Will never be called because Exceptions will be caught by callback
			e.printStackTrace();
		}
	}
	
	public void processNewPassword(String pwd1, String pwd2) {
		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				WaitSingleton.get().stop();
				String message = caught.getMessage();
				passwordChange(message, utente);
			}
			@Override
			public void onSuccess(Boolean result) {
				WaitSingleton.get().stop();
			}
		};
		if ((pwd1 != null) && (pwd2 != null)) {
			if ((pwd1.length() > 1) && (pwd2.length() > 1)) {
				if (pwd1.equals(pwd2)) {
					if (pwd1.length() >= AppConstants.PASSWORD_MIN_LENGTH) {
						WaitSingleton.get().start();
						authService.addPassword(utente.getId(), pwd1, false, callback);
					} else {
						passwordChange("La password deve essere almeno "+AppConstants.PASSWORD_MIN_LENGTH+" caratteri", utente);
					}
				} else {
					passwordChange("Le password sono diverse", utente);
				}
			} else {
				passwordChange("Alcuni campi non sono stati riempiti", utente);
			}
		} else {
			passwordChange("Alcuni campi non sono stati riempiti", utente);
		}
	}
}
