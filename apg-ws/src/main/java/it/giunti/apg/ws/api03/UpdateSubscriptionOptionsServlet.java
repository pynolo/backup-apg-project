package it.giunti.apg.ws.api03;

import it.giunti.apg.core.OpzioniUtil;
import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.business.PagamentiMatchBusiness;
import it.giunti.apg.core.business.WsLogBusiness;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.core.persistence.OpzioniDao;
import it.giunti.apg.core.persistence.OpzioniIstanzeAbbonamentiDao;
import it.giunti.apg.core.persistence.PagamentiDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.model.ApiServices;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Opzioni;
import it.giunti.apg.shared.model.OpzioniIstanzeAbbonamenti;
import it.giunti.apg.shared.model.Pagamenti;
import it.giunti.apg.ws.WsConstants;
import it.giunti.apg.ws.business.ValidationBusiness;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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

/*@WebServlet(Constants.PATTERN_API01+Constants.PATTERN_UPDATE_OPTIONS)*/
public class UpdateSubscriptionOptionsServlet extends ApiServlet {
	private static final long serialVersionUID = 5130175479932525104L;
	private static final String FUNCTION_NAME = Constants.PATTERN_UPDATE_OPTIONS;
	private static final Logger LOG = LoggerFactory.getLogger(UpdateSubscriptionOptionsServlet.class);

