package it.giunti.apg.updater;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.business.AnagraficheBusiness;
import it.giunti.apg.core.persistence.AnagraficheDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.core.persistence.ListiniDao;
import it.giunti.apg.core.persistence.LocalitaDao;
import it.giunti.apg.core.persistence.NazioniDao;
import it.giunti.apg.core.persistence.ProfessioniDao;
import it.giunti.apg.core.persistence.ProvinceDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.Indirizzi;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Listini;
import it.giunti.apg.shared.model.Localita;
import it.giunti.apg.shared.model.Nazioni;
import it.giunti.apg.shared.model.Professioni;
import it.giunti.apg.shared.model.Province;

public class InsertAnagraficaAndIstanza {
	
	private static final Logger LOG = LoggerFactory.getLogger(InsertAnagraficaAndIstanza.class);
	
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
	 * 9 UID listino
	 * 10 email
     * 11 Professione
     * 12 Adesione
	 */
	
	/** FORMATO CSV OUTPUT
	 * 0 UID anagrafica
	 * 1 cognome
	 * 2 nome
	 * 3 presso
	 * 4 indirizzo
	 * 5 cap
	 * 6 localita
	 * 7 provincia
	 * 8 UID istanza
	 * 9 codice abbonamento
 	 * 10 note
	 */
	
	private static final String SEPARATOR_REGEX = "\\;";
	private static final String SEP = ";";
	private static final String DISCARDED = "ignorato";
	
	private static IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
	private static ListiniDao lstDao = new ListiniDao();
	
	private static String utente = ServerConstants.DEFAULT_SYSTEM_USER;
	
