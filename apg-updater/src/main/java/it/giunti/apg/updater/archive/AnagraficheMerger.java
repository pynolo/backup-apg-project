package it.giunti.apg.updater.archive;

import it.giunti.apg.core.persistence.AnagraficheDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.IndirizziDao;
import it.giunti.apg.core.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.core.persistence.PagamentiDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.EvasioniArticoli;
import it.giunti.apg.shared.model.Indirizzi;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Pagamenti;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AnagraficheMerger {

	private static final Logger LOG = LoggerFactory.getLogger(AnagraficheMerger.class);

	private static final String SEP = ";";
	private static final String[] prefixList = {
		"V.LE ", "VLE ", "VIALE ",
		"P.LE ", "PLE ", "PIAZZALE ",
		"P.ZA ", "P.ZZA ", "PZA ", "PIAZZA ",
		"C.SO ", "CSO ", "CORSO ",
		"L.GO ",  "LARGO ",
		"LOC.", "LOC ", "LOCALITA ", "LOCALITA' ", "LOCALITÀ ",
		"VIA STRADA ", "VIA STR", 
		"STR.", "STR ", "STRADA ",
		"V.", "V ", "VIA "};
	
	private static final int PAGE_SIZE = 500;
	//private static final String UTF8 = "UTF-8";
	private static IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
	private static PagamentiDao pagDao = new PagamentiDao();
	private static AnagraficheDao anagDao = new AnagraficheDao();
	private static IndirizziDao indDao = new IndirizziDao();
	
	public static void mergeAnagrafiche(String reportFileName, boolean debug) throws BusinessException, IOException {
		ReportWriter reportWriter = new ReportWriter(reportFileName);
		massiveMerge(debug, reportWriter);
		reportWriter.close();
	}
	
	private static void massiveMerge(boolean debug, ReportWriter reportWriter) throws BusinessException, IOException {
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		AnagraficheDao dao = new AnagraficheDao();
		Map<String, Anagrafiche> anagMap = new HashMap<String, Anagrafiche>();
		int count = 0;
		int merged = 0;
		Anagrafiche ana = null;
		try {
			LOG.info("Query in corso per tutte le anagrafiche...");
			List<Anagrafiche> aList = new ArrayList<Anagrafiche>();
			/* La paginazione è troppo lenta */
			//List<Anagrafiche> list = null;
			//int offset = 0;
			//do {
			//	list = dao.findAnagraficheByLastModified(ses, offset, PAGE_SIZE);
			//	offset += list.size();
			//	aList.addAll(list);
			//	System.out.print(".");
			//} while (list.size() == PAGE_SIZE);
			int pageSize = Integer.MAX_VALUE;
			//if (debug) pageSize = 200;
			aList = dao.findAnagraficheByLastModified(ses, 0, pageSize);
			LOG.info("Query eseguita. Estratte "+aList.size()+" anagrafiche.");
			for (Anagrafiche a:aList) {
				ana = a;
				String digest = produceDigest(a);
				Anagrafiche firstFound = anagMap.get(digest);
				if (firstFound == null) {
					anagMap.put(digest, a);
					reportWriter.print(digest, a, false);
				} else {
					merged++;
					deduplicaUpdate(ses, debug, a, firstFound);
					reportWriter.print(digest, a, true);
				}
				count++;
				if (count%PAGE_SIZE == 0) {
					ses.flush();
					ses.clear();
					LOG.info("Progress: "+count+" ("+merged+" dup)");
				}
			}
			LOG.info("Progress: "+count+" ("+merged+" dup)");
			trn.commit();
		} catch (HibernateException e) {
			trn.rollback();
			LOG.error(ana.getUid()+" "+ana.getIndirizzoPrincipale().getCognomeRagioneSociale());
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
	}
	
	private static void deduplicaUpdate(Session ses, boolean debug,
			Anagrafiche source, Anagrafiche destination)
			throws HibernateException, IOException {
		//Sposta abbonamenti (personali)
		List<IstanzeAbbonamenti> iaList = GenericDao.findByProperty(ses, IstanzeAbbonamenti.class, "abbonato.id", source.getId());
		for (IstanzeAbbonamenti ia:iaList) {
			ia.setAbbonato(destination);
			if (!debug) iaDao.update(ses, ia);
		}
		//Sposta abbonamenti (pagati)
		iaList = GenericDao.findByProperty(ses, IstanzeAbbonamenti.class, "pagante.id", source.getId());
		for (IstanzeAbbonamenti ia:iaList) {
			ia.setPagante(destination);
			if (!debug) iaDao.update(ses, ia);
		}
		//Sposta istanze (promosse)
		iaList = GenericDao.findByProperty(ses, IstanzeAbbonamenti.class, "promotore.id", source.getId());
		for (IstanzeAbbonamenti ia:iaList) {
			ia.setPromotore(destination);
			if (!debug) iaDao.update(ses, ia);
		}
		//Sposta credito personale
		List<Pagamenti> pagList = GenericDao.findByProperty(ses, Pagamenti.class, "anagrafica.id", source.getId());
		for (Pagamenti pag:pagList) {
			pag.setAnagrafica(destination);
			if (!debug) pagDao.update(ses, pag);
		}
		//Arricchisce l'anagrafica di destinazione con dati presenti solo in quella sorgente
		enrichOptionalData(source, destination);
		//**Fase di rimozione**
		Indirizzi indPrinc = source.getIndirizzoPrincipale();
		Indirizzi indFatt = source.getIndirizzoFatturazione();
		ses.flush();
		
		//elimina anagrafica
		if (!debug) {
			anagDao.delete(ses, source);
			//ses.delete(source);
			ses.evict(source);
			source = null;
		}

		//elimina indirizzi
		if (!debug) {
			try {
				ses.flush();
				indDao.delete(ses, indPrinc);
				ses.flush();
				indDao.delete(ses, indFatt);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}
	
	private static String produceDigest(Anagrafiche anag) {
		String sourceString = anag.getIndirizzoPrincipale().getCognomeRagioneSociale();
		if (anag.getIndirizzoPrincipale().getNome() != null) {
			if (anag.getIndirizzoPrincipale().getNome().length() > 0) sourceString += anag.getIndirizzoPrincipale().getNome();
		}
		Indirizzi ind = anag.getIndirizzoPrincipale();
		sourceString += removePrefix(ind.getIndirizzo());
		if (ind.getPresso() != null) sourceString += ind.getPresso();
		if (ind.getCap() != null) sourceString += ind.getCap();
		if (ind.getLocalita() != null) sourceString += ind.getLocalita();
		if (ind.getProvincia() != null) sourceString += ind.getProvincia();
		if (ind.getNazione() != null) sourceString += ind.getNazione().getNomeNazione();
		
		sourceString = StringUtils.replace(sourceString, " ", "", -1);//Toglie gli spazi
		sourceString = StringUtils.replace(sourceString, ".", "", -1);
		sourceString = StringUtils.replace(sourceString, ",", "", -1);
		sourceString = StringUtils.replace(sourceString, "(", "", -1);
		sourceString = StringUtils.replace(sourceString, ")", "", -1);
		sourceString = StringUtils.replace(sourceString, "/", "", -1);
		sourceString = StringUtils.replace(sourceString, "'", "", -1);
		sourceString = StringUtils.replace(sourceString, "-", "", -1);
		sourceString = StringUtils.replace(sourceString, "*", "", -1);
		sourceString = StringUtils.replace(sourceString, "#", "", -1);
		
		//sourceString = sourceString.replaceAll("\\s", "");
		//sourceString = sourceString.replaceAll("\\.", "");
		//sourceString = sourceString.replaceAll(",", "");
		//sourceString = sourceString.replaceAll("\\(", "");
		//sourceString = sourceString.replaceAll("\\)", "");
		//sourceString = sourceString.replaceAll("/", "");
		//sourceString = sourceString.replaceAll("'", "");
		//sourceString = sourceString.replaceAll("-", "");
		//sourceString = sourceString.replaceAll("*", "");
		//sourceString = sourceString.replaceAll("#", "");
		return sourceString.toUpperCase();
	}
	
	//private static void writeReport(Map<String, Integer> reportMap,  FileWriter reportWriter)
	//		throws IOException {
	//	for (String key:reportMap.keySet()) {
	//		reportWriter.write(key+"; "+reportMap.get(key)+"\r\n");
	//	}
	//}
	
	private static String removePrefix(String indirizzo) {
		String result = indirizzo.toUpperCase();
		for (String pref:prefixList) {
			if (result.startsWith(pref)) {
				result = result.substring(pref.length());
				return result;
			}
		}
		return result;
	}
	
	private static void enrichOptionalData(Anagrafiche source, Anagrafiche destination) {
		//Arricchisci destination con dati aggiuntivi
		if (source.getGiuntiCard()) destination.setGiuntiCard(true); 
		//Codice fiscale
		if (destination.getCodiceFiscale() == null) destination.setCodiceFiscale("");
		if (source.getCodiceFiscale() == null) source.setCodiceFiscale("");
		if ((source.getCodiceFiscale().length() > 0) && (destination.getCodiceFiscale().length() == 0)) {
			destination.setCodiceFiscale(source.getCodiceFiscale());
		}
		//CodiceSap
		if (destination.getCodiceSap() == null) destination.setCodiceSap("");
		if (source.getCodiceSap() == null) source.setCodiceSap("");
		if ((source.getCodiceSap().length() > 0) && (destination.getCodiceSap().length() == 0)) {
			destination.setCodiceSap(source.getCodiceSap());
		}
		//Email primaria
		if (destination.getEmailPrimaria() == null) destination.setEmailPrimaria("");
		if (source.getEmailPrimaria() == null) source.setEmailPrimaria("");
		if ((source.getEmailPrimaria().length() > 0) && (destination.getEmailPrimaria().length() == 0)) {
			destination.setEmailPrimaria(source.getEmailPrimaria());
		}
		//Email secondaria
		if (destination.getEmailSecondaria() == null) destination.setEmailSecondaria("");
		if (source.getEmailSecondaria() == null) source.setEmailSecondaria("");
		if ((source.getEmailSecondaria().length() > 0) && (destination.getEmailSecondaria().length() == 0)) {
			destination.setEmailSecondaria(source.getEmailSecondaria());
		}
		//Note
		if (destination.getNote() == null) destination.setNote("");
		if (source.getNote() == null) source.setNote("");
		if (source.getNote().length() > 0) {
			String note = destination.getNote()+source.getNote();
			if (note.length() > 255) note = note.substring(0,255);
			destination.setNote(note);
		}
		//PartitaIva
		if (destination.getPartitaIva() == null) destination.setPartitaIva("");
		if (source.getPartitaIva() == null) source.setPartitaIva("");
		if ((source.getPartitaIva().length() > 0) && (destination.getPartitaIva().length() == 0)) {
			destination.setPartitaIva(source.getPartitaIva());
		}
		//Professione
		if ((destination.getProfessione() == null) && (source.getProfessione() != null)) {
			destination.setProfessione(source.getProfessione());
		}
		//TelCasa
		if (destination.getTelCasa() == null) destination.setTelCasa("");
		if (source.getTelCasa() == null) source.setTelCasa("");
		if ((source.getTelCasa().length() > 0) && (destination.getTelCasa().length() == 0)) {
			destination.setTelCasa(source.getTelCasa());
		}
		//TelMobile
		if (destination.getTelMobile() == null) destination.setTelMobile("");
		if (source.getTelMobile() == null) source.setTelMobile("");
		if ((source.getTelMobile().length() > 0) && (destination.getTelMobile().length() == 0)) {
			destination.setTelMobile(source.getTelMobile());
		}
		//Indirizzo fatturazione
		if (source.getIndirizzoFatturazione().getIndirizzo() == null) 
			source.getIndirizzoFatturazione().setIndirizzo("");
		if (destination.getIndirizzoFatturazione().getIndirizzo() == null) 
			destination.getIndirizzoFatturazione().setIndirizzo("");
		if ((source.getIndirizzoFatturazione().getIndirizzo().length() > 0) &&
				(destination.getIndirizzoFatturazione().getIndirizzo().length() == 0)) {
			//swap
			Indirizzi swap = source.getIndirizzoFatturazione();
			source.setIndirizzoFatturazione(destination.getIndirizzoFatturazione());
			destination.setIndirizzoFatturazione(swap);
		}
		//Sesso
		if (source.getSesso() != null) {
			if (source.getSesso().equals(AppConstants.SESSO_F))
				destination.setSesso(AppConstants.SESSO_F);
			if (source.getSesso() == null)
				destination.setSesso(null);
		}
	}

	private static class ReportWriter {
		private FileWriter writer = null;
		
		public ReportWriter(String fileName) throws IOException {
			File report = File.createTempFile(fileName, ".csv");
			LOG.info("Report su "+report.getAbsolutePath());
			writer = new FileWriter(report);
		}
		
		public void print(String digest, Anagrafiche anag, boolean isMerged) 
				throws IOException {
			Indirizzi ind = anag.getIndirizzoPrincipale();
			String nome = anag.getIndirizzoPrincipale().getCognomeRagioneSociale();
			if (anag.getIndirizzoPrincipale().getNome() != null) nome += " "+anag.getIndirizzoPrincipale().getNome();
			String presso = "";
			if (ind.getPresso() != null) presso = ind.getPresso();
			String merged = "+";
			if (isMerged) merged = "-";
			String line = digest+SEP+
					merged+SEP+
					nome+SEP+
					presso+SEP+
					ind.getCap()+SEP+
					ind.getLocalita()+SEP+
					ind.getProvincia()+SEP+
					ind.getNazione().getNomeNazione()+"\r\n";
			writer.write(line);
		}
		
		public void close() throws IOException {
			writer.close();
		}
	}
	
	public static void matchDoniReceiver() throws BusinessException {
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		int offset = 0;
		int count = 0;
		String hql = "from EvasioniDoni ed where ed.idAnagrafica not in " +
				"(select an.id from Anagrafiche an) order by ed.id";
		try {
			do {
				Query q = ses.createQuery(hql);
//				q.setFirstResult(offset);
//				q.setMaxResults(pageSize);
				@SuppressWarnings("unchecked")
				List<EvasioniArticoli> res = (List<EvasioniArticoli>) q.list();
				count = res.size();
				offset += count;
				for (EvasioniArticoli ed:res) {
					fixAnagrafica(ses, ed);
				}
			} while (count > 0);
			LOG.info("\r\nTotale: "+offset);
			trn.commit();
		} catch (HibernateException e) {
			trn.rollback();
			//LOG.error(ana.getCodiceCliente()+" "+ana.getCognomeRagioneSociale());
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
	}

	private static void fixAnagrafica(Session ses, EvasioniArticoli ed) {
		Anagrafiche anag = GenericDao.findById(ses, Anagrafiche.class, ed.getIdAnagrafica());
		if (anag == null) {
			IstanzeAbbonamenti ia = GenericDao.findById(ses, IstanzeAbbonamenti.class, ed.getIdIstanzaAbbonamento());
			if (ia == null) {
				LOG.info("Il dono "+ed.getId()+" non ha un abbonamento abbinato: id="+ed.getIdIstanzaAbbonamento());
				LOG.info("Il dono sarà eliminato");
				ses.delete(ed);
			} else {
				//Esiste un abbonamento => corregge
				if (ed.getIdTipoDestinatario().equals(AppConstants.DEST_BENEFICIARIO)) {
					anag = ia.getAbbonato();
				}
				if (ed.getIdTipoDestinatario().equals(AppConstants.DEST_PAGANTE)) {
					anag = ia.getPagante();
				}
				if (ed.getIdTipoDestinatario().equals(AppConstants.DEST_PROMOTORE)) {
					anag = ia.getPromotore();
				}
				if (anag == null) {
					LOG.info("Abb."+ia.getAbbonamento().getCodiceAbbonamento()+": il dono "+ed.getId()+" per il "+ed.getIdTipoDestinatario()+" non è assegnabile");
				} else {
					ed.setIdAnagrafica(anag.getId());
					ses.update(ed);
					System.out.print(ia.getAbbonamento().getCodiceAbbonamento()+" ");
				}
			}
		} else {
			LOG.info("No problem: dono "+ed.getId()+" anagrafica "+anag.getUid());
		}
	}
}
