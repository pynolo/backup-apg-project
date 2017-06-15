package it.giunti.apg.shared;

import java.io.Serializable;

public class ValidationException extends Exception implements Serializable {
	private static final long serialVersionUID = 9086261750497767011L;
	
	private static final String MESSAGE_PREFIX = "";//"Attenzione: ";
	
	private String message;

	public ValidationException() {
		super();
		message="";
	}
	
	public ValidationException(String message) {
		super(MESSAGE_PREFIX+message);
		this.message=MESSAGE_PREFIX+message;
	}
	
	public ValidationException(String message, Throwable e) {
		super(MESSAGE_PREFIX+message, e);
		this.message=MESSAGE_PREFIX+message;
	}
	
	public String getMessage() {
		return message;
	}

	
}
