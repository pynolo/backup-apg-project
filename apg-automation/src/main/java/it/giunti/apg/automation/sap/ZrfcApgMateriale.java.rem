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

public class ZrfcApgMateriale {
	static private Logger LOG = LoggerFactory.getLogger(ZrfcApgMateriale.class);

	public static final String FUNCTION_NAME = "ZRFC_APG_MATERIALE";
	
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
				inputTable.setValue(InputRow.MATNR_NAME, input.get(i).matnr);
				inputTable.setValue(InputRow.TIPO_NAME, input.get(i).tipo);
			}
			//Execution
			function.execute(destination);
			//Output
			for (int i = 0; i < outputTable.getNumRows(); i++) {
				outputTable.setRow(i);
				OutputRow row = new OutputRow();
				row.matnr = outputTable.getString(OutputRow.MATNR_NAME).trim();
				row.tipo = outputTable.getString(OutputRow.TIPO_NAME).trim();
				try {
					row.giacenza = Integer.parseInt(outputTable.getString(OutputRow.GIACENZA_NAME).trim());
				} catch (Exception e) {
					throw new BusinessException(e.getMessage(), e);
				}
				row.bloccato = outputTable.getString(OutputRow.BLOCCATO_NAME).equals(SapConstants.TRUE);
				output.add(row);
			}
		} catch (JCoException e) {
			throw new BusinessException(e.getMessage(), e);
		}
		return output;
	}
	
	
	//Inner classes
	
	
	public static class InputRow {
		public static String MATNR_NAME = "MATNR";
		public static String TIPO_NAME = "TIPO";
		
		public String matnr = null; //CHAR18
		public String tipo = null; //CHAR2
	}
	
	public static class OutputRow {
		public static String MATNR_NAME = "MATNR";
		public static String TIPO_NAME = "TIPO";
		public static String GIACENZA_NAME = "GIACENZA";
		public static String BLOCCATO_NAME = "BLOCCATO";
		
		public String matnr = null; //CHAR18
		public String tipo = null; //CHAR2
		public Integer giacenza = null; //CHAR13
		public Boolean bloccato = null; //CHAR1
	}
	
}
