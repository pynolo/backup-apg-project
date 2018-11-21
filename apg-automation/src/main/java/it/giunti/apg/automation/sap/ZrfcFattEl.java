package it.giunti.apg.automation.sap;

import it.giunti.apg.shared.BusinessException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

public class ZrfcFattEl {
	static private Logger LOG = LoggerFactory.getLogger(ZrfcFattEl.class);
	
	public static final String FUNCTION_NAME = "ZFATT_EL";//TODO
	public static final String TABLE_ZFATT_EL_HEAD = "ZFATT_EL_HEAD";
	public static final String TABLE_ZFATT_EL_ITEM = "ZFATT_EL_ITEM";
	public static final String TABLE_T_ERR = "T_ERR";
	
	public static List<ErrRow> execute(JCoDestination sapDestination, 
			List<HeadRow> heads, List<ItemRow> items) 
			throws BusinessException {
		List<ErrRow> output = new ArrayList<ErrRow>();
		try {
			LOG.info("SAP destination:"+sapDestination.getDestinationID()+
					" host:"+sapDestination.getGatewayHost()+
					" service:"+sapDestination.getGatewayService());
			JCoFunction function =
					sapDestination.getRepository().getFunction(FUNCTION_NAME);
			if(function == null) throw new BusinessException(
					FUNCTION_NAME+" not found in SAP");
			//Import/export Parameters
			JCoParameterList tableList = function.getTableParameterList();
			if(tableList == null) throw new BusinessException(
					"No table parameters found for function "+FUNCTION_NAME+" in SAP");
			JCoTable headTable = tableList.getTable(TABLE_ZFATT_EL_HEAD);
			if(headTable == null) throw new BusinessException("Import parameter "+TABLE_ZFATT_EL_HEAD+
					" not found for function "+FUNCTION_NAME+" in SAP");
			JCoTable itemTable = tableList.getTable(TABLE_ZFATT_EL_ITEM);
			if(itemTable == null) throw new BusinessException("Import parameter "+TABLE_ZFATT_EL_ITEM+
					" not found for function "+FUNCTION_NAME+" in SAP");
			JCoTable errTable = tableList.getTable(TABLE_T_ERR);
			if(errTable == null) throw new BusinessException("Export parameter "+TABLE_T_ERR+
					" not found for function "+FUNCTION_NAME+" in SAP");
			//Head table
			headTable.clear();
			for (int i = 0; i < heads.size(); i++) {
				headTable.appendRow();
				headTable.setValue(HeadRow.BUKRS_NAME, heads.get(i).bukrs);
				headTable.setValue(HeadRow.SEQUENZIALE_NAME, heads.get(i).sequenziale);
				headTable.setValue(HeadRow.BELNR_NAME, heads.get(i).belnr);
				headTable.setValue(HeadRow.GJAHR_NAME, heads.get(i).gjahr);
				headTable.setValue(HeadRow.VBTYP_NAME, heads.get(i).vbtyp);
				headTable.setValue(HeadRow.WAERS_NAME, heads.get(i).waers);
				headTable.setValue(HeadRow.BLDAT_NAME, heads.get(i).bldat);
				headTable.setValue(HeadRow.COUNTRY_NAME, heads.get(i).country);
				headTable.setValue(HeadRow.DEST_CODE_NAME, heads.get(i).destCode);
				headTable.setValue(HeadRow.DEST_PEC_NAME, heads.get(i).destPec);
				headTable.setValue(HeadRow.ABLAD_NAME, heads.get(i).ablad);
				headTable.setValue(HeadRow.KUNRG_NAME, heads.get(i).kunrg);
				headTable.setValue(HeadRow.KUNRG_STCEG_NAME, heads.get(i).kunrgStceg);
				headTable.setValue(HeadRow.KUNRG_STCD1_NAME, heads.get(i).kunrgStcd1);
				headTable.setValue(HeadRow.KUNRG_NAME_NAME, heads.get(i).kunrgName);
				headTable.setValue(HeadRow.KUNRG_STREET_NAME, heads.get(i).kunrgStreet);
				headTable.setValue(HeadRow.KUNRG_HOUSE_NUM1_NAME, heads.get(i).kunrgHouseNum1);
				headTable.setValue(HeadRow.KUNRG_POST_CODE1_NAME, heads.get(i).kunrgPostCode1);
				headTable.setValue(HeadRow.KUNRG_CITY1_NAME, heads.get(i).kunrgCity1);
				headTable.setValue(HeadRow.KUNRG_REGION_NAME, heads.get(i).kunrgRegion);
				headTable.setValue(HeadRow.KUNRG_COUNTRY_NAME, heads.get(i).kunrgCountry);
				headTable.setValue(HeadRow.BBBNR_NAME, heads.get(i).bbbnr);
				headTable.setValue(HeadRow.FISKN_STREET_NAME, heads.get(i).fisknStreet);
				headTable.setValue(HeadRow.FISKN_HOUSE_NUM1_NAME, heads.get(i).fisknHouseNum1);
				headTable.setValue(HeadRow.FISKN_POST_CODE1_NAME, heads.get(i).fisknPostCode1);
				headTable.setValue(HeadRow.FISKN_CITY1_NAME, heads.get(i).fisknCity1);
				headTable.setValue(HeadRow.FISKN_REGION_NAME, heads.get(i).fisknRegion);
				headTable.setValue(HeadRow.FISKN_COUNTRY_NAME, heads.get(i).fisknCountry);
				headTable.setValue(HeadRow.FISKN_NAME1_NAME, heads.get(i).fisknName1);
				headTable.setValue(HeadRow.FISKN_STCEG_NAME, heads.get(i).fisknStceg);
				headTable.setValue(HeadRow.TOTALE_DOC_NAME, heads.get(i).totaleDoc);
				headTable.setValue(HeadRow.CAUSALE_NAME, heads.get(i).causale);
				headTable.setValue(HeadRow.TOT_IMP_NAME, heads.get(i).totImp);
				headTable.setValue(HeadRow.ZFBDT_NAME, heads.get(i).zfbdt);
				headTable.setValue(HeadRow.SCONTO_H_NAME, heads.get(i).scontoH);
				headTable.setValue(HeadRow.SCONTO_VAL_H_NAME, heads.get(i).scontoValH);
				headTable.setValue(HeadRow.TRASP_H_NAME, heads.get(i).traspH);
				headTable.setValue(HeadRow.ELABORATO_NAME, heads.get(i).elaborato);
			}
			//Item table
			itemTable.clear();
			for (int i = 0; i < items.size(); i++) {
				itemTable.appendRow();
				itemTable.setValue(ItemRow.BUKRS_NAME, heads.get(i).bukrs);
				itemTable.setValue(ItemRow.SEQUENZIALE_NAME, items.get(i).sequenziale);
				itemTable.setValue(ItemRow.BELNR_NAME, items.get(i).belnr);
				itemTable.setValue(ItemRow.POSNR_NAME, items.get(i).posnr);
				itemTable.setValue(ItemRow.GJAHR_NAME, items.get(i).gjahr);
				itemTable.setValue(ItemRow.ORD_ID_DOCUME_NAME, items.get(i).ordIdDocume);
				itemTable.setValue(ItemRow.ORD_DATA_NAME, items.get(i).ordData);
				itemTable.setValue(ItemRow.ORD_NUM_ITEM_NAME, items.get(i).ordNumItem);
				itemTable.setValue(ItemRow.ORD_COMMESSA_NAME, items.get(i).ordCommessa);
				itemTable.setValue(ItemRow.ORD_CUP_NAME, items.get(i).ordCup);
				itemTable.setValue(ItemRow.ORD_CIG_NAME, items.get(i).ordCig);
				itemTable.setValue(ItemRow.CONT_ID_DOCUME_NAME, items.get(i).contIdDocume);
				itemTable.setValue(ItemRow.CONT_DATA_NAME, items.get(i).contData);
				itemTable.setValue(ItemRow.CONT_NUM_ITEM_NAME, items.get(i).contNumItem);
				itemTable.setValue(ItemRow.CONT_COMMESSA_NAME, items.get(i).contCommessa);
				itemTable.setValue(ItemRow.CONT_CUP_NAME, items.get(i).contCup);
				itemTable.setValue(ItemRow.CONT_CIG_NAME, items.get(i).contCig);
				itemTable.setValue(ItemRow.CONV_ID_DOCUME_NAME, items.get(i).convIdDocume);
				itemTable.setValue(ItemRow.CONV_DATA_NAME, items.get(i).convData);
				itemTable.setValue(ItemRow.CONV_NUM_ITEM_NAME, items.get(i).convNumItem);
				itemTable.setValue(ItemRow.CONV_COMMESSA_NAME, items.get(i).convCommessa);
				itemTable.setValue(ItemRow.CONV_CUP_NAME, items.get(i).convCup);
				itemTable.setValue(ItemRow.CONV_CIG_NAME, items.get(i).convCig);
				itemTable.setValue(ItemRow.RIC_ID_DOCUME_NAME, items.get(i).ricIdDocume);
				itemTable.setValue(ItemRow.RIC_DATA_NAME, items.get(i).ricData);
				itemTable.setValue(ItemRow.RIC_NUM_ITEM_NAME, items.get(i).ricNumItem);
				itemTable.setValue(ItemRow.RIC_COMMESSA_NAME, items.get(i).ricCommessa);
				itemTable.setValue(ItemRow.RIC_CUP_NAME, items.get(i).ricCup);
				itemTable.setValue(ItemRow.RIC_CIG_NAME, items.get(i).ricCig);
				itemTable.setValue(ItemRow.DDT_NAME, items.get(i).ddt);
				itemTable.setValue(ItemRow.DDT_ERDAT_NAME, items.get(i).ddtErdat);
				itemTable.setValue(ItemRow.EAN11_NAME, items.get(i).ean11);
				itemTable.setValue(ItemRow.ORD_COD_TIPO_NAME, items.get(i).ordCodTipo);
				itemTable.setValue(ItemRow.ORD_COD_VALORE_NAME, items.get(i).ordCodValore);
				itemTable.setValue(ItemRow.TESTO_VBBP_NAME, items.get(i).testoVbbp);
				itemTable.setValue(ItemRow.FKIMG_NAME, items.get(i).fkimg);
				itemTable.setValue(ItemRow.ORD_INIZIO_PRES_NAME, items.get(i).ordInizioPres);
				itemTable.setValue(ItemRow.ORD_FINE_PRES_NAME, items.get(i).ordFinePres);
				itemTable.setValue(ItemRow.KZWI1_NAME, items.get(i).kzwi1);
				itemTable.setValue(ItemRow.SCONTO_NAME, items.get(i).sconto);
				itemTable.setValue(ItemRow.SCONTO_VAL_NAME, items.get(i).scontoVal);
				itemTable.setValue(ItemRow.SC_PLUS_NAME, items.get(i).scPlus);
				itemTable.setValue(ItemRow.TRASP_NAME, items.get(i).trasp);
				itemTable.setValue(ItemRow.COD_IVA_NAME, items.get(i).codIva);
				itemTable.setValue(ItemRow.ALIQUOTA_NAME, items.get(i).aliqiva);
				itemTable.setValue(ItemRow.IMP_IVA_NAME, items.get(i).impIva);
				itemTable.setValue(ItemRow.IMPOSTA_IVA_NAME, items.get(i).impostaIva);
			}
			//Execution
			function.execute(sapDestination);
			//Err table
			for (int i = 0; i < errTable.getNumRows(); i++) {
				errTable.setRow(i);
				ErrRow row = new ErrRow();
				row.line = null;
				if (errTable.getString(ErrRow.LINE_NAME) != null)
					row.line = Integer.valueOf(errTable.getString(ErrRow.LINE_NAME));
				row.tabname = errTable.getString(ErrRow.TABNAME_NAME);
				row.fieldname = errTable.getString(ErrRow.FIELDNAME_NAME);
				row.message = errTable.getString(ErrRow.MESSAGE_NAME);
				output.add(row);
			}
		} catch (JCoException e) {
			throw new BusinessException(e.getMessage(), e);
		}
		return output;
	}
	
	
	//Inner classes
	
	
	public static class HeadRow {
		public static String BUKRS_NAME = "BUKRS";
		public static String SEQUENZIALE_NAME = "SEQUENZIALE";
		public static String BELNR_NAME = "BELNR";
		public static String GJAHR_NAME = "GJAHR";
		public static String VBTYP_NAME = "VBTYP";
		public static String WAERS_NAME = "WAERS";
		public static String BLDAT_NAME = "BLDAT";
		public static String COUNTRY_NAME = "COUNTRY";
		public static String DEST_CODE_NAME = "DEST_CODE";
		public static String DEST_PEC_NAME = "DEST_PEC";
		public static String ABLAD_NAME = "ABLAD";
		public static String KUNRG_NAME = "KUNRG";
		public static String KUNRG_STCEG_NAME = "KUNRG_STCEG";
		public static String KUNRG_STCD1_NAME = "KUNRG_STCD1";
		public static String KUNRG_NAME_NAME = "KUNRG_NAME";
		public static String KUNRG_STREET_NAME = "KUNRG_STREET";
		public static String KUNRG_HOUSE_NUM1_NAME = "KUNRG_HOUSE_NUM1";
		public static String KUNRG_POST_CODE1_NAME = "KUNRG_POST_CODE1";
		public static String KUNRG_CITY1_NAME = "KUNRG_CITY1";
		public static String KUNRG_REGION_NAME = "KUNRG_REGION";
		public static String KUNRG_COUNTRY_NAME = "KUNRG_COUNTRY";
		public static String BBBNR_NAME = "BBBRN";
		public static String FISKN_STREET_NAME = "FISKN_STREET";
		public static String FISKN_HOUSE_NUM1_NAME = "FISKN_HOUSE_NUM1";
		public static String FISKN_POST_CODE1_NAME = "FISKN_POST_CODE1";
		public static String FISKN_CITY1_NAME = "FISKN_CITY1";
		public static String FISKN_REGION_NAME = "FISKN_REGION";
		public static String FISKN_COUNTRY_NAME = "FISKN_COUNTRY";
		public static String FISKN_NAME1_NAME = "FISKN_NAME1";
		public static String FISKN_STCEG_NAME = "FISKN_STCEG";
		public static String TOTALE_DOC_NAME = "TOTALE_DOC";
		public static String CAUSALE_NAME = "CAUSALE";
		public static String TOT_IMP_NAME = "TOT_IMP";
		public static String ZFBDT_NAME = "ZFBDT";
		public static String SCONTO_H_NAME = "SCONTO_H";
		public static String SCONTO_VAL_H_NAME = "SCONTO_VAL_H";
		public static String TRASP_H_NAME = "TRASP_H";
		public static String ELABORATO_NAME = "ELABORATO";

