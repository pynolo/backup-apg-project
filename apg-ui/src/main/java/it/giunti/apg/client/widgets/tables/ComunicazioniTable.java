package it.giunti.apg.client.widgets.tables;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.UriManager;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.services.ComunicazioniService;
import it.giunti.apg.client.services.ComunicazioniServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Comunicazioni;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;

public class ComunicazioniTable extends PagingTable<Comunicazioni> {

	private static final ComunicazioniServiceAsync comService = GWT.create(ComunicazioniService.class);
	
	private static final int TABLE_ROWS = AppConstants.TABLE_ROWS_DEFAULT;
	
	private AsyncCallback<List<Comunicazioni>> callback = new AsyncCallback<List<Comunicazioni>>() {
		@Override
		public void onFailure(Throwable caught) {
			setTableRows(new ArrayList<Comunicazioni>());
			//WaitSingleton.get().stop();
		}
		@Override
		public void onSuccess(List<Comunicazioni> result) {
			setTableRows(result);
			//WaitSingleton.get().stop();
		}
	};
	
	public ComunicazioniTable(DataModel<Comunicazioni> model) {
		super(model, TABLE_ROWS);
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
	protected void addTableRow(int rowNum, Comunicazioni rowObj) {
		// Set the data in the current row
		//getInnerTable().setHTML(rowNum, 0, rowObj.getPeriodico().getNome());
		String linkText = "<b>"+rowObj.getTitolo()+"</b>";
		UriParameters params = new UriParameters();
		params.add(AppConstants.PARAM_ID, rowObj.getId());
		Hyperlink rowLink = params.getHyperlink(linkText, UriManager.COMUNICAZIONE);
		String linkTitle = "Valido";
		if (rowObj.getDataInizio() != null) linkTitle += " dal "+ClientConstants.FORMAT_DAY.format(rowObj.getDataInizio());
		if (rowObj.getDataFine() != null) linkTitle += " al " + ClientConstants.FORMAT_DAY.format(rowObj.getDataFine());
		rowLink.setTitle(linkTitle);
		getInnerTable().setWidget(rowNum, 1, rowLink);
		//Media
		String media = AppConstants.COMUN_MEDIA_DESC.get(rowObj.getIdTipoMedia());
		if (rowObj.getModelloBollettino() != null) media = "<b>"+rowObj.getModelloBollettino().getDescr()+"</b>";
		getInnerTable().setHTML(rowNum, 2, media);
		//Attivazione
		String attivazione = AppConstants.COMUN_ATTIVAZ_DESC.get(rowObj.getIdTipoAttivazione());
		if (rowObj.getIdTipoAttivazione().equals(AppConstants.COMUN_ATTIVAZ_DA_INIZIO) || 
				rowObj.getIdTipoAttivazione().equals(AppConstants.COMUN_ATTIVAZ_DA_FINE)) {
			attivazione = rowObj.getNumeriDaInizioOFine()+" "+attivazione;
		}
		getInnerTable().setHTML(rowNum, 3, "<b>"+attivazione+"</b>");
		//Destinatario
		String dest = AppConstants.DEST_DESC.get(rowObj.getIdTipoDestinatario());
		getInnerTable().setHTML(rowNum, 4, dest);
		//Bandella
		String bandella = "--";
		if (rowObj.getIdBandella() != null) {
			bandella = "<b>"+rowObj.getIdBandella()+"</b>";
		}
		getInnerTable().setHTML(rowNum, 5, bandella);
		//Dettagli
		String dettagli = "";
		//con pagante
		if (rowObj.getSoloConPagante()) {
			if (dettagli.length() > 0) dettagli += ", ";
			dettagli += "regalati";
		}
		//senza pagante
		if (rowObj.getSoloSenzaPagante()) {
			if (dettagli.length() > 0) dettagli += ", ";
			dettagli += "personali";
		}
		//rinnovo
		if (rowObj.getRichiestaRinnovo()) {
			if (dettagli.length() > 0) dettagli += ", ";
			dettagli += "rinnovo";
		}
		//bollettino
		if (rowObj.getIdTipoMedia().equals(AppConstants.COMUN_MEDIA_BOLLETTINO)) {
			if (rowObj.getMostraPrezzoAlternativo()) {
				if (dettagli.length() > 0) dettagli += ", ";
				dettagli += "prezzo alternativo";
			}
		}
		//una copia
		if (rowObj.getSoloUnaCopia()) {
			if (dettagli.length() > 0) dettagli += ", ";
			dettagli += "copie singole";
		}
		//più copie
		if (rowObj.getSoloPiuCopie()) {
			if (dettagli.length() > 0) dettagli += ", ";
			dettagli += "pi&ugrave; copie";
		}
		//non pagati
		if (rowObj.getSoloNonPagati()) {
			if (dettagli.length() > 0) dettagli += ", ";
			dettagli += "non pagati";
		}
		//soloFascicolo
		if (rowObj.getDataInizio() != null) {
			if (dettagli.length() > 0) dettagli += ", ";
			dettagli += "data inizio";
		}
		//non pagati
		if (rowObj.getSoloUnaIstanza()) {
			if (dettagli.length() > 0) dettagli += ", ";
			dettagli += "una istanza";
		}
		//non pagati
		if (rowObj.getSoloMolteIstanze()) {
			if (dettagli.length() > 0) dettagli += ", ";
			dettagli += "pi&ugrave; istanze";
		}
		getInnerTable().setHTML(rowNum, 6, dettagli);
		//più copie
		HTML tipiAbb = new HTML();
		getInnerTable().setWidget(rowNum, 7, tipiAbb);
		loadTipiAbb(rowObj,tipiAbb);
	}
	
	@Override
	protected void addHeader() {
		// Set the data in the current row
		//getInnerTable().setHTML(0, 0, "Periodico");
		getInnerTable().setHTML(0, 1, "Titolo");
		getInnerTable().setHTML(0, 2, "Media");
		getInnerTable().setHTML(0, 3, "Attivazione");
		getInnerTable().setHTML(0, 4, "Dest.");
		getInnerTable().setHTML(0, 5, "Testo");
		getInnerTable().setHTML(0, 6, "Condizioni");
		getInnerTable().setHTML(0, 7, "Tipi abbonamento");
	}
	
	@Override
	protected void onEmptyResult() {}
	
	//private String describeTrigger(Comunicazioni com) {
	//	String result;
	//	if (com.getIdTipoComunicazione().equals(AppConstants.COMUNIC_TIPO_NUOVO)) {
	//		if (com.getNumeriDaInizioOFine() == 0) {
	//			result = "All'inizio";
	//		} else {
	//			result = "Dopo "+com.getNumeriDaInizioOFine()+" ";
	//			if (com.getNumeriDaInizioOFine() == 1) {
	//				result += "numero";
	//			} else {
	//				result += "numeri";
	//			}
	//		}
	//	} else {
	//		if (com.getNumeriDaInizioOFine() == 0) {
	//			result = "Al numero finale";
	//		} else {
	//			if (com.getNumeriDaInizioOFine() < 0) {
	//				result = (-1*com.getNumeriDaInizioOFine())+" ";
	//				if (com.getNumeriDaInizioOFine() == -1) {
	//					result += "numero ";
	//				} else {
	//					result += "numeri ";
	//				}
	//				result += "prima della fine";
	//			} else {
	//				result = com.getNumeriDaInizioOFine()+" ";
	//				if (com.getNumeriDaInizioOFine() == 1) {
	//					result += "numero ";
	//				} else {
	//					result += "numeri ";
	//				}
	//				result += "dopo la fine";
	//			}
	//		}
	//	}
	//	return result;
	//}

	
	//Async methods
	
	private void loadTipiAbb(Comunicazioni com, HTML label) {
		final HTML fLabel = label;
		AsyncCallback<String> labelCallback = new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
			}
			@Override
			public void onSuccess(String result) {
				fLabel.setHTML("<b>"+result+"</b>");
			}
		};
		comService.getTipiAbbStringFromComunicazione(com.getId(), labelCallback);
	}
	
	
	
	
	
	
	//Inner classes
	
	
	
	public static class ComunicazioniByPeriodicoModel implements DataModel<Comunicazioni> {
		private Integer idPeriodico = null;
		private Date date = null;
		
		public ComunicazioniByPeriodicoModel(Integer idPeriodico, Date dt) {
			this.idPeriodico = idPeriodico;
			this.date = dt;
		}
		
		@Override
		public void find(int offset, int pageSize,
				AsyncCallback<List<Comunicazioni>> callback) {
			//WaitSingleton.get().start();
			comService.findComunicazioniByPeriodico(idPeriodico, date, offset, pageSize, callback);
		}
	}

	public static class ComunicazioniByTipoAbbModel implements DataModel<Comunicazioni> {
		private Integer idTipoAbb = null;
		private Date date = null;
		
		public ComunicazioniByTipoAbbModel(Integer idTipoAbb, Date dt) {
			this.idTipoAbb = idTipoAbb;
			this.date = dt;
		}
		
		@Override
		public void find(int offset, int pageSize,
				AsyncCallback<List<Comunicazioni>> callback) {
			//WaitSingleton.get().start();
			comService.findComunicazioniByTipoAbb(idTipoAbb, date, offset, pageSize, callback);
		}
	}
	
}
