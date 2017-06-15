package it.giunti.apg.client.widgets;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.OpzioniService;
import it.giunti.apg.client.services.OpzioniServiceAsync;
import it.giunti.apg.shared.model.Opzioni;
import it.giunti.apg.shared.model.OpzioniIstanzeAbbonamenti;
import it.giunti.apg.shared.model.OpzioniListini;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHTML;

public class OpzioniIstanzaPanel extends TitlePanel {
	
	private boolean enabled = true;
	//private boolean isTransientInstance = false;
	private Integer idPeriodico = null;
	private Integer idFascicolo = null;
	private List<CheckBox> checkboxList;
	private List<OpzioniIstanzeAbbonamenti> oiaList = new ArrayList<OpzioniIstanzeAbbonamenti>();
	private List<OpzioniListini> olList = new ArrayList<OpzioniListini>();
	private List<Opzioni> opzioniList;
	
	public OpzioniIstanzaPanel(Integer idPeriodico, Integer idFascicolo,
			Set<OpzioniIstanzeAbbonamenti> selectedSet, Set<OpzioniListini> mandatorySet, String title) {
		super(title);
		this.idPeriodico = idPeriodico;
		this.idFascicolo = idFascicolo;
		if (selectedSet != null) oiaList.addAll(selectedSet);
		if (mandatorySet != null) olList.addAll(mandatorySet);
		//this.isTransientInstance = isTransientInstance;
		loadOpzioniByPeriodico(idPeriodico, idFascicolo);
	}
	
	public void refresh() {
		loadOpzioniByPeriodico(idPeriodico, idFascicolo);
	}
	
	public void onListinoChange(Integer idPeriodico, Integer idFascicolo,
			Set<OpzioniListini> mandatorySet) {
		this.clear();
		if (this.idPeriodico.intValue() != idPeriodico.intValue()) oiaList.clear();
		olList.clear();
		if (mandatorySet != null) {			
			olList.addAll(mandatorySet);
			for (OpzioniListini ol:mandatorySet) {
				boolean found = false;
				for (OpzioniIstanzeAbbonamenti oia:oiaList) {
					if (oia.getOpzione().getId() == ol.getOpzione().getId()) found = true;
				}
				if (!found) {
					OpzioniIstanzeAbbonamenti oia = new OpzioniIstanzeAbbonamenti();
					oia.setOpzione(ol.getOpzione());
					oiaList.add(oia);
				}
			}
		}
		loadOpzioniByPeriodico(idPeriodico, idFascicolo);
	}
	
	private void drawOpzioni(List<Opzioni> availOpzList) {
		boolean isEmpty = true;
		if (availOpzList != null) {
			if (availOpzList.size()>0) {
				FlowPanel itemsPanel = new FlowPanel();
				this.clear();
				this.add(itemsPanel);
				checkboxList = new ArrayList<CheckBox>();
				opzioniList = new ArrayList<Opzioni>();
				for (Opzioni availOpz:availOpzList) {
					//Disegna
					String labelHtml = "["+availOpz.getUid()+"] "+availOpz.getNome() + 
							"&nbsp;" + //"<span class=\"label-small-caps\">"+valuta+"</span>" + 
							ClientConstants.FORMAT_CURRENCY.format(availOpz.getPrezzo());
					CheckBox c = new CheckBox(labelHtml, true);
					c.setEnabled(enabled);
					c.setValue(false);
					for (OpzioniIstanzeAbbonamenti oia:oiaList) {
						//Opzione selezionata?
						if (oia.getOpzione().getId() == availOpz.getId()) {
							c.setValue(true);
							//La fattura può influenzare la deselezionabilità?
							//c.setEnabled(oia.getIdFattura() == null);
						}
					}
					//Opzione obbligatoria del listino?
					for (OpzioniListini ol:olList) {
						if(ol.getOpzione().getId() == availOpz.getId()) {
							c.setValue(true);
							//if (isTransientInstance) c.setValue(true);
							c.setEnabled(false);
							c.setHTML(c.getHTML()+ClientConstants.MANDATORY);
						}
					}
					checkboxList.add(c);
					opzioniList.add(availOpz);
					itemsPanel.add(c);
					itemsPanel.add(new InlineHTML("&nbsp;&nbsp;&nbsp;"));
				}
				if (opzioniList.size()>0) {
					isEmpty=false;
				}
			}
		}
		this.setVisible(!isEmpty);
	}
	
	public Set<Integer> getValue() {
		Set<Integer> resultList = new HashSet<Integer>();
		if (checkboxList != null) {
			for (int i = 0; i < checkboxList.size(); i++) {
				if (checkboxList.get(i).getValue()) {
					resultList.add(opzioniList.get(i).getId());
				}
			}
		}
		return resultList;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled=enabled;
		if (checkboxList != null) {
			for (CheckBox c:checkboxList) {
				c.setEnabled(this.enabled);
			}
		}
	}
	
	private void loadOpzioniByPeriodico(Integer idPeriodico, Integer idFascicolo) {
		OpzioniServiceAsync opzioniService = GWT.create(OpzioniService.class);
		AsyncCallback<List<Opzioni>> callback = new AsyncCallback<List<Opzioni>>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(List<Opzioni> result) {
				drawOpzioni(result);
				WaitSingleton.get().stop();
			}
		};
		try {
			WaitSingleton.get().start();
			opzioniService.findOpzioni(idPeriodico, idFascicolo, callback);
		} catch (Exception e) {
			//Will never be called because Exceptions will be caught by callback
			e.printStackTrace();
		}
	}
}
