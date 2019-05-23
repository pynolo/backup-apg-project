package it.giunti.apg.client.services;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.model.AliquoteIva;
import it.giunti.apg.shared.model.FileResources;
import it.giunti.apg.shared.model.Macroaree;
import it.giunti.apg.shared.model.Nazioni;
import it.giunti.apg.shared.model.Periodici;
import it.giunti.apg.shared.model.Professioni;
import it.giunti.apg.shared.model.Province;
import it.giunti.apg.shared.model.Societa;
import it.giunti.apg.shared.model.TipiDisdetta;
import it.giunti.apg.shared.model.TitoliStudio;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath(AppConstants.SERV_LOOKUP)
public interface LookupService extends RemoteService {
	public List<Periodici> findPeriodici() throws BusinessException, EmptyResultException;
	public List<Periodici> findPeriodici(Date extractionDt) throws BusinessException, EmptyResultException;
	public List<Periodici> findPeriodici(Integer selectedId, Date extractionDt) throws BusinessException, EmptyResultException;
	public Societa findSocietaById(String idSocieta) throws BusinessException, EmptyResultException;
	public List<Province> findProvince() throws BusinessException, EmptyResultException;
	public List<Nazioni> findNazioni() throws BusinessException, EmptyResultException;
	public List<Professioni> findProfessioni() throws BusinessException, EmptyResultException;
	public List<TitoliStudio> findTitoliStudio() throws BusinessException, EmptyResultException;
	public List<Macroaree> findMacroaree() throws BusinessException, EmptyResultException;
	public List<TipiDisdetta> findTipiDisdetta() throws BusinessException, EmptyResultException;
	public List<AliquoteIva> findAliquoteIva(Date selectionDate) throws BusinessException, EmptyResultException;
	public List<FileResources> findFileResources(String fileType) throws BusinessException, EmptyResultException;
	
}
