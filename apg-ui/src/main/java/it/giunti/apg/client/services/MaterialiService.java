package it.giunti.apg.client.services;

import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.model.Articoli;
import it.giunti.apg.shared.model.ArticoliListini;
import it.giunti.apg.shared.model.ArticoliOpzioni;
import it.giunti.apg.shared.model.EvasioniArticoli;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath(AppConstants.SERV_MATERIALI)
public interface MaterialiService extends RemoteService {

	//Articolo
	public Articoli findArticoloById(Integer idArticolo) throws BusinessException, EmptyResultException;
	public Integer saveOrUpdateArticolo(Articoli articolo) throws BusinessException;
	public Articoli createArticolo() throws BusinessException;
	public List<Articoli> findArticoliByDate(Date validDt, int offset, int pageSize) throws BusinessException, EmptyResultException;
	public List<Articoli> findArticoliByDateInterval(Date startDt, Date finishDt) throws BusinessException, EmptyResultException;
	
	//EvasioniArticoli
	public List<EvasioniArticoli> findEvasioniArticoliByIstanza(Integer idIstanza) throws BusinessException, EmptyResultException;
	public List<EvasioniArticoli> findEvasioniArticoliByAnagrafica(Integer idAnagrafica) throws BusinessException, EmptyResultException;
	public EvasioniArticoli findEvasioniArticoliById(Integer idEd) throws BusinessException, EmptyResultException;
	//public Date loadDataLimite(Integer idIstanza, Integer idArticolo) throws BusinessException;
	//public Date loadDataLimite(Integer idArticolo) throws BusinessException;
	public EvasioniArticoli createEmptyEvasioneArticoloFromIstanza(Integer idIstanza,
			String idTipoDestinatario, String idUtente) throws BusinessException;
	public EvasioniArticoli createEvasioneArticoloFromAnagrafica(Integer idAnagrafica,
			Integer copie, String idTipoDestinatario, String idUtente) throws BusinessException;
	public Integer createEvasioneArticoloWithCodAbbo(String codAbbo, Integer idArticolo,
			String idTipoDestinatario, String idUtente) throws BusinessException;
	public Integer saveOrUpdateEvasioneArticolo(EvasioniArticoli evasioneArticolo) throws BusinessException;
	public List<EvasioniArticoli> deleteEvasioneArticolo(Integer idEvasioneArticolo) throws BusinessException, EmptyResultException;	
	
	//ArticoliListini
	public ArticoliListini findArticoloListinoById(Integer idArticoloListino) throws BusinessException, EmptyResultException;
	public Integer saveOrUpdateArticoloListino(ArticoliListini articoloListino) throws BusinessException;
	public ArticoliListini createArticoloListino(Integer idListino) throws BusinessException;
	public List<ArticoliListini> deleteArticoloListino(Integer idListino) throws BusinessException, EmptyResultException;
	public List<ArticoliListini> findArticoliListini(Integer idListino) throws BusinessException, EmptyResultException;
	public List<ArticoliListini> findArticoliListiniByPeriodicoDate(Integer idPeriodico, Date date) throws BusinessException, EmptyResultException;
	public Map<ArticoliListini, Integer> findPendingArticoliListiniCount() throws BusinessException, EmptyResultException;
			
	//ArticoliOpzioni
	public ArticoliOpzioni findArticoloOpzioneById(Integer idArticoloOpzione) throws BusinessException, EmptyResultException;
	public Integer saveOrUpdateArticoloOpzione(ArticoliOpzioni articoloOpzione) throws BusinessException;
	public ArticoliOpzioni createArticoloOpzione(Integer idOpzione) throws BusinessException;
	public List<ArticoliOpzioni> deleteArticoloOpzione(Integer idOpzione) throws BusinessException, EmptyResultException;
	public List<ArticoliOpzioni> findArticoliOpzioni(Integer idOpzione) throws BusinessException, EmptyResultException;
	public List<ArticoliOpzioni> findArticoliOpzioniByPeriodicoDate(Integer idPeriodico, Date date) throws BusinessException, EmptyResultException;
	public Map<ArticoliOpzioni, Integer> findPendingArticoliOpzioniCount() throws BusinessException, EmptyResultException;
	
}