	/*example testing url:
	 http://127.0.0.1:8888/api01/update_options?access_key=1234&id_subscription=576114&options=A01A&id_payment_type=CCR&payment_amount=9.50&payment_date=2017-04-23&payment_note=ma%20vafagulo
	 */
	/**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateSubscriptionOptionsServlet() {
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
				//Listini listino = null;
				List<Opzioni> optionList = null;
				String idPaymentType = null;
				Double paymentAmount = null;
				Date paymentDate = null;
				String paymentTrn = null;
				String paymentNote = null;
				
				try {
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
					//options - stringa con elenco identificativi separati da ";"
					String idOptionListS = request.getParameter(Constants.PARAM_OPTIONS);
					idOptionListS = ValidationBusiness.cleanInput(idOptionListS, 256);
					if (idOptionListS != null) {
						optionList = new ArrayList<Opzioni>();
						String[] idOptionList = idOptionListS.split(AppConstants.STRING_SEPARATOR);
						for (String idOption:idOptionList) {
							Opzioni option = new OpzioniDao().findByUid(ses, idOption.toUpperCase());
							if (option == null) throw new ValidationException(Constants.PARAM_OPTIONS+" value not found");
							optionList.add(option);
						}
					} else {
						throw new ValidationException(Constants.PARAM_OPTIONS+" is empty");
					}
					//payment_type - tipo pagamento
					idPaymentType = request.getParameter(Constants.PARAM_ID_PAYMENT_TYPE);
					idPaymentType = ValidationBusiness.cleanInput(idPaymentType, 4);
					if (idPaymentType != null) {
						idPaymentType = idPaymentType.toUpperCase();
						String desc = AppConstants.PAGAMENTO_DESC.get(idPaymentType);
						if (desc == null) throw new ValidationException(Constants.PARAM_ID_PAYMENT_TYPE+" value not found");
					} //else {
					//	throw new ValidationException(Constants.PARAM_ID_PAYMENT_TYPE+" is empty");
					//}
					//payment_amount - importo
					String paymentAmountS = request.getParameter(Constants.PARAM_PAYMENT_AMOUNT);
					paymentAmountS = ValidationBusiness.cleanInput(paymentAmountS, 10);
					if (paymentAmountS != null) {
						String[] amount = paymentAmountS.split("\\.");
						if (amount.length < 2) throw new ValidationException(Constants.PARAM_PAYMENT_AMOUNT+" wrong format");
						try {
							Double intAmount = Double.parseDouble(amount[0]);
							amount[1] = (amount[1]+"00").substring(0, 2);
							Double fracAmount = Double.parseDouble(amount[1]);
							paymentAmount = intAmount + (fracAmount/100);
						} catch (NumberFormatException e) { throw new ValidationException(Constants.PARAM_PAYMENT_AMOUNT+" wrong format");}
					} //else {
					//	throw new ValidationException(Constants.PARAM_PAYMENT_AMOUNT+" is empty");
					//}
					//payment_date - data 
					String paymentDateS = request.getParameter(Constants.PARAM_PAYMENT_DATE);
					paymentDateS = ValidationBusiness.cleanInput(paymentDateS, 10);
					if (paymentDateS != null) {
						try {
							paymentDate = ServerConstants.FORMAT_DAY_SQL.parse(paymentDateS);
						} catch (ParseException e) { throw new ValidationException(Constants.PARAM_PAYMENT_DATE+" wrong format");}
					} //else {
					//	throw new ValidationException(Constants.PARAM_PAYMENT_DATE+" is empty");
					//}
					//payment_trn - numero transazione 
					paymentTrn = request.getParameter(Constants.PARAM_PAYMENT_TRN);
					paymentTrn = ValidationBusiness.cleanInput(paymentTrn, 128);
					//payment_note - note sul pagamento 
					paymentNote = request.getParameter(Constants.PARAM_PAYMENT_NOTE);
					paymentNote = ValidationBusiness.cleanInput(paymentNote, 32);
					if (paymentNote != null) paymentNote = paymentNote.toUpperCase();
					
					if ((idPaymentType!=null || paymentAmount!=null || paymentDate!=null) &&
							(idPaymentType==null || paymentAmount==null || paymentDate==null)) {
						throw new ValidationException("payment information is incomplete");
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
					
					//Opzioni obbligatorie
					OpzioniUtil.addOpzioniObbligatorie(ses, ia, false);
					//Opzioni aggiuntive
					List<Integer> idOpzList = new ArrayList<Integer>();
					if (optionList != null) {
						if (optionList.size() > 0) {
							if (ia.getOpzioniIstanzeAbbonamentiSet() == null)
									ia.setOpzioniIstanzeAbbonamentiSet(new HashSet<OpzioniIstanzeAbbonamenti>());
							for (Opzioni opz:optionList) {
								Date dataFineOpzione = ServerConstants.DATE_FAR_FUTURE;
								if (opz.getDataFine() != null) dataFineOpzione = opz.getDataFine();
								//Verifica periodico
								if (!ia.getAbbonamento().getPeriodico().equals(opz.getPeriodico())) {
									throw new BusinessException("Subscription "+ia.getId()+" and option "+opz.getUid()+" are bound to different magazines");
								}
								//Verifica periodi di validità
								if (ia.getFascicoloInizio().getDataInizio().after(dataFineOpzione) ||
										ia.getFascicoloFine().getDataFine().before(opz.getDataInizio())) {
									throw new BusinessException("Subscription "+ia.getId()+" and option "+opz.getUid()+" are active on different time frames");
								}
								//Aggiunge
								boolean found = false;
								for (OpzioniIstanzeAbbonamenti inclOia:ia.getOpzioniIstanzeAbbonamentiSet()) {
									if (inclOia.getOpzione().getId().equals(opz.getId())) found = true;
								}
								if (!found) {
									idOpzList.add(opz.getId());
									OpzioniIstanzeAbbonamentiDao oiaDao = new OpzioniIstanzeAbbonamentiDao();
									OpzioniIstanzeAbbonamenti oia = new OpzioniIstanzeAbbonamenti();
									oia.setIstanza(ia);
									oia.setOpzione(opz);
									ia.getOpzioniIstanzeAbbonamentiSet().add(oia);
									oiaDao.save(ses, oia);
								}
							}
						}
					}
					
					//C'è un pagamento?
					if (idPaymentType!=null && paymentAmount!=null && paymentDate!=null) {
						//Pagamento
						List<Integer> idPagList = new ArrayList<Integer>();
						Pagamenti pag = new Pagamenti();
						pag.setAnagrafica(ia.getAbbonato());
						if (ia.getPagante() != null) pag.setAnagrafica(ia.getPagante());
						pag.setDataAccredito(now);
						pag.setDataCreazione(now);
						pag.setDataModifica(now);
						pag.setDataPagamento(paymentDate);
						pag.setIdTipoPagamento(idPaymentType);
						pag.setImporto(paymentAmount);
						pag.setNote(paymentNote);
						pag.setIdUtente(Constants.USER_API);
						pag.setCodiceAbbonamentoMatch(ia.getAbbonamento().getCodiceAbbonamento());
						pag.setIstanzaAbbonamento(ia);
						pag.setIdSocieta(ia.getAbbonamento().getPeriodico().getIdSocieta());
						pag.setTrn(paymentTrn);
						Integer id = (Integer) new PagamentiDao().save(ses, pag);
						idPagList.add(id);
						//Crea la fattura
						PagamentiMatchBusiness.processPayment(ses, paymentDate, now, 
								idPagList, null, ia.getId(), idOpzList, Constants.USER_API);
					}
					//Pagato?
					boolean pagato = (ia.getIdFattura() != null);
					for (OpzioniIstanzeAbbonamenti oia:ia.getOpzioniIstanzeAbbonamentiSet()) {
						pagato = pagato && (oia.getIdFattura() != null);
					}
					ia.setPagato(pagato);
					//ia.setNecessitaVerifica(true);
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
		response.setCharacterEncoding(AppConstants.CHARSET_UTF8);
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
