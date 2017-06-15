package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.client.widgets.tables.AnagraficheTable;
import it.giunti.apg.client.widgets.tables.DataModel;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.Utenti;

import com.google.gwt.user.client.ui.VerticalPanel;

public class AnagraficheModifiedFindFrame extends FramePanel
		implements IRefreshable, IAuthenticatedWidget {
	
	private VerticalPanel panel = null;
	private VerticalPanel anaPanel = null;
	private AnagraficheTable anaTable = null;
	
	public AnagraficheModifiedFindFrame(UriParameters params) {
		super();
		AuthSingleton.get().queueForAuthentication(this);
	}
	
	@Override
	public void onSuccessfulAuthentication(Utenti utente) {
		init(utente);
	}
	
	private void init(Utenti utente) {
		int ruolo = utente.getRuolo().getId();
		// UI
		if (ruolo >= AppConstants.RUOLO_OPERATOR) {
			draw();
		}
	}
	
	private void draw() {
		panel = new VerticalPanel();
		this.add(panel, "Anagrafiche modificate");
		panel.clear();
		//Pannello anagrafiche
		anaPanel = new VerticalPanel();
		DataModel<Anagrafiche> anaModel = new AnagraficheTable.LastModifiedModel();
		anaTable = new AnagraficheTable(anaModel, false, this);
		anaPanel.add(anaTable);
		panel.add(anaPanel);
	}
	
	@Override
	public void refresh() {
		if (anaTable != null) {
			if (anaTable.isEmpty()) {
				anaPanel.setVisible(false);
			}
		}
	}
}
