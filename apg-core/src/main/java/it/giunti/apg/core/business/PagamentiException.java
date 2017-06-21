package it.giunti.apg.core.business;

import it.giunti.apg.shared.AppConstants;

import java.io.Serializable;

public class PagamentiException extends Exception implements Serializable {
	private static final long serialVersionUID = 8618260001730098009L;

	private String idError;

	public PagamentiException(String idError) {
		super();
		this.idError=idError;
	}
	
	public PagamentiException(String idError, Throwable e) {
		super(AppConstants.PAGAMENTO_ERR_DESC.get(idError), e);
		this.idError=idError;
	}
	
	public String getIdError() {
		return idError;
	}
	
	public String getMessage() {
		return AppConstants.PAGAMENTO_ERR_DESC.get(idError);
	}

	
}
