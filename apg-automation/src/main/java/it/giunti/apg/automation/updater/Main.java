package it.giunti.apg.automation.updater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
	private static Logger LOG = LoggerFactory.getLogger(Main.class);
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			//CreateCartaDocenteFile.execute();
			FattureAccompagnamentoDaFileLista.execute(args);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

//	private void initLogger(){
//		try {
//			InputStream confIs = this.getClass().getResourceAsStream(ServerConstants.LOGGER_CONFIG_FILE);
////			try {
////				confIs = new FileInputStream("."+ServerConstants.LOGGER_CONFIG_FILE);
////			} catch (FileNotFoundException e) {
////				e.printStackTrace();
////			}
//			if(confIs!=null){
//				Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(confIs);
//				DOMConfigurator.configure(doc.getDocumentElement());
//				//Instanzio il logger
//				Logger LOG = LoggerFactory.getLogger(StartLoggerServlet.class);
//				LOG.info("Logger instantiated from ."+ServerConstants.LOGGER_CONFIG_FILE);
//			} else {
//				throw new RuntimeErrorException(null, ServerConstants.LOGGER_CONFIG_FILE+" NOT FOUND");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

}
