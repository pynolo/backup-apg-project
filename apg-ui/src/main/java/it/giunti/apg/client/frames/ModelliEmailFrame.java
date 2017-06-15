package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.ComunicazioniService;
import it.giunti.apg.client.services.ComunicazioniServiceAsync;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.EmailConstants;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.model.ModelliEmail;
import it.giunti.apg.shared.model.Utenti;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ModelliEmailFrame extends FramePanel implements IAuthenticatedWidget {
	
	private final ComunicazioniServiceAsync comService = GWT.create(ComunicazioniService.class);
		
	private Integer idModEmail = null;
	private ModelliEmail item = null;
	private boolean isAdmin = false;
	
	private VerticalPanel dataPanel = null;
	private TextBox descrText = null;
	private TextBox nomeMittenteText = null;
	private TextBox oggettoText = null;
	//private TextArea testoSempliceText = null;
	private TextArea testoHtmlText = null;
	
	// METHODS
	
	public ModelliEmailFrame(UriParameters params) {
		super();
		if (params == null) {
			params = new UriParameters();
		}
		idModEmail = params.getIntValue(AppConstants.PARAM_ID);
		AuthSingleton.get().queueForAuthentication(this);
	}
	
	@Override
	public void onSuccessfulAuthentication(Utenti utente) {
		init(utente);
	}
	
	private void init(Utenti utente) {
		// Editing rights
		int ruolo = utente.getRuolo().getId();
		isAdmin = (ruolo >= AppConstants.RUOLO_ADMIN);
		//isSuper = (ruolo >= AppConstants.RUOLO_SUPER);
		// UI
		if (isAdmin) {
			dataPanel = new VerticalPanel();
			this.add(dataPanel, "Modello email");
			loadModelliEmail();
		}
	}
	

	
	/** This method empties the ContentPanel and redraws the 'item' data
	 * @param item
	 */
	private void drawModelliBollettini() {
		// clean form
		dataPanel.clear();
		FlexTable table = new FlexTable();
		dataPanel.add(table);
		int r=0;
		
		// titolo
		table.setHTML(r, 0, "Descrizione"+ClientConstants.MANDATORY);
		descrText = new TextBox();
		descrText.setValue(item.getDescr());
		descrText.setEnabled(isAdmin);
		descrText.setMaxLength(256);
		descrText.setWidth("22em");
		table.setWidget(r, 1, descrText);
		//table.getFlexCellFormatter().setColSpan(r, 1, 4);
		r++;
		
		//Nome Mittente
		table.setHTML(r, 0, "Nome mittente"+ClientConstants.MANDATORY+" <i>(non email)</i>");
		nomeMittenteText = new TextBox();
		nomeMittenteText.setValue(item.getNomeMittente());
		nomeMittenteText.setEnabled(isAdmin);
		nomeMittenteText.setMaxLength(256);
		nomeMittenteText.setWidth("22em");
		table.setWidget(r, 1, nomeMittenteText);
		r++;
		
		//codice modello
		table.setHTML(r, 0, "Oggetto"+ClientConstants.MANDATORY);
		oggettoText = new TextBox();
		oggettoText.setValue(item.getOggetto());
		oggettoText.setEnabled(isAdmin);
		oggettoText.setMaxLength(256);
		oggettoText.setWidth("22em");
		table.setWidget(r, 1, oggettoText);
		r++;
		
		////Testo semplice
		//dataPanel.add(new HTML("Testo semplice <i>(non html)</i>"));
		//testoSempliceText = new TextArea();
		//testoSempliceText.setValue(item.getTestoSemplice());
		//testoSempliceText.setVisibleLines(1);
		//testoSempliceText.setWidth("60em");
		//testoSempliceText.setHeight("20em");
		//testoSempliceText.setEnabled(isAdmin);
		//dataPanel.add(testoSempliceText);
		
		//Testo html
		dataPanel.add(new HTML("Testo html"));
		testoHtmlText = new TextArea();
		testoHtmlText.setValue(item.getTestoHtml());
		testoHtmlText.setVisibleLines(1);
		testoHtmlText.setWidth("60em");
		testoHtmlText.setHeight("20em");
		testoHtmlText.setEnabled(isAdmin);
		dataPanel.add(testoHtmlText);
		
		dataPanel.add(getButtonPanel());

		dataPanel.add(getLegendaPanel());
	}
	
	private HorizontalPanel getButtonPanel() {
		HorizontalPanel buttonPanel = new HorizontalPanel();
		// Bottone SALVA
		Button submitButton = new Button("Salva", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				try {
					saveData();
				} catch (ValidationException e) {
					UiSingleton.get().addWarning(e.getMessage());
				}
			}
		});
		if (idModEmail.equals(AppConstants.NEW_ITEM_ID)) {
			submitButton.setText("Crea");
		}
		submitButton.setEnabled(isAdmin);
		buttonPanel.add(submitButton);
		return buttonPanel;
	}
	
	private FlowPanel getLegendaPanel() {
		FlowPanel legendaPanel = new FlowPanel();
		legendaPanel.add(new InlineHTML("<br />Legenda valori variabili per il corpo del messaggio:"));
		int r = 0;
		FlexTable legendaTable = new FlexTable();
		legendaTable.setStyleName("grey-panel");
		
		legendaTable.setHTML(r, 0, "<b>"+EmailConstants.VAL_NOME+"</b>");
		legendaTable.setHTML(r, 1, "Nome, <i>se disponibile</i>");
		legendaTable.setHTML(r, 3, "<b>"+EmailConstants.VAL_NOME_PAG+"</b>");
		legendaTable.setHTML(r, 4, "Nome pagante, <i>se disponibile</i>");
		r++;
		legendaTable.setHTML(r, 0, "<b>"+EmailConstants.VAL_COGNOME_RAGSOC+"</b>");
		legendaTable.setHTML(r, 1, "Cognome o rag. soc.");
		legendaTable.setHTML(r, 3, "<b>"+EmailConstants.VAL_COGNOME_RAGSOC_PAG+"</b>");
		legendaTable.setHTML(r, 4, "Cognome o rag. soc. pagante");
		r++;
		legendaTable.setHTML(r, 0, "<b>"+EmailConstants.VAL_TITOLO+"</b>");
		legendaTable.setHTML(r, 1, "Titolo");
		legendaTable.setHTML(r, 3, "<b>"+EmailConstants.VAL_TITOLO_PAG+"</b>");
		legendaTable.setHTML(r, 4, "Titolo pagante");
		r++;
		legendaTable.setHTML(r, 0, "<b>"+EmailConstants.VAL_PERIODICO+"</b>");
		legendaTable.setHTML(r, 1, "Nome periodico");
		//legendaTable.setHTML(r, 3, "<b>"+EmailConstants.VAL_IMPORTO+"</b>");
		//legendaTable.setHTML(r, 4, "Importo da pagare, <i>se disponibile</i>");
		r++;
		legendaTable.setHTML(r, 0, "<b>"+EmailConstants.VAL_CODICE_ABBONAMENTO+"</b>");
		legendaTable.setHTML(r, 1, "Codice abbonamento");
		legendaTable.setHTML(r, 3, "<b>"+EmailConstants.VAL_CODICE_ANAGRAFICA+"</b>");
		legendaTable.setHTML(r, 4, "Codice anagrafica");
		r++;
		legendaTable.setHTML(r, 0, "<b>"+EmailConstants.VAL_COPIE+"</b>");
		legendaTable.setHTML(r, 1, "Copie");
		legendaTable.setHTML(r, 3, "<b>"+EmailConstants.VAL_SUPPLEMENTI+"</b>");
		legendaTable.setHTML(r, 4, "Elenco opzioni");
		r++;
		legendaTable.setHTML(r, 0, "<b>"+EmailConstants.VAL_PROVINCIA+"</b>");
		legendaTable.setHTML(r, 1, "Sigla provincia, <i>se disponibile</i>");
		legendaTable.setHTML(r, 3, "<b>"+EmailConstants.VAL_EMAIL+"</b>");
		legendaTable.setHTML(r, 4, "Indirizzi email");
		r++;
		legendaTable.setHTML(r, 0, "<b>"+EmailConstants.VAL_SUFFISSO_SESSO+"</b>");
		legendaTable.setHTML(r, 1, "Suffisso \"o\"/\"a\" per genere");
		legendaTable.setHTML(r, 3, "<b>"+EmailConstants.VAL_SUFFISSO_SESSO_PAG+"</b>");
		legendaTable.setHTML(r, 4, "Suffisso \"o\"/\"a\" per pagante");
		r++;
		legendaTable.setHTML(r, 0, "<b>"+EmailConstants.VAL_FAS_INIZIO+"</b>");
		legendaTable.setHTML(r, 1, "Fascicolo iniziale con data");
		legendaTable.setHTML(r, 3, "<b>"+EmailConstants.VAL_FAS_FINE+"</b>");
		legendaTable.setHTML(r, 4, "Fascicolo finale con data");
		r++;
		legendaPanel.add(legendaTable);
		return legendaPanel;
	}

	
	
	
	// METODI ASINCRONI
	

	private void loadModelliEmail() {
		AsyncCallback<ModelliEmail> callback = new AsyncCallback<ModelliEmail>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(ModelliEmail result) {
				item = result;
				drawModelliBollettini();
				WaitSingleton.get().stop();
			}
		};
		
		//look for item with id only if id is defined
		if (idModEmail.intValue() != AppConstants.NEW_ITEM_ID) {
			WaitSingleton.get().start();
			comService.findModelliEmailById(idModEmail, callback);
		} else {
			//is new modello
			WaitSingleton.get().start();
			comService.createModelliEmail(callback);
		}
	}
	
	private void saveData() throws ValidationException {
		AsyncCallback<Integer> callback = new AsyncCallback<Integer>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(Integer result) {			
				idModEmail = result;
				UiSingleton.get().addInfo(AppConstants.MSG_SAVE_OK);
				WaitSingleton.get().stop();
				loadModelliEmail();
			}
		};
		//Validazione
		String descr = descrText.getValue();
		if (descr == null) throw new ValidationException("Descrizione mancante");
		if (descr.length()==0) throw new ValidationException("Descrizione mancante");
		String autBol = nomeMittenteText.getValue();
		if (autBol == null) throw new ValidationException("Nome mittente mancante");
		if (autBol.length()==0) throw new ValidationException("Nome mittente mancante");
		String codiceModello = oggettoText.getValue();
		if (codiceModello == null) throw new ValidationException("Oggetto mancante");
		if (codiceModello.length()==0) throw new ValidationException("Oggetto mancante");
		//Assegnamento
		item.setDescr(descr);
		item.setNomeMittente(nomeMittenteText.getValue());
		item.setOggetto(oggettoText.getValue());
		//String testoSemplice = testoSempliceText.getValue();
		//if (testoSemplice.length() < 2) testoSemplice = null;
		//item.setTestoSemplice(testoSemplice);
		String testoHtml = testoHtmlText.getValue();
		if (testoHtml.length() < 2) testoHtml = null;
		item.setTestoHtml(testoHtml);

		WaitSingleton.get().start();
		comService.saveOrUpdateModelliEmail(item, callback);
	}
	
}
