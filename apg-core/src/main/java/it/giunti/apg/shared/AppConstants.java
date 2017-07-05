package it.giunti.apg.shared;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class AppConstants {

	public static final String CHARSET = "UTF-8";//sostituito "ISO-8859-15";
	
	public static final String STRING_SEPARATOR = ";";
	public static final String PATTERN_DATETIME = "dd/MM/yyyy HH:mm";
	public static final String PATTERN_DAY = "dd/MM/yyyy";
	public static final String PATTERN_DAY_SQL = "yyyy-MM-dd";
	public static final String PATTERN_MONTH = "MM/yyyy";
	public static final String PATTERN_TIME = "HH:mm";
	public static final String PATTERN_INTEGER = "#0";
	public static final String PATTERN_CURRENCY = "#0.00";
	public static final int ROUND_DECIMALS = 2;
	public static final long HOUR = 3600000L;
	public static final long DAY = HOUR*24;
	public static final long MONTH = DAY*30; //millisecondi in 30 giorni 1000 * 60 * 60 * 24 * 30;
	public static final long YEAR = DAY*365; 
	
	public final static String REGEX_EMAIL = "^([\\w_!#\\$%&'\\*\\+\\-/=\\?\\^`\\{\\|\\}~\\.])+@([\\w\\-\\.]+\\.)+[\\w]{2,8}$";
	public final static String REGEX_CODFISC =  "^[a-zA-Z]{6}[0-9]{2}[abcdehlmprstABCDEHLMPRST]{1}[0-9]{2}([a-zA-Z]{1}[0-9]{3})[a-zA-Z]{1}$";
	public final static String REGEX_CODFISC_ALT = "^[0-9]{11}$";
	public final static String REGEX_TELEPHONE = "^(\\+)?[0-9\\s\\(\\)-\\\\]{4,18}$";
	public static final String REGEX_EAN = "^[0-9]{13}$";
	
	//DEFAULTS
	public static final String URL_APG_AUTOMATION_JOBS = "/apgautomation/joblistpage";
	public static final String URL_APG_AUTOMATION_REBUILD_FATTURA = "/apgautomation/rebuildfattura";
	//public static final String URL_APG_AUTOMATION_CREATE_RIMBORSO = "/apgautomation/createrimborso";
	public static final String URL_APG_AUTOMATION_DELIVERY = "/apgautomation/delivery.html";
	public static final String URL_APG_AUTOMATION_FATTURA_STAMPA = "/apgautomation/fatturestampe";
	public static final Double SOGLIA = 0.01D; //Soglia minima pagamenti
	public static final int SOGLIA_TEMPORALE_GIORNI_RINNOVA = 2; //Prima di questo tempo non si può rinnovare
	public static final int SOGLIA_TEMPORALE_MESI_RIGENERA = 6; //Prima di questo tempo non si può rinnovare
	public static final Date DEFAULT_DATE = new Date(0);// è 01/01/1970
	public static final Integer DEFAULT_ID_PERIODICO = 0;//DO NOT USE
	public static final String DEFAULT_TIPO_ABBO = "01";
	public static final String DEFAULT_ALIQUOTA_IVA = "VA";
	public static final int DEFAULT_ALIQUOTA_IVA_ID = 1;
	public static final String DEFAULT_IVA_SCORPORATA_DESCR = "fuori campo iva art.7";
	public static final int DEFAULT_MACROAREA = 1;
	public static final String DEFAULT_ID_NAZIONE_ITALIA = "ITA";
	public static final String DEFAULT_ID_PROVINCIA_ITALIA = "RM";
	public static final String DEFAULT_SOCIETA = "GE";
	public static final int TABLE_ROWS_DEFAULT = 25;
	public static final Integer SELECT_EMPTY_VALUE = -1;
	public static final String SELECT_EMPTY_VALUE_STRING = SELECT_EMPTY_VALUE.toString();
	public static final String SELECT_EMPTY_LABEL = "--";
	public static final String VALUTA = "EUR";
	public static final String SEARCH_STRING_SEPARATOR = ":";
	public static final String FATTURE_PREFISSO_FITTIZIO = "ZZZ";
	public static final int COMUN_ROLLBACK_DAYS = 90;
	public static final int MESE_INIZIO_MONTHS_FORWARD = 3;	
	
	//STATO APPLICAZIONE
	public static final String APG_PROD = "PROD";
	public static final String APG_TEST = "TEST";
	public static final String APG_DEV = "SVILUPPO";
	
	//UTENTI
	public static final int RUOLO_SUPER = 4;
	public static final int RUOLO_ADMIN = 3;
	public static final int RUOLO_EDITOR = 2;
	public static final int RUOLO_OPERATOR = 1;
	public static final int RUOLO_BLOCKED = 0;
	
	//PARAMETRI POST E GET
	public static final int NEW_ITEM_ID = -1;
	public static final String PARAM_ID = "id";
	public static final String PARAM_ID_RAPPORTO = "idRapporto";
	public static final String PARAM_ID_ANAGRAFICA = "idAnagrafica";
	public static final String PARAM_ID_PERIODICO = "idPeriodico";
	public static final String PARAM_ID_SOCIETA = "idSocieta";
	public static final String PARAM_ID_FASCICOLO = "idFas";
	public static final String PARAM_ID_COMUNICAZIONE = "idCom";
	public static final String PARAM_ID_TIPO_MEDIA = "idTipoMedia";
	public static final String PARAM_DATE = "date";
	public static final String PARAM_NAME = "name";
	public static final String PARAM_SCRIVI_DB = "scriviDb";
	public static final String PARAM_SCRIVI_DATA_ESTRAZIONE = "scriviDataEstrazione";
	public static final String PARAM_ID_TIPO_ABBONAMENTO = "idTipoAbb";
	public static final String PARAM_QUICKSEARCH = "qsearch";
	public static final String PARAM_ID_UTENTE = "idUtente";
	public static final String PARAM_DATA_INIZIO = "dataInizio";
	public static final String PARAM_DATA_FINE = "dataFine";
	public static final String PARAM_INCLUDI_COPIE = "copie";
	public static final String PARAM_INCLUDI_ITALIA = "italia";
	public static final String PARAM_TEST = "test";
	public static final String INCLUDI_TUTTI = "T";
	public static final String INCLUDI_INSIEME_INTERNO = "I";
	public static final String INCLUDI_INSIEME_ESTERNO = "E";
	public static final String PARAM_UNIONE_1 = "unione1";
	public static final String PARAM_UNIONE_2 = "unione2";
	public static final String PARAM_DIFFERENZA_1 = "differenza1";
	public static final String PARAM_DIFFERENZA_2 = "differenza2";
	
	//SERVIZI
	public static final String FAKE_ACTION_URL = "";
	public static final String SERV_AUTH = "servAuth";
	public static final String SERV_ANAGRAFICHE = "servAnagrafiche";
	public static final String SERV_ABBONAMENTI = "servAbbonamenti";
	public static final String SERV_TIPI_ABBONAMENTO = "servTipiAbb";
	public static final String SERV_LOOKUP = "servLookup";
	public static final String SERV_PAGAMENTI = "servPagamenti";
	public static final String SERV_FASCICOLI = "servFascicoli";
	public static final String SERV_OPZIONI = "servOpzioni";
	public static final String SERV_ARTICOLI = "servArticoli";
	public static final String SERV_COMUNICAZIONI = "servComunicazioni";
	public static final String SERV_LOGGING = "servLogging";
	public static final String SERV_STAT = "servStat";
	public static final String SERV_SAP = "servSap";
	
	//SERVLET
	public static final String SERVLET_UPLOAD_PAGAMENTI = "uploadPagamenti";
	public static final String SERVLET_DELIVERY_FILE_FILTER = "deliveryfilefilter";
	public static final String SERVLET_OUTPUT_FASCICOLI = "outputFascicoli";
	public static final String SERVLET_OUTPUT_ARTICOLI_LISTINI = "outputArticoliListini";
	public static final String SERVLET_OUTPUT_ARTICOLI_OPZIONI = "outputArticoliOpzioni";
	public static final String SERVLET_OUTPUT_ENQUEUED_EMAILS = "outputEnqueuedEmails";
	public static final String SERVLET_RINNOVO_MASSIVO = "rinnovoMassivo";
	public static final String SERVLET_QUERY_ISTANZE = "queryIstanze";

	//SUPPLEMENTI FORMAT
	public static final String SUPPL_ID_FORMAT = "###";
	public static final String SUPPL_SEPARATOR = ",";

	//MESSAGGI
	public static final String MSG_EMPTY_RESULT = "La ricerca non ha corrispondenze";
	public static final String MSG_EMPTY_LOG = "Log delle modifiche rimosso o non presente";
	public static final String MSG_SAVE_OK = "Salvataggio effettuato";
	public static final String MSG_LOG_END = "[FINE RAPPORTO]";
	
	//RESOURCES
	public static final String RESOURCE_DIR_LOGO = "/report/logo/";
	public static final String RESOURCE_DIR_JASPER = "/report/";
	public static final String RESOURCE_TYPE_LOGO = "LR";
	public static final String RESOURCE_TYPE_JASPER = "JR";
	
	//SESSO
	public static final String SESSO_M = "M";
	public static final String SESSO_F = "F";
	
	//BOOLEAN
	public static final String BOOLEAN_TRUE = "true";
	public static final String BOOLEAN_FALSE = "false";
	
	//TIPI FILE UPLOAD
	public static final String FILE_UPLOAD_BOLLETTINI = "BOL";
	
	//TIPI PAGAMENTO
	public static final int PAGAMENTO_MAX_MESI_RITARDO_DA_GRACING = 3;
	public static final int PAGAMENTO_MIN_MESI_ANTICIPO = 2;
	public static final String PAGAMENTO_BOLLETTINO = "BOL";
	public static final String PAGAMENTO_MANUALE ="MAN";
	public static final String PAGAMENTO_CARTA_CREDITO = "CCR";
	public static final String PAGAMENTO_RESTO = "RES";
	public static final String PAGAMENTO_ABBUONO = "ABB";
	//public static final String PAGAMENTO_ASSEGNO = "ASS";
	public static final String PAGAMENTO_BONIFICO = "BNF";
	public static final String PAGAMENTO_CARTA_DOCENTE = "CDO";
	public static final Map<String, String> PAGAMENTO_DESC = new HashMap<String, String>();
	static {
		PAGAMENTO_DESC.put(PAGAMENTO_BOLLETTINO, "Bollettino");
		PAGAMENTO_DESC.put(PAGAMENTO_MANUALE, "Altro");
		PAGAMENTO_DESC.put(PAGAMENTO_CARTA_CREDITO, "Carta di credito");
		//PAGAMENTO_DESC.put(PAGAMENTO_RESTO, "Resto");
		//PAGAMENTO_DESC.put(PAGAMENTO_ABBUONO, "Abbuono");
		//PAGAMENTO_DESC.put(PAGAMENTO_ASSEGNO, "Assegno");
		PAGAMENTO_DESC.put(PAGAMENTO_BONIFICO, "Bonifico");
		PAGAMENTO_DESC.put(PAGAMENTO_CARTA_DOCENTE, "Carta docente");};
	public static final String PAGAMENTO_DEFAULT = PAGAMENTO_MANUALE;

	//TIPI ERRORI PAGAMENTO
	public static final String PAGAMENTO_ERR_INESISTENTE = "NUL";//Codice abbonamento inesistente
	public static final String PAGAMENTO_ERR_NON_ABBINABILE = "NAB";//Istanza scaduta o non pagata da troppo tempo
	public static final String PAGAMENTO_ERR_IMPORTO = "IMP";//Importo errato
	public static final String PAGAMENTO_ERR_CREDITO_RESIDUO = "RES";//Esisite del credito da usare
	public static final String PAGAMENTO_ERR_NON_RINNOVABILE = "NRI";//Un tipo abbonamento (ancora) non rinnovabile
	public static final String PAGAMENTO_ERR_DUPLICATO = "DUP";//Un tipo abbonamento (ancora) non rinnovabile
	public static final Map<String, String> PAGAMENTO_ERR_DESC = new HashMap<String, String>();
	static {
		PAGAMENTO_ERR_DESC.put(PAGAMENTO_ERR_INESISTENTE, "Codice inesistente");
		PAGAMENTO_ERR_DESC.put(PAGAMENTO_ERR_NON_ABBINABILE, "Non abbinabile");
		PAGAMENTO_ERR_DESC.put(PAGAMENTO_ERR_IMPORTO, "Importo errato");
		PAGAMENTO_ERR_DESC.put(PAGAMENTO_ERR_CREDITO_RESIDUO, "Altri crediti presenti");
		PAGAMENTO_ERR_DESC.put(PAGAMENTO_ERR_NON_RINNOVABILE, "Non rinnovabile");
		PAGAMENTO_ERR_DESC.put(PAGAMENTO_ERR_DUPLICATO, "Duplicato");};

	//TIPI ANAGRAFICA
	public static final String ANAG_PRIVATO = "PVT";
	public static final String ANAG_AGENTE = "AGE";
	public static final String ANAG_AZIENDA = "AZN";
	public static final String ANAG_LIBRERIA = "LIB";
	public static final String ANAG_SCUOLA = "SCU";
	public static final String ANAG_BIBLIOTECA = "BIB";
	public static final Map<String, String> ANAG_DESC = new HashMap<String, String>();
	static {
		ANAG_DESC.put(ANAG_PRIVATO, "Privato");
		ANAG_DESC.put(ANAG_AGENTE, "Agente");
		ANAG_DESC.put(ANAG_AZIENDA, "Azienda");
		ANAG_DESC.put(ANAG_LIBRERIA, "Libreria");
		ANAG_DESC.put(ANAG_SCUOLA, "Scuola");
		ANAG_DESC.put(ANAG_BIBLIOTECA, "Biblioteca");};
		
	//TIPI SPEDIZIONE
	public static final String SPEDIZIONE_POSTA_ORDINARIA = "ORD";
	public static final String SPEDIZIONE_VIA_AEREA = "AER";
	public static final String SPEDIZIONE_CORRIERE = "COR";
	public static final Map<String, String> SPEDIZIONE_DESC = new HashMap<String, String>();
	static {
		SPEDIZIONE_DESC.put(SPEDIZIONE_POSTA_ORDINARIA, "Posta ordinaria");
		SPEDIZIONE_DESC.put(SPEDIZIONE_VIA_AEREA, "Via aerea");
		SPEDIZIONE_DESC.put(SPEDIZIONE_CORRIERE, "Corriere");};
	public static final String SPEDIZIONE_DEFAULT = SPEDIZIONE_POSTA_ORDINARIA;
	
	//TIPI PERIODICO
	public static final String PERIODICO_VARIA = "VAR";
	public static final String PERIODICO_SCOLASTICO = "SCO";
	public static final Map<String, String> PERIODICO_DESC = new HashMap<String, String>();
	static {
		PERIODICO_DESC.put(PERIODICO_VARIA, "Varia");
		PERIODICO_DESC.put(PERIODICO_SCOLASTICO, "Scolastico");};
	
	//TIPI EVASIONE FASCICOLO
	public static final String EVASIONE_FAS_REGOLARE = "REG";
	public static final String EVASIONE_FAS_INIZIO_ABBONAMENTO = "INI";
	public static final String EVASIONE_FAS_ARRETRATO = "ARR";
	public static final Map<String, String> EVASIONE_FAS_DESC = new HashMap<String, String>();
	static {
		EVASIONE_FAS_DESC.put(EVASIONE_FAS_REGOLARE, "Regolare(+1)");
		EVASIONE_FAS_DESC.put(EVASIONE_FAS_INIZIO_ABBONAMENTO, "Retroattivo(+1)");
		EVASIONE_FAS_DESC.put(EVASIONE_FAS_ARRETRATO, "Rispedizione");};
	
	//TIPI ANAGRAFICA_SAP
	public static final String ANAGRAFICA_SAP_GE_FASCICOLO = "RG";
	public static final String ANAGRAFICA_SAP_GE_LIBRO = "LI";
	public static final String ANAGRAFICA_SAP_GS = "RS";
	//public static final String ANAGRAFICA_SAP_OS = "RG";//TODO da cambiare
	public static final Map<String, String> ANAGRAFICA_SAP_DESC = new HashMap<String, String>();
	static {
		ANAGRAFICA_SAP_DESC.put(ANAGRAFICA_SAP_GE_FASCICOLO, "Giunti Ed. (fascicolo)");
		ANAGRAFICA_SAP_DESC.put(ANAGRAFICA_SAP_GE_LIBRO, "Giunti Ed. (libro o EDU)");
		ANAGRAFICA_SAP_DESC.put(ANAGRAFICA_SAP_GS, "Giunti Scuola");
		//ANAGRAFICA_SAP_DESC.put(ANAGRAFICA_SAP_OS, "Giunti OS");
		};
		
	//TIPI COMUNICAZIONE
	public static final String COMUN_TIPI_ABB_SEPARATOR = ",";
	public static final String COMUN_MEDIA_BOLLETTINO = "BOL";
	public static final String COMUN_MEDIA_LETTERA = "LET";
	/*public static final String COMUN_MEDIA_NDD = "NDD";*/
	public static final String COMUN_MEDIA_EMAIL = "EML";
	public static final Map<String, String> COMUN_MEDIA_DESC = new HashMap<String, String>();
	static {
		COMUN_MEDIA_DESC.put(COMUN_MEDIA_BOLLETTINO, "Bollettino");
		COMUN_MEDIA_DESC.put(COMUN_MEDIA_LETTERA, "Lettera");
		/*COMUN_MEDIA_DESC.put(COMUN_MEDIA_NDD, "Nota di debito");*/
		COMUN_MEDIA_DESC.put(COMUN_MEDIA_EMAIL, "Email");};
	public static final Map<String, String> COMUN_MEDIA_DESC_PLUR = new HashMap<String, String>();
	static {
		COMUN_MEDIA_DESC_PLUR.put(COMUN_MEDIA_BOLLETTINO, "bollettini");
		COMUN_MEDIA_DESC_PLUR.put(COMUN_MEDIA_LETTERA, "lettere");
		/*COMUN_MEDIA_DESC_PLUR.put(COMUN_MEDIA_NDD, "note di debito");*/
		COMUN_MEDIA_DESC_PLUR.put(COMUN_MEDIA_EMAIL, "email");};
		
	//TIPI ATTIVAZIONE COMUNICAZIONE
	public static final String COMUN_ATTIVAZ_DA_INIZIO = "BEG";//periodico partendo dall'inizio
	public static final String COMUN_ATTIVAZ_DA_FINE = "END";//periodico partendo dalla fine
	public static final String COMUN_ATTIVAZ_AL_PAGAMENTO = "PAY";//gestiti tramite job
	public static final String COMUN_ATTIVAZ_ALLA_CREAZIONE = "NEW";//gestiti tramite job
	public static final String COMUN_ATTIVAZ_PER_STATUS = "STA";
	public static final Map<String, String> COMUN_ATTIVAZ_DESC = new HashMap<String, String>();
	static {
		COMUN_ATTIVAZ_DESC.put(COMUN_ATTIVAZ_DA_INIZIO, "num dall'inizio");
		COMUN_ATTIVAZ_DESC.put(COMUN_ATTIVAZ_DA_FINE, "num. dalla fine");
		COMUN_ATTIVAZ_DESC.put(COMUN_ATTIVAZ_AL_PAGAMENTO, "pagamento/fatturazione");
		COMUN_ATTIVAZ_DESC.put(COMUN_ATTIVAZ_ALLA_CREAZIONE, "creazione/rinnovo/cambio tipo");
		COMUN_ATTIVAZ_DESC.put(COMUN_ATTIVAZ_PER_STATUS, "programmazione software");};

	//TIPI DESTINATARI
	public static final String DEST_BENEFICIARIO = "BEN";
	public static final String DEST_PAGANTE = "PAG";
	public static final String DEST_PROMOTORE = "PRM";
	public static final Map<String, String> DEST_DESC = new HashMap<String, String>();
	static {
		DEST_DESC.put(DEST_BENEFICIARIO, "beneficiario");
		DEST_DESC.put(DEST_PAGANTE, "pagante");
		DEST_DESC.put(DEST_PROMOTORE, "promotore");};
	
	//TAG
	public static final String TAG_AREAEXTRA = "AREAEXTRA";
	//public static final String TAG_SESAMO = "SESAMO";
	public static final String TAG_WEBTV = "WEBTV";
	public static final String[] TAG_DESC = {
		TAG_AREAEXTRA, TAG_WEBTV};
	
	//SOCIETA
	public static final String SOCIETA_GIUNTI_EDITORE = "GE";
	public static final String SOCIETA_GIUNTI_SCUOLA = "GS";
	public static final String SOCIETA_GIUNTI_OS = "OS";
	public static final Map<String, String> SOCIETA_DESC = new HashMap<String, String>();
	static {
		SOCIETA_DESC.put(SOCIETA_GIUNTI_EDITORE, "Giunti Editore");
		SOCIETA_DESC.put(SOCIETA_GIUNTI_SCUOLA, "Giunti Scuola");
		SOCIETA_DESC.put(SOCIETA_GIUNTI_OS, "Giunti O.S.");};
	
	//DOCUMENTO
	public static final String DOCUMENTO_FATTURA = "FAT";
	public static final String DOCUMENTO_NOTA_CREDITO = "NDC";
	public static final Map<String, String> DOCUMENTO_DESC = new HashMap<String, String>();
	static {
		DOCUMENTO_DESC.put(DOCUMENTO_FATTURA, "Fattura");
		DOCUMENTO_DESC.put(DOCUMENTO_NOTA_CREDITO, "Nota di credito");};
		
	//BOLLETTINO GENERICO
//	public static final String BOLLETTINO_AUT = "AUT. DB/SS1C/E6805/DEL 01/03/02";
//	public static final String BOLLETTINO_CODICE_MODELLO = "896";
//	public static final String BOLLETTINO_PATH_LOGO_VERTICAL = "/report/logo/logo_giunti_V.jpg";
//	public static final String BOLLETTINO_PATH_LOGO_SMALL = "/report/logo/icon.jpg";
//	public static final String BOLLETTINO_PATH_REPORT_BOL = "/report/bollettino896_2016.jasper";
////	public static final String BOLLETTINO_PATH_REPORT_NDD = "/report/ndd896Report.jasper";
	public static final int BOLLETTINO_MESSAGE_MAX_LENGTH = 1024;
	
	//TIPO IVA
	public static final String IVA_ITALIA_PRIVATO = "ITPV";
	public static final String IVA_ITALIA_SOCIETA = "ITSC";
	public static final String IVA_UE_PRIVATO = "UEPV";
	public static final String IVA_UE_SOCIETA = "UESC";
	public static final String IVA_EXTRA_UE = "EXTR";
		
}
