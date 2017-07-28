package it.giunti.apg.automation.servlet;

import it.giunti.apg.automation.AutomationConstants;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.naming.NamingException;
import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.simpl.SimpleClassLoadHelper;
import org.quartz.xml.XMLSchedulingDataProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartSchedulerServlet extends GenericServlet{
	private static final long serialVersionUID = -284746908142861897L;
	
	static private final Logger LOG = LoggerFactory.getLogger(StartSchedulerServlet.class);
	
	
	@Override
	public void init() throws ServletException {
		LOG.info("Instanziata StartSchedulerServlet");
		initQuartz();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		LOG.info("Instanziata StartSchedulerServlet con configurazione xml");
		initQuartz();
	}

	private void initQuartz(){
		try {
			URL confUrl = this.getClass().getResource(AutomationConstants.QUARTZ_CONFIG_FILE);
			if(confUrl!=null){
				LOG.debug(AutomationConstants.QUARTZ_CONFIG_FILE + " exists (path "+confUrl.getPath()+")");
				File f = new File(confUrl.getPath());
				String absPath = f.getAbsolutePath();
				if (f.exists()) {
					LOG.debug("opening conf file: "+absPath);
					Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
					LOG.debug("DefaultScheduler instantiated");
					XMLSchedulingDataProcessor xmlProcessor = new XMLSchedulingDataProcessor(new SimpleClassLoadHelper());
					xmlProcessor.processFileAndScheduleJobs(absPath, scheduler);
					LOG.debug("xmlProcessor scheduled");
				} else {
					LOG.error("file "+absPath+" not found");
					throw new IOException("config file "+absPath+" not found");
				}
			}else{
				LOG.error(AutomationConstants.QUARTZ_CONFIG_FILE + " does NOT exists");
			}
			//scheduler.start();
		} catch (NamingException e) {
			LOG.error("Non trovata la cartella di configurazione",e);
			e.printStackTrace();
		} catch (SchedulerException e) {
			LOG.error("Errore Scheduler",e);
			e.printStackTrace();
		} catch (Exception e) {
			LOG.error("Errore Scheduler",e);
			e.printStackTrace();
		}
		
	}

	@Override
	public void service(ServletRequest arg0, ServletResponse arg1)
			throws ServletException, IOException {
		this.initQuartz();
		
	}


}
