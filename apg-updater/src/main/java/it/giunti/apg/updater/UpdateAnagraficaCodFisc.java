package it.giunti.apg.updater;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.giunti.apg.core.persistence.AnagraficheDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.Anagrafiche;

public class UpdateAnagraficaCodFisc {
	private static final Logger LOG = LoggerFactory.getLogger(UpdateAnagraficaCodFisc.class);
	
	private static final String SEP = ";"; //SEPARATOR_REGEX
	private static final SimpleDateFormat FORMAT_DAY = new SimpleDateFormat("dd-MM-yyyy");
	private static AnagraficheDao anagDao = new AnagraficheDao();
	
	
	public static void updateAnagraficaFormCsv(String csvFilePath) 
			throws BusinessException, IOException {
		File csvFile = new File(csvFilePath);
		FileInputStream fstream = new FileInputStream(csvFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		StringBuffer logBuffer = new StringBuffer();
		
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			System.out.print("\r\n");
			int count = 0;
			String line = br.readLine();
			while (line != null) {
				updateCodFisc(ses, line, logBuffer);
				//updateEmailCodiceCliente(ses, line);
				count++;
				line = br.readLine();
				ses.flush();
				ses.clear();
			}
			LOG.info("Aggiornate "+count+" anagrafiche");
			trn.commit();
		} catch (HibernateException e) {
			trn.rollback();
			throw new BusinessException(e.getMessage(), e);
		} catch (IOException e) {
			trn.rollback();
			throw new IOException(e.getMessage(), e);
		} finally {
			ses.close();
			br.close();
			fstream.close();
		}
		File logFile = File.createTempFile("updateCodFis", ".txt");
		PrintWriter out = new PrintWriter(logFile);
		out.append(logBuffer.toString());
		out.close();
		LOG.info("LOG FILE: "+logFile.getAbsolutePath()+"\r\n\r\n");
		LOG.info(logBuffer.toString());
	}
	
	private static void updateCodFisc(Session ses, String line, StringBuffer logBuffer) 
			throws HibernateException, IOException {
		logBuffer.append("uid"+SEP+"old_cf"+SEP+"cf"+SEP+"old_pi"+SEP+"pi"+SEP+"old_email"+SEP+"email");
		String[] values = line.split(SEP);
		//String cognome;
		//String nome;
		String email;
		String cf;
		String pi;
		String uid;
		Date modifiedDate;
		try {
			//cognome = values[0].trim();
			//nome = values[1].trim();
			email = values[2].toLowerCase().trim();
			cf = values[3].toUpperCase().trim();
			pi = values[4].toLowerCase().trim();
			uid = values[5].toUpperCase().trim();
			String modified = values[6].toUpperCase().trim();
			modifiedDate = FORMAT_DAY.parse(modified);
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
		Anagrafiche anag = anagDao.findByUid(ses, uid);
		if (verificaCodici(cf, pi)) {
			if (anag != null) {
				//Verifica vecchio CF
				String oldCf = anag.getCodiceFiscale();
				String oldCfLog = oldCf;
				boolean isOldCfValid = ValueUtil.isValidCodFisc(anag.getCodiceFiscale(), AppConstants.DEFAULT_ID_NAZIONE_ITALIA);
				if (!isOldCfValid) oldCf = "";
				//Verifica vecchia PI
				String oldPi = anag.getPartitaIva();
				String oldPiLog = oldPi;
				boolean isOldPiValid = ValueUtil.isValidPIva(oldPi, AppConstants.DEFAULT_ID_NAZIONE_ITALIA);
				if (!isOldPiValid) oldPi = "";
				//Verifica vecchia EMAIL
				String oldEmail = anag.getEmailPrimaria();
				String oldEmailLog = oldEmail;
				boolean isOldEmailValid = ValueUtil.isValidEmail(oldEmail);
				if (!isOldEmailValid) oldEmail = "";
				
				anag.setCodiceFiscale(vuotoPerPieno(oldCf, cf, anag.getDataModifica(), modifiedDate));
				anag.setPartitaIva(vuotoPerPieno(oldPi, pi, anag.getDataModifica(), modifiedDate));
				anag.setEmailPrimaria(vuotoPerPieno(oldEmail, email, anag.getDataModifica(), modifiedDate));

				anagDao.update(ses, anag);
				String result = uid + SEP + oldCfLog + SEP + anag.getCodiceFiscale() + SEP +
						oldPiLog + SEP + anag.getPartitaIva() + SEP +
						oldEmailLog + SEP + anag.getEmailPrimaria();
				logBuffer.append(result+"\r\n");
				LOG.info(result);
			} else {
				logBuffer.append("ERR_ANAGR: "+uid+" \r\n");
				LOG.info("ERR_ANAGR: "+uid);
			}
		} else {
			logBuffer.append("ERR_CODICI uid:"+uid+" cf:"+cf+" pi:"+pi+"\r\n");
			LOG.info("ERR_CODICI uid:"+uid+" cf:"+cf+" pi:"+pi);
		}
	}

	private static String vuotoPerPieno(String oldVal, String newVal, Date dbDt, Date fileDt) {
		String result = "";
		if (oldVal == null) oldVal = "";
		if (newVal == null) newVal = "";
		if (oldVal.length() < 1) {
			result = newVal;
		} else {
			if (newVal.length() < 1) {
				result = "";
			} else {
				if (dbDt.before(fileDt)) {
					result = newVal;
				} else {
					result = oldVal;
				}
			}
		}
		return result;
	}
	
	private static boolean verificaCodici(String cf, String pi) {
		boolean ok = true;
		//cod_fisc - codice fiscale 
		boolean isCfValid = false;
		if (cf != null) {
			isCfValid = ValueUtil.isValidCodFisc(cf, AppConstants.DEFAULT_ID_NAZIONE_ITALIA);
		}
		//piva - partita iva
		boolean isPiValid = false;
		if (pi != null) {
			isPiValid = ValueUtil.isValidPIva(pi, AppConstants.DEFAULT_ID_NAZIONE_ITALIA);
		}
		ok = isCfValid || isPiValid;
		return ok;
	}
}
