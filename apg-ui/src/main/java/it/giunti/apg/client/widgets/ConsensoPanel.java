package it.giunti.apg.client.widgets;

import it.giunti.apg.client.ClientConstants;

import java.util.Date;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.InlineHTML;

public class ConsensoPanel extends TitlePanel {
	
	boolean marketing = false;
	boolean profilazione = false;
	Date dataConsenso = null;
	boolean enabled = false;
	
	private CheckBox marketingChk = null;
	private CheckBox profilingChk = null;
	
	public ConsensoPanel(boolean marketing, boolean profilazione, Date dataConsenso, boolean enabled) {
		super("Consenso");
		this.marketing = marketing;
		this.profilazione = profilazione;
		this.dataConsenso = dataConsenso;
		this.enabled = enabled;
		draw();
	}
	
	private void draw() {
		this.clear();
		this.add(new InlineHTML("Privacy e termini d'uso&nbsp;"));
		CheckBox obbligatorioChk = new CheckBox();
		obbligatorioChk.setValue(true);
		obbligatorioChk.setEnabled(false);
		this.add(obbligatorioChk);
		this.add(new InlineHTML("&nbsp;&nbsp;&nbsp;&nbsp;A fini di marketing&nbsp;"));
		marketingChk = new CheckBox();
		marketingChk.setValue(marketing);
		marketingChk.setEnabled(enabled);
		this.add(marketingChk);
		this.add(new InlineHTML("&nbsp;&nbsp;&nbsp;&nbsp;A fini di profilazione&nbsp;"));
		profilingChk = new CheckBox();
		profilingChk.setValue(profilazione);
		profilingChk.setEnabled(enabled);
		this.add(profilingChk);
		this.add(new InlineHTML("&nbsp;&nbsp;&nbsp;&nbsp;<i>(consenso del "+
				ClientConstants.FORMAT_DAY.format(dataConsenso)+")</i>&nbsp;&nbsp;&nbsp;"));
		if (!enabled) {
			Anchor editImg = new Anchor("&nbsp;"+ClientConstants.ICON_EDIT+"&nbsp;&nbsp;&nbsp;", true);
			editImg.setTitle("Modifica");
			editImg.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent arg0) {
					switchStatus();
				}
			});
			this.add(editImg);
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
