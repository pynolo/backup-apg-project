package it.giunti.apg.core.business;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeliveryFileFilterBusiness {

	private static final char SEP = ';';
	
	public static List<File> merge(File fileUnione1, File fileUnione2) throws BusinessException {
		List<File> resultList = new ArrayList<File>();
		try {
			Map<String,String> mapUnione1 = deliveryFileToMap(fileUnione1);
			Map<String,String> mapUnione2 = deliveryFileToMap(fileUnione2);
			Map<String,String> resultMap = new HashMap<String,String>();
			//Unisce le due mappe in result
			for (String key:mapUnione1.keySet()) resultMap.put(key, mapUnione1.get(key));
			for (String key:mapUnione2.keySet()) resultMap.put(key, mapUnione2.get(key));
			//Da mappa a file
			File result = mapToDeliveryFile(resultMap, "unione_etichette_");
			resultList.add(result);
		} catch (IOException e) {
			throw new BusinessException(e.getMessage(), e);
		}
		return resultList;
	}
	
	public static List<File> diff(File fileDifferenza1, File fileDifferenza2) throws BusinessException {
		List<File> resultList = new ArrayList<File>();
		try {
			Map<String,String> mapDifferenza1 = deliveryFileToMap(fileDifferenza1);
			Map<String,String> mapDifferenza2 = deliveryFileToMap(fileDifferenza2);
			Map<String,String> mergeMap = new HashMap<String,String>();
			//Unisce le due mappe
			for (String key:mapDifferenza1.keySet()) mergeMap.put(key, mapDifferenza1.get(key));
			for (String key:mapDifferenza2.keySet()) mergeMap.put(key, mapDifferenza2.get(key));
			//Crea due mappe con le differenze
			Map<String,String> only1 = new HashMap<String, String>();
			Map<String,String> only2 = new HashMap<String, String>();
			for (String key:mergeMap.keySet()) {
				String from1 = mapDifferenza1.get(key);
				String from2 = mapDifferenza2.get(key);
				if (from1 != null && from2 == null) only1.put(key, from1);
				if (from1 == null && from2 != null) only2.put(key, from2);
			}
			//Da mappa a file
			File result1 = mapToDeliveryFile(only1, "differenze_file1_");
			resultList.add(result1);
			File result2 = mapToDeliveryFile(only2, "differenze_file2_");
			resultList.add(result2);
		} catch (IOException e) {
			throw new BusinessException(e.getMessage(), e);
		}
		return resultList;
	}
	
	
	private static Map<String, String> deliveryFileToMap(File f)
			throws IOException {
		Map<String, String> result = new HashMap<String, String>();
		
		try {
			InputStreamReader reader = new InputStreamReader(new FileInputStream(f));
			CsvReader delivery = new CsvReader(reader, SEP);
			while (delivery.readRecord()) {
				String record = delivery.getRawRecord();
				String orderKey = FileFormatCommon.formatString(10, delivery.get(17))+//nazione oppure zz se italia
						FileFormatCommon.formatString(3, "000"+delivery.get(11))+//COPIE
						delivery.get(2)+//CAP
						delivery.get(1);//COD.ABBO (per avere unicità nella chiave)
				result.put(orderKey, record);
			}
			delivery.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	private static File mapToDeliveryFile(Map<String,String> map, String fileNamePrefix) throws IOException {
		List<String> keyList = new ArrayList<String>();
		keyList.addAll(map.keySet());
		Collections.sort(keyList);
		//keyList è ordinata
		File f = File.createTempFile(fileNamePrefix, ".csv");
		f.deleteOnExit();
		OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(f), AppConstants.CHARSET_UTF8);
        BufferedWriter out = new BufferedWriter(writer);
		for (String key:keyList) {
			String record = map.get(key);
			out.write(record+ServerConstants.INVIO_EOL);
		}
	    //Close the output stream
	    out.close();
	    return f;
	}
}
