package it.giunti.apg.automation.sap;

import java.net.Authenticator;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.giunti.apg.automation.business.OrderBean;
import it.giunti.apg.automation.business.OrderRowBean;
import it.giunti.apg.core.VisualLogger;
import it.giunti.apg.core.business.CharsetUtil;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.EvasioniArticoli;
import it.giunti.apg.shared.model.EvasioniFascicoli;
import it.giunti.apg.shared.model.IEvasioni;
import it.giunti.apg.soap.creaodv.ORDEROUT;
import it.giunti.apg.soap.creaodv.ORDERREQ;
import it.giunti.apg.soap.creaodv.ORDERRSP;

public class CreaodvSapServiceBusiness {

	private static final Logger LOG = LoggerFactory.getLogger(CreaodvSapServiceBusiness.class);
	
    public final static URL WSDL_LOCATION = new CreaodvSapServiceBusiness().getClass().getResource(CreaodvSapService.WSDL_FILE_LOCATION);
    
	public static void sendAndModifyOrders(Session ses, String wsUser, String wsPass, 
			List<OrderBean> ordList, int idRapporto)
			throws BusinessException, HibernateException {
		LOG.debug("WSDL: "+WSDL_LOCATION);
		
		//Prepara la tabella di input per SAP
		VisualLogger.get().addHtmlInfoLine(idRapporto, "Chiamata SAP per inserimento ordini");
		ORDERREQ tbInput = new ORDERREQ();
		for (OrderBean bean:ordList) {
			String bstkd = bean.getOrdineLogistica().getNumeroOrdine(); //CHAR35 Ordine APG
			//Nome CHAR30
			String name = bean.getAnagrafica().getIndirizzoPrincipale().getCognomeRagioneSociale();
			if (bean.getAnagrafica().getIndirizzoPrincipale().getNome() != null) 
					name += " "+bean.getAnagrafica().getIndirizzoPrincipale().getNome();
			name = CharsetUtil.toSapAscii(name, 30);
			//Presso CHAR30
			String presso = "";
			if (bean.getAnagrafica().getIndirizzoPrincipale().getPresso() != null)
				presso = bean.getAnagrafica().getIndirizzoPrincipale().getPresso();
			presso = CharsetUtil.toSapAscii(presso, 30);
			//Via CHAR60
			String street = bean.getAnagrafica().getIndirizzoPrincipale().getIndirizzo();
			street = CharsetUtil.toSapAscii(street, 60);
			//Cap CHAR10
			String postCode = bean.getAnagrafica().getIndirizzoPrincipale().getCap(); 
			//Localita CHAR40
			String localita =  bean.getAnagrafica().getIndirizzoPrincipale().getLocalita();
			localita = CharsetUtil.toSapAscii(localita, 40);
			//Provincia CHAR3
			String provincia = bean.getAnagrafica().getIndirizzoPrincipale().getProvincia();
			if (provincia == null) provincia = "";
			//Nazione CHAR3
			String nazione = bean.getAnagrafica().getIndirizzoPrincipale().getNazione().getSiglaNazione();
			for (OrderRowBean orb:bean.getRowList()) {
				IEvasioni eva = orb.getEvasione();
				ORDERREQ.RECORDSET row = new ORDERREQ.RECORDSET();
				row.setBSTKD(bstkd);
				row.setMENGE(eva.getCopie().toString());
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
				if (eva instanceof EvasioniFascicoli) {
					EvasioniFascicoli ef = (EvasioniFascicoli) eva;
					row.setMATNR(ef.getFascicolo().getCodiceMeccanografico());
				}
				if (eva instanceof EvasioniArticoli) {
					EvasioniArticoli ed = (EvasioniArticoli) eva;
					row.setMATNR(ed.getArticolo().getCodiceMeccanografico());
				}
				row.setTIPO(orb.getCommittente());
				tbInput.getRECORDSET().add(row);
			}
		}
		if (tbInput.getRECORDSET().size() == 0) {
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Nessun materiale deve essere ordinato");
			return;
		}
		
		
		//Chiamata funzione SAP
		CreaodvSapService wsService = new CreaodvSapService(WSDL_LOCATION, CreaodvSapService.SERVICE);
        //INVOICEEOUT port = wsService.getHTTPSPort();// no HTTPS !!
		ORDEROUT port = wsService.getHTTPPort();
		
        Map<String, Object> reqCtx = ((BindingProvider)port).getRequestContext();
        LOG.debug("Endpoint: "+reqCtx.get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY));
        Authenticator.setDefault(new S4Authenticator(wsUser, wsPass));
//        Map<String, List<String>> headers = new HashMap<String, List<String>>();
//        headers.put("Username", Collections.singletonList(wsUser));
//        headers.put("Password", Collections.singletonList(wsPass));
//        headers.put(BindingProvider.USERNAME_PROPERTY, Collections.singletonList(wsUser));
//        headers.put(BindingProvider.PASSWORD_PROPERTY, Collections.singletonList(wsPass));
//        reqCtx.put(MessageContext.HTTP_REQUEST_HEADERS, headers);
        
		LOG.debug("Invoking orderOUT...");
		ORDERRSP tbOutput = port.orderOUT(tbInput);
		LOG.debug("stockREPORTOUT result=" + tbOutput);
		
		
		//Acquisisce il risultato dell'inserimento
		VisualLogger.get().addHtmlInfoLine(idRapporto, "Analisi risposta SAP");
		Date today = DateUtil.now();
		for (ORDERRSP.RECORDSET row:tbOutput.getRECORDSET()) {
			boolean errore = false;
			if (row.getERRORE() != null) errore = row.getERRORE().equals("X");
			if (errore) {
				cancelOrder(ses, ordList, row.getBSTKD(), row.getTESTO(), today);
				VisualLogger.get().addHtmlInfoLine(idRapporto, "[Ord."+row.getBSTKD()+"] <b>Annullato</b> errore SAP: "+row.getTESTO());
			} else {
				VisualLogger.get().addHtmlInfoLine(idRapporto, "[Ord."+row.getBSTKD()+"] <b>OK</b>");
			}
		}
	}
	
	private static void cancelOrder(Session ses, List<OrderBean> ordList,
			String numeroOrdine, String errorMsg, Date date) throws HibernateException {
		numeroOrdine = numeroOrdine.trim();
		for (OrderBean bean:ordList) {
			if (bean.getOrdineLogistica().getNumeroOrdine().equals(numeroOrdine)) {
				//Ordine trovato, ora tutte le righe (i materiali) vanno smarcate
				for (OrderRowBean orb:bean.getRowList()) {
					IEvasioni eva = orb.getEvasione();
					eva.setDataOrdine(null);
					eva.setOrdiniLogistica(null);
					GenericDao.updateGeneric(ses, eva.getId(), eva);
				}
			}
			bean.getOrdineLogistica().setDataRifiuto(date);
			bean.getOrdineLogistica().setDataChiusura(null);
			bean.getOrdineLogistica().setNote(errorMsg);
			GenericDao.updateGeneric(ses, bean.getOrdineLogistica().getId(), bean.getOrdineLogistica());
		}
	}
	
}
