package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.AnagraficheService;
import it.giunti.apg.client.services.AnagraficheServiceAsync;
import it.giunti.apg.client.services.PagamentiService;
import it.giunti.apg.client.services.PagamentiServiceAsync;
import it.giunti.apg.client.widgets.FatturaActionPanel;
import it.giunti.apg.client.widgets.tables.DataModel;
import it.giunti.apg.client.widgets.tables.FattureArticoliTable;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.Fatture;
import it.giunti.apg.shared.model.FattureArticoli;
import it.giunti.apg.shared.model.Indirizzi;
import it.giunti.apg.shared.model.Utenti;

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

public class FatturaPopUp extends PopupPanel implements IRefreshable, IAuthenticatedWidget {

	private final AnagraficheServiceAsync anagraficheService = GWT.create(AnagraficheService.class);
	private final PagamentiServiceAsync pagamentiService = GWT.create(PagamentiService.class);
	
	private Utenti utente = null;
	private Integer idFattura = null;
	private IRefreshable parent = null;
	
	private HTML titleLabel = new HTML();
	private HTML societaLabel = new HTML();
	private HTML anagraficaLabel = new HTML();
	private HTML datiFatturaLabel = new HTML();
	private HTML totImpLabel = new HTML();
	private HTML totIvaLabel = new HTML();
	private HTML totFinaleLabel = new HTML();
	private FatturaActionPanel faPanel = null;
	
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
	
	
	
	
	
	//Async Methods
	
	
	
	
	private void load() {
		final AsyncCallback<Anagrafiche> anagCallback = new AsyncCallback<Anagrafiche>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(Anagrafiche result) {
				WaitSingleton.get().stop();
				Indirizzi ind = result.getIndirizzoPrincipale();
				String label = ind.getCognomeRagioneSociale()+" ";
				if (ind.getNome() != null) label += ind.getNome()+" ";
				label += "<br/>"+ind.getIndirizzo()+"<br>";
				if (ind.getCap() != null) label += ind.getCap()+" ";
				label += ind.getLocalita()+" ";
				if (ind.getProvincia() != null) label += ind.getProvincia();
				if (!ind.getNazione().getId().equals(AppConstants.DEFAULT_ID_NAZIONE_ITALIA)) {
					label += "<br>"+ind.getNazione().getNomeNazione();
				}
				anagraficaLabel.setHTML(label);
				refresh();
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
				
				if (result.getIdTipoDocumento().equalsIgnoreCase(AppConstants.DOCUMENTO_FATTURA)) {
					titleLabel.setHTML("Fattura");
				} else {
					titleLabel.setHTML("Nota di credito");
				}
				societaLabel.setHTML("Societ&agrave;: <b>"+result.getIdSocieta()+"</b>");
				datiFatturaLabel.setHTML("Fattura: <b>"+result.getNumeroFattura()+"</b> "+
						"Data: "+ClientConstants.FORMAT_DAY.format(result.getDataFattura()));
				totImpLabel.setHTML("Imponibile <b>&euro;"+
						ClientConstants.FORMAT_CURRENCY.format(result.getTotaleImponibile())+"</b>");
				totIvaLabel.setHTML("Totale IVA <b>&euro;"+
						ClientConstants.FORMAT_CURRENCY.format(result.getTotaleIva())+"</b>");
				totFinaleLabel.setHTML("TOTALE documento <b>&euro;"+
						ClientConstants.FORMAT_CURRENCY.format(result.getTotaleFinale())+"</b>");
				faPanel.draw(result);
				WaitSingleton.get().start();
				anagraficheService.findById(result.getIdAnagrafica(), anagCallback);
			}
		};
		WaitSingleton.get().start();
		pagamentiService.findFatturaById(idFattura, fattCallback);
	}
		
}
