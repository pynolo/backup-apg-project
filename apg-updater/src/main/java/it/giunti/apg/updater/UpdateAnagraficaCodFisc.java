package it.giunti.apg.updater;

import it.giunti.apg.server.persistence.AnagraficheDao;
import it.giunti.apg.server.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Anagrafiche;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateAnagraficaCodFisc {
	private static final Logger LOG = LoggerFactory.getLogger(UpdateAnagraficaCodFisc.class);
	
	private static final String SEPARATOR_REGEX = "\\t";
	private static AnagraficheDao anagDao = new AnagraficheDao();
	
	private static final String CF_PATTERN =
			"^[A-Z]{6}[0-9]{2}[A-Z][0-9]{2}[A-Z][0-9]{3}[A-Z]$";
	private static final String PI_PATTERN =
			"^[0-9]{11}$";
	private static final Pattern cfPattern;
	private static final Pattern piPattern;
	static {
		cfPattern = Pattern.compile(CF_PATTERN);
		piPattern = Pattern.compile(PI_PATTERN);
	}
	
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
		String[] values = line.split(SEPARATOR_REGEX);
		String cognomeNome;
		//String presso;
		String indirizzo;
		String cap;
		String codiceRivista;
		String codFis="";
		String pIva="";
		try {
			cognomeNome = values[3].toUpperCase().trim();
			//presso = values[4].toUpperCase().trim();
			indirizzo = values[5].toUpperCase().trim();
			cap = values[8].toUpperCase().trim();
			codiceRivista = values[12].toUpperCase().trim();
			if (values.length>13) codFis = values[13].toUpperCase().trim();
			if (values.length>14) pIva = values[14].toUpperCase().trim();
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
		List<Anagrafiche> anagList = findAnagraficheByDetails(ses, cognomeNome, cap, codiceRivista);
		if (verificaCodici(codFis, pIva)) {
			Anagrafiche anag = makeBestMatch(anagList, cognomeNome, indirizzo, codFis, pIva);
			if (anag != null) {
				if (anag.getCodiceFiscale() == null) anag.setCodiceFiscale("");
				if (anag.getCodiceFiscale().length() < 5) anag.setCodiceFiscale(codFis);
				if (anag.getPartitaIva() == null) anag.setPartitaIva("");
				if (anag.getPartitaIva().length() < 5) anag.setPartitaIva(pIva);
				anagDao.update(ses, anag);
				logBuffer.append("OK\t"+cognomeNome+" \t"+indirizzo+" \t"+cap+"\t "+codFis+" \t"+pIva+" \t"+
						anag.getUid()+" \t"+
						anag.getIndirizzoPrincipale().getCognomeRagioneSociale()+" "+anag.getIndirizzoPrincipale().getNome()+" \t"+
						anag.getIndirizzoPrincipale().getIndirizzo()+" \t"+
						anag.getIndirizzoPrincipale().getCap()+"\r\n");
				LOG.info("OK: "+cognomeNome+" - "+indirizzo);
			} else {
				logBuffer.append("ERR_ANAGR\t"+cognomeNome+" \t"+indirizzo+" \t"+cap+" \t"+codFis+" \t"+pIva+" \r\n");
				LOG.info("ERR_ANAGR: "+cognomeNome+" - "+indirizzo);
			}
		} else {
			logBuffer.append("ERR_CODFIS\t"+cognomeNome+" \t"+indirizzo+" \t"+cap+" \t"+codFis+" \t"+pIva+" \r\n");
			LOG.info("ERR_CODFIS: "+cognomeNome+" - "+indirizzo);
		}
	}
		
	@SuppressWarnings("unchecked")
	private static List<Anagrafiche> findAnagraficheByDetails(Session ses, String cognomeNome,
			String cap, String codiceRivista) 
			throws HibernateException {
		String[] cognomeSplit = cognomeNome.split("\\s");
		String cognome = cognomeSplit[0];
		String hql = "from Anagrafiche anag where "+
				"anag.cognomeRagioneSociale like :s2 and "+
				"anag.indirizzoPrincipale.cap = :s3 "+
				//"anag.id in (select ia.abbonato.id from IstanzeAbbonamenti ia where "+
				//	"ia.abbonamento.codiceAbbonamento like :s1) "+
				"order by anag.id desc ";
		Query q = ses.createQuery(hql);
		//q.setParameter("s1", codiceRivista+"%", StringType.INSTANCE);
		q.setParameter("s2", cognome+"%", StringType.INSTANCE);
		q.setParameter("s3", cap, StringType.INSTANCE);
		List<Anagrafiche> anagList = q.list();
		if (anagList != null) {
			if (anagList.size() > 0) {
				return anagList;
			}
		}
		return new ArrayList<Anagrafiche>();
	}
	
	private static Anagrafiche makeBestMatch(List<Anagrafiche> anagList,
			String cognomeNome, String indirizzo,
			String codFis, String pIva) {
		Anagrafiche result = null;
		if (anagList.size() == 1) {
			result = anagList.get(0);
		}
		if (anagList.size() > 1) {
			//Restringe sul nome
			List<Anagrafiche> nomeList = new ArrayList<Anagrafiche>();
			for (Anagrafiche anag:anagList) {
				String nome = (anag.getIndirizzoPrincipale().getCognomeRagioneSociale()+" "+anag.getIndirizzoPrincipale().getNome()).trim().toUpperCase();
				if (nome.equals(cognomeNome)) nomeList.add(anag);
				String ragsoc = (anag.getIndirizzoPrincipale().getCognomeRagioneSociale()+" "+anag.getIndirizzoPrincipale().getPresso()).trim().toUpperCase();
				if (ragsoc.equals(cognomeNome) && !nomeList.contains(anag)) nomeList.add(anag);
			}
			if (nomeList.size() == 1) {
				result = nomeList.get(0);
			}
			if (nomeList.size() > 1) {
				//Restringe sull'indirizzo
				List<Anagrafiche> indList = new ArrayList<Anagrafiche>();
				for (Anagrafiche anag:nomeList) {
					if (indirizzo.equals(anag.getIndirizzoPrincipale().getIndirizzo())) {
						indList.add(anag);
					}
				}
				if (indList.size() == 1) {
					result = indList.get(0);
				}
				/* NO MATCH */
			}
		}
		return result;
	}
	
	private static boolean verificaCodici(String codFis, String pIva) {
		boolean ok = true;
		if (codFis != null) {
			if (codFis.length() > 0) {
				Matcher cfMatcher = cfPattern.matcher(codFis);
				Matcher piMatcher = piPattern.matcher(codFis);
				ok = ok && (cfMatcher.matches() || piMatcher.matches());
			}
		}
		if (pIva != null) {
			if (pIva.length() > 0) {
				Matcher piMatcher = piPattern.matcher(pIva);
				ok = ok && piMatcher.matches();
			}
		}
		return ok;
	}
}
