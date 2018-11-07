package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.PagamentiService;
import it.giunti.apg.client.services.PagamentiServiceAsync;
import it.giunti.apg.client.widgets.VersioningPanel;
import it.giunti.apg.client.widgets.select.SocietaSelect;
import it.giunti.apg.client.widgets.select.TipiPagamentoSelect;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.model.Pagamenti;
import it.giunti.apg.shared.model.Utenti;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DateBox;

public class PagamentoPopUp extends PopupPanel implements IAuthenticatedWidget {

	private final PagamentiServiceAsync pagamentiService = GWT.create(PagamentiService.class);
	
	private FlexTable table = new FlexTable();
	private Integer idPagamento = null;
	private Pagamenti item = null;
	//private Integer idIstanza = null;
	private Integer idAnagrafica = null;
	private String codAbbo = null;
	private IRefreshable parent = null;
	private String idSocieta = null;
	
	private boolean isOperator = false;
	//private boolean isEditor = false;
	private boolean isAdmin = false;
	private boolean isSuper = false;
	
	private TipiPagamentoSelect tipoPagamentoList = null;
	private TextBox importoText = null;
	private SocietaSelect societaList = null;
	private DateBox pagaDate = null;
	private DateBox accrDate = null;
	private TextBox trnText = null;
	private TextBox noteText = null;
	
	//public PagamentoPopUp(Integer idIstanza, String codiceAbbonamento, IRefreshable parent) {
	//	super(false);
	//	this.idPagamento=AppConstants.NEW_ITEM_ID;
	//	this.idIstanza=idIstanza;
	//	this.codiceAbbonamento=codiceAbbonamento;
	//	this.parent=parent;
	//	AuthSingleton.get().queueForAuthentication(this);
	//}
	
	public PagamentoPopUp(IRefreshable parent, Integer idAnagrafica, String codAbbo, String idSocieta) {
		super(false);
		this.idPagamento=AppConstants.NEW_ITEM_ID;
		this.idAnagrafica = idAnagrafica;
		this.parent=parent;
		this.codAbbo=codAbbo;
		this.idSocieta=idSocieta;
		AuthSingleton.get().queueForAuthentication(this);
	}
	
	public PagamentoPopUp(IRefreshable parent, Integer idPagamento, String idSocieta) {
		super(false);
		this.idPagamento=idPagamento;
		//this.idIstanza=null;
		//this.codiceAbbonamento=null;
		this.parent=parent;
		this.idSocieta=idSocieta;
		AuthSingleton.get().queueForAuthentication(this);
	}

	@Override
	public void onSuccessfulAuthentication(Utenti utente) {
		init(utente);
	}
	
	private void init(Utenti utente) {
		// Editing rights
		int ruolo = utente.getRuolo().getId();
		isOperator = (ruolo >= AppConstants.RUOLO_OPERATOR);
		//isEditor = (ruolo >= AppConstants.RUOLO_EDITOR);
		isAdmin = (ruolo >= AppConstants.RUOLO_ADMIN);
		isSuper = (ruolo >= AppConstants.RUOLO_SUPER);
		//UI
		if (isOperator) {
			this.setModal(true);
			this.setGlassEnabled(true);
			this.add(table);
			loadPagamento();
		}
	}
	
