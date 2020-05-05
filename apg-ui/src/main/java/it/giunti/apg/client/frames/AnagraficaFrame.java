package it.giunti.apg.client.frames;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.UriManager;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.AnagraficheService;
import it.giunti.apg.client.services.AnagraficheServiceAsync;
import it.giunti.apg.client.widgets.AnagraficheSearchBox;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.client.widgets.LocalitaCapPanel;
import it.giunti.apg.client.widgets.SubPanel;
import it.giunti.apg.client.widgets.VersioningPanel;
import it.giunti.apg.client.widgets.select.NazioniSelect;
import it.giunti.apg.client.widgets.tables.CreditiTable;
import it.giunti.apg.client.widgets.tables.DataModel;
import it.giunti.apg.client.widgets.tables.EvasioniArticoliTable;
import it.giunti.apg.client.widgets.tables.FattureTable;
import it.giunti.apg.client.widgets.tables.IstanzeAbbonamentiTable;
import it.giunti.apg.client.widgets.tables.PagamentiTable;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.EvasioniArticoli;
import it.giunti.apg.shared.model.Fatture;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Pagamenti;
import it.giunti.apg.shared.model.PagamentiCrediti;
import it.giunti.apg.shared.model.Utenti;

public class AnagraficaFrame extends FramePanel implements IAuthenticatedWidget, IRefreshable {
	private final AnagraficheServiceAsync anagraficheService = GWT.create(AnagraficheService.class);
	
	private static final String TITLE_ANAGRAFICA = "Anagrafica";
	private static final String TITLE_ABB = "Abbonamenti";
	private static final String TITLE_ABB_REGALATI = "Abbonamenti pagati a terzi";
	private static final String TITLE_ABB_PROM = "Abbonamenti promossi";
	private static final String TITLE_ARTICOLI = "Articoli";
	private static final String TITLE_CREDITI = "Elenco crediti";
	private static final String TITLE_PAGAMENTI = "Storico pagamenti";
	private static final String TITLE_FATTURE = "Storico fatture da corrispettivo";
	
	private static final String BOX_WIDTH = "20em";
	
	private UriParameters params;
	private Integer idAnagrafica = null;
	private Anagrafiche item = null;
	private Utenti utente = null;
	private boolean isSuper = false;
	private boolean isEditor = false;
	private boolean isAdmin = false;
	private boolean isOperator = false;
	
	private FlowPanel panelAna = null;
	private AnagraficheSuggPanel suggPanel = null;
	private AnagraficaPanel anagPanel = null;
	
	private FlowPanel panelDet = null;
	private TextBox giuntiCardText = null;
	private TextBox sapText = null;
	//private CheckBox cCostoCheck = null;
	private TextBox titoloFattText = null;
	private TextBox ragSocFattText = null;
	private TextBox nomeFattText = null;
	private TextBox indirizzoFattText = null;
	private TextBox pressoFattText = null;
	private LocalitaCapPanel localitaFattCapPanel = null;
	private NazioniSelect nazioniFattList = null;
	
	private SubPanel panelPag = null;
	private SubPanel panelCred = null;
	
	private SubPanel panelAbb = null;
	private IstanzeAbbonamentiTable abbTable = null;
	private SubPanel panelRegalati = null;
	private IstanzeAbbonamentiTable regalatiTable = null;
	private SubPanel panelPromossi = null;
	private IstanzeAbbonamentiTable promossiTable = null;
	private SubPanel panelArticoli = null;
	private EvasioniArticoliTable articoliTable = null;
	private SubPanel panelFatture = null;
	private FattureTable fattureTable = null;
	
	public AnagraficaFrame(UriParameters params) {
		super();
		if (params != null) {
			this.params = params;
		} else {
			this.params = new UriParameters();
		}
		Integer value = this.params.getIntValue(AppConstants.PARAM_ID);
		if (value != null) {
			idAnagrafica = value;
			AuthSingleton.get().queueForAuthentication(this);
		}
	}
	
	@Override
	public void onSuccessfulAuthentication(Utenti utente) {
		init(utente);
	}
	
