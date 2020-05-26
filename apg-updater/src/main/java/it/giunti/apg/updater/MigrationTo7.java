package it.giunti.apg.updater;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import it.giunti.apg.core.persistence.MaterialiDao;
import it.giunti.apg.core.persistence.MaterialiProgrammazioneDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Articoli;
import it.giunti.apg.shared.model.EvasioniArticoli;
import it.giunti.apg.shared.model.EvasioniFascicoli;
import it.giunti.apg.shared.model.Fascicoli;
import it.giunti.apg.shared.model.Materiali;
import it.giunti.apg.shared.model.MaterialiProgrammazione;
import it.giunti.apg.shared.model.MaterialiSpedizione;

public class MigrationTo7 {

	public static void run() throws BusinessException {
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		MaterialiDao matDao = new MaterialiDao();
		MaterialiProgrammazioneDao matProgDao = new MaterialiProgrammazioneDao();
		try {
			// 1: fascicoli -> materiali (inoltre fascicoli -> materiali_programmazione)
			//    articoli -> materiali
			// 2: evasioni_comunicazioni.id_fascicolo -> .id_materiale_programmazione
			// 3: evasioni_fascicoli -> materiali_spedizione
			//    evasioni_articoli -> materiali_spedizione
			// 4: istanze_abbonamenti date based
			
			// FASE 1.1 - i fascicoli diventano materiali e materiali_programmazione
			Map<Integer,Materiali> fasMatMap = new HashMap<Integer, Materiali>();
			Map<Integer,MaterialiProgrammazione> fasMatProgMap = new HashMap<Integer, MaterialiProgrammazione>();
			String hql = "from Fascicoli f order by f.id";
			Query q = ses.createQuery(hql);
			List<Fascicoli> fasList = q.list();
			for (Fascicoli f:fasList) {
				Materiali mat = toMateriale(f);
				matDao.save(ses, mat);
				fasMatMap.put(f.getId(), mat);
				MaterialiProgrammazione matProg = toMaterialeProgrammazione(f, mat);
				matProgDao.save(ses, matProg);
				fasMatProgMap.put(f.getId(), matProg);
			}
			// FASE 1.2 - gli articoli diventano materiali
			Map<Integer,Materiali> artMatMap = new HashMap<Integer, Materiali>();
			hql = "from Articoli a order by a.id";
			q = ses.createQuery(hql);
			List<Articoli> artList = q.list();
			for (Articoli a:artList) {
				Materiali mat = toMateriale(a);
				matDao.save(ses, mat);
				artMatMap.put(a.getId(), mat);
			}
						

			// FASE 2.1 - istanze_abbonamenti
			hql = "update IstanzeAbbonamenti ia set "+
					"ia.dataInizio = ia.fascicoloInizio.dataInizio , "+
					"ia.dataFine = ia.fascicoloFine.dataFine";
			q = ses.createQuery(hql);
			q.executeUpdate();
			// FASE 2.2 - evasioni_comunicazioni punta a materiali_programmazione
			for (Integer idFas:fasMatProgMap.keySet()) {
				MaterialiProgrammazione matProg = fasMatProgMap.get(idFas);
				// EvasioniComunicazioni
				hql = "update EvasioniComunicazioni ec "+
						"set ec.idMaterialiProgrammazione = :id1 where "+
						"ec.fascicolo.id = :id2 ";
				q = ses.createQuery(hql);
				q.setParameter("id1", matProg.getId());
				q.setParameter("id2", idFas);
				q.executeUpdate();
			}
			
			
			// FASE 3.1 - spedizioni fascicoli
			for (Integer idFas:fasMatMap.keySet()) {
				Materiali mat = fasMatMap.get(idFas);
				hql = "update MaterialiSpedizione ms "+
						"set ms.idMateriale = :id1 where "+
						"ms.idFascicolo = :id2 ";
				q = ses.createQuery(hql);
				q.setParameter("id1", mat.getId());
				q.setParameter("id2", idFas);
				q.executeUpdate();
			}
			// FASE 3.2 - spedizioni articoli
			for (Integer idArt:artMatMap.keySet()) {
				Materiali mat = artMatMap.get(idArt);
				// Fascicoli
				hql = "update MaterialiSpedizione ms "+
						"set ms.idMateriale = :id1 where "+
						"ms.idArticolo = :id2 ";
				q = ses.createQuery(hql);
				q.setParameter("id1", mat.getId());
				q.setParameter("id2", idArt);
				q.executeUpdate();
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

	private static Materiali toMateriale(Fascicoli item) {
		Materiali mat = new Materiali();
		mat.setCodiceMeccanografico(item.getCodiceMeccanografico());
		mat.setIdTipoAnagraficaSap(item.getIdTipoAnagraficaSap());
		mat.setInAttesa(item.getInAttesa());
		mat.setNote(item.getNote());
		mat.setSottotitolo(item.getDataCop());
		mat.setTitolo(item.getTitoloNumero());
		return mat;
	}
	
	private static Materiali toMateriale(Articoli item) {
		Materiali mat = new Materiali();
		mat.setCodiceMeccanografico(item.getCodiceMeccanografico());
		mat.setIdTipoAnagraficaSap(item.getIdTipoAnagraficaSap());
		mat.setInAttesa(item.getInAttesa());
		mat.setNote(null);
		mat.setSottotitolo(item.getCodiceInterno());
		mat.setTitolo(item.getTitoloNumero());
		return mat;
	}
	
	private static MaterialiProgrammazione toMaterialeProgrammazione(Fascicoli item, Materiali mat) {
		MaterialiProgrammazione matProg = new MaterialiProgrammazione();
		matProg.setComunicazioniInviate(item.getComunicazioniInviate());
		matProg.setDataEstrazione(item.getDataEstrazione());
		matProg.setDataNominale(item.getDataInizio());
		matProg.setMateriale(mat);
		matProg.setOpzione(item.getOpzione());
		matProg.setPeriodico(item.getPeriodico());
		return matProg;
	}
	
	private static MaterialiSpedizione toMaterialeSpedizione(EvasioniFascicoli item, Materiali mat) {
		MaterialiSpedizione matSped = new MaterialiSpedizione();
		matSped.setCopie(item.getCopie());
		matSped.setDataAnnullamento(null);
		matSped.setDataConfermaEvasione(item.getDataConfermaEvasione());
		matSped.setDataCreazione(item.getDataCreazione());
		matSped.setDataInvio(item.getDataInvio());
		matSped.setDataLimite(null);
		matSped.setDataOrdine(item.getDataOrdine());
		matSped.setIdAbbonamento(item.getIdAbbonamento());
		matSped.setIdAnagrafica(item.getIdAnagrafica());
		matSped.setIdArticoloListino(null);
		matSped.setIdArticoloOpzione(null);
		matSped.setMateriale(mat);
		matSped.setNote(item.getNote());
		matSped.setOrdiniLogistica(item.getOrdiniLogistica());
		matSped.setPrenotazioneIstanzaFutura(null);
		return matSped;
	}
	
	private static MaterialiSpedizione toMaterialeSpedizione(EvasioniArticoli item, Materiali mat) {
		MaterialiSpedizione matSped = new MaterialiSpedizione();
		matSped.setCopie(item.getCopie());
		matSped.setDataAnnullamento(item.getDataAnnullamento());
		matSped.setDataConfermaEvasione(item.getDataConfermaEvasione());
		matSped.setDataCreazione(item.getDataCreazione());
		matSped.setDataInvio(item.getDataInvio());
		matSped.setDataLimite(item.getDataLimite());
		matSped.setDataOrdine(item.getDataOrdine());
		matSped.setIdAbbonamento(item.getIdAbbonamento());
		matSped.setIdAnagrafica(item.getIdAnagrafica());
		matSped.setIdArticoloListino(item.getIdArticoloListino());
		matSped.setIdArticoloOpzione(item.getIdArticoloOpzione());
		matSped.setMateriale(mat);
		matSped.setNote(item.getNote());
		matSped.setOrdiniLogistica(item.getOrdiniLogistica());
		matSped.setPrenotazioneIstanzaFutura(item.getPrenotazioneIstanzaFutura());
		return matSped;
	}
	
}
	
