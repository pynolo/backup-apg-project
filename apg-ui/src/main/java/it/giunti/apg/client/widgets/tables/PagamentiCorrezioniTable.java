package it.giunti.apg.client.widgets.tables;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.UriManager;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.frames.FatturazionePopUp;
import it.giunti.apg.client.frames.PagamentoPopUp;
import it.giunti.apg.client.services.AbbonamentiService;
import it.giunti.apg.client.services.AbbonamentiServiceAsync;
import it.giunti.apg.client.services.PagamentiService;
import it.giunti.apg.client.services.PagamentiServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Pagamenti;
import it.giunti.apg.shared.model.Ruoli;
import it.giunti.apg.shared.model.Utenti;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.SimplePanel;

public class PagamentiCorrezioniTable extends PagingTable<Pagamenti> implements IRefreshable {
	
	private static final PagamentiServiceAsync pagamentiService = GWT.create(PagamentiService.class);
	private static final AbbonamentiServiceAsync abbonamentiService = GWT.create(AbbonamentiService.class);
	
	private IRefreshable parent = null;
	private static final int TABLE_ROWS = 20;
	private static final String SPAN_PREFIX = "<span style='font-size:1.4em' class='text-info'>";
	private static final String SPAN_SUFFIX = "</span>&nbsp;";
	
	private boolean isEditor = false;
	
	private AsyncCallback<List<Pagamenti>> callback = new AsyncCallback<List<Pagamenti>>() {
		@Override
		public void onFailure(Throwable caught) {
			setTableRows(new ArrayList<Pagamenti>());
		}
		@Override
		public void onSuccess(List<Pagamenti> result) {
			setTableRows(result);
		}
	};
	
	private AsyncCallback<Anagrafiche> anagCallback = new AsyncCallback<Anagrafiche>() {
		@Override
		public void onFailure(Throwable caught) {
			UiSingleton.get().addError(caught);
			//WaitSingleton.get().stop();
		}
		@Override
		public void onSuccess(Anagrafiche result) {
			UiSingleton.get().addInfo("Salvataggio effettuato");
			//WaitSingleton.get().stop();
			//va alla pagina dell'anagrafica
			UriParameters params = new UriParameters();
			params.add(AppConstants.PARAM_ID, result.getId());
			params.triggerUri(UriManager.ANAGRAFICHE_MERGE);
		}
	};
	
