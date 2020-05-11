package it.giunti.apg.export.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("exportProcess")
public class ExportProcess {
	private static Logger LOG = LoggerFactory.getLogger(ExportProcess.class);
	
	@Autowired
	private ExportMainService exportMainService;
	
	public void runExport(boolean fullExport, boolean checkRunning) {
		boolean isRunning = false;
		if (checkRunning) isRunning = exportMainService.checkExportRunning();
		if (isRunning) {
			LOG.info("FINISHED: job is already running");
		} else {
			if (checkRunning) exportMainService.markExportStarted();
			Date beginTimestamp = exportMainService.loadBeginTimestamp();
			Date endTimestamp = exportMainService.loadEndTimestamp();
			
			//Clustered process
			int clusterRows = 0;
			int clusterCount = 0;
			int grandTotal = 0;
			do {
				LOG.info("CLUSTER "+clusterCount);
				grandTotal += exportMainService.exportCluster(fullExport, beginTimestamp, endTimestamp);
				clusterCount++;
			} while (clusterRows > 0);
			if (checkRunning) exportMainService.markExportFinished();
			LOG.info("FINISHED: updated "+grandTotal+" crm_export rows");
		}
	}
	
}
