package it.giunti.apg.ws.api04;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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

import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.MaterialiProgrammazioneDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.ApiServices;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.MaterialiProgrammazione;
import it.giunti.apg.ws.business.ValidationBusiness;

/*@WebServlet(Constants.PATTERN_API04+Constants.PATTERN_GET_SUBSCRIPTION_ISSUES)*/
public class GetSubscriptionIssuesServlet extends ApiServlet {
	private static final long serialVersionUID = 7584079889676952638L;
	private static final String FUNCTION_NAME = Constants.PATTERN_GET_SUBSCRIPTION_ISSUES;
	private static final Logger LOG = LoggerFactory.getLogger(GetSubscriptionIssuesServlet.class);

	/*example testing url:
	 http://127.0.0.1:8888/api01/get_subscription_issues?access_key=1234&id_subscription=501088
	 */
	/**
     * @see HttpServlet#HttpServlet()
     */
    public GetSubscriptionIssuesServlet() {
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
		String idSubscriptionS = request.getParameter(Constants.PARAM_ID_SUBSCRIPTION);
		Integer idSubscription = null;
		if (idSubscriptionS == null) {
			result = BaseJsonFactory.buildBaseObject(ErrorEnum.EMPTY_PARAMETER, Constants.PARAM_ID_SUBSCRIPTION+" is empty");
		} else {
			try {
				idSubscription = Integer.parseInt(idSubscriptionS);
			} catch (NumberFormatException e1) {
				result = BaseJsonFactory.buildBaseObject(ErrorEnum.WRONG_PARAMETER_VALUE, Constants.PARAM_ID_SUBSCRIPTION+" wrong format");
			}
	    }
		//build response
		if (result == null) {
			Session ses = SessionFactory.getSession();
			try {
				IstanzeAbbonamenti ia = GenericDao.findById(ses, IstanzeAbbonamenti.class, idSubscription);
				//List<MaterialiSpedizione> msList = new MaterialiSpedizioneDao().findByIstanza(ses, ia);
				//Elenco tutti i fascicoli fino a oggi o fine abbonamento
				Date fine = ia.getDataFine();
				if (fine.after(DateUtil.now())) fine = DateUtil.now();
				List<MaterialiProgrammazione> mpList = new ArrayList<MaterialiProgrammazione>();
				if (ia.getListino().getDigitale()) {
					mpList = new MaterialiProgrammazioneDao().findByPeriodico(ses,
							ia.getListino().getTipoAbbonamento().getPeriodico().getId(), null,
							ia.getDataInizio().getTime(),
							fine.getTime(),
							true/*includeOpzioni*/, true/*orderAsc*/,
							0/*offset*/, Integer.MAX_VALUE/*pageSize*/);
				}
				JsonObjectBuilder joBuilder = schemaBuilder(mpList);
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

	private JsonObjectBuilder schemaBuilder(List<MaterialiProgrammazione> mpList) throws BusinessException {
		JsonBuilderFactory factory = Json.createBuilderFactory(null);
		JsonArrayBuilder arrayBuilder = factory.createArrayBuilder();
		for (MaterialiProgrammazione mp:mpList) {
			JsonObjectBuilder ob = factory.createObjectBuilder();
			add(ob, "cm_issue", mp.getMateriale().getCodiceMeccanografico());
			if (mp.getDataNominale() != null) 
				add(ob, "nominal_issue_date", Constants
					.FORMAT_API_DATE.format(mp.getDataNominale()));
//			add(ob, "nominal_issue_end", Constants
//					.FORMAT_API_DATE.format(pub.fascicolo.getDataFine()));//TODO remove
//			add(ob, "worth", pub.fascicolo.getFascicoliAccorpati());//TODO remove
			add(ob, "description_number", mp.getMateriale().getTitolo());
			add(ob, "description_period", mp.getMateriale().getSottotitolo());
			if (mp.getOpzione() != null) 
					add(ob, "id_option", mp.getOpzione().getUid());
//			add(ob, "media_app", mp.getMateriale().digitale);//TODO remove
//			add(ob, "media_paper", pub.cartaceo);//TODO remove
			arrayBuilder.add(ob);
		}
		JsonObjectBuilder objectBuilder = factory.createObjectBuilder();
		objectBuilder.add("issues", arrayBuilder);
		return objectBuilder;
	}
	
}
