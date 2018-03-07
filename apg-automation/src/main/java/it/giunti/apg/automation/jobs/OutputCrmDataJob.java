package it.giunti.apg.automation.jobs;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.business.FtpBusiness;
import it.giunti.apg.core.business.FtpConfig;
import it.giunti.apg.core.business.FtpUtil;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
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

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.BooleanType;
import org.hibernate.type.IntegerType;
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
	
	public static String CUSTOMER_TYPE_GIFTER = "G";
	public static String CUSTOMER_TYPE_SUBSCRIBER = "S";
	public static String CUSTOMER_TYPE_BOTH = "B";
	public static String CUSTOMER_TYPE_NONE = "";
	
	private Map<Integer, String> periodiciMap = new HashMap<Integer, String>();
	
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
			}
		}
		//param: periodico2
		String periodico2 = (String) jobCtx.getMergedJobDataMap().get("periodico2");
		if (periodico2 != null) {
			if (!periodico2.equals("")) {
				periodiciMap.put(2, periodico2);
			}
		}
		//param: periodico3
		String periodico3 = (String) jobCtx.getMergedJobDataMap().get("periodico3");
		if (periodico3 != null) {
			if (!periodico3.equals("")) {
				periodiciMap.put(3, periodico3);
			}
		}
		//param: periodico4
		String periodico4 = (String) jobCtx.getMergedJobDataMap().get("periodico4");
		if (periodico4 != null) {
			if (!periodico4.equals("")) {
				periodiciMap.put(4, periodico4);
			}
		}
		//param: periodico5
		String periodico5 = (String) jobCtx.getMergedJobDataMap().get("periodico5");
		if (periodico5 != null) {
			if (!periodico5.equals("")) {
				periodiciMap.put(5, periodico5);
			}
		}
		//param: periodico6
		String periodico6 = (String) jobCtx.getMergedJobDataMap().get("periodico6");
		if (periodico6 != null) {
			if (!periodico6.equals("")) {
				periodiciMap.put(6, periodico6);
			}
		}
		//param: periodico7
		String periodico7 = (String) jobCtx.getMergedJobDataMap().get("periodico7");
		if (periodico7 != null) {
			if (!periodico7.equals("")) {
				periodiciMap.put(7, periodico7);
			}
		}
		//param: periodico8
		String periodico8 = (String) jobCtx.getMergedJobDataMap().get("periodico8");
		if (periodico8 != null) {
			if (!periodico8.equals("")) {
				periodiciMap.put(8, periodico8);
			}
		}
		//param: periodico9
		String periodico9 = (String) jobCtx.getMergedJobDataMap().get("periodico9");
		if (periodico9 != null) {
			if (!periodico9.equals("")) {
				periodiciMap.put(9, periodico9);
			}
		}
		
		//JOB
		Session ses = SessionFactory.getSession();
		List<Anagrafiche> aList = new ArrayList<Anagrafiche>();
		Integer offset = 0;
		File f = null;
		try {
			String hql = "select count(id) from Anagrafiche";
			Object result = ses.createQuery(hql).uniqueResult();
			Long totalAnag = (Long) result;
			LOG.info("Totale anagrafiche: "+totalAnag);
			ReportWriter fileWriter = new ReportWriter("crmExport");
			f = fileWriter.getFile();
			fileWriter.println(getHeader());
			
			Date dtStart = new Date();
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
				LOG.info("Estratte "+offset+" anagrafiche ("+DF.format(perc)+"%) "+
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
		//Caricamento file
		try {
			FtpConfig ftpConfig = FtpUtil.getFtpConfig(ses, AppConstants.SOCIETA_GIUNTI_EDITORE);
			String remoteNameAndDir = ServerConstants.FORMAT_FILE_NAME_TIMESTAMP.format(DateUtil.now())+
					"_exportCrm.csv";
			LOG.info("ftp://"+ftpConfig.getUsername()+"@"+ftpConfig.getHost()+"/"+remoteNameAndDir);
			FtpBusiness.upload(ftpConfig.getHost(), ftpConfig.getPort(), ftpConfig.getUsername(), ftpConfig.getPassword(),
					remoteNameAndDir, f);
		} catch (BusinessException | IOException e) {
			throw new JobExecutionException(e.getMessage(), e);
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
				"customer_type"+SEP+"consent_tos"+SEP+"consent_marketing"+SEP+
				"consent_profiling"+SEP+"consent_update_date"+SEP+"creation_date"+SEP+
				"modified_date";
		for (int i=1; i<= COLUMN_GROUPS; i++) {
			String groupHeader = SEP+"own_subscription_name_"+i+SEP+
					"own_subscription_blocked_"+i+SEP+"own_subscription_begin_"+i+SEP+
					"own_subscription_end_"+i+SEP+"gift_subscription_end_"+i+SEP+
					"subscription_creation_date_"+i;
			header += groupHeader;
		}
		return header;
	}
	
	private String createRow(Session ses, Anagrafiche a, ReportWriter fileWriter) {
		Date lastModified = AppConstants.DEFAULT_DATE;
		List<IstanzeAbbonamenti> iaList = findIstanzeByAnagrafica(ses, a.getId());
		String abbonamentiCsvString = "";
		for (int i = 1; i <= COLUMN_GROUPS; i++) {
			String uid = periodiciMap.get(i);
			if (uid == null) uid = "";
			if (uid.length() > 0) {
				//Fill column group
				abbonamentiCsvString += 
						formatColumnGroupByPeriodicoUid(uid, a, iaList, lastModified);
			} else {
				//Empty column group
				abbonamentiCsvString += SEP+SEP+SEP+SEP+SEP+SEP;
			}
		}
		if (a.getDataModifica().after(lastModified)) lastModified = a.getDataModifica();
		String customerType = CUSTOMER_TYPE_NONE;
		boolean isGifter = false;
		boolean isSubscriber = false;
		for (IstanzeAbbonamenti ia:iaList) {
			if (ia.getAbbonato().equals(a)) isSubscriber=true;
			if (ia.getPagante() != null) 
				if (ia.getPagante().equals(a)) isGifter=true;
		}
		if (isSubscriber) customerType = CUSTOMER_TYPE_SUBSCRIBER;
		if (isGifter) customerType = CUSTOMER_TYPE_GIFTER;
		if (isSubscriber && isGifter) customerType = CUSTOMER_TYPE_BOTH;
		String anagraficaCsvString = formatAnagraficaColumns(a, customerType, lastModified);
		return anagraficaCsvString+SEP+abbonamentiCsvString;
	}

	@SuppressWarnings("unchecked")
	private static List<IstanzeAbbonamenti> findIstanzeByAnagrafica(Session ses,
			Integer idAbbonato) throws HibernateException {
		String qs = "from IstanzeAbbonamenti ia where " +
				"ia.abbonato.id = :id1 or ia.pagante.id = :id2 and "+
				"ia.ultimaDellaSerie = :b1 "+
				"order by ia.dataCreazione desc ";
		Query q = ses.createQuery(qs);
		q.setParameter("id1", idAbbonato, IntegerType.INSTANCE);
		q.setParameter("id2", idAbbonato, IntegerType.INSTANCE);
		q.setParameter("b1", Boolean.TRUE, BooleanType.INSTANCE);
		List<IstanzeAbbonamenti> abbList = (List<IstanzeAbbonamenti>) q.list();
		return abbList;
	}
	
	//@SuppressWarnings("unchecked")
	//private static List<Object[]>  findAnagraficheIstanze(Session ses,
	//		Integer idAbbonato, int offset, int pageSize) throws HibernateException {
	//	String qs = "from Anagrafiche a, IstanzeAbbonamenti ia where " +
	//			"ia.abbonato.id = a.id or ia.pagante.id = a.id and "+
	//			"ia.ultimaDellaSerie = :b1 "+
	//			"order by a.id asc ";
	//	Query q = ses.createQuery(qs);
	//	q.setParameter("b1", Boolean.TRUE, BooleanType.INSTANCE);
	//	q.setFirstResult(offset);
	//	q.setMaxResults(pageSize);
	//	List<Object[]> list = (List<Object[]>) q.list();
	//	return list;
	//}
	
	private String formatAnagraficaColumns(Anagrafiche a, String customerType, Date lastModified) {
		String result = "";
		//id_customer
		result += a.getUid()+SEP;
		//address_title
		result += cleanString(a.getIndirizzoPrincipale().getTitolo())+SEP;
		//address_first_name
		result += cleanString(a.getIndirizzoPrincipale().getNome())+SEP;
		//address_last_name_company
		result += cleanString(a.getIndirizzoPrincipale().getCognomeRagioneSociale())+SEP;
		//address_co
		result += cleanString(a.getIndirizzoPrincipale().getPresso())+SEP;
		//address_address
		result += cleanString(a.getIndirizzoPrincipale().getIndirizzo())+SEP;
		//address_locality
		result += cleanString(a.getIndirizzoPrincipale().getLocalita())+SEP;
		//address_province
		result += cleanString(a.getIndirizzoPrincipale().getProvincia())+SEP;
		//address_zip
		result += cleanString(a.getIndirizzoPrincipale().getCap())+SEP;
		//address_country_code
		result += cleanString(a.getIndirizzoPrincipale().getNazione().getSiglaNazione())+SEP;
		//sex
		result += cleanString(a.getSesso())+SEP;
		//cod_fisc
		result += cleanString(a.getCodiceFiscale())+SEP;
		//piva
		result += cleanString(a.getPartitaIva())+SEP;
		//phone_mobile
		result += cleanString(a.getTelMobile())+SEP;
		//phone_landline
		result += cleanString(a.getTelCasa())+SEP;
		//email_primary
		result += cleanString(a.getEmailPrimaria())+SEP;
		//id_job
		String idJob = (a.getProfessione() != null)?a.getProfessione().getId().toString():"";
		result += idJob+SEP;
		//id_qualification
		String idQualification = (a.getTitoloStudio() != null)?a.getTitoloStudio().getId().toString():"";
		result += idQualification+SEP;
		//id_tipo_anagrafica
		result += cleanString(a.getIdTipoAnagrafica())+SEP;
		//birth_date
		String dataNascita = (a.getDataNascita() != null)?ServerConstants.FORMAT_DAY_SQL.format(a.getDataNascita()):"";
		result += dataNascita +SEP;
		//customer_type
		result += customerType+SEP;
		//consent_tos
		result += (a.getConsensoTos()?"true":"false")+SEP;
		//consent_marketing
		result += (a.getConsensoMarketing()?"true":"false")+SEP;
		//consent_profiling
		result += (a.getConsensoProfilazione()?"true":"false")+SEP;
		//consent_update_date
		String updateDate = (a.getDataModifica() != null)?ServerConstants.FORMAT_DAY_SQL.format(a.getDataModifica()):"";
		result += updateDate+SEP;
		//creation_date
		String creationDate = (a.getDataCreazione() != null)?ServerConstants.FORMAT_DAY_SQL.format(a.getDataCreazione()):"";
		result += creationDate+SEP;
		//modified_date
		result += ServerConstants.FORMAT_DAY_SQL.format(lastModified);
		return result;
	}
	
	private String formatColumnGroupByPeriodicoUid(String letter, 
			Anagrafiche a, List<IstanzeAbbonamenti> iaList, Date lastModified) {
		Date latestOwnDate = AppConstants.DEFAULT_DATE;
		Date latestGiftDate = AppConstants.DEFAULT_DATE;
		String codAbbo = "";
		String blocked = "";
		String ownBegin = "";
		String ownEnd = "";
		String giftEnd = "";
		String creation = "";
		for (IstanzeAbbonamenti ia:iaList) {
			if (ia.getAbbonamento().getPeriodico().getUid().equalsIgnoreCase(letter)) {
				if (ia.getDataModifica().after(lastModified)) lastModified = ia.getDataModifica();
				//Gift or not?
				if (ia.getAbbonato().equals(a)) {
					//Is own instance
					//Choose only latest ia:
					if (ia.getDataCreazione().after(latestOwnDate)) {
						latestOwnDate = ia.getDataCreazione();
						codAbbo = ia.getAbbonamento().getCodiceAbbonamento();
						blocked = (ia.getInvioBloccato()?"true":"false");
						ownBegin = ServerConstants.FORMAT_DAY_SQL.format(
								ia.getFascicoloInizio().getDataInizio());
						ownEnd = ServerConstants.FORMAT_DAY_SQL.format(
								ia.getFascicoloFine().getDataFine());
						creation = ServerConstants.FORMAT_DAY_SQL.format(
								ia.getAbbonamento().getDataCreazione());
					}
				} else {
					//Is gift instance
					//Choose only latest ia:
					if (ia.getDataCreazione().after(latestGiftDate)) {
						giftEnd = ServerConstants.FORMAT_DAY_SQL.format(
								ia.getFascicoloFine().getDataFine());
					}
				}
			}
		}
		String result = "";
		//own_subscription_name
		result = codAbbo+SEP;
		//own_subscription_blocked
		result += blocked+SEP;
		//own_subscription_begin
		result += ownBegin+SEP;
		//own_subscription_end
		result += ownEnd+SEP;
		//gift_subscription_end
		result += giftEnd+SEP;
		//subscription_creation_date
		result += creation+SEP;
		return result;
	}
	
	private String cleanString(String s) {
		if (s == null) s = "";
		s = s.trim();
		return s;
	}
	
	
	// INNER CLASSES
	
	
	private static class ReportWriter {
		private FileWriter writer = null;
		private File file = null;
		
		public ReportWriter(String fileName) throws IOException {
			file = File.createTempFile(fileName, ".csv");
			LOG.info("Output su "+file.getAbsolutePath());
			writer = new FileWriter(file);
		}
		
		public void println(String report) 
				throws IOException {
			String line = report +"\r\n";
			writer.write(line);
		}
		
		public void close() throws IOException {
			writer.close();
		}
		public File getFile() {
			return file;
		}
	}
}
