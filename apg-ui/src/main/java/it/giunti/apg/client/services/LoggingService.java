package it.giunti.apg.client.services;

import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.model.Avvisi;
import it.giunti.apg.shared.model.LogEditing;
import it.giunti.apg.shared.model.Rapporti;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath(AppConstants.SERV_LOGGING)
public interface LoggingService extends RemoteService {
	//Rapporti
	public Integer createRapporto(String titolo, String idUtente) throws BusinessException, EmptyResultException;
	public List<String> receiveLogLines(int idRapporto, int expectedLine) throws EmptyResultException;
	public List<Rapporti> findRapportiStripped(Date extractionDt, int offset, int size) throws BusinessException, EmptyResultException;
	public Rapporti findRapportoById(Integer idRapporto) throws BusinessException, EmptyResultException;
	
	//Notizie e Maintenance
	public List<Avvisi> findLastAvvisi(int offset, int size) throws BusinessException, EmptyResultException;
	public List<Avvisi> findLastAvvisiByGiorniTipo(int giorniAntecedenti) throws BusinessException;
	public Integer saveAvviso(String message, boolean importante, Date maintenanceDt, String idUtente) throws BusinessException;
	public List<Avvisi> deleteAvviso(Integer idAvviso, int pageSize) throws BusinessException, EmptyResultException;
	public Boolean updateImportanza(Integer idAvviso, boolean importante) throws BusinessException;
	public Avvisi checkMaintenence() throws BusinessException;
	
	//LogEditing
	public List<LogEditing> findEditLogs(String classSimpleName, Integer entityId) throws BusinessException, EmptyResultException;
}
