package it.giunti.apg.automation.business;

import it.giunti.apg.shared.model.IEvasioni;

public class OrderRowBean {
	
	private IEvasioni evasione = null;
	private String committente = null;
	
	public OrderRowBean(IEvasioni evasione, String committente) {
		this.evasione = evasione;
		this.committente = committente;
	}

	public IEvasioni getEvasione() {
		return evasione;
	}

	public void setEvasione(IEvasioni evasione) {
		this.evasione = evasione;
	}

	public String getCommittente() {
		return committente;
	}

	public void setCommittente(String committente) {
		this.committente = committente;
	}
	
}
