package it.giunti.apg.automation.sap;

import java.io.Serializable;

public class RfcConnectionException extends Exception implements Serializable {
	private static final long serialVersionUID = 8618269701736198769L;

	private String message;

	public RfcConnectionException() {
		super();
		message="";
	}
	
	public RfcConnectionException(String message) {
		super(message);
		this.message=message;
	}
	
	public RfcConnectionException(String message, Throwable e) {
		super(message, e);
		this.message=message;
	}
	
	public String getMessage() {
		return message;
	}

	
}
