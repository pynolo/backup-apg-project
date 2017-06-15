package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.client.widgets.tables.AvvisiReadonlyTable;
import it.giunti.apg.client.widgets.tables.DataModel;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Avvisi;
import it.giunti.apg.shared.model.Utenti;

import com.google.gwt.user.client.ui.FlowPanel;

public class AvvisiListFrame extends FramePanel implements IAuthenticatedWidget {
	
	private FlowPanel fPanel = null;
	private AvvisiReadonlyTable aTable = null;
	private boolean isOperator = false;
	
	// METHODS
	
	public AvvisiListFrame(UriParameters params) {
		super();
		if (params == null) {
			params = new UriParameters();
		}
		AuthSingleton.get().queueForAuthentication(this);
	}
	
	@Override
	public void onSuccessfulAuthentication(Utenti utente) {
		init(utente);
	}
	
	private void init(Utenti utente) {
		// Editing rights
		int ruolo = utente.getRuolo().getId();
		isOperator = (ruolo >= AppConstants.RUOLO_OPERATOR);
		// UI
		if (isOperator) {
			draw();
		}
	}
	
	private void draw() {
		fPanel = new FlowPanel();
		this.add(fPanel, "Ultimi avvisi");

		if (aTable != null) {
			fPanel.remove(aTable);
			aTable=null;
		}
		DataModel<Avvisi> model = new AvvisiReadonlyTable.AvvisiModel();
		aTable = new AvvisiReadonlyTable(model);
		fPanel.add(aTable);
	}
}