	private void drawPagamento() {
		boolean fatturato = (item.getIdFattura() != null);
		int r=0;
		
		HTML titleHtml = new HTML("Pagamento");
		titleHtml.setStyleName("frame-title");
		table.setWidget(r, 0, titleHtml);
		table.getFlexCellFormatter().setColSpan(r, 0, 5);
		r++;
		
		//Societa
		table.setHTML(r, 0, "Societ&agrave;");
		String idS = this.idSocieta;
		if (item.getIdSocieta() != null) idS = item.getIdSocieta();
		societaList = new SocietaSelect(idS);
		societaList.setEnabled(isOperator&&!fatturato);
		societaList.setFocus(true);
		societaList.setEnabled(isAdmin);
		table.setWidget(r, 1, societaList);
		table.setHTML(r, 3, "Fattura creata");
		CheckBox fatturaBox = new CheckBox();
		fatturaBox.setEnabled(false);
		fatturaBox.setValue(fatturato);
		table.setWidget(r, 4, fatturaBox);
		r++;
		
		//Tipo Pagamento
		table.setHTML(r, 0, "Tipo pagamento");
		tipoPagamentoList = new TipiPagamentoSelect(item.getIdTipoPagamento());
		tipoPagamentoList.setEnabled(isOperator&&!fatturato);
		tipoPagamentoList.setFocus(true);
		table.setWidget(r, 1, tipoPagamentoList);
		//Importo
		table.setHTML(r, 3, "Importo"+ClientConstants.MANDATORY);
		importoText = new TextBox();
		importoText.setValue(ClientConstants.FORMAT_CURRENCY.format(item.getImporto()));
		importoText.setEnabled(isOperator&&!fatturato);
		table.setWidget(r, 4, importoText);
		r++;
		
		//Data pagamento
		table.setHTML(r, 0, "Data pagamento"+ClientConstants.MANDATORY);
		pagaDate = new DateBox();
		pagaDate.setFormat(ClientConstants.BOX_FORMAT_DAY);
		pagaDate.setValue(item.getDataPagamento());
		if (isOperator&&!fatturato) {
			table.setWidget(r, 1, pagaDate);
		} else {
			table.setHTML(r, 1, "<b>"+ClientConstants.FORMAT_DAY.format(item.getDataPagamento())+"</b>");
		}
		//Data creazione
		table.setHTML(r, 3, "Data registrazione"+ClientConstants.MANDATORY);
		accrDate = new DateBox();
		accrDate.setFormat(ClientConstants.BOX_FORMAT_DAY);
		accrDate.setValue(item.getDataAccredito());
		if (isSuper&&!fatturato) {
			table.setWidget(r, 4, accrDate);
		} else {
			table.setHTML(r, 4, "<b>"+ClientConstants.FORMAT_DAY.format(item.getDataAccredito())+"</b>");
		}
		r++;

		//TRN pagamento
		table.setHTML(r, 0, "TRN");
		trnText = new TextBox();
		trnText.setValue(item.getTrn());
		if (isOperator) {
			table.setWidget(r, 1, trnText);
		} else {
			table.setHTML(r, 1, item.getTrn());
		}
		//Codice originale
		if (item.getCodiceAbbonamentoBollettino() != null) {
			if (item.getCodiceAbbonamentoBollettino().length() > 0) {
				table.setHTML(r, 3, "Versato per "+item.getCodiceAbbonamentoBollettino());
				//table.getFlexCellFormatter().setColSpan(r, 0, 2);
			}
		}
		r++;
		
		//Note
		table.setHTML(r, 0, "Note");
		noteText = new TextBox();
		noteText.setValue(item.getNote());
		noteText.setMaxLength(250);
		noteText.setEnabled(isOperator);
		noteText.setWidth("95%");
		table.setWidget(r, 1, noteText);
		table.getFlexCellFormatter().setColSpan(r, 1, 4);
		r++;
		
		//Note
		if (item.getId() != null) table.setHTML(r, 4, "<i>UID["+item.getId()+"]</i>");
		//table.getFlexCellFormatter().setColSpan(r, 1, 4);
		r++;
		
		HorizontalPanel buttonPanel = new HorizontalPanel();
		// Bottone SALVA
		Button submitButton = new Button(ClientConstants.ICON_SAVE+" Salva", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				try {
					saveData();
					close();
				} catch (Exception e) {
					UiSingleton.get().addError(e);
				}
			}
		});
		if (idPagamento.equals(AppConstants.NEW_ITEM_ID)) {
			submitButton.setHTML(ClientConstants.ICON_SAVE+" Crea");
		}
		submitButton.setEnabled(isOperator);
		buttonPanel.add(submitButton);
		
		// Bottone ANNULLA
		Button cancelButton = new Button(ClientConstants.ICON_CANCEL+" Annulla", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				close();
			}
		});
		buttonPanel.add(cancelButton);
		
		table.setWidget(r,0,buttonPanel);
		table.getFlexCellFormatter().setColSpan(r, 0, 5);
		r++;
		
		if (item.getId() != null) {
			//PANNELLO VERSIONAMENTO
			VersioningPanel versionPanel = new VersioningPanel(
					"Pagamenti", item.getId(), item.getIdUtente(), item.getDataModifica());
			table.setWidget(r, 0, versionPanel);
			table.getFlexCellFormatter().setColSpan(r, 0, 5);
		}
		
		//Info modifica
