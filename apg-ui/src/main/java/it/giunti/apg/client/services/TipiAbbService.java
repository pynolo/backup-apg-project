package it.giunti.apg.client.services;

import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.model.Listini;
import it.giunti.apg.shared.model.TipiAbbonamento;
import it.giunti.apg.shared.model.TipiAbbonamentoRinnovo;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath(AppConstants.SERV_TIPI_ABBONAMENTO)
public interface TipiAbbService extends RemoteService {
	
	//TipiAbbonamento
	public List<TipiAbbonamento> findTipiAbbonamentoByPeriodicoDate(Integer idPeriodico, Integer selectedId, Date beginDate) throws BusinessException, EmptyResultException;
	public List<TipiAbbonamento> findTipiAbbonamentoByPeriodico(Integer idPeriodico, Integer selectedId) throws BusinessException, EmptyResultException;
	
	//Listini
	public Listini findListinoById(Integer idListino) throws BusinessException, EmptyResultException;
	public Listini findDefaultListinoByPeriodicoDate(Integer idPeriodico, Date date) throws BusinessException, EmptyResultException;
	public Listini findDefaultListinoByFascicoloInizio(Integer idPeriodico, Integer idFascicolo) throws BusinessException, EmptyResultException;
	public List<Listini> findListiniByTipoAbb(Integer idTipoAbb, int offset, int pageSize) throws BusinessException, EmptyResultException;
	public List<Listini> findListiniByPeriodicoDate(Integer idPeriodico, Date dt, Integer selectedId, int offset, int pageSize) throws BusinessException, EmptyResultException;
	public List<Listini> findListiniByFascicoloInizio(Integer idPeriodico, Integer idFas, Integer selectedId, int offset, int pageSize) throws BusinessException, EmptyResultException;
	public Listini findListinoByTipoAbbDate(Integer idTipoAbb, Date dt) throws BusinessException, EmptyResultException;
	public Listini createListinoFromPeriodico(Integer idPeriodico) throws BusinessException, EmptyResultException;
	public Listini createListinoFromTipo(Integer idTipoAbbonamento) throws BusinessException, EmptyResultException;
	public Integer saveOrUpdate(Listini lst, List<Integer> tipiAbbRinnovoList) throws BusinessException;
	public Integer createVersion(Listini lst, List<Integer> tipiAbbRinnovoList) throws BusinessException;
	
	//TipiAbbonamentoRinnovo
	public List<TipiAbbonamentoRinnovo> findTipiAbbonamentoRinnovoByListino(Integer idListino) throws BusinessException, EmptyResultException;
	public List<String> findCodiceTipiAbbonamentoRinnovoByListino(
			Integer idListino) throws BusinessException, EmptyResultException;
}