	private void init(Utenti utente) {
		// Editing rights
		this.utente = utente;
		isOperator = (utente.getRuolo().getId() >= AppConstants.RUOLO_OPERATOR);
		isEditor = (utente.getRuolo().getId() >= AppConstants.RUOLO_EDITOR);
		isAdmin = (utente.getRuolo().getId() >= AppConstants.RUOLO_ADMIN);
		isSuper = (utente.getRuolo().getId() >= AppConstants.RUOLO_SUPER);
		// UI
		if (isOperator) {
			panelAna = new FlowPanel();
			this.add(panelAna, TITLE_ANAGRAFICA);
			this.setWidth("100%");
			loadAnagrafiche();
		}
	}


	@Override
	public void refresh() {
		if (abbTable != null) {
			if (abbTable.isEmpty()) {
				panelAbb.setVisible(false);
			}
		}
		if (regalatiTable != null) {
			if (regalatiTable.isEmpty()) {
				panelRegalati.setVisible(false);
			}
		}
		if (promossiTable != null) {
			if (promossiTable.isEmpty()) {
				panelPromossi.setVisible(false);
			}
		}
	}
	
	/** This method empties the ContentPanel and redraws the 'item' data
	 * @param item
	 */
	private void drawAnagrafiche() {
		String title = item.getIndirizzoPrincipale().getCognomeRagioneSociale();
		if (item.getIndirizzoPrincipale().getNome() != null) title += " " + item.getIndirizzoPrincipale().getNome();
		setBrowserWindowTitle(title);
		// clean anaForm
		panelAna.clear();
		
		//Necessita verifica?
		if (item.getNecessitaVerifica()) {
			HTML html = new HTML(ClientConstants.ICON_HAND_RIGHT+" <b>Questa anagrafica deve essere verificata</b>");
			html.setStyleName("message-info");
			panelAna.add(html);
		}
		
		FlexTable contentTable = new FlexTable();
		if (item.getUid() != null) {
			anagPanel = new AnagraficaPanel(item, null, true, isOperator);
		} else {
			suggPanel = new AnagraficheSuggPanel(this);
			anagPanel = new AnagraficaPanel(item, suggPanel, true, isOperator);
		}
		contentTable.setWidget(0, 0, anagPanel);
		contentTable.setWidget(0, 1, suggPanel);
		panelAna.add(contentTable);
		
		FlexTable table = new FlexTable();
		int r=0;
		
		//PANNELLO DETTAGLIO
		panelDet = new FlowPanel();//new TitlePanel("Dettaglio anagrafica");
		table.setWidget(r, 0, panelDet);
		table.getFlexCellFormatter().setColSpan(r, 0, 8);
		r++;
		
		//PANNELLO BOTTONI
		Panel buttonPanel = getButtonPanel();
		table.setWidget(r,0,buttonPanel);
		table.getFlexCellFormatter().setColSpan(r, 0, 6);//Span su 5 colonne
		r++;

		panelAna.add(table);
		//panelAna.add(new InlineHTML("<br/>"));
		
		if (item.getId() != null) {
			//riga
			panelAna.add(new HTML("<hr />"));
			//PANNELLO CREDITI
			panelCred = new SubPanel(TITLE_CREDITI);
			panelAna.add(panelCred);
			//PANNELLO ABBONAMENTI-STORICO
			panelAbb = new SubPanel(TITLE_ABB);
			panelAna.add(panelAbb);
			//PANNELLO REGALATI-STORICO
			panelRegalati = new SubPanel(TITLE_ABB_REGALATI);
			panelAna.add(panelRegalati);
			//PANNELLO PROMOSSI-STORICO
			panelPromossi = new SubPanel(TITLE_ABB_PROM);
			panelAna.add(panelPromossi);
			//PANNELLO ARTICOLI
			panelArticoli = new SubPanel(TITLE_ARTICOLI);
			panelAna.add(panelArticoli);
			//PANNELLO STORICO FATTURE
			panelFatture = new SubPanel(TITLE_FATTURE);
			panelAna.add(panelFatture);
			//PANNELLO STORICO PAGAMENTI
			panelPag = new SubPanel(TITLE_PAGAMENTI);
			panelAna.add(panelPag);
			//PANNELLO VERSIONAMENTO
			VersioningPanel versionPanel = new VersioningPanel(
					"Anagrafiche", item.getId(), item.getIdUtente(), item.getDataModifica());
			panelAna.add(versionPanel);
		}	
		
//		if (item.getDataModifica() != null) {
//			//Info modifica
//			String userName = item.getUtente().getDescrizione();
//			if (userName == null) userName = item.getUtente().getId();
//			if (userName.equals("")) userName = item.getUtente().getId();
//			InlineHTML modifiedInfo = new InlineHTML("<br/><i>Modificato da "+userName+" il "+
//					ClientConstants.FORMAT_TIMESTAMP.format(item.getDataModifica())+"</i>");
//			panelAna.add(modifiedInfo);
//		}
	}
	
