package it.giunti.apg.export.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import it.giunti.apg.export.service.ExportService;


@Component
public class ScheduledTasks {

	private static final Logger LOG = LoggerFactory.getLogger(ScheduledTasks.class);

	@Autowired
	ExportService exportService;

	@Scheduled(cron = "0 0 23 * * MON-FRI")
	public void reportCurrentTime() {
		LOG.debug("Started scheduled task: crm export service");
		exportService.runExport(false);
		LOG.debug("Finished scheduled task: crm export service");
	}
}
