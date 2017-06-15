package it.giunti.apg.client.widgets;

import it.giunti.apg.client.UriManager;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.frames.QuickSuggPanel;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.Indirizzi;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineHTML;

public class AnagraficheSuggestionPanel extends FlowPanel {
	
	public static final String STYLE = "suggestion-panel";
	
	private Anagrafiche anag = null;
	private boolean suggestionToForm = true;
	private QuickSuggPanel parent = null;
	
	public AnagraficheSuggestionPanel(Anagrafiche anag, QuickSuggPanel parent, boolean suggestionToForm) {
		this.anag=anag;
		this.suggestionToForm=suggestionToForm;
		this.parent=parent;
		draw();
	}
	
	public void draw() {
		this.setStyleName(STYLE);
		Indirizzi ind = anag.getIndirizzoPrincipale();
		//Riga nome + presso
		String nome = ind.getCognomeRagioneSociale();
		if (ind.getNome() != null) {
			if (ind.getNome().length() > 0) {
				nome += " " + ind.getNome();
			}
		}
		if (suggestionToForm) {
			//The link loads a new page
			UriParameters params = new UriParameters();
			params.add(AppConstants.PARAM_ID, anag.getId());
			Hyperlink rowLink = params.getHyperlink("<b>"+nome+"</b>&nbsp;&nbsp;", UriManager.ANAGRAFICA);
			this.add(rowLink);
		} else {
			//the link effects the same page
			Anchor nomeLink = new Anchor("<b>"+nome+"</b>&nbsp;&nbsp;",true);
			nomeLink.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent arg0) {
					parent.onSuggestionClick(anag);
				}
			});
			this.add(nomeLink);
		}
		//Box abbonamenti
		if (anag.getLastIstancesT() != null) {
			for (IstanzeAbbonamenti ia:anag.getLastIstancesT()) {
				this.add(new MiniInstanceLabel(ia, true));
			}
		} else {
			this.add(new InlineHTML("<br/>"));
		}
		//Presso
		if (ind.getPresso() != null) {
			if (ind.getPresso().length() > 0) {
				this.add(new InlineHTML(" "+ind.getPresso()+"<br/>"));
			}
		}
		//Riga via
		this.add(new InlineHTML(ind.getIndirizzo()+"<br/>"));
		//Riga localita/cap/prov
		String localita = "";
		if (ind.getLocalita() != null) localita += ind.getLocalita()+" ";
		if (ind.getProvincia() != null) localita += "("+ind.getProvincia()+") ";
		if (ind.getCap() != null) localita += ind.getCap()+" ";
		this.add(new InlineHTML(localita));
		//Riga tel/email
		String telefono = "";
		if (anag.getTelCasa() != null) {
			if (anag.getTelCasa().length() > 0) telefono += anag.getTelCasa()+" ";
		}
		if (anag.getTelMobile() != null) {
			if (anag.getTelMobile().length() > 0) telefono += anag.getTelMobile()+" ";
		}
		if (anag.getEmailPrimaria() != null) {
			if (anag.getEmailPrimaria().length() > 0) telefono += anag.getEmailPrimaria()+" ";
		}
		if (telefono.length() > 0) {
			this.add(new InlineHTML("<br/>tel."+telefono));
		}
		////Indirizzo di fatturazione
		//if (anag.getIndirizzoFatturazione() != null) {
		//	if (anag.getIndirizzoFatturazione().getIndirizzo() != null) {
		//		if (anag.getIndirizzoFatturazione().getIndirizzo().length() > 0) {
		//			Indirizzi fat = anag.getIndirizzoFatturazione();
		//			//Riga fatturazione + presso
		//			this.add(new InlineHTML("<br/>fatturazione: "));
		//			if (fat.getPresso() != null) {
		//				this.add(new InlineHTML(" "+fat.getPresso()));
		//			}
		//			this.add(new InlineHTML("<br/>"));
		//			//Riga via
		//			this.add(new InlineHTML(fat.getIndirizzo()+"<br/>"));
		//			//Riga localita/cap/prov
		//			String locFatt = "";
		//			if (fat.getLocalita() != null) locFatt += fat.getLocalita()+" ";
		//			if (fat.getProvincia() != null) locFatt += "("+fat.getProvincia()+") ";
		//			if (fat.getCap() != null) locFatt += fat.getCap()+" ";
		//			this.add(new InlineHTML(locFatt));
		//		}
		//	}
		//}
	}
}
