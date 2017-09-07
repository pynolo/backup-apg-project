package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.UriManager;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.client.widgets.tables.ArticoliTable;
import it.giunti.apg.client.widgets.tables.DataModel;
import it.giunti.apg.core.DateUtil;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Articoli;
import it.giunti.apg.shared.model.Utenti;

import java.util.Date;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DateBox;

public class ArticoliFindFrame extends FramePanel implements IAuthenticatedWidget {
	
	private Date date;
	private boolean isOperator = false;
	private boolean isEditor = false;
	
	private VerticalPanel panel = null;
	private DateBox extractionDate = null;
	
	public ArticoliFindFrame(UriParameters params) {
		super();
		if (params == null) {
			params = new UriParameters();
		}
		date =  params.getDateValue(AppConstants.PARAM_DATE);
		if (date == null) {
			date = DateUtil.now();
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
		isEditor = (ruolo >= AppConstants.RUOLO_EDITOR);
		// UI
		if (isOperator) {
			panel = new VerticalPanel();
			this.add(panel, "Articoli");
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
				params.triggerUri(UriManager.DONI_FIND);
			}
		});
		table.setWidget(0, 1, extractionDate);
		panel.add(table);
		
		DataModel<Articoli> model = new ArticoliTable.ArticoliModel(extractionDt);
		final ArticoliTable regTable = new ArticoliTable(model, isEditor);
		
		if (isEditor) {
			Anchor nuovoLink = new Anchor(ClientConstants.ICON_ADD+"Crea articolo", true);
			nuovoLink.addMouseDownHandler(new MouseDownHandler() {
				@Override
				public void onMouseDown(MouseDownEvent event) {
					new ArticoloPopUp(AppConstants.NEW_ITEM_ID, regTable);
				}
			});
			panel.add(nuovoLink);
		}
		panel.add(regTable);
	}

}
