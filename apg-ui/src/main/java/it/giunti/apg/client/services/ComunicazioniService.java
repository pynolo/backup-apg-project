package it.giunti.apg.client.services;

import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.model.Comunicazioni;
import it.giunti.apg.shared.model.EvasioniComunicazioni;
import it.giunti.apg.shared.model.ModelliBollettini;
import it.giunti.apg.shared.model.ModelliEmail;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath(AppConstants.SERV_COMUNICAZIONI)
public interface ComunicazioniService extends RemoteService {
	//Comunicazioni
	public List<Comunicazioni> findComunicazioniByPeriodico(Integer idPeriodico, Date dt, int offset, int pageSize) throws BusinessException, EmptyResultException;
	public List<Comunicazioni> findComunicazioniByTipoAbb(Integer idTipoAbb, Date dt, int offset, int pageSize) throws BusinessException, EmptyResultException;
	public Comunicazioni findComunicazioneById(Integer idCom) throws BusinessException, EmptyResultException;
	public Integer saveOrUpdateComunicazione(Comunicazioni comunicazione) throws BusinessException;
	public Comunicazioni createComunicazione(Integer idPeriodico) throws BusinessException;
	public List<Comunicazioni> deleteComunicazione(Integer idCom, int pageSize) throws BusinessException, EmptyResultException;
	public String getTipiAbbStringFromComunicazione(Integer idCom) throws BusinessException;
	public Map<Comunicazioni, Integer> findComunicazioniByEnqueuedMedia(String idTipoMedia) throws BusinessException, EmptyResultException;

	//EvasioniComunicazioni
	public List<EvasioniComunicazioni> findEvasioniComunicazioniByIstanza(Integer idIstanza) throws BusinessException, EmptyResultException;
	public Boolean enqueueEvasioneComunicazione(Integer idIstanza, String idUtente,
			String idTipoMedia, String idTipoDestinatario,
			Boolean richiestaRinnovo, String messaggio) throws BusinessException;
	public Integer saveOrUpdateEvasioneComunicazione(EvasioniComunicazioni evasioneComunicazione) throws BusinessException;
	public EvasioniComunicazioni createEvasioneComunicazione(Integer idIstanza, String idTipoMedia) throws BusinessException;
	public EvasioniComunicazioni findEvasioneComunicazioneById(Integer idEvasioneCom) throws BusinessException, EmptyResultException;
	public List<EvasioniComunicazioni> deleteEvasioneComunicazione(Integer idIstanza, Integer idEvasioneComunicazione) throws BusinessException, EmptyResultException;	
	
	//Modelli Bollettini
	public List<ModelliBollettini> findModelliBollettini(int offset, int pageSize) throws BusinessException, EmptyResultException;
	public List<ModelliBollettini> findModelliBollettiniByPeriodico(Integer idPeriodico, int offset, int pageSize) throws BusinessException, EmptyResultException;
	public ModelliBollettini findModelliBollettiniById(Integer idMb) throws BusinessException, EmptyResultException;
	public Integer saveOrUpdateModelliBollettini(ModelliBollettini bolMod) throws BusinessException;
	public ModelliBollettini createModelliBollettini(Integer idPeriodico) throws BusinessException;
	public List<ModelliBollettini> deleteModelliBollettini(Integer idBolMod, int pageSize) throws BusinessException, EmptyResultException;
	public String formatBollettinoText(String text, int lineWidth);
	
	//Modelli Email
	public List<ModelliEmail> findModelliEmail(int offset, int pageSize) throws BusinessException, EmptyResultException;
	public ModelliEmail findModelliEmailById(Integer idMe) throws BusinessException, EmptyResultException;
	public Integer saveOrUpdateModelliEmail(ModelliEmail modEmail) throws BusinessException;
	public ModelliEmail createModelliEmail() throws BusinessException;
	public List<ModelliEmail> deleteModelliEmail(Integer idModEmail, int pageSize) throws BusinessException, EmptyResultException;

}
