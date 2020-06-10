package it.giunti.apg.client.widgets.select;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHTML;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.TipiAbbService;
import it.giunti.apg.client.services.TipiAbbServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Listini;

public class ListiniSelect extends FlowPanel {

	private TipiAbbServiceAsync tipiAbbService = GWT.create(TipiAbbService.class);
	
	private Integer selectedId = null;
	private Integer idPeriodico = null;
	private Date extractionDt = null;
	private Boolean includeEmptyItem = null;
	private Boolean createChangeEvent = null;
	private ChangeHandler changeHandler = null;
	private boolean editingStatus = false;
	private boolean isEnabled = true;
	private InlineHTML lstLabel = null;
	private InlineHTML editImg = null;
	private ListinoSelect lstSelect = null;

	public ListiniSelect(Integer selectedId, Integer idPeriodico, Date extractionDt,
			boolean includeEmptyItem, boolean createChangeEvent, boolean editingStatus, boolean isEnabled) {
		init(selectedId, includeEmptyItem, editingStatus, isEnabled);
		reload(selectedId, idPeriodico, extractionDt, createChangeEvent);
	}
	
	private void init(Integer selectedId, boolean includeEmptyItem, boolean editingStatus, boolean isEnabled) {
		this.selectedId = selectedId;
		this.includeEmptyItem = includeEmptyItem;
		this.editingStatus = editingStatus;
		this.isEnabled = isEnabled;
		lstLabel = new InlineHTML();
		editImg = new InlineHTML(ClientConstants.ICON_EDIT);
		editImg.setTitle("Modifica");
		editImg.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				switchStatus();
			}
		});
	}
	
	public void reload(Integer selectedId, Integer idPeriodico, Date extractionDt,
			boolean createChangeEvent) {
		this.selectedId = selectedId;
		this.idPeriodico = idPeriodico;
		this.extractionDt = extractionDt;
		this.createChangeEvent = createChangeEvent;
		this.clear();
		if (editingStatus) {
			if (lstSelect == null) {
				lstSelect = new ListinoSelect(selectedId);
				if (changeHandler != null) lstSelect.addChangeHandler(changeHandler);
			} else {
				lstSelect.reload(selectedId);
			}
			this.add(lstSelect);
		} else {
			this.add(lstLabel);
			if (isEnabled) this.add(editImg);
			loadSelectedListino();
		}
	}
	
	private void switchStatus() {
		if (isEnabled) {
			editingStatus = !editingStatus;
			if (extractionDt != null) {
				reload(selectedId, idPeriodico, extractionDt, createChangeEvent);
			}
		}
		addChangeHandler(changeHandler);
	}
	
	private void drawListinoLabel(Listini lst) {
		String lstName = "";
		if (lst.getTipoAbbonamento().getPeriodico().getId().equals(idPeriodico)) {
			lstName += "<b>"+lst.getTipoAbbonamento().getCodice() +" "+
					lst.getTipoAbbonamento().getNome()+"</b>";
			if (lst.getPrezzo() > AppConstants.SOGLIA) lstName += " &euro;"+ClientConstants.FORMAT_CURRENCY.format(lst.getPrezzo());
			if (lst.getOpzioniListiniSet() != null) {
				if (lst.getOpzioniListiniSet().size() > 0) {
					lstName += " (include "+lst.getOpzioniListiniSet().size() + " opz.)";
				}
			}
			//if (lst.getPrezzoOpzObbligatori() != null) {
			//	if (lst.getPrezzoOpzObbligatori() > AppConstants.SOGLIA) {
			//		lstName += " sup." + ClientConstants.FORMAT_CURRENCY.format(lst.getPrezzoOpzObbligatori());
			//	}
			//}
		} else {
			lstName += "<b>Errore</b>";
		}
		lstName += "&nbsp;";
		lstLabel.setHTML(lstName);
	}
	
	public void addChangeHandler(ChangeHandler handler) {
		this.changeHandler=handler;
		if (lstSelect != null) lstSelect.addChangeHandler(changeHandler);
	}
	
	public Integer getSelectedValueInt() {
		Integer val = null;
		if (lstSelect != null) {
			val = lstSelect.getSelectedValueInt();
		} else {
			return selectedId;
		}
		return val;
	}
	
	public String getSelectedValueString() {
		String val = null;
		if (lstSelect != null) {
			val = lstSelect.getSelectedValueString();
		} else {
			return selectedId+"";
		}
		return val;
	}
	
	
	
	// Async Methods
	
	
	
	//Carica il listino abbinato (o altrimenti il default) e lo disegna
	public void loadSelectedListino() {
		AsyncCallback<Listini> callback = new AsyncCallback<Listini>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addInfo("Non e' possibile caricare il tipo abbonamento listino corretto");
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(Listini result) {
				selectedId = result.getId();
				drawListinoLabel(result);
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		if (selectedId !=  null) {
			if (selectedId.intValue() >= 0) {
				tipiAbbService.findListinoById(selectedId, callback);
				return;
			}
		}
		if (extractionDt != null) {
			tipiAbbService.findDefaultListinoByPeriodicoDate(idPeriodico, extractionDt, callback);
		}
	}

	
	
	// Inner Classes
	
	
	
	public class ListinoSelect extends EntitySelect<Listini> implements ChangeHandler {
	
		public ListinoSelect(Integer selectedId) {
			super(selectedId);
			reload(selectedId);
			this.addChangeHandler(this);
		}
	
		public void reload(Integer selectedId) {
			this.setSelectedId(selectedId);
			loadEntityList();
		}

		@Override
		public void onChange(ChangeEvent event) {
			if(createChangeEvent) {
				if (changeHandler != null) {
					changeHandler.onChange(null);
				}
			}
		}
		
		@Override
		protected void drawListBox(List<Listini> entityList) {
			this.clear();
			//this.setVisibleItemCount(1);
			if (includeEmptyItem) this.addItem(AppConstants.SELECT_EMPTY_LABEL);
			if (entityList != null) {
				for (int i=0; i<entityList.size(); i++) {
					Listini lst = entityList.get(i);
					String lstName = lst.getTipoAbbonamento().getCodice() +" "+
							lst.getTipoAbbonamento().getNome();
					if (lst.getPrezzo() > AppConstants.SOGLIA) lstName += " "+ClientConstants.FORMAT_CURRENCY.format(lst.getPrezzo());
					if (lst.getOpzioniListiniSet() != null) {
						if (lst.getOpzioniListiniSet().size() > 0) {
							lstName += "(con "+lst.getOpzioniListiniSet().size() + " opz.)";
						}
					}
					//if (lst.getPrezzoOpzObbligatori() != null) {
					//	if (lst.getPrezzoOpzObbligatori() > AppConstants.SOGLIA) {
					//		lstName += " sup." + ClientConstants.FORMAT_CURRENCY.format(lst.getPrezzoOpzObbligatori());
					//	}
					//}
					Integer lstId = lst.getId();
					this.addItem(lstName, lstId.toString());
				}
			}
			showSelectedValue();
		}
		
		@Override
		protected void loadEntityList() {
			AsyncCallback<List<Listini>> callback = new AsyncCallback<List<Listini>>() {
				@Override
				public void onFailure(Throwable caught) {
					//UiSingleton.get().addInfo("Non ci sono tipi abbonamento disponibili per questa rivista");
					drawListBox(null);
					WaitSingleton.get().stop();
				}
				@Override
				public void onSuccess(List<Listini> result) {
					//tipiAbbListinoCache = result;
					drawListBox(result);
					WaitSingleton.get().stop();
				}
			};
			WaitSingleton.get().start();
			if (extractionDt != null) {
				tipiAbbService.findListiniByPeriodicoDate(idPeriodico, extractionDt, selectedId,
						0, Integer.MAX_VALUE, callback);
			}
		}
	
	}
	
}
