package it.giunti.apg.client.services;

import it.giunti.apg.shared.model.Ruoli;
import it.giunti.apg.shared.model.Utenti;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AuthServiceAsync {
	void authenticate(String userName, String password, AsyncCallback<Utenti> callback);
	void findUtenti(boolean showBlocked, int offset, int size, AsyncCallback<List<Utenti>> callback);
	void findUtenteByUserName(String idUtente, AsyncCallback<Utenti> callback);
	void saveOrUpdate(Utenti utente, AsyncCallback<String> callback);
	//void delete(String id, AsyncCallback<List<Utenti>> callback);
	
	void findRuoli(AsyncCallback<List<Ruoli>> callback);
	//void findUtenteByUserName(String userName, AsyncCallback<Utenti> callback);
}