		public String bukrs = null; //*CHAR4 Societa
		public String sequenziale = null; //*CHAR5 prefisso sequenziale fattura
		public String belnr = null; //*CHAR10 numero documento
		public String gjahr = null; //*NUMC4 esercizio: anno fattura
		public String vbtyp = null; //CHAR1 tipo doc: 5=fattura 6=accredito
		public String waers = null; //*CUKY5 'EUR'
		public Date bldat = null; //*DATS8 data fattura 20181120
		public String country = null; //*CHAR3 codice paese XML
		public String destCode = null; //*CHAR10 codice destinatario
		public String destPec = null; //CHAR241 pec destinatario
		public String ablad = null; //CHAR25 -
		public String kunrg = null; //CHAR10 -
		public String kunrgStceg = null; //*CHAR20 partita iva
		public String kunrgStcd1 = null; //CHAR16 codice fiscale
		public String kunrgName = null; //*CHAR70 nome committente
		public String kunrgStreet = null; //*CHAR60 indirizzo stradale
		public String kunrgHouseNum1 = null; //CHAR10 civico
		public String kunrgPostCode1 = null; //*CHAR10 cap (se assente 00000)
		public String kunrgCity1 = null; //*CHAR40 località
		public String kunrgRegion = null; //CHAR3 provincia
		public String kunrgCountry = null; //*CHAR3 codice nazione XML
		public String bbbnr = null; //NUMC7 -
		public String fisknStreet = null; //CHAR60 indirizzo stradale
		public String fisknHouseNum1 = null; //CHAR10 civico
		public String fisknPostCode1 = null; //CHAR10 cap
		public String fisknCity1 = null; //CHAR40 localita
		public String fisknRegion = null; //CHAR3 provincia
		public String fisknCountry = null; //CHAR3 codice nazione
		public String fisknName1 = null; //CHAR35 nome 1 rap. fisc.
		public String fisknStceg = null; //CHAR20 iva rap. fisc.
		public Double totaleDoc = null; //*CURR15(2dec) totale
		public String causale = null; //CHAR200 causale -
		public Double totImp = null; //*CURR15(2dec) totale imponibile
		public String zfbdt = null; //*DATS8 data pagamento fattura
		public Double scontoH = null; //CURR11(2dec) percentuale sconto
		public Double scontoValH = null; //CURR11(2dec) importo sconto
		public Double traspH = null; //CURR11(2dec) importo trasp
		public String elaborato = null; //CHAR1 -
	}
	