	private void drawAnagraficheDettaglio() {
		panelDet.clear();
		FlexTable table = new FlexTable();
		int r=0;
		
		table.setHTML(r, 0, "<b>Indirizzo di fatturazione</b>");
		table.getFlexCellFormatter().setColSpan(r, 0, 6);
		r++;
		

		//Titolo
		table.setHTML(r, 0, "Titolo");
		titoloFattText = new TextBox();
		titoloFattText.setValue(item.getIndirizzoFatturazione().getTitolo());
		titoloFattText.setMaxLength(6);
		titoloFattText.setWidth("5em");
		titoloFattText.setFocus(true);
		titoloFattText.setEnabled(isOperator);
		table.setWidget(r, 1, titoloFattText);
		r++;
		
		// RagSoc
		table.setHTML(r, 0, "Cognome/Rag.soc.");
		ragSocFattText = new TextBox();
		ragSocFattText.setValue(item.getIndirizzoFatturazione().getCognomeRagioneSociale());
		ragSocFattText.setMaxLength(30);
		ragSocFattText.setWidth(BOX_WIDTH);
		ragSocFattText.setEnabled(isOperator);
		table.setWidget(r, 1, ragSocFattText);
		r++;
		
		// nome
		table.setHTML(r, 0, "Nome");
		nomeFattText = new TextBox();
		nomeFattText.setValue(item.getIndirizzoFatturazione().getNome());
		nomeFattText.setMaxLength(25);
		nomeFattText.setWidth(BOX_WIDTH);
		nomeFattText.setEnabled(isOperator);
		table.setWidget(r, 1, nomeFattText);
		r++;
		
		//PressoFatt
		table.setHTML(r, 0, "Presso");
		pressoFattText = new TextBox();
		pressoFattText.setValue(item.getIndirizzoFatturazione().getPresso());
		pressoFattText.setWidth(BOX_WIDTH);
		pressoFattText.setMaxLength(30);
		pressoFattText.setEnabled(isOperator);
		table.setWidget(r, 1, pressoFattText);
		r++;
		
		//NazioneFatt
		table.setHTML(r, 0, "Nazione");
		nazioniFattList = new NazioniSelect(item.getIndirizzoFatturazione().getNazione().getId());
		nazioniFattList.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				String idNazione = nazioniFattList.getSelectedValueString();
				if (idNazione != null) localitaFattCapPanel.setIdNazione(idNazione);
			}
		});
		nazioniFattList.setEnabled(isOperator);
		table.setWidget(r, 1, nazioniFattList);
		r++;
		
		//IndirizzoFatt
		table.setHTML(r, 0, "Indirizzo");
		indirizzoFattText = new TextBox();
		indirizzoFattText.setValue(item.getIndirizzoFatturazione().getIndirizzo());
		indirizzoFattText.setMaxLength(60);
		indirizzoFattText.setWidth(BOX_WIDTH);
		indirizzoFattText.setEnabled(isOperator);
		table.setWidget(r, 1, indirizzoFattText);
		r++;
		
		//LocalitaFatt
		table.setHTML(r, 0, "Localit&agrave;");
		if (item.getIndirizzoFatturazione() != null) {
			localitaFattCapPanel = new LocalitaCapPanel(
					item.getIndirizzoFatturazione().getLocalita(),
					item.getIndirizzoFatturazione().getProvincia(),
					item.getIndirizzoFatturazione().getCap());
		} else {
			localitaFattCapPanel = new LocalitaCapPanel("", "", "");
		}
		localitaFattCapPanel.setIdNazione(AppConstants.DEFAULT_ID_NAZIONE_ITALIA);
		if (item.getIndirizzoFatturazione() != null) {
			if (item.getIndirizzoFatturazione().getNazione() != null) {
				localitaFattCapPanel.setIdNazione(item.getIndirizzoFatturazione().getNazione().getId());
			}
		}
		localitaFattCapPanel.setEnabled(isOperator);
		table.getFlexCellFormatter().setColSpan(r, 1, 5);
		table.setWidget(r, 1, localitaFattCapPanel);
		//Verifica localita'
		if (localitaFattCapPanel.getLocalitaCap().length() > 0 &&
				localitaFattCapPanel.getLocalitaName().length() > 0 &&
				localitaFattCapPanel.getLocalitaProv().length() > 0 ) {
			localitaFattCapPanel.verifyStoredLocalita();
			if (localitaFattCapPanel.isEmpty()) {
				UiSingleton.get().addWarning("La localita' di fatturazione e' errata o incompleta");
			}
		}
		r++;
		
		table.setHTML(r, 0, "<b>Dati tecnici</b>");
		table.getFlexCellFormatter().setColSpan(r, 0, 6);
		r++;
		
		//Giunti Card Club
		table.setHTML(r, 0, "Giunti Card Club");
		giuntiCardText = new TextBox();
		giuntiCardText.setValue(item.getGiuntiCardClub());
		giuntiCardText.setWidth(BOX_WIDTH);
		giuntiCardText.setEnabled(isOperator);
		giuntiCardText.setMaxLength(16);
		table.setWidget(r, 1, giuntiCardText);
		r++;
		
		//SAP
		table.setHTML(r, 0, "Codice SAP");
		sapText = new TextBox();
		sapText.setValue(item.getCodiceSap());
		sapText.setWidth(BOX_WIDTH);
		sapText.setEnabled(isOperator);
		sapText.setMaxLength(64);
		table.setWidget(r, 1, sapText);
		r++;
		
		if (item.getDataCreazione() != null) {
			table.setHTML(r, 0, "Data creazione");
			table.setHTML(r, 1, ClientConstants.FORMAT_DAY.format(item.getDataCreazione()));
			r++;
		}
		
		panelDet.add(table);
	}
	
	private Panel getButtonPanel() {
		VerticalPanel buttonArea = new VerticalPanel();
		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonArea.add(buttonPanel);
		// Bottone SALVA
		Button submitButton = new Button(ClientConstants.ICON_SAVE+" Salva", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				try {
					saveData();
				} catch (BusinessException e) {
					UiSingleton.get().addError(e);
				}
			}
		});
		if (idAnagrafica.equals(AppConstants.NEW_ITEM_ID)) {
			submitButton.setHTML(ClientConstants.ICON_SAVE+" Crea");
		}
		submitButton.setEnabled(isOperator);
		buttonPanel.add(submitButton);
		//// Bottone ANNULLA
		//Button cancelButton = new Button("Annulla", new ClickHandler() {
		//	@Override
		//	public void onClick(ClickEvent event) {
		//		History.back();
		//	}
		//});
		//buttonPanel.add(cancelButton);
		//Image separator = new Image("img/separator.gif");
		//buttonPanel.add(separator);
		
		// Bottone NUOVO ABBONAMENTO
		buttonPanel.add(new Image("img/separator.gif"));
		Button newAbbButton = new Button(ClientConstants.ICON_ADD+"&nbsp;Crea abbonamento");
		newAbbButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				UriParameters params = new UriParameters();
				params.add(AppConstants.PARAM_ID, AppConstants.NEW_ITEM_ID);
				params.add(AppConstants.PARAM_ID_ANAGRAFICA, item.getId()+"");
				params.triggerUri(UriManager.ABBONAMENTO);
			}
		});
		if (idAnagrafica.equals(AppConstants.NEW_ITEM_ID)) {
			newAbbButton.setVisible(false);
		} else {
			newAbbButton.setVisible(isOperator);
		}
		buttonPanel.add(newAbbButton);
		
		// Bottone elimina
		if (isSuper && !idAnagrafica.equals(AppConstants.NEW_ITEM_ID)) {
			buttonPanel.add(new Image("img/separator.gif"));
			Button deleteAnaButton = new Button(ClientConstants.ICON_DELETE+"&nbsp;Elimina completamente!");
			deleteAnaButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					deleteAnagrafica();
				}
			});
			buttonPanel.add(deleteAnaButton);
		}
		
		// Bottone MERGE
		if (isEditor && !idAnagrafica.equals(AppConstants.NEW_ITEM_ID)) {
			buttonPanel.add(new Image("img/separator.gif"));
			//Bottone unisci
			Button mergeAnaButton = new Button(ClientConstants.ICON_MERGE+"&nbsp;Unisci anagrafiche");
			buttonPanel.add(mergeAnaButton);
			//Merge panel
			final VerticalPanel mergePanel = new VerticalPanel();
			mergePanel.setStyleName("suggestion-panel");
			mergePanel.setVisible(false);
			buttonArea.add(mergePanel);
			mergeAnaButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					mergePanel.setVisible(true);
				}
			});
			mergePanel.add(new InlineHTML("<b>Unisci con una anagrafica esistente:</b>"));
			//Anagrafiche search box
			final AnagraficheSearchBox anaSearch = new AnagraficheSearchBox("", null, isAdmin, true);
			mergePanel.add(anaSearch);
			//Bottone
			Button mergeLink = new Button(ClientConstants.ICON_ARROW+"&nbsp;Procedi con l'unione");
			mergeLink.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					String id = anaSearch.getIdValue();
					if (id == null) id = "";
					if (!id.equals("")) {
						UriParameters params = new UriParameters();
						params.add(AppConstants.PARAM_ID, item.getId()+"");
						params.add(AppConstants.PARAM_ID_ANAGRAFICA, id);
						params.triggerUri(UriManager.ANAGRAFICHE_MERGE);
					}
				}
			});
			mergePanel.add(mergeLink);
		}
		
		return buttonArea;
	}
	
	private void drawAbbonamentiPanel() {
		if (panelAbb != null) {
			panelAbb.clear();
			if (idAnagrafica != null) {
				if (idAnagrafica != AppConstants.NEW_ITEM_ID) {
					DataModel<IstanzeAbbonamenti> model =
							new IstanzeAbbonamentiTable.IstanzeProprieByAnagraficaModel(idAnagrafica, true);
					abbTable = new IstanzeAbbonamentiTable(model, false, this);
					panelAbb.add(abbTable);
				}
			}
		}
	}
	
	private void drawRegalatiPanel() {
		if (panelRegalati != null) {
			panelRegalati.clear();
			if (idAnagrafica != null) {
				if (idAnagrafica != AppConstants.NEW_ITEM_ID) {
					DataModel<IstanzeAbbonamenti> model =
							new IstanzeAbbonamentiTable.IstanzeRegalateByAnagraficaModel(idAnagrafica, true);
					regalatiTable = new IstanzeAbbonamentiTable(model, false, this);
					panelRegalati.add(regalatiTable);
				}
			}
		}
	}
	
	private void drawPromossiPanel() {
		if (panelPromossi != null) {
			panelPromossi.clear();
			if (idAnagrafica != null) {
				if (idAnagrafica != AppConstants.NEW_ITEM_ID) {
					DataModel<IstanzeAbbonamenti> model =
							new IstanzeAbbonamentiTable.IstanzePromosseByAnagraficaModel(idAnagrafica, true);
					promossiTable = new IstanzeAbbonamentiTable(model, false, this);
					panelPromossi.add(promossiTable);
				}
			}
		}
	}
	
	private void drawArticoliPanel() {
		if (panelArticoli != null) {
			panelArticoli.clear();
			if (idAnagrafica != null) {
				if (idAnagrafica != AppConstants.NEW_ITEM_ID) {
					Anchor nuovoLink = null;
					if (isOperator) {
						nuovoLink = new Anchor(ClientConstants.ICON_ADD+"Abbina articolo", true);
						panelArticoli.add(nuovoLink);
					}
					DataModel<EvasioniArticoli> model =
							new EvasioniArticoliTable.EvasioniArticoliByAnagraficaModel(idAnagrafica);
					articoliTable = new EvasioniArticoliTable(model, utente.getRuolo(), this, false);
					panelArticoli.add(articoliTable);
					if(isOperator) {
						nuovoLink.addMouseDownHandler(new MouseDownHandler() {
							@Override
							public void onMouseDown(MouseDownEvent event) {
								EvasioneArticoloPopUp popup = new EvasioneArticoloPopUp();
								popup.initByAnagrafica(item.getId(), true, false, articoliTable);
							}
						});
					}
				}
			}
		}
	}
	
	private void drawFatturePanel() {
		if (panelFatture != null) {
			panelFatture.clear();
			if (idAnagrafica != null) {
				if (idAnagrafica != AppConstants.NEW_ITEM_ID) {
					DataModel<Fatture> model =
							new FattureTable.FattureByAnagraficaModel(idAnagrafica);
					fattureTable = new FattureTable(model, utente, this);
					panelFatture.add(fattureTable);
				}
			}
		}
	}
	
	private void drawCrediti() {
		if (panelCred != null) {
			panelCred.clear();
			panelCred.setTitle(TITLE_CREDITI);
			if (item.getId() != null) {
				if (item.getId().intValue() != AppConstants.NEW_ITEM_ID) {
					DataModel<PagamentiCrediti> pagamentiModel = new CreditiTable.CreditiAnagraficaModel(item);
					final CreditiTable credTable = new CreditiTable(pagamentiModel, utente.getRuolo(), this);
					VerticalPanel holder = new VerticalPanel();
					//Anchor createAnchor = new Anchor(ClientConstants.ICON_ADD+"Aggiungi credito", true);
					//createAnchor.setVisible(isEditor);
					//if (isEditor) {
					//	createAnchor.addMouseDownHandler(new MouseDownHandler() {
					//		@Override
					//		public void onMouseDown(MouseDownEvent event) {
					//			new PagamentoPopUp(item.getId(), credTable, null);
					//		}
					//	});
					//}
					//if (item.getId() == null) {
					//	createAnchor.setVisible(false);
					//} else if (item.getId().intValue() == AppConstants.NEW_ITEM_ID) {
					//	createAnchor.setVisible(false);
					//}
					//holder.add(createAnchor);
					holder.add(credTable);
					panelCred.add(holder);
				}
			}
			//panelPag.add(new InlineHTML("<br/>"));
		}
	}

	private void drawPagamenti() {
		//final IRefreshable fThis = this;
		if (panelPag != null) {
			panelPag.clear();
			panelPag.setTitle(TITLE_PAGAMENTI);
			if (item.getId() != null) {
				if (item.getId().intValue() != AppConstants.NEW_ITEM_ID) {
					DataModel<Pagamenti> pagModel = new PagamentiTable.PagamentiAnagraficaModel(item.getId());
					PagamentiTable pagTable = new PagamentiTable(pagModel, utente.getRuolo(), this);
					VerticalPanel holder = new VerticalPanel();
					//Anchor createAnchor = new Anchor(ClientConstants.ICON_ADD+"Nuovo pagamento", true);
					//createAnchor.setVisible(isEditor);
					//if (isEditor) {
					//	createAnchor.addMouseDownHandler(new MouseDownHandler() {
					//		@Override
					//		public void onMouseDown(MouseDownEvent event) {
					//			new PagamentoPopUp(item.getId(), fThis, null);
					//		}
					//	});
					//}
					//if (item.getId() == null) {
					//	createAnchor.setVisible(false);
					//} else if (item.getId().intValue() == AppConstants.NEW_ITEM_ID) {
					//	createAnchor.setVisible(false);
					//}
					//holder.add(createAnchor);
					holder.add(pagTable);
					panelPag.add(holder);
				}
			}
			//panelPag.add(new InlineHTML("<br/>"));
		}
	}
	
	
	
	/***** ASYNC SERVICES *****/
	
	
	
	private void loadAnagrafiche() {
		AsyncCallback<Anagrafiche> callback = new AsyncCallback<Anagrafiche>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(Anagrafiche result) {
				item = result;
				if (result.getId() != null) idAnagrafica = result.getId();
				drawAnagrafiche();
				drawAnagraficheDettaglio();
				drawAbbonamentiPanel();
				drawRegalatiPanel();
				drawPromossiPanel();
				drawArticoliPanel();
				drawFatturePanel();
				drawCrediti();
				drawPagamenti();
				WaitSingleton.get().stop();
			}
		};
		
		//look for item with idAnagrafica only if idAnagrafica is defined
		if (idAnagrafica.intValue() != AppConstants.NEW_ITEM_ID) {
			WaitSingleton.get().start();
			anagraficheService.findById(idAnagrafica, callback);
		} else {
			//is new anagrafica
			WaitSingleton.get().start();
			anagraficheService.createAnagrafica(callback);
		}
	}
	
	private void saveData() throws BusinessException {
		AsyncCallback<Integer> callback = new AsyncCallback<Integer>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(Integer result) {			
				idAnagrafica = (Integer)result;
				//loadAnagrafiche();
				UiSingleton.get().addInfo(AppConstants.MSG_SAVE_OK);
				WaitSingleton.get().stop();
				UriParameters params = new UriParameters();
				params.add(AppConstants.PARAM_ID, idAnagrafica);
				params.triggerUri(UriManager.ANAGRAFICA);
			}
		};
		//scrittura
		Date today = DateUtil.now();
		try {
			item = anagPanel.getValue();
		} catch (ValidationException e) {
			throw new BusinessException(e.getMessage(), e);
		}
		item.setGiuntiCardClub(giuntiCardText.getValue().trim());
		item.setCodiceSap(sapText.getValue().trim());
		item.setNecessitaVerifica(false);
		
		localitaFattCapPanel.setIdNazione(nazioniFattList.getSelectedValueString());
		item.getIndirizzoFatturazione().setTitolo(titoloFattText.getValue().trim());
		item.getIndirizzoFatturazione().setCognomeRagioneSociale(ragSocFattText.getValue().trim());
		item.getIndirizzoFatturazione().setNome(nomeFattText.getValue().trim());
		item.getIndirizzoFatturazione().setIdNazioneT(nazioniFattList.getSelectedValueString());
		item.getIndirizzoFatturazione().setCap(localitaFattCapPanel.getLocalitaCap().trim());
		item.getIndirizzoFatturazione().setIndirizzo(indirizzoFattText.getValue().trim());
		item.getIndirizzoFatturazione().setLocalita(localitaFattCapPanel.getLocalitaName().trim());
		item.getIndirizzoFatturazione().setPresso(pressoFattText.getValue().trim());
		item.getIndirizzoFatturazione().setProvincia(localitaFattCapPanel.getLocalitaProv());
		item.getIndirizzoFatturazione().setDataModifica(today);
		item.getIndirizzoFatturazione().setIdUtente(AuthSingleton.get().getUtente().getId());
		WaitSingleton.get().start();
		anagraficheService.saveOrUpdate(item, callback);
	}
	
	private void deleteAnagrafica() {
		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
			}
			@Override
			public void onSuccess(Boolean result) {
				//Visualizza la ricerca anagrafica
				UriParameters params = new UriParameters();
				params.triggerUri(UriManager.ANAGRAFICHE_FIND);
			}
		};
		
		boolean confirm = Window.confirm("Vuoi veramente cancellare questa anagrafica?");
		if (confirm) {
			anagraficheService.deleteAnagrafica(item.getId(), callback);
		}
	}
}
