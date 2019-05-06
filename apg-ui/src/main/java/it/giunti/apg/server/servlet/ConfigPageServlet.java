package it.giunti.apg.server.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;

import com.mchange.v2.c3p0.impl.NewProxyConnection;

import it.giunti.apg.core.persistence.SessionFactory;

public class ConfigPageServlet extends HttpServlet {
	private static final long serialVersionUID = 2502489331123867659L;
	
	public ConfigPageServlet() {
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
							out.print("<tr><td>JDBC: </td><td>"+url+"</td></tr>");
						}
					}
				);
		} catch (HibernateException e) {
			out.print(e.getMessage());
			e.printStackTrace();
		} finally {
			ses.close();
		}
		
	    out.print("</table>");
	    out.write("</body></html>");
		out.close();
	}
	
}
