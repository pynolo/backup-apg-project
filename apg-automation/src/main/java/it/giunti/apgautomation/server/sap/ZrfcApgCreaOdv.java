package it.giunti.apgautomation.server.sap;

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

public class ZrfcApgCreaOdv {
	static private Logger LOG = LoggerFactory.getLogger(ZrfcApgCreaOdv.class);
	
	public static final String FUNCTION_NAME = "ZRFC_APG_CREA_ODV";
	
	public static List<OutputRow> execute(JCoDestination sapDestination, List<InputRow> input) 
			throws BusinessException {
		List<OutputRow> output = new ArrayList<OutputRow>();
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
				String menge = input.get(i).menge.toString();
				inputTable.setValue(InputRow.BSTKD_NAME, input.get(i).bstkd);
				inputTable.setValue(InputRow.TIPO_NAME, input.get(i).tipo);
				inputTable.setValue(InputRow.MATNR_NAME, input.get(i).matnr);
				inputTable.setValue(InputRow.MENGE_NAME, menge);
				inputTable.setValue(InputRow.NAME1_NAME, input.get(i).name1);
				inputTable.setValue(InputRow.NAME2_NAME, input.get(i).name2);
				inputTable.setValue(InputRow.STREET_NAME, input.get(i).street);
				inputTable.setValue(InputRow.POSTCODE1_NAME, input.get(i).postCode1);
				inputTable.setValue(InputRow.CITY1_NAME, input.get(i).city1);
				inputTable.setValue(InputRow.REGION_NAME, input.get(i).region);
				inputTable.setValue(InputRow.COUNTRY_NAME, input.get(i).country);
			}
			//Execution
			function.execute(sapDestination);
			//Output
			for (int i = 0; i < outputTable.getNumRows(); i++) {
				outputTable.setRow(i);
				OutputRow row = new OutputRow();
				row.bstkd = outputTable.getString(OutputRow.BSTKD_NAME);
				row.errore = outputTable.getString(OutputRow.ERRORE_NAME).equals(SapConstants.TRUE);
				row.testo = outputTable.getString(OutputRow.TESTO_NAME);
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
		public static String TIPO_NAME = "TIPO";
		public static String MATNR_NAME = "MATNR";
		public static String MENGE_NAME = "MENGE";
		public static String NAME1_NAME = "NAME1";
		public static String NAME2_NAME = "NAME2";
		public static String STREET_NAME = "STREET";
		public static String POSTCODE1_NAME = "POST_CODE1";
		public static String CITY1_NAME = "CITY1";
		public static String REGION_NAME = "REGION";
		public static String COUNTRY_NAME = "COUNTRY";

		public String bstkd = null; //CHAR35 Ordine APG
		public String tipo = null; //CHAR2 Cliente
		public String matnr = null; //CHAR18 Materiale
		public Integer menge = null; //CHAR13 Quantità ODV
		public String name1 = null; //CHAR30
		public String name2 = null; //CHAR30
		public String street = null; //CHAR60
		public String postCode1 = null; //CHAR10
		public String city1 = null; //CHAR40
		public String region = null; //CHAR3
		public String country = null; //CHAR3
	}
	
	public static class OutputRow {
		public static String BSTKD_NAME = "BSTKD";
		public static String ERRORE_NAME = "ERRORE";
		public static String TESTO_NAME = "TESTO";
		
		public String bstkd = null; //CHAR35
		public Boolean errore = null; //CHAR1
		public String testo = null; //CHAR150
	}
	
}
