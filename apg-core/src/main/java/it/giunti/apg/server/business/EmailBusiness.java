package it.giunti.apg.server.business;

import it.giunti.apg.server.ServerConstants;
import it.giunti.apg.server.persistence.PagamentiCreditiDao;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.EmailConstants;
import it.giunti.apg.shared.model.Fascicoli;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.OpzioniIstanzeAbbonamenti;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.hibernate.Session;

public class EmailBusiness {
	
	public static void postCommonsHtmlMail(String smtp,
			/*String smtpUserName, String smtpPassword,*/
			String from, String fromName,
			String recipients[ ], String subject, String htmlMsg, String textMsg) throws EmailException {
		HtmlEmail email = new HtmlEmail();
		//DefaultAuthenticator auth = new DefaultAuthenticator(smtpUserName, smtpPassword);
		//email.setAuthenticator(auth);
		try {
			email.setFrom(from, fromName);
			for (String recipient:recipients) {
				email.addTo(recipient);
			}
			email.setHostName(smtp);
			email.setSubject(subject);
			// set the html message
			email.setHtmlMsg(htmlMsg);
			// set the alternative message (but htmlMsg is seen as attachment)
			if (textMsg != null) {
				email.setTextMsg(textMsg);
			}
			
			email.send();
		} catch (EmailException e) {
			//Adds recipient info to error string
			String recString = "";
			for (String recipient:recipients) {
				recString += recipient;
			}
			throw new EmailException(
					"Sending to: '"+recString+"'\r\n"+
					e.getMessage(),
					e);
		}
	}
	

