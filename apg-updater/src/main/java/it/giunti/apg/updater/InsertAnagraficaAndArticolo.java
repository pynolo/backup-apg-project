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
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InsertAnagraficaAndArticolo {
	
	private static final Logger LOG = LoggerFactory.getLogger(InsertAnagraficaAndArticolo.class);
	
	/** FORMATO FILE
	 * 0 titolo
	 * 1 cognome-rag.soc
	 * 2 nome
	 * 3 indirizzo stradale
	 * 4 cap
	 * 5 localita
	 * 6 sigla provincia
	 * 7 nazione
	 * 8 CM articolo/specimen
	 * 9 data spedizione dd/MM/aaaa
	 */
	
	private static final String SEPARATOR_REGEX = "\\;";
	private static final String SEP = ";";
	
	private static EvasioniArticoliDao eaDao = new EvasioniArticoliDao();
	private static ArticoliDao aDao = new ArticoliDao();
	
	public static void parseFileAnagrafiche(String csvFilePath, String letteraPeriodico) 
			throws BusinessException, IOException {
		File logFile = File.createTempFile("import_"+letteraPeriodico+"_", ".csv");
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(logFile));
		LOG.info("Log: "+logFile.getAbsolutePath());
		File csvFile = new File(csvFilePath);
		FileInputStream fstream = new FileInputStream(csvFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream, AppConstants.CHARSET));
		int count = 0;
		int errors = 0;
		try {
			Session ses = SessionFactory.getSession();
			Transaction trn = ses.beginTransaction();
			try {
				//Ciclo su tutte le righe
				String line = br.readLine();
				while (line != null) {
					try {
						AnagraficaArticolo aa = parseAnagraficaArticolo(ses, line);
						addEvasioneArticolo(ses, aa.anagrafica, aa.articolo,
								letteraPeriodico, aa.dataInvio);
						String nome = "";
						if (aa.anagrafica.getIndirizzoPrincipale().getNome() != null)
							nome = aa.anagrafica.getIndirizzoPrincipale().getNome();
						String message = aa.anagrafica.getEmailPrimaria()+SEP+
								aa.anagrafica.getIndirizzoPrincipale().getCognomeRagioneSociale()+SEP+
								nome+SEP+
								aa.anagrafica.getIndirizzoPrincipale().getCap()+SEP+
								"OK spedito "+aa.articolo.getCodiceMeccanografico()+" il "+
								ServerConstants.FORMAT_DAY.format(aa.dataInvio);
						writer.write(message+"\r\n");
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
		LOG.info("Aggiunte "+count+" anagrafiche ("+errors+" errori)");
	}
	
	private static AnagraficaArticolo parseAnagraficaArticolo(Session ses, String line) 
			throws BusinessException, ValidationException {
		String[] values = line.split(SEPARATOR_REGEX);
		AnagraficaArticolo aa = null;
		try {
			Anagrafiche anag = new AnagraficheDao().createAnagrafiche(ses);
			try {
				//Anagrafica
				String utente = ServerConstants.DEFAULT_SYSTEM_USER;
				try {
					anag.setEmailPrimaria(values[8].toUpperCase().trim());
				} catch (Exception e) {	}
				anag.getIndirizzoPrincipale().setTitolo(values[0].toUpperCase().trim());
				anag.getIndirizzoPrincipale().setCognomeRagioneSociale(values[1].toUpperCase().trim());
				if (values[2].toUpperCase().trim().length() > 31) {
					String nome = "";
					if (anag.getIndirizzoPrincipale().getNome() != null) nome = anag.getIndirizzoPrincipale().getNome();
					throw new ValidationException(anag.getEmailPrimaria()+SEP+
							anag.getIndirizzoPrincipale().getCognomeRagioneSociale()+SEP+
							nome+SEP+
							anag.getIndirizzoPrincipale().getCap()+SEP+"Nome troppo lungo");
				}
				anag.getIndirizzoPrincipale().setNome(values[2].toUpperCase().trim());
				anag.getIndirizzoPrincipale().setIndirizzo(values[3].toUpperCase().trim());
				anag.getIndirizzoPrincipale().setLocalita(values[5].toUpperCase().trim());
				if (values[4].toUpperCase().trim().length() > 5) {
					String nome = "";
					if (anag.getIndirizzoPrincipale().getNome() != null) nome = anag.getIndirizzoPrincipale().getNome();
					throw new ValidationException(anag.getEmailPrimaria()+SEP+
							anag.getIndirizzoPrincipale().getCognomeRagioneSociale()+SEP+
							nome+SEP+
							anag.getIndirizzoPrincipale().getCap()+SEP+"CAP troppo lungo");
				}
				anag.getIndirizzoPrincipale().setCap(values[4].toUpperCase().trim());
				String prov = values[6].toUpperCase().trim();
				prov = encodeProvincia(ses, anag, prov);
				anag.getIndirizzoPrincipale().setProvincia(prov);
				Nazioni nazione = encodeNazione(ses, anag, values[7].toUpperCase().trim());
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
					throw new ValidationException(anag.getEmailPrimaria()+SEP+
							anag.getIndirizzoPrincipale().getCognomeRagioneSociale()+SEP+
							anag.getIndirizzoPrincipale().getNome()+SEP+
							anag.getIndirizzoPrincipale().getCap()+SEP+"Simile a UID["+existing.getUid()+"] "+
							existing.getIndirizzoPrincipale().getCognomeRagioneSociale()+" "+
							existing.getIndirizzoPrincipale().getCap());
				}
				//EvasioneArticolo
				Articoli articolo = encodeArticolo(ses, anag, values[8].toUpperCase().trim());
				Date dataInvio;
				try {
					dataInvio = ServerConstants.FORMAT_DAY.parse(values[9].trim());
				} catch (ParseException e) {
					throw new ValidationException(anag.getEmailPrimaria()+SEP+
							anag.getIndirizzoPrincipale().getCognomeRagioneSociale()+SEP+
							anag.getIndirizzoPrincipale().getNome()+SEP+
							anag.getIndirizzoPrincipale().getCap()+SEP+"Data errata: "+values[9].trim());
				}
				
				aa = new AnagraficaArticolo();
				aa.anagrafica=anag;
				aa.articolo=articolo;
				aa.dataInvio=dataInvio;
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
		LOG.info("OK: "+aa.anagrafica.getUid()+" "+aa.anagrafica.getIndirizzoPrincipale().getCognomeRagioneSociale()+" "+aa.anagrafica.getIndirizzoPrincipale().getNome());
		return aa;
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
		String streetPrefix = transAnag.getIndirizzoPrincipale().getIndirizzo();
		if (streetPrefix.length() > 5) streetPrefix = streetPrefix.substring(0, streetPrefix.length()-5);
		List<Anagrafiche> anagList = findSimilar(ses,
				transAnag.getIndirizzoPrincipale().getCognomeRagioneSociale(),
				streetPrefix, transAnag.getIndirizzoPrincipale().getCap());
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
					(ia.getFascicoloFine().getDataFine().after(now) || ia.getFascicoloInizio().getDataInizio().before(now))) {
				throw new ValidationException(anag.getEmailPrimaria()+SEP+
						anag.getIndirizzoPrincipale().getCognomeRagioneSociale()+SEP+
						anag.getIndirizzoPrincipale().getNome()+SEP+
						anag.getIndirizzoPrincipale().getCap()+SEP+"Possiede abbonamento "+
							ia.getAbbonamento().getCodiceAbbonamento()+" UID["+ia.getId()+"]");
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
					anag.getIndirizzoPrincipale().getNome()+SEP+
					anag.getIndirizzoPrincipale().getCap()+SEP+"Localita' errata "+
				anag.getIndirizzoPrincipale().getLocalita()+" ("+
				anag.getIndirizzoPrincipale().getProvincia()+") "+
				anag.getIndirizzoPrincipale().getCap());
		} else {
			if (!anag.getIndirizzoPrincipale().getCap().startsWith(loc.getCap())) {
				throw new ValidationException(anag.getEmailPrimaria()+SEP+
						anag.getIndirizzoPrincipale().getCognomeRagioneSociale()+SEP+
						anag.getIndirizzoPrincipale().getNome()+SEP+
						anag.getIndirizzoPrincipale().getCap()+SEP+"CAP errato "+
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
						anag.getIndirizzoPrincipale().getNome()+SEP+
						anag.getIndirizzoPrincipale().getCap()+SEP+"Provincia non riconosciuta: "+nome);
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
					anag.getIndirizzoPrincipale().getNome()+SEP+
					anag.getIndirizzoPrincipale().getCap()+SEP+"Nazione non riconosciuta: "+nome);
		}
		return result;
	}
	
	private static Articoli encodeArticolo(Session ses, Anagrafiche anag, String cm) throws HibernateException, ValidationException {
		Articoli result = aDao.findByCm(ses, cm);
		if (result == null) {
			throw new ValidationException(anag.getEmailPrimaria()+SEP+
					anag.getIndirizzoPrincipale().getCognomeRagioneSociale()+SEP+
					anag.getIndirizzoPrincipale().getNome()+SEP+
					anag.getIndirizzoPrincipale().getCap()+SEP+"Articolo non riconosciuto: "+cm);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private static List<Anagrafiche> findSimilar(Session ses, String cognomeRagioneSociale,
			String streetPrefix, String cap) {
		String qs = "from Anagrafiche a where "+
				"a.indirizzoPrincipale.cognomeRagioneSociale like :s1 and "+
				"a.indirizzoPrincipale.indirizzo like :s2 and "+
				"a.indirizzoPrincipale.cao like :s3 "+
				"order by a.dataModifica desc";
		Query q = ses.createQuery(qs);
		q.setParameter("s1", cognomeRagioneSociale+"%", StringType.INSTANCE);
		q.setParameter("s2", streetPrefix+"%", StringType.INSTANCE);
		q.setParameter("s3", "%"+cap, StringType.INSTANCE);
		List<Anagrafiche> anaList = (List<Anagrafiche>) q.list();
		if (anaList != null) {
			if (anaList.size() > 0) {
				return anaList;
			}
		}
		return anaList;
	}
	
	private static class AnagraficaArticolo {
		public Anagrafiche anagrafica = null;
		public Articoli articolo = null;
		public Date dataInvio = null;
	}
}
