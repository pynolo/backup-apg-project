package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.shared.model.Utenti;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ErrorFrame extends FramePanel implements IAuthenticatedWidget {
	
	private VerticalPanel panel = null;
	
	public ErrorFrame(UriParameters params) {
		AuthSingleton.get().queueForAuthentication(this);
	}
	
	@Override
	public void onSuccessfulAuthentication(Utenti utente) {
		draw();
	}
	
	private void draw() {
		panel = new VerticalPanel();
		this.add(panel, "Errore");
		HTML html = new HTML("<b>Non &egrave; possibile mostrare questa pagina</b>");
		panel.add(html);
	}
}
