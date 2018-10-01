package it.giunti.apg.ws.api04;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.ApiServices;
import it.giunti.apg.ws.business.ValidationBusiness;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetPaymentFormServlet extends ApiServlet {
	private static final long serialVersionUID = -992480649747526529L;
	private static final String FUNCTION_NAME = Constants.PATTERN_GET_PAYMENT_FORM;
	private static final Logger LOG = LoggerFactory.getLogger(GetPaymentFormServlet.class);

	private static final String SERVLET_URL ="/apgautomation/createbollettino";
	
	/*example testing url:
	 http://localhost:8080/apgws/api03/get_payment_form?access_key=1234&id_subscription=561304
	 */
	/**
     * @see HttpServlet#HttpServlet()
     */
    public GetPaymentFormServlet() {
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
		request.setCharacterEncoding(AppConstants.CHARSET_UTF8);
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
		//id_subscription
		String idString = request.getParameter(Constants.PARAM_ID_SUBSCRIPTION);
		Integer id = null;
		try {
			id = Integer.parseInt(idString);
		} catch (NumberFormatException e) {
			result = BaseJsonFactory.buildBaseObject(ErrorEnum.WRONG_PARAMETER_VALUE, Constants.PARAM_ID_SUBSCRIPTION+" wrong value");
		}
		if (idString == null) result = BaseJsonFactory.buildBaseObject(ErrorEnum.EMPTY_PARAMETER, Constants.PARAM_ID_SUBSCRIPTION+" is empty");
		if (idString.length() == 0) result = BaseJsonFactory.buildBaseObject(ErrorEnum.EMPTY_PARAMETER, Constants.PARAM_ID_SUBSCRIPTION+" is empty");
		
		//build response
		if (result == null) {
			try {
				URL servletUrl = new URL(request.getRequestURL().toString());
				String port = "";
				if (servletUrl.getPort() > 0) port = ":"+servletUrl.getPort();
				String url = servletUrl.getProtocol()+"://"+
						servletUrl.getHost()+port+
						SERVLET_URL+"?id="+id;
				String charset = StandardCharsets.UTF_8.name();
				LOG.debug("Forwarding to "+url);
				URLConnection connection = new URL(url).openConnection();
				connection.setRequestProperty("Accept-Charset", charset);
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
				String contentType = connection.getContentType();
				InputStream remoteResponse = connection.getInputStream();
				byte[] inBuffer = toByteArray(remoteResponse);
				
				JsonObjectBuilder joBuilder = schemaBuilder(id, contentType, inBuffer);
				result = BaseJsonFactory.buildBaseObject(joBuilder);
			} catch (BusinessException e) {
				result = BaseJsonFactory.buildBaseObject(ErrorEnum.WRONG_PARAMETER_VALUE, e.getMessage());
				LOG.info(e.getMessage(), e);
			} catch (IOException e) {
				result = BaseJsonFactory.buildBaseObject(ErrorEnum.INTERNAL_ERROR, e.getMessage());
				LOG.info(e.getMessage(), e);
			}
		}
		//send response
		response.setContentType("application/json");
		response.setCharacterEncoding(AppConstants.CHARSET_UTF8);
		PrintWriter out = response.getWriter();
		out.print(result.toString());
		out.flush();
	}

    private JsonObjectBuilder schemaBuilder(Integer idSubscription, String contentType, byte[] buffer) throws BusinessException, IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(buffer);
		String pdfBase64String = StringUtils.newStringUtf8(Base64.encodeBase64(baos.toByteArray()));
		String fileName = idSubscription+"_"+ServerConstants.FORMAT_FILE_NAME_TIMESTAMP.format(DateUtil.now())+".pdf";
		JsonBuilderFactory factory = Json.createBuilderFactory(null);
		JsonObjectBuilder ob = factory.createObjectBuilder();
		add(ob, Constants.PARAM_ID_SUBSCRIPTION, idSubscription);
		add(ob, "mime_type", contentType);
		add(ob, "file_name", fileName);
		add(ob, "content_base64", pdfBase64String);
		return ob;
	}

	public static byte[] toByteArray(InputStream is) throws IOException{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int reads = is.read();
		while(reads != -1){
			baos.write(reads);
			reads = is.read();
		}
		return baos.toByteArray();
	}
}
