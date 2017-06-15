package it.giunti.apgautomation.server.sap;

import it.giunti.apg.shared.BusinessException;

import java.util.Hashtable;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;

public class CustomDestinationDataProvider {
	static private Logger LOG = LoggerFactory.getLogger(CustomDestinationDataProvider.class);
	
	public CustomDestinationDataProvider(String ashost, String gwhost, String sysnr,
			String client, String user, String passwd, String lang)
			throws BusinessException {
		Properties destProp = new Properties();
		destProp.setProperty(DestinationDataProvider.JCO_ASHOST, ashost);
		destProp.setProperty(DestinationDataProvider.JCO_GWHOST, gwhost);
		destProp.setProperty(DestinationDataProvider.JCO_SYSNR, sysnr);
		destProp.setProperty(DestinationDataProvider.JCO_CLIENT, client);
		destProp.setProperty(DestinationDataProvider.JCO_USER, user);
		destProp.setProperty(DestinationDataProvider.JCO_PASSWD, passwd);
		destProp.setProperty(DestinationDataProvider.JCO_LANG, lang);
		destProp.setProperty(DestinationDataProvider.JCO_DEST, SapConstants.DEFAULT_DESTINATION);
		
		MyDestinationDataProvider myProvider = new MyDestinationDataProvider();
		try {
			com.sap.conn.jco.ext.Environment.registerDestinationDataProvider(myProvider);
			myProvider.setPropertiesForABAP_AS(destProp);
		} catch (IllegalStateException e) {
			if(e.getMessage().contains("already registered")) {
				LOG.info("Registering SAP data provider: "+e.getMessage());
			} else {
				throw new BusinessException(e.getMessage(), e);
			}
		} catch (Exception e) { //SAP lancia eccezioni non dichiarate
			throw new BusinessException(e.getMessage(), e);
		}
		try {
			JCoDestination ABAP_AS = JCoDestinationManager
					.getDestination(SapConstants.DEFAULT_DESTINATION);
			LOG.info("SAP connection attributes: \r\n"+ABAP_AS.getAttributes().toString());
			ABAP_AS.ping();
		} catch (JCoException e) {
			throw new BusinessException(e.getMessage(), e);
		} catch (Exception e) { //SAP lancia eccezioni non dichiarate
			throw new BusinessException(e.getMessage(), e);
		}
	}

	public JCoDestination getDestination() throws BusinessException {
		try {
			return JCoDestinationManager.getDestination(SapConstants.DEFAULT_DESTINATION);
		} catch (JCoException e) {
			throw new BusinessException(e.getMessage(), e);
		} catch (Exception e) { //SAP lancia eccezioni non dichiarate
			throw new BusinessException(e.getMessage(), e);
		}
	}
	
	
	//** INNER CLASSES **
	
	
	static class MyDestinationDataProvider implements DestinationDataProvider {
		private DestinationDataEventListener eL;

		private Hashtable<String, Properties> propertiesTab = new Hashtable<String, Properties>();

		public Properties getDestinationProperties(String destinationName) {
			if (propertiesTab.containsKey(destinationName)) {
				return propertiesTab.get(destinationName);
			}
			//return null;
			// alternatively throw runtime exception
			throw new RuntimeException("Destination " + destinationName +
			" is not available");
		}

		public void setDestinationDataEventListener(
				DestinationDataEventListener eventListener) {
			this.eL = eventListener;
		}

		public boolean supportsEvents() {
			return true;
		}

		void setPropertiesForABAP_AS(Properties pConProps) {
			propertiesTab.put(pConProps.getProperty("jco.client.dest"),
					pConProps);
			eL.updated(pConProps.getProperty("jco.client.dest"));
		}
		
		void removePropertiesForABAP_AS(Properties pConProps) {
			propertiesTab.remove(pConProps.getProperty("jco.client.dest"));
			eL.deleted(pConProps.getProperty("jco.client.dest"));
		}
		
		void changePropertiesForABAP_AS(Properties pConProps) {
			if (pConProps.getProperty("ACTION").equalsIgnoreCase("CREATE")) {
				propertiesTab.put(pConProps.getProperty("jco.client.dest"),
						pConProps);
				eL.updated(pConProps.getProperty("jco.client.dest"));
			} else if (pConProps.getProperty("ACTION").equalsIgnoreCase(
					"DELETE")) {
				propertiesTab.remove(pConProps.getProperty("jco.client.dest"));
				eL.deleted(pConProps.getProperty("jco.client.dest"));
			}
		}
	}
	
	//EXAMPLE 
	
