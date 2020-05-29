package it.giunti.apg.core.business;

import java.util.ArrayList;
import java.util.List;

import it.giunti.apg.shared.model.Abbonamenti;
import it.giunti.apg.shared.model.MaterialiSpedizione;

public class FascicoliGroupBean {
	
	private Abbonamenti abbonamento = null;
	private List<MaterialiSpedizione> materialiSpedizioneList = new ArrayList<MaterialiSpedizione>();
	
	public FascicoliGroupBean(Abbonamenti abbonamento) {
		this.abbonamento = abbonamento;
	}
	
	public Abbonamenti getAbbonamento() {
		return abbonamento;
	}

	public void setAbbonamento(Abbonamenti abbonamento) {
		this.abbonamento = abbonamento;
	}

	public List<MaterialiSpedizione> getMaterialiSpedizioneList() {
		return materialiSpedizioneList;
	}
	public void setMaterialiSpedizioneList(List<MaterialiSpedizione> materialiSpedizioneList) {
		this.materialiSpedizioneList = materialiSpedizioneList;
	}
}
