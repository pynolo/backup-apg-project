package it.giunti.apg.export.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import it.giunti.apg.export.ApgExportApplication;
import it.giunti.apg.export.ExportThread;


@Component
public class ScheduledTasks {

	private static final Logger LOG = LoggerFactory.getLogger(ScheduledTasks.class);

	@Value("${apg.export.mode}")
	private String apgExportMode;
	
	@Autowired
	ExportThread exportThread;

	//Don't overlap and wait 2h after end (2h = 2*60*60*1000 -> 7.200.000)
	@Scheduled(fixedDelay=7200000, initialDelay=7200000) 
	public void reportCurrentTime() {
		if (ApgExportApplication.CONFIG_EXPORT_MODE_AUTO.contentEquals(apgExportMode)) {
			LOG.debug("Started scheduled task: crm export service");
			exportThread.run(false);
			LOG.debug("Finished scheduled task: crm export service");
		}
	}
}
