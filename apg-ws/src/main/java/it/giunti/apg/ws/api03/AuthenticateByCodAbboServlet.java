package it.giunti.apg.ws.api03;

import it.giunti.apg.core.persistence.AnagraficheDao;
import it.giunti.apg.core.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.ApiServices;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.ws.WsConstants;
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

/*@WebServlet(Constants.PATTERN_API01+Constants.PATTERN_AUTHENTICATE_BY_COD_ABBO)*/
public class AuthenticateByCodAbboServlet extends ApiServlet {
	private static final long serialVersionUID = 2171046186804087813L;
	private static final String FUNCTION_NAME = Constants.PATTERN_AUTHENTICATE_BY_COD_ABBO;
	private static final Logger LOG = LoggerFactory.getLogger(AuthenticateByCodAbboServlet.class);

	/*example testing url:
	 http://127.0.0.1:8888/api01/authenticate_by_cod_abbo?access_key=1234&cod_abbo=M011883&address_province=tn
	 */

    public AuthenticateByCodAbboServlet() {
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
		//acquire cod_abbo
		String codAbbo = request.getParameter(Constants.PARAM_COD_ABBO);
		if (codAbbo == null) {
			result = BaseJsonFactory.buildBaseObject(ErrorEnum.EMPTY_PARAMETER, Constants.PARAM_COD_ABBO+" is empty");
		}
		//acquire prov
		String prov = request.getParameter(Constants.PARAM_ADDRESS_PROVINCE);
		if (prov == null) {
			result = BaseJsonFactory.buildBaseObject(ErrorEnum.EMPTY_PARAMETER, Constants.PARAM_ADDRESS_PROVINCE+" is empty");
		}
		//build response
		if (result == null) {
			Session ses = SessionFactory.getSession();
			try {
				Anagrafiche anag = null;
				IstanzeAbbonamenti ia = new IstanzeAbbonamentiDao().findUltimaIstanzaByCodice(ses, codAbbo);
				if (ia == null) {
					//look for codice cliente
					anag = new AnagraficheDao().recursiveFindByUid(ses, codAbbo);
					if (anag == null) {
						throw new BusinessException(codAbbo+" has no match");
					}
				} else {
					anag = ia.getAbbonato();
				}
				
				String realProv = anag.getIndirizzoPrincipale().getProvincia();
				if (!anag.getIndirizzoPrincipale().getNazione().getId()
						.equals(AppConstants.DEFAULT_ID_NAZIONE_ITALIA))
					realProv = WsConstants.PROVINCIA_ESTERO_AUTH;
				if (realProv.equalsIgnoreCase(prov)) {
					//Corrispondenza
					JsonObjectBuilder joBuilder = schemaBuilder(anag);
					result = BaseJsonFactory.buildBaseObject(joBuilder);
				} else {
					result = BaseJsonFactory.buildBaseObject(ErrorEnum.WRONG_PARAMETER_VALUE, "cod_abbo or province is wrong, not authenticated");
				}
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

	private JsonObjectBuilder schemaBuilder(Anagrafiche ana) throws BusinessException {
		JsonBuilderFactory factory = Json.createBuilderFactory(null);
		JsonObjectBuilder ob = factory.createObjectBuilder();
		add(ob, Constants.PARAM_ID_CUSTOMER, ana.getUid());
		return ob;
	}
	
}
