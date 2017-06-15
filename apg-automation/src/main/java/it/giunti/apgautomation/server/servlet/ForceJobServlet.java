package it.giunti.apgautomation.server.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForceJobServlet extends HttpServlet {

	private static final long serialVersionUID = 8068195193843175753L;
	static private Logger LOG = LoggerFactory.getLogger(ForceJobServlet.class);
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String jobName = request.getParameter("name");
		String jobGroup = request.getParameter("group");
		if (jobGroup==null) jobGroup = "giunti";
		if (jobName==null) {
			LOG.debug("ForceJobServlet non eseguita perché jobName o jobGroup non devono essere vuoti");
			return;
		}
		try {
			Scheduler scheduler;
			scheduler = StdSchedulerFactory.getDefaultScheduler();
			JobKey jobKey = new JobKey(jobName, jobGroup);
			scheduler.triggerJob(jobKey);
			//String urlWithSessionID = arg1.encodeRedirectURL("../");
			//arg1.sendRedirect(urlWithSessionID);
		} catch (SchedulerException e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		} catch (Throwable e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		response.setContentType("text/html");
		PrintWriter writer = response.getWriter();
		writer.println("<html><head>");
		writer.println("<link type='text/css' rel='stylesheet' href='style/standard.css'>");
		writer.println("</head><body>");
		writer.println("Lanciato il job '"+jobName+"' del gruppo '"+jobGroup+"'<br />");
		writer.println("<a href='#' onClick='history.go(-1)'>Esegui un altro job</a>");
		writer.println("</body></html>");
	}

	
}
