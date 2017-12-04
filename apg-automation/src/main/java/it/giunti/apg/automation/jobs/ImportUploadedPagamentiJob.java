package it.giunti.apg.automation.jobs;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.VisualLogger;
import it.giunti.apg.core.business.PagamentiImportBusiness;
import it.giunti.apg.core.persistence.FileUploadsDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.FileUploads;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImportUploadedPagamentiJob implements Job {
	
	static private Logger LOG = LoggerFactory.getLogger(ImportUploadedPagamentiJob.class);
	private static final int BUFSIZE = 2048;
	
	@Override
	public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
		String jobName = jobCtx.getJobDetail().getKey().getName();
		
		LOG.info("Started job '"+jobName+"'");
		Session ses = SessionFactory.getSession();
		Transaction trx = ses.beginTransaction();
		List<FileUploads> fuList = null;
		try {
			fuList = GenericDao.findByClass(ses, FileUploads.class, "id");
			trx.commit();
		} catch (Exception e) {
			LOG.info("ERROR in job '"+jobName+"'");
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new JobExecutionException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (fuList != null) {
			if (fuList.size() > 0) {
				try {
					importPagamenti(fuList);
					deleteFileUploads(fuList);
				} catch (BusinessException | IOException e) {
					throw new JobExecutionException(e.getMessage(), e);
				}
			}
		}
		LOG.info("Ended job '"+jobName+"'");
	}
	
	private static void importPagamenti(List<FileUploads> fuList) 
			throws BusinessException, IOException {
		PagamentiImportBusiness business = new PagamentiImportBusiness();
		for (FileUploads fu:fuList) {
			//RAPPORTO INIZIO
			int idRapporto = VisualLogger.get().createRapporto("Importazione pagamenti "+fu.getFileName(), ServerConstants.DEFAULT_SYSTEM_USER);
			
			File f = File.createTempFile(fu.getFileName(), ".tmp");
			//File data
			InputStream bais = new ByteArrayInputStream(fu.getContent());
			FileOutputStream fos = new FileOutputStream(f);
			int length=0;
			byte[] bbuf = new byte[BUFSIZE];
	        while ((bais != null) && ((length = bais.read(bbuf)) != -1)) {
	            fos.write(bbuf,0,length);
	        }
			fos.close();
			//Import
			business.importPagamenti(f, idRapporto, fu.getIdUtente());
			//Delete
			//RAPPORTO FINE
			VisualLogger.get().closeAndSaveRapporto(idRapporto);
		}
	}
	
	private static void deleteFileUploads(List<FileUploads> fuList) 
			throws BusinessException {
		Session ses = SessionFactory.getSession();
		Transaction trx = ses.beginTransaction();
		try {
			for (FileUploads fu:fuList) {
				new FileUploadsDao().delete(ses, fu);
			}
			trx.commit();
		} catch (HibernateException e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
	}
}
