package it.giunti.apg.client.services;

import java.io.IOException;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.model.FileUploads;
import it.giunti.apg.shared.model.RinnoviMassivi;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath(AppConstants.SERV_UTIL)
public interface UtilService extends RemoteService {
	//Install info
	public String getApgTitle() throws EmptyResultException;
	public String getApgStatus() throws EmptyResultException;
	public String getApgMenuImage() throws EmptyResultException;
	public String getApgLoginImage() throws EmptyResultException;
	public String getApgVersion() throws EmptyResultException;
	public String getApguiInstallInfo(String appBaseUrl) throws IOException;
	public String getApgwsInstallInfo(String appBaseUrl) throws IOException;
	public String getApgautomationInstallInfo(String appBaseUrl) throws IOException;
	
	//RinnoviMassivi
	public List<RinnoviMassivi> findRinnoviMassivi(Integer idPeriodico) throws BusinessException, EmptyResultException;
	public Boolean saveOrUpdateRinnoviMassiviList(List<RinnoviMassivi> rinnoviMassiviList) throws BusinessException;
	public Boolean deleteRinnovoMassivo(Integer idRinnovoMassivo) throws BusinessException;
	
	//File upload
	public List<FileUploads> findFileUploadsStripped() throws BusinessException, EmptyResultException;
	public Boolean deleteFileUpload(Integer idFileUpload) throws BusinessException;

}
