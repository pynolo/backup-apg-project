package it.giunti.apgautomation.server.jobs;

import it.giunti.apg.server.PropertyReader;
import it.giunti.apg.server.ServerConstants;
import it.giunti.apg.server.VisualLogger;
import it.giunti.apg.server.business.AvvisiBusiness;
import it.giunti.apg.server.business.FileFormatComunicazioni;
import it.giunti.apg.server.business.OutputComunicazioniBusiness;
import it.giunti.apg.server.business.SortBusiness;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.FileException;
import it.giunti.apg.shared.model.EvasioniComunicazioni;
import it.giunti.apg.shared.model.Periodici;
import it.giunti.apgautomation.server.business.EntityBusiness;
import it.giunti.apgautomation.server.business.ReportUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OutputComunicazioniByStatusJob implements Job {
	
	//private static final long serialVersionUID = 4394668127625471725L;
	static private Logger LOG = LoggerFactory.getLogger(OutputComunicazioniByStatusJob.class);
	
	private List<String> idTipoMediaList = new ArrayList<String>();
	
	@Override
	public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
		String jobName = jobCtx.getJobDetail().getKey().getName();
		LOG.info("Started job '"+jobName+"'");
		Date today = new Date();
		//param: letterePeriodici
		String letterePeriodici = (String) jobCtx.getMergedJobDataMap().get("letterePeriodici");
		if (letterePeriodici == null) throw new JobExecutionException("letterePeriodici non definito");
		if (letterePeriodici.equals("")) throw new JobExecutionException("letterePeriodici non definito");
		String[] lettereArray = letterePeriodici.split(AppConstants.STRING_SEPARATOR);
		//param: ftpSubDir
		String ftpSubDir = (String) jobCtx.getMergedJobDataMap().get("ftpSubDir");
		if (ftpSubDir == null) throw new JobExecutionException("ftpSubDir non definito");
		//param: idTipoMediaList
		String idTipoMediaListParam = (String) jobCtx.getMergedJobDataMap().get("idTipoMediaList");
		if (idTipoMediaListParam != null) {
			String[] array = idTipoMediaListParam.split(AppConstants.STRING_SEPARATOR);
			for (String s:array) {
				idTipoMediaList.add(s);
			}
		}
		//param: suffix
		String suffix = PropertyReader.getApgStatus();
		if (suffix == null) suffix = AppConstants.APG_DEV;
		if (suffix.length() == 0) suffix = AppConstants.APG_DEV;
		if (AppConstants.APG_PROD.equalsIgnoreCase(suffix)) suffix = "";
		//JOB
		Integer idRapporto;
		try {
			idRapporto = VisualLogger.get().createRapporto(
					jobCtx.getJobDetail().getKey().getName(),
					ServerConstants.DEFAULT_SYSTEM_USER);
		} catch (BusinessException e) {
			LOG.error(e.getMessage(), e);
			throw new JobExecutionException(e);
		}
		// Extract arretrati
		try {
			String avviso = "";
			//Ciclo su tutti i periodici
			List<Periodici> periodici = EntityBusiness.periodiciFromUidArray(lettereArray);
			for (Periodici periodico:periodici) {
				//Cerca tutte le comunicazioni per i nuovi attivati e genera gli EvasioniComunicazioni
				VisualLogger.get().addHtmlInfoLine(idRapporto, "Estrazione comunicazioni in base allo stato per <b>"+periodico.getNome()+"</b>");
				// Extract destinatari
				List<EvasioniComunicazioni> ecList = new ArrayList<EvasioniComunicazioni>();
				String tipiMediaDesc = "";
				for (String idTipoMedia:idTipoMediaList) {
					List<EvasioniComunicazioni> list = OutputComunicazioniBusiness
							.findOrCreateEvasioniComunicazioniProgrammate(
								today, periodico.getId(),
								idTipoMedia, AppConstants.COMUN_ATTIVAZ_PER_STATUS,
								null,
								idRapporto, ServerConstants.DEFAULT_SYSTEM_USER);
					ecList.addAll(list);
					if (tipiMediaDesc.length() > 0) tipiMediaDesc += " ";
					tipiMediaDesc += AppConstants.COMUN_MEDIA_DESC_PLUR.get(idTipoMedia);
				}
				if (ecList.size() == 0) {
					VisualLogger.get().addHtmlInfoLine(idRapporto, "Nessun dato da estrarre");
				} else {
					//Ordinamento secondo cap e nazione
					VisualLogger.get().addHtmlInfoLine(idRapporto, "Ordinamento per cap e nazione");
					new SortBusiness().sortEvasioniComunicazioni(ecList);
					//Creazione file
					VisualLogger.get().addHtmlInfoLine(idRapporto, "Formattazione dati");
					File f = File.createTempFile("destinatariCom"+periodico.getUid(), ".csv");
					f.deleteOnExit();
					FileFormatComunicazioni.formatEvasioniComunicazioni(f, ecList, idRapporto);
					//Invia file all'ftp
					ReportUtil.exportReportToFtp(idRapporto, f, ftpSubDir, tipiMediaDesc,
							periodico.getNome()+suffix, periodico.getIdSocieta(), "csv", today);
					//marca come stampati
					VisualLogger.get().addHtmlInfoLine(idRapporto, "Scrittura su DB in corso");
					OutputComunicazioniBusiness.writeEvasioniComunicazioniOnDb(
							ecList, today, idRapporto, ServerConstants.DEFAULT_SYSTEM_USER);
					if (ecList.size() > 0) avviso += "'"+periodico.getNome()+"' ";
					VisualLogger.get().addHtmlInfoLine(idRapporto, "Scrittura su DB terminata");
				}
			}
			//Avviso
			if (avviso.length() > 0) {
				String tipo = "";
				for (String idTipoMedia:idTipoMediaList) {
					tipo += AppConstants.COMUN_MEDIA_DESC.get(idTipoMedia)+" ";
				}
				avviso = "Estrazione automatica "+tipo+" per "+avviso;
				AvvisiBusiness.writeAvviso(avviso, false, ServerConstants.DEFAULT_SYSTEM_USER);
			}
		} catch (BusinessException e) {
			VisualLogger.get().addHtmlErrorLine(idRapporto, "ERROR: "+e.getMessage(), e);
			throw new JobExecutionException(e);
		} catch (NumberFormatException e) {
			VisualLogger.get().addHtmlErrorLine(idRapporto, "ERROR: "+e.getMessage(), e);
			throw new JobExecutionException(e);
		} catch (IOException e) {
			VisualLogger.get().addHtmlErrorLine(idRapporto, "ERROR: "+e.getMessage(), e);
			throw new JobExecutionException(e);
		} catch (FileException e) {
			VisualLogger.get().addHtmlErrorLine(idRapporto, "ERROR: "+e.getMessage(), e);
			throw new JobExecutionException(e);
		} finally {
			String titolo = "Report automatico ";
			for (String idTipoMedia:idTipoMediaList) {
				titolo += AppConstants.COMUN_MEDIA_DESC.get(idTipoMedia)+" ";
			}
			titolo += "per stato abbonamento";
			VisualLogger.get().setLogTitle(idRapporto, titolo);
			try {
				VisualLogger.get().closeAndSaveRapporto(idRapporto);
			} catch (BusinessException e) {
				LOG.error(e.getMessage(), e);
				throw new JobExecutionException(e);
			}
		}
		LOG.info("Ended job '"+jobName+"'");
	}
	
}
