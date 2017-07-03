package it.giunti.apg.automation.business;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.business.FattureBusiness;
import it.giunti.apg.core.persistence.ContatoriDao;
import it.giunti.apg.core.persistence.FattureArticoliDao;
import it.giunti.apg.core.persistence.FattureDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.AliquoteIva;
import it.giunti.apg.shared.model.Fatture;
import it.giunti.apg.shared.model.FattureArticoli;
import it.giunti.apg.shared.model.Societa;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FattureRegistroMensileBusiness {
	
	static private Logger LOG = LoggerFactory.getLogger(FattureRegistroMensileBusiness.class);
	
	private static String PAGE_TITLE = "REGISTRO DEI CORRISPETTIVI PERIODICI DIGITALI";
	private static final String EOL = "\r\n";
	private static final int PAGE_MAX_WIDTH = 130;
	private static final int PAGE_MAX_HEIGHT = 65;
	
	private static FattureArticoliDao faDao = new FattureArticoliDao();
	
	// ** file registro corrispettivi mensile con totali giornalieri **
	
	public static File createRegistroMensileFile(Session ses, 
			Date startDt, Date finishDt, String idSocieta) 
			throws BusinessException, HibernateException, IOException {
		Locale.setDefault(new Locale("it", "IT"));
		int offset = 0;
		int pageSize = 100;
		//Aggregazione fatture per giorno
		Map<String, List<Fatture>> dailyFattMap = new HashMap<String, List<Fatture>>();
		Map<String, List<String>> dailyNumFattMap = new HashMap<String, List<String>>();
		List<Fatture> fattureList = new ArrayList<Fatture>();
		List<Fatture> list = null;
		if (idSocieta != null) {
			//Cerca le stampe della societÃ  selezionata
			do {
				list = new FattureDao()
						.findBySocietaData(ses, idSocieta, startDt, finishDt, false, offset, pageSize);
				for (Fatture fattura:list) {
					if (!FattureBusiness.isFittizia(fattura)) {
						String key = ServerConstants.FORMAT_DAY.format(fattura.getDataFattura());
						//Mappa Istanze Abbonamenti
						List<Fatture> dayFattList = dailyFattMap.get(key);
						if (dayFattList == null) {
							dayFattList = new ArrayList<Fatture>();
							dailyFattMap.put(key, dayFattList);
						}
						dayFattList.add(fattura);
						//Mappa Numeri Fatture
						List<String> dayNumFattList = dailyNumFattMap.get(key);
						if (dayNumFattList == null) {
							dayNumFattList = new ArrayList<String>();
							dailyNumFattMap.put(key, dayNumFattList);
						}
						dayNumFattList.add(fattura.getNumeroFattura());
					}
				}
				fattureList.addAll(list);
				offset = fattureList.size();
				LOG.debug("Parsed "+offset+" prints");
			} while (list.size() > 0);
		}
		
		File f = null;
		if (dailyFattMap.size() > 0) {
			//inizializzazione num pagina
			Societa societa = GenericDao.findById(ses, Societa.class, idSocieta);
			ContatoriDao cDao = new ContatoriDao();
			cDao.initPagRegMensile(ses, societa.getPrefissoFatture(), startDt);
			//paginazione
			List<List<String>> pageList = formatTxtReport(ses,
					startDt, societa, dailyFattMap, dailyNumFattMap);
			pageList.add(createSummaryPage(ses, 
					startDt, finishDt, societa, fattureList));
			String document = formatPages(pageList);
			//File creation
			f = File.createTempFile("registroMensile", ".txt");
			f.deleteOnExit();
			FileWriter fw = new FileWriter(f);
			fw.write(document);
			fw.close();
			//Commit del contatore pagine
			cDao.commitPagRegMensile(ses, societa.getPrefissoFatture(), startDt);
		}
		return f;
	}
	
	private static String getPageHeader(Societa societa, Date dataRiferimento, int pageNum) {
		String pageHeader = societa.getNome().toUpperCase()+
				"  P.IVA: "+societa.getCodiceFiscale()+
				"  COD.FISC: "+societa.getCodiceFiscale()+
				"  pag: "+ServerConstants.FORMAT_YEAR.format(dataRiferimento)+"/"+pageNum;
		return pageHeader;
	}
	
	private static String formatPages(List<List<String>> pageList) {
		String document = "";
		for (List<String> lineList:pageList) {
			for (String line:lineList) {
				document += line+EOL;
			}
			for (int i=0; i<PAGE_MAX_HEIGHT-lineList.size(); i++) {
				document += EOL;
			}
		}
		return document;
	}
	
	
	// ** Daily **
	
	
	private static List<List<String>> formatTxtReport(Session ses,
			Date dataRiferimento, Societa societa,
			Map<String, List<Fatture>> dailyFattMap,
			Map<String, List<String>> dailyNumFattMap) throws BusinessException {
		List<List<String>> paragraphList = new ArrayList<List<String>>();
		List<String> keyList = new ArrayList<>();
		keyList.addAll(dailyFattMap.keySet());
		Collections.sort(keyList);//ordered by date (key is a formatted date)
		for (String key:keyList) {
			List<String> dailyTot = formatDailyTot(ses, key, dailyFattMap.get(key));
			paragraphList.add(dailyTot);
			List<String> fattureDetail = formatFattureDetail(key, dailyNumFattMap.get(key));
			paragraphList.add(fattureDetail);
		}
		// Create pages
		List<List<String>> pageList = new ArrayList<List<String>>();
		List<String> rowList = new ArrayList<String>();
		ContatoriDao cDao = new ContatoriDao();
		int pageNum = cDao.nextTempPagRegMensile(ses, societa.getPrefissoFatture(),
				dataRiferimento); 
		rowList.add(getPageHeader(societa, dataRiferimento, pageNum));
		rowList.add("");
		rowList.add(PAGE_TITLE);
		for (List<String> paragraph:paragraphList) {
			rowList.add("");
			for (String line:paragraph) {
				if (rowList.size() < PAGE_MAX_HEIGHT) {
					//Aggiunge la singola riga se c'e' ancora spazio a fondo pagina
					rowList.add(line);
				} else {
					//Aggiunge la lista di righe (pagina) alla lista di pagine
					pageList.add(rowList);
					//Acquisisce il num pagina successivo
					pageNum = cDao.nextTempPagRegMensile(ses, societa.getPrefissoFatture(),
							dataRiferimento);
					//Inizia un nuovo elenco di righe (pagina)
					rowList = new ArrayList<String>();
					rowList.add(getPageHeader(societa, dataRiferimento, pageNum));
					rowList.add("");
					rowList.add(PAGE_TITLE);
					rowList.add("");
					rowList.add(line);
				}
			}
			
		}
		//Porta la pagina eventualmente incompleta a PAGE_MAX_HEIGHT righe
		for (int i = rowList.size(); i < PAGE_MAX_HEIGHT; i++) {
			rowList.add("");
		}
		//Aggiunge l'ultima pagina, appena completata
		pageList.add(rowList);
		return pageList;
	}
	
	
//	private static List<String> formatDailyTot(Session ses, String formattedDay, List<Fatture> dailyList) {
//		Map<String, Double> totalMap = new HashMap<String, Double>();
//		//Crea la mappa aliquote - totali
//		for (Fatture fattura:dailyList) {
//			List<FattureArticoli> faList = faDao.findByFattura(ses, fattura.getId());
//			for (FattureArticoli fa:faList) {
//				Double tot = totalMap.get(fa.getAliquotaIva());
//				if (tot == null) tot = new Double(0D);
//				tot += fa.getImportoTotUnit()*fa.getQuantita();
//				String codiceIva = ValueUtil.getCodiceIva(fa.getAliquotaIva(), fattura.getTipoIva());
//				totalMap.put(codiceIva, tot);
//			}
//		}
//		List<String> result = new ArrayList<String>();
//		result.add(getDayRowHeader());
//		for (AliquoteIva iva:totalMap.keySet()) {
//			result.add(formatDayRow(formattedDay, iva, totalMap.get(iva)));
//		}
//		return result;
//	}
	
	private static List<String> formatDailyTot(Session ses, String formattedDay, List<Fatture> dailyList) 
			throws BusinessException {
		//Crea le mappe:
		//	tipoIva+idAliquotaIva - tipoIva
		//	tipoIva+idAliquotaIva - AliquoteIva
		//	tipoIva+idAliquotaIva - totale importi
		Map<String, String> tipiIvaMap = new HashMap<String, String>();
		Map<String, AliquoteIva> aliquoteMap = new HashMap<String, AliquoteIva>();
		Map<String, Totali> totaliMap = new HashMap<String, Totali>();
		
		//Popola mappe di AliquoteIva e totali importi
		for (Fatture fattura:dailyList) {
			List<FattureArticoli> faList = faDao.findByFattura(ses, fattura.getId());
			for (FattureArticoli fa:faList) {
				//key
				String key;
				if (fa.getAliquotaIva() != null) {
					String codiceIva = ValueUtil.getCodiceIva(fa.getAliquotaIva(), fattura.getTipoIva());
					key = codiceIva+fa.getAliquotaIva().getId();
				} else {
					key = AppConstants.DEFAULT_ALIQUOTA_IVA+AppConstants.DEFAULT_ALIQUOTA_IVA_ID;
				}
				//tipoIva
				String tipoIva = tipiIvaMap.get(key);
				if (tipoIva == null) tipiIvaMap.put(key, fattura.getTipoIva());
				//AliquoteIva
				AliquoteIva aliquota = aliquoteMap.get(key);
				if (aliquota == null) aliquoteMap.put(key, fa.getAliquotaIva());
				//totale importi
				Totali tot = totaliMap.get(key);
				if (tot == null) tot = new Totali();
				tot.finale += fa.getImportoTotUnit()*fa.getQuantita();
				tot.imponibile += fa.getImportoImpUnit()*fa.getQuantita();
				tot.iva += fa.getImportoIvaUnit()*fa.getQuantita();
				totaliMap.put(key, tot);
			}
		}
		
		List<String> lineList = new ArrayList<String>();
		lineList.add(getDayRowHeader());
		for (String key:totaliMap.keySet()) {
			String tipoIva = tipiIvaMap.get(key);
			Totali totali = totaliMap.get(key);
			AliquoteIva aliquota = aliquoteMap.get(key);
			lineList.add(formatDayRow(formattedDay, aliquota, tipoIva, totali));
		}
		return lineList;
	}
	
	private static String getDayRowHeader() {
		String h="";
		//  123456789012
		h+="        DATA"+
		   " IMPORTO TOT"+
		   "  CODICE IVA"+
		   "  IMPONIBILE"+
		   "         IVA";
		return h;
	}
	private static String formatDayRow(String formattedDay, AliquoteIva iva, String tipoIva,
			Totali totali) throws BusinessException {
		String codiceAlqIva = ValueUtil.getCodiceIva(iva, tipoIva);
		String stringDay = ValueUtil.addLeftPaddingSpaces(formattedDay, 12);
		String stringTotale = ValueUtil.addLeftPaddingSpaces(
				ServerConstants.FORMAT_CURRENCY.format(totali.finale), 12);
		String stringCodice = ValueUtil.addLeftPaddingSpaces(codiceAlqIva, 12);
		String stringImponibile = ValueUtil.addLeftPaddingSpaces(
				ServerConstants.FORMAT_CURRENCY.format(totali.imponibile), 12);
		String stringIva = "";
		if (totali.iva > 0.009) {
			stringIva = ValueUtil.addLeftPaddingSpaces(
					ServerConstants.FORMAT_CURRENCY.format(totali.iva), 12);
		}
		return stringDay+stringTotale+stringCodice+stringImponibile+stringIva;		
	}
	
	private static List<String> formatFattureDetail(String formattedDay, List<String> numFattList) {
		List<String> result = new ArrayList<String>();
		String row = ValueUtil.addLeftPaddingSpaces(formattedDay, 12)+" FATTURE EMESSE: ";
		for (String numFatt:numFattList) {
			String tmpRow = row+" "+numFatt;
			if (tmpRow.length() <= PAGE_MAX_WIDTH) {
				row = tmpRow; 
			} else {
				result.add(row);
				row = "             "+numFatt;//13char+fattura
			}
		}
		result.add(row);
		return result;
	}
	
	
	// ** Summary **
	
	
//	private static List<String> createSummaryPage(Session ses,
//			Date startDt, Date finishDt, Societa societa,
//			Map<String, List<Fatture>> dailyFattMap) {
//		Map<AliquoteIva, Double> totalMap = new HashMap<AliquoteIva, Double>();
//		//Crea la mappa aliquote - totali
//		for (String day:dailyFattMap.keySet()) {
//			for (Fatture fattura:dailyFattMap.get(day)) {
//				List<FattureArticoli> faList = faDao.findByFattura(ses, fattura.getId());
//				for (FattureArticoli fa:faList) {
//					Double tot = totalMap.get(fa.getAliquotaIva());
//					if (tot == null) tot = new Double(0D);
//					tot += fa.getImportoTotUnit();
//					totalMap.put(fa.getAliquotaIva(), tot);
//				}
//			}
//		}
//		List<String> lineList = new ArrayList<String>();
//		int pageNum = new ContatoriDao().nextTempPagRegMensile(ses, societa.getPrefissoFatture(),
//				startDt);
//		lineList.add(getPageHeader(societa, startDt, pageNum));
//		lineList.add("");
//		lineList.add(PAGE_TITLE);
//		lineList.add("");
//		lineList.add("TOTALI DAL "+ServerConstants.FORMAT_DAY.format(startDt)+" AL "+
//				ServerConstants.FORMAT_DAY.format(finishDt));
//		lineList.add(getSummaryRowHeader());
//		for (AliquoteIva iva:totalMap.keySet()) {
//			lineList.add(formatSummaryRow(iva, totalMap.get(iva)));
//		}
//		return lineList;
//	}
	
	private static List<String> createSummaryPage(Session ses,
			Date startDt, Date finishDt, Societa societa,
			List<Fatture> fattureList) throws BusinessException {
		//Crea le mappe:
		//	tipoIva+idAliquotaIva - tipoIva
		//	tipoIva+idAliquotaIva - AliquoteIva
		//	tipoIva+idAliquotaIva - totale importi
		Map<String, String> tipiIvaMap = new HashMap<String, String>();
		Map<String, AliquoteIva> aliquoteMap = new HashMap<String, AliquoteIva>();
		Map<String, Totali> totaliMap = new HashMap<String, Totali>();
		
		//Popola mappe di AliquoteIva e totali importi
		for (Fatture fattura:fattureList) {
			List<FattureArticoli> faList = faDao.findByFattura(ses, fattura.getId());
			for (FattureArticoli fa:faList) {
				//key
				String key;
				if (fa.getAliquotaIva() != null) {
					String codiceIva = ValueUtil.getCodiceIva(fa.getAliquotaIva(), fattura.getTipoIva());
					key = codiceIva+fa.getAliquotaIva().getId();
				} else {
					key = AppConstants.DEFAULT_ALIQUOTA_IVA+AppConstants.DEFAULT_ALIQUOTA_IVA_ID;
				}
				//tipoIva
				String tipoIva = tipiIvaMap.get(key);
				if (tipoIva == null) tipiIvaMap.put(key, fattura.getTipoIva());
				//AliquoteIva
				AliquoteIva aliquota = aliquoteMap.get(key);
				if (aliquota == null) aliquoteMap.put(key, fa.getAliquotaIva());
				//totale importi
				Totali tot = totaliMap.get(key);
				if (tot == null) tot = new Totali();
				tot.finale += fa.getImportoTotUnit()*fa.getQuantita();
				tot.imponibile += fa.getImportoImpUnit()*fa.getQuantita();
				tot.iva += fa.getImportoIvaUnit()*fa.getQuantita();
				totaliMap.put(key, tot);
			}
		}
		
		List<String> lineList = new ArrayList<String>();
		int pageNum = new ContatoriDao().nextTempPagRegMensile(ses,
				societa.getPrefissoFatture(), startDt);
		lineList.add(getPageHeader(societa, startDt, pageNum));
		lineList.add("");
		lineList.add(PAGE_TITLE);
		lineList.add("");
		lineList.add("TOTALI DAL "+ServerConstants.FORMAT_DAY.format(startDt)+" AL "+
				ServerConstants.FORMAT_DAY.format(finishDt));
		lineList.add(getSummaryRowHeader());
		for (String key:totaliMap.keySet()) {
			String tipoIva = tipiIvaMap.get(key);
			Totali totale = totaliMap.get(key);
			AliquoteIva aliquota = aliquoteMap.get(key);
			lineList.add(formatSummaryRow(aliquota, tipoIva, totale));
		}
		return lineList;
	}
	
	private static String getSummaryRowHeader() {
		String h="";
		//  123456789012
		h+="  CODICE IVA"+
		   " IMPORTO TOT"+
		   "  IMPONIBILE"+
		   "         IVA";
		return h;
	}
	private static String formatSummaryRow(AliquoteIva iva, String tipoIva,
			Totali totali) throws BusinessException {
		String codiceAlqIva = ValueUtil.getCodiceIva(iva, tipoIva);
		String stringCodice = ValueUtil.addLeftPaddingSpaces(codiceAlqIva, 12);
		String stringTotale = ValueUtil.addLeftPaddingSpaces(
				ServerConstants.FORMAT_CURRENCY.format(totali.finale), 12);
		String stringImponibile = ValueUtil.addLeftPaddingSpaces(
				ServerConstants.FORMAT_CURRENCY.format(totali.imponibile), 12);
		String stringIva = "";
		if (totali.iva > 0.001) {
			stringIva = ValueUtil.addLeftPaddingSpaces(
					ServerConstants.FORMAT_CURRENCY.format(totali.iva), 12);
		}
		return stringCodice+stringTotale+stringImponibile+stringIva;
	}
	
	public static class Totali {
		public double finale = 0D;
		public double imponibile = 0D;
		public double iva = 0D;
	}
}
