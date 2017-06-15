package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.UriManager;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.client.widgets.tables.DataModel;
import it.giunti.apg.client.widgets.tables.ModelliEmailTable;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.ModelliEmail;
import it.giunti.apg.shared.model.Utenti;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineHTML;

public class ModelliEmailFindFrame extends FramePanel implements IAuthenticatedWidget {
	
	private boolean isAdmin = false;
	
	private FlowPanel topPanel = null;
	private ModelliEmailTable bTable = null;
	
	// METHODS
	
	public ModelliEmailFindFrame(UriParameters params) {
		super();
		if (params == null) {
			params = new UriParameters();
		}
		this.setWidth("100%");
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
			draw();
		}
	}
	
	private void draw() {
		topPanel = new FlowPanel();
		this.add(topPanel, "Modelli per email");

		if (isAdmin) {
			topPanel.add(new InlineHTML("<br/>"));
			UriParameters params = new UriParameters();
			params.add(AppConstants.PARAM_ID, AppConstants.NEW_ITEM_ID);
			Hyperlink nuovoModelloLink = params.getHyperlink(
					ClientConstants.ICON_ADD+"Crea modello", UriManager.MODELLI_EMAIL);
			topPanel.add(nuovoModelloLink);
		}
		
		changePeriodico();
	}
	
	
	private void changePeriodico() {
		if (bTable != null) {
			topPanel.remove(bTable);
			bTable=null;
		}
		DataModel<ModelliEmail> model = new ModelliEmailTable.ModelliEmailModel();
		bTable = new ModelliEmailTable(model);
		topPanel.add(bTable);
	}
	
}
