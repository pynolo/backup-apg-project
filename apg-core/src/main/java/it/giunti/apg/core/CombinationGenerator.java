package it.giunti.apg.core;

import it.giunti.apg.core.persistence.OpzioniDao;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Listini;
import it.giunti.apg.shared.model.Opzioni;
import it.giunti.apg.shared.model.OpzioniListini;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;


public class CombinationGenerator  {
	
	// string implementation
//  public static void comb2(String s) { comb2("", s); }
//  private static void comb2(String prefix, String s) {
//      System.out.println(prefix);
//      for (int i = 0; i < s.length(); i++)
//          comb2(prefix + s.charAt(i), s.substring(i + 1));
//  }  
	
	//E' la mappa della corrispondenza tra (periodici+datainizio) e combinazione
	//importi opzioni
	private static Map<String, Map<Double, Set<Opzioni>>> listiniMap =
			new HashMap<String, Map<Double, Set<Opzioni>>>();

    public static <T> void combination(List<T> objList, List<Set<T>> resultSetList) {
    	combination(new HashSet<T>(), objList, resultSetList);
    }
    
    private static <T> void combination(Set<T> prefix, List<T> objList, List<Set<T>> combSetList) {
    	combSetList.add(prefix);
        for (int i = 0; i < objList.size(); i++) {
        	Set<T> newPrefix = new HashSet<T>();
        	newPrefix.addAll(prefix);
        	newPrefix.add(objList.get(i));
        	List<T> newObjList = new ArrayList<T>();
        	for (int j=i+1; j<objList.size(); j++) {
        		newObjList.add(objList.get(j));
        	}
        	combination(newPrefix, newObjList, combSetList);
            //comb(prefix + s.charAt(i), s.substring(i + 1));
        }
    }  

	//
	//public static Map<Double, Set<Opzioni>> getPrezziOpzioniExtraMapByTipoAbb(Session ses, Integer idTipoAbb,
	//		Date extractionDate) throws BusinessException, EmptyResultException {
	//	String tipiAbbMapKey = idTipoAbb + "_" + ServerConstants.FORMAT_DAY.format(extractionDate);
	//	Map<Double, Set<Opzioni>> importoOpzioniMap = tipiAbbMap.get(tipiAbbMapKey);
	//	if (importoOpzioniMap == null) {
	//    	OpzioniDao supDao = new OpzioniDao();
	//    	List<Opzioni> opzExtraList = new ArrayList<Opzioni>();//Solo i supplementi aggiuntivi
	//		try {
	//			//Cerco Listino (e opzioni incluse)
	//			Listini lsn = new ListiniDao().findListinoByTipoAbbDate(ses, idTipoAbb, extractionDate);
	//			if (lsn == null) throw new EmptyResultException();
	//			//Cerco tutti i listini possibili
	//			List<Opzioni> allOpzList = supDao.findByPeriodicoDate(ses,
	//					lsn.getTipoAbbonamento().getPeriodico().getId(), extractionDate, false);
	//			//Elenco opzioni che si possono acquistare
	//			for (Opzioni opz:allOpzList) {
	//				boolean mandatory = false;
	//				for (OpzioniListini ol:lsn.getOpzioniListiniSet()) {
	//					if (opz.getId() == ol.getOpzione().getId()) mandatory = true;
	//				}
	//				if (!mandatory) opzExtraList.add(opz);
	//			}
	//		} catch (HibernateException e) {
	//			throw new BusinessException(e.getMessage(), e);
	//		}
	//    	List<Set<Opzioni>> supSetList = new ArrayList<Set<Opzioni>>();
	//    	combination(opzExtraList, supSetList);
	//    	
	//    	Map<Double, Set<Opzioni>> result = new HashMap<Double, Set<Opzioni>>();
	//    	for (Set<Opzioni> set:supSetList) {
	//    		Double subtotale = 0D;
	//    		for (Opzioni sup:set) {
	//    			subtotale += sup.getPrezzo();
	//    		}
	//    		Double key = subtotale;
	//    		result.put(key, set);
	//    	}
	//    	tipiAbbMap.put(tipiAbbMapKey, result);
	//    	return result;
	//	} else {
	//		return importoOpzioniMap;
	//	}
	//}

    public static Map<Double, Set<Opzioni>> getPrezziOpzioniExtraMapByListino(Session ses, Listini listino,
    		Date extractionDate) throws BusinessException {
    	String listinoMapKey = listino.getId() + "_" + ServerConstants.FORMAT_DAY.format(extractionDate);
    	Map<Double, Set<Opzioni>> importoOpzioniMap = listiniMap.get(listinoMapKey);
    	if (importoOpzioniMap == null) {
	    	OpzioniDao supDao = new OpzioniDao();
	    	List<Opzioni> opzExtraList = new ArrayList<Opzioni>();//Solo i supplementi aggiuntivi
			try {
				//Cerco tutti i listini possibili
				List<Opzioni> allOpzList = supDao.findByPeriodicoDate(ses,
						listino.getTipoAbbonamento().getPeriodico().getId(), extractionDate, false);
				//Elenco opzioni che si possono acquistare
				for (Opzioni opz:allOpzList) {
					boolean mandatory = false;
					for (OpzioniListini ol:listino.getOpzioniListiniSet()) {
						if (opz.getId() == ol.getOpzione().getId()) mandatory = true;
					}
					if (!mandatory) opzExtraList.add(opz);
				}
			} catch (HibernateException e) {
				throw new BusinessException(e.getMessage(), e);
			}
	    	List<Set<Opzioni>> supSetList = new ArrayList<Set<Opzioni>>();
	    	combination(opzExtraList, supSetList);
	    	
	    	Map<Double, Set<Opzioni>> result = new HashMap<Double, Set<Opzioni>>();
	    	for (Set<Opzioni> set:supSetList) {
	    		Double subtotale = 0D;
	    		for (Opzioni sup:set) {
	    			subtotale += sup.getPrezzo();
	    		}
	    		Double key = subtotale;
	    		result.put(key, set);
	    	}
	    	listiniMap.put(listinoMapKey, result);
	    	return result;
    	} else {
    		return importoOpzioniMap;
    	}
    }
    
	//public static Set<Opzioni> getOpzioniByTipoAbbImporto(Session ses, Double importoUnitarioSup,
	//		Integer idTipoAbb, Date extractionDate) throws BusinessException {
	//	Map<Double, Set<Opzioni>> map = getPrezziOpzioniExtraMapByTipoAbb(ses, idTipoAbb, extractionDate);
	//	for (Double dovuto:map.keySet()) {
	//		if ( ((dovuto-AppConstants.SOGLIA) <= importoUnitarioSup) && ((dovuto+AppConstants.SOGLIA) >= importoUnitarioSup) ) {
	//			return map.get(dovuto);
	//		}
	//	}
	//	return null;
	//}
    
}
