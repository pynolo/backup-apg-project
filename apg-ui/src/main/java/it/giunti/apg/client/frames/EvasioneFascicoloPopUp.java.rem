package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.FascicoliService;
import it.giunti.apg.client.services.FascicoliServiceAsync;
import it.giunti.apg.client.widgets.select.FascicoliSelect;
import it.giunti.apg.client.widgets.select.TipiEvasioneSelect;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.model.EvasioniFascicoli;
import it.giunti.apg.shared.model.Utenti;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DateBox;

public class EvasioneFascicoloPopUp extends PopupPanel implements IAuthenticatedWidget {

	private final FascicoliServiceAsync fascicoliService = GWT.create(FascicoliService.class);
	
	private FlexTable table = new FlexTable();
	private EvasioniFascicoli item = null;
	private Integer idPeriodico = null;
	private Integer idIstanza = null;
	private Integer idEf = null;
	private Date startIstanzaDt = null;
	private boolean isOperator = false;
	//private boolean isEditor = false;
	//private boolean isSuper = false;
	private IRefreshable parent = null;
	
	private TipiEvasioneSelect tipoEvasioneList = null;
	private FascicoliSelect fascicoliList = null;
	private TextBox copieText = null;
	private DateBox estrazioneDate = null;
	private TextBox noteText = null;
	
	
	
	public EvasioneFascicoloPopUp() {
		super(false);
	}
	
	public void initByPeriodicoIstanza(Integer idPeriodico, Integer idIstanza, Date startIstanzaDt, IRefreshable parent) {
		this.idPeriodico=idPeriodico;
		this.idEf=AppConstants.NEW_ITEM_ID;
		this.idIstanza=idIstanza;
		this.startIstanzaDt=startIstanzaDt;
		this.parent=parent;
		AuthSingleton.get().queueForAuthentication(this);
	}
	
	public void initByIstanzaFascicolo(Integer idIstanza, Date startIstanzaDt, Integer idFascicolo, IRefreshable parent) {
		this.idPeriodico=null;
		this.idEf=idFascicolo;
		this.idIstanza=idIstanza;
		this.startIstanzaDt=startIstanzaDt;
		this.parent=parent;
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
		//isSuper = (ruolo >= AppConstants.RUOLO_SUPER);
		//UI
		if (isOperator) {
			this.setModal(true);
			this.setGlassEnabled(true);
			this.add(table);
			loadEvasioneFascicolo();
		}
	}
	
	private void drawEvasioneFascicolo() {
		int r=0;
		
		HTML titleHtml = new HTML("Stato del fascicolo");
		titleHtml.setStyleName("frame-title");
		table.setWidget(r, 0, titleHtml);
		table.getFlexCellFormatter().setColSpan(r, 0, 5);
		r++;
		
		//Tipo Evasione
		table.setHTML(r, 0, "Tipo evasione");
		tipoEvasioneList = new TipiEvasioneSelect(item.getIdTipoEvasione());
		tipoEvasioneList.setEnabled(isOperator);
		table.setWidget(r, 1, tipoEvasioneList);
		//Numero fascicolo
		table.setHTML(r, 3, "Numero fascicolo");
		Integer fasId=-1;
		if (item.getFascicolo() != null) {
			fasId=item.getFascicolo().getId();
		}
		long time = this.startIstanzaDt.getTime();
		long startDt = time - AppConstants.MONTH * 12;
		long finishDt = time + AppConstants.MONTH * 36;
		fascicoliList = new FascicoliSelect(fasId, idPeriodico, startDt, finishDt, true, true, true, true, false);
		fascicoliList.setEnabled(isOperator);
		table.setWidget(r, 4, fascicoliList);
		r++;
		
		//Quantita
		table.setHTML(r, 0, "Copie"+ClientConstants.MANDATORY);
		copieText = new TextBox();
		copieText.setText(item.getCopie().toString());
		copieText.setMaxLength(3);
		copieText.setEnabled(false);
		table.setWidget(r, 1, copieText);
		//Data stampa
		table.setHTML(r, 3, "Data estrazione");
		estrazioneDate = new DateBox();
		estrazioneDate.setValue(item.getDataInvio());
		estrazioneDate.setFormat(ClientConstants.BOX_FORMAT_DAY);
		estrazioneDate.setEnabled(isOperator);
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
		if (idEf.equals(AppConstants.NEW_ITEM_ID)) {
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
				idEf = result;
				parent.refresh();
				close();
				UiSingleton.get().addInfo(AppConstants.MSG_SAVE_OK);
				WaitSingleton.get().stop();
				verifyFascicoloWithinIstanza(idEf);
			}
		};
		try {
			item.setDataModifica(DateUtil.now());
			item.setIdTipoEvasione(tipoEvasioneList.getSelectedValueString());
			item.setIdFascicoliT(fascicoliList.getSelectedValueString());
			int copie = Integer.parseInt(copieText.getText());
			item.setCopie(copie);
			item.setDataInvio(estrazioneDate.getValue());
			item.setNote(noteText.getValue());
			item.setIdUtente(AuthSingleton.get().getUtente().getId());
		} catch (NumberFormatException e) {
			throw new ValidationException("Valore numerico non corretto");
		}
		WaitSingleton.get().start();
		fascicoliService.saveOrUpdate(item, callback);
	}

	private void loadEvasioneFascicolo() {
		AsyncCallback<EvasioniFascicoli> callback = new AsyncCallback<EvasioniFascicoli>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(EvasioniFascicoli result) {
				item = result;
				if (result != null) {
					if (result.getFascicolo() != null) {
						idPeriodico = result.getFascicolo().getPeriodico().getId();
					}
				}
				drawEvasioneFascicolo();
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		//look for item with id only if id is defined
		if (idEf.intValue() != AppConstants.NEW_ITEM_ID) {
			fascicoliService.findEvasioneFascicoloById(idEf, callback);
		} else {
			//is new abbonamento
			fascicoliService.createEvasioneFascicoloForIstanza(idIstanza, AppConstants.EVASIONE_FAS_ARRETRATO, callback);
		}
	}
	
	private void verifyFascicoloWithinIstanza(Integer idFascicolo) {
		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
			}
			@Override
			public void onSuccess(Boolean result) {
				if (Boolean.FALSE.equals(result)) {
					UiSingleton.get().addWarning("L'arretrato &egrave; stato creato, ma non &egrave; compreso tra il fascicolo iniziale e finale. Controllarne la correttezza.");
				}
			}
		};
		fascicoliService.verifyFascicoloWithinIstanza(idIstanza, idFascicolo, callback);
	}
}
