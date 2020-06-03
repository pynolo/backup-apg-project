package it.giunti.apg.automation.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Abbonamenti;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.MaterialiSpedizione;

public class ArticoliDataSource {

	private static List<MaterialiSpedizione> _msList = null;
	private static String _logoFile;
	private static String _stampFile;
	private static String _summaryString;
	private static Date _dataInvio;
	
	public static void initDataSource(List<MaterialiSpedizione> msList, String logoFileName,
			String stampFileName, Date dataInvio) {
		_msList = msList;
		_logoFile = logoFileName;
		_stampFile = stampFileName;
		_dataInvio = dataInvio;
		initSummaryString();
	}
	
	public static List<Etichetta> createBeanCollection() throws BusinessException {
  		List<Etichetta> list = new ArrayList<Etichetta>();
  		if (_msList != null) {
	  		Session ses = SessionFactory.getSession();
	  		try {
				for (MaterialiSpedizione ms:_msList) {
					if (ms.getDataAnnullamento() == null) {
						Abbonamenti abb = GenericDao.findById(ses,
								Abbonamenti.class, ms.getIdAbbonamento());
						String codAbb = abb.getCodiceAbbonamento();
						Integer copie = ms.getCopie();
						Anagrafiche anag = GenericDao.findById(ses,
								Anagrafiche.class, ms.getIdAnagrafica());
						if (anag == null) throw new BusinessException(
								"E' richiesto un dono per un promotore o pagante vuoto in " +
								abb.getCodiceAbbonamento());
						Etichetta bean = new Etichetta(ses, anag, ms.getMateriale(), codAbb, copie,
								_logoFile, _stampFile, _dataInvio);
						list.add(bean);
					}
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
		for (MaterialiSpedizione ms:_msList) {
			if (ms.getDataAnnullamento() == null) {
				if (ms.getMateriale().getCodiceMeccanografico() != null) {
					String key = ms.getMateriale().getCodiceMeccanografico().trim();
					increaseMap(key, copieCmMap);
				}
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
	
	private static void increaseMap(String key, Map<String, Integer> map) {
		Integer copie = map.get(key);
		if (copie == null) {
			copie = 1;
		} else {
			copie++;
		}
		map.put(key, copie);
	}

	public static String getSummaryString() {
		return _summaryString;
	}
	
}
