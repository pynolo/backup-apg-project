package it.giunti.apg.client.services;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.model.Adesioni;
import it.giunti.apg.shared.model.AliquoteIva;
import it.giunti.apg.shared.model.FileResources;
import it.giunti.apg.shared.model.FileUploads;
import it.giunti.apg.shared.model.Macroaree;
import it.giunti.apg.shared.model.Nazioni;
import it.giunti.apg.shared.model.Periodici;
import it.giunti.apg.shared.model.Professioni;
import it.giunti.apg.shared.model.Province;
import it.giunti.apg.shared.model.RinnoviMassivi;
import it.giunti.apg.shared.model.Societa;
import it.giunti.apg.shared.model.TipiDisdetta;
import it.giunti.apg.shared.model.TitoliStudio;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath(AppConstants.SERV_LOOKUP)
public interface LookupService extends RemoteService {
	public String getApgTitle() throws EmptyResultException;
	public String getApgStatus() throws EmptyResultException;
	public String getApgMenuImage() throws EmptyResultException;
	public String getApgLoginImage() throws EmptyResultException;
	public String getApgVersion() throws EmptyResultException;
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
	public List<FileUploads> findFileUploadsStripped() throws BusinessException, EmptyResultException;
	public Boolean deleteFileUpload(Integer idFileUpload) throws BusinessException;

	//Adesioni
	public List<Adesioni> findAdesioni(String filterPrefix, int offset, int pageSize)
			throws BusinessException, EmptyResultException;
	public Integer saveOrUpdateAdesione(Adesioni adesioni) throws BusinessException;
	public Adesioni findAdesioneById(Integer idAdesione)
			throws BusinessException, EmptyResultException;
	public Adesioni createAdesione();
	
	//RinnoviMassivi
	public List<RinnoviMassivi> findRinnoviMassivi(Integer idPeriodico) throws BusinessException, EmptyResultException;
	public Boolean saveOrUpdateRinnoviMassiviList(List<RinnoviMassivi> rinnoviMassiviList) throws BusinessException;
	public Boolean deleteRinnovoMassivo(Integer idRinnovoMassivo) throws BusinessException;
}
