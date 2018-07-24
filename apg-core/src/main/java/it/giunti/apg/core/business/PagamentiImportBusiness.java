package it.giunti.apg.core.business;

import it.giunti.apg.core.VisualLogger;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.core.persistence.PagamentiDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Pagamenti;
import it.giunti.apg.shared.model.Periodici;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.input.BOMInputStream;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class PagamentiImportBusiness {

	//private static final Logger LOG = LoggerFactory.getLogger(PagamentiImportBusiness.class);
	private SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");
	private DecimalFormat DF = new DecimalFormat("#0.00");
	
	private PagamentiDao pagDao = new PagamentiDao();
	
	private static Map<Integer, Periodici> ccMap = null;

	public void importPagamenti(File inputFile, int idRapporto, String idUtente) 
			throws FileNotFoundException, BusinessException {
		
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			//Fase 0: rimozione caratteri iniziali errati
			File cleanFile = copyWithoutLeadingSpaces(inputFile, idRapporto);
			//Fase 1: da file a db
			InputStream is = new BOMInputStream(new FileInputStream(cleanFile));
			List<Pagamenti> persistedPagaList = parseStreamAndPersistPagamenti(ses, is, idUtente, idRapporto);
			//Fase 2: scarta i duplicati
			List<Pagamenti> pagaList = removeDuplicatePayments(ses, persistedPagaList, idRapporto);
			//Fase 3: abbinamento nuovi pagamenti con istanze
			pagaList = PagamentiMatchBusiness.matchBollettiniToIstanze(ses, pagaList, idUtente, idRapporto);
			trn.commit();
		} catch (Exception e) {
			trn.rollback();
			VisualLogger.get().addHtmlErrorLine(idRapporto, "ERRORE "+e.getClass().getSimpleName()+": "+e.getMessage(), e);
		} finally {
			ses.close();
		}
		VisualLogger.get().closeAndSaveRapporto(idRapporto);
	}

	private File copyWithoutLeadingSpaces(File inputFile, int idRapporto)
			throws IOException {
		InputStream is = new BOMInputStream(new FileInputStream(inputFile));
		File outputFile = File.createTempFile("inputPagamentiClean", ".txt");
		outputFile.deleteOnExit();
		FileOutputStream fos = new FileOutputStream(outputFile);
		boolean trailingCharacter = true;
		byte[] buffer = new byte[1];
		int counter = 0;
		while (trailingCharacter) {
			is.read(buffer);
			String c = new String(buffer, Charset.forName(AppConstants.CHARSET_UTF8));
			if (c.equals(" ") || c.equals("\r") || c.equals("\n")) {
				trailingCharacter = true;
				counter++;
			} else {
				trailingCharacter = false;
			}
		}
		fos.write(buffer);
		byte[] longBuffer = new byte[2048];
		int len;
		while ((len = is.read(longBuffer)) > 0){
			fos.write(longBuffer, 0, len);
		}
		is.close();
		fos.close();
		if (counter > 0) VisualLogger.get().addHtmlInfoLine(idRapporto, "Rimossi "+counter+" caratteri errati in testa al file");
		return outputFile;
	}
	
	/**
	 * Salva i pagamenti corretti su DB e scrive riga per riga i log nella lista listLog
	 * @param is
	 * @param utente
	 * @return
	 */
	private List<Pagamenti> parseStreamAndPersistPagamenti(Session ses, InputStream is,
			String idUtente, int idRapporto)
			throws NumberFormatException, BusinessException, IOException {
		List<Pagamenti> persPagaList = new ArrayList<Pagamenti>();
		int count = 0;
		//Acquisizione dei byte array
		VisualLogger.get().addHtmlInfoLine(idRapporto, "<b>Fase 1</b>: acquisizione pagamenti da file");
		while (is.available()>0) {
			PagamentiImportBean pib= new PagamentiImportBean();
			
			//stringaBollettinoBuffer è la stringa da cui si verifica l'unicità
			byte[] stringaBollettinoBuffer = new byte[17];//char 00-17 identificativo bollettino
			is.read(stringaBollettinoBuffer);
			byte[] ccBuffer = new byte[10];//char 18-27 conto corrente
			is.read(ccBuffer);
			byte[] dataVersamentoBuffer = new byte[6];//char 28-33 data versamento
			is.read(dataVersamentoBuffer);
			byte[] tipoBuffer = new byte[3];//char 34-36 tipo bollettino
			is.read(tipoBuffer);
			is.skip(4);//37-40 skip
			byte[] importoBuffer = new byte[6];//char 41-46 importo
			is.read(importoBuffer);
			is.skip(8);//47-55 skip
			byte[] divisaBuffer = new byte[1];//char 55 divisa (euro=2)
			is.read(divisaBuffer);
			byte[] dataAccreditoBuffer = new byte[6];//char 56-61 data accredito NON USATA
			is.read(dataAccreditoBuffer);//NON E' USATA
			is.skip(2);//62-63 skip
			byte[] codiceBuffer = new byte[6];//char 64-69 codice abbonamento (solo numero)
			is.read(codiceBuffer);
			byte[] ccVCampoBuffer = new byte[8];//char 70-77 conto corrente da V campo (inutile)
			is.read(ccVCampoBuffer);
			is.skip(125);
			
			pib.setStringaBollettino(ValueUtil.btos(stringaBollettinoBuffer));
			pib.setTipo(ValueUtil.btos(tipoBuffer));
			pib.setDivisa(ValueUtil.btos(divisaBuffer));
			Integer cc = -1;
			try {
				cc = Integer.parseInt(ValueUtil.btos(ccBuffer));
			} catch (NumberFormatException e) {}
			pib.setCc(cc);
			String importoString = ValueUtil.btos(importoBuffer);
			pib.setImporto(toImporto(importoString));
			String dataString = ValueUtil.btos(dataVersamentoBuffer);
			Date dataVersamento = toDate(dataString);
			pib.setDataVersamento(dataVersamento);
			dataString = ValueUtil.btos(dataAccreditoBuffer);
			Date dataAccredito = toDate(dataString);
			pib.setDataAccredito(dataAccredito);
			String codiceString = ValueUtil.btos(codiceBuffer);
			pib.setCodiceNumAbbonamento(codiceString);
			
			//E' dei tipi accettati 896 e 674?
			//Controlla che sia nella valuta corretta euro=2
			if ((pib.getTipo().equals("896") || pib.getTipo().equals("674"))
					&& pib.getDivisa().equals("2")) {
				Pagamenti persPagamento = null;
					try {
						Pagamenti transPagamento = bollettinoBeanToPagamenti(ses, pib, idUtente);
						//Inizialmente tutti i pagamenti sono importati come errati
						//L'errore viene rimosso solo al momento di un abbinamento corretto:
						transPagamento.setIdErrore(AppConstants.PAGAMENTO_ERR_INESISTENTE);
						Integer id = (Integer) pagDao.save(ses, transPagamento);
						persPagamento = GenericDao.findById(ses, Pagamenti.class, id);
						//Il NUOVO pagamento è stato inserito ed è persPagamento
						String logLine = "Acquisito:" +
								" <b>EUR "+DF.format(pib.getImporto())+"</b>"+
								" "+SDF.format(pib.getDataVersamento())+
								" <b>"+persPagamento.getCodiceAbbonamentoBollettino()+"</b>";
								//" boll."+p.getStringaBollettino();
						VisualLogger.get().addHtmlInfoLine(idRapporto, logLine);
						count++;
					} catch (HibernateException e) {
						throw new BusinessException(e.getMessage(), e);
					} catch (ValidationException e) {
						throw new BusinessException(e.getMessage(), e);
					}
				if (persPagamento != null) {
					persPagaList.add(persPagamento);
				}
			}
		}
		VisualLogger.get().addHtmlInfoLine(idRapporto, "<b>Fase 1</b>: acquisizione terminata: acquisiti "+count+" pagamenti");
		return persPagaList;
	}
	
	private List<Pagamenti> removeDuplicatePayments(Session ses, List<Pagamenti> pagaList, int idRapporto) throws BusinessException {
		List<Pagamenti> result = new ArrayList<Pagamenti>();
		int i = 0;
		int count = 0;
		VisualLogger.get().addHtmlInfoLine(idRapporto, "<b>Fase 2</b>: filtraggio pagamenti duplicati");
		for (Pagamenti p:pagaList) {
			boolean dup = isDuplicate(ses, p);
			if (dup) {
				new PagamentiDao().delete(ses, p);
				count++;
				if ((count % 10) == 0) VisualLogger.get().addHtmlInfoLine(idRapporto, count+" pagamenti duplicati");
			} else {
				result.add(p);
			}
			i++;
			if ((i % 50) == 0) VisualLogger.get().addHtmlInfoLine(idRapporto, i+"/"+pagaList.size()+" pagamenti");
		}
		VisualLogger.get().addHtmlInfoLine(idRapporto, i+"/"+pagaList.size()+" pagamenti");
		VisualLogger.get().addHtmlInfoLine(idRapporto, "<b>Fase 2</b>: filtraggio terminato: rimossi "+count+" pagamenti");
		return result;
	}
	
	
	
	
	
	
	
	/**
	 * Scrive il pagamento su DB a condizione che il CC corrisponda ad un periodico
	 * e che non esista un altro pagamento con stessa stringaBollettino, data e cod.abbonamnento
	 * @param ses
	 * @param pib
	 * @param utente
	 * @return
	 * @throws ValidationException
	 */
	private Pagamenti bollettinoBeanToPagamenti(Session ses, PagamentiImportBean pib, String idUtente)
			throws ValidationException, BusinessException {
		if (ccMap == null) {
			loadCcMap(ses);
		}
		Periodici periodico = ccMap.get(pib.getCc());
		if (periodico == null) {
			String errorMsg = "Non esiste un periodico con CC "+pib.getCc();
			if (pib.getCodiceNumAbbonamento() != null) {
				errorMsg += " (codice abb. "+pib.getCodiceNumAbbonamento()+" ";
				if (pib.getImporto() != null) errorMsg += "importo EUR "+DF.format(pib.getImporto())+" ";
				errorMsg += ")";
			}
			throw new ValidationException(errorMsg);
		}
		Pagamenti pagamento = new Pagamenti();
		pagamento.setDataPagamento(pib.getDataVersamento());
		pagamento.setDataAccredito(pib.getDataAccredito());
		pagamento.setIdErrore(null);
		pagamento.setIdTipoPagamento(AppConstants.PAGAMENTO_BOLLETTINO);
		pagamento.setImporto(pib.getImporto());
		pagamento.setStringaBollettino(pib.getStringaBollettino());
		String codiceAbbonamento = periodico.getUid()+pib.getCodiceNumAbbonamento();
		pagamento.setCodiceAbbonamentoBollettino(codiceAbbonamento);
		pagamento.setCodiceAbbonamentoMatch(codiceAbbonamento);
		pagamento.setDataCreazione(DateUtil.now());
		pagamento.setDataModifica(DateUtil.now());
		pagamento.setNote("bollettino "+pib.getTipo());
		pagamento.setIdUtente(idUtente);
		pagamento.setIdFattura(null);
		pagamento.setIdSocieta(periodico.getIdSocieta());
		//Tentativo di abbinamento anagrafica
		IstanzeAbbonamenti ia = new IstanzeAbbonamentiDao()
			.findUltimaIstanzaByCodice(ses, codiceAbbonamento);
		if (ia != null) {
			if (ia.getPagante() != null) {
				pagamento.setAnagrafica(ia.getPagante());
			} else {
				pagamento.setAnagrafica(ia.getAbbonato());
			}
		}
		return pagamento;
	}
	
	private boolean isDuplicate(Session ses, Pagamenti persistedPagamento) throws BusinessException {
		List<Pagamenti> pagamentiEsistentiList;
		try {
			pagamentiEsistentiList = pagDao.findPagamentiByDateImportoCodiceTipo(ses,
					persistedPagamento.getDataPagamento(),
					persistedPagamento.getImporto(),
					persistedPagamento.getCodiceAbbonamentoBollettino(),
					persistedPagamento.getIdTipoPagamento());
		} catch (HibernateException e) {
			throw new BusinessException(e.getMessage(), e);
		}
		if (pagamentiEsistentiList != null) {
			if (pagamentiEsistentiList.size() > 1) {// se > 1 è duplicato
				return true;
			}
		}
		return false;
	}
	
	
	

	
	/** Carica la mappa tra conti correnti e periodici. Ma, nel caso in cui esistano
	 * erroneamente due riviste con CC identico, assoccia il CC a quella con anzianità maggiore
	 * @param ses
	 */
	private void loadCcMap(Session ses) throws BusinessException {
		ccMap = new HashMap<Integer, Periodici>();
		List<Periodici> periodiciList;
		try {
			periodiciList = GenericDao.findByClass(ses, Periodici.class, "numeroCc");
		} catch (HibernateException e) {
			throw new BusinessException(e.getMessage(), e);
		}
		for (Periodici periodico:periodiciList) {
			Integer cc = Integer.parseInt(periodico.getNumeroCc());
			Periodici periodicoInMap = ccMap.get(cc);
			if (periodicoInMap != null) {
				if (periodicoInMap.getDataInizio().after(periodico.getDataInizio())) {
					//Se il periodicoInMap è iniziato dopo periodico allora lo sovrascrive
					try {
						ccMap.put(cc, periodico);
					} catch (NumberFormatException e) { }
				}
			} else {
				try {
					ccMap.put(cc, periodico);
				} catch (NumberFormatException e) { }
			}
		}
	}
	
	private Date toDate(String s) {
		if (s==null) return null;
		if (s.equals("")) return null;
		Integer yy = Integer.parseInt("20"+s.substring(0, 2));
		Integer mm = Integer.parseInt(s.substring(2,4))-1;//Sottrae 1 perché i mesi partono da 0
		Integer dd = Integer.parseInt(s.substring(4,6));
		Calendar gc = new GregorianCalendar(yy.intValue(),mm.intValue(),dd.intValue());
		return gc.getTime();
	}
	
	private Double toImporto(String s) throws NumberFormatException {
		if (s==null) return null;
		if (s.equals("")) return null;
		Double d =  new Double(0);
		d = new Double (Integer.parseInt(s));
		d = d/100;
		return d;		
	}
	
	
	
	//Inner classes

	
	
	public class PagamentiImportBean {
		private Integer id;
		private String stringaBollettino;
		private String tipo;
		private Double importo;
		private String divisa;
		private Date dataVersamento;
		private Date dataAccredito;
		private String codiceNumAbbonamento;
		private Integer cc;
		private Short errore;
		private String operatore;
		private Date dataModifica;
		
		public Integer getCc() {
			return cc;
		}
		public void setCc(Integer cc) {
			this.cc = cc;
		}
		public String getCodiceNumAbbonamento() {
			return codiceNumAbbonamento;
		}
		public void setCodiceNumAbbonamento(String codiceNum) {
			this.codiceNumAbbonamento = codiceNum;
		}
		public Date getDataVersamento() {
			return dataVersamento;
		}
		public void setDataVersamento(Date dataVersamento) {
			this.dataVersamento = dataVersamento;
		}
		public Date getDataAccredito() {
			return dataAccredito;
		}
		public void setDataAccredito(Date dataAccredito) {
			this.dataAccredito = dataAccredito;
		}
		public String getDivisa() {
			return divisa;
		}
		public void setDivisa(String divisa) {
			this.divisa = divisa;
		}
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public Double getImporto() {
			return importo;
		}
		public void setImporto(Double importo) {
			this.importo = importo;
		}
		public String getTipo() {
			return tipo;
		}
		public void setTipo(String tipo) {
			this.tipo = tipo;
		}
		public Date getDataModifica() {
			return dataModifica;
		}
		public void setDataModifica(Date dataModifica) {
			this.dataModifica = dataModifica;
		}
		public String getOperatore() {
			return operatore;
		}
		public void setOperatore(String operatore) {
			this.operatore = operatore;
		}
		public Short getErrore() {
			return errore;
		}
		public void setErrore(Short errore) {
			this.errore = errore;
		}
		public String getStringaBollettino() {
			return stringaBollettino;
		}
		public void setStringaBollettino(String stringaBollettino) {
			this.stringaBollettino = stringaBollettino;
		}
	}
}
