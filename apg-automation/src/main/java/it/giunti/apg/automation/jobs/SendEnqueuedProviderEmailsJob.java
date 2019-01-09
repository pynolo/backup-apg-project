package it.giunti.apg.automation.jobs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.VisualLogger;
import it.giunti.apg.core.business.AvvisiBusiness;
import it.giunti.apg.core.business.EmailProviderBusiness;
import it.giunti.apg.core.business.OutputComunicazioniBusiness;
import it.giunti.apg.core.persistence.EvasioniComunicazioniDao;
import it.giunti.apg.core.persistence.PeriodiciDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.EvasioniComunicazioni;
import it.giunti.apg.shared.model.Periodici;
import it.giunti.apg.soap.magnews.BatchEmailMessage;

public class SendEnqueuedProviderEmailsJob implements Job {
	
	static private Logger LOG = LoggerFactory.getLogger(SendEnqueuedProviderEmailsJob.class);

	@Override
	public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
		LOG.info("Started job '"+jobCtx.getJobDetail().getKey().getName()+"'");
		//param: letterePeriodici
		String letterePeriodici = (String) jobCtx.getMergedJobDataMap().get("letterePeriodici");
		if (letterePeriodici == null) throw new JobExecutionException("letterePeriodici non definito");
		if (letterePeriodici.equals("")) throw new JobExecutionException("letterePeriodici non definito");
		String[] lettereArray = letterePeriodici.split(AppConstants.STRING_SEPARATOR);
		//param: maxBatchSize
		String maxBatchSizeS = (String) jobCtx.getMergedJobDataMap().get("maxBatchSize");
		if (maxBatchSizeS == null) throw new JobExecutionException("maxBatchSize non definito");
		if (maxBatchSizeS.equals("")) throw new JobExecutionException("maxBatchSize non definito");
		Integer maxBatchSize = Integer.parseInt(maxBatchSizeS);
		//JOB
		Integer idRapporto;
		try {
			idRapporto = VisualLogger.get().createRapporto(
					jobCtx.getJobDetail().getKey().getName(),
					ServerConstants.DEFAULT_SYSTEM_USER);
		} catch (BusinessException e) {
			throw new JobExecutionException(e);
		}
		// SPEDIZIONE
		Session ses = SessionFactory.getSession();
		try {
			//identificativo invio
			String idMessageType = "0";//TODO
			String avviso = "";
			Date now = DateUtil.now();
			
			//Ciclo su tutti i periodici
			for (String l:lettereArray) {
				Periodici periodico = new PeriodiciDao().findByUid(ses, l);
				avviso += periodico.getNome()+" ";
				
				//Cerca tutte le comunicazioni per i nuovi attivati e genera gli EvasioniComunicazioni
				VisualLogger.get().addHtmlInfoLine(idRapporto, "Spedizione email accodate per <b>"+periodico.getNome()+"</b>");
				List<EvasioniComunicazioni> ecList =
						findEnqueuedEmailByPeriodico(idRapporto, periodico.getId());
				if (ecList == null) ecList = new ArrayList<EvasioniComunicazioni>();
				//Creazione report interno
				if (ecList.size() == 0) {
					VisualLogger.get().addHtmlInfoLine(idRapporto, "Nessuna email da spedire");
				} else {
					//Spedizione e scrittura su DB
					VisualLogger.get().addHtmlInfoLine(idRapporto, "Spedizione e scrittura su DB in corso");
					//Split into smaller lists
					List<List<EvasioniComunicazioni>> listOfLists = splitEcList(ecList, maxBatchSize);
					
					//TRANSACTIONS
					Transaction trn = ses.beginTransaction();
					for (List<EvasioniComunicazioni> subList:listOfLists) {
						//For each smaller list;
						//Write on DB
						OutputComunicazioniBusiness.writeEvasioniComunicazioniOnDb(ses,
								ecList, now);
						//Create a batch and send
						List<BatchEmailMessage> batchEmailList = EmailProviderBusiness
								.createBatchEmailMessageList(subList,
									ServerConstants.PROVIDER_EMAIL_FROM_EMAIL,
									ServerConstants.PROVIDER_EMAIL_FROM_NAME,
									idMessageType);
						EmailProviderBusiness.batchSendEmailMessage(batchEmailList);
						//next transaction
						trn.commit();
						trn = ses.beginTransaction();
					}
					trn.commit();
					
					VisualLogger.get().addHtmlInfoLine(idRapporto, "Spedizione e scrittura su DB terminata: "+
								"inviate "+ecList.size()+" email");
				}
			}
			//Avviso
			if (avviso.length() > 0) {
				avviso = "Invio automatico email "+avviso;
				AvvisiBusiness.writeAvviso(avviso, false, ServerConstants.DEFAULT_SYSTEM_USER);
			}
		} catch (HibernateException | BusinessException e) {
			VisualLogger.get().addHtmlErrorLine(idRapporto, e.getMessage(), e);
			throw new JobExecutionException(e);
		} finally {
			String titolo = "Invio automatico email in coda";
			VisualLogger.get().setLogTitle(idRapporto, titolo);
			try {
				VisualLogger.get().closeAndSaveRapporto(idRapporto);
			} catch (BusinessException e) {
				LOG.error(e.getMessage(), e);
				throw new JobExecutionException(e);
			}
		}
		LOG.info("Ended job '"+jobCtx.getJobDetail().getKey().getName()+"'");
	}
	
	private List<EvasioniComunicazioni> findEnqueuedEmailByPeriodico(
			Integer idRapporto, Integer idPeriodico) throws BusinessException {
		Session ses = SessionFactory.getSession();
		List<EvasioniComunicazioni> result = null;
		try {
			result = new EvasioniComunicazioniDao()
					.findEnqueuedComunicazioniByMedia(ses,
						idPeriodico,
						AppConstants.COMUN_MEDIA_PROVIDER,
						idRapporto);
		} catch (HibernateException e) {
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	private static List<List<EvasioniComunicazioni>> splitEcList(List<EvasioniComunicazioni> ecList,
			Integer maxBatchSize) {
		List<List<EvasioniComunicazioni>> listOfList = new ArrayList<List<EvasioniComunicazioni>>();
		List<EvasioniComunicazioni> subList = new ArrayList<EvasioniComunicazioni>();
		for (int i = 0; i < ecList.size(); i++) {
			subList.add(ecList.get(i));
			if (i % maxBatchSize == 0) {
				listOfList.add(subList);
				subList = new ArrayList<EvasioniComunicazioni>();
			}
		}
		if (subList.size() > 0) listOfList.add(subList);
		return listOfList;
	}
	
}
