package it.giunti.apg.client.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.UriManager;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Utenti;

public class LeftMenuPanel extends VerticalPanel implements IAuthenticatedWidget {
	
	private Tree tree = null;
	private boolean isOperator = false;
	private boolean isEditor = false;
	private boolean isAdmin = false;
	private boolean isSuper = false;
	
	public LeftMenuPanel() {
		tree = new Tree();
		AuthSingleton.get().queueForAuthentication(this);
	}

	@Override
	public void onSuccessfulAuthentication(Utenti utente) {
		int ruolo = utente.getRuolo().getId();
		isOperator = (ruolo >= AppConstants.RUOLO_OPERATOR);
		isEditor = (ruolo >= AppConstants.RUOLO_EDITOR);
		isAdmin = (ruolo >= AppConstants.RUOLO_ADMIN);
		isSuper = (ruolo >= AppConstants.RUOLO_SUPER);
		draw(utente);
	}
	
	public void draw(Utenti utente) {
		this.clear();
		tree.clear();
		//Utente
		String userLabel = utente.getId();
		if (utente.getDescrizione() != null) {
			if (utente.getDescrizione().length() > 0) {
				userLabel = utente.getDescrizione();
			}
		}
		if (isOperator && !isEditor) userLabel = ClientConstants.ICON_USER_OPERATOR + "&nbsp;" + userLabel;
		if (isEditor && !isAdmin) userLabel = ClientConstants.ICON_USER_EDITOR + "&nbsp;" + userLabel;
		if (isAdmin && !isSuper) userLabel = ClientConstants.ICON_USER_ADMIN + "&nbsp;" + userLabel;
		if (isSuper) userLabel = ClientConstants.ICON_USER_SUPER + "&nbsp;" + userLabel;
		tree.addItem(new HTML(userLabel));
		HorizontalPanel userCommandPanel = new HorizontalPanel();
		//Logout
		Anchor logoutLink = new Anchor("Logout");
		final LeftMenuPanel fThisPanel = this;
		logoutLink.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				AuthSingleton.get().logout(fThisPanel);
			}
		});
		userCommandPanel.add(logoutLink);
		//Cambio password
		if (!utente.getAziendale()) {
			userCommandPanel.add(new InlineHTML("&nbsp;|&nbsp;"));
			Anchor passwordLink = new Anchor(ClientConstants.ICON_PASSWORD, true);
			passwordLink.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					AuthSingleton.get().passwordChange("", utente);
				}
			});
			userCommandPanel.add(passwordLink);
		}
		tree.addItem(userCommandPanel);
		
		//Servizio clienti
		if (isOperator) {
			String userIcon = ClientConstants.ICON_USERS;
			String cardIcon = ClientConstants.ICON_USER_NEW;
			String timeIcon = ClientConstants.ICON_CLOCK;
			HTML clientLabel = new HTML("Servizio clienti");
			TreeItem clientRoot = new TreeItem(clientLabel);
			clientLabel.addClickHandler(new TreeClickHandler(clientRoot));
			Hyperlink findAnagLink = new Hyperlink(userIcon+" Cerca anagrafica", true, UriManager.ANAGRAFICHE_FIND);
			UriParameters params = new UriParameters();
			params.add(AppConstants.PARAM_ID, AppConstants.NEW_ITEM_ID);
			Hyperlink nuovaAnagLink = params.getHyperlink(
					userIcon+" Nuova anagrafica", UriManager.ANAGRAFICA);
			Hyperlink feedbackAnagLink = params.getHyperlink(
					userIcon+" Verifica anagrafiche", UriManager.ANAGRAFICHE_MERGE_FIND);
			Hyperlink quickDataEntryLink = new Hyperlink(cardIcon+" Inserimento veloce", true, UriManager.QUICK_DATA_ENTRY);
			Hyperlink findModAnagLink = new Hyperlink(timeIcon+" Anagrafiche modificate", true, UriManager.ANAGRAFICHE_MODIFIED_FIND);
			Hyperlink findModAbbLink = new Hyperlink(timeIcon+" Abbonamenti modificati", true, UriManager.ABBONAMENTI_MODIFIED_FIND);
			Hyperlink destComLink = new Hyperlink(ClientConstants.ICON_EMAIL+" Email in coda", true, UriManager.OUTPUT_COMUNICAZIONI);
			Hyperlink findAvvisiListLink = new Hyperlink(ClientConstants.ICON_ANNOUNCE+" Ultimi avvisi", true, UriManager.AVVISI_LIST);
			clientRoot.addItem(findAnagLink);
			clientRoot.addItem(nuovaAnagLink);
			clientRoot.addItem(feedbackAnagLink);
			clientRoot.addItem(quickDataEntryLink);
			clientRoot.addItem(findModAnagLink);
			clientRoot.addItem(findModAbbLink);
			clientRoot.addItem(destComLink);
			clientRoot.addItem(findAvvisiListLink);
			clientRoot.setState(true);
			tree.addItem(clientRoot);
		}
		
		//Pagamenti
		if (isEditor) {
			String icon = ClientConstants.ICON_EURO;
			HTML paymLabel = new HTML("Pagamenti");
			TreeItem paymRoot = new TreeItem(paymLabel);
			paymLabel.addClickHandler(new TreeClickHandler(paymRoot));
			Hyperlink pagImportLink = new Hyperlink(icon+" Importazione", true, UriManager.INPUT_PAGAMENTI);
			Hyperlink correzionePagamentiLink = new Hyperlink(icon+" Elenco errori", true, UriManager.PAGAMENTI_CORREZIONE);
			Hyperlink correzioneCreditiLink = new Hyperlink(icon+" Elenco crediti", true, UriManager.PAGAMENTI_CREDITI_FIND);
			Hyperlink rapportiLink = new Hyperlink(ClientConstants.ICON_LOG+" Rapporti", true, UriManager.RAPPORTI_FIND);
			paymRoot.addItem(pagImportLink);
			paymRoot.addItem(correzionePagamentiLink);
			paymRoot.addItem(correzioneCreditiLink);
			paymRoot.addItem(rapportiLink);
			paymRoot.setState(true);
			tree.addItem(paymRoot);
		}
		
		//Estrazioni
		if (isAdmin) {
			String icon = ClientConstants.ICON_DATABASE;
			HTML extractLabel = new HTML("Estrazioni");
			TreeItem extractRoot = new TreeItem(extractLabel);
			extractLabel.addClickHandler(new TreeClickHandler(extractRoot));
			Hyperlink fascicoliToSendLink = new Hyperlink(icon+" Fascicoli", true, UriManager.OUTPUT_FASCICOLI);
			Hyperlink articoliListiniToSendLink = new Hyperlink(icon+" Articoli per tipo abb.", true, UriManager.OUTPUT_ARTICOLI_LISTINI);
			Hyperlink articoliOpzioniToSendLink = new Hyperlink(icon+" Articoli per opzione", true, UriManager.OUTPUT_ARTICOLI_OPZIONI);
			Hyperlink pdfInvioLink = new Hyperlink(icon+" Operazioni su etichette", true, UriManager.DELIVERY_FILE_MANAGEMENT);
			Hyperlink queryIstanzeLink = new Hyperlink(ClientConstants.ICON_QUERY+" Query istanze", true, UriManager.QUERY_ISTANZE);
			Hyperlink rapportiLink = new Hyperlink(ClientConstants.ICON_LOG+" Rapporti", true, UriManager.RAPPORTI_FIND);
			if (isAdmin) extractRoot.addItem(fascicoliToSendLink);
			if (isAdmin) extractRoot.addItem(articoliListiniToSendLink);
			if (isAdmin) extractRoot.addItem(articoliOpzioniToSendLink);
			if (isAdmin) extractRoot.addItem(pdfInvioLink);
			if (isAdmin) extractRoot.addItem(queryIstanzeLink);
			if (isEditor) extractRoot.addItem(rapportiLink);
			extractRoot.setState(true);
			tree.addItem(extractRoot);
		}

		//Impostazioni
		if (isOperator) {
			String iconSettings = ClientConstants.ICON_WRENCH;
			HTML settingsLabel = new HTML("Impostazioni");
			TreeItem settingsRoot = new TreeItem(settingsLabel);
			settingsLabel.addClickHandler(new TreeClickHandler(settingsRoot));
			Hyperlink periodiciLink = new Hyperlink(iconSettings+" Periodici", true, UriManager.PERIODICI_FIND);
			Hyperlink tipiAbbonamentoLink = new Hyperlink(iconSettings+" Tipi abbonamento", true, UriManager.TIPI_ABBONAMENTO_FIND);
			Hyperlink opzioniLink = new Hyperlink(iconSettings+" Opzioni", true, UriManager.OPZIONI_FIND);
			Hyperlink fasLink = new Hyperlink(iconSettings+" Fascicoli", true, UriManager.FASCICOLI_FIND);
			Hyperlink articoliLink = new Hyperlink(iconSettings+" Articoli", true, UriManager.DONI_FIND);
			Hyperlink comLink = new Hyperlink(iconSettings+" Comunicazioni", true, UriManager.COMUNICAZIONI_FIND);
			Hyperlink adeLink = new Hyperlink(iconSettings+" Adesioni", true, UriManager.ADESIONI_FIND);
			
			settingsRoot.addItem(periodiciLink);
			settingsRoot.addItem(tipiAbbonamentoLink);
			settingsRoot.addItem(fasLink);
			settingsRoot.addItem(opzioniLink);
			settingsRoot.addItem(articoliLink);
			if (isEditor) settingsRoot.addItem(adeLink);
			if (isEditor) settingsRoot.addItem(comLink);
			settingsRoot.setState(isAdmin);
			tree.addItem(settingsRoot);
			
			//Amministrazione
			if (isAdmin) {
				String iconAdmin = ClientConstants.ICON_DANGER;
				HTML adminLabel = new HTML("Amministrazione");
				TreeItem adminRoot = new TreeItem(adminLabel);
				adminLabel.addClickHandler(new TreeClickHandler(adminRoot));
				Hyperlink fattureInvioLink = new Hyperlink(iconAdmin+" Invio fatture SAP", true, UriManager.FATTURE_INVIO_FIND);
				Hyperlink ordiniLink = new Hyperlink(iconAdmin+" Ordini SAP", true, UriManager.ORDINI_FIND);
				Hyperlink modBolLink = new Hyperlink(iconAdmin+" Modelli bollettini", true, UriManager.MODELLI_BOLLETTINI_FIND);
				Hyperlink modEmailLink = new Hyperlink(iconAdmin+" Modelli email", true, UriManager.MODELLI_EMAIL_FIND);
				Hyperlink avvisiLink = new Hyperlink(iconAdmin+" Avvisi", true, UriManager.AVVISI_FIND);
				Hyperlink utentiLink = new Hyperlink(iconAdmin+" Utenti", true, UriManager.UTENTI_FIND);
				Hyperlink rmLink = new Hyperlink(iconAdmin+" Rinnovo massivo", true, UriManager.RINNOVI_MASSIVI);
				Hyperlink jobLink = new Hyperlink(iconAdmin+" Job programmati", true, UriManager.JOB_FIND);
				Hyperlink installLink = new Hyperlink(iconAdmin+" Installazione", true, UriManager.INSTALL_FIND);
				
				adminRoot.addItem(fattureInvioLink);
				adminRoot.addItem(ordiniLink);
				adminRoot.addItem(modBolLink);
				adminRoot.addItem(modEmailLink);
				adminRoot.addItem(avvisiLink);
				adminRoot.addItem(utentiLink);
				adminRoot.addItem(rmLink);
				adminRoot.addItem(jobLink);
				adminRoot.addItem(installLink);
				adminRoot.setState(false);
				settingsRoot.addItem(adminRoot);
			}
			
		////Help Desk Giunti
		//if (isEditor) {
		//	Anchor hdgLink = new Anchor(ClientConstants.ICON_LIGHTBULB+" Help Desk Giunti", true,
		//			AppConstants.URL_HELP_DESK_GIUNTI);
		//	hdgLink.setTarget("_blank");
		//	tree.addItem(hdgLink);
		//}
		}
		
		this.add(tree);
		this.add(new FeedbackWidget(utente.getId()));
	}


	
	//Inner classes
	
	
	
	class TreeClickHandler implements ClickHandler {
		TreeItem item = null;
		public TreeClickHandler(TreeItem item) {
			this.item=item;
		}
		@Override
		public void onClick(ClickEvent event) {
			item.setState(!item.getState());
		}
	}
}
