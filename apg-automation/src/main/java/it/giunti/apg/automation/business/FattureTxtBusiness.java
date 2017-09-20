package it.giunti.apg.automation.business;

import it.giunti.apg.automation.AutomationConstants;
import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.persistence.FattureArticoliDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.PagamentiDao;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.IndirizziBusiness;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.AliquoteIva;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.Fatture;
import it.giunti.apg.shared.model.FattureArticoli;
import it.giunti.apg.shared.model.FattureStampe;
import it.giunti.apg.shared.model.Indirizzi;
import it.giunti.apg.shared.model.Pagamenti;
import it.giunti.apg.shared.model.Periodici;
import it.giunti.apg.shared.model.Societa;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FattureTxtBusiness {
	
	static private Logger LOG = LoggerFactory.getLogger(FattureTxtBusiness.class);
	
	private static final String SEP_COR = "|";
	private static final String SEP_CSV = ";";
	private static final String LINEFEED = "\r\n";
	
	public static final SimpleDateFormat FORMAT_DAY_SPESOMETRO = new SimpleDateFormat("dd.MM.yyyy");

	// ** Accompagnamento Pdf **
	
	
	public static File createAccompagnamentoPdfFile(Session ses, List<Fatture> fatList, Societa societa) 
			throws IOException, HibernateException {
		Locale.setDefault(new Locale("it", "IT"));
		//File content
		String fileString = "";
		for (Fatture fattura:fatList) {
			String fileName = "";
			if (fattura.getIdFatturaStampa() != null) {
				FattureStampe stampa = GenericDao.findById(ses, FattureStampe.class, fattura.getIdFatturaStampa());
				fileName = stampa.getFileName();
			}
			fileString += formatAccompagnamentoPdfRow(ses, fattura, societa, fileName)+LINEFEED;
		}
		//File creation
		File f = null;
		f = File.createTempFile("datixarchi", ".frd");
		f.deleteOnExit();
		FileWriter fw = new FileWriter(f);
		fw.write(fileString);
		fw.close();
		return f;
	}
	
	public static String formatAccompagnamentoPdfRow(Session ses, Fatture fattura, Societa societa,
			String fileName) throws HibernateException {
		int segno = 1;
		if (fattura.getIdTipoDocumento().equals(AppConstants.DOCUMENTO_NOTA_CREDITO)) {
			segno = -1;
		}
		Anagrafiche pagante = GenericDao.findById(ses, Anagrafiche.class, fattura.getIdAnagrafica());
		//FattureStampe stampa = GenericDao.findById(ses, FattureStampe.class, fattura.getIdFatturaStampa());
		String ragioneSociale = null;
		Indirizzi ind = pagante.getIndirizzoPrincipale();
		if (IndirizziBusiness.isFilledUp(pagante.getIndirizzoFatturazione())) {
			ind = pagante.getIndirizzoFatturazione();
		}
		ragioneSociale = ind.getCognomeRagioneSociale();
		if (ind.getNome() != null) {
			ragioneSociale += " "+ind.getNome();
		}
		String result = "";
		//Campo 1: DATA FATTURA
		result += ServerConstants.FORMAT_DAY.format(fattura.getDataFattura()) + SEP_COR;
		//Campo 2: CODICE CLIENTE
		result += pagante.getUid() + SEP_COR;
		//Campo 3: RAGIONE SOCIALE
		result += ragioneSociale + SEP_COR;
		//Campo 4: NUMERO FATTURA
		result += fattura.getNumeroFattura() + SEP_COR;
		//Campo 5: NUMERO FATTURA (questo campo è doppio perché il nostro documento contabile ha un numero diverso dal numero commerciale, nel vostro caso il contenuto sarà identico)
		result += fattura.getNumeroFattura() + SEP_COR;
		//Campo 6: vuoto
		result += SEP_COR;
		//Campo 7: vuoto
		result += SEP_COR;
		//Campo 8: vuoto
		result += SEP_COR;
		//Campo 9: vuoto
		result += SEP_COR;
		//Campo 10: vuoto
		result += SEP_COR;
		//Campo 11: valorizzato con il prefisso adottato per la fattura
		result += fattura.getNumeroFattura().substring(0,3)+SEP_COR;
		//Campo 12: valorizzato a 001
		result += societa.getCodiceSocieta()+SEP_COR;
		//Campo 13: PARTITA IVA
		if (pagante.getPartitaIva() != null) {
			result += pagante.getPartitaIva() +SEP_COR;
		} else {
			result += SEP_COR;
		}
		//Campo 14: importo (con due decimali)
		Double totaleFinale = fattura.getTotaleFinale()*segno;
		result += ServerConstants.FORMAT_CURRENCY.format(totaleFinale) + SEP_COR;
		//Campo 15: nome del file fattura
		fileName = (fileName == null ? "" : fileName);
		result += fileName + SEP_COR;
		//Campo 16: CODICE FISCALE
		String codFisc = AutomationConstants.LABEL_NON_DISPONIBILE;
		if (pagante.getCodiceFiscale() != null) {
			if (pagante.getCodiceFiscale().length() > 1) {
				codFisc = pagante.getCodiceFiscale();
			}
		}
		result += codFisc + SEP_COR;
		//Campo 17: valuta
		result += AppConstants.VALUTA + SEP_COR;
		return result;
	}
	
	
	
	// ** File Registro corrispettivi quotidiani **
	
	
	
	public static File createRegCorGiornalieroFile(Session ses,
			List<Fatture> fatList, Periodici p) throws BusinessException, IOException {
		Locale.setDefault(new Locale("it", "IT"));
		//Ottiene gli oggetti FatturaBean dalle istanze abbonamenti
		Map<String, Totali> totMap = new HashMap<String, FattureTxtBusiness.Totali>();

		//Aggregazione per giorno e aliquota
		for (Fatture fattura:fatList) {
			int segno = 1;
			if (fattura.getIdTipoDocumento().equals(AppConstants.DOCUMENTO_NOTA_CREDITO)) segno = -1;
			List<FattureArticoli> faList = new FattureArticoliDao().findByFattura(ses, fattura.getId());
			Totali fTot = new Totali();
			fTot.totaleFinale = 0;
			fTot.totaleImpon = 0;
			for (FattureArticoli fa:faList) {
				AliquoteIva aliquota = fa.getAliquotaIva();
				String aliquotaCodice;
				if (aliquota != null) {
					aliquotaCodice = ValueUtil.getCodiceIva(aliquota, fattura.getTipoIva());
				} else {
					aliquotaCodice = AppConstants.DEFAULT_ALIQUOTA_IVA;
				}
				String key = ServerConstants.FORMAT_DAY_SQL.format(fattura.getDataFattura())+
						aliquotaCodice;
				Totali tot = totMap.get(key);
				if (tot == null) {
					tot = new Totali();
					tot.date = fattura.getDataFattura();
					tot.ivaCodice = aliquotaCodice;
					totMap.put(key, tot);
				}
				tot.totaleFinale += (fa.getImportoTotUnit()*fa.getQuantita()*segno);
				tot.totaleImpon += (fa.getImportoImpUnit()*fa.getQuantita()*segno);
				fTot.totaleFinale += (fa.getImportoTotUnit()*fa.getQuantita()*segno);
				fTot.totaleImpon += (fa.getImportoImpUnit()*fa.getQuantita()*segno);
			}
			//verifica totali
			if ((Math.abs(Math.abs(fattura.getTotaleFinale())-Math.abs(fTot.totaleFinale)) >= 0.01) ||
					(Math.abs(Math.abs(fattura.getTotaleImponibile())-Math.abs(fTot.totaleImpon)) >= 0.01)){
				LOG.error(fattura.getNumeroFattura()+" anomalia. Articoli: imp="+fTot.totaleImpon+" tot="+fTot.totaleFinale+
						" fattura: imp="+fattura.getTotaleImponibile()+" tot="+fattura.getTotaleFinale());
			}
		}
		
		//File content
		String fileString = "";
		ArrayList<String> keyList = new ArrayList<String>();
		keyList.addAll(totMap.keySet());
		Object[] keys =  keyList.toArray();
		Arrays.sort(keys);
		for (Object key:keys) {
			Totali tot = totMap.get((String)key);
			fileString += formatRegCorQuotidianoRow(tot)+LINEFEED;
		}
		//File creation
		File f = null;
		f = File.createTempFile("registrazioni", ".txt");
		f.deleteOnExit();
		FileWriter fw = new FileWriter(f);
		fw.write(fileString);
		fw.close();
		return f;
	}
	
	private static String formatRegCorQuotidianoRow(Totali tot) {
		String result = "";
		//Campo 1: DATA FATTURA
		result += ServerConstants.FORMAT_DAY.format(tot.date)+SEP_CSV;
		//Campo 2: totaleFinale
		result += ServerConstants.FORMAT_CURRENCY.format(tot.totaleFinale)+SEP_CSV;
		//Campo 3: IVA
		result += tot.ivaCodice+SEP_CSV;
		//Campo 4: totaleImpon
		result += ServerConstants.FORMAT_CURRENCY.format(tot.totaleImpon)+SEP_CSV;
		//Campo 5: totaleIva
		result += ServerConstants.FORMAT_CURRENCY.format(tot.totaleFinale-tot.totaleImpon);
		return result;
	}
	
	
	
	// ** File spesometro **
	
	
	
	public static File createSpesometroFile(Session ses, List<Fatture> fatList, Societa societa)
			throws BusinessException, IOException, HibernateException {
		Locale.setDefault(new Locale("it", "IT"));
		//File content
		String fileString = "";
		int count = 0;
		for (Fatture fattura:fatList) {
			fileString += formatSpesometroRows(ses, fattura, societa);//LINEFEED added by method
			count++;
			if (count%250 == 0) LOG.debug("Fatture elaborate: "+count);
		}
		//File creation
		File f = null;
		f = File.createTempFile("spesometro", ".txt");
		f.deleteOnExit();
		FileWriter fw = new FileWriter(f);
		fw.write(fileString);
		fw.close();
		return f;
	}
	
	//private static String formatSpesometroRows(Session ses, Fatture fattura, Societa societa) 
	//		throws HibernateException {
	//	String fileName = null;
	//	int segno = 1;
	//	if (fattura.getIdTipoDocumento().equals(AppConstants.DOCUMENTO_NOTA_CREDITO)) {
	//		segno = -1;
	//	}
	//	Anagrafiche pagante = GenericDao.findById(ses, Anagrafiche.class, fattura.getIdAnagrafica());
	//	//FattureStampe stampa = GenericDao.findById(ses, FattureStampe.class, fattura.getIdFatturaStampa());
	//	String ragioneSociale = null;
	//	Indirizzi ind = pagante.getIndirizzoPrincipale();
	//	if (IndirizziBusiness.isFilledUp(pagante.getIndirizzoFatturazione())) {
	//		ind = pagante.getIndirizzoFatturazione();
	//	}
	//	ragioneSociale = ind.getCognomeRagioneSociale();
	//	if (ind.getNome() != null) {
	//		ragioneSociale += " "+ind.getNome();
	//	}
	//	String tipoIva = ValueUtil.getTipoIva(
	//			ind.getNazione(),
	//			(pagante.getIdTipoAnagrafica() != AppConstants.ANAG_PRIVATO) );//isAzienda
	//	String rowPrefix = "";
	//	//Campo 1: DATA FATTURA
	//	rowPrefix += ServerConstants.FORMAT_DAY.format(fattura.getDataFattura()) + SEP_COR;
	//	//Campo 2: CODICE CLIENTE
	//	rowPrefix += pagante.getUid() + SEP_COR;
	//	//Campo 3: RAGIONE SOCIALE
	//	rowPrefix += ragioneSociale + SEP_COR;
	//	//Campo 4: NUMERO FATTURA
	//	rowPrefix += fattura.getNumeroFattura() + SEP_COR;
	//	//Campo 5: NUMERO FATTURA (questo campo è doppio perché il nostro documento contabile ha un numero diverso dal numero commerciale, nel vostro caso il contenuto sarà identico)
	//	rowPrefix += fattura.getNumeroFattura() + SEP_COR;
	//	//Campo 6: vuoto
	//	rowPrefix += SEP_COR;
	//	//Campo 7: vuoto
	//	rowPrefix += SEP_COR;
	//	//Campo 8: vuoto
	//	rowPrefix += SEP_COR;
	//	//Campo 9: vuoto
	//	rowPrefix += SEP_COR;
	//	//Campo 10: vuoto
	//	rowPrefix += SEP_COR;
	//	//Campo 11: valorizzato con il prefisso adottato per la fattura
	//	rowPrefix += fattura.getNumeroFattura().substring(0,3)+SEP_COR;
	//	//Campo 12: valorizzato a 001
	//	rowPrefix += societa.getCodiceSocieta()+SEP_COR;
	//	//Campo 13: PARTITA IVA
	//	if (pagante.getPartitaIva() != null) {
	//		rowPrefix += pagante.getPartitaIva() +SEP_COR;
	//	} else {
	//		rowPrefix += SEP_COR;
	//	}
	//	//Campo 14: importo (con due decimali)
	//	Double totaleFinale = fattura.getTotaleFinale()*segno;
	//	rowPrefix += ServerConstants.FORMAT_CURRENCY.format(totaleFinale) + SEP_COR;
	//	//Campo 15: nome del file fattura
	//	fileName = (fileName == null ? "" : fileName);
	//	rowPrefix += fileName + SEP_COR;
	//	//Campo 16: CODICE FISCALE
	//	String codFisc = AutomationConstants.LABEL_NON_DISPONIBILE;
	//	if (pagante.getCodiceFiscale() != null) {
	//		if (pagante.getCodiceFiscale().length() > 1) {
	//			codFisc = pagante.getCodiceFiscale();
	//		}
	//	}
	//	rowPrefix += codFisc + SEP_COR;
	//	//Campo 17: valuta
	//	rowPrefix += AppConstants.VALUTA + SEP_COR;
	//	
	//	List<FattureArticoli> faList = new FattureArticoliDao().findByFattura(ses, fattura.getId());
	//	//Raggruppa gli importi per aliquota
	//	Map<AliquoteIva, Double> aliquoteImpMap = new HashMap<AliquoteIva, Double>();
	//	Map<AliquoteIva, Double> aliquoteIvaMap = new HashMap<AliquoteIva, Double>();
	//	for (FattureArticoli fa:faList) {
	//		//Imponibile
	//		Double imp = aliquoteImpMap.get(fa.getAliquotaIva());
	//		if (imp == null) imp = 0D;
	//		imp += fa.getImportoImpUnit();
	//		aliquoteImpMap.put(fa.getAliquotaIva(), imp);
	//		//Iva
	//		Double iva = aliquoteIvaMap.get(fa.getAliquotaIva());
	//		if (iva == null) iva = 0D;
	//		iva += fa.getImportoIvaUnit();
	//		aliquoteIvaMap.put(fa.getAliquotaIva(), iva);
	//	}
	//	String result = "";
	//	segno = 1;
	//	if (fattura.getIdTipoDocumento().equals(AppConstants.DOCUMENTO_NOTA_CREDITO)) segno = -1;
	//	for (AliquoteIva ai:aliquoteImpMap.keySet()) {
	//		result += rowPrefix + SEP_COR;
	//		//Campo 18: imponibile
	//		result += ServerConstants.FORMAT_CURRENCY.format(segno*aliquoteImpMap.get(ai)) + SEP_COR;
	//		//Campo 19: iva
	//		result += ServerConstants.FORMAT_CURRENCY.format(segno*aliquoteIvaMap.get(ai)) + SEP_COR;
	//		//Campo 20: codice iva
	//		String codiceIva = ValueUtil.getCodiceIva(ai, tipoIva);
	//		result += codiceIva + SEP_COR;
	//		//Campo 21: indirizzo
	//		result += ind.getIndirizzo() + SEP_COR;
	//		//Campo 22: localita
	//		if (ind.getNazione().getId().equals(AppConstants.DEFAULT_ID_NAZIONE_ITALIA)) {
	//			result += ind.getLocalita();
	//		} else {
	//			result += ind.getLocalita() + " - " +
	//					ind.getNazione().getNomeNazione();
	//		}
	//		result += LINEFEED;
	//	}
	//	return result;
	//}
	
	private static String formatSpesometroRows(Session ses, Fatture fattura, Societa societa) 
			throws BusinessException, HibernateException {
		int segno = -1;//Fatture -1 note di credito +1 (richiesto da alinari 05/07/2017)
		if (fattura.getIdTipoDocumento().equals(AppConstants.DOCUMENTO_NOTA_CREDITO)) {
			segno = 1;
		}
		Anagrafiche pagante = GenericDao.findById(ses, Anagrafiche.class, fattura.getIdAnagrafica());
		//FattureStampe stampa = GenericDao.findById(ses, FattureStampe.class, fattura.getIdFatturaStampa());
		String ragioneSociale = null;
		Indirizzi ind = pagante.getIndirizzoPrincipale();
		if (IndirizziBusiness.isFilledUp(pagante.getIndirizzoFatturazione())) {
			ind = pagante.getIndirizzoFatturazione();
		}
		ragioneSociale = ind.getCognomeRagioneSociale();
		if (ind.getNome() != null) {
			ragioneSociale += " "+ind.getNome();
		}
		String tipoIva = ValueUtil.getTipoIva(
				ind.getNazione(),
				(pagante.getIdTipoAnagrafica() != AppConstants.ANAG_PRIVATO) );//isAzienda
		List<FattureArticoli> faList = new FattureArticoliDao().findByFattura(ses, fattura.getId());
		//Raggruppa gli importi per aliquota
		Map<AliquoteIva, Double> aliquoteImpMap = new HashMap<AliquoteIva, Double>();
		Map<AliquoteIva, Double> aliquoteIvaMap = new HashMap<AliquoteIva, Double>();
		for (FattureArticoli fa:faList) {
			AliquoteIva ai = fa.getAliquotaIva();
			//Se l'aliquota non è popolata, è presa come aliquota default "iva assolta"
			if (ai == null) ai = GenericDao.findById(ses, AliquoteIva.class, AppConstants.DEFAULT_ALIQUOTA_IVA_ID);
			//Imponibile
			Double imp = aliquoteImpMap.get(ai);
			if (imp == null) imp = 0D;
			imp += fa.getImportoImpUnit();
			aliquoteImpMap.put(ai, imp);
			//Iva
			Double iva = aliquoteIvaMap.get(ai);
			if (iva == null) iva = 0D;
			iva += fa.getImportoIvaUnit();
			aliquoteIvaMap.put(ai, iva);
		}
		String result = "";
		for (AliquoteIva ai:aliquoteImpMap.keySet()) {
			//1: soc
			result += societa.getCodiceSocieta()+SEP_COR;
			//2: anno
			result += ServerConstants.FORMAT_YEAR.format(fattura.getDataFattura()) + SEP_COR;
			//3: numero fattura
			result += fattura.getNumeroFattura() + SEP_COR;
			//4: codice iva
			String codiceIva = ValueUtil.getCodiceIva(ai, tipoIva);
			result += codiceIva + SEP_COR;
			//5: primi 2 caratteri numero fattura
			result += fattura.getNumeroFattura().substring(0,2) + SEP_COR;
			//6: data reg.
			result += FORMAT_DAY_SPESOMETRO.format(fattura.getDataFattura()) + SEP_COR;
			//7: data doc.
			result += FORMAT_DAY_SPESOMETRO.format(fattura.getDataFattura()) + SEP_COR;
			//8: riferimento
			result += fattura.getNumeroFattura() + SEP_COR;
			//9: rif. fatt.
			result += SEP_COR;
			//10: esercizio
			result += SEP_COR;
			//11: cliente
			result += pagante.getUid() + SEP_COR;
			//12: t.tco
			result += "D" + SEP_COR;
			//13: cod fisc
			String codFisc = AutomationConstants.LABEL_NON_DISPONIBILE;
			if (pagante.getCodiceFiscale() != null) {
				if (pagante.getCodiceFiscale().length() > 1) {
					codFisc = pagante.getCodiceFiscale();
				}
			}
			result += codFisc + SEP_COR;
			//14: p.iva
			String pIva = "";
			if (pagante.getPartitaIva() != null) {
				if (pagante.getPartitaIva().length() > 1) {
					pIva = pagante.getPartitaIva();
				}
			}
			result += pIva + SEP_COR;
			//15: paese
			result += ind.getNazione().getSiglaNazione() + SEP_COR;
			//16: nome 1
			result += ragioneSociale + SEP_COR;
			//17: nome 2
			result += SEP_COR;
			//18: localita
			String localita = "";
			if (ind.getNazione().getId().equals(AppConstants.DEFAULT_ID_NAZIONE_ITALIA)) {
				localita += ind.getLocalita();
			} else {
				localita += ind.getLocalita() + " - " +
						ind.getNazione().getNomeNazione();
			}
			result += localita + SEP_COR;
			//19: indirizzo
			result += ind.getIndirizzo() + SEP_COR;
			//20: op. ?
			result += "MWS" + SEP_COR;
			//21: base DI
			result += ServerConstants.FORMAT_CURRENCY.format(segno*aliquoteImpMap.get(ai)) + SEP_COR;
			//22: imp in DI
			result += ServerConstants.FORMAT_CURRENCY.format(segno*aliquoteIvaMap.get(ai)) + SEP_COR;
			//23: tipo trans
			result += "1" + SEP_COR;
			//24: pers. fis (vale X se è presente cod fisc)
			String persFis = "";
			if (pagante.getCodiceFiscale() != null) {
				if (pagante.getCodiceFiscale().length() > 1) {
					persFis = "X";
				}
			}
			result += persFis + SEP_COR;
			//25: self inv
			result += SEP_COR;
			//26: rev chrg
			result += SEP_COR;
			//27: sezione
			String sezione = "FE";
			if (fattura.getIdTipoDocumento().equals(AppConstants.DOCUMENTO_NOTA_CREDITO)) sezione = "NE";
			result += sezione + LINEFEED;
			
		}
		return result;
	}
	
	
	
	
	//** File carta docente **
	
	
	
	public static File createCartaDocenteFile(Session ses, List<Fatture> fatList, Societa societa)
			throws IOException, HibernateException, EmptyResultException {
		PagamentiDao pDao = new PagamentiDao();
		Locale.setDefault(new Locale("it", "IT"));
		//File content
		String fileString = "";
		int i = 0;
		int count = 0;
		for (Fatture fattura:fatList) {
			i++;
			List<Pagamenti> pList = pDao.findPagamentiByIdFattura(ses, fattura.getId());
			if (pList.size() > 0) {
				Pagamenti pagamento = pList.get(0);
				if (pagamento.getIdTipoPagamento().equals(AppConstants.PAGAMENTO_CARTA_DOCENTE)) {
					count++;
					String uidPeriodico = "";
					if (pagamento.getCodiceAbbonamentoMatch() != null) {
						if (pagamento.getCodiceAbbonamentoMatch().length() > 0) {
							uidPeriodico = pagamento.getCodiceAbbonamentoMatch().substring(0, 1);
						}
					}
					String trn = "";
					if (pagamento.getTrn() != null) trn = pagamento.getTrn();
					fileString += formatCartaDocenteRows(ses, fattura, societa, uidPeriodico, trn);//LINEFEED added by method
				}
				if (i%250 == 0) LOG.debug("Fatture elaborate: "+i);
			}
		}
		if (count > 0) {
			//File creation
			File f = null;
			f = File.createTempFile("cartadocente", ".csv");
			f.deleteOnExit();
			FileWriter fw = new FileWriter(f);
			fw.write(fileString);
			fw.close();
			return f;
		} else {
			throw new EmptyResultException("Nessuna fattura da carta docente");
		}
	}
	
	private static String formatCartaDocenteRows(Session ses, Fatture fattura, Societa societa,
			String uidPeriodico, String trn) throws HibernateException {
		Anagrafiche pagante = GenericDao.findById(ses, Anagrafiche.class, fattura.getIdAnagrafica());
		FattureStampe stampa = GenericDao.findById(ses, FattureStampe.class, fattura.getIdFatturaStampa());
		int segno = 1;
		if (fattura.getIdTipoDocumento().equals(AppConstants.DOCUMENTO_NOTA_CREDITO)) segno = -1;
		String ragioneSociale = null;
		Indirizzi ind = pagante.getIndirizzoPrincipale();
		if (IndirizziBusiness.isFilledUp(pagante.getIndirizzoFatturazione())) {
			ind = pagante.getIndirizzoFatturazione();
		}
		ragioneSociale = ind.getCognomeRagioneSociale();
		if (ind.getNome() != null) {
			ragioneSociale += " "+ind.getNome();
		}
		String result = "";
		//Campo 1: DATA FATTURA
		result += ServerConstants.FORMAT_DAY.format(fattura.getDataFattura())+SEP_CSV;
		//Campo 2: CODICE CLIENTE
		result += pagante.getUid()+SEP_CSV;
		//Campo 3: RAGIONE SOCIALE
		result += ragioneSociale;
		result += SEP_CSV;
		//Campo 4: NUMERO FATTURA
		result += fattura.getNumeroFattura() + SEP_CSV;
		//Campo 5: valorizzato con il prefisso adottato per la fattura
		result += fattura.getNumeroFattura().substring(0,3)+SEP_CSV;
		//Campo 6: valorizzato a 001
		result += societa.getCodiceSocieta() +SEP_CSV;
		//Campo 7: PARTITA IVA
		if (pagante.getPartitaIva() != null) {
			result += pagante.getPartitaIva() +SEP_CSV;
		} else {
			result += SEP_CSV;
		}
		//Campo 8: importo (con due decimali)
		Double totaleFinale = fattura.getTotaleFinale()*segno;
		result += ServerConstants.FORMAT_CURRENCY.format(totaleFinale) + SEP_CSV;
		//Campo 9: nome del file fattura
		String filename = ( (stampa.getFileName() != null) ? stampa.getFileName() : "");
		result += filename + SEP_CSV;
		//Campo 10: CODICE FISCALE
		String codFisc = AutomationConstants.LABEL_NON_DISPONIBILE;
		if (pagante.getCodiceFiscale() != null) {
			if (pagante.getCodiceFiscale().length() > 1) {
				codFisc = pagante.getCodiceFiscale();
			}
		}
		result += codFisc + SEP_CSV;
		//Campo 11: valuta
		result += AppConstants.VALUTA + SEP_CSV;
		//Campo 12: periodico
		result += uidPeriodico + SEP_CSV;
		//Campo 13: trn (numero voucher)
		result += trn;
		result += LINEFEED;
		return result;
	}
	
	
	
	
	// INNER CLASSES
	
	
	public static class Totali {
		Date date = null;
		String ivaCodice = null;
		double totaleImpon = 0D;
		double totaleFinale = 0D;
	}
}
