package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.UriManager;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.services.AnagraficheService;
import it.giunti.apg.client.services.AnagraficheServiceAsync;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.client.widgets.select.PeriodiciSelect;
import it.giunti.apg.client.widgets.select.TipiAbbSelect;
import it.giunti.apg.client.widgets.tables.AnagraficheTable;
import it.giunti.apg.client.widgets.tables.DataModel;
import it.giunti.apg.core.DateUtil;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.Localita;
import it.giunti.apg.shared.model.Utenti;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;

public class AnagraficheFindFrame extends FramePanel implements IAuthenticatedWidget {
	
	private static final String BOX_WIDTH = "20em";
	
	private static final String PARAM_CODICE_ANAGRAFICA = "codanag";
	private static final String PARAM_RAGSOC = "ragsoc";
	private static final String PARAM_NOME = "nome";
	private static final String PARAM_PRESSO = "co";
	private static final String PARAM_INDIRIZZO = "ind";
	private static final String PARAM_CAP = "cap";
	private static final String PARAM_LOCALITA = "loc";
	private static final String PARAM_PROV = "pv";
	private static final String PARAM_EMAIL = "mail";
	private static final String PARAM_COD_FISC_IVA = "cfiva";
	private static final String PARAM_PERIODICO = "periodico";
	private static final String PARAM_TIPO_ABB = "tipoabb";
	
	private UriParameters params = null;
	private final AnagraficheServiceAsync anagraficheService = GWT.create(AnagraficheService.class);
	private SimplePanel contentPanel = null;

	private TextBox codAnagTxt = null;
	private TextBox ragSocTxt = null;
	private TextBox nomeTxt = null;
	private TextBox pressoTxt = null;
	private TextBox indTxt = null;
	private SuggestBox locSuggest = null;
	private MultiWordSuggestOracle locOracle = null;
	private SuggestBox.DefaultSuggestionDisplay locSuggDisplay = null;
	private TextBox capTxt = null;
	private TextBox pvTxt = null;
	private TextBox mailTxt = null;
	private TextBox cfivaTxt = null;
	private PeriodiciSelect periodiciList = null;
	private TipiAbbSelect tipoAbbList = null;
	private Utenti utente = null;
	
	public AnagraficheFindFrame(UriParameters params) {
		super();
		if (params != null) {
			this.params = params;
		} else {
			this.params = new UriParameters();
		}
		contentPanel = new SimplePanel();
		this.add(contentPanel, "Ricerca anagrafiche");
		this.setWidth("100%");
		AuthSingleton.get().queueForAuthentication(this);
	}
	
	@Override
	public void onSuccessfulAuthentication(Utenti utente) {
		this.utente = utente;
		init(utente);
	}
	
	private void init(Utenti utente) {
		// Editing rights
		int ruolo = utente.getRuolo().getId();
		// UI
		if (ruolo >= AppConstants.RUOLO_OPERATOR) {
			draw();
		}
	}
	
	private void draw() {
		// update search result
		String codAnag = params.getValue(PARAM_CODICE_ANAGRAFICA);
		String ragSoc = params.getValue(PARAM_RAGSOC);
		String nome = params.getValue(PARAM_NOME);
		String presso = params.getValue(PARAM_PRESSO);
		String indirizzo = params.getValue(PARAM_INDIRIZZO);
		String cap = params.getValue(PARAM_CAP);
		String loc = params.getValue(PARAM_LOCALITA);
		String prov = params.getValue(PARAM_PROV);
		String email = params.getValue(PARAM_EMAIL);
		String cfiva = params.getValue(PARAM_COD_FISC_IVA);
		Integer idPeriodico = ValueUtil.stoi(params.getValue(PARAM_PERIODICO));
		String tipoAbb = params.getValue(PARAM_TIPO_ABB);
		if ( (codAnag != null) || (ragSoc != null) || (nome != null) || (presso != null) || (indirizzo != null)
				|| (cap != null) || (loc != null) || (prov != null) || (email != null) || (cfiva != null)
				|| (idPeriodico != null) || (tipoAbb != null)) {
			//Mostra i risultati
			DataModel<Anagrafiche> model = new AnagraficheTable.FindByPropertiesModel(
					codAnag, ragSoc, nome, presso, indirizzo, cap, loc, prov, email, cfiva, idPeriodico, tipoAbb);
			AnagraficheTable resultTable = new AnagraficheTable(model, true, null);
			contentPanel.add(resultTable);
		} else {
			//Mostra il form di ricerca
			RicercaAnagraficheForm form = new RicercaAnagraficheForm();
			contentPanel.add(form);
		}
	}
	
	
	
