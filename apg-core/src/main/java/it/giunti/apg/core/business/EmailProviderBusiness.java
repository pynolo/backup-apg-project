package it.giunti.apg.core.business;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.persistence.ConfigDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.EvasioniComunicazioni;
import it.giunti.apg.soap.magnews.AuthenticationFailedException_Exception;
import it.giunti.apg.soap.magnews.BatchEmailMessage;
import it.giunti.apg.soap.magnews.Credentials;
import it.giunti.apg.soap.magnews.EmailMessage;
import it.giunti.apg.soap.magnews.InvalidMessageException_Exception;
import it.giunti.apg.soap.magnews.MagNewsAPI;
import it.giunti.apg.soap.magnews.MagNewsAPIService;
import it.giunti.apg.soap.magnews.Option;
import it.giunti.apg.soap.magnews.ServiceNotAvailableException_Exception;
import it.giunti.apg.soap.magnews.TypedValue;

public class EmailProviderBusiness {
	private static final Logger LOG = LoggerFactory.getLogger(EmailProviderBusiness.class);

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
	
	public static List<BatchEmailMessage> createBatchEmailMessageList(List<EvasioniComunicazioni> ecList,
			String fromEmail, String fromName, String idMessageType) {
		List<BatchEmailMessage> bemList = new ArrayList<BatchEmailMessage>();
		for (EvasioniComunicazioni ec:ecList) {
			//Anagrafica
			Anagrafiche anag = ec.getIstanzaAbbonamento().getAbbonato();
			if (ec.getComunicazione().getIdTipoDestinatario().equals(AppConstants.DEST_PAGANTE))
				 anag = ec.getIstanzaAbbonamento().getPagante();
			if (ec.getComunicazione().getIdTipoDestinatario().equals(AppConstants.DEST_PROMOTORE))
				 anag = ec.getIstanzaAbbonamento().getPromotore();
			
			if (anag.getEmailPrimaria() != null) {
				if (anag.getEmailPrimaria().length() > 4) {
					//Containing bean
					BatchEmailMessage bem = new BatchEmailMessage();
					
					//ValoriVariabili
					List<TypedValue> tvList = new ArrayList<TypedValue>();
					//TODO
					
					//Email content
					EmailMessage em = new EmailMessage();
					em.setFromemail(fromEmail);// email address to put inside 'From:' header
					em.setFromname(fromName);// from name, to be put inside the 'From:' header
					em.setTo(anag.getEmailPrimaria());// email address to send the email to (optional if usecontactemail option is given)
					em.setReplyto(fromEmail);// email address to use for the 'Reply-To:' header
					em.setSubject(ec.getComunicazione().getOggettoMessaggio());// subject of the email
					em.setChartset("utf-8"); // preferred charset for the email (if you leave it empty or unset the default of your account will be used). Common values are 'utf-8' or 'windows-1252'
					//htmlbody (optional) 	String 	if htmlbody is given> text/html content of the email, it can contain [contact:xxx] placeholders, image references ( [image:mylogo.gif from=global], [image:mylogo.gif from=website]) or service links ([link:unsubscribe], [link:subscribe])
					//textbody (optional) 	String 	if textbody is given> text/plain content of the email, it can contain [contact:xxx] placeholders, or service links ([link:unsubscribe], [link:subscribe])
					//header 	list of FieldValue 	additional headers for the email
					//attachment 	list of Attachment 	additional attachments for the email
					em.setIdmessagetype(idMessageType); // used to group messages in order to create reports. Insert the numeric ID of message type. Use method getAllSimpleMessageTypes to get all simpleMessages types
					em.getTempvar().addAll(tvList); // list of TypedValue used to pass temp vars in newsletter templates
					//externalId (optional) 	String 	used to assign an external ID to a notification message
					em.getInputparam().addAll(tvList);// list of TypedValue used to pass input parameters in newsletter templates. To use inputparam you have to declare them first and you must write them lowercase.
					bem.setMessage(em);
					
					//Options
					//Int idcontact: looks up for contact's profile data using the internal id of the contact  
					//Int iddatabase: if the contact is not recognized this values is used to render the email as it was sent to a contact of a database  
					//String contactprimarykey: looks up for contact's profile data searching for a contact with the given primary key, in the given database (see iddatabase option)  
					//Boolean usecontactemail: uses the email of the given contact as the recipient address of the message (overriding message.to). Default = false  
					Option cssinline = new Option();// apply css inline to the html content of the message. Default = false  
					cssinline.setKey("cssinline");
					cssinline.setValue("true");
					bem.getOptions().add(cssinline);
					//Boolean embbeddedimages: download and embed all the images founded in the html content of the message (attaching them at the message). Default = false  
					//Int idwebsite: specifies the website to use when rendering absolute URLs (for example image urls or [link:ununsubscribe] urls..). Default = id of the default website  
					Option usenewsletterastemplate = new Option();// indicates to use a newsletter template (or notification message template, which is a special type of newsletter) as the message body. Default = false  
					cssinline.setKey("usenewsletterastemplate");
					cssinline.setValue("true");
					bem.getOptions().add(usenewsletterastemplate);
					//Boolean renderatsend: indicates to render the message during the delivery and not while serving the API call. This option must be used in conjunction with usenewsletterastemplate. This option will result in faster API calls and in less resources usage. This is the preferred way of sending messages using usenewsletterastemplate option. Default = false. With 'renderAtSend' option, message body will never be persisted on db, even if 'messageRetention' level is set to 'full'  
					Option usehtmlcodeastemplate = new Option();// indicates to use html code as the message body. In this case for example you can use mn:if conditions and EmailMessage.tempvars. Default = false  
					cssinline.setKey("usehtmlcodeastemplate");
					cssinline.setValue("true");
					bem.getOptions().add(usehtmlcodeastemplate);
					Option idnewsletter = new Option();// the newsletter whose template is used to draw the message (usenewsletterastemplate must be set to true).  
					cssinline.setKey("idnewsletter");
					cssinline.setValue(ec.getComunicazione().getIdBandella());
					bem.getOptions().add(idnewsletter);
					//String format: the format of the email (default is 'html', admitted values are 'html', 'text' and 'multipart').  
					//Timestamp expectedDeliveryTs: requests to send the message at a given time. If empty it means 'as soon as possible'.   This field must be formatted according to the formatting options of the user who runs the request. The timestap is always the user timestamp.
					//String messageRetention: Specify a retention policy for the given message between "minimal" (discard the message body), "full" (keep the whole message and headers) and "reserved" (discard the recipient).  
					//String idlanguage: language of the notification message if the contact is not subscribed in any database
				  
					bemList.add(bem);
				}
			}
		}
		return bemList;
	}
	
	public static void batchSendEmailMessage(List<BatchEmailMessage> batchEmailList) 
	        	throws BusinessException {
		URL serviceUrl = null;
		try {
			serviceUrl = new URL(ServerConstants.PROVIDER_EMAIL_WSDL);
		} catch (MalformedURLException e) {
			throw new BusinessException(e.getMessage(), e);
		}
		String password = getAccessToken();
		Credentials credentials = new Credentials();
		credentials.setPassword(password);

		MagNewsAPIService service = new MagNewsAPIService(serviceUrl);
		MagNewsAPI port = service.getMagNewsAPIPort();
		
		try {
			port.batchSendEmailMessage(batchEmailList, credentials);
		} catch (AuthenticationFailedException_Exception e) {
			throw new BusinessException(e.getMessage(), e);
		} catch (InvalidMessageException_Exception e) {
			throw new BusinessException(e.getMessage(), e);
		} catch (ServiceNotAvailableException_Exception e) {
			throw new BusinessException(e.getMessage(), e);
		}
	}
}
