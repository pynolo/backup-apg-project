package it.giunti.apg.client.widgets.tables;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.UriManager;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.services.TipiAbbService;
import it.giunti.apg.client.services.TipiAbbServiceAsync;
import it.giunti.apg.client.widgets.TipiAbbonamentoRinnovoLabel;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.MaterialiListini;
import it.giunti.apg.shared.model.Listini;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineHTML;

public class ListiniTable extends PagingTable<Listini> {

	//private static final AbbonamentiServiceAsync aService = GWT.create(AbbonamentiService.class);
	private static final int TABLE_ROWS = 45;
	private boolean isEditor = false;
	
	private AsyncCallback<List<Listini>> callback = new AsyncCallback<List<Listini>>() {
		@Override
		public void onFailure(Throwable caught) {
			setTableRows(new ArrayList<Listini>());
			//WaitSingleton.get().stop();
		}
		@Override
		public void onSuccess(List<Listini> result) {
			setTableRows(result);
			//WaitSingleton.get().stop();
		}
	};
	
	public ListiniTable(DataModel<Listini> model, boolean isEditor) {
		super(model, TABLE_ROWS);
		this.isEditor=isEditor;
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
	protected void addTableRow(int rowNum, Listini rowObj) {
		// Codice-Link
		String linkText = rowObj.getTipoAbbonamento().getCodice();
		linkText = "<b>"+linkText+"</b>";
		if (isEditor) {
			UriParameters params = new UriParameters();
			params.add(AppConstants.PARAM_ID, rowObj.getId());
			Hyperlink rowLink = params.getHyperlink(linkText, UriManager.LISTINO);
			String linkTitle = "Valido dal "+ClientConstants.FORMAT_DAY.format(rowObj.getDataInizio());
			if (rowObj.getDataFine() != null) linkTitle += " al " + ClientConstants.FORMAT_DAY.format(rowObj.getDataFine());
			rowLink.setTitle(linkTitle);
			getInnerTable().setWidget(rowNum, 0, rowLink);
		} else {
			getInnerTable().setHTML(rowNum, 0, "<b>"+linkText+"</b>");
		}
		//Nome
		String nome = rowObj.getTipoAbbonamento().getNome();
		if (rowObj.getCartaceo()) nome += " "+ClientConstants.ICON_CARTACEO;
		if (rowObj.getDigitale()) nome += " "+ClientConstants.ICON_APP;
		getInnerTable().setHTML(rowNum, 1, nome);
		//Numero di instanze regolari
		//InlineHTML countIstanzeHtml = new InlineHTML("...");//ClientConstants.ICON_LOADING_SMALL);
		//getInnerTable().setWidget(rowNum, 2, countIstanzeHtml);
		//loadCountIstanzeByTipo(rowObj.getTipoAbbonamento().getId(), countIstanzeHtml);
		//Prezzo
		String prezzo = "";
		if (rowObj.getPrezzo() > AppConstants.SOGLIA) {
			if (rowObj.getFatturaDifferita()) prezzo += ClientConstants.ICON_FATTURA_DIFFERITO+" ";
			prezzo += "&euro;" + ClientConstants.FORMAT_CURRENCY.format(rowObj.getPrezzo());
		} else {
			prezzo = ClientConstants.ICON_OMAGGIO+" <i>omaggio</i>";
		}
		if (rowObj.getAliquotaIva().getValore() > 0) prezzo += " <i>iva&nbsp;"+
				rowObj.getAliquotaIva().getDescr()+"</i>";
		getInnerTable().setHTML(rowNum, 2, prezzo);
		//Durata
		String numFascicoli = rowObj.getDurataMesi()+" ";
		getInnerTable().setHTML(rowNum, 3, numFascicoli);
		//Gracing
		getInnerTable().setHTML(rowNum, 4, rowObj.getGracingInizialeMesi()+"&nbsp;-&nbsp;"+rowObj.getGracingFinaleMesi());
		//Macroarea
		getInnerTable().setHTML(rowNum, 5, zonaDesc(rowObj.getIdMacroarea()));
		//Abb.al rinnovo
		TipiAbbonamentoRinnovoLabel tarLabel = new TipiAbbonamentoRinnovoLabel(rowObj.getId());
		getInnerTable().setWidget(rowNum, 6, tarLabel);
		//Descrizione caratteristiche
		String caratteristiche = "";
		if (rowObj.getOpzioniListiniSet() != null) {
			if (rowObj.getOpzioniListiniSet().size() > 0) {
				if (caratteristiche.length() > 0) caratteristiche += ", ";
				caratteristiche += "<b>"+rowObj.getOpzioniListiniSet().size() + " opz. obbligatorie</b>";
			}
		}
		if (rowObj.getInvioSenzaPagamento()) {
			if (caratteristiche.length() > 0) caratteristiche += ", ";
			caratteristiche += "invio senza pag.";
		}
		if (rowObj.getFatturaInibita()) {
			if (caratteristiche.length() > 0) caratteristiche += ", ";
			caratteristiche += "no fatture";
		}
		if (rowObj.getMeseInizio() != null) {
			if (caratteristiche.length() > 0) caratteristiche += ", ";
			caratteristiche += "da "+ClientConstants.MESI[rowObj.getMeseInizio()];
		}
		if (rowObj.getStampaDonatore()) {
			if (caratteristiche.length() > 0) caratteristiche += ", ";
			caratteristiche += "stampa donatore";
		}
		if (rowObj.getStampaScrittaOmaggio()) {
			if (caratteristiche.length() > 0) caratteristiche += ", ";
			caratteristiche += "etichetta 'omaggio'";
		}
		if (rowObj.getMaterialiListiniSet() != null) {
			if (rowObj.getMaterialiListiniSet().size() > 0) {
				if (caratteristiche.length() > 0) caratteristiche += ", ";
				caratteristiche += "articoli: ";
				for (MaterialiListini al:rowObj.getMaterialiListiniSet()) {
					caratteristiche += al.getMateriale().getCodiceMeccanografico()+"("+
							AppConstants.DEST_DESC.get(al.getIdTipoDestinatario())+") ";
				}
			}
		}
		//if (rowObj.getPrezzoOpzObbligatori() != null) {
		//	if (caratteristiche.length() > 0) caratteristiche += ", ";
		//	caratteristiche += "opz. &euro;"+ClientConstants.FORMAT_CURRENCY.format(rowObj.getPrezzoOpzObbligatori());
		//}
		if (rowObj.getDataFine() != null) {
			if (caratteristiche.length() > 0) caratteristiche += ", ";
			caratteristiche += "scad. "+ClientConstants.FORMAT_DAY.format(rowObj.getDataFine());
		}
		if (rowObj.getTag() != null) {
			if (rowObj.getTag().length() > 0) {
				if (caratteristiche.length() > 0) caratteristiche += ", ";
				caratteristiche += "tag: <b>"+rowObj.getTag()+"</b>";
			}
		}
		getInnerTable().setHTML(rowNum, 7, caratteristiche);
		//BO Blocco Offerta
		String bo = "";
		if (rowObj.getTipoAbbonamento().getDeltaInizioBloccoOfferta() != null)
			bo = ClientConstants.FORMAT_INTEGER.format(rowObj.getTipoAbbonamento().getDeltaInizioBloccoOfferta());
		getInnerTable().setHTML(rowNum, 8, bo);
		//EA Email Addebito
		String ea = "";
		if (rowObj.getTipoAbbonamento().getDeltaInizioAvvisoPagamento() != null)
			ea = ClientConstants.FORMAT_INTEGER.format(rowObj.getTipoAbbonamento().getDeltaInizioAvvisoPagamento());
		getInnerTable().setHTML(rowNum, 9, ea);
		//AA Addebito Automatico
		String aa = "";
		if (rowObj.getTipoAbbonamento().getDeltaInizioPagamentoAutomatico() != null)
			aa = ClientConstants.FORMAT_INTEGER.format(rowObj.getTipoAbbonamento().getDeltaInizioPagamentoAutomatico());
		getInnerTable().setHTML(rowNum, 10, aa);
		//FR Fase Rinnovabile
		String fr = "";
		if (rowObj.getTipoAbbonamento().getDeltaFineRinnovoAbilitato() != null)
			fr = ClientConstants.FORMAT_INTEGER.format(rowObj.getTipoAbbonamento().getDeltaFineRinnovoAbilitato());
		getInnerTable().setHTML(rowNum, 11, fr);
		//ER Email Rinnovo
		String er = "";
		if (rowObj.getTipoAbbonamento().getDeltaFineAvvisoRinnovo() != null)
			er = ClientConstants.FORMAT_INTEGER.format(rowObj.getTipoAbbonamento().getDeltaFineAvvisoRinnovo());
		getInnerTable().setHTML(rowNum, 12, er);
		//RA Rinnovo Automatico
		String ra = "";
		if (rowObj.getTipoAbbonamento().getDeltaFineRinnovoAutomatico() != null)
			ra = ClientConstants.FORMAT_INTEGER.format(rowObj.getTipoAbbonamento().getDeltaFineRinnovoAutomatico());
		getInnerTable().setHTML(rowNum, 13, ra);
		//UID
		getInnerTable().setHTML(rowNum, 14, "<b>["+rowObj.getUid()+"]</b>");
	}
	
	@Override
	protected void addHeader() {
		// Set the data in the current row
		getInnerTable().setHTML(0, 0, "Codice");
		getInnerTable().setHTML(0, 1, "Nome");
		getInnerTable().setHTML(0, 2, "Prezzo");
		getInnerTable().setHTML(0, 3, "Durata");
		getInnerTable().setHTML(0, 4, "Gracing");
		getInnerTable().setHTML(0, 5, "Zona");
		getInnerTable().setHTML(0, 6, "Rinnovo");
		getInnerTable().setHTML(0, 7, "Propriet&agrave;");
		InlineHTML boHtml = new InlineHTML("BO");
		boHtml.setTitle("Blocco Offerta (dall'inizio)");
		getInnerTable().setWidget(0, 8, boHtml);
		InlineHTML eaHtml = new InlineHTML("EA");
		eaHtml.setTitle("Email Addebito (dall'inizio)");
		getInnerTable().setWidget(0, 9, eaHtml);
		InlineHTML aaHtml = new InlineHTML("AA");
		aaHtml.setTitle("Addebito Automatico (dall'inizio)");
		getInnerTable().setWidget(0, 10, aaHtml);
		InlineHTML frHtml = new InlineHTML("FR");
		frHtml.setTitle("Fase Rinnovabile (dalla fine)");
		getInnerTable().setWidget(0, 11, frHtml);
		InlineHTML erHtml = new InlineHTML("ER");
		erHtml.setTitle("Email Rinnovo (dalla fine)");
		getInnerTable().setWidget(0, 12, erHtml);
		InlineHTML raHtml = new InlineHTML("RA");
		raHtml.setTitle("Rinnovo Automatico (dalla fine)");
		getInnerTable().setWidget(0, 13, raHtml);
		getInnerTable().setHTML(0, 14, "UID");
	}
	
	@Override
	protected void onEmptyResult() {}

	private String zonaDesc(int idZona) {
		if (idZona == 1) {
			return "Italia";
		} else {
			return "Zona "+(idZona-1);
		}
	}
	
	
	//Async methods
	
	//private void loadCountIstanzeByTipo(Integer idTipoAbbonamento, HTML label) {
	//	final HTML fLabel = label;
	//	AsyncCallback<Integer> labelCallback = new AsyncCallback<Integer>() {
	//		@Override
	//		public void onFailure(Throwable caught) {
	//			UiSingleton.get().addError(caught);
	//		}
	//		@Override
	//		public void onSuccess(Integer result) {
	//			fLabel.setHTML(ClientConstants.FORMAT_INTEGER.format(result));
	//		}
	//	};
	//	aService.countIstanzeByTipoAbbonamento(idTipoAbbonamento, DateUtil.now(), labelCallback);
	//}
	
	
	//Inner classes

	
	public static class ListiniByPeriodicoDateModel implements DataModel<Listini> {
		private final TipiAbbServiceAsync tipiAbbService = GWT.create(TipiAbbService.class);
		private Integer idPeriodico = null;
		private Date dt = null;
		
		public ListiniByPeriodicoDateModel(Integer idPeriodico, Date dt) {
			this.idPeriodico=idPeriodico;
			this.dt = dt;
		}
		
		@Override
		public void find(int offset, int pageSize,
				AsyncCallback<List<Listini>> callback) {
			//WaitSingleton.get().start();
			tipiAbbService.findListiniByPeriodicoDate(idPeriodico, dt, null, offset, pageSize, callback);
		}
	}
	
	public static class StoricoListiniModel implements DataModel<Listini> {
		private final TipiAbbServiceAsync tipiAbbService = GWT.create(TipiAbbService.class);
		private Integer idTipoAbb = null;
		
		public StoricoListiniModel(Integer idTipoAbb) {
			this.idTipoAbb=idTipoAbb;
		}
		
		@Override
		public void find(int offset, int pageSize,
				AsyncCallback<List<Listini>> callback) {
			//WaitSingleton.get().start();
			tipiAbbService.findListiniByTipoAbb(idTipoAbb, offset, pageSize, callback);
		}
	}
}
