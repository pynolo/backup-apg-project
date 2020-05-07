package it.giunti.apg.export;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

import it.giunti.apg.export.service.ExportService;

@SpringBootApplication
@EnableScheduling
public class ApgExportApplication {

	//private static Logger LOG = LoggerFactory.getLogger(ApgExportApplication.class);

	public static final String LAST_EXPORT_TIMESTAMP="last_export";
	public static final Integer PAGING=500;
	
	@Autowired
	ExportService exportService;
	
	public static void main(String[] args) {
		SpringApplication.run(ApgExportApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void runAfterStartup() {
		exportService.runExport();
	}
}
