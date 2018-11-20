package it.giunti.apg.client.frames;

import java.util.Date;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DateBox;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.UriManager;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.client.widgets.tables.DataModel;
import it.giunti.apg.client.widgets.tables.FattureInvioSapTable;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.FattureInvioSap;
import it.giunti.apg.shared.model.Utenti;

public class FattureInvioFindFrame extends FramePanel implements IAuthenticatedWidget {

	private Utenti utente = null;
	private Date date = null;
	
	private VerticalPanel mainPanel = null;
	private FattureInvioSapTable pcTable = null;
	private DateBox extractionDate = null;
	
	public FattureInvioFindFrame(UriParameters params) {
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
		this.utente = utente;
		// UI
		if (utente.getRuolo().getId() >= AppConstants.RUOLO_EDITOR) {
			draw();
		}
	}
	
	private void draw() {
		mainPanel = new VerticalPanel();
		this.add(mainPanel, "Fatturazione elettronica - SAP");

		HorizontalPanel topPanel = new HorizontalPanel();
		//Data estrazione
		topPanel.add(new InlineHTML("&nbsp;A partire dal&nbsp;"));
		extractionDate = new DateBox();
		extractionDate.setFormat(ClientConstants.BOX_FORMAT_DAY);
		extractionDate.setValue(date);
		extractionDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				UriParameters params = new UriParameters();
				params.add(AppConstants.PARAM_DATE, extractionDate.getValue());
				params.triggerUri(UriManager.FATTURE_INVIO_FIND);
			}
		});
		topPanel.add(extractionDate);
		mainPanel.add(topPanel);
		
		long start = date.getTime();
		long finish = start + AppConstants.MONTH * 120;
		DataModel<FattureInvioSap> model = new FattureInvioSapTable
				.FattureInvioSapModel(start, finish);
		pcTable = new FattureInvioSapTable(model, utente.getRuolo());
		mainPanel.add(pcTable);
	}


}
