package it.giunti.apg.client.services;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.model.ArticoliListini;
import it.giunti.apg.shared.model.ArticoliOpzioni;
import it.giunti.apg.shared.model.Materiali;
import it.giunti.apg.shared.model.MaterialiProgrammazione;
import it.giunti.apg.shared.model.MaterialiSpedizione;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath(AppConstants.SERV_MATERIALI)
public interface MaterialiService extends RemoteService {

	//** Materiali **
	public Materiali createMateriale(String tipoMateriale, String tipoAnagraficaSap) throws BusinessException;
	public Materiali createMaterialeArticolo() throws BusinessException;
	public Materiali createMaterialeFascicoloGe() throws BusinessException;
	public Materiali findMaterialeById(Integer idMateriale) throws BusinessException, EmptyResultException;
	public Integer saveOrUpdateMateriale(Materiali item) throws BusinessException;
	public Boolean deleteMateriale(Integer idMateriale) throws BusinessException;
	public List<Materiali> findMaterialiByDate(Date validDt, int offset, int pageSize) throws BusinessException, EmptyResultException;


	//** MaterialiProgrammazione **
	public MaterialiProgrammazione createMaterialeProgrammazione(Materiali materiale, Integer idPeriodico) throws BusinessException;
	public Integer saveOrUpdateMaterialiProgrammazione(MaterialiProgrammazione item) throws BusinessException;
	public MaterialiProgrammazione findMaterialiProgrammazioneById(Integer idMatProg) throws BusinessException, EmptyResultException;
	public Boolean deleteMaterialiProgrammazione(Integer idMaterialiProgrammazione) throws BusinessException;
	public List<MaterialiProgrammazione> findMaterialiProgrammazioneByPeriodico(Integer idPeriodico, long startDt, long finishDt,
			boolean includeOpzioni, boolean orderAsc, int offset, int pageSize) throws BusinessException, EmptyResultException;
	public List<MaterialiProgrammazione> findMaterialiProgrammazioneByPeriodico(Integer idPeriodico,
			Integer selectedId, long startDt, long finishDt, boolean includeOpzioni, boolean orderAsc, int offset, int pageSize)
			throws BusinessException, EmptyResultException;
	public List<MaterialiProgrammazione> findMaterialiProgrammazioneByOpzione(Integer idOpzione,
			boolean orderAsc, int offset, int pageSize) throws BusinessException, EmptyResultException;
	public MaterialiProgrammazione findMaterialeProgrammazioneByPeriodicoDataInizio(Integer idPeriodico, 
			Date date) throws BusinessException;
	public MaterialiProgrammazione findPrimoFascicoloNonSpedito(Integer idPeriodico, Date date) throws BusinessException;
	public Map<MaterialiProgrammazione, Integer> findFascicoliByEnqueuedMedia(String idTipoMedia)
			throws BusinessException, EmptyResultException;
	public Boolean verifyMaterialiProgrammazioneWithinIstanza(Integer idIstanza, Integer idMatProg) throws BusinessException;
	
	
	//** MaterialiSpedizione **
	public MaterialiSpedizione createMaterialiSpedizioneForAbbonamento(Integer idIstanza) throws BusinessException;
	public MaterialiSpedizione createMaterialiSpedizioneForAnagrafica(Integer idAnagrafica, Integer copie) 
			throws BusinessException;
	public Integer createMaterialiSpedizioneForCodAbboAndAnagrafica(String codAbbo, Integer idMateriale,
			Integer idAnagrafica) throws BusinessException;
	public MaterialiSpedizione findMaterialiSpedizioneById(Integer idMatSped) throws BusinessException, EmptyResultException;
	public List<MaterialiSpedizione> findMaterialiSpedizioneByIstanza(Integer idIstanza)
			throws BusinessException, EmptyResultException;
	public List<MaterialiSpedizione> findMaterialiSpedizioneByAnagrafica(Integer idAnagrafica)
			throws BusinessException, EmptyResultException;
	public Integer saveOrUpdateMaterialiSpedizione(MaterialiSpedizione item) throws BusinessException;
	public Boolean deleteMaterialiSpedizione(Integer idMatSped) throws BusinessException;
	
	public List<MaterialiSpedizione> createAllArretrati(Integer idIa, Date today) throws BusinessException;
	public List<MaterialiSpedizione> createAllArretrati(String codiceAbbonamento, Date today) throws BusinessException;
	
	
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
