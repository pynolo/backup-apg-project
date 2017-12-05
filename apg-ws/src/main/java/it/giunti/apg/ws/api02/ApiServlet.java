package it.giunti.apg.ws.api02;

import it.giunti.apg.core.ServerConstants;

import java.util.Date;

import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServlet;

public class ApiServlet extends HttpServlet {
	private static final long serialVersionUID = -1181563551265405480L;

	public void add(JsonObjectBuilder ob, String key, String value) {
		if (value != null) {
			if (!value.equals("")) {
				ob.add(key, value);
			}
		}
	}
	
	public void add(JsonObjectBuilder ob, String key, Integer value) {
		if (value != null) {
			ob.add(key, value);
		}
	}
	
	public void add(JsonObjectBuilder ob, String key, Double value) {
		if (value != null) {
			String valueString = ServerConstants.FORMAT_INTEGER.format(Math.floor(value))+"."+
					ServerConstants.FORMAT_INTEGER.format(Math.round((value-Math.floor(value))*100));
			ob.add(key, valueString);
		}
	}
	
	public void add(JsonObjectBuilder ob, String key, Boolean value) {
		if (value != null) {
			ob.add(key, value.toString());
		}
	}
	
	public void add(JsonObjectBuilder ob, String key, Date value) {
		if (value != null) {
			String s = Constants.FORMAT_API_DATE.format(value);
			ob.add(key, s);
		}
	}
}
