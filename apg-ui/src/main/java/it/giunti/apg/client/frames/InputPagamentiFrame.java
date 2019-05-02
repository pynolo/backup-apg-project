package it.giunti.apg.client.frames;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.VerticalPanel;

import it.giunti.apg.client.AuthSingleton;
import it.giunti.apg.client.IAuthenticatedWidget;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.widgets.FramePanel;
import it.giunti.apg.client.widgets.SubPanel;
import it.giunti.apg.client.widgets.tables.DataModel;
import it.giunti.apg.client.widgets.tables.FileUploadsTable;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.FileUploads;
import it.giunti.apg.shared.model.Utenti;

public class InputPagamentiFrame extends FramePanel implements IAuthenticatedWidget {

	private static final String TITLE_FORM = "Importazione pagamenti";
	private static int DELAY = 10000; //10 seconds
	
	private VerticalPanel mainPanel = null;
	private boolean isEditor = false;
	private boolean isAdmin = false;
	
	public InputPagamentiFrame(UriParameters params) {
		super();
		AuthSingleton.get().queueForAuthentication(this);
	}
	
	@Override
	public void onSuccessfulAuthentication(Utenti utente) {
		init(utente);
	}
	
	private void init(Utenti utente) {
		// Editing rights
		isEditor = (utente.getRuolo().getId() >= AppConstants.RUOLO_EDITOR);
		isAdmin = (utente.getRuolo().getId() >= AppConstants.RUOLO_ADMIN);
		// UI
		mainPanel = new VerticalPanel();
		this.add(mainPanel, TITLE_FORM);
		if (isEditor) draw();
	}
	
	private void draw() {
		//Form Panel
		FormPanel uploadPanel = drawUploadPanel();
		mainPanel.add(uploadPanel);
		
		SubPanel panelUploaded = new SubPanel("File in attesa");
		this.add(panelUploaded);
		DataModel<FileUploads> futModel = new FileUploadsTable.FileUploadsModel();
		FileUploadsTable fut = new FileUploadsTable(futModel, isAdmin);
		panelUploaded.add(fut);
		mainPanel.add(panelUploaded);
	}
	
	private FormPanel drawUploadPanel() {
		final FormPanel form = new FormPanel();
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);
		VerticalPanel holder = new VerticalPanel();
		form.setAction(GWT.getModuleBaseURL()+AppConstants.SERVLET_UPLOAD_PAGAMENTI);

		holder.add(new HTML("Inserire il file dei bollettini fornito da Poste Italiane:<br/><br/>"));
		final FileUpload upload = new FileUpload();
		upload.setName("upload");
		holder.add(upload);
		holder.add(new HTML("<br />"));
		Hidden utenteHid = new Hidden(AppConstants.PARAM_ID_UTENTE);
		utenteHid.setValue(AuthSingleton.get().getUtente().getId());
		holder.add(utenteHid);
		//rapportoHid = new Hidden(AppConstants.PARAM_ID_RAPPORTO);
		//rapportoHid.setValue("0");
		//holder.add(rapportoHid);
		holder.add(new Button("Carica", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				submitAndDelayRefresh(form);
			}
		}));
		holder.add(new HTML("&nbsp;"));
		form.add(holder);
		return form;
	}

	private void submitAndDelayRefresh(FormPanel form) {
		WaitSingleton.get().start(WaitSingleton.MODE_LONG);
		form.submit();
		Timer timer = new Timer() {
			@Override
			public void run() {
				mainPanel.clear();
				draw();
				WaitSingleton.get().stop();
				this.cancel();
			}
		};
		timer.schedule(DELAY);
	}
}
