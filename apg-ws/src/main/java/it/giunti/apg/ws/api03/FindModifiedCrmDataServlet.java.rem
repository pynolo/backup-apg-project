/** find_modified_customers

Funzione che restituisce l'elenco di anagrafiche che sono state modificate o create a partire dalla data specificata.
L'intervallo dt_begin - data attuale non può superare il mese.
L'interrogazione è paginata, un risultato vuoto significa che si è raggiunta l'ultima pagina.

Schema URL: http://api_endpoint/find_modified_customers

Parametri POST:

access_key - chiave di riconoscimento
dt_begin - data iniziale dell'intervallo di ricerca
page - pagina dei risultati (parte da 0)

La risposta sarà un oggetto JSON aderente allo standard descritto precedentemente con payload cosi definito:
{
  customers: [
    {
      id_customer: <string>, //identificativo dell'anagrafica cliente
      address_title: <string>, //titolo appellativo
      address_first_name: <string>, //nome di battesimo (opzionale)
      address_last_name_company: <string>, //cognome o ragione sociale
      address_co: <string>, //presso (opzionale)
      address_address: <string>, //indirizzo
      address_locality: <string>, //localita
      address_province: <string>, //sigla provincia (opzionale se estero)
      address_zip: <string>, //CAP (opzionale se estero)
      address_country_code: <string>, //codice nazione ISO 3166 (IT, FR, UK...)
      sex: <"m"|"f"|"">, //sesso (opzionale)
      cod_fisc: <string>, //codice fiscale
      piva: <string>, //partita iva (opzionale)
      phone_mobile: <string>, //cellulare (opzionale)
      phone_landline: <string>, //telefono fisso (opzionale)
      email_primary: <string>, //email primaria (opzionale)
      id_job: <integer>, //id professione (opzionale)
      id_qualification: <integer>, //id titolo di studio (opzionale)
      id_tipo_anagrafica: <integer>, //id tipo anagrafica (opzionale) §
      birth_date: "yyyy-mm-dd", //data di nascita (opzionale)
      consent_tos: <"true"|"false">, //consenso terms of service
      consent_marketing: <"true"|"false">, //consenso comunicazioni marketing
      consent_profiling: <"true"|"false">, //consenso profilazione
      consent_update_date: "yyyy-mm-dd", //data di ultimo aggiornamento del consenso
      creation_date: "yyyy-mm-dd", //data di creazione §
      modified_date: "yyyy-mm-dd" //data di ultima modifica
    },
    ...
  ]
}
*/


package it.giunti.apg.ws.api03;

import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.ApiServices;
import it.giunti.apg.shared.model.CacheCrm;
import it.giunti.apg.ws.business.ValidationBusiness;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.DateType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*@WebServlet(Constants.PATTERN_API01+Constants.PATTERN_FIND_MODIFIED_CRM_DATA)*/
public class FindModifiedCrmDataServlet extends ApiServlet {
	private static final long serialVersionUID = 7467463503748183261L;
	private static final String FUNCTION_NAME = Constants.PATTERN_FIND_MODIFIED_CRM_DATA;
	private static final Logger LOG = LoggerFactory.getLogger(FindModifiedCrmDataServlet.class);

	/*example testing url:
	http://127.0.0.1:8080/apgws/api03/find_modified_crm_data?access_key=1234 &id_magazine=Q&dt_begin=2017-12-27&dt_end=2018-01-03&action=RENEWAL_WARNING&page=0
	*/
	
	private static final int PAGE_SIZE = 250;

