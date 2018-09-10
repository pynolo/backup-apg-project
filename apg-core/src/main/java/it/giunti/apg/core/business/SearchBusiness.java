package it.giunti.apg.core.business;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.giunti.apg.core.persistence.QueryFactory;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Anagrafiche;

public class SearchBusiness {

	private static final Logger LOG = LoggerFactory.getLogger(SearchBusiness.class);
	
	private static boolean isWord(String s) {
		Pattern pattern = Pattern.compile("[a-zA-Z%\\.',-/&]+");
	    Matcher matcher = pattern.matcher(s);
	    return matcher.matches();
	}
	
	@SuppressWarnings("unused")
	private static boolean isCodiceAbbonamento(String s) {
		Pattern pattern = Pattern.compile("[A-Za-z][0-9]{6}");
	    Matcher matcher = pattern.matcher(s);
	    return matcher.matches();
	}
	
	private static boolean isUidCliente(String s) {
		Pattern pattern = Pattern.compile("[A-Za-z0-9]{6}");
	    Matcher matcher = pattern.matcher(s);
	    return matcher.matches();
	}
	
	private static boolean isCap(String s) {
		Pattern pattern = Pattern.compile("[0-9]{5}");
	    Matcher matcher = pattern.matcher(s);
	    return matcher.matches();
	}
	
	public static List<Anagrafiche> quickSearchAnagrafiche(String searchString,
			Integer offset, Integer size) throws BusinessException {
		Session ses = SessionFactory.getSession();
		List<Anagrafiche> anaList = new ArrayList<Anagrafiche>();
		List<String> sList = null;
		
		searchString = CharsetUtil.normalize(searchString);
		try {
			//Analisi searchString
			QueryFactory qf = new QueryFactory(ses, "from Anagrafiche a");
			sList = splitString(searchString);
			for (int i=0; i<sList.size(); i++) {
				if (sList.get(i).length() > 0){
					String orString = "";
					String param = sList.get(i).replace('*', '%');
					if (isWord(param)) {
						orString += " (a.searchString like :i"+i+"j11 )";
						qf.addParam("i"+i+"j11", "%"+AppConstants.SEARCH_STRING_SEPARATOR+param+AppConstants.SEARCH_STRING_SEPARATOR+"%");
					}
					if (isCap(param)) {
						if (orString.length() > 0) orString += " or ";
						orString += " (a.indirizzoPrincipale.cap like :i"+i+"j21 )";
						qf.addParam("i"+i+"j21", param);
					}
					if (isUidCliente(param)) {
						if (orString.length() > 0) orString += " or ";
						orString += " (a.uid like :i"+i+"j31 )";
						qf.addParam("i"+i+"j31", param);
					}
					if (orString.length() > 1) qf.addWhere(orString);
				}
			}
			if (qf.getConditionsCount() > 0) {
				qf.addOrder("a.dataModifica desc");
				qf.setPaging(offset, size);
				Query q = qf.getQuery();
				@SuppressWarnings("unchecked")
				List<Anagrafiche> list = (List<Anagrafiche>) q.list();
				anaList = list;
			}
			//for(Anagrafiche anag:anaList) {
			//	dao.fillAnagraficheWithLastInstances(ses, anag);
			//}
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return anaList;
	}
	
	public static List<String> splitString(String searchString) {
		List<String> sList = new ArrayList<String>();
		searchString = searchString.trim();
		//Divide con il carattere "
		boolean insideQuotes = false;
		String quoted = "";
		String searchString2 = "";
		for (int i=0; i<searchString.length(); i++) {
			//Controlla se entriamo o usciamo da una coppia di "
			String carattere = searchString.substring(i, i+1);
			if (carattere.equals("\"")) {
				if (quoted.length() > 0) {
					sList.add(quoted);
					quoted = "";
				}
				insideQuotes = !insideQuotes;
			} else {
				if (insideQuotes) {
					quoted += carattere;
				} else {
					searchString2 += carattere;
				}
			}
		}
		//Divide con il carattere spazio e trasforma eventuali spazi restanti in ":"
		String[] strings2 = searchString2.split("\\s");
		for (String string:strings2) {
			string = string.replaceAll("\\s", AppConstants.SEARCH_STRING_SEPARATOR);
			if (!string.equals("")) sList.add(string);
		}
		return sList;
	}
	
	public static String buildAnagraficheSearchString(Anagrafiche anag) {
		String searchString = "";
		//UID
		if (anag.getUid() != null) {
			searchString += AppConstants.SEARCH_STRING_SEPARATOR+anag.getUid();
		}
		//cognomeRagioneSociale
		if (anag.getIndirizzoPrincipale().getCognomeRagioneSociale() != null) {
			searchString += AppConstants.SEARCH_STRING_SEPARATOR+anag.getIndirizzoPrincipale().getCognomeRagioneSociale();
		}
		//nome
		if (anag.getIndirizzoPrincipale().getNome() != null) {
			if (anag.getIndirizzoPrincipale().getNome().length() > 1) {
				searchString += AppConstants.SEARCH_STRING_SEPARATOR+anag.getIndirizzoPrincipale().getNome();
			}
		}
		//presso
		if (anag.getIndirizzoPrincipale().getPresso() != null) {
			if (anag.getIndirizzoPrincipale().getPresso().length() > 1) {
				searchString += AppConstants.SEARCH_STRING_SEPARATOR+anag.getIndirizzoPrincipale().getPresso();
			}
		}
		//indirizzo
		if (anag.getIndirizzoPrincipale().getIndirizzo() != null) {
			if (anag.getIndirizzoPrincipale().getIndirizzo().length() > 1) {
				searchString += AppConstants.SEARCH_STRING_SEPARATOR+anag.getIndirizzoPrincipale().getIndirizzo();
			}
		}
		//CAP
		if (anag.getIndirizzoPrincipale().getCap() != null) {
			if (anag.getIndirizzoPrincipale().getCap().length() > 1) {
				searchString += AppConstants.SEARCH_STRING_SEPARATOR+anag.getIndirizzoPrincipale().getCap();
			}
		}
		//localita
		if (anag.getIndirizzoPrincipale().getLocalita() != null) {
			if (anag.getIndirizzoPrincipale().getLocalita().length() > 1) {
				searchString += AppConstants.SEARCH_STRING_SEPARATOR+anag.getIndirizzoPrincipale().getLocalita();
			}
		}
		//Cod Fisc
		if (anag.getCodiceFiscale() != null) {
			if (anag.getCodiceFiscale().length() > 1) {
				searchString += AppConstants.SEARCH_STRING_SEPARATOR+anag.getCodiceFiscale();
			}
		}
		//PIva
		if (anag.getPartitaIva() != null) {
			if (anag.getPartitaIva().length() > 1) {
				searchString += AppConstants.SEARCH_STRING_SEPARATOR+anag.getPartitaIva();
			}
		}
		//email
		if (anag.getEmailPrimaria() != null) {
			if (anag.getEmailPrimaria().length() > 1) {
				searchString += AppConstants.SEARCH_STRING_SEPARATOR+anag.getEmailPrimaria();
			}
		}
		//sostituzione spazi
		searchString = searchString.replaceAll("\\s", ":");
		if (searchString.length()>255) searchString = searchString.substring(0, 255);
		searchString += AppConstants.SEARCH_STRING_SEPARATOR;
		
		//Sostituzione caratteri speciali con caratteri base:
		searchString = CharsetUtil.normalize(searchString);
		
		return searchString.toUpperCase();
	}
	
}
