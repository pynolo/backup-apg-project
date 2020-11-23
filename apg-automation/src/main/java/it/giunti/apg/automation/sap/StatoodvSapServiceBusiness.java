package it.giunti.apg.automation.sap;

import java.net.Authenticator;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.VisualLogger;
import it.giunti.apg.core.persistence.EvasioniArticoliDao;
import it.giunti.apg.core.persistence.EvasioniFascicoliDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.EvasioniArticoli;
import it.giunti.apg.shared.model.EvasioniFascicoli;
import it.giunti.apg.shared.model.IEvasioni;
import it.giunti.apg.shared.model.OrdiniLogistica;
import it.giunti.apg.soap.statoodv.ORDERSTATUSOUT;
import it.giunti.apg.soap.statoodv.ORDERSTATUSREQ;
import it.giunti.apg.soap.statoodv.ORDERSTATUSRSP;

public class StatoodvSapServiceBusiness {

	private static final Logger LOG = LoggerFactory.getLogger(StatoodvSapServiceBusiness.class);
	
    public final static URL WSDL_LOCATION = new StatoodvSapServiceBusiness().getClass().getResource(StatoodvSapService.WSDL_FILE_LOCATION);
    
	public static final String ABGRU_BLOCCO_ABBONAMENTI = "BA";
	
	private static EvasioniFascicoliDao efDao = new EvasioniFascicoliDao();
	private static EvasioniArticoliDao edDao = new EvasioniArticoliDao();
	
	public static String verifyAndUpdateOrders(Session ses, String wsUser, String wsPass, 
			List<OrdiniLogistica> olList, Date today, int idRapporto)
			throws HibernateException, BusinessException {
		LOG.debug("WSDL: "+WSDL_LOCATION);
		
		String avviso = "";
		//Crea la tabella di input per SAP
		VisualLogger.get().addHtmlInfoLine(idRapporto, "Chiamata SAP per verifica ordini");
		List<ORDERSTATUSREQ> tbInput = new ArrayList<ORDERSTATUSREQ>();
		for (OrdiniLogistica ol:olList) {
			ORDERSTATUSREQ row = new ORDERSTATUSREQ();
			ORDERSTATUSREQ.RECORDSET recordset = new ORDERSTATUSREQ.RECORDSET();
			recordset.setBSTKD(ol.getNumeroOrdine());
			row.setRECORDSET(recordset);
			tbInput.add(row);
		}
		
		
		//Chiamata funzione SAP
		StatoodvSapService wsService = new StatoodvSapService(WSDL_LOCATION, StatoodvSapService.SERVICE);
        //INVOICEEOUT port = wsService.getHTTPSPort();// no HTTPS !!
		ORDERSTATUSOUT port = wsService.getHTTPPort();
		
        Map<String, Object> reqCtx = ((BindingProvider)port).getRequestContext();
        LOG.debug("Endpoint: "+reqCtx.get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY));
        Authenticator.setDefault(new S4Authenticator(wsUser, wsPass));
//        Map<String, List<String>> headers = new HashMap<String, List<String>>();
//        headers.put("Username", Collections.singletonList(wsUser));
//        headers.put("Password", Collections.singletonList(wsPass));
//        headers.put(BindingProvider.USERNAME_PROPERTY, Collections.singletonList(wsUser));
//        headers.put(BindingProvider.PASSWORD_PROPERTY, Collections.singletonList(wsPass));
//        reqCtx.put(MessageContext.HTTP_REQUEST_HEADERS, headers);
        
