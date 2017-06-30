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
import it.giunti.apg.shared.model.Avvisi;
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

public class MaintenancePopUp extends PopupPanel {

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
	
	public MaintenancePopUp(Avvisi avviso) {
		super(false);
		draw();
	}
	
	private void draw() {
		VerticalPanel panel = new VerticalPanel();
		//Titolo
		titleLabel.setStyleName("frame-title");
		titleLabel.setHTML("Avviso importante");
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
		faPanel = new FatturaActionPanel(true, isEditor, parent);
		buttonPanel.add(faPanel);
		panel.add(buttonPanel);
		this.add(panel);
		this.show();
	}
	
	private void close() {
		this.hide();
	}
	
}
