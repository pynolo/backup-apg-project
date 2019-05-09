package it.giunti.apg.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import it.giunti.apg.shared.model.RinnoviMassivi;

public interface UtilServiceAsync {
	void getApgTitle(AsyncCallback<String> callback);
	void getApgStatus(AsyncCallback<String> callback);
	void getApgMenuImage(AsyncCallback<String> callback);
	void getApgLoginImage(AsyncCallback<String> callback);
	void getApgVersion(AsyncCallback<String> callback);
	
	void findRinnoviMassivi(Integer idPeriodico, AsyncCallback<List<RinnoviMassivi>> callback);
	void saveOrUpdateRinnoviMassiviList(List<RinnoviMassivi> rinnoviMassiviList,
			AsyncCallback<Boolean> callback);
	void deleteRinnovoMassivo(Integer idRinnovoMassivo, AsyncCallback<Boolean> callback);
}
