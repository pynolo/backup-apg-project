package it.giunti.apg.automation.sap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.conn.jco.JCoDestination;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.VisualLogger;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.MaterialiSpedizioneDao;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.MaterialiSpedizione;
import it.giunti.apg.shared.model.OrdiniLogistica;

public class ZrfcApgOrdiniEvasiBusiness {

	static private Logger LOG = LoggerFactory.getLogger(ZrfcApgOrdiniEvasiBusiness.class);
	private static MaterialiSpedizioneDao msDao = new MaterialiSpedizioneDao();
	
	public static String verifyAndUpdateOrders(Session ses, 
			JCoDestination sapDestination, List<OrdiniLogistica> olList,
			Date expirationDate, Date today, int idRapporto)
			throws HibernateException, BusinessException {
		String avviso = "";
		//Crea la tabella di input per SAP
		VisualLogger.get().addHtmlInfoLine(idRapporto, "Chiamata SAP per verifica ordini");
		List<ZrfcApgOrdiniEvasi.InputRow> tbInput = new ArrayList<ZrfcApgOrdiniEvasi.InputRow>();
		for (OrdiniLogistica ol:olList) {
			ZrfcApgOrdiniEvasi.InputRow row = new ZrfcApgOrdiniEvasi.InputRow();
			row.bstkd = ol.getNumeroOrdine();
			tbInput.add(row);
		}
		//Funzione SAP
		List<ZrfcApgOrdiniEvasi.OutputRow> tbOutput = 
				ZrfcApgOrdiniEvasi.execute(sapDestination, tbInput);
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
			avviso += verifyAndUpdateOrder(ses, ol, tbOutput, expirationDate, today, idRapporto);
		}
		return avviso;
	}
	
	//Scorre tutte le righe dei materiali evasi e modifica gli ordini e le evasioni su apg di conseguenza
	private static String verifyAndUpdateOrder(Session ses, OrdiniLogistica ol,
			List<ZrfcApgOrdiniEvasi.OutputRow> tbOutput,
			Date expirationDate, Date today, int idRapporto) throws HibernateException, BusinessException {
		int count = 0;//count order rows
		int countBA = 0;//count delivered rows
		String avviso = "";
		//Verifica fascicoli
		List<MaterialiSpedizione> msList = msDao.findByNumeroOrdine(ses, ol.getNumeroOrdine());
		for (MaterialiSpedizione ms:msList) {
			for (ZrfcApgOrdiniEvasi.OutputRow row:tbOutput) {
				if (ol.getNumeroOrdine().equals(row.bstkd.trim()) &&//Stesso ordine
						ms.getMateriale().getCodiceMeccanografico().equals(row.matnr.trim())) {//Stesso materiale
					count++;
					boolean bloccoAbbonamenti = row.abgru.equals(ZrfcApgOrdiniEvasi.ABGRU_BLOCCO_ABBONAMENTI);
					if ((row.menge <= row.evasa) || bloccoAbbonamenti) {
						//This line must be closed if MENGE equals EVASA or ABGRU is 'BA'
						avviso += checkAndWriteEvasi(ses, ms, ol, row.matnr.trim(),
								row.menge, row.evasa, bloccoAbbonamenti, today, idRapporto);
						countBA++;
					} else {
						if (row.evasa == 0) {
							//Nothing has been sent
							VisualLogger.get().addHtmlInfoLine(idRapporto,
									"["+ol.getNumeroOrdine()+"] " +
									"Materiale <b>"+row.matnr+"</b> IN ATTESA non evase:"+row.menge);
						} else {
							//Has been partially sent
							avviso += checkAndWriteEvasi(ses, ms, ol, row.matnr.trim(),
									row.menge, row.evasa, bloccoAbbonamenti, today, idRapporto);
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
	private static String checkAndWriteEvasi(Session ses, MaterialiSpedizione ms,
			OrdiniLogistica ol, String cm,
			int copieSap, int copieEvase,
			boolean bloccoAbbonamenti, Date today, int idRapporto) throws HibernateException {
		//int countEvasi = 0;
		String avviso = "";
		//Analisi riga ordine del materiale
		if (copieSap != ms.getCopie()) {//Non c'e' corrispondenza tra le richieste
			replaceNote(ses, ms, "ERRORE su SAP risultano richieste "+copieSap+
					" copie a fronte di "+ms.getCopie()+" su APG ");
			VisualLogger.get().addHtmlInfoLine(idRapporto, "["+ol.getNumeroOrdine()+"] " +
					"Materiale <b>"+cm+"</b> <b>ERRORE</b> richieste APG:"+ms.getCopie()+
					" richieste SAP:"+copieSap);
			String message = "ANOMALIA Ord."+ol.getNumeroOrdine()+" mat."+cm+
					" richieste APG:"+ms.getCopie()+
					" richieste SAP:"+copieSap;
			LOG.error(message);
			avviso += message+"<br />";
		} else {//Le richieste coincidono
			//Verifica copie evase
			if (copieEvase > ms.getCopie()) {//evase più del richiesto
				replaceNote(ses, ms, "ERRORE su SAP risultano evase "+copieEvase+
						" copie a fronte di "+ms.getCopie()+" richieste ");
				VisualLogger.get().addHtmlInfoLine(idRapporto, "["+ol.getNumeroOrdine()+"] " +
						"Materiale <b>"+cm+"</b> <b>ERRORE</b> richieste APG:"+ms.getCopie()+
						" evase SAP:"+copieEvase);
				String message = "ANOMALIA Ord."+ol.getNumeroOrdine()+" mat."+cm+
						" richieste APG:"+ms.getCopie()+
						" evase SAP:"+copieEvase;
				LOG.error(message);
				avviso += message+"<br />";
				replaceQuantity(ses, ms, copieEvase);
				confirmEvasioni(ses, ms, today);
			} else {
				//copie evase <= richieste
				if (ms.getCopie() == 0) {
					//zero copie RICHIESTE! ordine eventualmente da annullare
					VisualLogger.get().addHtmlInfoLine(idRapporto,"["+ol.getNumeroOrdine()+"] " +
							"Materiale <b>"+cm+"</b> <b>ERRORE</b> richieste "+
							ms.getCopie()+" copie");
					String message = "ANOMALIA Ord."+ol.getNumeroOrdine()+" mat."+cm+
							" "+ms.getCopie()+" copie richieste";
					LOG.error(message);
					avviso += message+"<br />";
				} else {
					if (copieEvase == 0) {
						//Non fa nulla (condizione che non dovrebbe verificarsi in qs metodo)
						if (bloccoAbbonamenti) {
							VisualLogger.get().addHtmlInfoLine(idRapporto,
									"["+ol.getNumeroOrdine()+"] " +
									"Materiale <b>"+cm+"</b> evasione chiusa con 0 copie");
							detachSpedizioneFromOrdine(ses, ms);
						} else {
							VisualLogger.get().addHtmlInfoLine(idRapporto,
									"["+ol.getNumeroOrdine()+"] " +
									"Materiale <b>"+cm+"</b> IN ATTESA");
						}
					} else {
						//Ci sono copie evase da verificare
						if (copieEvase < ms.getCopie()) {
							//Evase meno del richiesto
							if (bloccoAbbonamenti) {
								MaterialiSpedizione splitted = splitSpedizioni(ses, ms, copieEvase, today);
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
										"/"+ms.getCopie());
								replaceNote(ses, ms, copieEvase+" evase al "+ 
										ServerConstants.FORMAT_DAY.format(today)+" ");
							}
						} else {
							//Evase nella quantità giusta
							confirmEvasioni(ses, ms, today);
							VisualLogger.get().addHtmlInfoLine(idRapporto,
									"["+ol.getNumeroOrdine()+"] " +
									"Materiale <b>"+cm+"</b> evasione chiusa "+
									copieEvase+"/"+ms.getCopie());
							//countEvasi++;
						}
					}
				}
			}
		}
		return avviso;
	}
	
	private static void replaceQuantity(Session ses,
			MaterialiSpedizione ms, int copieEvase) throws HibernateException {
		ms.setCopie(copieEvase);
		msDao.update(ses, ms);
	}
	
	private static void replaceNote(Session ses,
			MaterialiSpedizione ms, String note) throws HibernateException {
		ms.setNote(note);
		if (ms.getNote().length() >= 255) ms.setNote(ms.getNote().substring(0,255));
		msDao.update(ses, ms);
	}
	
	private static void detachSpedizioneFromOrdine(Session ses,
			MaterialiSpedizione ms) throws HibernateException {
		ms.setDataInvio(null);
		ms.setOrdineLogistica(null);
		ms.setDataOrdine(null);
		msDao.update(ses, ms);
	}
	
	private static MaterialiSpedizione splitSpedizioni(Session ses, MaterialiSpedizione ef, Integer copieEvase,
			Date today) throws HibernateException {
		//Crea una EvasioneFascicoli con le copie non evase
		MaterialiSpedizione newMs = new MaterialiSpedizione();
		newMs.setCopie(ef.getCopie()-copieEvase);
		newMs.setDataCreazione(today);
		newMs.setDataInvio(null);
		newMs.setDataConfermaEvasione(null);
		newMs.setDataOrdine(null);
		newMs.setMateriale(ef.getMateriale());
		newMs.setIdAbbonamento(ef.getIdAbbonamento());
		newMs.setIdAnagrafica(ef.getIdAnagrafica());
		newMs.setNote(null);
		newMs.setOrdineLogistica(null);
		msDao.save(ses, newMs);//Crea
		//Modifica l'EvasioneFascicoliEvasa
		ef.setNote("Copie evase "+copieEvase+" a fronte di "+ef.getCopie()+" richieste");
		ef.setCopie(copieEvase);
		ef.setDataConfermaEvasione(today);
		msDao.update(ses, ef);
		return newMs;
	}
	
	private static void confirmEvasioni(Session ses, MaterialiSpedizione ms, Date today) 
			 throws HibernateException {
		ms.setDataConfermaEvasione(today);
		msDao.update(ses, ms);
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
