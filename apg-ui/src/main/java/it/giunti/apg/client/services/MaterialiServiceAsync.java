package it.giunti.apg.client.services;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

import it.giunti.apg.shared.model.ArticoliListini;
import it.giunti.apg.shared.model.ArticoliOpzioni;
import it.giunti.apg.shared.model.Materiali;
import it.giunti.apg.shared.model.MaterialiProgrammazione;
import it.giunti.apg.shared.model.MaterialiSpedizione;

public interface MaterialiServiceAsync {
	//** Materiali **
	void createMateriale(String tipoMateriale, String tipoAnagraficaSap, AsyncCallback<Materiali> callback);
	void createMaterialeArticolo(AsyncCallback<Materiali> callback);
	void createMaterialeFascicoloGe(AsyncCallback<Materiali> callback);
	void findMaterialeById(Integer idMateriale, AsyncCallback<Materiali> callback);
	void saveOrUpdateMateriale(Materiali item, AsyncCallback<Integer> callback);
	void deleteMateriale(Integer idMateriale, AsyncCallback<Boolean> callback);
	void findMaterialiByDate(Date validDt, int offset, int pageSize, AsyncCallback<List<Materiali>> callback);


	//** MaterialiProgrammazione **
	void createMaterialeProgrammazione(Materiali mat, Integer idPeriodico, AsyncCallback<MaterialiProgrammazione> callback);
	void saveOrUpdateMaterialiProgrammazione(MaterialiProgrammazione item, AsyncCallback<Integer> callback);
	void findMaterialiProgrammazioneById(Integer idMatProg, AsyncCallback<MaterialiProgrammazione> callback);
	void deleteMaterialiProgrammazione(Integer idMaterialiProgrammazione, AsyncCallback<Boolean> callback);
	void findMaterialiProgrammazioneByPeriodico(Integer idPeriodico, long startDt, long finishDt,
			boolean includeOpzioni, boolean orderAsc, int offset, int pageSize, AsyncCallback<List<MaterialiProgrammazione>> callback);
	void findMaterialiProgrammazioneByPeriodico(Integer idPeriodico,
			Integer selectedId, long startDt, long finishDt, boolean includeOpzioni, boolean orderAsc, 
			int offset, int pageSize, AsyncCallback<List<MaterialiProgrammazione>> callback);
	void findMaterialiProgrammazioneByOpzione(Integer idOpzione,
			boolean orderAsc, int offset, int pageSize, AsyncCallback<List<MaterialiProgrammazione>> callback);
	void findMaterialeProgrammazioneByPeriodicoDataInizio(Integer idPeriodico, 
			Date date, AsyncCallback<MaterialiProgrammazione> callback);
	void findPrimoFascicoloNonSpedito(Integer idPeriodico, Date date, 
			AsyncCallback<MaterialiProgrammazione> callback);
	void findFascicoliByEnqueuedMedia(String idTipoMedia, 
			AsyncCallback<Map<MaterialiProgrammazione, Integer>> callback);
	void verifyMaterialiProgrammazioneWithinIstanza(Integer idIstanza, Integer idMatProg, 
			AsyncCallback<Boolean> callback);
	
	
	//** MaterialiSpedizione **
	void createMaterialiSpedizioneForAbbonamento(Integer idIstanza, AsyncCallback<MaterialiSpedizione> callback);
	void createMaterialiSpedizioneForAnagrafica(Integer idAnagrafica, Integer copie, 
			AsyncCallback<MaterialiSpedizione> callback);
	void createMaterialiSpedizioneForCodAbboAndAnagrafica(String codAbbo, Integer idMateriale,
			Integer idAnagrafica, AsyncCallback<Integer> callback);
	void findMaterialiSpedizioneById(Integer idMatSped, AsyncCallback<MaterialiSpedizione> callback);
	void findMaterialiSpedizioneByIstanza(Integer idIstanza, AsyncCallback<List<MaterialiSpedizione>> callback);
	void findMaterialiSpedizioneByAnagrafica(Integer idAnagrafica, AsyncCallback<List<MaterialiSpedizione>> callback);
	void saveOrUpdateMaterialiSpedizione(MaterialiSpedizione item, AsyncCallback<Integer> callback);
	void deleteMaterialiSpedizione(Integer idMatSped, AsyncCallback<Boolean> callback);
	
	void createAllArretrati(Integer idIa, Date today, AsyncCallback<List<MaterialiSpedizione>> callback);
	void createAllArretrati(String codiceAbbonamento, Date today, AsyncCallback<List<MaterialiSpedizione>> callback);
		
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
