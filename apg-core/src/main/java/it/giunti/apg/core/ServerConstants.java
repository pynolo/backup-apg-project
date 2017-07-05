package it.giunti.apg.core;

import it.giunti.apg.shared.AppConstants;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ServerConstants {
	private static final Logger LOG = LoggerFactory.getLogger(ServerConstants.class);
	
	public static String QUARTZ_CONFIG_FILE = "/quartz-jobs.xml";
	public static String LOGGER_CONFIG_FILE = "/log4j-custom.xml";
	public static String PROPERTY_FILE = "/apg.properties";
	public static String VERSION_FILE = "/version.properties";
	public static String UPLOAD_DIRECTORY = System.getProperty("java.io.tmpdir");
	
	//public static final Charset DEFAULT_FILE_CHARSET = Charset.forName("ISO-8859-15");//Now UTF-8
	public static final String DEFAULT_SYSTEM_USER = "admin";
	
	public static final SimpleDateFormat FORMAT_DAY = new SimpleDateFormat(AppConstants.PATTERN_DAY);
	public static final SimpleDateFormat FORMAT_DAY_SQL = new SimpleDateFormat(AppConstants.PATTERN_DAY_SQL);
	public static final SimpleDateFormat FORMAT_MONTH = new SimpleDateFormat(AppConstants.PATTERN_MONTH);
	public static final SimpleDateFormat FORMAT_YEAR = new SimpleDateFormat("yyyy");
	public static final SimpleDateFormat FORMAT_DATETIME = new SimpleDateFormat(AppConstants.PATTERN_DATETIME);
	public static final SimpleDateFormat FORMAT_FILE_NAME_TIMESTAMP = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	public static final DecimalFormat FORMAT_INTEGER = new DecimalFormat(AppConstants.PATTERN_INTEGER);
	public static final DecimalFormat FORMAT_CURRENCY = new DecimalFormat(AppConstants.PATTERN_CURRENCY);
	public static Date DATE_FAR_PAST;
	public static Date DATE_FAR_FUTURE;
	static {
		try {
			DATE_FAR_PAST = FORMAT_DAY.parse("01/01/1000");
			DATE_FAR_FUTURE = FORMAT_DAY.parse("01/01/3000");
		} catch (ParseException e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	public static final Integer DEFAULT_ABBONAMENTI_START_VALUE = 1000;
	public static final Integer DEFAULT_ANAGRAFICHE_START_VALUE = 0;
	public static final Integer DEFAULT_ANAGRAFICHE_CODE_LENGTH = 5;//Senza contare il carattere di checksum
	public static final Integer DEFAULT_ORDINI_CODE_LENGTH = 7;
	public static final Integer FATTURE_PAGE_SIZE = 200;
	
	//INVIO
	public static final SimpleDateFormat INVIO_SDF_FASCICOLI = new SimpleDateFormat("dd/MM/yyyy");
	public static final String INVIO_OMAGGIO = "O M A G G I O";
	public static final String INVIO_COPIA_OFFERTA = "Copia offerta da: ";
	public static final String INVIO_SCADENZA = "scadenza abbonamento: ";
	public static final String INVIO_EOL = "\r\n";
	
	//CONTATORI
	public static final String CONTATORE_PERIODICO_PREFIX = "PERIODICO_";
	public static final String CONTATORE_NDD = "NDD";
	public static final String CONTATORE_ORDINI = "ORDINI";
	public static final String CONTATORE_ANAGRAFICHE = "ANAGRAFICHE";
	public static final String CONTATORE_PAGINE_REG_MENS_FATTURE = "REGMENS";
	//LDAP Active directory
	public static final String LDAP_HOST = "ldap.intranet.giunti.it";
	public static final String LDAP_DOMAIN = "giunti.it";
	public static final String LDAP_BASE_DN = "dc=intranet,dc=giunti,dc=it";
	public static final String LDAP_PRINCIPAL = "CN=Ricercheportale,OU=Utenti di Servizio,DC=intranet,DC=giunti,DC=it";
	public static final String LDAP_CREDENTIAL = "x7ap2roj";
	
	//SMTP
	public static final String SMTP_HOST = "fismtprelay.intranet.giunti.it";
	//public static final String SMTP_HOST = "poca01.intranet.giunti.it";//"192.168.4.30";
	//public static final String SMTP_USER = "intranet\\relay.apg";
	//public static final String SMTP_PASSWORD = "Rry74apg";
	public static final String SMTP_FROM = "relay.apg@giunti.it";
	//public static final String[] SMTP_DEFAULT_RECIPIENTS = { "p.tacconi@giunti.it" };
	public static final String[] SMTP_DEFAULT_RECIPIENTS = { };
	
	//FTP
	//public static final String FTP_BASE_HOST = "ftp.giunti.it";
	//public static final String FTP_BASE_PORT = "21";
	//public static final String FTP_BASE_USERNAME = "apg-rw";
	//public static final String FTP_BASE_PASSWORD = "Rwapg2014";
	//public static final String FTP_BASE_DIR = "";
	
}
