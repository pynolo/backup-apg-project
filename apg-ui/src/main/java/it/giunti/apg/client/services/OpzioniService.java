package it.giunti.apg.client.services;

import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.model.Opzioni;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath(AppConstants.SERV_OPZIONI)
public interface OpzioniService extends RemoteService {
	public Opzioni findOpzioneById(Integer idOpzione) throws BusinessException, EmptyResultException;	
	public Opzioni createOpzione(Integer idPeriodico) throws BusinessException;
	public Integer saveOrUpdateOpzione(Opzioni opzione) throws BusinessException;
	public List<Opzioni> findOpzioni(Long startDt, Long finishDt) throws BusinessException;
	public List<Opzioni> findOpzioni(Date extractionDt) throws BusinessException;
	public List<Opzioni> findOpzioni(Integer idPeriodico, Date extractionDt, Boolean soloCartacei) throws BusinessException;
	public List<Opzioni> findOpzioni(Integer idPeriodico, Date startDt, Date finishDt, Boolean soloCartacei) throws BusinessException;
	public List<Opzioni> findOpzioni(Integer idPeriodico, Integer idFascicolo) throws BusinessException;
	public List<Opzioni> findOpzioniByListino(Integer idListino) throws BusinessException;
	public List<Opzioni> findOpzioniFacoltativeByListino(Integer idListino, Integer idFascicolo) throws BusinessException;
	public String createNewUid(Integer idPeriodico) throws BusinessException;
}
