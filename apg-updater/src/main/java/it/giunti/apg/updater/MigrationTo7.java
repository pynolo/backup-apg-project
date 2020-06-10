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
import it.giunti.apg.shared.model.Fascicoli6;
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
				count++;
				LOG.info("Materiali da Fascicoli: "+count+"/"+fasList.size());
			}
			// FASE 1.2 - gli articoli diventano materiali
			count = 0;
			Map<Integer,Materiali> artMatMap = new HashMap<Integer, Materiali>();
			hql = "from Articoli6 a order by a.id";
			q = ses.createQuery(hql);
			List<Articoli6> artList = q.list();
			for (Articoli6 a:artList) {
				Materiali mat = matDao.findByCodiceMeccanografico(ses, a.getCodiceMeccanografico());
				if (mat == null) {
					mat = toMateriale(a);
					matDao.save(ses, mat);
					count++;
					LOG.info("Materiale "+a.getCodiceMeccanografico()+" da Articoli: "+count+"/"+artList.size());
				} else {
					LOG.info("Materiale "+a.getCodiceMeccanografico()+" già fascicolo");
				}
				artMatMap.put(a.getId(), mat);
				
			}
			

			// FASE 2.1 - istanze_abbonamenti
			hql = "update IstanzeAbbonamenti ia set "+
					"ia.dataInizio = ia.fascicoloInizio6.dataInizio , "+
					"ia.dataFine = ia.fascicoloFine6.dataFine";
			q = ses.createQuery(hql);
			count = q.executeUpdate();
			LOG.info("Istanze migrate a intervallo di date: "+count);
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
				q.executeUpdate();
				count++;
				LOG.info("EvasioniComunicazioni modificate: "+count+"/"+fasMatProgMap.size());
			}
			
			
			// FASE 3.1 - spedizioni fascicoli
			count = 0;
			for (Integer idFas:fasMatMap.keySet()) {
				Materiali mat = fasMatMap.get(idFas);
				hql = "update MaterialiSpedizione ms "+
						"set ms.materiale = :obj1 where "+
						"ms.idFascicolo = :id1 ";
				q = ses.createQuery(hql);
				q.setParameter("obj1", mat);
				q.setParameter("id1", idFas);
				q.executeUpdate();
				count++;
				LOG.info("MaterialiSpedizione (fascicolo) modificati: "+count+"/"+fasMatMap.size());
			}
			// FASE 3.2 - spedizioni articoli
			count = 0;
			for (Integer idArt:artMatMap.keySet()) {
				Materiali mat = artMatMap.get(idArt);
				// Fascicoli
				hql = "update MaterialiSpedizione ms "+
						"set ms.materiale = :obj1 where "+
						"ms.idArticolo = :id1 ";
				q = ses.createQuery(hql);
				q.setParameter("obj1", mat);
				q.setParameter("id1", idArt);
				q.executeUpdate();
				count++;
				LOG.info("MaterialiSpedizione (articolo) modificati: "+count+"/"+artMatMap.size());
			}
			
			
			// FASE 4 - articoli_listini e articoli_opzioni
			for (Integer idArt:artMatMap.keySet()) {
				Materiali mat = artMatMap.get(idArt);
				// ArticoliListini
				hql = "update ArticoliListini al "+
						"set al.materiale = :obj1 where "+
						"al.articolo6.id = :id1 ";
				q = ses.createQuery(hql);
				q.setParameter("obj1", mat);
				q.setParameter("id1", idArt);
				q.executeUpdate();
				// ArticoliOpzioni
				hql = "update ArticoliOpzioni ao "+
						"set ao.materiale = :obj1 where "+
						"ao.articolo6.id = :id1 ";
				q = ses.createQuery(hql);
				q.setParameter("obj1", mat);
				q.setParameter("id1", idArt);
				q.executeUpdate();
				count++;
				LOG.info("ArticoliListini+Opzioni modificati: "+count+"/"+artMatMap.size());
			}
			
			// FASE 5 - migrare i rinnovi massivi
			count = 0;
			for (Integer idFas:fasMatMap.keySet()) {
				hql = "update RinnoviMassivi rm "+
						"set rm.dataInizio = rm.fascicoloInizio6.dataInizio where "+
						"rm.fascicoloInizio6.id = :id1 ";
				q = ses.createQuery(hql);
				q.setParameter("id1", idFas);
				q.executeUpdate();
				count++;
				LOG.info("RinnoviMassivi modificati: "+count+"/"+fasMatMap.size());
			}
			
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
		mat.setTitolo(item.getTitoloNumero()+" "+item.getPeriodico().getNome());
		String idTipoMateriale = AppConstants.MATERIALE_FASCICOLO;
		if (item.getFascicoliAccorpati() == 0) idTipoMateriale = AppConstants.MATERIALE_ALLEGATO;
		mat.setIdTipoMateriale(idTipoMateriale);
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
	//	matSped.setIdArticoloListino(null);
	//	matSped.setIdArticoloOpzione(null);
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
	//	matSped.setIdArticoloListino(item.getIdArticoloListino());
	//	matSped.setIdArticoloOpzione(item.getIdArticoloOpzione());
	//	matSped.setMateriale(mat);
	//	matSped.setNote(item.getNote());
	//	matSped.setOrdiniLogistica(item.getOrdiniLogistica());
	//	matSped.setPrenotazioneIstanzaFutura(item.getPrenotazioneIstanzaFutura());
	//	return matSped;
	//}
	
}
	