	public static class ItemRow {
		public static String BUKRS_NAME = "BUKRS";
		public static String SEQUENZIALE_NAME = "SEQUENZIALE";
		public static String BELNR_NAME = "BELNR";
		public static String POSNR_NAME = "POSNR";
		public static String GJAHR_NAME = "GJAHR";
		public static String ORD_ID_DOCUME_NAME = "ORD_ID_DOCUME";
		public static String ORD_DATA_NAME = "ORD_DATA";
		public static String ORD_NUM_ITEM_NAME = "ORD_NUM_ITEM";
		public static String ORD_COMMESSA_NAME = "ORD_COMMESSA";
		public static String ORD_CUP_NAME = "ORD_CUP";
		public static String ORD_CIG_NAME = "ORD_CIG";
		public static String CONT_ID_DOCUME_NAME = "CONT_ID_DOCUME";
		public static String CONT_DATA_NAME = "CONT_DATA";
		public static String CONT_NUM_ITEM_NAME = "CONT_NUM_ITEM";
		public static String CONT_COMMESSA_NAME = "CONT_COMMESSA";
		public static String CONT_CUP_NAME = "CONT_CUP";
		public static String CONT_CIG_NAME = "CONT_CIG";
		public static String CONV_ID_DOCUME_NAME = "CONV_ID_DOCUME";
		public static String CONV_DATA_NAME = "CONV_DATA";
		public static String CONV_NUM_ITEM_NAME = "CONV_NUM_ITEM";
		public static String CONV_COMMESSA_NAME = "CONV_COMMESSA";
		public static String CONV_CUP_NAME = "CONV_CUP";
		public static String CONV_CIG_NAME = "CONV_CIG";
		public static String RIC_ID_DOCUME_NAME = "RIC_ID_DOCUME";
		public static String RIC_DATA_NAME = "RIC_DATA";
		public static String RIC_NUM_ITEM_NAME = "RIC_NUM_ITEM";
		public static String RIC_COMMESSA_NAME = "RIC_COMMESSA";
		public static String RIC_CUP_NAME = "RIC_CUP";
		public static String RIC_CIG_NAME = "RIC_CIG";
		public static String DDT_NAME = "DDT";
		public static String DDT_ERDAT_NAME = "DDT_ERDAT";
		public static String EAN11_NAME = "EAN11";
		public static String ORD_COD_TIPO_NAME = "ORD_COD_TIPO";
		public static String ORD_COD_VALORE_NAME = "ORD_COD_VALORE";
		public static String TESTO_VBBP_NAME = "TESTO_VBBP";
		public static String FKIMG_NAME = "FKIMG";
		public static String ORD_INIZIO_PRES_NAME = "ORD_INIZIO_PRES";
		public static String ORD_FINE_PRES_NAME = "ORD_FINE_PRES";
		public static String KZWI1_NAME = "KZWI1";
		public static String SCONTO_NAME = "SCONTO";
		public static String SCONTO_VAL_NAME = "SCONTO_VAL";
		public static String SC_PLUS_NAME = "SC_PLUS";
		public static String TRASP_NAME = "TRASP";
		public static String COD_IVA_NAME = "COD_IVA";
		public static String ALIQUOTA_NAME = "ALIQUOTA";
		public static String IMP_IVA_NAME = "IMP_IVA";
		public static String IMPOSTA_IVA_NAME = "IMPOSTA_IVA";
		
