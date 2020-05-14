package it.giunti.apg.export;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import it.giunti.apg.export.service.ExportService;

@Component
public class ExportThread {
	private static Logger LOG = LoggerFactory.getLogger(ExportService.class);
	
	@Autowired
	ExportService exportService;
	
	@Async
	public void startThread() {
		export();
	}
	
	public void export() {
		//Load mode and set it to auto for next run
		String mode = exportService.loadExportMode();
		boolean canDoExport = (!ApgExportModeEnum.NONE.getMode().equals(mode));
		boolean fullExport = ApgExportModeEnum.FULL.getMode().equals(mode);
		LOG.info("************************");
		LOG.info("*APG EXPORT MODE: "+mode+" *");
		LOG.info("************************");
		
		if(canDoExport) {
			//Start export
			exportService.markExportStarted();
			Date beginTimestamp;
			if (fullExport) {
				beginTimestamp = new Date(0L);
			} else  {
				beginTimestamp = exportService.loadBeginTimestamp();
			}
			Date endTimestamp = exportService.loadEndTimestamp();
			
			//Clustered process
			int clusterRows = 0;
			int clusterCount = 0;
			int grandTotal = 0;
			do {
				LOG.info("* CLUSTER "+clusterCount+" *");
				clusterRows += exportService.exportCluster(beginTimestamp, endTimestamp, fullExport);
				grandTotal += clusterRows;
				clusterCount++;
			} while (clusterRows > 0);
			exportService.markExportFinished();
			exportService.saveExportMode(ApgExportModeEnum.AUTO.getMode());//next export will always be "auto"
			LOG.info("FINISHED: updated "+grandTotal+" crm_export rows");
		}
	}
	
}
