package it.giunti.apg.ws.api04;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

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
import it.giunti.apg.core.persistence.ListiniDao;
import it.giunti.apg.core.persistence.MaterialiProgrammazioneDao;
import it.giunti.apg.core.persistence.PagamentiDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.core.persistence.TipiAbbonamentoRinnovoDao;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.IstanzeStatusUtil;
import it.giunti.apg.shared.model.ApiServices;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Listini;
import it.giunti.apg.shared.model.MaterialiProgrammazione;
import it.giunti.apg.shared.model.OpzioniIstanzeAbbonamenti;
import it.giunti.apg.shared.model.OpzioniListini;
import it.giunti.apg.shared.model.TipiAbbonamento;
import it.giunti.apg.ws.business.ValidationBusiness;

/**
 * Servlet implementation class FindIssuesServlet
 */
public class GetSubscriptionServlet extends ApiServlet {
	private static final long serialVersionUID = 6326490703963367861L;
	private static final String FUNCTION_NAME = Constants.PATTERN_GET_SUBSCRIPTION;
	private static final Logger LOG = LoggerFactory.getLogger(GetSubscriptionServlet.class);
	
	/*example testing url:
	 http://127.0.0.1:8888/api01/get_subscription?access_key=1234&id_subscription=208377
	 */

    public GetSubscriptionServlet() {
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
				if (ia == null) throw new BusinessException(idSubscription+" has no match");
				
				JsonObjectBuilder joBuilder = subscriptionSchemaBuilder(ses, ia);
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

    public static JsonObjectBuilder subscriptionSchemaBuilder(Session ses, IstanzeAbbonamenti ia) 
    		throws BusinessException {
    	//Paid Amount
		Double paidAmount = new PagamentiDao().sumPagamentiByIstanza(ses, ia.getId());
		//Gracing iniziale
		MaterialiProgrammazione fasGracingIni = new MaterialiProgrammazioneDao().stepForwardFascicoloAfterDate(ses, 
				ia.getListino().getTipoAbbonamento().getPeriodico().getId(),
				ia.getListino().getGracingIniziale(), ia.getDataInizio());
		Date initialGracingDate = fasGracingIni.getDataNominale();
		//Data blocco offerta
		Calendar cal = new GregorianCalendar();
		Date offeringStopDate = null;
		TipiAbbonamento ta = ia.getListino().getTipoAbbonamento();
		if (ta.getDeltaInizioBloccoOfferta() != null) { 
			cal.setTime(ia.getDataInizio());
			cal.add(Calendar.DAY_OF_MONTH, ta.getDeltaInizioBloccoOfferta());
			offeringStopDate = cal.getTime();
		}
		//Avviso di pagamento
		Date chargeWarningDate = null;
		if (ta.getDeltaInizioAvvisoPagamento() != null) {
			cal.setTime(ia.getDataInizio());
			cal.add(Calendar.DAY_OF_MONTH, ta.getDeltaInizioAvvisoPagamento());
			chargeWarningDate = cal.getTime();
		}
		//Pagamento automatico
		Date automaticChargeDate = null;
		if (ta.getDeltaInizioPagamentoAutomatico() != null) {
			cal.setTime(ia.getDataInizio());
			cal.add(Calendar.DAY_OF_MONTH, ta.getDeltaInizioPagamentoAutomatico());
			automaticChargeDate = cal.getTime();
		}
		//Data abilitazione rinnovo
		Date renewalEnabledDate = null;
		if (ta.getDeltaFineRinnovoAbilitato() != null) {
			cal.setTime(ia.getDataFine());
			cal.add(Calendar.DAY_OF_MONTH, ta.getDeltaFineRinnovoAbilitato());
			renewalEnabledDate = cal.getTime();
		}
		//Avviso di rinnovo
		Date renewalWarningDate = null;
		if (ta.getDeltaFineAvvisoRinnovo() != null) {
			cal.setTime(ia.getDataFine());
			cal.add(Calendar.DAY_OF_MONTH, ta.getDeltaFineAvvisoRinnovo());
			renewalWarningDate = cal.getTime();
		}
		//Rinnovo automatico
		Date automaticRenewalDate = null;
		if (ta.getDeltaFineRinnovoAutomatico() != null) {
			cal.setTime(ia.getDataFine());
			cal.add(Calendar.DAY_OF_MONTH, ta.getDeltaFineRinnovoAutomatico());
			automaticRenewalDate = cal.getTime();
		}
		//Gracing Finale
		MaterialiProgrammazione fasGracingFin = new MaterialiProgrammazioneDao().stepForwardFascicoloAfterDate(ses,
				ia.getListino().getTipoAbbonamento().getPeriodico().getId(),
				ia.getListino().getGracingFinale(), ia.getDataFine());
		Date finalGracingDate = fasGracingFin.getDataNominale();
		//Listino al rinnovo
		String renewalLisUid = getRenewalListinoUid(ses, ia.getListino(), ia.getDataFine());
		
		JsonBuilderFactory factory = Json.createBuilderFactory(null);
		JsonObjectBuilder ob = factory.createObjectBuilder();
		if (paidAmount == null) paidAmount = 0D;
		if (paidAmount < 0) throw new BusinessException("paidAmount cannot be negative "+paidAmount.toString());
		add(ob, Constants.PARAM_COD_ABBO, ia.getAbbonamento().getCodiceAbbonamento());
		add(ob, Constants.PARAM_ID_SUBSCRIPTION, ia.getId());
		add(ob, Constants.PARAM_ID_MAGAZINE, ia.getAbbonamento().getPeriodico().getUid());
		add(ob, Constants.PARAM_ID_OFFERING, ia.getListino().getUid());
		add(ob, Constants.PARAM_ID_RENEWAL_OFFERING, renewalLisUid);
		add(ob, Constants.PARAM_ID_CUSTOMER_RECIPIENT, ia.getAbbonato().getUid());
		if (ia.getPagante() != null) add(ob, Constants.PARAM_ID_CUSTOMER_PAYER, ia.getPagante().getUid());
		if (ia.getOpzioniIstanzeAbbonamentiSet() != null) {
			if (ia.getOpzioniIstanzeAbbonamentiSet().size() > 0) {
				JsonArrayBuilder ab = factory.createArrayBuilder();
				for (OpzioniIstanzeAbbonamenti oia:ia.getOpzioniIstanzeAbbonamentiSet()) {
					boolean isIncluded = false;
					for (OpzioniListini ol:ia.getListino().getOpzioniListiniSet()) {
						if (oia.getOpzione().getUid().equals(ol.getOpzione().getUid()))
								isIncluded = true;
					}
					JsonObjectBuilder b1 = factory.createObjectBuilder();
					add(b1, "id_option", oia.getOpzione().getUid());
					add(b1, "name", oia.getOpzione().getNome());
					add(b1, "is_included", isIncluded);
					if (!isIncluded) add(b1, "price", oia.getOpzione().getPrezzo());
					ab.add(b1);
					add(b1, "is_paid", (oia.getIdFattura() != null));
				}
				ob.add("options", ab);
			}
		}
		add(ob, "quantity", ia.getCopie());
//		add(ob, "cm_first_issue", ia.getFascicoloInizio().getCodiceMeccanografico());//TODO remove
//		add(ob, "cm_last_issue", ia.getFascicoloFine().getCodiceMeccanografico());//TODO remove
		add(ob, "subscription_begin", ia.getDataInizio());
		add(ob, "subscription_end", ia.getDataFine());
		add(ob, "is_paid", (ia.getIdFattura() != null));
		add(ob, "is_deferred_bill", IstanzeStatusUtil.isFatturato(ia));
		add(ob, "is_free_gift", IstanzeStatusUtil.isOmaggio(ia));
		add(ob, "is_blocked", ia.getInvioBloccato());
		add(ob, "is_purchase_proposal", ia.getPropostaAcquisto());
		add(ob, "media_app", ia.getListino().getDigitale());
		add(ob, "media_paper", ia.getListino().getCartaceo());
		Set<String> tagSet = buildTagSet(ia);
		if (tagSet.size() > 0) {
			JsonArrayBuilder tagArrayBuilder = factory.createArrayBuilder();
			for (String tag:tagSet) tagArrayBuilder.add(tag);
			ob.add("feature_tags", tagArrayBuilder);
		}
		add(ob, "price", ia.getListino().getPrezzo());
		add(ob, "paid_amount", paidAmount);
		add(ob, "cancellation_request_date", ia.getDataDisdetta());
//		add(ob, "issues_total", ia.getFascicoliTotali()); //TODO remove
//		add(ob, "issues_past", ia.getFascicoliSpediti()); //TODO remove
		add(ob, "initial_gracing_end_date", initialGracingDate);
		add(ob, "offering_stop_date", offeringStopDate);
		add(ob, "charge_warning_date", chargeWarningDate);
		add(ob, "automatic_charge_date", automaticChargeDate);
		add(ob, "renewal_enabled_date", renewalEnabledDate);
		add(ob, "renewal_warning_date", renewalWarningDate);
		add(ob, "automatic_renewal_date", automaticRenewalDate);
		add(ob, "final_gracing_end_date", finalGracingDate);
		add(ob, "modified_date", ia.getDataModifica());
		return ob;
	}
	
	private static String getRenewalListinoUid(Session ses, Listini oldLis, Date oldEndDate) throws BusinessException {
		Calendar cal = new GregorianCalendar();
		cal.setTime(oldEndDate);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		Date dt = cal.getTime();
		String renewalLisUid = null;
		TipiAbbonamento taRinn = new TipiAbbonamentoRinnovoDao().findFirstTipoRinnovoByIdListino(ses, oldLis.getId());
		Listini newLis = null;
		if (taRinn != null) {
			newLis = new ListiniDao().findListinoByTipoAbbDate(ses, taRinn.getId(), dt);
		}
		if (newLis != null) renewalLisUid = newLis.getUid();
		return renewalLisUid;
	}
	
	private static Set<String> buildTagSet(IstanzeAbbonamenti ia) {
		Set<String> tagSet = new HashSet<String>();
		//Add tags from Listino
		String tagListino = ia.getListino().getTag();
		if (tagListino != null) { 
			if (tagListino.length() > 0) {
				String tags[] = tagListino.split(AppConstants.STRING_SEPARATOR);
				for (String tag:tags) tagSet.add(tag);
			}
		}
		//Add tags from each Option
		if (ia.getOpzioniIstanzeAbbonamentiSet() != null) {
			for (OpzioniIstanzeAbbonamenti oia:ia.getOpzioniIstanzeAbbonamentiSet()) {
				String tagOpzione = oia.getOpzione().getTag();
				if (tagOpzione != null) {
					if (tagOpzione.length() > 0) {
						String tags[] = tagOpzione.split(AppConstants.STRING_SEPARATOR);
						for (String tag:tags) tagSet.add(tag);
					}
				}
			}
		}
		return tagSet;
	}
}
