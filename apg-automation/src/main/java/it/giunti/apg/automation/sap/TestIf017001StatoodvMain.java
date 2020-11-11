package it.giunti.apg.automation.sap;

import java.net.Authenticator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.giunti.apg.soap.statoodv.ORDERSTATUSOUT;
import it.giunti.apg.soap.statoodv.ORDERSTATUSREQ;
import it.giunti.apg.soap.statoodv.ORDERSTATUSRSP;

public class TestIf017001StatoodvMain {

	private static final Logger LOG = LoggerFactory.getLogger(TestIf017001StatoodvMain.class);
    
    public static void main(String[] args) {
    	TestIf017001StatoodvMain test = new TestIf017001StatoodvMain();
    	test.test("giuntitest", "giunti02");
    }
    
    public TestIf017001StatoodvMain() {
    }
    
	public void test(String wsUser, String wsPass) {
		LOG.info("WSDL: "+StatoodvSapService.WSDL_LOCATION);
		
		//Crea la tabella di input per SAP
		LOG.info("Chiamata SAP per verifica ordini");
		List<ORDERSTATUSREQ> tbInput = new ArrayList<ORDERSTATUSREQ>();

		ORDERSTATUSREQ req = new ORDERSTATUSREQ();
		ORDERSTATUSREQ.RECORDSET recordset = new ORDERSTATUSREQ.RECORDSET();
		recordset.setBSTKD("0008E7F");
		req.setRECORDSET(recordset);
		tbInput.add(req);
		
		//Chiamata funzione SAP
		StatoodvSapService wsService = new StatoodvSapService(StatoodvSapService.WSDL_LOCATION, StatoodvSapService.SERVICE);
        //INVOICEEOUT port = wsService.getHTTPSPort();// no HTTPS !!
		ORDERSTATUSOUT port = wsService.getHTTPPort();
		
        Map<String, Object> reqCtx = ((BindingProvider)port).getRequestContext();
        LOG.info("Endpoint: "+reqCtx.get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY));
        Authenticator.setDefault(new S4Authenticator(wsUser, wsPass));
//        Map<String, List<String>> headers = new HashMap<String, List<String>>();
//        headers.put("Username", Collections.singletonList(wsUser));
//        headers.put("Password", Collections.singletonList(wsPass));
//        headers.put(BindingProvider.USERNAME_PROPERTY, Collections.singletonList(wsUser));
//        headers.put(BindingProvider.PASSWORD_PROPERTY, Collections.singletonList(wsPass));
//        reqCtx.put(MessageContext.HTTP_REQUEST_HEADERS, headers);
        
		LOG.info("Invoking orderSTATUSOUT...");
		List<ORDERSTATUSRSP> tbOutput = new ArrayList<ORDERSTATUSRSP>();
		for (ORDERSTATUSREQ row:tbInput) {
			ORDERSTATUSRSP out = port.orderSTATUSOUT(row);
			tbOutput.add(out);
		}
		LOG.info("orderSTATUSOUT result=" + tbOutput);

		
		//Riscontra ogni ordine con la risposta SAP
		LOG.info("Analisi risposta SAP");
		for (ORDERSTATUSRSP rsp:tbOutput) {
			List<ORDERSTATUSRSP.ORDERDATA> dataList = rsp.getORDERDATA();
			for (ORDERSTATUSRSP.ORDERDATA row:dataList) {
				LOG.info("ABGRU:"+row.getABGRU()+
						" BSTKD:"+row.getBSTKD()+
						" MATNR:"+row.getMATNR()+
						" EVASA:"+row.getEVASA()+
						" MENGE:"+row.getMENGE());
			}
		}
		
	}
	
}
