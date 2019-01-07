package it.giunti.apg.client.frames;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.AnagraficheService;
import it.giunti.apg.client.services.AnagraficheServiceAsync;
import it.giunti.apg.client.services.LookupService;
import it.giunti.apg.client.services.LookupServiceAsync;
import it.giunti.apg.client.services.PagamentiService;
import it.giunti.apg.client.services.PagamentiServiceAsync;
import it.giunti.apg.client.widgets.FatturaActionPanel;
import it.giunti.apg.client.widgets.tables.DataModel;
import it.giunti.apg.client.widgets.tables.FattureArticoliTable;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.IndirizziUtil;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.Fatture;
import it.giunti.apg.shared.model.FattureArticoli;
import it.giunti.apg.shared.model.Indirizzi;
import it.giunti.apg.shared.model.Nazioni;
import it.giunti.apg.shared.model.Societa;
import it.giunti.apg.shared.model.Utenti;

public class FatturaPopUp extends PopupPanel implements IRefreshable, IAuthenticatedWidget {

	private final AnagraficheServiceAsync anagraficheService = GWT.create(AnagraficheService.class);
	private final PagamentiServiceAsync pagamentiService = GWT.create(PagamentiService.class);
	private final LookupServiceAsync lookupService = GWT.create(LookupService.class);
	
	private Utenti utente = null;
	private Integer idFattura = null;
	private IRefreshable parent = null;
	
	private HTML titleLabel = new HTML();
	private HTML societaLabel = new HTML();
	private HTML anagraficaLabel = new HTML();
	private HTML datiFiscaliLabel = new HTML();
	private HTML datiFatturaLabel = new HTML();
	private HTML totImpLabel = new HTML();
	private HTML totIvaLabel = new HTML();
	private HTML totFinaleLabel = new HTML();
	private FatturaActionPanel faPanel = null;
	
	private Fatture fattura = null;
	private Anagrafiche backupAnag = null;
	private Societa societa = null;
	
	public FatturaPopUp(Integer idFattura, IRefreshable parent) {
		super(false);
		this.idFattura = idFattura;
		this.parent = parent;
		AuthSingleton.get().queueForAuthentication(this);
	}
	
	
	@Override
	public void onSuccessfulAuthentication(Utenti utente) {
		init(utente);
	}
	
	private void init(Utenti utente) {
		this.utente = utente;
		int ruolo = utente.getRuolo().getId();
		//UI
		if (ruolo >= AppConstants.RUOLO_OPERATOR) {
			this.setModal(true);
			this.setGlassEnabled(true);
			boolean isEditor = ruolo >= AppConstants.RUOLO_EDITOR;
			draw(isEditor);
			load();
		}
	}
	
