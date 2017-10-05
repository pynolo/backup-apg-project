package it.giunti.apg.core.persistence;

import it.giunti.apg.core.business.PagamentiMatchBusiness;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Listini;
import it.giunti.apg.shared.model.Opzioni;
import it.giunti.apg.shared.model.OpzioniIstanzeAbbonamenti;
import it.giunti.apg.shared.model.OpzioniListini;
import it.giunti.apg.shared.model.Pagamenti;
import it.giunti.apg.shared.model.Periodici;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.DateType;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;

public class PagamentiDao implements BaseDao<Pagamenti> {
	
	@Override
	public void update(Session ses, Pagamenti instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
		//if (instance.getIstanzaAbbonamento() != null) {
		//	updateIstanzaArretratiOnSumPagamenti(ses,
		//			instance.getIstanzaAbbonamento(),
		//			instance.getUtente(),
		//			true);
		//}
		EditLogDao.writeEditLog(ses, Pagamenti.class, instance.getId(), instance.getIdUtente());
	}

	public void updateNoLog(Session ses, Pagamenti instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
		//if (instance.getIstanzaAbbonamento() != null) {
		//	updateIstanzaArretratiOnSumPagamenti(ses,
		//			instance.getIstanzaAbbonamento(),
		//			instance.getUtente(),
		//			false);
		//}
	}
	
	@Override
	public Serializable save(Session ses, Pagamenti transientInstance)
			throws HibernateException {
		Integer id = (Integer)GenericDao.saveGeneric(ses, transientInstance);
		//if (transientInstance.getIstanzaAbbonamento() != null) {
		//	updateIstanzaArretratiOnSumPagamenti(ses,
		//			transientInstance.getIstanzaAbbonamento(),
		//			transientInstance.getUtente(),
		//			true);
		//}
		EditLogDao.writeEditLog(ses, Pagamenti.class, id, transientInstance.getIdUtente());
		return id;
	}

	@Override
	public void delete(Session ses, Pagamenti instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}
	
