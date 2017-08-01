package it.giunti.apg.updater.archive;

import it.giunti.apg.core.persistence.AnagraficheDao;
import it.giunti.apg.core.persistence.LocalitaDao;
import it.giunti.apg.core.persistence.NazioniDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.Indirizzi;
import it.giunti.apg.shared.model.Localita;
import it.giunti.apg.shared.model.Nazioni;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class MixedCaseConversion {

	private static int PAGE_SIZE = 500;
	private static AnagraficheDao anagDao = new AnagraficheDao();
	private static LocalitaDao locDao = new LocalitaDao();
	private static NazioniDao nazDao = new NazioniDao();
	
	@SuppressWarnings("unchecked")
	public static void updateAnagraficheCase() 
			throws BusinessException, IOException {
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		List<Anagrafiche> aList = new ArrayList<Anagrafiche>();
		List<Localita> lList = new ArrayList<Localita>();
		List<Nazioni> nList = new ArrayList<Nazioni>();
		int offset = 0;
		try {
			//Update Anagrafiche
			String hql = "from Anagrafiche a order by a.id";
			do {
				Query q = ses.createQuery(hql);
				q.setFirstResult(offset);
				q.setMaxResults(PAGE_SIZE);
				aList = (List<Anagrafiche>) q.list();
				for (Anagrafiche a:aList) {
					updateCaseAnagrafica(ses, a);
				}
				offset += aList.size();
				System.out.println("Aggiornate "+offset+" anagrafiche");
				ses.flush();
				ses.clear();
			} while (aList.size() == PAGE_SIZE);
			//Update Localita
			hql = "from Localita l order by l.id";
			do {
				Query q = ses.createQuery(hql);
				q.setFirstResult(offset);
				q.setMaxResults(PAGE_SIZE);
				lList = (List<Localita>) q.list();
				for (Localita l:lList) {
					updateCaseLocalita(ses, l);
				}
				offset += lList.size();
				System.out.println("Aggiornate "+offset+" località");
				ses.flush();
				ses.clear();
			} while (lList.size() == PAGE_SIZE);
			//Update Nazioni
			hql = "from Nazioni n order by n.id";
			do {
				Query q = ses.createQuery(hql);
				q.setFirstResult(offset);
				q.setMaxResults(PAGE_SIZE);
				nList = (List<Nazioni>) q.list();
				for (Nazioni n:nList) {
					updateCaseNazione(ses, n);
				}
				offset += lList.size();
				System.out.println("Aggiornate "+offset+" nazioni");
				ses.flush();
				ses.clear();
			} while (lList.size() == PAGE_SIZE);
			trn.commit();
		} catch (HibernateException e) {
			trn.rollback();
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
	}
	
	private static void updateCaseAnagrafica(Session ses, Anagrafiche a) {
		if (a.getEmailPrimaria() != null)
			a.setEmailPrimaria(a.getEmailPrimaria().toLowerCase());
		if (a.getEmailSecondaria() != null)
			a.setEmailSecondaria(a.getEmailSecondaria().toLowerCase());
		if (a.getNote() != null)
			a.setNote(a.getNote().toLowerCase());
		if (a.getIndirizzoPrincipale() != null)
			updateCaseIndirizzo(a.getIndirizzoPrincipale());
		if (a.getIndirizzoFatturazione() != null)
			updateCaseIndirizzo(a.getIndirizzoFatturazione());
		System.out.println(a.getIndirizzoPrincipale().getCognomeRagioneSociale()+" "+
				a.getIndirizzoPrincipale().getNome()+" "+
				a.getIndirizzoPrincipale().getPresso()+" "+
				a.getIndirizzoPrincipale().getIndirizzo()+" "+
				a.getIndirizzoPrincipale().getLocalita()+" "+
				a.getIndirizzoPrincipale().getTitolo());//TODO
		anagDao.updateUnlogged(ses, a);
	}
	
	private static void updateCaseIndirizzo(Indirizzi ind) {
		ind.setCognomeRagioneSociale(toMixedCase(ind.getCognomeRagioneSociale()));
		ind.setIndirizzo(toMixedCase(ind.getIndirizzo()));
		ind.setLocalita(toMixedCase(ind.getLocalita()));
		ind.setNome(toMixedCase(ind.getNome()));
		ind.setPresso(toMixedCase(ind.getPresso()));
		ind.setTitolo(toMixedCase(ind.getTitolo()));
		if (ind.getProvincia() != null)
			ind.setProvincia(ind.getProvincia().toUpperCase());
	}
	
	private static void updateCaseLocalita(Session ses, Localita loc) {
		loc.setNome(toMixedCase(loc.getNome()));
		locDao.update(ses, loc);
	}
	
	private static void updateCaseNazione(Session ses, Nazioni naz) {
		naz.setNomeNazione(toMixedCase(naz.getNomeNazione()));
		nazDao.update(ses, naz);
	}
	
	public static String toMixedCase(String s) {
		if (s == null) return null;
		//Phase 1: aggregate
		String result1 = "";
		for (int i=s.length()-1; i >= 0; i--) {
			String ch = s.substring(i, i+1).toLowerCase();
			String r1 = "";
			if (ch.equals("'") || ch.equals("`")) {
				//Controllo carattere precedente
				if (i > 0) {
					String prev = s.substring(i-1, i).toLowerCase();
					if (prev.contains("a")) r1 = "à";
					if (prev.contains("e")) r1 = "è";
					if (prev.contains("i")) r1 = "ì";
					if (prev.contains("o")) r1 = "ò";
					if (prev.contains("u")) r1 = "ù";
					if (r1.length()>0) i--;
				}
			}
			if (r1.equals("")) r1 = ch;
			result1 = r1+result1;
		}
		//Phase 2: change case
		String wordStart = " .-/\"&()#,_'";
		String result2 = "";
		for (int i = 0; i < result1.length(); i++) {
			String ch = "";
			if (i > 0) ch = result1.substring(i-1, i);
			if (i == 0 || wordStart.contains(ch)) {
				//First char of word
				result2 += result1.substring(i, i+1).toUpperCase();
			} else {
				result2 += result1.substring(i, i+1).toLowerCase();
			}
		}
		return result2;
	}
	
}
