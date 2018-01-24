package it.giunti.apg.client.widgets.tables;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.frames.EvasioneComunicazionePopUp;
import it.giunti.apg.client.services.ComunicazioniService;
import it.giunti.apg.client.services.ComunicazioniServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.EvasioniComunicazioni;
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

public class EvasioniComunicazioniTable extends PagingTable<EvasioniComunicazioni> implements IRefreshable {
	private static final ComunicazioniServiceAsync comService = GWT.create(ComunicazioniService.class);
	
	private static final int TABLE_ROWS = 200;
	
	private boolean showDelete = false;
	
	private AsyncCallback<List<EvasioniComunicazioni>> callback = new AsyncCallback<List<EvasioniComunicazioni>>() {
		@Override
		public void onFailure(Throwable caught) {
			setTableRows(new ArrayList<EvasioniComunicazioni>());
			//WaitSingleton.get().stop();
		}
		@Override
		public void onSuccess(List<EvasioniComunicazioni> result) {
			setTableRows(result);
			//WaitSingleton.get().stop();
		}
	};
	
	public EvasioniComunicazioniTable(DataModel<EvasioniComunicazioni> model, Ruoli userRole) {
		super(model, TABLE_ROWS);
		showDelete = (userRole.getId().intValue() >= AppConstants.RUOLO_OPERATOR);
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
	protected void addTableRow(int rowNum, EvasioniComunicazioni rowObj) {
		final Integer idIstanza = rowObj.getIstanzaAbbonamento().getId();
		final Integer idEc = rowObj.getId();
		final IRefreshable fThis = this;
		// Set the data in the current row
		//Descrizione
		String descr = "Manuale";
		if (rowObj.getComunicazione() != null) {
			descr = rowObj.getComunicazione().getTitolo();
			getInnerTable().setHTML(rowNum, 0, "<b>"+descr+"</b>&nbsp;");
		} else {
			Anchor itemLink = new Anchor("<b>"+descr+"</b>", true);
			itemLink.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					EvasioneComunicazionePopUp popUp = new EvasioneComunicazionePopUp();
					popUp.initByEvasioneComunicazione(idEc, fThis);
				}
			});
			getInnerTable().setWidget(rowNum, 0, itemLink);
		}
		// Tipo comunicazione (media)
		String mediaDescr = AppConstants.COMUN_MEDIA_DESC.get(rowObj.getIdTipoMedia());
		if (rowObj.getProgressivo() != null) mediaDescr += " "+rowObj.getProgressivo();
		if (rowObj.getRichiestaRinnovo()) mediaDescr += " rinnovo";
		getInnerTable().setHTML(rowNum, 1, mediaDescr+"&nbsp;");
		//Destinatario
		String destinatario = "";
		if (AppConstants.DEST_BENEFICIARIO.equals(rowObj.getIdTipoDestinatario())) {
			destinatario = rowObj.getIstanzaAbbonamento().getAbbonato().getIndirizzoPrincipale().getCognomeRagioneSociale();
			if (rowObj.getIstanzaAbbonamento().getAbbonato().getIndirizzoPrincipale().getNome() != null) {
				destinatario += " " + rowObj.getIstanzaAbbonamento().getAbbonato().getIndirizzoPrincipale().getNome();
			}
		}
		if (AppConstants.DEST_PAGANTE.equals(rowObj.getIdTipoDestinatario()) &&
				(rowObj.getIstanzaAbbonamento().getPagante() != null)) {
			destinatario = rowObj.getIstanzaAbbonamento().getPagante().getIndirizzoPrincipale().getCognomeRagioneSociale();
			if (rowObj.getIstanzaAbbonamento().getPagante().getIndirizzoPrincipale().getNome() != null) {
				destinatario += " " + rowObj.getIstanzaAbbonamento().getPagante().getIndirizzoPrincipale().getNome();
			}
		}
		if (AppConstants.DEST_PROMOTORE.equals(rowObj.getIdTipoDestinatario()) &&
				(rowObj.getIstanzaAbbonamento().getPromotore() != null)) {
			destinatario = rowObj.getIstanzaAbbonamento().getPromotore().getIndirizzoPrincipale().getCognomeRagioneSociale();
			if (rowObj.getIstanzaAbbonamento().getPromotore().getIndirizzoPrincipale().getNome() != null) {
				destinatario += " " + rowObj.getIstanzaAbbonamento().getPromotore().getIndirizzoPrincipale().getNome();
			}
		}
		getInnerTable().setHTML(rowNum, 2, destinatario);
		//Data estrazione
		String estrazione = "";
		if (rowObj.getEliminato()) {
			estrazione += "<b>Spedizione annullata</b> ";
		} else {
			if (rowObj.getDataEstrazione() != null) {
				estrazione += "<b>"+ClientConstants.FORMAT_DAY.format(rowObj.getDataEstrazione())+"</b>&nbsp;";
			} else {
				estrazione = "--&nbsp;";
			}
		}
		getInnerTable().setHTML(rowNum, 3, estrazione);
		//Importo
		String importo = "";
		if (rowObj.getImportoStampato() != null)
			importo += "&euro;"+ClientConstants.FORMAT_CURRENCY.format(rowObj.getImportoStampato())+" ";
		if (rowObj.getImportoAlternativoStampato() != null)
			importo += "&amp; &euro;"+ClientConstants.FORMAT_CURRENCY.format(rowObj.getImportoAlternativoStampato())+" ";
		getInnerTable().setHTML(rowNum, 4, importo);
		//Note
		String note = "";
		if (rowObj.getNote() != null) note = rowObj.getNote();
		getInnerTable().setHTML(rowNum, 5, note+" <i>("+rowObj.getIdUtente()+")</i>");
		//Elimina
		if ((rowObj.getDataEstrazione() == null) && showDelete ) {
			InlineHTML trashImg = new InlineHTML(ClientConstants.ICON_DELETE);
			trashImg.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent arg0) {
					confirmAndDelete(idIstanza, idEc);
				}
			});
			getInnerTable().setWidget(rowNum, 6, trashImg);
		}
	}
	
	@Override
	protected void addHeader() {
		// Set the data in the current row
		getInnerTable().setHTML(0, 0, "Tipo");
		getInnerTable().setHTML(0, 1, "Media");
		getInnerTable().setHTML(0, 2, "Destinatario");
		getInnerTable().setHTML(0, 3, "Estrazione");
		getInnerTable().setHTML(0, 4, "Importo");
		getInnerTable().setHTML(0, 5, "Note");
	}
	
	@Override
	protected void onEmptyResult() {}
	
	private void confirmAndDelete(Integer idIstanza, Integer idEvasioneComunicazione) {
		boolean confirm = Window.confirm("Vuoi veramente cancellare la comunicazione?");
		if (confirm) {
			delete(idIstanza, idEvasioneComunicazione);
		}
	}
	
	public void delete(Integer idIstanza, Integer idEvasioneComunicazione) {
		//WaitSingleton.get().start();
		comService.deleteEvasioneComunicazione(idIstanza, idEvasioneComunicazione, callback);
	}

	
	
	
	//Inner classes
	
	
	
	public static class EvasioniComunicazioniModel implements DataModel<EvasioniComunicazioni> {
		private Integer idIstanza = null;
		
		public EvasioniComunicazioniModel(Integer idIstanza) {
			this.idIstanza=idIstanza;
		}
		
		@Override
		public void find(int offset, int pageSize,
				AsyncCallback<List<EvasioniComunicazioni>> callback) {
			//WaitSingleton.get().start();
			comService.findEvasioniComunicazioniByIstanza(idIstanza, callback);
		}
	}
	
}
