package it.giunti.apg.client.services;

import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.model.Abbonamenti;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Pagamenti;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath(AppConstants.SERV_ABBONAMENTI)
public interface AbbonamentiService extends RemoteService {
	public Abbonamenti findAbbonamentiById(Integer id) throws BusinessException, EmptyResultException;
	public Abbonamenti findAbbonamentiByCodice(String codice) throws BusinessException, EmptyResultException;
	//public List<Abbonamenti> findAbbonamentiByAbbonato(Integer idAbbonato) throws PagamentiException, EmptyResultException;
	//public List<Abbonamenti> findAbbonamentiByPagante(Integer idPagante) throws PagamentiException, EmptyResultException;
	//public List<Abbonamenti> findAbbonamentiByAgente(Integer idAgente) throws PagamentiException, EmptyResultException;
	//public List<String> findAdesioniSuggestions(String adesionePrefix) throws BusinessException;
	public String createCodiceAbbonamento(Integer idPeriodico) throws BusinessException, EmptyResultException;
	public String getStatusMessage(Integer idIstanza) throws BusinessException;
	
	public IstanzeAbbonamenti findIstanzeById(Integer id) throws BusinessException, EmptyResultException;
	public IstanzeAbbonamenti findLastIstanzaByCodice(String codice) throws BusinessException, EmptyResultException;
	public List<IstanzeAbbonamenti> quickSearchIstanzeAbbonamenti(String searchString, int offset, int size) throws BusinessException;
	public List<IstanzeAbbonamenti> findIstanzeByAbbonamento(Integer idAbbonamenti) throws BusinessException, EmptyResultException;
	public List<IstanzeAbbonamenti> findIstanzeByCodice(String codice, int offset, int size) throws BusinessException, EmptyResultException;
	//public List<IstanzeAbbonamenti> findIstanzeByAbbonato(Integer idAbbonato) throws PagamentiException, EmptyResultException;
	//public List<IstanzeAbbonamenti> findIstanzeByPagante(Integer idPagante) throws PagamentiException, EmptyResultException;
	//public List<IstanzeAbbonamenti> findIstanzeByAgente(Integer idAgente) throws PagamentiException, EmptyResultException;
	public List<IstanzeAbbonamenti> findIstanzeByLastModified(Integer idPeriodico, int offset, int pageSize) throws BusinessException, EmptyResultException;
	public List<IstanzeAbbonamenti> findIstanzeConCreditoBySocieta(String idSocieta, int monthsExpired, boolean regalo, int offset, int pageSize) throws BusinessException, EmptyResultException;
	public List<IstanzeAbbonamenti> findIstanzeProprieByAnagrafica(Integer idAnag, boolean onlyLatest, int offset, int pageSize) throws BusinessException, EmptyResultException;
	public List<IstanzeAbbonamenti> findIstanzeRegalateByAnagrafica(Integer idAnag, boolean onlyLatest, int offset, int pageSize) throws BusinessException, EmptyResultException;
	public List<IstanzeAbbonamenti> findIstanzePromosseByAnagrafica(Integer idAnag, boolean onlyLatest, int offset, int pageSize) throws BusinessException, EmptyResultException;
	
	public IstanzeAbbonamenti createAbbonamentoAndIstanza(Integer idAbbonato, Integer idPagante, Integer idAgente, Integer idPeriodico) throws BusinessException, EmptyResultException;
	public Boolean isRenewable(Integer idIstanza)  throws BusinessException;
	public Boolean isRegenerable(Integer idIstanza)  throws BusinessException;
	public IstanzeAbbonamenti makeBasicRenewal(Integer idOldIstanza, String userId) throws BusinessException, EmptyResultException;
	public IstanzeAbbonamenti makeBasicRegeneration(Integer idOldIstanza, String userId) throws BusinessException, EmptyResultException;
	public Integer save(IstanzeAbbonamenti ia) throws BusinessException;
	public Integer update(IstanzeAbbonamenti ia, boolean assignNewCodiceAbbonamento) throws BusinessException;
	public Integer saveWithPayment(IstanzeAbbonamenti ia, Pagamenti pagamento) throws BusinessException, ValidationException;
	public String saveWithAnagraficaAndPayment(Anagrafiche anag, IstanzeAbbonamenti ia, Pagamenti pagamento) throws BusinessException, ValidationException;
	public Integer countIstanzeByCodice(String codice) throws BusinessException;
	public Integer countIstanzeByTipoAbbonamento(Integer idTipoAbbonamento, Date date) throws BusinessException;
	public Boolean deleteIstanza(Integer idIstanza) throws BusinessException;
	
	public Date calculateDataFine(Date inizio, Integer months);
	public IstanzeAbbonamenti changePeriodico(IstanzeAbbonamenti istanzaT, Integer idPeriodico, String stringaTipoAbbonamento) throws BusinessException;
	public IstanzeAbbonamenti changeFascicoloInizio(IstanzeAbbonamenti istanzaT, Integer idFascicolo, String stringaTipoAbbonamento) throws BusinessException;
	public IstanzeAbbonamenti changeListino(IstanzeAbbonamenti istanzaT, Integer idListino) throws BusinessException;

	//Verifica
	public Boolean findCodiceAbbonamento(String codiceAbbonamento) throws BusinessException;
	public Boolean findCodiceAbbonamentoIfDifferentAbbonato(String codiceAbbonamento, Integer idAbbonato) throws BusinessException;
	public Boolean verifyTotaleNumeri(Integer idIstanza) throws BusinessException, ValidationException;
	//public Boolean verifyPagante(Integer idIstanza) throws BusinessException, ValidationException;
	public Boolean verifyMacroarea(Integer idIstanza) throws BusinessException, ValidationException;

	//Modifiche abbonamenti
	public IstanzeAbbonamenti changeListinoAndOpzioni(Integer idIa,
			Integer selectedIdListino, Integer copie, 
			List<Integer> selectedIdOpzList, String idUtente) throws BusinessException;
	
}
