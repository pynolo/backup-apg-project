package it.giunti.apgws.server.ws;

import it.giunti.apg.server.business.WsLogBusiness;
import it.giunti.apg.server.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apgws.server.WsConstants;
import it.giunti.apgws.server.business.HbsauthBusiness;
import it.giunti.apgws.wsbeans.hbsauth.AuthenticationParams;
import it.giunti.apgws.wsbeans.hbsauth.AuthenticationResult;
import it.giunti.apgws.wsbeans.hbsauth.Hbsauth;

import javax.jws.WebService;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebService(serviceName = "hbsauth", portName = "hbsauthSOAP",
		endpointInterface = "it.giunti.apgws.wsbeans.hbsauth.Hbsauth",
		targetNamespace = "http://applicazioni.giunti.it/apgws/hbsauth",
		wsdlLocation = "WEB-INF/wsdl/hbsauth.wsdl")
public class HbsauthImpl implements Hbsauth {

	private static final Logger LOG = LoggerFactory.getLogger(HbsauthImpl.class);

	@Override
	public AuthenticationResult authentication(AuthenticationParams parameters) {
		//parameter validation
		String username = "";
		if (parameters.getUsername() != null) username = parameters.getUsername().toUpperCase();
		if (username.length() > 16) username = username.substring(0, 16);
		String password = "";
		if (parameters.getPassword() != null) password = parameters.getPassword().toUpperCase();
		if (password.length() > 16) password = password.substring(0, 16);
		String publicationId = "";
		if (parameters.getPublicationId() != null) publicationId = parameters.getPublicationId().toUpperCase();
		if (publicationId.length() > 4) publicationId = publicationId.substring(0, 4);
		AuthenticationResult authResult = null;
		//Esegue la ricerca
		Session ses = SessionFactory.getSession();
		try {
			authResult = HbsauthBusiness.buildAuthenticationResult(ses,
					username, password, publicationId);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			authResult = new AuthenticationResult();
			authResult.getAuthData().setAuthorized(Boolean.FALSE);
			authResult.getAuthData().setSubscribed(Boolean.FALSE);
			authResult.getAuthData().setMessage(WsConstants.WS_ERR_SYSTEM_DESC);
		}
		//LOG_WS
		try {
			String params = "username="+username+WsConstants.SERVICE_SEPARATOR+
					"publicationId="+publicationId;
			String resString = WsConstants.SERVICE_OK;
			if (!authResult.getAuthData().isAuthorized() || !authResult.getAuthData().isSubscribed()) {
				resString = "";
				if (!authResult.getAuthData().isAuthorized()) resString += "not authorized ";
				if (!authResult.getAuthData().isSubscribed()) resString += "not subscribed ";
				if (authResult.getAuthData().getMessage() != null) {
					resString += authResult.getAuthData().getMessage();
				}
			}
			WsLogBusiness.writeWsLog(WsConstants.SERVICE_HBSAUTH,
					"authentication", params, resString);
		} catch (BusinessException e) {
			LOG.error(e.getMessage(), e);
		}
				
		return authResult;
	}
	

}

