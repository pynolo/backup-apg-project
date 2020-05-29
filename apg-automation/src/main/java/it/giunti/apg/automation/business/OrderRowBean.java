package it.giunti.apg.automation.business;

import it.giunti.apg.shared.model.MaterialiSpedizione;

public class OrderRowBean {
	
	private MaterialiSpedizione spedizione = null;
	private String committenteSap = null;
	
	public OrderRowBean(MaterialiSpedizione spedizione, String committenteSap) {
		this.spedizione = spedizione;
		this.committenteSap = committenteSap;
	}

	public MaterialiSpedizione getSpedizione() {
		return spedizione;
	}

	public void setSpedizione(MaterialiSpedizione spedizione) {
		this.spedizione = spedizione;
	}

	public String getCommittenteSap() {
		return committenteSap;
	}

	public void setCommittenteSap(String committenteSap) {
		this.committenteSap = committenteSap;
	}
	
}
