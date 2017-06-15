package it.giunti.apg.client.widgets;

import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.OpzioniService;
import it.giunti.apg.client.services.OpzioniServiceAsync;
import it.giunti.apg.shared.model.Opzioni;
import it.giunti.apg.shared.model.OpzioniListini;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHTML;

public class OpzioniListiniPanel extends TitlePanel {
	
	private boolean enabled = true;
	private Map<CheckBox, Opzioni> checkboxMap;
	private Integer idPeriodico;
	private Date startDt;
	private Date finishDt;
	private Set<OpzioniListini> selectedSet = new HashSet<OpzioniListini>();
	
	public OpzioniListiniPanel(Integer idPeriodico, Date startDt, Date finishDt,
			Set<OpzioniListini> selectedSet, String title) {
		super(title);
		checkboxMap = new HashMap<CheckBox, Opzioni>();
		this.idPeriodico = idPeriodico;
		this.startDt = startDt;
		this.finishDt = finishDt;
		if (selectedSet != null) this.selectedSet = selectedSet;
		loadOpzioni();
	}
	
	public void changePeriodico(Integer idPeriodico) {
		this.clear();
		this.idPeriodico = idPeriodico;
		loadOpzioni();
	}
	
	private void drawOpzioni(List<Opzioni> availOpzList) {
		boolean isEmpty = true;
		if (availOpzList != null) {
			if (availOpzList.size()>0) {
				FlowPanel itemsPanel = new FlowPanel();
				this.clear();
				this.add(itemsPanel);
				checkboxMap = new HashMap<CheckBox, Opzioni>();
				for (Opzioni opz:availOpzList) {
					//Disegna
					String labelHtml = "["+opz.getUid()+"] "+opz.getNome(); // "&nbsp;" + ClientConstants.FORMAT_CURRENCY.format(opz.getPrezzo());
					CheckBox c = new CheckBox(labelHtml, true);
					c.setEnabled(enabled);
					c.setValue(false);
					for (OpzioniListini ol:selectedSet) {
						if (ol.getOpzione().getId() == opz.getId()) {
							c.setValue(true);
						}
					}
					itemsPanel.add(c);
					checkboxMap.put(c,opz);
					itemsPanel.add(new InlineHTML("&nbsp;&nbsp;&nbsp;"));
				}
				if (checkboxMap.size()>0) {
					isEmpty=false;
				}
			}
		}
		this.setVisible(!isEmpty);
	}
	
	public Set<Integer> getValue() {
		Set<Integer> resultList = new HashSet<Integer>();
		if (checkboxMap != null) {
			for (CheckBox c:checkboxMap.keySet()) {
				if (c.getValue()) {
					resultList.add(checkboxMap.get(c).getId());
				}
			}
		}
		return resultList;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled=enabled;
		if (checkboxMap != null) {
			for (CheckBox c:checkboxMap.keySet()) {
				c.setEnabled(this.enabled);
			}
		}
	}
	
	private void loadOpzioni() {
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
			opzioniService.findOpzioni(idPeriodico, startDt, finishDt, false, callback);
		} catch (Exception e) {
			//Will never be called because Exceptions will be caught by callback
			e.printStackTrace();
		}
	}
}
