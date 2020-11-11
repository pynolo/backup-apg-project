package it.giunti.apg.automation.sap;

import java.net.Authenticator;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
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
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.OrdiniLogisticaDao;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Articoli;
import it.giunti.apg.shared.model.EvasioniArticoli;
import it.giunti.apg.shared.model.EvasioniFascicoli;
import it.giunti.apg.shared.model.Fascicoli;
import it.giunti.apg.shared.model.IEvasioni;
import it.giunti.apg.soap.anagmat.STOCKREPORTOUT;
import it.giunti.apg.soap.anagmat.STOCKREPORTREQ;
import it.giunti.apg.soap.anagmat.STOCKREPORTRSP;

public class AnagmatSapServiceBusiness {

	private static final Logger LOG = LoggerFactory.getLogger(AnagmatSapService.class);
			
    public final static URL WSDL_LOCATION = new AnagmatSapServiceBusiness().getClass().getResource(AnagmatSapService.WSDL_FILE_LOCATION);
    
	public static void checkGiacenzaAndModifyOrders(Session ses, String wsUser, String wsPass, 
			List<OrderBean> ordList, int idRapporto)
			throws BusinessException, HibernateException {
		LOG.debug("WSDL: "+WSDL_LOCATION);
		
		//Aggrega i cm e calcolando copie e committente
		List<ArticleBean> articleList = createArticleList(ordList);
		//Scorre tutti i cm e crea la tabella di input per SAP
		VisualLogger.get().addHtmlInfoLine(idRapporto, "Chiamata SAP per verifica giacenze");
		List<STOCKREPORTREQ> tbInput = new ArrayList<STOCKREPORTREQ>();
		for (ArticleBean ab:articleList) {
			STOCKREPORTREQ row = new STOCKREPORTREQ();
			row.setMATNR(ab.getCm());
			row.setTIPO(ab.getCommittente());
			tbInput.add(row);
		}

		
		//Chiamata funzione SAP
		AnagmatSapService wsService = new AnagmatSapService(WSDL_LOCATION, AnagmatSapService.SERVICE);
        //INVOICEEOUT port = wsService.getHTTPSPort();// no HTTPS !!
		STOCKREPORTOUT port = wsService.getHTTPPort();
		
        Map<String, Object> reqCtx = ((BindingProvider)port).getRequestContext();
        LOG.debug("Endpoint: "+reqCtx.get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY));
        Authenticator.setDefault(new S4Authenticator(wsUser, wsPass));
//        Map<String, List<String>> headers = new HashMap<String, List<String>>();
//        headers.put("Username", Collections.singletonList(wsUser));
//        headers.put("Password", Collections.singletonList(wsPass));
//        headers.put(BindingProvider.USERNAME_PROPERTY, Collections.singletonList(wsUser));
//        headers.put(BindingProvider.PASSWORD_PROPERTY, Collections.singletonList(wsPass));
//        reqCtx.put(MessageContext.HTTP_REQUEST_HEADERS, headers);
        
