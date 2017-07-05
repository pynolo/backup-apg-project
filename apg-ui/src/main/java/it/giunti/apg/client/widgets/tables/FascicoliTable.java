package it.giunti.apg.client.widgets.tables;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.frames.FascicoloPopUp;
import it.giunti.apg.client.services.FascicoliService;
import it.giunti.apg.client.services.FascicoliServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Fascicoli;
import it.giunti.apg.shared.model.Ruoli;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.InlineHTML;

public class FascicoliTable extends PagingTable<Fascicoli> implements IRefreshable {
	
	private static final FascicoliServiceAsync fascicoliService = GWT.create(FascicoliService.class);
	
	private static final int TABLE_ROWS = 50;//ClientConstants.TABLE_ROWS_DEFAULT;
	private static final int NOTE_MAX_LENGTH = 30;
	private boolean isEditor = false;
	private boolean isSuper = false;
	private static final String ICON_MAIL = "<img src='img/icon16/mail-forward.png' style='vertical-align:middle;border:none;' title='Comunicazioni accodate' />";

	private AsyncCallback<List<Fascicoli>> callback = new AsyncCallback<List<Fascicoli>>() {
		@Override
		public void onFailure(Throwable caught) {
			setTableRows(new ArrayList<Fascicoli>());
			//WaitSingleton.get().stop();
		}
		@Override
		public void onSuccess(List<Fascicoli> result) {
			setTableRows(result);
			//WaitSingleton.get().stop();
		}
	};
	
	public FascicoliTable(DataModel<Fascicoli> model, Ruoli role) {
		super(model, TABLE_ROWS);
		this.isEditor = (role.getId() >= AppConstants.RUOLO_EDITOR);
		this.isSuper = (role.getId() >= AppConstants.RUOLO_SUPER);
		drawPage(0);
	}

