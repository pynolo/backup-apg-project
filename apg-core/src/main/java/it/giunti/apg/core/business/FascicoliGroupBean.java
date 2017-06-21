package it.giunti.apg.core.business;

import it.giunti.apg.shared.model.EvasioniFascicoli;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;

import java.util.ArrayList;
import java.util.List;

public class FascicoliGroupBean {
	
	private IstanzeAbbonamenti istanzaAbbonamento = null;
	private List<EvasioniFascicoli> evasioniFascicoliList = new ArrayList<EvasioniFascicoli>();
	
	public FascicoliGroupBean(IstanzeAbbonamenti istanzaAbbonamento) {
		this.istanzaAbbonamento = istanzaAbbonamento;
	}
	public IstanzeAbbonamenti getIstanzaAbbonamento() {
		return istanzaAbbonamento;
	}
	public void setIstanzaAbbonamento(IstanzeAbbonamenti istanzaAbbonamento) {
		this.istanzaAbbonamento = istanzaAbbonamento;
	}
	public List<EvasioniFascicoli> getEvasioniFacicoliList() {
		return evasioniFascicoliList;
	}
	public void setEvasioniFascicoliList(List<EvasioniFascicoli> evasioniFascicoliList) {
		this.evasioniFascicoliList = evasioniFascicoliList;
	}
}
