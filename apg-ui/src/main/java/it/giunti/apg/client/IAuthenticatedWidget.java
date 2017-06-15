package it.giunti.apg.client;

import it.giunti.apg.shared.model.Utenti;

public interface IAuthenticatedWidget {

	public void onSuccessfulAuthentication(Utenti utente);
	
//	public void onFailedAuthentication();
}
