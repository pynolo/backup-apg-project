package it.giunti.apg.client.services;

import it.giunti.apg.shared.model.Listini;
import it.giunti.apg.shared.model.TipiAbbonamento;
import it.giunti.apg.shared.model.TipiAbbonamentoRinnovo;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TipiAbbServiceAsync {
	
	//TipiAbbonamento
	void findTipiAbbonamentoByPeriodicoDate(Integer idPeriodico, Integer selectedId, Date beginDate, AsyncCallback<List<TipiAbbonamento>> callback);
	void findTipiAbbonamentoByPeriodico(Integer idPeriodico, Integer selectedId, AsyncCallback<List<TipiAbbonamento>> callback);
	
	//Listini
	void findListinoById(Integer idListino, AsyncCallback<Listini> callback);
	void findDefaultListinoByPeriodicoDate(Integer idPeriodico, Date date, AsyncCallback<Listini> callback);
	void findDefaultListinoByFascicoloInizio(Integer idPeriodico, Integer idFascicolo, AsyncCallback<Listini> callback);
	void findListiniByTipoAbb(Integer idTipoAbb, int offset, int pageSize, AsyncCallback<List<Listini>> callback);
	void findListiniByPeriodicoDate(Integer idPeriodico, Date dt, Integer selectedId, int offset, int pageSize, AsyncCallback<List<Listini>> callback);
	void findListiniByFascicoloInizio(Integer idPeriodico, Integer idFas, Integer selectedId, int offset, int pageSize, AsyncCallback<List<Listini>> callback);
	void findListinoByTipoAbbDate(Integer idPeriodico, Date dt, AsyncCallback<Listini> callback);
	void createListinoFromPeriodico(Integer idPeriodico, AsyncCallback<Listini> callback);
	void createListinoFromTipo(Integer idTipoAbbonamento, AsyncCallback<Listini> callback);
	void saveOrUpdate(Listini lst, List<Integer> tipiAbbRinnovoList, AsyncCallback<Integer> callback);
	void createVersion(Listini lst, List<Integer> tipiAbbRinnovoList, AsyncCallback<Integer> callback);

	//TipiAbbonamentoRinnovo
	void findTipiAbbonamentoRinnovoByListino(Integer idListino,
			AsyncCallback<List<TipiAbbonamentoRinnovo>> callback);
	void findCodiceTipiAbbonamentoRinnovoByListino(Integer idListino,
			AsyncCallback<List<String>> callback);


	
}
