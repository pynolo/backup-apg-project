package it.giunti.apg.automation.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.VisualLogger;
import it.giunti.apg.core.business.OutputArticoliBusiness;
import it.giunti.apg.core.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.core.persistence.MaterialiSpedizioneDao;
import it.giunti.apg.core.persistence.PagamentiDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.MaterialiSpedizione;
import it.giunti.apg.shared.model.Pagamenti;

public class ArticoliBusiness {

	//private static final Logger LOG = LoggerFactory.getLogger(ArticoliBusiness.class);
	
	public static List<MaterialiSpedizione> findPendingSpedizioniIstanze(Date date, int idRapporto)
			throws BusinessException, EmptyResultException {
		List<MaterialiSpedizione> msList = new ArrayList<MaterialiSpedizione>();
		Session ses = SessionFactory.getSession();
		MaterialiSpedizioneDao msDao = new MaterialiSpedizioneDao();
		try {
			List<MaterialiSpedizione> eaManualList = msDao.findPendingByIstanzeManual(ses, date);
			msList.addAll(eaManualList);
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Articoli inseriti a mano da evadere: "+eaManualList.size());
			List<MaterialiSpedizione> eaListiniList = msDao.findPendingByIstanzeListini(ses, date);
			msList.addAll(eaListiniList);
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Articoli da listino da evadere: "+eaListiniList.size());
			List<MaterialiSpedizione> eaOpzioniList = msDao.findPendingByIstanzeOpzioni(ses, date);
			msList.addAll(eaOpzioniList);
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Articoli da opzioni da evadere: "+eaOpzioniList.size());
			
			//Filtraggio pagati dopo il limite
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Filtraggio delle date di scadenza in corso");
			msList = OutputArticoliBusiness.filterArticoliListiniByScadenza(ses, msList);
		} catch (HibernateException e) {
			VisualLogger.get().addHtmlErrorLine(idRapporto, e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (msList == null) throw new EmptyResultException("Nessun articolo da estrarre dalle istanze");
		if (msList.size() == 0) throw new EmptyResultException("Nessun articolo da estrarre dalle istanze");
		return msList;
	}

	public static List<MaterialiSpedizione> findPendingSpedizioniAnagrafiche(Date date, int idRapporto)
			throws BusinessException, EmptyResultException {
		List<MaterialiSpedizione> eaList = null;
		Session ses = SessionFactory.getSession();
		try {
			eaList = new MaterialiSpedizioneDao().findPendingByAnagrafiche(ses, date);
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
	
	public static void markSpedizioniEliminate(List<MaterialiSpedizione> msList, Date dataInvio,
			int idRapporto, String idUtente) throws BusinessException {
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		MaterialiSpedizioneDao msDao = new MaterialiSpedizioneDao();
		PagamentiDao pDao = new PagamentiDao();
		int count = 0;
		try {
			for (MaterialiSpedizione ms:msList) {
				if (!ms.getPrenotazioneIstanzaFutura()) {//questa istanza
					Date dataLimite = ms.getDataLimite();
					if (dataLimite != null) {
						//if (dataLimite.before(ed.getDataCreazione()) ) {
						//	//Eliminato se la data limite era prima della data di creazione
						//	ed.setEliminato(true);
						//}
						IstanzeAbbonamenti ia = new IstanzeAbbonamentiDao().findUltimaIstanzaByAbbonamento(ses, ms.getIdAbbonamento());
						if (ia != null) {
							Date dataUltimoPagamento = pDao.findLastDataPagamento(ses, ia.getId());
							if (dataUltimoPagamento != null) {
								if (dataLimite.before(dataUltimoPagamento)) {
									//Ha saldato dopo la data limite
									ms.setDataAnnullamento(dataInvio);
								}
							}
							if (dataLimite.after(ia.getDataInizio())) {
								//Il dono ha un limite fuori dalla durata dell'istanza: è da eliminare
								ms.setDataAnnullamento(dataInvio);
							}
						}
					}
				}
				if (ms.getDataAnnullamento() != null) {
					msDao.update(ses, ms);
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
	
	public static List<MaterialiSpedizione> filterArticoliListiniByScadenza(List<MaterialiSpedizione> msList) 
			throws BusinessException {
		List<MaterialiSpedizione> result = new ArrayList<MaterialiSpedizione>();
		Session ses = SessionFactory.getSession();
		try {
			for (MaterialiSpedizione ms:msList) {
				if ((ms.getDataLimite() != null) && (ms.getIdAbbonamento() != null)) {
					//Ha scadenza => confronta con la data del saldo
					IstanzeAbbonamenti ia = new IstanzeAbbonamentiDao().findUltimaIstanzaByAbbonamento(ses, ms.getIdAbbonamento());
					if (ia.getDataSaldo().before(ms.getDataLimite()) ||
							(ia.getFatturaDifferita())) {
						//Se saldato prima del limite allora OK
						result.add(ms);
					} else {
						//Altrimenti confronta la data dell'ultimo pagamento
						if (ia.getPagato()) {
							List<Pagamenti> pList = new PagamentiDao()
									.findPagamentiByIstanzaAbbonamento(ses, ia.getId());
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
		} catch (HibernateException e) {
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	public static void writeDoniOnDb(List<MaterialiSpedizione> msList, Date dataInvio, int idRapporto, String idUtente) 
		throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		MaterialiSpedizioneDao msDao = new MaterialiSpedizioneDao();
		try {
			for (MaterialiSpedizione ms:msList) {
				ms.setDataInvio(dataInvio);
				msDao.update(ses, ms);
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
