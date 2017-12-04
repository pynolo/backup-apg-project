package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.CookieSingleton;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.TipiAbbService;
import it.giunti.apg.client.services.TipiAbbServiceAsync;
import it.giunti.apg.client.widgets.DateOnlyBox;
import it.giunti.apg.client.widgets.DeltaDaysPanel;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.client.widgets.OpzioniListiniPanel;
import it.giunti.apg.client.widgets.SubPanel;
import it.giunti.apg.client.widgets.TipiAbbRinnovoSelectPanel;
import it.giunti.apg.client.widgets.TitlePanel;
import it.giunti.apg.client.widgets.VersioningPanel;
import it.giunti.apg.client.widgets.select.AliquoteIvaSelect;
import it.giunti.apg.client.widgets.select.MacroareeSelect;
import it.giunti.apg.client.widgets.select.PeriodiciSelect;
import it.giunti.apg.client.widgets.select.TagSelectPanel;
import it.giunti.apg.client.widgets.tables.ArticoliListiniTable;
import it.giunti.apg.client.widgets.tables.ComunicazioniTable;
import it.giunti.apg.client.widgets.tables.DataModel;
import it.giunti.apg.client.widgets.tables.ListiniTable;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.ArticoliListini;
import it.giunti.apg.shared.model.Comunicazioni;
import it.giunti.apg.shared.model.Listini;
import it.giunti.apg.shared.model.Ruoli;
import it.giunti.apg.shared.model.Utenti;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TipoAbbonamentoFrame extends FramePanel implements IAuthenticatedWidget {
	
	private final TipiAbbServiceAsync tipiAbbService = GWT.create(TipiAbbService.class);
	
	private static final String BOX_WIDTH = "20em";
	
	private static final String TITLE_TIPO_ABBONAMENTO = "Tipo Abbonamento";
	private static final String TITLE_ARTICOLI = "Articoli da spedire";
	private static final String TITLE_COMUNICAZIONI = "Comunicazioni previste";
	private static final String TITLE_STORICO = "Storico Tipo Abbonamento";
	
	private Integer idListino = null;
	private Integer idTipoAbbonamento = null;
	private Listini item = null;
	private Ruoli ruolo = null;
	private boolean isOperator = false;
	private boolean isEditor = false;
	private boolean isAdmin = false;
	private boolean isSuper = false;
	private Utenti utente = null;
	
	private VerticalPanel panelLst = null;
	private TextBox codiceText = null;
	private PeriodiciSelect periodiciList = null;
	private TextBox nomeText = null;
	private TextBox prezzoText = null;
	private CheckBox cartaceoCheck = null;
	private CheckBox digitaleCheck = null;
	//private TextBox prezzoOpzText = null;
	private TextBox numFascicoliText = null;
	private MacroareeSelect macroareeList = null;
	private ListBox meseInizioList = null;
	private DateOnlyBox inizioDate = null;
	private DateOnlyBox fineDate = null;
	private OpzioniListiniPanel opzPanel = null;
	private CheckBox invioNoPagCheck = null;
	private CheckBox fatturaDifferitaCheck = null;
	private CheckBox fatturaInibitaCheck = null;
	private CheckBox permettiPaganteCheck = null;
	private CheckBox stampaOmaggioCheck = null;
	private CheckBox stampaDonatoreCheck = null;
	private TextBox gracingInizialeText = null;
	private TextBox gracingFinaleText = null;
	private TipiAbbRinnovoSelectPanel tipiAbbRinnPanel = null;
	private TextBox noteText = null;
	private TagSelectPanel tagSelect = null;
	private AliquoteIvaSelect ivaSelect = null;
	private DeltaDaysPanel ddBloccoOfferta = null;
	private DeltaDaysPanel ddRinnovoAbilitato = null;
	private DeltaDaysPanel ddAvvisoAccredito = null;
	private DeltaDaysPanel ddAvvisoRinnovo = null;
	private DeltaDaysPanel ddAccreditoAuto = null;
	private DeltaDaysPanel ddRinnovoAuto = null;
	
	private SubPanel panelArticoli = null;
	private SubPanel panelCom = null;
	private SubPanel panelStorico = null;
	
	// METHODS
	
	public TipoAbbonamentoFrame(UriParameters params) {
		super();
		if (params == null) {
			params = new UriParameters();
		}
		idTipoAbbonamento = params.getIntValue(AppConstants.PARAM_ID_TIPO_ABBONAMENTO);
		Integer value = params.getIntValue(AppConstants.PARAM_ID);
		if (value != null) {
			idListino = value;
			AuthSingleton.get().queueForAuthentication(this);
		}
	}
	
	@Override
	public void onSuccessfulAuthentication(Utenti utente) {
		this.utente = utente;
		init(utente);
	}
	
	private void init(Utenti utente) {
		// Editing rights
		ruolo = utente.getRuolo();
		isOperator = (ruolo.getId() >= AppConstants.RUOLO_OPERATOR);
		isEditor = (ruolo.getId() >= AppConstants.RUOLO_EDITOR);
		isAdmin = (ruolo.getId() >= AppConstants.RUOLO_ADMIN);
		isSuper = (ruolo.getId() >= AppConstants.RUOLO_SUPER);
		// UI
		if (isOperator) {
			panelLst = new VerticalPanel();
			this.add(panelLst, TITLE_TIPO_ABBONAMENTO);
			loadListini();
		}
	}


	private void draw() {
		drawTipoAbb();
		//Articoli
		panelArticoli = new SubPanel(TITLE_ARTICOLI);
		panelLst.add(panelArticoli);
		drawArticoli(item.getId());
		//Comunicazioni
		panelCom = new SubPanel(TITLE_COMUNICAZIONI);
		panelLst.add(panelCom);
		drawComunicazioni(item.getTipoAbbonamento().getId());
		//Storico
		panelStorico = new SubPanel(TITLE_STORICO);
		panelLst.add(panelStorico);
		drawStorico(item.getTipoAbbonamento().getId());
		//PANNELLO VERSIONAMENTO
		if (item.getId() != null) {
			VersioningPanel versionPanel = new VersioningPanel(
					"Listini", item.getId(), item.getIdUtente(), item.getDataModifica());
			panelLst.add(versionPanel);
		}
	}
	
	/** This method empties the ContentPanel and redraws the 'item' data
	 * @param item
	 */
	private void drawTipoAbb() {
		final Listini item = this.item;
		//Changes isAdmin if is a old instance:
		boolean isLast = false;
		if (item.getDataFine() == null) {
			isLast = true;
		} else {
			if (item.getDataFine().before(DateUtil.now())) {
				isLast = true;
			}
		}
		if (item.getId() == null) {
			UiSingleton.get().addWarning("Attenzione, si sta creando un nuovo Tipo Abbonamento. " +
					"Dopo il salvataggio non sarà più possibile eliminarlo.");
		}
		boolean editable = (isAdmin && isLast) || isSuper;
		// clean form
		panelLst.clear();
		
		TitlePanel tipoPanel = new TitlePanel("Tipo");
		FlexTable tipoTable = new FlexTable();
		panelLst.add(tipoPanel);
		tipoPanel.add(tipoTable);
		TitlePanel listinoPanel = new TitlePanel("Listino");
		FlexTable listinoTable = new FlexTable();
		panelLst.add(listinoPanel);
		listinoPanel.add(listinoPanel);
		
		//Warning
		InlineHTML warningHtml = new InlineHTML("Un Tipo Abbonamento è suddiviso in una parte costante, "+
				"il <b>Tipo</b> vero e proprio, e una parte che cambia nel tempo, il <b>Listino</b>, "+
				"sono rilasciate versioni differenti a seconda di aumenti di prezzi, cambi di gracing e altro.<br/> "+
				"In questa pagina &egrave; possibile cambiare entrambe le sezioni, considerando che "+
				"quindi ogni modifica al Tipo impatta su tutti i listini passati collegati.");
		panelLst.add(warningHtml);

		
		// ** Tipo **
		int r=0;
		
		// Periodico
		tipoTable.setHTML(r, 0, "Periodico");
		periodiciList = new PeriodiciSelect(item.getTipoAbbonamento().getPeriodico().getId(),
				DateUtil.now(), false, false, utente);
		periodiciList.setEnabled(editable);
		periodiciList.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				Integer idPeriodico = periodiciList.getSelectedValueInt();
				CookieSingleton.get().setCookie(ClientConstants.COOKIE_LAST_PERIODICO, idPeriodico+"");
			}
		});
		tipoTable.setWidget(r, 1, periodiciList);
		// Codice
		tipoTable.setHTML(r, 3, "Codice tipo abb."+ClientConstants.MANDATORY);
		HorizontalPanel codicePanel = new HorizontalPanel();
		codiceText = new TextBox();
		codiceText.setValue(item.getTipoAbbonamento().getCodice());
		codiceText.setEnabled(editable);
		codiceText.setMaxLength(4);
		codiceText.setWidth("5em");
		codicePanel.add(codiceText);
		if (item.getUid() != null) {
			if (!item.getUid().equals(""))
					codicePanel.add(new InlineHTML(" <i>UID ["+item.getUid()+"]</i>"));
		}
		tipoTable.setWidget(r, 4, codicePanel);
		r++;
		
		//Nome
		tipoTable.setHTML(r, 0, "Descrizione"+ClientConstants.MANDATORY);
		nomeText = new TextBox();
		nomeText.setValue(item.getTipoAbbonamento().getNome());
		nomeText.setEnabled(editable);
		nomeText.setWidth(BOX_WIDTH);
		nomeText.setMaxLength(64);
		tipoTable.setWidget(r, 1, nomeText);
		//Invio senza pagamento
		tipoTable.setHTML(r, 3, "Pagante &ne; abbonato");
		permettiPaganteCheck = new CheckBox();
		permettiPaganteCheck.setValue(item.getTipoAbbonamento().getPermettiPagante());
		permettiPaganteCheck.setEnabled(editable);
		tipoTable.setWidget(r, 4, permettiPaganteCheck);
		r++;

		//Blocco offerta
		tipoTable.setHTML(r, 0, "Data blocco offerta");
		ddBloccoOfferta = new DeltaDaysPanel(item.getTipoAbbonamento().getDeltaInizioBloccoOfferta(), "inizio");
		ddBloccoOfferta.setEnabled(editable);
		tipoTable.setWidget(r, 1, ddBloccoOfferta);
		//Fase rinnovabile
		tipoTable.setHTML(r, 3, "Data fase rinnovabile");
		ddRinnovoAbilitato = new DeltaDaysPanel(item.getTipoAbbonamento().getDeltaFineRinnovoAbilitato(), "fine");
		ddRinnovoAbilitato.setEnabled(editable);
		tipoTable.setWidget(r, 4, ddRinnovoAbilitato);
		r++;
		//Data avviso addebito
		tipoTable.setHTML(r, 0, "Data avviso addebito");
		ddAvvisoAccredito = new DeltaDaysPanel(item.getTipoAbbonamento().getDeltaInizioAvvisoPagamento(), "inizio");
		ddAvvisoAccredito.setEnabled(editable);
		tipoTable.setWidget(r, 1, ddAvvisoAccredito);
		//Data avviso rinnovo
		tipoTable.setHTML(r, 3, "Data avviso rinnovo");
		ddAvvisoRinnovo = new DeltaDaysPanel(item.getTipoAbbonamento().getDeltaFineAvvisoRinnovo(), "fine");
		ddAvvisoRinnovo.setEnabled(editable);
		tipoTable.setWidget(r, 4, ddAvvisoRinnovo);
		r++;
		//addebito automatico
		tipoTable.setHTML(r, 0, "Data addebito autom.");
		ddAccreditoAuto = new DeltaDaysPanel(item.getTipoAbbonamento().getDeltaInizioPagamentoAutomatico(), "inizio");
		ddAccreditoAuto.setEnabled(editable);
		tipoTable.setWidget(r, 1, ddAccreditoAuto);
		//Rinnovo automatico
		tipoTable.setHTML(r, 3, "Data rinnovo autom.");
		ddRinnovoAuto = new DeltaDaysPanel(item.getTipoAbbonamento().getDeltaFineRinnovoAutomatico(), "fine");
		ddRinnovoAuto.setEnabled(editable);
		tipoTable.setWidget(r, 4, ddRinnovoAuto);
		r++;
		
		
		// ** Listino **
		r=0;
		
		//Opzioni
		opzPanel = new OpzioniListiniPanel(
				item.getTipoAbbonamento().getPeriodico().getId(),
				item.getDataInizio(),
				item.getDataFine(),
				item.getOpzioniListiniSet(), "Opzioni incluse nel prezzo "+ClientConstants.ICON_OPZIONI);
		opzPanel.setVisible(false);
		opzPanel.setEnabled(isOperator);
		listinoTable.setWidget(r, 0, opzPanel);
		listinoTable.getFlexCellFormatter().setColSpan(r, 0, 5);
		r++;
		
		//Prezzo
		listinoTable.setHTML(r, 0, "Prezzo"+ClientConstants.MANDATORY);
		HorizontalPanel prezzoPanel = new HorizontalPanel();
		prezzoText = new TextBox();
		prezzoText.setValue(ClientConstants.FORMAT_CURRENCY.format(item.getPrezzo()));
		prezzoText.setEnabled(editable);
		prezzoText.setWidth("5em");
		prezzoPanel.add(prezzoText);
		prezzoPanel.add(new InlineHTML("<i>0,00&nbsp;=&nbsp;omaggio</i>"));
		listinoTable.setWidget(r, 1, prezzoPanel);
		//Fatturazione inibita
		listinoTable.setHTML(r, 3, "Non produrre fattura");
		fatturaInibitaCheck = new CheckBox();
		fatturaInibitaCheck.setValue(item.getFatturaInibita());
		fatturaInibitaCheck.setEnabled(editable);
		listinoTable.setWidget(r, 4, fatturaInibitaCheck);
		r++;
		
		//Invio senza pagamento
		listinoTable.setHTML(r, 0, "Invia fascicoli senza pagamento");
		invioNoPagCheck = new CheckBox();
		invioNoPagCheck.setValue(item.getInvioSenzaPagamento());
		invioNoPagCheck.setEnabled(editable);
		listinoTable.setWidget(r, 1, invioNoPagCheck);
		//Pagato con fattura
		listinoTable.setHTML(r, 3, "Fattura a pagamento differito");
		fatturaDifferitaCheck = new CheckBox();
		fatturaDifferitaCheck.setValue(item.getFatturaDifferita());
		fatturaDifferitaCheck.setEnabled(editable);
		listinoTable.setWidget(r, 4, fatturaDifferitaCheck);
		r++;
		
		//Supporto
		listinoTable.setHTML(r, 0, "Supporto");
		HorizontalPanel opzortoPanel = new HorizontalPanel();
		//Cartaceo
		cartaceoCheck = new CheckBox("Cartaceo "+ClientConstants.ICON_CARTACEO+" &nbsp;&nbsp;&nbsp;", true);
		cartaceoCheck.setValue(item.getCartaceo());
		cartaceoCheck.setEnabled(isEditor);
		opzortoPanel.add(cartaceoCheck);
		//Digitale
		digitaleCheck = new CheckBox("App "+ClientConstants.ICON_APP, true);
		digitaleCheck.setValue(item.getDigitale());
		digitaleCheck.setEnabled(isEditor);
		opzortoPanel.add(digitaleCheck);
		listinoTable.setWidget(r, 1, opzortoPanel);
		//IVA
		listinoTable.setHTML(r, 3, "Aliquota IVA");
		Integer aliquota = AppConstants.SELECT_EMPTY_VALUE;
		if (item.getAliquotaIva() != null) aliquota = item.getAliquotaIva().getId();
		ivaSelect = new AliquoteIvaSelect(aliquota, item.getDataInizio());
		ivaSelect.setEnabled(isEditor);
		listinoTable.setWidget(r, 4, ivaSelect);
		r++;
		
		//Mesi
		listinoTable.setHTML(r, 0, "Numero fascicoli"+ClientConstants.MANDATORY);
		numFascicoliText = new TextBox();
		numFascicoliText.setValue(item.getNumFascicoli()+"");
		numFascicoliText.setEnabled(isSuper);
		numFascicoliText.setMaxLength(2);
		numFascicoliText.setWidth(BOX_WIDTH);
		listinoTable.setWidget(r, 1, numFascicoliText);
		//Macroarea
		listinoTable.setHTML(r, 3, "Zona");
		macroareeList = new MacroareeSelect(item.getIdMacroarea());
		macroareeList.setEnabled(editable);
		listinoTable.setWidget(r, 4, macroareeList);
		r++;
		
		//Mese di inizio
		listinoTable.setHTML(r, 0, "Mese fisso di inizio");
		meseInizioList = new ListBox();
		meseInizioList.addItem("[qualsiasi]", "");
		for (int i=1;i<13;i++) {
			meseInizioList.addItem(ClientConstants.MESI[i], i+"");
		}
		if (item.getMeseInizio() != null) {
			meseInizioList.setSelectedIndex(item.getMeseInizio());
		}
		meseInizioList.setEnabled(editable);
		listinoTable.setWidget(r, 1, meseInizioList);
		r++;
		
		//Gracing iniziale
		listinoTable.setHTML(r, 0, "Gracing iniziale"+ClientConstants.MANDATORY);
		gracingInizialeText = new TextBox();
		gracingInizialeText.setValue(formatNum(item.getGracingIniziale()));
		gracingInizialeText.setEnabled(isSuper);
		gracingInizialeText.setWidth("2em");
		gracingInizialeText.setMaxLength(1);
		listinoTable.setWidget(r, 1, gracingInizialeText);
		//Gracing finale
		listinoTable.setHTML(r, 3, "Gracing finale"+ClientConstants.MANDATORY);
		gracingFinaleText = new TextBox();
		gracingFinaleText.setValue(formatNum(item.getGracingFinale()));
		gracingFinaleText.setEnabled(isSuper);
		gracingFinaleText.setWidth("2em");
		gracingFinaleText.setMaxLength(1);
		listinoTable.setWidget(r, 4, gracingFinaleText);
		r++;
		
		//Scritte su talloncini e bollettini
		listinoTable.setHTML(r, 0, "Stampa la scritta 'OMAGGIO'");
		stampaOmaggioCheck = new CheckBox();
		stampaOmaggioCheck.setValue(item.getStampaScrittaOmaggio());
		stampaOmaggioCheck.setEnabled(editable);
		listinoTable.setWidget(r, 1, stampaOmaggioCheck);
		listinoTable.setHTML(r, 3, "Stampa 'Copia offerta da'");
		stampaDonatoreCheck = new CheckBox();
		stampaDonatoreCheck.setValue(item.getStampaDonatore());
		stampaDonatoreCheck.setEnabled(editable);
		listinoTable.setWidget(r, 4, stampaDonatoreCheck);
		r++;
		
		//Tipi abbonamento al rinnovo
		listinoTable.setHTML(r, 0, "Tipi al rinnovo<br />"
				+ "<i>(Solo i primi due potranno<br/>"
				+ "comparire nei bolletini)</i>");
		tipiAbbRinnPanel = new TipiAbbRinnovoSelectPanel(item);
		tipiAbbRinnPanel.setEnabled(editable);
		listinoTable.setWidget(r, 1, tipiAbbRinnPanel);
		//Tag
		listinoTable.setHTML(r, 3, "Tag");
		tagSelect = new TagSelectPanel(item.getTag());
		tagSelect.setEnabled(editable);
		listinoTable.setWidget(r, 4, tagSelect);
		r++;
		
		// DataInizio
		listinoTable.setHTML(r, 0, "Valido da"+ClientConstants.MANDATORY);
		inizioDate = new DateOnlyBox();
		inizioDate.setFormat(ClientConstants.BOX_FORMAT_DAY);
		inizioDate.setValue(item.getDataInizio());
		inizioDate.setWidth(BOX_WIDTH);
		inizioDate.setEnabled(isSuper);
		if (editable) {
			listinoTable.setWidget(r, 1, inizioDate);
		} else if (item.getDataInizio() != null) {
			listinoTable.setHTML(r, 1, ClientConstants.FORMAT_DAY.format(item.getDataInizio()));
		}
		// DataFine
		listinoTable.setHTML(r, 3, "Fino a");
		fineDate = new DateOnlyBox();
		fineDate.setFormat(ClientConstants.BOX_FORMAT_DAY);
		fineDate.setValue(item.getDataFine());
		fineDate.setWidth(BOX_WIDTH);
		if (isSuper) {
			listinoTable.setWidget(r, 4, fineDate);
		} else if (item.getDataFine() != null) {
			listinoTable.setHTML(r, 4, ClientConstants.FORMAT_DAY.format(item.getDataFine()));
		}
		r++;
		
		//Note
		listinoTable.setHTML(r, 0, "Note");
		noteText = new TextBox();
		noteText.setValue(item.getNote());
		noteText.setWidth(BOX_WIDTH);
		noteText.setEnabled(editable);
		listinoTable.getFlexCellFormatter().setColSpan(r, 1, 4);
		listinoTable.setWidget(r, 1, noteText);
		r++;
		
				
		HorizontalPanel buttonPanel = getButtonPanel(editable);
		listinoTable.setWidget(r,0,buttonPanel);
		listinoTable.getFlexCellFormatter().setColSpan(r, 0, 6);//Span su 5 colonne
		r++;
		
		panelLst.add(new InlineHTML("<br/>"));
	}
	
	private HorizontalPanel getButtonPanel(boolean editable) {
		HorizontalPanel buttonPanel = new HorizontalPanel();
		if (editable) {
			// Bottone SALVA
			Button submitButton = new Button("Salva", new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					try {
						saveData(false);//senza versionamento
					} catch (ValidationException e) {
						UiSingleton.get().addWarning(e.getMessage());
					}
				}
			});
			if (idListino.equals(AppConstants.NEW_ITEM_ID)) {
				submitButton.setText("Crea");
			}
			submitButton.setEnabled(editable);
			buttonPanel.add(submitButton);
			
			if (!idListino.equals(AppConstants.NEW_ITEM_ID)) {
				// separatore
				Image separator = new Image("img/separator.gif");
				buttonPanel.add(separator);
				// Bottone RINNOVA
				Button versionaButton = new Button("Modifica per i nuovi abbonati", new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						try {
							saveData(true);//Salva con versionamento
						} catch (ValidationException e) {
							UiSingleton.get().addWarning(e.getMessage());
						}
					}
				});
				versionaButton.setEnabled(isSuper);
				buttonPanel.add(versionaButton);
			}
		}
		return buttonPanel;
	}
	
	private String formatNum(Integer i) {
		if (i == null) return "";
		else return ClientConstants.FORMAT_INTEGER.format(i);
	}
	
	private void drawArticoli(Integer idListino) {
		panelArticoli.setTitle(TITLE_ARTICOLI);
		panelArticoli.clear();
		if (idListino != null) {
			DataModel<ArticoliListini> model = new ArticoliListiniTable.ArticoliListiniModel(idListino);
			ArticoliListiniTable alTable = new ArticoliListiniTable(model, ruolo);
			FlowPanel holder = new FlowPanel();
			panelArticoli.add(holder);
			Anchor nuovoLink = null;
			if (isAdmin) {
				nuovoLink = new Anchor(ClientConstants.ICON_ADD+"Abbina articolo", true);
				holder.add(nuovoLink);
			}
			String warning = "<br />Attenzione, le modifiche agli articoli saranno "+
					"retroattive per gli abbonamenti <b>di tipo "+item.getTipoAbbonamento().getCodice()+
					" creati dal "+ClientConstants.FORMAT_DAY.format(item.getDataInizio());
			if (item.getDataFine() != null) warning +=
					" al "+ClientConstants.FORMAT_DAY.format(item.getDataFine());
			warning += "</b>.<br />"+
					"Per evitare spedizioni indesiderate &egrave; preferibile creare una nuova versione di questo tipo abbonamento "+
					"con il bottone <b>Modifica per i nuovi abbonati</b>";
			holder.add(new InlineHTML(warning));
			holder.add(alTable);
			if(isAdmin) {
				final Integer fIdListino = idListino;
				final ArticoliListiniTable fAlTable = alTable;
				nuovoLink.addMouseDownHandler(new MouseDownHandler() {
					@Override
					public void onMouseDown(MouseDownEvent event) {
						new ArticoloListinoPopUp(AppConstants.NEW_ITEM_ID, fIdListino, fAlTable);
					}
				});
			}
		}
	}
	
	private void drawComunicazioni(Integer idTipoAbb) {
		if (item.getDataFine() == null) {
			panelCom.setTitle(TITLE_COMUNICAZIONI);
			panelCom.clear();
			if (item.getTipoAbbonamento().getId() != null) {
				DataModel<Comunicazioni> model = new ComunicazioniTable.ComunicazioniByTipoAbbModel(idTipoAbb, DateUtil.now());
				ComunicazioniTable comTable = new ComunicazioniTable(model);
				panelCom.add(comTable);
			}
			panelCom.add(new InlineHTML("<br/>"));
		}
	}
	
	private void drawStorico(Integer idTipoAbb) {
		panelStorico.setTitle(TITLE_STORICO);
		panelStorico.clear();
		if (item.getTipoAbbonamento().getId() != null) {
			DataModel<Listini> model = new ListiniTable.StoricoListiniModel(idTipoAbb);
			ListiniTable iaTable = new ListiniTable(model, isEditor);
			panelStorico.add(iaTable);
		}
		//panelStorico.add(new InlineHTML("<br/>"));
	}
	
	
	
	// METODI ASINCRONI
	
	
	
	private void loadListini() {
		AsyncCallback<Listini> callback = new AsyncCallback<Listini>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(Listini result) {
				item = result;
				WaitSingleton.get().stop();
				draw();
			}
		};
		
		//look for item with id only if id is defined
		if (idListino.intValue() != AppConstants.NEW_ITEM_ID) {
			WaitSingleton.get().start();
			tipiAbbService.findListinoById(idListino, callback);
		} else {
			//is new tipo abbonamento
			WaitSingleton.get().start();
			if (idTipoAbbonamento != null) {
				tipiAbbService.createListinoFromTipo(idTipoAbbonamento, callback);
			} else {
				Integer idPeriodico = ValueUtil.stoi(CookieSingleton.get().getCookie(ClientConstants.COOKIE_LAST_PERIODICO));
				tipiAbbService.createListinoFromPeriodico(idPeriodico, callback);
			}
		}
	}

	private void saveData(boolean versionamento) throws ValidationException {
		String avviso = "ATTENZIONE. Confermando l'operazione il tipo abbonamento " +
				"sarà modificato in modo retroattivo e in alcuni casi il " +
				"comportamento di APG potrebbe essere imprevedibile.";
		if (versionamento) avviso = "Salvando la nuova versione del tipo abbonamento " +
				"i valori aggiornati saranno applicati solamente ai nuovi abbonamenti o ai " +
				"rinnovi effettuati da ora in avanti.";
		boolean confirm = Window.confirm(avviso);
		if (!confirm) {
			return;
		}
		AsyncCallback<Integer> callback = new AsyncCallback<Integer>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(Integer result) {			
				idListino = result;
				loadListini();
				UiSingleton.get().addInfo(AppConstants.MSG_SAVE_OK);
				WaitSingleton.get().stop();
			}
		};
		
		//Validazione
		String codice = codiceText.getValue();
		if (codice == null) throw new ValidationException("Codice mancante");
		if (codice.length()==0) throw new ValidationException("Codice mancante");
		Integer numFascicoli = 0;
		try {
			numFascicoli = Integer.valueOf(numFascicoliText.getValue());
		} catch (NumberFormatException e1) {
			throw new ValidationException("Valore non valido nel numero fascicoli");
		}
		Integer numeriAntepagamento = 0;
		try {
			numeriAntepagamento = Integer.valueOf(gracingInizialeText.getValue());
		} catch (NumberFormatException e) {
			throw new ValidationException("Valore non valido di numeri gratuiti iniziali");
		}
		Integer numeriSuccessivi = 0;
		try {
			numeriSuccessivi = Integer.valueOf(gracingFinaleText.getValue());
		} catch (NumberFormatException e) {
			throw new ValidationException("Valore non valido di numeri gratuiti finali");
		}
		Double prezzo = 0D;
		if (prezzoText.getValue() == null) throw new ValidationException("Valore non valido nel prezzo");
		if (prezzoText.getValue().equals("")) throw new ValidationException("Valore non valido nel prezzo");
		try {
			prezzo = ClientConstants.FORMAT_CURRENCY.parse(prezzoText.getValue());
		} catch (NumberFormatException e1) {
			throw new ValidationException("Valore non valido nel prezzo");
		}
		//Double prezzoSuppl = null;
		//if (prezzoOpzText.getValue() != null) {
		//	if (prezzoOpzText.getValue().length() > 0) {
		//		try {
		//			prezzoSuppl = ClientConstants.FORMAT_CURRENCY.parse(prezzoOpzText.getValue());
		//		} catch (NumberFormatException e1) {
		//			throw new ValidationException("Valore non valido nel prezzo opzioni");
		//		}
		//	}
		//}
		if (inizioDate.getValue() == null) {
			throw new ValidationException("La data iniziale non puo' essere vuota");
		}
		if ((prezzo < AppConstants.SOGLIA) && !invioNoPagCheck.getValue() && !fatturaDifferitaCheck.getValue()) {
			throw new ValidationException("Se il prezzo e' 0 allora e' obbligatorio impostare l'invio senza pagamento o la fattura a pagamento differito");
		}
		//Assegnamento
		Date today = DateUtil.now();
		item.setDataInizio(inizioDate.getValue());
		item.setDataFine(fineDate.getValue());
		item.setIdMacroarea(macroareeList.getSelectedValueInt());
		item.setNumFascicoli(numFascicoli);
		item.setNote(noteText.getValue());
		item.setGracingIniziale(numeriAntepagamento);
		item.setGracingFinale(numeriSuccessivi);
		item.setPrezzo(prezzo);
		//item.setPrezzoOpzObbligatori(prezzoSuppl);
		item.setIdAliquotaIvaT(ivaSelect.getSelectedValueString());
		//if (tipoAbbRinnList != null) {
		//	Integer idRinnovo = tipoAbbRinnList.getSelectedValueInt();
		//	item.setTipoAbbonamentoRinnovo(null);
		//	item.setIdTipoAbbonamentoRinnovoT(idRinnovo);
		//}
		//if (tipoAbbRinnAltList != null) {
		//	Integer idAlternativa = tipoAbbRinnAltList.getSelectedValueInt();
		//	item.setTipoAbbonamentoRinnovoAlternativa(null);
		//	item.setIdTipoAbbonamentoRinnovoAltT(idAlternativa);
		//}
		List<Integer> idTipiRinnList = new ArrayList<Integer>();
		if (tipiAbbRinnPanel != null) {
			idTipiRinnList = tipiAbbRinnPanel.getIdValues();
		}
		try {
			int meseInizio = Integer.parseInt(meseInizioList.getValue(meseInizioList.getSelectedIndex()));
			item.setMeseInizio(meseInizio);
		} catch (NumberFormatException e) {
			item.setMeseInizio(null);
		}
		item.setInvioSenzaPagamento(invioNoPagCheck.getValue());
		item.setFatturaDifferita(fatturaDifferitaCheck.getValue());
		item.setFatturaInibita(fatturaInibitaCheck.getValue());
		item.setStampaScrittaOmaggio(stampaOmaggioCheck.getValue());
		item.setStampaDonatore(stampaDonatoreCheck.getValue());
		item.setDataModifica(today);
		item.setCartaceo(cartaceoCheck.getValue());
		item.setDigitale(digitaleCheck.getValue());
		item.setTag(tagSelect.getTagValues());
		item.setIdUtente(AuthSingleton.get().getUtente().getId());
		
		item.getTipoAbbonamento().setCodice(codice.toUpperCase());
		item.getTipoAbbonamento().setNome(nomeText.getValue());
		item.getTipoAbbonamento().setIdPeriodicoT(periodiciList.getSelectedValueString());
		item.getTipoAbbonamento().setPermettiPagante(permettiPaganteCheck.getValue());
		item.getTipoAbbonamento().setDataModifica(today);
		item.getTipoAbbonamento().setIdUtente(AuthSingleton.get().getUtente().getId());
		item.getTipoAbbonamento().setDeltaInizioBloccoOfferta(ddBloccoOfferta.getDeltaDays());
		item.getTipoAbbonamento().setDeltaInizioAvvisoPagamento(ddAvvisoAccredito.getDeltaDays());
		item.getTipoAbbonamento().setDeltaInizioPagamentoAutomatico(ddAccreditoAuto.getDeltaDays());
		item.getTipoAbbonamento().setDeltaFineRinnovoAbilitato(ddRinnovoAbilitato.getDeltaDays());
		item.getTipoAbbonamento().setDeltaFineAvvisoRinnovo(ddAvvisoRinnovo.getDeltaDays());
		item.getTipoAbbonamento().setDeltaFineRinnovoAutomatico(ddRinnovoAuto.getDeltaDays());
		
		item.setIdOpzioniListiniSetT(opzPanel.getValue());
		
		WaitSingleton.get().start();
		if (!versionamento) {
			tipiAbbService.saveOrUpdate(item, idTipiRinnList, callback);
		} else {
			tipiAbbService.createVersion(item, idTipiRinnList, callback);
		}
	}

}
