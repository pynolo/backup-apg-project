package it.giunti.apg.client.widgets;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.SimplePanel;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.services.MaterialiService;
import it.giunti.apg.client.services.MaterialiServiceAsync;
import it.giunti.apg.shared.model.Materiali;
import it.giunti.apg.shared.model.MaterialiProgrammazione;

public class MaterialiProgrammazioneLabel extends SimplePanel {

	private final MaterialiServiceAsync matService = GWT.create(MaterialiService.class);
	
	private Date date = null;
	private Integer idPeriodico = null;
	private InlineHTML descrLabel = null;
	
	public MaterialiProgrammazioneLabel(Date date, Integer idPeriodico) {
		this.date = date;
		this.idPeriodico = idPeriodico;
		this.descrLabel = new InlineHTML();
		this.add(descrLabel);
		updateDescription();
	}
	
	public void setDate(Date date) {
		this.date = date;
		updateDescription();
	}
	
	public void setIdPeriodico(Integer idPeriodico) {
		this.idPeriodico = idPeriodico;
		updateDescription();
	}
	
	
	// Async methods
	
	
	private void updateDescription() {
		AsyncCallback<MaterialiProgrammazione> callback = new AsyncCallback<MaterialiProgrammazione>() {
			@Override
			public void onFailure(Throwable caught) {
				descrLabel.setHTML("");
				UiSingleton.get().addInfo(caught.getMessage());
			}
			@Override
			public void onSuccess(MaterialiProgrammazione result) {
				descrLabel.setHTML("");
				if (result != null) {
					Materiali mat = result.getMateriale();
					String s = "";
					if (mat.getTitolo() != null) s += " "+mat.getTitolo();
					if (mat.getSottotitolo() != null) s += " "+mat.getSottotitolo();
					descrLabel.setHTML(s);
				}
			}
		};
		if (date != null && idPeriodico != null) {
			descrLabel.setHTML(ClientConstants.ICON_LOADING_SMALL);
			matService.findMaterialeProgrammazioneByPeriodicoDataInizio(this.idPeriodico, date, callback);
		}
	}
}