    public FindModifiedCrmDataServlet() {
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
				Map<Anagrafiche, CacheCrm> anaMap = findAnagraficheData(ses,
						dtBegin, offset, PAGE_SIZE);
				JsonObjectBuilder joBuilder = schemaBuilder(ses, anaMap);
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

    @SuppressWarnings("unchecked")
	private Map<Anagrafiche, CacheCrm> findAnagraficheData(Session ses,
    		Date dtBegin, int offset, int pageSize) {
    	Map<Anagrafiche, CacheCrm> map = new HashMap<Anagrafiche, CacheCrm>();
    	String hql = "from Anagrafiche a, CacheCrm cache where "
    			+ "a.id = cache.idAnagrafica and "
				+ "a.dataModifica >= :dt1 and "
				+ "a.idAnagraficaDaAggiornare is null "
				+ "order by a.dataModifica desc";
    	Query q = ses.createQuery(hql);
		q.setParameter("dt1", dtBegin, DateType.INSTANCE);
		q.setFirstResult(offset);
		q.setMaxResults(pageSize);
		List<Object[]> objList = (List<Object[]>) q.list();
    	for (Object[] obj:objList) {
    		Anagrafiche a = (Anagrafiche) obj[0];
    		CacheCrm ca = (CacheCrm) obj[1];
    		map.put(a, ca);
    	}
    	return map;
    }
    
	private JsonObjectBuilder schemaBuilder(Session ses, Map<Anagrafiche, CacheCrm> anaMap) 
			throws BusinessException {
		JsonBuilderFactory factory = Json.createBuilderFactory(null);
		JsonArrayBuilder arrayBuilder = factory.createArrayBuilder();
		for (Anagrafiche ana:anaMap.keySet()) {
			CacheCrm cache = anaMap.get(ana);
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
			add(ob, Constants.PARAM_ID_TIPO_ANAGRAFICA, ana.getIdTipoAnagrafica());
			add(ob, Constants.PARAM_BIRTH_DATE, ana.getDataNascita());
			add(ob, Constants.PARAM_CONSENT_TOS, ana.getConsensoTos());
			add(ob, Constants.PARAM_CONSENT_MARKETING, ana.getConsensoMarketing());
			add(ob, Constants.PARAM_CONSENT_PROFILING, ana.getConsensoProfilazione());
			add(ob, Constants.PARAM_CONSENT_UPDATE_DATE, ana.getDataAggiornamentoConsenso());
			add(ob, Constants.PARAM_CREATION_DATE, ana.getDataCreazione());
			
			add(ob, Constants.PARAM_MODIFIED_DATE, cache.getModifiedDate());
			add(ob, Constants.PARAM_CUSTOMER_TYPE, cache.getCustomerType());
			
			try {
				Method getter;
				for (int i=0; i<8; i++) {
					getter = CacheCrm.class.getMethod("getOwnSubscriptionIdentifier"+i);
					String identifier = (String) getter.invoke(cache);
					if (identifier != null) {
						if (identifier.length() > 0) {
								add(ob, "own_subscription_identifier_"+i, identifier);
								getter = CacheCrm.class.getMethod("getOwnSubscriptionBlocked"+i);
								Boolean blocked = (Boolean) getter.invoke(cache);
								add(ob, "own_subscription_blocked_"+i, blocked.toString());
								getter = CacheCrm.class.getMethod("getOwnSubscriptionBegin"+i);
								Date ownBegin = (Date) getter.invoke(cache);
								add(ob, "own_subscription_begin_"+i, ownBegin);
								getter = CacheCrm.class.getMethod("getOwnSubscriptionEnd"+i);
								Date ownEnd = (Date) getter.invoke(cache);
								add(ob, "own_subscription_end_"+i, ownEnd);
								getter = CacheCrm.class.getMethod("getGiftSubscriptionEnd"+i);
								Date giftEnd = (Date) getter.invoke(cache);
								add(ob, "gift_subscription_end_"+i, giftEnd);
								getter = CacheCrm.class.getMethod("getSubscriptionCreationDate"+i);
								Date creation = (Date) getter.invoke(cache);
								add(ob, "subscription_creation_date_"+i, creation);
						}
					}
				}
			} catch (NoSuchMethodException | SecurityException | 
					IllegalAccessException | IllegalArgumentException |
					InvocationTargetException e) {
				throw new BusinessException(e.getMessage(), e);
			}
			arrayBuilder.add(ob);
		}
		JsonObjectBuilder objectBuilder = factory.createObjectBuilder();
		objectBuilder.add("customers", arrayBuilder);
		return objectBuilder;
	}

}
