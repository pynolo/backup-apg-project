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
	
	public void runExport(boolean fullExport, boolean checkRunning) {
		boolean isRunning = false;
		if (checkRunning) isRunning = exportService.checkExportRunning();
		if (isRunning) {
			LOG.info("FINISHED: job is already running");
		} else {
			if (checkRunning) exportService.markExportStarted();
			Date beginTimestamp = exportService.loadBeginTimestamp();
			Date endTimestamp = exportService.loadEndTimestamp();
			
			//Clustered process
			int clusterRows = 0;
			int clusterCount = 0;
			int grandTotal = 0;
			do {
				LOG.info("CLUSTER "+clusterCount);
				grandTotal += exportService.exportCluster(fullExport, beginTimestamp, endTimestamp);
				clusterCount++;
			} while (clusterRows > 0);
			if (checkRunning) exportService.markExportFinished();
			LOG.info("FINISHED: updated "+grandTotal+" crm_export rows");
		}
	}
	
}
