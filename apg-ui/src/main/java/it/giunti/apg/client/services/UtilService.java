package it.giunti.apg.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.model.RinnoviMassivi;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath(AppConstants.SERV_AUTH)
public interface UtilService extends RemoteService {
	public String getApgTitle() throws EmptyResultException;
	public String getApgStatus() throws EmptyResultException;
	public String getApgMenuImage() throws EmptyResultException;
	public String getApgLoginImage() throws EmptyResultException;
	public String getApgVersion() throws EmptyResultException;

	//RinnoviMassivi
	public List<RinnoviMassivi> findRinnoviMassivi(Integer idPeriodico) throws BusinessException, EmptyResultException;
	public Boolean saveOrUpdateRinnoviMassiviList(List<RinnoviMassivi> rinnoviMassiviList) throws BusinessException;
	public Boolean deleteRinnovoMassivo(Integer idRinnovoMassivo) throws BusinessException;
	
}
