package it.giunti.apg.core.business;

import it.giunti.apg.core.VisualLogger;
import it.giunti.apg.core.persistence.EvasioniFascicoliDao;
import it.giunti.apg.core.persistence.FascicoliDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.core.persistence.QueryFactory;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.model.Fascicoli;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Listini;
import it.giunti.apg.shared.model.Utenti;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class OutputInvioBusiness {

	//private static final Logger LOG = LoggerFactory.getLogger(OutputInvioBusiness.class);
	private static final int DELTA_MESI = 1;
	private static final int PAGE_SIZE = 500;

	@SuppressWarnings("unchecked")
	public static List<IstanzeAbbonamenti> extractIstanzeRiceventiFascicolo(Integer idPeriodico,
			Integer idFascicolo, String copie, String italia, int idRapporto)
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<IstanzeAbbonamenti> result = new ArrayList<IstanzeAbbonamenti>();
		try {
			Fascicoli fascicolo = GenericDao.findById(ses, Fascicoli.class, idFascicolo);
			//ottiene le date di 18 mesi prima l'evasione del fascicolo
			Date dtFine = addMonth(fascicolo.getDataInizio(), (-1)*DELTA_MESI);
			Integer idOpzione = null;
			if (fascicolo.getOpzione() != null) {
				idOpzione = fascicolo.getOpzione().getId();
			}
			//estrae i tipi abbonamento associati ad abbonamenti attivi (tipi solo cartacei!)
			//ovvero: i tipi degli ia con attivi al tempo del fascicolo e che scadano DELTA_MESI prima (x succ)
			QueryFactory qfLst = new QueryFactory(ses, "select distinct ia.listino from IstanzeAbbonamenti as ia");
			qfLst.addWhere("ia.abbonamento.periodico.id = :d0");
			qfLst.addParam("d0", idPeriodico);
			qfLst.addWhere("ia.fascicoloInizio.dataInizio <= :d1");
			qfLst.addParam("d1", fascicolo.getDataFine());
			qfLst.addWhere("ia.fascicoloFine.dataFine >= :d2");
			qfLst.addParam("d2", dtFine);
			if (idOpzione == null) {
				//l'estrazione è ristretta ai tipi abbonamento cartacei
				//solo se non si sta estraendo un opzione!
				qfLst.addWhere("ia.listino.cartaceo = :b1");
				qfLst.addParam("b1", Boolean.TRUE);
			}
			Query tipiAbbQ = qfLst.getQuery();
			List<Listini> lstList = (List<Listini>) tipiAbbQ.list();
			
			//esegue una query per ciascun tipo abbonamento
			for (Listini lst:lstList) {
				VisualLogger.get().addHtmlInfoLine(idRapporto,
						"Estrazione '"+lst.getTipoAbbonamento().getNome()+"'");
				String baseSelect = "select distinct ia from IstanzeAbbonamenti as ia ";
				if (idOpzione != null) 
					baseSelect += "join ia.opzioniIstanzeAbbonamentiSet as s with s.opzione.id = :opz1 ";
				QueryFactory qf = new QueryFactory(ses, baseSelect);
				if (idOpzione != null) qf.addParam("opz1", idOpzione);
				qf.addWhere("ia.listino.id = :p0");
				qf.addParam("p0", lst.getId());
				qf.addWhere("ia.fascicoloInizio.dataInizio <= :d1");//data inizio <= data fascicolo
				qf.addParam("d1", fascicolo.getDataInizio());
				qf.addWhere("(" +//regolare e pagato: spediti-totali<=gracing [es. 7-6<=1 ok]
							"((ia.fascicoliSpediti-ia.fascicoliTotali) < :p1 and " +
							"((ia.pagato = :b11 or ia.inFatturazione = :b12 or ia.listino.invioSenzaPagamento = :b13 or ia.listino.fatturaDifferita = :b14 or (ia.listino.prezzo < :d15)) and ia.dataDisdetta is null and ia.ultimaDellaSerie = :b16)) " +
						"or " +//pagato ma con disdetta o non "ultima della serie":
							"((ia.fascicoliSpediti < ia.fascicoliTotali) and " +
							"((ia.pagato = :b21 or ia.inFatturazione = :b22 or ia.listino.invioSenzaPagamento = :b23 or ia.listino.fatturaDifferita = :b24 or (ia.listino.prezzo < :d25)) and (ia.dataDisdetta is not null or ia.ultimaDellaSerie = :b26))) " +
						"or " +//gracing iniziale:
							"(ia.fascicoliSpediti < :p2) " +
						")");
				qf.addParam("b11", Boolean.TRUE);
				qf.addParam("b12", Boolean.TRUE);
				qf.addParam("b13", Boolean.TRUE);
				qf.addParam("b14", Boolean.TRUE);
				qf.addParam("d15", AppConstants.SOGLIA);
				qf.addParam("b16", Boolean.TRUE);
				qf.addParam("b21", Boolean.TRUE);
				qf.addParam("b22", Boolean.TRUE);
				qf.addParam("b23", Boolean.TRUE);
				qf.addParam("b24", Boolean.TRUE);
				qf.addParam("d25", AppConstants.SOGLIA);
				qf.addParam("b26", Boolean.FALSE);
				qf.addParam("p1", lst.getGracingFinale());
				qf.addParam("p2", lst.getGracingIniziale());
				qf.addWhere("ia.invioBloccato = :b4");
				qf.addParam("b4", false);
				//restrizione su copie
				if (AppConstants.INCLUDI_INSIEME_INTERNO.equals(copie)) {
					qf.addWhere("ia.copie = :p3");
					qf.addParam("p3", 1);
				}
				if (AppConstants.INCLUDI_INSIEME_ESTERNO.equals(copie)) {
					qf.addWhere("ia.copie > :p3");
					qf.addParam("p3", 1);
				}
				//restrizione esteri
				if (AppConstants.INCLUDI_INSIEME_INTERNO.equals(italia)) {
					qf.addWhere("upper(ia.abbonato.indirizzoPrincipale.nazione.nomeNazione) = :p4");
					qf.addParam("p4", "ITALIA");
				}
				if (AppConstants.INCLUDI_INSIEME_ESTERNO.equals(italia)) {
					qf.addWhere("upper(ia.abbonato.indirizzoPrincipale.nazione.nomeNazione) <> :p4");
					qf.addParam("p4", "ITALIA");
				}
				qf.addOrder("ia.id asc");
				Query iaQ = qf.getQuery();
				
				//Estrazione paginata
				int offset = 0;
				int size = 0;
				do {
					if (size > 0) VisualLogger.get().addHtmlInfoLine(idRapporto, "Estratti:"+size+" Totale:"+result.size());
					iaQ.setFirstResult(offset);
					iaQ.setMaxResults(PAGE_SIZE);
					List<IstanzeAbbonamenti> iaList = (List<IstanzeAbbonamenti>) iaQ.list();
					size = iaList.size();
					offset += size;
					result.addAll(iaList);
					ses.flush();
					ses.clear();
				} while (size > 0);
			}
		} catch (HibernateException e) {
			VisualLogger.get().addHtmlErrorLine(idRapporto, e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (result != null) {
			if (result.size() > 0) {
				return result;
			}
		}
		throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	}
	
//	private static void addWithFilter(Integer idOpzione, List<IstanzeAbbonamenti> result, List<IstanzeAbbonamenti> iaList) {
//		for (IstanzeAbbonamenti ia:iaList) {
//			if (ia.opzioniSet() != null) {
//				if (ia.opzioniSet().size() > 0) {
//					for (Opzioni s:ia.opzioniSet()) {
//						if (s.getId().equals(idOpzione)) result.add(ia);
//					}
//				}
//			}
//		}
//	}
	
	public static void writeEvasioniFascicoliOnDb(List<IstanzeAbbonamenti> iaList, Integer idFascicolo,
			String copie, String italia, int idRapporto, String idUtente) 
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			EvasioniFascicoliDao efDao = new EvasioniFascicoliDao();
			IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
			Utenti utente = GenericDao.findById(ses, Utenti.class, idUtente);
			Fascicoli fascicolo = GenericDao.findById(ses, Fascicoli.class, idFascicolo);
			
			//DecimalFormat df = new DecimalFormat("0.0");
			Date today = new Date();
			boolean spedito = false;
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Inizio scrittura su DB dell'estrazione");
			for (int i = 0; i < iaList.size(); i++) {
				IstanzeAbbonamenti ia = iaList.get(i);
				spedito = efDao.checkFascicoloIstanza(ses, ia, idFascicolo);
				if (!spedito) {
					//Sostituiti da un'istruzione SQL:
					//EvasioniFascicoli ef = createTransientEvasioniFascicoli(ia, fascicolo, today, utente);
					//dao.save(ses, ef);
					efDao.sqlInsert(ses, ia, fascicolo, today, utente);
					int numFas = ia.getFascicoliSpediti()+fascicolo.getFascicoliAccorpati();
					//Sostituiti da un'istruzione SQL:
					//ia.setFascicoliSpediti(numFas);
					//efDao.update(ses, ia.getId(), ia);
					iaDao.sqlUpdateFascicoliSpediti(ses, ia, numFas);
					if (((i % 500)==0) && (i >0)) {
						VisualLogger.get().addHtmlInfoLine(idRapporto, "Scrittura in corso: "+i+" di "+iaList.size());
						ses.flush();
					}
				}
			}
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Scrittura in corso: "+iaList.size()+" di "+iaList.size());
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
	
	//private static EvasioniFascicoli createTransientEvasioniFascicoli(IstanzeAbbonamenti ia, Fascicoli fascicolo, Date day, Utenti utente) {
	//	EvasioniFascicoli ef = new EvasioniFascicoli();
	//	ef.setDataCreazione(day);
	//	ef.setDataModifica(day);
	//	ef.setDataStampa(day);
	//	ef.setFascicolo(fascicolo);
	//	ef.setIdAbbonamento(ia.getAbbonamento().getId());
	//	ef.setIdIstanzaAbbonamento(ia.getId());
	//	ef.setIdTipoEvasione(AppConstants.EVASIONE_REGOLARE);
	//	ef.setQuantita(ia.getCopie());
	//	ef.setUtente(utente);
	//	return ef;
	//}
	
	public static void writeDataEvasioneFascicolo(Integer idFascicolo, int idRapporto, String idUtente) 
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		FascicoliDao fDao = new FascicoliDao();
		Date today = new Date();
		try {
			Fascicoli fas = GenericDao.findById(ses, Fascicoli.class, idFascicolo);
			if (fas != null) {
				if (fas.getDataEstrazione() == null) {
					fas.setDataEstrazione(today);
					fDao.update(ses, fas);
					//List<Fascicoli> opzFasList = fDao.findFascicoliOpzioniAbbinati(ses, fas);
					//for (Fascicoli sf:opzFasList) {
					//	sf.setDataEstrazione(today);
					//	fDao.update(ses, sf);
					//}
					trn.commit();
					VisualLogger.get().addHtmlInfoLine(idRapporto, "La data/ora attuale e' stata impostata come data di estrazione del fascicolo");
				} else {
					VisualLogger.get().addHtmlInfoLine(idRapporto, "La data di estrazione del fascicolo era gia' stata impostata in precedenza");
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
	
	private static Date addMonth(Date data, int mesi) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(data);
		cal.add(Calendar.MONTH, mesi);
		return cal.getTime();
	}
	
	public static <S> S findEntityById(Class<S> findClass, Serializable key, int idRapporto) throws BusinessException {
		Session ses = SessionFactory.getSession();
		S result = null;
		try {
			result = GenericDao.findById(ses, findClass, key);
		} catch (HibernateException e) {
			VisualLogger.get().addHtmlErrorLine(idRapporto, e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
			
	public static boolean verifyFascicoloEvaso(List<IstanzeAbbonamenti> iaList,
			Integer idFascicolo, int idRapporto) throws BusinessException {
		Session ses = SessionFactory.getSession();
		EvasioniFascicoliDao dao = new EvasioniFascicoliDao();
		boolean fascicoloFound = false;
		try {
			int i = 0;
			while ((i<iaList.size()) && !fascicoloFound) {
				IstanzeAbbonamenti ia = iaList.get(i);
				fascicoloFound = dao.checkFascicoloIstanza(ses, ia, idFascicolo);
				i++;
			}
		} catch (HibernateException e) {
			VisualLogger.get().addHtmlErrorLine(idRapporto, e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return fascicoloFound;
	}
	
}
