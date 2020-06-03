package it.giunti.apg.automation.sap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import com.sap.conn.jco.JCoDestination;

import it.giunti.apg.automation.business.OrderBean;
import it.giunti.apg.automation.business.OrderRowBean;
import it.giunti.apg.core.VisualLogger;
import it.giunti.apg.core.business.CharsetUtil;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.MaterialiSpedizione;

public class ZrfcApgCreaOdvBusiness {

	public static void sendAndModifyOrders(Session ses,
			JCoDestination sapDestination, List<OrderBean> ordList,
			int idRapporto)
			throws BusinessException, HibernateException {
		//Prepara la tabella di input per SAP
		VisualLogger.get().addHtmlInfoLine(idRapporto, "Chiamata SAP per inserimento ordini");
		List<ZrfcApgCreaOdv.InputRow> tbInput = new ArrayList<ZrfcApgCreaOdv.InputRow>();
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
				MaterialiSpedizione ms = orb.getSpedizione();
				ZrfcApgCreaOdv.InputRow row = new ZrfcApgCreaOdv.InputRow();
				row.bstkd = bstkd;
				row.menge = ms.getCopie();
				row.name1 = name;
				row.name2 = presso;
				row.street = street;
				row.postCode1 = postCode;
				row.city1 = localita;
				if (provincia != null) {
					row.region = provincia;
				} else {
					row.region = "";
				}
				row.country = nazione;
				row.matnr = ms.getMateriale().getCodiceMeccanografico();
				row.tipo = orb.getCommittenteSap();
				tbInput.add(row);
			}
		}
		if (tbInput.size() == 0) {
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Nessun materiale deve essere ordinato");
			return;
		}
		//Funzione SAP
		List<ZrfcApgCreaOdv.OutputRow> tbOutput =
				ZrfcApgCreaOdv.execute(sapDestination, tbInput);
		//Acquisisce il risultato dell'inserimento
		VisualLogger.get().addHtmlInfoLine(idRapporto, "Analisi risposta SAP");
		Date today = DateUtil.now();
		for (ZrfcApgCreaOdv.OutputRow row:tbOutput) {
			if (row.errore) {
				cancelOrder(ses, ordList, row.bstkd, row.testo, today);
				VisualLogger.get().addHtmlInfoLine(idRapporto, "[Ord."+row.bstkd+"] <b>Annullato</b> errore SAP: "+row.testo);
			} else {
				VisualLogger.get().addHtmlInfoLine(idRapporto, "[Ord."+row.bstkd+"] <b>OK</b>");
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
					MaterialiSpedizione ms = orb.getSpedizione();
					ms.setDataOrdine(null);
					ms.setOrdineLogistica(null);
					GenericDao.updateGeneric(ses, ms.getId(), ms);
				}
			}
			bean.getOrdineLogistica().setDataRifiuto(date);
			bean.getOrdineLogistica().setDataChiusura(null);
			bean.getOrdineLogistica().setNote(errorMsg);
			GenericDao.updateGeneric(ses, bean.getOrdineLogistica().getId(), bean.getOrdineLogistica());
		}
	}
	
}
