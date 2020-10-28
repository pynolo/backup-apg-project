package it.giunti.apg.ws.api04;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

import it.giunti.apg.core.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.ApiServices;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Pagamenti;
import it.giunti.apg.ws.business.ValidationBusiness;

/*@WebServlet(Constants.PATTERN_API04+Constants.PATTERN_FIND_SUBSCRIPTION_STATUS_CHANGES)*/
public class FindSubscriptionStatusChangesServlet extends ApiServlet {
	private static final long serialVersionUID = 8069064863495404345L;
	
	private static final String FUNCTION_NAME = Constants.PATTERN_FIND_SUBSCRIPTION_STATUS_CHANGES;
	private static final Logger LOG = LoggerFactory.getLogger(FindSubscriptionStatusChangesServlet.class);

	/*example testing url:
	http://127.0.0.1:8080/apgws/api02/find_subscriptions_by_action?access_key=1234&id_magazine=Q&dt_begin=2017-12-27&dt_end=2018-01-03&action=RENEWAL_WARNING&page=0
	*/
	
	private static final int PAGE_SIZE = 250;

    public FindSubscriptionStatusChangesServlet() {
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
		//acquire begin_timestamp
		String beginTsString = request.getParameter(Constants.PARAM_BEGIN_TIMESTAMP);
		if (beginTsString == null) result = BaseJsonFactory.buildBaseObject(ErrorEnum.EMPTY_PARAMETER, Constants.PARAM_BEGIN_TIMESTAMP+" is empty");
		Long beginTimestamp = 0L;
		try {
			beginTimestamp = Long.parseLong(beginTsString);
		} catch (NumberFormatException e1) { }
		if (beginTimestamp < 1L) {
			result = BaseJsonFactory.buildBaseObject(ErrorEnum.EMPTY_PARAMETER, Constants.PARAM_BEGIN_TIMESTAMP+" is invalid");
		}
		//acquire page
		Integer page = null;
		String pageS = request.getParameter(Constants.PARAM_PAGE);
		if (pageS == null) {
			result = BaseJsonFactory.buildBaseObject(ErrorEnum.EMPTY_PARAMETER, Constants.PARAM_PAGE+" is empty");
		} else {
			try {
				page = Integer.parseInt(pageS);
			} catch (NumberFormatException e) {
				result = BaseJsonFactory.buildBaseObject(ErrorEnum.WRONG_PARAMETER_VALUE, Constants.PARAM_PAGE+" wrong format");
			}
			if (page < 0) BaseJsonFactory.buildBaseObject(ErrorEnum.WRONG_PARAMETER_VALUE, Constants.PARAM_PAGE+" negative value");
		}
		
		//build response
		if (result == null) {
			//Verify timeframe
			Long timeframe = new Date().getTime()-beginTimestamp;
			Double days = timeframe.doubleValue()/AppConstants.DAY;
			if (days > 31) {
				result = BaseJsonFactory.buildBaseObject(ErrorEnum.WRONG_PARAMETER_VALUE, "Time frame is spanning more than one month");
			}
			
			Session ses = SessionFactory.getSession();
			IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
			try {
				Date beginTime = new Date(beginTimestamp);
				Map<IstanzeAbbonamenti, Pagamenti> iaMap = iaDao.findIstanzePagamentiByChangedStatus(ses, beginTime, page*PAGE_SIZE, PAGE_SIZE);
				
				JsonObjectBuilder joBuilder = schemaBuilder(ses, iaMap);
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

	private JsonObjectBuilder schemaBuilder(Session ses, Map<IstanzeAbbonamenti, Pagamenti> iaMap) 
			throws BusinessException {
		JsonBuilderFactory factory = Json.createBuilderFactory(null);
		JsonArrayBuilder arrayBuilder = factory.createArrayBuilder();
		List<IstanzeAbbonamenti> orderedIaList = new ArrayList<IstanzeAbbonamenti>();
		
		//Ordinamento per update_timestamp asc
		for (IstanzeAbbonamenti ia:iaMap.keySet()) orderedIaList.add(ia);
		Collections.sort(orderedIaList, new Comparator<IstanzeAbbonamenti>() {
			@Override
			public int compare(IstanzeAbbonamenti ia1, IstanzeAbbonamenti ia2) {
				if (ia1.getUpdateTimestamp().equals(ia2.getUpdateTimestamp())) return 0;
				if (ia1.getUpdateTimestamp().after(ia2.getUpdateTimestamp())) return 1;				
				return -1;
			}
		});
		
		for (IstanzeAbbonamenti ia:orderedIaList) {
			JsonObjectBuilder ob = factory.createObjectBuilder();
			add(ob, Constants.PARAM_COD_ABBO, ia.getAbbonamento().getCodiceAbbonamento());
			add(ob, Constants.PARAM_ID_SUBSCRIPTION, ia.getId());
			add(ob, Constants.PARAM_ID_MAGAZINE, ia.getAbbonamento().getPeriodico().getUid());
			add(ob, Constants.PARAM_ID_CUSTOMER_RECIPIENT, ia.getAbbonato().getUid());
			if (ia.getPagante() != null) {
				add(ob, Constants.PARAM_ID_CUSTOMER_PAYER, ia.getPagante().getUid());
			}
			add(ob, "modified_timestamp", ia.getUpdateTimestamp().getTime());
			add(ob, "is_blocked", ia.getInvioBloccato());
			add(ob, "is_cancelled", (ia.getDataDisdetta() != null));
			add(ob, "is_paid", ia.getPagato());
			
			Pagamenti pag = iaMap.get(ia);
			if (pag != null)
				if (pag.getIdTipoPagamento() != null) 
					add(ob, "id_payment_type", pag.getIdTipoPagamento());
			arrayBuilder.add(ob);
		}
		
		JsonObjectBuilder objectBuilder = factory.createObjectBuilder();
		objectBuilder.add("subscriptions", arrayBuilder);
		return objectBuilder;
	}
	
}
