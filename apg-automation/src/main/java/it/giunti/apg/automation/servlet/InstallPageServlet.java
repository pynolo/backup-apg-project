package it.giunti.apg.automation.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import com.mchange.v2.c3p0.impl.NewProxyConnection;

import it.giunti.apg.core.persistence.SessionFactory;

public class InstallPageServlet extends HttpServlet {
	private static final long serialVersionUID = 2502489331123867659L;
	
	public InstallPageServlet() {
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
				"<link type=\"text/css\" rel=\"stylesheet\" href=\"fonts/fork-awesome/css/fork-awesome.css\" />"+
				"</head>" +
				"<body>");

		out.print("<table class='apg-table'>");
		//HIBERNATE SESSION
		Session ses = SessionFactory.getSession();
		try {
			ses.doWork(
				    new Work() {
						@Override
						public void execute(Connection conn) throws SQLException {
							NewProxyConnection pooledConn = (NewProxyConnection) conn;
							DatabaseMetaData dmd = pooledConn.getMetaData();
							String url = dmd.getURL();
							out.print("<tr><td><b>JDBC:</b> </td><td>"+url+"</td></tr>");
						}
					}
				);
		} catch (HibernateException e) {
			out.print(e.getMessage());
			e.printStackTrace();
		} finally {
			ses.close();
		}
		
		//QUARTZ SETTINGS
		List<JobDetail> jdList;
		try {
			jdList = findJobDetails();
		} catch (SchedulerException e) {
			throw new ServletException(e);
		}
		if (jdList != null) {
		    for (JobDetail jd:jdList) {
				JobDataMap jdm = jd.getJobDataMap();
				Map<String,Object> jdmMap = jdm.getWrappedMap();
				for (String jdmKey:jdmMap.keySet()) {
					String lKey = jdmKey.toLowerCase();
					if (lKey.contains("host")) {
						out.print("<tr><td><b>"+jd.getKey()+":</b> </td><td>"+jdmKey+"="+jdm.getString(jdmKey)+"</td></tr>");
					}
				}
			}
	    }
		
	    out.print("</table>");
	    out.write("</body></html>");
		out.close();
	}
	
	public static List<JobDetail> findJobDetails() throws SchedulerException {
		List<JobDetail> result = new ArrayList<JobDetail>();
		try {
			Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
			List<String> jobGroups = scheduler.getJobGroupNames();
			for (String jobGroup:jobGroups){
				Set<JobKey> jobKeys = scheduler.getJobKeys((GroupMatcher<JobKey>)GroupMatcher.jobGroupEquals(jobGroup));
				for (JobKey jobKey:jobKeys) {
					result.add(scheduler.getJobDetail(jobKey));
				}
			}
		} catch (org.quartz.SchedulerException e) {
			throw new SchedulerException(e.getMessage(), e);
		}
		return result;
	}
}
