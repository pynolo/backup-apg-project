package it.giunti.apg.client.widgets;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.UriManager;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Utenti;

public class TopMenuPanel extends TabLayoutPanel implements IAuthenticatedWidget {
	
	private static final String BULLET = "&bull;";
	private boolean isOperator = false;
	private boolean isEditor = false;
	private boolean isAdmin = false;
	//private boolean isSuper = false;
	
	public TopMenuPanel() {
		super(1.5D,Style.Unit.EM);//altezza: 2em
		this.setHeight("4em");
		AuthSingleton.get().queueForAuthentication(this);
	}

	@Override
	public void onSuccessfulAuthentication(Utenti utente) {
		int ruolo = utente.getRuolo().getId();
		isOperator = (ruolo >= AppConstants.RUOLO_OPERATOR);
		isEditor = (ruolo >= AppConstants.RUOLO_EDITOR);
		isAdmin = (ruolo >= AppConstants.RUOLO_ADMIN);
		//isSuper = (ruolo >= AppConstants.RUOLO_SUPER);
		draw(utente);
	}
	
	public void draw(Utenti utente) {
		this.clear();
		
		//Servizio clienti
		if (isOperator) {
			String clientLabel = ClientConstants.ICON_USERS+" Abbonamenti";
			SimplePanel holder = new SimplePanel();
			HorizontalPanel clientPanel = new HorizontalPanel();
			holder.add(clientPanel);
			Hyperlink findAnagLink = new Hyperlink(BULLET+"Cerca anagrafica", true, UriManager.ANAGRAFICHE_FIND);
			findAnagLink.setStyleName("label-top-menu");
			UriParameters params = new UriParameters();
			params.add(AppConstants.PARAM_ID, AppConstants.NEW_ITEM_ID);
			Hyperlink nuovaAnagLink = params.getHyperlink(
					BULLET+"Nuova anagrafica", UriManager.ANAGRAFICA);
			nuovaAnagLink.setStyleName("label-top-menu");
			Hyperlink feedbackAnagLink = params.getHyperlink(
					BULLET+"Verifica anagrafiche", UriManager.ANAGRAFICHE_MERGE_FIND);
			feedbackAnagLink.setStyleName("label-top-menu");
			Hyperlink quickDataEntryLink = new Hyperlink(BULLET+"Inserimento veloce", true, UriManager.QUICK_DATA_ENTRY);
			quickDataEntryLink.setStyleName("label-top-menu");
			Hyperlink findModAnagLink = new Hyperlink(BULLET+"Anagrafiche modificate", true, UriManager.ANAGRAFICHE_MODIFIED_FIND);
			findModAnagLink.setStyleName("label-top-menu");
			Hyperlink findModAbbLink = new Hyperlink(BULLET+"Abbonamenti modificati", true, UriManager.ABBONAMENTI_MODIFIED_FIND);
			findModAbbLink.setStyleName("label-top-menu");
			Hyperlink findAvvisiListLink = new Hyperlink(BULLET+"Ultimi avvisi", true, UriManager.AVVISI_LIST);
			findAvvisiListLink.setStyleName("label-top-menu");
			clientPanel.add(findAnagLink);
			clientPanel.add(nuovaAnagLink);
			clientPanel.add(feedbackAnagLink);
			clientPanel.add(quickDataEntryLink);
			clientPanel.add(findModAnagLink);
			clientPanel.add(findModAbbLink);
			clientPanel.add(findAvvisiListLink);
			this.add(holder, clientLabel, true);
			this.selectTab(holder);//selezionato
			clientPanel.setHeight("100%");
		}
		
		//Pagamenti
		if (isEditor) {
			String paymLabel = ClientConstants.ICON_EURO+" Pagamenti";
			SimplePanel holder = new SimplePanel();
			HorizontalPanel paymPanel = new HorizontalPanel();
			holder.add(paymPanel);
			Hyperlink pagImportLink = new Hyperlink(BULLET+"Importazione", true, UriManager.INPUT_PAGAMENTI);
			pagImportLink.setStyleName("label-top-menu");
			Hyperlink correzionePagamentiLink = new Hyperlink(BULLET+"Elenco errori", true, UriManager.PAGAMENTI_CORREZIONE);
			correzionePagamentiLink.setStyleName("label-top-menu");
			Hyperlink correzioneCreditiLink = new Hyperlink(BULLET+"Elenco crediti", true, UriManager.PAGAMENTI_CREDITI_FIND);
			correzioneCreditiLink.setStyleName("label-top-menu");
			Hyperlink rapportiLink = new Hyperlink(BULLET+"Rapporti", true, UriManager.RAPPORTI_FIND);
			rapportiLink.setStyleName("label-top-menu");
			paymPanel.add(pagImportLink);
			paymPanel.add(correzionePagamentiLink);
			paymPanel.add(correzioneCreditiLink);
			paymPanel.add(rapportiLink);
			this.add(holder, paymLabel, true);
			paymPanel.setHeight("100%");
		}
		
		//Estrazioni
		if (isEditor) {
			String extractLabel = ClientConstants.ICON_DATABASE+" Estrazioni";
			SimplePanel holder = new SimplePanel();
			HorizontalPanel extractPanel = new HorizontalPanel();
			holder.add(extractPanel);
			Hyperlink fascicoliToSendLink = new Hyperlink(BULLET+"Fascicoli", true, UriManager.OUTPUT_FASCICOLI);
			fascicoliToSendLink.setStyleName("label-top-menu");
			Hyperlink articoliListiniToSendLink = new Hyperlink(BULLET+"Articoli per tipo abb.", true, UriManager.OUTPUT_ARTICOLI_LISTINI);
			articoliListiniToSendLink.setStyleName("label-top-menu");
			Hyperlink articoliOpzioniToSendLink = new Hyperlink(BULLET+"Articoli per opzione", true, UriManager.OUTPUT_ARTICOLI_OPZIONI);
			articoliOpzioniToSendLink.setStyleName("label-top-menu");
			Hyperlink destinatariComLink = new Hyperlink(BULLET+"Email in coda", true, UriManager.OUTPUT_COMUNICAZIONI);
			destinatariComLink.setStyleName("label-top-menu");
			Hyperlink pdfInvioLink = new Hyperlink(BULLET+"Operazioni su etichette", true, UriManager.DELIVERY_FILE_MANAGEMENT);
			pdfInvioLink.setStyleName("label-top-menu");
			Hyperlink queryIstanzeLink = new Hyperlink(BULLET+"Query istanze", true, UriManager.QUERY_ISTANZE);
			queryIstanzeLink.setStyleName("label-top-menu");
			Hyperlink rapportiLink = new Hyperlink(BULLET+"Rapporti", true, UriManager.RAPPORTI_FIND);
			rapportiLink.setStyleName("label-top-menu");
			if (isEditor) extractPanel.add(fascicoliToSendLink);
			if (isEditor) extractPanel.add(articoliListiniToSendLink);
			if (isEditor) extractPanel.add(articoliOpzioniToSendLink);
			extractPanel.add(destinatariComLink);
			if (isEditor) extractPanel.add(pdfInvioLink);
			if (isEditor) extractPanel.add(queryIstanzeLink);
			if (isEditor) extractPanel.add(rapportiLink);
			this.add(holder, extractLabel, true);
			extractPanel.setHeight("100%");
		}
		
		//Statistiche
		if (isAdmin) {
			String statLabel = ClientConstants.ICON_PIECHART+" Statistiche";
			SimplePanel holder = new SimplePanel();
			HorizontalPanel statPanel = new HorizontalPanel();
			holder.add(statPanel);
			//Hyperlink periodiciLink = new Hyperlink(BULLET+"Periodici", true, UriManager.STAT_PERIODICI);
			//periodiciLink.setStyleName("label-top-menu");
			//Hyperlink tipiAbbLink = new Hyperlink(BULLET+"Tipi abbonamento", true, UriManager.STAT_TIPI_ABBONAMENTO);
			//tipiAbbLink.setStyleName("label-top-menu");
			Hyperlink statInvioLink = new Hyperlink(BULLET+"Ultimo invio", true, UriManager.STAT_INVIO);
			statInvioLink.setStyleName("label-top-menu");
			Hyperlink statInvioStoricoLink = new Hyperlink(BULLET+"Andamento invii", true, UriManager.STAT_INVIO_STORICO);
			statInvioStoricoLink.setStyleName("label-top-menu");
			//Hyperlink statAbbonatiLink = new Hyperlink(BULLET+"Andamento abbonati", true, UriManager.STAT_ANDAMENTO);
			//statAbbonatiLink.setStyleName("label-top-menu");
			//statPanel.add(periodiciLink);
			//statPanel.add(tipiAbbLink);
			statPanel.add(statInvioLink);
			statPanel.add(statInvioStoricoLink);
			//statPanel.add(statAbbonatiLink);
			this.add(holder, statLabel, true);
			statPanel.setHeight("100%");
		}
		
		//Impostazioni
		if (isOperator) {
			String settingsLabel = ClientConstants.ICON_WRENCH+" Impostazioni";
			SimplePanel holder = new SimplePanel();
			HorizontalPanel settingsPanel = new HorizontalPanel();
			holder.add(settingsPanel);
			Hyperlink periodiciLink = new Hyperlink(BULLET+"Periodici", true, UriManager.PERIODICI_FIND);
			periodiciLink.setStyleName("label-top-menu");
			Hyperlink tipiAbbonamentoLink = new Hyperlink(BULLET+"Tipi abbonamento", true, UriManager.TIPI_ABBONAMENTO_FIND);
			tipiAbbonamentoLink.setStyleName("label-top-menu");
			Hyperlink opzioniLink = new Hyperlink(BULLET+"Opzioni", true, UriManager.OPZIONI_FIND);
			opzioniLink.setStyleName("label-top-menu");
			Hyperlink fasLink = new Hyperlink(BULLET+"Fascicoli", true, UriManager.FASCICOLI_FIND);
			fasLink.setStyleName("label-top-menu");
			Hyperlink articoliLink = new Hyperlink(BULLET+"Articoli", true, UriManager.DONI_FIND);
			articoliLink.setStyleName("label-top-menu");
			Hyperlink comLink = new Hyperlink(BULLET+"Comunicazioni", true, UriManager.COMUNICAZIONI_FIND);
			comLink.setStyleName("label-top-menu");
			Hyperlink adeLink = new Hyperlink(BULLET+"Adesioni", true, UriManager.ADESIONI_FIND);
			adeLink.setStyleName("label-top-menu");
			
			settingsPanel.add(periodiciLink);
			settingsPanel.add(tipiAbbonamentoLink);
			settingsPanel.add(fasLink);
			settingsPanel.add(opzioniLink);
			settingsPanel.add(articoliLink);
			if (isEditor) settingsPanel.add(adeLink);
			if (isEditor) settingsPanel.add(comLink);
			this.add(holder, settingsLabel, true);
			settingsPanel.setHeight("100%");
		}
		
		//Amministrazione
		if (isAdmin) {
			String adminLabel = ClientConstants.ICON_DANGER+" Amministrazione";
			SimplePanel holder = new SimplePanel();
			HorizontalPanel adminPanel = new HorizontalPanel();
			holder.add(adminPanel);

			Hyperlink fattureInvioLink = new Hyperlink(BULLET+"Invio fatture SAP", true, UriManager.FATTURE_INVIO_FIND);
			fattureInvioLink.setStyleName("label-top-menu");
			Hyperlink ordiniLink = new Hyperlink(BULLET+"Ordini SAP", true, UriManager.ORDINI_FIND);
			ordiniLink.setStyleName("label-top-menu");
			Hyperlink modBolLink = new Hyperlink(BULLET+"Modelli bollettini", true, UriManager.MODELLI_BOLLETTINI_FIND);
			modBolLink.setStyleName("label-top-menu");
			Hyperlink modEmailLink = new Hyperlink(BULLET+"Modelli email", true, UriManager.MODELLI_EMAIL_FIND);
			modEmailLink.setStyleName("label-top-menu");
			Hyperlink avvisiLink = new Hyperlink(BULLET+"Avvisi", true, UriManager.AVVISI_FIND);
			avvisiLink.setStyleName("label-top-menu");
			Hyperlink utentiLink = new Hyperlink(BULLET+"Utenti", true, UriManager.UTENTI_FIND);
			utentiLink.setStyleName("label-top-menu");
			Hyperlink jobLink = new Hyperlink(BULLET+"Job programmati", true, UriManager.JOB_FIND);
			jobLink.setStyleName("label-top-menu");
			Hyperlink rmLink = new Hyperlink(BULLET+"Rinnovo massivo", true, UriManager.RINNOVI_MASSIVI);
			rmLink.setStyleName("label-top-menu");
			
			adminPanel.add(ordiniLink);
			adminPanel.add(modBolLink);
			adminPanel.add(modEmailLink);
			adminPanel.add(avvisiLink);
			adminPanel.add(utentiLink);
			adminPanel.add(jobLink);
			adminPanel.add(rmLink);
			this.add(holder, adminLabel, true);
			adminPanel.setHeight("100%");
		}
		
		//Utente
		String userLabel = ClientConstants.ICON_USER+" "+utente.getId();
		if (utente.getDescrizione() != null) {
			if (utente.getDescrizione().length() > 0) {
				userLabel = ClientConstants.ICON_USER+" "+utente.getDescrizione();
			}
		}
		SimplePanel holder = new SimplePanel();
		FlowPanel utentePanel = new FlowPanel();
		holder.add(utentePanel);
		//Logout
		Anchor logoutLink = new Anchor(BULLET+"Logout", true);
		logoutLink.setStyleName("label-top-menu");
		final TopMenuPanel fThisPanel = this;
		logoutLink.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				AuthSingleton.get().logout(fThisPanel);
			}
		});
		utentePanel.add(logoutLink);
		//Cambio password
		if (!utente.getAziendale()) {
			Anchor passwordLink = new Anchor(" &nbsp;"+BULLET+"Password", true);
			passwordLink.setStyleName("label-top-menu");
			passwordLink.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					AuthSingleton.get().passwordChange("", utente);
				}
			});
			utentePanel.add(passwordLink);
		}
		//utentePanel.add(new FeedbackWidget(utente.getId()));
		this.add(holder, userLabel, true);
		utentePanel.setWidth("100%");
	}

}
