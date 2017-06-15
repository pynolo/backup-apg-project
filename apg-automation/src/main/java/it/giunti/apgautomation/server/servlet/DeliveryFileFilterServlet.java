package it.giunti.apgautomation.server.servlet;

import it.giunti.apg.server.ServerConstants;
import it.giunti.apg.server.business.DeliveryFileFilterBusiness;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeliveryFileFilterServlet extends HttpServlet {
	private static final long serialVersionUID = -2615869314482423040L;
	
	private static final Logger LOG = LoggerFactory.getLogger(DeliveryFileFilterServlet.class);
	
	public DeliveryFileFilterServlet() {
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
		//String idUtente = "";
		Map<String, File> fileMap = new HashMap<String, File>();
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
						////is a form field, not a file
						//String paramName = item.getFieldName();
						//String paramValue = item.getString();
						//if (paramName != null) {
						//	if (paramName.equals(AppConstants.PARAM_ID_UTENTE)) {
						//		idUtente = paramValue;
						//	}
						//	if (paramName.equals(AppConstants.PARAM_ID_RAPPORTO)) {
						//		idRapporto = ValueUtil.stoi(paramValue);
						//	}
						//}
					} else {
						//Is a file
						String fileName = item.getName();
						// get only the file name not whole path
						if (fileName != null) {
							fileName = FilenameUtils.getName(fileName);
						}
						File uploadedFile = new File(ServerConstants.UPLOAD_DIRECTORY, fileName);
						uploadedFile.deleteOnExit();
						if (uploadedFile.exists()) {
							uploadedFile.delete();
						}
						uploadedFile.createNewFile();
						item.write(uploadedFile);
						//resp.setStatus(HttpServletResponse.SC_CREATED);
						//resp.getWriter().print("The file was created successfully.");
						//resp.flushBuffer();
						//Aggiunge il contenuto del file
						fileMap.put(item.getFieldName(), uploadedFile);
					}
				}
			} catch (Exception e) {
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"An error occurred while creating the file : " + e.getMessage());
				throw new ServletException(e);
			}
		} else {
			resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
					"Request contents type is not supported by the servlet.");
			throw new ServletException("Unsupported media type");
		}
		List<File> resultList = new ArrayList<File>();
		try {
			File fileUnione1 = fileMap.get(AppConstants.PARAM_UNIONE_1);
			File fileUnione2 = fileMap.get(AppConstants.PARAM_UNIONE_2);
			if ((fileUnione1 != null) && (fileUnione2 != null)) {
				List<File> result = DeliveryFileFilterBusiness.merge(fileUnione1, fileUnione2);
				resultList.addAll(result);
			}
			File fileDifferenza1 = fileMap.get(AppConstants.PARAM_DIFFERENZA_1);
			File fileDifferenza2 = fileMap.get(AppConstants.PARAM_DIFFERENZA_2);
			if ((fileDifferenza1 != null) && (fileDifferenza2 != null)) {
				List<File> result = DeliveryFileFilterBusiness.diff(fileDifferenza1, fileDifferenza2);
				resultList.addAll(result);
			}
		} catch (BusinessException e) {
			LOG.error(e.getMessage(), e);
			throw new ServletException(e.getMessage(), e);
		}
		
		ServletOutputStream sos = resp.getOutputStream();
        resp.setContentType("application/zip");
        resp.setHeader("Content-Disposition", "attachment; filename=\""+
        		ServerConstants.FORMAT_FILE_NAME_TIMESTAMP.format(new Date())+
        		"_etichetteFiltrate.zip\"");
        byte[] zip = zipFiles(resultList);
        
        sos.write(zip);
        sos.flush();
        resp.flushBuffer();
	}
	
    /**
     * Compress the given directory with all its files.
     */
    private byte[] zipFiles(List<File> fileList) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        byte bytes[] = new byte[2048];
 
        for (File f:fileList) {
            FileInputStream fis = new FileInputStream(f);
            BufferedInputStream bis = new BufferedInputStream(fis);
            zos.putNextEntry(new ZipEntry(f.getName()));
            int bytesRead;
            while ((bytesRead = bis.read(bytes)) != -1) {
                zos.write(bytes, 0, bytesRead);
            }
            zos.closeEntry();
            bis.close();
            fis.close();
        }
        zos.flush();
        baos.flush();
        zos.close();
        baos.close();
 
        return baos.toByteArray();
    }
}