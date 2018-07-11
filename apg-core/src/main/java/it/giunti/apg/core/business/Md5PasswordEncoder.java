package it.giunti.apg.core.business;

import it.giunti.apg.shared.AppConstants;

public class Md5PasswordEncoder {

	private static final DefaultPasswordEncoder defaultEncoder = new DefaultPasswordEncoder("MD5");
	
	public static String encode(String password) {
		defaultEncoder.setCharacterEncoding(AppConstants.CHARSET_UTF8);
		String encoded = defaultEncoder.encode(password);
		return encoded;
	}
	
}
