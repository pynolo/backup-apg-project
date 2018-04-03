package it.giunti.apg.ws.api02;

import it.giunti.apg.core.persistence.AnagraficheDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.ApiServices;
import it.giunti.apg.ws.business.ValidationBusiness;

import java.io.IOException;
import java.io.PrintWriter;

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

/*@WebServlet(Constants.PATTERN_API01+Constants.PATTERN_GET_CUSTOMER_DATA)*/
public class GetCustomerServlet extends ApiServlet {
	private static final long serialVersionUID = 507704264971850384L;
	private static final String FUNCTION_NAME = Constants.PATTERN_GET_CUSTOMER;
	private static final Logger LOG = LoggerFactory.getLogger(GetCustomerServlet.class);

	/*example testing url:
	 http://127.0.0.1:8888/api01/get_customer?access_key=1234&id_customer=Q090NQ
	 */

    public GetCustomerServlet() {
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
		//acquire idClient
		String idCustomer = request.getParameter(Constants.PARAM_ID_CUSTOMER);
		if (idCustomer == null) {
			result = BaseJsonFactory.buildBaseObject(ErrorEnum.EMPTY_PARAMETER, Constants.PARAM_ID_CUSTOMER+" is empty");
		}
		//build response
		if (result == null) {
			Session ses = SessionFactory.getSession();
			try {
				Anagrafiche anag = new AnagraficheDao().findByUid(ses, idCustomer);
				if (anag == null) anag = new AnagraficheDao().findByMergedUidCliente(ses, idCustomer);
				if (anag == null) throw new BusinessException(idCustomer+" has no match");
				//Double credit = 0D;
				//List<PagamentiCrediti> credList = new PagamentiCreditiDao()
				//		.findByAnagrafica(ses, anag.getId(), false);
				//if (credList != null) {
				//	for(Pagamenti cred:credList) {
				//		credit += cred.getImporto();
				//	}
				//}
				JsonObjectBuilder joBuilder = schemaBuilder(anag);
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

	private JsonObjectBuilder schemaBuilder(Anagrafiche ana) throws BusinessException {
		JsonBuilderFactory factory = Json.createBuilderFactory(null);
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
		add(ob, Constants.PARAM_BILLING_TITLE, ana.getIndirizzoFatturazione().getTitolo());
		add(ob, Constants.PARAM_BILLING_FIRST_NAME, ana.getIndirizzoFatturazione().getNome());
		add(ob, Constants.PARAM_BILLING_LAST_NAME_COMPANY, ana.getIndirizzoFatturazione().getCognomeRagioneSociale());
		add(ob, Constants.PARAM_BILLING_CO, ana.getIndirizzoFatturazione().getPresso());
		add(ob, Constants.PARAM_BILLING_ADDRESS, ana.getIndirizzoFatturazione().getIndirizzo());
		add(ob, Constants.PARAM_BILLING_LOCALITY, ana.getIndirizzoFatturazione().getLocalita());
		add(ob, Constants.PARAM_BILLING_PROVINCE, ana.getIndirizzoFatturazione().getProvincia());
		add(ob, Constants.PARAM_BILLING_ZIP, ana.getIndirizzoFatturazione().getCap());
		add(ob, Constants.PARAM_BILLING_COUNTRY_CODE, ana.getIndirizzoFatturazione().getNazione().getSiglaNazione());
		add(ob, Constants.PARAM_SEX, ana.getSesso());
		add(ob, Constants.PARAM_COD_FISC, ana.getCodiceFiscale());
		add(ob, Constants.PARAM_PIVA, ana.getPartitaIva());
		add(ob, Constants.PARAM_PHONE_MOBILE, ana.getTelMobile());
		add(ob, Constants.PARAM_PHONE_LANDLINE, ana.getTelCasa());
		add(ob, Constants.PARAM_EMAIL_PRIMARY, ana.getEmailPrimaria());
		add(ob, Constants.PARAM_EMAIL_SECONDARY, ana.getEmailSecondaria());
		if (ana.getProfessione() != null)
			add(ob, Constants.PARAM_ID_JOB, ana.getProfessione().getId());
		if (ana.getTitoloStudio() != null)
			add(ob, Constants.PARAM_ID_QUALIFICATION, ana.getTitoloStudio().getId());
		add(ob, Constants.PARAM_MARKETING_CONSENT, ana.getConsensoMarketing());
		add(ob, Constants.PARAM_BIRTH_DATE, ana.getDataNascita());
		return ob;
	}
	
}
