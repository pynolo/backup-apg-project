package it.giunti.apg.automation.jobs;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OutputCrmDataJob implements Job {

	private static Logger LOG = LoggerFactory.getLogger(OutputIstanzeScaduteJob.class);
	private static char SEP = ';';
	private static int PAGE_SIZE = 500;
	private static int COLUMN_GROUPS = 9;
	private static DecimalFormat DF = new DecimalFormat("0.00");
	private static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private static IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
	private Map<Integer, String> periodiciMap = new HashMap<Integer, String>();
	private String periodiciString = "";
	
	@SuppressWarnings("unchecked")
	@Override
	public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
		String jobName = jobCtx.getJobDetail().getKey().getName();
		LOG.info("Started job '"+jobName+"'");
		
		//param: periodico1
		String periodico1 = (String) jobCtx.getMergedJobDataMap().get("periodico1");
		if (periodico1 != null) {
			if (!periodico1.equals("")) {
				periodiciMap.put(1, periodico1);
				periodiciString += periodico1+SEP;
			}
		}
		//param: periodico2
		String periodico2 = (String) jobCtx.getMergedJobDataMap().get("periodico2");
		if (periodico2 != null) {
			if (!periodico2.equals("")) {
				periodiciMap.put(2, periodico2);
				periodiciString += periodico2+SEP;
			}
		}
		//param: periodico3
		String periodico3 = (String) jobCtx.getMergedJobDataMap().get("periodico3");
		if (periodico3 != null) {
			if (!periodico3.equals("")) {
				periodiciMap.put(3, periodico3);
				periodiciString += periodico3+SEP;
			}
		}
		//param: periodico4
		String periodico4 = (String) jobCtx.getMergedJobDataMap().get("periodico4");
		if (periodico4 != null) {
			if (!periodico4.equals("")) {
				periodiciMap.put(4, periodico4);
				periodiciString += periodico4+SEP;
			}
		}
		//param: periodico5
		String periodico5 = (String) jobCtx.getMergedJobDataMap().get("periodico5");
		if (periodico5 != null) {
			if (!periodico5.equals("")) {
				periodiciMap.put(5, periodico5);
				periodiciString += periodico5+SEP;
			}
		}
		//param: periodico6
		String periodico6 = (String) jobCtx.getMergedJobDataMap().get("periodico6");
		if (periodico6 != null) {
			if (!periodico6.equals("")) {
				periodiciMap.put(6, periodico6);
				periodiciString += periodico6+SEP;
			}
		}
		//param: periodico7
		String periodico7 = (String) jobCtx.getMergedJobDataMap().get("periodico7");
		if (periodico7 != null) {
			if (!periodico7.equals("")) {
				periodiciMap.put(7, periodico7);
				periodiciString += periodico7+SEP;
			}
		}
		//param: periodico8
		String periodico8 = (String) jobCtx.getMergedJobDataMap().get("periodico8");
		if (periodico8 != null) {
			if (!periodico8.equals("")) {
				periodiciMap.put(8, periodico8);
				periodiciString += periodico8+SEP;
			}
		}
		//param: periodico9
		String periodico9 = (String) jobCtx.getMergedJobDataMap().get("periodico9");
		if (periodico9 != null) {
			if (!periodico9.equals("")) {
				periodiciMap.put(9, periodico9);
				periodiciString += periodico9+SEP;
			}
		}
		
		//JOB
		Date dtStart = new Date();
		Session ses = SessionFactory.getSession();
		List<Anagrafiche> aList = new ArrayList<Anagrafiche>();
		Integer offset = 0;
		try {
			String hql = "select count(id) from Anagrafiche";
			Object result = ses.createQuery(hql).uniqueResult();
			Long totalAnag = (Long) result;
			ReportWriter fileWriter = new ReportWriter("crmExport");
			fileWriter.println(getHeader());
			//Parse Anagrafiche
			hql = "from Anagrafiche a order by a.id";
			do {
				Query q = ses.createQuery(hql);
				q.setFirstResult(offset);
				q.setMaxResults(PAGE_SIZE);
				aList = (List<Anagrafiche>) q.list();
				for (Anagrafiche a:aList) {
					String row = createRow(ses, a, fileWriter);
					fileWriter.println(row);
				}
				offset += aList.size();
				Double perc = 100*(offset.doubleValue()/totalAnag.doubleValue());
				LOG.info("Aggiornate "+offset+" anagrafiche ("+DF.format(perc)+"%) "+
						"fine stimata "+stimaFine(dtStart, offset, totalAnag));
				ses.flush();
				ses.clear();
			} while (aList.size() == PAGE_SIZE);
			fileWriter.close();
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			throw new JobExecutionException(e);
		} finally {
			ses.close();
		}
		LOG.info("Ended job '"+jobName+"'");
	}
	
	private static String stimaFine(Date dtInizio, Integer offset, Long total) {
		Date now = new Date();
		Long elapsed = now.getTime()-dtInizio.getTime();
		Double forecastDouble = elapsed.doubleValue()*total.doubleValue()/offset.doubleValue();
		Long forecastTime = forecastDouble.longValue() + dtInizio.getTime();
		Date forecastDt = new Date(forecastTime);
		return SDF.format(forecastDt);
	}
	
	private String getHeader() {
		String header = "id_customer"+SEP+"address_title"+SEP+"address_first_name"+SEP+
				"address_last_name_company"+SEP+"address_co"+SEP+
				"address_address"+SEP+"address_locality"+SEP+"address_province"+SEP+
				"address_zip"+SEP+"address_country_code"+SEP+"sex"+SEP+
				"cod_fisc"+SEP+"piva"+SEP+"phone_mobile"+SEP+
				"phone_landline"+SEP+"email_primary"+SEP+"id_job"+SEP+
				"id_qualification"+SEP+"id_tipo_anagrafica"+SEP+"birth_date"+SEP+
				"consent_tos"+SEP+"consent_marketing"+SEP+"consent_profiling"+SEP+
				"consent_update_date"+SEP+"creation_date"+SEP+"modified_date"+SEP+
				"customer_type";
		for (int i=1; i<= COLUMN_GROUPS; i++) {
			String groupHeader = SEP+"own_subscription_name_"+1+SEP+
					"own_subscription_blocked_"+i+SEP+"own_subscription_begin_"+i+SEP+
					"own_subscription_end_"+i+SEP+"gift_subscription_end_"+i+SEP+
					"subscription_creation_date_"+i;
			header += groupHeader;
		}
		return header;
	}
	
	private String createRow(Session ses, Anagrafiche a, ReportWriter fileWriter) {
		Date lastModified = AppConstants.DEFAULT_DATE;
		List<IstanzeAbbonamenti> iaList = iaDao.findIstanzeProprieByAnagrafica(ses,
				a.getId(), false, 0, Integer.MAX_VALUE);
		String abbonamentiCsvString = "";
		for (int i = 1; i <= COLUMN_GROUPS; i++) {
			String uid = periodiciMap.get(i);
			if (uid == null) uid = "";
			if (uid.length() > 0) {
				//Fill column group
				abbonamentiCsvString += 
						formatColumnGroupByPeriodicoUid(uid, iaList, lastModified);
			} else {
				//Empty column group
				abbonamentiCsvString += SEP+SEP+SEP+SEP+SEP+SEP;
			}
		}
		if (a.getDataModifica().after(lastModified)) lastModified = a.getDataModifica();
		String anagraficaCsvString = formatAnagraficaColumns(a, lastModified);
		return anagraficaCsvString+SEP+abbonamentiCsvString;
	}

	private String formatAnagraficaColumns(Anagrafiche anag, Date lastModified) {
		String result = "";
		result += anag.getUid()+SEP;
		result += anag.getIndirizzoPrincipale().getCognomeRagioneSociale()+SEP;
		***
		result += ServerConstants.FORMAT_DAY_SQL.format(lastModified);
		return result;
	}
	
	private String formatColumnGroupByPeriodicoUid(String letter, 
			List<IstanzeAbbonamenti> iaList, Date lastModified) {
		for (IstanzeAbbonamenti ia:iaList) {
			if (ia.getAbbonamento().getPeriodico().getUid().equalsIgnoreCase(letter)) {
				if (ia.getDataModifica().after(lastModified)) lastModified = ia.getDataModifica();
				***
			}
		}
	}
	
	
	// INNER CLASSES
	
	
	private static class ReportWriter {
		private FileWriter writer = null;
		
		public ReportWriter(String fileName) throws IOException {
			File report = File.createTempFile(fileName, ".csv");
			LOG.info("Output su "+report.getAbsolutePath());
			writer = new FileWriter(report);
		}
		
		public void println(String report) 
				throws IOException {
			String line = report +"\r\n";
			writer.write(line);
		}
		
		public void close() throws IOException {
			writer.close();
		}
	}
}
