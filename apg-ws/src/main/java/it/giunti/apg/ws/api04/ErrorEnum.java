package it.giunti.apg.ws.api04;

public enum ErrorEnum {
	NO_ERROR("0", "All went fine. "),
	INTERNAL_ERROR("1", "Internal error. "),
	EMPTY_PARAMETER("2", "Mandatory parameter is empty. "),
	WRONG_PARAMETER_VALUE("3", "Parameter has a wrong value. "),
	WRONG_ACCESS_KEY("4", "Wrong access key. "),
	DATA_NOT_FOUND("5", "Data not found. ");
	
	private String errorCode; 
	private String errorDescr;
	
	private ErrorEnum(String errorCode, String errorDescr) {
		this.errorCode=errorCode;
		this.errorDescr=errorDescr;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorDescr() {
		return errorDescr;
	}
	
}
