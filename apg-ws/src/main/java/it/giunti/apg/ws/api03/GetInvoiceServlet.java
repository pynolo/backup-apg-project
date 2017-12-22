package it.giunti.apg.ws.api03;

import it.giunti.apg.core.persistence.FattureDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.model.ApiServices;
import it.giunti.apg.shared.model.Fatture;
import it.giunti.apg.shared.model.FattureStampe;
import it.giunti.apg.ws.business.ValidationBusiness;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetInvoiceServlet extends ApiServlet {
	private static final long serialVersionUID = 6259075901788538264L;
	private static final String FUNCTION_NAME = Constants.PATTERN_GET_INVOICE;
	private static final Logger LOG = LoggerFactory.getLogger(GetInvoiceServlet.class);

	/*example testing url:
	 http://localhost:8080/apgws/api03/get_invoice?access_key=1234&id_invoice=FXS6017637
	 */
	/**
     * @see HttpServlet#HttpServlet()
     */
    public GetInvoiceServlet() {
        super();
        LOG.info(FUNCTION_NAME+" started");
    }
	
	//@Override
	//protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	//		throws ServletException, IOException {
	//	doPost(req, resp);
	//}
	
	//Process the HTTP POST request
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		BaseUrlSingleton.get().setBaseUrl(request);
		JsonObject result = null;
		//acquire access key
		String accessKey = request.getParameter(Constants.PARAM_ACCESS_KEY);
		ApiServices service = null;
		if (accessKey == null) {
			result = BaseJsonFactory.buildBaseObject(ErrorEnum.WRONG_ACCESS_KEY, ErrorEnum.WRONG_ACCESS_KEY.getErrorDescr());
		} else {
			try {
				service = ValidationBusiness.validateAccessKey(accessKey);
			} catch (BusinessException e) {
				result = BaseJsonFactory.buildBaseObject(ErrorEnum.INTERNAL_ERROR, ErrorEnum.INTERNAL_ERROR.getErrorDescr());
				LOG.error(e.getMessage(), e);
			}
			if (service == null) {
				result = BaseJsonFactory.buildBaseObject(ErrorEnum.WRONG_ACCESS_KEY, ErrorEnum.WRONG_ACCESS_KEY.getErrorDescr());
			} else {
				LOG.debug(FUNCTION_NAME+" chiamata da "+service.getNome());
			}
		}
		//id_invoice
		String idString = request.getParameter(Constants.PARAM_ID_INVOICE);
		try {
			idString = ValidationBusiness.cleanInput(idString, 20);
		} catch (ValidationException e1) {
			result = BaseJsonFactory.buildBaseObject(ErrorEnum.WRONG_PARAMETER_VALUE, Constants.PARAM_ID_INVOICE+" wrong format");
		}
		if (idString == null) result = BaseJsonFactory.buildBaseObject(ErrorEnum.EMPTY_PARAMETER, Constants.PARAM_ID_INVOICE+" is empty");
		if (idString.length() == 0) result = BaseJsonFactory.buildBaseObject(ErrorEnum.EMPTY_PARAMETER, Constants.PARAM_ID_INVOICE+" is empty");
		
		//build response
		if (result == null) {
			Session ses = SessionFactory.getSession();
			try {
				List<Fatture> fList = new FattureDao().findByNumeroFattura(ses, idString);
				if (fList == null) throw new ServletException("Wrong id_invoice supplied");
				if (fList.size() == 0) throw new ServletException("Wrong id_invoice supplied");
				Fatture fat = fList.get(0);
				FattureStampe fs = null;
				if (fat.getIdFatturaStampa() != null) {
					fs = GenericDao.findById(ses, FattureStampe.class, fat.getIdFatturaStampa());
				} else {
					throw new BusinessException(Constants.PARAM_ID_INVOICE+" is invalid");
				}
				if (fs == null) throw new BusinessException(Constants.PARAM_ID_INVOICE+" no matching data available");
				if (fs.getContent() == null) 
						throw new BusinessException(Constants.PARAM_ID_INVOICE+" no data bundled in object");
				if (fs.getContent().length < 2) 
						throw new BusinessException(Constants.PARAM_ID_INVOICE+" no data bundled in object");
				if (fs.getMimeType() == null) 
						throw new BusinessException(Constants.PARAM_ID_INVOICE+" no file content type available");
				if (fs.getMimeType().length() < 2) 
						throw new BusinessException(Constants.PARAM_ID_INVOICE+" no file content type available");
				
				JsonObjectBuilder joBuilder = schemaBuilder(fat, fs);
				result = BaseJsonFactory.buildBaseObject(joBuilder);
			} catch (BusinessException e) {
				result = BaseJsonFactory.buildBaseObject(ErrorEnum.WRONG_PARAMETER_VALUE, e.getMessage());
				LOG.info(e.getMessage(), e);
			} catch (IOException e) {
				result = BaseJsonFactory.buildBaseObject(ErrorEnum.INTERNAL_ERROR, e.getMessage());
				LOG.info(e.getMessage(), e);
			} finally {
				ses.close();
			}
		}
		//send response
		response.setContentType("application/json");
		response.setCharacterEncoding(AppConstants.CHARSET_UTF8);
		PrintWriter out = response.getWriter();
		out.print(result.toString());
		out.flush();
	}

    private JsonObjectBuilder schemaBuilder(Fatture fat, FattureStampe fs) throws BusinessException, IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(fs.getContent());
		String pdfBase64String = StringUtils.newStringUtf8(Base64.encodeBase64(baos.toByteArray()));
		
		JsonBuilderFactory factory = Json.createBuilderFactory(null);
		JsonObjectBuilder ob = factory.createObjectBuilder();
		add(ob, Constants.PARAM_ID_INVOICE, fat.getNumeroFattura());
		add(ob, "mime_type", fs.getMimeType());
		add(ob, "file_name", fs.getFileName());
		add(ob, "content_base64", pdfBase64String);
		return ob;
	}

}
