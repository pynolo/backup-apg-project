package it.giunti.apg.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import it.giunti.apg.shared.model.MaterialiSpedizione;
import it.giunti.apg.shared.model.OrdiniLogistica;

public interface SapServiceAsync {
	
	//OrdiniLogistica
	void findOrdineById(Integer idOrdine, AsyncCallback<OrdiniLogistica> callback);
	void findOrdini(boolean showAnnullati, int offset, int pageSize,
			AsyncCallback<List<OrdiniLogistica>> callback);
	
	void findMaterialiSpedizioneByOrdine(String numOrdine,
			AsyncCallback<List<MaterialiSpedizione>> callback);
		
}