//		if ((item.getDataModifica() != null) && (item.getId() != null)) {
//			r++;
//			String userName = item.getUtente().getDescrizione();
//			if (userName == null) userName = item.getUtente().getId();
//			if (userName.equals("")) userName = item.getUtente().getId();
//			InlineHTML modifiedInfo = new InlineHTML("<br/><i>Modificato o creato da "+userName+" il "+
//					ClientConstants.FORMAT_TIMESTAMP.format(item.getDataModifica())+"</i>");
//			table.setWidget(r,0,modifiedInfo);
//			table.getFlexCellFormatter().setColSpan(r, 0, 5);
//		}
		
		this.center();
		this.show();
	}
	
	private void close() {
		this.hide();
	}
	
	
	
	// METODI ASINCRONI
	
	
	
	private void saveData() throws ValidationException {
		AsyncCallback<Integer> callback = new AsyncCallback<Integer>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				parent.refresh();
				close();
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(Integer result) {			
				idPagamento = result;
				parent.refresh();
				close();
				UiSingleton.get().addInfo(AppConstants.MSG_SAVE_OK);
				WaitSingleton.get().stop();
			}
		};
		item.setDataModifica(DateUtil.now());
		if (pagaDate.getValue() != null) {
			item.setDataPagamento(pagaDate.getValue());
		} else {
			throw new ValidationException("La data del pagamento e' obbligatoria");
		}
		if (accrDate.getValue() != null) {
			item.setDataAccredito(accrDate.getValue());
		} else {
			throw new ValidationException("La data di accredito e' obbligatoria");
		}
		try {
			item.setImporto(ClientConstants.FORMAT_CURRENCY.parse(importoText.getValue()));
		} catch (NumberFormatException e) {
			throw new ValidationException("Importo non corretto: "+importoText.getValue());
		}
		if (item.getImporto() < AppConstants.SOGLIA) throw new ValidationException("L'importo deve essere maggiore di zero");
		String idTipo = tipoPagamentoList.getSelectedValueString();
		item.setIdTipoPagamento(idTipo);
		item.setIdSocieta(societaList.getSelectedValueString());
		item.setNote(noteText.getValue());
		item.setCodiceAbbonamentoMatch(codAbbo);
		item.setTrn(trnText.getValue());
		item.setIdUtente(AuthSingleton.get().getUtente().getId());
		
		WaitSingleton.get().start();
		pagamentiService.saveOrUpdate(item, callback);
	}

	private void loadPagamento() {
		AsyncCallback<Pagamenti> callback = new AsyncCallback<Pagamenti>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(Pagamenti result) {
				item = result;
				drawPagamento();
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		//look for item with id only if id is defined
		if (idPagamento.intValue() != AppConstants.NEW_ITEM_ID) {
			pagamentiService.findPagamentoById(idPagamento, callback);
		} else {
			//is new abbonamento
			pagamentiService.createPagamentoManuale(idAnagrafica, callback);
		}
	}
}
