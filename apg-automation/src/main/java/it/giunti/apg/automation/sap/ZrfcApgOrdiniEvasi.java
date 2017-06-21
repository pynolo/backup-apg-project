package it.giunti.apg.automation.sap;

import it.giunti.apg.shared.BusinessException;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

public class ZrfcApgOrdiniEvasi {
	static private Logger LOG = LoggerFactory.getLogger(ZrfcApgOrdiniEvasi.class);
	
	public static final String FUNCTION_NAME = "ZRFC_APG_ORDINI_EVASI";
	
	public static final String ABGRU_BLOCCO_ABBONAMENTI = "BA";
	
	public static List<OutputRow> execute(JCoDestination destination, List<InputRow> input) 
			throws BusinessException {
		List<OutputRow> output = new ArrayList<OutputRow>();
		try {
			LOG.info("SAP destination:"+destination.getDestinationID()+
					" host:"+destination.getGatewayHost()+
					" service:"+destination.getGatewayService());
			JCoFunction function =
					destination.getRepository().getFunction(FUNCTION_NAME);
			if(function == null) throw new BusinessException(
					FUNCTION_NAME+" not found in SAP");
			//Import/export Parameters
			JCoParameterList tableList = function.getTableParameterList();
			if(tableList == null) throw new BusinessException(
					"No table parameters found for function "+FUNCTION_NAME+" in SAP");
			JCoTable outputTable = tableList.getTable(SapConstants.TABLE_OUTPUT);
			if(outputTable == null) throw new BusinessException("Export parameter "+
					SapConstants.TABLE_OUTPUT+
					" not found for function "+FUNCTION_NAME+" in SAP");
			JCoTable inputTable = tableList.getTable(SapConstants.TABLE_INPUT);
			if(inputTable == null) throw new BusinessException("Import parameter "+
					SapConstants.TABLE_INPUT+
					" not found for function "+FUNCTION_NAME+" in SAP");
			//Input
			inputTable.clear();
			for (int i = 0; i < input.size(); i++) {
				inputTable.appendRow();
				inputTable.setValue(InputRow.BSTKD_NAME, input.get(i).bstkd);
			}
			//Execution
			function.execute(destination);
			//Output
			for (int i = 0; i < outputTable.getNumRows(); i++) {
				outputTable.setRow(i);
				OutputRow row = new OutputRow();
				row.bstkd = outputTable.getString(OutputRow.BSTKD_NAME).trim();
				row.matnr = outputTable.getString(OutputRow.MATNR_NAME).trim();
				try {
					row.menge = Integer.parseInt(outputTable.getString(OutputRow.MENGE_NAME).trim());
				} catch (Exception e) {
					throw new BusinessException(e.getMessage(), e);
				}
				try {
					row.evasa = Integer.parseInt(outputTable.getString(OutputRow.EVASA_NAME).trim());
				} catch (Exception e) {
					throw new BusinessException(e.getMessage(), e);
				}
				row.abgru = outputTable.getString(OutputRow.ABGRU_NAME).trim();
				output.add(row);
			}
		} catch (JCoException e) {
			throw new BusinessException(e.getMessage(), e);
		}
		return output;
	}
	
	
	//Inner classes
	
	
	public static class InputRow {
		public static String BSTKD_NAME = "BSTKD";
		
		public String bstkd = null; //CHAR35 numero ordine
	}
	
	public static class OutputRow {
		public static String BSTKD_NAME = "BSTKD";
		public static String MATNR_NAME = "MATNR";
		public static String MENGE_NAME = "MENGE";
		public static String EVASA_NAME = "EVASA";
		public static String ABGRU_NAME = "ABGRU";
		
		public String bstkd = null; //CHAR35 numero ordine
		public String matnr = null; //CHAR18 meccanografico
		public Integer menge = null; //CHAR13 quantità richiesta
		public Integer evasa = null; //CHAR13 quantità evasa
		public String abgru = null; //CHAR2 popolato se posizione non ulteriormente evadibile
	}
	
}
