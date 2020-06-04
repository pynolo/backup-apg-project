package it.giunti.apg.client.services;

import it.giunti.apg.shared.model.Articoli;
import it.giunti.apg.shared.model.ArticoliListini;
import it.giunti.apg.shared.model.ArticoliOpzioni;
import it.giunti.apg.shared.model.EvasioniArticoli;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MaterialiServiceAsync {
	//Materiali
	void findArticoloById(Integer idArticolo, AsyncCallback<Articoli> Articolick);
	void saveOrUpdateArticolo(Articoli articolo, AsyncCallback<Integer> callback);
	void createArticolo(AsyncCallback<Articoli> callback);
	void findArticoliByDate(Date validDt, int offset, int pageSize, AsyncCallback<List<Articoli>> callback);
	void findArticoliByDateInterval(Date startDt, Date finishDt, AsyncCallback<List<Articoli>> callback);
	
	//Evasioni articoli
	void findEvasioniArticoliByIstanza(Integer idIstanza, AsyncCallback<List<EvasioniArticoli>> callback);
	void findEvasioniArticoliByAnagrafica(Integer idAnagrafica, AsyncCallback<List<EvasioniArticoli>> callback);
	void findEvasioniArticoliById(Integer idEd, AsyncCallback<EvasioniArticoli> callback);
	//void loadDataLimite(Integer idIstanza, Integer idArticolo, AsyncCallback<Date> callback);
	//void loadDataLimite(Integer idArticolo, AsyncCallback<Date> callback);
	void createEmptyEvasioneArticoloFromIstanza(Integer idIstanza, String idTipoDestinatario,
			String idUtente, AsyncCallback<EvasioniArticoli> callback);
	void createEvasioneArticoloFromAnagrafica(Integer idAnagrafica, Integer copie,
			String idTipoDestinatario, String idUtente, AsyncCallback<EvasioniArticoli> callback);
	void createEvasioneArticoloWithCodAbbo(String codAbbo, Integer idArticolo,
			String idTipoDestinatario, String idUtente, AsyncCallback<Integer> callback);
	void saveOrUpdateEvasioneArticolo(EvasioniArticoli evasioneArticolo, AsyncCallback<Integer> callback);
	void deleteEvasioneArticolo(Integer idEvasioneArticolo, AsyncCallback<List<EvasioniArticoli>> callback);
	//void reattachArticoliToInstanza(Integer idIstanza, AsyncCallback<Integer> callback);
	
	//ArticoliListini
	void findArticoloListinoById(Integer idArticoloListino, AsyncCallback<ArticoliListini> callback);
	void saveOrUpdateArticoloListino(ArticoliListini articoloListino, AsyncCallback<Integer> callback);
	void createArticoloListino(Integer idListino, AsyncCallback<ArticoliListini> callback);
	void deleteArticoloListino(Integer idListino, AsyncCallback<List<ArticoliListini>> callback);
	void findArticoliListini(Integer idListino, AsyncCallback<List<ArticoliListini>> callback);
	void findArticoliListiniByPeriodicoDate(Integer idPeriodico, Date date, AsyncCallback<List<ArticoliListini>> callback);
	void findPendingArticoliListiniCount(AsyncCallback<Map<ArticoliListini, Integer>> callback);

	//ArticoliOpzioni
	void findArticoloOpzioneById(Integer idArticoloOpzione, AsyncCallback<ArticoliOpzioni> callback);
	void saveOrUpdateArticoloOpzione(ArticoliOpzioni articoloOpzione, AsyncCallback<Integer> callback);
	void createArticoloOpzione(Integer idOpzione, AsyncCallback<ArticoliOpzioni> callback);
	void deleteArticoloOpzione(Integer idOpzione, AsyncCallback<List<ArticoliOpzioni>> callback);
	void findArticoliOpzioni(Integer idOpzione, AsyncCallback<List<ArticoliOpzioni>> callback);
	void findArticoliOpzioniByPeriodicoDate(Integer idPeriodico, Date date, AsyncCallback<List<ArticoliOpzioni>> callback);
	void findPendingArticoliOpzioniCount(AsyncCallback<Map<ArticoliOpzioni, Integer>> callback);

}
