package it.giunti.apg.client.services;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import it.giunti.apg.shared.model.Opzioni;

public interface OpzioniServiceAsync {
	void findOpzioneById(Integer idOpzione, AsyncCallback<Opzioni> callback);
	void saveOrUpdateOpzione(Opzioni opzione, AsyncCallback<Integer> callback);
	void createOpzione(Integer idPeriodico, AsyncCallback<Opzioni> callback);
	void findOpzioni(Long startDt, Long finishDt, AsyncCallback<List<Opzioni>> callback);
	void findOpzioni(Date extractionDt, AsyncCallback<List<Opzioni>> callback);
	void findOpzioni(Integer idPeriodico, Date extractionDt, Boolean soloCartacei, AsyncCallback<List<Opzioni>> callback);
	void findOpzioni(Integer idPeriodico, Date startDt, Date finishDt, Boolean soloCartacei, AsyncCallback<List<Opzioni>> callback);
	void findOpzioniByListino(Integer idListino, AsyncCallback<List<Opzioni>> callback);
	void findOpzioniFacoltativeByListino(Integer idListino, Date dt, AsyncCallback<List<Opzioni>> callback);
	
	void getOpzioniDescr(String opzioniList, AsyncCallback<String> callback);
}