		LOG.debug("Invoking stockREPORTOUT...");
		List<STOCKREPORTRSP> tbOutput = new ArrayList<STOCKREPORTRSP>();
		for (STOCKREPORTREQ row:tbInput) {
			STOCKREPORTRSP out = port.stockREPORTOUT(row);
			tbOutput.add(out);
		}
		LOG.debug("stockREPORTOUT result=" + tbOutput);

		
		//Crea la mappa delle disponibilità e scrive log materiali
		VisualLogger.get().addHtmlInfoLine(idRapporto, "Analisi risposta SAP");
		updateArticleList(idRapporto, articleList, tbOutput);
		//Rimodulazione ordini:
		//scorre i cm e se la giacenza è insufficiente rimodula
		VisualLogger.get().addHtmlInfoLine(idRapporto, "Rimodulazione ordini");
		int countMateriali = 0;
		for (ArticleBean ab:articleList) {
			if (ab.getCopieRichieste() > ab.getCopieDisponibili()) {
				//La giacenza non basta
				rimodulaUpdateOrdiniEvasioni(ses, ordList, ab.getCm(),
						ab.getCommittente(), ab.getCopieDisponibili(), idRapporto);
				countMateriali++;
			}
		}
		if (countMateriali > 0) { 
			VisualLogger.get().addHtmlInfoLine(idRapporto, 
					"Rimodulati gli ordini contenenti "+countMateriali+" materiali");
		} else {
			VisualLogger.get().addHtmlInfoLine(idRapporto, 
					"Nessun ordine e' stato rimodulato");
		}
		//Elimina ordini su DB se sono vuoti e li rimuove dalla lista ordList.
		deleteOrdiniVuoti(ses, ordList, idRapporto);
	}
	
	private static void rimodulaUpdateOrdiniEvasioni(Session ses, List<OrderBean> ordList,
			String cm, String committente, Integer giacenza, int idRapporto) throws HibernateException {
		int ordCount = 0;
		int assigned = 0;
		String codiciClienti = "";
		for (OrderBean bean:ordList) {
			List<OrderRowBean> newRowList = new ArrayList<OrderRowBean>();
			for (OrderRowBean orb:bean.getRowList()) {
				IEvasioni eva = orb.getEvasione();
				//EvasioniFascicoli
				if (eva instanceof EvasioniFascicoli) {
					EvasioniFascicoli ef = (EvasioniFascicoli) eva;
					
					if (ef.getFascicolo().getCodiceMeccanografico().equals(cm) &&
							orb.getCommittente().equals(committente)) {
						//L'ordine contiene il CM
						if (ef.getCopie()+assigned > giacenza) {
							ordCount++;
							codiciClienti += bean.getAnagrafica().getUid()+" ";
							//Rimuove il riferimento all'ordine se la giacenza non basta
							ef.setDataOrdine(null);
							ef.setOrdiniLogistica(null);
							GenericDao.updateGeneric(ses, ef.getId(), ef);
							//...e non aggiunge al nuovo elenco evasioni nell'ordine
						} else {
							assigned += ef.getCopie();
							OrderRowBean newOrb = new OrderRowBean(ef, committente);
							newRowList.add(newOrb);
						}
					} else {
						newRowList.add(orb);
					}
				}
				//EvasioniArticoli
				if (eva instanceof EvasioniArticoli) {
					EvasioniArticoli ed = (EvasioniArticoli) eva;
					if (ed.getArticolo().getCodiceMeccanografico().equals(cm) &&
							orb.getCommittente().equals(committente)) {
						//L'ordine contiene il CM
						if (ed.getCopie()+assigned > giacenza) {
							ordCount++;
							codiciClienti += bean.getAnagrafica().getUid()+" ";
							//Rimuove il riferimento all'ordine se la giacenza non basta
							ed.setDataOrdine(null);
							ed.setOrdiniLogistica(null);
							GenericDao.updateGeneric(ses, ed.getId(), ed);
							//...e non aggiunge al nuovo elenco evasioni nell'ordine
						} else {
							assigned += ed.getCopie();
							OrderRowBean newOrb = new OrderRowBean(ed, committente);
							newRowList.add(newOrb);
						}
					} else {
						newRowList.add(orb);
					}
				}
			}
			bean.setRowList(newRowList);//sostituisce l'elenco evasioni
		}
		if (ordCount > 0) VisualLogger.get().addHtmlInfoLine(idRapporto, "Materiale <b>"+cm+
				"</b> rimosso da "+ordCount+" ordini. Anagrafiche: <b>"+codiciClienti+"</b>");
	}
	
	private static void deleteOrdiniVuoti(Session ses, List<OrderBean> ordList, int idRapporto) 
			 throws HibernateException {
		int ordCount = 0;
		List<OrderBean> newOrdList = new ArrayList<OrderBean>();
		for (OrderBean bean:ordList) {
			boolean cancelled = true;
			for (OrderRowBean orb:bean.getRowList()) {
				if (orb.getEvasione().getOrdiniLogistica() != null) cancelled=false;
			}
			if (cancelled) {
				ordCount++;
				new OrdiniLogisticaDao().delete(ses, bean.getOrdineLogistica());
			} else {
				newOrdList.add(bean);
			}
		}
		//Sostituisce la lista con quella privata dagli eliminati
		ordList.clear();
		ordList.addAll(newOrdList);
		if (ordCount > 0) VisualLogger.get().addHtmlInfoLine(idRapporto, "Eliminati "+ordCount+
				" ordini vuoti. "+ordList.size()+" ordini restanti.");
	}
	
	private static List<ArticleBean> createArticleList(List<OrderBean> ordList) {
		Map<String, ArticleBean> articleMap = new HashMap<String, ArticleBean>();
		//Aggrega evasioni per cm e committente
		for (OrderBean bean:ordList) {
			for (OrderRowBean orb:bean.getRowList()) {
				IEvasioni eva = orb.getEvasione();
				String cm = null;
				if (eva instanceof EvasioniFascicoli) {
					Fascicoli fas = ((EvasioniFascicoli) eva).getFascicolo();
					cm = fas.getCodiceMeccanografico();
				}
				if (eva instanceof EvasioniArticoli) {
					Articoli dono = ((EvasioniArticoli) eva).getArticolo();
					cm = dono.getCodiceMeccanografico();
				}
				String key = cm+"-"+orb.getCommittente();
				ArticleBean article = articleMap.get(key);
				if (article == null) {//non è stato ancora inserito
					article = new ArticleBean();
					article.setCm(cm);
					article.setCommittente(orb.getCommittente());
					article.setCopieRichieste(0);
				}
				article.setCopieRichieste(article.getCopieRichieste()+eva.getCopie());
				articleMap.put(key, article);
			}
		}
		//Crea la lista di articoli
		List<ArticleBean> result = new ArrayList<AnagmatSapServiceBusiness.ArticleBean>();
		result.addAll(articleMap.values());
		return result;
	}
	
	private static void updateArticleList(int idRapporto, List<ArticleBean> articleList, 
			List<STOCKREPORTRSP> tbOutput) {
		//Mette le risposte di sap in una mappa
		Map<String, STOCKREPORTRSP.RECORDSET> rowMap = new HashMap<String, STOCKREPORTRSP.RECORDSET>();
		for (STOCKREPORTRSP row:tbOutput) {
			for (STOCKREPORTRSP.RECORDSET rset:row.getRECORDSET()) {
				String key = rset.getMATNR()+"-"+rset.getTIPO();
				rowMap.put(key, rset);
			}
		}
		//Scorre tutti gli articoli e li confronta con la mappa
		for (ArticleBean ab:articleList) {
			String key = ab.getCm()+"-"+ab.getCommittente();
			STOCKREPORTRSP.RECORDSET row = rowMap.get(key);
			if (row == null) {
				ab.setCopieDisponibili(0);
				VisualLogger.get().addHtmlInfoLine(idRapporto, "Materiale <b>"+ab.getCm()+
						"</b>["+ab.getCommittente()+"] NON PRESENTE NELLA RISPOSTA");
			} else {
				boolean bloccato = true;
				if (row.isBLOCCATO() == null) {
					bloccato = false;
				} else {
					bloccato = row.isBLOCCATO();
				}
				if (bloccato) {
					ab.setCopieDisponibili(0);
					VisualLogger.get().addHtmlInfoLine(idRapporto, "Materiale <b>"+ab.getCm()+
							"</b>["+ab.getCommittente()+"] BLOCCATO");
					//Se è bloccato non va nella mappa giacenze
				} else {
					ab.setCopieDisponibili(row.getGIACENZA().intValue());
					if (ab.getCopieRichieste() > ab.getCopieDisponibili()) {
						VisualLogger.get().addHtmlInfoLine(idRapporto, "Materiale <b>"+ab.getCm()+
								"</b>["+ab.getCommittente()+"] " +
								"Giacenza:"+ab.getCopieDisponibili()+
								" < Richiesta:"+ab.getCopieRichieste());
					} else {
						VisualLogger.get().addHtmlInfoLine(idRapporto, "Materiale <b>"+ab.getCm()+
								"</b>["+ab.getCommittente()+"] " +
								"OK "+ab.getCopieRichieste()+" copie");
					}
				}
			}
		}
	}
	
	
	
	
	// Inner Classes
	
	
	
	public static class ArticleBean {
		private String cm = null;
		private String committente = null;
		private Integer copieRichieste = null;
		private Integer copieDisponibili = null;
		
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
		public Integer getCopieRichieste() {
			return copieRichieste;
		}
		public void setCopieRichieste(Integer copieRichieste) {
			this.copieRichieste = copieRichieste;
		}
		public Integer getCopieDisponibili() {
			return copieDisponibili;
		}
		public void setCopieDisponibili(Integer copieDisponibili) {
			this.copieDisponibili = copieDisponibili;
		}
		
	}
}
