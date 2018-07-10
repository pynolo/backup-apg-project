package it.giunti.apg.automation.jobs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.business.FtpBusiness;
import it.giunti.apg.core.business.FtpConfig;
import it.giunti.apg.core.business.FtpUtil;
import it.giunti.apg.core.persistence.CacheCrmDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.CacheCrm;

public class OutputCacheCrmJob implements Job {

	private static Logger LOG = LoggerFactory.getLogger(OutputCacheCrmJob.class);
	private static String SEP = ";";
	private static int PAGE_SIZE = 500;
	private static DecimalFormat DF = new DecimalFormat("0.00");
	private static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private Map<Integer, String> periodiciMap = new HashMap<Integer, String>();
	private CacheCrmDao cacheCrmDao = new CacheCrmDao();
	
	@SuppressWarnings("unchecked")
	@Override
	public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
		String jobName = jobCtx.getJobDetail().getKey().getName();
		LOG.info("Started job '"+jobName+"'");
		
		//Cache periodici order	
		String periodico0 = AppConstants.CACHE_PERIODICI_ORDER[0];
		if (periodico0 != null) {
			if (!periodico0.equals("")) {
				periodiciMap.put(0, periodico0);
			}
		}
		//param: periodico1
		String periodico1 = AppConstants.CACHE_PERIODICI_ORDER[1];
		if (periodico1 != null) {
			if (!periodico1.equals("")) {
				periodiciMap.put(1, periodico1);
			}
		}
		//param: periodico2
		String periodico2 = AppConstants.CACHE_PERIODICI_ORDER[2];
		if (periodico2 != null) {
			if (!periodico2.equals("")) {
				periodiciMap.put(2, periodico2);
			}
		}
		//param: periodico3
		String periodico3 = AppConstants.CACHE_PERIODICI_ORDER[3];
		if (periodico3 != null) {
			if (!periodico3.equals("")) {
				periodiciMap.put(3, periodico3);
			}
		}
		//param: periodico4
		String periodico4 = AppConstants.CACHE_PERIODICI_ORDER[4];
		if (periodico4 != null) {
			if (!periodico4.equals("")) {
				periodiciMap.put(4, periodico4);
			}
		}
		//param: periodico5
		String periodico5 = AppConstants.CACHE_PERIODICI_ORDER[5];
		if (periodico5 != null) {
			if (!periodico5.equals("")) {
				periodiciMap.put(5, periodico5);
			}
		}
		//param: periodico6
		String periodico6 = AppConstants.CACHE_PERIODICI_ORDER[6];
		if (periodico6 != null) {
			if (!periodico6.equals("")) {
				periodiciMap.put(6, periodico6);
			}
		}
		//param: periodico7
		String periodico7 = AppConstants.CACHE_PERIODICI_ORDER[7];
		if (periodico7 != null) {
			if (!periodico7.equals("")) {
				periodiciMap.put(7, periodico7);
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
		} catch (IOException | BusinessException e) {
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
				"customer_type"+SEP+"consent_tos"+SEP+"consent_marketing"+SEP+
				"consent_profiling"+SEP+"consent_update_date"+SEP+"creation_date"+SEP+
				"modified_date";
		for (int i=0; i<= AppConstants.CACHE_PERIODICI_ORDER.length; i++) {
			String groupHeader = SEP+"own_subscription_name_"+i+SEP+
					"own_subscription_blocked_"+i+SEP+"own_subscription_begin_"+i+SEP+
					"own_subscription_end_"+i+SEP+"gift_subscription_end_"+i+SEP+
					"subscription_creation_date_"+i;
			header += groupHeader;
		}
		return header;
	}
	
	private String createRow(Session ses, Anagrafiche a, ReportWriter fileWriter) throws BusinessException {
		CacheCrm crmData = cacheCrmDao.findByAnagrafica(ses, a.getId());
		String abbonamentiCsvString = "";
		for (int i = 0; i <= AppConstants.CACHE_PERIODICI_ORDER.length; i++) {
			String uid = periodiciMap.get(i);
			if (uid == null) uid = "";
			if (uid.length() > 0) {
				//Fill column group
				abbonamentiCsvString += 
						formatColumnGroupByPeriodicoOrder(i, crmData);
			} else {
				//Empty column group
				abbonamentiCsvString += SEP+SEP+SEP+SEP+SEP+SEP;
			}
		}
		String anagraficaCsvString = formatAnagraficaColumns(a, crmData);
		return anagraficaCsvString+SEP+abbonamentiCsvString;
	}

	//@SuppressWarnings("unchecked")
	//private static List<IstanzeAbbonamenti> findIstanzeByAnagrafica(Session ses,
	//		Integer idAbbonato) throws HibernateException {
	//	String qs = "from IstanzeAbbonamenti ia where " +
	//			"ia.abbonato.id = :id1 or ia.pagante.id = :id2 and "+
	//			"ia.ultimaDellaSerie = :b1 "+
	//			"order by ia.dataCreazione desc ";
	//	Query q = ses.createQuery(qs);
	//	q.setParameter("id1", idAbbonato, IntegerType.INSTANCE);
	//	q.setParameter("id2", idAbbonato, IntegerType.INSTANCE);
	//	q.setParameter("b1", Boolean.TRUE, BooleanType.INSTANCE);
	//	List<IstanzeAbbonamenti> abbList = (List<IstanzeAbbonamenti>) q.list();
	//	return abbList;
	//}
	
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
	
	private String formatAnagraficaColumns(Anagrafiche a, CacheCrm crmData) {
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
		result += crmData.getCustomerType()+SEP;
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
		result += ServerConstants.FORMAT_DAY_SQL.format(crmData.getModifiedDate());
		return result;
	}
	
	private String formatColumnGroupByPeriodicoOrder(Integer i, CacheCrm crmData) throws BusinessException {
		String result = "";
		try {
			String className = CacheCrm.class.getName();
			Method giftSubscriptionEndMethod = Class.forName(className).getMethod("getGiftSubscriptionEnd"+i);
			Method ownSubscriptionBeginMethod = Class.forName(className).getMethod("getOwnSubscriptionBegin"+i);
			Method ownSubscriptionBlockedMethod = Class.forName(className).getMethod("getOwnSubscriptionBlocked"+i);
			Method ownSubscriptionEndMethod = Class.forName(className).getMethod("getOwnSubscriptionEnd"+i);
			Method ownSubscriptionIdentifierMethod = Class.forName(className).getMethod("getOwnSubscriptionIdentifier"+i);
			Method subscriptionCreationDateMethod = Class.forName(className).getMethod("getSubscriptionCreationDate"+i);
			//codAbbo
			String codAbbo = (String) ownSubscriptionIdentifierMethod.invoke(crmData);
			if (codAbbo == null) codAbbo = "";
			//blocked
			String blocked = "";
			Boolean isBlocked = (Boolean) ownSubscriptionBlockedMethod.invoke(crmData);
			if (isBlocked != null) blocked = (isBlocked?"true":"false");
			//ownBegin
			String ownBegin = "";
			Date ownBeginDt = (Date) ownSubscriptionBeginMethod.invoke(crmData);
			if (ownBeginDt != null) ownBegin = ServerConstants.FORMAT_DAY_SQL.format(ownBeginDt);
			//ownEnd
			String ownEnd = "";
			Date ownEndDt = (Date) ownSubscriptionEndMethod.invoke(crmData);
			if (ownEndDt != null) ownEnd = ServerConstants.FORMAT_DAY_SQL.format(ownEndDt);
			//giftEnd
			String giftEnd = "";
			Date giftEndDt = (Date) giftSubscriptionEndMethod.invoke(crmData);
			if (giftEndDt != null) giftEnd = ServerConstants.FORMAT_DAY_SQL.format(giftEndDt);
			//creation
			String creation = "";
			Date creationDt = (Date) subscriptionCreationDateMethod.invoke(crmData);
			if (creationDt != null) creation = ServerConstants.FORMAT_DAY_SQL.format(creationDt);
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
		} catch (NoSuchMethodException | SecurityException | ClassNotFoundException |
				IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new BusinessException(e.getMessage(), e);
		}
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