		public String bukrs = null; //*CHAR4 Societa
		public String sequenziale = null; //*CHAR5 prefisso sequenziale fattura
		public String belnr = null; //*CHAR10 numero documento
		public String posnr = null; //*NUMC6 posizione nel documento
		public String gjahr = null; //*NUMC4 esercizio: anno fattura
		public String ordIdDocume = null; //CHAR20 ordine acquisto
		public Date ordData = null; //DATS8 data ordine
		public String ordNumItem = null; //CHAR20 posizione ord acquisto
		public String ordCommessa = null; //CHAR100
		public String ordCup = null; //CHAR15
		public String ordCig = null; //CHAR14
		public String contIdDocume = null; //CHAR20 id contratto
		public Date contData = null; //DATS8
		public String contNumItem = null; //CHAR20
		public String contCommessa = null; //CHAR100
		public String contCup = null; //CHAR15
		public String contCig = null; //CHAR14
		public String convIdDocume = null; //CONV_ID_DOCUME
		public Date convData = null; //DATS8
		public String convNumItem = null; //CHAR20
		public String convCommessa = null; //CHAR100
		public String convCup = null; //CHAR15
		public String convCig = null; //CHAR14
		public String ricIdDocume = null; //RIC_ID_DOCUME
		public Date ricData = null; //DATS8
		public String ricNumItem = null; //CHAR20
		public String ricCommessa = null; //CHAR100
		public String ricCup = null; //CHAR15
		public String ricCig = null; //CHAR14
		public String ddt = null; //DDT
		public Date ddtErdat = null; //DATS8
		public String ean11 = null; //*CHAR18 ean
		public String ordCodTipo = null; //*CHAR35 'EAN'
		public String ordCodValore = null; //*CHAR35 codice valore
		public String testoVbbp = null; //*CHAR255 descrizione prodotto
		public String fkimg = null; //*QUAN13(3dec) quantità
		public String ordInizioPres = null; //DATS8 inizio presentazione
		public String ordFinePres = null; //DATS8 fine presentazione
		public String kzwi1 = null; //*CURR13(2dec) subtotale
		public String sconto = null; //CURR11(2dec) sconto
		public String scontoVal = null; //CURR11(2dec) importo sconto
		public String scPlus = null; //CURR11(2dec) sconto aggiuntivo
		public String trasp = null; //CURR11(2dec) importo trasp
		public String codIva = null; //*CHAR2 codice iva
		public String aliqiva = null; //*CHAR17 aliquota iva
		public String impIva = null; //*CURR11(2dec) imponibile iva
		public String impostaIva = null; //*CURR11(2dec) importo effettivo iva
	}
	
	public static class ErrRow {
		public static String LINE_NAME = "LINE";
		public static String TABNAME_NAME = "TABNAME";
		public static String FIELDNAME_NAME = "FIELDNAME";
		public static String MESSAGE_NAME = "MESSAGE";
		
		public Integer line = null; //INT4 riga dell'errore
		public String tabname = null; //CHAR30 può essere 'HEAD' e 'ITEM'
		public String fieldname = null; //CHAR30 campo con errore
		public String message = null; //CHAR220
	}
	
}