	private void draw(boolean isEditor) {
		VerticalPanel panel = new VerticalPanel();
		//Titolo
		titleLabel.setStyleName("frame-title");
		panel.add(titleLabel);
		//Intestazioni
		panel.add(societaLabel);
		panel.add(anagraficaLabel);
		anagraficaLabel.addStyleName("align-right");
		//Dati fiscali
		panel.add(datiFiscaliLabel);
		datiFiscaliLabel.addStyleName("align-right");
		//Dati fattura
		panel.add(datiFatturaLabel);
		//Tabella articoli
		DataModel<FattureArticoli> articoliModel = new FattureArticoliTable.FattureArticoliByFatturaModel(idFattura);
		FattureArticoliTable faTable = new FattureArticoliTable(articoliModel, this);
		panel.add(faTable);
		//Totali
		panel.add(new InlineHTML("<hr/>"));
		panel.add(totImpLabel);
		totImpLabel.setStyleName("align-right");
		panel.add(totIvaLabel);
		totIvaLabel.setStyleName("align-right");
		panel.add(new InlineHTML("<hr/>"));
		panel.add(totFinaleLabel);
		totFinaleLabel.setStyleName("align-right");
		panel.add(new InlineHTML("&nbsp;"));
		
		HorizontalPanel buttonPanel = new HorizontalPanel();
		Button okButton = new Button("&nbsp;Chiudi&nbsp;", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				close();
			}
		});
		buttonPanel.add(okButton);
		buttonPanel.add(new InlineHTML("&nbsp;&nbsp;&nbsp;&nbsp;"));
		faPanel = new FatturaActionPanel(utente, parent);
		buttonPanel.add(faPanel);
		panel.add(buttonPanel);
		this.add(panel);
		refresh();
		this.show();
	}
	
	@Override
	public void refresh() {
		this.center();
	}
	
	private void close() {
		this.hide();
	}
	
	private void fillLabels() {
		Indirizzi ind = new Indirizzi();
		if (backupAnag != null) {
			ind = backupAnag.getIndirizzoPrincipale();
			if (IndirizziUtil.isFilledUp(backupAnag.getIndirizzoFatturazione())) {
				ind = backupAnag.getIndirizzoFatturazione();
			}
		}
		String ragSoc = null;
		String nome = null;
		String indirizzo = null;
		String cap = null;
		String localita = null;
		String idProvincia = null;
		Nazioni nazione = null;
		String codFisc = null;
		String partIva = null;
		if (fattura.getNazione() != null) {
			ragSoc = fattura.getCognomeRagioneSociale();
			nome = fattura.getNome();
			indirizzo = fattura.getIndirizzo();
			cap = fattura.getCap();
			localita = fattura.getLocalita();
			idProvincia = fattura.getIdProvincia();
			nazione = fattura.getNazione();
			codFisc = fattura.getCodiceFiscale();
			partIva = fattura.getPartitaIva();
		} else {
			ragSoc = ind.getCognomeRagioneSociale();
			nome = ind.getNome();
			indirizzo = ind.getIndirizzo();
			cap = ind.getCap();
			localita = ind.getLocalita();
			idProvincia = ind.getProvincia();
			nazione = ind.getNazione();
			codFisc = backupAnag.getCodiceFiscale();
			partIva = backupAnag.getPartitaIva();
		}
		// Societa
		if (fattura.getIdTipoDocumento().equalsIgnoreCase(AppConstants.DOCUMENTO_FATTURA)) {
			titleLabel.setHTML("Fattura");
		} else {
			titleLabel.setHTML("Nota di credito");
		}
		//String socTesto = societa.getTestoFattura1();
		//socTesto = socTesto.replaceAll("\\n", "<br/>");
		societaLabel.setHTML("Società:<br/><b>"+societa.getNome()+"</b>");
		// Anagrafica
		String label = ragSoc+" ";
		if (nome != null) label += nome+" ";
		label += "<br/>"+indirizzo+"<br>";
		if (cap != null) label += cap+" ";
		label += localita+" ";
		if (idProvincia != null) {
			if (idProvincia.length() > 0) label += idProvincia;
		}
		if (!nazione.getId().equals(AppConstants.DEFAULT_ID_NAZIONE_ITALIA)) {
			label += "<br>"+nazione.getNomeNazione();
		}
		anagraficaLabel.setHTML(label);
		// Dati fiscali
		String datiFiscali = "";
		if (codFisc != null) {
			if (codFisc.length() > 0) datiFiscali += "C.F. <b>"+codFisc+"</b> ";
		}
		if (partIva != null) {
			if (partIva.length() > 0) datiFiscali += "P.I. <b>"+partIva+"</b> ";
		}
		datiFiscaliLabel.setHTML(datiFiscali);
		// Dati fattura
		datiFatturaLabel.setHTML("Fattura: <b>"+fattura.getNumeroFattura()+"</b> "+
				"Data: "+ClientConstants.FORMAT_DAY.format(fattura.getDataFattura()));
		// Totali
		totImpLabel.setHTML("Imponibile <b>&euro;"+
				ClientConstants.FORMAT_CURRENCY.format(fattura.getTotaleImponibile())+"</b>");
		totIvaLabel.setHTML("Totale IVA <b>&euro;"+
				ClientConstants.FORMAT_CURRENCY.format(fattura.getTotaleIva())+"</b>");
		totFinaleLabel.setHTML("TOTALE documento <b>&euro;"+
				ClientConstants.FORMAT_CURRENCY.format(fattura.getTotaleFinale())+"</b>");
		faPanel.draw(fattura);
	}
	
	
	
	
	
	//Async Methods
	
	
	
	
	private void load() {
		final AsyncCallback<Societa> societaCallback = new AsyncCallback<Societa>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(Societa result) {
				WaitSingleton.get().stop();
				societa = result;
				fillLabels();
				refresh();
			}
		};
		final AsyncCallback<Anagrafiche> anagCallback = new AsyncCallback<Anagrafiche>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(Anagrafiche result) {
				WaitSingleton.get().stop();
				backupAnag = result;
				WaitSingleton.get().start();
				lookupService.findSocietaById(fattura.getIdSocieta(), societaCallback);
			}
		};
		final AsyncCallback<Fatture> fattCallback = new AsyncCallback<Fatture>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(Fatture result) {
				WaitSingleton.get().stop();
				fattura = result;
				if (fattura.getNazione() != null) {
					//Anagrafica in fattura is filled up => load società and end
					WaitSingleton.get().start();
					lookupService.findSocietaById(fattura.getIdSocieta(), societaCallback);
				} else {
					//Anagrafica in fattura is NOT filled up => load anagrafica
					WaitSingleton.get().start();
					anagraficheService.findById(result.getIdAnagrafica(), anagCallback);
				}
			}
		};
		WaitSingleton.get().start();
		pagamentiService.findFatturaById(idFattura, fattCallback);
	}
		
}
