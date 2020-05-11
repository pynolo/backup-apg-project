package it.giunti.apg.export;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ApgExportApplication {

	//private static Logger LOG = LoggerFactory.getLogger(ApgExportApplication.class);

	public static final String CONFIG_LAST_EXPORT_TIMESTAMP="last_export";
	public static final String CONFIG_EXPORT_RUNNING_TIMESTAMP="running";
	public static final Integer CLUSTER_SIZE=250;
	public static final Integer PAGING_SIZE=250;
	
	@Autowired
	ExportThread exportThread;
	
	public static void main(String[] args) {
		SpringApplication.run(ApgExportApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void runAfterStartup() {
		exportThread.runExport(true, false);
	}
}
