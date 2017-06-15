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

public class ZrfcCamTest {
	static private Logger LOG = LoggerFactory.getLogger(ZrfcCamTest.class);
	
	public static final String FUNCTION_NAME = "ZRFC_CAM_TEST";
	
	public static List<OutputRow> execute(JCoDestination jcoDestination, List<InputRow> input) 
			throws BusinessException {
		List<OutputRow> output = new ArrayList<OutputRow>();
		try {
			LOG.info("SAP destination:"+jcoDestination.getDestinationID()+
					" host:"+jcoDestination.getGatewayHost()+
					" service:"+jcoDestination.getGatewayService());
			JCoFunction function =
					jcoDestination.getRepository().getFunction(FUNCTION_NAME);
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
				inputTable.setValue("CAMPO1", input.get(i).CAMPO1);
				inputTable.setValue("CAMPO2", input.get(i).CAMPO2);
			}
			//Execution
			function.execute(jcoDestination);
			//Output
			for (int i = 0; i < outputTable.getNumRows(); i++) {
				outputTable.setRow(i);
				OutputRow row = new OutputRow();
				row.CAMPO1 = outputTable.getString("CAMPO1");
				row.CAMPO2 = outputTable.getInt("CAMPO2");
				row.ESITO = outputTable.getString("ESITO");
				output.add(row);
			}
		} catch (JCoException e) {
			throw new BusinessException(e.getMessage(), e);
		}
		return output;
	}
	
	
	//Inner classes
	
	
	public static class InputRow {
		public String CAMPO1 = null; //CHAR10
		public Integer CAMPO2 = null; //NUM5
	}
	
	public static class OutputRow {
		public String CAMPO1 = null; //CHAR10
		public Integer CAMPO2 = null; //NUM5
		public String ESITO = null; //CHAR100
	}
	
}
