package it.giunti.apg.client.widgets.tables;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.frames.EvasioneFascicoloPopUp;
import it.giunti.apg.client.services.FascicoliService;
import it.giunti.apg.client.services.FascicoliServiceAsync;
import it.giunti.apg.client.services.SapService;
import it.giunti.apg.client.services.SapServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.EvasioniFascicoli;
import it.giunti.apg.shared.model.Ruoli;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.InlineHTML;

public class EvasioniFascicoliTable extends PagingTable<EvasioniFascicoli> 
		implements IRefreshable {
	private static final FascicoliServiceAsync fascicoliService = GWT.create(FascicoliService.class);

	private static final int TABLE_ROWS = 200;
	private static final int NOTE_LENGTH = 40;
	
	private IRefreshable parent = null;
	private Date inizioIstanza = null;
	private boolean orderMode = false;
	private boolean isOperator = false;
	private boolean isSuper = false;
	
	private AsyncCallback<List<EvasioniFascicoli>> callback = new AsyncCallback<List<EvasioniFascicoli>>() {
		@Override
		public void onFailure(Throwable caught) {
			setTableRows(new ArrayList<EvasioniFascicoli>());
			//WaitSingleton.get().stop();
		}
		@Override
		public void onSuccess(List<EvasioniFascicoli> result) {
			setTableRows(result);
			//WaitSingleton.get().stop();
		}
	};
	
	public EvasioniFascicoliTable(DataModel<EvasioniFascicoli> model, Date inizioIstanza, Ruoli userRole,
			IRefreshable parent, boolean orderMode) {
		super(model, TABLE_ROWS);
		this.parent = parent;
		this.inizioIstanza = inizioIstanza;
		this.orderMode = orderMode;
		isOperator = (userRole.getId().intValue() >= AppConstants.RUOLO_OPERATOR);
		isSuper = (userRole.getId().intValue() >= AppConstants.RUOLO_SUPER);
		drawPage(0);
	}

	@Override
	public void refresh() {
		drawPage(0);
		if (parent != null) {
			parent.refresh();
		}
	}
	
	@Override
	public void drawPage(int page) {
		clearInnerTable();
		getInnerTable().setHTML(0, 0, ClientConstants.LABEL_LOADING);
		getModel().find(page*TABLE_ROWS,
				TABLE_ROWS,
				callback);
	}
	
	@Override
	protected void addTableRow(int rowNum, EvasioniFascicoli rowObj) {
		final EvasioniFascicoliTable table = this;
		final Integer idIstanza = rowObj.getIdIstanzaAbbonamento();
		final Integer idEf = rowObj.getId();
		String numeroDesc = "";
		if (rowObj.getFascicolo().getOpzione() != null) {
			numeroDesc += rowObj.getFascicolo().getOpzione().getNome()+" ";
		}
		if (rowObj.getFascicolo().getTitoloNumero() != null) {
			numeroDesc += rowObj.getFascicolo().getTitoloNumero();
		}
		// Set the data in the current row
		String linkText = "";
		if (orderMode) {
			linkText += rowObj.getFascicolo().getCodiceMeccanografico();
		} else {
			linkText += numeroDesc;
		}
		if (isOperator && !orderMode ) {
			linkText = "<b>"+linkText+"</b>";
			Anchor fascicoloAnchor = new Anchor(linkText, true);
			fascicoloAnchor.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent arg0) {
					EvasioneFascicoloPopUp popUp = new EvasioneFascicoloPopUp();
					popUp.initByIstanzaFascicolo(idIstanza, inizioIstanza, idEf, table);
				}
			});
			getInnerTable().setWidget(rowNum, 0, fascicoloAnchor);
		} else {
			getInnerTable().setHTML(rowNum, 0, "<b>"+linkText+"</b>");
		}
		//Copertina
		String copertina = "";
		if (orderMode) { 
			copertina += rowObj.getFascicolo().getPeriodico().getNome()+": "+numeroDesc+"&nbsp;";
		} else {
			copertina += rowObj.getFascicolo().getDataCop() + "&nbsp;" +
					ClientConstants.FORMAT_YEAR.format(rowObj.getFascicolo().getDataInizio())+"&nbsp;";
		}
		getInnerTable().setHTML(rowNum, 1, copertina);
		//Creazione
		getInnerTable().setHTML(rowNum, 2, 
				ClientConstants.FORMAT_DAY.format(rowObj.getDataCreazione())+"&nbsp;");
		getInnerTable().setHTML(rowNum, 3, 
				AppConstants.EVASIONE_FAS_DESC.get(rowObj.getIdTipoEvasione()));
		getInnerTable().setHTML(rowNum, 4, rowObj.getCopie()+"&nbsp;");
		//Invio
		String invio = "";
		if (rowObj.getDataInvio() != null) {
			//Inviato massivamente o manualmente
			invio += "spedito "+ClientConstants.FORMAT_DAY.format(rowObj.getDataInvio())+" ";
		}
