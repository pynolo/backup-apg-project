package it.giunti.apg.client;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Hyperlink;


public class UriParameters {
	
	public static final String FORMAT_DATE_URI = "ddMMyyyy";
	private static final String PARAM_RANDOM = "xyz";
	
	private Map<String,String> paramMap = new HashMap<String, String>();
	
	public UriParameters() {}
	
	public void add(String key, String value) {
		paramMap.put(key, value);
	}
	
	public void add(String key, Integer value) {
		if (value != null) {
			paramMap.put(key, value.toString());
		}
	}
	
	public void add(String key, Long value) {
		if (value != null) {
			paramMap.put(key, value.toString());
		}
	}
	
	public void add(String key, Date value) {
		if (value != null) {
			DateTimeFormat dtf = DateTimeFormat.getFormat(FORMAT_DATE_URI);
			paramMap.put(key, dtf.format(value));
		}
	}
	
	/** can't return empty strings.
	 * it's only null or a string longer than 0
	 * @param key
	 * @return
	 */
	public String getValue(String key) {
		return paramMap.get(key);
	}
	
	public Integer getIntValue(String key) {
		String value = getValue(key);
		Integer result = null;
		if (value !=  null) {
			try {
				result = Integer.parseInt(value);
			} catch (NumberFormatException e) {	}
		}
		return result;
	}
	
	public Date getDateValue(String key) {
		String value = getValue(key);
		Date result = null;
		if (value !=  null) {
			try {
				DateTimeFormat dtf = DateTimeFormat.getFormat(FORMAT_DATE_URI);
				result = dtf.parse(value);
			} catch (IllegalArgumentException e) {	}
		}
		return result;
	}
	
	public String getUri(String destinationPageIdentifier) {
		add(PARAM_RANDOM, new Date().getTime());
		String result = "";
		for (String key:paramMap.keySet()) {
			String value = paramMap.get(key);
			if (value != null) {
				if (!value.equals("")) {
					if (result.length() > 0) result += UriManager.SEPARATOR_PARAMS;
					result += key + UriManager.SEPARATOR_VALUES + value;
				}
			}
		}
		result = destinationPageIdentifier + UriManager.SEPARATOR_TOKEN + result;
		return result;
	}
	
	public Hyperlink getHyperlink(String htmlLabel, String destinationPageIdentifier) {
		Hyperlink result = new Hyperlink(htmlLabel, true, this.getUri(destinationPageIdentifier));
		return result;
	}
	
	public void triggerUri(String destinationPageIdentifier) {
		History.newItem(this.getUri(destinationPageIdentifier), true);
	}
	
	@Override
	public String toString() {
		String result = "{";
		for (String key:paramMap.keySet()) {
			String value = paramMap.get(key);
			if (value != null) result += key+"="+value+"; ";
		}
		return result+"}";
	}
	
}
