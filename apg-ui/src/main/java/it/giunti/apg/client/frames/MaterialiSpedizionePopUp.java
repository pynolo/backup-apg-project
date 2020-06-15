package it.giunti.apg.client.frames;

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

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.MaterialiService;
import it.giunti.apg.client.services.MaterialiServiceAsync;
import it.giunti.apg.client.widgets.DateSafeBox;
import it.giunti.apg.client.widgets.MaterialiPanel;
import it.giunti.apg.client.widgets.select.AnagraficaDestinatarioSelect;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.MaterialiSpedizione;
import it.giunti.apg.shared.model.Utenti;

public class MaterialiSpedizionePopUp extends PopupPanel implements IAuthenticatedWidget {

	private final MaterialiServiceAsync matService = GWT.create(MaterialiService.class);
	
	private FlexTable table = new FlexTable();
	private MaterialiSpedizione item = null;
	private Integer idMatSped = null;
	private Integer idAnagrafica = null;
	private Integer idAbbonamento = null;
	private boolean canChangeCopie = true;
	private boolean canChangeDestinatario = true;
	private boolean isOperator = false;
	private boolean isEditor = false;
	private boolean isSuper = false;
	private IRefreshable parent = null;
	
	private MaterialiPanel materialiPanel = null;
	private AnagraficaDestinatarioSelect destList = null;
	private CheckBox istanzaFuturaCheck = null;
	private TextBox copieText = null;
	private DateSafeBox creazioneDate = null;
	private DateSafeBox estrazioneDate = null;
	private DateSafeBox annullamentoDate = null;
	private TextBox noteText = null;

	public MaterialiSpedizionePopUp() {
		super(false);
	}

	public void initByMaterialiSpedizione(Integer idMatSped, boolean canChangeCopie, boolean canChangeDestinatario, IRefreshable parent) {
		this.idMatSped = idMatSped;
		this.idAnagrafica = null;
		this.idAbbonamento = null;
		this.canChangeCopie=canChangeCopie;
		this.canChangeDestinatario=canChangeDestinatario;
		this.parent=parent;
		AuthSingleton.get().queueForAuthentication(this);
	}

	public void initByAbbonamento(Integer idAbbonamento, boolean canChangeCopie, boolean canChangeDestinatario, IRefreshable parent) {
		this.idMatSped = AppConstants.NEW_ITEM_ID;
		this.idAbbonamento = idAbbonamento;
		this.idAnagrafica = null;
		this.canChangeCopie=canChangeCopie;
		this.canChangeDestinatario=canChangeDestinatario;
		this.parent = parent;
		AuthSingleton.get().queueForAuthentication(this);
	}
	
	public void initByAnagrafica(Integer idAnagrafica, boolean canChangeCopie, boolean canChangeDestinatario, IRefreshable parent) {
		this.idMatSped = AppConstants.NEW_ITEM_ID;
		this.idAbbonamento = null;
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
			loadMaterialiSpedizione();
		}
	}
	
	private void drawMaterialiSpedizione() {
		int r=0;
		
		HTML titleHtml = new HTML("Spedizione materiale");
		titleHtml.setStyleName("frame-title");
		table.setWidget(r, 0, titleHtml);
		table.getFlexCellFormatter().setColSpan(r, 0, 5);
		r++;
		
		//Materiale
		table.setHTML(r, 0, "Materiale");
		materialiPanel = new MaterialiPanel(idMatSped, 30, isOperator);
		//TODO table.setWidget(r, 1, materialiPanel);
		table.getFlexCellFormatter().setColSpan(r, 1, 4);
		r++;
		
		//Destinatario
		table.setHTML(r, 0, "Destinatario");
		Integer idAna = item.getIdAnagrafica();
		if (idAnagrafica != null) idAna = idAnagrafica;
		destList = new AnagraficaDestinatarioSelect(idAna, item.getIdAbbonamento());
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
		creazioneDate = new DateSafeBox();
		creazioneDate.setValue(item.getDataCreazione());
		creazioneDate.setFormat(ClientConstants.BOX_FORMAT_DAY);
		creazioneDate.setEnabled(isSuper);
		creazioneDate.setWidth("10em");
		table.setWidget(r, 4, creazioneDate);
		r++;
		
		//Data estrazione
		table.setHTML(r, 0, "Data estrazione");
		estrazioneDate = new DateSafeBox();
		estrazioneDate.setValue(item.getDataInvio());
		estrazioneDate.setFormat(ClientConstants.BOX_FORMAT_DAY);
		estrazioneDate.setEnabled(isEditor);
		estrazioneDate.setWidth("10em");
		table.setWidget(r, 1, estrazioneDate);
		//Eliminato
		table.setHTML(r, 3, "Data annullamento");
		annullamentoDate = new DateSafeBox();
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
		if (idMatSped.equals(AppConstants.NEW_ITEM_ID)) {
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
		if ((item.getDataCreazione() != null) && (item.getId() != null)) {
			r++;
			InlineHTML modifiedInfo = new InlineHTML("<br/><i>Inserito da "+item.getIdUtente()+" il "+
					ClientConstants.FORMAT_DATETIME.format(item.getDataCreazione())+"</i>");
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
				idMatSped = result;
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
			item.setMaterialeCmT(materialiPanel.getCodiceMeccanografico());
			item.setIdAnagrafica(destList.getSelectedValueInt());
			item.setPrenotazioneIstanzaFutura(istanzaFuturaCheck.getValue());
			//item.setDataLimite(limiteDate.getValue());
			item.setCopie(copie);
			item.setDataCreazione(creazioneDate.getValue());
			item.setDataInvio(estrazioneDate.getValue());
			item.setDataAnnullamento(annullamentoDate.getValue());
			item.setNote(noteText.getValue());
			item.setIdUtente(AuthSingleton.get().getUtente().getId());
		} catch (Exception e) {
			throw new ValidationException(e.getMessage());
		}
		WaitSingleton.get().start();
		matService.saveOrUpdateMaterialiSpedizione(item, callback);
	}

	private void loadMaterialiSpedizione() {
		AsyncCallback<MaterialiSpedizione> callback = new AsyncCallback<MaterialiSpedizione>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(MaterialiSpedizione result) {
				item = result;
				drawMaterialiSpedizione();
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		//look for item with id only if id is defined
		if (idMatSped.intValue() != AppConstants.NEW_ITEM_ID) {
			matService.findMaterialiSpedizioneById(idMatSped, callback);
		} else {
			//is new abbonamento
			//String idUtente = AuthSingleton.get().getUtente().getId();
			if (idAbbonamento != null) {
				matService.createMaterialiSpedizioneForAbbonamento(idAbbonamento, callback);
			} else {
				if (idAnagrafica != null) {
					matService.createMaterialiSpedizioneForAnagrafica(idAnagrafica, 1, callback);
				}
			}
		}
	}

}