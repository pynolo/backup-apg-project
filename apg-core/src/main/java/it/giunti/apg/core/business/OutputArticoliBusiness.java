package it.giunti.apg.core.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.VisualLogger;
import it.giunti.apg.core.persistence.ArticoliListiniDao;
import it.giunti.apg.core.persistence.ArticoliOpzioniDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.core.persistence.MaterialiSpedizioneDao;
import it.giunti.apg.core.persistence.PagamentiDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.Abbonamenti;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.ArticoliListini;
import it.giunti.apg.shared.model.ArticoliOpzioni;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.MaterialiSpedizione;
import it.giunti.apg.shared.model.Pagamenti;

public class OutputArticoliBusiness {

	
	// ArticoliListini
	
	
	public static List<MaterialiSpedizione> findPendingMaterialiSpedizioneArticoliListini(
			Integer idArticoloListino, Date date, int offset, int pageSize, int idRapporto)
			throws BusinessException {
		Session ses = SessionFactory.getSession();
		List<MaterialiSpedizione> msList = null;
		try {
			msList = new MaterialiSpedizioneDao().findPendingByArticoloListino(ses,
					idArticoloListino, date, offset, pageSize);
		} catch (HibernateException e) {
			VisualLogger.get().addHtmlErrorLine(idRapporto, e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return msList;
	}
		
	public static List<MaterialiSpedizione> filterArticoliListiniByScadenza(Session ses, List<MaterialiSpedizione> msList) 
			throws HibernateException {
		List<MaterialiSpedizione> result = new ArrayList<MaterialiSpedizione>();
		for (MaterialiSpedizione ms:msList) {
			if ((ms.getDataLimite() != null) && (ms.getIdAbbonamento() != null)) {
				//Ha scadenza => confronta con la data del saldo
				Abbonamenti abb = GenericDao.findById(ses, Abbonamenti.class, ms.getIdAbbonamento());
				IstanzeAbbonamenti ia = new IstanzeAbbonamentiDao()
						.findIstanzaByCodiceData(ses, abb.getCodiceAbbonamento(), ms.getDataCreazione());
				if (ia.getDataSaldo().before(ms.getDataLimite()) ||
						(ia.getFatturaDifferita())) {
					//Se saldato prima del limite allora OK
					result.add(ms);
				} else {
					//Altrimenti confronta la data dell'ultimo pagamento
					if (ia.getPagato()) {
						List<Pagamenti> pList = new PagamentiDao()
								.findPagamentiByIstanzaAbbonamento(ses, ms.getIdAbbonamento());
						Date latest = ServerConstants.DATE_FAR_PAST;
						for (Pagamenti p:pList) {
							if (p.getDataPagamento().after(latest)) {
								latest = p.getDataPagamento();
							}
						}
						if (latest.before(ms.getDataLimite())) {
							result.add(ms);
						}
					}
				}
			} else {
				//Non ha scadenza => passa il filtro
				result.add(ms);
			}
		}
		return result;
	}
	
	public static List<MaterialiSpedizione> filterArticoliListiniByScadenza(List<MaterialiSpedizione> msList) 
			throws BusinessException {
		List<MaterialiSpedizione> result = null;
		Session ses = SessionFactory.getSession();
		try {
			result = filterArticoliListiniByScadenza(ses, msList);
		} catch (HibernateException e) {
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	//public static List<EvasioniArticoli> createEvasioniFromArticoloListino(ArticoliListini al,
	//		List<IstanzeAbbonamenti> iaList, Date date, String idUtente)
	//		throws BusinessException {
	//	Session ses = SessionFactory.getSession();
	//	Utenti utente = null;
	//	try {
	//		utente = GenericDao.findById(ses, Utenti.class, idUtente);
	//	} catch (HibernateException e) {
	//		throw new BusinessException(e.getMessage(), e);
	//	} finally {
	//		ses.close();
	//	}
	//	List<EvasioniArticoli> eaList = new ArrayList<EvasioniArticoli>();
	//	for (IstanzeAbbonamenti ia:iaList) {
	//		Anagrafiche dest = ia.getAbbonato();
	//		if (al.getIdTipoDestinatario().equals(AppConstants.DEST_PAGANTE) && 
	//				(ia.getPagante() != null)) dest = ia.getPagante();
	//		EvasioniArticoli transEa = new EvasioniArticoli();
	//		transEa.setIdArticoloListino(al.getId());
	//		transEa.setIdArticoloOpzione(null);
	//		transEa.setArticolo(al.getArticolo());
	//		transEa.setCopie(ia.getCopie());
	//		transEa.setDataCreazione(date);
	//		transEa.setDataInvio(date);
	//		transEa.setDataModifica(date);
	//		transEa.setDataOrdine(null);
	//		transEa.setEliminato(false);
	//		transEa.setIdAbbonamento(ia.getAbbonamento().getId());
	//		transEa.setIdAnagrafica(dest.getId());
	//		transEa.setIdIstanzaAbbonamento(ia.getId());
	//		transEa.setIdTipoDestinatario(al.getIdTipoDestinatario());
	//		transEa.setNote(null);
	//		transEa.setOrdiniLogistica(null);
	//		transEa.setPrenotazioneIstanzaFutura(false);
	//		transEa.setUtente(utente);
	//		eaList.add(transEa);
	//	}
	//	return eaList;
	//}
	
	public static void updateDataEstrazioneArticoloListino(Integer idArticoloListino,
			int idRapporto, String idUtente) throws BusinessException {
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		ArticoliListiniDao alDao = new ArticoliListiniDao();
		Date today = DateUtil.now();
		try {
			ArticoliListini al = GenericDao.findById(ses, ArticoliListini.class, idArticoloListino);
			if (al != null) {
				if (al.getDataEstrazione() == null) {
					al.setDataEstrazione(today);
					alDao.update(ses, al);
					trn.commit();
					VisualLogger.get().addHtmlInfoLine(idRapporto, "La data/ora attuale e' stata impostata come data di estrazione dell'articolo");
				}
			}
		} catch (HibernateException e) {
			trn.rollback();
			VisualLogger.get().addHtmlErrorLine(idRapporto, e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
	}
	
	
	// ArticoliOpzioni
	
	
	public static List<MaterialiSpedizione> findPendingMaterialiSpedizioneArticoliOpzioni(
			Integer idArticoloListino, int offset, int pageSize, int idRapporto)
			throws BusinessException {
		Session ses = SessionFactory.getSession();
		List<MaterialiSpedizione> msList = null;
		try {
			msList = new MaterialiSpedizioneDao().findPendingByArticoloOpzione(ses,
					idArticoloListino, offset, pageSize);
		} catch (HibernateException e) {
			VisualLogger.get().addHtmlErrorLine(idRapporto, e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return msList;
	}
		
	public static void updateDataEstrazioneArticoloOpzione(Integer idArticoloOpzione,
			int idRapporto, String idUtente) throws BusinessException {
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		ArticoliOpzioniDao aoDao = new ArticoliOpzioniDao();
		Date today = DateUtil.now();
		try {
			ArticoliOpzioni ao = GenericDao.findById(ses, ArticoliOpzioni.class, idArticoloOpzione);
			if (ao.getDataEstrazione() == null) {
				ao.setDataEstrazione(today);
				aoDao.update(ses, ao);
				trn.commit();
				VisualLogger.get().addHtmlInfoLine(idRapporto, "La data/ora attuale e' stata impostata come data di estrazione dell'articolo");
			} else {
				VisualLogger.get().addHtmlInfoLine(idRapporto, "L'articolo aveva gia' una data di estrazione impostata!");
			}
		} catch (HibernateException e) {
			trn.rollback();
			VisualLogger.get().addHtmlErrorLine(idRapporto, e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
	}
	
	
	// Comuni a tutti
	
	
	public static Map<Anagrafiche, List<MaterialiSpedizione>> buildMapFromSpedizioni(
			List<MaterialiSpedizione> msList) throws BusinessException {
		Session ses = SessionFactory.getSession();
		Map<Anagrafiche, List<MaterialiSpedizione>> evasioniMap =
				new HashMap<Anagrafiche, List<MaterialiSpedizione>>();
		try {
			for (MaterialiSpedizione ms:msList) {
				Anagrafiche ana = GenericDao.findById(ses, Anagrafiche.class, ms.getIdAnagrafica());
				List<MaterialiSpedizione> msGroup = evasioniMap.get(ana);
				if (msGroup == null) {
					msGroup = new ArrayList<MaterialiSpedizione>();
					evasioniMap.put(ana, msGroup);
				}
				msGroup.add(ms);
			}
		} catch (HibernateException e) {
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return evasioniMap;
	}
	
	public static void writeMaterialiSpedizioneOnDb(List<MaterialiSpedizione> msList, Date dataInvio,
			int idRapporto) throws BusinessException {
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			MaterialiSpedizioneDao msDao = new MaterialiSpedizioneDao();
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Inizio scrittura su DB dell'estrazione");
			for (int i=0; i<msList.size(); i++) {
				MaterialiSpedizione ms = msList.get(i);
				MaterialiSpedizione found = msDao.checkMaterialeAbbonamento(ses, ms.getMateriale().getId(), ms.getIdAbbonamento());
				if (found != null) {//Esiste un'EvasioneFascicolo
					if (found.getDataInvio() != null) {//Esiste ed ha una data invio
						VisualLogger.get().addHtmlInfoLine(idRapporto,"ATTENZIONE: "+
								"L'abbonamento con id="+ms.getIdAbbonamento()+
								" ha gi&agrave; ricevuto "+ms.getMateriale().getCodiceMeccanografico());
					} else {//Esiste ma non ha una data invio
						found.setDataInvio(dataInvio);
						msDao.update(ses, found);
					}
				} else {//Non esiste un'EvasioneFascicolo
					ms.setDataInvio(dataInvio);
					msDao.sqlInsert(ses, ms);
				}
				if (((i % 500)==0) && (i >0)) {
					VisualLogger.get().addHtmlInfoLine(idRapporto, "Scrittura in corso: "+i+" di "+msList.size()+"...");
					ses.flush();
				}
			}
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Scrittura in corso: "+msList.size()+" di "+msList.size()+"...");
			trn.commit();
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Fine scrittura su DB dell'estrazione");
		} catch (HibernateException e) {
			trn.rollback();
			VisualLogger.get().addHtmlErrorLine(idRapporto, e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
	}
	

	
}
