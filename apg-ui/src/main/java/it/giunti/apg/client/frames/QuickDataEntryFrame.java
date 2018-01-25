package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.AbbonamentiService;
import it.giunti.apg.client.services.AbbonamentiServiceAsync;
import it.giunti.apg.client.services.FascicoliService;
import it.giunti.apg.client.services.FascicoliServiceAsync;
import it.giunti.apg.client.widgets.AnagraficheSuggestionPanel;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.EvasioniFascicoli;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Pagamenti;
import it.giunti.apg.shared.model.Utenti;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

public class QuickDataEntryFrame extends FramePanel implements IRefreshable, IAuthenticatedWidget {
	
	private final AbbonamentiServiceAsync abbonamentiService = GWT.create(AbbonamentiService.class);
	//private final ArticoliServiceAsync articoliService = GWT.create(ArticoliService.class);
	private final FascicoliServiceAsync fascicoliService = GWT.create(FascicoliService.class);
	
	private static final String FRAME_TITLE = "Inserimento veloce";
	private Utenti utente = null;
	private boolean isOperator = false;
	
	private FramePanel stack = null;
	private FlexTable contentTable = null;
	private QuickAnagPanel anagPanel = null;
	private QuickSuggPanel suggPanel = null;
	private QuickAbboPanel abboPanel = null;
	private ButtonPanel buttonPanel = null;
	
	public QuickDataEntryFrame() {
		AuthSingleton.get().queueForAuthentication(this);
	}
	
	@Override
	public void onSuccessfulAuthentication(Utenti utente) {
		// Editing rights
		this.utente = utente;
		isOperator = (utente.getRuolo().getId() >= AppConstants.RUOLO_OPERATOR);
		// UI
		if (isOperator) {
			draw();
		}
	}
	
	private void draw() {
		this.clear();
		GWT.debugger();//TODO
		stack = new FramePanel();
		contentTable = new FlexTable();
		suggPanel = new QuickSuggPanel(this);
		anagPanel = new QuickAnagPanel(null, suggPanel, false, isOperator);
		abboPanel = new QuickAbboPanel(utente);
		buttonPanel = new ButtonPanel();
		
		HTML anagTitle = new HTML("Anagrafica");
		anagTitle.setStyleName("section-title");
		HTML abboTitle = new HTML("Abbonamento");
		abboTitle.setStyleName("section-title");
		contentTable.setWidget(0, 0, anagTitle);
		contentTable.getFlexCellFormatter().setColSpan(0, 0, 2);
		contentTable.setWidget(1, 0, anagPanel);
		contentTable.setWidget(1, 1, suggPanel);
		contentTable.setWidget(2, 0, abboTitle);
		contentTable.getFlexCellFormatter().setColSpan(2, 0, 2);
		contentTable.setWidget(3, 0, abboPanel);
		contentTable.getFlexCellFormatter().setColSpan(3, 0, 2);
		contentTable.setWidget(4, 0, buttonPanel);
		contentTable.getFlexCellFormatter().setColSpan(4, 0, 2);
		stack.add(contentTable, FRAME_TITLE);
		this.add(stack);
		contentTable.setWidth("100%");
	}

	@Override
	public void refresh() {
		if (suggPanel.getValue() != null) {
			//C'è un nome selezionato
			AnagraficheSuggestionPanel asp =
					new AnagraficheSuggestionPanel(suggPanel.getValue(), suggPanel, false);
			Anchor unbindLink = new Anchor("Annulla l'abbinamento");
			unbindLink.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent arg0) {
					suggPanel.clearSelection();
				}
			});
			contentTable.setWidget(1, 0, asp);
			contentTable.setWidget(1, 1, unbindLink);
		} else {
			//Non c'è un nome selezionato
			contentTable.setWidget(1, 0, anagPanel);
			contentTable.setWidget(1, 1, suggPanel);
		}
	}
	
	
	//Async Methods

	
	
	private void save() {
		AsyncCallback<String> callback = new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				WaitSingleton.get().stop();
				if (caught instanceof ValidationException) {
					UiSingleton.get().addWarning(caught.getMessage());
				} else {
					UiSingleton.get().addError(caught);
				}
			}
			@Override
			public void onSuccess(String codiceAbb) {
				WaitSingleton.get().stop();
				//Salva l'eventuale articolo
				//saveEvasioneArticolo(codiceAbb);
				//Assegna eventuali arretrati
				generaTuttiArretrati(codiceAbb);
				//Warning sempre presente
				UiSingleton.get().addWarning("L'abbonamento e' stato creato correttamente con codice <b>"+codiceAbb+"</b>");
				//Svuota il form
				draw();
			}
		};
		try {
			//Salvataggio
			Anagrafiche anag = suggPanel.getValue();
			if (anag == null) {
				anag = anagPanel.getValue();
			}
			Pagamenti pag = abboPanel.getPagamento();
			IstanzeAbbonamenti ia = abboPanel.getIstanzaAbbonamento();
			WaitSingleton.get().start();
			abbonamentiService.saveWithAnagraficaAndPayment(anag, ia, pag, 
					callback);
		} catch (ValidationException e) {
			UiSingleton.get().addWarning(e.getMessage());
		}
	}
	
//	private void saveEvasioneArticolo(String codiceAbbonamento) {
//		AsyncCallback<Integer> callback = new AsyncCallback<Integer>() {
//			@Override
//			public void onFailure(Throwable caught) {
//				WaitSingleton.get().stop();
//				if (caught instanceof ValidationException) {
//					UiSingleton.get().addWarning(caught.getMessage());
//				} else {
//					UiSingleton.get().addError(caught);
//				}
//			}
//			@Override
//			public void onSuccess(Integer result) {
//				WaitSingleton.get().stop();
//			}
//		};
//		//Salvataggio
//		Integer idArticolo = abboPanel.getIdArticoloScelto();
//		String idTipo = abboPanel.getIdTipoDestinatario();
//		if (idArticolo != null) {
//			WaitSingleton.get().start();
//			articoliService.createEvasioneArticoloWithCodAbbo(codiceAbbonamento, idArticolo, idTipo,
//					AuthSingleton.get().getUtente().getId(), callback);
//		}
//	}
	
	private void generaTuttiArretrati(String codiceAbb) {
		final String fCodiceAbb = codiceAbb;
		AsyncCallback<List<EvasioniFascicoli>> callback = new AsyncCallback<List<EvasioniFascicoli>>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(List<EvasioniFascicoli> result) {
				String warn = "";
				for (EvasioniFascicoli ef: result) {
					warn += ef.getFascicolo().getTitoloNumero()+" ";
				}
				if (result.size() == 1) {
					warn = "All'abbonamento "+fCodiceAbb+" e' stato abbinato l'arretrato "+warn;
				}
				if (result.size() > 1) {
					warn = "All'abbonamento "+fCodiceAbb+" sono stati abbinati gli arretrati "+warn;
				}
				if (warn.length() > 1) UiSingleton.get().addWarning(warn);
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		Date today = DateUtil.now();
		fascicoliService.createMassiveArretrati(codiceAbb, today,
				AuthSingleton.get().getUtente().getId(), callback);
	}
	
	
	
	//Inner Classes
	
	
	
	public class ButtonPanel extends FlowPanel {
		public ButtonPanel() {
			Button submitButton = new Button("&nbsp;Crea&nbsp;");
			submitButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent arg0) {
					submit();
				}
			});
			this.add(submitButton);
		}
		
		private void submit() {
			save();
		}
	}

}
