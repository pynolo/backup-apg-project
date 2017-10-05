package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.IRefreshable;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.FascicoliService;
import it.giunti.apg.client.services.FascicoliServiceAsync;
import it.giunti.apg.client.widgets.select.OpzioniSelect;
import it.giunti.apg.client.widgets.select.PeriodiciSelect;
import it.giunti.apg.client.widgets.select.TipiAnagraficaSapSelect;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.model.Fascicoli;
import it.giunti.apg.shared.model.Utenti;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
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

public class FascicoloPopUp extends PopupPanel implements IAuthenticatedWidget {

	private final FascicoliServiceAsync fascicoliService = GWT.create(FascicoliService.class);
	
	private FlexTable table = new FlexTable();
	private Fascicoli item = null;
	private Integer idPeriodico = null;
	private Integer idFas = null;
	private IRefreshable parent = null;
	private Utenti utente = null;
	
	private boolean isEditor = false;
	private boolean isAdmin = false;
	
	private PeriodiciSelect periodiciList = null;
	private OpzioniSelect opzioniList = null;
	private TextBox numFascText = null;
	private TextBox accorpatiText = null;
	private TextBox meccText = null;
	private TextBox dataCopText = null;
	private DateBox dataNominaleText = null;
	private DateBox dataPubblicazioneText = null;
	private TextBox noteText = null;
	private CheckBox attesaCheck = null;
	private DateBox dataEstrazText = null;
	//private SocietaSelect societaSel = null;
	private TipiAnagraficaSapSelect tipoAnagraficaSap = null;
	
	
	public FascicoloPopUp() {
		super(false);
	}
	
	public void initByPeriodico(Integer idPeriodico, IRefreshable parent) {
		this.idPeriodico=idPeriodico;
		this.idFas=AppConstants.NEW_ITEM_ID;
		this.parent=parent;
		AuthSingleton.get().queueForAuthentication(this);
	}
	
	public void initByFascicolo(Integer idFascicolo, IRefreshable parent) {
		this.idPeriodico=null;
		this.idFas=idFascicolo;
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
		isEditor = (ruolo >= AppConstants.RUOLO_EDITOR);
		isAdmin = (ruolo >= AppConstants.RUOLO_ADMIN);
		//UI
		if (isEditor) {
			this.setModal(true);
			this.setGlassEnabled(true);
			this.add(table);
			loadFascicolo();
		}
	}
	