	/* Inner classes */
	
	
	
	private class RicercaAnagraficheForm extends SimplePanel {
		
		public RicercaAnagraficheForm() {
			FlexTable table = new FlexTable();
			add(table);
			int r = 0;
			
			//Codice cliente
			table.setHTML(r, 0, "Codice cliente");
			codAnagTxt = new TextBox();
			codAnagTxt.setWidth(BOX_WIDTH);
			codAnagTxt.setValue(params.getValue(PARAM_CODICE_ANAGRAFICA));
			table.setWidget(r, 1, codAnagTxt);
			r++;
			
			//Cognome
			table.setHTML(r, 0, "Cognome/Rag.soc.");
			ragSocTxt = new TextBox();
			ragSocTxt.setWidth(BOX_WIDTH);
			ragSocTxt.setValue(params.getValue(PARAM_RAGSOC));
			table.setWidget(r, 1, ragSocTxt);
			//Nome
			table.setHTML(r, 3, "Nome");
			nomeTxt = new TextBox();
			nomeTxt.setWidth(BOX_WIDTH);
			nomeTxt.setValue(params.getValue(PARAM_NOME));
			table.setWidget(r, 4, nomeTxt);
			r++;
			
			//Presso
			table.setHTML(r, 0, "Presso");
			pressoTxt = new TextBox();
			pressoTxt.setWidth(BOX_WIDTH);
			pressoTxt.setValue(params.getValue(PARAM_PRESSO));
			table.setWidget(r, 1, pressoTxt);
			//Indirizzo
			table.setHTML(r, 3, "Indirizzo");
			indTxt = new TextBox();
			indTxt.setWidth(BOX_WIDTH);
			indTxt.setValue(params.getValue(PARAM_INDIRIZZO));
			table.setWidget(r, 4, indTxt);
			r++;
			
			//Località
			table.setHTML(r, 0, "Localit&agrave;");
			locOracle = new MultiWordSuggestOracle();
			locSuggDisplay = new SuggestBox.DefaultSuggestionDisplay();
			locSuggest = new SuggestBox(locOracle, new TextBox(), locSuggDisplay);
			locSuggest.setWidth(BOX_WIDTH);
			locSuggest.setValue(params.getValue(PARAM_LOCALITA));
			locSuggest.addKeyUpHandler(new KeyUpHandler() {
				@Override
				public void onKeyUp(KeyUpEvent arg0) {
					loadLocalitaSuggestions(locSuggest.getValue());
				}
			});
			table.setWidget(r, 1, locSuggest);
			//Cap
			table.setHTML(r, 3, "Prov.");
			FlowPanel capProvPanel = new FlowPanel();
			pvTxt = new TextBox();
			pvTxt.setMaxLength(4);
			pvTxt.setWidth("3em");
			pvTxt.setValue(params.getValue(PARAM_PROV));
			capProvPanel.add(pvTxt);
			//Provincia
			capProvPanel.add(new InlineHTML("&nbsp;&nbsp;CAP&nbsp;"));
			capTxt = new TextBox();
			capTxt.setWidth("5em");
			capTxt.setMaxLength(6);
			capTxt.setValue(params.getValue(PARAM_CAP));
			capProvPanel.add(capTxt);
			table.setWidget(r, 4, capProvPanel);
			r++;
			
			//email
			table.setHTML(r, 0, "Email");
			mailTxt = new TextBox();
			mailTxt.setWidth(BOX_WIDTH);
			mailTxt.setValue(params.getValue(PARAM_EMAIL));
			table.setWidget(r, 1, mailTxt);
			//email
			table.setHTML(r, 3, "Cod.Fisc./P.Iva");
			cfivaTxt = new TextBox();
			cfivaTxt.setWidth(BOX_WIDTH);
			cfivaTxt.setValue(params.getValue(PARAM_COD_FISC_IVA));
			table.setWidget(r, 4, cfivaTxt);
			r++;
			
			// Periodico
			table.setHTML(r, 0, "Periodico");
			periodiciList = new PeriodiciSelect(0, DateUtil.now(), true, true, utente);
			periodiciList.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					onPeriodicoChange();
				}
			});
			table.setWidget(r, 1, periodiciList);
			//tipo abbonamento
			table.setHTML(r,3, "Tipo abbonamento");
			tipoAbbList = new TipiAbbSelect(null, 0,
						null, true, false);
			table.setWidget(r, 4, tipoAbbList);
			r++;
			
			//bottone
			table.setWidget(r, 0, new Button("Cerca", new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					submit();
				}
			}));
			//suggerimento
			table.setHTML(r, 3, "<i>Usa * o % per cercare nomi incompleti</i>");
			table.getFlexCellFormatter().setColSpan(r, 3, 2);
		}
		
		public void submit() {
			UriParameters params = new UriParameters();
			params.add(PARAM_CODICE_ANAGRAFICA, codAnagTxt.getValue());
			params.add(PARAM_RAGSOC, ragSocTxt.getValue());
			params.add(PARAM_NOME, nomeTxt.getValue());
			params.add(PARAM_PRESSO, pressoTxt.getValue());
			params.add(PARAM_INDIRIZZO, indTxt.getValue());
			params.add(PARAM_CAP, capTxt.getValue());
			params.add(PARAM_LOCALITA, locSuggest.getValue());
			params.add(PARAM_PROV, pvTxt.getValue());
			params.add(PARAM_EMAIL, mailTxt.getValue());
			params.add(PARAM_COD_FISC_IVA, cfivaTxt.getValue());
			String periodico = periodiciList.getSelectedValueString();
			if (!AppConstants.SELECT_EMPTY_LABEL.equals(periodico)) {
				params.add(PARAM_PERIODICO, periodico);
			}
			String tipoAbb = tipoAbbList.getSelectedValueString();
			if (!AppConstants.SELECT_EMPTY_LABEL.equals(tipoAbb)) {
				params.add(PARAM_TIPO_ABB, tipoAbb);
			}
			params.triggerUri(UriManager.ANAGRAFICHE_FIND);
		}
	}
	
	private void onPeriodicoChange() {
		Integer idPeriodico = periodiciList.getSelectedValueInt();;
		tipoAbbList.reload(null, idPeriodico,
				DateUtil.now(), false);
	}

	
	
	//Async methods
	
	
	
	private void loadLocalitaSuggestions(String locPrefix) {
		if (locPrefix == null) return;
		if (locPrefix.length() < 3) {
			locOracle.clear();
			locSuggDisplay.hideSuggestions();
		} else {
			AsyncCallback<List<Localita>> callback = new AsyncCallback<List<Localita>>() {
				@Override
				public void onFailure(Throwable caught) {
					UiSingleton.get().addInfo(caught.getLocalizedMessage());
				}
				@Override
				public void onSuccess(List<Localita> result) {
					locOracle.clear();
					List<String> list = new ArrayList<String>();
					for (Localita l:result) list.add(l.getNome());
					locOracle.addAll(list);
					if (!locSuggDisplay.isSuggestionListShowing()) {
						locSuggest.showSuggestionList();
					}
				}
			};
			anagraficheService.findLocalitaSuggestions(locPrefix, callback);
		}
	}
}
