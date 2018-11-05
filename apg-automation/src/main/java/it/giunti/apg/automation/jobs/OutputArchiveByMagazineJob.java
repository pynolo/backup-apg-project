package it.giunti.apg.automation.jobs;

import it.giunti.apg.automation.business.EntityBusiness;
import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.business.CsvWriter;
import it.giunti.apg.core.business.FtpBusiness;
import it.giunti.apg.core.business.FtpConfig;
import it.giunti.apg.core.business.FtpUtil;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.Periodici;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.DoubleType;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OutputArchiveByMagazineJob implements Job {

	static private Logger LOG = LoggerFactory.getLogger(OutputArchiveByMagazineJob.class);
	static private char SEP = ';';
	static private String NULL_STRING = "";
	static private int PAGE_SIZE = 500;

	static private String SQL = "select "
				+ "a.uid as uid_anagrafica, ind.nome, ind.cognome_ragione_sociale, "
				+ "a.sesso, a.codice_fiscale, prof.nome as professione, "
				+ "ind.localita, ind.cap, ind.id_provincia, a.email_primaria, "
				+ "a.tel_casa, a.tel_mobile,"
				+ "ta.codice as tipo_abb, ta.nome as tipo_abb_descr, "
				+ "abb.codice_abbonamento, ia.id as uid_istanza, "
				+ "abb.data_creazione as dt_creazione_abb, "
				+ "ia.data_creazione as dt_creazione_istanza, "
				+ "fi.data_inizio as dt_inizio_istanza, ff.data_fine as dt_fine_istanza, "
				+ "fi.titolo_numero as numero_inizio_istanza, ff.titolo_numero as numero_fine_istanza, "
				+ "ia.invio_bloccato, ia.data_disdetta as dt_disdetta, "
				+ "l.gracing_iniziale, l.gracing_finale, "
				+ "IF (l.prezzo < :n1, 'true', 'false'), "//omaggio
				+ "IF (l.fattura_differita or ia.in_fatturazione, 'true', 'false'), "//in fatturazione
				+ "ia.pagato "
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

//	static private String SQL = "select "
//			+ "a.uid as uid_anagrafica, ind.nome, ind.cognome_ragione_sociale, "
//			+ "a.sesso, a.codice_fiscale, prof.nome as professione, "
//			+ "ind.localita, ind.cap, ind.id_provincia, a.email_primaria, "
//			+ "a.tel_casa, a.tel_mobile,"
//			+ "ta.codice as tipo_abb, ta.nome as tipo_abb_descr, "
//			+ "abb.codice_abbonamento, ia.id as uid_istanza, "
//			+ "abb.data_creazione as dt_creazione_abb, "
//			+ "ia.data_creazione as dt_creazione_istanza, "
//			+ "fi.data_inizio as dt_inizio_istanza, ff.data_fine as dt_fine_istanza, "
//			+ "ia.invio_bloccato, ia.data_disdetta as dt_disdetta, "
//			+ "l.gracing_iniziale, l.gracing_finale, lastPag.id_tipo_pagamento "
//		+"from istanze_abbonamenti ia left outer join "
//				+ "(select id_istanza_abbonamento, id_tipo_pagamento, max(data_pagamento) as MaxDate "
//				+ "from pagamenti group by id_istanza_abbonamento) lastPag "
//				+ "on ia.id = lastPag.id_istanza_abbonamento, "
//			+ "abbonamenti abb, listini l, "
//			+ "tipi_abbonamento ta, indirizzi ind, fascicoli fi, fascicoli ff, "
//			+ "anagrafiche a left outer join professioni prof on a.id_professione=prof.id "
//		+"where "
//			+ "a.id_indirizzo_principale = ind.id and ia.id_abbonato = a.id and "
//			+ "ia.id_fascicolo_inizio=fi.id and ia.id_fascicolo_fine = ff.id and "
//			+ "ia.id_listino = l.id and l.id_tipo_abbonamento = ta.id and "
//			+ "ia.id_abbonamento=abb.id and ff.id_periodico = 2 and "
//			+ "ia.invio_bloccato = false and ia.data_disdetta is null "
//		+"order by ia.data_creazione";
	
	@SuppressWarnings("unchecked")
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
		try {
			List<Periodici> periodici = EntityBusiness.periodiciFromUidArray(ses, lettereArray);
			for (Periodici periodico:periodici) {
				//Creazione file
				File f = File.createTempFile("archivio_"+periodico.getUid()+"_", ".csv");
				LOG.info("Temp file for '"+periodico.getUid()+"': "+f.getAbsolutePath());
				OutputStream fos = new FileOutputStream(f);
				CsvWriter writer = new CsvWriter(fos, SEP, Charset.forName(AppConstants.CHARSET_UTF8));
				writer.writeRecord(getHeader());
				int size = 0;
				int offset = 0;
				do {
					Query q = ses.createSQLQuery(SQL);
					q.setParameter("n1", AppConstants.SOGLIA, DoubleType.INSTANCE);
					q.setMaxResults(PAGE_SIZE);
					q.setFirstResult(offset);
					List<Object> list = q.list();
					writeToFile(writer, list);
					size = list.size();
					offset += size;
					ses.flush();
					ses.clear();
					LOG.info("Scritte "+offset+" istanze su file");
				} while (size > 0);
				writer.flush();
				fos.flush();
				fos.close();
				//Caricamento file
				FtpConfig ftpConfig = FtpUtil.getFtpConfig(ses, periodico.getIdSocieta());
				String remoteNameAndDir = ServerConstants.FORMAT_FILE_NAME_TIMESTAMP.format(DateUtil.now())+
						"_archivio_"+periodico.getUid()+".csv";
				LOG.info("ftp://"+ftpConfig.getUsername()+"@"+ftpConfig.getHost()+"/"+remoteNameAndDir);
				FtpBusiness.upload(ftpConfig.getHost(), ftpConfig.getPort(), ftpConfig.getUsername(), ftpConfig.getPassword(),
						remoteNameAndDir, f);
			}
		} catch (BusinessException e) {
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
	
	private String[] getHeader() {
		String[] headers = { "uid_anagrafica", "nome", "cognome_ragione_sociale",
				"sesso", "codice_fiscale", "professione",
				"localita", "cap", "provincia", "email_primaria",
				"tel_casa", "tel_mobile",
				"tipo_abb", "tipo_abb_descr",
				"codice_abbonamento", "uid_istanza",
				"dt_creazione_abb",
				"dt_creazione_istanza",
				"dt_inizio_istanza", "dt_fine_istanza",
				"numero_inizio_istanza", "numero_fine_istanza",
				"invio_bloccato", "dt_disdetta",
				"gracing_iniziale", "gracing_finale",
				"omaggio", "fatturazione", "pagato"};
			return headers;
	}
	
	private void writeToFile(CsvWriter writer, List<Object> list) throws IOException {
		for (Object rowObj:list) {
			Object[] row = (Object[]) rowObj;
			String[] record = new String[row.length];
			for (int i=0; i < row.length; i++) {
				Object obj = row[i];
				String s = null;
				if (obj == null) {
					s = NULL_STRING;
				} else {
					if (obj instanceof Date) s = ServerConstants.FORMAT_DAY_SQL.format((Date) obj);
					if (obj instanceof String) s = (String) obj;
					if (obj instanceof Boolean) s = Boolean.toString((Boolean) obj);
					if (obj instanceof Integer) s = ((Integer) obj).toString();
					if (s == null) s = obj.toString();
				}
				record[i] = s;
			}
			writer.writeRecord(record);
		}
	}
	
}
