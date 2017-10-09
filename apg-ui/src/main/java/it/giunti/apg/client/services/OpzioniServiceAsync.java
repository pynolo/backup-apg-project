package it.giunti.apg.client.services;

import it.giunti.apg.shared.model.Opzioni;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface OpzioniServiceAsync {
	void findOpzioneById(Integer idOpzione, AsyncCallback<Opzioni> callback);
	void saveOrUpdateOpzione(Opzioni opzione, AsyncCallback<Integer> callback);
	void createOpzione(Integer idPeriodico, AsyncCallback<Opzioni> callback);
	void findOpzioni(Long startDt, Long finishDt, AsyncCallback<List<Opzioni>> callback);
	void findOpzioni(Date extractionDt, AsyncCallback<List<Opzioni>> callback);
	void findOpzioni(Integer idPeriodico, Date extractionDt, Boolean soloCartacei, AsyncCallback<List<Opzioni>> callback);
	void findOpzioni(Integer idPeriodico, Date startDt, Date finishDt, Boolean soloCartacei, AsyncCallback<List<Opzioni>> callback);
	void findOpzioni(Integer idPeriodico, Integer idFascicolo, AsyncCallback<List<Opzioni>> callback);
	void findOpzioniByListino(Integer idListino, AsyncCallback<List<Opzioni>> callback);
	void findOpzioniFacoltativeByListino(Integer idListino, Integer idFascicolo, AsyncCallback<List<Opzioni>> callback);
	void createNewUid(Integer idPeriodico, AsyncCallback<String> callback);
	
}