		LOG.debug("Invoking orderSTATUSOUT...");
		List<ORDERSTATUSRSP> tbOutput = new ArrayList<ORDERSTATUSRSP>();
		for (ORDERSTATUSREQ row:tbInput) {
			ORDERSTATUSRSP out = port.orderSTATUSOUT(row);
			tbOutput.add(out);
		}
		LOG.debug("orderSTATUSOUT result=" + tbOutput);

		
		//Riscontra ogni ordine con la risposta SAP
		VisualLogger.get().addHtmlInfoLine(idRapporto, "Analisi risposta SAP");
		for (OrdiniLogistica ol:olList) {
			Anagrafiche anag = GenericDao.findById(ses, Anagrafiche.class, ol.getIdAnagrafica());
			String logLine = "Ordine <b>["+ol.getNumeroOrdine()+"]</b> ";
			String nome = anag.getIndirizzoPrincipale().getCognomeRagioneSociale();
			if (anag.getIndirizzoPrincipale().getNome() != null) {
				if (anag.getIndirizzoPrincipale().getNome().length() > 0)
						nome = anag.getIndirizzoPrincipale().getNome()+" "+anag.getIndirizzoPrincipale().getCognomeRagioneSociale();
			}
			if (anag != null) logLine += " per <b>"+anag.getUid()+"</b> "+nome;
			VisualLogger.get().addHtmlInfoLine(idRapporto, logLine);
			avviso += verifyAndUpdateOrder(ses, ol, tbOutput, today, idRapporto);
		}
		return avviso;
	}
	
	//Scorre tutte le righe dei materiali evasi e modifica gli ordini e le evasioni su apg di conseguenza
	private static String verifyAndUpdateOrder(Session ses, OrdiniLogistica ol,
			List<ORDERSTATUSRSP> tbOutput, Date today, int idRapporto) throws HibernateException, BusinessException {
		int count = 0;//count order rows
		int countBA = 0;//count delivered rows
		String avviso = "";
		//Verifica fascicoli
		List<EvasioniFascicoli> efList = efDao.findByNumeroOrdine(ses, ol.getNumeroOrdine());
		for (EvasioniFascicoli ef:efList) {
			for (ORDERSTATUSRSP rsp:tbOutput) {
				List<ORDERSTATUSRSP.ORDERDATA> dataList = rsp.getORDERDATA();
				for (ORDERSTATUSRSP.ORDERDATA row:dataList) {
					if (ol.getNumeroOrdine().equals(row.getBSTKD().trim()) &&//Stesso ordine
							ef.getFascicolo().getCodiceMeccanografico().equals(row.getMATNR().trim())) {//Stesso materiale
						count++;
						boolean bloccoAbbonamenti = row.getABGRU().equals(ABGRU_BLOCCO_ABBONAMENTI);
						Integer menge = row.getMENGE().intValue();
						Integer evasa = row.getEVASA().intValue();
						if ((menge <= evasa) || bloccoAbbonamenti) {
							//This line must be closed if MENGE equals EVASA or ABGRU is 'BA'
							avviso += checkAndWriteEvasi(ses, ef, ol, row.getMATNR().trim(),
									menge, evasa, bloccoAbbonamenti, today, idRapporto);
							countBA++;
						} else {
							if (evasa == 0) {
								//Nothing has been sent
								VisualLogger.get().addHtmlInfoLine(idRapporto,
										"["+ol.getNumeroOrdine()+"] " +
										"Materiale <b>"+row.getMATNR()+"</b> IN ATTESA non evase: "+menge);
							} else {
								//Has been partially sent
								avviso += checkAndWriteEvasi(ses, ef, ol, row.getMATNR().trim(),
										menge, evasa, bloccoAbbonamenti, today, idRapporto);
							}
						}
					}
				}
			}
		}
		//Verifica articoli
		List<EvasioniArticoli> edList = edDao.findByNumeroOrdine(ses, ol.getNumeroOrdine());
		for (EvasioniArticoli ed:edList) {
			for (ORDERSTATUSRSP rsp:tbOutput) {
				List<ORDERSTATUSRSP.ORDERDATA> dataList = rsp.getORDERDATA();
				for (ORDERSTATUSRSP.ORDERDATA row:dataList) {
					if (ol.getNumeroOrdine().equals(row.getBSTKD().trim()) &&//Stesso ordine
							ed.getArticolo().getCodiceMeccanografico().equals(row.getMATNR().trim())) {//Stesso materiale
						count++;
						boolean bloccoAbbonamenti = row.getABGRU().equals(ABGRU_BLOCCO_ABBONAMENTI);
						Integer menge = row.getMENGE().intValue();
						Integer evasa = row.getEVASA().intValue();
						if ((menge <= evasa) || bloccoAbbonamenti) {
							//This line must be closed if MENGE equals EVASA or ABGRU is 'BA'
							avviso += checkAndWriteEvasi(ses, ed, ol, row.getMATNR().trim(),
									menge, evasa, bloccoAbbonamenti, today, idRapporto);
							countBA++;
						} else {
							if (evasa == 0) {
								//Nothing has been sent
								VisualLogger.get().addHtmlInfoLine(idRapporto, 
										"["+ol.getNumeroOrdine()+"] " +
										"Materiale <b>"+row.getMATNR()+"</b> IN ATTESA non evase: "+menge);
							} else {
								//Has been partially sent
								avviso += checkAndWriteEvasi(ses, ed, ol, row.getMATNR().trim(),
										menge, evasa, bloccoAbbonamenti, today, idRapporto);
							}
						}
					}
				}
			}
		}
		
		//se tutte le righe sono in blocco o completamente evase
		if (count == countBA) {
			markOrdineChiuso(ses, ol, today, idRapporto);
		}
		return avviso;
	}
	
	// Per righe evase totalmente o parzialmente (NON evase 0)
	private static String checkAndWriteEvasi(Session ses, IEvasioni eva,
			OrdiniLogistica ol, String cm,
			int copieSap, int copieEvase,
			boolean bloccoAbbonamenti, Date today, int idRapporto) throws HibernateException {
		//int countEvasi = 0;
		String avviso = "";
		//Analisi riga ordine del materiale
		if (copieSap != eva.getCopie()) {//Non c'e' corrispondenza tra le richieste
			replaceNote(ses, eva, "ERRORE su SAP risultano richieste "+copieSap+
					" copie a fronte di "+eva.getCopie()+" su APG ");
			VisualLogger.get().addHtmlInfoLine(idRapporto, "["+ol.getNumeroOrdine()+"] " +
					"Materiale <b>"+cm+"</b> <b>ERRORE</b> richieste APG:"+eva.getCopie()+
					" richieste SAP:"+copieSap);
			String message = "ANOMALIA Ord."+ol.getNumeroOrdine()+" mat."+cm+
					" richieste APG:"+eva.getCopie()+
					" richieste SAP:"+copieSap;
			LOG.error(message);
			avviso += message+"<br />";
		} else {//Le richieste coincidono
			//Verifica copie evase
			if (copieEvase > eva.getCopie()) {//evase più del richiesto
				replaceNote(ses, eva, "ERRORE su SAP risultano evase "+copieEvase+
						" copie a fronte di "+eva.getCopie()+" richieste ");
				VisualLogger.get().addHtmlInfoLine(idRapporto, "["+ol.getNumeroOrdine()+"] " +
						"Materiale <b>"+cm+"</b> <b>ERRORE</b> richieste APG:"+eva.getCopie()+
						" evase SAP:"+copieEvase);
				String message = "ANOMALIA Ord."+ol.getNumeroOrdine()+" mat."+cm+
						" richieste APG:"+eva.getCopie()+
						" evase SAP:"+copieEvase;
				LOG.error(message);
				avviso += message+"<br />";
				replaceQuantity(ses, eva, copieEvase);
				confirmEvasioni(ses, eva, today);
			} else {
				//copie evase <= richieste
				if (eva.getCopie() == 0) {
					//zero copie RICHIESTE! ordine eventualmente da annullare
					VisualLogger.get().addHtmlInfoLine(idRapporto,"["+ol.getNumeroOrdine()+"] " +
							"Materiale <b>"+cm+"</b> <b>ERRORE</b> richieste "+
							eva.getCopie()+" copie");
					String message = "ANOMALIA Ord."+ol.getNumeroOrdine()+" mat."+cm+
							" "+eva.getCopie()+" copie richieste";
					LOG.error(message);
					avviso += message+"<br />";
				} else {
					if (copieEvase == 0) {
						//Non fa nulla (condizione che non dovrebbe verificarsi in qs metodo)
						if (bloccoAbbonamenti) {
							VisualLogger.get().addHtmlInfoLine(idRapporto,
									"["+ol.getNumeroOrdine()+"] " +
									"Materiale <b>"+cm+"</b> evasione chiusa con 0 copie");
							detachEvasioneFromOrdine(ses, eva);
						} else {
							VisualLogger.get().addHtmlInfoLine(idRapporto,
									"["+ol.getNumeroOrdine()+"] " +
									"Materiale <b>"+cm+"</b> IN ATTESA");
						}
					} else {
						//Ci sono copie evase da verificare
						if (copieEvase < eva.getCopie()) {
							//Evase meno del richiesto
							if (bloccoAbbonamenti) {
								IEvasioni splitted = splitEvasioni(ses, eva, copieEvase, today);
								VisualLogger.get().addHtmlInfoLine(idRapporto,
										"["+ol.getNumeroOrdine()+"] " +
										"Materiale <b>"+cm+"</b> evasione chiusa con "+copieEvase+" copie");
								VisualLogger.get().addHtmlInfoLine(idRapporto,
										"["+ol.getNumeroOrdine()+"] " +
										"Materiale <b>"+cm+"</b> "+"creata nuova richiesta per "+
										splitted.getCopie()+" copie mancanti");
								//countEvasi++;
							} else {
								VisualLogger.get().addHtmlInfoLine(idRapporto,
										"["+ol.getNumeroOrdine()+"] " +
										"Materiale <b>"+cm+"</b> IN ATTESA evasione parziale "+copieEvase+
										"/"+eva.getCopie());
								replaceNote(ses, eva, copieEvase+" evase al "+ 
										ServerConstants.FORMAT_DAY.format(today)+" ");
							}
						} else {
							//Evase nella quantità giusta
							confirmEvasioni(ses, eva, today);
							VisualLogger.get().addHtmlInfoLine(idRapporto,
									"["+ol.getNumeroOrdine()+"] " +
									"Materiale <b>"+cm+"</b> evasione chiusa "+
									copieEvase+"/"+eva.getCopie());
							//countEvasi++;
						}
					}
				}
			}
		}
		return avviso;
	}
	
	private static IEvasioni splitEvasioni(Session ses, IEvasioni eva, Integer copieEvase,
			Date today) throws HibernateException {
		IEvasioni result = null;
		if (eva instanceof EvasioniFascicoli) {
			result = splitEvasioniFascicoli(ses, (EvasioniFascicoli)eva, copieEvase, today);
		}
		if (eva instanceof EvasioniArticoli) {
			result = splitEvasioniArticoli(ses, (EvasioniArticoli)eva, copieEvase, today);
		}
		return result;
	}
	
	private static void replaceQuantity(Session ses,
			IEvasioni eva, int copieEvase) throws HibernateException {
		eva.setCopie(copieEvase);
		if (eva instanceof EvasioniFascicoli) {
			efDao.update(ses,(EvasioniFascicoli)eva);
		}
		if (eva instanceof EvasioniArticoli) {
			edDao.update(ses,(EvasioniArticoli)eva);
		}
	}
	
	private static void replaceNote(Session ses,
			IEvasioni eva, String note) throws HibernateException {
		eva.setNote(note);
		if (eva.getNote().length() >= 255) eva.setNote(eva.getNote().substring(0,255));
		if (eva instanceof EvasioniFascicoli) {
			efDao.update(ses,(EvasioniFascicoli)eva);
		}
		if (eva instanceof EvasioniArticoli) {
			edDao.update(ses,(EvasioniArticoli)eva);
		}
	}
	
	private static void detachEvasioneFromOrdine(Session ses,
			IEvasioni eva) throws HibernateException {
		eva.setDataInvio(null);
		eva.setOrdiniLogistica(null);
		eva.setDataOrdine(null);
		if (eva instanceof EvasioniFascicoli) {
			efDao.update(ses, (EvasioniFascicoli)eva);
		}
		if (eva instanceof EvasioniArticoli) {
			edDao.update(ses,(EvasioniArticoli)eva);
		}
	}
	
	private static EvasioniFascicoli splitEvasioniFascicoli(Session ses, EvasioniFascicoli ef, Integer copieEvase,
			Date today) throws HibernateException {
		//Crea una EvasioneFascicoli con le copie non evase
		EvasioniFascicoli newEf = new EvasioniFascicoli();
		newEf.setCopie(ef.getCopie()-copieEvase);
		newEf.setDataCreazione(today);
		newEf.setDataInvio(null);
		newEf.setDataConfermaEvasione(null);
		newEf.setDataModifica(today);
		newEf.setDataOrdine(null);
		newEf.setFascicolo(ef.getFascicolo());
		newEf.setIdAbbonamento(ef.getIdAbbonamento());
		newEf.setIdAnagrafica(ef.getIdAnagrafica());
		newEf.setIdIstanzaAbbonamento(ef.getIdIstanzaAbbonamento());
		newEf.setIdTipoEvasione(ef.getIdTipoEvasione());
		newEf.setNote(null);
		newEf.setOrdiniLogistica(null);
		newEf.setIdUtente(ServerConstants.DEFAULT_SYSTEM_USER);
		efDao.save(ses, newEf);//Crea
		//Modifica l'EvasioneFascicoliEvasa
		ef.setNote("Copie evase "+copieEvase+" a fronte di "+ef.getCopie()+" richieste");
		ef.setCopie(copieEvase);
		ef.setDataConfermaEvasione(today);
		efDao.update(ses, ef);
		return newEf;
	}
	
	private static EvasioniArticoli splitEvasioniArticoli(Session ses, EvasioniArticoli ed, Integer copieEvase,
			Date today) throws HibernateException {
		//Crea una EvasioniArticoli con le copie non evase
		EvasioniArticoli newEd = new EvasioniArticoli();
		newEd.setCopie(ed.getCopie()-copieEvase);
		newEd.setDataCreazione(today);
		newEd.setDataInvio(null);
		newEd.setDataConfermaEvasione(null);
		newEd.setDataLimite(ed.getDataLimite());
		newEd.setDataModifica(today);
		newEd.setDataOrdine(null);
		newEd.setIdArticoloListino(ed.getIdArticoloListino());
		newEd.setIdArticoloOpzione(ed.getIdArticoloOpzione());
		newEd.setArticolo(ed.getArticolo());
		newEd.setDataAnnullamento(ed.getDataAnnullamento());
		newEd.setIdAbbonamento(ed.getIdAbbonamento());
		newEd.setIdAnagrafica(ed.getIdAnagrafica());
		newEd.setIdIstanzaAbbonamento(ed.getIdIstanzaAbbonamento());
		newEd.setIdTipoDestinatario(ed.getIdTipoDestinatario());
		newEd.setNote(null);
		newEd.setOrdiniLogistica(null);
		newEd.setPrenotazioneIstanzaFutura(ed.getPrenotazioneIstanzaFutura());
		newEd.setIdUtente(ServerConstants.DEFAULT_SYSTEM_USER);
		edDao.save(ses, newEd);//Crea
		//Modifica l'EvasioneDoni evasa
		ed.setNote("Copie evase "+copieEvase+" a fronte di "+ed.getCopie()+" richieste");
		ed.setCopie(copieEvase);
		ed.setDataConfermaEvasione(today);
		edDao.update(ses, ed);
		return newEd;
	}
	
	private static void confirmEvasioni(Session ses, IEvasioni eva, Date today) 
			 throws HibernateException {
		//eva.setDataInvio(today);
		eva.setDataConfermaEvasione(today);
		if (eva instanceof EvasioniFascicoli) {
			efDao.update(ses, (EvasioniFascicoli)eva);
		}
		if (eva instanceof EvasioniArticoli) {
			edDao.update(ses, (EvasioniArticoli)eva);
		}
	}
	
	private static void markOrdineChiuso(Session ses, OrdiniLogistica ol, Date today, int idRapporto)
			throws HibernateException {
		if (ol.getDataChiusura() == null) ol.setDataChiusura(today);
		ol.setDataRifiuto(null);
		ol.setNote(null);
		GenericDao.updateGeneric(ses, ol.getId(), ol);
		VisualLogger.get().addHtmlInfoLine(idRapporto, "["+ol.getNumeroOrdine()+"] " +
				"Ordine chiuso");
	}
}
