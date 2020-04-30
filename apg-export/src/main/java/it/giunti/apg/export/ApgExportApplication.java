package it.giunti.apg.export;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import it.giunti.apg.export.service.ExportService;

@SpringBootApplication
public class ApgExportApplication {

	private static Logger LOG = LoggerFactory.getLogger(ApgExportApplication.class);

	@Autowired
	ExportService exportService;
	
	public static void main(String[] args) {
		SpringApplication.run(ApgExportApplication.class, args);
	}

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {
    	try {
    		exportService.updateTasksAndPlans();
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
    }
}
