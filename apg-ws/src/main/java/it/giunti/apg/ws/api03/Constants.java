package it.giunti.apg.ws.api03;

import java.text.SimpleDateFormat;

public class Constants {

	public static final SimpleDateFormat FORMAT_API_DATE = new SimpleDateFormat("yyyy-MM-dd");

	public static final String USER_API = "api";
	
	public static final String PATTERN_FIND_ISSUES = "/find_issues";
	public static final String PATTERN_GET_SUBSCRIPTION_ISSUES = "/get_subscription_issues";
	public static final String PATTERN_GET_CUSTOMER = "/get_customer";
	public static final String PATTERN_CREATE_CUSTOMER = "/create_customer";
	public static final String PATTERN_UPDATE_CUSTOMER = "/update_customer";
	public static final String PATTERN_AUTHENTICATE_BY_COD_ABBO = "/authenticate_by_cod_abbo";
	public static final String PATTERN_REQUEST_CUSTOMER_MERGE = "/request_customer_merge";
	public static final String PATTERN_GET_CUSTOMER_SUBSCRIPTIONS = "/get_customer_subscriptions";
	public static final String PATTERN_GET_SUBSCRIPTION = "/get_subscription";
	public static final String PATTERN_CREATE_SUBSCRIPTION = "/create_subscription";
	public static final String PATTERN_UPDATE_OFFERING = "/update_offering";
	public static final String PATTERN_UPDATE_SUBSCRIPTION_OFFERING = "/update_subscription_offering";
	public static final String PATTERN_UPDATE_OPTIONS = "/update_options";
	public static final String PATTERN_PAY_SUBSCRIPTION = "/pay_subscription";
	public static final String PATTERN_GET_CUSTOMER_INVOICES = "/get_customer_invoices";
	public static final String PATTERN_GET_INVOICE_FILE = "/get_invoice_file";
	public static final String PATTERN_GET_PAYMENT_FORM_FILE = "/get_payment_form_file";
	public static final String PATTERN_GET_OFFERING = "/get_offering";
	public static final String PATTERN_GET_OPTION = "/get_option";
	public static final String PATTERN_FIND_SUBSCRIPTIONS_BY_ACTION = "/find_subscriptions_by_action";
	
	public static final String PARAM_ACCESS_KEY = "access_key";
	public static final String PARAM_ID_MAGAZINE = "id_magazine";
	public static final String PARAM_DT_BEGIN = "dt_begin";
	public static final String PARAM_DT_END = "dt_end";
	public static final String PARAM_ID_SUBSCRIPTION = "id_subscription";
	public static final String PARAM_ID_CUSTOMER = "id_customer";
	public static final String PARAM_ID_CUSTOMER_PROPOSED = "id_customer_proposed";
	public static final String PARAM_ID_INVOICE = "id_invoice";
	public static final String PARAM_SEX = "sex";
	public static final String PARAM_ADDRESS_FIRST_NAME = "address_first_name"; 
	public static final String PARAM_ADDRESS_LAST_NAME_COMPANY = "address_last_name_company";
	public static final String PARAM_ADDRESS_TITLE = "address_title";
	public static final String PARAM_ADDRESS_CO = "address_co";
	public static final String PARAM_ADDRESS_ADDRESS = "address_address"; 
	public static final String PARAM_ADDRESS_LOCALITY = "address_locality"; 
	public static final String PARAM_ADDRESS_PROVINCE = "address_province"; 
	public static final String PARAM_ADDRESS_ZIP = "address_zip"; 
	public static final String PARAM_ADDRESS_COUNTRY_CODE = "address_country_code";
	public static final String PARAM_BILLING_FIRST_NAME = "billing_first_name"; 
	public static final String PARAM_BILLING_LAST_NAME_COMPANY = "billing_last_name_company";
	public static final String PARAM_BILLING_TITLE = "billing_title";
	public static final String PARAM_BILLING_CO = "billing_co";
	public static final String PARAM_BILLING_ADDRESS = "billing_address";
	public static final String PARAM_BILLING_LOCALITY = "billing_locality";
	public static final String PARAM_BILLING_PROVINCE = "billing_province";
	public static final String PARAM_BILLING_ZIP = "billing_zip";
	public static final String PARAM_BILLING_COUNTRY_CODE = "billing_country_code";
	public static final String PARAM_COD_FISC = "cod_fisc";
	public static final String PARAM_PIVA = "piva";
	public static final String PARAM_PHONE_MOBILE = "phone_mobile";
	public static final String PARAM_PHONE_LANDLINE = "phone_landline";
	public static final String PARAM_EMAIL_PRIMARY = "email_primary";
	public static final String PARAM_EMAIL_SECONDARY = "email_secondary";
	public static final String PARAM_ID_JOB = "id_job";
	public static final String PARAM_ID_QUALIFICATION = "id_qualification";
	public static final String PARAM_ID_TIPO_ANAGRAFICA = "id_tipo_anagrafica";
	public static final String PARAM_CONSENT_TOS_DATE = "consent_tos_date";
	public static final String PARAM_CONSENT_MARKETING_DATE = "consent_marketing_date";
	public static final String PARAM_CONSENT_PROFILING_DATE = "consent_profiling_date";
	public static final String PARAM_CONSENT_TOS = "consent_tos";
	public static final String PARAM_CONSENT_MARKETING = "consent_marketing";
	public static final String PARAM_CONSENT_PROFILING = "consent_profiling";
	public static final String PARAM_COD_ABBO = "cod_abbo";
	public static final String PARAM_ID_OFFERING = "id_offering";
	public static final String PARAM_ID_RENEWAL_OFFERING = "id_renewal_offering";
	public static final String PARAM_ID_OPTION = "id_option";
	public static final String PARAM_ID_CUSTOMER_RECIPIENT = "id_customer_recipient";
	public static final String PARAM_ID_CUSTOMER_PAYER = "id_customer_payer";
	public static final String PARAM_OPTIONS = "options";
	public static final String PARAM_QUANTITY = "quantity";
	public static final String PARAM_CM_FIRST_ISSUE = "cm_first_issue";
	public static final String PARAM_CM_LAST_ISSUE = "cm_last_issue";
	public static final String PARAM_ID_PAYMENT_TYPE = "id_payment_type";
	public static final String PARAM_PAYMENT_AMOUNT = "payment_amount";
	public static final String PARAM_PAYMENT_DATE = "payment_date";
	public static final String PARAM_PAYMENT_TRN = "payment_trn";
	public static final String PARAM_PAYMENT_NOTE = "payment_note";
	public static final String PARAM_BIRTH_DATE = "birth_date";
	public static final String PARAM_RENEWAL_DATE = "renewal_date";
	public static final String PARAM_ACTION = "action";
	public static final String PARAM_PAGE = "page";
	
	public static final String VALUE_ACTION_CHARGE_WARNING = "CHARGE_WARNING";
	public static final String VALUE_ACTION_CHARGE = "CHARGE";
	public static final String VALUE_ACTION_RENEWAL_WARNING = "RENEWAL_WARNING";
	public static final String VALUE_ACTION_RENEWAL = "RENEWAL";
}
