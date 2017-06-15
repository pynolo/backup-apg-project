package it.giunti.apgautomation.server.servlet;

import it.giunti.apg.server.persistence.GenericDao;
import it.giunti.apg.server.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.FileResources;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScanFilesServlet extends GenericServlet{
	private static final long serialVersionUID = 1643053535488916705L;
	
	static private final Logger LOG = LoggerFactory.getLogger(ScanFilesServlet.class);
	private ServletContext context = null;
	
	@Override
	public void init() throws ServletException {
		LOG.info("Instanziata "+this.getClass().getSimpleName());
		context = this.getServletContext();
		try {
			this.rescanFiles();
		} catch (BusinessException e) {
			throw new ServletException(e.getMessage(), e);
		}
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		LOG.info("Instanziata "+this.getClass().getSimpleName()+" con configurazione xml");
		context = config.getServletContext();
		try {
			this.rescanFiles();
		} catch (BusinessException e) {
			throw new ServletException(e.getMessage(), e);
		}
	}

	private void rescanFiles() throws BusinessException {
		List<String> logoList = findFiles(AppConstants.RESOURCE_DIR_LOGO);
		List<String> jasperList = findFiles(AppConstants.RESOURCE_DIR_JASPER, "jasper");
		
		Session ses = SessionFactory.getSession();
		Transaction trx = ses.beginTransaction();
		try {
			//Svuota la tabella
			List<FileResources> resList = GenericDao.findByClass(ses, FileResources.class, "id");
			for (FileResources res:resList) GenericDao.deleteGeneric(ses, res.getId(), res);
			//Riempie la tabella
			for (String s:logoList) {
				FileResources res = new FileResources();
				res.setFileType(AppConstants.RESOURCE_TYPE_LOGO);
				res.setPath(s);
				GenericDao.saveGeneric(ses, res);
			}
			for (String s:jasperList) {
				FileResources res = new FileResources();
				res.setFileType(AppConstants.RESOURCE_TYPE_JASPER);
				res.setPath(s);
				GenericDao.saveGeneric(ses, res);
			}
			trx.commit();
		} catch (HibernateException e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}		
	}
	
	public List<String> findFiles(String relativePath) throws BusinessException {
		return findFiles(relativePath, null);
	}
	
	public List<String> findFiles(String relativePath, String filterExt) throws BusinessException {
		final String fFileExt = filterExt;
		String basePath = context.getRealPath("/");
		String classesPath = basePath + "/WEB-INF/classes";
		File dir = new File(classesPath+relativePath);
		String[] list;
		if (filterExt != null) {
			FilenameFilter filter = new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.contains("."+fFileExt);
				}
			};
			list = dir.list(filter);
		} else {
			list = dir.list();
		}
		List<String> result = new ArrayList<String>();
		if (list != null) {
			for (String s:list) result.add(s);
		}
		return result;
	}
	

	@Override
	public void service(ServletRequest request, ServletResponse response)
			throws ServletException, IOException {
		context = request.getServletContext();
		try {
			this.rescanFiles();
		} catch (BusinessException e) {
			throw new ServletException(e.getMessage(), e);
		}
	}


}
