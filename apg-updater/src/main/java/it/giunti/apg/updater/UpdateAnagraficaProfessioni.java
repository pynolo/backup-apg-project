package it.giunti.apg.updater;

import it.giunti.apg.core.persistence.AnagraficheDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Professioni;
import it.giunti.apg.shared.model.TitoliStudio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateAnagraficaProfessioni {
	private static final Logger LOG = LoggerFactory.getLogger(UpdateAnagraficaProfessioni.class);
	
	private static final String SEPARATOR_REGEX = "\\;";
	private static AnagraficheDao anagDao = new AnagraficheDao();
	private static IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
	
	private static Map<String, Integer> titoliMap = new HashMap<String, Integer>();
	private static Map<String, Integer> profMap = new HashMap<String, Integer>();
	static {
		titoliMap.put(clean("licenza elementare"),1);
		titoliMap.put(clean("licenza media"),2);
		titoliMap.put(clean("diploma di terza superiore"),3);
		titoliMap.put(clean("diploma"),3);
		titoliMap.put(clean("laurea triennale"),4);
		titoliMap.put(clean("laurea specialistica (o vecchio ordinamento)"),4);
		titoliMap.put(clean("laurea + master"),5);
		titoliMap.put(clean("dottorato/ricercatore"),6);
		profMap.put(clean("altro"),1);
		profMap.put(clean("archeologo (o attività affine)"),47);
		profMap.put(clean("artigiano"),3);
		profMap.put(clean("artista (attore/cantante/pittore/ecc)"),4);
		profMap.put(clean("casalinga/o"),7);
		profMap.put(clean("commerciante"),9);
		profMap.put(clean("dirigente"),11);
		profMap.put(clean("disoccupato/a"),13);
		profMap.put(clean("docente scuole superiori/università"),23);
		profMap.put(clean("educatore"),14);
		profMap.put(clean("impiegato"),16);
		profMap.put(clean("imprenditore"),17);
		profMap.put(clean("insegnante di sostegno"),19);
		profMap.put(clean("insegnante scuola secondaria"),42);
		profMap.put(clean("insegnante scuola infanzia"),20);
		profMap.put(clean("insegnante scuola primaria"),21);
		profMap.put(clean("libero professionista (avvocato/medico/commercialista/consulente/ecc.)"),25);
		profMap.put(clean("pensionato"),32);
		profMap.put(clean("psicologo - psichiatra - psicoterapeuta (o attività affine)"),33);
		profMap.put(clean("studente"),54);
		profMap.put(clean("studente università materie artistiche"),40);
		profMap.put(clean("studente università materie letterarie"),38);
		profMap.put(clean("studente università materie storiche e archeologiche"),39);
		profMap.put(clean("studente università psicologia/scienze formazione"),37);
	}
	
	public static void updateAnagraficaFormCsv(String csvFilePath) 
			throws BusinessException, IOException, ValidationException {
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
				updateAnagrafica(ses, line);
				count++;
				line = br.readLine();
			}
			trn.commit();
			LOG.info("Aggiornate "+count+" anagrafiche");
		} catch (Exception e) {
			trn.rollback();
			throw e;
		} finally {
			ses.close();
			br.close();
			fstream.close();
		}
	}
	
	private static String clean(String s) {
		s = s.replaceAll("[^\\p{ASCII}]", "");
		s = s.replaceAll("\\\"", "");
		s = s.trim();
		return s;
	}
	
	private static void updateAnagrafica(Session ses, String line) 
			throws HibernateException, IOException, ValidationException {
		String[] values = line.split(SEPARATOR_REGEX);
		String codAbb;
		String tel;
		String titolo;
		String prof;
		try {
			codAbb = clean(values[0].toUpperCase());
			tel = clean(values[1].toLowerCase());
			titolo = clean(values[2].toLowerCase());
			prof = clean(values[3].toLowerCase());
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
		IstanzeAbbonamenti ia = iaDao.findUltimaIstanzaByCodice(ses, codAbb);
		if (ia != null) {
			Anagrafiche anag = ia.getAbbonato();
			try {
				//Aggiornamento dati
				replaceTelefono(anag, tel);
				replaceTitoloStudio(ses, anag, titolo);
				replaceProfessione(ses, anag, prof);
				anagDao.update(ses, anag);
				LOG.info(anag.getUid()+" updt:"+codAbb+" "+anag.getUid());
			} catch (Exception e) {
				throw new ValidationException(e.getMessage());
			}
		} else {
			throw new ValidationException(codAbb+" non trovato");
		}
	}
	
	private static void replaceTelefono(Anagrafiche anag, String tel) throws ValidationException {
		String oldMobile = "";
		String oldFisso = "";
		if (anag.getTelMobile() != null) oldMobile = anag.getTelMobile();
		if (anag.getTelCasa() != null) oldFisso = anag.getTelCasa();
		if ((tel.length() >= 8) && (tel.length() <= 16)) {
			if (tel.startsWith("3")) {
				//Rimpiazza il vecchio telMobile ed eventualmente sposta il
				//vecchio valore in telCasa se necessario
				if (oldMobile.startsWith("0")) {
					if (oldFisso.length() < 8) {
						anag.setTelCasa(oldMobile);
					}
				}
				anag.setTelMobile(tel);
			}
			if (tel.startsWith("0")) {
				//Rimpiazza il vecchio telCasa ed eventualmente sposta il
				//vecchio valore in telMobile se necessario
				if (oldFisso.startsWith("3")) {
					if (oldMobile.length() < 7) {
						anag.setTelMobile(oldFisso);
					}
				}
				anag.setTelCasa(tel);
			}
		} else {
			if (tel.length() > 0) throw new ValidationException("Telefono non valido: '"+tel+"'");
		}
	}
	
	private static void replaceTitoloStudio(Session ses, Anagrafiche anag, String titolo) throws ValidationException {
		Integer id = titoliMap.get(titolo.toLowerCase());
		if ((id == null) && (titolo.length() > 1)) {
			throw new ValidationException("Titolo non valido: '"+titolo+"'");
		}
		TitoliStudio ts = GenericDao.findById(ses, TitoliStudio.class, id);
		anag.setTitoloStudio(ts);
	}
	
	private static void replaceProfessione(Session ses, Anagrafiche anag, String prof) throws ValidationException {
		Integer id = profMap.get(prof.toLowerCase());
		if ((id == null) && (prof.length() > 1)) {
			throw new ValidationException("Professione non valida: '"+prof+"'");
		}
		Professioni p = GenericDao.findById(ses, Professioni.class, id);
		anag.setProfessione(p);
	}
}
