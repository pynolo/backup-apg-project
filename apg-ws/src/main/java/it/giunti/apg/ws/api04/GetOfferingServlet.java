package it.giunti.apg.ws.api04;

import it.giunti.apg.core.persistence.ListiniDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.ApiServices;
import it.giunti.apg.shared.model.Listini;
import it.giunti.apg.shared.model.OpzioniListini;
import it.giunti.apg.ws.business.ValidationBusiness;

import java.io.IOException;
import java.io.PrintWriter;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet implementation class FindIssuesServlet
 */
public class GetOfferingServlet extends ApiServlet {
	private static final long serialVersionUID = 4415621515382617461L;
	private static final String FUNCTION_NAME = Constants.PATTERN_GET_OFFERING;
	private static final Logger LOG = LoggerFactory.getLogger(GetOfferingServlet.class);
	
	/*example testing url:
	 http://127.0.0.1:8888/api01/get_offering?access_key=1234&id_offering=0501EC
	 */

    public GetOfferingServlet() {
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
		//acquire idOffering
		String idOffering = request.getParameter(Constants.PARAM_ID_OFFERING);
		if (idOffering == null) result = BaseJsonFactory.buildBaseObject(ErrorEnum.EMPTY_PARAMETER, Constants.PARAM_ID_OFFERING+" is empty");
		if (idOffering.length() == 0) result = BaseJsonFactory.buildBaseObject(ErrorEnum.EMPTY_PARAMETER, Constants.PARAM_ID_OFFERING+" is empty");
		
		//build response
		if (result == null) {
			Session ses = SessionFactory.getSession();
			ListiniDao lDao = new ListiniDao();
			try {
				Listini lst = lDao.findByUid(ses, idOffering);
				if (lst == null) throw new BusinessException(idOffering+" has no match");
				JsonObjectBuilder joBuilder = schemaBuilder(lst);
				result = BaseJsonFactory.buildBaseObject(joBuilder);
			} catch (BusinessException e) {
				result = BaseJsonFactory.buildBaseObject(ErrorEnum.DATA_NOT_FOUND, ErrorEnum.DATA_NOT_FOUND.getErrorDescr());
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

    private JsonObjectBuilder schemaBuilder(Listini lst) throws BusinessException {
    	JsonBuilderFactory factory = Json.createBuilderFactory(null);
		JsonObjectBuilder ob = factory.createObjectBuilder();
		add(ob, Constants.PARAM_ID_OFFERING, lst.getUid());
		add(ob, Constants.PARAM_ID_MAGAZINE, lst.getTipoAbbonamento().getPeriodico().getUid());
		add(ob, "name", lst.getTipoAbbonamento().getNome());
		add(ob, "price", lst.getPrezzo());
		add(ob, "included_issues_number", lst.getNumFascicoli());
		if (lst.getOpzioniListiniSet() != null) {
			if (lst.getOpzioniListiniSet().size() > 0) {
				JsonArrayBuilder ab = factory.createArrayBuilder();
				for (OpzioniListini ol:lst.getOpzioniListiniSet()) {
					JsonObjectBuilder b1 = factory.createObjectBuilder();
					add(b1, "id_option", ol.getOpzione().getUid());
					add(b1, "name", ol.getOpzione().getNome());
					ab.add(b1);
				}
				ob.add("included_options", ab);
			}
		}
		return ob;
	}
	
}
