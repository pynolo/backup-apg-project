package it.giunti.apg.client.frames;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DateBox;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.ComunicazioniService;
import it.giunti.apg.client.services.ComunicazioniServiceAsync;
import it.giunti.apg.client.widgets.select.DestinatarioSelect;
import it.giunti.apg.client.widgets.select.TipiMediaComSelect;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.model.EvasioniComunicazioni;
import it.giunti.apg.shared.model.Utenti;

public class EvasioneComunicazionePopUp extends PopupPanel implements IAuthenticatedWidget {

	private final ComunicazioniServiceAsync comService = GWT.create(ComunicazioniService.class);
	
	private FlexTable table = new FlexTable();
	private EvasioniComunicazioni item = null;
	private Integer idEvasioneCom = null;
	private Integer idIstanza = null;
	private String idTipoMedia = null;
	private boolean isOperator = false;
	//private boolean isEditor = false;
	private boolean isSuper = false;
	private IRefreshable parent = null;
	
	private TipiMediaComSelect mediaList = null;
	private DestinatarioSelect destinatarioList = null;
	private TextArea messageArea = null;
	private DateBox creazioneDate = null;
	private DateBox estrazioneDate = null;
	private TextBox noteText = null;
	private CheckBox rinnovoCheck = null;
	private CheckBox eliminatoCheck = null;
	private InlineHTML suggerimentoMessaggio = null;
	
	public EvasioneComunicazionePopUp() {
		super(false);
	}

	public void initByEvasioneComunicazione(Integer idEvasioneCom, IRefreshable parent) {
		this.idEvasioneCom = idEvasioneCom;
		this.idIstanza = null;
		this.parent=parent;
		AuthSingleton.get().queueForAuthentication(this);
	}

	public void initByIstanzaAbbonamento(Integer idIstanza, String idTipoMedia, IRefreshable parent) {
		this.idEvasioneCom = AppConstants.NEW_ITEM_ID;
		this.idIstanza = idIstanza;
		this.idTipoMedia = idTipoMedia;
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
		//isEditor = (ruolo >= AppConstants.RUOLO_EDITOR);
		isSuper = (ruolo >= AppConstants.RUOLO_SUPER);
		//UI
		if (isOperator) {
			this.setModal(true);
			this.setGlassEnabled(true);
			this.add(table);
			loadEvasioneCom();
		}
	}
	
