package it.giunti.apg.automation.jobs;

import it.giunti.apg.automation.business.EntityBusiness;
import it.giunti.apg.core.business.CsvWriter;
import it.giunti.apg.core.persistence.FascicoliDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.FileException;
import it.giunti.apg.shared.model.Periodici;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;

import org.hibernate.Session;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OutputArchiveByMagazineJob implements Job {

	static private Logger LOG = LoggerFactory.getLogger(OutputIstanzeScaduteJob.class);
	static private char SEP = ';';

	static private String SQL = "select "
				+ "a.uid as uid_anagrafica, ind.nome, ind.cognome_ragione_sociale, "
				+ "a.sesso, a.codice_fiscale, prof.nome as professione, "
				+ "ind.localita, ind.cap, ind.id_provincia, a.email_primaria, "
				+ "ta.codice as tipo_abb, ta.nome as tipo_abb_descr, "
				+ "abb.codice_abbonamento, ia.id as uid_istanza, "
				+ "abb.data_creazione as dt_creazione_abb, "
				+ "ia.data_creazione as dt_creazione_istanza, "
				+ "fi.data_inizio as dt_inizio_istanza, ff.data_fine as dt_fine_istanza, "
				+ "ia.invio_bloccato, ia.data_disdetta as dt_disdetta, "
				+ "l.gracing_iniziale, l.gracing_finale "
			+"from istanze_abbonamenti ia, abbonamenti abb, listini l, "
				+ "tipi_abbonamento ta, indirizzi ind, fascicoli fi, fascicoli ff, "
				+ "anagrafiche a left outer join professioni prof on a.id_professione=prof.id "
			+"where "
				+ "a.id_indirizzo_principale = ind.id and ia.id_abbonato = a.id and "
				+ "ia.id_fascicolo_inizio=fi.id and ia.id_fascicolo_fine = ff.id and "
				+ "ia.id_listino = l.id and l.id_tipo_abbonamento = ta.id and "
				+ "ia.id_abbonamento=abb.id and ff.id_periodico = 2 and "
				+ "ia.invio_bloccato = false and ia.data_disdetta is null "
			+"order by ia.data_creazione";
	
	@Override
	public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
		String jobName = jobCtx.getJobDetail().getKey().getName();
		LOG.info("Started job '"+jobName+"'");
		//param: letterePeriodici
		String letterePeriodici = (String) jobCtx.getMergedJobDataMap().get("letterePeriodici");
		if (letterePeriodici == null) throw new JobExecutionException("letterePeriodici non definito");
		if (letterePeriodici.equals("")) throw new JobExecutionException("letterePeriodici non definito");
		String[] lettereArray = letterePeriodici.split(AppConstants.STRING_SEPARATOR);
		//JOB
		Session ses = SessionFactory.getSession();
		FascicoliDao fasDao = new FascicoliDao();
		try {
			List<Periodici> periodici = EntityBusiness.periodiciFromUidArray(ses, lettereArray);
			for (Periodici periodico:periodici) {
				File f = File.createTempFile("archivio_"+periodico.getUid()+"_", ".csv");
				LOG.info("Temp file for '"+periodico.getUid()+"': "+f.getAbsolutePath());
				OutputStream fos = new FileOutputStream(f);
				CsvWriter writer = new CsvWriter(fos, SEP, Charset.forName(AppConstants.CHARSET));
				
				fos.close();
			}
		} catch (BusinessException e) {
			LOG.error(e.getMessage(), e);
			throw new JobExecutionException(e);
		} catch (FileException e) {
			LOG.error(e.getMessage(), e);
			throw new JobExecutionException(e);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			throw new JobExecutionException(e);
		} finally {
			ses.close();
		}
		LOG.info("Ended job '"+jobName+"'");
	}
}
