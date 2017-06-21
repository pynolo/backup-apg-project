package it.giunti.apg.core.business;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MonthBusiness {

//	private static MonthBusiness instance = null;
//	private Map<Integer,Month> monthMap;
//	
//	public static MonthBusiness getInstance() {
//		if (instance == null) instance = new MonthBusiness();
//		return instance;
//	}
//	
//	private MonthBusiness() {
//		monthMap = new HashMap<Integer,MonthBusiness.Month>();
//
//		Calendar cal = new GregorianCalendar();
//		cal.add(Calendar.YEAR, 10);
//		Date stopDt = cal.getTime();
//		cal.add(Calendar.YEAR, -20);
//		int i=0;
//		do {
//			Month m = new Month(cal.get(Calendar.MONTH), cal.get(Calendar.YEAR));
//			monthMap.put(i, m);
//			i++;
//			cal.add(Calendar.MONTH, 1);
//		} while (cal.getTime().before(stopDt));
//	}
//	
//	private Month getMonthByDate(Date dt) {
//		Calendar cal = new GregorianCalendar();
//		cal.setTime(dt);
//		Month m = new Month(cal.get(Calendar.MONTH), cal.get(Calendar.YEAR));
//		return m;
//	}
	
	public static Integer getMonthsToSpecificMonth(Date date, int month) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(getFirstDayOfMonth(date));
		int i = 0;
		while (cal.get(Calendar.MONTH) != month) {
			cal.add(Calendar.MONTH, 1);
			i++;
		}
		return i;
	}
	
	public static Date getFirstDayOfSpecificMonth(Date date, int month) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(getFirstDayOfMonth(date));
		while (cal.get(Calendar.MONTH) != month) {
			cal.add(Calendar.MONTH, 1);
		}
		return cal.getTime();
	}
	
	public static Date getFirstDayOfPastMonth(Date date, int month) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(getFirstDayOfMonth(date));
		while (cal.get(Calendar.MONTH) != month) {
			cal.add(Calendar.MONTH, -1);
		} 
		return cal.getTime();
	}
	
	public static Date getFirstDayOfMonth(Date date) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 1);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	
//	// Inner classes
//	
//	public static class Month {
//		Integer dateMonth;
//		Integer dateYear;
//				
//		public Month(Integer dateMonth, Integer dateYear) {
//			this.dateMonth=dateMonth;
//			this.dateYear=dateYear;
//		}
//		
//		public Date getFirstDay() {
//			Calendar cal = new GregorianCalendar();
//			cal.set(Calendar.YEAR, dateYear);
//			cal.set(Calendar.MONTH, dateMonth+1);
//			cal.set(Calendar.DAY_OF_MONTH, 1);
//			cal.set(Calendar.HOUR, 0);
//			cal.set(Calendar.MINUTE, 1);
//			cal.set(Calendar.MILLISECOND, 0);
//			return cal.getTime();
//		}
//	}
	
}