	private void drawEvasioneComunicazione() {
		int r=0;
		boolean storicizzato = (item.getDataEstrazione() != null);
		boolean enabled = (isOperator && !storicizzato) || isSuper;
		HTML titleHtml = new HTML("Comunicazione");
		titleHtml.setStyleName("frame-title");
		table.setWidget(r, 0, titleHtml);
		table.getFlexCellFormatter().setColSpan(r, 0, 5);
		r++;
		
		//Tipo media
		table.setHTML(r, 0, "Media");
		mediaList = new TipiMediaComSelect(item.getIdTipoMedia());
		mediaList.setEnabled(false);
		table.setWidget(r, 1, mediaList);
		//Destinatario
		table.setHTML(r, 3, "Destinatario");
		destinatarioList = new DestinatarioSelect(item.getIdTipoDestinatario());
		destinatarioList.setEnabled(enabled);
		table.setWidget(r, 4, destinatarioList);
		r++;
		
		//Richiesta rinnovo
		table.setHTML(r, 0, "Richiesta rinnovo");
		rinnovoCheck = new CheckBox();
		rinnovoCheck.setValue(item.getRichiestaRinnovo());
		rinnovoCheck.setEnabled(enabled);
		table.setWidget(r, 1, rinnovoCheck);
		//Eliminato
		table.setHTML(r, 3, "Estrazione annullata");
		eliminatoCheck = new CheckBox();
		eliminatoCheck.setValue(item.getEliminato());
		eliminatoCheck.setEnabled(isSuper);
		table.setWidget(r, 4, eliminatoCheck);
		r++;
		
		boolean hasMessage = (item.getIdTipoMedia().equals(AppConstants.COMUN_MEDIA_BOLLETTINO)) ||
				(item.getIdTipoMedia().equals(AppConstants.COMUN_MEDIA_EMAIL));
		if (hasMessage) {
			//Messaggio
			table.setHTML(r, 0, "Messaggio");
			messageArea = new TextArea();
			messageArea.setWidth("33em");
			messageArea.setHeight("5em");
			messageArea.setValue(item.getMessaggio());
			messageArea.setEnabled(enabled);
			messageArea.addKeyPressHandler(new KeyPressHandler() {
				@Override
				public void onKeyPress(KeyPressEvent event) {
					updateSuggerimento();
				}
			});
			messageArea.addValueChangeHandler(new ValueChangeHandler<String>() {
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					updateSuggerimento();
				}
			});
			messageArea.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					updateSuggerimento();
				}
			});
			table.setWidget(r, 1, messageArea);
			table.getFlexCellFormatter().setColSpan(r, 1, 4);
			r++;
			//Suggerimento
			suggerimentoMessaggio = new InlineHTML();
			table.setWidget(r, 1, suggerimentoMessaggio);
			table.getFlexCellFormatter().setColSpan(r, 1, 4);
			r++;
		}
		
		//Data creazione
		table.setHTML(r, 0, "Data creazione");
		creazioneDate = new DateBox();
		creazioneDate.setValue(item.getDataCreazione());
		creazioneDate.setFormat(ClientConstants.BOX_FORMAT_DAY);
		creazioneDate.setEnabled(isSuper);
		creazioneDate.setWidth("10em");
		table.setWidget(r, 1, creazioneDate);
		//Data estrazione
		table.setHTML(r, 3, "Data estrazione");
		estrazioneDate = new DateBox();
		estrazioneDate.setValue(item.getDataEstrazione());
		estrazioneDate.setFormat(ClientConstants.BOX_FORMAT_DAY);
		estrazioneDate.setEnabled(isSuper);
		estrazioneDate.setWidth("10em");
		table.setWidget(r, 4, estrazioneDate);
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
		if (idEvasioneCom.equals(AppConstants.NEW_ITEM_ID)) {
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
	
	private void updateSuggerimento() {
		int disp = AppConstants.BOLLETTINO_MESSAGE_MAX_LENGTH - messageArea.getValue().length();
		String suggerimento = "Caratteri disponibili: <b>"+disp+"</b>";
		suggerimentoMessaggio.setHTML(suggerimento);
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
				idEvasioneCom = result;
				parent.refresh();
				close();
				UiSingleton.get().addInfo(AppConstants.MSG_SAVE_OK);
				WaitSingleton.get().stop();
			}
		};
		try {
			//Verifica se è un bolletino. Se lo è avvisa su eventuali annullamenti
			if ( (item.getIstanzaAbbonamento() != null) && 
					(item.getIdTipoMedia().equals(AppConstants.COMUN_MEDIA_BOLLETTINO) /*||
					item.getIdTipoMedia().equals(AppConstants.COMUN_MEDIA_NDD)*/)
				){
				boolean pagato = item.getIstanzaAbbonamento().getPagato();
				boolean ricRinnovo = rinnovoCheck.getValue();
				if (pagato && !ricRinnovo) {
						UiSingleton.get().addWarning("L'abbonamento risulta gi&agrave; " +
								"saldato, quindi la comunicazione verr&agrave; " +
								"annullata al momento dell'estrazione.<br /> Perché non " +
								"accada, il valore nella casella 'Pagato' deve essere negativo.");
				}
			}
			//Assegnazione
			if (item.getIstanzaAbbonamento() != null) {
				item.setIdIstanzaAbbonamentoT(item.getIstanzaAbbonamento().getId());
			} else {
				item.setIdIstanzaAbbonamentoT(idIstanza);
			}
			item.setIdTipoDestinatario(destinatarioList.getSelectedValueString());
			item.setIdTipoMedia(mediaList.getSelectedValueString());
			item.setRichiestaRinnovo(rinnovoCheck.getValue());
			item.setEliminato(eliminatoCheck.getValue());
			if (messageArea != null) {
				item.setMessaggio(messageArea.getValue().trim());
			} else {
				item.setMessaggio(null);
			}
			item.setDataCreazione(creazioneDate.getValue());
			item.setDataEstrazione(estrazioneDate.getValue());
			item.setNote(noteText.getValue().trim());
			item.setDataModifica(DateUtil.now());
			item.setIdUtente(AuthSingleton.get().getUtente().getId());
		} catch (Exception e) {
			throw new ValidationException(e.getLocalizedMessage());
		}
		WaitSingleton.get().start();
		comService.saveOrUpdateEvasioneComunicazione(item, callback);
	}

	private void loadEvasioneCom() {
		AsyncCallback<EvasioniComunicazioni> callback = new AsyncCallback<EvasioniComunicazioni>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(EvasioniComunicazioni result) {
				item = result;
				idTipoMedia = result.getIdTipoMedia();
				drawEvasioneComunicazione();
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		//look for item with id only if id is defined
		if (idEvasioneCom.intValue() != AppConstants.NEW_ITEM_ID) {
			comService.findEvasioneComunicazioneById(idEvasioneCom, callback);
		} else {
			//is new abbonamento
			comService.createEvasioneComunicazione(idIstanza, idTipoMedia, callback);
		}
	}


}
