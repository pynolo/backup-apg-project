package it.giunti.apg.export;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class ApgExportApplication extends SpringBootServletInitializer {

	private static Logger LOG = LoggerFactory.getLogger(ApgExportApplication.class);

	public static final String CONFIG_LAST_EXPORT_TIMESTAMP="last_export";
	public static final String CONFIG_EXPORT_RUNNING_TIMESTAMP="running";
	public static final Integer CLUSTER_SIZE=2500;
	public static final Integer PAGING_SIZE=250;
	public static final String CONFIG_EXPORT_MODE_FULL="full";
	public static final String CONFIG_EXPORT_MODE_AUTO="auto";
	
	@Value("${apg.export.mode}")
	private String apgExportMode;
	
	@Autowired
	ExportThread exportThread;
	
	public static void main(String[] args) {
		SpringApplication.run(ApgExportApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void runAfterStartup() {
		LOG.info("EXPORT MODE: "+apgExportMode);
		if (CONFIG_EXPORT_MODE_FULL.equalsIgnoreCase(apgExportMode)) {
			exportThread.startThread(true);
		}
	}
}
