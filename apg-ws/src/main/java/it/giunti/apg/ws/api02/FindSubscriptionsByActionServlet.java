package it.giunti.apg.ws.api02;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.core.persistence.ListiniDao;
import it.giunti.apg.core.persistence.PeriodiciDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.core.persistence.TipiAbbonamentoRinnovoDao;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.ApiServices;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Listini;
import it.giunti.apg.shared.model.Periodici;
import it.giunti.apg.shared.model.TipiAbbonamento;
import it.giunti.apg.ws.business.ValidationBusiness;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
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

/*@WebServlet(Constants.PATTERN_API01+Constants.PATTERN_GET_CUSTOMER_SUBSCRIPTIONS)*/
public class FindSubscriptionsByActionServlet extends ApiServlet {
	private static final long serialVersionUID = 3891558646904498327L;
	private static final String FUNCTION_NAME = Constants.PATTERN_FIND_SUBSCRIPTIONS_BY_ACTION;
	private static final Logger LOG = LoggerFactory.getLogger(FindSubscriptionsByActionServlet.class);

	/*example testing url:
	 http://127.0.0.1:8080/apgws/api02/find_subscription_action_list?access_key=1234&id_magazine=Q&dt_begin=2017-11-28&dt_end=2017-12-02&page=0
	*/
	
	private static final int PAGE_SIZE = 250;
	private Map<String,String> renewalLisMap = new HashMap<String, String>();

