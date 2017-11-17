package it.giunti.apg.ws.api02;

import it.giunti.apg.core.OpzioniUtil;
import it.giunti.apg.core.business.WsLogBusiness;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.core.persistence.ListiniDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.model.ApiServices;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Listini;
import it.giunti.apg.ws.WsConstants;
import it.giunti.apg.ws.business.ValidationBusiness;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*@WebServlet(Constants.PATTERN_API01+Constants.PATTERN_UPDATE_OFFERING)*/
public class UpdateSubscriptionOfferingServlet extends ApiServlet {
	private static final long serialVersionUID = 1726849882243460270L;
	private static final String FUNCTION_NAME = Constants.PATTERN_UPDATE_SUBSCRIPTION_OFFERING;
	private static final Logger LOG = LoggerFactory.getLogger(UpdateSubscriptionOfferingServlet.class);

	/*example testing url:
	 http://127.0.0.1:8888/api01/update_offering?access_key=1234&id_offering=0101EB
	 */
	/**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateSubscriptionOfferingServlet() {
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
		if (result == null) {
			//All parameters string for logging
			String allParameters = "";
			Map<String, String[]> paramMap = request.getParameterMap();
			for (String key:paramMap.keySet()) allParameters += key+"="+paramMap.get(key)[0].toString()+"&";
			if (allParameters.length()>1024) allParameters = allParameters.substring(0,1024);
			
			Session ses = SessionFactory.getSession();
			Transaction trn = ses.beginTransaction();
			try {
				IstanzeAbbonamenti ia = null;
				Listini newListino = null;
				
				try {
					//id_subscription
					String idSubscriptionS = request.getParameter(Constants.PARAM_ID_SUBSCRIPTION);
					idSubscriptionS = ValidationBusiness.cleanInput(idSubscriptionS, 10);
					if (idSubscriptionS != null) {
						try {
							Integer id = Integer.parseInt(idSubscriptionS);
							ia = GenericDao.findById(ses, IstanzeAbbonamenti.class, id);
							if (ia == null) throw new ValidationException(Constants.PARAM_ID_SUBSCRIPTION+" value not found");
						} catch (NumberFormatException e) { throw new ValidationException(Constants.PARAM_ID_SUBSCRIPTION+" wrong format");}
					} else {
						throw new ValidationException(Constants.PARAM_ID_SUBSCRIPTION+" is empty");
					}
					//id_offering - identificativo del listino/tipo abbonamento
					String idOffering = request.getParameter(Constants.PARAM_ID_OFFERING);
					idOffering = ValidationBusiness.cleanInput(idOffering, 10);
					if (idOffering == null) {
						result = BaseJsonFactory.buildBaseObject(ErrorEnum.EMPTY_PARAMETER,Constants.PARAM_ID_OFFERING+" is empty");
					} else {
						newListino = new ListiniDao().findByUid(ses, idOffering.toUpperCase());
						if (newListino == null) throw new ValidationException(Constants.PARAM_ID_OFFERING+" value not found");
					}
				} catch (ValidationException e) {
					result = BaseJsonFactory.buildBaseObject(ErrorEnum.WRONG_PARAMETER_VALUE, e.getMessage());
					String message = e.getMessage();
					if (message.length() > 256) message = message.substring(0, 256);
					WsLogBusiness.writeWsLog(ses, WsConstants.SERVICE_API01,
							FUNCTION_NAME, allParameters, message);
				}
				
				try {
					// Verifica stato istanza: 
					// - non pagata
					// - non disdettata
					// - non in fatturazione
					// - con inizio non antecedente ai ?? giorni
					if (ia.getInFatturazione() || ia.getInvioBloccato() || 
							(ia.getDataDisdetta() != null) || ia.getPagato() ) {
						throw new ValidationException(ia.getAbbonamento().getCodiceAbbonamento()+" ["+
							ia.getId()+"] cannot be moved to a different offering");
					}
					// Verifica listini nuovo e vecchio:
					// - senza fatturazione differita
					// - stessa durata
					// - solo zona Italia 
					Listini oldListino = ia.getListino();
					if (oldListino.getFatturaDifferita() || 
							!oldListino.getIdMacroarea().equals(AppConstants.DEFAULT_MACROAREA)) {
						throw new ValidationException(ia.getAbbonamento().getCodiceAbbonamento()+" ["+
								ia.getId()+"] has an offering that cannot be changed ");
					}
					if (newListino.getFatturaDifferita() || 
							!newListino.getIdMacroarea().equals(AppConstants.DEFAULT_MACROAREA)) {
						throw new ValidationException(ia.getAbbonamento().getCodiceAbbonamento()+" ["+
								ia.getId()+"] cannot be moved to the selected offering");
					}
					if (oldListino.getNumFascicoli() != newListino.getNumFascicoli()) {
						throw new ValidationException(ia.getAbbonamento().getCodiceAbbonamento()+" ["+
								ia.getId()+"] old and new offerings differ in issue number");
					}
						
				} catch (ValidationException e) {
					result = BaseJsonFactory.buildBaseObject(ErrorEnum.WRONG_PARAMETER_VALUE, e.getMessage());
					String message = e.getMessage();
					if (message.length() > 256) message = message.substring(0, 256);
					WsLogBusiness.writeWsLog(ses, WsConstants.SERVICE_API01,
							FUNCTION_NAME, allParameters, message);
				}
				
				//build response
				if (result == null) {
					Date now = DateUtil.now();
					//Istanza
					ia.setDataModifica(now);
					ia.setIdUtente(Constants.USER_API);
					ia.setListino(newListino);
					ia.setDataCambioTipo(now);
					
					//Opzioni obbligatorie
					OpzioniUtil.addOpzioniObbligatorie(ses, ia, false);
					new IstanzeAbbonamentiDao().update(ses, ia);
					
					WsLogBusiness.writeWsLog(ses, WsConstants.SERVICE_API01,
							FUNCTION_NAME, allParameters, WsConstants.SERVICE_OK);
					trn.commit();
					
					JsonObjectBuilder joBuilder = schemaBuilder(ia);
					result = BaseJsonFactory.buildBaseObject(joBuilder);
				}
			} catch (BusinessException | HibernateException e) {
				trn.rollback();
				result = BaseJsonFactory.buildBaseObject(ErrorEnum.INTERNAL_ERROR, ErrorEnum.INTERNAL_ERROR.getErrorDescr());
				LOG.error(e.getMessage(), e);
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

	private JsonObjectBuilder schemaBuilder(IstanzeAbbonamenti ia) throws BusinessException {
		JsonBuilderFactory factory = Json.createBuilderFactory(null);
		JsonObjectBuilder ob = factory.createObjectBuilder();
		add(ob, Constants.PARAM_COD_ABBO, ia.getAbbonamento().getCodiceAbbonamento());
		add(ob, Constants.PARAM_ID_SUBSCRIPTION, ia.getId());
		return ob;
	}
	
}
