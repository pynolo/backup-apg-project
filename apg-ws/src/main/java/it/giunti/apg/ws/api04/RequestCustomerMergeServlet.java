package it.giunti.apg.ws.api04;

import it.giunti.apg.core.business.WsLogBusiness;
import it.giunti.apg.core.persistence.AnagraficheDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.ApiServices;
import it.giunti.apg.ws.WsConstants;
import it.giunti.apg.ws.business.ValidationBusiness;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*@WebServlet(Constants.PATTERN_API04+Constants.PATTERN_REQUEST_CUSTOMER_MERGE)*/
public class RequestCustomerMergeServlet extends ApiServlet {
	private static final long serialVersionUID = -2610259046879366197L;
	private static final String FUNCTION_NAME = Constants.PATTERN_REQUEST_CUSTOMER_MERGE;
	private static final String SERVICE = WsConstants.SERVICE_API03;
	private static final Logger LOG = LoggerFactory.getLogger(RequestCustomerMergeServlet.class);

	/*example testing url:
	 http://127.0.0.1:8888/api01/request_customer_merge?access_key=1234&id_customer=A00018&id_customer_proposed=B00019
	 */
	/**
     * @see HttpServlet#HttpServlet()
     */
    public RequestCustomerMergeServlet() {
        super();
        LOG.info(FUNCTION_NAME+" started");
    }

	@Override
	public void init() throws ServletException {
		super.init();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	//@Override
	//protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	//	doPost(request, response);
	//}
    
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
		//idCustomer
		String idCustomer = request.getParameter(Constants.PARAM_ID_CUSTOMER);
		if (idCustomer == null) {
			result = BaseJsonFactory.buildBaseObject(ErrorEnum.EMPTY_PARAMETER, Constants.PARAM_ID_CUSTOMER+" is empty");
		}
		//idCustomerProposed
		String idCustomerProposed = request.getParameter(Constants.PARAM_ID_CUSTOMER_PROPOSED);
		if (idCustomerProposed == null) {
			result = BaseJsonFactory.buildBaseObject(ErrorEnum.EMPTY_PARAMETER, Constants.PARAM_ID_CUSTOMER_PROPOSED+" is empty");
		}
		
		if (result == null) {
			AnagraficheDao anaDao = new AnagraficheDao();
			//All parameters string for logging
			String allParameters = "";
			Map<String, String[]> paramMap = request.getParameterMap();
			for (String key:paramMap.keySet()) allParameters += key+"="+paramMap.get(key)[0].toString()+"&";
			if (allParameters.length()>1024) allParameters = allParameters.substring(0,1024);
			
			Session ses = SessionFactory.getSession();
			Transaction trn = ses.beginTransaction();
			try {
				//build response
				if (result == null) {
					Anagrafiche ana = anaDao.findByUid(ses, idCustomer);
					if (ana == null) anaDao.findByMergedUidCliente(ses, idCustomer);
					if (ana == null) throw new BusinessException(idCustomer+" has no match");
					Anagrafiche anaProp = anaDao.findByUid(ses, idCustomerProposed);
					if (anaProp == null) anaDao.findByMergedUidCliente(ses, idCustomerProposed);
					if (anaProp == null) throw new BusinessException(idCustomerProposed+" has no match");
					
					if (ana.getIdAnagraficaDaAggiornare() == null && 
							anaProp.getIdAnagraficaDaAggiornare() == null) {
						ana.setNecessitaVerifica(true);
						anaProp.setNecessitaVerifica(true);
						anaProp.setIdAnagraficaDaAggiornare(ana.getId());
						anaDao.update(ses, ana);
						anaDao.update(ses, anaProp);
						WsLogBusiness.writeWsLog(ses, SERVICE,
								FUNCTION_NAME, allParameters, WsConstants.SERVICE_OK);
						trn.commit();
						JsonObjectBuilder joBuilder = schemaBuilder(true);
						result = BaseJsonFactory.buildBaseObject(joBuilder);
					} else {
						result = BaseJsonFactory.buildBaseObject(ErrorEnum.INTERNAL_ERROR, "customer data has already been marked for a merge");
					}
				}
			} catch (BusinessException | HibernateException e) {
				trn.rollback();
				result = BaseJsonFactory.buildBaseObject(ErrorEnum.INTERNAL_ERROR, ErrorEnum.INTERNAL_ERROR.getErrorDescr());
				LOG.error(e.getMessage(), e);
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

	private JsonObjectBuilder schemaBuilder(boolean result) throws BusinessException {
		JsonBuilderFactory factory = Json.createBuilderFactory(null);
		JsonObjectBuilder ob = factory.createObjectBuilder();
		add(ob, "request_enqueued", result);
		return ob;
	}
	
}
