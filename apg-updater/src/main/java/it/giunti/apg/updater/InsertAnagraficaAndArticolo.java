package it.giunti.apg.updater;

import it.giunti.apg.server.ServerConstants;
import it.giunti.apg.server.business.AnagraficheBusiness;
import it.giunti.apg.server.persistence.AnagraficheDao;
import it.giunti.apg.server.persistence.ArticoliDao;
import it.giunti.apg.server.persistence.EvasioniArticoliDao;
import it.giunti.apg.server.persistence.GenericDao;
import it.giunti.apg.server.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.server.persistence.LocalitaDao;
import it.giunti.apg.server.persistence.NazioniDao;
import it.giunti.apg.server.persistence.ProvinceDao;
import it.giunti.apg.server.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.Articoli;
import it.giunti.apg.shared.model.EvasioniArticoli;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Localita;
import it.giunti.apg.shared.model.Nazioni;
import it.giunti.apg.shared.model.Province;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InsertAnagraficaAndArticolo {
	
	private static final Logger LOG = LoggerFactory.getLogger(InsertAnagraficaAndArticolo.class);
	
	public static String DATA_ARTICOLO_STRING = "2016-07-29";
	
	private static final String SEPARATOR_REGEX = "\\;";
	private static final String SEP = ";";
	
	private static EvasioniArticoliDao eaDao = new EvasioniArticoliDao();
	
	public static void parseFileAnagrafiche(String csvFilePath, String cmArticolo, String letteraPeriodico) 
			throws BusinessException, IOException {
		File logFile = File.createTempFile("Import_"+letteraPeriodico+"_", ".csv");
		Date DATA_ARTICOLO = null;
		try {
			DATA_ARTICOLO = new SimpleDateFormat("yyyy-MM-dd").parse(DATA_ARTICOLO_STRING);
		} catch (ParseException e1) {e1.printStackTrace();}
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(logFile));
		LOG.info("Log: "+logFile.getAbsolutePath());
		File csvFile = new File(csvFilePath);
		FileInputStream fstream = new FileInputStream(csvFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream, "ISO-8859-1"));
		int count = 0;
		int errors = 0;
		try {
			Session ses = SessionFactory.getSession();
			Transaction trn = ses.beginTransaction();
			Articoli art = null;
			try {
				art = new ArticoliDao().findByCm(ses, cmArticolo);
				//Ciclo su tutte le righe
				LOG.info("Aggiunta articolo '"+art.getTitoloNumero()+"' "+art.getCodiceMeccanografico()+
						" ad anagrafiche su file "+csvFile.getAbsolutePath()+ " data "+
						ServerConstants.FORMAT_DAY.format(DATA_ARTICOLO));
				String line = br.readLine();
				while (line != null) {
					try {
						Anagrafiche anag = parseAddAnagrafica(ses, line);
						addEvasioneArticolo(ses, anag, art, letteraPeriodico, DATA_ARTICOLO);
					} catch (ValidationException e) {
						LOG.warn(e.getMessage());
						writer.write(e.getMessage()+"\r\n");
						errors++;
					}
					count++;
					if (count%100 == 0) {
						ses.flush();
						LOG.info(count+" righe, "+errors+" errori");
					}
					line = br.readLine();
				}
				trn.commit();
			} catch (HibernateException e) {
				trn.rollback();
				throw new BusinessException(e.getMessage(), e);
			} finally {
				ses.close();
			}
		} catch (IOException e) {
			throw new IOException(e.getMessage(), e);
		} finally {
			br.close();
			fstream.close();
			try {// Close the writer regardless of what happens...
				writer.close();
            } catch (Exception e) { }
		}
		LOG.info("Aggiunte "+count+" anagrafiche con articolo "+cmArticolo+" ("+errors+" errori)");
	}
	
	private static Anagrafiche parseAddAnagrafica(Session ses, String line) 
			throws BusinessException, ValidationException {
		String[] values = line.split(SEPARATOR_REGEX);
		Anagrafiche anag;
		try {
			anag = new AnagraficheDao().createAnagrafiche(ses);
			try {
				String utente = ServerConstants.DEFAULT_SYSTEM_USER;
				try {
					anag.setEmailPrimaria(values[8].toUpperCase().trim());
				} catch (Exception e) {	}
				anag.getIndirizzoPrincipale().setTitolo(values[0].toUpperCase().trim());
				anag.getIndirizzoPrincipale().setCognomeRagioneSociale(values[1].toUpperCase().trim());
				if (values[2].toUpperCase().trim().length() > 31) {
					throw new ValidationException(anag.getEmailPrimaria()+SEP+
							anag.getIndirizzoPrincipale().getCognomeRagioneSociale()+SEP+
							anag.getIndirizzoPrincipale().getNome()+SEP+"Nome troppo lungo");
				}
				anag.getIndirizzoPrincipale().setNome(values[2].toUpperCase().trim());
				anag.getIndirizzoPrincipale().setIndirizzo(values[3].toUpperCase().trim());
				anag.getIndirizzoPrincipale().setLocalita(values[5].toUpperCase().trim());
				if (values[4].toUpperCase().trim().length() > 5) {
					throw new ValidationException(anag.getEmailPrimaria()+SEP+
							anag.getIndirizzoPrincipale().getCognomeRagioneSociale()+SEP+
							anag.getIndirizzoPrincipale().getNome()+SEP+"CAP troppo lungo");
				}
				anag.getIndirizzoPrincipale().setCap(values[4].toUpperCase().trim());
				String prov = values[6].toUpperCase().trim();
				prov = encodeProvincia(ses, anag, prov);
				anag.getIndirizzoPrincipale().setProvincia(prov);
				Nazioni nazione = encodeNazione(ses, anag, values[7].toUpperCase().trim());
				anag.getIndirizzoPrincipale().setNazione(nazione);
				//String tel = values[11];
				//anag.setTelMobile(tel);
				anag.getIndirizzoPrincipale().setIdUtente(utente);
				anag.getIndirizzoFatturazione().setIdUtente(utente);
				anag.setIdUtente(utente);
				replaceLocalitaAccents(anag);
				if (nazione.getId().equals("ITA")) {
					validateLocalitaCapProv(ses, anag);
				}
				Anagrafiche existing = findExisting(ses, anag);
				if (existing == null) {
					AnagraficheBusiness.saveOrUpdate(ses, anag, false);
				} else {
					anag = existing;
				}
			} catch (BusinessException e) {
				throw new IOException(e.getMessage());
			}
		} catch (HibernateException e) {
			LOG.error("Impossible to parse: "+line);
			throw new BusinessException(e.getMessage(), e);
		} catch (IOException e) {
			LOG.error("Impossible to parse: "+line);
			throw new BusinessException(e.getMessage(), e);
		}
		LOG.info("OK: "+anag.getUid()+" "+anag.getIndirizzoPrincipale().getCognomeRagioneSociale()+" "+anag.getIndirizzoPrincipale().getNome());
		return anag;
	}
	
	private static void addEvasioneArticolo(Session ses, Anagrafiche anag, Articoli art, String letteraPeriodico, Date date) 
		throws HibernateException, ValidationException {
		checkActiveSubscription(ses, anag, letteraPeriodico);
		//Se non ci sono abbonamenti
		EvasioniArticoli ea = eaDao.createEvasioniArticoliFromAnagrafica(ses,
				anag.getId(), 1, AppConstants.DEST_BENEFICIARIO, ServerConstants.DEFAULT_SYSTEM_USER);
		ea.setArticolo(art);
		ea.setDataCreazione(date);
		ea.setDataInvio(date);
		eaDao.save(ses, ea);
	}
	
	private static Anagrafiche findExisting(Session ses, Anagrafiche transAnag) throws BusinessException {
		String ind = transAnag.getIndirizzoPrincipale().getIndirizzo();
		if (ind.length() > 5) ind = ind.substring(0, ind.length()-5);
		List<Anagrafiche> anagList = new AnagraficheDao().findByProperties(ses, null/*codAnag*/,
				transAnag.getIndirizzoPrincipale().getCognomeRagioneSociale(),
				transAnag.getIndirizzoPrincipale().getNome(), null/*presso*/,
				ind,
				transAnag.getIndirizzoPrincipale().getCap(),
				transAnag.getIndirizzoPrincipale().getLocalita(),
				null/*prov*/, null/*email*/, null/*cfiva*/, null/*idPeriodico*/,
				null/*tipoAbb*/, 0, 10);
		if (anagList.size() > 0) {
			return anagList.get(0);
		} else {
			return null;
		}
	}
	
	private static void checkActiveSubscription(Session ses, Anagrafiche anag, String letteraPeriodico)
			throws HibernateException, ValidationException {
		List<IstanzeAbbonamenti> iaList = new IstanzeAbbonamentiDao().findIstanzeProprieByAnagrafica(ses, anag.getId(),
				false, 0, Integer.MAX_VALUE);
		Date now = new Date();
		for (IstanzeAbbonamenti ia:iaList) {
			if (ia.getAbbonamento().getCodiceAbbonamento().startsWith(letteraPeriodico) &&
					ia.getFascicoloInizio().getDataInizio().after(now)) {
				throw new ValidationException(anag.getEmailPrimaria()+SEP+
						anag.getIndirizzoPrincipale().getCognomeRagioneSociale()+SEP+
						anag.getIndirizzoPrincipale().getNome()+SEP+"Possiede un abbonamento attivo");
			}
		}
	}
	
	private static void replaceLocalitaAccents(Anagrafiche anag) {
		//Accenti
		String locName = anag.getIndirizzoPrincipale().getLocalita();
		locName = locName.replaceAll("à", "A'");
		locName = locName.replaceAll("è", "E'");
		locName = locName.replaceAll("é", "E'");
		locName = locName.replaceAll("ì", "I'");
		locName = locName.replaceAll("ò", "O'");
		locName = locName.replaceAll("ù", "U'");
		locName = locName.replaceAll("\\s\\s", " ");//Rimuove doppi spazi
		locName = locName.replaceAll("\\s\\s", " ");//Rimuove doppi spazi
		anag.getIndirizzoPrincipale().setLocalita(locName);
	}
	
	private static void validateLocalitaCapProv(Session ses, Anagrafiche anag)
			throws ValidationException, HibernateException {
		//corrispondenza localita cap prov
		Localita loc;
		try {
			loc = new LocalitaDao().findCapByLocalitaProv(ses,
					anag.getIndirizzoPrincipale().getLocalita(),
					anag.getIndirizzoPrincipale().getProvincia());
		} catch (EmptyResultException e) {
			loc = null;
		}
		if (loc == null) {
			throw new ValidationException(anag.getEmailPrimaria()+SEP+
					anag.getIndirizzoPrincipale().getCognomeRagioneSociale()+SEP+
					anag.getIndirizzoPrincipale().getNome()+SEP+"Localita' errata "+
				anag.getIndirizzoPrincipale().getLocalita()+" ("+
				anag.getIndirizzoPrincipale().getProvincia()+") "+
				anag.getIndirizzoPrincipale().getCap());
		} else {
			if (!anag.getIndirizzoPrincipale().getCap().startsWith(loc.getCap())) {
				throw new ValidationException(anag.getEmailPrimaria()+SEP+
						anag.getIndirizzoPrincipale().getCognomeRagioneSociale()+SEP+
						anag.getIndirizzoPrincipale().getNome()+SEP+"CAP errato "+
					anag.getIndirizzoPrincipale().getLocalita()+" ("+
					anag.getIndirizzoPrincipale().getProvincia()+") "+
					anag.getIndirizzoPrincipale().getCap());
			}
		}
	}
	
	private static String encodeProvincia(Session ses, Anagrafiche anag, String nome) throws HibernateException, ValidationException {
		if (nome.length() > 2) {
			Province prov = null;
			if (!nome.equals("")) {
				prov = new ProvinceDao().findByName(ses, nome);
			}
			if (prov == null) {
				throw new ValidationException(anag.getEmailPrimaria()+SEP+
						anag.getIndirizzoPrincipale().getCognomeRagioneSociale()+SEP+
						anag.getIndirizzoPrincipale().getNome()+SEP+"Provincia non riconosciuta: "+nome);
			} else {
				nome = prov.getId();
			}
		}
		return nome;
	}
	
	private static Nazioni encodeNazione(Session ses, Anagrafiche anag, String nome) throws HibernateException, ValidationException {
		Nazioni result = GenericDao.findById(ses, Nazioni.class, AppConstants.DEFAULT_ID_NAZIONE_ITALIA);
		if (nome == null) nome = "";
		if (!nome.equals("")) {
			result = new NazioniDao().findByName(ses, nome);
		}
		if (result == null) {
			throw new ValidationException(anag.getEmailPrimaria()+SEP+
					anag.getIndirizzoPrincipale().getCognomeRagioneSociale()+SEP+
					anag.getIndirizzoPrincipale().getNome()+SEP+"Nazione non riconosciuta: "+nome);
		}
		return result;
	}
}
