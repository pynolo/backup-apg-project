package it.giunti.apg.updater;

import it.giunti.apg.core.persistence.AbbonamentiDao;
import it.giunti.apg.core.persistence.AnagraficheDao;
import it.giunti.apg.core.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.Abbonamenti;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateAnagraficaEmail {
	private static final Logger LOG = LoggerFactory.getLogger(UpdateAnagraficaEmail.class);
	
	private static final String SEPARATOR_REGEX = "\\;";
	private static AnagraficheDao anagDao = new AnagraficheDao();
	private static AbbonamentiDao aDao = new AbbonamentiDao();
	private static IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
	
	public static void updateAnagraficaFormCsv(String csvFilePath) 
			throws BusinessException, IOException {
		File csvFile = new File(csvFilePath);
		FileInputStream fstream = new FileInputStream(csvFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			System.out.print("\r\n");
			int count = 0;
			String line = br.readLine();
			while (line != null) {
				updateEmailCodiceAbbonamento(ses, line);
				//updateEmailCodiceCliente(ses, line);
				count++;
				line = br.readLine();
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
	}
	
	public static void updateEmailCodiceCliente(Session ses, String line) 
			throws HibernateException, IOException {
		String[] values = line.split(SEPARATOR_REGEX);
		String codiceCliente;
		String email;
		try {
			codiceCliente = values[0].toUpperCase();
			email = values[1].toLowerCase();
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
		Anagrafiche anag = anagDao.findByUid(ses, codiceCliente);
		if (anag != null) {
			boolean newEmailValid = ValueUtil.isValidEmail(email);
			boolean oldEmailValid = ValueUtil.isValidEmail(anag.getEmailPrimaria());
			if (newEmailValid || (!newEmailValid && !oldEmailValid)) {
				//Sostituisce se nuova email è valida o se vecchia e nuova non sono valide
				anag.setEmailPrimaria(email);
				anagDao.update(ses, anag);
				LOG.info(anag.getUid()+" updt:"+anag.getEmailPrimaria());
			} else {
				LOG.info(anag.getUid()+" left:"+anag.getEmailPrimaria());
			}
		}
	}
	
	public static void updateEmailCodiceAbbonamento(Session ses, String line) 
			throws HibernateException, IOException {
		String[] values = line.split(SEPARATOR_REGEX);
		String codiceAbb;
		String email;
		try {
			codiceAbb = values[0].toUpperCase();
			email = values[1].toLowerCase();
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}			
		Abbonamenti a = aDao.findAbbonamentiByCodice(ses, codiceAbb);
		if (a != null) {
			IstanzeAbbonamenti ia = iaDao.findUltimaIstanzaByAbbonamento(ses, a.getId());
			if (ia != null) {
				Anagrafiche anag = ia.getAbbonato();
				boolean newEmailValid = ValueUtil.isValidEmail(email);
				boolean oldEmailValid = ValueUtil.isValidEmail(anag.getEmailPrimaria());
				if (newEmailValid || (!newEmailValid && !oldEmailValid)) {
					//Sostituisce se nuova email è valida o se vecchia e nuova non sono valide
					anag.setEmailPrimaria(email);
					anagDao.update(ses, anag);
					LOG.info(anag.getUid()+" updt:"+anag.getEmailPrimaria());
				} else {
					LOG.info(anag.getUid()+" left:"+anag.getEmailPrimaria());
				}
			}
		}
	}
}
