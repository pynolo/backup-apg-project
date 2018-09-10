package it.giunti.apg.client.services;

import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.model.Ruoli;
import it.giunti.apg.shared.model.Utenti;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath(AppConstants.SERV_AUTH)
public interface AuthService extends RemoteService {
	//Utenti
	public Utenti authenticate(String userName, String password) throws BusinessException, EmptyResultException;
	public List<Utenti> findUtenti(boolean showBlocked, int offset, int size) throws BusinessException, EmptyResultException;
	public Utenti findUtenteByUserName(String idUtente) throws BusinessException, EmptyResultException;
	public String saveOrUpdate(Utenti item, String password, boolean askReset) throws BusinessException;
	public Boolean sendHeartbeat(String idUtente) throws BusinessException;
	
	//Password
	public Boolean addPassword(String idUtente, String password, boolean askReset) throws BusinessException;
	
	//Ruoli
	public List<Ruoli> findRuoli() throws BusinessException, EmptyResultException;
	
}
