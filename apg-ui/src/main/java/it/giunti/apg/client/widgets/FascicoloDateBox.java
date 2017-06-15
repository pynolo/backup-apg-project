package it.giunti.apg.client.widgets;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.services.FascicoliService;
import it.giunti.apg.client.services.FascicoliServiceAsync;
import it.giunti.apg.shared.model.Fascicoli;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.datepicker.client.DateBox;

public class FascicoloDateBox extends FlowPanel {
	
	private final FascicoliServiceAsync fascicoliService = GWT.create(FascicoliService.class);
	private Integer idPeriodico = null;
	
	private final DateBox dateBox;
	private final InlineHTML html;
	
	public FascicoloDateBox(Integer idPeriodico) {
		this.idPeriodico=idPeriodico;
		dateBox = new DateBox();
		html = new InlineHTML();
		init();
	}
	
	private void init() {
		dateBox.setFormat(ClientConstants.BOX_FORMAT_MONTH);
		dateBox.setWidth("7em");
		dateBox.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> arg0) {
				refresh();
			}
		});
		this.add(dateBox);
		this.add(html);
	}
	
	public void setValue(Date dt) {
		this.dateBox.setValue(dt);
		refresh();
	}
	
	public Date getValue() {
		return this.dateBox.getValue();
	}
	
	public void setIdPeriodico(Integer idPeriodico) {
		this.idPeriodico = idPeriodico;
		refresh();
	}
	
	public Integer getIdPeriodico() {
		return this.idPeriodico;
	}
	
	private void refresh() {
		Date value = dateBox.getValue();
		AsyncCallback<Fascicoli> callback = new AsyncCallback<Fascicoli>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
			}
			@Override
			public void onSuccess(Fascicoli result) {
				if (result != null) {
					html.setHTML("&nbsp;<b>n&deg;&nbsp;"+result.getTitoloNumero()+"</b>");
				} else {
					html.setHTML("");
				}
			}
		};
		if (value != null) {
			fascicoliService.findFascicoloByPeriodicoDataInizio(idPeriodico, value, callback);
		}
	}
}
