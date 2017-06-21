package it.giunti.apg.automation.report;

import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.EvasioniArticoli;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

public class ArticoliDataSource {

	private static List<EvasioniArticoli> _edList = null;
	private static String _logoFile;
	private static String _stampFile;
	private static String _summaryString;
	private static Date _dataInvio;
	
	public static void initDataSource(List<EvasioniArticoli> edList, String logoFileName,
			String stampFileName, Date dataInvio) {
		_edList = edList;
		_logoFile = logoFileName;
		_stampFile = stampFileName;
		_dataInvio = dataInvio;
		initSummaryString();
	}
	
	public static List<Etichetta> createBeanCollection() throws BusinessException {
  		List<Etichetta> list = new ArrayList<Etichetta>();
  		if (_edList != null) {
	  		Session ses = SessionFactory.getSession();
	  		try {
				for (EvasioniArticoli ed:_edList) {
					if (ed.getDataAnnullamento() == null) {
						IstanzeAbbonamenti ia = GenericDao.findById(ses,
								IstanzeAbbonamenti.class, ed.getIdIstanzaAbbonamento());
						String codAbb = ia.getAbbonamento().getCodiceAbbonamento();
						Integer copie = ia.getCopie();
						Anagrafiche anag = GenericDao.findById(ses,
								Anagrafiche.class, ed.getIdAnagrafica());
						if (anag == null) throw new BusinessException(
								"E' richiesto un dono per un promotore o pagante vuoto in " +
								ia.getAbbonamento().getCodiceAbbonamento());
						Etichetta bean = new Etichetta(ses, anag, ed.getArticolo(), codAbb, copie,
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
		for (EvasioniArticoli ed:_edList) {
			if (ed.getDataAnnullamento() == null) {
				if (ed.getArticolo().getCodiceMeccanografico() != null) {
					String key = ed.getArticolo().getCodiceMeccanografico().trim();
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