	@SuppressWarnings("unchecked")
	public List<Pagamenti> findPagamentiByIstanzaAbbonamento(Session ses,
			Integer idIstanzaAbbonamento) throws HibernateException {
		QueryFactory qf = new QueryFactory(ses, "from Pagamenti p");
		qf.addWhere("p.istanzaAbbonamento.id = :id1");
		qf.addParam("id1", idIstanzaAbbonamento);
		qf.addOrder("p.dataPagamento asc");
		Query q = qf.getQuery();
		List<Pagamenti> pList = (List<Pagamenti>) q.list();
		return pList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Pagamenti> findByAnagrafica(Session ses, 
			Integer idAnagrafica, Boolean fatturati, Boolean abbinati) throws HibernateException {
		QueryFactory qf = new QueryFactory(ses, "from Pagamenti p");
		qf.addWhere("p.anagrafica.id = :id1");
		qf.addParam("id1", idAnagrafica);
		if (fatturati != null) {
			if (fatturati) {
				qf.addWhere("p.idFattura is not null");
			} else {
				qf.addWhere("p.idFattura is null");
			}
		}
		if (abbinati != null) {
			if (abbinati) {
				qf.addWhere("p.istanzaAbbonamento is not null");
			} else {
				qf.addWhere("p.istanzaAbbonamento is null");
			}
		}
		qf.addOrder("p.dataAccredito desc");
		Query q = qf.getQuery();
		List<Pagamenti> pList = (List<Pagamenti>) q.list();
		return pList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Pagamenti> findPagamentiConErrore(Session ses, 
			Integer idPeriodico, int offset, int pageSize) throws HibernateException {
		Periodici per = GenericDao.findById(ses, Periodici.class, idPeriodico);
		String hql = "from Pagamenti p where "+
				"p.codiceAbbonamentoMatch like :s1 and "+//Filtro periodico
				"((p.idErrore is not null) or (p.istanzaAbbonamento is null) or (p.idFattura is null)) and "+
				"p.idFattura is null "+
				"order by p.dataPagamento desc";
		Query q = ses.createQuery(hql);
		q.setParameter("s1", per.getUid()+"%");
		q.setMaxResults(pageSize);
		q.setFirstResult(offset);
		List<Pagamenti> pList = (List<Pagamenti>) q.list();
		return pList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Pagamenti> findPagamentiByIdFattura(Session ses, 
			Integer idFattura) throws HibernateException {
		String hql = "from Pagamenti p where "+
				"p.idFattura = :id1 "+
				"order by p.dataPagamento desc";
		Query q = ses.createQuery(hql);
		q.setParameter("id1", idFattura, IntegerType.INSTANCE);
		List<Pagamenti> pList = (List<Pagamenti>) q.list();
		return pList;
	}
	
	//@SuppressWarnings("unchecked")
	//public List<Pagamenti> findPagamentiNonRegistrati(Session ses, 
	//		Integer idPeriodico, int offset, int pageSize) throws HibernateException {
	//	Periodici per = GenericDao.findById(ses, Periodici.class, idPeriodico);
	//	QueryFactory qf = new QueryFactory(ses, "from Pagamenti p");
	//	qf.addWhere("p.codiceAbbonamentoMatch like :s1");
	//	qf.addParam("s1", per.getUid()+"%");
	//	qf.addWhere("p.idFattura is null");
	//	qf.addWhere("p.istanzaAbbonamento is null");
	//	qf.addOrder("p.dataPagamento asc");
	//	Query q = qf.getQuery();
	//	q.setMaxResults(pageSize);
	//	q.setFirstResult(offset);
	//	List<Pagamenti> pList = (List<Pagamenti>) q.list();
	//	return pList;
	//}
	
	@SuppressWarnings("unchecked")
	public List<Pagamenti> findByIstanzaAbbonamento(Session ses, 
			Integer idIa) throws HibernateException {
		QueryFactory qf = new QueryFactory(ses, "from Pagamenti p");
		qf.addWhere("p.istanzaAbbonamento.id = :id1");
		qf.addParam("id1", idIa);
		qf.addOrder("p.dataPagamento asc");
		Query q = qf.getQuery();
		List<Pagamenti> pList = (List<Pagamenti>) q.list();
		return pList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Pagamenti> findPagamentiByDateImportoCodiceTipo(Session ses,
				Date dataPagamento, Double importo, String codiceAbbonamento, String idTipoPagamento) throws HibernateException {
		//Query
		String qs = "from Pagamenti p where " +
				"p.dataPagamento = :dt1 and " +
				"p.importo = :d1 and " +
				"(p.codiceAbbonamentoBollettino like :s11 or p.codiceAbbonamentoMatch like :s12) and " +
				"p.idTipoPagamento like :s2 ";
		Query q = ses.createQuery(qs);
		q.setParameter("dt1", dataPagamento, DateType.INSTANCE);
		q.setParameter("d1", importo, DoubleType.INSTANCE);
		q.setParameter("s11", "%"+codiceAbbonamento, StringType.INSTANCE);
		q.setParameter("s12", "%"+codiceAbbonamento, StringType.INSTANCE);
		q.setParameter("s2", idTipoPagamento, StringType.INSTANCE);
		List<Pagamenti> pList = (List<Pagamenti>) q.list();
		return pList;
	}
	
	@SuppressWarnings("unchecked")
	public Double sumPagamentiByIstanza(Session ses, Integer idIstanza) throws HibernateException {
		if (idIstanza == null) return 0D;
		Double result = 0D;
		String qs = "from Pagamenti as p where " +
			"p.istanzaAbbonamento.id = :p1";
		Query q = ses.createQuery(qs);
		q.setInteger("p1", idIstanza);
		List<Pagamenti> list = (List<Pagamenti>) q.list();
		if (list != null) {
			if (list.size() > 0) {
				result = PagamentiMatchBusiness.getTotalAmount(list, null);
			}
		}
		return result;
	}
	
	////@SuppressWarnings("unchecked")
	//public Double getImportoTotale(Session ses, Integer idIstanza) throws HibernateException {
	//	if (idIstanza == null) return null;
	//	IstanzeAbbonamenti ia =  GenericDao.findById(ses, IstanzeAbbonamenti.class, idIstanza);
	//	//Set<Integer> idSuppSet = new OpzioniUtil(ia.getOpzioniSet()).getOpzioniIdList();
	//	//Double result = ia.getTipoAbbonamentoListino().getPrezzo();
	//	//for (Integer idSupp:idSuppSet) {
	//	//	Opzioni opz = GenericDao.findById(ses, Opzioni.class, idSupp);
	//	//	result += opz.getPrezzo();
	//	//}
	//	Double result = ia.getListino().getPrezzo();
	//	if (result >= AppConstants.SOGLIA) {
	//		for (OpzioniIstanzeAbbonamenti oia:ia.getOpzioniIstanzeAbbonamentiSet()) {
	//			boolean mandatory = false;
	//			for (OpzioniListini ol:ia.getListino().getOpzioniListiniSet()) {
	//				if (oia.getOpzione().getId() == ol.getOpzione().getId()) mandatory = true;
	//			}
	//			if (!mandatory) result += oia.getOpzione().getPrezzo();
	//		}
	//		result *= ia.getCopie();
	//	}
	//	return result;
	//}
	
	//@SuppressWarnings("unchecked")
	public Double getStimaImportoTotale(Session ses, Integer idListino,
			Integer copie, Set<Integer> idOpzSet) throws HibernateException {
		Listini lst = GenericDao.findById(ses, Listini.class, idListino);
		Double result = lst.getPrezzo();
		if (result >= AppConstants.SOGLIA && idOpzSet != null) {
			for (Integer idOpz:idOpzSet) {
				boolean mandatory = false;
				for (OpzioniListini ol:lst.getOpzioniListiniSet()) {
					if (idOpz == ol.getOpzione().getId()) mandatory = true;
				}
				if (!mandatory) {
					Opzioni opz = GenericDao.findById(ses, Opzioni.class, idOpz);
					result += opz.getPrezzo();
				}
			}
			result *= copie;
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public Date findLastDataPagamento(Session ses, Integer idIstanza) throws HibernateException {
		if (idIstanza == null) return null;
		Date result = null;
		String qs = "from Pagamenti as p where " +
			"p.istanzaAbbonamento = :p1 " +
			"order by p.dataPagamento desc";
		Query q = ses.createQuery(qs);
		q.setInteger("p1", idIstanza);
		List<Pagamenti> list = (List<Pagamenti>) q.list();
		if (list != null) {
			if (list.size() > 0) {
				result = list.get(0).getDataPagamento();
			}
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public Date findLastDataAccredito(Session ses, Integer idIstanza) throws HibernateException {
		if (idIstanza == null) return null;
		Date result = null;
		String qs = "from Pagamenti as p where " +
			"p.istanzaAbbonamento = :p1 and " +
			"p.dataAccredito is not null " +
			"order by p.dataAccredito desc ";
		Query q = ses.createQuery(qs);
		q.setInteger("p1", idIstanza);
		List<Pagamenti> list = (List<Pagamenti>) q.list();
		if (list != null) {
			if (list.size() > 0) {
				result = list.get(0).getDataAccredito();
			}
		}
		return result;
	}
	
	public void updateIstanzaArretratiOnSumPagamenti(Session ses, IstanzeAbbonamenti ia,
				String idUtente, boolean writeLog) throws HibernateException {
		double sum = sumPagamentiByIstanza(ses, ia.getId());
		double dovuto = ia.getListino().getPrezzo();
		Date today = DateUtil.now();
		//Elenco opzioni incluse (obbligatorie)
		List<Opzioni> mandatoryOpz = new ArrayList<Opzioni>();
		if (ia.getListino().getOpzioniListiniSet() != null) {
			for (OpzioniListini ol:ia.getListino().getOpzioniListiniSet()) {
				mandatoryOpz.add(ol.getOpzione());
			}
		}
		//Scorre tutte le opzioni ma somma le non-incluse
		if (ia.getOpzioniIstanzeAbbonamentiSet() != null) {
			for (OpzioniIstanzeAbbonamenti oia:ia.getOpzioniIstanzeAbbonamentiSet()) {
				boolean mandatory = false;
				for (Opzioni opz:mandatoryOpz) {
					if (opz.equals(oia.getOpzione())) mandatory = true;
				}
				if (!mandatory) dovuto += oia.getOpzione().getPrezzo();
			}
		}
		dovuto *= ia.getCopie();
		boolean pagato = false;
		//in origine: if ( ((dovuto-delta)<sum) && ((dovuto+delta)>sum) ) {
		if ((dovuto-AppConstants.SOGLIA)<sum) {
			pagato = true;
		}
		if (pagato && (ia.getDataSaldo() == null)) {
			ia.setDataSaldo(today);
		}
		if (pagato && !ia.getPagato()) {
			//Passa da pagato=false a pagato=true
			ia.setPagato(pagato);
		}
		ia.setDataModifica(today);
		ia.setIdUtente(idUtente);
		if (writeLog) {
			new IstanzeAbbonamentiDao().update(ses, ia);
		} else {
			new IstanzeAbbonamentiDao().updateUnlogged(ses, ia);
		}
		//Nel caso che sia omaggio, fatturato, scolastico o
		//pagato genera i fascicoli arretrati
		new EvasioniFascicoliDao().enqueueMissingArretratiByStatus(ses, ia, today, idUtente);
	}
	
	public void switchToPagatoAfterCheck(Session ses, IstanzeAbbonamenti ia, String idUtente,
			boolean writeLog) throws HibernateException {
		if (ia.getPagato()) throw new HibernateException("Istanza "+ia.getId()+" is already paid");
		double sum = sumPagamentiByIstanza(ses, ia.getId());
		double dovuto = ia.getListino().getPrezzo();
		Date today = DateUtil.now();
		//Elenco opzioni incluse (obbligatorie)
		List<Opzioni> mandatoryOpz = new ArrayList<Opzioni>();
		if (ia.getListino().getOpzioniListiniSet() != null) {
			for (OpzioniListini ol:ia.getListino().getOpzioniListiniSet()) {
				mandatoryOpz.add(ol.getOpzione());
			}
		}
		//Scorre tutte le opzioni ma somma le non-incluse
		if (ia.getOpzioniIstanzeAbbonamentiSet() != null) {
			for (OpzioniIstanzeAbbonamenti oia:ia.getOpzioniIstanzeAbbonamentiSet()) {
				boolean mandatory = false;
				for (Opzioni opz:mandatoryOpz) {
					if (opz.equals(oia.getOpzione())) mandatory = true;
				}
				if (!mandatory) dovuto += oia.getOpzione().getPrezzo();
			}
		}
		dovuto *= ia.getCopie();
		boolean pagato = false;
		//in origine: if ( ((dovuto-delta)<sum) && ((dovuto+delta)>sum) ) {
		if ((dovuto-AppConstants.SOGLIA)<sum) {
			pagato = true;
		}
		if (pagato && (ia.getDataSaldo() == null)) {
			ia.setDataSaldo(today);
		}
		if (pagato) {
			ia.setPagato(pagato);//cannot change from true to false
		}
		ia.setDataModifica(today);
		ia.setIdUtente(idUtente);
		if (writeLog) {
			new IstanzeAbbonamentiDao().update(ses, ia);
		} else {
			new IstanzeAbbonamentiDao().updateUnlogged(ses, ia);
		}
	}
	
//	@SuppressWarnings("unchecked")
//	public List<Pagamenti> findPagamentiCorrezioniAttachingIstanza(Session ses,
//			Integer idPeriodico, boolean showCreditList, int offset, int pageSize) throws HibernateException {
//		//Query
//		IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
//		Periodici periodico = GenericDao.findById(ses, Periodici.class, idPeriodico);
//		QueryFactory qf = new QueryFactory(ses, "from Pagamenti p");
//		qf.addWhere("p.codiceAbbonamentoMatch like :s1");
//		qf.addParam("s1", periodico.getUid()+"%");
//		qf.addWhere("p.idErrore is not null");
//		if (showCreditList) {
//			qf.addWhere("(p.idTipoPagamento like :s2 or p.idErrore like :s3)");
//		} else {
//			qf.addWhere("(p.idTipoPagamento not like :s2 and p.idErrore not like :s3)");
//		}
//		qf.addParam("s2", AppConstants.PAGAMENTO_CREDITO_RESIDUO);
//		qf.addParam("s3", AppConstants.PAGAMENTO_ERR_CREDITO_RESIDUO);
//		qf.addOrder("p.dataPagamento desc");
//		qf.setPaging(offset, pageSize);
//		Query q = qf.getQuery();
//		List<Pagamenti> pList = (List<Pagamenti>) q.list();
//		for (Pagamenti p:pList) {
//			IstanzeAbbonamenti ia = iaDao.findUltimaIstanzaByCodice(ses, p.getCodiceAbbonamentoMatch());
//			p.setIstanzaAbbonamento(ia);
//		}
//		return pList;
//	}
	
	//@SuppressWarnings("unchecked")
	//public List<Pagamenti> findPagamentiCorrezioniAttachingIstanza(Session ses, Integer idPeriodico,
	//		Date dataInizio, Date dataFine, int offset, int pageSize) throws HibernateException {
	//	//si sommano 24h alla data fine per includere i pagamenti effettuati nel giorno stesso
	//	Calendar cal = new GregorianCalendar();
	//	cal.setTime(dataFine);
	//	cal.add(Calendar.DAY_OF_MONTH, 1);
	//	dataFine = cal.getTime();
	//	//Query
	//	IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
	//	Periodici periodico = GenericDao.findById(ses, Periodici.class, idPeriodico);
	//	QueryFactory qf = new QueryFactory(ses, "from Pagamenti p");
	//	qf.addWhere("p.codiceAbbonamento like :p1");
	//	qf.addParam("p1", periodico.getLettera()+"%");
	//	qf.addWhere("p.dataPagamento >= :p2");
	//	qf.addParam("p2", dataInizio);
	//	qf.addWhere("p.dataPagamento <= :p3");
	//	qf.addParam("p3", dataFine);
	//	qf.addWhere("p.errore is not null");
	//	qf.addOrder("p.dataPagamento desc");
	//	qf.setPaging(offset, pageSize);
	//	Query q = qf.getQuery();
	//	List<Pagamenti> pList = (List<Pagamenti>) q.list();
	//	for (Pagamenti p:pList) {
	//		IstanzeAbbonamenti ia = iaDao.findIstanzaPiuRecenteByCodice(ses, p.getCodiceAbbonamento());
	//		p.setIstanzaAbbonamento(ia);
	//	}
	//	return pList;
	//}
	
//	/**
//	 * Cerca i crediti di abbonamenti in scadenza o da saldare
//	 * @param ses
//	 * @param idPeriodico
//	 * @param today
//	 * @return
//	 */
//	@SuppressWarnings("unchecked")
//	public List<Pagamenti> findCreditiAbbonamentiDaPagare(Session ses, Integer idPeriodico, Date today) {
//		Calendar cal = new GregorianCalendar();
//		//cal.setTime(today);
//		//cal.add(Calendar.DAY_OF_MONTH, -20);
//		//Date twentyDaysAgo = cal.getTime();
//		cal.setTime(today);
//		cal.add(Calendar.MONTH, 1);
//		Date oneMonthAhead = cal.getTime();
//		cal.setTime(today);
//		cal.add(Calendar.MONTH, -7);
//		Date sevenMonthsAgo = cal.getTime();
//		//Periodico
//		Periodici periodico = GenericDao.findById(ses, Periodici.class, idPeriodico);
//		//La ricerca effettuata richiede gli abbonamenti così:
//		///*rimosso* -> L'anagrafica ha CREDITI modificati prima di 10gg fa*/
//		//istanze PAGATI (IN SCADENZA) per cui la data nominale del fascicolo fine è prima di dt12 (1 mese nel futuro)
//		//istanze NON PAGATI con data inizio successiva a dt22 (7 mesi fa)
//		//INOLTRE abbina solo i pagamenti fatti per lo stesso periodico (o senza indicazione del codiceAbb originale)
//		String qs = "select pag, ia.abbonamento from IstanzeAbbonamenti ia, Pagamenti pag where "+
//				"ia.abbonato.id = pag.anagrafica.id and " +
//				"ia.abbonamento.periodico.id = :id1 and " +
//				"pag.istanzaAbbonamento is null and " +//pag è un credito
//				"ia.ultimaDellaSerie = :b01 and " +//TRUE
//				"ia.invioBloccato = :b02 and " +//FALSE
//				"(pag.codiceAbbonamentoBollettino like :s1 or pag.codiceAbbonamentoBollettino = :s2 or pag.codiceAbbonamentoBollettino is null) and"+
//				/*"pag.dataModifica < :dt01 and " +*///Pagamento inserito o corretto prima di 10 giorni fa
//				"( " +
//					"(ia.pagato = :b11 and ia.fascicoloFine.dataInizio <= :dt12) " +//l'ultimo numero esce prima di dt12
//				"or " +
//					"(ia.pagato = :b21 and ia.fascicoloInizio.dataInizio > :dt22)" +
//				")";
//		Query q = ses.createQuery(qs);
//		q.setParameter("id1", idPeriodico, IntegerType.INSTANCE);
//		q.setParameter("b01", Boolean.TRUE, BooleanType.INSTANCE);
//		q.setParameter("b02", Boolean.FALSE, BooleanType.INSTANCE);
//		q.setParameter("s1", periodico.getUid()+"%", StringType.INSTANCE);
//		q.setParameter("s2", "", StringType.INSTANCE);
//		//q.setParameter("dt01", twentyDaysAgo, DateType.INSTANCE);
//		q.setParameter("b11", Boolean.TRUE, BooleanType.INSTANCE);
//		q.setParameter("dt12", oneMonthAhead, DateType.INSTANCE);
//		q.setParameter("b21", Boolean.FALSE, BooleanType.INSTANCE);
//		q.setParameter("dt22", sevenMonthsAgo, DateType.INSTANCE);
//		List<Object[]> objList = (List<Object[]>) q.list();
//		List<Pagamenti> credList = new ArrayList<Pagamenti>();
//		for (Object[] couple:objList) {
//			//C'è un abbonamento da pagare e un credito residuo
//			//la procedura effettua un tentativo di abbinamento assegnando il codice
//			Pagamenti pag = (Pagamenti) couple[0];
//			Abbonamenti abb = (Abbonamenti) couple[1];
//			pag.setCodiceAbbonamentoMatch(abb.getCodiceAbbonamento());
//			credList.add(pag);
//		}
//		return credList;
//	}
	
}
