package it.giunti.apg.client.services;

import it.giunti.apg.shared.model.Comunicazioni;
import it.giunti.apg.shared.model.EvasioniComunicazioni;
import it.giunti.apg.shared.model.ModelliBollettini;
import it.giunti.apg.shared.model.ModelliEmail;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ComunicazioniServiceAsync {
	//Comunicazioni
	void findComunicazioniByPeriodico(Integer idPeriodico, Date dt, int offset, int pageSize, AsyncCallback<List<Comunicazioni>> callback);
	void findComunicazioniByTipoAbb(Integer idTipoAbb, Date dt, int offset, int pageSize, AsyncCallback<List<Comunicazioni>> callback);
	void findComunicazioneById(Integer idCom, AsyncCallback<Comunicazioni> callback);
	void saveOrUpdateComunicazione(Comunicazioni comunicazione, AsyncCallback<Integer> callback);
	void createComunicazione(Integer idPeriodico, AsyncCallback<Comunicazioni> callback);
	void deleteComunicazione(Integer idCom, int pageSize, AsyncCallback<List<Comunicazioni>> callback);
	void getTipiAbbStringFromComunicazione(Integer idCom, AsyncCallback<String> callback);
	void findComunicazioniByEnqueuedMedia(String idTipoMedia, AsyncCallback<Map<Comunicazioni, Integer>> callback);
	
	//EvasioniComunicazioni
	void findEvasioniComunicazioniByIstanza(Integer idIstanza, AsyncCallback<List<EvasioniComunicazioni>> callback);
	void enqueueEvasioneComunicazione(Integer idIstanza, String idUtente,
			String idTipoMedia, String idTipoDestinatario, Boolean richiestaRinnovo,
			String messaggio, AsyncCallback<Boolean> callback);
	void saveOrUpdateEvasioneComunicazione(EvasioniComunicazioni evasioneComunicazione,
			AsyncCallback<Integer> callback);
	void findEvasioneComunicazioneById(Integer idEvasioneCom, AsyncCallback<EvasioniComunicazioni> callback);
	void createEvasioneComunicazione(Integer idIstanza, String idTipoMedia,
			AsyncCallback<EvasioniComunicazioni> callback);
	void deleteEvasioneComunicazione(Integer idIstanza, Integer idEvasioneComunicazione, AsyncCallback<List<EvasioniComunicazioni>> callback);
	
	//Modelli Bollettini
	void findModelliBollettini(int offset, int pageSize, AsyncCallback<List<ModelliBollettini>> callback);
	void findModelliBollettiniByPeriodico(Integer idPeriodico, int offset, int pageSize, AsyncCallback<List<ModelliBollettini>> callback);
	void findModelliBollettiniById(Integer idCom, AsyncCallback<ModelliBollettini> callback);
	void saveOrUpdateModelliBollettini(ModelliBollettini bolMod, AsyncCallback<Integer> callback);
	void createModelliBollettini(Integer idPeriodico, AsyncCallback<ModelliBollettini> callback);
	void deleteModelliBollettini(Integer idBolMod, int pageSize, AsyncCallback<List<ModelliBollettini>> callback);
	void formatBollettinoText(String text, int lineWidth, AsyncCallback<String> callback);
	
	//Modelli Email
	void findModelliEmail(int offset, int pageSize, AsyncCallback<List<ModelliEmail>> callback);
	void findModelliEmailById(Integer idMe, AsyncCallback<ModelliEmail> callback);
	void saveOrUpdateModelliEmail(ModelliEmail modEmail, AsyncCallback<Integer> callback);
	void createModelliEmail(AsyncCallback<ModelliEmail> callback);
	void deleteModelliEmail(Integer idModEmail, int pageSize, AsyncCallback<List<ModelliEmail>> callback);


}
