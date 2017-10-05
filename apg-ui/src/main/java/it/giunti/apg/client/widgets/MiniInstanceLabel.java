package it.giunti.apg.client.widgets;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.UriManager;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;

import java.util.Date;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;

public class MiniInstanceLabel extends FlexTable {

	public MiniInstanceLabel(IstanzeAbbonamenti ia, boolean clickable) {
		final IstanzeAbbonamenti fIa = ia;
		int column = 0;
		Date today = DateUtil.now();
		
		String abbCode = ia.getAbbonamento().getCodiceAbbonamento();
		this.setHTML(0, column, "<b>" + ia.getListino().getTipoAbbonamento().getPeriodico().getUid() + "</b>");
		column++;
		String codiceTipoAbb = ia.getListino().getTipoAbbonamento().getCodice();
		String tooltip = abbCode+" ["+codiceTipoAbb+"] ";
		//ICONA IN CORSO
		String timeIcon = null;
		if (ia.getFascicoloFine().getDataFine().before(today)) {
			//Scaduto
			timeIcon = ClientConstants.ICON_MINI_SCADUTO;
			tooltip += "scaduto il " +
					ClientConstants.FORMAT_DAY.format(ia.getFascicoloFine().getDataFine())+" ";
		}
		if (ia.getFascicoloInizio().getDataInizio().after(today)) {
			//Nel futuro
			timeIcon = ClientConstants.ICON_MINI_FUTURO;
			tooltip += "inizia il " +
					ClientConstants.FORMAT_DAY.format(ia.getFascicoloInizio().getDataInizio())+" ";
		}
		if (timeIcon == null) {
			timeIcon = ClientConstants.ICON_MINI_IN_CORSO;
			tooltip += "in corso ";
		}
		this.setHTML(0, column, timeIcon);
		column++;
		//ICONA PAGAMENTO
		String pagIcon = null;
		if (ia.getListino().getPrezzo().doubleValue() < AppConstants.SOGLIA) {
			pagIcon = ClientConstants.ICON_MINI_OMAGGIO;
			tooltip += "omaggio ";
		} else {
			if (ia.getInFatturazione() || ia.getListino().getFatturaDifferita()) {
				pagIcon = ClientConstants.ICON_MINI_FATTURAZIONE;
				tooltip += "fatt. pag. differito ";
			} else {
				if (ia.getPagato()) {
					//Pagato
					pagIcon = ClientConstants.ICON_MINI_PAGATO;
					tooltip += "pagato ";
				}
			}
			if (pagIcon == null) {
				//Da pagare
				pagIcon = ClientConstants.ICON_MINI_DA_PAGARE;
				tooltip += "da pagare ";
			}
		}
		this.setHTML(0, column, pagIcon);
		column++;
		//ICONA BLOCCO/DISDETTA
		String blockIcon = null;
		if (ia.getInvioBloccato()) {
			//Bloccato
			blockIcon = ClientConstants.ICON_MINI_BLOCCATO;
			tooltip += "BLOCCATO ";
		} else {
			if (ia.getDataDisdetta() != null) {
				//Disdettato
				blockIcon = ClientConstants.ICON_MINI_DISDETTA;
				tooltip += "DISDETTA il "+ClientConstants.FORMAT_DAY.format(ia.getDataDisdetta()) + " ";
			}
		}
		if (blockIcon != null) {
			this.setHTML(0, column, blockIcon);
			column++;
		}
		this.setTitle(tooltip);
		this.setStyleName("mini-instance-panel");
		if (clickable) {
			this.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					UriParameters params = new UriParameters();
					params.add(AppConstants.PARAM_ID, fIa.getId());
					params.triggerUri(UriManager.ABBONAMENTO);
				}
			});
		}
	}
	
}
