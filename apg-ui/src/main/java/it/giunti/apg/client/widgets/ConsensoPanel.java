package it.giunti.apg.client.widgets;

import it.giunti.apg.client.ClientConstants;

import java.util.Date;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;

public class ConsensoPanel extends HorizontalPanel {
	
	boolean marketing = false;
	boolean profilazione = false;
	Date dataConsenso = null;
	boolean enabled = false;
	
	private CheckBox marketingChk = null;
	private CheckBox profilingChk = null;
	private InlineHTML editImg = null;
	
	public ConsensoPanel(boolean marketing, boolean profilazione, Date dataConsenso, boolean enabled) {
		this.marketing = marketing;
		this.profilazione = profilazione;
		this.dataConsenso = dataConsenso;
		this.enabled = enabled;
		draw();
	}
	
	private void draw() {
		this.clear();
		this.add(new InlineHTML("Consensi: Obbligatorio"));
		CheckBox obbligatorioChk = new CheckBox();
		obbligatorioChk.setValue(true);
		obbligatorioChk.setEnabled(false);
		this.add(obbligatorioChk);
		this.add(new InlineHTML("&nbsp;Marketing"));
		marketingChk = new CheckBox();
		marketingChk.setValue(marketing);
		marketingChk.setEnabled(enabled);
		this.add(marketingChk);
		this.add(new InlineHTML("&nbsp;Profilazione"));
		profilingChk = new CheckBox();
		profilingChk.setValue(profilazione);
		profilingChk.setEnabled(enabled);
		this.add(profilingChk);
		this.add(new InlineHTML("&nbsp;<i>Aggiornato il "+
				ClientConstants.FORMAT_DAY.format(dataConsenso)+"</i>"));
		if (!enabled) {
			editImg = new InlineHTML(ClientConstants.ICON_EDIT);
			editImg.setTitle("Modifica");
			editImg.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent arg0) {
					switchStatus();
				}
			});
		}
	}
	
	private void switchStatus() {
		enabled = !enabled;
		draw();
	}

	public boolean getMarketing() {
		if (enabled) {
			return marketingChk.getValue();
		} else {
			return marketing;
		} 
	}

	public boolean getProfilazione() {
		if (enabled) {
			return profilingChk.getValue();
		} else {
			return profilazione;
		} 
	}

	public boolean getEnabled() {
		return enabled;
	}
	
}
