package it.giunti.apg.client.widgets.tables;

import java.util.ArrayList;
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
import it.giunti.apg.client.frames.MaterialiProgrammazionePopUp;
import it.giunti.apg.client.services.MaterialiService;
import it.giunti.apg.client.services.MaterialiServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.MaterialiProgrammazione;
import it.giunti.apg.shared.model.Ruoli;

public class MaterialiProgrammazioneTable extends PagingTable<MaterialiProgrammazione> implements IRefreshable {
	
	private static final MaterialiServiceAsync matService = GWT.create(MaterialiService.class);
	
	private static final int TABLE_ROWS = 50;//ClientConstants.TABLE_ROWS_DEFAULT;
	private static final int NOTE_MAX_LENGTH = 30;
	private boolean isEditor = false;
	private boolean isSuper = false;
	private static final String ICON_MAIL = "<img src='img/icon16/mail-forward.png' style='vertical-align:middle;border:none;' title='Comunicazioni accodate' />";

	private AsyncCallback<List<MaterialiProgrammazione>> callback = new AsyncCallback<List<MaterialiProgrammazione>>() {
		@Override
		public void onFailure(Throwable caught) {
			setTableRows(new ArrayList<MaterialiProgrammazione>());
			//WaitSingleton.get().stop();
		}
		@Override
		public void onSuccess(List<MaterialiProgrammazione> result) {
			setTableRows(result);
			//WaitSingleton.get().stop();
		}
	};
	
	public MaterialiProgrammazioneTable(DataModel<MaterialiProgrammazione> model, Ruoli role) {
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
	protected void addTableRow(int rowNum, MaterialiProgrammazione rowObj) {
		// Set the data in the current row
		final MaterialiProgrammazione fRowObj = rowObj;
		final MaterialiProgrammazioneTable table = this;
		//CM
		if (isEditor) {
			Anchor fascicoloAnchor = new Anchor("<b>"+rowObj.getMateriale().getCodiceMeccanografico()+"</b>", true);
			fascicoloAnchor.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent arg0) {
					MaterialiProgrammazionePopUp popUp = new MaterialiProgrammazionePopUp();
					popUp.initByMaterialeProgrammazione(fRowObj.getId(), table);
				}
			});
			getInnerTable().setWidget(rowNum, 0, fascicoloAnchor);
		} else {
			getInnerTable().setHTML(rowNum, 0, "<b>"+rowObj.getMateriale().getCodiceMeccanografico()+"</b>");
		}
		//Descrizione
		String descr = "";
		if (rowObj.getOpzione() != null) {
			descr += rowObj.getOpzione().getNome()+" ";
		}
		descr += rowObj.getMateriale().getTitolo();
		descr = "<b>"+descr+"</b>";
		getInnerTable().setHTML(rowNum, 1, descr);
		//Data copertina
		String dataCop = "";
		if (rowObj.getOpzione() != null) dataCop += "<b>opzione</b> ";
		dataCop += rowObj.getMateriale().getSottotitolo();
		getInnerTable().setHTML(rowNum, 2, dataCop);
		//Data nominale
		getInnerTable().setHTML(rowNum, 3, 
				ClientConstants.SPAN_SMALL_START+
				ClientConstants.FORMAT_DAY.format(rowObj.getDataNominale())+"&nbsp;"+
				ClientConstants.SPAN_STOP);
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
		if (rowObj.getMateriale().getInAttesa()) inAttesa = "<b>SI</b>";
		getInnerTable().setHTML(rowNum, 7, inAttesa+"&nbsp;");
		//Classificazione
		getInnerTable().setHTML(rowNum, 8, "<i>"+
				AppConstants.ANAGRAFICA_SAP_DESC.get(rowObj.getMateriale().getIdTipoAnagraficaSap())+"</i>");
		//Note
		String noteNumero = "";
		if (rowObj.getOpzione() != null) {
			noteNumero += "<b>opz. "+rowObj.getOpzione().getNome()+"</b> ";
		}
		String note = rowObj.getMateriale().getNote();
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
		getInnerTable().setHTML(0, 6, "Estrazione");
		getInnerTable().setHTML(0, 7, "In&nbsp;attesa");
		getInnerTable().setHTML(0, 8, "Anagrafica SAP");
		getInnerTable().setHTML(0, 9, "Dettagli");
	}
	
	@Override
	protected void onEmptyResult() {}
	
	private void confirmAndDelete(Integer idMatProg) {
		boolean confirm = Window.confirm("Vuoi veramente eliminare la programmazione del materiale?");
		if (confirm) {
			delete(idMatProg);
		}
	}
	
	public void delete(Integer idMatProg) {
		AsyncCallback<Boolean> deleteCallback = new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				setTableRows(new ArrayList<MaterialiProgrammazione>());
			}

			@Override
			public void onSuccess(Boolean result) {
				drawPage(0);
			}
		};
		matService.deleteMaterialiProgrammazione(idMatProg, deleteCallback);
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
	
	
	
	public static class MaterialiProgrammazioneByPeriodicoModel implements DataModel<MaterialiProgrammazione> {
		private Integer idPeriodico = null;
		private Long startDt = null;
		private Long finishDt = null;
		
		public MaterialiProgrammazioneByPeriodicoModel(Integer idPeriodico, long startDt, long finishDt) {
			this.idPeriodico=idPeriodico;
			this.startDt=startDt;
			this.finishDt=finishDt;
		}
		
		@Override
		public void find(int offset, int pageSize,
				AsyncCallback<List<MaterialiProgrammazione>> callback) {
			//WaitSingleton.get().start();
			matService.findMaterialiProgrammazioneByPeriodico(idPeriodico, startDt, finishDt, true, false, offset, pageSize,  callback);
		}
	}
	
	public static class MaterialiProgrammazioneByOpzioneModel implements DataModel<MaterialiProgrammazione> {
		private Integer idOpzione = null;
		
		public MaterialiProgrammazioneByOpzioneModel(Integer idOpzione) {
			this.idOpzione=idOpzione;
		}
		
		@Override
		public void find(int offset, int pageSize,
				AsyncCallback<List<MaterialiProgrammazione>> callback) {
			//WaitSingleton.get().start();
			matService.findMaterialiProgrammazioneByOpzione(idOpzione, false, offset, pageSize, callback);
		}
	}
}
