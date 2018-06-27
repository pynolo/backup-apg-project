package it.giunti.apg.client.services;

import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.Fatture;
import it.giunti.apg.shared.model.FattureArticoli;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Pagamenti;
import it.giunti.apg.shared.model.PagamentiCrediti;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath(AppConstants.SERV_PAGAMENTI)
public interface PagamentiService extends RemoteService {
	public List<Pagamenti> findPagamentiByIstanzaAbbonamento(IstanzeAbbonamenti ist) throws BusinessException, EmptyResultException;
	public List<Pagamenti> findPagamentiByAnagrafica(Integer idAnagrafica) throws BusinessException, EmptyResultException;
	//public List<Pagamenti> findPagamentiCorrezioniAttachingIstanza(
	//		Integer idPeriodico, boolean showCreditList, int offset, int pageSize) throws BusinessException, EmptyResultException;
	//public List<Pagamenti> findPagamentiNonRegistrati(Integer idPeriodico, int offset, int pageSize) throws BusinessException, EmptyResultException;
	public List<Pagamenti> findPagamentiConErrore(Integer idPeriodico, int offset, int pageSize) throws BusinessException, EmptyResultException;
	public Pagamenti findPagamentoById(Integer idPagamento) throws BusinessException, EmptyResultException;
	public List<Pagamenti> findPagamentiById(Integer idPagamento) throws BusinessException, EmptyResultException;
	public Integer saveOrUpdate(Pagamenti pagamento) throws BusinessException;
	//public Pagamenti createPagamentoManuale(Integer idIstanza, String codiceAbbonamento) throws BusinessException;
	//public Pagamenti createPagamentoManuale(String codiceAbbonamento) throws BusinessException;
	public Pagamenti createPagamentoManuale(Integer idAnagrafica) throws BusinessException;
	public Boolean deletePagamento(Integer idPagamento) throws BusinessException;
	public Boolean isPagato(Integer idIstanza) throws BusinessException;
	public Double sumPagamentiByIstanzaAbbonamento(Integer idIstanza) throws BusinessException, ValidationException;
	public Double getImportoTotale(Integer idIstanza) throws BusinessException, ValidationException;
	public Double getStimaImportoTotale(Integer idLst, Integer copie, Set<Integer> idOpzSet) throws BusinessException, ValidationException;
	public List<Pagamenti> findPagamentiFatturabiliByAnagraficaSocieta(Integer idIa, String idSocieta) throws BusinessException, EmptyResultException;
	public Boolean verifyPagatoAndUpdate(Integer idIa) throws BusinessException;
	public Double getImportoMancante(Integer idIstanza) throws BusinessException, ValidationException;
	
	//Crediti
	public Double getCreditoByAnagraficaSocieta(Integer idAnagrafica, String idSocieta, Boolean stornati, Boolean fatturati) throws BusinessException;
	public List<PagamentiCrediti> findCreditiByAnagrafica(Integer idAnagrafica) throws BusinessException, EmptyResultException;
	public List<PagamentiCrediti> findCreditiByAnagraficaSocieta(Integer idAnagrafica, String idSocieta, Boolean stornati) throws BusinessException, EmptyResultException;
	public List<PagamentiCrediti> findCreditiByIstanza(Integer idIstanzaAbbonamento) throws BusinessException, EmptyResultException;
	
	//metodi di correzione
	//public IstanzeAbbonamenti correzioneAbbina(Integer idPagamento, Integer idIa, boolean marcaCredito, Utenti utente) throws BusinessException, EmptyResultException;
	//public IstanzeAbbonamenti correzioneRinnova(Integer idPagamento, Integer idIa, Utenti utente) throws BusinessException, EmptyResultException;
	//public IstanzeAbbonamenti correzioneRigenera(Integer idPagamento, Integer idIa, Utenti utente) throws BusinessException, EmptyResultException;
	//public Anagrafiche correzioneCredito(Integer idPagamento, Integer idAnagrafica, Utenti utente) throws BusinessException, EmptyResultException;
	public Anagrafiche registraAnticipoFattura(Integer idPagamento, Integer idAnagrafica, String idUtente) throws BusinessException, EmptyResultException;
	
	//fatture
	public Fatture processPayment(Date dataPagamento, Date dataAccredito, List<Integer> idPagList, List<Integer> idCredList,
			Integer idIa, List<Integer> idOpzList, String idUtente) throws BusinessException;
	public Fatture processPayment(Date dataPagamento, Date dataAccredito, Integer idPagamento, Integer idPagante,
			String idSocieta, String idUtente) throws BusinessException;
	public List<Fatture> findFattureByAnagrafica(Integer idAnagrafica, boolean publicOnly) throws BusinessException, EmptyResultException;
	public Fatture findFatturaById(Integer idFattura) throws BusinessException, EmptyResultException;
	public List<Fatture> findFattureByIstanza(Integer idIstanzaAbbonamento, boolean publicOnly) throws BusinessException, EmptyResultException;
	public List<FattureArticoli> findFattureArticoliByIdFattura(Integer idFattura) throws BusinessException, EmptyResultException;
	public Fatture createRimborsoTotale(Integer idFattura) throws BusinessException;
	public Fatture createStornoTotale(Integer idFattura) throws BusinessException;
	public Fatture createRimborsoResto(Integer idFattura) throws BusinessException;
	public Fatture createStornoResto(Integer idFattura) throws BusinessException;
	public Fatture createPagamentoAfterFatturaRimborso(Integer idFattura, String idUtente) throws BusinessException;

}
