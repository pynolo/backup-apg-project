package it.giunti.apg.ws.api03;

import it.giunti.apg.core.persistence.AnagraficheDao;
import it.giunti.apg.core.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.ApiServices;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.ws.business.ValidationBusiness;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

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

/*@WebServlet(Constants.PATTERN_API01+Constants.PATTERN_GET_CUSTOMER_SUBSCRIPTIONS)*/
public class GetCustomerSubscriptionsServlet extends ApiServlet {
	private static final long serialVersionUID = 328810654231294249L;
	private static final String FUNCTION_NAME = Constants.PATTERN_GET_CUSTOMER_SUBSCRIPTIONS;
	private static final Logger LOG = LoggerFactory.getLogger(GetCustomerSubscriptionsServlet.class);

	/*example testing url:
	 http://127.0.0.1:8888/api01/get_customer_subscriptions?access_key=1234&id_customer=Q090NQ
	*/

    public GetCustomerSubscriptionsServlet() {
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
		//acquire idSubscription
		String idCustomer = request.getParameter(Constants.PARAM_ID_CUSTOMER);
		if (idCustomer == null) {
			result = BaseJsonFactory.buildBaseObject(ErrorEnum.EMPTY_PARAMETER, Constants.PARAM_ID_CUSTOMER+" is empty");
		}
		//build response
		if (result == null) {
			Session ses = SessionFactory.getSession();
			try {
				Anagrafiche ana = new AnagraficheDao().findByUid(ses, idCustomer);
				if (ana == null) throw new BusinessException(idCustomer+" has no match");
				List<IstanzeAbbonamenti> iaProprieList = new IstanzeAbbonamentiDao().findIstanzeProprieByAnagrafica(ses, ana.getId(), false, 0, Integer.MAX_VALUE);
				List<IstanzeAbbonamenti> iaRegalateList = new IstanzeAbbonamentiDao().findIstanzeRegalateByAnagrafica(ses, ana.getId(), false, 0, Integer.MAX_VALUE);
				List<IstanzeAbbonamenti> iaList = new ArrayList<IstanzeAbbonamenti>();
				//iaList.addAll(filterIstanze(iaProprieList));
				//iaList.addAll(filterIstanze(iaRegalateList));
				iaList.addAll(iaProprieList);
				iaList.addAll(iaRegalateList);
				JsonObjectBuilder joBuilder = schemaBuilder(iaList);
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

	private JsonObjectBuilder schemaBuilder(List<IstanzeAbbonamenti> iaList) throws BusinessException {
		JsonBuilderFactory factory = Json.createBuilderFactory(null);
		JsonArrayBuilder arrayBuilder = factory.createArrayBuilder();
		for (IstanzeAbbonamenti ia:iaList) {
			JsonObjectBuilder ob = factory.createObjectBuilder();
			add(ob, Constants.PARAM_COD_ABBO, ia.getAbbonamento().getCodiceAbbonamento());
			add(ob, Constants.PARAM_ID_SUBSCRIPTION, ia.getId());
			add(ob, Constants.PARAM_ID_MAGAZINE, ia.getAbbonamento().getPeriodico().getUid());
			add(ob, Constants.PARAM_ID_OFFERING, ia.getListino().getUid());
			add(ob, Constants.PARAM_ID_CUSTOMER_RECIPIENT, ia.getAbbonato().getUid());
			if (ia.getPagante() != null) add(ob, Constants.PARAM_ID_CUSTOMER_PAYER, ia.getPagante().getUid());
			add(ob, "subscription_begin", ia.getFascicoloInizio().getDataInizio());
			add(ob, "subscription_end", ia.getFascicoloFine().getDataFine());
			//add(ob, "is_paid", IstanzeStatusBusiness.isFatturato(ia));
			//add(ob, "is_deferred_bill", IstanzeStatusBusiness.isFatturato(ia));
			//add(ob, "is_free_gift", IstanzeStatusBusiness.isOmaggio(ia));
			//add(ob, "is_blocked", ia.getInvioBloccato());
			//add(ob, "cancellation_request_date", ia.getDataDisdetta());
			arrayBuilder.add(ob);
		}
		JsonObjectBuilder objectBuilder = factory.createObjectBuilder();
		objectBuilder.add("subscriptions", arrayBuilder);
		return objectBuilder;
	}

//	private List<IstanzeAbbonamenti> filterIstanze(List<IstanzeAbbonamenti> iaList) {
//		List<IstanzeAbbonamenti> resultList = new ArrayList<IstanzeAbbonamenti>();
//		Map<String, IstanzeAbbonamenti> istanzeMap = new HashMap<String, IstanzeAbbonamenti>();
//		Date now = DateUtil.now();
//		for (IstanzeAbbonamenti ia:iaList) {
//			if (!ia.getInvioBloccato()) {
//				if (ia.getFascicoloInizio().getDataInizio().after(now)) {
//					//Abbonamento futuro
//					resultList.add(ia);
//				} else {
//					//Abbonamento corrente o concluso
//					String lettera = ia.getAbbonamento().getPeriodico().getLettera();
//					IstanzeAbbonamenti mapIa = istanzeMap.get(lettera);
//					if (mapIa == null) {
//						istanzeMap.put(lettera, ia);
//					} else {
//						if (mapIa.getFascicoloInizio().getDataInizio().before(ia.getFascicoloInizio().getDataInizio())) {
//							//mapIa è precedente ad ia
//							istanzeMap.put(lettera, ia);
//						} //else: non sostituisce mapIa nella mappa
//					}
//				}
//			}
//		}
//		//Copia la mappa nell'array finale
//		for (String l:istanzeMap.keySet()) {
//			resultList.add(istanzeMap.get(l));
//		}
//		return resultList;
//	}
}
