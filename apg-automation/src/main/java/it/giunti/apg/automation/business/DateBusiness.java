package it.giunti.apg.automation.business;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateBusiness {
	
	//le ore 00:01 del giorno
	public static Date dayStart(Date day) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(day);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 1);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime();
	}
	//Le 23:59 del giorno
	public static Date dayEnd(Date day) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(day);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime();
	}
	//Le 23:59 di ieri
	public static Date previousDayEnd(Date day) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(day);
		cal.set(Calendar.HOUR_OF_DAY, 1);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 0);
		cal.add(Calendar.HOUR_OF_DAY, -2);
		return cal.getTime();
	}
	//le ore 00:01 del giorno 1 del mese corrente
	public static Date monthStart(Date day) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(day);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 1);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime();
	}
	//Le 23:59 dell'ultimo giorno del mese corrente
	public static Date monthEnd(Date day) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(day);
		cal.add(Calendar.MONTH, 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 1);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 0);
		cal.add(Calendar.HOUR_OF_DAY, -2);
		return cal.getTime();
	}
	//le ore 00:01 del giorno 1 del mese scorso
	public static Date previousMonthStart(Date day) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(day);
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 1);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime();
	}
	//le ore 00:01 del giorno 1 dell'inizio del periodo (trimestre, quadrimestre, semestre)
	public static Date previousYearlyPeriodStart(Date day, int monthsInPeriod) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(day);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 1);
		cal.set(Calendar.SECOND, 0);
		cal.add(Calendar.MONTH, -1*monthsInPeriod);
		int month = cal.get(Calendar.MONTH);//January=0;
		Double periodD = Math.floor(month/monthsInPeriod);
		int period = periodD.intValue();
		cal.set(Calendar.MONTH, monthsInPeriod*period);
		return cal.getTime();
	}
	//Le 23:59 dell'ultimo giorno del mese scorso
	public static Date previousMonthEnd(Date day) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(day);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 1);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 0);
		cal.add(Calendar.HOUR_OF_DAY, -2);
		return cal.getTime();
	}
	//Le 23:59 dell'ultimo giorno del periodo (trimestre, quadrimestre, semestre)
	public static Date previousYearlyPeriodEnd(Date day, int monthsInPeriod) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(day);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 1);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 0);
		cal.add(Calendar.MONTH, -1*monthsInPeriod);
		int month = cal.get(Calendar.MONTH);//January=0;
		Double periodD = Math.floor(month/monthsInPeriod);
		int period = periodD.intValue();
		cal.set(Calendar.MONTH, monthsInPeriod*(period+1));
		cal.add(Calendar.HOUR_OF_DAY, -2);
		return cal.getTime();
	}
	//le ore 00:01 di un mese fa
	public static Date oneMonthAgoStart(Date day) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(day);
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 1);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime();
	}
	//le ore 00:01 del giorno 1 gennaio anno corrente
	public static Date yearStart(Date day) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(day);
		cal.set(Calendar.DAY_OF_YEAR, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 1);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime();
	}
	//le ore 00:01 del giorno 1 dell'anno scorso
	public static Date previousYearStart(Date day) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(day);
		cal.add(Calendar.YEAR, -1);
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 1);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime();
	}
	//Le 23:59 dell'ultimo giorno dell'anno scorso
	public static Date previousYearEnd(Date day) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(day);
		cal.set(Calendar.MONTH, 0);//Current year
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 1);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 0);
		cal.add(Calendar.HOUR_OF_DAY, -2);
		return cal.getTime();
	}

}
