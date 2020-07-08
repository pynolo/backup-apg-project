package it.giunti.apg.updater;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.giunti.apg.core.persistence.MaterialiDao;
import it.giunti.apg.core.persistence.MaterialiProgrammazioneDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Articoli6;
import it.giunti.apg.shared.model.Comunicazioni;
import it.giunti.apg.shared.model.Fascicoli6;
import it.giunti.apg.shared.model.Listini;
import it.giunti.apg.shared.model.Materiali;
import it.giunti.apg.shared.model.MaterialiProgrammazione;

public class MigrationTo7 {

	private static final Logger LOG = LoggerFactory.getLogger(MigrationTo7.class);
	
	@SuppressWarnings("unchecked")
	public static void run() throws BusinessException {
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		MaterialiDao matDao = new MaterialiDao();
		MaterialiProgrammazioneDao matProgDao = new MaterialiProgrammazioneDao();
		try {
			// 1: fascicoli -> materiali (inoltre fascicoli -> materiali_programmazione)
			//    articoli -> materiali
			// 2: evasioni_comunicazioni.id_fascicolo -> .id_materiale_programmazione
			// 3: istanze_abbonamenti date based
			//    evasioni_fascicoli -> materiali_spedizione
			//    evasioni_articoli -> materiali_spedizione
			// 4: articoli_listini -> .id_materiale
			//    articoli_opzioni -> .id_materiale
			//    stat_invio -> .id_materiale_spedizione
			// 5: rinnovi_massivi -> data_inizio
			// 6: gracing numeri -> mesi
			//    comunicazioni numeri -> mesi
			
			// FASE 1.1 - i fascicoli diventano materiali e materiali_programmazione
			int count = 0;
			Map<Integer,Materiali> fasMatMap = new HashMap<Integer, Materiali>();
			Map<Integer,MaterialiProgrammazione> fasMatProgMap = new HashMap<Integer, MaterialiProgrammazione>();
			String hql = "from Fascicoli6 f order by f.id";
			Query q = ses.createQuery(hql);
			List<Fascicoli6> fasList = q.list();
			for (Fascicoli6 f:fasList) {
				Materiali mat = toMateriale(f);
				matDao.save(ses, mat);
				fasMatMap.put(f.getId(), mat);
				MaterialiProgrammazione matProg = toMaterialeProgrammazione(f, mat);
				matProgDao.save(ses, matProg);
				fasMatProgMap.put(f.getId(), matProg);
			}
			LOG.info("1.1 - Materiali da Fascicoli: "+fasList.size());
			// FASE 1.2 - gli articoli diventano materiali
			count = 0;
			Map<Integer,Materiali> artMatMap = new HashMap<Integer, Materiali>();
			hql = "from Articoli6 a order by a.id";
			q = ses.createQuery(hql);
			List<Articoli6> artList = q.list();
			for (Articoli6 a:artList) {
				//LASCIO I DUPLICATI FASCICOLI+ARTICOLI 
				Materiali mat = toMateriale(a);
				matDao.save(ses, mat);
				artMatMap.put(a.getId(), mat);
			}
			LOG.info("1.2 - Materiali da Articoli: "+artList.size());
			
			
			// FASE 2.1 - istanze_abbonamenti
			String sql = "UPDATE istanze_abbonamenti ia "+
					"INNER JOIN fascicoli fi ON ia.id_fascicolo_inizio=fi.id "+
					"INNER JOIN fascicoli ff ON ia.id_fascicolo_fine=ff.id "+
					"SET ia.data_inizio = fi.data_inizio, ia.data_fine = ff.data_fine";
			q = ses.createSQLQuery(sql);
			count = q.executeUpdate();
			LOG.info("2.1 - Istanze migrate a intervallo di date: "+count);
			// FASE 2.2 - evasioni_comunicazioni punta a materiali_programmazione
			count = 0;
			for (Integer idFas:fasMatProgMap.keySet()) {
				MaterialiProgrammazione matProg = fasMatProgMap.get(idFas);
				// EvasioniComunicazioni
				hql = "update EvasioniComunicazioni ec "+
						"set ec.materialeProgrammazione = :obj1 where "+
						"ec.fascicolo6.id = :id1 ";
				q = ses.createQuery(hql);
				q.setParameter("obj1", matProg);
				q.setParameter("id1", idFas);
				count += q.executeUpdate();
			}
			LOG.info("2.2 - EvasioniComunicazioni modificate: "+count);

			
			// FASE 3.1 - materiali_spedizione fascicoli
			sql = "UPDATE materiali_spedizione "+
					"SET id_materiale = "+
						"(SELECT id FROM materiali WHERE materiali_spedizione.id_fascicolo = materiali.id_fascicolo LIMIT 1) "+
					"WHERE materiali_spedizione.id_fascicolo is not null ";
			q = ses.createSQLQuery(sql);
			count = q.executeUpdate();
			LOG.info("3.1 - MaterialiSpedizione (fascicolo) modificati: "+count);
			// FASE 3.2 - materiali_spedizione articoli
			String sql1 = "UPDATE materiali_spedizione "+
					"SET id_materiale = "+
						"(SELECT id FROM materiali WHERE materiali_spedizione.id_articolo = materiali.id_articolo LIMIT 1) "+
					"WHERE materiali_spedizione.id_articolo is not null";
			q = ses.createSQLQuery(sql1);
			count = q.executeUpdate();
			LOG.info("3.2 - MaterialiSpedizione (articolo) modificati: "+count);
			
			
			// FASE 4.1 - materiali_listini
			sql = "UPDATE materiali_listini "+
					"SET id_materiale = "+
						"(SELECT id FROM materiali WHERE articoli_listini.id_articolo = materiali.id_articolo LIMIT 1) "+
					"WHERE materiali_listini.id_articolo is not null ";
			q = ses.createSQLQuery(sql);
			count = q.executeUpdate();
			LOG.info("4.1 - MaterialiListini modificati: "+count);
			// FASE 4.2 - materiali_opzioni
			sql = "UPDATE materiali_opzioni "+
				"SET id_materiale = "+
					"(SELECT id FROM materiali WHERE articoli_opzioni.id_articolo = materiali.id_articolo LIMIT 1) "+
				"WHERE materiali_opzioni.id_articolo is not null ";
			q = ses.createSQLQuery(sql);
			count = q.executeUpdate();
			LOG.info("4.2 - MaterialiOpzioni modificati: "+count);

			
			// FASE 5 - migrare i rinnovi massivi
			sql = "UPDATE rinnovi_massivi "+
				"SET rinnovi_massivi.data_inizio = "+
					"(SELECT f.data_inizio FROM fascicoli f WHERE rinnovi_massivi.id_fascicolo_inizio = f.id LIMIT 1) "+
				"WHERE rinnovi_massivi.id_fascicolo_inizio is not null ";
			q = ses.createSQLQuery(sql);
			count = q.executeUpdate();
			LOG.info("5 - RinnoviMassivi modificati: "+count);
			
			// FASE 6.1 - migrazione listini: gracing da numeri a mesi
			Map<String, Integer> monthMap = new HashMap<String, Integer>();
			monthMap.put("W", 2);
			monthMap.put("N", 2);
			monthMap.put("D", 2);
			hql = "from Listini l";
			q = ses.createQuery(hql);
			List<Listini> lList = q.list();
			for (Listini l:lList) {
				Integer months = monthMap.get(l.getTipoAbbonamento().getPeriodico().getUid());
				if (months == null) months = 1;
				l.setGracingInizialeMesi(l.getGracingIniziale6()*months);
				l.setGracingFinaleMesi(l.getGracingFinale6()*months);
				ses.update(l);
			}
			LOG.info("6.1 - Listini modificati: "+lList.size());
			// FASE 6.2 - migrazione comunicazioni: da numeri a mesi
			hql = "from Comunicazioni c";
			q = ses.createQuery(hql);
			List<Comunicazioni> cList = q.list();
			for (Comunicazioni c:cList) {
				Integer months = monthMap.get(c.getPeriodico().getUid());
				if (months == null) months = 1;
				c.setMesiDaInizioOFine(c.getNumeriDaInizioOFine6()*months);
				ses.update(c);
			}
			LOG.info("6.2 - Comunicazioni modificate: "+cList);
			
			ses.flush();
			ses.clear();
			trn.commit();
		} catch (HibernateException e) {
			trn.rollback();
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
	}

	private static Materiali toMateriale(Fascicoli6 item) {
		Materiali mat = new Materiali();
		mat.setCodiceMeccanografico(item.getCodiceMeccanografico());
		mat.setIdTipoAnagraficaSap(item.getIdTipoAnagraficaSap());
		mat.setInAttesa(item.getInAttesa());
		mat.setNote(item.getNote());
		mat.setSottotitolo(item.getDataCop());
		mat.setTitolo(item.getTitoloNumero());
		if (item.getOpzione() != null) {
			mat.setTitolo(mat.getTitolo()+" "+item.getOpzione().getNome());
		}
		if (item.getPeriodico() != null) {
			mat.setTitolo(mat.getTitolo()+" "+item.getPeriodico().getNome());
		}
		String idTipoMateriale = AppConstants.MATERIALE_FASCICOLO;
		if (item.getFascicoliAccorpati() == 0) idTipoMateriale = AppConstants.MATERIALE_ALLEGATO;
		mat.setIdTipoMateriale(idTipoMateriale);
		mat.setIdFascicolo(item.getId());
		return mat;
	}
	
	private static Materiali toMateriale(Articoli6 item) {
		Materiali mat = new Materiali();
		mat.setCodiceMeccanografico(item.getCodiceMeccanografico());
		mat.setIdTipoAnagraficaSap(item.getIdTipoAnagraficaSap());
		mat.setInAttesa(item.getInAttesa());
		mat.setNote(null);
		mat.setSottotitolo(item.getCodiceInterno());
		if (item.getPeriodico() != null) {
			mat.setTitolo(item.getTitoloNumero()+" "+item.getPeriodico().getNome());
		} else {
			mat.setTitolo(item.getTitoloNumero());
		}
		mat.setIdTipoMateriale(AppConstants.MATERIALE_ARTICOLO_LIBRO);
		mat.setIdArticolo(item.getId());
		return mat;
	}
	
	private static MaterialiProgrammazione toMaterialeProgrammazione(Fascicoli6 item, Materiali mat) {
		MaterialiProgrammazione matProg = new MaterialiProgrammazione();
		matProg.setComunicazioniInviate(item.getComunicazioniInviate());
		matProg.setDataEstrazione(item.getDataEstrazione());
		matProg.setDataNominale(item.getDataInizio());
		matProg.setMateriale(mat);
		matProg.setOpzione(item.getOpzione());
		matProg.setPeriodico(item.getPeriodico());
		return matProg;
	}
	
	//private static MaterialiSpedizione toMaterialeSpedizione(EvasioniFascicoli6 item, Materiali mat) {
	//	MaterialiSpedizione matSped = new MaterialiSpedizione();
	//	matSped.setCopie(item.getCopie());
	//	matSped.setDataAnnullamento(null);
	//	matSped.setDataConfermaEvasione(item.getDataConfermaEvasione());
	//	matSped.setDataCreazione(item.getDataCreazione());
	//	matSped.setDataInvio(item.getDataInvio());
	//	matSped.setDataLimite(null);
	//	matSped.setDataOrdine(item.getDataOrdine());
	//	matSped.setIdAbbonamento(item.getIdAbbonamento());
	//	matSped.setIdAnagrafica(item.getIdAnagrafica());
	//	matSped.setIdMaterialeListino(null);
	//	matSped.setIdMaterialeOpzione(null);
	//	matSped.setMateriale(mat);
	//	matSped.setNote(item.getNote());
	//	matSped.setOrdiniLogistica(item.getOrdiniLogistica());
	//	matSped.setPrenotazioneIstanzaFutura(null);
	//	return matSped;
	//}
	//
	//private static MaterialiSpedizione toMaterialeSpedizione(EvasioniArticoli6 item, Materiali mat) {
	//	MaterialiSpedizione matSped = new MaterialiSpedizione();
	//	matSped.setCopie(item.getCopie());
	//	matSped.setDataAnnullamento(item.getDataAnnullamento());
	//	matSped.setDataConfermaEvasione(item.getDataConfermaEvasione());
	//	matSped.setDataCreazione(item.getDataCreazione());
	//	matSped.setDataInvio(item.getDataInvio());
	//	matSped.setDataLimite(item.getDataLimite());
	//	matSped.setDataOrdine(item.getDataOrdine());
	//	matSped.setIdAbbonamento(item.getIdAbbonamento());
	//	matSped.setIdAnagrafica(item.getIdAnagrafica());
	//	matSped.setIdMaterialeListino(item.getIdMaterialeListino());
	//	matSped.setIdMaterialeOpzione(item.getIdMaterialeOpzione());
	//	matSped.setMateriale(mat);
	//	matSped.setNote(item.getNote());
	//	matSped.setOrdiniLogistica(item.getOrdiniLogistica());
	//	matSped.setPrenotazioneIstanzaFutura(item.getPrenotazioneIstanzaFutura());
	//	return matSped;
	//}
	
}
	
