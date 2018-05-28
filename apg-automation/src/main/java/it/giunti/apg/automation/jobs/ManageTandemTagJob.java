package it.giunti.apg.automation.jobs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.giunti.apg.automation.business.EntityBusiness;
import it.giunti.apg.core.OpzioniUtil;
import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.VisualLogger;
import it.giunti.apg.core.business.FascicoliBusiness;
import it.giunti.apg.core.persistence.EvasioniFascicoliDao;
import it.giunti.apg.core.persistence.FascicoliDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.core.persistence.PeriodiciDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.Fascicoli;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Listini;
import it.giunti.apg.shared.model.Periodici;

public class ManageTandemTagJob implements Job {
	
	static private Logger LOG = LoggerFactory.getLogger(ManageTandemTagJob.class);

	private static final int PAGE_SIZE = 250;
	
	private static final String TAG_TANDEM1 = "TANDEM1";
	
	private Map<Integer, Listini> _periodiciTd2Map = new HashMap<Integer, Listini>();
	private Map<String, Periodici> _uidPeriodiciMap = new HashMap<String, Periodici>();
	private Map<String, Integer> _uidCountMap = new HashMap<String, Integer>();
	
	private IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
	private FascicoliDao fasDao = new FascicoliDao();
	private EvasioniFascicoliDao efDao = new EvasioniFascicoliDao();
	//private EvasioniArticoliDao eaDao = new EvasioniArticoliDao();
	
	@Override
	public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
		String jobName = jobCtx.getJobDetail().getKey().getName();
		//param: letterePeriodici
		String letterePeriodici = (String) jobCtx.getMergedJobDataMap().get("letterePeriodici");
		if (letterePeriodici == null) throw new JobExecutionException("letterePeriodici non definito");
		if (letterePeriodici.equals("")) throw new JobExecutionException("letterePeriodici non definito");
		String[] lettereArray = letterePeriodici.split(AppConstants.STRING_SEPARATOR);
		//param: backwardDays
		String backwardDaysString = (String) jobCtx.getMergedJobDataMap().get("backwardDays");
		Integer backwardDays = ValueUtil.stoi(backwardDaysString);
		if (backwardDays == null) throw new JobExecutionException("Non sono definiti i giorni di aggiornamento");
		
		int idRapporto;
		try {
			idRapporto = VisualLogger.get().createRapporto("Gestione abbinamenti Tandem", ServerConstants.DEFAULT_SYSTEM_USER);
		} catch (BusinessException e) {
			throw new JobExecutionException(e.getMessage(), e);
		}
		LOG.info("Started job '"+jobName+"'");
		Session ses = SessionFactory.getSession();
		Transaction trx = ses.beginTransaction();
		try {
			//Tandem1 map
			List<Periodici> perList = new PeriodiciDao().findByDate(ses, DateUtil.now());
			for (Periodici periodico:perList) {
				_uidPeriodiciMap.put(periodico.getUid(), periodico);
				_uidCountMap.put(periodico.getUid(), 0);
			}

			//Ricerca tutti i listini TD2
			List<Periodici> periodici = EntityBusiness.periodiciFromUidArray(ses, lettereArray);
			for (Periodici periodico:periodici) {
				//Listino target
				try {
					Listini lstTandem = findListinoTd2ByPeriodico(ses, periodico.getId(), DateUtil.now());
					_periodiciTd2Map.put(periodico.getId(), lstTandem);
				} catch (Exception e) {
					VisualLogger.get().addHtmlInfoLine(idRapporto, "Il periodico '"+periodico.getNome()+"' non ha un listino con tag "+AppConstants.TAG_TANDEM2);
				}
			}
			
			//Creazione TD2 a partire da TD1
			List<IstanzeAbbonamenti> tandemList = new ArrayList<IstanzeAbbonamenti>();
			List<IstanzeAbbonamenti> list = new ArrayList<IstanzeAbbonamenti>();
			int offset = 0;
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Ricerca richieste tandem in corso...");
			do {
				list = findTandem1List(ses, idRapporto, backwardDays, offset, PAGE_SIZE);
				tandemList.addAll(list);
				offset += list.size();
				VisualLogger.get().addHtmlInfoLine(idRapporto, "Trovati "+offset+" abbonamenti "+TAG_TANDEM1+"...");
			} while (list.size() > 0);
			if (tandemList.size() > 0) {
				VisualLogger.get().addHtmlInfoLine(idRapporto, "Attivazione tandem in corso...");
				createTandem(ses, idRapporto, tandemList);
			} else {
				VisualLogger.get().addHtmlInfoLine(idRapporto, "Nessuna attivazione necessaria");
			}
			trx.commit();
		} catch (Exception e) {
			VisualLogger.get().addHtmlErrorLine(idRapporto, "Errore", e);
			LOG.info("ERROR in job '"+jobName+"'");
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new JobExecutionException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		//Rapporto del totale attivazioni
		VisualLogger.get().addHtmlInfoLine(idRapporto, "Totale attivazioni:");
		for (String uid:_uidCountMap.keySet()) {
			Integer count = _uidCountMap.get(uid);
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Attivate "+count+" istanze per periodico "+uid);
		}
		try {
			VisualLogger.get().closeAndSaveRapporto(idRapporto);
		} catch (BusinessException e) {
			throw new JobExecutionException(e.getMessage(), e);
		}
		LOG.info("Ended job '"+jobName+"'");
	}
	
