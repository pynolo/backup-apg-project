package it.giunti.apg.client.frames;

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

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.widgets.PasswordBoxEnter;
import it.giunti.apg.shared.model.Utenti;

public class PasswordChangePopUp extends PopupPanel {
	
	private String msg = "";
	private Utenti utente = null;
	private AuthSingleton parent = null;

	private PasswordBoxEnter pswBox1 = null;
	private PasswordBoxEnter pswBox2 = null;
	private Image logoImage = new Image();
	
	public PasswordChangePopUp(String msg, Utenti utente, AuthSingleton parent) {
		super(false);
		this.msg=msg;
		this.utente = utente;
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
		title.setHTML("<h3>Cambio password per "+utente.getId()+"</h3>");
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
		table.setHTML(r, 0, "Password");
		pswBox1 = new PasswordBoxEnter(form);
		pswBox1.setWidth("12em");
		pswBox1.setTitle("password");
		pswBox1.setName("password1");
		table.setWidget(r, 1, pswBox1);
		r++;
		//Tipo Anagrafica
		table.setHTML(r, 0, "Ripeti la password");
		pswBox2 = new PasswordBoxEnter(form);
		pswBox2.setWidth("12em");
		pswBox2.setTitle("password");
		pswBox2.setName("password2");
		table.setWidget(r, 1, pswBox2);
		r++;
		
		HorizontalPanel buttonPanel = new HorizontalPanel();
		// Bottone SALVA
		Button submitButton = new Button("Cambia password");
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
				if (pswBox1.getValue() != null && pswBox2.getValue() != null) {
					if (pswBox1.getValue().length() >= 1 && pswBox2.getValue().length() >= 1) {
						close();
						parent.processNewPassword(pswBox1.getValue(), pswBox2.getValue());
					}
				}
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
