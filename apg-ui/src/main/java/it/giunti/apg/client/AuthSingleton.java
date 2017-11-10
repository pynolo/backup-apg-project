package it.giunti.apg.client;

import it.giunti.apg.client.services.AuthService;
import it.giunti.apg.client.services.AuthServiceAsync;
import it.giunti.apg.client.widgets.PasswordBoxEnter;
import it.giunti.apg.client.widgets.TextBoxEnter;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.Utenti;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;

public class AuthSingleton {

	private final AuthServiceAsync authService = GWT.create(AuthService.class);
		
	private static AuthSingleton instance = null;
	private Utenti utente = null;
	private List<IAuthenticatedWidget> widgetList = null;
	
	private TextBoxEnter userNameText = null;
	private PasswordBoxEnter passwordPswd = null;
	private Image logoImage = new Image();
	
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
	
	
	//Cookie
	private void authenticateByCookieOrPopUp(List<IAuthenticatedWidget> widgetList) {
		reloadOnExpiredLogin();
		String userName = CookieSingleton.get().getCookie(ClientConstants.COOKIE_USERNAME);
		String password = CookieSingleton.get().getCookie(ClientConstants.COOKIE_PASSWORD);
		authenticateOrPopUp(userName, password, widgetList);
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


	private void authenticateOrPopUp(String userName, String password,
			List<IAuthenticatedWidget> widgetList) {
		final String fUserName = userName;
		final String fPassword = password;
		final List<IAuthenticatedWidget> fWidgetList = widgetList;
		AsyncCallback<Utenti> callback = new AsyncCallback<Utenti>() {
			@Override
			public void onFailure(Throwable caught) {
				WaitSingleton.get().stop();
				if (caught instanceof BusinessException) {
					new AuthPopUp("Errore di connessione al db", fWidgetList);
				} else {
					String message = caught.getMessage();
					if (message == null) message = "Autenticazione fallita";
					if (message.equals("")) message = "Autenticazione fallita";
					new AuthPopUp(message, fWidgetList);
				}
			}
			@Override
			public void onSuccess(Utenti result) {
				utente = result;
				saveCookie(fUserName, fPassword);
				unlockWidgets(result, fWidgetList);
				WaitSingleton.get().stop();
			}
		};
		try {
			if ((userName != null) && (password != null)) {
				WaitSingleton.get().start();
				authService.authenticate(fUserName, fPassword, callback);
			} else {
				new AuthPopUp("", fWidgetList);
			}
		} catch (Exception e) {
			//Will never be called because Exceptions will be caught by callback
			e.printStackTrace();
		}
	}
	
	
	
	// Inner Classes
	
	
	
	private class AuthPopUp extends PopupPanel {
		
		private List<IAuthenticatedWidget> widgetList = null;
		public String msg = "";
		
		public AuthPopUp(String msg, List<IAuthenticatedWidget> widgetList) {
			super(false);
			this.widgetList = widgetList;
			this.msg=msg;
			init();
		}
		
		private void init() {
			//UI
			this.setModal(true);
			this.setGlassEnabled(true);
			drawForm();
		}
		
		private void drawForm() {
			final FormPanel form = new FormPanel();
			FlexTable table = new FlexTable();
			int r=0;
			
			UiSingleton.get().getApgLoginImage(logoImage);
			table.setWidget(r, 0, logoImage);
			table.getFlexCellFormatter().setColSpan(r, 0, 2);
			r++;
			
			HTML title = new HTML();
			title.setHTML("<h3>Autenticazione</h3>");
			table.setWidget(r, 0, title);
			table.getFlexCellFormatter().setColSpan(r, 0, 2);
			r++;
			
			//Messaggio eventuale
			HTML message = new HTML(msg);
			message.setStyleName("message-error");
			table.setWidget(r, 0, message);
			table.getFlexCellFormatter().setColSpan(r, 0, 2);
			r++;
			
			//Tipo Anagrafica
			table.setHTML(r, 0, "Nome utente");
			userNameText = new TextBoxEnter(form);
			userNameText.setWidth("12em");
			userNameText.setTitle("username");
			userNameText.setName("username");
			table.setWidget(r, 1, userNameText);
			r++;
			
			//Tipo Anagrafica
			table.setHTML(r, 0, "Password");
			passwordPswd = new PasswordBoxEnter(form);
			passwordPswd.setWidth("12em");
			passwordPswd.setTitle("password");
			passwordPswd.setName("password");
			table.setWidget(r, 1, passwordPswd);
			r++;
			
			HorizontalPanel buttonPanel = new HorizontalPanel();
			// Bottone SALVA
			Button submitButton = new Button("Login");
			submitButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					form.submit();
				}
			});
			buttonPanel.add(submitButton);
			
			table.setWidget(r,0,buttonPanel);
			table.getFlexCellFormatter().setColSpan(r, 0, 2);
			
			form.add(table);
			this.add(form);
			form.addSubmitHandler(new SubmitHandler() {
				@Override
				public void onSubmit(SubmitEvent event) {
					close();
					authenticateOrPopUp(userNameText.getValue(), passwordPswd.getValue(), widgetList);
				}
			});
			this.center();
			this.show();
		}
		
		public void close() {
			this.hide();
			this.removeFromParent();
		}

	}
	
}
