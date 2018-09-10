package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.widgets.PasswordBoxEnter;
import it.giunti.apg.client.widgets.TextBoxEnter;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;

public class LoginPopUp extends PopupPanel {
	
	private String msg = "";
	private List<IAuthenticatedWidget> widgetList = null;
	private AuthSingleton parent = null;

	private TextBoxEnter userNameText = null;
	private PasswordBoxEnter passwordPswd = null;
	private Image logoImage = new Image();
	
	public LoginPopUp(String msg, List<IAuthenticatedWidget> widgetList, AuthSingleton parent) {
		super(false);
		this.msg=msg;
		this.widgetList = widgetList;
		this.parent = parent;
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
				parent.processCredentials(userNameText.getValue(), passwordPswd.getValue(), widgetList);
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
