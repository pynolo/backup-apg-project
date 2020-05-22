package it.giunti.apg.updater.archive;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
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
	private static AnagraficheDao anagDao = new AnagraficheDao();
	
	
	public static void updateAnagraficaFormCsv(String csvFilePath) 
			throws BusinessException, IOException {
		File csvFile = new File(csvFilePath);
		FileInputStream fstream = new FileInputStream(csvFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		
		File logFile = File.createTempFile("updateCodFis", ".csv");
		PrintWriter out = new PrintWriter(logFile);
		LOG.info("LOG FILE: "+logFile.getAbsolutePath()+"\r\n\r\n");
		out.append("uid"+SEP+"old_cf"+SEP+"cf"+SEP+"email"+SEP+
				"problema\r\n");
		
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			int count = 0;
			String line = br.readLine();
			while (line != null) {
				updateCodFisc(ses, line, out);
				count++;
				line = br.readLine();
				ses.flush();
				ses.clear();
				//trn.commit();
				//trn = ses.beginTransaction();
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
		out.close();
	}
	
	private static void updateCodFisc(Session ses, String line, PrintWriter writer) 
			throws HibernateException, IOException {
		String[] values = line.split(SEP);
		String email;
		String cf;
		String uid;
		try {
			cf = values[0].toUpperCase().trim().replaceAll("\"", "");
			cf = cf.equalsIgnoreCase("\\N")?"":cf;
			email = values[1].toLowerCase().trim().replaceAll("\"", "");
			email = email.equalsIgnoreCase("\\N")?"":email;
			uid = values[2].toUpperCase().trim().replaceAll("\"", "");
			uid = uid.equalsIgnoreCase("\\N")?"":uid;
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
		String uidString = uid;
		Anagrafiche anag = anagDao.recursiveFindByUid(ses, uid);
		if (anag == null) {
			anag = findByEmail(ses, uid);
		}
		String result = "";
		if (ValueUtil.isValidCodFisc(cf, AppConstants.DEFAULT_ID_NAZIONE_ITALIA)) {
			if (anag != null) {				
				//Verifica vecchio CF
				String oldCf = anag.getCodiceFiscale();
				boolean isOldCfValid = ValueUtil.isValidCodFisc(anag.getCodiceFiscale(), AppConstants.DEFAULT_ID_NAZIONE_ITALIA);
				if (!isOldCfValid) oldCf = "";
				
				if (!isOldCfValid) {
					anag.setCodiceFiscale(cf);
					anagDao.update(ses, anag);
					result = uidString + SEP + oldCf + SEP + 
							anag.getCodiceFiscale() + SEP + anag.getEmailPrimaria();
				} else {
					//Esisteva già un CF valido
					if (!oldCf.equalsIgnoreCase(cf)) {
						//E' diverso da quello presente
						result = uidString + SEP + oldCf + SEP + 
								anag.getCodiceFiscale() + SEP + anag.getEmailPrimaria() + SEP +
								"IGNORATO: VERIFICARE";
					} else {
						result = uidString + SEP + oldCf + SEP + 
								anag.getCodiceFiscale() + SEP + anag.getEmailPrimaria() + SEP +
								"IGNORATO: GIA' INSERITO";
					}
				}
			} else {
				result = uidString + SEP + "" + SEP + cf + SEP + email + SEP +
						"ERRORE: NESSUNA ANAGRAFICA";
			}
		} else {
			result = uidString + SEP + "" + SEP + cf + SEP + email + SEP +
					"ERRORE: CF NON VALIDO";
		}
		writer.append(result+"\r\n");
		LOG.info(result);
	}

	public static String vuotoPerPieno(String oldVal, String newVal, Date dbDt, Date fileDt) {
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
	
	//private static boolean verificaCodici(String cf, String pi) {
	//	boolean ok = true;
	//	//cod_fisc - codice fiscale 
	//	boolean isCfValid = false;
	//	if (cf != null) {
	//		isCfValid = ValueUtil.isValidCodFisc(cf, AppConstants.DEFAULT_ID_NAZIONE_ITALIA);
	//	}
	//	//piva - partita iva
	//	boolean isPiValid = false;
	//	if (pi != null) {
	//		isPiValid = ValueUtil.isValidPartitaIva(pi, AppConstants.DEFAULT_ID_NAZIONE_ITALIA);
	//	}
	//	ok = isCfValid || isPiValid;
	//	return ok;
	//}
	
	@SuppressWarnings("unchecked")
	public static Anagrafiche findByEmail(Session ses, String email) 
			throws HibernateException {
		if (email != null) {
			if ((email.length() > 5) && (email.length() <= 10)) {
				String qs = "from Anagrafiche anag where " +
						"anag.emailPrimaria like :s1";
				Query q = ses.createQuery(qs);
				q.setParameter("s1", "%"+email+"%");
				List<Anagrafiche> anagList = q.list();
				Anagrafiche result = null;
				if (anagList != null) {
					if (anagList.size() > 0) {
						result = anagList.get(0);
					}
				}
				return result;
			}
		}
		return null;
	}
}
