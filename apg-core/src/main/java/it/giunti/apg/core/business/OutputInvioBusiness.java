package it.giunti.apg.core.business;

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

import it.giunti.apg.core.VisualLogger;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.MaterialiProgrammazioneDao;
import it.giunti.apg.core.persistence.MaterialiSpedizioneDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Listini;
import it.giunti.apg.shared.model.MaterialiProgrammazione;
import it.giunti.apg.shared.model.MaterialiSpedizione;

public class OutputInvioBusiness {

	//private static final Logger LOG = LoggerFactory.getLogger(OutputInvioBusiness.class);
	private static final int DELTA_MESI = 1;
	private static final int PAGE_SIZE = 500;

	@SuppressWarnings("unchecked")
	public static List<IstanzeAbbonamenti> extractIstanzeRiceventiMateriale(Integer idMaterialeProgrammazione,
			String copie, String italia, int idRapporto) throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		MaterialiProgrammazioneDao mpDao = new MaterialiProgrammazioneDao();
		List<IstanzeAbbonamenti> result = new ArrayList<IstanzeAbbonamenti>();
		try {
			MaterialiProgrammazione mp = GenericDao.findById(ses, MaterialiProgrammazione.class, idMaterialeProgrammazione);
			//ottiene le date di 18 mesi prima l'evasione del fascicolo
			Date dtFine = addMonth(mp.getDataNominale(), (-1)*DELTA_MESI);
			Integer idOpzione = null;
			if (mp.getOpzione() != null) {
				idOpzione = mp.getOpzione().getId();
			}
			//estrae i tipi abbonamento associati ad abbonamenti attivi (tipi solo cartacei!)
			//ovvero: i tipi degli ia con attivi al tempo del fascicolo e che scadano DELTA_MESI prima (x succ)
			String hql1 = "select distinct ia.listino from IstanzeAbbonamenti as ia, OpzioniListini ol where "+
					"ia.listino.id = ol.listino.id and "+
					"ol.opzione.id = :id1 and "+
					"ia.dataInizio <= :d1 and "+
					"ia.dataFine >= :d2 ";
			Query q1 = ses.createQuery(hql1);
			q1.setInteger("id1", idOpzione);
			q1.setDate("d1", mp.getDataNominale());
			q1.setDate("d2", dtFine);
			List<Listini> lstList = (List<Listini>) q1.list();
			
			//esegue una query per ciascun tipo abbonamento
			for (Listini lst:lstList) {
				//Date
				Date dataFascicolo = mp.getDataNominale();
				MaterialiProgrammazione previousMpGracingInizio = mpDao.stepBackFascicoloBeforeDate(ses, lst.getId(), mp.getDataNominale(), lst.getGracingIniziale());
				MaterialiProgrammazione previousMpGracingFine = mpDao.stepForwardFascicoloAfterDate(ses, lst.getId(), mp.getDataNominale(), lst.getGracingFinale());
				
				// CONDIZIONI IN OR
				//1) ia ha data gracingIni dopo questo fascicolo && ia non pagato
				//2) ia ha data fine dopo questo fascicolo && ia pagato con disdetta
				//3) ia ha data gracingFin dopo questo fascicolo && ia pagato
				// si traducono in
				//1) ia deve avere data inizio >= a data nominale di Xgi fascicoli fa (&& ia non pagato)
				//2) ia deve avere data fine >= data nominale fascicolo (&& ia pagato con disdetta)
				//3) ia deve avere data fine >= data nominale di Xgf fascicoli fa (&& ia pagato)
				VisualLogger.get().addHtmlInfoLine(idRapporto,
						"Estrazione '"+lst.getTipoAbbonamento().getNome()+"'");
				String hql2 = "select distinct ia from IstanzeAbbonamenti as ia ";
				if (idOpzione != null) 
					hql2 += "join ia.opzioniIstanzeAbbonamentiSet as s with s.opzione.id = :opz1 ";
				hql2 += "where "+
					"ia.listino.id = :p0 and "+
					"ia.dataInizio <= :dt1 and "+//ia data inizio prima di questo fascicolo
					"("+
						"(ia.dataInizio >= :dt21) or "+ //1) non pagato
						"(ia.dataFine >= :dt22 and "+ //2) pagato con disdetta:
							"("+
								"(ia.pagato = :b21 or ia.fatturaDifferita = :b22 or ia.listino.invioSenzaPagamento = :b23 or ia.listino.fatturaDifferita = :b24 or (ia.listino.prezzo < :d25)) and "+
								"(ia.dataDisdetta is not null or ia.ultimaDellaSerie = :b26) "+
							") "+
						") or "+
						"(ia.dataFine >= :dt23 and "+ //3) pagato:
							"("+
								"(ia.pagato = :b11 or ia.fatturaDifferita = :b12 or ia.listino.invioSenzaPagamento = :b13 or ia.listino.fatturaDifferita = :b14 or (ia.listino.prezzo < :d15)) and "+
								"ia.dataDisdetta is null and "+
								"ia.ultimaDellaSerie = :b16 "+
							") " +
						") "+
					") and ";
				//restrizione su copie
				if (AppConstants.INCLUDI_INSIEME_INTERNO.equals(copie)) 
					hql2 += "ia.copie = :p3 and ";
				if (AppConstants.INCLUDI_INSIEME_ESTERNO.equals(copie)) 
					hql2 += "ia.copie > :p3 and ";
				//restrizione esteri
				if (AppConstants.INCLUDI_INSIEME_INTERNO.equals(italia))
					hql2 += "upper(ia.abbonato.indirizzoPrincipale.nazione.nomeNazione) = :p4 and ";
				if (AppConstants.INCLUDI_INSIEME_ESTERNO.equals(italia))
					hql2 += "upper(ia.abbonato.indirizzoPrincipale.nazione.nomeNazione) <> :p4 and ";
				hql2 += "ia.invioBloccato = :b4 "+
						"order by ia.id asc";
						
				Query q2 = ses.createQuery(hql2);
				if (idOpzione != null) q2.setParameter("opz1", idOpzione);
				q2.setParameter("p0", lst.getId());
				q2.setParameter("dt1", dataFascicolo);
				q2.setParameter("dt21", previousMpGracingInizio.getDataNominale());
				q2.setParameter("dt22", dataFascicolo);
				q2.setParameter("dt23", previousMpGracingFine.getDataNominale());
				q2.setParameter("b11", Boolean.TRUE);
				q2.setParameter("b12", Boolean.TRUE);
				q2.setParameter("b13", Boolean.TRUE);
				q2.setParameter("b14", Boolean.TRUE);
				q2.setParameter("d15", AppConstants.SOGLIA);
				q2.setParameter("b16", Boolean.TRUE);
				q2.setParameter("b21", Boolean.TRUE);
				q2.setParameter("b22", Boolean.TRUE);
				q2.setParameter("b23", Boolean.TRUE);
				q2.setParameter("b24", Boolean.TRUE);
				q2.setParameter("d25", AppConstants.SOGLIA);
				q2.setParameter("b26", Boolean.FALSE);
				if (AppConstants.INCLUDI_INSIEME_INTERNO.equals(copie)) 
					q2.setParameter("p3", 1);
				if (AppConstants.INCLUDI_INSIEME_ESTERNO.equals(copie)) 
					q2.setParameter("p3", 1);
				if (AppConstants.INCLUDI_INSIEME_INTERNO.equals(italia))
					q2.setParameter("p4", "ITALIA");
				if (AppConstants.INCLUDI_INSIEME_ESTERNO.equals(italia)) 
					q2.setParameter("p4", "ITALIA");
				q2.setParameter("b4", false);
				
				//Estrazione paginata
				int offset = 0;
				int size = 0;
				do {
					if (size > 0) VisualLogger.get().addHtmlInfoLine(idRapporto, "Estratti:"+size+" Totale:"+result.size());
					q2.setFirstResult(offset);
					q2.setMaxResults(PAGE_SIZE);
					List<IstanzeAbbonamenti> iaList = (List<IstanzeAbbonamenti>) q2.list();
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
	
	public static void writeMaterialiSpedizioneOnDb(List<IstanzeAbbonamenti> iaList, Integer idMateriale,
			String copie, String italia, int idRapporto, String idUtente) 
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			MaterialiSpedizioneDao msDao = new MaterialiSpedizioneDao();
			//DecimalFormat df = new DecimalFormat("0.0");
			Date today = DateUtil.now();
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Inizio scrittura su DB dell'estrazione");
			for (int i = 0; i < iaList.size(); i++) {
				IstanzeAbbonamenti ia = iaList.get(i);
				MaterialiSpedizione existing = msDao.checkMaterialeAbbonamento(ses, idMateriale, ia.getAbbonamento().getId());
				if (existing == null) {
					//Istruzione SQL:
					msDao.sqlInsertFascicolo(ses, ia, idMateriale, today);
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
	
	//private static MaterialiSpedizioni createTransientMaterialiSpedizioni(IstanzeAbbonamenti ia, Fascicoli fascicolo, Date day, Utenti utente) {
	//	MaterialiSpedizioni ef = new MaterialiSpedizioni();
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
	
	public static void writeDataSpedizione(int idMateriale, int idPeriodico, int idRapporto) 
			throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		MaterialiProgrammazioneDao mpDao = new MaterialiProgrammazioneDao();
		Date today = DateUtil.now();
		try {
			MaterialiProgrammazione mp = mpDao.findByMaterialePeriodico(ses, idMateriale, idPeriodico);
			if (mp != null) {
				if (mp.getDataEstrazione() == null) {
					mp.setDataEstrazione(today);
					mpDao.update(ses, mp);
					trn.commit();
					VisualLogger.get().addHtmlInfoLine(idRapporto, "La data/ora attuale e' stata impostata come data di estrazione del fascicolo");
				} else {
					VisualLogger.get().addHtmlInfoLine(idRapporto, "La data di estrazione del fascicolo era gia' stata impostata in precedenza");
				}
			} else {
				VisualLogger.get().addHtmlInfoLine(idRapporto, "Materiale non trovato. id="+idMateriale);
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
			
	public static boolean verifyMaterialeSpedito(List<IstanzeAbbonamenti> iaList,
			Integer idMateriale, int idRapporto) throws BusinessException {
		Session ses = SessionFactory.getSession();
		MaterialiSpedizioneDao msDao = new MaterialiSpedizioneDao();
		boolean fascicoloFound = false;
		try {
			int i = 0;
			while ((i<iaList.size()) && !fascicoloFound) {
				IstanzeAbbonamenti ia = iaList.get(i);
				MaterialiSpedizione ms = msDao.checkMaterialeAbbonamento(ses, idMateriale, ia.getAbbonamento().getId());
				if (ms != null) fascicoloFound = true;
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
