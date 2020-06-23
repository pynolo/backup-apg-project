package it.giunti.apg.core.persistence;

import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.IstanzeStatusUtil;
import it.giunti.apg.shared.model.Abbonamenti;
import it.giunti.apg.shared.model.EvasioniFascicoli6;
import it.giunti.apg.shared.model.Fascicoli6;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.OpzioniIstanzeAbbonamenti;
import it.giunti.apg.shared.model.Periodici;
import it.giunti.apg.shared.model.Utenti;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;

public class EvasioniFascicoli6Dao implements BaseDao<EvasioniFascicoli6> {

	@Override
	public void update(Session ses, EvasioniFascicoli6 instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
	}

	@Override
	public Serializable save(Session ses, EvasioniFascicoli6 transientInstance)
			throws HibernateException {
		return GenericDao.saveGeneric(ses, transientInstance);
	}

	@Override
	public void delete(Session ses, EvasioniFascicoli6 instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}
	
	//@SuppressWarnings("unchecked")
	//public List<EvasioniFascicoli6> findByIstanza(Session ses, IstanzeAbbonamenti istanza)
	//		throws HibernateException {
	//	//Domanda: esistono istanze successive a quella in esame?
	//	boolean isLast = true;
	//	List<IstanzeAbbonamenti> istList = new IstanzeAbbonamentiDao()
	//			.findIstanzeByAbbonamento(ses, istanza.getAbbonamento().getId());
	//	for (IstanzeAbbonamenti ia:istList) {
	//		if (ia.getFascicoloInizio().getDataNominale().after(istanza.getFascicoloInizio().getDataNominale())) {
	//			isLast = false;
	//		}
	//	}
	//	QueryFactory qf = new QueryFactory(ses, "from EvasioniFascicoli6 ef");
	//	qf.addWhere("(ef.idAbbonamento = :p1 and ef.fascicolo.dataNominale >= :p2) " +
	//			"or (ef.idIstanzaAbbonamento = :p3)");
	//	qf.addParam("p1", istanza.getAbbonamento().getId());
	//	qf.addParam("p2", istanza.getFascicoloInizio().getDataNominale());
	//	qf.addParam("p3", istanza.getId());
	//	if (!isLast) {// se non è l'ultima istanza della serie allora si ferma al numero prima della fine
	//		qf.addWhere("ef.fascicolo.dataNominale <= :p4");
	//		qf.addParam("p4", istanza.getFascicoloFine().getDataNominale());
	//	}
	//	qf.addOrder("ef.fascicolo.dataNominale asc");
	//	Query q = qf.getQuery();
	//	List<EvasioniFascicoli6> cList = (List<EvasioniFascicoli6>) q.list();
	//	return cList;
	//}
	
	@SuppressWarnings("unchecked")
	public List<EvasioniFascicoli6> findByIstanza(Session ses, IstanzeAbbonamenti istanza)
			throws HibernateException {
		String qs = "from EvasioniFascicoli6 ef " +
				"where ef.idIstanzaAbbonamento = :id1 " +
				"order by ef.fascicolo.dataInizio asc";
		Query q = ses.createQuery(qs);
		q.setParameter("id1", istanza.getId(), IntegerType.INSTANCE);
		List<EvasioniFascicoli6> cList = (List<EvasioniFascicoli6>) q.list();
		return cList;
	}
	
	@SuppressWarnings("unchecked")
	public List<EvasioniFascicoli6> findByAbbonamento(Session ses, Abbonamenti abb)
			throws HibernateException {
		QueryFactory qf = new QueryFactory(ses, "from EvasioniFascicoli6 ef");
		qf.addWhere("ef.idAbbonamento = :p1");
		qf.addParam("p1", abb.getId());
		qf.addOrder("ef.fascicolo.dataInizio asc");
		Query q = qf.getQuery();
		List<EvasioniFascicoli6> cList = (List<EvasioniFascicoli6>) q.list();
		return cList;
	}
	
