package it.giunti.apg.automation.sap;

import java.net.Authenticator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.giunti.apg.soap.anagmat.STOCKREPORTOUT;
import it.giunti.apg.soap.anagmat.STOCKREPORTREQ;
import it.giunti.apg.soap.anagmat.STOCKREPORTRSP;

public class TestIf017003AnagmatMain {

	private static final Logger LOG = LoggerFactory.getLogger(TestIf017003AnagmatMain.class);

    private List<ArticleBean> abList = new ArrayList<>();
    
    
    public static void main(String[] args) {
    	TestIf017003AnagmatMain test = new TestIf017003AnagmatMain();
    	test.test("giuntitest", "giunti02");
    }
    
    public TestIf017003AnagmatMain() {
    	ArticleBean ab1 = new ArticleBean();
    	ab1.setCm("X0199W");
    	ab1.setCommittente("RG");
    	abList.add(ab1);
    	ArticleBean ab2 = new ArticleBean();
    	ab2.setCm("X1146W");
    	ab2.setCommittente("RG");
    	abList.add(ab2);
    }
    
	public void test(String wsUser, String wsPass) {
		LOG.info("WSDL: "+AnagmatSapService.WSDL_LOCATION);
		
		//Scorre tutti i cm e crea la tabella di input per SAP
		LOG.info("Chiamata SAP per verifica giacenze");
		List<STOCKREPORTREQ> tbInput = new ArrayList<STOCKREPORTREQ>();
		for (ArticleBean ab:abList) {
			STOCKREPORTREQ row = new STOCKREPORTREQ();
			row.setMATNR(ab.getCm());
			row.setTIPO(ab.getCommittente());
			tbInput.add(row);
		}

		
		//Chiamata funzione SAP
		AnagmatSapService wsService = new AnagmatSapService(AnagmatSapService.WSDL_LOCATION, AnagmatSapService.SERVICE);
        //INVOICEEOUT port = wsService.getHTTPSPort();// no HTTPS !!
		STOCKREPORTOUT port = wsService.getHTTPPort();
		
        Map<String, Object> reqCtx = ((BindingProvider)port).getRequestContext();
        LOG.info("Endpoint: "+reqCtx.get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY));
        Authenticator.setDefault(new S4Authenticator(wsUser, wsPass));
//        Map<String, List<String>> headers = new HashMap<String, List<String>>();
//        headers.put("Username", Collections.singletonList(wsUser));
//        headers.put("Password", Collections.singletonList(wsPass));
//        headers.put(BindingProvider.USERNAME_PROPERTY, Collections.singletonList(wsUser));
//        headers.put(BindingProvider.PASSWORD_PROPERTY, Collections.singletonList(wsPass));
//        reqCtx.put(MessageContext.HTTP_REQUEST_HEADERS, headers);
        
		LOG.info("Invoking stockREPORTOUT...");
		List<STOCKREPORTRSP> tbOutput = new ArrayList<STOCKREPORTRSP>();
		for (STOCKREPORTREQ row:tbInput) {
			STOCKREPORTRSP out = port.stockREPORTOUT(row);
			tbOutput.add(out);
		}
		LOG.info("stockREPORTOUT result=" + tbOutput);

		
		//Crea la mappa delle disponibilità e scrive log materiali
		LOG.info("Risposta SAP");
		for (STOCKREPORTRSP row:tbOutput) {
			for (STOCKREPORTRSP.RECORDSET rset:row.getRECORDSET()) {
				LOG.info("MATNR:"+rset.getMATNR()+
						" TIPO:"+rset.getTIPO()+
						" GIACENZA:"+rset.getGIACENZA());
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
