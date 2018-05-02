package it.giunti.apg.client.widgets;

import it.giunti.apg.shared.AppConstants;

import java.util.Date;

import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DatePicker;

public class DateOnlyBox extends DateBox {

	
	private static int SECURITY_HOUR_OFFSET = 4;
	
	public DateOnlyBox() {
		super();
	}
	
	public DateOnlyBox(DatePicker picker, Date dt, Format format) {
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
