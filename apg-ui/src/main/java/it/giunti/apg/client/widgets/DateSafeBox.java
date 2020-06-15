package it.giunti.apg.client.widgets;

import java.util.Date;

import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DatePicker;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.shared.AppConstants;

public class DateSafeBox extends DateBox {

	
	private static int SECURITY_HOUR_OFFSET = 4;
	
	public DateSafeBox() {
		super();
		this.setFormat(ClientConstants.BOX_FORMAT_DAY);//default
	}
	
	public DateSafeBox(DatePicker picker, Date dt, Format format) {
		super(picker, moveToSafeTime(dt), format);
	}
	
	@Override
	public void setValue(Date date) {
		super.setValue(moveToSafeTime(date));
	}
	
	private static Date moveToSafeTime(Date dt) {
		if (dt != null) {
			Long longDt = dt.getTime();
			longDt += AppConstants.HOUR*SECURITY_HOUR_OFFSET;
			return new Date(longDt);
		} else return null;
	}

	@Override
	public Date getValue() {
		Date value = super.getValue();
		if (value != null) {
			return moveToSafeTime(value);
		} else return null;
	}
	
}