	@SuppressWarnings("unchecked")
	private List<IstanzeAbbonamenti> findTandem1List(Session ses, int idRapporto, 
			int backwardDays, int offset, int pageSize) throws BusinessException {
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DAY_OF_MONTH, -1*backwardDays);
		Date fromDay = cal.getTime();
		List<IstanzeAbbonamenti> iaList = null;
		try {
			//Tutti i Tandem1D con dataJob=null a prescindere dal pagamento
			String hql = "from IstanzeAbbonamenti ia where "+
					"ia.dataModifica >= :dt1 and "+// fromDay
					"ia.listino.tag like :tag1 and "+
					//"(ia.pagato = :b1 or ia.inFatturazione = :b2 or "+//true true
					//	"ia.listino.fatturaDifferita = :b3 or ia.listino.prezzo < :d1) and "+//true 0
					"ia.dataJob is null "+
					"order by ia.dataModifica asc";
			Query q = ses.createQuery(hql);
			q.setParameter("dt1", fromDay);
			q.setParameter("tag1", "%"+TAG_TANDEM1+"%");
			//q.setParameter("b1", Boolean.TRUE, BooleanType.INSTANCE);
			//q.setParameter("b2", Boolean.TRUE, BooleanType.INSTANCE);
			//q.setParameter("b3", Boolean.TRUE, BooleanType.INSTANCE);
			//q.setParameter("d1", AppConstants.SOGLIA, DoubleType.INSTANCE);
			q.setFirstResult(offset);
			q.setMaxResults(pageSize);
			iaList = q.list();
		} catch (HibernateException e) {
			throw new BusinessException(e.getMessage(), e);
		}
		return iaList;
	}
	
	@SuppressWarnings("unchecked")
	private Listini findListinoTd2ByPeriodico(Session ses, Integer idPeriodico, Date now)  throws BusinessException {
		List<Listini> lList = null;
		try {
			String hql = "from Listini lst where "+
					"lst.tipoAbbonamento.periodico.id = :id1 and "+
					"lst.dataInizio <= :dt1 and "+
					"(lst.dataFine >= :dt2 or lst.dataFine is null) and "+
					"lst.tag like :tag1 "+
					"order by lst.dataInizio desc";
			Query q = ses.createQuery(hql);
			q.setParameter("id1", idPeriodico, IntegerType.INSTANCE);
			q.setParameter("dt1", now, DateType.INSTANCE);
			q.setParameter("dt2", now, DateType.INSTANCE);
			q.setParameter("tag1", "%"+AppConstants.TAG_TANDEM2+"%", StringType.INSTANCE);
			lList = q.list();
		} catch (HibernateException e) {
			throw new BusinessException(e.getMessage(), e);
		}
		if (lList.size() == 0) 
			throw new BusinessException("Nessun listino attivo con tag "+AppConstants.TAG_TANDEM2);
		if (lList.size() > 1) 
			throw new BusinessException("Piu' di un listino attivo con tag "+AppConstants.TAG_TANDEM2);
		return lList.get(0);
	}
	
	private IstanzeAbbonamenti findTargetByAnagrafica(Session ses, Integer idAnagrafica, Integer idPeriodico)
			throws BusinessException {
		IstanzeAbbonamenti ia = null;
		try {
			//cerco l'istanza più recente e restituisco l'Abbonamento
			String hql = "from IstanzeAbbonamenti ia where "+
					"ia.abbonato.id = :id1 and "+
					"ia.fascicoloFine.periodico.id = :id2 "+
					"order by ia.fascicoloFine.dataFine desc";
			Query q = ses.createQuery(hql);
			q.setParameter("id1", idAnagrafica, IntegerType.INSTANCE);
			q.setParameter("id2", idPeriodico, IntegerType.INSTANCE);
			@SuppressWarnings("unchecked")
			List<IstanzeAbbonamenti> iaList = q.list();
			if (iaList.size() > 0) {
				ia = iaList.get(0);
			}
		} catch (HibernateException e) {
			throw new BusinessException(e.getMessage(), e);
		}
		return ia;
	}
	
	private String findTandem1Uid(String tagString) {
		int idx1 = tagString.indexOf(TAG_TANDEM1);
		int idx2 = idx1+TAG_TANDEM1.length();
		String uid = tagString.substring(idx2, idx2+1);
		return uid;
	}
	
	private void increaseCounter(String perUid) {
		Integer count = _uidCountMap.get(perUid);
		_uidCountMap.put(perUid, count+1);
	}
	
	private void createTandem(Session ses, int idRapporto,
			List<IstanzeAbbonamenti> iaList) throws BusinessException {
		Date now = DateUtil.now();
		for (IstanzeAbbonamenti ia:iaList) {
			//Quale tandem?
			String perUid = findTandem1Uid(ia.getListino().getTag());
			increaseCounter(perUid);
			Periodici perTd2 = _uidPeriodiciMap.get(perUid);
			Listini lstTd2 = _periodiciTd2Map.get(perTd2.getId());
			if (lstTd2 == null) throw new BusinessException("L'istanza "+ia.getAbbonamento().getCodiceAbbonamento()+
					" ["+ia.getId()+"] richiede un tandem con '"+perTd2.getNome()+"' che non ha un listino "+AppConstants.TAG_TANDEM2);
			
			//Anagrafiche benef = ia.getAbbonato();
			//IstanzeAbbonamenti existingIa = findTargetByAnagrafica(ses, benef.getId(), perTd2.getId());
			IstanzeAbbonamenti newIa;
			String action = "";
			//if (existingIa == null) {
				//L'abbonamento tandem2 non esiste, allora lo crea
				Integer idPagante = (ia.getPagante() == null ? null : ia.getPagante().getId());
				IstanzeAbbonamenti transientNewIa = new IstanzeAbbonamentiDao()
						.createAbbonamentoAndIstanza(ses,
						ia.getAbbonato().getId(), idPagante,
						null, lstTd2.getTipoAbbonamento().getPeriodico().getId(),
						lstTd2.getTipoAbbonamento().getCodice());
				transientNewIa.setIdUtente(ServerConstants.DEFAULT_SYSTEM_USER);
				transientNewIa.setCopie(ia.getCopie());
				transientNewIa.getAbbonamento().setIdUtente(ServerConstants.DEFAULT_SYSTEM_USER);
				
				Integer newIaId = iaDao.save(ses, transientNewIa, false);
				newIa = GenericDao.findById(ses, IstanzeAbbonamenti.class, newIaId);
			//} else {
			//	//L'abbonamento già esisteva e lo rigenera e ASSEGNA IL LISTINO
			//	IstanzeAbbonamenti transientNewIa = RinnovoBusiness.makeBasicTransientRenewal(ses,
			//			existingIa.getId(), false,
			//			ServerConstants.DEFAULT_SYSTEM_USER);
			//	transientNewIa.setCopie(ia.getCopie());
			//	Integer newIaId = iaDao.save(ses, transientNewIa, false);
			//	newIa = GenericDao.findById(ses, IstanzeAbbonamenti.class, newIaId);
			//	action = "vecchio abbonato";
			//}
			//Imposta listino e fascicolo inizio (quando inizia TD1)
			newIa.setListino(lstTd2);
			Fascicoli fascicoloInizio = fasDao.findFascicoloByPeriodicoDataInizio(ses,
					lstTd2.getTipoAbbonamento().getPeriodico().getId(),
					ia.getFascicoloInizio().getDataInizio());
			newIa.setFascicoloInizio(fascicoloInizio);
			//Fascicoli
			//efDao.reattachEvasioniFascicoliToIstanza(ses, newIa);
			//eaDao.reattachEvasioniArticoliToInstanza(ses, newIa, ServerConstants.DEFAULT_SYSTEM_USER);
			
			//Aggiusta i dettagli dell'istanza
			iaDao.markUltimaDellaSerie(ses, newIa.getAbbonamento());
			FascicoliBusiness.setupFascicoloFine(ses, newIa);
			newIa.setDataCambioTipo(DateUtil.now());
			newIa.setFascicoliTotali(lstTd2.getNumFascicoli());
			OpzioniUtil.addOpzioniObbligatorie(ses, newIa, false);
			efDao.enqueueMissingArretratiByStatus(ses, newIa, ServerConstants.DEFAULT_SYSTEM_USER);
			
			String note = "";
			if (newIa.getNote() != null) note = newIa.getNote()+" ";
			newIa.setNote(note+"Tandem con "+ia.getAbbonamento().getCodiceAbbonamento()+" ["+ia.getId()+"]");
			iaDao.update(ses, newIa);
			note = "";
			if (ia.getNote() != null) note = ia.getNote()+" ";
			ia.setNote(note+"Tandem con "+newIa.getAbbonamento().getCodiceAbbonamento()+" ["+newIa.getId()+"]");
			ia.setDataJob(now);
			iaDao.update(ses, ia);
			
			VisualLogger.get().addHtmlInfoLine(idRapporto, ia.getListino().getTipoAbbonamento().getCodice()+
					" "+ia.getAbbonamento().getCodiceAbbonamento()+" ["+ia.getId()+"] -> "+
					newIa.getListino().getTipoAbbonamento().getCodice()+
					" "+newIa.getAbbonamento().getCodiceAbbonamento()+" ["+newIa.getId()+"] "+action);
		}
	}
	
}
