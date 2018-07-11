package it.giunti.apg.client.services;

import it.giunti.apg.shared.model.Ruoli;
import it.giunti.apg.shared.model.Utenti;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AuthServiceAsync {
	//Utenti
	void authenticate(String userName, String password, AsyncCallback<Utenti> callback);
	void findUtenti(boolean showBlocked, int offset, int size, AsyncCallback<List<Utenti>> callback);
	void findUtenteByUserName(String idUtente, AsyncCallback<Utenti> callback);
	void saveOrUpdate(Utenti item, String password, AsyncCallback<String> callback);
	void sendHeartbeat(String idUtente, AsyncCallback<Boolean> callback);
	
	//Password
	void addPassword(String idUtente, String password, AsyncCallback<Boolean> callback);
	
	//Ruoli
	void findRuoli(AsyncCallback<List<Ruoli>> callback);
}
