package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.AuthService;
import it.giunti.apg.client.services.AuthServiceAsync;
import it.giunti.apg.client.widgets.select.RuoliSelect;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.model.Ruoli;
import it.giunti.apg.shared.model.Utenti;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;

public class UtentePopUp extends PopupPanel implements IAuthenticatedWidget {

	private final AuthServiceAsync authService = GWT.create(AuthService.class);
	
	private FlexTable table = new FlexTable();
	private String idUtente = null;
	private Utenti item = null;
	private boolean isAdmin = false;
	private boolean isSuper = false;
	private IRefreshable parent = null;
	
	private TextBox usernameText = null;
	private TextBox descrizioneText = null;
	private PasswordTextBox passwordText = null;
	private TextBox allowedMagazinesText = null;
	private RuoliSelect ruoliList = null;
	private CheckBox ldapCheck = null;
	
	public UtentePopUp(String idUtente, IRefreshable parent) {
		super(false);
		this.idUtente=idUtente;
		this.parent=parent;
		AuthSingleton.get().queueForAuthentication(this);
	}
	
	public UtentePopUp(IRefreshable parent) {
		super(false);
		this.idUtente="";
		this.parent=parent;
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
		isSuper = (ruolo >= AppConstants.RUOLO_SUPER);
		// UI
		if (isAdmin) {
			this.setModal(true);
			this.setGlassEnabled(true);
			this.add(table);
			//Load utente
			if (idUtente == null) idUtente = "";
			if (idUtente != "") {
				loadUtente(idUtente);
			} else {
				createUtente();
				drawUtente();
			}
		}
	}
	
	private void drawUtente() {
		int r=0;
		
		HTML titleHtml = new HTML("Utente");
		titleHtml.setStyleName("frame-title");
		table.setWidget(r, 0, titleHtml);
		table.getFlexCellFormatter().setColSpan(r, 0, 5);
		r++;
		
		//Username
		table.setHTML(r, 0, "Nome utente"+ClientConstants.MANDATORY);
		usernameText = new TextBox();
		usernameText.setValue(item.getId());
		usernameText.setMaxLength(32);
		if ((item.getId() == null) && isAdmin) {
			usernameText.setEnabled(true);
		} else {
			usernameText.setEnabled(false);
		}
		table.setWidget(r, 1, usernameText);
		r++;
		
		//Ruolo
		table.setHTML(r, 0, "Ruolo");
		ruoliList = new RuoliSelect(item.getRuolo().getId());
		ruoliList.setEnabled(isAdmin);
		table.setWidget(r, 1, ruoliList);
		r++;
		
		//Intranet
		table.setHTML(r, 0, "Utente intranet");
		ldapCheck = new CheckBox();
		ldapCheck.setEnabled(isAdmin);
		ldapCheck.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				toggleLdapValue(ldapCheck.getValue());
			}
		});
		table.setWidget(r, 1, ldapCheck);
		r++;
		
		//Password
		table.setHTML(r, 0, "Password");
		passwordText = new PasswordTextBox();
		passwordText.setValue(item.getPassword());
		passwordText.setMaxLength(32);
		passwordText.setEnabled(isAdmin);
		table.setWidget(r, 1, passwordText);
		r++;

		//Password
		table.setHTML(r, 0, "Uid periodici permessi");
		allowedMagazinesText = new TextBox();
		allowedMagazinesText.setValue(item.getPeriodiciUidRestriction());
		allowedMagazinesText.setMaxLength(64);
		allowedMagazinesText.setEnabled(isAdmin);
		table.setWidget(r, 1, allowedMagazinesText);
		r++;
		
		table.setHTML(r, 1, "<i>Valori separati da \";\" Nessun uid = tutto permesso.</i>");
		//table.getFlexCellFormatter().setColSpan(r, 0, 5);
		r++;
		
		//Note
		table.setHTML(r, 0, "Note");
		descrizioneText = new TextBox();
		descrizioneText.setValue(item.getDescrizione());
		descrizioneText.setMaxLength(64);
		descrizioneText.setWidth("18em");
		descrizioneText.setEnabled(isAdmin);
		table.setWidget(r, 1, descrizioneText);
		r++;
				
		//Impostazione valore checkbox
		if (item.getPassword() == null) {
			toggleLdapValue(true);
		} else {
			if (item.getPassword().equals("")) {
				toggleLdapValue(true);
			}
		}
		
		HorizontalPanel buttonPanel = new HorizontalPanel();
		// Bottone SALVA
		Button submitButton = new Button("Salva", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				try {
					saveData();
					close();
				} catch (Exception e) {
					UiSingleton.get().addError(e);
				}
			}
		});
		if (idUtente.equals("")) {
			submitButton.setText("Crea");
		}
		submitButton.setEnabled(isAdmin);
		buttonPanel.add(submitButton);
		
		// Bottone ANNULLA
		Button cancelButton = new Button("Annulla", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				close();
			}
		});
		buttonPanel.add(cancelButton);
		
		table.setWidget(r,0,buttonPanel);
		table.getFlexCellFormatter().setColSpan(r, 0, 5);
		
		this.center();
		this.show();
	}
	
	private void toggleLdapValue(boolean isLdap) {
		if ((passwordText != null) && (ldapCheck != null)) {
			ldapCheck.setValue(isLdap);
			passwordText.setEnabled(!isLdap && isAdmin);
		}
	}
	
	private void createUtente() {
		item = new Utenti();
		item.setRuolo(new Ruoli());
	}
	
	private void close() {
		this.hide();
	}
	
	
	
	// METODI ASINCRONI
	
	
	
	private void saveData() throws ValidationException {
		AsyncCallback<String> callback = new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				close();
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(String result) {			
				idUtente = result;
				parent.refresh();
				close();
				UiSingleton.get().addInfo(AppConstants.MSG_SAVE_OK);
				WaitSingleton.get().stop();
			}
		};
		if (passwordText.getValue().length() == 0 && !ldapCheck.getValue())
			throw new ValidationException("La password &egrave; obbligatoria per gli utenti non intranet");
		item.setNewId(usernameText.getValue().trim());
		if (ldapCheck.getValue()) {
			item.setPassword("");
		} else {
			item.setPassword(passwordText.getValue());
		}
		item.setDescrizione(descrizioneText.getValue());
		String periodiciUidRestriction = allowedMagazinesText.getValue();
		if (periodiciUidRestriction != null) periodiciUidRestriction = periodiciUidRestriction.toUpperCase();
		item.setPeriodiciUidRestriction(periodiciUidRestriction);
		if (ruoliList.getSelectedValueInt() < AppConstants.RUOLO_SUPER ||
				isSuper) {
			item.setIdRuoloT(ruoliList.getSelectedValueString());
		} else {
			UiSingleton.get().addWarning("Diritti non sufficienti ad assegnare il ruolo");
		}
		WaitSingleton.get().start();
		authService.saveOrUpdate(item, callback);
	}

	private void loadUtente(String idUtente) {
		AsyncCallback<Utenti> callback = new AsyncCallback<Utenti>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(Utenti result) {
				item = result;
				drawUtente();
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		authService.findUtenteByUserName(idUtente, callback);
	}
	
}
