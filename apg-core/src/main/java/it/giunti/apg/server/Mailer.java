package it.giunti.apg.server;

import java.io.File;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mailer {

	private static Logger LOG = LoggerFactory.getLogger(Mailer.class);
	private static boolean DEBUG = true;


	//public static void postMail(String smtpHost, String from,
	//		String recipients[ ], String subject,
	//		String message) throws /*EmailException,*/ MessagingException{
	//	postMail(smtpHost, from, recipients, subject, message, true);
	//}
	public static void postMail(String smtpHost, String from,
			String recipients[ ], String subject,
			String message, boolean isHtml) throws /*EmailException,*/ MessagingException{
		//postCommonsMail(smtpHost, smtpUserName, smtpPassword, from, recipients, subject, message);
		postJavaMail(smtpHost, null, null, from, recipients,
				subject, message, isHtml, false);
	}
	
	//public static void postMail(String smtpHost, String from,
	//		String recipients[ ], String subject,
	//		String message, File attachment) throws /*EmailException,*/ MessagingException{
	//	postMail(smtpHost, from, recipients, subject, message, true, attachment);
	//}
	public static void postMail(String smtpHost, String from,
			String recipients[ ], String subject,
			String message, boolean isHtml, File attachment) throws /*EmailException,*/ MessagingException{
		//postCommonsMail(smtpHost, smtpUserName, smtpPassword, from, recipients, subject, message);
		postJavaMail(smtpHost, null, null, from, recipients,
				subject, message, isHtml, attachment, false);
	}
	
	private static void postJavaMail( String smtpHost, String smtpUserName,
			String smtpPassword, String from,
			String recipients[ ], String subject, String body, boolean isHtml, boolean authenticate) throws MessagingException {

		//Set the host smtp address
		Properties props = new Properties();
		// create some properties and get the default Session
		props.put("mail.smtp.host", smtpHost);
		Session session = null;
		if(authenticate) {
            props.put("mail.smtp.auth", "true");
            session = Session.getInstance(props, new SMTPAuthenticator(smtpUserName, smtpPassword));
        } else {
			// Remove the need to authenticate. Most developers will not set the auth to false
            props.put("mail.smtp.auth", "false");
        	session = Session.getInstance(props, null);
        }
		session.setDebug(DEBUG);

		// create a message
		MimeMessage msg = new MimeMessage(session);

		// set the from and to address
		InternetAddress addressFrom = new InternetAddress(from);
		msg.setFrom(addressFrom);

		InternetAddress[] addressTo = new InternetAddress[recipients.length]; 
		for (int i = 0; i < recipients.length; i++) {
			addressTo[i] = new InternetAddress(recipients[i]);
		}
		msg.setRecipients(Message.RecipientType.TO, addressTo);

		// Optional : You can also set your custom headers in the Email if you Want
		//msg.addHeader("MyHeaderName", "myHeaderValue");

		// Setting the Subject and Content Type
		msg.setSubject(subject);
		if (isHtml) {
			msg.setContent(body, "text/html");
		} else {
			msg.setContent(body, "text/plain");
		}
		Transport t = session.getTransport("smtp");
		try {
			t.connect();
			t.sendMessage(msg, addressTo);
		} catch (MessagingException ex) {
			printException(ex);
		} finally {
			t.close();
		}
	}
	
	private static void postJavaMail( String smtpHost, String smtpUserName,
			String smtpPassword, String from,
			String recipients[ ], String subject,
			String body, boolean isHtml,
			File attachment, boolean authenticate) throws MessagingException {

		//Set the host smtp address
		Properties props = new Properties();
		// create some properties and get the default Session
		props.put("mail.smtp.host", smtpHost);
		Session session = null;
		if(authenticate) {
            props.put("mail.smtp.auth", "true");
            session = Session.getInstance(props, new SMTPAuthenticator(smtpUserName, smtpPassword));
        } else {
			// Remove the need to authenticate. Most developers will not set the auth to false
            props.put("mail.smtp.auth", "false");
        	session = Session.getInstance(props, null);
        }
		session.setDebug(DEBUG);

		// create a message
		Message msg = new MimeMessage(session);

		// set the from and to address
		InternetAddress addressFrom = new InternetAddress(from);
		msg.setFrom(addressFrom);

		InternetAddress[] addressTo = new InternetAddress[recipients.length]; 
		for (int i = 0; i < recipients.length; i++) {
			addressTo[i] = new InternetAddress(recipients[i]);
		}
		msg.setRecipients(Message.RecipientType.TO, addressTo);

		// Optional : You can also set your custom headers in the Email if you Want
		//msg.addHeader("MyHeaderName", "myHeaderValue");

		// Setting the Subject and Content Type
		msg.setSubject(subject);
		//
		// Set the email message text.
		//
		MimeBodyPart messagePart = new MimeBodyPart();
		if (isHtml) {
			messagePart.setContent(body, "text/html");
		} else {
			messagePart.setContent(body, "text/plain");
		}
		
		// Set the email attachment file
		//
		MimeBodyPart attachmentPart = new MimeBodyPart();
		FileDataSource fileDataSource = new FileDataSource(attachment) {
			@Override
			public String getContentType() {
				return "application/octet-stream";
			}
		};
		attachmentPart.setDataHandler(new DataHandler(fileDataSource));
		attachmentPart.setFileName(attachment.getName());
		 
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messagePart);
		multipart.addBodyPart(attachmentPart);
		 
		msg.setContent(multipart);
		
		Transport t = session.getTransport("smtp");
		try {
			t.connect();
			t.sendMessage(msg, addressTo);
		} catch (MessagingException ex) {
			printException(ex);
		} finally {
			t.close();
		}
	}
	
	
	private static void printException(MessagingException me) {
		// approfondimento errore:
		String mailDesc = "";
		MessagingException nestedException = me;
		do {
			if (nestedException instanceof SendFailedException) {
				SendFailedException sfex = (SendFailedException) nestedException;
				Address[] invalid = sfex.getInvalidAddresses();
				if (invalid != null) {
					for (int i = 0; i < invalid.length; i++) {
						mailDesc += "Invalid address: " + invalid[i] + "\r\n";
					}
				}
				Address[] validUnsent = sfex.getValidUnsentAddresses();
				if (validUnsent != null) {
					for (int i = 0; i < validUnsent.length; i++) {
						mailDesc += "Valid unsent address: " + validUnsent[i] + "\r\n";
					}
				}
				Address[] validSent = sfex.getValidSentAddresses();
				if (validSent != null) {
					for (int i = 0; i < validSent.length; i++) {
						mailDesc += "Valid sent address: " + validSent[i] + "\r\n";
					}
				}
			}

			if (nestedException instanceof MessagingException) {
				Exception ex = nestedException.getNextException();
				if (ex instanceof MessagingException) {
					nestedException = (MessagingException) ex;
				} else {
					nestedException = null;
				}
			} else {
				nestedException = null;
			}
		} while (nestedException != null);
		LOG.error("ERROR:\r\n"+mailDesc);
		//throw ex;
	}
	
	
	
	//INNER CLASSES
	
	
	
    public static class SMTPAuthenticator extends javax.mail.Authenticator {
    	private String smtpUserName = null;
    	private String smtpPassword = null;
    	
    	public SMTPAuthenticator(String smtpUserName, String smtpPassword) {
    		this.smtpUserName = smtpUserName;
    		this.smtpPassword = smtpPassword;
    	}
    	public PasswordAuthentication getPasswordAuthentication() {
	        return new PasswordAuthentication(smtpUserName, smtpPassword);
	    }
	}
}
