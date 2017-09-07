package it.giunti.apg.client.frames;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.CookieSingleton;
import it.giunti.apg.core.DateUtil;
import it.giunti.apg.shared.model.Avvisi;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MaintenancePopUp extends PopupPanel {

	private static final int SIGN_COUNT = 9;
	private static final int IMG_WIDTH = 70;
	private static final int IMG_HEIGHT = 60;
	
	public static boolean isVisible = false;
	private Avvisi avviso = null;
	
	private HTML titleLabel = new HTML();
	private HTML messaggioLabel = new HTML();
	private HTML orarioLabel = new HTML();
	private HTML imageLabel = new HTML();


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
		table.getFlexCellFormatter().setRowSpan(0, 0, 3);
		//Messaggio
		messaggioLabel = new InlineHTML(avviso.getMessaggio());
		if (avviso.getImportante()) messaggioLabel.setStyleName("message-warn");
		table.setWidget(0, 1, messaggioLabel);
		//Manutenzione
		String orario = "<br/>";
		String today = ClientConstants.FORMAT_DAY.format(DateUtil.now());
		String maintenanceDay = ClientConstants.FORMAT_DAY.format(avviso.getDataManutenzione());
		if (!today.equals(maintenanceDay)) orario += "Data: "+maintenanceDay+"<br/>";
		if (avviso.getOraInizio() != null) {
			orario += "Inizio manutenzione: <i>"+ClientConstants.FORMAT_TIME.format(avviso.getOraInizio())+"</i><br/>";
			if (avviso.getOraFine() != null)
					orario += "Fine stimata: <i>"+ClientConstants.FORMAT_TIME.format(avviso.getOraFine())+"</i>";
		}
		orarioLabel = new InlineHTML(orario);
		orarioLabel.setStyleName("avviso-big");
		table.setWidget(1, 0, orarioLabel);
		//Bottone
		Button okButton = new Button("&nbsp;OK&nbsp;", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				//Segna nel cookie che l'avviso è letto
				CookieSingleton.get().setCookie(ClientConstants.COOKIE_LAST_ID_MAINTENANCE,
						avviso.getId().toString());
				close();
			}
		});
		table.setWidget(2, 0, okButton);
		this.add(panel);
		
		this.center();
		isVisible = true;
		this.show();
	}
	
	private void close() {
		isVisible = false;
		this.hide();
	}
	
}
