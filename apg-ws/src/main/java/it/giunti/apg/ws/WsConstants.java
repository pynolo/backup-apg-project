package it.giunti.apg.ws;

import java.util.HashMap;
import java.util.Map;

public class WsConstants {

	public static final String HIBERNATE_CONFIG_FILE = "/hibernate.cfg.xml";
	
	public static final String PROVINCIA_ESTERO_AUTH="EE";
		
	public static final boolean BUONO_ACQUISTO_DEFAULT = false;
	public static final boolean AREA_EXTRA_DEFAULT = false;
	
	public static final int DAYS_TO_DELETE_CONTENT = 1;
	public static final int SUBSCRIPTION_RANGE_EXTENSION_DAYS = 7;
	
	public static final Integer WS_ERR_SYSTEM = 1;
	public static final Integer WS_ERR_NOT_FOUND = 2;
	public static final Integer WS_ERR_PARAMETER = 3;
	public static final Integer WS_ERR_REMOVED = 4;
	public static final String WS_ERR_SYSTEM_DESC = "Errore interno del server, contattare l'amministratore";
	public static final String WS_ERR_NOT_FOUND_DESC = "La ricerca non ha corrispondenze";
	public static final String WS_ERR_PARAMETER_DESC = "Uno o piu' parametri di ricerca non sono validi";
	public static final String WS_ERR_REMOVED_DESC = "I dati sono stati rimossi";
	
	//SERVICES
	
	public static final String SERVICE_SEPARATOR = ";";
	public static final String SERVICE_OK = "OK";
	public static final String SERVICE_ABBONAMENTI_INFO = "ABBO_INFO";
	public static final String SERVICE_HBSAUTH = "HBSAUTH";
	public static final String SERVICE_INFOPROVIDER = "INFOPROVIDER";
	public static final String SERVICE_GIUNTISCUOLAINFO = "GIUNTISCUOLAINFO";
	public static final String SERVICE_GIUNTISCUOLAINFO2 = "GIUNTISCUOLAINFO2";
	public static final String SERVICE_PAPERLIT = "PAPERLIT";
	public static final String SERVICE_API01 = "API01";
	
	//SERVICE CONSTANTS
	
	public static final String PAPERLIT_PARAM_USER = "user";
	public static final String PAPERLIT_PARAM_PASSWORD = "password";
	public static final String PAPERLIT_PARAM_DEVICE_ID = "deviceId";
	public static final String PAPERLIT_PARAM_BUNDLE_ID = "bundleId";
	public static final String PAPERLIT_PARAM_CALLBACK = "callback";
	//Mappe che relazionano l'id del periodico con il bundleId e publicationId di paperlit
	public static final Map<String, Integer> PAPERLIT_BUNDLE_IDS = new HashMap<String, Integer>();
	static {
		PAPERLIT_BUNDLE_IDS.put("com.paperlit.psicologiacontemporanea", 2);
		PAPERLIT_BUNDLE_IDS.put("com.paperlit.ipad.psicologiacontemporanea", 2);//iOS
		PAPERLIT_BUNDLE_IDS.put("com.paperlit.android.psicologiacontemporanea", 2);//Android
		PAPERLIT_BUNDLE_IDS.put("com.paperlit.android.artedossier", 8);//Android
		PAPERLIT_BUNDLE_IDS.put("com.giunti.aed", 8);//iOS
		PAPERLIT_BUNDLE_IDS.put("com.paperlit.archeologiaviva", 9);
		PAPERLIT_BUNDLE_IDS.put("com.paperlit.ipad.archeologiaviva", 9);//iOS
		PAPERLIT_BUNDLE_IDS.put("com.paperlit.android.archeologiaviva", 9);//Android
	};
	public static final Map<Integer, String> PAPERLIT_PUBLICATION_IDS = new HashMap<Integer, String>();
	static {
		PAPERLIT_PUBLICATION_IDS.put(2, "psicologiacontemporanea");
		PAPERLIT_PUBLICATION_IDS.put(8, "artedossier");
		PAPERLIT_PUBLICATION_IDS.put(9, "archeologiaviva");
	};
}