	@SuppressWarnings("unchecked")
	public List<EvasioniFascicoli6> findByAnagrafica(Session ses, Integer idAnagrafica)
			throws HibernateException {
		QueryFactory qf = new QueryFactory(ses, "from EvasioniFascicoli6 ef");
		qf.addWhere("ef.idAnagrafica = :p1");
		qf.addParam("p1", idAnagrafica);
		qf.addOrder("ef.fascicolo.dataInizio asc");
		Query q = qf.getQuery();
		List<EvasioniFascicoli6> cList = (List<EvasioniFascicoli6>) q.list();
		return cList;
	}
	
	@SuppressWarnings("unchecked")
	public List<EvasioniFascicoli6> findByNumeroOrdine(Session ses, String numeroOrdine)
			throws HibernateException {
		String hql = "from EvasioniFascicoli6 ef where "+
			"ef.ordiniLogistica.numeroOrdine = :s1 " +
			"order by ef.id asc";
		Query q = ses.createQuery(hql);
		q.setParameter("s1", numeroOrdine, StringType.INSTANCE);
		List<EvasioniFascicoli6> efList = (List<EvasioniFascicoli6>) q.list();
		return efList;
	}
	
	@SuppressWarnings("unchecked")
	public List<EvasioniFascicoli6> findPending(Session ses, Integer idPeriodico)
			throws HibernateException {
		String hql =  "from EvasioniFascicoli6 ef where "+
				"ef.fascicolo.periodico.id = :id1 and " +
				"ef.dataInvio is null and ef.dataOrdine is null and " +
				"ef.fascicolo.inAttesa = :b1 " +//Non deve avere l'invio arretrato sospeso
				"order by ef.copie desc, ef.fascicolo.dataInizio asc";
		Query q = ses.createQuery(hql);
		q.setParameter("id1", idPeriodico, IntegerType.INSTANCE);
		q.setParameter("b1", Boolean.FALSE);
		List<EvasioniFascicoli6> cList = (List<EvasioniFascicoli6>) q.list();
		return cList;
	}
	
