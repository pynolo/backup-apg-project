package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.CookieSingleton;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.services.AbbonamentiService;
import it.giunti.apg.client.services.AbbonamentiServiceAsync;
import it.giunti.apg.client.services.PagamentiService;
import it.giunti.apg.client.services.PagamentiServiceAsync;
import it.giunti.apg.client.widgets.AnagraficheSearchBox;
import it.giunti.apg.client.widgets.ArticoliListiniPanel;
import it.giunti.apg.client.widgets.OpzioniIstanzaPanel;
import it.giunti.apg.client.widgets.TitlePanel;
import it.giunti.apg.client.widgets.select.AdesioniSelect;
import it.giunti.apg.client.widgets.select.ArticoliSelect;
import it.giunti.apg.client.widgets.select.DestinatarioSelect;
import it.giunti.apg.client.widgets.select.FascicoliSelect;
import it.giunti.apg.client.widgets.select.ListiniSelect;
import it.giunti.apg.client.widgets.select.PeriodiciSelect;
import it.giunti.apg.client.widgets.select.TipiPagamentoSelect;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.Abbonamenti;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Pagamenti;
import it.giunti.apg.shared.model.Utenti;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DateBox;

public class QuickAbboPanel extends FlowPanel {
	
	private final AbbonamentiServiceAsync abbonamentiService = GWT.create(AbbonamentiService.class);
	private final PagamentiServiceAsync pagamentiService = GWT.create(PagamentiService.class);

	private IstanzeAbbonamenti item = null;
	private boolean isOperator = false;
	private long startDt;
	private long finishDt;
	private Utenti utente = null;
	
	private AnagraficheSearchBox promotoreSearchBox = null;
	private PeriodiciSelect periodiciList = null;
	private TextBox copieText = null;
	private ListiniSelect listiniList = null;
	private OpzioniIstanzaPanel opzioniIstanzaPanel = null;
	private ArticoliListiniPanel artListPanel = null;
	private FascicoliSelect fasInizioList = null;
	private FascicoliSelect fasFineList = null;
	//private AdesioniSuggestBox adesioniSuggest = null;
	private AdesioniSelect adesioniList = null;
	private TextBox noteArea = null;
	private DestinatarioSelect destArticoloList = null;
	private ArticoliSelect articoloList = null;
	//private DateBox articoloExpDate = null;
	
	private TextBox initialPaymentAmountText = null;
	private DateBox initialPaymentDate = null;
	private TipiPagamentoSelect initialPaymentTypeList = null;
	private TextBox initialPaymentNoteText = null;
	
	
	// METHODS
	
	public QuickAbboPanel(Utenti utente) {
		super();
		init(utente);
	}
	
	private void init(Utenti utente) {
		this.utente = utente;
		// Editing rights
		isOperator = (utente.getRuolo().getId() >= AppConstants.RUOLO_OPERATOR);
		// UI
		if (isOperator) {
			loadAbbonamento();
		}
	}
		
