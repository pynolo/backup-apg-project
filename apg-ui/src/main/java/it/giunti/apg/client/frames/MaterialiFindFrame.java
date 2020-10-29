package it.giunti.apg.client.frames;

import java.util.Date;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.UriManager;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.widgets.DateSafeBox;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.client.widgets.tables.DataModel;
import it.giunti.apg.client.widgets.tables.MaterialiTable;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.Materiali;
import it.giunti.apg.shared.model.Utenti;

public class MaterialiFindFrame extends FramePanel implements IAuthenticatedWidget {
	
	private String search;
	private Date date;
	private boolean isOperator = false;
	private boolean isEditor = false;
	
	private VerticalPanel panel = null;
	private TextBox searchText = null;
	private DateSafeBox extractionDate = null;
	
	public MaterialiFindFrame(UriParameters params) {
		super();
		if (params == null) {
			params = new UriParameters();
		}
		date =  params.getDateValue(AppConstants.PARAM_DATE);
		if (date == null) {
			date = DateUtil.now();
		}
		search =  params.getValue(AppConstants.PARAM_QUICKSEARCH);
		if (search == null) {
			search = "";
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
			this.add(panel, "Materiali");
			drawResults(search, date);
		}
	}
	
	private void drawResults(String search, Date extractionDt) {
		panel.clear();
		FlexTable table = new FlexTable();
		//Search
		table.setHTML(0, 0, "Filtro ");
		searchText = new TextBox();
		searchText.setValue(search);
		table.setWidget(0, 1, searchText);
		//Data
		table.setHTML(0, 2, " in data ");
		extractionDate = new DateSafeBox();
		extractionDate.setFormat(ClientConstants.BOX_FORMAT_DAY);
		extractionDate.setValue(date);
		extractionDate.setWidth("8em");
		table.setWidget(0, 3, extractionDate);
		//bottone
		Button button = new Button(" Cerca ");
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				UriParameters params = new UriParameters();
				params.add(AppConstants.PARAM_DATE, extractionDate.getValue());
				params.add(AppConstants.PARAM_QUICKSEARCH, searchText.getValue());
				params.triggerUri(UriManager.MATERIALI_FIND);
			}
		});
		table.setWidget(0, 4, button);
		panel.add(table);
		
		DataModel<Materiali> model = new MaterialiTable.MaterialiModel(search, extractionDt);
		final MaterialiTable regTable = new MaterialiTable(model, isEditor);
		
		if (isEditor) {
			Anchor nuovoLink = new Anchor(ClientConstants.ICON_ADD+"Crea materiale", true);
			nuovoLink.addMouseDownHandler(new MouseDownHandler() {
				@Override
				public void onMouseDown(MouseDownEvent event) {
					new MaterialiPopUp(AppConstants.NEW_ITEM_ID, regTable);
				}
			});
			panel.add(nuovoLink);
		}
		panel.add(regTable);
	}

}
