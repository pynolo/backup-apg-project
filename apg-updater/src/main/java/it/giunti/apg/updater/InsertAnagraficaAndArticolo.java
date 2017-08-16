package it.giunti.apg.updater;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.business.AnagraficheBusiness;
import it.giunti.apg.core.persistence.AnagraficheDao;
import it.giunti.apg.core.persistence.ArticoliDao;
import it.giunti.apg.core.persistence.EvasioniArticoliDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.LocalitaDao;
import it.giunti.apg.core.persistence.NazioniDao;
import it.giunti.apg.core.persistence.ProvinceDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.Articoli;
import it.giunti.apg.shared.model.EvasioniArticoli;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InsertAnagraficaAndArticolo {
	
	private static final Logger LOG = LoggerFactory.getLogger(InsertAnagraficaAndArticolo.class);
	
	/** FORMATO FILE INPUT
	 * 0 titolo
	 * 1 cognome-rag.soc
	 * 2 nome
	 * 3 presso
	 * 4 indirizzo stradale
	 * 5 cap
	 * 6 localita
	 * 7 sigla provincia
	 * 8 nazione
	 * 9 CM articolo/specimen
	 * 10 data spedizione dd/MM/aaaa
	 */
	
	/** FORMATO CSV OUTPUT
	 * 0 UID
	 * 1 cognome
	 * 2 nome
	 * 3 presso
	 * 4 cap
	 * 5 indirizzo
	 * 6 risultato
	 * 7 note
	 */
	
	private static final String SEPARATOR_REGEX = "\\;";
	private static final String SEP = ";";
	private static final String DISCARDED = "ignorato";
	
	private static Map<String, Articoli> articoliMap = new HashMap<String, Articoli>();
	private static EvasioniArticoliDao eaDao = new EvasioniArticoliDao();
	private static ArticoliDao aDao = new ArticoliDao();
	
	public static void parseFileAnagrafiche(String csvFilePath, String letteraPeriodico) 
			throws BusinessException, IOException {
		int count = 0;
		int errors = 0;
		int warn = 0;
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			File logFile = File.createTempFile("import_"+letteraPeriodico+"_", ".csv");
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(logFile));
			LOG.info("Log: "+logFile.getAbsolutePath());
			File csvFile = new File(csvFilePath);
			FileInputStream fstream = new FileInputStream(csvFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream, AppConstants.CHARSET));
			try {
				//Ciclo su tutte le righe
				String line = br.readLine();
				while (line != null) {
					count++;
					try {
						AnagraficaArticolo aa = parseAnagraficaArticolo(ses, line);
						addEvasioneArticolo(ses, aa.anagrafica, aa.articolo,
								letteraPeriodico, aa.dataInvio);
						String message = getCsvData(aa.anagrafica)+
								aa.articolo.getCodiceMeccanografico()+" spedito "+
								ServerConstants.FORMAT_DAY.format(aa.dataInvio)+SEP+
								aa.note;
						writer.write(message+"\r\n");
						//LOG.info(count+") "+message);
					} catch (ValidationException e) {
						LOG.info(count+") "+e.getMessage());
						writer.write(e.getMessage()+"\r\n");
						errors++;
					}
					if (count%100 == 0) {
						ses.flush();
						ses.clear();
						LOG.info(count+" righe totali, "+warn+" avvisi, "+errors+" errori");
					}
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
	
	private static AnagraficaArticolo parseAnagraficaArticolo(Session ses, String line) 
			throws BusinessException, ValidationException {
		String[] values = line.split(SEPARATOR_REGEX);
		AnagraficaArticolo aa = null;
		String note = "";
		try {
			Anagrafiche anag = new AnagraficheDao().createAnagrafiche(ses);
			try {
				//Anagrafica
				String utente = ServerConstants.DEFAULT_SYSTEM_USER;
				//Email
				//try {
				//	anag.setEmailPrimaria(values[0].toUpperCase().trim());
				//} catch (Exception e) {	}
				//Titolo
				anag.getIndirizzoPrincipale().setTitolo(values[0].toUpperCase().trim());
				//Cognome
				if (values[1].toUpperCase().trim().length() > 63) {
					throw new ValidationException(getCsvData(anag)+
							DISCARDED+SEP+"Cognome troppo lungo: "+values[1]);
				}
				anag.getIndirizzoPrincipale().setCognomeRagioneSociale(values[1].toUpperCase().trim());
				//Nome
				if (values[2].toUpperCase().trim().length() > 31) {
					throw new ValidationException(getCsvData(anag)+
							DISCARDED+SEP+"Nome troppo lungo: "+values[2]);
				}
				anag.getIndirizzoPrincipale().setNome(values[2].toUpperCase().trim());
				//Presso
				anag.getIndirizzoPrincipale().setPresso(values[3].toUpperCase().trim());
				//Indirizzo
				anag.getIndirizzoPrincipale().setIndirizzo(values[4].toUpperCase().trim());
				//Localita
				anag.getIndirizzoPrincipale().setLocalita(values[6].toUpperCase().trim());
				//Cap
				if (values[5].toUpperCase().trim().length() > 5) {
					throw new ValidationException(getCsvData(anag)+
							DISCARDED+SEP+"CAP troppo lungo: "+values[5]);
				}
				anag.getIndirizzoPrincipale().setCap(values[5].toUpperCase().trim());
				//Prov
				String prov = values[7].toUpperCase().trim();
				prov = encodeProvincia(ses, anag, prov);
				anag.getIndirizzoPrincipale().setProvincia(prov);
				//Nazione
				Nazioni nazione = encodeNazione(ses, anag, values[8].toUpperCase().trim());
				anag.getIndirizzoPrincipale().setNazione(nazione);
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
//					throw new ValidationException(getCsvData(anag)+
//							DISCARDED+SEP+"Simile a UID["+existing.getUid()+"] "+
//							existing.getIndirizzoPrincipale().getCognomeRagioneSociale()+" "+
//							existing.getIndirizzoPrincipale().getNome()+" "+
//							existing.getIndirizzoPrincipale().getCap()+" "+
//							existing.getIndirizzoPrincipale().getIndirizzo());
					note = "Dati riconciliati: "+anag.getIndirizzoPrincipale().getCognomeRagioneSociale()+" ";
					if (anag.getIndirizzoPrincipale().getNome() != null) note += anag.getIndirizzoPrincipale().getNome()+" ";
					if (anag.getIndirizzoPrincipale().getPresso() != null) note += anag.getIndirizzoPrincipale().getPresso()+" ";
					note += anag.getIndirizzoPrincipale().getCap()+" "+
							anag.getIndirizzoPrincipale().getIndirizzo()+" ";
					anag = existing;
				}
				//EvasioneArticolo
				Articoli articolo = encodeArticolo(ses, anag, values[9].toUpperCase().trim());
				Date dataInvio;
				try {
					dataInvio = ServerConstants.FORMAT_DAY.parse(values[10].trim());
				} catch (ParseException e) {
					throw new ValidationException(getCsvData(anag)+
							DISCARDED+SEP+"Data errata: "+values[10].trim());
				}
				
				aa = new AnagraficaArticolo();
				aa.anagrafica=anag;
				aa.articolo=articolo;
				aa.dataInvio=dataInvio;
				aa.note=note;
			} catch (BusinessException e) {
				throw new IOException(e.getMessage());
			}
		} catch (HibernateException | IOException e) {
			LOG.error("Impossible to parse: "+line);
			throw new BusinessException(e.getMessage(), e);
		}
		return aa;
	}
	
	private static void addEvasioneArticolo(Session ses, Anagrafiche anag, Articoli art, String letteraPeriodico, Date date) 
		throws HibernateException, ValidationException {
		//checkActiveSubscription(ses, anag, letteraPeriodico);
		
		//Se non ci sono abbonamenti (nonò va bene comunque)
		EvasioniArticoli ea = eaDao.createEvasioniArticoliFromAnagrafica(ses,
				anag.getId(), 1, AppConstants.DEST_BENEFICIARIO, ServerConstants.DEFAULT_SYSTEM_USER);
		ea.setArticolo(art);
		ea.setDataCreazione(date);
		ea.setDataInvio(date);
		eaDao.save(ses, ea);
	}
	
	private static Anagrafiche findExisting(Session ses, Anagrafiche transAnag) throws BusinessException {
		String streetPrefix = transAnag.getIndirizzoPrincipale().getIndirizzo();
		if (streetPrefix.length() > 4) streetPrefix = streetPrefix.substring(0, streetPrefix.length()-4);
		List<Anagrafiche> anagList = findSimilar(ses,
				transAnag.getIndirizzoPrincipale().getCognomeRagioneSociale(),
				transAnag.getIndirizzoPrincipale().getNome(),
				streetPrefix, transAnag.getIndirizzoPrincipale().getCap());
		if (anagList.size() > 0) {
			return anagList.get(0);
		} else {
			return null;
		}
	}
	
//	private static void checkActiveSubscription(Session ses, Anagrafiche anag, String letteraPeriodico)
//			throws HibernateException, ValidationException {
//		List<IstanzeAbbonamenti> iaList = new IstanzeAbbonamentiDao().findIstanzeProprieByAnagrafica(ses, anag.getId(),
//				false, 0, Integer.MAX_VALUE);
//		Date now = new Date();
//		for (IstanzeAbbonamenti ia:iaList) {
//			if (ia.getAbbonamento().getCodiceAbbonamento().startsWith(letteraPeriodico) &&
//					(ia.getFascicoloFine().getDataFine().after(now) || ia.getFascicoloInizio().getDataInizio().before(now))) {
//				throw new ValidationException(getCsvData(anag)+
//						DISCARDED+SEP+"Possiede abbonamento "+
//						ia.getAbbonamento().getCodiceAbbonamento()+" UID["+ia.getId()+"]");
//			}
//		}
//	}
	
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
			throw new ValidationException(getCsvData(anag)+
					DISCARDED+SEP+"Localita' errata "+
					anag.getIndirizzoPrincipale().getLocalita()+" ("+
					anag.getIndirizzoPrincipale().getProvincia()+") "+
					anag.getIndirizzoPrincipale().getCap());
		} else {
			if (!anag.getIndirizzoPrincipale().getCap().startsWith(loc.getCap())) {
				throw new ValidationException(getCsvData(anag)+
						DISCARDED+SEP+"CAP errato "+
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
				throw new ValidationException(getCsvData(anag)+"Provincia non riconosciuta: "+nome);
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
			throw new ValidationException(getCsvData(anag)+
					DISCARDED+SEP+
					"Nazione non riconosciuta: "+nome);
		}
		return result;
	}
	
	private static Articoli encodeArticolo(Session ses, Anagrafiche anag, String cm) throws HibernateException, ValidationException {
		Articoli result = articoliMap.get(cm);
		if (result == null) {
			result = aDao.findByCm(ses, cm);
			if (result != null) {
				articoliMap.put(cm, result);
			} else {
				throw new ValidationException(getCsvData(anag)+
						DISCARDED+SEP+
						"Articolo non riconosciuto: "+cm);
			}
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private static List<Anagrafiche> findSimilar(Session ses, 
			String cognomeRagioneSociale, String nome,
			String streetPrefix, String cap) {
		String qs = "from Anagrafiche a where "+
				"("+
					"(a.indirizzoPrincipale.cognomeRagioneSociale = :s1 and a.indirizzoPrincipale.nome = :s2) "+
					" or " +
					"a.indirizzoPrincipale.cognomeRagioneSociale = :s3 "+
				") and "+
				"a.indirizzoPrincipale.indirizzo like :s4 and "+
				"a.indirizzoPrincipale.cap = :s5";
		cognomeRagioneSociale = cognomeRagioneSociale.toUpperCase();
		String cognomeNome = cognomeRagioneSociale;
		if (nome != null) {
			if (nome.length() > 0) cognomeNome += " "+nome;
		} else {
			nome = "";
		}
		nome = nome.toUpperCase();
		streetPrefix = streetPrefix.toUpperCase();
		if (cap != null) cap = cap.toUpperCase();
		Query q = ses.createQuery(qs);
		q.setParameter("s1", cognomeRagioneSociale, StringType.INSTANCE);
		q.setParameter("s2", nome, StringType.INSTANCE);
		q.setParameter("s3", cognomeNome, StringType.INSTANCE);
		q.setParameter("s4", streetPrefix+"%", StringType.INSTANCE);
		q.setParameter("s5", cap, StringType.INSTANCE);
		List<Anagrafiche> anaList = (List<Anagrafiche>) q.list();
		if (anaList != null) {
			if (anaList.size() > 0) {
				return anaList;
			}
		}
		return anaList;
	}
	
	private static String getCsvData(Anagrafiche anag) {
		String row = "";
		row += (anag.getUid() == null ? "" : anag.getUid())+SEP;
		row += anag.getIndirizzoPrincipale().getCognomeRagioneSociale()+SEP;
		row += (anag.getIndirizzoPrincipale().getNome() == null ? "" : anag.getIndirizzoPrincipale().getNome())+SEP;
		row += (anag.getIndirizzoPrincipale().getPresso() == null ? "" : anag.getIndirizzoPrincipale().getPresso())+SEP;
		row += anag.getIndirizzoPrincipale().getIndirizzo()+SEP;
		row += anag.getIndirizzoPrincipale().getCap()+SEP;
		row += anag.getIndirizzoPrincipale().getLocalita()+SEP;
		row += anag.getIndirizzoPrincipale().getProvincia()+SEP;
		return row;
	}
	
	private static class AnagraficaArticolo {
		public Anagrafiche anagrafica = null;
		public Articoli articolo = null;
		public Date dataInvio = null;
		public String note = "";
	}
	
}
