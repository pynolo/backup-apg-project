package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.client.widgets.tables.DataModel;
import it.giunti.apg.client.widgets.tables.IstanzeAbbonamentiTable;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Utenti;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AbbonamentiQuickSearchFrame extends FramePanel
		implements IAuthenticatedWidget {
		
	private UriParameters params;
	private String searchString = "";
	
	private VerticalPanel panel = null;
	private VerticalPanel abbPanel = null;
	private IstanzeAbbonamentiTable abbTable = null;
	
	public AbbonamentiQuickSearchFrame(UriParameters params) {
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
		//Pannello abbonamenti
		abbPanel = new VerticalPanel();
		HTML abbTitle = new HTML("Abbonamenti");
		abbTitle.setStyleName("section-title");
		abbPanel.add(abbTitle);
		DataModel<IstanzeAbbonamenti> model = new IstanzeAbbonamentiTable.FindIstanzeModel(searchString);
		abbTable = new IstanzeAbbonamentiTable(model, true, null);
		abbPanel.add(abbTable);
		panel.add(abbPanel);
	}
	
	//@Override
	//public void refresh() {
	//	if (abbTable != null) {
	//		if (abbTable.isEmpty()) {
	//			abbPanel.setVisible(false);
	//		}
	//	}
	//}
}
