package it.giunti.apg.automation.sap;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Authenticator;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.BindingProvider;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.giunti.apg.core.business.CharsetUtil;
import it.giunti.apg.core.persistence.FattureArticoliDao;
import it.giunti.apg.core.persistence.FattureInvioSapDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.Fatture;
import it.giunti.apg.shared.model.FattureArticoli;
import it.giunti.apg.shared.model.FattureInvioSap;
import it.giunti.apg.shared.model.Societa;
import it.giunti.apg.soap.fattele.INVOICEEOUT;
import it.giunti.apg.soap.fattele.INVOICEEREQ;
import it.giunti.apg.soap.fattele.INVOICEERSP;

public class FatteleSapServiceBusiness {
	
	private static final Logger LOG = LoggerFactory.getLogger(FatteleSapServiceBusiness.class);
	
    public final static URL WSDL_LOCATION = new FatteleSapServiceBusiness().getClass().getResource(FatteleSapService.WSDL_FILE_LOCATION);
    
	private static FattureArticoliDao faDao = new FattureArticoliDao();
	private static FattureInvioSapDao fisDao = new FattureInvioSapDao();
	
	public static List<INVOICEERSP.ZFATTELERRS> sendFattura(Session ses, 
			String wsUser, String wsPass, Fatture fatt, int idInvio) 
					throws HibernateException, BusinessException, DatatypeConfigurationException {
		LOG.debug("WSDL: "+WSDL_LOCATION);
		
		//Input tables
		List<INVOICEEREQ.FATTELE.ZFATTELHEAD> headList = new ArrayList<INVOICEEREQ.FATTELE.ZFATTELHEAD>();
		List<INVOICEEREQ.FATTELE.ZFATTELITEM> itemList = new ArrayList<INVOICEEREQ.FATTELE.ZFATTELITEM>();

		//HEAD
		INVOICEEREQ.FATTELE.ZFATTELHEAD head = new INVOICEEREQ.FATTELE.ZFATTELHEAD();
		Societa societa = GenericDao.findById(ses, Societa.class, fatt.getIdSocieta());
		head.setBUKRS(CharsetUtil.toSapAscii(societa.getCodiceSocieta(), 4));
		head.setSEQUENZIALE(CharsetUtil.toSapAscii(fatt.getNumeroFattura().substring(0, 3), 5));
		head.setBELNR(CharsetUtil.toSapAscii(fatt.getNumeroFattura(), 10));
		GregorianCalendar dataFatturaCalendar = new GregorianCalendar();
		dataFatturaCalendar.setTime(fatt.getDataFattura());
		BigInteger annoFatturaBig = new BigInteger(""+dataFatturaCalendar.get(Calendar.YEAR));
		head.setGJAHR(annoFatturaBig);
		String tipoDocumento = "5"; // 5 => AppConstants.DOCUMENTO_FATTURA
		if (fatt.getIdTipoDocumento() != null) {
			if (fatt.getIdTipoDocumento().equals(AppConstants.DOCUMENTO_NOTA_CREDITO))
					tipoDocumento = "6"; // 6 => AppConstants.DOCUMENTO_NOTA_CREDITO
		}
		head.setVBTYP(CharsetUtil.toSapAscii(tipoDocumento, 1));
		head.setWAERS(CharsetUtil.toSapAscii("EUR", 5));
		XMLGregorianCalendar dataFatturaXml = DatatypeFactory.newInstance().newXMLGregorianCalendar(dataFatturaCalendar);
		head.setBLDAT(dataFatturaXml);
		head.setCOUNTRY(CharsetUtil.toSapAscii("IT", 3));
		head.setMODPAG(CharsetUtil.toSapAscii("MP99", 4));
		String destCode = fatt.getCodiceDestinatario();
		if (destCode == null) destCode = "0000000";
		if (destCode.length() == 0) destCode = "0000000";
		if (!fatt.getNazione().getSiglaNazione().equals("IT")) destCode = "XXXXXXX";
		head.setDESTCODE(CharsetUtil.toSapAscii(destCode, 10));
		String pec = null;
		if (fatt.getEmailPec() != null) {
			if (fatt.getEmailPec().length() > 0) pec = fatt.getEmailPec();
		}
		head.setDESTPEC(CharsetUtil.toSapAscii(pec, 241));
		String partitaIva = null;
		if (fatt.getPartitaIva() != null) {
			if (fatt.getPartitaIva().length() > 0) partitaIva = "IT"+fatt.getPartitaIva();
		}
		head.setKUNRGSTCEG(CharsetUtil.toSapAscii(partitaIva, 20));
		String codFisc = null;
		if (fatt.getCodiceFiscale() != null) {
			if (fatt.getCodiceFiscale().length() > 0) codFisc = fatt.getCodiceFiscale();
		}
		head.setKUNRGSTCD1(CharsetUtil.toSapAscii(codFisc, 20));
		String nome = fatt.getCognomeRagioneSociale();
		if (fatt.getNome() != null) {
			if (fatt.getNome().length() > 0) nome += " "+fatt.getNome();
		}
		head.setKUNRGNAME(CharsetUtil.toSapAscii(nome, 70));
		head.setKUNRGSTREET(CharsetUtil.toSapAscii(fatt.getIndirizzo(), 60));
		String cap = "00000";
		if (fatt.getCap() != null) {
			if (fatt.getCap().length() > 0) cap = fatt.getCap();
		}
		head.setKUNRGPOSTCODE1(CharsetUtil.toSapAscii(cap, 10));
		head.setKUNRGCITY1(CharsetUtil.toSapAscii(fatt.getLocalita(), 40));
		head.setKUNRGCOUNTRY(CharsetUtil.toSapAscii(fatt.getNazione().getSiglaNazione(), 3));
		BigDecimal totaleFinale = new BigDecimal(fatt.getTotaleFinale());
		head.setTOTALEDOC(totaleFinale);
		BigDecimal totaleImponibile = new BigDecimal(fatt.getTotaleImponibile());
		head.setTOTIMP(totaleImponibile);
		head.setZFBDT(dataFatturaXml);//fatt.getDataFattura());
		headList.add(head);
		//Articoli
		List<FattureArticoli> faList = faDao.findByFattura(ses, fatt.getId());
		int posnr = 1;
		for (FattureArticoli fa:faList) {
			//ITEM
			INVOICEEREQ.FATTELE.ZFATTELITEM item = new INVOICEEREQ.FATTELE.ZFATTELITEM();
			item.setBUKRS(head.getBUKRS());
			item.setSEQUENZIALE(head.getSEQUENZIALE());
			item.setBELNR(head.getBELNR());
			BigInteger posnrBig = new BigInteger(""+posnr);
			item.setPOSNR(posnrBig);
			item.setGJAHR(head.getGJAHR());
			//item.ean11
			//item.ordCodTipo
			//item.ordCodValore
			item.setTESTOVBBP(CharsetUtil.toSapAscii(fa.getDescrizione(), 255));
			BigDecimal quantitaBig = new BigDecimal(fa.getQuantita());
			item.setFKIMG(quantitaBig);
			BigDecimal importoImpTotBig = new BigDecimal(fa.getImportoImpUnit()*fa.getQuantita());
			item.setKZWI1(importoImpTotBig);
			String codIva;
			try {
				codIva = ValueUtil.getCodiceIva(fa.getAliquotaIva(), fatt.getTipoIva());
			} catch (BusinessException e) {
				throw new BusinessException("Fattura "+fatt.getNumeroFattura()+": "+e.getMessage());
			}
			item.setCODIVA(CharsetUtil.toSapAscii(codIva, 2));
			Integer aliquota = new Double(Math.round(fa.getAliquotaIva().getValore()*100)).intValue();
			item.setALIQIVA(CharsetUtil.toSapAscii(""+aliquota, 13));
			item.setIMPIVA(importoImpTotBig);//fa.getImportoImpUnit()*fa.getQuantita());
			//Imposta Iva deve essere null se = 0 altrimenti SAP la rifiuta
			Double impostaIva = (fa.getImportoTotUnit()-fa.getImportoImpUnit())*fa.getQuantita();
			BigDecimal impostaIvaBig = null;
			if (impostaIva > 0) impostaIvaBig = new BigDecimal(impostaIva);
			item.setIMPOSTAIVA(impostaIvaBig);//(impostaIva > 0) ? impostaIva : null);
			itemList.add(item);
			posnr++;
		}
			
		if (headList.size() == 0 || itemList.size() == 0) {
			//Niente da inviare
			return null;
		}
		
		
		//Creazione struttura dati SAP: invoiceEREQ
		INVOICEEREQ.FATTELE fatturaElettronica = new INVOICEEREQ.FATTELE();
		fatturaElettronica.setZFATTELHEAD(head);
		fatturaElettronica.getZFATTELITEM().addAll(itemList);
		INVOICEEREQ invoiceEREQ = new INVOICEEREQ();
		invoiceEREQ.getFATTELE().add(fatturaElettronica);
		
		//Chiamata funzione SAP
		FatteleSapService wsService = new FatteleSapService(WSDL_LOCATION, FatteleSapService.SERVICE);
        //INVOICEEOUT port = wsService.getHTTPSPort();// no HTTPS !!
		INVOICEEOUT port = wsService.getHTTPPort();
		
        Map<String, Object> reqCtx = ((BindingProvider)port).getRequestContext();
        LOG.debug("Endpoint: "+reqCtx.get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY));
        Authenticator.setDefault(new S4Authenticator(wsUser, wsPass));
//        Map<String, List<String>> headers = new HashMap<String, List<String>>();
//        headers.put("Username", Collections.singletonList(wsUser));
//        headers.put("Password", Collections.singletonList(wsPass));
//        headers.put(BindingProvider.USERNAME_PROPERTY, Collections.singletonList(wsUser));
//        headers.put(BindingProvider.PASSWORD_PROPERTY, Collections.singletonList(wsPass));
//        reqCtx.put(MessageContext.HTTP_REQUEST_HEADERS, headers);
        
