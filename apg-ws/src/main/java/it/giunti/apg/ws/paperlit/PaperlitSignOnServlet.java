package it.giunti.apg.ws.paperlit;

import it.giunti.apg.core.business.WsLogBusiness;
import it.giunti.apg.core.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.ws.WsConstants;
import it.giunti.apg.ws.business.AbbonamentiDateBusiness;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PaperlitSignOnServlet extends HttpServlet {
	private static final long serialVersionUID = -3975032263427587306L;

	private static final Logger LOG = LoggerFactory.getLogger(PaperlitSignOnServlet.class);
	
	private final String ERROR_PARAMETERS = "Tutti i parametri sono obbligatori";
	private final String ERROR_AUTH = "Le credenziali fornite non sono corrette";
	private final String ERROR_PUBLICATION = "Il periodico che hai richiesto non e' ancora disponibile in formato digitale";
	private final String ERROR_INTERNAL = "Errore nei dati, impossibile eseguire l'autenticazione";
		
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {	
		//Request parameters
		String user = request.getParameter(WsConstants.PAPERLIT_PARAM_USER);
		String password = request.getParameter(WsConstants.PAPERLIT_PARAM_PASSWORD);
		//String deviceId = request.getParameter(WsConstants.PAPERLIT_PARAM_DEVICE_ID);
		String bundleId = request.getParameter(WsConstants.PAPERLIT_PARAM_BUNDLE_ID);
		//String callback = request.getParameter(WsConstants.PAPERLIT_PARAM_CALLBACK);
		boolean validRequest = true;
		if ((user == null) || (password == null) || (bundleId == null)) {
			validRequest = false;
		} else 	if ((user.length() == 0) || (password.length() == 0) || (bundleId.length() == 0)) {
			validRequest = false;
		}
		//Riscontro su APG
		PublicationJso publicationJso = null;
		if (validRequest) {
			if (user.length() > 7) user = user.substring(0, 7);
			List<IstanzeAbbonamenti> iaList;
			try {
				iaList = findIstanzeByCodiceAbb(user.toUpperCase());
				//Autenticazione
				boolean authenticated = authenticate(iaList, password, bundleId);
				if (authenticated) {
					publicationJso = convertPublication(iaList);
				} else {
					publicationJso = errorToPublicationJso(ERROR_AUTH);
				}
			} catch (BusinessException e) {
				LOG.error(e.getMessage(), e);
				publicationJso = errorToPublicationJso(ERROR_INTERNAL);
			}
		} else {
			publicationJso = errorToPublicationJso(ERROR_PARAMETERS);
		}
		//JSON string
		String jsonString = "singlesignon({";
		jsonString += PublicationSerializer.toJson(publicationJso);
		jsonString += "});";
		LOG.debug(jsonString);
		
		//Response
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.print(jsonString);
		
		//LOG_WS
		try {
			String params = "user="+user+WsConstants.SERVICE_SEPARATOR+
					"bundleId="+bundleId;
			String resString = WsConstants.SERVICE_OK;
			if (!publicationJso.getIssubscribed()) {
				resString = publicationJso.getMessage();
			}
			WsLogBusiness.writeWsLog(WsConstants.SERVICE_PAPERLIT,
					"paperlitSignOn", params, resString);
		} catch (BusinessException e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	private List<IstanzeAbbonamenti> findIstanzeByCodiceAbb(String codiceAbb) throws BusinessException {
		Session ses = SessionFactory.getSession();
		List<IstanzeAbbonamenti> result = null;
		try {
			result = new IstanzeAbbonamentiDao().findIstanzeByCodice(ses, codiceAbb, 0, Integer.MAX_VALUE);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	private PublicationJso convertPublication(List<IstanzeAbbonamenti> iaList)
			throws BusinessException {
		Session ses = SessionFactory.getSession();
		PublicationJso result = null;
		try {
			result = convertPublication(ses, iaList);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	private PublicationJso convertPublication(Session ses, List<IstanzeAbbonamenti> iaList) {
		if (iaList != null) {
			if (iaList.size() > 0) {
				Date beginDate = AbbonamentiDateBusiness.getBeginPubblicazioneOfFirstInstance(iaList);
				//La prima versione rispondeva con la data pubblicazione dell'ultimo gracing:
				//Date endDate = AbbonamentiDateBusiness.getEndGracingOfLastInstance(ses, iaList);
				Date endDate = AbbonamentiDateBusiness.getEndOfLastInstance(ses, iaList);
				
				PublicationJso jso = new PublicationJso();
				String publicationId = WsConstants.PAPERLIT_PUBLICATION_IDS.get(
						iaList.get(0).getAbbonamento().getPeriodico().getId());
				if (publicationId != null) {
					jso.setPublicationId(publicationId);
					jso.setIssubscribed(Boolean.TRUE);
					//Calendar cal = new GregorianCalendar();
					//cal.setTime(beginDate);
					//cal.add(Calendar.DAY_OF_MONTH, (-1)*WsConstants.SUBSCRIPTION_RANGE_EXTENSION_DAYS);
					//jso.setStartedon(cal.getTime());//pubblicazione primo numero - 7 giorni
					jso.setStartedon(beginDate);
					//cal.setTime(endDate);
					//cal.add(Calendar.DAY_OF_MONTH, WsConstants.SUBSCRIPTION_RANGE_EXTENSION_DAYS);
					//jso.setExpireson(cal.getTime());//pubblicazione ultimo numero + 7 giorni
					jso.setExpireson(endDate);
				} else {
					jso = errorToPublicationJso(ERROR_PUBLICATION);
				}
				return jso;
			}
		}
		PublicationJso jso = errorToPublicationJso(ERROR_AUTH);
		return jso;
	}

	private boolean authenticate(List<IstanzeAbbonamenti> iaList, String password,
			String bundleId) {
		boolean auth = false;
		if (iaList != null) {
			if (iaList.size() > 0) {
				//Verifica il bundleId
				Integer periodicoIdFromBundleId = WsConstants.PAPERLIT_BUNDLE_IDS.get(bundleId);
				Integer periodicoId = iaList.get(0).getAbbonamento().getPeriodico().getId();
				if (periodicoIdFromBundleId != null) {
					if (periodicoIdFromBundleId.equals(periodicoId)) {
						//Controllo user/password (provincia)
						Anagrafiche anag = iaList.get(0).getAbbonato();
						String provincia = anag.getIndirizzoPrincipale().getProvincia();
						if (provincia == null) provincia = "";
						if (provincia.length() > 0) {
							auth = provincia.equalsIgnoreCase(password);
						} else {
							if (!anag.getIndirizzoPrincipale().getNazione().getId()
									.equalsIgnoreCase(AppConstants.DEFAULT_ID_NAZIONE_ITALIA)) {
								//Se è estero
								auth = WsConstants.PROVINCIA_ESTERO_AUTH.equalsIgnoreCase(password);
							}
						}
					}
				}
			}
		}
		return auth;
	}
	
	private PublicationJso errorToPublicationJso(String message) {
		PublicationJso publicationJso = new PublicationJso();
		publicationJso.setIssubscribed(Boolean.FALSE);
		publicationJso.setMessage(message);
		return publicationJso;
	}
}
