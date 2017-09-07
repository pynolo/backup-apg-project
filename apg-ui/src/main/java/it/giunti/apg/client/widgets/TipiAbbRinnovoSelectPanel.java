package it.giunti.apg.client.widgets;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.TipiAbbService;
import it.giunti.apg.client.services.TipiAbbServiceAsync;
import it.giunti.apg.client.widgets.select.TipiAbbSelect;
import it.giunti.apg.core.DateUtil;
import it.giunti.apg.shared.model.Listini;
import it.giunti.apg.shared.model.TipiAbbonamentoRinnovo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TipiAbbRinnovoSelectPanel extends VerticalPanel {
	
	private boolean enabled = true;
	private Listini listino;
	private Integer idPeriodico = null;
	private Date beginDt = null;
	private List<TipiAbbonamentoRinnovo> tarList = new ArrayList<TipiAbbonamentoRinnovo>();
	private List<TipiAbbSelect> selectList = new ArrayList<TipiAbbSelect>();
	private HTML addIcon = null;
	public TipiAbbRinnovoSelectPanel(Listini listino) {
		this.listino = listino;
		this.idPeriodico = listino.getTipoAbbonamento().getPeriodico().getId();
		this.beginDt = DateUtil.now();
		WaitSingleton.get().start();
		loadTipiRinnovo();
	}
	
	private void draw() {
		this.clear();
		for (TipiAbbonamentoRinnovo tar:tarList) {
			TipiAbbSelect select = new TipiAbbSelect(tar.getTipoAbbonamento().getId(),
					idPeriodico, beginDt, true, false);
			select.setEnabled(enabled);
			selectList.add(select);
			HorizontalPanel hp = new HorizontalPanel();
			String ordine = "";
			if (tar.getOrdine() != null) ordine = new Integer(tar.getOrdine()+1).toString()+".";
			hp.add(new InlineHTML(ordine));
			hp.add(select);
			this.add(hp);
		}
		addIcon = new HTML(ClientConstants.ICON_PLUS);
		addIcon.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addEmpty();
			}
		});
		this.add(addIcon);
		//pack();
	}
	
	public List<Integer> getIdValues() {
		List<Integer> result = new ArrayList<Integer>();
		for (TipiAbbSelect select:selectList) {
			Integer value = select.getSelectedValueInt();
			if (value != null) {
				result.add(value);
			}
		}
		return result;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		for (TipiAbbSelect select:selectList) {
			select.setEnabled(enabled);
		}
	}
	
	///**
	// * Verifica l'elenco dei select box e fa in modo che ce ne sia
	// * uno solo vuoto, in fondo alla lista.
	// */
	//protected void pack() {
	//	List<TipiAbbSelect> checkList = new ArrayList<TipiAbbSelect>();
	//	checkList.addAll(selectList);
	//	for (TipiAbbSelect select:checkList) {
	//		if (select.getSelectedValueString() == null) {
	//			this.remove(select);
	//			selectList.remove(select);
	//		} else if (select.getSelectedValueString().equals("")) {
	//			this.remove(select);
	//			selectList.remove(select);
	//		}
	//	}
	//	addEmpty();
	//}
	
	protected void addEmpty() {
		this.remove(addIcon);
		//Opzione vuota in coda
		TipiAbbSelect select = new TipiAbbSelect(-1, idPeriodico, beginDt, true, false);
		select.setEnabled(enabled);
		this.add(select);
		selectList.add(select);
		this.add(addIcon);
	}
	
	private void loadTipiRinnovo() {
		AsyncCallback<List<TipiAbbonamentoRinnovo>> callback =
				new AsyncCallback<List<TipiAbbonamentoRinnovo>>() {
			@Override
			public void onFailure(Throwable caught) {
				//UiSingleton.get().addInfo("Non e' possibile caricare i tipi abbonamento al rinnovo");
				WaitSingleton.get().stop();
				tarList = new ArrayList<TipiAbbonamentoRinnovo>();
				draw();
			}
			@Override
			public void onSuccess(List<TipiAbbonamentoRinnovo> result) {
				tarList = result;
				WaitSingleton.get().stop();
				draw();
			}
		};
		TipiAbbServiceAsync tipiAbbService = GWT.create(TipiAbbService.class);
		tipiAbbService.findTipiAbbonamentoRinnovoByListino(listino.getId(), callback);
	}
	
}


