package it.giunti.apg.server.business;

import it.giunti.apg.server.VisualLogger;
import it.giunti.apg.server.persistence.GenericDao;
import it.giunti.apg.server.persistence.SessionFactory;
import it.giunti.apg.server.persistence.StatInvioDao;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Fascicoli;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.StatInvio;
import it.giunti.apg.shared.model.TipiAbbonamento;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatInvioBusiness {

	private static final Logger LOG = LoggerFactory.getLogger(StatInvioBusiness.class);
	
	public static void saveOrUpdateStatInvioCartaceo(List<IstanzeAbbonamenti> iaList,
			Integer idFas, Date date, Integer idRapporto) 
			throws BusinessException {
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		StatInvioDao siDao = new StatInvioDao();
		try {
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Creazione statistiche di invio in corso");
			Fascicoli fas = GenericDao.findById(ses, Fascicoli.class, idFas);
			Map<TipiAbbonamento, StatInvio> statMap = new HashMap<TipiAbbonamento, StatInvio>();
			List<StatInvio> persistedList = siDao.findStatInvioByFascicolo(ses, idFas);
			//Riempie la mappa con tutti i tipi abbonamento già sul DB
			for (StatInvio si:persistedList) {
				TipiAbbonamento tipo = si.getTipoAbbonamento();
				StatInvio mapSi = statMap.get(tipo);
				if (mapSi == null) {
					statMap.put(tipo, si);
				} else {
					mapSi.setQuantita(mapSi.getQuantita()+si.getQuantita());
				}
			}
			//La mappa contiene i valori su db
			//Quindi si effettua un ciclo sugli abbonamenti dell'invio
			for (IstanzeAbbonamenti ia:iaList) {
				if (ia.getListino().getCartaceo()) {
					//Conta solo se è un abbonamento cartaceo
					//questa condizione esclude dalla somma i
					//digitali con opzioni cartacei
					TipiAbbonamento tipo = ia.getListino().getTipoAbbonamento();
					StatInvio mapSi = statMap.get(tipo);
					if (mapSi == null) {
						mapSi = new StatInvio();
						mapSi.setFascicolo(fas);
						mapSi.setDataCreazione(date);
						mapSi.setQuantita(ia.getCopie());
						mapSi.setTipoAbbonamento(tipo);
						statMap.put(tipo, mapSi);
					} else {
						mapSi.setQuantita(mapSi.getQuantita()+ia.getCopie());
					}
				}
			}
			//Save/update il contenuto della mappa
			for (TipiAbbonamento ta:statMap.keySet()) {
				StatInvio mapSi = statMap.get(ta);
				if (mapSi.getId() == null) {
					siDao.save(ses, mapSi);
				} else {
					siDao.update(ses, mapSi);
				}
			}
			trn.commit();
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Statistiche create correttamente");
		} catch (HibernateException e) {
			trn.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
	}
}
