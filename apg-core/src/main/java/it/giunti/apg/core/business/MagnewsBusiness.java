package it.giunti.apg.core.business;

import java.net.MalformedURLException;
import java.net.URL;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.persistence.ConfigDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.soap.magnews.Credentials;
import it.giunti.apg.soap.magnews.MagNewsAPI;
import it.giunti.apg.soap.magnews.MagNewsAPIService;

public class MagnewsBusiness {
	private static final Logger LOG = LoggerFactory.getLogger(MagnewsBusiness.class);

	private static String MAGNEWS_ACCESS_TOKEN = "magnewsAccessToken";
	private static ConfigDao configDao = new ConfigDao();
	
	private static String getAccessToken() throws BusinessException {
		Session ses = SessionFactory.getSession();
		String result = null;
		try {
			result = configDao.findValore(ses, MAGNEWS_ACCESS_TOKEN);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (result != null) {
			return result;
		}
		throw new BusinessException(AppConstants.MSG_EMPTY_RESULT);
	}
	
	public static void connect() throws BusinessException {
		URL serviceUrl = null;
		try {
			serviceUrl = new URL(ServerConstants.MAGNEWS_WSDL);
		} catch (MalformedURLException e) {
			throw new BusinessException(e.getMessage(), e);
		}
		String password = getAccessToken();
		Credentials c = new Credentials();
		c.setPassword(password);

		MagNewsAPIService service = new MagNewsAPIService(serviceUrl);
		MagNewsAPI port = service.getMagNewsAPIPort();
		
	}
}
