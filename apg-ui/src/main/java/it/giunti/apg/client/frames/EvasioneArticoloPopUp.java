package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.ArticoliService;
import it.giunti.apg.client.services.ArticoliServiceAsync;
import it.giunti.apg.client.widgets.DateOnlyBox;
import it.giunti.apg.client.widgets.select.ArticoliSelect;
import it.giunti.apg.client.widgets.select.DestinatarioSelect;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.EvasioniArticoli;
import it.giunti.apg.shared.model.Utenti;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;

public class EvasioneArticoloPopUp extends PopupPanel implements IAuthenticatedWidget {

	private final ArticoliServiceAsync articoliService = GWT.create(ArticoliService.class);
	
	private FlexTable table = new FlexTable();
	private EvasioniArticoli item = null;
	private Integer idEvasioneArticolo = null;
	private Integer idIstanza = null;
	private Integer idAnagrafica = null;
	private boolean canChangeCopie = true;
	private boolean canChangeDestinatario = true;
	private boolean isOperator = false;
	private boolean isEditor = false;
	private boolean isSuper = false;
	private IRefreshable parent = null;
	
	private ArticoliSelect articoliList = null;
	private DestinatarioSelect destList = null;
	private CheckBox istanzaFuturaCheck = null;
	private TextBox copieText = null;
	private DateOnlyBox creazioneDate = null;
	private DateOnlyBox estrazioneDate = null;
	private DateOnlyBox annullamentoDate = null;
	private TextBox noteText = null;

	public EvasioneArticoloPopUp() {
		super(false);
	}

	public void initByEvasioneArticolo(Integer idEvasioneArticolo, boolean canChangeCopie, boolean canChangeDestinatario, IRefreshable parent) {
		this.idEvasioneArticolo = idEvasioneArticolo;
		this.idIstanza = null;
		this.idAnagrafica = null;
		this.canChangeCopie=canChangeCopie;
		this.canChangeDestinatario=canChangeDestinatario;
		this.parent=parent;
		AuthSingleton.get().queueForAuthentication(this);
	}

	public void initByIstanzaAbbonamento(Integer idIstanza, boolean canChangeCopie, boolean canChangeDestinatario, IRefreshable parent) {
		this.idEvasioneArticolo = AppConstants.NEW_ITEM_ID;
		this.idIstanza = idIstanza;
		this.idAnagrafica = null;
		this.canChangeCopie=canChangeCopie;
		this.canChangeDestinatario=canChangeDestinatario;
		this.parent = parent;
		AuthSingleton.get().queueForAuthentication(this);
	}
	
	public void initByAnagrafica(Integer idAnagrafica, boolean canChangeCopie, boolean canChangeDestinatario, IRefreshable parent) {
		this.idEvasioneArticolo = AppConstants.NEW_ITEM_ID;
		this.idIstanza = null;
		this.idAnagrafica = idAnagrafica;
		this.canChangeCopie=canChangeCopie;
		this.canChangeDestinatario=canChangeDestinatario;
		this.parent = parent;
		AuthSingleton.get().queueForAuthentication(this);
	}
	
	@Override
	public void onSuccessfulAuthentication(Utenti utente) {
		init(utente);
	}
	
	private void init(Utenti utente) {
		int ruolo = utente.getRuolo().getId();
		// Editing rights
		isOperator = (ruolo >= AppConstants.RUOLO_OPERATOR);
		isEditor = (ruolo >= AppConstants.RUOLO_EDITOR);
		isSuper = (ruolo >= AppConstants.RUOLO_SUPER);
		//UI
		if (isOperator) {
			this.setModal(true);
			this.setGlassEnabled(true);
			this.add(table);
			loadEvasioneArticolo();
		}
	}
	
