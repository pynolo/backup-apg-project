package it.giunti.apg.automation.business;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.VisualLogger;
import it.giunti.apg.core.business.OutputArticoliBusiness;
import it.giunti.apg.core.persistence.EvasioniArticoliDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.PagamentiDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.model.EvasioniArticoli;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Pagamenti;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ArticoliBusiness {

	//private static final Logger LOG = LoggerFactory.getLogger(ArticoliBusiness.class);
	
	public static List<EvasioniArticoli> findPendingArticoliIstanze(Date date, int idRapporto)
			throws BusinessException, EmptyResultException {
		List<EvasioniArticoli> eaList = new ArrayList<EvasioniArticoli>();
		Session ses = SessionFactory.getSession();
		try {
			List<EvasioniArticoli> eaManualList = new EvasioniArticoliDao().findPendingByIstanzeManual(ses, date);
			eaList.addAll(eaManualList);
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Articoli inseriti a mano da evadere: "+eaManualList.size());
			List<EvasioniArticoli> eaListiniList = new EvasioniArticoliDao().findPendingByIstanzeListini(ses, date);
			eaList.addAll(eaListiniList);
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Articoli da listino da evadere: "+eaListiniList.size());
			List<EvasioniArticoli> eaOpzioniList = new EvasioniArticoliDao().findPendingByIstanzeOpzioni(ses, date);
			eaList.addAll(eaOpzioniList);
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Articoli da opzioni da evadere: "+eaOpzioniList.size());
			
			//Filtraggio pagati dopo il limite
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Filtraggio delle date di scadenza in corso");
			eaList = OutputArticoliBusiness.filterArticoliListiniByScadenza(ses, eaList);
		} catch (HibernateException e) {
			VisualLogger.get().addHtmlErrorLine(idRapporto, e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (eaList == null) throw new EmptyResultException("Nessun articolo da estrarre dalle istanze");
		if (eaList.size() == 0) throw new EmptyResultException("Nessun articolo da estrarre dalle istanze");
		return eaList;
	}

	public static List<EvasioniArticoli> findPendingArticoliAnagrafiche(Date date, int idRapporto)
			throws BusinessException, EmptyResultException {
		List<EvasioniArticoli> eaList = null;
		Session ses = SessionFactory.getSession();
		try {
			eaList = new EvasioniArticoliDao().findPendingByAnagrafiche(ses, date);
		} catch (HibernateException e) {
			VisualLogger.get().addHtmlErrorLine(idRapporto, e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (eaList == null) throw new EmptyResultException("Nessun articolo da estrarre dalle anagrafiche");
		if (eaList.size() == 0) throw new EmptyResultException("Nessun articolo da estrarre dalle anagrafiche");
		return eaList;
	}
	
	public static void markEvasioniArticoliEliminati(List<EvasioniArticoli> edList, Date dataInvio,
			int idRapporto, String idUtente) throws BusinessException {
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		EvasioniArticoliDao edDao = new EvasioniArticoliDao();
		PagamentiDao pDao = new PagamentiDao();
		int count = 0;
		try {
			for (EvasioniArticoli ed:edList) {
				if (!ed.getPrenotazioneIstanzaFutura()) {//questa istanza
					Date dataLimite = ed.getDataLimite();
					if (dataLimite != null) {
						//if (dataLimite.before(ed.getDataCreazione()) ) {
						//	//Eliminato se la data limite era prima della data di creazione
						//	ed.setEliminato(true);
						//}
						IstanzeAbbonamenti ia = GenericDao.findById(ses,
								IstanzeAbbonamenti.class, ed.getIdIstanzaAbbonamento());
						if (ia != null) {
							Date dataUltimoPagamento = pDao.findLastDataPagamento(ses, ia.getId());
							if (dataUltimoPagamento != null) {
								if (dataLimite.before(dataUltimoPagamento)) {
									//Ha saldato dopo la data limite
									ed.setDataAnnullamento(dataInvio);
								}
							}
							if (dataLimite.after(ia.getFascicoloFine().getDataInizio())) {
								//Il dono ha un limite fuori dalla durata dell'istanza: è da eliminare
								ed.setDataAnnullamento(dataInvio);
							}
						}
					}
				}
				if (ed.getDataAnnullamento() != null) {
					edDao.update(ses, ed);
					count++;
				}
			}
			trn.commit();
			if (count > 0) VisualLogger.get().addHtmlInfoLine(idRapporto, count+" doni sono stati aggiornati su DB come eliminati");
		} catch (HibernateException e) {
			trn.rollback();
			VisualLogger.get().addHtmlErrorLine(idRapporto, e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
	}
	
	public static List<EvasioniArticoli> filterArticoliListiniByScadenza(List<EvasioniArticoli> eaList) 
			throws BusinessException {
		List<EvasioniArticoli> result = new ArrayList<EvasioniArticoli>();
		Session ses = SessionFactory.getSession();
		try {
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
		} catch (HibernateException e) {
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	public static void writeDoniOnDb(List<EvasioniArticoli> edList, Date dataInvio, int idRapporto, String idUtente) 
		throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		EvasioniArticoliDao edDao = new EvasioniArticoliDao();
		try {
			for (EvasioniArticoli ed:edList) {
				ed.setDataInvio(dataInvio);
				edDao.update(ses, ed);
			}
			trn.commit();
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Fine scrittura su DB dell'invio doni");
		} catch (HibernateException e) {
			trn.rollback();
			VisualLogger.get().addHtmlErrorLine(idRapporto, e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
	}
}
