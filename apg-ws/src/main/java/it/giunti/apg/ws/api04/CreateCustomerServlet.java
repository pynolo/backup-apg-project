package it.giunti.apg.ws.api04;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
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

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.business.SearchBusiness;
import it.giunti.apg.core.business.WsLogBusiness;
import it.giunti.apg.core.persistence.AnagraficheDao;
import it.giunti.apg.core.persistence.ContatoriDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.IndirizziDao;
import it.giunti.apg.core.persistence.NazioniDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.ApiServices;
import it.giunti.apg.shared.model.Indirizzi;
import it.giunti.apg.shared.model.Nazioni;
import it.giunti.apg.shared.model.Professioni;
import it.giunti.apg.shared.model.Province;
import it.giunti.apg.shared.model.TitoliStudio;
import it.giunti.apg.ws.WsConstants;
import it.giunti.apg.ws.api03.ApiServlet;
import it.giunti.apg.ws.api03.BaseJsonFactory;
import it.giunti.apg.ws.api03.BaseUrlSingleton;
import it.giunti.apg.ws.api03.Constants;
import it.giunti.apg.ws.api03.ErrorEnum;
import it.giunti.apg.ws.business.ValidationBusiness;

/*@WebServlet(Constants.PATTERN_API01+Constants.PATTERN_CREATE_CUSTOMER)*/
public class CreateCustomerServlet extends ApiServlet {
	private static final long serialVersionUID = -644995367420077098L;
	private static final String FUNCTION_NAME = Constants.PATTERN_CREATE_CUSTOMER;
	private static final String SERVICE = WsConstants.SERVICE_API03;
	private static final Logger LOG = LoggerFactory.getLogger(CreateCustomerServlet.class);

