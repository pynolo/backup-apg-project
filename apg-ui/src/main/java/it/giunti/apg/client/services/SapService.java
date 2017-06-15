package it.giunti.apg.client.services;

import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.model.EvasioniArticoli;
import it.giunti.apg.shared.model.EvasioniFascicoli;
import it.giunti.apg.shared.model.OrdiniLogistica;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath(AppConstants.SERV_SAP)
public interface SapService extends RemoteService {
	
	//OrdiniLogistica
	public OrdiniLogistica findOrdineById(Integer idOrdine)
			throws BusinessException, EmptyResultException;
	public List<OrdiniLogistica> findOrdini(boolean showAnnullati, int offset, int pageSize)
			throws BusinessException, EmptyResultException;
	
	//EvasioniFascicoli
	public List<EvasioniFascicoli> findEvasioniFascicoliByOrdine(String numOrdine)
			throws BusinessException, EmptyResultException;
	
	//EvasioniArticoli
		public List<EvasioniArticoli> findEvasioniArticoliByOrdine(String numOrdine)
				throws BusinessException, EmptyResultException;
}
