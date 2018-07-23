package it.giunti.apg.client.widgets.tables;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineHTML;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.UriManager;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.services.AnagraficheService;
import it.giunti.apg.client.services.AnagraficheServiceAsync;
import it.giunti.apg.client.widgets.MiniInstancePanel;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Anagrafiche;

public class AnagraficheTable extends PagingTable<Anagrafiche> {
	
	private static final int TABLE_ROWS = AppConstants.TABLE_ROWS_DEFAULT;
	
	private boolean quickSearch = false; //se caricare direttamente la pagina col risultato singolo
	private IRefreshable parent;
	
	private AsyncCallback<List<Anagrafiche>> callback = new AsyncCallback<List<Anagrafiche>>() {
		@Override
		public void onFailure(Throwable caught) {
			setTableRows(new ArrayList<Anagrafiche>());
			//WaitSingleton.get().stop();
		}
		@Override
		public void onSuccess(List<Anagrafiche> result) {
			if (result != null) {
				if (result.size() == 1 && quickSearch) {
					UriParameters params = new UriParameters();
					params.add(AppConstants.PARAM_ID, result.get(0).getId());
					params.triggerUri(UriManager.ANAGRAFICA);
				}
			}
			setTableRows(result);
			//WaitSingleton.get().stop();
		}
	};
	
