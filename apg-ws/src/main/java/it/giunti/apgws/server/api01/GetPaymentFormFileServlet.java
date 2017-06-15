package it.giunti.apgws.server.api01;

import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.ApiServices;
import it.giunti.apgws.server.business.ValidationBusiness;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetPaymentFormFileServlet extends HttpServlet {
	private static final long serialVersionUID = -7619212397741425084L;
	private static final String FUNCTION_NAME = Constants.PATTERN_GET_PAYMENT_FORM_FILE;
	private static final Logger LOG = LoggerFactory.getLogger(GetPaymentFormFileServlet.class);

	private static final String SERVLET_URL ="/apgautomation/createbollettino";
	
	/*example testing url:
	 http://127.0.0.1:8888/api01/get_payment_form_file?access_key=1234&id_subscription=561304
	 */
	/**
     * @see HttpServlet#HttpServlet()
     */
    public GetPaymentFormFileServlet() {
        super();
        LOG.info(FUNCTION_NAME+" started");
    }

	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}
	
	//Process the HTTP Get request
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		//access key
		String accessKey = request.getParameter(Constants.PARAM_ACCESS_KEY);
		ApiServices service = null;
		if (accessKey == null) {
			throw new ServletException("Wrong access key");
		} else {
			try {
				service = ValidationBusiness.validateAccessKey(accessKey);
			} catch (BusinessException e) {
				throw new ServletException("Internal error");
			}
			if (service == null) {
				throw new ServletException("Wrong access key");
			} else {
				LOG.debug(FUNCTION_NAME+" chiamata da "+service.getNome());
			}
		}
		//id_subscription
		Integer id = null;
		String idString = request.getParameter(Constants.PARAM_ID_SUBSCRIPTION);
		try {
			id = Integer.parseInt(idString);
		} catch (NumberFormatException e) {
			throw new ServletException("No id_subscription supplied");
		}
		
		try {
			//http://stackoverflow.com/questions/2793150/using-java-net-urlconnection-to-fire-and-handle-http-requests
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
			
			PrintWriter out = new PrintWriter(response.getOutputStream());
			ServletOutputStream binout = response.getOutputStream();
			response.setContentType(contentType);
			response.setHeader("Content-Disposition", "attachment;filename="+idString+".pdf");
			binout.write(inBuffer);
			out.close();
		} catch (IOException e) {
			throw new ServletException(e.getMessage());
		}
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
