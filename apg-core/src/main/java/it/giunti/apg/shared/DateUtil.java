package it.giunti.apg.shared;

import java.util.Date;

public class DateUtil {

	public static Date now() {
		return new Date();
		//Date dt = new Date();
		//dt.setDate(4);
		//dt.setMonth(0);//month: 0-11
		//dt.setYear(119);
		//return dt;
	}
	
	@SuppressWarnings("deprecation")
	public static Date longAgo() {
		Date dt = new Date();
		dt.setDate(1);
		dt.setMonth(0);//month: 0-11
		dt.setYear(70);
		dt.setHours(0);
		dt.setMinutes(0);
		dt.setSeconds(1);
		return dt;
	}
}
