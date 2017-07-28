package it.giunti.apg.automation;


public class AutomationConstants {
	
	public static String QUARTZ_CONFIG_FILE = "/quartz-jobs.xml";
	
	public static final String ICON_JOB_DEFAULT = "img/appointment-new.png";
	
	//Presentazione
	public static final String LABEL_NON_DISPONIBILE = "N.D.";
	public static final String ICON_AMBULANCE = "<i class='fa fa-ambulance'></i>";
	public static final String ICON_MAGNIFIER = "<i class='fa fa-search'></i>";
	
	//JasperReports
	public static final String REPORT_RESOURCES_PATH = "/report";
	public static final String SEPARATORE_CM_ETICHETTA = "  ";
	public static final String IMG_PERIODICO_LOGO_VARIA = "/logo/logo_giunti.jpg";
	public static final String IMG_PERIODICO_LOGO_SCOLASTICO = "/logo/logo_giuntiscuola.jpg";
	public static final String IMG_STAMP_PERIODICO = "/img/stamp_periodico.jpg";
	public static final String IMG_STAMP_ECONOMY = "/img/stamp_economy.jpg";
	public static final String IMG_STAMP_DONO = "/img/stamp_dono.jpg";
	public static final String IMG_DESTINATARIO_UNICO = "/img/destinatario_unico.jpg";
	public static final String IMG_EURO = "/img/euro_black.jpg";
	public static final String IMG_ANNULLATO = "/img/annullato.gif";
	public static final String IMG_ICON_GIUNTI = "/logo/icon.jpg";
	public static final String REPORT_TEMPLATE_FATTURE = AutomationConstants.REPORT_RESOURCES_PATH+"/fatturaReport.jasper";
	
	//EMAIL accompagnamento fatture
	public static String EMAIL_FATTURE_SUBJECT = "Giunti Editore: invio fattura del pagamento a saldo quota di abbonamento";
	public static String EMAIL_FATTURE_HTML_BODY = "Gentile Abbonato,<br />"+
			"in allegato a questa e-mail troverai la fattura relativa al pagamento che hai "+
			"effettuato a saldo dell'abbonamento.<br />"+
			"&nbsp;<br />"+
			"Verifica l'esattezza dei tuoi dati fiscali e, nel caso siano errati o assenti, "+
			"ti invitiamo a non rispondere a questa e-mail ma a contattarci ai seguenti "+
			"recapiti:<br />"+
			"&nbsp;<br />"+
			"<b>Giunti Servizio Abbonati</b><br />"+
			"e-mail <a href='mailto:periodici@giunti.it'>periodici@giunti.it</a><br />"+
			"tel. +39 055 5062424<br />"+ 
			"(in orario continuato 9:00 - 18:00 da luned&igrave; al venerd&igrave;)<br />"+
			"&nbsp;<br />"+
			"Grazie per aver scelto una delle nostre riviste, buona lettura.<br />"+
			"Cordiali saluti.<br />"+
			"&nbsp;<br />"+
			"Antonella Rapaccini<br />"+
			"Responsabile Servizio Abbonati<br />"+
			"___________________________________<br />"+
			"Giunti Editore Spa<br />"+
			"via Bolognese, 165<br />"+
			"50139 Firenze<br />"+
			"<a href='http://www.giuntiabbonamenti.it'>www.giuntiabbonamenti.it</a><br />"+
			"&nbsp;<br />"+
			"&nbsp;<br />"+
			"<i>Questo messaggio &egrave; stato generato automaticamente e non abbiamo la "+
			"possibilit&agrave; di leggere eventuali e-mail di risposta.</i>";
	

	////EMAIL richiesta feedback
	//public static final String EMAIL_FEEDBACK_SUBJECT = "Giunti Editore: aggiornamento della tua anagrafica Cliente";
	//public static final String EMAIL_FEEDBACK_HTML_BODY_1 =
	//		"Gentile Cliente<br />"+
	//		"secondo quanto previsto dalla Legge n. 228 (del 24-12-2012) "+
	//		"i documenti fiscali relativi agli acquisti di prodotti soggetti a IVA (come "+
	//		"CD/DVD/Ebook/ Edizioni digitali delle riviste ecc) devono riportare i dati "+
	//		"fiscali del cliente.<br />"+
	//		"Per facilitare la gestione del tuo abbonamento ti chiediamo quindi di "+
	//		"completare la tua anagrafica Cliente con il codice fiscale (e se la possiedi "+
	//		"la Partita IVA).<br /> "+
	//		"&nbsp;<br />"+
	//		"Questo &egrave; l'indirizzo univoco per aggiornare i Tuoi dati:<br />";
	//public static final String FEEDBACK_EMAIL_URL =
	//		"https://serviziapg.giunti.it/#feedback"+
	//		UriManager.SEPARATOR_TOKEN+"id=";
	//public static final String EMAIL_FEEDBACK_HTML_BODY_2 ="<br />"+
	//		"&nbsp;<br />"+
	//		"Con l'occasione ti ricordiamo che l'abbonamento all'edizione cartacea di "+
	//		"Archeologia Viva, Art e Dossier, Psicologia contemporanea e Psicologia e "+
	//		"Scuola, e l'abbonamento con formula &quot;Pi&ugrave;&quot; a La Vita "+
	//		"Scolastica e Scuola dell'infanzia danno diritto alla versione digitale della "+
	//		"rivista per iPad e Android.<br />"+
	//		"<b>Per accedere alla versione digitale della rivista</b>, dopo aver scaricato l'app "+
	//		"gratuita da Apple Store o Play Store, clicca sul pulsante in alto a sinistra "+
	//		"&quot;Login Abbonati&quot; e inserisci il Codice Abbonato e la sigla della "+
	//		"tua provincia. Il Codice Abbonato &egrave; sempre presente "+
	//		"sull'etichetta che accompagna la rivista cartacea.<br />"+
	//		"&nbsp;<br />"+
	//		"Cordiali saluti<br />"+
	//		"<i>Giunti - Servizio Clienti</i><br />"+
	//		"&nbsp;<br />"+
	//		"<i>Questo messaggio &egrave; stato generato automaticamente e non abbiamo la "+
	//		"possibilit&agrave; di leggere eventuali e-mail di risposta.</i> Per contattarci "+
	//		"scrivi a <a href='mailto:periodici@giunti.it'>periodici@giunti.it</a> oppure chiama il n. 055 5062424 "+
	//		"(orario 9-18, dal luned&igrave; al venerd&igrave;).";
	
}
