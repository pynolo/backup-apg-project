package it.giunti.apg.ws.api04;

import it.giunti.apg.core.OpzioniUtil;
import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.business.FascicoliBusiness;
import it.giunti.apg.core.business.PagamentiMatchBusiness;
import it.giunti.apg.core.business.WsLogBusiness;
import it.giunti.apg.core.persistence.AbbonamentiDao;
import it.giunti.apg.core.persistence.AnagraficheDao;
import it.giunti.apg.core.persistence.ContatoriDao;
import it.giunti.apg.core.persistence.EvasioniArticoliDao;
import it.giunti.apg.core.persistence.EvasioniFascicoliDao;
import it.giunti.apg.core.persistence.FascicoliDao;
import it.giunti.apg.core.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.core.persistence.ListiniDao;
import it.giunti.apg.core.persistence.OpzioniDao;
import it.giunti.apg.core.persistence.OpzioniIstanzeAbbonamentiDao;
import it.giunti.apg.core.persistence.PagamentiDao;
import it.giunti.apg.core.persistence.PeriodiciDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.model.Abbonamenti;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.ApiServices;
import it.giunti.apg.shared.model.Fascicoli;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Listini;
import it.giunti.apg.shared.model.Opzioni;
import it.giunti.apg.shared.model.OpzioniIstanzeAbbonamenti;
import it.giunti.apg.shared.model.Pagamenti;
import it.giunti.apg.shared.model.Periodici;
import it.giunti.apg.ws.WsConstants;
import it.giunti.apg.ws.business.ValidationBusiness;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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

/*@WebServlet(Constants.PATTERN_API04+Constants.PATTERN_CREATE_SUBSCRIPTION)*/
public class CreateSubscriptionServlet extends ApiServlet {
	private static final long serialVersionUID = 4456731800813741866L;
	private static final String FUNCTION_NAME = Constants.PATTERN_CREATE_SUBSCRIPTION;
	private static final String SERVICE = WsConstants.SERVICE_API04;
	private static final Logger LOG = LoggerFactory.getLogger(CreateSubscriptionServlet.class);

