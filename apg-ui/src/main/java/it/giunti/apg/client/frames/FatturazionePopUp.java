package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.AbbonamentiService;
import it.giunti.apg.client.services.AbbonamentiServiceAsync;
import it.giunti.apg.client.services.OpzioniService;
import it.giunti.apg.client.services.OpzioniServiceAsync;
import it.giunti.apg.client.services.PagamentiService;
import it.giunti.apg.client.services.PagamentiServiceAsync;
import it.giunti.apg.client.widgets.select.ListiniSelect;
import it.giunti.apg.client.widgets.tables.DataModel;
import it.giunti.apg.client.widgets.tables.PagingTable;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.IstanzeStatusUtil;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.Fatture;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Opzioni;
import it.giunti.apg.shared.model.OpzioniIstanzeAbbonamenti;
import it.giunti.apg.shared.model.Pagamenti;
import it.giunti.apg.shared.model.PagamentiCrediti;
import it.giunti.apg.shared.model.Utenti;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class FatturazionePopUp extends PopupPanel implements IAuthenticatedWidget {

	private final AbbonamentiServiceAsync abbonamentiService = GWT.create(AbbonamentiService.class);
	private final PagamentiServiceAsync pagamentiService = GWT.create(PagamentiService.class);
	
	private IstanzeAbbonamenti istanza = null;
	private Integer idPaymentWithError = null;
	private IRefreshable parent = null;
	private Double valoreFatturato = 0D;
	private VerticalPanel opzInclPanel = null;
	private VerticalPanel opzFaclPanel = null;
	
	private Set<Pagamenti> pagSet = new HashSet<Pagamenti>();
	private Set<PagamentiCrediti> credSet = new HashSet<PagamentiCrediti>();
	private Set<Opzioni> opzSet = new HashSet<Opzioni>();
	private Button saveButton = null;
	
	private ListiniSelect listiniList = null;
	private TextBox copieText = null;
	private HTML deltaLabel = null;
	private HTML deltaValue = null;
	
	private static String DA_PAGARE = "Da&nbsp;pagare:&nbsp;";
	private static String RESTO = "RESTO:&nbsp;";
	
	public FatturazionePopUp(IstanzeAbbonamenti istanza, IRefreshable parent) {
		super(false);
		this.istanza=istanza;
		this.valoreFatturato = getValoreFatturato(istanza);
		this.parent=parent;
		AuthSingleton.get().queueForAuthentication(this);
	}
	
	public FatturazionePopUp(IstanzeAbbonamenti istanza, Integer idPaymentWithError, IRefreshable parent) {
		super(false);
		this.istanza=istanza;
		this.valoreFatturato = getValoreFatturato(istanza);
		this.idPaymentWithError = idPaymentWithError;
		this.parent=parent;
		AuthSingleton.get().queueForAuthentication(this);
	}
	
	@Override
	public void onSuccessfulAuthentication(Utenti utente) {
		init(utente);
	}
	
	private void init(Utenti utente) {
		int ruolo = utente.getRuolo().getId();
		//UI
		if (ruolo >= AppConstants.RUOLO_OPERATOR) {
			this.setModal(true);
			this.setGlassEnabled(true);
			draw();
		}
	}
	
	private void draw() {
		VerticalPanel panel = new VerticalPanel();
		//Titolo
		HTML titleHtml = new HTML("Cambia offerta e pagamento");
		titleHtml.setStyleName("frame-title");
		panel.add(titleHtml);
		//Descrizione abbonamento
		HTML descrLabel = new HTML("<br/>Abbonamento <b>"+istanza.getAbbonamento().getCodiceAbbonamento()+
				"</b> ["+istanza.getId()+"] <i>"+
				istanza.getAbbonamento().getPeriodico().getNome()+"</i><br/>&nbsp;");
		panel.add(descrLabel);
		//Cambio tipo abbonamento
		HorizontalPanel listinoPanel = new HorizontalPanel();
		listinoPanel.add(new InlineHTML("Tipo&nbsp;abb.&nbsp;&nbsp;"));
		if (istanza.getIdFattura() == null) {
			listiniList = new ListiniSelect(istanza.getListino().getId(),
					istanza.getAbbonamento().getPeriodico().getId(),
					istanza.getFascicoloInizio().getDataInizio(), true, false, true, true);
			listinoPanel.add(listiniList);
			listiniList.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					refreshOpzioniTables();
				}
			});
		} else {
			listinoPanel.add(new InlineHTML("<b>"+istanza.getListino().getTipoAbbonamento().getCodice()+" "+
					istanza.getListino().getTipoAbbonamento().getNome()+"</b>"));
		}
		panel.add(listinoPanel);
		//Cambio copie
		HorizontalPanel copiePanel = new HorizontalPanel();
		copiePanel.add(new InlineHTML("Copie&nbsp;&nbsp;"));
		if (istanza.getIdFattura() == null) {
			copieText = new TextBox();
			copieText.setWidth("2em");
			copieText.setMaxLength(3);
			copieText.setValue(""+istanza.getCopie());
			copiePanel.add(copieText);
		} else {
			copiePanel.add(new InlineHTML("<b>"+istanza.getCopie()+"</b>"));
		}
		panel.add(copiePanel);
		
		//Opzioni incluse
		opzInclPanel = new VerticalPanel();
		panel.add(opzInclPanel);
		drawOpzioniIncluse(istanza.getListino().getId());
		
		//Opzioni facoltative
		opzFaclPanel = new VerticalPanel();
		panel.add(opzFaclPanel);
		drawOpzioniFacoltative(istanza.getListino().getId(),
				istanza.getFascicoloInizio().getId(),
				istanza.getOpzioniIstanzeAbbonamentiSet());
		
		//Pagamenti nuovi
		HTML nuoviTitle = new HTML("Importi versati");
		nuoviTitle.setStyleName("section-title");
		panel.add(nuoviTitle);
		PagamentiNuoviTable nuoviTable;
		if (idPaymentWithError == null) {
			nuoviTable = new PagamentiNuoviTable(istanza, parent);
		} else {
			nuoviTable = new PagamentiNuoviTable(idPaymentWithError, parent);
		}
		panel.add(nuoviTable);
		//CreditiFatturati
		HTML creditiTitle = new HTML("Crediti");
		creditiTitle.setStyleName("section-title");
		panel.add(creditiTitle);
		Anagrafiche pagante = istanza.getAbbonato();
		if (istanza.getPagante() != null) pagante = istanza.getPagante();
		PagamentiCreditiTable credTable = new PagamentiCreditiTable(pagante.getId(),
				istanza.getFascicoloInizio().getPeriodico().getIdSocieta(), parent);
		panel.add(credTable);
		panel.add(new HTML("&nbsp;"));
		
		// Bilancio
		HorizontalPanel bilancioPanel = new HorizontalPanel();
		deltaLabel = new HTML(DA_PAGARE);
		bilancioPanel.add(deltaLabel);
		deltaValue = new HTML();
		bilancioPanel.add(deltaValue);
		panel.add(bilancioPanel);
		updateAmountLabels();
		panel.add(new HTML("&nbsp;"));
		
		// Bottoni
		HorizontalPanel buttonPanel = new HorizontalPanel();
		saveButton = new Button("Crea fattura", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				try {
					createFattura();
				} catch (ValidationException e) {
					UiSingleton.get().addError(e);
				}
			}
		});
		updateSaveButtonStatus();
		buttonPanel.add(saveButton);
		buttonPanel.add(new InlineHTML("&nbsp;"));
		Button integButton = new Button("Cambia offerta", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				try {
					changeOffering();
				} catch (ValidationException e) {
					UiSingleton.get().addError(e);
				}
			}
		});
		buttonPanel.add(integButton);
		buttonPanel.add(new InlineHTML("&nbsp;"));
		Button cancelButton = new Button("Annulla", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				close();
			}
		});
		buttonPanel.add(cancelButton);
		panel.add(buttonPanel);
		this.add(panel);
		this.center();
		this.show();
	}
	
	private void refreshOpzioniTables() {
		Integer idListino = listiniList.getSelectedValueInt();
		drawOpzioniIncluse(idListino);
		drawOpzioniFacoltative(idListino, istanza.getFascicoloInizio().getId(),
				istanza.getOpzioniIstanzeAbbonamentiSet());
	}
	
	private void updateAmountLabels() {
		AsyncCallback<Double> dovutoCallback = new AsyncCallback<Double>() {
			@Override
			public void onFailure(Throwable caught) {
				if (!(caught instanceof EmptyResultException))
						UiSingleton.get().addError(caught);
			}
			@Override
			public void onSuccess(Double result) {
				drawImportiLabel(result);
			}
		};
		
		Set<Integer> idOpzSet = new HashSet<Integer>();
		for (Opzioni opz:opzSet) idOpzSet.add(opz.getId());
		deltaValue.setHTML(ClientConstants.ICON_LOADING_SMALL+"&nbsp;&nbsp;&nbsp;");
		if (listiniList != null) {
			if (listiniList.getSelectedValueInt() != null) {
				Integer copie = istanza.getCopie();
				if (copieText != null) copie = Integer.parseInt(copieText.getValue());
				pagamentiService.getStimaImportoTotale(listiniList.getSelectedValueInt(),
						copie, idOpzSet, dovutoCallback);
			} else {
				pagamentiService.getStimaImportoTotale(istanza.getListino().getId(),
						istanza.getCopie(), idOpzSet, dovutoCallback);
			}
		} else {
			pagamentiService.getStimaImportoTotale(istanza.getListino().getId(),
					istanza.getCopie(), idOpzSet, dovutoCallback);
		}
	}
	private void drawImportiLabel(Double dovuto) {
		Double pagato = valoreFatturato;
		for (Pagamenti pag:pagSet) pagato += pag.getImporto();
		for (PagamentiCrediti cred:credSet) pagato += cred.getImporto();
		
		Double delta = pagato - dovuto;
		if (delta >= AppConstants.SOGLIA) {
			deltaLabel.setHTML(RESTO);
		} else {
			deltaLabel.setHTML(DA_PAGARE);
			if (delta < 0D) delta = -1*delta;
		}
		//Dovuto
		deltaValue.setHTML("<b>&euro;"+ClientConstants.FORMAT_CURRENCY.format(delta)+"</b>");
	}
	
	private void drawOpzioniIncluse(Integer idListino) {
		opzInclPanel.clear();
		HTML title = new HTML("Opzioni incluse");
		title.setStyleName("section-title");
		opzInclPanel.add(title);
		OpzioniInclTable opzTable = new OpzioniInclTable(idListino, parent);
		opzInclPanel.add(opzTable);
	}
	
	private void drawOpzioniFacoltative(Integer idListino, Integer idFasIni,
			Set<OpzioniIstanzeAbbonamenti> oiaSet) {
		opzFaclPanel.clear();
		HTML title = new HTML("Opzioni facoltative");
		title.setStyleName("section-title");
		opzFaclPanel.add(title);
		OpzioniFaclTable opzTable = new OpzioniFaclTable(idListino, idFasIni, oiaSet, parent);
		opzFaclPanel.add(opzTable);
	}
	
	private void close() {
		this.hide();
	}
	
	private void updateSaveButtonStatus() {
		boolean hasPayments = (pagSet.size() > 0) || (credSet.size() > 0);
		boolean isFatturato = IstanzeStatusUtil.isFatturatoOppureOmaggio(istanza);
		saveButton.setEnabled(hasPayments && !isFatturato);
	}
	
	private void addPagamento(Pagamenti pag) {
		pagSet.add(pag);
		updateSaveButtonStatus();
		updateAmountLabels();
	}
	private void removePagamento(Pagamenti pag) {
		if (pagSet.contains(pag)) pagSet.remove(pag);
		updateSaveButtonStatus();
		updateAmountLabels();
	}
	
	private void addCredito(PagamentiCrediti cred) {
		credSet.add(cred);
		updateSaveButtonStatus();
		updateAmountLabels();
	}
	private void removeCredito(PagamentiCrediti cred) {
		if (credSet.contains(cred)) credSet.remove(cred);
		updateSaveButtonStatus();
		updateAmountLabels();
	}
	
	private void addOpzione(Opzioni opz) {
		opzSet.add(opz);
		updateAmountLabels();
	}
	private void removeOpzione(Opzioni opz) {
		if (opzSet.contains(opz)) opzSet.remove(opz);
		updateAmountLabels();
	}
	
	private void createFattura() throws ValidationException {
		Integer idListino;
		if (listiniList != null) {
			idListino = listiniList.getSelectedValueInt();
		} else {
			idListino = istanza.getListino().getId();
		}
		final List<Integer> idOpzList = new ArrayList<Integer>();
		for (Opzioni opz:opzSet) idOpzList.add(opz.getId());
		final List<Integer> idPagList = new ArrayList<Integer>();
		for (Pagamenti pag:pagSet) idPagList.add(pag.getId());
		final List<Integer> idCredList = new ArrayList<Integer>();
		for (PagamentiCrediti cred:credSet) idCredList.add(cred.getId());
		Integer copie = null;
		try {
			if (copieText != null) {
				copie = Integer.parseInt(copieText.getValue());
			} else {
				copie = istanza.getCopie();
			}
		} catch (NumberFormatException e) {
			throw new ValidationException("Numero errato di copie");
		}
		final AsyncCallback<Fatture> pagCallback = new AsyncCallback<Fatture>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(Fatture result) {
				if (parent != null) {
					parent.refresh();
				}
				close();
				UiSingleton.get().addInfo(AppConstants.MSG_SAVE_OK);
				WaitSingleton.get().stop();
			}
		};
		AsyncCallback<IstanzeAbbonamenti> iaCallback = new AsyncCallback<IstanzeAbbonamenti>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(IstanzeAbbonamenti result) {
				WaitSingleton.get().stop();
				WaitSingleton.get().start();
				pagamentiService.processPayment(new Date(), idPagList, idCredList,
						result.getId(), idOpzList, 
						AuthSingleton.get().getUtente().getId(), pagCallback);
			}
		};
		WaitSingleton.get().start();
		abbonamentiService.changeListinoAndOpzioni(istanza.getId(), idListino, copie, idOpzList,
				AuthSingleton.get().getUtente().getId(), iaCallback);
	}
	
	private void changeOffering() throws ValidationException {
		Integer idListino;
		if (listiniList != null) {
			idListino = listiniList.getSelectedValueInt();
		} else {
			idListino = istanza.getListino().getId();
		}
		final List<Integer> idOpzList = new ArrayList<Integer>();
		for (Opzioni opz:opzSet) idOpzList.add(opz.getId());
		final List<Integer> idPagList = new ArrayList<Integer>();
		for (Pagamenti pag:pagSet) idPagList.add(pag.getId());
		final List<Integer> idCredList = new ArrayList<Integer>();
		for (PagamentiCrediti cred:credSet) idCredList.add(cred.getId());
		Integer copie = null;
		try {
			if (copieText != null) {
				copie = Integer.parseInt(copieText.getValue());
			} else {
				copie = istanza.getCopie();
			}
		} catch (NumberFormatException e) {
			throw new ValidationException("Numero errato di copie");
		}
		final AsyncCallback<Boolean> statusCallback = new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(Boolean result) {
				if (parent != null) {
					parent.refresh();
				}
				close();
				UiSingleton.get().addInfo(AppConstants.MSG_SAVE_OK);
				WaitSingleton.get().stop();
			}
		};
		AsyncCallback<IstanzeAbbonamenti> iaCallback = new AsyncCallback<IstanzeAbbonamenti>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(IstanzeAbbonamenti result) {
				WaitSingleton.get().stop();
				WaitSingleton.get().start();
				pagamentiService.verifyPagatoAndUpdate(istanza.getId(), statusCallback);
			}
		};
		WaitSingleton.get().start();
		abbonamentiService.changeListinoAndOpzioni(istanza.getId(), idListino, copie, idOpzList, 
				AuthSingleton.get().getUtente().getId(), iaCallback);
	}
	
	private Double getValoreFatturato(IstanzeAbbonamenti ia) {
		Double result = 0D;
		if (ia.getIdFattura() != null) result += ia.getListino().getPrezzo();
		if (ia.getOpzioniIstanzeAbbonamentiSet() != null) {
			for (OpzioniIstanzeAbbonamenti oia:ia.getOpzioniIstanzeAbbonamentiSet()) {
				if (oia.getIdFattura() != null) result += oia.getOpzione().getPrezzo();
			}
		}
		return (result * ia.getCopie());
	}
	
	
	
	
	
	
	
	//Inner classes
	
	
	
	
	
	
	
	public class PagamentiNuoviTable extends PagingTable<Pagamenti> implements IRefreshable {
		
		private static final int TABLE_ROWS = 50;
		private IRefreshable parent = null;
		
		private AsyncCallback<List<Pagamenti>> callback = new AsyncCallback<List<Pagamenti>>() {
			@Override
			public void onFailure(Throwable caught) {
				setTableRows(new ArrayList<Pagamenti>());
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(List<Pagamenti> result) {
				setTableRows(result);
				WaitSingleton.get().stop();
			}
		};
		
		public PagamentiNuoviTable(IstanzeAbbonamenti istanza, IRefreshable parent) {
			super(new NuoviModel(istanza), TABLE_ROWS);
			this.parent=parent;
			drawPage(0);
		}

		public PagamentiNuoviTable(Integer idPaymentWithError,
				IRefreshable parent) {
			super(new PaymentWithErrorModel(idPaymentWithError), TABLE_ROWS);
			this.parent=parent;
			drawPage(0);
		}
		
		@Override
		public void drawPage(int page) {
			clearInnerTable();
			getInnerTable().setHTML(0, 0, "Caricamento in corso...");
			getModel().find(page*AppConstants.TABLE_ROWS_DEFAULT,
					AppConstants.TABLE_ROWS_DEFAULT,
					callback);
		}
		
		public void refresh() {
			if (parent != null) {
				parent.refresh();
			}
			drawPage(0);
		}
		
		@Override
		protected void addTableRow(int rowNum, Pagamenti rowObj) {
			final Pagamenti fRowObj = rowObj;
			// Set the data in the current row
			final CheckBox pagBox = new CheckBox();
			pagBox.setValue(false);
			pagBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					if (event.getValue()) {
						addPagamento(fRowObj);
					} else {
						removePagamento(fRowObj);
					}
				}
			});
			getInnerTable().setWidget(rowNum, 0, pagBox);
			String descr = "&euro;"+
					ClientConstants.FORMAT_CURRENCY.format(rowObj.getImporto())+
					" accreditati il "+ClientConstants.FORMAT_DAY.format(rowObj.getDataAccredito());
			getInnerTable().setHTML(rowNum, 1, descr);
			getInnerTable().setHTML(rowNum, 2, AppConstants.PAGAMENTO_DESC.get(rowObj.getIdTipoPagamento()));
			rowNum += 1;
		}
		
		@Override
		protected void addHeader() {
			// Set the data in the current row
			//getInnerTable().setHTML(0, 0, "Scelta");
			//getInnerTable().setHTML(0, 1, "Importo");
			//getInnerTable().setHTML(0, 2, "Tipo");
		}
		
		@Override
		protected void onEmptyResult() {}
				
	}
	
	public class PagamentiCreditiTable extends PagingTable<PagamentiCrediti> implements IRefreshable {
		
		private static final int TABLE_ROWS = 50;
		private IRefreshable parent = null;
		
		private AsyncCallback<List<PagamentiCrediti>> callback = new AsyncCallback<List<PagamentiCrediti>>() {
			@Override
			public void onFailure(Throwable caught) {
				setTableRows(new ArrayList<PagamentiCrediti>());
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(List<PagamentiCrediti> result) {
				setTableRows(result);
				WaitSingleton.get().stop();
			}
		};
		
		public PagamentiCreditiTable(Integer idAnagrafica, String idSocieta, IRefreshable parent) {
			super(new CreditiModel(idAnagrafica, idSocieta), TABLE_ROWS);
			this.parent=parent;
			drawPage(0);
		}

		@Override
		public void drawPage(int page) {
			clearInnerTable();
			getInnerTable().setHTML(0, 0, "Caricamento in corso...");
			getModel().find(page*AppConstants.TABLE_ROWS_DEFAULT,
					AppConstants.TABLE_ROWS_DEFAULT,
					callback);
		}
		
		public void refresh() {
			if (parent != null) {
				parent.refresh();
			}
			drawPage(0);
		}
		
		@Override
		protected void addTableRow(int rowNum, PagamentiCrediti rowObj) {
			final PagamentiCrediti fRowObj = rowObj;
			// Set the data in the current row
			CheckBox pagBox = new CheckBox();
			pagBox.setValue(false);
			pagBox.setEnabled(rowObj.getStornatoDaOrigine());
			if (rowObj.getStornatoDaOrigine()) {
				pagBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
					@Override
					public void onValueChange(ValueChangeEvent<Boolean> event) {
						if (event.getValue()) {
							addCredito(fRowObj);
						} else {
							removeCredito(fRowObj);
						}
					}
				});
			}
			getInnerTable().setWidget(rowNum, 0, pagBox);
			String descr = "<b>&euro;"+
					ClientConstants.FORMAT_CURRENCY.format(rowObj.getImporto())+"</b>";
			getInnerTable().setHTML(rowNum, 1, descr);
			String note = "Anticipo ex ";
			if (!rowObj.getStornatoDaOrigine()) note = "<b>Da stornare</b> ex ";
			getInnerTable().setHTML(rowNum, 2, note+rowObj.getFatturaOrigine().getNumeroFattura());
			rowNum += 1;
		}
		
		@Override
		protected void addHeader() {
			// Set the data in the current row
			//getInnerTable().setHTML(0, 0, "Scelta");
			//getInnerTable().setHTML(0, 1, "Importo");
			//getInnerTable().setHTML(0, 2, "Note");
		}
		
		@Override
		protected void onEmptyResult() {}
				
	}
	
	
	
	public class OpzioniInclTable extends PagingTable<Opzioni> implements IRefreshable {
		
		private static final int TABLE_ROWS = 50;
		private IRefreshable parent = null;
		
		private AsyncCallback<List<Opzioni>> callback = new AsyncCallback<List<Opzioni>>() {
			@Override
			public void onFailure(Throwable caught) {
				setTableRows(new ArrayList<Opzioni>());
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(List<Opzioni> result) {
				setTableRows(result);
				WaitSingleton.get().stop();
			}
		};
		
		public OpzioniInclTable(Integer idListino, IRefreshable parent) {
			super(new OpzioniInclModel(idListino), TABLE_ROWS);
			this.parent = parent;
			drawPage(0);
		}

		@Override
		public void drawPage(int page) {
			clearInnerTable();
			getInnerTable().setHTML(0, 0, "Caricamento in corso...");
			getModel().find(page*AppConstants.TABLE_ROWS_DEFAULT,
					AppConstants.TABLE_ROWS_DEFAULT,
					callback);
		}
		
		public void refresh() {
			if (parent != null) {
				parent.refresh();
			}
			drawPage(0);
		}
		
		@Override
		protected void addTableRow(int rowNum, Opzioni rowObj) {
			// Set the data in the current row
			getInnerTable().setHTML(rowNum, 0, ClientConstants.ICON_CHECK);
			getInnerTable().setHTML(rowNum, 1, rowObj.getNome()+" ["+rowObj.getUid()+"]");
			rowNum += 1;
		}
		
		@Override
		protected void addHeader() {
			// Set the data in the current row
			//getInnerTable().setHTML(0, 0, "Scelta");
			//getInnerTable().setHTML(0, 1, "Opzione");
		}
		
		@Override
		protected void onEmptyResult() {}
				
	}
	
	public class OpzioniFaclTable extends PagingTable<Opzioni> implements IRefreshable {
		
		private static final int TABLE_ROWS = 50;
		private IRefreshable parent = null;
		private Set<OpzioniIstanzeAbbonamenti> oiaSet = null;
		
		private AsyncCallback<List<Opzioni>> callback = new AsyncCallback<List<Opzioni>>() {
			@Override
			public void onFailure(Throwable caught) {
				setTableRows(new ArrayList<Opzioni>());
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(List<Opzioni> result) {
				setTableRows(result);
				WaitSingleton.get().stop();
			}
		};
		
		public OpzioniFaclTable(Integer idListino, Integer idFasIni,
				Set<OpzioniIstanzeAbbonamenti> opzioniAbbinate, IRefreshable parent) {
			super(new OpzioniFaclModel(idListino, idFasIni), TABLE_ROWS);
			this.oiaSet = opzioniAbbinate;
			this.parent = parent;
			drawPage(0);
		}

		@Override
		public void drawPage(int page) {
			clearInnerTable();
			getInnerTable().setHTML(0, 0, "Caricamento in corso...");
			getModel().find(page*AppConstants.TABLE_ROWS_DEFAULT,
					AppConstants.TABLE_ROWS_DEFAULT,
					callback);
		}
		
		public void refresh() {
			if (parent != null) {
				parent.refresh();
			}
			drawPage(0);
		}
		
		@Override
		protected void addTableRow(int rowNum, Opzioni rowObj) {
			final Opzioni fRowObj = rowObj;
			//Opzione fatturata?
			boolean hasInvoice = false;
			boolean isSelected = false;
			for (OpzioniIstanzeAbbonamenti oia:oiaSet) {
				if (oia.getOpzione().getId().equals(rowObj.getId())) {
					hasInvoice = (oia.getIdFattura() != null);
					isSelected = true;
					addOpzione(fRowObj);
				}
			}
			// Set the data in the current row
			if (hasInvoice) {
				getInnerTable().setHTML(rowNum, 0, ClientConstants.ICON_CHECK);
			} else {
				CheckBox opzBox = new CheckBox();
				opzBox.setValue(isSelected);
				opzBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
					@Override
					public void onValueChange(ValueChangeEvent<Boolean> event) {
						if (event.getValue()) {
							addOpzione(fRowObj);
						} else {
							removeOpzione(fRowObj);
						}
					}
				});
				getInnerTable().setWidget(rowNum, 0, opzBox);
			}
			getInnerTable().setHTML(rowNum, 1, rowObj.getNome()+" ["+rowObj.getUid()+"]");
			getInnerTable().setHTML(rowNum, 2, "&euro;"+
					ClientConstants.FORMAT_CURRENCY.format(rowObj.getPrezzo()));
			rowNum += 1;
		}
		
		@Override
		protected void addHeader() {
			// Set the data in the current row
			//getInnerTable().setHTML(0, 0, "Scelta");
			//getInnerTable().setHTML(0, 1, "Opzione");
			//getInnerTable().setHTML(0, 2, "Prezzo");
		}
		
		@Override
		protected void onEmptyResult() {}
				
	}
	
	public class NuoviModel implements DataModel<Pagamenti> {
		private final PagamentiServiceAsync pagamentiService = GWT.create(PagamentiService.class);
		private IstanzeAbbonamenti istanza = null;
		
		public NuoviModel(IstanzeAbbonamenti istanza) {
			this.istanza=istanza;
		}
		@Override
		public void find(int offset, int pageSize, AsyncCallback<List<Pagamenti>> callback) {
			pagamentiService.findPagamentiFatturabiliByAnagraficaSocieta(istanza.getId(), 
					istanza.getAbbonamento().getPeriodico().getIdSocieta(), callback);
		}
	}
	
	public class PaymentWithErrorModel implements DataModel<Pagamenti> {
		private final PagamentiServiceAsync pagamentiService = GWT.create(PagamentiService.class);
		private Integer idPaymentWithError = null;
		
		public PaymentWithErrorModel(Integer idPaymentWithError) {
			this.idPaymentWithError=idPaymentWithError;
		}
		@Override
		public void find(int offset, int pageSize, AsyncCallback<List<Pagamenti>> callback) {
			pagamentiService.findPagamentiById(idPaymentWithError, callback);
		}
	}
	
	public class CreditiModel implements DataModel<PagamentiCrediti> {
		private final PagamentiServiceAsync pagamentiService = GWT.create(PagamentiService.class);
		private Integer idAnagrafica = null;
		private String idSocieta = null;
		
		public CreditiModel(Integer idAnagrafica, String idSocieta) {
			this.idAnagrafica = idAnagrafica;
			this.idSocieta = idSocieta;
		}
		@Override
		public void find(int offset, int pageSize, AsyncCallback<List<PagamentiCrediti>> callback) {
			pagamentiService.findCreditiByAnagraficaSocieta(idAnagrafica,
					idSocieta, null, callback);
		}
	}
	
	public class OpzioniInclModel implements DataModel<Opzioni> {
		private final OpzioniServiceAsync opzioniService = GWT.create(OpzioniService.class);
		private Integer idListino = null;
		
		public OpzioniInclModel(Integer idListino) {
			this.idListino = idListino;
		}
		@Override
		public void find(int offset, int pageSize, AsyncCallback<List<Opzioni>> callback) {
			opzioniService.findOpzioniByListino(idListino, callback);
		}
	}
	
	public class OpzioniFaclModel implements DataModel<Opzioni> {
		private final OpzioniServiceAsync opzioniService = GWT.create(OpzioniService.class);
		private Integer idListino = null;
		private Integer idFasIni = null;
		
		public OpzioniFaclModel(Integer idListino, Integer idFasIni) {
			this.idListino = idListino;
			this.idFasIni = idFasIni;
		}
		@Override
		public void find(int offset, int pageSize, AsyncCallback<List<Opzioni>> callback) {
			opzioniService.findOpzioniFacoltativeByListino(idListino,
					idFasIni, callback);
		}
	}
	
}