//		if (rowObj.getDataAnnullamento() != null) {
//			invio += "annullato "+ClientConstants.FORMAT_DAY.format(rowObj.getDataAnnullamento())+" ";
//		}
		if (rowObj.getDataConfermaEvasione() != null) {
			invio += "evaso "+ClientConstants.FORMAT_DAY.format(rowObj.getDataConfermaEvasione())+" ";
		}
		if (rowObj.getOrdiniLogistica() != null) {
			//Ordine via SAP
			if (invio.length() == 0) invio += "in corso ";
			invio += "[ord."+rowObj.getOrdiniLogistica().getNumeroOrdine()+"]";
		}
		getInnerTable().setHTML(rowNum, 5, invio+"&nbsp;");
		//note
		String note = rowObj.getNote();
		if (note != null) {
			if (note.length()>NOTE_LENGTH) {
				note = note.substring(0, NOTE_LENGTH)+"...";
			}
		} else {
			note = "";
		}
		//username
		if (!orderMode) {
			note += " <i>("+rowObj.getIdUtente()+")</i>";
		}
		getInnerTable().setHTML(rowNum, 6, note);
		//delete
		if ((rowObj.getDataInvio() == null && rowObj.getDataOrdine() == null && isOperator) || isSuper ) {
			InlineHTML trashImg = new InlineHTML(ClientConstants.ICON_DELETE);
			trashImg.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent arg0) {
					confirmAndDelete(idIstanza, idEf);
				}
			});
			getInnerTable().setWidget(rowNum, 7, trashImg);
		}
	}
	
	@Override
	protected void addHeader() {
		// Set the data in the current row
		getInnerTable().setHTML(0, 0, "Fascicolo");
		getInnerTable().setHTML(0, 1, "Copertina");
		getInnerTable().setHTML(0, 2, "Creazione");
		getInnerTable().setHTML(0, 3, "Tipo");
		getInnerTable().setHTML(0, 4, "Copie");
		getInnerTable().setHTML(0, 5, "Invio");
		getInnerTable().setHTML(0, 6, "Note");
	}
	
	@Override
	protected void onEmptyResult() {}
	
	private void confirmAndDelete(Integer idIstanza, Integer idEvasioneFascicolo) {
		boolean confirm = Window.confirm("Vuoi veramente eliminare l'invio del fascicolo?");
		if (confirm) {
			delete(idIstanza, idEvasioneFascicolo);
		}
	}
	
	public void delete(Integer idIstanza, Integer idEvasioneFascicolo) {
		//WaitSingleton.get().start();
		fascicoliService.deleteEvasioneFascicolo(idIstanza, idEvasioneFascicolo, callback);
	}
	
	
	
	//Inner classes
	
	
	
	public static class EvasioniFascicoliByIstanzaModel implements DataModel<EvasioniFascicoli> {
		private Integer idIstanza = null;
		
		public EvasioniFascicoliByIstanzaModel(Integer idIstanza) {
			this.idIstanza=idIstanza;
		}
		
		@Override
		public void find(int offset, int pageSize,
				AsyncCallback<List<EvasioniFascicoli>> callback) {
			fascicoliService.findEvasioniFascicoliByIstanza(idIstanza, callback);
		}
	}
	
	public static class EvasioniFascicoliByOrdineModel implements DataModel<EvasioniFascicoli> {
		private final SapServiceAsync sapService = GWT.create(SapService.class);
		private String numOrdine = null;
		
		public EvasioniFascicoliByOrdineModel(String numOrdine) {
			this.numOrdine=numOrdine;
		}
		
		@Override
		public void find(int offset, int pageSize,
				AsyncCallback<List<EvasioniFascicoli>> callback) {
			sapService.findEvasioniFascicoliByOrdine(numOrdine, callback);
		}
	}
}
