package it.giunti.apg.export.scheduler;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import it.giunti.apg.export.ApgExportModeEnum;
import it.giunti.apg.export.service.ExportService;

@Component
public class ExportThread {
	private static Logger LOG = LoggerFactory.getLogger(ExportService.class);
	private static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	
	@Autowired
	ExportService exportService;
	
	@Async
	public void startThread() {
		export();
	}
	
	public void export() {
		Date startTime = new Date();
		//Load mode and set it to auto for next run
		String mode = exportService.loadExportMode();
		boolean canDoExport = (!ApgExportModeEnum.NONE.getMode().equals(mode));
		boolean fullExport = ApgExportModeEnum.FULL.getMode().equals(mode);
		LOG.info("*************************");
		LOG.info("* APG EXPORT MODE: "+mode+" *");
		LOG.info("*************************");
		
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
			
			exportService.exportChanges(beginTimestamp, endTimestamp, fullExport);

			exportService.markExportFinished();
			exportService.saveExportMode(ApgExportModeEnum.AUTO.getMode());//next export will always be "auto"
		}
		LOG.info("*************************");
		LOG.info("* STARTED:  "+SDF.format(startTime));
		LOG.info("* FINISHED: "+SDF.format(new Date()));
		LOG.info("*************************");
	}
	
}
