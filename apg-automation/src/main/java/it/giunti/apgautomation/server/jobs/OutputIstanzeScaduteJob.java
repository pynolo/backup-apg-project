package it.giunti.apgautomation.server.jobs;

import it.giunti.apg.server.PropertyReader;
import it.giunti.apg.server.business.FileFormatInvio;
import it.giunti.apg.server.persistence.FascicoliDao;
import it.giunti.apg.server.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.FileException;
import it.giunti.apg.shared.model.Fascicoli;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Periodici;
import it.giunti.apgautomation.server.business.EntityBusiness;
import it.giunti.apgautomation.server.business.ReportUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.BooleanType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OutputIstanzeScaduteJob implements Job {
	
	static private Logger LOG = LoggerFactory.getLogger(OutputIstanzeScaduteJob.class);
	static private String FILE_NAME_PREFIX = "scaduti";
	
	@Override
	public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
		//DEBUG try { today = ServerConstants.FORMAT_DAY.parse("01/03/2012");
		//} catch (ParseException e1) {e1.printStackTrace();}
		String jobName = jobCtx.getJobDetail().getKey().getName();
		LOG.info("Started job '"+jobName+"'");
		//param: letterePeriodici
		String letterePeriodici = (String) jobCtx.getMergedJobDataMap().get("letterePeriodici");
		if (letterePeriodici == null) throw new JobExecutionException("letterePeriodici non definito");
		if (letterePeriodici.equals("")) throw new JobExecutionException("letterePeriodici non definito");
		String[] lettereArray = letterePeriodici.split(AppConstants.STRING_SEPARATOR);
		//param: tipiAbbonamento
		String tipiAbbonamento = (String) jobCtx.getMergedJobDataMap().get("tipiAbbonamento");
		if (tipiAbbonamento == null) throw new JobExecutionException("tipiAbbonamento non definito");
		if (tipiAbbonamento.equals("")) throw new JobExecutionException("tipiAbbonamento non definito");
		String[] tipiAbbonamentoArray = tipiAbbonamento.split(AppConstants.STRING_SEPARATOR);
		//param: ftpSubDir
		String ftpSubDir = (String) jobCtx.getMergedJobDataMap().get("ftpSubDir");
		if (ftpSubDir == null) throw new JobExecutionException("ftpSubDir non definito");
		//param: suffix
		String suffix = PropertyReader.getApgStatus();
		if (suffix == null) suffix = AppConstants.APG_DEV;
		if (suffix.length() == 0) suffix = AppConstants.APG_DEV;
		if (AppConstants.APG_PROD.equalsIgnoreCase(suffix)) suffix = "";
		//JOB
		Session ses = SessionFactory.getSession();
		FascicoliDao fasDao = new FascicoliDao();
		try {
			List<Periodici> periodici = EntityBusiness.periodiciFromUidArray(ses, lettereArray);
			int scadutiCount = 0;
			for (Periodici periodico:periodici) {
				Fascicoli fasAttivo = fasDao.findFascicoloByPeriodicoDataInizio(ses,
						periodico.getId(), new Date());
				//String body ="Scadenze '"+periodico.getNome()+"'\r\n" +
				//		"Tipi monitorati: "+tipiAbbonamento+"\r\n\r\n"+
				//		"Il file allegato contiene gli abbonamenti che hanno come " +
				//		"ultimo fascicolo il "+fasAttivo.getTitoloNumero()+" '" + fasAttivo.getDataCop() +"'\r\n";
				List<IstanzeAbbonamenti> iaList = findScadutiByPeriodicoTipi(ses,
						fasAttivo, tipiAbbonamentoArray);
				Date dataEstrazione = new Date();
				File attachment = createReportFile(ses, iaList, fasAttivo, dataEstrazione);
				if (attachment != null) {
					String nameSuffix = fasAttivo.getTitoloNumero()+" "+periodico.getNome()+suffix;
					ReportUtil.exportReportToFtp(ses, null, attachment, ftpSubDir, FILE_NAME_PREFIX,
							nameSuffix, periodico.getIdSocieta(), "txt", dataEstrazione);
					////Spedisce il report
					//sendReport("[APG] In scadenza "+periodico.getNome(),
					//	recipientArray, body, attachment);
					scadutiCount++;
				}
			}
			if (scadutiCount == 0) LOG.info(jobName+": nessuna scadenza i periodici "+letterePeriodici);
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

	@SuppressWarnings("unchecked")
	private List<IstanzeAbbonamenti> findScadutiByPeriodicoTipi(Session ses, Fascicoli fas,
			String[] tipiAbbonamentoArray) throws BusinessException {
		List<IstanzeAbbonamenti> iaList = new ArrayList<IstanzeAbbonamenti>();
		//Query per trovare gli abbonamenti scaduti in data odierna,
		//fissando periodico e codiceTipoAbbonamento
		String qs = "from IstanzeAbbonamenti ia where " +
				"ia.listino.tipoAbbonamento.codice like :s1 and " +
				"ia.fascicoloFine.id = :id1 and " +
				"ia.dataDisdetta is null and " +
				"ia.invioBloccato = :b1 and " +//FALSE
				"ia.ultimaDellaSerie = :b2 ";//TRUE
		try {
			for (String codice:tipiAbbonamentoArray) {
				Query q = ses.createQuery(qs);
				q.setParameter("s1", codice, StringType.INSTANCE);
				q.setParameter("id1", fas.getId(), IntegerType.INSTANCE);
				q.setParameter("b1", Boolean.FALSE, BooleanType.INSTANCE);
				q.setParameter("b2", Boolean.TRUE, BooleanType.INSTANCE);
				List<IstanzeAbbonamenti> list = q.list();
				if (list != null) {
					iaList.addAll(list);
				}
			}
		} catch (HibernateException e) {
			throw new BusinessException(e.getMessage(), e);
		}
		return iaList;
	}
	
	
	private File createReportFile(Session ses, List<IstanzeAbbonamenti> iaList,
			Fascicoli fas, Date dataEstrazione)  throws BusinessException, FileException {
		//A questo punto la lista degli scaduti è completa
		//Deve essere creato il file
		if (iaList.size() > 0) {
			File result = null;
			try {
				result = File.createTempFile("In scadenza "+fas.getPeriodico().getNome()+" ", ".txt");
				result.deleteOnExit();
				formatInviiRegolari(ses, result, iaList, fas, dataEstrazione);
			} catch (IOException e) {
				throw new FileException(e.getMessage(), e);
			}
			return result;
		} else {
			return null;
		}
	}
	
	private void formatInviiRegolari(Session ses, File destFile,
			List<IstanzeAbbonamenti> iaList, Fascicoli fas, Date dataEstrazione)
			throws BusinessException, FileException {
		try {
			FileOutputStream fos = new FileOutputStream(destFile);
			OutputStreamWriter fileWriter = new OutputStreamWriter(fos, AppConstants.CHARSET);
			FileFormatInvio.createIndirizzarioFileContent(ses,
					iaList,
					fas,
					dataEstrazione,
					fileWriter,
					null);
			fileWriter.close();
		} catch (FileNotFoundException e) {
			throw new FileException(e.getMessage(), e);
		} catch (IOException e) {
			throw new FileException(e.getMessage(), e);
		}
	}
	
//	private void sendReport(String subject, String[] recipients,
//			String messageBody, File attachment) {
//		try {
//			Mailer.postMail(ServerConstants.SMTP_HOST,
//					ServerConstants.SMTP_USER,
//					ServerConstants.SMTP_PASSWORD,
//					ServerConstants.SMTP_FROM,
//					recipients, subject, messageBody, attachment);
//		} catch (Exception e) {
//			LOG.error(e.getMessage(), e);
//		}
//	}
}
