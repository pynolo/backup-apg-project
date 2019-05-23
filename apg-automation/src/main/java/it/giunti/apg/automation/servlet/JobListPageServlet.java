package it.giunti.apg.automation.servlet;

import it.giunti.apg.automation.AutomationConstants;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobListPageServlet extends HttpServlet {
	private static final long serialVersionUID = 6342259079076611815L;
	
	private static final Logger LOG = LoggerFactory.getLogger(JobListPageServlet.class);
	
	private static final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm dd/MM/yyyy");
	
	public JobListPageServlet() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		out.write("<html>" +
				"<head>" +
				"<link type=\"text/css\" rel=\"stylesheet\" href=\"Apg.css\" />"+
				"<link type=\"text/css\" rel=\"stylesheet\" href=\"style/standard.css\" />"+
				"<link type=\"text/css\" rel=\"stylesheet\" href=\"fonts/fork-awesome/css/fork-awesome.css\" />"+
				"<script language=\"javascript\">"+
					"function startJob(jobName,jobGroup){"+
						"document.location.href=\"forcejob?name=\"+jobName+\"&group=\"+jobGroup;"+
					"}"+
				"</script>" +
				"</head>" +
				"<body>");
		List<JobData> jobList;
		try {
			jobList = findJobs(0, Integer.MAX_VALUE);
		} catch (SchedulerException e) {
			throw new ServletException(e);
		}
		Collections.sort(jobList, new JobDataComparator());
		int rowNum = 0;
		out.print("<table class='apg-table'>");
	    for (JobData jd:jobList) {
	    	String icon = AutomationConstants.ICON_JOB_DEFAULT;
	    	String cssStyleName = getCssStyleName(rowNum);
	    	if (jd.getIcon() != null) icon = jd.getIcon();
	    	out.print("<tr class='"+cssStyleName+"'>");
	    	//out.print("<td valign='top'><img src='"+icon+"' border='0' /></td><td valign='top'>");
	    	out.print("<td valign='top' style='text-align: center; color: #337ab7'><i class='"+icon+"' aria-hidden='true' style='font-size: 1.8em'></i></td><td valign='top'>");
	    	out.print("<b>"+jd.getDescription()+"</b><br />");
	    	//out.print("<i>"+jd.getName()+"</i><br />");
	    	if (jd.getNextFireTime() != null) {
	    		out.print("Next: <i>"+SDF.format(jd.getNextFireTime())+"</i><br />");
	    	}
	    	if (jd.getPreviousFireTime() != null) {
	    		out.print("Prev: <i>"+SDF.format(jd.getPreviousFireTime())+"</i>");
	    	}
	    	out.print("</td><td valign='top'>");
	    	out.print("<input type=\"button\" class=\"gwt-Button\" " +
	    			"value=\"Esegui ora\" " + //jd.getName()+"\" " +
		    		"onclick=\"javascript:startJob('"+jd.getName()+"','"+jd.getGroup()+"');\" />");
	    	out.print("</td></tr>");
	    	rowNum++;
	    }
	    out.print("</table>");
	    out.write("</body></html>");
	}
	
	private static String getCssStyleName(int rowNum) {
		String name = "apg-row-even";
		if ((rowNum % 2) != 0) name = "apg-row-odd";
		return name;
	}
	
	@SuppressWarnings("unchecked")
	public static List<JobData> findJobs(int offset, int pageSize)
			throws SchedulerException {
		List<JobData> result = new ArrayList<JobData>();
		try {
			Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
			List<String> jobGroups = scheduler.getJobGroupNames();
			for (String jobGroup:jobGroups){
				Set<JobKey> jobKeys = scheduler.getJobKeys((GroupMatcher<JobKey>)GroupMatcher.jobGroupEquals(jobGroup));
				for (JobKey jobKey:jobKeys) {
					JobDetail jobDetail = scheduler.getJobDetail(jobKey);
					List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
					for (Trigger trigger:triggers) {
						JobData jobData = new JobData();
						jobData.setName(jobDetail.getKey().getName());
						jobData.setGroup(jobDetail.getKey().getGroup());
						jobData.setDescription(jobDetail.getDescription());
						jobData.setIcon((String) jobDetail.getJobDataMap().get("icon"));
						jobData.setNextFireTime(trigger.getNextFireTime());
						jobData.setPreviousFireTime(trigger.getPreviousFireTime());
						result.add(jobData);
					}
				}
			}
		} catch (org.quartz.SchedulerException e) {
			LOG.error(e.getMessage(), e);
			throw new SchedulerException(e.getMessage(), e);
		}
		return result;
	}
	
	
//	@Override
//	public Boolean forceJob(String name, String group)
//			throws SchedulerException, EmptyResultException {
//		LOG.warn("Eseguito da client il job '"+name+"'");
//		try {
//			Scheduler scheduler;
//			scheduler = StdSchedulerFactory.getDefaultScheduler();
//			JobKey jobKey = new JobKey(name, group);
//			scheduler.triggerJob(jobKey);
//		} catch (org.quartz.SchedulerException e) {
//			throw new SchedulerException(e.getMessage(), e);
//		}
//		return null;
//	}
	
	
	public static class JobDataComparator implements Comparator<JobData> {
		@Override
		public int compare(JobData job1, JobData job2) {
			int compare = 0;
			if ((job1 !=null) && (job2 != null)) {
				try {
					compare = job1.getNextFireTime().compareTo(job2.getNextFireTime());
				} catch (Exception e) {
					LOG.warn("Warning JobDataComparator:" +
							" "+job1.getName()+" next fire "+job1.getNextFireTime()+
							" "+job2.getName()+" next fire "+job2.getNextFireTime(), e);
				}
			} 
			return compare;
		}
		
	}
}
