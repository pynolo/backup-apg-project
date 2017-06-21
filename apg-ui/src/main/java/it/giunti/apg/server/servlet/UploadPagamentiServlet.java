package it.giunti.apg.server.servlet;

import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.FileUploads;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadPagamentiServlet extends HttpServlet {
	private static final long serialVersionUID = -2615869314482423040L;
	
	private static final Logger LOG = LoggerFactory.getLogger(UploadPagamentiServlet.class);
	
	public UploadPagamentiServlet() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doGet(req, resp);
	}

	//@SuppressWarnings("unchecked")
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//Integer idRapporto = null;
		String idUtente = "";
		List<File> fileList = new ArrayList<File>();
		// process only multipart requests
		if (ServletFileUpload.isMultipartContent(req)) {
			// Create a factory for disk-based file items
			FileItemFactory factory = new DiskFileItemFactory();
			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);
			// Parse the request
			try {
				List<FileItem> items = (List<FileItem>) upload.parseRequest(req);
				for (FileItem item : items) {
					// process only file upload - discard other form item types
					if (item.isFormField()) {
						//is a form field, not a file
						String paramName = item.getFieldName();
						String paramValue = item.getString();
						if (paramName != null) {
							if (paramName.equals(AppConstants.PARAM_ID_UTENTE)) {
								idUtente = paramValue;
							}
							//if (paramName.equals(AppConstants.PARAM_ID_RAPPORTO)) {
							//	idRapporto = ValueUtil.stoi(paramValue);
							//}
						}
					} else {
						//Is a file
						String fileName = item.getName();
						// get only the file name not whole path
						if (fileName != null) {
							fileName = FilenameUtils.getName(fileName);
						}
						File uploadedFile = File.createTempFile(fileName+"@", ".tmp");
						uploadedFile.deleteOnExit();
						if (uploadedFile.exists()) {
							uploadedFile.delete();
						}
						uploadedFile.createNewFile();
						item.write(uploadedFile);
						resp.setStatus(HttpServletResponse.SC_CREATED);
						resp.getWriter().print("The file was created successfully.");
						resp.flushBuffer();
						//Processa il contenuto del file
						fileList.add(uploadedFile);
					}
				}
			} catch (Exception e) {
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"An error occurred while creating the file : " + e.getMessage());
			}
		} else {
			resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
					"Request contents type is not supported by the servlet.");
		}
		for (File f:fileList) {
			try {
				saveUploadedFile(f, idUtente);
			} catch (BusinessException e) {
				LOG.error(e.getMessage(), e);
				throw new ServletException(e.getMessage(), e);
			}
		//	PagamentiImportBusiness business = new PagamentiImportBusiness();
		//	try {
		//		business.importPagamenti(f, idRapporto, idUtente);
		//	} catch (BusinessException e) {
		//		LOG.error(e.getMessage(), e);
		//		throw new ServletException(e);
		//	}
		}
	}
	
	private static void saveUploadedFile(File f, String idUtente) throws BusinessException {
		Session ses = SessionFactory.getSession();
		Transaction trx = ses.beginTransaction();
		try {
			FileUploads fu = new FileUploads();
			fu.setDataCreazione(new Date());
			fu.setIdUtente(idUtente);
			String fileName = f.getName();
			// get only the file name not whole path
			if (fileName != null) {
				fileName = FilenameUtils.getName(fileName);
				if (fileName.contains("@")) fileName = fileName.substring(0, fileName.indexOf("@"));
			}
			fu.setFileName(fileName);
			fu.setMimeType("text/plain");
			fu.setIdTipoFile(AppConstants.FILE_UPLOAD_BOLLETTINI);
			//File data
			byte[] content = fileToByteArray(f);
			fu.setContent(content);
			GenericDao.saveGeneric(ses, fu);
			trx.commit();
		} catch (HibernateException| IOException e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
	}
	
	private static byte[] fileToByteArray(File f) throws IOException {
		InputStream is = new FileInputStream(f);
		// Get the size of the file
		long length = f.length();
		// Create the byte array to hold the data
		byte[] bytes = new byte[(int)length];
		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
			offset += numRead;
		}
		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			if (is != null) is.close();
			throw new IOException("Could not completely read file "+f.getName());
		}
		// Close the input stream and return bytes
		is.close();
		return bytes; 
	}
}