package it.giunti.apg.export.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import it.giunti.apg.export.ExportThread;


@Component
public class ScheduledTasks {

	private static final Logger LOG = LoggerFactory.getLogger(ScheduledTasks.class);

	@Autowired
	ExportThread exportThread;

	//Don't overlap and wait 2h after end (2h = 2*60*60*1000 -> 7.200.000)
	@Scheduled(fixedDelay=7200000, initialDelay=30000) 
	public void reportCurrentTime() {
		LOG.debug("Started scheduled task: crm export service");
		exportThread.export();
		LOG.debug("Finished scheduled task: crm export service");
	}
}
