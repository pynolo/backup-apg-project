package it.giunti.apg.client.services;

import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.model.EvasioniFascicoli;
import it.giunti.apg.shared.model.Fascicoli;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath(AppConstants.SERV_FASCICOLI)
public interface FascicoliService6 extends RemoteService {
	
	//Fascicoli
	public List<Fascicoli> findFascicoliByPeriodico(Integer idPeriodico,
			long startDt, long finishDt,
			boolean includeOpzioni, boolean orderAsc,
			int offset, int pageSize) throws BusinessException, EmptyResultException;
	public List<Fascicoli> findFascicoliByPeriodico(Integer idPeriodico, Integer selectedId,
			long startDt, long finishDt,
			boolean includeOpzioni, boolean orderAsc,
			int offset, int pageSize) throws BusinessException, EmptyResultException;
	public List<Fascicoli> findFascicoliByOpzione(Integer idOpzione, boolean orderAsc,
			int offset, int pageSize) throws BusinessException, EmptyResultException;
	public Fascicoli findFascicoloById(Integer idFas) throws BusinessException, EmptyResultException;	
	public Fascicoli createFascicolo(Integer idPeriodico, Boolean isOpzione) throws BusinessException;
	public Integer saveOrUpdate(Fascicoli fascicolo) throws BusinessException;
	public List<Fascicoli> deleteFascicolo(Integer idFas) throws BusinessException, EmptyResultException;	
	public Fascicoli findFascicoloByPeriodicoDataInizio(Integer idPeriodico, Date date) throws BusinessException;
	public Fascicoli findPrimoFascicoloNonSpedito(Integer idPeriodico, Date date, Boolean includeAllegati) throws BusinessException;
	public Map<Fascicoli, Integer> findFascicoliByEnqueuedMedia(String idTipoMedia) throws BusinessException, EmptyResultException;
	public Integer countFascicoliBetweenFascicoli(String idPeriodico, String idFasInizio, String idFasFine) throws BusinessException;
	public Integer countFascicoliTotali(Integer idIstanza) throws BusinessException;
	public Integer countFascicoliDaSpedire(Integer idIstanza) throws BusinessException;
	public Integer countFascicoliSpediti(Integer idIstanza) throws BusinessException;
	public Boolean verifyFascicoloWithinIstanza(Integer idIstanza, Integer idFascicolo) throws BusinessException;
	
	//Evasioni fascicoli
	public List<EvasioniFascicoli> findEvasioniFascicoliByIstanza(Integer idIstanza) throws BusinessException, EmptyResultException;
	public EvasioniFascicoli findEvasioneFascicoloById(Integer idEvasioneFascicolo) throws BusinessException, EmptyResultException;
	public Integer saveOrUpdate(EvasioniFascicoli evasioneFascicolo) throws BusinessException;
	//opzioni dall'istanza
	public EvasioniFascicoli createEvasioneFascicoloForIstanza(Integer idIstanza,
			String idTipoEvasione) throws BusinessException;	
	public EvasioniFascicoli createEvasioneFascicoloForAnagrafica(Integer idAnagrafica,
			Integer copie, String idTipoEvasione) throws BusinessException;
	public List<EvasioniFascicoli> deleteEvasioneFascicolo(Integer idIstanza, Integer idEvasioneFascicolo) throws BusinessException, EmptyResultException;	
	//Creazione massiva arretrati
	public List<EvasioniFascicoli> createMassiveArretrati(Integer idIstanza, Date today, String idUtente) throws BusinessException;
	public List<EvasioniFascicoli> createMassiveArretrati(String codiceAbbonamento, Date today, String idUtente) throws BusinessException;

	//Opzioni
	public String getOpzioniDescr(String opzioniList) throws BusinessException;
}