	@Override
	public void refresh() {
		drawPage(0);
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
	protected void addTableRow(int rowNum, Fascicoli rowObj) {
		// Set the data in the current row
		final Fascicoli fRowObj = rowObj;
		final FascicoliTable table = this;
		//CM
		if (isEditor) {
			Anchor fascicoloAnchor = new Anchor("<b>"+rowObj.getCodiceMeccanografico()+"</b>", true);
			fascicoloAnchor.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent arg0) {
					FascicoloPopUp popUp = new FascicoloPopUp();
					popUp.initByFascicolo(fRowObj.getId(), table);
				}
			});
			getInnerTable().setWidget(rowNum, 0, fascicoloAnchor);
		} else {
			getInnerTable().setHTML(rowNum, 0, "<b>"+rowObj.getCodiceMeccanografico()+"</b>");
		}
		//Descrizione
		String descr = "";
		if (rowObj.getOpzione() != null) {
			descr += rowObj.getOpzione().getNome()+" ";
		}
		descr += rowObj.getTitoloNumero();
		descr = "<b>"+descr+"</b>";
		getInnerTable().setHTML(rowNum, 1, descr);
		//Data copertina
		String dataCop = "";
		if (rowObj.getOpzione() != null) dataCop += "<b>opzione</b> ";
		dataCop += rowObj.getDataCop();
		getInnerTable().setHTML(rowNum, 2, dataCop);
		//Data nominale
		getInnerTable().setHTML(rowNum, 3, 
				ClientConstants.SPAN_SMALL_START+
				ClientConstants.FORMAT_DAY.format(rowObj.getDataInizio())+"&nbsp;"+
				ClientConstants.SPAN_STOP);
		//Data pubblicazione
		String nomPubbl = "--";
		if (rowObj.getDataPubblicazione() != null) {
			nomPubbl = ClientConstants.SPAN_SMALL_START+
					ClientConstants.FORMAT_DAY.format(rowObj.getDataPubblicazione())+
					ClientConstants.SPAN_STOP;
		}
		getInnerTable().setHTML(rowNum, 4, nomPubbl+"&nbsp;");
		//Fine nominale
		String fineNominale = "--";
		if (rowObj.getDataFine() != null) {
			fineNominale = ClientConstants.SPAN_SMALL_START+
					ClientConstants.FORMAT_DAY.format(rowObj.getDataFine())+"&nbsp;"+
					ClientConstants.SPAN_STOP;
		}
		getInnerTable().setHTML(rowNum, 5, fineNominale);
		//Data estrazione
		String estrazione = "--";
		if (rowObj.getDataEstrazione() != null) {
			estrazione = ClientConstants.FORMAT_DATETIME.format(rowObj.getDataEstrazione());
		}
		if (rowObj.getComunicazioniInviate()) {
			estrazione += "&nbsp;"+ICON_MAIL;
		}
		getInnerTable().setHTML(rowNum, 6, "<b>"+estrazione+"</b>&nbsp;");
		//Invio arretrato sospeso
		String inAttesa = "NO";
		if (rowObj.getInAttesa()) inAttesa = "<b>SI</b>";
		getInnerTable().setHTML(rowNum, 7, inAttesa+"&nbsp;");
		//Classificazione
		getInnerTable().setHTML(rowNum, 8, "<i>"+
				AppConstants.ANAGRAFICA_SAP_DESC.get(rowObj.getIdTipoAnagraficaSap())+"</i>");
		//Note
		String noteNumero = "";
		if (rowObj.getFascicoliAccorpati() != null) {
			if (rowObj.getFascicoliAccorpati() > 1) {
				noteNumero += "("+rowObj.getFascicoliAccorpati()+" accorpati) ";
			}
			if (rowObj.getFascicoliAccorpati() == 0) {
				if (rowObj.getOpzione() != null) {
					noteNumero += "<b>opz. "+rowObj.getOpzione().getNome()+"</b> ";
				} else {
					noteNumero += "<b>allegato</b> ";
				}
			}
		}
		String note = rowObj.getNote();
		if (note == null) note = "";
		if (note.length() > NOTE_MAX_LENGTH) note = note.substring(0, NOTE_MAX_LENGTH)+"&hellip;";
		getInnerTable().setHTML(rowNum, 9, noteNumero+" "+note);
		//Elimina
		if (isSuper) {
			InlineHTML trashImg = new InlineHTML(ClientConstants.ICON_DELETE);
			final Integer idFas = rowObj.getId();
			trashImg.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent arg0) {
					confirmAndDelete(idFas);
				}
			});
			getInnerTable().setWidget(rowNum, 10, trashImg);
		}
	}
	
	@Override
	protected void addHeader() {
		// Set the data in the current row
		getInnerTable().setHTML(0, 0, "CM");
		getInnerTable().setHTML(0, 1, "Numero");
		getInnerTable().setHTML(0, 2, "Copertina");
		getInnerTable().setHTML(0, 3, "Data nominale");
		getInnerTable().setHTML(0, 4, "Pubblic.");
		getInnerTable().setHTML(0, 5, "Fine nominale");
		getInnerTable().setHTML(0, 6, "Estrazione");
		getInnerTable().setHTML(0, 7, "In&nbsp;attesa");
		getInnerTable().setHTML(0, 8, "Anagrafica SAP");
		getInnerTable().setHTML(0, 9, "Dettagli");
	}
	
	@Override
	protected void onEmptyResult() {}
	
	private void confirmAndDelete(Integer idFas) {
		boolean confirm = Window.confirm("Vuoi veramente eliminare il fascicolo?");
		if (confirm) {
			delete(idFas);
		}
	}
	
	public void delete(Integer idFas) {
		//WaitSingleton.get().start();
		fascicoliService.deleteFascicolo(idFas, callback);
	}
	
	//Async methods
	
	
//	private void loadOpzioniDesc(String opzioniList, InlineHTML label) {
//		final InlineHTML fLabel = label;
//		AsyncCallback<String> callback = new AsyncCallback<String>() {
//			@Override
//			public void onFailure(Throwable caught) {
//				UiSingleton.get().addError(caught);
//			}
//			@Override
//			public void onSuccess(String result) {
//				fLabel.setHTML(result);
//			}
//		};
//		fascicoliService.getOpzioniDescr(opzioniList, callback);
//	}
	
	
	
	//Inner classes
	
	
	
	public static class FascicoliByPeriodicoModel implements DataModel<Fascicoli> {
		private Integer idPeriodico = null;
		private Long startDt = null;
		private Long finishDt = null;
		
		public FascicoliByPeriodicoModel(Integer idPeriodico, long startDt, long finishDt) {
			this.idPeriodico=idPeriodico;
			this.startDt=startDt;
			this.finishDt=finishDt;
		}
		
		@Override
		public void find(int offset, int pageSize,
				AsyncCallback<List<Fascicoli>> callback) {
			//WaitSingleton.get().start();
			fascicoliService.findFascicoliByPeriodico(idPeriodico, startDt, finishDt, true, false, offset, pageSize,  callback);
		}
	}
	
	public static class FascicoliByOpzioneModel implements DataModel<Fascicoli> {
		private Integer idOpzione = null;
		
		public FascicoliByOpzioneModel(Integer idOpzione) {
			this.idOpzione=idOpzione;
		}
		
		@Override
		public void find(int offset, int pageSize,
				AsyncCallback<List<Fascicoli>> callback) {
			//WaitSingleton.get().start();
			fascicoliService.findFascicoliByOpzione(idOpzione, false, offset, pageSize, callback);
		}
	}
}