	private void drawEvasioneFascicolo() {
		int r=0;
		
		HTML titleHtml = new HTML("Fascicolo");
		titleHtml.setStyleName("frame-title");
		table.setWidget(r, 0, titleHtml);
		table.getFlexCellFormatter().setColSpan(r, 0, 5);
		r++;
		
		//Periodico
		table.setHTML(r, 0, "Periodico");
		periodiciList = new PeriodiciSelect(item.getPeriodico().getId(),
				item.getDataInizio(), false, false, utente);
		periodiciList.setEnabled(isAdmin);
		periodiciList.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				onPeriodiciChange();
			}
		});
		table.setWidget(r, 1, periodiciList);
		//Tipo Articolo
		table.setHTML(r, 3, "Anagrafica SAP");
		tipoAnagraficaSap = new TipiAnagraficaSapSelect(item.getIdTipoAnagraficaSap());
		tipoAnagraficaSap.setEnabled(isEditor);
		table.setWidget(r, 4, tipoAnagraficaSap);
		r++;
		
		//Numero fascicolo
		table.setHTML(r, 0, "Invio solo con opzione");
		Integer opzId = null;
		Date startDt = item.getDataInizio();
		Date finishDt = new Date(item.getDataInizio().getTime() + AppConstants.YEAR);
		if (item.getOpzione() != null) opzId=item.getOpzione().getId();
		opzioniList = new OpzioniSelect(opzId,
				item.getPeriodico().getId(), startDt, finishDt, true, false);
		opzioniList.setEnabled(isAdmin);
		table.setWidget(r, 1, opzioniList);
		r++;

		//Fascicoli
		table.setHTML(r, 0, "Numero o descrizione");
		numFascText = new TextBox();
		numFascText.setValue(item.getTitoloNumero());
		numFascText.setMaxLength(64);
		numFascText.setEnabled(isAdmin);
		table.setWidget(r, 1, numFascText);
		//Fascicoli accorpati
		table.setHTML(r, 3, "Fascicoli accorpati"+ClientConstants.MANDATORY);
		accorpatiText = new TextBox();
		accorpatiText.setValue(item.getFascicoliAccorpati()+"");
		accorpatiText.setMaxLength(2);
		accorpatiText.setWidth("2em");
		accorpatiText.setEnabled(isAdmin);
		table.setWidget(r, 4, accorpatiText);
		r++;
		
		//Codice meccanografico
		table.setHTML(r, 0, "Codice meccanografico"+ClientConstants.MANDATORY);
		meccText = new TextBox();
		meccText.setValue(item.getCodiceMeccanografico());
		meccText.setMaxLength(32);
		meccText.setEnabled(isAdmin);
		table.setWidget(r, 1, meccText);
		//Etichetta separata
		table.setHTML(r, 3, "Arretrati in attesa");
		attesaCheck = new CheckBox();
		attesaCheck.setValue(item.getInAttesa());
		attesaCheck.setEnabled(isEditor);
		table.setWidget(r, 4, attesaCheck);
		r++;
		
		//Data copertina
		table.setHTML(r, 0, "Data copertina (descr.)");
		dataCopText = new TextBox();
		dataCopText.setValue(item.getDataCop());
		dataCopText.setMaxLength(64);
		dataCopText.setEnabled(isAdmin);
		table.setWidget(r, 1, dataCopText);
		//Data Nominale
		table.setHTML(r, 3, "Data nominale"+ClientConstants.MANDATORY);
		dataNominaleText = new DateBox();
		dataNominaleText.setFormat(ClientConstants.BOX_FORMAT_DAY);
		dataNominaleText.setValue(item.getDataInizio());
		dataNominaleText.setEnabled(isAdmin);
		table.setWidget(r, 4, dataNominaleText);
		r++;
		
		//Data sped effettiva
		table.setHTML(r, 0, "Data estrazione");
		dataEstrazText = new DateBox();
		DateBox.Format BOX_FORMAT_TIMESTAMP = new DateBox.DefaultFormat(ClientConstants.FORMAT_DATETIME);
		dataEstrazText.setFormat(BOX_FORMAT_TIMESTAMP);
		dataEstrazText.setValue(item.getDataEstrazione());
		dataEstrazText.setEnabled(isAdmin);
		table.setWidget(r, 1, dataEstrazText);
		//Data sped prevista
		table.setHTML(r, 3, "Data pubblicazione");
		dataPubblicazioneText = new DateBox();
		dataPubblicazioneText.setFormat(ClientConstants.BOX_FORMAT_DAY);
		dataPubblicazioneText.setValue(item.getDataPubblicazione());
		dataPubblicazioneText.setEnabled(isEditor);
		table.setWidget(r, 4, dataPubblicazioneText);
		r++;
		
		//Note
		table.setHTML(r, 0, "Note");
		noteText = new TextBox();
		noteText.setValue(item.getNote());
		noteText.setMaxLength(250);
		noteText.setEnabled(isEditor);
		noteText.setWidth("95%");
		table.setWidget(r, 1, noteText);
		table.getFlexCellFormatter().setColSpan(r, 1, 4);
		r++;
		
		onPeriodiciChange();
		
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
		if (idFas.equals(AppConstants.NEW_ITEM_ID)) {
			submitButton.setText("Crea");
		}
		submitButton.setEnabled(isEditor);
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
		
		this.center();
		this.show();
	}
	
	private void close() {
		this.hide();
	}
	
	private void onPeriodiciChange() {
		try {
			if (periodiciList != null) {
				if (periodiciList.getItemCount() > 0) {
					idPeriodico = periodiciList.getSelectedValueInt();
				}
			}
		} catch (Exception e) {
			return;
		}
		Date now = DateUtil.now();
		long startDt = now.getTime() - AppConstants.MONTH * 9;
		long finishDt = now.getTime() + AppConstants.MONTH * 36;
		//Opzioni
		Integer supId = 0;
		if (item.getOpzione() != null) supId = item.getOpzione().getId();
		opzioniList.reload(supId, idPeriodico, new Date(startDt), new Date(finishDt));
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
				idFas = result;
				parent.refresh();
				close();
				UiSingleton.get().addInfo(AppConstants.MSG_SAVE_OK);
				WaitSingleton.get().stop();
			}
		};
		//Validation
		if ( (accorpatiText.getValue() == null) ||
				(dataCopText.getValue() == null) ||
				(dataNominaleText.getValue() == null)||
				(numFascText.getValue() == null) ||
				(meccText.getValue() == null)
				) {
				new ValidationException("Manca un dato obbligatorio");
		}
		if ( (accorpatiText.getValue().length() == 0) ||
				(dataCopText.getValue().length() == 0) ||
				(numFascText.getValue().length() == 0) ||
				(meccText.getValue().length() == 0)
				) {
				new ValidationException("Manca un dato obbligatorio");
		}
		int fasAccorpati = 0;
		try {
			fasAccorpati = Integer.parseInt(accorpatiText.getValue());
		} catch (NumberFormatException e) {
			throw new ValidationException("Valore numerico non corretto");
		}
		if ((fasAccorpati > 0) && (opzioniList.getSelectedValueInt() > 0)) {
			throw new ValidationException("Per le opzioni il valore fascicoli accorpati deve essere 0");
		}
		if (meccText.getValue() == null) throw new ValidationException("Il codice meccanografico non può essere vuoto");
		if (meccText.getValue().length() == 0) throw new ValidationException("Il codice meccanografico non può essere vuoto");
		if (dataNominaleText.getValue() == null) throw new ValidationException("La data nominale non può essere vuota");
		if (dataPubblicazioneText.getValue() == null) dataPubblicazioneText.setValue(dataNominaleText.getValue());
		//if (dataSpedPrevistaText.getValue() == null) throw new ValidationException("La data nominale non può essere vuota");
		//Assignment
		item.setDataCop(dataCopText.getValue());
		item.setDataEstrazione(dataEstrazText.getValue());
		item.setDataInizio(dataNominaleText.getValue());
		item.setDataPubblicazione(dataPubblicazioneText.getValue());
		item.setIdTipoAnagraficaSap(tipoAnagraficaSap.getSelectedValueString());
		item.setInAttesa(attesaCheck.getValue());
		item.setFascicoliAccorpati(fasAccorpati);
		String cm = "";
		if (meccText.getValue() != null) cm = meccText.getValue().toUpperCase();
		item.setCodiceMeccanografico(cm);
		item.setNote(noteText.getValue());
		item.setTitoloNumero(numFascText.getValue());
		item.setIdPeriodicoT(periodiciList.getSelectedValueString());
		item.setIdOpzioneT(opzioniList.getSelectedValueString());
		//item.setIdFascicoloAbbinatoT(fascicoloList.getSelectedValueString());
		WaitSingleton.get().start();
		fascicoliService.saveOrUpdate(item, callback);
	}

	private void loadFascicolo() {
		AsyncCallback<Fascicoli> callback = new AsyncCallback<Fascicoli>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(Fascicoli result) {
				item = result;
				idPeriodico = item.getPeriodico().getId();
				drawEvasioneFascicolo();
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		//look for item with id only if id is defined
		if (idFas.intValue() != AppConstants.NEW_ITEM_ID) {
			fascicoliService.findFascicoloById(idFas, callback);
		} else {
			//is new fascicolo
			fascicoliService.createFascicolo(idPeriodico, false, callback);
		}
	}
	
}
