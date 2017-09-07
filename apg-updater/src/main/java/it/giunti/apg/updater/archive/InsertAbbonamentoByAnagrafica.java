package it.giunti.apg.updater.archive;

import it.giunti.apg.core.DateUtil;
import it.giunti.apg.core.persistence.AbbonamentiDao;
import it.giunti.apg.core.persistence.AnagraficheDao;
import it.giunti.apg.core.persistence.EvasioniFascicoliDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Abbonamenti;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Date;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InsertAbbonamentoByAnagrafica {
	
	private static final Logger LOG = LoggerFactory.getLogger(InsertAbbonamentoByAnagrafica.class);

	private static final String SEP = ";";
	private static final String SEPARATOR_REGEX = "\\$";
	private static final Integer ID_PERIODICO = 4;//4=Psicologia Scuola
	private static final String TIPO_ABB = "TV";
	private static final String CODICE_PAGANTE = "N056LN";
	//TODO private static final String ADESIONE = "GIUNTITVP";
	
	private static AnagraficheDao anagDao = new AnagraficheDao();
	private static AbbonamentiDao aDao = new AbbonamentiDao();
	private static IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
	
	private static String idUtente = "admin";
	private static int counter = 0;
	
	public static void addAbbonamenti(String csvFilePath) 
			throws BusinessException, IOException {
		ReportWriter reportWriter = new ReportWriter("addAbbonamenti");
		
		File csvFile = new File(csvFilePath);
		FileInputStream fstream = new FileInputStream(csvFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			Anagrafiche pagante = new AnagraficheDao().findByUid(ses, CODICE_PAGANTE);
			String line = br.readLine();
			while (line != null) {
				addAbbonamento(ses, line, TIPO_ABB, pagante, reportWriter);
				counter++;
				//if (count%100 == 0) System.out.print(".");
				line = br.readLine();
			}
			LOG.info("\r\nProcessate "+counter+" anagrafiche");
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
		reportWriter.close();
	}
	
	private static void addAbbonamento(Session ses, String line, String siglaTa, Anagrafiche pagante, ReportWriter reportWriter) 
			throws HibernateException, IOException {
		String[] values = line.split(SEPARATOR_REGEX);
		String codiceCliente = null;
		String codiceAbbonamento = null;
		Date today = DateUtil.now();
		try {
			codiceCliente = values[0];
			//email = values[1];
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
		Anagrafiche anag = anagDao.findByUid(ses, codiceCliente);
		if (anag != null) {			
			IstanzeAbbonamenti ia = iaDao.createAbbonamentoAndIstanza(ses, anag.getId(), null, null, ID_PERIODICO, siglaTa);
			//TODO ia.setAdesione(ADESIONE);
			ia.setNote("Attivato come ex abbonato Sesamo");
			ia.setIdUtente(idUtente);
			ia.setPagante(pagante);
			ia.getAbbonamento().setIdUtente(idUtente);
			//Salvataggio Abbonamento
			Serializable aId = aDao.save(ses, ia.getAbbonamento());
			Abbonamenti a = GenericDao.findById(ses, Abbonamenti.class, aId);
			ia.setAbbonamento(a);
			//Salavataggio Istanza
			Serializable savedId = iaDao.save(ses, ia);
			IstanzeAbbonamenti savedIa = GenericDao.findById(ses, IstanzeAbbonamenti.class, savedId);
			codiceAbbonamento = savedIa.getAbbonamento().getCodiceAbbonamento();
			//Generazione arretrati
			new EvasioniFascicoliDao().enqueueMissingArretratiByStatus(ses,
					savedIa, today, idUtente);
			reportWriter.print(savedIa);
		} else {
			LOG.warn("Non trovato cliente "+codiceCliente);
			System.out.println("Non trovato cliente "+codiceCliente);
		}
		System.out.println(codiceCliente+"; "+codiceAbbonamento+"; "+counter);
	}
	
	
	private static class ReportWriter {
		private FileWriter writer = null;
		
		public ReportWriter(String fileName) throws IOException {
			File report = File.createTempFile(fileName, ".csv");
			LOG.info("Report su "+report.getAbsolutePath());
			writer = new FileWriter(report);
		}
		
		public void print(IstanzeAbbonamenti ia) 
				throws IOException {
			String codice = ia.getAbbonamento().getCodiceAbbonamento();
			String nome = ia.getAbbonato().getIndirizzoPrincipale().getCognomeRagioneSociale();
			if (ia.getAbbonato().getIndirizzoPrincipale().getNome() != null) nome += " "+ia.getAbbonato().getIndirizzoPrincipale().getNome();
			String line = codice+SEP+
					ia.getAbbonato().getUid()+SEP+
					nome+"\r\n";
			writer.write(line);
		}
		
		public void close() throws IOException {
			writer.close();
		}
		
	}
}
