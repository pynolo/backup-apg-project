package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.client.widgets.tables.DataModel;
import it.giunti.apg.client.widgets.tables.UtentiTable;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Utenti;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class UtentiFindFrame extends FramePanel implements IAuthenticatedWidget {
	
	private boolean isAdmin = false;
	//private boolean isSuper = false;
	private boolean showBlocked = false;
	
	private VerticalPanel panel = null;
	private UtentiTable uTable = null;
	private CheckBox showBlockedBox = null;
	
	public UtentiFindFrame(UriParameters params) {
		super();
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
		//isSuper = (ruolo >= AppConstants.RUOLO_SUPER);
		// UI
		if (isAdmin) {
			draw(utente);
		}
	}
	
	private void draw(Utenti utente) {
		panel = new VerticalPanel();
		this.add(panel, "Utenti");
		DataModel<Utenti> model = new UtentiTable.UtentiModel(showBlocked);
		uTable = new UtentiTable(model, utente);
		if (isAdmin) {
			Anchor createButton = new Anchor(ClientConstants.ICON_ADD+"Crea utente", true);
			createButton.addMouseDownHandler(new MouseDownHandler() {
				@Override
				public void onMouseDown(MouseDownEvent event) {
					new UtentePopUp("", uTable);
				}
			});
			panel.add(createButton);
		}
		//Bloccati
		showBlockedBox = new CheckBox("Mostra anche i bloccati");
		showBlockedBox.setValue(showBlocked);
		showBlockedBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> arg0) {
				showBlocked = showBlockedBox.getValue();
				uTable.setModel(new UtentiTable.UtentiModel(showBlocked));
			}
		});
		panel.add(showBlockedBox);
		
		panel.add(uTable);
	}


}
