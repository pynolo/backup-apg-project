package it.giunti.apg.shared;

import java.io.Serializable;

public class FileException extends Exception implements Serializable {
	private static final long serialVersionUID = 8618269701736198769L;

	private String message;

	public FileException() {
		super();
		message="";
	}
	
	public FileException(String message) {
		super(message);
		this.message=message;
	}
	
	public FileException(String message, Throwable e) {
		super(message, e);
		this.message=message;
	}
	
	public String getMessage() {
		return message;
	}

	
}
