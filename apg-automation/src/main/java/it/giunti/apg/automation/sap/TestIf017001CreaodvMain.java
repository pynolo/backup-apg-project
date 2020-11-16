package it.giunti.apg.automation.sap;

import java.net.Authenticator;
import java.util.Map;

import javax.xml.ws.BindingProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.giunti.apg.soap.creaodv.ORDEROUT;
import it.giunti.apg.soap.creaodv.ORDERREQ;
import it.giunti.apg.soap.creaodv.ORDERRSP;

public class TestIf017001CreaodvMain {

	private static final Logger LOG = LoggerFactory.getLogger(TestIf017001CreaodvMain.class);
	
    public static void main(String[] args) {
    	TestIf017001CreaodvMain test = new TestIf017001CreaodvMain();
    	test.test("GIUNTI_APG", "giunti02");
    }
    
    public TestIf017001CreaodvMain() {
    }
    
	public void test(String wsUser, String wsPass) {
		LOG.info("WSDL: "+CreaodvSapService.WSDL_LOCATION);
		
		//Prepara la tabella di input per SAP
		LOG.info("Chiamata SAP per inserimento ordini");
		ORDERREQ tbInput = new ORDERREQ();
		
		String bstkd = "0008E80"; //CHAR35 Ordine APG
		//Nome CHAR30
		String name = "DE SANCTIS ANNA";
		//Presso CHAR30
		String presso = "";
		//Via CHAR60
		String street = "VIA BATTISTI 17";
		//Cap CHAR10
		String postCode = "20061";
		//Localita CHAR40
		String localita =  "CARUGATE";
		//Provincia CHAR3
		String provincia = "";//MI
		//Nazione CHAR3
		String nazione = "SE";//IT
		
		String tipo = "RG";
		String copie = "1";
		
		String[] matnrArray = {"X0199W", "X1146W"};
		
		for (String matnr:matnrArray) {
			ORDERREQ.RECORDSET row = new ORDERREQ.RECORDSET();
			row.setBSTKD(bstkd);
			row.setMENGE(copie);
			row.setNAME1(name);
			row.setNAME2(presso);
			row.setSTREET(street);
			row.setPOSTCODE1(postCode);
			row.setCITY1(localita);
			if (provincia != null) {
				row.setREGION(provincia);
			} else {
				row.setREGION("");
			}
			row.setCOUNTRY(nazione);
			row.setMATNR(matnr);
			row.setTIPO(tipo);
			tbInput.getRECORDSET().add(row);
		}
		
		//Chiamata funzione SAP
		CreaodvSapService wsService = new CreaodvSapService(CreaodvSapService.WSDL_LOCATION, CreaodvSapService.SERVICE);
        //INVOICEEOUT port = wsService.getHTTPSPort();// no HTTPS !!
		ORDEROUT port = wsService.getHTTPPort();
		
        Map<String, Object> reqCtx = ((BindingProvider)port).getRequestContext();
        LOG.info("Endpoint: "+reqCtx.get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY));
        Authenticator.setDefault(new S4Authenticator(wsUser, wsPass));
//        Map<String, List<String>> headers = new HashMap<String, List<String>>();
//        headers.put("Username", Collections.singletonList(wsUser));
//        headers.put("Password", Collections.singletonList(wsPass));
//        headers.put(BindingProvider.USERNAME_PROPERTY, Collections.singletonList(wsUser));
//        headers.put(BindingProvider.PASSWORD_PROPERTY, Collections.singletonList(wsPass));
//        reqCtx.put(MessageContext.HTTP_REQUEST_HEADERS, headers);
        
		LOG.info("Invoking orderOUT...");
		ORDERRSP tbOutput = port.orderOUT(tbInput);
		LOG.info("stockREPORTOUT result=" + tbOutput);

		
		//Crea la mappa delle disponibilità e scrive log materiali
		LOG.info("Risposta SAP");
		for (ORDERRSP.RECORDSET row:tbOutput.getRECORDSET()) {
			boolean errore = false;
			if (row.getERRORE() != null) errore = row.getERRORE().equals("X");
			if (errore) {
				LOG.info("[Ord."+row.getBSTKD()+"] <b>Annullato</b> errore SAP: "+row.getTESTO());
			} else {
				LOG.info("[Ord."+row.getBSTKD()+"] <b>OK</b>");
			}
		}
	}
	
	
	
	
	// Inner Classes
	
	
	
	public static class ArticleBean {
		private String cm = null;
		private String committente = null;
		//private Integer copieRichieste = null;
		//private Integer copieDisponibili = null;
		
		public String getCm() {
			return cm;
		}
		public void setCm(String cm) {
			this.cm = cm;
		}
		public String getCommittente() {
			return committente;
		}
		public void setCommittente(String committente) {
			this.committente = committente;
		}
		//public Integer getCopieRichieste() {
		//	return copieRichieste;
		//}
		//public void setCopieRichieste(Integer copieRichieste) {
		//	this.copieRichieste = copieRichieste;
		//}
		//public Integer getCopieDisponibili() {
		//	return copieDisponibili;
		//}
		//public void setCopieDisponibili(Integer copieDisponibili) {
		//	this.copieDisponibili = copieDisponibili;
		//}
		
	}
}
