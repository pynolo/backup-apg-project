package it.giunti.apg.client.services;

import it.giunti.apg.shared.model.Abbonamenti;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Pagamenti;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AbbonamentiServiceAsync {
	void findLastIstanzaByCodice(String codice,
			AsyncCallback<IstanzeAbbonamenti> callback);
	void findAbbonamentiById(Integer id, AsyncCallback<Abbonamenti> callback);
	void findAbbonamentiByCodice(String codice, AsyncCallback<Abbonamenti> callback);
	//void findAbbonamentiByAbbonato(Integer idAbbonato, AsyncCallback<List<Abbonamenti>> callback);
	//void findAbbonamentiByPagante(Integer idPagante, AsyncCallback<List<Abbonamenti>> callback);
	//void findAbbonamentiByAgente(Integer idAgente, AsyncCallback<List<Abbonamenti>> callback);
	//void findAdesioniSuggestions(String adesionePrefix, AsyncCallback<List<String>> callback);
	void createCodiceAbbonamento(Integer idPeriodico, AsyncCallback<String> callback);
	void getStatusMessage(Integer idIstanza, AsyncCallback<String> callback);
	
	void findIstanzeById(Integer id, AsyncCallback<IstanzeAbbonamenti> callback);
	void quickSearchIstanzeAbbonamenti(String searchString, int offset, int size, AsyncCallback<List<IstanzeAbbonamenti>> callback);
	void findIstanzeByAbbonamento(Integer idAbbonamento, AsyncCallback<List<IstanzeAbbonamenti>> callback);
	void findIstanzeByCodice(String codice, int offset, int size, AsyncCallback<List<IstanzeAbbonamenti>> callback);
	//void findIstanzaPiuRecenteByCodice(String codiceAbb, AsyncCallback<IstanzeAbbonamenti> callback);
	//void findIstanzeByAbbonato(Integer idAbbonato, AsyncCallback<List<IstanzeAbbonamenti>> callback);
	//void findIstanzeByPagante(Integer idPagante, AsyncCallback<List<IstanzeAbbonamenti>> callback);
	//void findIstanzeByAgente(Integer idAgente, AsyncCallback<List<IstanzeAbbonamenti>> callback);
	void findIstanzeByLastModified(Integer idPeriodico, int offset, int pageSize, AsyncCallback<List<IstanzeAbbonamenti>> callback);
	//void findIstanzeConCreditoBySocieta(String idSocieta, int monthsExpired, boolean regalo, int offset, int pageSize, AsyncCallback<List<IstanzeAbbonamenti>> callback);
	void findIstanzeProprieByAnagrafica(Integer idAnag, boolean onlyLatest, int offset, int pageSize, AsyncCallback<List<IstanzeAbbonamenti>> callback);
	void findIstanzeRegalateByAnagrafica(Integer idAnag, boolean onlyLatest, int offset, int pageSize, AsyncCallback<List<IstanzeAbbonamenti>> callback);
	void findIstanzePromosseByAnagrafica(Integer idAnag, boolean onlyLatest, int offset, int pageSize, AsyncCallback<List<IstanzeAbbonamenti>> callback);
	void findLastIstanzeByAnagraficaSocieta(Integer idAnagrafica, String idSocieta, boolean soloPagate, boolean soloScadute, AsyncCallback<List<IstanzeAbbonamenti>> callback);

	void createAbbonamentoAndIstanza(Integer idAbbonato, Integer idPagante, Integer idAgente, Integer idPeriodico, AsyncCallback<IstanzeAbbonamenti> callback);
	void isRenewable(Integer idIstanza, AsyncCallback<Boolean> callback);
	void isRegenerable(Integer idIstanza, AsyncCallback<Boolean> callback);
	void makeBasicRenewal(Integer idOldIstanza, String userId, AsyncCallback<IstanzeAbbonamenti> callback);
	void makeBasicRegeneration(Integer idOldIstanza, String userId, AsyncCallback<IstanzeAbbonamenti> callback);
	void save(IstanzeAbbonamenti ia, AsyncCallback<Integer> callback);
	void update(IstanzeAbbonamenti ia, boolean assignNewCodiceAbbonamento, AsyncCallback<Integer> callback);
	void saveWithPayment(IstanzeAbbonamenti ia, Pagamenti pagamento, AsyncCallback<Integer> callback);
	void saveWithAnagraficaAndPayment(Anagrafiche anag, IstanzeAbbonamenti ia, Pagamenti pagamento, AsyncCallback<String> callback);
	void countIstanzeByCodice(String codice, AsyncCallback<Integer> callback);
	void countIstanzeByTipoAbbonamento(Integer idTipoAbbonamento, Date date, AsyncCallback<Integer> callback);
	void deleteIstanza(Integer idIstanza, AsyncCallback<Boolean> callback);
	
	void calculateDataFine(Date inizio, Integer months, AsyncCallback<Date> callback);
	void changePeriodico(IstanzeAbbonamenti istanzaT, Integer idPeriodico, String stringaTipoAbbonamento, AsyncCallback<IstanzeAbbonamenti> callback);
	void changeFascicoloInizio(IstanzeAbbonamenti istanzaT, Integer idFascicolo, String stringaTipoAbbonamento, AsyncCallback<IstanzeAbbonamenti> callback);
	void changeListino(IstanzeAbbonamenti istanzaT, Integer idListino, AsyncCallback<IstanzeAbbonamenti> callback);

	//Verifica
	void findCodiceAbbonamento(String codiceAbbonamento, AsyncCallback<Boolean> callback);
	void findCodiceAbbonamentoIfDifferentAbbonato(String codiceAbbonamento, Integer idAbbonato, AsyncCallback<Boolean> callback);
	void verifyTotaleNumeri(Integer idIstanza, AsyncCallback<Boolean> callback);
	void verifyPagante(Integer idIstanza, AsyncCallback<Boolean> callback);
	void verifyMacroarea(Integer idIstanza, AsyncCallback<Boolean> callback);
	
	void changeListinoAndOpzioni(Integer idIa, Integer selectedIdListino, Integer copie,
			List<Integer> selectedIdOpzList, String idUtente,
			AsyncCallback<IstanzeAbbonamenti> callback);

}
