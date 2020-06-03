package it.giunti.apg.core.business;

import java.util.ArrayList;
import java.util.List;

import it.giunti.apg.shared.model.Abbonamenti;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.MaterialiSpedizione;

public class FascicoliGroupBean {
	
	private Abbonamenti abbonamento = null;
	private Anagrafiche anagrafica = null;
	private List<MaterialiSpedizione> materialiSpedizioneList = new ArrayList<MaterialiSpedizione>();
	
	public FascicoliGroupBean(Abbonamenti abbonamento, Anagrafiche anagrafica) {
		this.abbonamento = abbonamento;
		this.anagrafica = anagrafica;
	}
	
	public Abbonamenti getAbbonamento() {
		return abbonamento;
	}

	public void setAbbonamento_(Abbonamenti abbonamento) {
		this.abbonamento = abbonamento;
	}

	public Anagrafiche getAnagrafica() {
		return anagrafica;
	}

	public void setAnagrafica(Anagrafiche anagrafica) {
		this.anagrafica = anagrafica;
	}

	public List<MaterialiSpedizione> getMaterialiSpedizioneList() {
		return materialiSpedizioneList;
	}
	public void setMaterialiSpedizioneList(List<MaterialiSpedizione> materialiSpedizioneList) {
		this.materialiSpedizioneList = materialiSpedizioneList;
	}
}
