package it.giunti.apg.updater;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.giunti.apg.core.persistence.AnagraficheDao;
import it.giunti.apg.core.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.Indirizzi;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;

public class ImportGcc {
	
	private static final Logger LOG = LoggerFactory.getLogger(InsertAnagraficaAndIstanza.class);
		
	private static final String SEPARATOR_REGEX = "\\;";
	private static final String SEP = ";";
	
	private static IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
	private static AnagraficheDao anaDao = new AnagraficheDao();
	
	public static void parseFileAnagrafiche(String csvFilePath) 
			throws BusinessException, IOException {
		int count = 0;
		int errors = 0;
		int warn = 0;
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			File logFile = File.createTempFile("import_", ".csv");
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(logFile));
			writer.write("uid_anagrafica"+SEP+"gcc"+SEP+"pinza_id"+SEP+
				"cognome_ragsoc"+SEP+"nome"+SEP+"presso"+SEP+"indirizzo"+SEP+"cap"+SEP+
				"localita"+SEP+"provincia"+SEP+"email"+SEP+"esito"+SEP+
				"note\r\n");
			LOG.info("Log: "+logFile.getAbsolutePath());
			File csvFile = new File(csvFilePath);
			FileInputStream fstream = new FileInputStream(csvFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream, AppConstants.CHARSET_UTF8));
			try {
				//Ciclo su tutte le righe
				String line = br.readLine();
				while (line != null) {
					count++;
					ParsedLine pl = parseCsv(ses, line);
					if (pl.ia != null) pl = updateGcc(ses, pl);
					String message = getOutputData(pl);
					writer.write(message+"\r\n");
					LOG.info(count+") "+message);
//					if (count%100 == 0) {
//						ses.flush();
//						ses.clear();
//						LOG.info(count+" righe totali, "+warn+" avvisi, "+errors+" errori");
//					}
					line = br.readLine();
				}
				LOG.info("Log: "+logFile.getAbsolutePath());
			} catch (IOException e) {
				throw new IOException(e.getMessage(), e);
			} finally {
				br.close();
				fstream.close();
				try {// Close the writer regardless of what happens...
					writer.close();
	            } catch (Exception e) { }
			}
			trn.commit();
		} catch (HibernateException e) {
			trn.rollback();
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		LOG.info(count+" righe totali, "+warn+" avvisi, "+errors+" errori");
		LOG.info("Aggiunte "+count+" anagrafiche ("+errors+" errori)");
	}
	
	private static ParsedLine parseCsv(Session ses, String line) {
		String[] values = line.split(SEPARATOR_REGEX);
		ParsedLine pl = new ParsedLine();

		//Lettura
		String codAbbo = values[1].trim();
		IstanzeAbbonamenti ia = iaDao.findUltimaIstanzaByCodice(ses, codAbbo);
		if (ia == null && pl.esito == null) {
			pl.esito = "ko";
			pl.note = "Nessun abbonamento corrisponde a "+codAbbo;
		}
		pl.ia=ia;
		pl.gcc=values[0].trim();
		pl.pinzaId=values[2].trim();
		if ((pl.gcc == null || pl.pinzaId == null) && pl.esito == null) {
			pl.esito = "ko";
			pl.note = "Mancano gcc o pinzaId "+codAbbo;
		}
		return pl;
	}
			
	private static ParsedLine updateGcc(Session ses, ParsedLine pl) 
			throws BusinessException {
		Anagrafiche abb = pl.ia.getAbbonato();
		//Anagrafica
		if (pl.ia.getPagante() != null && pl.esito == null) {
			pl.esito = "ko";
			pl.note = "Intestatario ambiguo: inserire manualmente";
			pl.ia = null;
		} else {
			//Aggiornamento GCC
			String gccOld = abb.getGiuntiCardClub();
			if (gccOld == null) gccOld = "";
			if (gccOld.length() > 0 && !gccOld.equals(pl.gcc)) {
				pl.esito = "ko";
				pl.note = "GCC già presente e diversa "+pl.gcc+" ‡ "+gccOld;
			} else {
				abb.setGiuntiCardClub(pl.gcc);
				//TODO anaDao.update(ses, abb);
			}
		}
		return pl;
	}
	
	private static String getOutputData(ParsedLine pl) {
		Anagrafiche anag = new Anagrafiche();
		Indirizzi ind = new Indirizzi();
		if (pl.ia != null) {
			anag = pl.ia.getAbbonato();
			ind = anag.getIndirizzoPrincipale();
		}
		String row = "";
		row += (anag.getUid() == null ? "" : anag.getUid())+SEP;
		row += (pl.gcc == null ? "" : pl.gcc)+SEP;
		row += (pl.pinzaId == null ? "" : pl.pinzaId)+SEP;
		row += (ind.getCognomeRagioneSociale() == null ? "" : ind.getCognomeRagioneSociale())+SEP;
		row += (ind.getNome() == null ? "" : ind.getNome())+SEP;
		row += (ind.getPresso() == null ? "" : ind.getPresso())+SEP;
		row += (ind.getIndirizzo() == null ? "" : ind.getIndirizzo())+SEP;
		row += (ind.getCap() == null ? "" : ind.getCap())+SEP;
		row += (ind.getLocalita() == null ? "" : ind.getLocalita())+SEP;
		row += (ind.getProvincia() == null ? "" : ind.getProvincia())+SEP;
		row += anag.getEmailPrimaria()+SEP;
		row += (pl.esito == null ? "OK" : pl.esito)+SEP;
		row += pl.note;
		return row;
	}
	
	private static class ParsedLine {
		public IstanzeAbbonamenti ia = null;
		public String gcc = null;
		public String pinzaId = null;
		public String esito = null;
		public String note = "";
	}
	
}
