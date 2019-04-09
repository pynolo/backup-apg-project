package it.giunti.apg.client.frames;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.UriManager;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.client.widgets.tables.AdesioniTable;
import it.giunti.apg.client.widgets.tables.DataModel;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Adesioni;
import it.giunti.apg.shared.model.Ruoli;
import it.giunti.apg.shared.model.Utenti;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.TextBox;

public class AdesioniFindFrame extends FramePanel implements IAuthenticatedWidget {
	
	private Ruoli role = null;
	private boolean isOperator = false;
	//private boolean isEditor = false;
	private boolean isAdmin = false;
	
	private String prefix = null;
	private FlowPanel fPanel = null;
	private TextBox prefixTxt = null;
	private AdesioniTable aTable = null;
	
	// METHODS
	
	public AdesioniFindFrame(UriParameters params) {
		super();
		if (params == null) {
			params = new UriParameters();
		}
		// prefisso
		prefix = params.getValue(AppConstants.PARAM_QUICKSEARCH);
		if (prefix == null) {
			prefix = "";
		}
		prefix = prefix.trim();
		this.setWidth("100%");
		AuthSingleton.get().queueForAuthentication(this);
	}
	
	@Override
	public void onSuccessfulAuthentication(Utenti utente) {
		init(utente);
	}
	
	private void init(Utenti utente) {
		// Editing rights
		role = utente.getRuolo();
		isOperator = (role.getId() >= AppConstants.RUOLO_OPERATOR);
		//isEditor = (role.getId() >= AppConstants.RUOLO_EDITOR);
		isAdmin = (role.getId() >= AppConstants.RUOLO_ADMIN);
		// UI
		if (isOperator) {
			draw();
		}
	}
	
	private void draw() {
		this.clear();
		fPanel = new FlowPanel();
		this.add(fPanel, "Adesioni");

		HorizontalPanel topPanel = new HorizontalPanel();
		//Data estrazione
		topPanel.add(new InlineHTML("&nbsp;Filtro&nbsp;"));
		prefixTxt = new TextBox();
		prefixTxt.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				UriParameters params = new UriParameters();
				params.add(AppConstants.PARAM_QUICKSEARCH, prefixTxt.getValue());
				params.triggerUri(UriManager.ADESIONI_FIND);
			}
		});
		topPanel.add(prefixTxt);
		fPanel.add(topPanel);
		
		if (isAdmin) {
			fPanel.add(new InlineHTML("<br/>"));
			//Adesioni
			Anchor createFasButton = new Anchor(ClientConstants.ICON_ADD+"Crea adesione", true);
			createFasButton.addMouseDownHandler(new MouseDownHandler() {
				@Override
				public void onMouseDown(MouseDownEvent event) {
					new AdesionePopUp(AppConstants.NEW_ITEM_ID, aTable);
				}
			});
			fPanel.add(createFasButton);
		}
		
		DataModel<Adesioni> model = new AdesioniTable.AdesioniModel(prefix);
		aTable = new AdesioniTable(model, role);
		fPanel.add(aTable);
	}
	
}
