package it.giunti.apg.updater;

import it.giunti.apg.server.ServerConstants;
import it.giunti.apg.server.persistence.EvasioniArticoliDao;
import it.giunti.apg.server.persistence.GenericDao;
import it.giunti.apg.server.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.EvasioniArticoli;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoveArticoliDuplicati {
	
	private static final Logger LOG = LoggerFactory.getLogger(RemoveArticoliDuplicati.class);
		
	private static EvasioniArticoliDao eaDao = new EvasioniArticoliDao();
	
	@SuppressWarnings("unchecked")
	public static void update()
			throws BusinessException, IOException {
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			LOG.info("Rimozione arretrati duplicati");
			String sql = "select distinct ia.id from evasioni_articoli ea1, istanze_abbonamenti ia where "+
					"ea1.id_istanza_abbonamento = ia.id and "+
					"(select count(id) from evasioni_articoli ea2 where "+
						"ea1.id_istanza_abbonamento = ea2.id_istanza_abbonamento and "+
						"ea1.id_articolo = ea2.id_articolo and "+
						"ea1.data_creazione = ea2.data_creazione "+
					") > 1 "+
					"order by ia.id, ea1.data_creazione, ea1.data_invio asc";
			Query q = ses.createSQLQuery(sql);
			List<Integer> idList = q.list();
			LOG.info("Totale abbonamenti da verificare: "+idList.size());
			int count = 0;
			int deleted = 0;
			for (Integer idIstanza:idList) {
				IstanzeAbbonamenti ia = GenericDao.findById(ses, IstanzeAbbonamenti.class, idIstanza);
				List<EvasioniArticoli> eaList = eaDao.findByIstanza(ses, idIstanza);
				if (eaList.size() > 1) {
					//Raggruppa per data+cm
					Map<String,List<EvasioniArticoli>> dateMap = new HashMap<String, List<EvasioniArticoli>>();
					for (EvasioniArticoli ea:eaList) {
						String key = ServerConstants.FORMAT_DAY.format(ea.getDataCreazione())+ea.getArticolo().getCodiceMeccanografico();
						List<EvasioniArticoli> eaByDate = dateMap.get(key);
						if (eaByDate == null) {
							eaByDate = new ArrayList<EvasioniArticoli>();
							dateMap.put(key, eaByDate);
						}
						eaByDate.add(ea);
					}
					LOG.info(ia.getAbbonamento().getCodiceAbbonamento()+" ha "+eaList.size()+
							" articoli creati in "+dateMap.size()+" data/e");
					//Scorre i gruppi
					for (List<EvasioniArticoli> dateList:dateMap.values()) {
						if (dateList.size() > 1) {//se ci sono due articoli prodotti nella stessa data
							boolean deduped = deduplica(ses, ia, dateList);
							if (deduped) deleted++;
						}
					}
				}
				count++;
				if (count%20 == 0) LOG.info("Verificati "+count+"/"+idList.size());
			}
			trn.commit();
			LOG.info("Istanze verificate: "+count+"/"+idList.size()+" Articoli rimossi: "+deleted);
		} catch (HibernateException e) {
			trn.rollback();
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
	}
	
	private static boolean deduplica(Session ses, IstanzeAbbonamenti ia, List<EvasioniArticoli> eaList) 
			throws HibernateException {
		//Lista che include solo i non spediti
		List<EvasioniArticoli> filteredList = new ArrayList<EvasioniArticoli>();
		for (EvasioniArticoli ea:eaList) {
			if (ea.getDataInvio() == null) filteredList.add(ea);
		}
		if (filteredList.size() > 0) {
			//Elimina i duplicati non spediti
			EvasioniArticoli eaExtra = null;
			for (EvasioniArticoli ea:filteredList) {
				if (eaExtra == null) eaExtra=ea;
			}
			LOG.info(ia.getAbbonamento().getCodiceAbbonamento()+" deduplica di "+eaExtra.getArticolo().getCodiceMeccanografico()+" del "+eaExtra.getDataCreazione());
			GenericDao.deleteGeneric(ses, eaExtra.getId(), eaExtra);
			return true;
		}
		return false;
	}
}
