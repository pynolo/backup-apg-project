package it.giunti.apg.ws.api01;

public class ErrorPayload {

	private ErrorEnum error;
	private String message;
	
	public ErrorPayload(ErrorEnum error, String message) {
		this.error=error;
		this.message=message;
	}
	
	public ErrorEnum getError() {
		return error;
	}
	public void setError(ErrorEnum error) {
		this.error = error;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
