package it.giunti.apg.client.widgets.tables;

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

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.services.MaterialiService;
import it.giunti.apg.client.services.MaterialiServiceAsync;
import it.giunti.apg.client.services.SapService;
import it.giunti.apg.client.services.SapServiceAsync;
import it.giunti.apg.client.widgets.AnagraficaLink;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.MaterialiSpedizione;
import it.giunti.apg.shared.model.Ruoli;

public class MaterialiSpedizioneTable extends PagingTable<MaterialiSpedizione> 
		implements IRefreshable {
	private static final MaterialiServiceAsync matService = GWT.create(MaterialiService.class);

	private static final int TABLE_ROWS = 200;
	private static final int NOTE_LENGTH = 40;
	
	private IRefreshable parent = null;
	private Date inizioIstanza = null;
	private boolean isOperator = false;
	private boolean isSuper = false;
	
	private AsyncCallback<List<MaterialiSpedizione>> callback = new AsyncCallback<List<MaterialiSpedizione>>() {
		@Override
		public void onFailure(Throwable caught) {
			setTableRows(new ArrayList<MaterialiSpedizione>());
			//WaitSingleton.get().stop();
		}
		@Override
		public void onSuccess(List<MaterialiSpedizione> result) {
			setTableRows(result);
			//WaitSingleton.get().stop();
		}
	};
	
	public MaterialiSpedizioneTable(DataModel<MaterialiSpedizione> model, Date inizioIstanza, Ruoli userRole,
			IRefreshable parent) {
		super(model, TABLE_ROWS);
		this.parent = parent;
		this.inizioIstanza = inizioIstanza;
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
	protected void addTableRow(int rowNum, MaterialiSpedizione rowObj) {
		final MaterialiSpedizioneTable table = this;
		final Integer idAbbonamento = rowObj.getIdAbbonamento();
		final Integer idMs = rowObj.getId();
		// Materiale
		String numeroDesc = "";
		if (rowObj.getMateriale().getTitolo() != null) {
			numeroDesc += rowObj.getMateriale().getTitolo();
		}
		// Set the data in the current row
		String linkText = rowObj.getMateriale().getCodiceMeccanografico();
		if (isOperator) {
			linkText = "<b>"+linkText+"</b>";
			Anchor matSpedAnchor = new Anchor(linkText, true);
			matSpedAnchor.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent arg0) {
					MaterialiSpedizionePopUp popUp = new MaterialiSpedizionePopUp();
					popUp.initByIstanzaFascicolo(idAbbonamento, inizioIstanza, idMs, table);
				}
			});
			getInnerTable().setWidget(rowNum, 0, matSpedAnchor);
		} else {
			getInnerTable().setHTML(rowNum, 0, "<b>"+linkText+"</b>");
		}
		//Descrizione
		String desc = rowObj.getMateriale().getTitolo()+" - "+rowObj.getMateriale().getSottotitolo() + "&nbsp;";
		getInnerTable().setHTML(rowNum, 1, desc);
		//Copie
		getInnerTable().setHTML(rowNum, 2, rowObj.getCopie()+"&nbsp;");
		//Creazione
		getInnerTable().setHTML(rowNum, 3, ClientConstants.FORMAT_DAY.format(rowObj.getDataCreazione())+"&nbsp;");
		//Invio
		String invio = "";
		if (rowObj.getDataInvio() != null) {
			//Inviato massivamente o manualmente
			invio += "invio "+ClientConstants.FORMAT_DAY.format(rowObj.getDataInvio())+" ";
		}
		if (rowObj.getDataConfermaEvasione() != null) {
			invio += "evasione SAP "+ClientConstants.FORMAT_DAY.format(rowObj.getDataConfermaEvasione())+" ";
		}
		if (rowObj.getOrdineLogistica() != null) {
			//Ordine via SAP
			if (invio.length() == 0) invio += "in corso ";
			invio += "[ord."+rowObj.getOrdineLogistica().getNumeroOrdine()+"]";
		}
		getInnerTable().setHTML(rowNum, 4, invio+"&nbsp;");
		//Destinatario
		AnagraficaLink anaLink = new AnagraficaLink(rowObj.getIdAnagrafica(), false);
		getInnerTable().setWidget(rowNum, 5, anaLink);
		//note
		String note = rowObj.getNote();
		if (note != null) {
			if (note.length()>NOTE_LENGTH) {
				note = note.substring(0, NOTE_LENGTH)+"...";
			}
		} else {
			note = "";
		}
		getInnerTable().setHTML(rowNum, 6, note);
		//delete
		if ((rowObj.getDataInvio() == null && rowObj.getDataOrdine() == null && isOperator) || isSuper ) {
			InlineHTML trashImg = new InlineHTML(ClientConstants.ICON_DELETE);
			trashImg.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent arg0) {
					confirmAndDelete(idMs);
				}
			});
			getInnerTable().setWidget(rowNum, 7, trashImg);
		}
	}
	
	@Override
	protected void addHeader() {
		// Set the data in the current row
		getInnerTable().setHTML(0, 0, "Materiale");
		getInnerTable().setHTML(0, 1, "Descrizione");
		getInnerTable().setHTML(0, 2, "Copie");
		getInnerTable().setHTML(0, 3, "Creazione");
		getInnerTable().setHTML(0, 4, "Invio");
		getInnerTable().setHTML(0, 5, "Destinatario");
		getInnerTable().setHTML(0, 6, "Note");
	}
	
	@Override
	protected void onEmptyResult() {}
	
	private void confirmAndDelete(Integer idMatSped) {
		boolean confirm = Window.confirm("Vuoi veramente eliminare questa riga?");
		if (confirm) {
			delete(idMatSped);
		}
	}
	
	public void delete(Integer idMatSped) {
		AsyncCallback<Boolean> deleteCallback = new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				refresh();
			}
			@Override
			public void onSuccess(Boolean result) {
				refresh();
			}
		};
		matService.deleteMaterialiSpedizione(idMatSped, deleteCallback);
	}
	
	
	
	//Inner classes
	
	
	
	public static class MaterialiSpedizioneByIstanzaModel implements DataModel<MaterialiSpedizione> {
		private Integer idIstanza = null;
		
		public MaterialiSpedizioneByIstanzaModel(Integer idIstanza) {
			this.idIstanza=idIstanza;
		}
		
		@Override
		public void find(int offset, int pageSize,
				AsyncCallback<List<MaterialiSpedizione>> callback) {
			matService.findMaterialiSpedizioneByIstanza(idIstanza, callback);
		}
	}
	
	public static class MaterialiSpedizioneByOrdineModel implements DataModel<MaterialiSpedizione> {
		private final SapServiceAsync sapService = GWT.create(SapService.class);
		private String numOrdine = null;
		
		public MaterialiSpedizioneByOrdineModel(String numOrdine) {
			this.numOrdine=numOrdine;
		}
		
		@Override
		public void find(int offset, int pageSize,
				AsyncCallback<List<MaterialiSpedizione>> callback) {
			sapService.findMaterialiSpedizioneByOrdine(numOrdine, callback);
		}
	}
}
