package it.giunti.apg.client.frames;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.datepicker.client.DateBox;

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
import it.giunti.apg.client.widgets.select.OpzioniSelect;
import it.giunti.apg.client.widgets.select.PeriodiciSelect;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.model.MaterialiProgrammazione;
import it.giunti.apg.shared.model.Utenti;

public class MaterialiProgrammazionePopUp extends PopupPanel implements IAuthenticatedWidget {

	private final MaterialiServiceAsync matService = GWT.create(MaterialiService.class);
	
	private FlexTable table = new FlexTable();
	private MaterialiProgrammazione item = null;
	private Integer idPeriodico = null;
	private Integer idMatProg = null;
	private IRefreshable parent = null;
	private Utenti utente = null;
	
	private boolean isEditor = false;
	private boolean isAdmin = false;
	
	private PeriodiciSelect periodiciList = null;
	private MaterialiPanel materialiPanel = null;
	private OpzioniSelect opzioniList = null;
	private DateSafeBox dataNominaleText = null;
	private DateBox dataEstrazText = null;
	
	public MaterialiProgrammazionePopUp() {
		super(false);
	}
	
	public void initByPeriodico(Integer idPeriodico, IRefreshable parent) {
		this.idPeriodico=idPeriodico;
		this.idMatProg=AppConstants.NEW_ITEM_ID;
		this.parent=parent;
		AuthSingleton.get().queueForAuthentication(this);
	}
	
	public void initByMaterialeProgrammazione(Integer idMatProg, IRefreshable parent) {
		this.idPeriodico=null;
		this.idMatProg=idMatProg;
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
			loadMaterialiProgrammazione();
		}
	}
	
	private void drawMaterialiProgrammazione() {
		int r=0;
		
		HTML titleHtml = new HTML("Calendario");
		titleHtml.setStyleName("frame-title");
		table.setWidget(r, 0, titleHtml);
		table.getFlexCellFormatter().setColSpan(r, 0, 5);
		r++;
		
		//Periodico
		table.setHTML(r, 0, "Periodico");
		periodiciList = new PeriodiciSelect(item.getPeriodico().getId(),
				item.getDataNominale(), false, false, utente);
		periodiciList.setEnabled(isAdmin);
		periodiciList.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				onPeriodiciChange();
			}
		});
		table.setWidget(r, 1, periodiciList);
		//Numero fascicolo
		table.setHTML(r, 3, "Invio solo con opzione");
		Integer opzId = null;
		Date startDt = item.getDataNominale();
		Date finishDt = new Date(item.getDataNominale().getTime() + AppConstants.YEAR);
		if (item.getOpzione() != null) opzId=item.getOpzione().getId();
		opzioniList = new OpzioniSelect(opzId,
				item.getPeriodico().getId(), startDt, finishDt, true, false);
		opzioniList.setEnabled(isAdmin);
		table.setWidget(r, 4, opzioniList);
		r++;
		
		//Materiale
		table.setHTML(r, 0, "Materiale"+ClientConstants.MANDATORY);
		Integer idMat = null;
		if (item.getMateriale() != null) idMat = item.getMateriale().getId();
		materialiPanel = new MaterialiPanel(idMat, 30, isEditor);
		table.setWidget(r, 1, materialiPanel);
		table.getFlexCellFormatter().setColSpan(r, 1, 4);
		r++;
		
		//Data Nominale
		table.setHTML(r, 0, "Data nominale"+ClientConstants.MANDATORY);
		dataNominaleText = new DateSafeBox();
		dataNominaleText.setFormat(ClientConstants.BOX_FORMAT_DAY);
		dataNominaleText.setValue(item.getDataNominale());
		dataNominaleText.setEnabled(isAdmin);
		table.setWidget(r, 1, dataNominaleText);
		//Data sped effettiva
		table.setHTML(r, 3, "Data estrazione");
		dataEstrazText = new DateBox();
		DateBox.Format BOX_FORMAT_TIMESTAMP = new DateBox.DefaultFormat(ClientConstants.FORMAT_DATETIME);
		dataEstrazText.setFormat(BOX_FORMAT_TIMESTAMP);
		dataEstrazText.setValue(item.getDataEstrazione());
		dataEstrazText.setEnabled(isAdmin);
		table.setWidget(r, 4, dataEstrazText);
		r++;
		
		onPeriodiciChange();
		
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
		if (idMatProg.equals(AppConstants.NEW_ITEM_ID)) {
			submitButton.setHTML(ClientConstants.ICON_SAVE+" Crea");
		}
		submitButton.setEnabled(isEditor);
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
				idMatProg = result;
				parent.refresh();
				close();
				UiSingleton.get().addInfo(AppConstants.MSG_SAVE_OK);
				WaitSingleton.get().stop();
			}
		};
		//Validation
		if (dataNominaleText.getValue() == null) {
				new ValidationException("Manca la data nominale");
		}
		
		if (dataNominaleText.getValue() == null) throw new ValidationException("La data nominale non può essere vuota");
		//Assignment
		item.setDataEstrazione(dataEstrazText.getValue());
		item.setDataNominale(dataNominaleText.getValue());
		item.setIdPeriodicoT(periodiciList.getSelectedValueString());
		item.setIdOpzioneT(opzioniList.getSelectedValueString());
		item.setMaterialeCmT(materialiPanel.getCodiceMeccanografico());
		//item.setIdFascicoloAbbinatoT(fascicoloList.getSelectedValueString());
		WaitSingleton.get().start();
		matService.saveOrUpdateMaterialiProgrammazione(item, callback);
	}

	private void loadMaterialiProgrammazione() {
		AsyncCallback<MaterialiProgrammazione> callback = new AsyncCallback<MaterialiProgrammazione>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(MaterialiProgrammazione result) {
				item = result;
				idPeriodico = item.getPeriodico().getId();
				drawMaterialiProgrammazione();
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		//look for item with id only if id is defined
		if (idMatProg.intValue() != AppConstants.NEW_ITEM_ID) {
			matService.findMaterialiProgrammazioneById(idMatProg, callback);
		} else {
			//is new fascicolo
			matService.createMaterialeProgrammazione(null, idPeriodico, callback);
		}
	}
	
}
