package it.giunti.apg.client.frames;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.shared.model.Avvisi;

import java.util.Date;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MaintenancePopUp extends PopupPanel {

	private Avvisi avviso = null;
	
	private InlineHTML titleLabel = new InlineHTML();
	private InlineHTML messaggioLabel = new InlineHTML();
	private InlineHTML orarioLabel = new InlineHTML();
	private InlineHTML imageLabel = new InlineHTML();
	
	private int SIGN_COUNT = 9;
	private int IMG_HEIGHT = 70;
	private int IMG_WIDTH = 60;
	
	public MaintenancePopUp(Avvisi avviso) {
		super(false);
		this.avviso = avviso;
		draw();
	}
	
	private void draw() {
		VerticalPanel panel = new VerticalPanel();
		//Titolo
		titleLabel.setStyleName("frame-title");
		titleLabel.setHTML("Avviso: manutenzione programmata");
		panel.add(titleLabel);
		FlexTable table = new FlexTable();
		panel.add(table);
		//Immagine
		Double rnd = Math.floor(Random.nextDouble() * SIGN_COUNT);
		imageLabel = new InlineHTML("<img src='img/maintenance/sign0"+rnd+".png"+
				"' width='"+IMG_WIDTH+"px' height='"+IMG_HEIGHT+"px' />");
		table.setWidget(0, 0, imageLabel);
		table.getFlexCellFormatter().setColSpan(0, 0, 2);
		//Messaggio
		messaggioLabel = new InlineHTML(avviso.getMessaggio());
		if (avviso.getImportante()) messaggioLabel.setStyleName("message-warn");
		table.setWidget(0, 1, messaggioLabel);
		//Manutenzione
		String orario = "";
		String today = ClientConstants.FORMAT_DAY.format(new Date());
		String maintenanceDay = ClientConstants.FORMAT_DAY.format(avviso.getDataManutenzione());
		if (!today.equals(maintenanceDay)) orario = "Data: "+maintenanceDay+"<br/>";
		if (avviso.getOraInizio() != null) {
			if (avviso.getOraInizio().length() > 1) {
				orario += "Inizio manutenzione: <i>"+avviso.getOraInizio()+"</i><br/>";
				if (avviso.getOraFine() != null) {
					if (avviso.getOraFine().length() > 1) {
						orario += "Fine stimata: <i>"+avviso.getOraFine()+"</i>";
					}
				}
			}
		}
		orarioLabel.setStyleName("avviso-big");
		orarioLabel = new InlineHTML(orario);
		table.setWidget(1, 0, orarioLabel);
		//Bottone
		Button okButton = new Button("&nbsp;OK&nbsp;", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				close();
			}
		});
		table.setWidget(2, 0, okButton);
		this.add(panel);
		this.show();
	}
	
	private void close() {
		this.hide();
	}
	
}
