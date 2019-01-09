package it.giunti.apg.core.business;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.VisualLogger;
import it.giunti.apg.core.persistence.ArticoliListiniDao;
import it.giunti.apg.core.persistence.ArticoliOpzioniDao;
import it.giunti.apg.core.persistence.EvasioniArticoliDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.PagamentiDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.ArticoliListini;
import it.giunti.apg.shared.model.ArticoliOpzioni;
import it.giunti.apg.shared.model.EvasioniArticoli;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Pagamenti;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class OutputArticoliBusiness {

	
	// ArticoliListini
	
	
	public static List<EvasioniArticoli> findPendingEvasioniArticoliListini(
			Integer idArticoloListino, Date date, int offset, int pageSize, int idRapporto)
			throws BusinessException {
		Session ses = SessionFactory.getSession();
		List<EvasioniArticoli> eaList = null;
		try {
			eaList = new EvasioniArticoliDao().findPendingByArticoloListino(ses,
					idArticoloListino, date, offset, pageSize);
		} catch (HibernateException e) {
			VisualLogger.get().addHtmlErrorLine(idRapporto, e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return eaList;
	}
		
	public static List<EvasioniArticoli> filterArticoliListiniByScadenza(Session ses, List<EvasioniArticoli> eaList) 
			throws HibernateException {
		List<EvasioniArticoli> result = new ArrayList<EvasioniArticoli>();
		for (EvasioniArticoli ea:eaList) {
			if ((ea.getDataLimite() != null) && (ea.getIdIstanzaAbbonamento() != null)) {
				//Ha scadenza => confronta con la data del saldo
				IstanzeAbbonamenti ia = GenericDao.findById(ses, IstanzeAbbonamenti.class, ea.getIdIstanzaAbbonamento());
				if (ia.getDataSaldo().before(ea.getDataLimite()) ||
						(ia.getFatturaDifferita())) {
					//Se saldato prima del limite allora OK
					result.add(ea);
				} else {
					//Altrimenti confronta la data dell'ultimo pagamento
					if (ia.getPagato()) {
						List<Pagamenti> pList = new PagamentiDao().findPagamentiByIstanzaAbbonamento(ses, ea.getIdIstanzaAbbonamento());
						Date latest = ServerConstants.DATE_FAR_PAST;
						for (Pagamenti p:pList) {
							if (p.getDataPagamento().after(latest)) {
								latest = p.getDataPagamento();
							}
						}
						if (latest.before(ea.getDataLimite())) {
							result.add(ea);
						}
					}
				}
			} else {
				//Non ha scadenza => passa il filtro
				result.add(ea);
			}
		}
		return result;
	}
	
	public static List<EvasioniArticoli> filterArticoliListiniByScadenza(List<EvasioniArticoli> eaList) 
			throws BusinessException {
		List<EvasioniArticoli> result = null;
		Session ses = SessionFactory.getSession();
		try {
			result = filterArticoliListiniByScadenza(ses, eaList);
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
	
	
	public static List<EvasioniArticoli> findPendingEvasioniArticoliOpzioni(
			Integer idArticoloListino, int offset, int pageSize, int idRapporto)
			throws BusinessException {
		Session ses = SessionFactory.getSession();
		List<EvasioniArticoli> eaList = null;
		try {
			eaList = new EvasioniArticoliDao().findPendingByArticoloOpzione(ses,
					idArticoloListino, offset, pageSize);
		} catch (HibernateException e) {
			VisualLogger.get().addHtmlErrorLine(idRapporto, e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return eaList;
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
	
	
	public static Map<Anagrafiche, List<EvasioniArticoli>> buildMapFromEvasioni(
			List<EvasioniArticoli> eaList) throws BusinessException {
		Session ses = SessionFactory.getSession();
		Map<Anagrafiche, List<EvasioniArticoli>> evasioniMap =
				new HashMap<Anagrafiche, List<EvasioniArticoli>>();
		try {
			for (EvasioniArticoli ea:eaList) {
				Anagrafiche ana = GenericDao.findById(ses, Anagrafiche.class, ea.getIdAnagrafica());
				List<EvasioniArticoli> eaGroup = evasioniMap.get(ana);
				if (eaGroup == null) {
					eaGroup = new ArrayList<EvasioniArticoli>();
					evasioniMap.put(ana, eaGroup);
				}
				eaGroup.add(ea);
			}
		} catch (HibernateException e) {
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return evasioniMap;
	}
	
	public static void writeEvasioniArticoliOnDb(List<EvasioniArticoli> eaList, Date dataInvio,
			int idRapporto) throws BusinessException {
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			EvasioniArticoliDao eaDao = new EvasioniArticoliDao();
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Inizio scrittura su DB dell'estrazione");
			for (int i=0; i<eaList.size(); i++) {
				EvasioniArticoli ea = eaList.get(i);
				EvasioniArticoli found = eaDao.checkArticoloIstanza(ses, ea.getIdIstanzaAbbonamento(), ea.getArticolo().getId());
				if (found != null) {//Esiste un'EvasioneFascicolo
					if (found.getDataInvio() != null) {//Esiste ed ha una data invio
						VisualLogger.get().addHtmlInfoLine(idRapporto,"ATTENZIONE: "+
								"L'istanza con id="+ea.getIdIstanzaAbbonamento()+
								" ha gi&agrave; ricevuto "+ea.getArticolo().getCodiceMeccanografico());
					} else {//Esiste ma non ha una data invio
						found.setDataInvio(dataInvio);
						eaDao.update(ses, found);
					}
				} else {//Non esiste un'EvasioneFascicolo
					ea.setDataInvio(dataInvio);
					eaDao.sqlInsert(ses, ea);
				}
				if (((i % 500)==0) && (i >0)) {
					VisualLogger.get().addHtmlInfoLine(idRapporto, "Scrittura in corso: "+i+" di "+eaList.size()+"...");
					ses.flush();
				}
			}
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Scrittura in corso: "+eaList.size()+" di "+eaList.size()+"...");
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
