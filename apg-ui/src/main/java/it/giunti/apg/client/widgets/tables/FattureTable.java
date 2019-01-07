package it.giunti.apg.client.widgets.tables;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.services.PagamentiService;
import it.giunti.apg.client.services.PagamentiServiceAsync;
import it.giunti.apg.client.widgets.FatturaActionPanel;
import it.giunti.apg.client.widgets.FatturaPubblicaCheckBox;
import it.giunti.apg.client.widgets.FatturaStampaLink;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Fatture;
import it.giunti.apg.shared.model.Utenti;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;

public class FattureTable extends PagingTable<Fatture> implements IRefreshable {
	private static final PagamentiServiceAsync paymentService = GWT.create(PagamentiService.class);

	private static final int TABLE_ROWS = 200;
	
	private IRefreshable parent = null;
	private Utenti utente = null;
	private boolean isOperator = false;
	private boolean isAdmin = false;

	
	private AsyncCallback<List<Fatture>> callback = new AsyncCallback<List<Fatture>>() {
		@Override
		public void onFailure(Throwable caught) {
			setTableRows(new ArrayList<Fatture>());
			//WaitSingleton.get().stop();
		}
		@Override
		public void onSuccess(List<Fatture> result) {
			setTableRows(result);
			//WaitSingleton.get().stop();
		}
	};
	
	public FattureTable(DataModel<Fatture> model, Utenti utente,
			IRefreshable parent) {
		super(model, TABLE_ROWS);
		this.utente = utente;
		this.parent = parent;
		isOperator = (utente.getRuolo().getId().intValue() >= AppConstants.RUOLO_OPERATOR);
		isAdmin = (utente.getRuolo().getId().intValue() >= AppConstants.RUOLO_ADMIN);
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
	protected void addTableRow(int rowNum, Fatture rowObj) {
		boolean isNotaCred = rowObj.getIdTipoDocumento().equalsIgnoreCase(AppConstants.DOCUMENTO_NOTA_CREDITO);
		
		// Set the data in the current row
		HorizontalPanel fPanel = new HorizontalPanel();
		if (isOperator) {
			FatturaStampaLink fsLink = new FatturaStampaLink(rowObj.getId(), false);
			fPanel.add(fsLink);
		} else {
			if (isNotaCred) {
				fPanel.add(new InlineHTML("<i>"+rowObj.getNumeroFattura()+"</i>"));
			} else {
				fPanel.add(new InlineHTML("<b>"+rowObj.getNumeroFattura()+"</b>"));
			}
		}
		getInnerTable().setWidget(rowNum, 0, fPanel);
		//Importo
		getInnerTable().setHTML(rowNum, 1, 
				ClientConstants.FORMAT_CURRENCY.format(rowObj.getTotaleFinale())+"&nbsp;");
		//Data fattura
		getInnerTable().setHTML(rowNum, 2, 
				ClientConstants.FORMAT_DAY.format(rowObj.getDataFattura()));
		//Pubblica
		FatturaPubblicaCheckBox pubBox = 
				new FatturaPubblicaCheckBox(rowObj.getId(), rowObj.getPubblica());
		pubBox.setEnabled(isAdmin);
		getInnerTable().setWidget(rowNum, 3, pubBox);
		//Rigenera, rimborsi e storni
		FatturaActionPanel faPanel = new FatturaActionPanel(rowObj, utente, this);
		getInnerTable().setWidget(rowNum, 4, faPanel);
	}
	
	@Override
	protected void addHeader() {
		// Set the data in the current row
		getInnerTable().setHTML(0, 0, "Numero");
		getInnerTable().setHTML(0, 1, "Importo");
		getInnerTable().setHTML(0, 2, "Data emissione");
		getInnerTable().setHTML(0, 3, "Pubblica");
		getInnerTable().setHTML(0, 4, "&nbsp;");
	}
	
	@Override
	protected void onEmptyResult() {}

	
	
	//Inner classes
	
	
	
	public static class FattureByAnagraficaModel implements DataModel<Fatture> {
		private Integer idAnagrafica = null;
		
		public FattureByAnagraficaModel(Integer idAnagrafica) {
			this.idAnagrafica=idAnagrafica;
		}
		
		@Override
		public void find(int offset, int pageSize,
				AsyncCallback<List<Fatture>> callback) {
			paymentService.findFattureByAnagrafica(idAnagrafica, false, callback);
		}
	}
	
	public static class FattureByIstanzaModel implements DataModel<Fatture> {
		private Integer idIstanza = null;
		
		public FattureByIstanzaModel(Integer idIstanza) {
			this.idIstanza=idIstanza;
		}
		
		@Override
		public void find(int offset, int pageSize,
				AsyncCallback<List<Fatture>> callback) {
			paymentService.findFattureByIstanza(idIstanza, false, callback);
		}
	}
}