	/*example testing url:
	 http://127.0.0.1:8888/api01/create_subscription?access_key=1234&id_magazine=A&id_offering=0101EB&id_customer_recipient=6090P5&id_customer_payer=1090P0&options=A01A;A01B&quantity=2&cm_first_issue=X1601A&id_payment_type=CCR&payment_amount=70.50&payment_date=2016-08-23&payment_note=ma%20vafagulo
	 */
	/**
     * @see HttpServlet#HttpServlet()
     */
    public CreateSubscriptionServlet() {
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
		if (result == null) {
			//All parameters string for logging
			String allParameters = "";
			Map<String, String[]> paramMap = request.getParameterMap();
			for (String key:paramMap.keySet()) allParameters += key+"="+paramMap.get(key)[0].toString()+"&";
			if (allParameters.length()>1024) allParameters = allParameters.substring(0,1024);
			
			Session ses = SessionFactory.getSession();
			Transaction trn = ses.beginTransaction();
			try {
				String codAbbo = null;
				Abbonamenti abbonamento = null;
				Periodici periodico = null;
				Listini listino = null;
				Anagrafiche customerRecipient = null;
				Anagrafiche customerPayer = null;
				List<Opzioni> optionList = null;
				Integer quantity = null;
				Fascicoli firstIssue = null;
				String idPaymentType = null;
				Double paymentAmount = null;
				Date paymentDate = null;
				String paymentTrn = null;
				String paymentNote = null;
				int paymentDataCount = 0;// dovrà essere 0 o 3 ma nessun altro valore
				
				try {
					//id_magazine - identificativo periodico 
					String idMagazineS = request.getParameter(Constants.PARAM_ID_MAGAZINE);
					idMagazineS = ValidationBusiness.cleanInput(idMagazineS, 1);
					if (idMagazineS == null) {
						result = BaseJsonFactory.buildBaseObject(ErrorEnum.EMPTY_PARAMETER, Constants.PARAM_ID_MAGAZINE+" is empty");
					} else {
						periodico = new PeriodiciDao().findByUid(ses, idMagazineS.toUpperCase());
						if (periodico == null) throw new ValidationException(Constants.PARAM_ID_MAGAZINE+" value not found");
					}
					//cod_abbo - identificativo della sequenza di abbonamenti negli anni, SE POPOLATO => RINNOVO 
					codAbbo = request.getParameter(Constants.PARAM_COD_ABBO);
					codAbbo = ValidationBusiness.cleanInput(codAbbo, 8);
					if (codAbbo != null) {
						if (!codAbbo.startsWith(idMagazineS)) throw new ValidationException(Constants.PARAM_COD_ABBO+" and "+Constants.PARAM_ID_MAGAZINE+" doesn't match");
						abbonamento = new AbbonamentiDao().findAbbonamentiByCodice(ses, codAbbo);
						if (abbonamento == null) throw new ValidationException(Constants.PARAM_COD_ABBO+" value not found");
					}
					//id_offering - identificativo del listino/tipo abbonamento
					String idOffering = request.getParameter(Constants.PARAM_ID_OFFERING);
					idOffering = ValidationBusiness.cleanInput(idOffering, 10);
					if (idOffering == null) {
						result = BaseJsonFactory.buildBaseObject(ErrorEnum.EMPTY_PARAMETER,Constants.PARAM_ID_OFFERING+" is empty");
					} else {
						listino = new ListiniDao().findByUid(ses, idOffering.toUpperCase());
						if (listino == null) throw new ValidationException(Constants.PARAM_ID_OFFERING+" value not found");
						if (!listino.getTipoAbbonamento().getPeriodico().equals(periodico))
							throw new ValidationException(Constants.PARAM_ID_OFFERING+
									" and "+Constants.PARAM_ID_MAGAZINE+" don't match");
					}
					//id_customer_recipient - identificativo beneficiario
					String idRecipient = request.getParameter(Constants.PARAM_ID_CUSTOMER_RECIPIENT);
					idRecipient = ValidationBusiness.cleanInput(idRecipient, 10);
					if (idRecipient == null) {
						result = BaseJsonFactory.buildBaseObject(ErrorEnum.EMPTY_PARAMETER, Constants.PARAM_ID_CUSTOMER_RECIPIENT+" is empty");
					} else {
						try {
							customerRecipient = new AnagraficheDao().findByUid(ses, idRecipient.toUpperCase());
							if (customerRecipient == null) throw new ValidationException(Constants.PARAM_ID_CUSTOMER_RECIPIENT+" value not found");
						} catch (NumberFormatException e) { throw new ValidationException(Constants.PARAM_ID_CUSTOMER_RECIPIENT+" wrong format");}
					}
					//id_customer_payer - identificativo pagante, popolato SOLO SE DIVERSO da beneficiario 
					String idPayer = request.getParameter(Constants.PARAM_ID_CUSTOMER_PAYER);
					idPayer = ValidationBusiness.cleanInput(idPayer, 10);
					if (idPayer != null) {
						try {
							customerPayer = new AnagraficheDao().findByUid(ses, idPayer.toUpperCase());
							if (customerPayer == null) throw new ValidationException(Constants.PARAM_ID_CUSTOMER_PAYER+" value not found");
						} catch (NumberFormatException e) { throw new ValidationException(Constants.PARAM_ID_CUSTOMER_PAYER+" wrong format");}
					}
					//options - stringa con elenco identificativi separati da ";"
					String idOptionListS = request.getParameter(Constants.PARAM_OPTIONS);
					idOptionListS = ValidationBusiness.cleanInput(idOptionListS, 256);
					if (idOptionListS != null) {
						optionList = new ArrayList<Opzioni>();
						String[] idOptionList = idOptionListS.split(AppConstants.STRING_SEPARATOR);
						for (String idOption:idOptionList) {
							try {
								Opzioni option = new OpzioniDao().findByUid(ses, idOption.toUpperCase());
								if (option == null) throw new ValidationException(Constants.PARAM_OPTIONS+" value not found");
								if (!option.getPeriodico().equals(periodico))
									throw new ValidationException(Constants.PARAM_ID_OPTION+
											" and "+Constants.PARAM_ID_MAGAZINE+" don't match");
								optionList.add(option);
							} catch (NumberFormatException e) { throw new ValidationException(Constants.PARAM_OPTIONS+" wrong format");}
						}
					}
					//quantity - numero di copie
					String quantityS = request.getParameter(Constants.PARAM_QUANTITY);
					quantityS = ValidationBusiness.cleanInput(quantityS, 4);
					if (quantityS == null) {
						result = BaseJsonFactory.buildBaseObject(ErrorEnum.EMPTY_PARAMETER, Constants.PARAM_QUANTITY+" is empty");
					} else {
						try {
							quantity = Integer.parseInt(quantityS);
						} catch (NumberFormatException e) { throw new ValidationException(Constants.PARAM_QUANTITY+" wrong format");}
					}
					//id_first_issue - primo fascicolo a cui ha diritto
					FascicoliDao fasDao = new FascicoliDao();
					String cmFirstIssue = request.getParameter(Constants.PARAM_CM_FIRST_ISSUE);
					cmFirstIssue = ValidationBusiness.cleanInput(cmFirstIssue, 10);
					if (cmFirstIssue != null) {
						try {
							cmFirstIssue = cmFirstIssue.toUpperCase();
							firstIssue = fasDao.findByCodiceMeccanografico(ses, cmFirstIssue);
							if (firstIssue == null) throw new ValidationException(Constants.PARAM_CM_FIRST_ISSUE+" value not found");
							if (!firstIssue.getPeriodico().equals(periodico))
								throw new ValidationException(Constants.PARAM_CM_FIRST_ISSUE+
										" and "+Constants.PARAM_ID_MAGAZINE+" doesn't match");
						} catch (NumberFormatException e) { throw new ValidationException(Constants.PARAM_CM_FIRST_ISSUE+" wrong format");}
					}
					//payment_type - tipo pagamento
					idPaymentType = request.getParameter(Constants.PARAM_ID_PAYMENT_TYPE);
					idPaymentType = ValidationBusiness.cleanInput(idPaymentType, 4);
					if (idPaymentType != null) {
						paymentDataCount++;
						idPaymentType = idPaymentType.toUpperCase();
						String desc = AppConstants.PAGAMENTO_DESC.get(idPaymentType);
						if (desc == null) throw new ValidationException(Constants.PARAM_ID_PAYMENT_TYPE+" value not found");
					}
					//payment_amount - importo
					String paymentAmountS = request.getParameter(Constants.PARAM_PAYMENT_AMOUNT);
					paymentAmountS = ValidationBusiness.cleanInput(paymentAmountS, 10);
					if (paymentAmountS != null) {
						paymentDataCount++;
						String[] amount = paymentAmountS.split("\\.");
						if (amount.length < 2) throw new ValidationException(Constants.PARAM_PAYMENT_AMOUNT+" wrong format");
						try {
							Double intAmount = Double.parseDouble(amount[0]);
							amount[1] = (amount[1]+"00").substring(0, 2);
							Double fracAmount = Double.parseDouble(amount[1]);
							paymentAmount = intAmount + (fracAmount/100);
						} catch (NumberFormatException e) { throw new ValidationException(Constants.PARAM_PAYMENT_AMOUNT+" wrong format");}
					}
					//payment_date - data 
					String paymentDateS = request.getParameter(Constants.PARAM_PAYMENT_DATE);
					paymentDateS = ValidationBusiness.cleanInput(paymentDateS, 10);
					if (paymentDateS != null) {
						paymentDataCount++;
						try {
							paymentDate = ServerConstants.FORMAT_DAY_SQL.parse(paymentDateS);
						} catch (ParseException e) { throw new ValidationException(Constants.PARAM_PAYMENT_DATE+" wrong format");}
					}
					//payment_trn - numero transazione 
					paymentTrn = request.getParameter(Constants.PARAM_PAYMENT_TRN);
					paymentTrn = ValidationBusiness.cleanInput(paymentTrn, 128);
					//payment_note - note sul pagamento 
					paymentNote = request.getParameter(Constants.PARAM_PAYMENT_NOTE);
					paymentNote = ValidationBusiness.cleanInput(paymentNote, 32);
					if (paymentNote != null) paymentNote = paymentNote.toUpperCase();
				    //validazione dati pagamento
					if (paymentDataCount >0 && paymentDataCount <3) {
						result = BaseJsonFactory.buildBaseObject(ErrorEnum.EMPTY_PARAMETER, "payment parameters are incomplete");
					}
				} catch (ValidationException e) {
					result = BaseJsonFactory.buildBaseObject(ErrorEnum.WRONG_PARAMETER_VALUE, e.getMessage());
					//LOG errore
					String message = e.getMessage();
					if (message.length() > 256) message = message.substring(0, 256);
					WsLogBusiness.writeWsLog(ses, SERVICE,
							FUNCTION_NAME, allParameters, message);
				}
				
				//build response
				if (result == null) {
					
					/* INIZIO creazione abbonamento */
					
					Date now = DateUtil.now();
					//Abbonamento
					if (abbonamento == null) {
						abbonamento = new Abbonamenti();
						String codiceAbbonamento = new ContatoriDao().createCodiceAbbonamento(ses, periodico.getId());
						abbonamento.setCodiceAbbonamento(codiceAbbonamento);
						abbonamento.setDataCreazione(now);
						abbonamento.setPeriodico(periodico);
						abbonamento.setIdTipoSpedizione(AppConstants.SPEDIZIONE_POSTA_ORDINARIA);
					}
					abbonamento.setDataModifica(now);
					abbonamento.setIdUtente(Constants.USER_API);
					new AbbonamentiDao().save(ses, abbonamento);
					//First issue
					if (firstIssue == null) {
						Date fasDate = now;
						if (listino.getMeseInizio() != null) {
							fasDate = getFirstDayOfNextMonth(
									listino.getDataInizio(),
									listino.getMeseInizio()-1);
						}
						firstIssue =  new FascicoliDao().findFascicoloByPeriodicoDataInizio(ses, periodico.getId(), fasDate);
					}
					//Istanza
					IstanzeAbbonamenti ia = new IstanzeAbbonamenti();
					ia.setAbbonato(customerRecipient);
					ia.setCopie(quantity);
					ia.setDataCambioTipo(now);
					ia.setDataCreazione(now);
					ia.setDataModifica(now);
					ia.setDataSaldo(null);
					ia.setDataSyncMailing(ServerConstants.DATE_FAR_PAST);
					ia.setFascicoloInizio(firstIssue);
					ia.setFascicoliTotali(listino.getNumFascicoli());
					ia.setFatturaDifferita(false);
					ia.setInvioBloccato(false);
					ia.setListino(listino);
					ia.setPagante(customerPayer);
					ia.setPagato(false);//sarà verificato col pagamento
					ia.setIdUtente(Constants.USER_API);
					ia.setAbbonamento(abbonamento);
					FascicoliBusiness.setupFascicoloFine(ses, ia);
					IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
					iaDao.save(ses, ia);
					iaDao.markUltimaDellaSerie(ses, ia.getAbbonamento());
					//Pagamento
					List<Integer> idPagList = new ArrayList<Integer>();
					Pagamenti pag = null;
					if (paymentDataCount == 3) {
						pag = new Pagamenti();
						pag.setAnagrafica(customerRecipient);
						if (customerPayer != null) pag.setAnagrafica(customerPayer);
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
						pag.setIdSocieta(periodico.getIdSocieta());
						pag.setTrn(paymentTrn);
						Integer id = (Integer) new PagamentiDao().save(ses, pag);
						idPagList.add(id);
					}
					//Opzioni obbligatorie
					OpzioniUtil.addOpzioniObbligatorie(ses, ia, false);
					
					/* FINE creazione abbonamento */
					
					//Opzioni aggiuntive
					List<Integer> idOpzList = new ArrayList<Integer>();
					if (ia.getOpzioniIstanzeAbbonamentiSet() == null)
							ia.setOpzioniIstanzeAbbonamentiSet(new HashSet<OpzioniIstanzeAbbonamenti>());
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
					//Crea la fattura oppure rimuove il flag "pagato"
					if (idPagList.size() > 0) {
						PagamentiMatchBusiness.processPayment(ses, paymentDate, now, 
								idPagList, null, ia.getId(), idOpzList, Constants.USER_API);
					} else {
						PagamentiMatchBusiness.verifyPagatoAndUpdate(ses, ia.getId());
					}
					//Aggancia a questa istanza tutti i fascicoli tra inizio e fine
					new EvasioniFascicoliDao().reattachEvasioniFascicoliToIstanza(ses, 
							ia);
					//Forza evantuali articoli obbligatori
					new EvasioniArticoliDao().reattachEvasioniArticoliToInstanza(ses,
							ia, ia.getIdUtente());
					//Aggiunge eventuali arretrati
					new EvasioniFascicoliDao().enqueueMissingArretratiByStatus(ses, ia, Constants.USER_API);
					//Salvataggio e verifica pagamento
					if (ia.getPagato()) {
						ia.setDataSaldo(now);
						//ia.setNecessitaVerifica(false);
					} else {
						//ia.setNecessitaVerifica(true);
					}
					iaDao.updateUnlogged(ses, ia);
					
					WsLogBusiness.writeWsLog(ses, SERVICE,
							FUNCTION_NAME, allParameters, WsConstants.SERVICE_OK);
					
					JsonObjectBuilder joBuilder = schemaBuilder(ia);
					result = BaseJsonFactory.buildBaseObject(joBuilder);
				}
				trn.commit();
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

	private Date getFirstDayOfNextMonth(Date date, int desiredMonth) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		int nowMonth = cal.get(Calendar.MONTH);
		if (desiredMonth < nowMonth) cal.add(Calendar.YEAR, 1);
		cal.set(Calendar.MONTH, desiredMonth);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 1);
		return cal.getTime();
	}
}