	@SuppressWarnings("unchecked")
	public List<EvasioniFascicoli6> enqueueMissingArretratiByStatus(Session ses, IstanzeAbbonamenti ia,
			String idUtente) throws HibernateException {
		Periodici p = ia.getAbbonamento().getPeriodico();
		//Pagato?
		boolean spedibile = IstanzeStatusUtil.isSpedibile(ia);
		//Bloccato?
		boolean bloccato = ia.getInvioBloccato();
		//Cartaceo
		boolean cartaceo = ia.getListino().getCartaceo();
		//Calcolo ultimo fascicolo arretrato a cui ha diritto
		Fascicoli6 maxFascicolo = ia.getFascicoloFine6();
		if (ia.getDataDisdetta() == null) {	//Senza disdetta
			maxFascicolo = new Fascicoli6Dao().findFascicoliAfterFascicolo(ses,
					ia.getFascicoloFine6(), ia.getListino().getGracingFinale6());
		}
		//Fascicoli6 (+opzionali) nel periodo d'interesse
		QueryFactory qf1 = new QueryFactory(ses, "from Fascicoli6 f");
		qf1.addWhere("f.periodico.id = :id1");
		qf1.addParam("id1", p.getId());
		qf1.addWhere("f.dataInizio >= :d1");
		qf1.addParam("d1", ia.getFascicoloInizio6().getDataInizio());
		qf1.addWhere("f.dataEstrazione is not null");
		qf1.addWhere("f.dataInizio <= :d2");
		qf1.addTimestampParam("d2", maxFascicolo.getDataInizio());
		qf1.addOrder("f.dataInizio asc");
		Query q1 = qf1.getQuery();
		List<Fascicoli6> fList = (List<Fascicoli6>) q1.list();
		List<EvasioniFascicoli6> efList = new ArrayList<EvasioniFascicoli6>();
		if (fList != null) {
			if (fList.size() > 0) {
				//Arretrati già programmati o spediti presenti in fList
				String efHql = "from EvasioniFascicoli6 ef where " +
						"ef.idAbbonamento = :p1 and " +
						"ef.fascicolo in (:fascList) " +
						"order by ef.fascicolo.dataInizio asc";
				Query efQ = ses.createQuery(efHql);
				efQ.setInteger("p1", ia.getAbbonamento().getId());
				efQ.setParameterList("fascList", fList);
				efList = (List<EvasioniFascicoli6>) efQ.list();
			}
		}
		//Ricerca arretrati e opzioni mancanti e crea una lista di quelli da creare
		List<EvasioniFascicoli6> listToSend = new ArrayList<EvasioniFascicoli6>();
		int fasCount = 0;
		for (Fascicoli6 f:fList) {
			//Considera il fascicolo se non è un opzione 
			//se è opzione lo considera solo se fa parte dei opzioni dell'istanza
			boolean fascicoloIsOpzione = (f.getOpzione() != null);
			if (ia.getOpzioniIstanzeAbbonamentiSet() == null) ia.setOpzioniIstanzeAbbonamentiSet(new HashSet<OpzioniIstanzeAbbonamenti>());
			boolean selectedOpzione = false;
			for (OpzioniIstanzeAbbonamenti oia:ia.getOpzioniIstanzeAbbonamentiSet()) {
				if (oia.getOpzione().equals(f.getOpzione())) selectedOpzione = true;
			}
			if ( !fascicoloIsOpzione || (fascicoloIsOpzione && selectedOpzione) ) {
				boolean found = false;
				//Cerca se esiste già un EvasioniFascicolo per questo Fascicolo
				for (EvasioniFascicoli6 ef:efList) {
					if (f.getId().intValue() == ef.getFascicolo6().getId().intValue()) {
						found = true;
						if (!fascicoloIsOpzione) fasCount += f.getFascicoliAccorpati();
						break;
					}
				}
				//Il fascicolo è aggiunto se: 1) non è già nell'elenco 2) l'abbonamento non è bloccato
				// 3) l'istanza è cartacea, altrimenti solo se è un opzione 
				if (!found && !bloccato && (cartaceo || fascicoloIsOpzione)) {
					//ef non c'è e dovrebbe essere creato (alle seguenti condizioni)
					if (spedibile || ia.getListino().getInvioSenzaPagamento() ||
							(fasCount < ia.getListino().getGracingIniziale6())) {
						EvasioniFascicoli6 newEf = createEvasioneFromFascicolo(ia, f, idUtente);
						listToSend.add(newEf);
						if (!fascicoloIsOpzione) fasCount += f.getFascicoliAccorpati();
					}
				}
			}
		}
		List<EvasioniFascicoli6> result = new ArrayList<EvasioniFascicoli6>();
		for (EvasioniFascicoli6 trans:listToSend) {
			Integer id = (Integer) ses.save(trans);
			EvasioniFascicoli6 persist = (EvasioniFascicoli6) ses.get(EvasioniFascicoli6.class, id);
			result.add(persist);
		}
		updateFascicoliSpediti(ses, ia);
		return result;
	}
	private EvasioniFascicoli6 createEvasioneFromFascicolo(IstanzeAbbonamenti ia,
			Fascicoli6 f, String idUtente) {
		EvasioniFascicoli6 ef = new EvasioniFascicoli6();
		//Evasione di un fascicolo
		ef.setDataCreazione(DateUtil.now());
		ef.setDataModifica(DateUtil.now());
		ef.setDataInvio(null);
		ef.setDataOrdine(null);
		ef.setFascicolo6(f);
		ef.setIdAbbonamento(ia.getAbbonamento().getId());
		ef.setIdIstanzaAbbonamento(ia.getId());
		ef.setIdAnagrafica(ia.getAbbonato().getId());
		ef.setIdTipoEvasione(AppConstants.EVASIONE_FAS_INIZIO_ABBONAMENTO);
		ef.setCopie(ia.getCopie());
		ef.setIdUtente(idUtente);
		return ef;
	}
	
