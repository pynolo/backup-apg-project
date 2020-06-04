package it.giunti.apg.client.widgets.tables;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.UriManager;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.services.AbbonamentiService;
import it.giunti.apg.client.services.AbbonamentiServiceAsync;
import it.giunti.apg.client.widgets.CreditoLabel;
import it.giunti.apg.client.widgets.MiniInstanceLabel;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;

public class IstanzeAbbonamentiTable extends PagingTable<IstanzeAbbonamenti> {

	private static final int TABLE_ROWS = AppConstants.TABLE_ROWS_DEFAULT;
	public static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat("dd/MM/yyyy");
	private static final AbbonamentiServiceAsync abbonamentiService = GWT.create(AbbonamentiService.class);
		
	private boolean quickSearch = false; //se caricare direttamente la pagina col risultato singolo
	private IRefreshable parent;
	
	private AsyncCallback<List<IstanzeAbbonamenti>> callback = new AsyncCallback<List<IstanzeAbbonamenti>>() {
		@Override
		public void onFailure(Throwable caught) {
			setTableRows(null);
			//WaitSingleton.get().stop();
		}
		@Override
		public void onSuccess(List<IstanzeAbbonamenti> result) {
			if (result != null) {
				if (result.size() == 1 && quickSearch) {
					UriParameters params = new UriParameters();
					params.add(AppConstants.PARAM_ID, result.get(0).getId());
					params.triggerUri(UriManager.ABBONAMENTO);
				}
			}
			setTableRows(result);
			//WaitSingleton.get().stop();
		}
	};
	
