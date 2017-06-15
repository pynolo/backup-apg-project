package it.giunti.apg.shared;

import it.giunti.apg.shared.model.Opzioni;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class AbstractOpzioniUtil {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 6512495123855046979L;
	
	private String instance;
	
	public AbstractOpzioniUtil(String s) {
		instance = s;
	}
	
	public AbstractOpzioniUtil(List<Opzioni> opzList) {
		instance = opzListToString(opzList);
	}
	
	public AbstractOpzioniUtil(Set<Integer> opzIdArray) {
		instance = opzIdSetToString(opzIdArray);
	}
	
	public List<Integer> getOpzioniIdList() {
		return stringToOpzList(instance);
	}
	
	public abstract String opzListToString(List<Opzioni> opzList);
	//	String result = "";
	//	if (opzList != null) {
	//		for (Opzioni opz:opzList) {
	//			result += DF.format(opz.getId())+AppConstants.SUPPL_SEPARATOR;
	//		}
	//		//Se esiste un elenco opzioni rimuove l'ultimo SEPARATOR
	//		if (result.length()>0) {
	//			result = result.substring(0, result.length()-1);
	//		}
	//	}
	//	return result;
	//}

	private String opzIdSetToString(Set<Integer> opzIdArray) {
		String result = "";
		for (int id:opzIdArray) {
			result += id+AppConstants.SUPPL_SEPARATOR;
		}
		//Se esiste un elenco opzioni rimuove l'ultimo SEPARATOR
		if (result.length()>0) {
			result = result.substring(0, result.length()-1);
		}
		return result;
	}
	
	private List<Integer> stringToOpzList(String s) {
		List<Integer> result = new ArrayList<Integer>();
		if (s == null) return result;
		String[] idArray = s.split(AppConstants.SUPPL_SEPARATOR);
		for (String idString:idArray) {
			try {
				Integer id = Integer.valueOf(idString);
				result.add(id);
			} catch (NumberFormatException e) {}
		}
		return result;
	}
	
	@Override
	public String toString() {
		return instance;
	}
}
