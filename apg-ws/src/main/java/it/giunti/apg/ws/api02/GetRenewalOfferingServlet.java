package it.giunti.apg.ws.api02;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.persistence.ListiniDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.core.persistence.TipiAbbonamentoRinnovoDao;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.model.ApiServices;
import it.giunti.apg.shared.model.Listini;
import it.giunti.apg.shared.model.TipiAbbonamento;
import it.giunti.apg.ws.business.ValidationBusiness;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Date;

import javax.json.Json;
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
/*@WebServlet(WsConstants.PATTERN_GET_OFFERING)*/
public class GetRenewalOfferingServlet extends ApiServlet {
	private static final long serialVersionUID = -8138282179035238530L;
	private static final String FUNCTION_NAME = Constants.PATTERN_GET_OFFERING;
	private static final Logger LOG = LoggerFactory.getLogger(GetRenewalOfferingServlet.class);
	
	/*example testing url:
	 http://127.0.0.1:8888/api01/get_offering?access_key=1234&id_offering=0501EC
	 */

    public GetRenewalOfferingServlet() {
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
		//acquire idOffering
		String idOffering = request.getParameter(Constants.PARAM_ID_OFFERING);
		if (idOffering == null) result = BaseJsonFactory.buildBaseObject(ErrorEnum.EMPTY_PARAMETER, Constants.PARAM_ID_OFFERING+" is empty");
		if (idOffering.length() == 0) result = BaseJsonFactory.buildBaseObject(ErrorEnum.EMPTY_PARAMETER, Constants.PARAM_ID_OFFERING+" is empty");
		
		//renewal_date
		Date renewalDate = null;
		try {
			String renewalDateS = request.getParameter(Constants.PARAM_RENEWAL_DATE);
			renewalDateS = ValidationBusiness.cleanInput(renewalDateS, 10);
			if (renewalDateS != null) {
				try {
					renewalDate = ServerConstants.FORMAT_DAY_SQL.parse(renewalDateS);
				} catch (ParseException e) { throw new ValidationException(Constants.PARAM_RENEWAL_DATE+" wrong format");}
			} else {
				throw new ValidationException(Constants.PARAM_RENEWAL_DATE+" is empty");
			}
			if (renewalDate == null) throw new ValidationException(Constants.PARAM_RENEWAL_DATE+" is empty");
		} catch (ValidationException e1) {
			result = BaseJsonFactory.buildBaseObject(ErrorEnum.WRONG_PARAMETER_VALUE, e1.getMessage());
		}
		
		//build response
		if (result == null) {
			Session ses = SessionFactory.getSession();
			ListiniDao lDao = new ListiniDao();
			try {
				Listini lst = lDao.findByUid(ses, idOffering);
				if (lst == null) throw new BusinessException(idOffering+" has no match");
				TipiAbbonamento taRinn = new TipiAbbonamentoRinnovoDao().findFirstTipoRinnovoByIdListino(ses, lst.getId());
				Listini lstRinn = null;
				if (taRinn != null)
					lstRinn = new ListiniDao().findListinoByTipoAbbDate(ses, taRinn.getId(), DateUtil.now());
				JsonObjectBuilder joBuilder = schemaBuilder(lst, lstRinn);
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
		PrintWriter out = response.getWriter();
		out.print(result.toString());
		out.flush();
	}

    private JsonObjectBuilder schemaBuilder(Listini lst, Listini lstRinnovo) throws BusinessException {
    	JsonBuilderFactory factory = Json.createBuilderFactory(null);
		JsonObjectBuilder ob = factory.createObjectBuilder();
		add(ob, Constants.PARAM_ID_OFFERING, lst.getUid());
		add(ob, Constants.PARAM_ID_MAGAZINE, lst.getTipoAbbonamento().getPeriodico().getUid());
		if (lstRinnovo != null)	
			add(ob, Constants.PARAM_RENEWAL_DATE, lstRinnovo.getUid());
		return ob;
	}
	
}
