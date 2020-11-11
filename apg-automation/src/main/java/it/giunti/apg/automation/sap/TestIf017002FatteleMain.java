package it.giunti.apg.automation.sap;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Authenticator;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.BindingProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.giunti.apg.soap.fattele.INVOICEEOUT;
import it.giunti.apg.soap.fattele.INVOICEEREQ;
import it.giunti.apg.soap.fattele.INVOICEERSP;

public class TestIf017002FatteleMain {

	private static final Logger LOG = LoggerFactory.getLogger(TestIf017002FatteleMain.class);
	
    public static void main(String[] args) {
    	TestIf017002FatteleMain test = new TestIf017002FatteleMain();
    	try {
    		test.test("giuntitest", "giunti02");
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
    }
    
    public TestIf017002FatteleMain() {
    }
    
	public void test(String wsUser, String wsPass) throws DatatypeConfigurationException {
		LOG.info("WSDL: "+FatteleSapService.WSDL_LOCATION);
		
		//Input tables
		List<INVOICEEREQ.FATTELE.ZFATTELHEAD> headList = new ArrayList<INVOICEEREQ.FATTELE.ZFATTELHEAD>();
		List<INVOICEEREQ.FATTELE.ZFATTELITEM> itemList = new ArrayList<INVOICEEREQ.FATTELE.ZFATTELITEM>();

		//HEAD
		INVOICEEREQ.FATTELE.ZFATTELHEAD head = new INVOICEEREQ.FATTELE.ZFATTELHEAD();
		head.setBUKRS("001");
		head.setSEQUENZIALE("FXE");
		head.setBELNR("FXE0000015");
		BigInteger annoFatturaBig = new BigInteger("2020");
		head.setGJAHR(annoFatturaBig);
		head.setVBTYP("5");
		head.setWAERS("EUR");
		GregorianCalendar dataFatturaCalendar = new GregorianCalendar();
		dataFatturaCalendar.set(2020, 8, 2);
		XMLGregorianCalendar dataFatturaXml = DatatypeFactory.newInstance().newXMLGregorianCalendar(dataFatturaCalendar);
		head.setBLDAT(dataFatturaXml);
		head.setCOUNTRY("IT");
		head.setMODPAG("MP99");
		head.setDESTCODE("0000000");
		//head.setDESTPEC(CharsetUtil.toSapAscii(pec, 241));
		//head.setKUNRGSTCEG(CharsetUtil.toSapAscii(partitaIva, 20));
		head.setKUNRGSTCD1("PNCMNC59C62D458Z");
		head.setKUNRGNAME("PIANCASTELLI MONICA");
		head.setKUNRGSTREET("VIA DUCA D'AOSTA 15/A");
		head.setKUNRGPOSTCODE1("48121");
		head.setKUNRGCITY1("RAVENNA");
		head.setKUNRGCOUNTRY("IT");
		BigDecimal totaleFinale = new BigDecimal("26.40");
		head.setTOTALEDOC(totaleFinale);
		BigDecimal totaleImponibile = new BigDecimal("26.40");
		head.setTOTIMP(totaleImponibile);
		head.setZFBDT(dataFatturaXml);
		headList.add(head);
		//Articoli
		
		//for (FattureArticoli fa:faList) {
			//ITEM
			INVOICEEREQ.FATTELE.ZFATTELITEM item = new INVOICEEREQ.FATTELE.ZFATTELITEM();
			item.setBUKRS(head.getBUKRS());
			item.setSEQUENZIALE(head.getSEQUENZIALE());
			item.setBELNR(head.getBELNR());
			BigInteger posnrBig = new BigInteger("1");
			item.setPOSNR(posnrBig);
			item.setGJAHR(head.getGJAHR());
			//item.ean11
			//item.ordCodTipo
			//item.ordCodValore
			item.setTESTOVBBP("QUOTA ABBONAMENTO A 'ARCHEOLOGIA VIVA' 07/2019 - 06/2020  CODICE ABBONATO W009628");
			BigDecimal quantitaBig = new BigDecimal("1");
			item.setFKIMG(quantitaBig);
			BigDecimal importoImpTotBig = new BigDecimal("26.40");
			item.setKZWI1(importoImpTotBig);
			item.setCODIVA("VA");
			item.setALIQIVA("0");
			item.setIMPIVA(importoImpTotBig);
			BigDecimal impostaIvaBig = importoImpTotBig;
			item.setIMPOSTAIVA(impostaIvaBig);
			itemList.add(item);
		//}
		
		//Creazione struttura dati SAP: invoiceEREQ
		INVOICEEREQ.FATTELE fatturaElettronica = new INVOICEEREQ.FATTELE();
		fatturaElettronica.setZFATTELHEAD(head);
		fatturaElettronica.getZFATTELITEM().addAll(itemList);
		INVOICEEREQ invoiceEREQ = new INVOICEEREQ();
		invoiceEREQ.getFATTELE().add(fatturaElettronica);
		
		//Chiamata funzione SAP
		FatteleSapService wsService = new FatteleSapService(FatteleSapService.WSDL_LOCATION, FatteleSapService.SERVICE);
        //INVOICEEOUT port = wsService.getHTTPSPort();// no HTTPS !!
		INVOICEEOUT port = wsService.getHTTPPort();
		
        Map<String, Object> reqCtx = ((BindingProvider)port).getRequestContext();
        LOG.info("Endpoint: "+reqCtx.get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY));
        Authenticator.setDefault(new S4Authenticator(wsUser, wsPass));
//        Map<String, List<String>> headers = new HashMap<String, List<String>>();
//        headers.put("Username", Collections.singletonList(wsUser));
//        headers.put("Password", Collections.singletonList(wsPass));
//        headers.put(BindingProvider.USERNAME_PROPERTY, Collections.singletonList(wsUser));
//        headers.put(BindingProvider.PASSWORD_PROPERTY, Collections.singletonList(wsPass));
//        reqCtx.put(MessageContext.HTTP_REQUEST_HEADERS, headers);
        
		LOG.info("Invoking invoiceEOUT...");
		INVOICEERSP out = port.invoiceEOUT(invoiceEREQ);
		List<INVOICEERSP.ZFATTELERRS> errList = out.getZFATTELERRS();
		LOG.info("invoiceEOUT result=" + out);
		
		
		//Acquisisce il risultato dell'inserimento
		if (errList == null) errList = new ArrayList<INVOICEERSP.ZFATTELERRS>();
		if (errList.size() > 0) {
			for (INVOICEERSP.ZFATTELERRS err:errList) {
				LOG.info("Errore TAB:"+err.getTABNAME()+
						" LINE:"+err.getLINE()+
						" FIELDNAME:"+err.getFIELDNAME()+
						" MESSAGE:"+err.getMESSAGE());
			}
		} else {
			LOG.info("Inserimento fattura OK");
		}
	}
}
