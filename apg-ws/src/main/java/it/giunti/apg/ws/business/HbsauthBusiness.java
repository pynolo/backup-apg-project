package it.giunti.apg.ws.business;

import it.giunti.apg.core.DateUtil;
import it.giunti.apg.core.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.OpzioniIstanzeAbbonamenti;
import it.giunti.apg.ws.WsConstants;
import it.giunti.apgws.wsbeans.hbsauth.AuthData;
import it.giunti.apgws.wsbeans.hbsauth.AuthenticationResult;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.XMLGregorianCalendar;

import org.hibernate.Session;

public class HbsauthBusiness {

	/**
	 * Metodo che verifica le politiche commerciali attuali, per sapere se un dato abbonamento
	 * 
	 */
	public static boolean hasDigitalSubscription(IstanzeAbbonamenti ia) {
		boolean result = false;
		if (ia.getListino().getDigitale()) {
			result = true;
		} else {
			for(OpzioniIstanzeAbbonamenti opz:ia.getOpzioniIstanzeAbbonamentiSet()) {
				if (opz.getOpzione().getDigitale()) result = true;
			}
		}
		return result;
	}
	
	public static AuthenticationResult buildAuthenticationResult(Session ses,
			String username, String password, String publicationId) {
		Date today = DateUtil.now();
		AuthenticationResult authResult = new AuthenticationResult();
		authResult.setAuthData(new AuthData());
		boolean authorized = false;
		if (username.startsWith(publicationId)) {
			//La rivista richiesta corrisponde al codice abbonamento
			//IstanzeAbbonamenti ia = new IstanzeAbbonamentiDao().findUltimaIstanzaByCodice(ses, username);
			IstanzeAbbonamenti ia = new IstanzeAbbonamentiDao().findIstanzaByCodiceData(ses, username, today);
			if (ia != null) {
				//Lo username esiste, adesso va controllata la password
				String provincia = ia.getAbbonato().getIndirizzoPrincipale().getProvincia();
				if (provincia == null) provincia = "";
				if (!ia.getAbbonato().getIndirizzoPrincipale().getNazione().getId()
						.equalsIgnoreCase(AppConstants.DEFAULT_ID_NAZIONE_ITALIA)) {
					provincia = WsConstants.PROVINCIA_ESTERO_AUTH;
				}
				if (password.equalsIgnoreCase(provincia)) {
					//La password corrisponde, può entrare
					authResult.getAuthData().setAuthorized(Boolean.TRUE);
					boolean subscribed = hasDigitalSubscription(ia);
					authResult.getAuthData().setSubscribed(subscribed);
					if (subscribed) {
						//Calcola l'intervallo di validità e lo mette in authResult
//						List<IstanzeAbbonamenti> iaList = new IstanzeAbbonamentiDao()
//								.findIstanzeByCodice(ses, username, 0, Integer.MAX_VALUE);
						Calendar cal = new GregorianCalendar();
						//Begin date
//						cal.setTime(AbbonamentiDateBusiness.getBeginOfFirstInstance(iaList));
						cal.setTime(ia.getFascicoloInizio().getDataPubblicazione());
						cal.add(Calendar.DAY_OF_MONTH, (-1)*WsConstants.SUBSCRIPTION_RANGE_EXTENSION_DAYS);
						XMLGregorianCalendar beginDateXml = CommonBusiness.dateToXmlDate(cal.getTime());
						authResult.getAuthData().setSubscriptionStartDate(beginDateXml);
						//End date
//						cal.setTime(AbbonamentiDateBusiness.getEndOfLastInstance(ses, iaList));
						cal.setTime(ia.getFascicoloFine().getDataPubblicazione());
						cal.add(Calendar.DAY_OF_MONTH, WsConstants.SUBSCRIPTION_RANGE_EXTENSION_DAYS);
						XMLGregorianCalendar endDateXml = CommonBusiness.dateToXmlDate(cal.getTime());
						authResult.getAuthData().setSubscriptionExpiryDate(endDateXml);
						//Messaggio vuoto
						authResult.getAuthData().setMessage(null);
					}
					authorized=true;
				}
			}
		}
		
		if (!authorized) {
			authResult.getAuthData().setAuthorized(Boolean.FALSE);
			authResult.getAuthData().setSubscribed(Boolean.FALSE);
			authResult.getAuthData().setMessage(WsConstants.WS_ERR_NOT_FOUND_DESC);
		}
		return authResult;
	}
}