    public FindSubscriptionsByActionServlet() {
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
		//acquire idMagazine
		String idMagazine = request.getParameter(Constants.PARAM_ID_MAGAZINE);
		if (idMagazine == null) result = BaseJsonFactory.buildBaseObject(ErrorEnum.EMPTY_PARAMETER, Constants.PARAM_ID_MAGAZINE+" is empty");
		//acquire action
		String action = request.getParameter(Constants.PARAM_ACTION);
		if (action == null) result = BaseJsonFactory.buildBaseObject(ErrorEnum.EMPTY_PARAMETER, Constants.PARAM_ACTION+" is empty");
		//acquire dtBegin
		Date dtBegin = null;
		String dtBeginS = request.getParameter(Constants.PARAM_DT_BEGIN);
		if (dtBeginS == null) {
			result = BaseJsonFactory.buildBaseObject(ErrorEnum.EMPTY_PARAMETER, Constants.PARAM_DT_BEGIN+" is empty");
		} else {
			try {
				dtBegin = Constants.FORMAT_API_DATE.parse(dtBeginS);
			} catch (ParseException e1) {
				result = BaseJsonFactory.buildBaseObject(ErrorEnum.WRONG_PARAMETER_VALUE, Constants.PARAM_DT_BEGIN+" wrong format");
			}
	    }
		//acquire dtEnd
		Date dtEnd = null;
		String dtEndS = request.getParameter(Constants.PARAM_DT_END);
		if (dtEndS == null) {
			result = BaseJsonFactory.buildBaseObject(ErrorEnum.EMPTY_PARAMETER, Constants.PARAM_DT_END+" is empty");
		} else {
			try {
				dtEnd = Constants.FORMAT_API_DATE.parse(dtEndS);
			} catch (ParseException e1) {
				result = BaseJsonFactory.buildBaseObject(ErrorEnum.WRONG_PARAMETER_VALUE, Constants.PARAM_DT_END+" wrong format");
			}
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
		}
		
		//Verify timeframe
		Long timeframe = dtEnd.getTime()-dtBegin.getTime();
		Double days = timeframe.doubleValue()/AppConstants.DAY;
		if (days > 31) {
			result = BaseJsonFactory.buildBaseObject(ErrorEnum.WRONG_PARAMETER_VALUE, "Time frame is spanning more than one month");
		}
		
		//build response
		if (result == null) {
			Session ses = SessionFactory.getSession();
			try {
				Calendar cal = new GregorianCalendar();
				Periodici p = new PeriodiciDao().findByUid(ses, idMagazine);
				//Listini per cui ci sono abbonamenti, ad oggi
				List<Listini> activeListiniList =
						new ListiniDao().findActiveListiniByTimeFrame(ses, p.getId(), dtBegin, dtEnd);
				List<IstanzeAbbonamenti> iaList = new ArrayList<IstanzeAbbonamenti>();
				int offset = PAGE_SIZE * page;
				
				for (Listini lst:activeListiniList) {
					Integer delta = null;
					if (action.equalsIgnoreCase(Constants.VALUE_ACTION_CHARGE_WARNING))
						delta = lst.getDeltaInizioAvvisoPagamento();
					if (action.equalsIgnoreCase(Constants.VALUE_ACTION_CHARGE))
						delta = lst.getDeltaInizioPagamentoAutomatico();
					if (action.equalsIgnoreCase(Constants.VALUE_ACTION_RENEWAL_WARNING))
						delta = lst.getDeltaFineAvvisoRinnovo();
					if (action.equalsIgnoreCase(Constants.VALUE_ACTION_RENEWAL))
						delta = lst.getDeltaFineRinnovoAutomatico();
					if (delta != null) {
						cal.setTime(dtBegin);
						cal.add(Calendar.DAY_OF_MONTH, (-1)*delta);
						Date inizioDt = cal.getTime();
						cal.setTime(dtEnd);
						cal.add(Calendar.DAY_OF_MONTH, (-1)*delta);
						Date fineDt = cal.getTime();
						//Ricerca abbonamenti attivi con fine compresa tra inizioDt e fineDt
						List<IstanzeAbbonamenti> iaL = null;
						if (action.equalsIgnoreCase(Constants.VALUE_ACTION_CHARGE_WARNING) 
								|| action.equalsIgnoreCase(Constants.VALUE_ACTION_CHARGE)) {
							iaL = new IstanzeAbbonamentiDao().findActiveIstanzeByDataInizio(ses,
									lst.getId(), inizioDt, fineDt, false, offset, PAGE_SIZE);
						}
						if (action.equalsIgnoreCase(Constants.VALUE_ACTION_RENEWAL_WARNING) 
								|| action.equalsIgnoreCase(Constants.VALUE_ACTION_RENEWAL)) {
							iaL = new IstanzeAbbonamentiDao().findActiveIstanzeByDataFine(ses,
									lst.getId(), inizioDt, fineDt, false, offset, PAGE_SIZE);
						}
						if (iaL != null) iaList.addAll(iaL);
						LOG.debug("Dates: "+ServerConstants.FORMAT_DAY.format(inizioDt)+" - "+
								ServerConstants.FORMAT_DAY.format(fineDt)+ " Tipo "+lst.getUid()+
								": "+iaL.size());
					}
				}
				
				JsonObjectBuilder joBuilder = schemaBuilder(ses, iaList);
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

	private JsonObjectBuilder schemaBuilder(Session ses, List<IstanzeAbbonamenti> iaList) 
			throws BusinessException {
		JsonBuilderFactory factory = Json.createBuilderFactory(null);
		JsonArrayBuilder arrayBuilder = factory.createArrayBuilder();
		String renewalLisUid = null;
		for (IstanzeAbbonamenti ia:iaList) {
			renewalLisUid = getRenewalListinoUid(ses, ia.getListino(), ia.getFascicoloFine().getDataFine());
			JsonObjectBuilder ob = factory.createObjectBuilder();
			add(ob, Constants.PARAM_COD_ABBO, ia.getAbbonamento().getCodiceAbbonamento());
			add(ob, Constants.PARAM_ID_SUBSCRIPTION, ia.getId());
			add(ob, Constants.PARAM_ID_MAGAZINE, ia.getAbbonamento().getPeriodico().getUid());
			add(ob, Constants.PARAM_ID_OFFERING, ia.getListino().getUid());
			if (renewalLisUid != null) 
				add(ob, Constants.PARAM_ID_RENEWAL_OFFERING, renewalLisUid);
			add(ob, Constants.PARAM_ID_CUSTOMER_RECIPIENT, ia.getAbbonato().getUid());
			add(ob, "email_recipient", ia.getAbbonato().getEmailPrimaria());
			if (ia.getPagante() != null) {
				add(ob, Constants.PARAM_ID_CUSTOMER_PAYER, ia.getPagante().getUid());
				add(ob, "email_payer", ia.getPagante().getEmailPrimaria());
			}
			add(ob, "subscription_begin", ia.getFascicoloInizio().getDataInizio());
			add(ob, "subscription_end", ia.getFascicoloFine().getDataFine());
			arrayBuilder.add(ob);
		}
		JsonObjectBuilder objectBuilder = factory.createObjectBuilder();
		objectBuilder.add("subscriptions", arrayBuilder);
		return objectBuilder;
	}

	private String getRenewalListinoUid(Session ses, Listini oldLis, Date oldEndDate) throws BusinessException {
		Calendar cal = new GregorianCalendar();
		cal.setTime(oldEndDate);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		Date dt = cal.getTime();
		String key = oldLis.getUid()+ServerConstants.FORMAT_DAY.format(dt);
		String renewalLisUid = renewalLisMap.get(key);
		if (renewalLisUid == null) {
			TipiAbbonamento taRinn = new TipiAbbonamentoRinnovoDao().findFirstTipoRinnovoByIdListino(ses, oldLis.getId());
			Listini newLis = null;
			if (taRinn != null) {
				newLis = new ListiniDao().findListinoByTipoAbbDate(ses, taRinn.getId(), dt);
			}
			if (newLis != null) {
				renewalLisUid = newLis.getUid();
				renewalLisMap.put(key, newLis.getUid());
			} else {
				renewalLisUid = null;
				renewalLisMap.put(key, "");
			}
		} else {
			if (renewalLisUid.equals("")) renewalLisUid = null;
		}
		return renewalLisUid;
	}
}
