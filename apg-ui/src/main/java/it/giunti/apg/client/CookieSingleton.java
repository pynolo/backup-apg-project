package it.giunti.apg.client;

import it.giunti.apg.shared.DateUtil;

import java.util.Date;

import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.datepicker.client.CalendarUtil;

public class CookieSingleton {

	private static CookieSingleton instance = null;
	
	private Date expiryDate = null;
	
	private CookieSingleton() {
		Date date = DateUtil.now();
		CalendarUtil.addDaysToDate(date, ClientConstants.COOKIE_EXPIRATION_DAYS);
		expiryDate = date;
	}
	
	public static CookieSingleton get() {
		if (instance == null) instance = new CookieSingleton();
		return instance;
	}
	
	private String rot13(String input) {
		if (input == null) return "";
		char[] inArray = input.toCharArray();
		String out = "";
        for (char inChar:inArray){
            // top sekrit encryption, 
            // algorithm from www.miranda.org.~jkominek/rot13
            char outChar = Character.toUpperCase((char)inChar);
            if ('A' <= outChar && outChar <= 'Z'){
                outChar = (char)(((int)outChar - 'A' + 13) % 26 + 'A');
            }
            if (Character.isLowerCase((char)inChar)){
                outChar= Character.toLowerCase(outChar);
            }
            out += new Character(outChar);
        }
        return out;
	}
		
	public void removeCookie(String cookieName) {
		Cookies.removeCookie(cookieName);
	}
	
	//public void setCookie(String cookieName, String value) {
	//	Cookies.setCookie(cookieName, value, expiryDate);
	//}
	//public String getCookie(String cookieName) {
	//	return Cookies.getCookie(cookieName);
	//}

	public void setCookie(String cookieName, String value) {
		value = rot13(value);
		Cookies.setCookie(cookieName, value, expiryDate);
	}
	public String getCookie(String cookieName) {
		String value = rot13(Cookies.getCookie(cookieName));
		return value;
	}
	
}
