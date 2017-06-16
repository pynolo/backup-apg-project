package it.giunti.apg.ws.paperlit;

import java.text.SimpleDateFormat;

public class PublicationSerializer {

	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

	public static String toJson(PublicationJso object) {
		String s = null;
		if (object.getIssubscribed()) {
			s = "\""+object.getPublicationId()+"\":{";
			s += "\"issubscribed\":"+object.getIssubscribed()+",";
			if (object.getIssubscribed()) {
				s += "\"startedon\":\""+SDF.format(object.getStartedon())+"\",";
				s += "\"expireson\":\""+SDF.format(object.getExpireson())+"\"";
			} else {
				//issubscribed:false
				
			}
			s += "}";
		} else {
			s = "\"issubscribed\":"+object.getIssubscribed()+",";
			s += "\"message\":\""+object.getMessage()+"\"";
		}
		return s;
	}
}
