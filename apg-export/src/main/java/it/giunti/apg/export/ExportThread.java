package it.giunti.apg.export;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.giunti.apg.export.service.ExportService;

@Component
public class ExportThread {
	private static Logger LOG = LoggerFactory.getLogger(ExportService.class);
	
	@Autowired
	ExportService exportService;
	
	public void run(boolean fullExport) {
		exportService.markExportStarted();
		Date beginTimestamp = exportService.loadBeginTimestamp();
		Date endTimestamp = exportService.loadEndTimestamp();
		
		//Clustered process
		int clusterRows = 0;
		int clusterCount = 0;
		int grandTotal = 0;
		do {
			LOG.info("* CLUSTER "+clusterCount+" *");
			clusterRows += exportService.exportCluster(fullExport, beginTimestamp, endTimestamp);
			grandTotal += clusterRows;
			clusterCount++;
		} while (clusterRows > 0);
		exportService.markExportFinished();
		LOG.info("FINISHED: updated "+grandTotal+" crm_export rows");
	}
	
}
