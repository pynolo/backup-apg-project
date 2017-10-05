package it.giunti.apg.automation.servlet;

import it.giunti.apg.automation.business.FatturePdfBusiness;
import it.giunti.apg.automation.business.FattureTxtBusiness;
import it.giunti.apg.core.ConfigUtil;
import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.business.FtpBusiness;
import it.giunti.apg.core.business.FtpConfig;
import it.giunti.apg.core.persistence.ConfigDao;
import it.giunti.apg.core.persistence.FattureDao;
import it.giunti.apg.core.persistence.FattureStampeDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.Fatture;
import it.giunti.apg.shared.model.FattureStampe;
import it.giunti.apg.shared.model.Societa;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class RebuildFatturaServlet extends HttpServlet {
	private static final long serialVersionUID = 510346861934804813L;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}
	
	//Process the HTTP Get request
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		//param: numeroFattura
		String numeroFattura = request.getParameter(AppConstants.PARAM_NAME);
		if (numeroFattura == null) throw new ServletException("No name=numeroFattura supplied");
		if (numeroFattura.equals("")) throw new ServletException("No name=numeroFattura supplied");
		//param: prod
		boolean prod = ConfigUtil.isApgProd();
		//param: debug
		boolean debug = false;
		
		Date now = DateUtil.now();
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			String debugString = new ConfigDao().findValore(ses, "RebuildFatturaServlet_debug");
  			if (debugString != null) debug = debugString.equalsIgnoreCase("true");
  			
			List<Fatture> fattureList = new FattureDao().findByNumeroFattura(ses, numeroFattura);
			if (fattureList == null) throw new ServletException("Incorrect numeroFattura");
			if (fattureList.size() == 0) throw new ServletException("Incorrect numeroFattura");
			if (fattureList.size() > 1) throw new ServletException("More occurrencies of numeroFattura");
			Fatture fattura = fattureList.get(0);
			//TIME FRAME CHECK
			//E' possibile agire solamente sulle fatture dell'anno corrente oppure dell'anno
			//precedente solo se non si è concluso da più di sei mesi: dal 1° giugno 2016
			//non è più possibile editare tutte de fatture del 2015
			Date firstJanuary = getCurrent1stJanuary();
			Date firstJune = getCurrent1stJune();
			boolean prevYearBlocked = firstJune.before(DateUtil.now());//quest'anno è passato giugno
			for (Fatture f:fattureList) {
				if (firstJanuary.after(f.getDataFattura())) {
					//fattura dell'anno precedente
					if (prevYearBlocked) throw new ServletException("Fattura archiviata: non rigenerabile");
				}
				if (f.getDataModifica() != null) {
					Long tDiff = now.getTime() - f.getDataModifica().getTime();
					if (tDiff < (12*AppConstants.HOUR))
							throw new ServletException("Fattura modificata: non rigenerabile entro 24h");
				}
			}
			//Re-draw the bill
			FattureStampe stampa = FatturePdfBusiness.createTransientStampaFattura(ses, fattura);
			//Save the bill .pdf data to database replacing the old one
			FattureDao fDao = new FattureDao();
			FattureStampeDao fsDao = new FattureStampeDao();
			FattureStampe oldStampa = GenericDao.findById(ses, FattureStampe.class, fattura.getIdFatturaStampa());
			Integer idNewStampa = (Integer) fsDao.save(ses, stampa);
			fattura.setIdFatturaStampa(idNewStampa);
			fattura.setDataModifica(now);
			fDao.update(ses, fattura);
			if (oldStampa != null) fsDao.delete(ses, oldStampa);
			//Build the .pdf file
			File sfTmpFile = FatturePdfBusiness.createTempPdfFile(ses, stampa);
			
			//Build the companion .frd file
			Societa societa = GenericDao.findById(ses, Societa.class, fattura.getIdSocieta());
			File corFile = FattureTxtBusiness.createAccompagnamentoPdfFile(ses, fattureList, societa);
			//Write .pdf & .frd to remote ftp
			FtpConfig ftpConfig = null;
			if (prod) { 
				ftpConfig = ConfigUtil.loadFtpPdfBySocieta(ses, fattura.getIdSocieta());
				String pdfRemoteNameAndDir = ftpConfig.getDir()+"/"+stampa.getFileName();
				FtpBusiness.upload(ftpConfig.getHost(), ftpConfig.getPort(),
						ftpConfig.getUsername(), ftpConfig.getPassword(),
						pdfRemoteNameAndDir, sfTmpFile);
				String frdRemoteNameAndDir = ftpConfig.getDir()+"/"+societa.getCodiceSocieta()+
						"_datixarchi_"+ServerConstants.FORMAT_FILE_NAME_TIMESTAMP.format(DateUtil.now())+".frd";
				FtpBusiness.upload(ftpConfig.getHost(), ftpConfig.getPort(), ftpConfig.getUsername(), ftpConfig.getPassword(),
						frdRemoteNameAndDir, corFile);
			}
			if (!prod || debug) {
				ftpConfig = ConfigUtil.loadFtpFattureRegistri(ses, true);
				String pdfRemoteNameAndDir = ftpConfig.getDir()+"/"+stampa.getFileName();
				FtpBusiness.upload(ftpConfig.getHost(), ftpConfig.getPort(),
						ftpConfig.getUsername(), ftpConfig.getPassword(),
						pdfRemoteNameAndDir, sfTmpFile);
				String frdRemoteNameAndDir = ftpConfig.getDir()+"/"+societa.getCodiceSocieta()+
						"_datixarchi_"+ServerConstants.FORMAT_FILE_NAME_TIMESTAMP.format(DateUtil.now())+".frd";
				FtpBusiness.upload(ftpConfig.getHost(), ftpConfig.getPort(), ftpConfig.getUsername(), ftpConfig.getPassword(),
						frdRemoteNameAndDir, corFile);
			}
			//Return .pdf to HTTP outputstream
			PrintWriter out = new PrintWriter(response.getOutputStream());
			ServletOutputStream binout = response.getOutputStream();
			response.setContentType(stampa.getMimeType());
			response.setHeader("Content-Disposition", "attachment;filename="+stampa.getFileName());
			byte[] sfBytes = stampa.getContent();
			binout.write(sfBytes);
			out.close();
			trn.commit();
		} catch (HibernateException | JRException | BusinessException e) {
			trn.rollback();
			throw new ServletException(e.getMessage());
		} catch (IOException e) {
			trn.rollback();
			throw new ServletException(e.getMessage());
		} finally {
			ses.close();
		}
	}
	
	private Date getCurrent1stJanuary() {
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		return cal.getTime();
	}
	
	private Date getCurrent1stJune() {
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.MONTH, Calendar.JUNE);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		return cal.getTime();
	}
}
