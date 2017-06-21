package it.giunti.apg.automation.report;

import it.giunti.apg.automation.AutomationConstants;
import it.giunti.apg.core.business.FascicoliGroupBean;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.EvasioniFascicoli;
import it.giunti.apg.shared.model.Nazioni;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

public class ArretratiDataSource {

	private static List<FascicoliGroupBean> _fgList = null;
	private static String _logoFile;
	private static String _summaryString;
	private static Date _dataInvio;
	
	public static void initDataSource(List<FascicoliGroupBean> fgList, String logoFileName,
			Date dataInvio) {
		_fgList = fgList;
		_logoFile = logoFileName;
		_dataInvio = dataInvio;
		initSummaryString();
	}
	
	public static List<Etichetta> createBeanCollection() throws BusinessException {
  		List<Etichetta> list = new ArrayList<Etichetta>();
  		if (_fgList != null) {
	  		Session ses = SessionFactory.getSession();
	  		try {
				for (FascicoliGroupBean fg:_fgList) {
					Nazioni naz = fg.getIstanzaAbbonamento().getAbbonato().getIndirizzoPrincipale().getNazione();
					String stampFileName = AutomationConstants.IMG_STAMP_PERIODICO;
					if (!naz.getId().equals(AppConstants.DEFAULT_ID_NAZIONE_ITALIA)) {
						stampFileName = AutomationConstants.IMG_STAMP_ECONOMY;
					}
					Etichetta bean = new Etichetta(ses, fg, _logoFile, stampFileName, _dataInvio);
					list.add(bean);
				}
			} catch (BusinessException e) {
				throw e;
			} finally {
				ses.close();
			}
  		}
  		return list;
	}

	private static void initSummaryString() {
		//Create a map of CM - quantity
		Map<String, Integer> copieCmMap = new HashMap<String, Integer>();
		for (FascicoliGroupBean fg:_fgList) {
			for (EvasioniFascicoli ef:fg.getEvasioniFacicoliList()) {
				String key = ef.getFascicolo().getCodiceMeccanografico();
				Integer copie = copieCmMap.get(key);
				if (copie == null) copie = 0;
				copie += ef.getCopie();
				copieCmMap.put(key, copie);
			}
		}
		//Make a string with collected summary:
		String result = "";
		List<String> keyList = new ArrayList<String>();
		keyList.addAll(copieCmMap.keySet());
		Collections.sort(keyList);
		for (String key:keyList) {
			if (result.length() > 0) result += "\r\n";
			result += key+": "+copieCmMap.get(key);
		}
		_summaryString = result;
	}

	public static String getSummaryString() {
		return _summaryString;
	}
	
}
