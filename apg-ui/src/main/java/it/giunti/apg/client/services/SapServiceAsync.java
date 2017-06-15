package it.giunti.apg.client.services;

import it.giunti.apg.shared.model.EvasioniArticoli;
import it.giunti.apg.shared.model.EvasioniFascicoli;
import it.giunti.apg.shared.model.OrdiniLogistica;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SapServiceAsync {
	
	//OrdiniLogistica
	void findOrdineById(Integer idOrdine, AsyncCallback<OrdiniLogistica> callback);
	void findOrdini(boolean showAnnullati, int offset, int pageSize,
			AsyncCallback<List<OrdiniLogistica>> callback);
	
	//EvasioniFascicoli
	void findEvasioniFascicoliByOrdine(String numOrdine,
			AsyncCallback<List<EvasioniFascicoli>> callback);
	
	//EvasioniArticoli
	void findEvasioniArticoliByOrdine(String numOrdine,
			AsyncCallback<List<EvasioniArticoli>> callback);
	
}
