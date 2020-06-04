package it.giunti.apg.client.widgets.tables;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.frames.EvasioneArticoloPopUp;
import it.giunti.apg.client.services.AnagraficheService;
import it.giunti.apg.client.services.AnagraficheServiceAsync;
import it.giunti.apg.client.services.ArticoliService;
import it.giunti.apg.client.services.ArticoliServiceAsync;
import it.giunti.apg.client.services.SapService;
import it.giunti.apg.client.services.SapServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.EvasioniArticoli;
import it.giunti.apg.shared.model.Ruoli;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.SimplePanel;

public class EvasioniArticoliTable extends PagingTable<EvasioniArticoli> implements IRefreshable {
	private static final ArticoliServiceAsync articoliService = GWT.create(ArticoliService.class);
	private static final AnagraficheServiceAsync anagraficheService = GWT.create(AnagraficheService.class);

	private static final int TABLE_ROWS = 10;
	private static final int NOTE_LENGTH = 40;
	
	//private static final DateTimeFormat FORMAT_LIMIT = DateTimeFormat.getFormat("dd MMMM");
	
	private IRefreshable parent = null;
	private boolean orderMode = false;
	private boolean isEditor = false;
	private boolean isSuper = false;
	
	private AsyncCallback<List<EvasioniArticoli>> callback = new AsyncCallback<List<EvasioniArticoli>>() {
		@Override
		public void onFailure(Throwable caught) {
			setTableRows(new ArrayList<EvasioniArticoli>());
			//WaitSingleton.get().stop();
		}
		@Override
		public void onSuccess(List<EvasioniArticoli> result) {
			setTableRows(result);
			//WaitSingleton.get().stop();
		}
	};
	
