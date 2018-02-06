package it.giunti.apg.client.services;

import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.Localita;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AnagraficheServiceAsync {
	void findByProperties(String codAnag, String ragSoc, String nome,
			String presso, String indirizzo,
			String cap, String loc, String prov, 
			String email, String cfiva,
			Integer idPeriodico, String tipoAbb,
			Date dataValidita, String numFat,
			Integer offset, Integer size, AsyncCallback<List<Anagrafiche>> callback);
	void findAnagraficheByLastModified(int offset, int pageSize, AsyncCallback<List<Anagrafiche>> callback);
	void quickSearchAnagrafiche(String searchString, Integer offset, Integer size, AsyncCallback<List<Anagrafiche>> callback);
	//void simpleSearchByCognomeNome(String searchString, Integer size, AsyncCallback<List<Anagrafiche>> callback);
	void findAnagraficheToVerify(int offset, int pageSize, AsyncCallback<List<Anagrafiche>> callback);
	void findById(Integer id, AsyncCallback<Anagrafiche> callback);
	void findDescriptionById(Integer id, AsyncCallback<String> callback);
	void createAnagrafica(AsyncCallback<Anagrafiche> callback);
	void deleteAnagrafica(Integer idAnagrafica, AsyncCallback<Boolean> callback);
	void saveOrUpdate(Anagrafiche item, AsyncCallback<Integer> callback);
	void countAnagraficaLikeRagSoc(String ragSoc, AsyncCallback<Integer> callback);
	void findCapByLocalitaCapString(String localita, String cap, AsyncCallback<Localita> callback);
	void findCapByCapString(String cap, AsyncCallback<Localita> callback);
	void findCapByLocalitaProv(String localita, String optionalProv, AsyncCallback<Localita> callback);
	void findLocalitaCapSuggestions(String localitaPrefix, String provinciaPrefix, String capPrefix, AsyncCallback<List<Localita>> callback);
	void findLocalitaSuggestions(String localitaPrefix, AsyncCallback<List<Localita>> callback);
	void verifyLocalitaItalia(String localitaName, String localitaProv, String localitaCap, AsyncCallback<Boolean> callback);
	void findMergeArray(Integer idAnagrafica, AsyncCallback<List<Anagrafiche>> callback);
	void findMergeArray(Integer idAnagrafica1, Integer idAnagrafica2, AsyncCallback<List<Anagrafiche>> callback);
	void merge(Anagrafiche anag1, Anagrafiche anag2, Anagrafiche anag3, AsyncCallback<Anagrafiche> callback);
	
}
