package it.giunti.apg.ws.business;

import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.model.ApiServices;
import it.giunti.apg.shared.model.Province;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidationBusiness {
	private static final Logger LOG = LoggerFactory.getLogger(ValidationBusiness.class);
	
	private static final Pattern emailPattern = Pattern.compile(AppConstants.REGEX_EMAIL, Pattern.CASE_INSENSITIVE);
	private static final Pattern eanPattern = Pattern.compile(AppConstants.REGEX_EAN, Pattern.CASE_INSENSITIVE);
	private static final Pattern codFisPattern = Pattern.compile(AppConstants.REGEX_CODFISC, Pattern.CASE_INSENSITIVE);
	private static final Pattern codFisAltPattern = Pattern.compile(AppConstants.REGEX_CODFISC_ALT, Pattern.CASE_INSENSITIVE);
	
	private static Map<String,Province> provinceMap = null;
	private static Map<String,ApiServices> serviceMap = null;
	
	public static void resetAccessKeyCache() {
		serviceMap = null;
	}
	
	public static String cleanInput(String s, Integer maxLength) throws ValidationException {
		if (s == null) return null;
		if (s.length() == 0) return null;
		if (s.length() > maxLength) throw new ValidationException("Value '"+s+"' is longer than "+maxLength+" chars");//s = s.substring(0, maxLength);
		return s;
	}
	
	public static ApiServices validateAccessKey(String accessKey) 
			throws BusinessException {
		if (accessKey == null) return null;
		if (serviceMap == null) loadAccessKeys();
		ApiServices found = serviceMap.get(accessKey);
		if (found != null) {
			return found;
		}
		return null;
	}

	public static String validateEmail(String email) throws ValidationException {
		return validateEmail(null, email);
	}
	
	public static String validateEmail(String userUid, String email)
			throws ValidationException {
		if (email == null) {
			new ValidationException("Email non valida");
		} else if (email.equals("")) new ValidationException("Email non valida");
		Matcher matcher = emailPattern.matcher(email);
		if (!matcher.matches()) {
			throw new ValidationException("Email non valida");
		}
		return email;
	}
	
	public static String validateEan(String stringValue)
			throws ValidationException {
		Matcher matcher = eanPattern.matcher(stringValue);
		if (!matcher.matches()) {
			throw new ValidationException("Ean non valido");
		}
		return stringValue;
	}
	
	public static String validateCodiceFiscale(String codFis)
			throws ValidationException {
		if (codFis == null) {
			new ValidationException("Codice fiscale non valido");
		} else if (codFis.equals("")) new ValidationException("Codice fiscale non valido");
		Matcher matcher1 = codFisPattern.matcher(codFis);
		Matcher matcher2 = codFisAltPattern.matcher(codFis);
		if (!matcher1.matches() && !matcher2.matches()) {
			throw new ValidationException("Codice fiscale non valido");
		}
		return codFis;
	}
	
	public static String validatePartitaIva(String pIva)
			throws ValidationException {
		if (pIva == null) {
			new ValidationException("Partita iva non valida");
		} else {
			if (!(pIva.length() == 11 || pIva.length()==16))
					throw new ValidationException("Partita iva non valida");
		}
		return pIva;
	}
	
	@SuppressWarnings("unchecked")
	private static void loadAccessKeys() 
			throws BusinessException {
		Session ses = SessionFactory.getSession();
		List<ApiServices> accessKeyList = null;
		try {
			String hql = "from ApiServices s order by s.nome";
			Query q = ses.createQuery(hql);
			accessKeyList = q.list();
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		serviceMap = new HashMap<String, ApiServices>();
		for (ApiServices ak:accessKeyList) {
			serviceMap.put(ak.getAccessKey(), ak);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void loadProvinceCodes(Session ses)
			throws BusinessException {
		try {
			List<Province> result = null;
			String hql="from Province pro order by pro.nomeProvincia asc";
			Query q = ses.createQuery(hql);
			result = q.list();
			provinceMap = new HashMap<String, Province>();
			for (Province p:result) {
				provinceMap.put(p.getId(),p);
			}
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}
	}
}
