package it.giunti.apg.client.services;

import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.StatData;
import it.giunti.apg.shared.model.Periodici;
import it.giunti.apg.shared.model.StatAbbonati;
import it.giunti.apg.shared.model.StatInvio;
import it.giunti.apg.shared.model.TipiAbbonamento;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath(AppConstants.SERV_STAT)
public interface StatService extends RemoteService {
	public List<StatData<Periodici>> statTiraturaPeriodici() throws BusinessException, EmptyResultException;
	public List<StatData<TipiAbbonamento>> statTipiAbbPeriodico(Date date, Integer idPeriodico) throws BusinessException, EmptyResultException;

	public List<StatAbbonati> findStatAbbonatiBetweenDates(Integer idPeriodico, Date dataInizio, Date dataFine) throws BusinessException, EmptyResultException;
	public StatAbbonati findLastStatAbbonati(Integer idPeriodico) throws BusinessException, EmptyResultException;

	public List<StatInvio> findLastStatInvio(Integer idPeriodico) throws BusinessException, EmptyResultException;
	public List<List<StatInvio>> findStatInvio(Integer idPeriodico) throws BusinessException, EmptyResultException;

}
