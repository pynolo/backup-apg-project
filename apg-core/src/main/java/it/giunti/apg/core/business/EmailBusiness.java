package it.giunti.apg.core.business;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.hibernate.Session;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.persistence.PagamentiCreditiDao;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.EmailConstants;
import it.giunti.apg.shared.model.Fascicoli;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;

public class EmailBusiness {
	
	public static void postCommonsHtmlMail(String smtp,
			/*String smtpUserName, String smtpPassword,*/
			String from, String fromName,
			String recipients[ ], String subject, String htmlMsg, String textMsg) throws EmailException {
		HtmlEmail email = new HtmlEmail();
		//DefaultAuthenticator auth = new DefaultAuthenticator(smtpUserName, smtpPassword);
		//email.setAuthenticator(auth);
		try {
			email.setCharset(AppConstants.CHARSET_UTF8);
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
		
		Map<String, String> map = createValueMap(ses, ia);
		for (String key:map.keySet()) {
			result = StringUtils.replace(result, "%"+key+"%", map.get(key));
		}
		return result;
	}
	
	public static Map<String, String> createValueMap(Session ses, IstanzeAbbonamenti ia) {
		HashMap<String, String> map = new HashMap<String, String>();
		//NOME
		String nome = ia.getAbbonato().getIndirizzoPrincipale().getNome();
		if (nome == null) nome = "";
		map.put(EmailConstants.VAL_NOME, nome);
		//COGNOME
		String cognome = ia.getAbbonato().getIndirizzoPrincipale().getCognomeRagioneSociale();
		if (cognome == null) cognome = "";
		map.put(EmailConstants.VAL_COGNOME_RAGSOC, cognome);
		//NOME PAG
		String nomePag = ia.getAbbonato().getIndirizzoPrincipale().getNome();
		if (ia.getPagante() != null) nomePag = ia.getPagante().getIndirizzoPrincipale().getNome();
		if (nomePag == null) nomePag = "";
		map.put(EmailConstants.VAL_NOME_PAG, nomePag);
		//COGNOME PAG
		String cognomePag = ia.getAbbonato().getIndirizzoPrincipale().getCognomeRagioneSociale();
		if (ia.getPagante() != null) cognomePag = ia.getPagante().getIndirizzoPrincipale().getCognomeRagioneSociale();
		if (cognomePag == null) cognomePag = "";
		map.put(EmailConstants.VAL_COGNOME_RAGSOC_PAG, cognomePag);
		//TITOLO
		String titolo = ia.getAbbonato().getIndirizzoPrincipale().getTitolo();
		if (titolo == null) titolo = "";
		map.put(EmailConstants.VAL_TITOLO, titolo);
		//TITOLO PAG
		String titoloPag = ia.getAbbonato().getIndirizzoPrincipale().getTitolo();
		if (ia.getPagante() != null) titoloPag = ia.getPagante().getIndirizzoPrincipale().getTitolo();
		if (titoloPag == null) titoloPag = "";
		map.put(EmailConstants.VAL_TITOLO_PAG, titoloPag);
		//VAL_PERIODICO
		String periodico = ia.getAbbonamento().getPeriodico().getNome();
		map.put(EmailConstants.VAL_PERIODICO, periodico);
		//VAL_IMPORTO
		Double importoTotale = PagamentiMatchBusiness.getMissingAmount(ses, ia.getId());
		Double importoPagato = new PagamentiCreditiDao()
				.getCreditoByAnagraficaSocieta(ses, ia.getId(),
				ia.getAbbonamento().getPeriodico().getIdSocieta(), null, false);
		String importo = ServerConstants.FORMAT_CURRENCY.format(importoTotale-importoPagato);
		map.put(EmailConstants.VAL_IMPORTO, importo);
		//CODICE_ABBONAMENTO
		String codAbbo = ia.getAbbonamento().getCodiceAbbonamento();
		if (codAbbo == null) codAbbo = "";
		map.put(EmailConstants.VAL_CODICE_ABBONAMENTO, codAbbo);
		//CODICE_ANAGRAFICA
		String codAnag = ia.getAbbonato().getUid();
		if (codAnag == null) codAnag = "";
		map.put(EmailConstants.VAL_CODICE_ANAGRAFICA, codAnag);
		//CODICE_ANAGRAFICA_PAGANTE
		String codAnagPag = ia.getAbbonato().getUid();
		if (ia.getPagante() != null) codAnagPag = ia.getPagante().getUid();
		if (codAnagPag == null) codAnagPag = "";
		map.put(EmailConstants.VAL_CODICE_ANAGRAFICA_PAG, codAnagPag);
		//COPIE
		String copie = ia.getCopie()+"";
		map.put(EmailConstants.VAL_COPIE, copie);
		//PROVINCIA
		String provincia = ia.getAbbonato().getIndirizzoPrincipale().getProvincia();
		if (provincia == null) provincia = "";
		map.put(EmailConstants.VAL_PROVINCIA, provincia);
		//SUFFISSO DI GENERE
		String suffSex = "o";
		if (ia.getAbbonato().getSesso() != null) {
			if (ia.getAbbonato().getSesso().equals(AppConstants.SESSO_F)) suffSex = "a";
		}
		map.put(EmailConstants.VAL_SUFFISSO_SESSO, suffSex);
		//SUFFISSO DI GENERE PAGANTE
		String suffSexPag = suffSex;
		if (ia.getPagante() != null) {
			if (ia.getPagante().getSesso() != null) {
				if (ia.getPagante().getSesso().equals(AppConstants.SESSO_F)) suffSexPag = "a";
			}
		}
		map.put(EmailConstants.VAL_SUFFISSO_SESSO_PAG, suffSexPag);
		//FAS INIZIO
		Fascicoli fasIni = ia.getFascicoloInizio();
		String fasInizio = fasIni.getTitoloNumero() +
				" ("+fasIni.getDataCop()+" "+
				ServerConstants.FORMAT_YEAR.format(fasIni.getDataInizio())+")";
		map.put(EmailConstants.VAL_FAS_INIZIO, fasInizio);
		//FAS FINE
		Fascicoli fasFin = ia.getFascicoloFine();
		String fasFine = fasFin.getTitoloNumero() +
				" ("+fasFin.getDataCop()+" "+
				ServerConstants.FORMAT_YEAR.format(fasFin.getDataInizio())+")";
		map.put(EmailConstants.VAL_FAS_FINE, fasFine);
		return map;
	}
}
