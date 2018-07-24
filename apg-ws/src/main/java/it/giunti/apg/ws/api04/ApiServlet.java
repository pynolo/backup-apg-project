package it.giunti.apg.ws.api04;

import it.giunti.apg.core.ServerConstants;

import java.util.Date;

import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServlet;

public class ApiServlet extends HttpServlet {
	private static final long serialVersionUID = -7832613002652808430L;

	public static void add(JsonObjectBuilder ob, String key, String value) {
		if (value != null) {
			if (!value.equals("")) {
				ob.add(key, value);
			}
		}
	}
	
	public static void add(JsonObjectBuilder ob, String key, Integer value) {
		if (value != null) {
			ob.add(key, value);
		}
	}
	
	public static void add(JsonObjectBuilder ob, String key, Double value) {
		if (value != null) {
			String valueString = ServerConstants.FORMAT_INTEGER.format(Math.floor(value))+"."+
					ServerConstants.FORMAT_INTEGER.format(Math.round((value-Math.floor(value))*100));
			ob.add(key, valueString);
		}
	}
	
	public static void add(JsonObjectBuilder ob, String key, Boolean value) {
		if (value != null) {
			ob.add(key, value.toString());
		}
	}
	
	public static void add(JsonObjectBuilder ob, String key, Date value) {
		if (value != null) {
			String s = Constants.FORMAT_API_DATE.format(value);
			ob.add(key, s);
		}
	}
}
