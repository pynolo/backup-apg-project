package it.giunti.apg.client.services;

import it.giunti.apg.shared.model.Avvisi;
import it.giunti.apg.shared.model.LogEditing;
import it.giunti.apg.shared.model.Rapporti;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LoggingServiceAsync {
	void createRapporto(String titolo, String idUtente, AsyncCallback<Integer> callback);
	void receiveLogLines(int idRapporto, int expectedLine, AsyncCallback<List<String>> callback);
	void findRapportiStripped(Date extractionDt, int offset, int size, AsyncCallback<List<Rapporti>> callback);
	void findRapportoById(Integer idRapporto, AsyncCallback<Rapporti> callback);
	
	void findLastAvvisi(int offset, int size, AsyncCallback<List<Avvisi>> callback);
	void findLastAvvisiByGiorniTipo(int giorniAntecedenti, AsyncCallback<List<Avvisi>> callback);
	void saveAvviso(String message, boolean importante, String idUtente, AsyncCallback<Integer> callback);
	void deleteAvviso(Integer idAvviso, int pageSize, AsyncCallback<List<Avvisi>> callback);
	void updateImportanza(Integer idAvviso, boolean importante, AsyncCallback<Boolean> callback);
	
	void findEditLogs(String classSimpleName, Integer entityId, AsyncCallback<List<LogEditing>> callback);

}
