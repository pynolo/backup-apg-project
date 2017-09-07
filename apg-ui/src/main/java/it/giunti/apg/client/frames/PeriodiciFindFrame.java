package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.UriManager;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.client.widgets.tables.DataModel;
import it.giunti.apg.client.widgets.tables.PeriodiciTable;
import it.giunti.apg.core.DateUtil;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Periodici;
import it.giunti.apg.shared.model.Utenti;

import java.util.Date;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DateBox;

public class PeriodiciFindFrame extends FramePanel implements IAuthenticatedWidget {
	
	private Date date=null;
	
	private VerticalPanel panel = null;
	private DateBox extractionDate = null;
	
	public PeriodiciFindFrame(UriParameters params) {
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
		int ruolo = utente.getRuolo().getId();
		// UI
		if (ruolo >= AppConstants.RUOLO_OPERATOR) {
			panel = new VerticalPanel();
			this.add(panel, "Periodici");
			if (date == null) {
				date = DateUtil.now();
			}
			drawResults(date);
		}
	}
	
	private void drawResults(Date extractionDt) {
		panel.clear();
		FlexTable table = new FlexTable();
		//Data
		table.setHTML(0, 0, "In vigore in data ");
		extractionDate = new DateBox();
		extractionDate.setFormat(ClientConstants.BOX_FORMAT_DAY);
		extractionDate.setValue(date);
		extractionDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				UriParameters params = new UriParameters();
				params.add(AppConstants.PARAM_DATE, extractionDate.getValue());
				params.triggerUri(UriManager.PERIODICI_FIND);
			}
		});
		table.setWidget(0, 1, extractionDate);
		panel.add(table);
		DataModel<Periodici> model = new PeriodiciTable.PeriodiciModel(extractionDt);
		PeriodiciTable perTable = new PeriodiciTable(model);
		panel.add(perTable);
	}

}