	public EvasioniArticoliTable(DataModel<EvasioniArticoli> model, Ruoli userRole,
			IRefreshable parent, boolean orderMode) {
		super(model, TABLE_ROWS);
		this.parent = parent;
		this.orderMode = orderMode;
		isEditor = (userRole.getId().intValue() >= AppConstants.RUOLO_EDITOR);
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
	protected void addTableRow(int rowNum, EvasioniArticoli rowObj) {
		final EvasioniArticoliTable table = this;
		final EvasioniArticoli rowFinal = rowObj;
		// Set the data in the current row
		String linkText = "";
		if (orderMode) {
			linkText += rowObj.getArticolo().getCodiceMeccanografico()+" ";
		} else {
			linkText += "";
			if (rowObj.getArticolo().getCodiceInterno() != null) {
				if (rowObj.getArticolo().getCodiceInterno().length() > 0) {
					linkText += "["+rowObj.getArticolo().getCodiceInterno()+"] ";
				}
			}
		}
		linkText += "<b>"+rowObj.getArticolo().getTitoloNumero()+"</b>";
		if (isEditor && !orderMode) {
			Anchor rowLink = new Anchor(linkText, true);
			rowLink.addMouseDownHandler(new MouseDownHandler() {
				@Override
				public void onMouseDown(MouseDownEvent event) {
					EvasioneArticoloPopUp popup = new EvasioneArticoloPopUp();
					popup.initByEvasioneArticolo(rowFinal.getId(),false, false, table);
				}
			});
			getInnerTable().setWidget(rowNum, 0, rowLink);
		} else {
			getInnerTable().setHTML(rowNum, 0, "<b>"+linkText+"</b>");
		}
		//Destinatario
		FlowPanel destPanel = new FlowPanel();
		DestinatarioPanel anagPanel = new DestinatarioPanel(rowObj.getIdAnagrafica());
		destPanel.add(anagPanel);
		destPanel.add(new InlineHTML("<i>("+
				AppConstants.DEST_DESC.get(rowObj.getIdTipoDestinatario())+
				")</i>"));//descr tipo destinatario
		getInnerTable().setWidget(rowNum, 1, destPanel);
		//Creazione
		String dataCreaz = "--&nbsp;";
		if (rowObj.getDataCreazione() != null) {
			dataCreaz = ClientConstants.FORMAT_DAY.format(rowObj.getDataCreazione());
		}
		getInnerTable().setHTML(rowNum, 2, dataCreaz);
		//Estrazione e annullamento
		String invio = "";
		if (rowObj.getDataAnnullamento() != null) {
			invio += "annullato "+ClientConstants.FORMAT_DAY.format(rowObj.getDataAnnullamento())+" ";
		}
		//Inviato massivamente o manualmente
		if (rowObj.getDataInvio() != null) {
			invio += "spedito "+ClientConstants.FORMAT_DAY.format(rowObj.getDataInvio())+" ";
		} else {
			if (rowObj.getPrenotazioneIstanzaFutura()) {
				invio += "<b>Prenotato al rinnovo</b> ";
			}
		}
		if (rowObj.getDataConfermaEvasione() != null) {
			invio += "evaso "+ClientConstants.FORMAT_DAY.format(rowObj.getDataConfermaEvasione())+" ";
		}
		if (rowObj.getOrdiniLogistica() != null) {
			//Ordine via SAP
			if (invio.length() == 0) invio = "in corso ";
			invio += "[ord."+rowObj.getOrdiniLogistica().getNumeroOrdine()+"]";
		}
		if (rowObj.getDataLimite() != null) {
			invio += "limite pag. "+ClientConstants.FORMAT_DAY.format(rowObj.getDataLimite())+" ";
		}
		if (invio.length() == 0) invio ="--&nbsp;";
		getInnerTable().setHTML(rowNum, 3, invio);
		//note
		String note = rowObj.getNote();
		if (note != null) {
			if (note.length()>NOTE_LENGTH) {
				note = note.substring(0, NOTE_LENGTH)+"...";
			}
		} else {
			note = "";
		}
		//utente
		if (!orderMode) {
			note += " <i>("+rowObj.getIdUtente()+")</i>";
		}
		getInnerTable().setHTML(rowNum, 4, note);
		//delete
		if ((rowObj.getDataInvio() == null && rowObj.getDataOrdine() == null && isEditor) || isSuper ) {
			InlineHTML trashImg = new InlineHTML(ClientConstants.ICON_DELETE);
			trashImg.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent arg0) {
					confirmAndDelete(rowFinal.getIdIstanzaAbbonamento(),
							rowFinal.getId());
				}
			});
			getInnerTable().setWidget(rowNum, 5, trashImg);
		}
	}
	
	@Override
	protected void addHeader() {
		// Set the data in the current row
		getInnerTable().setHTML(0, 0, "Articolo");
		getInnerTable().setHTML(0, 1, "Destinatario");
		getInnerTable().setHTML(0, 2, "Creazione");
		getInnerTable().setHTML(0, 3, "Invio");
		getInnerTable().setHTML(0, 4, "Note");
	}
	
	@Override
	protected void onEmptyResult() {}
	
	private void confirmAndDelete(Integer idIstanza, Integer idEvasioneArticolo) {
		boolean confirm = Window.confirm("Vuoi veramente eliminare il articolo dall'elenco?");
		if (confirm) {
			delete(idIstanza, idEvasioneArticolo);
		}
	}
	
	public void delete(Integer idIstanza, Integer idEvasioneArticolo) {
		//WaitSingleton.get().start();
		articoliService.deleteEvasioneArticolo(idEvasioneArticolo, callback);
	}
	
	
	
	//Inner classes
	
	
	
	public static class EvasioniArticoliByIstanzaModel implements DataModel<EvasioniArticoli> {
		private Integer idIstanza = null;
		
		public EvasioniArticoliByIstanzaModel(Integer idIstanza) {
			this.idIstanza = idIstanza;
		}
		
		@Override
		public void find(int offset, int pageSize,
				AsyncCallback<List<EvasioniArticoli>> callback) {
			articoliService.findEvasioniArticoliByIstanza(idIstanza, callback);
		}
	}
	
	public static class EvasioniArticoliByOrdineModel implements DataModel<EvasioniArticoli> {
		private final SapServiceAsync sapService = GWT.create(SapService.class);
		private String numOrdine = null;
		
		public EvasioniArticoliByOrdineModel(String numOrdine) {
			this.numOrdine = numOrdine;
		}
		
		@Override
		public void find(int offset, int pageSize,
				AsyncCallback<List<EvasioniArticoli>> callback) {
			sapService.findEvasioniArticoliByOrdine(numOrdine, callback);
		}
	}
	
	public static class EvasioniArticoliByAnagraficaModel implements DataModel<EvasioniArticoli> {
		private Integer idAnagrafica = null;
		
		public EvasioniArticoliByAnagraficaModel(Integer idAnagrafica) {
			this.idAnagrafica = idAnagrafica;
		}
		
		@Override
		public void find(int offset, int pageSize,
				AsyncCallback<List<EvasioniArticoli>> callback) {
			articoliService.findEvasioniArticoliByAnagrafica(idAnagrafica, callback);
		}
	}
	
	public static class DestinatarioPanel extends SimplePanel {
		private Integer idAnagrafica = null;
		
		public DestinatarioPanel(Integer idAnagrafica) {
			super();
			this.idAnagrafica = idAnagrafica;
			add(new InlineHTML(ClientConstants.LABEL_LOADING));
			load();
		}
		
		private void load() {
			AsyncCallback<String> callback = new AsyncCallback<String>() {
				@Override
				public void onFailure(Throwable caught) {
					UiSingleton.get().addError(caught);
				}
				@Override
				public void onSuccess(String result) {
					clear();
					add(new InlineHTML(result));
				}
			};
			anagraficheService.findDescriptionById(idAnagrafica, callback);
		}
	}
	
}