	public void reattachEvasioniFascicoli6ToIstanza(Session ses, IstanzeAbbonamenti ia) throws HibernateException {
		Fascicoli6 fasInizio = ia.getFascicoloInizio6();
		Fascicoli6 fasFine = ia.getFascicoloFine6();
		//Associa EvasioniFascicoli6 in gracing al nuovo abbonamento
		List<EvasioniFascicoli6> efList = findByAbbonamento(ses, ia.getAbbonamento());
		for (EvasioniFascicoli6 ef:efList) {
			//Se il fascicolo >= fascicolo iniziale
			if(fasInizio.getDataInizio().before(ef.getFascicolo6().getDataInizio()) ||
					fasInizio.getId().equals(ef.getFascicolo6().getId())) {
				//Se il fascicolo <= fascicolo finale
				if(fasFine.getDataFine().after(ef.getFascicolo6().getDataInizio()) ||
						fasFine.getId().equals(ef.getFascicolo6().getId())) {
					ef.setIdAbbonamento(ia.getAbbonamento().getId());
					ef.setIdIstanzaAbbonamento(ia.getId());
					ef.setIdAnagrafica(ia.getAbbonato().getId());
					update(ses, ef);
				}
			}
		}
	}
	
	public void updateFascicoliSpediti(Session ses, Integer idIstanza)
			throws HibernateException {
		IstanzeAbbonamenti ia = (IstanzeAbbonamenti) ses.get(IstanzeAbbonamenti.class, idIstanza);
		updateFascicoliSpediti(ses, ia);
	}
	public void updateFascicoliSpediti(Session ses, IstanzeAbbonamenti istanza)
			throws HibernateException {
		int spediti = countFascicoliSpediti(ses, istanza);
		istanza.setFascicoliSpediti6(spediti);
		new IstanzeAbbonamentiDao().updateUnlogged(ses, istanza);
	}
	
	public Integer countFascicoliSpediti(Session ses, Integer idIstanza)
			throws HibernateException {
		IstanzeAbbonamenti ia = (IstanzeAbbonamenti) ses.get(IstanzeAbbonamenti.class, idIstanza);
		return countFascicoliSpediti(ses, ia);
	}
	@SuppressWarnings("unchecked")
	public Integer countFascicoliSpediti(Session ses, IstanzeAbbonamenti istanza)
			 throws HibernateException {
		int spediti = 0;
		if (istanza.getListino().getCartaceo()) {
			//Cartaceo
			String queryString = "from EvasioniFascicoli6 ef where "+
				//"ef.fascicolo.opzione is null and " + //Gli accorpati per i opzioni tanto sono 0
				"ef.idIstanzaAbbonamento = :p1";
			Query q = ses.createQuery(queryString);
			q.setParameter("p1", istanza.getId(), IntegerType.INSTANCE);
			List<EvasioniFascicoli6> fList = (List<EvasioniFascicoli6>) q.list();
			for (EvasioniFascicoli6 f:fList) {
				if (!f.getIdTipoEvasione().equals(AppConstants.EVASIONE_FAS_ARRETRATO)) {
					spediti+=f.getFascicolo6().getFascicoliAccorpati();
				}
			}
		} else {
			if (istanza.getListino().getDigitale()) {
				//Digitale
				spediti = 0;
				//String queryString = "from Fascicoli6 f where "+
				//	"f.opzione is null and "+
				//	"f.dataNominale >= :dt1 and "+
				//	"f.dataNominale <= :dt2 and "+
				//	"f.dataEstrazioneEffettiva is not null";
				//Query q = ses.createQuery(queryString);
				//q.setParameter("dt1", istanza.getFascicoloInizio().getDataNominale(), DateType.INSTANCE);
				//q.setParameter("dt2", istanza.getFascicoloFine().getDataNominaleFine(), DateType.INSTANCE);
				//List<Fascicoli6> fList = (List<Fascicoli6>) q.list();
				//for (Fascicoli6 f:fList) {
				//	spediti+=f.getFascicoliAccorpati();
				//}
			}
		}
		return spediti;
	}
	
