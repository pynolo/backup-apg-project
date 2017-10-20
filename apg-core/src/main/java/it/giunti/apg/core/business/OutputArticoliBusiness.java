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
	
	//@SuppressWarnings("unchecked")
	//public static List<IstanzeAbbonamenti> extractIstanzeReceivingArticoliListini(
	//		Integer idArticoloListino, int offset, int pageSize, int idRapporto)
	//		throws BusinessException, EmptyResultException {
	//	Session ses = SessionFactory.getSession();
	//	List<IstanzeAbbonamenti> result = new ArrayList<IstanzeAbbonamenti>();
	//	try {
	//		ArticoliListini al = GenericDao.findById(ses, ArticoliListini.class, idArticoloListino);
	//		String sql = "select distinct ia.* from istanze_abbonamenti as ia " +
	//				"where "+
	//				//Condiz: deve avere il listino :id1 
	//				"ia.id_listino = :id1 and " +
	//				//Condiz: deve essere pagato
	//				"(ia.pagato = :b11 or ia.in_fatturazione = :b12) and "+//b11, b12 TRUE
	//				//Condiz: non deve essere bloccato
	//				"ia.invio_bloccato = :b2 and " +//b2 FALSE
	//				//Condiz: non ha ricevuto l'articolo
	//				"(select count(*) from evasioni_articoli as ea "+
	//					"where ea.id_istanza_abbonamento = ia.id and ea.id_articolo = :id2) = :i1 "+
	//				//Ordinamento
	//				"order by ia.id";
	//		//String hql = "from IstanzeAbbonamenti ia " +
	//		//		"where "+
	//		//		"ia.id not in "+
	//		//			"(select ea.idIstanzaAbbonamento from EvasioniArticoli ea "+
	//		//			"where ea.articolo.id = :id2) and "+
	//		//		"ia.listino.id = :id1 " +
	//		//		"order by ia.id";
	//		//Query q = ses.createQuery(hql);
	//		Query q = ses.createSQLQuery(sql).addEntity("ia",IstanzeAbbonamenti.class);
	//		q.setParameter("id1", al.getListino().getId(), IntegerType.INSTANCE);
	//		q.setParameter("b11", Boolean.TRUE, BooleanType.INSTANCE);
	//		q.setParameter("b12", Boolean.TRUE, BooleanType.INSTANCE);
	//		q.setParameter("b2", Boolean.FALSE, BooleanType.INSTANCE);
	//		q.setParameter("id2", al.getArticolo().getId(), IntegerType.INSTANCE);
	//		q.setParameter("i1", 0, IntegerType.INSTANCE);
	//		q.setFirstResult(offset);
	//		q.setMaxResults(pageSize);
	//		result = (List<IstanzeAbbonamenti>) q.list();
	//	} catch (HibernateException e) {
	//		VisualLogger.get().addHtmlErrorLine(idRapporto, e.getMessage(), e);
	//		throw new BusinessException(e.getMessage(), e);
	//	} finally {
	//		ses.close();
	//	}
	//	if (result != null) {
	//		if (result.size() > 0) {
	//			return result;
	//		}
	//	}
	//	throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	//}
	
	public static List<EvasioniArticoli> filterArticoliListiniByScadenza(Session ses, List<EvasioniArticoli> eaList) 
			throws HibernateException {
		List<EvasioniArticoli> result = new ArrayList<EvasioniArticoli>();
		for (EvasioniArticoli ea:eaList) {
			if ((ea.getDataLimite() != null) && (ea.getIdIstanzaAbbonamento() != null)) {
				//Ha scadenza => confronta con la data del saldo
				IstanzeAbbonamenti ia = GenericDao.findById(ses, IstanzeAbbonamenti.class, ea.getIdIstanzaAbbonamento());
				if (ia.getDataSaldo().before(ea.getDataLimite()) ||
						(ia.getInFatturazione())) {
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
	
	//@SuppressWarnings("unchecked")
	//public static List<IstanzeAbbonamenti> extractIstanzeReceivingArticoliOpzioni(
	//		Integer idArticoloOpzione, int offset, int pageSize, int idRapporto) throws BusinessException, EmptyResultException {
	//	Session ses = SessionFactory.getSession();
	//	List<IstanzeAbbonamenti> result = new ArrayList<IstanzeAbbonamenti>();
	//	try {
	//		ArticoliOpzioni ao = GenericDao.findById(ses, ArticoliOpzioni.class, idArticoloOpzione);
	//		//Tutti gli abbonamenti ATTIVI con l'opzione prevista che non hanno ricevuto l'Articolo
	//		String sql = "select distinct ia.* from istanze_abbonamenti as ia " +
	//					"left join opzioni_istanze_abbonamenti as oia on " +
	//					"(oia.id_istanza_abbonamento = ia.id) " +
	//					"left join listino as l on " +
	//					"(ia.id_listino = l.id) " +
	//				"where "+
	//				//Condiz: l'opzione deve essere :id1
	//				"oia.id_opzione = :id1 and "+
	//				//Condiz: deve essere pagato oppure il listino prevede l'invio senza pag
	//				"(ia.pagato = :b11 or ia.in_fatturazione = :b12 or l.invio_senza_pagamento = :b13) and "+//b11, b12, b13 TRUE
	//				//Condiz: non deve essere bloccato
	//				"ia.invio_bloccato = :b2 and " +//b2 FALSE
	//				//Condiz: non ha ricevuto l'articolo
	//				"(select count(*) from evasioni_articoli as ea "+
	//					"where ea.id_istanza_abbonamento = ia.id and ea.id_articolo = :id2) = :i1 "+
	//				//Ordinamento
	//				"order by ia.id";
	//		//String hql = "select oia.ia from OpzioniIstanzeAbbonamenti oia " +
	//		//		"where "+
	//		//		"oia.istanza.id not in "+
	//		//			"(select ea.idIstanzaAbbonamento from EvasioniArticoli ea "+
	//		//			"where oia.articolo.id = :id2) and " +
	//		//		"oia.opzione.id = :id1 " +
	//		//		"order by oia.ia.id";
	//		Query q = ses.createSQLQuery(sql).addEntity("ia", IstanzeAbbonamenti.class);
	//		q.setParameter("id1", ao.getOpzione().getId(), IntegerType.INSTANCE);
	//		q.setParameter("b11", Boolean.TRUE, BooleanType.INSTANCE);
	//		q.setParameter("b12", Boolean.TRUE, BooleanType.INSTANCE);
	//		q.setParameter("b13", Boolean.TRUE, BooleanType.INSTANCE);
	//		q.setParameter("b2", Boolean.FALSE, BooleanType.INSTANCE);
	//		q.setParameter("id2", ao.getArticolo().getId(), IntegerType.INSTANCE);
	//		q.setParameter("i1", 0, IntegerType.INSTANCE);
	//		q.setFirstResult(offset);
	//		q.setMaxResults(pageSize);
	//		result = (List<IstanzeAbbonamenti>) q.list();
	//	} catch (HibernateException e) {
	//		VisualLogger.get().addHtmlErrorLine(idRapporto, e.getMessage(), e);
	//		throw new BusinessException(e.getMessage(), e);
	//	} finally {
	//		ses.close();
	//	}
	//	if (result != null) {
	//		if (result.size() > 0) {
	//			return result;
	//		}
	//	}
	//	throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	//}
	
	//public static List<EvasioniArticoli> createEvasioniFromArticoloOpzione(ArticoliOpzioni ao,
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
	//		EvasioniArticoli transEa = new EvasioniArticoli();
	//		transEa.setIdArticoloListino(null);
	//		transEa.setIdArticoloOpzione(ao.getId());
	//		transEa.setArticolo(ao.getArticolo());
	//		transEa.setCopie(ia.getCopie());
	//		transEa.setDataCreazione(date);
	//		transEa.setDataInvio(date);
	//		transEa.setDataModifica(date);
	//		transEa.setDataOrdine(null);
	//		transEa.setEliminato(false);
	//		transEa.setIdAbbonamento(ia.getAbbonamento().getId());
	//		transEa.setIdAnagrafica(ia.getAbbonato().getId());
	//		transEa.setIdIstanzaAbbonamento(ia.getId());
	//		transEa.setIdTipoDestinatario(AppConstants.DEST_BENEFICIARIO);
	//		transEa.setNote(null);
	//		transEa.setOrdiniLogistica(null);
	//		transEa.setPrenotazioneIstanzaFutura(false);
	//		transEa.setUtente(utente);
	//		eaList.add(transEa);
	//	}
	//	return eaList;
	//}
	
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