		LOG.debug("Invoking invoiceEOUT...");
		INVOICEERSP out = port.invoiceEOUT(invoiceEREQ);
		List<INVOICEERSP.ZFATTELERRS> errList = out.getZFATTELERRS();
		LOG.debug("invoiceEOUT result=" + out);
		
		
		//Acquisisce il risultato dell'inserimento
		if (errList == null) errList = new ArrayList<INVOICEERSP.ZFATTELERRS>();
		if (errList.size() > 0) {
			for (INVOICEERSP.ZFATTELERRS err:errList) {
				createFattureInvioError(ses, idInvio, err, fatt);
			}
		} else {
			createFattureInvioOk(ses, idInvio, fatt);
		}
		return errList;
	}
	
	private static void createFattureInvioError(Session ses, Integer idInvio,
			INVOICEERSP.ZFATTELERRS err, Fatture fatt) {
		FattureInvioSap fis = new FattureInvioSap();
		fis.setDataCreazione(new Date());
		fis.setErrField(err.getFIELDNAME());
		fis.setErrMessage(err.getMESSAGE());
		fis.setErrTable(err.getTABNAME());
		fis.setIdFattura(fatt.getId());
		fis.setIdInvio(idInvio);
		fis.setNumeroFattura(fatt.getNumeroFattura());
		fisDao.save(ses, fis);
	}
	
	private static void createFattureInvioOk(Session ses, Integer idInvio, Fatture fatt) {
		FattureInvioSap fis = new FattureInvioSap();
		fis.setDataCreazione(new Date());
		fis.setErrField(null);
		fis.setErrMessage(null);
		fis.setErrTable(null);
		fis.setIdFattura(fatt.getId());
		fis.setIdInvio(idInvio);
		fis.setNumeroFattura(fatt.getNumeroFattura());
		fisDao.save(ses, fis);
	}
}
