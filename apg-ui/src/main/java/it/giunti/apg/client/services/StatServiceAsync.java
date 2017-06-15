package it.giunti.apg.client.services;

import it.giunti.apg.shared.StatData;
import it.giunti.apg.shared.model.Periodici;
import it.giunti.apg.shared.model.StatAbbonati;
import it.giunti.apg.shared.model.StatInvio;
import it.giunti.apg.shared.model.TipiAbbonamento;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface StatServiceAsync {
	void statTiraturaPeriodici(AsyncCallback<List<StatData<Periodici>>> callback);
	void statTipiAbbPeriodico(Date date, Integer idPeriodico, AsyncCallback<List<StatData<TipiAbbonamento>>> callback);
	
	void findStatAbbonatiBetweenDates(Integer idPeriodico, Date dataInizio, Date dataFine, AsyncCallback<List<StatAbbonati>> callback);
	void findLastStatAbbonati(Integer idPeriodico, AsyncCallback<StatAbbonati> callback);
	
	void findLastStatInvio(Integer idPeriodico, AsyncCallback<List<StatInvio>> callback);
	void findStatInvio(Integer idPeriodico, AsyncCallback<List<List<StatInvio>>> callback);
}
