package it.giunti.apg.automation.jobs;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.VisualLogger;
import it.giunti.apg.core.business.RinnovoBusiness;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Listini;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.type.BooleanType;
import org.hibernate.type.DateType;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManageTandemTagJob implements Job {
	
	static private Logger LOG = LoggerFactory.getLogger(ManageTandemTagJob.class);

	private static final int PAGE_SIZE = 250;
	
	private static final String ACTION_NUOVO = "nuovo";
	private static final String ACTION_RINNOVO = "rinnovo";
	
	private IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
	
	@Override
	public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
		String jobName = jobCtx.getJobDetail().getKey().getName();
		//param: backwardDays
		String backwardDaysString = (String) jobCtx.getMergedJobDataMap().get("backwardDays");
		Integer backwardDays = ValueUtil.stoi(backwardDaysString);
		if (backwardDays == null) throw new JobExecutionException("Non sono definiti i giorni di aggiornamento");
		//param: tandemRenewalMonths
		String tandemRenewalMonthsString = (String) jobCtx.getMergedJobDataMap().get("tandemRenewalMonths");
		Integer tandemRenewalMonths = ValueUtil.stoi(tandemRenewalMonthsString);
		if (tandemRenewalMonths == null) throw new JobExecutionException("Non sono definiti i mesi entro cui rinnovare un vecchio tandem2");

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
			//Listino target
			Listini lstTandem = findTandem2Listino(ses, DateUtil.now());
			//Elenchi
			List<IstanzeAbbonamenti> tandemList = new ArrayList<IstanzeAbbonamenti>();
			List<IstanzeAbbonamenti> list = new ArrayList<IstanzeAbbonamenti>();
			int offset = 0;
			do {
				list = findTandem1DList(ses, idRapporto, backwardDays, offset, PAGE_SIZE);
				tandemList.addAll(list);
				offset += list.size();
				VisualLogger.get().addHtmlInfoLine(idRapporto, "Trovati "+offset+" abbonamenti "+AppConstants.TAG_TANDEM1D+"...");
			} while (list.size() > 0);
			if (tandemList.size() > 0) {
				VisualLogger.get().addHtmlInfoLine(idRapporto, "Attivazione tandem in corso:");
				createOrRenewTandem(ses, idRapporto, lstTandem, tandemList, tandemRenewalMonths);
			} else {
				VisualLogger.get().addHtmlInfoLine(idRapporto, "Nessuna attivazione necessaria");
			}
			trx.commit();
		} catch (Exception e) {
			LOG.info("ERROR in job '"+jobName+"'");
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new JobExecutionException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		try {
			VisualLogger.get().closeAndSaveRapporto(idRapporto);
		} catch (BusinessException e) {
			throw new JobExecutionException(e.getMessage(), e);
		}
		LOG.info("Ended job '"+jobName+"'");
	}
	
	@SuppressWarnings("unchecked")
	private List<IstanzeAbbonamenti> findTandem1DList(Session ses, int idRapporto, 
			int backwardDays, int offset, int pageSize) throws BusinessException {
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DAY_OF_MONTH, -1*backwardDays);
		Date fromDay = cal.getTime();
		List<IstanzeAbbonamenti> iaList = null;
		try {
			String hql = "from IstanzeAbbonamenti ia where "+
					"ia.dataModifica >= :dt1 and "+// fromDay
					"ia.listino.tag like :tag1 and "+
					"ia.dataJob is null";
			Query q = ses.createQuery(hql);
			q.setParameter("dt1", fromDay);
			q.setParameter("tag1", "%"+AppConstants.TAG_TANDEM1D+"%");
			q.setFirstResult(offset);
			q.setMaxResults(pageSize);
			iaList = q.list();
		} catch (HibernateException e) {
			throw new BusinessException(e.getMessage(), e);
		}
		return iaList;
	}
	
	@SuppressWarnings("unchecked")
	private Listini findTandem2Listino(Session ses, Date now)  throws BusinessException {
		List<Listini> lList = null;
		try {
			String hql = "from Listini lst where "+
					"lst.dataInizio <= :dt1 and "+
					"lst.dataFine >= :dt2 and "+
					"lst.tag like :tag1";
			Query q = ses.createQuery(hql);
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
	
	private IstanzeAbbonamenti findTandem2ByAnagrafica(Session ses, Integer idAnagrafica,
			Date limitDate) throws BusinessException {
		IstanzeAbbonamenti ia = null;
		try {
			//Istanze entro una soglia temporale nel passato
			//che siano stati pagati o omaggio
			String hql = "from IstanzeAbbonamenti ia where "+
					"ia.abbonato.id = id1 and "+
					"ia.fascicoloFine.dataFine >= :dt1 and "+
					"ia.listino.tag like :tag1 "+
					"(ia.pagato = :b1 or ia.inFatturazione = :b2 or "+//true true
						"ia.listino.fatturaDifferita = :b3 or ia.listino.prezzo < :d1) and "+//true 0
					"order by ia.fascicoloFine.dataFine desc";
			Query q = ses.createQuery(hql);
			q.setParameter("id1", idAnagrafica, IntegerType.INSTANCE);
			q.setParameter("dt1", limitDate, DateType.INSTANCE);
			q.setParameter("tag1", "%"+AppConstants.TAG_TANDEM2+"%", StringType.INSTANCE);
			q.setParameter("b1", Boolean.TRUE, BooleanType.INSTANCE);
			q.setParameter("b2", Boolean.TRUE, BooleanType.INSTANCE);
			q.setParameter("b3", Boolean.TRUE, BooleanType.INSTANCE);
			q.setParameter("d1", AppConstants.SOGLIA, DoubleType.INSTANCE);
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
	
	private void createOrRenewTandem(Session ses, int idRapporto, Listini lst, 
			List<IstanzeAbbonamenti> iaList, Integer tandemRenewalMonths) 
					throws BusinessException {
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.MONTH, (-1)*tandemRenewalMonths);
		Date limitDate = cal.getTime();
		Date now = DateUtil.now();
		for (IstanzeAbbonamenti ia:iaList) {
			Anagrafiche benef = ia.getAbbonato();
			IstanzeAbbonamenti iaTandem2 = findTandem2ByAnagrafica(ses, benef.getId(), limitDate);
			IstanzeAbbonamenti newIa;
			String action = null;
			if (iaTandem2 == null) {
				//L'abbonamento tandem2 non esiste, allora lo crea
				IstanzeAbbonamenti transientNewIa = new IstanzeAbbonamentiDao()
						.createAbbonamentoAndIstanza(ses,
						ia.getAbbonato().getId(), ia.getPagante().getId(),
						null, lst.getTipoAbbonamento().getPeriodico().getId(),
						lst.getTipoAbbonamento().getCodice());
				Integer newIaId = iaDao.save(ses, transientNewIa);
				newIa = GenericDao.findById(ses, IstanzeAbbonamenti.class, newIaId);
				action = ACTION_NUOVO;
			} else {
				//L'abbonamento già esisteva e lo rinnova
				newIa = RinnovoBusiness.makeBasicRenewal(iaTandem2.getId(), true, true,
						ServerConstants.DEFAULT_SYSTEM_USER);
				action = ACTION_RINNOVO;
			}
			newIa.setNote("Tandem con "+ia.getAbbonamento().getCodiceAbbonamento());
			newIa.setDataJob(now);
			iaDao.update(ses, newIa);
			VisualLogger.get().addHtmlInfoLine(idRapporto, ia.getAbbonamento().getCodiceAbbonamento()+
					" ["+ia.getId()+"] -> "+newIa.getAbbonamento().getCodiceAbbonamento()+
					" ["+newIa.getId()+"] "+action);
		}
	}
	
}