	private void drawEvasioneArticolo() {
		int r=0;
		
		HTML titleHtml = new HTML("Evasione del articolo");
		titleHtml.setStyleName("frame-title");
		table.setWidget(r, 0, titleHtml);
		table.getFlexCellFormatter().setColSpan(r, 0, 5);
		r++;
		
		//Articolo
		table.setHTML(r, 0, "Articolo scelto");
		Integer idArticolo=0;
		Date listDate = new Date();
		if (item.getArticolo() != null) {
			idArticolo=item.getArticolo().getId();
			listDate = item.getDataCreazione();
		}
		articoliList = new ArticoliSelect(idArticolo, listDate, listDate, true, false);
		articoliList.setEnabled(isOperator);
		//articoliList.addChangeHandler(new ChangeHandler() {
		//	@Override
		//	public void onChange(ChangeEvent event) {
		//		loadDataLimite();
		//	}
		//});
		table.setWidget(r, 1, articoliList);
		table.getFlexCellFormatter().setColSpan(r, 1, 4);
		r++;
		
		//Destinatario
		table.setHTML(r, 0, "Destinatario");
		destList = new DestinatarioSelect(item.getIdTipoDestinatario());
		destList.setEnabled(isOperator && canChangeDestinatario);
		table.setWidget(r, 1, destList);
		//Istanza futura
		table.setHTML(r, 3, "Prenotato al rinnovo");
		istanzaFuturaCheck = new CheckBox();
		istanzaFuturaCheck.setValue(item.getPrenotazioneIstanzaFutura());
		istanzaFuturaCheck.setEnabled(isOperator);
		table.setWidget(r, 4, istanzaFuturaCheck);
		r++;
		
		//Data limite
		table.setHTML(r, 0, "Copie");
		copieText = new TextBox();
		copieText.setValue(ClientConstants.FORMAT_INTEGER.format(item.getCopie()));
		copieText.setEnabled(isOperator && canChangeCopie);
		copieText.setWidth("3em");
		table.setWidget(r, 1, copieText);
		//Data creazione
		table.setHTML(r, 3, "Data creazione");
		creazioneDate = new DateOnlyBox();
		creazioneDate.setValue(item.getDataCreazione());
		creazioneDate.setFormat(ClientConstants.BOX_FORMAT_DAY);
		creazioneDate.setEnabled(isSuper);
		creazioneDate.setWidth("10em");
		table.setWidget(r, 4, creazioneDate);
		r++;
		
		//Data estrazione
		table.setHTML(r, 0, "Data estrazione");
		estrazioneDate = new DateOnlyBox();
		estrazioneDate.setValue(item.getDataInvio());
		estrazioneDate.setFormat(ClientConstants.BOX_FORMAT_DAY);
		estrazioneDate.setEnabled(isEditor);
		estrazioneDate.setWidth("10em");
		table.setWidget(r, 1, estrazioneDate);
		//Eliminato
		table.setHTML(r, 3, "Data annullamento");
		annullamentoDate = new DateOnlyBox();
		annullamentoDate.setValue(item.getDataAnnullamento());
		annullamentoDate.setFormat(ClientConstants.BOX_FORMAT_DAY);
		annullamentoDate.setEnabled(isSuper);
		annullamentoDate.setWidth("10em");
		table.setWidget(r, 4, annullamentoDate);
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
		
		HorizontalPanel buttonPanel = new HorizontalPanel();
		// Bottone SALVA
		Button submitButton = new Button("Salva", new ClickHandler() {
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
		if (idArticolo.equals(AppConstants.NEW_ITEM_ID)) {
			submitButton.setText("Crea");
		}
		submitButton.setEnabled(isOperator);
		buttonPanel.add(submitButton);
		
		// Bottone ANNULLA
		Button cancelButton = new Button("Annulla", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				close();
			}
		});
		buttonPanel.add(cancelButton);
		
		table.setWidget(r,0,buttonPanel);
		table.getFlexCellFormatter().setColSpan(r, 0, 5);
		
		//Info modifica
		if ((item.getDataModifica() != null) && (item.getId() != null)) {
			r++;
			InlineHTML modifiedInfo = new InlineHTML("<br/><i>Modificato o inserito da "+item.getIdUtente()+" il "+
					ClientConstants.FORMAT_DATETIME.format(item.getDataModifica())+"</i>");
			table.setWidget(r,0,modifiedInfo);
			table.getFlexCellFormatter().setColSpan(r, 0, 5);
		}
		
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
				close();
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(Integer result) {			
				idEvasioneArticolo = result;
				parent.refresh();
				close();
				UiSingleton.get().addInfo(AppConstants.MSG_SAVE_OK);
				WaitSingleton.get().stop();
			}
		};
		try {
			Integer copie = ValueUtil.stoi(copieText.getValue());
			if (copie == null) throw new ValidationException("Il valore delle copie non è valido");
			if (copie < 1) throw new ValidationException("Il valore delle copie non è valido");
			item.setIdArticoloT(articoliList.getSelectedValueString());
			if ((item.getIdIstanzaAbbonamento() == null) && (idIstanza != null)) {
				item.setIdIstanzaAbbonamento(idIstanza);
			}
			if ((item.getIdAnagrafica() == null) && (idAnagrafica != null)) {
				item.setIdAnagrafica(idAnagrafica);
			}
			String idTipoDestinatario = destList.getSelectedValueString();
			if (idTipoDestinatario == null) throw new ValidationException("Il tipo destinatario e' obbligatorio");
			item.setIdTipoDestinatario(idTipoDestinatario);
			item.setPrenotazioneIstanzaFutura(istanzaFuturaCheck.getValue());
			//item.setDataLimite(limiteDate.getValue());
			item.setCopie(copie);
			item.setDataCreazione(creazioneDate.getValue());
			item.setDataInvio(estrazioneDate.getValue());
			item.setDataAnnullamento(annullamentoDate.getValue());
			item.setNote(noteText.getValue());
			item.setDataModifica(new Date());
			item.setIdUtente(AuthSingleton.get().getUtente().getId());
		} catch (Exception e) {
			throw new ValidationException(e.getMessage());
		}
		WaitSingleton.get().start();
		articoliService.saveOrUpdateEvasioneArticolo(item, callback);
	}

	private void loadEvasioneArticolo() {
		AsyncCallback<EvasioniArticoli> callback = new AsyncCallback<EvasioniArticoli>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(EvasioniArticoli result) {
				item = result;
				drawEvasioneArticolo();
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		//look for item with id only if id is defined
		if (idEvasioneArticolo.intValue() != AppConstants.NEW_ITEM_ID) {
			articoliService.findEvasioniArticoliById(idEvasioneArticolo, callback);
		} else {
			//is new abbonamento
			String idUtente = AuthSingleton.get().getUtente().getId();
			if (idIstanza != null) {
				articoliService.createEmptyEvasioneArticoloFromIstanza(idIstanza,
						AppConstants.DEST_BENEFICIARIO, idUtente, callback);
			} else {
				if (idAnagrafica != null) {
					articoliService.createEvasioneArticoloFromAnagrafica(idAnagrafica,
							1, AppConstants.DEST_BENEFICIARIO, idUtente, callback);
				}
			}
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
	//			limiteDate.setValue(result);
	//		}
	//	};
	//	//look for item with id only if id is defined
	//	if (item.getDataLimite() == null) {
	//		Integer idArticolo = articoliList.getSelectedValueInt();
	//		articoliService.loadDataLimite(idIstanza, idArticolo, callback);
	//	}
	//}
	
}
