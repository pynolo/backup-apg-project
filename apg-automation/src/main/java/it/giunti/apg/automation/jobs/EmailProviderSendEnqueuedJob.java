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

public class EmailProviderSendEnqueuedJob implements Job {
	
	static private Logger LOG = LoggerFactory.getLogger(EmailProviderSendEnqueuedJob.class);

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
		String avviso = "";
		
		// SPEDIZIONE
		Session ses = SessionFactory.getSession();
		Transaction trn = null;
		try {
			Date now = DateUtil.now();
			//Ciclo su tutti i periodici
			for (String l:lettereArray) {
				trn = ses.beginTransaction();
				
				Periodici periodico = new PeriodiciDao().findByUid(ses, l);
				avviso += periodico.getNome()+" ";
				
				//Cerca tutte le comunicazioni per i nuovi attivati e genera gli EvasioniComunicazioni
				VisualLogger.get().addHtmlInfoLine(idRapporto, "Ricerca email da inviare per <b>"+periodico.getNome()+"</b>");
				List<EvasioniComunicazioni> ecList =
						findEnqueuedProviderEmailByPeriodico(idRapporto, ses, periodico.getId());
				if (ecList == null) ecList = new ArrayList<EvasioniComunicazioni>();
				//Creazione report interno
				if (ecList.size() == 0) {
					VisualLogger.get().addHtmlInfoLine(idRapporto, "Nessuna email da inviare");
				} else {
					//Esistono dati da elaborare
					VisualLogger.get().addHtmlInfoLine(idRapporto, "Elaborazione in corso: "+ecList.size()+" messaggi");
					//Split into smaller lists
					List<List<EvasioniComunicazioni>> listOfLists = splitEcList(ecList, maxBatchSize);
					
					//TRANSACTIONS
					int count = 0;
					int countDiff = 0;
					for (List<EvasioniComunicazioni> subList:listOfLists) {
						//For each smaller list;
						count += subList.size();
						//Creazione contenuti
						VisualLogger.get().addHtmlInfoLine(idRapporto, "Creazione contenuti in corso "+count+"/"+ecList.size());
						List<BatchEmailMessage> batchEmailList = EmailProviderBusiness
								.createBatchEmailMessageList(ses, subList,
									ServerConstants.PROVIDER_EMAIL_FROM_EMAIL,
									ServerConstants.PROVIDER_EMAIL_FROM_NAME,
									ServerConstants.PROVIDER_EMAIL_REPLY_TO, idRapporto);
						int diff = subList.size()-batchEmailList.size();
						//Write on DB
						VisualLogger.get().addHtmlInfoLine(idRapporto, "Salvataggio locale in corso "+count+"/"+ecList.size());
						OutputComunicazioniBusiness.writeEvasioniComunicazioniOnDb(ses,	subList, now);
						//Create a batch and send
						VisualLogger.get().addHtmlInfoLine(idRapporto, "Invio al provider in corso "+count+"/"+ecList.size());
						EmailProviderBusiness.batchSendEmailMessage(ses, batchEmailList);
						//EC senza email
						if (diff > 0) {
							countDiff += diff;
							VisualLogger.get().addHtmlInfoLine(idRapporto, "Anagrafiche senza email "+countDiff+"/"+count);
						}
						//next transaction
						trn.commit();
						trn = ses.beginTransaction();
					}
					trn.commit();
					VisualLogger.get().addHtmlInfoLine(idRapporto, "Invio e salvataggio locale terminato:");
					VisualLogger.get().addHtmlInfoLine(idRapporto, ecList.size()+" istanze elaborate");
					VisualLogger.get().addHtmlInfoLine(idRapporto, (ecList.size()-countDiff)+" email inviate");
					VisualLogger.get().addHtmlInfoLine(idRapporto, countDiff+" anagrafiche senza email");
				}
			}
		} catch (HibernateException | BusinessException e) {
			trn.rollback();
			LOG.error(e.getMessage(), e);
			VisualLogger.get().addHtmlErrorLine(idRapporto, e.getMessage(), e);
			throw new JobExecutionException(e);
		} finally {
			ses.close();
			String titolo = "Invio email in coda tramite provider";
			VisualLogger.get().setLogTitle(idRapporto, titolo);
			try {
				VisualLogger.get().closeAndSaveRapporto(idRapporto);
			} catch (BusinessException e) {
				LOG.error(e.getMessage(), e);
				throw new JobExecutionException(e);
			}
		}
		//Avviso
		if (avviso.length() > 0) {
			avviso = "Invio email in coda tramite provider "+avviso;
			try {
				AvvisiBusiness.writeAvviso(avviso, false, ServerConstants.DEFAULT_SYSTEM_USER);
			} catch (BusinessException e) {
				LOG.error(e.getMessage(), e);
				throw new JobExecutionException(e);
			}
		}
		LOG.info("Ended job '"+jobCtx.getJobDetail().getKey().getName()+"'");
	}
	
	private List<EvasioniComunicazioni> findEnqueuedProviderEmailByPeriodico(
			Integer idRapporto, Session ses, Integer idPeriodico) throws BusinessException {
		List<EvasioniComunicazioni> result = null;
		try {
			result = new EvasioniComunicazioniDao()
					.findEnqueuedComunicazioniByMedia(ses,
						idPeriodico,
						AppConstants.COMUN_MEDIA_PROVIDER,
						idRapporto);
		} catch (HibernateException e) {
			throw new BusinessException(e.getMessage(), e);
		}
		return result;
	}
	
	private static List<List<EvasioniComunicazioni>> splitEcList(List<EvasioniComunicazioni> ecList,
			Integer maxBatchSize) {
		List<List<EvasioniComunicazioni>> listOfList = new ArrayList<List<EvasioniComunicazioni>>();
		List<EvasioniComunicazioni> subList = new ArrayList<EvasioniComunicazioni>();
		for (int i = 0; i < ecList.size(); i++) {
			subList.add(ecList.get(i));
			if ((i+1) % maxBatchSize == 0) {
				listOfList.add(subList);
				subList = new ArrayList<EvasioniComunicazioni>();
			}
		}
		if (subList.size() > 0) listOfList.add(subList);
		return listOfList;
	}
	
}