	public IstanzeAbbonamentiTable(DataModel<IstanzeAbbonamenti> dataModel, boolean quickSearch, IRefreshable parent) {
		super(dataModel, TABLE_ROWS);
		this.quickSearch=quickSearch;
		this.parent = parent;
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
	protected void addTableRow(int rowNum, IstanzeAbbonamenti rowObj) {
		// Set the data in the current row
		String hp = "";//htmlPrefix
		String hs = "";//htmlSuffix
		if (rowObj.getUltimaDellaSerie()) {
			hp = "<b>";
			hs = "</b>";
		}
		int idAnagrafica = rowObj.getAbbonato().getId();
		//Codice abbonamento
		String linkText = hp+rowObj.getAbbonamento().getCodiceAbbonamento().toString()+hs;
		if (rowObj.getOpzioniIstanzeAbbonamentiSet() != null) {
			if (rowObj.getOpzioniIstanzeAbbonamentiSet().size() > 0) linkText += " " + ClientConstants.ICON_OPZIONI;
		}
		UriParameters params1 = new UriParameters();
		params1.add(AppConstants.PARAM_ID_ANAGRAFICA, idAnagrafica);
		params1.add(AppConstants.PARAM_ID, rowObj.getId());
		Hyperlink rowLink;
		if (rowObj.getNecessitaVerifica()) {
			rowLink = params1.getHyperlink(ClientConstants.ICON_HAND_RIGHT+" "+linkText, UriManager.ABBONAMENTO);
		} else {
			rowLink = params1.getHyperlink(linkText, UriManager.ABBONAMENTO);
		}
		getInnerTable().setWidget(rowNum, 0, rowLink);
		//Tipo abbonamento
		String ta = "<b>" + rowObj.getListino().getTipoAbbonamento().getCodice() + "</b> " +
				rowObj.getListino().getTipoAbbonamento().getNome();
		getInnerTable().setHTML(rowNum, 1, ta);
		//Destinatario
		String destinatario = rowObj.getAbbonato().getIndirizzoPrincipale().getCognomeRagioneSociale();
		if (rowObj.getAbbonato().getIndirizzoPrincipale().getNome() != null) {
			destinatario += " " + rowObj.getAbbonato().getIndirizzoPrincipale().getNome();
		}
		destinatario = /*ClientConstants.ICON_MAGNIFIER+*/"&nbsp;<b>"+destinatario+"</b>";
		UriParameters params2 = new UriParameters();
		params2.add(AppConstants.PARAM_ID, rowObj.getAbbonato().getId());
		Hyperlink destLink = params2.getHyperlink(destinatario, UriManager.ANAGRAFICHE_MERGE);
		getInnerTable().setWidget(rowNum, 2, destLink);
		//Inizio
		getInnerTable().setHTML(rowNum, 3, ClientConstants.FORMAT_MONTH.format(rowObj.getDataInizio()));
		//Fine
		getInnerTable().setHTML(rowNum, 4, ClientConstants.FORMAT_MONTH.format(rowObj.getDataInizio()));
		//Icona di stato
		FlowPanel statusPanel = new FlowPanel();
		MiniInstanceLabel stato = new MiniInstanceLabel(rowObj, true);
		statusPanel.add(stato);
		Anagrafiche pagante = rowObj.getAbbonato();
		if (rowObj.getPagante() != null) pagante = rowObj.getPagante();
		statusPanel.add(new CreditoLabel(pagante.getId(),
				rowObj.getListino().getTipoAbbonamento().getPeriodico().getIdSocieta()));
		getInnerTable().setWidget(rowNum, 5, statusPanel);
		//UID Istanza
		String istanza = "<b>["+rowObj.getId()+"]</b>";
		getInnerTable().setHTML(rowNum, 6, istanza);
	}
	
	@Override
	protected void addHeader() {
		// Set the data in the current row
		getInnerTable().setHTML(0, 0, "Codice");
		getInnerTable().setHTML(0, 1, "Tipo");
		getInnerTable().setHTML(0, 2, "Destinatario");
		getInnerTable().setHTML(0, 3, "Inizio");
		getInnerTable().setHTML(0, 4, "Fine");
		getInnerTable().setHTML(0, 5, "Stato");
		getInnerTable().setHTML(0, 6, "UID");
	}
	
	@Override
	protected void onEmptyResult() {
		if (parent != null) parent.refresh();
	}
	
	
	//Inner classes

	
	
	public static class FindIstanzeModel implements DataModel<IstanzeAbbonamenti> {
		private String searchString = null;
		
		public FindIstanzeModel(String searchString) {
			this.searchString=searchString;
		}
		
		@Override
		public void find(int offset, int pageSize,
				AsyncCallback<List<IstanzeAbbonamenti>> callback) {
			//WaitSingleton.get().start();
			abbonamentiService.quickSearchIstanzeAbbonamenti(searchString, offset, pageSize, callback);
		}
	}
	
	public static class StoricoIstanzeModel implements DataModel<IstanzeAbbonamenti> {
		private Integer idAbbonamento = null;
		
		public StoricoIstanzeModel(Integer idAbbonamento) {
			this.idAbbonamento=idAbbonamento;
		}
		
		@Override
		public void find(int offset, int pageSize,
				AsyncCallback<List<IstanzeAbbonamenti>> callback) {
			//WaitSingleton.get().start();
			abbonamentiService.findIstanzeByAbbonamento(idAbbonamento, callback);
		}
	}
	
	public static class LastModifiedModel implements DataModel<IstanzeAbbonamenti> {
		Integer idPeriodico = null;
		
		public LastModifiedModel(Integer idPeriodico) {
			this.idPeriodico = idPeriodico;
		}
		
		@Override
		public void find(int offset, int pageSize,
				AsyncCallback<List<IstanzeAbbonamenti>> callback) {
			//WaitSingleton.get().start();
			abbonamentiService.findIstanzeByLastModified(idPeriodico, offset, pageSize, callback);
		}
	}
	
	public static class IstanzeProprieByAnagraficaModel implements DataModel<IstanzeAbbonamenti> {
		private Integer idAnagrafica = null;
		private boolean onlyLatest = false;
		
		public IstanzeProprieByAnagraficaModel(Integer idAnagrafica, boolean onlyLatest) {
			this.idAnagrafica=idAnagrafica;
			this.onlyLatest=onlyLatest;
		}
		
		@Override
		public void find(int offset, int pageSize,
				AsyncCallback<List<IstanzeAbbonamenti>> callback) {
			//WaitSingleton.get().start();
			abbonamentiService.findIstanzeProprieByAnagrafica(idAnagrafica, onlyLatest, offset, pageSize, callback);
		}
	}
	
	public static class IstanzeRegalateByAnagraficaModel implements DataModel<IstanzeAbbonamenti> {
		private Integer idAnagrafica = null;
		private boolean onlyLatest = false;
		
		public IstanzeRegalateByAnagraficaModel(Integer idAnagrafica, boolean onlyLatest) {
			this.idAnagrafica=idAnagrafica;
			this.onlyLatest=onlyLatest;
		}
		
		@Override
		public void find(int offset, int pageSize,
				AsyncCallback<List<IstanzeAbbonamenti>> callback) {
			//WaitSingleton.get().start();
			abbonamentiService.findIstanzeRegalateByAnagrafica(idAnagrafica, onlyLatest, offset, pageSize, callback);
		}
	}
	
	public static class IstanzePromosseByAnagraficaModel implements DataModel<IstanzeAbbonamenti> {
		private Integer idAnagrafica = null;
		private boolean onlyLatest = false;
		
		public IstanzePromosseByAnagraficaModel(Integer idAnagrafica, boolean onlyLatest) {
			this.idAnagrafica=idAnagrafica;
			this.onlyLatest=onlyLatest;
		}
		
		@Override
		public void find(int offset, int pageSize,
				AsyncCallback<List<IstanzeAbbonamenti>> callback) {
			//WaitSingleton.get().start();
			abbonamentiService.findIstanzePromosseByAnagrafica(idAnagrafica, onlyLatest, offset, pageSize, callback);
		}
	}
	
	//public static class IstanzeConCredito implements DataModel<IstanzeAbbonamenti> {
	//	private String idSocieta = null;
	//	private boolean regalo = false;
	//	
	//	public IstanzeConCredito(String idSocieta, boolean regalo) {
	//		this.idSocieta = idSocieta;
	//		this.regalo = regalo;
	//	}
	//	
	//	@Override
	//	public void find(int offset, int pageSize,
	//			AsyncCallback<List<IstanzeAbbonamenti>> callback) {
	//		//WaitSingleton.get().start();
	//		abbonamentiService.findIstanzeConCreditoBySocieta(idSocieta, MONTHS_EXPIRED, regalo, offset, pageSize, callback);
	//	}
	//}
	
}
