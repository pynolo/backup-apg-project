package it.giunti.apg.ws.api03;

import it.giunti.apg.core.persistence.AnagraficheDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.ApiServices;
import it.giunti.apg.ws.business.ValidationBusiness;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Date;
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

/*@WebServlet(Constants.PATTERN_API01+Constants.PATTERN_FIND_MODIFIED_CUSTOMERS)*/
public class FindModifiedCustomersServlet extends ApiServlet {
	private static final long serialVersionUID = -344001091822280260L;
	private static final String FUNCTION_NAME = Constants.PATTERN_FIND_MODIFIED_CUSTOMERS;
	private static final Logger LOG = LoggerFactory.getLogger(FindModifiedCustomersServlet.class);

	/*example testing url:
	http://127.0.0.1:8080/apgws/api02/find_subscriptions_by_action?access_key=1234&id_magazine=Q&dt_begin=2017-12-27&dt_end=2018-01-03&action=RENEWAL_WARNING&page=0
	*/
	
	private static final int PAGE_SIZE = 250;

    public FindModifiedCustomersServlet() {
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
			Date dtEnd = DateUtil.now();
			//Verify timeframe
			Long timeframe = dtEnd.getTime()-dtBegin.getTime();
			Double days = timeframe.doubleValue()/AppConstants.DAY;
			if (days > 31) {
				result = BaseJsonFactory.buildBaseObject(ErrorEnum.WRONG_PARAMETER_VALUE, "Time frame is spanning more than one month");
			}
			int offset = page * PAGE_SIZE;
			Session ses = SessionFactory.getSession();
			try {
				List<Anagrafiche> anaList = new AnagraficheDao()
						.findModifiedSinceDate(ses, dtBegin, offset, PAGE_SIZE);
				
				JsonObjectBuilder joBuilder = schemaBuilder(ses, anaList);
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

	private JsonObjectBuilder schemaBuilder(Session ses, List<Anagrafiche> anaList) 
			throws BusinessException {
		JsonBuilderFactory factory = Json.createBuilderFactory(null);
		JsonArrayBuilder arrayBuilder = factory.createArrayBuilder();
		for (Anagrafiche ana:anaList) {
			JsonObjectBuilder ob = factory.createObjectBuilder();
			add(ob, Constants.PARAM_ID_CUSTOMER, ana.getUid());
			add(ob, Constants.PARAM_ADDRESS_TITLE, ana.getIndirizzoPrincipale().getTitolo());
			add(ob, Constants.PARAM_ADDRESS_FIRST_NAME, ana.getIndirizzoPrincipale().getNome());
			add(ob, Constants.PARAM_ADDRESS_LAST_NAME_COMPANY, ana.getIndirizzoPrincipale().getCognomeRagioneSociale());
			add(ob, Constants.PARAM_ADDRESS_CO, ana.getIndirizzoPrincipale().getPresso());
			add(ob, Constants.PARAM_ADDRESS_ADDRESS, ana.getIndirizzoPrincipale().getIndirizzo());
			add(ob, Constants.PARAM_ADDRESS_LOCALITY, ana.getIndirizzoPrincipale().getLocalita());
			add(ob, Constants.PARAM_ADDRESS_PROVINCE, ana.getIndirizzoPrincipale().getProvincia());
			add(ob, Constants.PARAM_ADDRESS_ZIP, ana.getIndirizzoPrincipale().getCap());
			add(ob, Constants.PARAM_ADDRESS_COUNTRY_CODE, ana.getIndirizzoPrincipale().getNazione().getSiglaNazione());
			add(ob, Constants.PARAM_SEX, ana.getSesso());
			add(ob, Constants.PARAM_COD_FISC, ana.getCodiceFiscale());
			add(ob, Constants.PARAM_PIVA, ana.getPartitaIva());
			add(ob, Constants.PARAM_PHONE_MOBILE, ana.getTelMobile());
			add(ob, Constants.PARAM_PHONE_LANDLINE, ana.getTelCasa());
			add(ob, Constants.PARAM_EMAIL_PRIMARY, ana.getEmailPrimaria());
			if (ana.getProfessione() != null)
				add(ob, Constants.PARAM_ID_JOB, ana.getProfessione().getId());
			if (ana.getTitoloStudio() != null)
				add(ob, Constants.PARAM_ID_QUALIFICATION, ana.getTitoloStudio().getId());
			add(ob, Constants.PARAM_BIRTH_DATE, ana.getDataNascita());
			add(ob, Constants.PARAM_CONSENT_TOS, ana.getConsensoTos());
			add(ob, Constants.PARAM_CONSENT_MARKETING, ana.getConsensoMarketing());
			add(ob, Constants.PARAM_CONSENT_PROFILING, ana.getConsensoProfilazione());
			add(ob, Constants.PARAM_CONSENT_UPDATE_DATE, ana.getDataAggiornamentoConsenso());
			add(ob, "modified_date", ana.getDataModifica());
			arrayBuilder.add(ob);
		}
		JsonObjectBuilder objectBuilder = factory.createObjectBuilder();
		objectBuilder.add("customers", arrayBuilder);
		return objectBuilder;
	}

}
