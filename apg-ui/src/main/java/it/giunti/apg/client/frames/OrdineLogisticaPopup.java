package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.widgets.DestinatarioPanel;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.client.widgets.tables.DataModel;
import it.giunti.apg.client.widgets.tables.EvasioniArticoliTable;
import it.giunti.apg.client.widgets.tables.EvasioniFascicoliTable;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.EvasioniArticoli;
import it.giunti.apg.shared.model.EvasioniFascicoli;
import it.giunti.apg.shared.model.OrdiniLogistica;
import it.giunti.apg.shared.model.Utenti;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class OrdineLogisticaPopup extends PopupPanel implements IAuthenticatedWidget, IRefreshable {

	private String POPUP_WIDTH = "650px";
	private OrdiniLogistica ol = null;
	private boolean isOperator = false;
	
	
	public OrdineLogisticaPopup(OrdiniLogistica ol) {
		super(false);
		this.ol = ol;
		this.setWidth(POPUP_WIDTH);
		AuthSingleton.get().queueForAuthentication(this);
	}
		
	@Override
	public void onSuccessfulAuthentication(Utenti utente) {
		init(utente);
	}
	
	private void init(Utenti utente) {
		int ruolo = utente.getRuolo().getId();
		// Editing rights
		isOperator = (ruolo >= AppConstants.RUOLO_OPERATOR);
		//isEditor = (ruolo >= AppConstants.RUOLO_EDITOR);
		//isSuper = (ruolo >= AppConstants.RUOLO_SUPER);
		//UI
		if (isOperator) {
			this.setModal(true);
			this.setGlassEnabled(true);
			draw(utente);
		}
	}
	
	private void draw(Utenti utente) {
		FramePanel frame = new FramePanel();
		this.add(frame);
		VerticalPanel holder = new VerticalPanel();
		frame.add(holder, "Ordine "+ol.getNumeroOrdine());
		//Destinatario
		DestinatarioPanel destPanel = new DestinatarioPanel(ol.getIdAnagrafica(), false);
		holder.add(destPanel);
		
		//Tabella Fascicoli
		HTML efTitle = new HTML("Fascicoli:");
		efTitle.setStyleName("frame-title");
		holder.add(efTitle);
		DataModel<EvasioniFascicoli> efModel =
				new EvasioniFascicoliTable.EvasioniFascicoliByOrdineModel(ol.getNumeroOrdine());
		EvasioniFascicoliTable efTable = new EvasioniFascicoliTable(efModel, null, utente.getRuolo(), this, true);
		holder.add(efTable);
		//Tabella Articoli
		HTML edTitle = new HTML("Articoli:");
		edTitle.setStyleName("frame-title");
		holder.add(edTitle);
		DataModel<EvasioniArticoli> edModel =
				new EvasioniArticoliTable.EvasioniArticoliByOrdineModel(ol.getNumeroOrdine());
		EvasioniArticoliTable edTable = new EvasioniArticoliTable(edModel, utente.getRuolo(), this, true);
		holder.add(edTable);
		
		// Bottone Chiudi
		Button cancelButton = new Button("Chiudi", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				close();
			}
		});
		holder.add(cancelButton);
		
		this.center();
		this.show();
	}
	
	private void close() {
		this.hide();
	}

	@Override
	public void refresh() {
		
	}
	

}
