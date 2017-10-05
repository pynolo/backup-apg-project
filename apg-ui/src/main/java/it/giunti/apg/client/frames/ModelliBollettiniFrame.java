package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.CookieSingleton;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.ComunicazioniService;
import it.giunti.apg.client.services.ComunicazioniServiceAsync;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.client.widgets.TitlePanel;
import it.giunti.apg.client.widgets.select.FileResourcesSelect;
import it.giunti.apg.client.widgets.select.PeriodiciSelect;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.ModelliBollettini;
import it.giunti.apg.shared.model.Utenti;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ModelliBollettiniFrame extends FramePanel implements IAuthenticatedWidget {
	
	private final ComunicazioniServiceAsync comService = GWT.create(ComunicazioniService.class);
	private static final int TEXT_WIDTH_BOLLETTINO = 65;
	private static final int TEXT_HEIGHT_BOLLETTINO = 22;
	
	private static final String TITLE_COMUNICAZIONE = "Modello per bollettino";
	
	private Integer idPeriodico = null;
	private Integer idBolMod = null;
	private ModelliBollettini item = null;
	private boolean isAdmin = false;
	private boolean isSuper = false;
	private Utenti utente = null;
	
	private SimplePanel dataPanel = null;
	private PeriodiciSelect periodiciList = null;
	private TextBox descrText = null;
	private TextBox autBolText = null;
	private TextBox codiceModelloText = null;
	private FileResourcesSelect logoVerticalPathSelect = null;
	private FileResourcesSelect logoSmallPathSelect = null;
	private FileResourcesSelect pathReportSelect = null;
	private TextArea testoArea = null;
	//private TextArea opzArea = null;
	private TitlePanel previewPanel = null;
	
	// METHODS
	
	public ModelliBollettiniFrame(UriParameters params) {
		super();
		if (params == null) {
			params = new UriParameters();
		}
		idBolMod = params.getIntValue(AppConstants.PARAM_ID);
		AuthSingleton.get().queueForAuthentication(this);
	}
	
	@Override
	public void onSuccessfulAuthentication(Utenti utente) {
		this.utente = utente;
		init(utente);
	}
	
	private void init(Utenti utente) {
		idPeriodico = ValueUtil.stoi(CookieSingleton.get().getCookie(ClientConstants.COOKIE_LAST_PERIODICO));
		if (idPeriodico == null) idPeriodico=UiSingleton.get().getDefaultIdPeriodico(utente);
		// Editing rights
		int ruolo = utente.getRuolo().getId();
		isAdmin = (ruolo >= AppConstants.RUOLO_ADMIN);
		isSuper = (ruolo >= AppConstants.RUOLO_SUPER);
		// UI
		if (isAdmin) {
			dataPanel = new SimplePanel();
			this.add(dataPanel, TITLE_COMUNICAZIONE);
			loadModelliBollettini();
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
		descrText.setWidth("18em");
		table.setWidget(r, 1, descrText);
		// Periodico
		table.setHTML(r, 3, "Periodico");
		periodiciList = new PeriodiciSelect(item.getPeriodico().getId(), DateUtil.now(), false, false, utente);
		periodiciList.setEnabled(isSuper);
		table.setWidget(r, 4, periodiciList);
		r++;
		
		//Autorizzazione bollettino
		table.setHTML(r, 0, "Autorizz.bollettino"+ClientConstants.MANDATORY);
		autBolText = new TextBox();
		autBolText.setValue(item.getAutorizzazione());
		autBolText.setEnabled(isAdmin);
		autBolText.setMaxLength(256);
		autBolText.setWidth("18em");
		table.setWidget(r, 1, autBolText);
		//codice modello
		table.setHTML(r, 3, "Codice modello"+ClientConstants.MANDATORY);
		codiceModelloText = new TextBox();
		codiceModelloText.setValue(item.getCodiceModello());
		codiceModelloText.setEnabled(isAdmin);
		codiceModelloText.setMaxLength(4);
		codiceModelloText.setWidth("4em");
		table.setWidget(r, 4, codiceModelloText);
		r++;
		
		//Path report
		table.setHTML(r, 0, "Path report"+ClientConstants.MANDATORY);
		pathReportSelect = new FileResourcesSelect(item.getReportFilePath(),
				AppConstants.RESOURCE_DIR_JASPER,
				AppConstants.RESOURCE_TYPE_JASPER);
		pathReportSelect.setEnabled(isAdmin);
		table.setWidget(r, 1, pathReportSelect);
		r++;
		
		//Path report
		table.setHTML(r, 0, "Path logo piccolo"+ClientConstants.MANDATORY);
		logoSmallPathSelect = new FileResourcesSelect(item.getLogoSmallPath(),
				AppConstants.RESOURCE_DIR_LOGO,
				AppConstants.RESOURCE_TYPE_LOGO);
		logoSmallPathSelect.setEnabled(isAdmin);
		table.setWidget(r, 1, logoSmallPathSelect);
		//Path logo
		table.setHTML(r, 3, "Path logo verticale"+ClientConstants.MANDATORY);
		logoVerticalPathSelect = new FileResourcesSelect(item.getLogoVerticalPath(),
				AppConstants.RESOURCE_DIR_LOGO,
				AppConstants.RESOURCE_TYPE_LOGO);
		logoVerticalPathSelect.setEnabled(isAdmin);
		table.setWidget(r, 4, logoVerticalPathSelect);
		r++;
		
		HorizontalPanel bandTitlePanel = new HorizontalPanel();
		InlineHTML titoloBandella = new InlineHTML("Testo bandella"+ClientConstants.MANDATORY+"&nbsp;&nbsp;&nbsp;");
		bandTitlePanel.add(titoloBandella);
		Anchor linkPreviewBandella = new Anchor(ClientConstants.ICON_MAGNIFIER+"anteprima",true);
		linkPreviewBandella.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				loadTextPreview();
			}
		});
		bandTitlePanel.add(linkPreviewBandella);
		table.setWidget(r, 0, bandTitlePanel);
		table.getFlexCellFormatter().setColSpan(r, 0, 5);
		r++;
		testoArea = new TextArea();
		testoArea.setValue(item.getTestoBandella());
		testoArea.setVisibleLines(1);
		testoArea.setWidth("60em");
		testoArea.setHeight("20em");
		testoArea.setEnabled(isAdmin);
		table.getFlexCellFormatter().setColSpan(r, 0, 5);
		table.setWidget(r, 0, testoArea);
		r++;
		
		//HorizontalPanel opzTitlePanel = new HorizontalPanel();
		//InlineHTML titoloSuppl = new InlineHTML("Html opzioni <i>se previsti</i>&nbsp;&nbsp;&nbsp;");
		//opzTitlePanel.add(titoloSuppl);
		//Anchor linkPreviewSuppl = new Anchor(ClientConstants.ICON_MAGNIFIER+"anteprima",true);
		//linkPreviewSuppl.addClickHandler(new ClickHandler() {
		//	@Override
		//	public void onClick(ClickEvent arg0) {
		//		loadSupplPreview();
		//	}
		//});
		//opzTitlePanel.add(linkPreviewSuppl);
		//InlineHTML opzSuggerimenti = new InlineHTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
		//		"(<i>Suggerimento:</i> &#9744; = &amp;#9744;)");
		//opzTitlePanel.add(opzSuggerimenti);
		//table.setWidget(r, 0, opzTitlePanel);
		//table.getFlexCellFormatter().setColSpan(r, 0, 5);
		//r++;
		//opzArea = new TextArea();
		//opzArea.setValue(item.getTestoOpzioni());
		//opzArea.setVisibleLines(1);
		//opzArea.setWidth("60em");
		//opzArea.setHeight("12em");
		//opzArea.setEnabled(isAdmin);
		//table.getFlexCellFormatter().setColSpan(r, 0, 5);
		//table.setWidget(r, 0, opzArea);
		//r++;
		
		HorizontalPanel buttonPanel = getButtonPanel();
		table.setWidget(r,0,buttonPanel);
		table.getFlexCellFormatter().setColSpan(r, 0, 6);//Span su 5 colonne
		r++;
		
		previewPanel = new TitlePanel("Anteprima");
		previewPanel.setVisible(false);
		table.setWidget(r, 0, previewPanel);
		table.getFlexCellFormatter().setColSpan(r, 0, 6);//Span su 5 colonne
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
		if (idBolMod.equals(AppConstants.NEW_ITEM_ID)) {
			submitButton.setText("Crea");
		}
		submitButton.setEnabled(isAdmin);
		buttonPanel.add(submitButton);
		return buttonPanel;
	}

	
	
	
	// METODI ASINCRONI
	

	private void loadModelliBollettini() {
		AsyncCallback<ModelliBollettini> callback = new AsyncCallback<ModelliBollettini>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(ModelliBollettini result) {
				item = result;
				drawModelliBollettini();
				WaitSingleton.get().stop();
			}
		};
		
		//look for item with id only if id is defined
		if (idBolMod.intValue() != AppConstants.NEW_ITEM_ID) {
			WaitSingleton.get().start();
			comService.findModelliBollettiniById(idBolMod, callback);
		} else {
			//is new modello
			WaitSingleton.get().start();
			comService.createModelliBollettini(idPeriodico, callback);
		}
	}
	
	private void saveData() throws ValidationException {
		//Conferma
		boolean confirm = Window.confirm("Vuoi veramente modificare le impostazioni del modello di bollettino?");
		if (!confirm) return;
		
		AsyncCallback<Integer> callback = new AsyncCallback<Integer>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(Integer result) {			
				idBolMod = result;
				UiSingleton.get().addInfo(AppConstants.MSG_SAVE_OK);
				WaitSingleton.get().stop();
				loadModelliBollettini();
			}
		};
		//Validazione
		String descr = descrText.getValue();
		if (descr == null) throw new ValidationException("Descrizione mancante");
		if (descr.length()==0) throw new ValidationException("Descrizione mancante");
		String autBol = autBolText.getValue();
		if (autBol == null) throw new ValidationException("Autorizz.bollettino mancante");
		if (autBol.length()==0) throw new ValidationException("Autorizz.bollettino mancante");
		String codiceModello = codiceModelloText.getValue();
		if (codiceModello == null) throw new ValidationException("Codice modello mancante");
		if (codiceModello.length()==0) throw new ValidationException("Codice modello mancante");
		String pathReport = pathReportSelect.getSelectedValueString();
		if (pathReport == null) throw new ValidationException("Path report mancante");
		if (pathReport.length()==0) throw new ValidationException("Path report mancante");
		String logoVerticalPath = logoVerticalPathSelect.getSelectedValueString();
		if (logoVerticalPath == null) throw new ValidationException("Path logo verticale mancante");
		if (logoVerticalPath.length()==0) throw new ValidationException("Path logo verticale mancante");
		String logoSmallPath = logoSmallPathSelect.getSelectedValueString();
		if (logoSmallPath == null) throw new ValidationException("Path logo piccolo mancante");
		if (logoSmallPath.length()==0) throw new ValidationException("Path logo piccolo mancante");
		//Assegnamento
		String idPer = periodiciList.getSelectedValueString();
		item.setIdPeriodicoT(idPer);
		item.setDescr(descr);
		item.setAutorizzazione(autBol);
		item.setCodiceModello(codiceModello);
		item.setLogoVerticalPath(logoVerticalPath);
		item.setLogoSmallPath(logoSmallPath);
		item.setReportFilePath(pathReport);
		item.setTestoBandella(testoArea.getValue());
		//item.setTestoOpzioni(opzArea.getValue());
		//item.setDataModifica(DateUtil.now());
		//item.setUtente(AuthSingleton.get().getUtente());

		WaitSingleton.get().start();
		comService.saveOrUpdateModelliBollettini(item, callback);
	}
	
	
	//Text preview
	
	private void loadTextPreview() {
		AsyncCallback<String> callback = new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(String result) {
				drawTextPreview(result);
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		comService.formatBollettinoText(testoArea.getValue(), TEXT_WIDTH_BOLLETTINO, callback);
	}
	private void drawTextPreview(String text) {
		VerticalPanel holder = new VerticalPanel();
		String[] lines = text.split("\r\n");
		int maxWidth = 0;
		int linesTotal = lines.length;
		for (String line:lines) {
			if (line.length() > maxWidth) maxWidth = line.length();
		}
		String widthLabel = "<b>Larghezza massima: " +maxWidth + "</b> (max "+TEXT_WIDTH_BOLLETTINO+") ";
		if (maxWidth > TEXT_WIDTH_BOLLETTINO) widthLabel += ClientConstants.ICON_WARNING;
		holder.add(new HTML(widthLabel));
		String heightLabel = "<b>Altezza massima: "+linesTotal+ "</b> (max "+TEXT_HEIGHT_BOLLETTINO+") ";
		if (linesTotal > TEXT_HEIGHT_BOLLETTINO) heightLabel += ClientConstants.ICON_WARNING;
		holder.add(new HTML(heightLabel));
		
		InlineHTML html = new InlineHTML("<pre>"+text+"</pre>");
		holder.add(html);
		previewPanel.clear();
		previewPanel.add(holder);
		previewPanel.setVisible(true);
	}
	
	
	//private void loadSupplPreview() {
	//	VerticalPanel holder = new VerticalPanel();
	//	InlineHTML html = new InlineHTML(opzArea.getValue());
	//	holder.add(html);
	//	previewPanel.clear();
	//	previewPanel.add(holder);
	//	previewPanel.setVisible(true);
	//}
}
