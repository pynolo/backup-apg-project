package it.giunti.apg.client.services;

import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.Localita;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath(AppConstants.SERV_ANAGRAFICHE)
public interface AnagraficheService extends RemoteService {
	public List<Anagrafiche> findByProperties(String codAnag, String ragSoc,
			String nome, String presso, String indirizzo,
			String cap, String loc, String prov,
			String email, String cfiva,
			Integer idPeriodico, String tipoAbb,
			Date dataValidita, String numFat,
			Integer offset, Integer size) throws BusinessException, EmptyResultException;
	public List<Anagrafiche> findAnagraficheByLastModified(int offset, int pageSize) throws BusinessException, EmptyResultException;
	public List<Anagrafiche> quickSearchAnagrafiche(String searchString, Integer offset, Integer size) throws BusinessException;
	public List<Anagrafiche> simpleSearchByCognomeNome(String searchString, Integer size) throws BusinessException;
	public List<Anagrafiche> findAnagraficheToVerify(int offset, int pageSize) throws BusinessException, EmptyResultException;
	public Anagrafiche findById(Integer id) throws BusinessException, EmptyResultException;
	public String findDescriptionById(Integer id) throws BusinessException, EmptyResultException;
	public Anagrafiche createAnagrafica() throws BusinessException, EmptyResultException;
	public Boolean deleteAnagrafica(Integer idAnagrafica) throws BusinessException;
	public Integer saveOrUpdate(Anagrafiche item) throws BusinessException, ValidationException;
	public Integer countAnagraficaLikeRagSoc(String ragSoc) throws BusinessException;
	public Localita findCapByLocalitaCapString(String localita, String cap) throws BusinessException;
	public Localita findCapByCapString(String cap) throws BusinessException;
	public Localita findCapByLocalitaProv(String localita, String optionalProv) throws BusinessException, EmptyResultException;
	public List<Localita> findLocalitaCapSuggestions(String localitaPrefix, String provinciaPrefix, String capPrefix) throws BusinessException;
	public List<Localita> findLocalitaSuggestions(String localitaPrefix) throws BusinessException;
	public Boolean verifyLocalitaItalia(String localitaName, String localitaProv, String localitaCap) throws BusinessException;
	
	public List<Anagrafiche> findMergeArray(Integer idAnagrafica) throws BusinessException, EmptyResultException;
	public List<Anagrafiche> findMergeArray(Integer idAnagrafica1, Integer idAnagrafica2) throws BusinessException, EmptyResultException;
	public Anagrafiche merge(Anagrafiche anag1, Anagrafiche anag2, Anagrafiche anag3) throws BusinessException, EmptyResultException;

}
