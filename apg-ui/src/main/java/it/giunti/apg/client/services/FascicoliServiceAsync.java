package it.giunti.apg.client.services;

import it.giunti.apg.shared.model.EvasioniFascicoli;
import it.giunti.apg.shared.model.Fascicoli;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FascicoliServiceAsync {

	//Fascicoli
	void findFascicoliByPeriodico(Integer idPeriodico,
			long startDt, long finishDt,
			boolean includeOpzioni, boolean orderAsc,
			int offset, int pageSize, AsyncCallback<List<Fascicoli>> callback);
	void findFascicoliByPeriodico(Integer idPeriodico, Integer selectedId,
			long startDt, long finishDt,
			boolean includeOpzioni, boolean orderAsc,
			int offset, int pageSize, AsyncCallback<List<Fascicoli>> callback);
	void findFascicoliByOpzione(Integer idOpzione, boolean orderAsc,
			int offset, int pageSize, AsyncCallback<List<Fascicoli>> callback);
	void findFascicoloById(Integer idFas, AsyncCallback<Fascicoli> callback);
	void createFascicolo(Integer idPeriodico, Boolean isOpzione, AsyncCallback<Fascicoli> callback);
	void saveOrUpdate(Fascicoli fascicolo, AsyncCallback<Integer> callback);
	void deleteFascicolo(Integer idFas, AsyncCallback<List<Fascicoli>> callback);
	void findFascicoloByPeriodicoDataInizio(Integer idPeriodico, Date date, AsyncCallback<Fascicoli> callback);
	void findPrimoFascicoloNonSpedito(Integer idPeriodico, Date date, Boolean includeAllegati, AsyncCallback<Fascicoli> callback);
	void findFascicoliByEnqueuedMedia(String idTipoMedia, AsyncCallback<Map<Fascicoli, Integer>> callback);
	void countFascicoliBetweenFascicoli(String idPeriodico, String idFasInizio, String idFasFine, AsyncCallback<Integer> callback);
	void countFascicoliTotali(Integer idIstanza, AsyncCallback<Integer> callback);
	void countFascicoliDaSpedire(Integer idIstanza, AsyncCallback<Integer> callback);
	void countFascicoliSpediti(Integer idIstanza, AsyncCallback<Integer> callback);
	void verifyFascicoloWithinIstanza(Integer idIstanza, Integer idFascicolo, AsyncCallback<Boolean> callback);

	//Evasioni fascicoli
	void findEvasioniFascicoliByIstanza(Integer idIstanza, AsyncCallback<List<EvasioniFascicoli>> callback);
	//void findFascicoliBeforeFascicolo(Integer idOldFascicolo, Integer fascicoliCount, AsyncCallback<Fascicoli> callback);
	//void findFascicoliAfterFascicolo(Integer idOldFascicolo, Integer fascicoliCount, AsyncCallback<Fascicoli> callback);
	
	void findEvasioneFascicoloById(Integer idEvasioneFascicolo, AsyncCallback<EvasioniFascicoli> callback);
	void saveOrUpdate(EvasioniFascicoli evasioneFascicolo, AsyncCallback<Integer> callback);
	void createEvasioneFascicoloForIstanza(Integer idIstanza,
			String idTipoEvasione, AsyncCallback<EvasioniFascicoli> callback);	
	void createEvasioneFascicoloForAnagrafica(Integer idAnagrafica,
			Integer copie, String idTipoEvasione, AsyncCallback<EvasioniFascicoli> callback);
	void deleteEvasioneFascicolo(Integer idIstanza, Integer idEvasioneFascicolo, AsyncCallback<List<EvasioniFascicoli>> callback);	
	void createMassiveArretrati(Integer idIstanza, Date today, String idUtente, AsyncCallback<List<EvasioniFascicoli>> callback);
	void createMassiveArretrati(String codiceAbbonamento, Date today, String idUtente, AsyncCallback<List<EvasioniFascicoli>> callback);

	//Opzioni
	void getOpzioniDescr(String opzioniList, AsyncCallback<String> callback);

}
