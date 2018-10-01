package it.giunti.apg.ws.api04;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class BaseJsonFactory {
	//STATUS
	private static final String STATUS = "status";
	private static final String STATUS_ERROR = "ER";
	private static final String STATUS_OK = "OK";
	//ERROR
	private static final String ERROR_CODE = "error_code";
	private static final String ERROR_MESSAGE = "error_message";
	//PAYLOAD
	private static final String DATA = "data";
	
	public static JsonObject buildBaseObject(JsonObjectBuilder payload) {
		return buildBaseObject(new ErrorPayload(ErrorEnum.NO_ERROR,
					ErrorEnum.NO_ERROR.getErrorDescr()),
				payload);
	}
	
	public static JsonObject buildBaseObject(ErrorEnum error, String message) {
		return buildBaseObject(new ErrorPayload(error, message), null);
	}
	
	public static JsonObject buildBaseObject(ErrorPayload errorPayload) {
		return buildBaseObject(errorPayload, null);
	}
	
	public static JsonObject buildBaseObject(ErrorPayload errorPayload, JsonObjectBuilder payload) {
		JsonObjectBuilder joBuilder = Json.createObjectBuilder();
		if (errorPayload.getError().equals(ErrorEnum.NO_ERROR)) {
			joBuilder.add(STATUS, STATUS_OK);
		} else {
			joBuilder.add(STATUS, STATUS_ERROR);
		}
		joBuilder.add(ERROR_CODE, errorPayload.getError().getErrorCode());
		joBuilder.add(ERROR_MESSAGE, errorPayload.getMessage());
		if (payload != null) {
			joBuilder.add(DATA, payload);
		}
		return joBuilder.build();
	}
}