	public static String replaceValues(Session ses, String body, IstanzeAbbonamenti ia) {
		if (body == null) return null;
		if (body.length() <= 1) return body;
		String result = body;
		//NOME
		String nome = ia.getAbbonato().getIndirizzoPrincipale().getNome();
		if (nome == null) nome = "";
		result = StringUtils.replace(result, EmailConstants.VAL_NOME, nome);
		//COGNOME
		String cognome = ia.getAbbonato().getIndirizzoPrincipale().getCognomeRagioneSociale();
		if (cognome == null) cognome = "";
		result = StringUtils.replace(result, EmailConstants.VAL_COGNOME_RAGSOC, cognome);
		//NOME PAG
		String nomePag = null;
		if (ia.getPagante() != null) nomePag = ia.getPagante().getIndirizzoPrincipale().getNome();
		if (nomePag == null) nomePag = "";
		result = StringUtils.replace(result, EmailConstants.VAL_NOME_PAG, nomePag);
		//COGNOME PAG
		String cognomePag = null;
		if (ia.getPagante() != null) cognomePag = ia.getPagante().getIndirizzoPrincipale().getCognomeRagioneSociale();
		if (cognomePag == null) cognomePag = "";
		result = StringUtils.replace(result, EmailConstants.VAL_COGNOME_RAGSOC_PAG, cognomePag);
		//TITOLO
		String titolo = ia.getAbbonato().getIndirizzoPrincipale().getTitolo();
		if (titolo == null) titolo = "";
		result = StringUtils.replace(result, EmailConstants.VAL_TITOLO, titolo);
		//TITOLO PAG
		String titoloPag = null;
		if (ia.getPagante() != null) titoloPag = ia.getPagante().getIndirizzoPrincipale().getTitolo();
		if (titoloPag == null) titoloPag = "";
		result = StringUtils.replace(result, EmailConstants.VAL_TITOLO_PAG, titoloPag);
		//EMAIL
		String emails = ia.getAbbonato().getEmailPrimaria();
		if (emails == null) emails = "";
		String email = emails.split(AppConstants.STRING_SEPARATOR)[0];
		result = StringUtils.replace(result, EmailConstants.VAL_COGNOME_RAGSOC, email);
		//VAL_PERIODICO
		String periodico = ia.getAbbonamento().getPeriodico().getNome();
		result = StringUtils.replace(result, EmailConstants.VAL_PERIODICO, periodico);
		//VAL_IMPORTO
		if (result.contains(EmailConstants.VAL_IMPORTO)) {
			Double importoTotale = PagamentiMatchBusiness.getMissingAmount(ses, ia.getId());
			Double importoPagato = new PagamentiCreditiDao()
					.getCreditoByAnagraficaSocieta(ses, ia.getId(),
					ia.getAbbonamento().getPeriodico().getIdSocieta(), null, false);
			String importo = ServerConstants.FORMAT_CURRENCY.format(importoTotale-importoPagato);
			result = StringUtils.replace(result, EmailConstants.VAL_IMPORTO, importo);
		}
		//CODICE_ABBONAMENTO
		String codAbbo = ia.getAbbonamento().getCodiceAbbonamento();
		if (codAbbo == null) codAbbo = "";
		result = StringUtils.replace(result, EmailConstants.VAL_CODICE_ABBONAMENTO, codAbbo);
		//CODICE_ANAGRAFICA
		String codAnag = ia.getAbbonato().getUid();
		if (codAnag != null) codAnag = "";
		result = StringUtils.replace(result, EmailConstants.VAL_CODICE_ANAGRAFICA, codAnag);
		//COPIE
		String copie = ia.getCopie()+"";
		result = StringUtils.replace(result, EmailConstants.VAL_COPIE, copie);
		//SUPPLEMENTI
		if (result.contains(EmailConstants.VAL_SUPPLEMENTI)) {
			String opzioni = "";
			Set<OpzioniIstanzeAbbonamenti> opzSet = ia.getOpzioniIstanzeAbbonamentiSet();
			if (opzSet != null) {
				for (OpzioniIstanzeAbbonamenti opz:opzSet) {
					if (opzioni.length() > 0) opzioni += ", ";
					opzioni += opz.getOpzione().getNome();
				}
			}
			result = StringUtils.replace(result, EmailConstants.VAL_SUPPLEMENTI, opzioni);
		}
		//PROVINCIA
		String provincia = ia.getAbbonato().getIndirizzoPrincipale().getProvincia();
		if (provincia == null) provincia = "";
		result = StringUtils.replace(result, EmailConstants.VAL_PROVINCIA, provincia);
		//SUFFISSO DI GENERE
		String suffSex = "o";
		if (ia.getAbbonato().getSesso() != null) {
			if (ia.getAbbonato().getSesso().equals(AppConstants.SESSO_F)) suffSex = "a";
		}
		result = StringUtils.replace(result, EmailConstants.VAL_SUFFISSO_SESSO, suffSex);
		//SUFFISSO DI GENERE
		String suffSexPag = "o";
		if (ia.getPagante() != null) {
			if (ia.getPagante().getSesso() != null) {
				if (ia.getPagante().getSesso().equals(AppConstants.SESSO_F)) suffSexPag = "a";
			}
		}
		result = StringUtils.replace(result, EmailConstants.VAL_SUFFISSO_SESSO_PAG, suffSexPag);
		//FAS INIZIO
		if (result.contains(EmailConstants.VAL_FAS_INIZIO)) {
			Fascicoli fas = ia.getFascicoloInizio();
			String fasInizio = fas.getTitoloNumero() +
					" ("+fas.getDataCop()+" "+
					ServerConstants.FORMAT_YEAR.format(fas.getDataInizio())+")";
			result = StringUtils.replace(result, EmailConstants.VAL_FAS_INIZIO, fasInizio);
		}
		//FAS FINE
		if (result.contains(EmailConstants.VAL_FAS_FINE)) {
			Fascicoli fas = ia.getFascicoloFine();
			String fasFine = fas.getTitoloNumero() +
					" ("+fas.getDataCop()+" "+
					ServerConstants.FORMAT_YEAR.format(fas.getDataInizio())+")";
			result = StringUtils.replace(result, EmailConstants.VAL_FAS_FINE, fasFine);
		}
		return result;
	}
}
