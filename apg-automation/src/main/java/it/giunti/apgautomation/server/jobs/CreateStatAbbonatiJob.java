package it.giunti.apgautomation.server.jobs;

import it.giunti.apg.server.persistence.SessionFactory;
import it.giunti.apg.server.persistence.StatAbbonatiDao;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Periodici;
import it.giunti.apg.shared.model.StatAbbonati;
import it.giunti.apgautomation.server.business.CreateStatAbbonatiBusiness;
import it.giunti.apgautomation.server.business.EntityBusiness;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateStatAbbonatiJob implements Job {
	
	//private static final long serialVersionUID = 4394668127625471725L;
	static private Logger LOG = LoggerFactory.getLogger(CreateStatAbbonatiJob.class);
	
	@Override
	public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
		LOG.info("Started job '"+jobCtx.getJobDetail().getKey().getName()+"'");
		//param: letterePeriodici
		String letterePeriodici = (String) jobCtx.getMergedJobDataMap().get("letterePeriodici");
		if (letterePeriodici == null) throw new JobExecutionException("letterePeriodici non definito");
		if (letterePeriodici.equals("")) throw new JobExecutionException("letterePeriodici non definito");
		String[] lettereArray = letterePeriodici.split(AppConstants.STRING_SEPARATOR);
		//JOB
		Date today = new Date();
		Session ses = SessionFactory.getSession();
		Transaction trx = ses.beginTransaction();
		CreateStatAbbonatiBusiness sab = new CreateStatAbbonatiBusiness();
		StatAbbonatiDao saDao = new StatAbbonatiDao();
		try {
			List<Periodici> periodiciList = EntityBusiness.periodiciFromUidArray(ses, lettereArray);
			for (Periodici periodico:periodiciList) {
				//Creazione statistiche
				Integer tiratura = sab.countProssimaTiratura(ses, today, periodico.getId(), false, false, false, false);
				Integer nuovi = sab.countProssimaTiratura(ses, today, periodico.getId(), true, false, false, false);
				Integer disdette = sab.countDisdette(ses, today, periodico.getId());
				Integer morosiAnnoPrec = sab.countMorosiAnnoPrec(ses, today, periodico.getId());
				Integer quotePagate = sab.countProssimaTiratura(ses, today, periodico.getId(), false, false, true, false);
				Integer morosiAttuali = sab.countProssimaTiratura(ses, today, periodico.getId(), false, true, false, false);
				Integer omaggi = sab.countProssimaTiratura(ses, today, periodico.getId(), false, false, false, true);
				//Scrittura statistiche
				StatAbbonati sa = new StatAbbonati();
				sa.setPeriodico(periodico);
				sa.setTiratura(tiratura);
				sa.setNuovi(nuovi);
				sa.setDisdette(disdette);
				sa.setMorosiAnnoPrec(morosiAnnoPrec);
				sa.setPagati(quotePagate);
				sa.setMorosiAttuali(morosiAttuali);
				sa.setOmaggi(omaggi);
				sa.setDataCreazione(today);
				saDao.save(ses, sa);
			}
			trx.commit();
		} catch (Exception e) {
			LOG.info("ERROR in job '"+jobCtx.getJobDetail().getKey().getName()+"'");
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new JobExecutionException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		LOG.info("Ended job '"+jobCtx.getJobDetail().getKey().getName()+"'");
	}

}
