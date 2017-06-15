package it.giunti.apg.shared;

import java.io.Serializable;

public class EmptyResultException extends Exception implements Serializable {
	private static final long serialVersionUID = -2105681056394250457L;
	private String message;

	public EmptyResultException() {
		super();
		message="";
	}
	
	public EmptyResultException(String message) {
		super(message);
		this.message=message;
	}
	
	public EmptyResultException(String message, Throwable e) {
		super(message, e);
		this.message=message;
	}
	
	public String getMessage() {
		return message;
	}

	
}
