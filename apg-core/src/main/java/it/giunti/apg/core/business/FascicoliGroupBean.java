package it.giunti.apg.core.business;

import java.util.ArrayList;
import java.util.List;

import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.MaterialiSpedizione;

public class FascicoliGroupBean {
	
	private IstanzeAbbonamenti istanzaAbbonamento = null;
	private List<MaterialiSpedizione> materialiSpedizioneList = new ArrayList<MaterialiSpedizione>();
	
	public FascicoliGroupBean(IstanzeAbbonamenti istanzaAbbonamento) {
		this.istanzaAbbonamento = istanzaAbbonamento;
	}
	public IstanzeAbbonamenti getIstanzaAbbonamento() {
		return istanzaAbbonamento;
	}
	public void setIstanzaAbbonamento(IstanzeAbbonamenti istanzaAbbonamento) {
		this.istanzaAbbonamento = istanzaAbbonamento;
	}
	public List<MaterialiSpedizione> getMaterialiSpedizioneList() {
		return materialiSpedizioneList;
	}
	public void setMaterialiSpedizioneList(List<MaterialiSpedizione> materialiSpedizioneList) {
		this.materialiSpedizioneList = materialiSpedizioneList;
	}
}