	public static void parseFileAnagrafiche(String csvFilePath) 
			throws BusinessException, IOException {
		int count = 0;
		int errors = 0;
		int warn = 0;
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
		try {
			File logFile = File.createTempFile("import_", ".csv");
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(logFile));
			writer.write("uid_anagrafica"+SEP+"cognome_rag_soc"+SEP+"nome"+SEP+
				"presso"+SEP+"indirizzo"+SEP+"cap"+SEP+"localita"+SEP+
				"provincia"+SEP+"uid_istanza"+SEP+"cod_abbo\r\n");
			LOG.info("Log: "+logFile.getAbsolutePath());
			File csvFile = new File(csvFilePath);
			FileInputStream fstream = new FileInputStream(csvFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream, AppConstants.CHARSET_UTF8));
			try {
				//Ciclo su tutte le righe
				String line = br.readLine();
				while (line != null) {
					count++;
					try {
						AnagraficaListino al = parseAnagraficaIstanza(ses, line);
						IstanzeAbbonamenti ia = addIstanzaAbbonamento(ses, al.abbonato, al.uidListino, al.adesione );
						ia.setIdUtente(utente);
						ia.getAbbonamento().setIdUtente(utente);
						iaDao.save(ses, ia);
						String message = getCsvData(al.abbonato)+
								ia.getId()+SEP+
								ia.getAbbonamento().getCodiceAbbonamento()+SEP+
								al.note;
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
	
	private static AnagraficaListino parseAnagraficaIstanza(Session ses, String line) 
			throws BusinessException, ValidationException {
		String[] values = line.split(SEPARATOR_REGEX);
		AnagraficaListino al = null;
		String note = "";
		try {
			Anagrafiche anag = new AnagraficheDao().createAnagrafiche(ses);
			try {
				//Anagrafica
				
				//Email
				//try {
				//	anag.setEmailPrimaria(values[0].toUpperCase().trim());
				//} catch (Exception e) {	}
				//Titolo
				anag.getIndirizzoPrincipale().setTitolo(values[0].toUpperCase().trim());
				//Cognome
				if (values[1].trim().length() > 63) {
					throw new ValidationException(getCsvData(anag)+
							DISCARDED+SEP+"Cognome troppo lungo: "+values[1]);
				}
				anag.getIndirizzoPrincipale().setCognomeRagioneSociale(values[1].trim());
				//Nome
				if (values[2].trim().length() > 31) {
					throw new ValidationException(getCsvData(anag)+
							DISCARDED+SEP+"Nome troppo lungo: "+values[2]);
				}
				anag.getIndirizzoPrincipale().setNome(values[2].trim());
				//Presso
				if (values[3].trim().length() > 63) {
					throw new ValidationException(getCsvData(anag)+
							DISCARDED+SEP+"Presso troppo lungo: "+values[3]);
				}
				anag.getIndirizzoPrincipale().setPresso(values[3].trim());
				//Indirizzo
				if (values[4].trim().length() > 127) {
					throw new ValidationException(getCsvData(anag)+
							DISCARDED+SEP+"Indirizzo troppo lungo: "+values[4]);
				}
				anag.getIndirizzoPrincipale().setIndirizzo(values[4].trim());
				//Localita
				if (values[6].trim().length() > 63) {
					throw new ValidationException(getCsvData(anag)+
							DISCARDED+SEP+"Localita troppo lunga: "+values[6]);
				}
				anag.getIndirizzoPrincipale().setLocalita(values[6].trim());
				//Cap
				if (values[5].trim().length() > 5) {
					throw new ValidationException(getCsvData(anag)+
							DISCARDED+SEP+"CAP troppo lungo: "+values[5]);
				}
				anag.getIndirizzoPrincipale().setCap(values[5].trim());
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
				normalizeCase(anag);
				if (nazione.getId().equals("ITA")) {
					validateLocalitaCapProv(ses, anag);
				}
				Anagrafiche existing = findExisting(ses, anag);
				boolean isExisting =  (existing != null);
				if (isExisting) {
					note += "Dati riconciliati: "+anag.getIndirizzoPrincipale().getCognomeRagioneSociale()+" ";
					if (anag.getIndirizzoPrincipale().getNome() != null) note += anag.getIndirizzoPrincipale().getNome()+" ";
					if (anag.getIndirizzoPrincipale().getPresso() != null) note += anag.getIndirizzoPrincipale().getPresso()+" ";
					note += anag.getIndirizzoPrincipale().getCap()+" "+
							anag.getIndirizzoPrincipale().getIndirizzo()+" ";
					anag = existing;
				}
				//UID Listino
				String uidListino = values[9].toUpperCase().trim();
				//Email
				String email = values[10].toLowerCase().trim();
				if (!ValueUtil.isValidEmail(email)) {
					note ="EMAIL non valida: "+values[10]+" "+note;
				}
				anag.setEmailPrimaria(email);
				//// 11 Consenso Trattamento dati (1/0)
				//String consTosStr = values[11].trim();
				//boolean consTos = !(consTosStr.equals("0")||consTosStr.equals(""));
				//anag.setConsensoTos(consTos);
				//anag.setDataAggiornamentoConsenso(DateUtil.now());
				//// 12 Consenso Marketing (1/0)
				//String consMktStr = values[12].trim();
				//boolean consMkt = !(consMktStr.equals("0")||consMktStr.equals(""));
				//anag.setConsensoMarketing(consMkt);
				//// 13 Consenso profilazione (1/0)
				//String consPrfStr = values[13].trim();
				//boolean consPrf = !(consPrfStr.equals("0")||consPrfStr.equals(""));
				//anag.setConsensoProfilazione(consPrf);
				// 11 Professione
				String profStr = values[11].trim();
				Professioni prof = encodeProfessione(ses, anag, profStr);
				anag.setProfessione(prof);
				// 12 Adesione
				String adeStr = values[12].trim();
				if (adeStr != null) {
					if (adeStr.length() == 0) {
						adeStr = null;
					}
				}
				
				//SAVE OR UPDATE
				if (!isExisting) {
					anag.setConsensoTos(true);
					anag.setConsensoMarketing(false);
					anag.setConsensoProfilazione(false);
					anag.setDataAggiornamentoConsenso(DateUtil.now());
				}
				anag.setDataModifica(DateUtil.now());
				AnagraficheBusiness.saveOrUpdate(ses, anag, false);
				
				al = new AnagraficaListino();
				al.abbonato=anag;
				al.uidListino=uidListino;
				al.adesione=adeStr;
				al.note=note;
			} catch (BusinessException e) {
				throw new IOException(e.getMessage());
			}
		} catch (HibernateException | IOException e) {
			LOG.error("Impossible to parse: "+line);
			throw new BusinessException(e.getMessage(), e);
		}
		return al;
	}
	
	private static IstanzeAbbonamenti addIstanzaAbbonamento(Session ses,
			Anagrafiche abbonato, String uidListino, String adesione) throws BusinessException {
		Listini lst = lstDao.findByUid(ses, uidListino);
		if (lst == null) throw new BusinessException("UID listino '"+uidListino+"' has not been found");
		
		//Istanza abbonamento
		IstanzeAbbonamenti ia = iaDao.createAbbonamentoAndIstanzaByUidListino(ses,
				abbonato.getId(), null, null, 
				lst.getTipoAbbonamento().getPeriodico().getId(), uidListino);
		ia.setAdesione(adesione);
		return ia;
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
	
	private static void normalizeCase(Anagrafiche anag) {
		Indirizzi ind = anag.getIndirizzoPrincipale();
		ind.setTitolo(ValueUtil.capitalizeFirstLetters(ind.getTitolo()));
		ind.setCognomeRagioneSociale(ValueUtil.capitalizeFirstLetters(ind.getCognomeRagioneSociale()));
		ind.setNome(ValueUtil.capitalizeFirstLetters(ind.getNome()));
		ind.setPresso(ValueUtil.capitalizeFirstLetters(ind.getPresso()));
		ind.setIndirizzo(ValueUtil.capitalizeFirstLetters(ind.getIndirizzo()));
		ind.setLocalita(ValueUtil.capitalizeFirstLetters(ind.getLocalita()));
	}
	
	//private static void replaceLocalitaAccents(Anagrafiche anag) {
	//	//Accenti
	//	String locName = anag.getIndirizzoPrincipale().getLocalita();
	//	locName = locName.replaceAll("à", "A'");
	//	locName = locName.replaceAll("è", "E'");
	//	locName = locName.replaceAll("é", "E'");
	//	locName = locName.replaceAll("ì", "I'");
	//	locName = locName.replaceAll("ò", "O'");
	//	locName = locName.replaceAll("ù", "U'");
	//	locName = locName.replaceAll("\\s\\s", " ");//Rimuove doppi spazi
	//	locName = locName.replaceAll("\\s\\s", " ");//Rimuove doppi spazi
	//	anag.getIndirizzoPrincipale().setLocalita(locName);
	//}
	
	private static void validateLocalitaCapProv(Session ses, Anagrafiche anag)
			throws ValidationException, HibernateException {
		//corrispondenza localita cap prov
		String srcLoc = anag.getIndirizzoPrincipale().getLocalita();
		String srcProv = anag.getIndirizzoPrincipale().getProvincia();
		String srcCap = anag.getIndirizzoPrincipale().getCap();
		Localita loc;
		try {
			loc = new LocalitaDao().findCapByLocalitaProv(ses,srcLoc,srcProv);
		} catch (EmptyResultException e) {
			String srcLoc2 = srcLoc.replaceAll("-", " ");
			try {
				loc = new LocalitaDao().findCapByLocalitaProv(ses,srcLoc2,srcProv);
			} catch (EmptyResultException e1) {
				loc = null;
			}
		}
		if (loc == null) {
			throw new ValidationException(getCsvData(anag)+
					DISCARDED+SEP+"Localita errata "+srcLoc+" ("+srcProv+") "+srcCap);
		} else {
			if (!anag.getIndirizzoPrincipale().getCap().startsWith(loc.getCap())) {
				throw new ValidationException(getCsvData(anag)+
						DISCARDED+SEP+"CAP errato "+srcLoc+" ("+srcProv+") "+srcCap);
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
				throw new ValidationException(getCsvData(anag)+
						DISCARDED+SEP+"Provincia non riconosciuta: "+nome);
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
					DISCARDED+SEP+"Nazione non riconosciuta: "+nome);
		}
		return result;
	}
	
	private static Professioni encodeProfessione(Session ses, Anagrafiche anag, String nome) throws HibernateException, ValidationException {
		Professioni prof = null;
		if (nome.length() > 1) {
			if (!nome.equals("")) {
				prof = new ProfessioniDao().findByName(ses, nome);
			}
			if (prof == null) {
				throw new ValidationException(getCsvData(anag)+
						DISCARDED+SEP+"Professione non riconosciuta: "+nome);
			}
		}
		return prof;
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
	
	private static class AnagraficaListino {
		public Anagrafiche abbonato = null;
		public String uidListino = null;
		public String adesione = null;
		public String note = "";
	}
	
}
