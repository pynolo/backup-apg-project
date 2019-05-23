package it.giunti.apg.client.services;

import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.Fatture;
import it.giunti.apg.shared.model.FattureArticoli;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Pagamenti;
import it.giunti.apg.shared.model.PagamentiCrediti;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface PagamentiServiceAsync {
	void findPagamentiByIstanzaAbbonamento(IstanzeAbbonamenti ist, AsyncCallback<List<Pagamenti>> callback);
	void findPagamentiByAnagrafica(Integer idAnagrafica, AsyncCallback<List<Pagamenti>> callback);
	//void findPagamentiCorrezioniAttachingIstanza(
	//		Integer idPeriodico, boolean showCreditList, int offset, int pageSize, AsyncCallback<List<Pagamenti>> callback);
	//void findPagamentiNonRegistrati(Integer idPeriodico, int offset, int pageSize,
	//		AsyncCallback<List<Pagamenti>> callback);
	void findPagamentiConErrore(Integer idPeriodico, int offset, int pageSize,
			AsyncCallback<List<Pagamenti>> callback);
	void findPagamentoById(Integer idPagamento, AsyncCallback<Pagamenti> callback);
	void findPagamentiById(Integer idPagamento,	AsyncCallback<List<Pagamenti>> callback);
	void saveOrUpdate(Pagamenti pagamento, AsyncCallback<Integer> callback);
	//void createPagamentoManuale(Integer idIstanza, String codiceAbbonamento, AsyncCallback<Pagamenti> callback);
	//void createPagamentoManuale(String codiceAbbonamento, AsyncCallback<Pagamenti> callback);
	void createPagamentoManuale(Integer idAnagrafica, AsyncCallback<Pagamenti> callback);
	void deletePagamento(Integer idPagamento, AsyncCallback<Boolean> callback);
	void sumPagamentiByIstanzaAbbonamento(Integer idIstanza, AsyncCallback<Double> callback);
	void isPagato(Integer idIstanza, AsyncCallback<Boolean> callback);
	void getImportoTotale(Integer idIstanza, AsyncCallback<Double> callback);
	void getStimaImportoTotale(Integer idLst, Integer copie, Set<Integer> idOpzSet, AsyncCallback<Double> callback);
	void findPagamentiFatturabiliByAnagraficaSocieta(Integer idIa, String idSocieta, AsyncCallback<List<Pagamenti>> callback);
	void verifyPagatoAndUpdate(Integer idIa, AsyncCallback<Boolean> callback);
	void getImportoMancante(Integer idIstanza, AsyncCallback<Double> callback);
	
	void getCreditoByAnagraficaSocieta(Integer idAnagrafica, String idSocieta, Boolean stornati, Boolean fatturati, AsyncCallback<Double> callback);
	void findCreditiByAnagrafica(Integer idAnagrafica, AsyncCallback<List<PagamentiCrediti>> callback);
	void findCreditiByAnagraficaSocieta(Integer idAnagrafica, String idSocieta, Boolean stornati, AsyncCallback<List<PagamentiCrediti>> callback);
	void findCreditiByIstanza(Integer idIstanzaAbbonamento, AsyncCallback<List<PagamentiCrediti>> callback);
	void findCreditiBySocieta(String idSocieta, boolean conIstanzeDaPagare, boolean conIstanzeScadute, int offset, int pageSize, AsyncCallback<List<PagamentiCrediti>> callback);
	
	//void correzioneAbbina(Integer idPagamento, Integer idIa, boolean marcaCredito, Utenti utente, AsyncCallback<IstanzeAbbonamenti> callback);
	//void correzioneRinnova(Integer idPagamento, Integer idIa, Utenti utente, AsyncCallback<IstanzeAbbonamenti> callback);
	//void correzioneRigenera(Integer idPagamento, Integer idIa, Utenti utente, AsyncCallback<IstanzeAbbonamenti> callback);
	//void correzioneCredito(Integer idPagamento, Integer idAnagrafica, Utenti utente, AsyncCallback<Anagrafiche> callback);
	void registraAnticipoFattura(Integer idPagamento, Integer idAnagrafica, String idUtente, AsyncCallback<Anagrafiche> callback);
	
	//void getRimborsoByIdFattura(Integer idFatturaOrig, AsyncCallback<Fatture> callback);
	void processFinalPayment(Date dataPagamento, Date dataAccredito, Set<Integer> idPagSet,
			Set<Integer> idCredSet, Integer idIa, Set<Integer> idOpzSet, 
			String annotazioneArticoli, String idUtente, AsyncCallback<Fatture> callback);
	void processDepositPayment(Date dataPagamento, Date dataAccredito, Integer idPagamento, Integer idPagante, 
			String idSocieta, String idUtente, AsyncCallback<Fatture> callback);
	void findFattureByAnagrafica(Integer idAnagrafica, boolean publicOnly, AsyncCallback<List<Fatture>> callback);
	void findFatturaById(Integer idFattura, AsyncCallback<Fatture> callback);
	void findFattureByIstanza(Integer idIstanzaAbbonamento, boolean publicOnly, AsyncCallback<List<Fatture>> callback);
	void findFattureArticoliByIdFattura(Integer idFattura, AsyncCallback<List<FattureArticoli>> callback);
	void setFatturaPubblica(Integer idFattura, boolean pubblica, AsyncCallback<Boolean> callback);
	void createRimborsoTotale(Integer idFattura, String idUtente, AsyncCallback<Fatture> callback);
	void createStornoTotale(Integer idFattura, String idUtente, AsyncCallback<Fatture> callback);
	void createRimborsoResto(Integer idFattura, String idUtente, AsyncCallback<Fatture> callback);
	void createStornoResto(Integer idFattura, String idUtente, AsyncCallback<Fatture> callback);
	void createPagamentoAfterFatturaRimborso(Integer idFattura, String idUtente, AsyncCallback<Fatture> callback);

}
