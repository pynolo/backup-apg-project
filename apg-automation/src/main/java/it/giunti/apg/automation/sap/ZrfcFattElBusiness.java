package it.giunti.apg.automation.sap;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import com.sap.conn.jco.JCoDestination;

import it.giunti.apg.core.business.CharsetUtil;
import it.giunti.apg.core.persistence.FattureArticoliDao;
import it.giunti.apg.core.persistence.FattureInvioSapDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.IndirizziUtil;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.Fatture;
import it.giunti.apg.shared.model.FattureArticoli;
import it.giunti.apg.shared.model.FattureInvioSap;
import it.giunti.apg.shared.model.Indirizzi;
import it.giunti.apg.shared.model.Societa;

public class ZrfcFattElBusiness {
	
	private static FattureArticoliDao faDao = new FattureArticoliDao();
	private static FattureInvioSapDao fisDao = new FattureInvioSapDao();
	
	public static void sendFattura(Session ses,	JCoDestination sapDestination, 
			Fatture fatt, int idInvio) throws BusinessException, HibernateException {
		
		//Input tables
		List<ZrfcFattEl.HeadRow> headList = new ArrayList<ZrfcFattEl.HeadRow>();
		List<ZrfcFattEl.ItemRow> itemList = new ArrayList<ZrfcFattEl.ItemRow>();

		//HEAD
		ZrfcFattEl.HeadRow head = new ZrfcFattEl.HeadRow();
		Societa societa = GenericDao.findById(ses, Societa.class, fatt.getIdSocieta());
		head.bukrs = societa.getCodiceSocieta();
		head.bukrs = CharsetUtil.toSapAscii(head.bukrs, 4);
		head.sequenziale = fatt.getNumeroFattura().substring(0, 3);
		head.sequenziale = CharsetUtil.toSapAscii(head.sequenziale, 5);
		head.belnr = CharsetUtil.toSapAscii(fatt.getNumeroFattura(), 10);
		Calendar cal = new GregorianCalendar();
		cal.setTime(fatt.getDataFattura());
		head.gjahr = CharsetUtil.toSapAscii(""+cal.get(Calendar.YEAR), 4);
		head.waers = CharsetUtil.toSapAscii("EUR", 5);
		head.bldat = fatt.getDataFattura();
		Anagrafiche anag = GenericDao.findById(ses, Anagrafiche.class, fatt.getIdAnagrafica());
		Indirizzi indFatt = anag.getIndirizzoPrincipale();
		if (IndirizziUtil.isFilledUp(anag.getIndirizzoFatturazione()))
			indFatt = anag.getIndirizzoFatturazione();
		head.country = CharsetUtil.toSapAscii(indFatt.getNazione().getSiglaNazione(), 3);
		String destCode = "0000000";
		if (!indFatt.getNazione().getSiglaNazione().equals("IT")) destCode = "XXXXXXX";
		head.destCode = destCode;
		String partitaIva = "";
		if (anag.getPartitaIva() != null) partitaIva = "IT"+anag.getPartitaIva();
		head.kunrgStceg = CharsetUtil.toSapAscii(partitaIva, 20);
		String nome = indFatt.getCognomeRagioneSociale();
		if (indFatt.getNome() != null) {
			if (indFatt.getNome().length() > 0) nome += " "+indFatt.getNome();
		}
		head.kunrgName = CharsetUtil.toSapAscii(nome, 70);
		head.kunrgStreet = CharsetUtil.toSapAscii(indFatt.getIndirizzo(), 60);
		String cap = "00000";
		if (indFatt.getCap() != null) {
			if (indFatt.getCap().length() > 0) cap = indFatt.getCap();
		}
		head.kunrgPostCode1 = CharsetUtil.toSapAscii(cap, 10);
		head.kunrgCity1 = CharsetUtil.toSapAscii(indFatt.getLocalita(), 40);
		head.kunrgCountry = CharsetUtil.toSapAscii(indFatt.getNazione().getSiglaNazione(), 3);
		head.totaleDoc = fatt.getTotaleFinale();
		head.totImp = fatt.getTotaleImponibile();
		head.zfbdt = fatt.getDataFattura();
		headList.add(head);
		//Articoli
		List<FattureArticoli> faList = faDao.findByFattura(ses, fatt.getId());
		int posnr = 0;
		for (FattureArticoli fa:faList) {
			//ITEM
			ZrfcFattEl.ItemRow item = new ZrfcFattEl.ItemRow();
			item.bukrs = head.bukrs;
			item.sequenziale = head.sequenziale;
			item.belnr = head.belnr;
			item.posnr = CharsetUtil.toSapAscii(""+posnr,6);
			item.gjahr = head.gjahr;
			//item.ean11
			//item.ordCodTipo
			//item.ordCodValore
			item.testoVbbp = CharsetUtil.toSapAscii(fa.getDescrizione(), 255);
			item.fkimg = CharsetUtil.toSapAscii(""+fa.getQuantita(), 13);
			item.kzwi1 = fa.getImportoTotUnit()*fa.getQuantita();
			String codIva = ValueUtil.getCodiceIva(fa.getAliquotaIva(), fatt.getTipoIva());
			item.codIva = CharsetUtil.toSapAscii(codIva, 2);
			Integer aliquota = new Double(Math.round(fa.getAliquotaIva().getValore()*100)).intValue();
			item.aliqiva = CharsetUtil.toSapAscii(""+aliquota, 13);
			item.impIva = fa.getImportoImpUnit()*fa.getQuantita();
			item.impostaIva = (fa.getImportoTotUnit()-fa.getImportoImpUnit())*fa.getQuantita();
			posnr++;
		}
			
		if (headList.size() == 0 || itemList.size() == 0) {
			//Niente da inviare
			return;
		}
		
		//Chiamata funzione SAP
		List<ZrfcFattEl.ErrRow> errList =
				ZrfcFattEl.execute(sapDestination, headList, itemList);
		
		//Acquisisce il risultato dell'inserimento
		if (errList == null) errList = new ArrayList<ZrfcFattEl.ErrRow>();
		if (errList.size() > 0) {
			for (ZrfcFattEl.ErrRow err:errList) {
				createFattureInvioError(ses, idInvio, err, fatt);
			}
		} else {
			createFattureInvioOk(ses, idInvio, fatt);
		}
	}
	
	private static void createFattureInvioError(Session ses, Integer idInvio,
			ZrfcFattEl.ErrRow err, Fatture fatt) {
		FattureInvioSap fis = new FattureInvioSap();
		fis.setDataCreazione(new Date());
		fis.setErrField(err.fieldname);
		fis.setErrMessage(err.message);
		fis.setErrTable(err.tabname);
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
