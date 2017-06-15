package it.giunti.apg.client;

import it.giunti.apg.shared.AbstractOpzioniUtil;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Opzioni;

import java.util.List;
import java.util.Set;

import com.google.gwt.i18n.client.NumberFormat;

public class OpzioniUtil extends AbstractOpzioniUtil {

	private static final NumberFormat NF = NumberFormat.getFormat(AppConstants.SUPPL_ID_FORMAT);
	
	public OpzioniUtil(String s) {
		super(s);
	}
	
	public OpzioniUtil(List<Opzioni> opzList) {
		super(opzList);
	}
	
	public OpzioniUtil(Set<Integer> opzIdArray) {
		super(opzIdArray);
	}
	
	@Override
	public String opzListToString(List<Opzioni> opzList) {
		String result = "";
		if (opzList != null) {
			for (Opzioni opz:opzList) {
				result += NF.format(opz.getId())+AppConstants.SUPPL_SEPARATOR;
			}
			//Se esiste un elenco opzioni rimuove l'ultimo SEPARATOR
			if (result.length()>0) {
				result = result.substring(0, result.length()-1);
			}
		}
		return result;
	}
}
