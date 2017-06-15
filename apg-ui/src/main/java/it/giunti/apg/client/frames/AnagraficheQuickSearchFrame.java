package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.client.widgets.tables.AnagraficheTable;
import it.giunti.apg.client.widgets.tables.DataModel;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.Utenti;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AnagraficheQuickSearchFrame extends FramePanel
		implements IAuthenticatedWidget {
		
	private UriParameters params;
	private String searchString = "";
	
	private VerticalPanel panel = null;
	private VerticalPanel anaPanel = null;
	private AnagraficheTable anaTable = null;
	
	public AnagraficheQuickSearchFrame(UriParameters params) {
		super();
		if (params != null) {
			this.params = params;
		} else {
			this.params = new UriParameters();
		}
		String value = this.params.getValue(AppConstants.PARAM_QUICKSEARCH);
		if (value != null) {
			searchString = value;
			AuthSingleton.get().queueForAuthentication(this);
		}
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
		this.add(panel, "Risultati");
		panel.clear();
		//Pannello anagrafiche
		anaPanel = new VerticalPanel();
		HTML anaTitle = new HTML("Anagrafiche");
		anaTitle.setStyleName("section-title");
		anaPanel.add(anaTitle);
		DataModel<Anagrafiche> anaModel = new AnagraficheTable.QuickSearchModel(searchString);
		anaTable = new AnagraficheTable(anaModel, true, null);
		anaPanel.add(anaTable);
		panel.add(anaPanel);
	}
	
	//@Override
	//public void refresh() {
	//	if (anaTable != null) {
	//		if (anaTable.isEmpty()) {
	//			anaPanel.setVisible(false);
	//		}
	//	}
	//}
}
