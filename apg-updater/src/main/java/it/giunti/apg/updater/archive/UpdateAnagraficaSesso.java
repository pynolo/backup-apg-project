package it.giunti.apg.updater.archive;

import it.giunti.apg.core.persistence.AnagraficheDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Anagrafiche;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class UpdateAnagraficaSesso {

	private static final String SEPARATOR_REGEX = "\\$";
	private static AnagraficheDao anagDao = new AnagraficheDao();
	
	public static void updateAnagraficaFormDollarCsv(String csvFilePath) 
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
				updateSesso(ses, line);
				count++;
				if (count%100 == 0) System.out.print(".");
				line = br.readLine();
			}
			System.out.println("\r\nAggiornate "+count+" anagrafiche");
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
	
	private static void updateSesso(Session ses, String line) 
			throws HibernateException, IOException {
		String[] values = line.split(SEPARATOR_REGEX);
		String codiceCliente;
		String sesso;
		try {
			codiceCliente = values[0];
			sesso = values[1];
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
		Anagrafiche anag = anagDao.findByUid(ses, codiceCliente);
		if (anag != null) {
			anag.setSesso(sesso);
			anagDao.update(ses, anag);
		}
	}

}