	public Integer countFascicoliTotali(Session ses, Integer idIstanza)
			throws HibernateException {
		Integer result = null;
		IstanzeAbbonamenti ia = (IstanzeAbbonamenti) ses.get(IstanzeAbbonamenti.class, idIstanza);
		Fascicoli6 fasInizio = ia.getFascicoloInizio6();
		Fascicoli6 fasFine = ia.getFascicoloFine6();
		Integer idPeriodico = fasInizio.getPeriodico().getId();
		List<Fascicoli6> fList = null;
		if ((fasInizio != null) && (fasFine != null)) {
			fList = new Fascicoli6Dao().findFascicoliBetweenDates(ses, idPeriodico,
					fasInizio.getDataInizio(), fasFine.getDataInizio());
			int totFascicoli = 0;
			for (Fascicoli6 fas:fList) {
				totFascicoli += fas.getFascicoliAccorpati();
			}
			result = totFascicoli;
		} else {
			throw new HibernateException("Il fascicolo di inizio o fine e' null per istanza "+idIstanza);
		}
		return result;
	}
	
	public Integer countFascicoliDaSpedire(Session ses, Integer idIstanza) 
			throws HibernateException {
		Integer result = null;
		Integer spediti = countFascicoliSpediti(ses, idIstanza);
		Integer totali = countFascicoliTotali(ses, idIstanza);
		if ( (spediti != null) && (totali != null) ) {
			result = totali - spediti;
		}
		if (result != null) {
			if (result < 0) result = 0;
		}
		return result;
	}
	
	/** returns true if Fascicolo has been sent for given IstanzaAbbonamenti
	 * @param ses
	 * @param istanza
	 * @param idFascicolo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean checkFascicoloIstanza(Session ses, IstanzeAbbonamenti istanza,
			Integer idFascicolo) throws HibernateException {
		String qs = "from EvasioniFascicoli6 ef where " +
				"(ef.idIstanzaAbbonamento = :p1 or ef.idAbbonamento = :p2) and " +
				"ef.fascicolo.id = :p3";
		Query q = ses.createQuery(qs);
		q.setInteger("p1", istanza.getId());
		q.setInteger("p2", istanza.getAbbonamento().getId());
		q.setInteger("p3", idFascicolo);
		List<EvasioniFascicoli6> fList = (List<EvasioniFascicoli6>) q.list();
		if (fList == null) return false;
		if (fList.size() > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	
	//metodi con SQL
	
	
	public void sqlInsert(Session ses, IstanzeAbbonamenti ia, Fascicoli6 fascicolo,
			Date day, Utenti utente) throws HibernateException {
		String sql = "insert into evasioni_fascicoli(" +
					"data_creazione, data_modifica, data_invio, id_fascicolo, " +
					"id_abbonamento, id_istanza_abbonamento, id_anagrafica, " +
					"id_tipo_evasione, copie, id_utente" +
				") values(" +
					":d1, :d2, :d3, :id1, " +
					":id2, :id3, :id4, " +
					":id5, :i1, :id6 " +
				")";
		Query q = ses.createSQLQuery(sql);
		q.setDate("d1", day);
		q.setDate("d2", day);
		q.setDate("d3", day);
		q.setInteger("id1", fascicolo.getId());
		q.setInteger("id2", ia.getAbbonamento().getId());
		q.setInteger("id3", ia.getId());
		q.setInteger("id4", ia.getAbbonato().getId());
		q.setString("id5", AppConstants.EVASIONE_FAS_REGOLARE);
		q.setInteger("i1", ia.getCopie());
		q.setString("id6", utente.getId());
		q.executeUpdate();
	}
}
