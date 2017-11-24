package it.giunti.apg.ws.api02;

import it.giunti.apg.core.persistence.FascicoliDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.ListiniDao;
import it.giunti.apg.core.persistence.PagamentiDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.core.persistence.TipiAbbonamentoRinnovoDao;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.IstanzeStatusUtil;
import it.giunti.apg.shared.model.ApiServices;
import it.giunti.apg.shared.model.Fascicoli;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Listini;
import it.giunti.apg.shared.model.OpzioniIstanzeAbbonamenti;
import it.giunti.apg.shared.model.OpzioniListini;
import it.giunti.apg.shared.model.TipiAbbonamento;
import it.giunti.apg.ws.business.ValidationBusiness;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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

/**
 * Servlet implementation class FindIssuesServlet
 */
/*@WebServlet(WsConstants.PATTERN_GET_SUBSCRIPTION_DATA)*/
public class GetSubscriptionServlet extends ApiServlet {
	private static final long serialVersionUID = 328810654231294249L;
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
				//Paid Amount
				Double paidAmount = new PagamentiDao().sumPagamentiByIstanza(ses, idSubscription);
				//Gracing iniziale
				Fascicoli fasGracingIni = new FascicoliDao().findFascicoliAfterFascicolo(ses,
						ia.getFascicoloInizio().getId(),
						ia.getListino().getGracingIniziale());
				Date initialGracingDate = fasGracingIni.getDataFine();
				//Data blocco offerta
				Calendar cal = new GregorianCalendar();
				Date offeringStopDate = null;
				if (ia.getListino().getDeltaInizioBloccoOfferta() != null) { 
					cal.setTime(ia.getFascicoloInizio().getDataInizio());
					cal.add(Calendar.DAY_OF_MONTH, ia.getListino().getDeltaInizioBloccoOfferta());
					offeringStopDate = cal.getTime();
				}
				//Avviso di pagamento
				Date chargeWarningDate = null;
				if (ia.getListino().getDeltaInizioAvvisoPagamento() != null) {
					cal.setTime(ia.getFascicoloInizio().getDataInizio());
					cal.add(Calendar.DAY_OF_MONTH, ia.getListino().getDeltaInizioAvvisoPagamento());
					chargeWarningDate = cal.getTime();
				}
				//Pagamento automatico
				Date automaticChargeDate = null;
				if (ia.getListino().getDeltaInizioPagamentoAutomatico() != null) {
					cal.setTime(ia.getFascicoloInizio().getDataInizio());
					cal.add(Calendar.DAY_OF_MONTH, ia.getListino().getDeltaInizioPagamentoAutomatico());
					automaticChargeDate = cal.getTime();
				}
				//Data abilitazione rinnovo
				Date renewalEnabledDate = null;
				if (ia.getListino().getDeltaFineRinnovoAbilitato() != null) {
					cal.setTime(ia.getFascicoloFine().getDataFine());
					cal.add(Calendar.DAY_OF_MONTH, ia.getListino().getDeltaFineRinnovoAbilitato());
					renewalEnabledDate = cal.getTime();
				}
				//Avviso di rinnovo
				Date renewalWarningDate = null;
				if (ia.getListino().getDeltaFineAvvisoRinnovo() != null) {
					cal.setTime(ia.getFascicoloFine().getDataFine());
					cal.add(Calendar.DAY_OF_MONTH, ia.getListino().getDeltaFineAvvisoRinnovo());
					renewalWarningDate = cal.getTime();
				}
				//Rinnovo automatico
				Date automaticRenewalDate = null;
				if (ia.getListino().getDeltaFineRinnovoAutomatico() != null) {
					cal.setTime(ia.getFascicoloFine().getDataFine());
					cal.add(Calendar.DAY_OF_MONTH, ia.getListino().getDeltaFineRinnovoAutomatico());
					automaticRenewalDate = cal.getTime();
				}
				//Gracing Finale
				Fascicoli fasGracingFin = new FascicoliDao().findFascicoliAfterFascicolo(ses,
						ia.getFascicoloFine().getId(),
						ia.getListino().getGracingFinale());
				Date finalGracingDate = fasGracingFin.getDataFine();
				//Listino al rinnovo
				String renewalLisUid = getRenewalListinoUid(ses, ia.getListino(), ia.getFascicoloFine().getDataFine());
				
				JsonObjectBuilder joBuilder = schemaBuilder(ia, paidAmount,
						initialGracingDate, offeringStopDate, 
						chargeWarningDate, automaticChargeDate, 
						renewalEnabledDate, renewalWarningDate, automaticRenewalDate, 
						finalGracingDate, renewalLisUid);
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

    private JsonObjectBuilder schemaBuilder(IstanzeAbbonamenti ia, Double paidAmount,
    		Date initialGracingDate, Date offeringStopDate, 
    		Date chargeWarningDate, Date automaticChargeDate,
    		Date renewalEnabledDate, Date renewalWarningDate, Date automaticRenewalDate, 
    		Date finalGracingDate, String renewalLisUid) throws BusinessException {
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
		add(ob, "id_first_issue", ia.getFascicoloInizio().getTitoloNumero());
		add(ob, "id_last_issue", ia.getFascicoloFine().getTitoloNumero());
		add(ob, "subscription_begin", ia.getFascicoloInizio().getDataInizio());
		add(ob, "subscription_end", ia.getFascicoloFine().getDataFine());
		add(ob, "is_paid", (ia.getIdFattura() != null));
		add(ob, "is_deferred_bill", IstanzeStatusUtil.isFatturato(ia));
		add(ob, "is_free_gift", IstanzeStatusUtil.isOmaggio(ia));
		add(ob, "is_blocked", ia.getInvioBloccato());
		add(ob, "price", ia.getListino().getPrezzo());
		add(ob, "paid_amount", paidAmount);
		add(ob, "cancellation_request_date", ia.getDataDisdetta());
		add(ob, "issues_total", ia.getFascicoliTotali());
		add(ob, "issues_past", ia.getFascicoliSpediti());
		add(ob, "initial_gracing_end_date", initialGracingDate);
		add(ob, "offering_stop_date", offeringStopDate);
		add(ob, "charge_warning_date", chargeWarningDate);
		add(ob, "automatic_charge_date", automaticChargeDate);
		add(ob, "renewal_enabled_date", renewalEnabledDate);
		add(ob, "renewal_warning_date", renewalWarningDate);
		add(ob, "automatic_renewal_date", automaticRenewalDate);
		add(ob, "final_gracing_end_date", finalGracingDate);
		return ob;
	}
	
	private String getRenewalListinoUid(Session ses, Listini oldLis, Date oldEndDate) throws BusinessException {
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
}