	/*example testing url:
	 http://127.0.0.1:8888/api01/create_customer?access_key=1234&sex=f&address_first_name=genoveffa&address_last_name_company=gongolini&address_title=sig.ra&address_co=nugolini%20inc.&address_address=via%20delle%20donne%20volgari%2011&address_locality=ghiondone&address_country_code=fr&billing_first_name=genoveffa&billing_last_name_company=gongolini&billing_title=sig.ra&billing_co=nugolini2%20inc.&billing_address=via%20dei%20cani%20secchi%2014&billing_locality=vaporella&billing_province=cz&billing_zip=85020&billing_country_code=it&cod_fisc=CLLLRT82S25D575M&piva=0983321201&phone_mobile=333/3333333&phone_landline=087425532&email_primary=genofeffy%40gmail.com&email_secondary=trogolona%40tiscali.it&id_job=2&id_qualification=4&id_tipo_anagrafica=azn&marketing_consent=false*/
	/**
     * @see HttpServlet#HttpServlet()
     */
    public CreateCustomerServlet() {
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
				String addressFirstName = null;
				String addressLastName = null;
				String addressTitle = null;
				String addressCo = null;
				String addressAddress = null;
				String addressLocality = null;
				String addressProvince = null;
				String addressZip = null;
				Nazioni addressCountry = null;
				String billingFirstName = null;
				String billingLastName = null;
				String billingTitle = null;
				String billingCo = null;
				String billingAddress = null;
				String billingLocality = null;
				String billingProvince = null;
				String billingZip = null;
				Nazioni billingCountry = null;
				String sex = null;
				String pIva = null;
				String codFisc = null;
				String phoneMobile = null;
				String phoneLandline = null;
				String emailPrimary = null;
				String emailSecondary = null;
				Professioni job = null;
				TitoliStudio qualification = null;
				String idTipoAnagrafica = null;
				boolean consentTos = false;
				boolean consentMarketing = false;
				boolean consentProfiling = false;
				Date birthDate = null;
	
				try {
					NazioniDao nazioniDao = new NazioniDao();
					//first_name - nome di battesimo (opzionale)
					addressFirstName = request.getParameter(Constants.PARAM_ADDRESS_FIRST_NAME);
					addressFirstName = ValidationBusiness.cleanInput(addressFirstName, 25);
				    //last_name_company - cognome o ragione sociale
					addressLastName = request.getParameter(Constants.PARAM_ADDRESS_LAST_NAME_COMPANY);
					if (addressLastName == null) result = BaseJsonFactory.buildBaseObject(ErrorEnum.EMPTY_PARAMETER, "Last name is empty");
					addressLastName = ValidationBusiness.cleanInput(addressLastName, 30);
				    //title - titolo appellativo (opzionale)
					addressTitle = request.getParameter(Constants.PARAM_ADDRESS_TITLE);
					addressTitle = ValidationBusiness.cleanInput(addressTitle, 6);
				    //sex - <m|f> (opzionale, non specificare per persone giuridiche) 
					sex = request.getParameter(Constants.PARAM_SEX);
					if (sex != null) {
						if (!sex.equalsIgnoreCase("F") && !sex.equalsIgnoreCase("M")) throw new ValidationException(Constants.PARAM_SEX+" wrong format");
					}
					sex = ValidationBusiness.cleanInput(sex, 1);
				    //address_co - presso (opzionale)
					addressCo = request.getParameter(Constants.PARAM_ADDRESS_CO);
					addressCo = ValidationBusiness.cleanInput(addressCo, 28);
				    //address_country_code - codice nazione (IT, FR, UK...)
					String addressCountryCode = request.getParameter(Constants.PARAM_ADDRESS_COUNTRY_CODE);
					if (addressCountryCode == null) result = BaseJsonFactory.buildBaseObject(ErrorEnum.EMPTY_PARAMETER, Constants.PARAM_ADDRESS_COUNTRY_CODE+" is empty");
					addressCountryCode = ValidationBusiness.cleanInput(addressCountryCode, 2);
					if (addressCountryCode != null) {
						addressCountryCode = addressCountryCode.toUpperCase();
						addressCountry = nazioniDao.findBySiglaNazione(ses, addressCountryCode);
						if (addressCountry == null) throw new ValidationException(Constants.PARAM_ADDRESS_COUNTRY_CODE+" value not found");
					}
				    //address_address - indirizzo 
					addressAddress = request.getParameter(Constants.PARAM_ADDRESS_ADDRESS);
					if (addressAddress == null) result = BaseJsonFactory.buildBaseObject(ErrorEnum.EMPTY_PARAMETER, Constants.PARAM_ADDRESS_ADDRESS+" is empty");
				    addressAddress = ValidationBusiness.cleanInput(addressAddress, 36);
					//address_locality - localita 
					addressLocality = request.getParameter(Constants.PARAM_ADDRESS_LOCALITY);
					if (addressLocality == null) result = BaseJsonFactory.buildBaseObject(ErrorEnum.EMPTY_PARAMETER, Constants.PARAM_ADDRESS_LOCALITY+" is empty");
					addressLocality = ValidationBusiness.cleanInput(addressLocality, 36);
					//address_province
					addressProvince = request.getParameter(Constants.PARAM_ADDRESS_PROVINCE);
					addressProvince = ValidationBusiness.cleanInput(addressProvince, 2);
					if (addressProvince == null && addressCountry.getId().equals(AppConstants.DEFAULT_ID_NAZIONE_ITALIA))
							result = BaseJsonFactory.buildBaseObject(ErrorEnum.EMPTY_PARAMETER, Constants.PARAM_ADDRESS_PROVINCE+" is empty");
					if (addressProvince != null) {
						addressProvince = addressProvince.toUpperCase();
						Province pv = GenericDao.findById(ses, Province.class, addressProvince);
						if (pv == null) throw new ValidationException(Constants.PARAM_ADDRESS_PROVINCE+" value not found");
					}
					//address_zip - CAP (opzionale se estero) 
					addressZip = request.getParameter(Constants.PARAM_ADDRESS_ZIP);
					if (addressZip == null && addressCountry.getId().equals(AppConstants.DEFAULT_ID_NAZIONE_ITALIA))
							result = BaseJsonFactory.buildBaseObject(ErrorEnum.EMPTY_PARAMETER, Constants.PARAM_ADDRESS_ZIP+" is empty");
					addressZip = ValidationBusiness.cleanInput(addressZip, 5);
					//first_name - nome di battesimo (opzionale)
					billingFirstName = request.getParameter(Constants.PARAM_BILLING_FIRST_NAME);
					billingFirstName = ValidationBusiness.cleanInput(billingFirstName, 25);
				    //last_name_company - cognome o ragione sociale
					billingLastName = request.getParameter(Constants.PARAM_BILLING_LAST_NAME_COMPANY);
					billingLastName = ValidationBusiness.cleanInput(billingLastName, 30);
				    //title - titolo appellativo (opzionale)
					billingTitle = request.getParameter(Constants.PARAM_BILLING_TITLE);
					billingTitle = ValidationBusiness.cleanInput(billingTitle, 6);
				    //billing_co - fatturazione/presso (opzionale)
					billingCo = request.getParameter(Constants.PARAM_BILLING_CO);
					billingCo = ValidationBusiness.cleanInput(billingCo, 28);
				    //billing_country_code - fatturazione/codice nazione (IT, FR, UK...) (opzionale) 
					String billingCountryCode = request.getParameter(Constants.PARAM_BILLING_COUNTRY_CODE);
					if (billingCountryCode == null) billingCountryCode = "IT";
					billingCountryCode = ValidationBusiness.cleanInput(billingCountryCode, 2);
					if (billingCountryCode != null) {
						billingCountryCode = billingCountryCode.toUpperCase();
						billingCountry = nazioniDao.findBySiglaNazione(ses, billingCountryCode);
						if (billingCountry == null) throw new ValidationException(Constants.PARAM_BILLING_COUNTRY_CODE+" value not found");
					}
				    //billing_address - fatturazione/indirizzo (opzionale) 
					billingAddress = request.getParameter(Constants.PARAM_BILLING_ADDRESS);
					billingAddress = ValidationBusiness.cleanInput(billingAddress, 36);
					//billing_locality - fatturazione/localita (opzionale) 
				    billingLocality = request.getParameter(Constants.PARAM_BILLING_LOCALITY);
					billingLocality = ValidationBusiness.cleanInput(billingLocality, 36);
					//billing_province
					billingProvince = request.getParameter(Constants.PARAM_BILLING_PROVINCE);
					billingProvince = ValidationBusiness.cleanInput(billingProvince, 2);
					if (billingProvince != null) {
						billingProvince = billingProvince.toUpperCase();
						Province pv = GenericDao.findById(ses, Province.class, billingProvince);
						if (pv == null) throw new ValidationException(Constants.PARAM_BILLING_PROVINCE+" value not found");
					}
					//billing_zip - fatturazione/CAP (opzionale) 
					billingZip = request.getParameter(Constants.PARAM_BILLING_ZIP);
					billingZip = ValidationBusiness.cleanInput(billingZip, 5);

					//cod_fisc - codice fiscale 
					codFisc = request.getParameter(Constants.PARAM_COD_FISC);
					ValidationBusiness.validateCodiceFiscale(codFisc, addressCountry.getId());
					codFisc = ValidationBusiness.cleanInput(codFisc, 16);
					//piva - partita iva (opzionale) 
					pIva = request.getParameter(Constants.PARAM_PIVA);
					if (pIva != null) ValidationBusiness.validatePartitaIva(pIva, addressCountry.getId());
					pIva = ValidationBusiness.cleanInput(pIva, 16);
					//phone_mobile - cellulare (opzionale)
					phoneMobile = request.getParameter(Constants.PARAM_PHONE_MOBILE);
					phoneMobile = ValidationBusiness.cleanInput(phoneMobile, 32);
					//phone_landline - telefono fisso (opzionale) 
					phoneLandline = request.getParameter(Constants.PARAM_PHONE_LANDLINE);
					phoneLandline = ValidationBusiness.cleanInput(phoneLandline, 32);
					//email_primary - email primaria (opzionale) 
					emailPrimary = request.getParameter(Constants.PARAM_EMAIL_PRIMARY);
					if (emailPrimary != null) ValidationBusiness.validateEmail(emailPrimary);
					emailPrimary = ValidationBusiness.cleanInput(emailPrimary, 64);
					//email_secondary - email secondaria (opzionale) 
					emailSecondary = request.getParameter(Constants.PARAM_EMAIL_SECONDARY);
					if (emailSecondary != null) ValidationBusiness.validateEmail(emailSecondary);
					emailSecondary = ValidationBusiness.cleanInput(emailSecondary, 64);
					//id_job - id professione (opzionale) 
					String idJobS = request.getParameter(Constants.PARAM_ID_JOB);
					idJobS = ValidationBusiness.cleanInput(idJobS, 6);
					try {
						if (idJobS != null) {
							Integer idJob = Integer.parseInt(idJobS);
							if (idJob != null) {
								job = GenericDao.findById(ses, Professioni.class, idJob);
								if (job == null) throw new ValidationException(Constants.PARAM_ID_JOB+" value not found");
							}
						}
					} catch (NumberFormatException nfe) {
						throw new ValidationException(Constants.PARAM_ID_JOB+" wrong format");
					}
					//id_qualification - id titolo di studio (opzionale) 
					String idQualificationS = request.getParameter(Constants.PARAM_ID_QUALIFICATION);
					idQualificationS = ValidationBusiness.cleanInput(idQualificationS, 6);
					try {
						if (idQualificationS != null) {
							Integer idQualification = Integer.parseInt(idQualificationS);
							if (idQualification != null) {
								qualification = GenericDao.findById(ses, TitoliStudio.class, idQualification);
								if (qualification == null) throw new ValidationException(Constants.PARAM_ID_QUALIFICATION+" value not found");
							}
						}
					} catch (NumberFormatException nfe) {
						throw new ValidationException(Constants.PARAM_ID_QUALIFICATION+" wrong format");
					}
					//id_tipo_anagrafica
					idTipoAnagrafica = request.getParameter(Constants.PARAM_ID_TIPO_ANAGRAFICA);
					idTipoAnagrafica = ValidationBusiness.cleanInput(idTipoAnagrafica, 4);
					if (idTipoAnagrafica != null) {
						idTipoAnagrafica = idTipoAnagrafica.toUpperCase();
						String desc = AppConstants.ANAG_DESC.get(idTipoAnagrafica);
						if (desc == null) throw new ValidationException(Constants.PARAM_ID_TIPO_ANAGRAFICA+" value not found");
					}
					//privacy_tos
					String consentTosS = request.getParameter(Constants.PARAM_CONSENT_TOS);
					if (consentTosS != null) {
						consentTos = consentTosS.equalsIgnoreCase("true");
					} else {
						throw new ValidationException(Constants.PARAM_CONSENT_TOS+" value not found");
					}
					if (!consentTos) throw new ValidationException(Constants.PARAM_CONSENT_TOS+" must be 'true'");
					//privacy_marketing
					String consentMarketingS = request.getParameter(Constants.PARAM_CONSENT_MARKETING);
					if (consentMarketingS != null) {
						consentMarketing = consentMarketingS.equalsIgnoreCase("true");
					} else {
						throw new ValidationException(Constants.PARAM_CONSENT_MARKETING+" value not found");
					}
					//privacy_profiling
					String consentProfilingS = request.getParameter(Constants.PARAM_CONSENT_PROFILING);
					if (consentProfilingS != null) {
						consentProfiling = consentProfilingS.equalsIgnoreCase("true");
					} else {
						throw new ValidationException(Constants.PARAM_CONSENT_PROFILING+" value not found");
					}
					//birth_date 
					String birthDateS = request.getParameter(Constants.PARAM_BIRTH_DATE);
					birthDateS = ValidationBusiness.cleanInput(birthDateS, 10);
					if (birthDateS != null) {
						try {
							birthDate = ServerConstants.FORMAT_DAY_SQL.parse(birthDateS);
						} catch (ParseException e) { throw new ValidationException(Constants.PARAM_BIRTH_DATE+" wrong format");}
					}
					
				} catch (ValidationException e) {
					result = BaseJsonFactory.buildBaseObject(ErrorEnum.WRONG_PARAMETER_VALUE, e.getMessage());
					String message = e.getMessage();
					if (message.length() > 256) message = message.substring(0, 256);
					WsLogBusiness.writeWsLog(ses, SERVICE,
							FUNCTION_NAME, allParameters, message);
				}
	
				//build response
				if (result == null) {
					Date now = DateUtil.now();
					Anagrafiche ana = new AnagraficheDao().createAnagrafiche(ses);
					String uid = new ContatoriDao().generateUidCliente(ses);
					ana.setUid(uid);
					ana.setCodiceFiscale(codFisc);
					ana.setConsensoTos(consentTos);
					ana.setConsensoMarketing(consentMarketing);
					ana.setConsensoProfilazione(consentProfiling);
					ana.setDataAggiornamentoConsenso(now);
					ana.setDataModifica(now);
					ana.setEmailPrimaria(emailPrimary);
					ana.setEmailSecondaria(emailSecondary);
					ana.setIdTipoAnagrafica(idTipoAnagrafica);
					ana.setNecessitaVerifica(true);
					ana.setPartitaIva(pIva);
					ana.setProfessione(job);
					ana.setSesso(sex);
					ana.setTelCasa(phoneLandline);
					ana.setTelMobile(phoneMobile);
					ana.setTitoloStudio(qualification);
					ana.setDataNascita(birthDate);
					ana.setIdUtente(Constants.USER_API);
					Indirizzi indP = new Indirizzi();
					indP.setCognomeRagioneSociale(addressLastName);
					indP.setNome(addressFirstName);
					indP.setTitolo(addressTitle);
					indP.setCap(addressZip);
					indP.setDataModifica(now);
					indP.setIndirizzo(addressAddress);
					indP.setLocalita(addressLocality);
					indP.setNazione(addressCountry);
					indP.setPresso(addressCo);
					indP.setProvincia(addressProvince);
					indP.setIdUtente(Constants.USER_API);
					Indirizzi indF = new Indirizzi();
					indF.setCognomeRagioneSociale(billingLastName);
					indF.setNome(billingFirstName);
					indF.setTitolo(billingTitle);
					indF.setCap(billingZip);
					indF.setDataModifica(now);
					indF.setIndirizzo(billingAddress);
					indF.setLocalita(billingLocality);
					indF.setNazione(billingCountry);
					indF.setPresso(billingCo);
					indF.setProvincia(billingProvince);
					indF.setIdUtente(Constants.USER_API);
					
					new IndirizziDao().save(ses, indP);
					new IndirizziDao().save(ses, indF);
					ana.setIndirizzoPrincipale(indP);
					ana.setIndirizzoFatturazione(indF);
					ana.setSearchString(SearchBusiness.buildAnagraficheSearchString(ana));
					new AnagraficheDao().save(ses, ana);
					WsLogBusiness.writeWsLog(ses, SERVICE,
							FUNCTION_NAME, allParameters, WsConstants.SERVICE_OK);
					trn.commit();
					
					JsonObjectBuilder joBuilder = schemaBuilder(ana);
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

	private JsonObjectBuilder schemaBuilder(Anagrafiche ana) throws BusinessException {
		JsonBuilderFactory factory = Json.createBuilderFactory(null);
		JsonObjectBuilder ob = factory.createObjectBuilder();
		add(ob, Constants.PARAM_ID_CUSTOMER, ana.getUid());
		return ob;
	}
	

}
