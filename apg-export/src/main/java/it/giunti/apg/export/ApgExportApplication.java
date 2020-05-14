package it.giunti.apg.export;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class ApgExportApplication extends SpringBootServletInitializer {

	//private static Logger LOG = LoggerFactory.getLogger(ApgExportApplication.class);

	public static final String CONFIG_LAST_EXPORT_TIMESTAMP="last_export";
	public static final String CONFIG_EXPORT_RUNNING_TIMESTAMP="running";
	public static final String CONFIG_EXPORT_MODE="mode";
	public static final Integer FIND_PAGING_SIZE=1000;
	public static final Integer FILL_PAGING_SIZE=1000;
	public static final Integer PERSIST_PAGING_SIZE=1000;
	
	//@Autowired
	//ExportThread exportThread;
	
	public static void main(String[] args) {
		SpringApplication.run(ApgExportApplication.class, args);
	}

	//@EventListener(ApplicationReadyEvent.class)
	//public void runAfterStartup() {
	//	exportThread.startThread();
	//}
}
