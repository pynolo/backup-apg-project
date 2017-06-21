package it.giunti.apg.core;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ServerUtil {

	public static Date getMonthFirstDay(Date input) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(input);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return cal.getTime();
	}
	
	public static Date getMonthLastDay(Date input) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(input);
		cal.add(Calendar.MONTH, 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		return cal.getTime();
	}
	
	/** Se il mese "input" precede il meseInizio-MESE_ANTICIPO allora sceglie l'anno precedente
	 * ed imposta il meseInizio
	 * @param meseInizio
	 * @param input
	 * @return
	 */
	public static Date getInizioByMonth(Integer meseInizio, Date input) {
		int MESI_ANTICIPO = 3;
		Calendar calInput = new GregorianCalendar();
		calInput.setTime(input);
		calInput.add(Calendar.MONTH, MESI_ANTICIPO);
		Date shiftedInput = calInput.getTime();
		Calendar calInizio = new GregorianCalendar();
		calInizio.set(Calendar.MONTH, meseInizio-1);
		calInizio.set(Calendar.DAY_OF_MONTH, 1);
		Date inizioSucc = calInizio.getTime();
		calInizio.add(Calendar.YEAR, -1);
		Date inizioPrec = calInizio.getTime();
		if (shiftedInput.after(inizioSucc)) {
			return inizioSucc;
		}
		return inizioPrec;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> void pojoToUppercase(T pojo) {
		Class<T> c = (Class<T>) pojo.getClass();
		Method[] methods = c.getDeclaredMethods();
		for (Method m:methods) {
			if (m.getName().substring(0, 3).equals("get") 
					&& !m.getName().substring(0, 5).equals("getId")
					&& !m.getName().toLowerCase().contains("utente")) {
				if ( m.getReturnType().equals(String.class)) {
					try {
						//Se il field è una stringa allora la manda in uppercase
						String value = (String) m.invoke(pojo);
						String setterName = m.getName().replaceFirst("get", "set");
						Method setter = c.getMethod(setterName, String.class);
						setter.invoke(pojo, toUpperCase(value));
					} catch (Exception e) { }
				} else {
					if (m.getReturnType().getCanonicalName().contains("model")) {
						try {
							//è un oggetto del model
							Object incapsulatedPojo = m.invoke(pojo);
							pojoToUppercase(incapsulatedPojo);
						} catch (Exception e) { }
					}
				}
			}
		}
	}
	
	//Uppercase e sostituisce accenti con apostrofi se in fondo a parola
	public static String toUpperCase(String s) {
		if (s == null) return null;
		String result="";
		if (s != null) {
			for (int i=0;i<s.length();i++) {
				String c = s.substring(i,i+1);
				boolean isLastChar = false;
				try {
					String n = s.substring(i+1,i+2);
					if (" .,".contains(n)) isLastChar = true;
				} catch (Exception e) {
					isLastChar = true;
				}
				
				if ("àèéìòù".contains(c)) {
					if (c.equals("à")) c = "A";
					if (c.equals("è") || c.equals("é")) c = "E";
					if (c.equals("ì")) c = "I";
					if (c.equals("ò")) c = "O";
					if (c.equals("ù")) c = "U";
					if (isLastChar) c+= "'";
				} else {
					c = c.toUpperCase();
				}
				result+=c;
			}
		}
		return result;
	}
	
	public static Integer createChecksumChar(String s) {
		int c = Integer.valueOf(s);
		return createChecksumChar(c);
	}
	
	public static Integer createChecksumChar(Integer i) {
		int result = (i*7)%9;//Il checksum è il resto della divisione per 9 del (numero * 7)
		return result;
	}
}