	public AnagraficheTable(DataModel<Anagrafiche> model, boolean quickSearch, IRefreshable parent) {
		super(model, TABLE_ROWS);
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
	protected void addTableRow(int rowNum, Anagrafiche rowObj) {
		// Set the data in the current row
		//Nome-link
		String linkText = rowObj.getIndirizzoPrincipale().getCognomeRagioneSociale();
		if (rowObj.getIndirizzoPrincipale().getNome() != null) {
			linkText += " " + rowObj.getIndirizzoPrincipale().getNome();
		}
		linkText = "<b>"+linkText+"</b>";
		UriParameters params = new UriParameters();
		params.add(AppConstants.PARAM_ID, rowObj.getId());
		Hyperlink rowLink = null;
		if (rowObj.getNecessitaVerifica() || (rowObj.getIdAnagraficaDaAggiornare() != null)) {
			rowLink = params.getHyperlink(ClientConstants.ICON_HAND_RIGHT+" "+linkText, UriManager.ANAGRAFICHE_MERGE);
		} else {
			rowLink = params.getHyperlink(linkText, UriManager.ANAGRAFICA);
		}
		getInnerTable().setWidget(rowNum, 0, rowLink);
		//Indirizzo
		String indirizzo = rowObj.getIndirizzoPrincipale().getIndirizzo() + " ";
		if (rowObj.getIndirizzoPrincipale().getCap() != null)
				indirizzo += "<b>"+rowObj.getIndirizzoPrincipale().getCap() + "</b> ";
		if (rowObj.getIndirizzoPrincipale().getLocalita() != null)
				indirizzo += "<b>"+rowObj.getIndirizzoPrincipale().getLocalita()+"</b> ";
		if (rowObj.getIndirizzoPrincipale().getProvincia() != null)
				indirizzo += "("+rowObj.getIndirizzoPrincipale().getProvincia()+")";
		getInnerTable().setHTML(rowNum, 1, indirizzo);
		//Tipo
		if (rowObj.getIdTipoAnagrafica() != null) {
			String anagIcon = ClientConstants.ICON_ANAG_PRIVATO;
			if (!rowObj.getIdTipoAnagrafica().equals(AppConstants.ANAG_PRIVATO)) {
				anagIcon = ClientConstants.ICON_ANAG_SOCIETA;
			}
			getInnerTable().setHTML(rowNum, 2, anagIcon);
		}
		//Stato abb
		MiniInstancePanel mip = new MiniInstancePanel(rowObj.getId(), false, false, true, true);
		getInnerTable().setWidget(rowNum, 3, mip);
		//UID
		InlineHTML codice = new InlineHTML("<b>["+rowObj.getUid()+"]</b>");
		getInnerTable().setWidget(rowNum, 4, codice);
	}
	
	@Override
	protected void addHeader() {
		// Set the data in the current row
		getInnerTable().setHTML(0, 0, "Nome");
		getInnerTable().setHTML(0, 1, "Indirizzo");
		getInnerTable().setHTML(0, 2, "Tipo");
		getInnerTable().setHTML(0, 3, "Ultimi "+ClientConstants.INSTANCE_SHOW_YEARS+" anni");
		getInnerTable().setHTML(0, 4, "UID");
	}
	
	@Override
	protected void onEmptyResult() {
		if (parent != null) parent.refresh();
	}
	
	
	
	//Inner classes
	
	
	
	public static class FindByPropertiesModel implements DataModel<Anagrafiche> {
		private final AnagraficheServiceAsync anagraficheService = GWT.create(AnagraficheService.class);
		private String codAnag = null;
		private String ragSoc = null;
		private String nome = null;
		private String presso = null;
		private String indirizzo = null;
		private String cap = null;
		private String loc = null;
		private String prov = null;
		private String email = null;
		private String cfiva = null;
		private Integer idPeriodico = null;
		private String tipoAbb = null;
		private Date dataValidita = null;
		private String numFat = null;
		
		public FindByPropertiesModel(String codAnag, String ragSoc, 
				String nome, String presso, String indirizzo,
				String cap, String loc, String prov, String email, 
				String cfiva, Integer idPeriodico, String tipoAbb,
				Date dataValidita, String numFat) {
			this.codAnag=codAnag;
			this.ragSoc=ragSoc;
			this.nome=nome;
			this.presso=presso;
			this.indirizzo=indirizzo;
			this.cap=cap;
			this.loc=loc;
			this.prov=prov;
			this.email=email;
			this.cfiva=cfiva;
			this.idPeriodico=idPeriodico;
			this.tipoAbb=tipoAbb;
			this.dataValidita=dataValidita;
			this.numFat=numFat;
		}
		
		@Override
		public void find(int offset, int pageSize,
				AsyncCallback<List<Anagrafiche>> callback) {
			//WaitSingleton.get().start();
			anagraficheService.findByProperties(codAnag,
					ragSoc, nome, presso, indirizzo, cap, loc, prov, email, cfiva, 
					idPeriodico, tipoAbb, dataValidita, numFat, offset, pageSize, callback);
		}
	}
	
	public static class QuickSearchModel implements DataModel<Anagrafiche> {
		private final AnagraficheServiceAsync anagraficheService = GWT.create(AnagraficheService.class);
		private String searchString = null;
		
		public QuickSearchModel(String searchString) {
			this.searchString=searchString;
		}
		
		@Override
		public void find(int offset, int pageSize,
				AsyncCallback<List<Anagrafiche>> callback) {
			//WaitSingleton.get().start();
			anagraficheService.quickSearchAnagrafiche(searchString, offset, pageSize, callback);
		}
	}
	
	public static class LastModifiedModel implements DataModel<Anagrafiche> {
		private final AnagraficheServiceAsync anagraficheService = GWT.create(AnagraficheService.class);
		
		public LastModifiedModel() { }
		
		@Override
		public void find(int offset, int pageSize,
				AsyncCallback<List<Anagrafiche>> callback) {
			//WaitSingleton.get().start();
			anagraficheService.findAnagraficheByLastModified(offset, pageSize, callback);
		}
	}
	
	public static class VerificaModel implements DataModel<Anagrafiche> {
		private final AnagraficheServiceAsync anagraficheService = GWT.create(AnagraficheService.class);
		
		public VerificaModel() { }
		
		@Override
		public void find(int offset, int pageSize,
				AsyncCallback<List<Anagrafiche>> callback) {
			//WaitSingleton.get().start();
			anagraficheService.findAnagraficheToVerify(offset, pageSize, callback);
		}
	}
}