	private AsyncCallback<Boolean> deleteCallback = new AsyncCallback<Boolean>() {
		@Override
		public void onFailure(Throwable caught) {
			UiSingleton.get().addError(caught);
			//WaitSingleton.get().stop();
		}
		@Override
		public void onSuccess(Boolean result) {
			UiSingleton.get().addInfo("Eliminazione effettuata");
			//WaitSingleton.get().stop();
			//va alla pagina dell'anagrafica
			refresh();
		}
	};
	
	
	public PagamentiCorrezioniTable(IRefreshable parent, DataModel<Pagamenti> model, Utenti utente) {
		super(model, TABLE_ROWS);
		this.parent=parent;
		Ruoli userRole = utente.getRuolo();
		this.isEditor = (userRole.getId() >= AppConstants.RUOLO_EDITOR);
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
	
	public void refresh() {
		if (parent != null) {
			parent.refresh();
		}
		drawPage(0);
	}
	
	@Override
	protected void addTableRow(int rowNum, Pagamenti rowObj) {
		final Pagamenti fRow = rowObj;
		final PagamentiCorrezioniTable fTable = this;
		// Importo
		String linkText = "&euro;"+ClientConstants.FORMAT_CURRENCY.format(rowObj.getImporto());
		if (rowObj.getDataPagamento() != null) {
			linkText += ClientConstants.SPAN_SMALL_START + "&nbsp;del&nbsp;" +
					ClientConstants.FORMAT_MONTH.format(rowObj.getDataPagamento()) +
					ClientConstants.SPAN_STOP;
		}
		Anchor rowLink = new Anchor(linkText, true);
		rowLink.addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				new PagamentoPopUp(fTable, fRow.getId(), null);
			}
		});
		getInnerTable().setWidget(rowNum, 0, rowLink);
		
		//Codice abbonato
		SimplePanel codicePanel = new SimplePanel();
		codicePanel.add(new InlineHTML(rowObj.getCodiceAbbonamentoMatch()));
		getInnerTable().setWidget(rowNum, 1, codicePanel);
		//Importo Listino
		SimplePanel importoTotPanel = new SimplePanel();
		getInnerTable().setWidget(rowNum, 2, importoTotPanel);
		//Abbonamento
		SimplePanel abbonamentoDescPanel = new SimplePanel();
		getInnerTable().setWidget(rowNum, 3, abbonamentoDescPanel);
		//Pagante
		SimplePanel pagantePanel = new SimplePanel();
		getInnerTable().setWidget(rowNum, 4, pagantePanel);
		//Errore
		String error = "--";
		if (rowObj.getIdErrore() != null) {
			error = AppConstants.PAGAMENTO_ERR_DESC.get(rowObj.getIdErrore());
		}
		getInnerTable().setHTML(rowNum, 5, error);
		//Azioni
		SimplePanel iconPanel = new SimplePanel();
		getInnerTable().setWidget(rowNum, 6, iconPanel);
		
		loadAsyncRowValues(rowObj, rowObj.getCodiceAbbonamentoMatch(), 
				codicePanel, importoTotPanel, abbonamentoDescPanel, pagantePanel, iconPanel);
	}
	
	@Override
	protected void addHeader() {
		// Set the data in the current row
		getInnerTable().setHTML(0, 0, "Pagamento");
		getInnerTable().setHTML(0, 1, "Codice");
		getInnerTable().setHTML(0, 2, "Listino");
		getInnerTable().setHTML(0, 3, "Ultima istanza");
		getInnerTable().setHTML(0, 4, "Pagante");
		getInnerTable().setHTML(0, 5, "Errore");
	}
	
	@Override
	protected void onEmptyResult() {}
	
	private void drawAsyncRowValues(Pagamenti pag, IstanzeAbbonamenti ia, SimplePanel codicePanel,
			SimplePanel abbonamentoDescPanel, SimplePanel pagantePanel, SimplePanel iconPanel) {
		final PagamentiCorrezioniTable fTable = this;
		final Pagamenti fPag = pag;
		final IstanzeAbbonamenti fIa = ia;
		boolean daPagare = false;
		//Codice
		codicePanel.clear();
		String abbText = ia.getAbbonamento().getCodiceAbbonamento()+" ["+ia.getId()+"] ";
		UriParameters params1 = new UriParameters();
		params1.add(AppConstants.PARAM_ID, ia.getId());
		Hyperlink codiceLink = params1.getHyperlink(abbText, UriManager.ABBONAMENTO);
		codicePanel.add(codiceLink);
		//Abbonamento
		abbonamentoDescPanel.clear();
		String iaDesc = "<b>"+ia.getListino().getTipoAbbonamento().getCodice()+"</b> ";
		if (ia.getFatturaDifferita() || ia.getListino().getFatturaDifferita()) {
			iaDesc += "Fattura differita. ";
		} else {
			if (ia.getPagato()) {
				iaDesc += "Pagato. ";
			} else {
				iaDesc += "Da pagare. ";
				daPagare = true;
			}
		}
		iaDesc += "Fine:<b>" +
				ia.getFascicoloFine().getTitoloNumero()+"</b>"+
				"&nbsp;" + ClientConstants.SPAN_SMALL_START +
				ClientConstants.FORMAT_MONTH.format(ia.getFascicoloFine().getDataInizio()) +
				ClientConstants.SPAN_STOP;
		abbonamentoDescPanel.add(new InlineHTML(iaDesc));
		//Pagante
		pagantePanel.clear();
		final Anagrafiche pagante;
		if (ia.getPagante() != null) {
			pagante = ia.getPagante();
		} else {
			pagante = ia.getAbbonato();
		}
		String paganteDesc = pagante.getIndirizzoPrincipale().getCognomeRagioneSociale();
		if (pagante.getIndirizzoPrincipale().getNome() != null) {
			if (pagante.getIndirizzoPrincipale().getNome().length() > 0) {
				paganteDesc = pagante.getIndirizzoPrincipale().getNome() + " " +paganteDesc;
			}
		}
		UriParameters params2 = new UriParameters();
		params2.add(AppConstants.PARAM_ID, pagante.getId());
		Hyperlink paganteLink = params2.getHyperlink(paganteDesc, UriManager.ANAGRAFICHE_MERGE);
		pagantePanel.add(paganteLink);
		
		//Icone
		HorizontalPanel hPanel = new HorizontalPanel();
		iconPanel.clear();
		iconPanel.add(hPanel);
		
		if (daPagare) {
			InlineHTML abbinaImg = new InlineHTML(SPAN_PREFIX+ClientConstants.ICON_EURO+SPAN_SUFFIX);
			abbinaImg.setTitle("Fattura come saldo");
			abbinaImg.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent arg0) {
					new FatturazionePopUp(fIa, fPag.getId(), fTable);
				}
			});
			hPanel.add(abbinaImg);
		}
		//Come acconto
		InlineHTML accontoImg = new InlineHTML(SPAN_PREFIX+ClientConstants.ICON_DATABASE+SPAN_SUFFIX);
		accontoImg.setTitle("Fattura come anticipo");
		accontoImg.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				acconto(fPag, pagante);
			}
		});
		hPanel.add(accontoImg);
		if (isEditor) {
			//Elimina
			InlineHTML trashImg = new InlineHTML(SPAN_PREFIX+ClientConstants.ICON_DELETE+SPAN_SUFFIX);
			trashImg.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent arg0) {
					delete(fPag);
				}
			});
			hPanel.add(trashImg);
		}
	}
	
	
	//Metodi asincroni
	
	
	//private void loadIstanzaPage(Integer idIa) {
	//	UriParameters params1 = new UriParameters();
	//	params1.add(AppConstants.PARAM_ID, idIa);
	//	params1.triggerUri(UriManager.ABBONAMENTO);
	//}
	
	//private void abbina(Pagamenti pag) {
	//	UriParameters params1 = new UriParameters();
	//	params1.add(AppConstants.PARAM_ID, pag.getIstanzaAbbonamento().getId());
	//	params1.triggerUri(UriManager.ABBONAMENTO);
	//	//boolean ok = Window.confirm("Il pagamento di EUR " + ClientConstants.FORMAT_CURRENCY.format(pag.getImporto()) + " " +
	//	//		"sara' abbinato all'abbonamento esistente " + pag.getIstanzaAbbonamento().getAbbonamento().getCodiceAbbonamento() + " " +
	//	//		"senza rinnovarlo o cambiarne la durata");
	//	//if (ok) {
	//	//	//WaitSingleton.get().start();
	//	//	pagamentiService.correzioneAbbina(pag.getId(), pag.getIstanzaAbbonamento().getId(), false,
	//	//			AuthSingleton.get().getUtente(), iaCallback);
	//	//}
	//}
	
	//private void rinnova(Pagamenti pag) {
	//	boolean ok = Window.confirm("L'abbonamento " + pag.getIstanzaAbbonamento().getAbbonamento().getCodiceAbbonamento() + " " +
	//			"sara' rinnovato con il pagamento di EUR " + ClientConstants.FORMAT_CURRENCY.format(pag.getImporto()) );
	//	if (ok) {
	//		//WaitSingleton.get().start();
	//		pagamentiService.correzioneRinnova(pag.getId(), pag.getIstanzaAbbonamento().getId(),
	//				AuthSingleton.get().getUtente(), iaCallback);
	//	}
	//}
	//
	//private void rigenera(Pagamenti pag) {
	//	boolean ok = Window.confirm("L'abbonamento " + pag.getIstanzaAbbonamento().getAbbonamento().getCodiceAbbonamento() + " " +
	//			"sara' rigenerato con il pagamento di EUR " + ClientConstants.FORMAT_CURRENCY.format(pag.getImporto()) +
	//			" a partire dal numero corrente.");
	//	if (ok) {
	//		//WaitSingleton.get().start();
	//		pagamentiService.correzioneRigenera(pag.getId(), pag.getIstanzaAbbonamento().getId(),
	//				AuthSingleton.get().getUtente(), iaCallback);
	//	}
	//}
	
	private void acconto(Pagamenti pag, Anagrafiche pagante) {
		boolean ok = Window.confirm("Il pagamento di EUR " + ClientConstants.FORMAT_CURRENCY.format(pag.getImporto()) + " " +
				"sara' fatturato come anticipo a " + pagante.getIndirizzoPrincipale().getCognomeRagioneSociale());
		if (ok) {
			//WaitSingleton.get().start();
			pagamentiService.registraAnticipoFattura(pag.getId(), pagante.getId(),
					AuthSingleton.get().getUtente().getId(), anagCallback);
		}
	}
	
	private void delete(Pagamenti pag) {
		boolean ok = Window.confirm("Il pagamento di EUR " + ClientConstants.FORMAT_CURRENCY.format(pag.getImporto()) + " " +
				"sara' eliminato in modo permanente.");
		if (ok) {
			//WaitSingleton.get().start();
			pagamentiService.deletePagamento(pag.getId(), deleteCallback);
		}
	}
	
	private void loadImportoTotale(SimplePanel importoTotPanel, Integer idIstanza) { 
		final SimplePanel fImportoTotPanel = importoTotPanel;
		final Integer fId = idIstanza;
		AsyncCallback<Double> callback = new AsyncCallback<Double>() {
			@Override
			public void onFailure(Throwable caught) {
				if (!(caught instanceof ValidationException)) {
					UiSingleton.get().addError(caught);
				} else {
					fImportoTotPanel.add(new InlineHTML("&nbsp;non necessario"));
				}
			}
			@Override
			public void onSuccess(Double result) {
				String labelHtml = "&nbsp;prezzo&nbsp;tot.&euro;";
				if (result == null) {
					labelHtml += "0";
				} else {
					labelHtml += ClientConstants.FORMAT_CURRENCY.format(result);
				}
				fImportoTotPanel.add(new InlineHTML(labelHtml));
			}
		};
		pagamentiService.getImportoTotale(fId, callback);
	}
	
	private void loadAsyncRowValues(Pagamenti pag, String codice, SimplePanel codicePanel,
			SimplePanel importoTotPanel, SimplePanel abbonamentoDescPanel, 
			SimplePanel pagantePanel, SimplePanel iconPanel) {
		final Pagamenti fPag = pag;
		final SimplePanel fCodicePanel = codicePanel;
		final SimplePanel fImportoTotPanel = importoTotPanel;
		final SimplePanel fAbbonamentoDescPanel = abbonamentoDescPanel;
		final SimplePanel fPagantePanel = pagantePanel;
		final SimplePanel fIconPanel = iconPanel;
		AsyncCallback<IstanzeAbbonamenti> callback = new AsyncCallback<IstanzeAbbonamenti>() {
			@Override
			public void onFailure(Throwable caught) {
				if (caught instanceof BusinessException) {
					UiSingleton.get().addError(caught);
				}
				fAbbonamentoDescPanel.add(new InlineHTML("Non trovato"));
			}
			@Override
			public void onSuccess(IstanzeAbbonamenti result) {
				loadImportoTotale(fImportoTotPanel, result.getId());
				drawAsyncRowValues(fPag, result, fCodicePanel, fAbbonamentoDescPanel, fPagantePanel, fIconPanel);
			}
		};
		abbonamentiService.findLastIstanzaByCodice(codice, callback);
	}
	
	
	//Inner classes
	
	
	
	public static class PagamentiConErroreModel implements DataModel<Pagamenti> {
		private Integer idPeriodico = null;
		
		public PagamentiConErroreModel(Integer idPeriodico) {
			this.idPeriodico=idPeriodico;
		}

		@Override
		public void find(int offset, int pageSize, AsyncCallback<List<Pagamenti>> callback) {
			pagamentiService.findPagamentiConErrore(idPeriodico, offset, pageSize, callback);
		}
	}
	
}
