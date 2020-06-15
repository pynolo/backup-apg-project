package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.UriManager;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.widgets.DateSafeBox;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.client.widgets.tables.DataModel;
import it.giunti.apg.client.widgets.tables.OpzioniTable;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.Opzioni;
import it.giunti.apg.shared.model.Utenti;

import java.util.Date;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.VerticalPanel;

public class OpzioniFindFrame extends FramePanel implements IAuthenticatedWidget {
		
	private Date date;
	private boolean isOperator = false;
	private boolean isAdmin = false;
	
	private VerticalPanel panel = null;
	private DateSafeBox extractionDate = null;
	
	public OpzioniFindFrame(UriParameters params) {
		super();
		if (params == null) {
			params = new UriParameters();
		}
		date = params.getDateValue(AppConstants.PARAM_DATE);
		if (date == null) date = DateUtil.now();
		AuthSingleton.get().queueForAuthentication(this);
	}
	
	@Override
	public void onSuccessfulAuthentication(Utenti utente) {
		// Editing rights
		isOperator = (utente.getRuolo().getId() >= AppConstants.RUOLO_OPERATOR);
		isAdmin = (utente.getRuolo().getId() >= AppConstants.RUOLO_ADMIN);
		// UI
		if (isOperator) {
			panel = new VerticalPanel();
			this.add(panel, "Opzioni");
			if (date == null) {
				date = DateUtil.now();
			}
			drawResults(date, utente);
		}
	}
	
	private void drawResults(Date extractionDt, Utenti utente) {
		panel.clear();
		FlexTable topTable = new FlexTable();
		//Data
		topTable.setHTML(0, 0, "In vigore in data ");
		extractionDate = new DateSafeBox();
		extractionDate.setFormat(ClientConstants.BOX_FORMAT_DAY);
		extractionDate.setValue(date);
		extractionDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				UriParameters params = new UriParameters();
				params.add(AppConstants.PARAM_DATE, extractionDate.getValue());
				params.triggerUri(UriManager.OPZIONI_FIND);
			}
		});
		topTable.setWidget(0, 1, extractionDate);
		if (isAdmin) {
			//Fascicolo
			Anchor createOpzButton = new Anchor(ClientConstants.ICON_ADD+"Crea opzione", true);
			createOpzButton.addMouseDownHandler(new MouseDownHandler() {
				@Override
				public void onMouseDown(MouseDownEvent event) {
					UriParameters params = new UriParameters();
					params.add(AppConstants.PARAM_ID, AppConstants.NEW_ITEM_ID);
					params.triggerUri(UriManager.OPZIONE);
				}
			});
			topTable.setWidget(1, 0, createOpzButton);
		}
		panel.add(topTable);
		DataModel<Opzioni> model = new OpzioniTable.OpzioniModel(extractionDt);
		OpzioniTable abbTable = new OpzioniTable(model, utente);
		panel.add(abbTable);
	}

}