	/** This method empties the ContentPanel and redraws the 'item' data
	 * @param item
	 */
	private void drawAbbonamento() {
		final IstanzeAbbonamenti item = this.item;
		long now = item.getFascicoloInizio().getDataInizio().getTime();
		startDt = now - AppConstants.MONTH * 13;
		finishDt = now + AppConstants.MONTH * 36;
		// clean form
		FlexTable table = new FlexTable();
		this.add(table);
		int r=0;
		
		//Caption con anagrafiche
		VerticalPanel anagPanel = new VerticalPanel();
		anagPanel.setStyleName("grey-panel");
		anagPanel.setWidth("100%");
		promotoreSearchBox = new AnagraficheSearchBox("Promotore",
				item.getPromotore(),
				isOperator);
		anagPanel.add(promotoreSearchBox);
		table.setWidget(r, 0, anagPanel);
		table.getFlexCellFormatter().setColSpan(r, 0, 5);
		r++;

		// Periodico
		table.setHTML(r, 0, "Periodico");
		periodiciList = new PeriodiciSelect(item.getAbbonamento().getPeriodico().getId(), 
				item.getFascicoloInizio().getDataInizio(), false, true, utente);
		periodiciList.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				onPeriodicoChange();
			}
		});
		periodiciList.setEnabled(isOperator && (item.getAbbonamento().getId() == null));//solo se nuovo
		table.setWidget(r, 1, periodiciList);
		// Codice
		table.setHTML(r, 3, "Codice abbonamento");
		table.setHTML(r, 4, "<b>[generato automaticamente]</b>");
		r++;

		// TipoAbb
		table.setHTML(r,0, "Tipo abbonamento");
		listiniList = new ListiniSelect(
					item.getListino().getId(),
					item.getAbbonamento().getPeriodico().getId(),
					item.getFascicoloInizio().getDataInizio(),
					false, true, false, isOperator);
		listiniList.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				onListinoChange();
			}
		});
		table.setWidget(r, 1, listiniList);
		// Copie
		table.setHTML(r, 3, "Copie"+ClientConstants.MANDATORY);
		copieText = new TextBox();
		copieText.setValue(item.getCopie()+"");
		copieText.setEnabled(isOperator);
		copieText.setMaxLength(3);
		copieText.setWidth("3em");
		table.setWidget(r, 4, copieText);
		r++;

		// FascicoloInizio
		table.setHTML(r, 0, "Inizio");
		fasInizioList = new FascicoliSelect(
				item.getFascicoloInizio().getId(),
				item.getAbbonamento().getPeriodico().getId(),
				startDt, finishDt, false, false, true, false, false);
		fasInizioList.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				onFascicoloInizioChange();
			}
		});
		if (isOperator) {
			table.setWidget(r, 1, fasInizioList);
		} else {
			table.setHTML(r, 1, "<b>"+item.getFascicoloInizio().getTitoloNumero() + "&nbsp;(" +
					item.getFascicoloInizio().getDataCop() + " " +
					ClientConstants.FORMAT_YEAR.format(item.getFascicoloInizio().getDataInizio())+")</b>");
		}
		// FacicoloFine
		table.setHTML(r, 3, "Fine");
		fasFineList = new FascicoliSelect(
				item.getFascicoloFine().getId(),
				item.getAbbonamento().getPeriodico().getId(),
				startDt, finishDt, false, false, true, false, false);
		if (isOperator) {
			table.setWidget(r, 4, fasFineList);
		} else {
			table.setHTML(r, 4, "<b>"+item.getFascicoloFine().getTitoloNumero() + "&nbsp;(" +
					item.getFascicoloFine().getDataCop() + " " +
					ClientConstants.FORMAT_YEAR.format(item.getFascicoloFine().getDataInizio())+")</b>");
		}
		r++;
		
		//Opzioni
		opzioniIstanzaPanel = new OpzioniIstanzaPanel(
				item.getListino().getTipoAbbonamento().getPeriodico().getId(),
				item.getFascicoloInizio().getId(),
				item.getOpzioniIstanzeAbbonamentiSet(),
				item.getListino().getOpzioniListiniSet(),
				"Opzioni "+ClientConstants.ICON_OPZIONI);
		opzioniIstanzaPanel.setEnabled(isOperator);
		table.setWidget(r, 0, opzioniIstanzaPanel);
		table.getFlexCellFormatter().setColSpan(r, 0, 5);
		r++;
		
		//Articoli regalo
		artListPanel = new ArticoliListiniPanel(
				item.getListino().getArticoliListiniSet(), "Articoli inclusi");
		table.setWidget(r, 0, artListPanel);
		table.getFlexCellFormatter().setColSpan(r, 0, 5);
		r++;
		
		// Adesione
		table.setHTML(r, 0, "Adesione");
		Integer idAdesione = null;
		if (item.getAdesione() != null) idAdesione = item.getAdesione().getId();
		adesioniList = new AdesioniSelect(idAdesione);
		adesioniList.setEnabled(isOperator);
		table.setWidget(r, 1, adesioniList);
		//adesioniSuggest = new AdesioniSuggestBox();
		//adesioniSuggest.setValue(item.getAdesione());
		//if (isEditor) {
		//	table.setWidget(r, 1, adesioniSuggest);
		//} else {
		//	table.setHTML(r, 1, item.getAdesione());
		//}
		r++;
		
		//Articolo
		table.setHTML(r, 0, "Articolo per");
		HorizontalPanel articoloPanel = new HorizontalPanel();
		destArticoloList = new DestinatarioSelect(AppConstants.DEST_BENEFICIARIO);
		destArticoloList.setEnabled(isOperator);
		articoloPanel.add(destArticoloList);
		articoloList = new ArticoliSelect(null, item.getFascicoloInizio().getDataInizio(),
				item.getFascicoloInizio().getDataInizio(), true, true);
		articoloList.setEnabled(isOperator);
		//articoloList.addChangeHandler(new ChangeHandler() {
		//	@Override
		//	public void onChange(ChangeEvent event) {
		//		loadDataLimite();
		//	}
		//});
		articoloPanel.add(articoloList);
		//articoloPanel.add(new InlineHTML("&nbsp;Data limite&nbsp;"));
		//articoloExpDate = new DateBox();
		//articoloExpDate.setEnabled(isEditor);
		//articoloExpDate.setWidth("8em");
		//articoloExpDate.setFormat(ClientConstants.BOX_FORMAT_DAY);
		//articoloPanel.add(articoloExpDate);
		table.setWidget(r, 1, articoloPanel);
		table.getFlexCellFormatter().setColSpan(r, 1, 5);
		r++;
				
		//Note
		table.setHTML(r, 0, "Note");
		noteArea = new TextBox();
		noteArea.setValue(item.getNote());
		noteArea.setWidth("95%");
		noteArea.setMaxLength(250);
		noteArea.setEnabled(isOperator);
		table.getFlexCellFormatter().setColSpan(r, 1, 4);
		table.setWidget(r, 1, noteArea);
		r++;
		
		//Pagamento alla creazione
		TitlePanel paymentPanel = getPaymentPanel();
		table.setWidget(r, 0, paymentPanel);
		table.getFlexCellFormatter().setColSpan(r, 0, 5);
		r++;
	}
	
	private TitlePanel getPaymentPanel() {
		TitlePanel panel = new TitlePanel("Pagamento iniziale");
		HorizontalPanel holder = new HorizontalPanel();
		holder.add(new HTML("Importo&nbsp;"));
		initialPaymentAmountText = new TextBox();
		initialPaymentAmountText.setEnabled(isOperator);
		initialPaymentAmountText.setMaxLength(10);
		initialPaymentAmountText.setWidth("6em");
		initialPaymentAmountText.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent arg0) {
				checkImportoPagamento();
			}
		});
		holder.add(initialPaymentAmountText);
		holder.add(new HTML("&nbsp;&nbsp;Data&nbsp;"));
		initialPaymentDate = new DateBox();
		initialPaymentDate.setFormat(ClientConstants.BOX_FORMAT_DAY);
		initialPaymentDate.setEnabled(isOperator);
		initialPaymentDate.setWidth("8em");
		holder.add(initialPaymentDate);
		holder.add(new HTML("&nbsp;&nbsp;Tipo&nbsp;"));
		initialPaymentTypeList = new TipiPagamentoSelect(AppConstants.PAGAMENTO_DEFAULT);
		initialPaymentTypeList.setEnabled(isOperator);
		holder.add(initialPaymentTypeList);
		panel.add(holder);
		holder.add(new HTML("&nbsp;&nbsp;Note&nbsp;"));
		initialPaymentNoteText = new TextBox();
		initialPaymentNoteText.setEnabled(isOperator);
		initialPaymentNoteText.setMaxLength(250);
		initialPaymentNoteText.setWidth("12em");
		holder.add(initialPaymentNoteText);
		return panel;
	}
	
	public IstanzeAbbonamenti getIstanzaAbbonamento() throws ValidationException {
		IstanzeAbbonamenti result = new IstanzeAbbonamenti();
		result.setAbbonamento(new Abbonamenti());
		//Validazione	
		Integer copie = null;
		try {
			copie = Integer.valueOf(copieText.getValue());
		} catch (NumberFormatException e1) {
			throw new ValidationException("Errore nel numero di copie");
		}
		//Assegnazione
		Date today = new Date();
		result.setCopie(copie);
		result.setIdFascicoloInizioT(fasInizioList.getSelectedValueString());
		result.setIdFascicoloFineT(fasFineList.getSelectedValueString());
		result.setNote(noteArea.getValue());
		result.setIdListinoT(listiniList.getSelectedValueString());
		result.setDataCreazione(today);
		result.setDataSyncMailing(AppConstants.DEFAULT_DATE);
		result.setDataModifica(today);
		result.setIdUtente(AuthSingleton.get().getUtente().getId());
		result.setIdPromotoreT(promotoreSearchBox.getIdValue());
		//result.setAdesioneTxt(adesioniSuggest.getValue());
		result.setIdAdesioneT(adesioniList.getSelectedValueString());
		
		result.getAbbonamento().setIdPeriodicoT(periodiciList.getSelectedValueString());
		result.getAbbonamento().setDataModifica(today);
		result.getAbbonamento().setDataCreazione(today);
		result.getAbbonamento().setIdUtente(AuthSingleton.get().getUtente().getId());
		result.getAbbonamento().setIdTipoSpedizione(AppConstants.SPEDIZIONE_DEFAULT);
		
		result.setIdOpzioniIstanzeAbbonamentiSetT(opzioniIstanzaPanel.getValue());
		return result;
	}
	
	public Integer getIdArticoloScelto() {
		String idArticolo = articoloList.getSelectedValueString();
		if (!idArticolo.equals(AppConstants.SELECT_EMPTY_VALUE_STRING)) {
			Integer id = ValueUtil.stoi(idArticolo);
			return id;
		} else {
			return null;
		}
	}
	
	public String getIdTipoDestinatario() {
		String idTipo = destArticoloList.getSelectedValueString();
		return idTipo;
	}
	
	//public EvasioniArticoli getArticoloScelto() {
	//	EvasioniArticoli ed = null;
	//	Date today = new Date();
	//	String idArticolo = articoloList.getSelectedValueString();
	//	if (!idArticolo.equals(AppConstants.DEFAULT_LIST_EMPTY_VALUE)) {
	//		ed = new EvasioniArticoli();
	//		ed.setDataCreazione(today);
	//		ed.setPrenotazioneIstanzaFutura(false);
	//		ed.setIdArticoloT(idArticolo);
	//		ed.setIdTipoDestinatario(destArticoloList.getSelectedValueString());
	//		ed.setDataModifica(today);
	//		ed.setDataLimite(articoloExpDate.getValue());
	//		ed.setCopie(1);
	//		ed.setUtente(AuthSingleton.get().getUtente());
	//	}
	//	return ed;
	//}
	
	public Pagamenti getPagamento() throws ValidationException {
		Pagamenti pagamento = null;
		if (initialPaymentAmountText != null) {
			if (initialPaymentAmountText.getValue() != null) {
				if (initialPaymentAmountText.getValue().length() > 0) {
					if (initialPaymentDate.getValue() == null) {
						throw new ValidationException("Il pagamento iniziale non è stato salvato perche' manca la data");
					} else {
						//C'è pagamento iniziale
						pagamento = new Pagamenti();
						Date today = new Date();
						try {
							pagamento.setImporto(ClientConstants.FORMAT_CURRENCY.parse(initialPaymentAmountText.getValue()));
						} catch (NumberFormatException e) {
							throw new ValidationException("Importo non corretto");
						}
						String idTipo = initialPaymentTypeList.getSelectedValueString();
						pagamento.setIdTipoPagamento(idTipo);
						pagamento.setDataPagamento(initialPaymentDate.getValue());
						pagamento.setDataAccredito(initialPaymentDate.getValue());
						pagamento.setDataModifica(today);
						pagamento.setDataCreazione(today);
						pagamento.setNote(initialPaymentNoteText.getValue());
						pagamento.setIdUtente(AuthSingleton.get().getUtente().getId());
					}
				}
			}
		}
		return pagamento;
	}
	
	public Double getImportoPagamento() {
		Double importo = null;
		if (initialPaymentAmountText != null) {
			if (initialPaymentAmountText.getValue() != null) {
				if (initialPaymentAmountText.getValue().length() > 0) {
					importo = ClientConstants.FORMAT_CURRENCY.parse(initialPaymentAmountText.getValue());
				}
			}
		}
		return importo;
	}
		
	
	
	// METODI ASINCRONI DI AGGIORNAMENTO UI
	
	
	private void onPeriodicoChange() {
		Integer idPeriodico = periodiciList.getSelectedValueInt();
		CookieSingleton.get().setCookie(ClientConstants.COOKIE_LAST_PERIODICO, idPeriodico+"");
		AsyncCallback<IstanzeAbbonamenti> callback = new AsyncCallback<IstanzeAbbonamenti>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
			}
			@Override
			public void onSuccess(IstanzeAbbonamenti result) {
				item=result;
				fasInizioList.reload(
						result.getFascicoloInizio().getId(),
						result.getListino().getTipoAbbonamento().getPeriodico().getId(),
						startDt, finishDt, false, false, true, false, false);// NON scatena onChange
				listiniList.reload(item.getListino().getId(),
						item.getFascicoloInizio().getPeriodico().getId(),
						item.getFascicoloInizio().getId(),
						false); // scatena onChange
				opzioniIstanzaPanel.onListinoChange(
						item.getListino().getTipoAbbonamento().getPeriodico().getId(),
						item.getFascicoloInizio().getId(),
						item.getListino().getOpzioniListiniSet());
				fasFineList.reload(
						item.getFascicoloFine().getId(),
						item.getListino().getTipoAbbonamento().getPeriodico().getId(),
						startDt, finishDt, false, false, true, false, false); // NON scatena onChange()
				artListPanel.changeListino(item.getListino().getArticoliListiniSet());
			}
		};
		abbonamentiService.changePeriodico(item, idPeriodico,
				item.getListino().getTipoAbbonamento().getCodice(), callback);
	}

	public void onFascicoloInizioChange() {
		if ((fasInizioList.getSelectedValueString() != null) &&
				(periodiciList.getSelectedValueString() != null)) {
			AsyncCallback<IstanzeAbbonamenti> callback = new AsyncCallback<IstanzeAbbonamenti>() {
				@Override
				public void onFailure(Throwable caught) {
					UiSingleton.get().addError(caught);
				}
				@Override
				public void onSuccess(IstanzeAbbonamenti result) {
					item=result;
					listiniList.reload(item.getListino().getId(),
							item.getFascicoloInizio().getPeriodico().getId(),
							item.getFascicoloInizio().getId(),
							false); // scatena onChange
					opzioniIstanzaPanel.onListinoChange(
							item.getListino().getTipoAbbonamento().getPeriodico().getId(),
							item.getFascicoloInizio().getId(),
							item.getListino().getOpzioniListiniSet());
					fasFineList.reload(
							item.getFascicoloFine().getId(),
							item.getListino().getTipoAbbonamento().getPeriodico().getId(),
							startDt, finishDt, false, false, true, false, false); // NON scatena onChange()
					artListPanel.changeListino(item.getListino().getArticoliListiniSet());
				}
			};
			abbonamentiService.changeFascicoloInizio(item,
					fasInizioList.getSelectedValueInt(),
					item.getListino().getTipoAbbonamento().getCodice(), callback);
		}
	}
	
	private void onListinoChange() {
		//Cambia le date e i totali
		AsyncCallback<IstanzeAbbonamenti> callback = new AsyncCallback<IstanzeAbbonamenti>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
			}
			@Override
			public void onSuccess(IstanzeAbbonamenti result) {
				item=result;
				opzioniIstanzaPanel.onListinoChange(
						item.getListino().getTipoAbbonamento().getPeriodico().getId(),
						item.getFascicoloInizio().getId(),
						item.getListino().getOpzioniListiniSet());
				fasInizioList.reload(
						result.getFascicoloInizio().getId(),
						result.getListino().getTipoAbbonamento().getPeriodico().getId(),
						startDt, finishDt, false, false, true, false, false);// NON scatena onChange
				fasFineList.reload(
						item.getFascicoloFine().getId(),
						item.getListino().getTipoAbbonamento().getPeriodico().getId(),
						startDt, finishDt, false, false, true, false, false); // NON scatena onChange()
				artListPanel.changeListino(item.getListino().getArticoliListiniSet());
			}
		};
		abbonamentiService.changeListino(item,
				listiniList.getSelectedValueInt(), callback);
	}
	
	
	
	// Async methods
	
	
	
	private void loadAbbonamento() {
		AsyncCallback<IstanzeAbbonamenti> callback = new AsyncCallback<IstanzeAbbonamenti>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
			}
			@Override
			public void onSuccess(IstanzeAbbonamenti result) {
				item = result;
				drawAbbonamento();
			}
		};
		Integer idPeriodico = ValueUtil.stoi(CookieSingleton.get().getCookie(ClientConstants.COOKIE_LAST_PERIODICO));
		if (idPeriodico == null) {
			idPeriodico=UiSingleton.get().getDefaultIdPeriodico(utente);
			CookieSingleton.get().setCookie(ClientConstants.COOKIE_LAST_PERIODICO, idPeriodico.toString());
		}
		abbonamentiService.createAbbonamentoAndIstanza(null, null, null, idPeriodico, callback);
	}

	private void checkImportoPagamento() {
		try {
			final Double pagato = getImportoPagamento();
			if (pagato != null) {
				IstanzeAbbonamenti ia = getIstanzaAbbonamento();
				Integer idLst = ValueUtil.stoi(ia.getIdListinoT());
				AsyncCallback<Double> callback = new AsyncCallback<Double>() {
					@Override
					public void onFailure(Throwable caught) {
						UiSingleton.get().addError(caught);
					}
					@Override
					public void onSuccess(Double dovuto) {
						Double delta = Math.abs(dovuto-pagato);
						if (delta > AppConstants.SOGLIA) {
							UiSingleton.get().addWarning("Attenzione, l'importo inserito (" +
									ClientConstants.FORMAT_CURRENCY.format(pagato) +
									") non corrisponde al dovuto (" +
									ClientConstants.FORMAT_CURRENCY.format(dovuto) + ")");
						}
					}
				};
				pagamentiService.getStimaImportoTotale(idLst, ia.getCopie(), ia.getIdOpzioniIstanzeAbbonamentiSetT(), callback);
			}
		} catch (ValidationException e) {
			UiSingleton.get().addWarning(e.getMessage());
		}
		
	}

	//private void loadDataLimite() {
	//	AsyncCallback<Date> callback = new AsyncCallback<Date>() {
	//		@Override
	//		public void onFailure(Throwable caught) {
	//			UiSingleton.get().addError(caught);
	//		}
	//		@Override
	//		public void onSuccess(Date result) {
	//			articoloExpDate.setValue(result);
	//		}
	//	};
	//	//look for item with id only if id is defined
	//	Integer idArticolo = articoloList.getSelectedValueInt();
	//	articoliService.loadDataLimite(idArticolo, callback);
	//}
	
	
}