	//public class CustomDestinationDataProvider
	//{
	//    static class MyDestinationDataProvider implements DestinationDataProvider
	//    {
	//        private DestinationDataEventListener eL;
	//
	//        private Hashtable<String, Properties> propertiesTab = new Hashtable<String, Properties>();
	//        
	//        public Properties getDestinationProperties(String destinationName)
	//        {
	//             if(propertiesTab.containsKey(destinationName)){
	//                  return propertiesTab.get(destinationName);
	//             }
	//            
	//            return null;
	//            //alternatively throw runtime exception
	//            //throw new RuntimeException("Destination " + destinationName + " is not available");
	//        }
	//
	//        public void setDestinationDataEventListener(DestinationDataEventListener eventListener)
	//        {
	//            this.eL = eventListener;
	//        }
	//
	//        public boolean opzortsEvents()
	//        {
	//            return true;
	//        }
	//        
	//        void changePropertiesForABAP_AS(Properties pConProps)
	//        {
	//            if(pConProps.getProperty("ACTION").equalsIgnoreCase("CREATE")){
	//                 propertiesTab.put(pConProps.getProperty("jco.client.dest"), pConProps);
	//                 eL.updated(pConProps.getProperty("jco.client.dest"));                 
	//            }
	//            else if(pConProps.getProperty("ACTION").equalsIgnoreCase("DELETE")){
	//                 propertiesTab.remove(pConProps.getProperty("jco.client.dest"));
	//                 eL.deleted(pConProps.getProperty("jco.client.dest"));
	//            }
	//        }
	//    }
	//    
	//    
	//    public static void main(String[] args) throws Exception
	//    {
	//        Properties connectPropertiesEN = new Properties();
	//        connectPropertiesEN.setProperty("ACTION", "CREATE");
	//        connectPropertiesEN.setProperty(DestinationDataProvider.JCO_DEST, "POOL_EN");
	//        connectPropertiesEN.setProperty(DestinationDataProvider.JCO_ASHOST, "<HOST_IP>");
	//        connectPropertiesEN.setProperty(DestinationDataProvider.JCO_SYSNR,  "<SYSNR>");
	//        connectPropertiesEN.setProperty(DestinationDataProvider.JCO_CLIENT, "<CLIENT>");
	//        connectPropertiesEN.setProperty(DestinationDataProvider.JCO_USER,   "<USER>");
	//        connectPropertiesEN.setProperty(DestinationDataProvider.JCO_PASSWD, "<PASS>");
	//        connectPropertiesEN.setProperty(DestinationDataProvider.JCO_LANG,   "en");
	//        
	//        Properties connectPropertiesDE = new Properties();
	//        connectPropertiesDE.setProperty("ACTION", "CREATE");
	//        connectPropertiesDE.setProperty(DestinationDataProvider.JCO_DEST, "POOL_DE");
	//        connectPropertiesDE.setProperty(DestinationDataProvider.JCO_ASHOST, "<HOST_IP>");
	//        connectPropertiesDE.setProperty(DestinationDataProvider.JCO_SYSNR,  "<SYSNR>");
	//        connectPropertiesDE.setProperty(DestinationDataProvider.JCO_CLIENT, "<CLIENT>");
	//        connectPropertiesDE.setProperty(DestinationDataProvider.JCO_USER,   "<USER>");
	//        connectPropertiesDE.setProperty(DestinationDataProvider.JCO_PASSWD, "<PASS>");
	//        connectPropertiesDE.setProperty(DestinationDataProvider.JCO_LANG,   "de");        
	//
	//        MyDestinationDataProvider myProvider = new MyDestinationDataProvider();
	//        
	//        com.sap.conn.jco.ext.Environment.registerDestinationDataProvider(myProvider);
	//        myProvider.changePropertiesForABAP_AS(connectPropertiesEN);
	//        myProvider.changePropertiesForABAP_AS(connectPropertiesDE);
	//        
	//        JCoDestination ABAP_AS_EN = JCoDestinationManager.getDestination("POOL_EN");
	//        System.out.println(ABAP_AS_EN.getAttributes());
	//        ABAP_AS_EN.ping();
	//        
	//        JCoDestination ABAP_AS_DE = JCoDestinationManager.getDestination("POOL_DE");
	//        System.out.println(ABAP_AS_DE.getAttributes());
	//        ABAP_AS_DE.ping();        
	//
	//        System.out.println(ABAP_AS_EN.getDestinationName() +" destination is ok");
	//        System.out.println(ABAP_AS_DE.getDestinationName() +" destination is ok");        
	//        
	//    }
	//    
	//}
}
