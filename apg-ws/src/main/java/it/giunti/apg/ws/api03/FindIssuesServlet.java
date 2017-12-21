package it.giunti.apg.ws.api03;

import it.giunti.apg.core.persistence.FascicoliDao;
import it.giunti.apg.core.persistence.PeriodiciDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.model.ApiServices;
import it.giunti.apg.shared.model.Fascicoli;
import it.giunti.apg.shared.model.Periodici;
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

/*@WebServlet(Constants.PATTERN_API01+Constants.PATTERN_FIND_ISSUES)*/
public class FindIssuesServlet extends ApiServlet {
	private static final long serialVersionUID = -8582631007494006910L;
	private static final String FUNCTION_NAME = Constants.PATTERN_FIND_ISSUES;
	private static final Logger LOG = LoggerFactory.getLogger(FindIssuesServlet.class);

	/**
     * @see HttpServlet#HttpServlet()
     */
    public FindIssuesServlet() {
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
		//build response
		if (result == null) {
			Session ses = SessionFactory.getSession();
			try {
				//Periodico
				Periodici periodico = new PeriodiciDao().findByUid(ses, idMagazine);
				if (periodico == null) throw new ValidationException(Constants.PARAM_ID_MAGAZINE+" value not found");
				//Elenco fascicoli
				List<Fascicoli> fList = new FascicoliDao().findFascicoliByPeriodico(ses,
						periodico.getId(), null,
						dtBegin.getTime(), dtEnd.getTime(),
						true/*includeOpzioni*/, true/*orderAsc*/,
						0/*offset*/, Integer.MAX_VALUE/*pageSize*/);
				JsonObjectBuilder joBuilder = schemaBuilder(fList);
				result = BaseJsonFactory.buildBaseObject(joBuilder);
			} catch (ValidationException e) {
				result = BaseJsonFactory.buildBaseObject(ErrorEnum.WRONG_PARAMETER_VALUE, e.getMessage());
				LOG.error(e.getMessage(), e);
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

	private JsonObjectBuilder schemaBuilder(List<Fascicoli> fList) throws BusinessException {
		JsonBuilderFactory factory = Json.createBuilderFactory(null);
		JsonArrayBuilder arrayBuilder = factory.createArrayBuilder();
		for (Fascicoli fas:fList) {
			JsonObjectBuilder ob = factory.createObjectBuilder();
			add(ob, "cm_issue", fas.getCodiceMeccanografico());
			add(ob, "nominal_issue_date", Constants.FORMAT_API_DATE.format(fas.getDataInizio()));
			if (fas.getDataFine() != null)
				add(ob, "nominal_issue_end", Constants.FORMAT_API_DATE.format(fas.getDataFine()));
			add(ob, "worth", fas.getFascicoliAccorpati());
			add(ob, "description_number", fas.getTitoloNumero());
			add(ob, "description_period", fas.getDataCop());
			if (fas.getOpzione() != null)
				add(ob, "id_option", fas.getOpzione().getUid());
			arrayBuilder.add(ob);
		}
		JsonObjectBuilder objectBuilder = factory.createObjectBuilder();
		objectBuilder.add("issues", arrayBuilder);
		return objectBuilder;
	}
	
}
