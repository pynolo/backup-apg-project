package it.giunti.apg.client;

import it.giunti.apg.client.frames.MaintenancePopUp;
import it.giunti.apg.client.services.LoggingService;
import it.giunti.apg.client.services.LoggingServiceAsync;
import it.giunti.apg.client.services.LookupService;
import it.giunti.apg.client.services.LookupServiceAsync;
import it.giunti.apg.client.widgets.LeftMenuPanel;
import it.giunti.apg.client.widgets.MessagePanel;
import it.giunti.apg.client.widgets.TopMenuPanel;
import it.giunti.apg.client.widgets.TopPanel;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.Avvisi;
import it.giunti.apg.shared.model.Periodici;
import it.giunti.apg.shared.model.Utenti;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class UiSingleton implements ValueChangeHandler<String> {

	/*
	 *  I------------------------------------------I
	 *  I      I                                   I
	 *  I Logo I  Quick +infoMessages              I
	 *  I      I                                   I
	 *  I------------------------------------------I
	 *  I       I Message                          I
	 *  I       I----------------------------------I
	 *  I Menu  I                                  I
	 *  I       I Content                          I
	 *  I       I                                  I
	 *  I------------------------------------------I
	 */
	public static final int LAYOUT_LEFT_MENU = 0;
	public static final int LAYOUT_TOP_MENU = 1;
	private static final int BANNER_IMAGE_COUNT = 31;
	private static final String BANNER_STYLE_PREFIX = "top-banner-";
	
	private static UiSingleton instance = null;
	private int layoutType = LAYOUT_LEFT_MENU;
	private TopPanel topPanel = null;
	
	private List<Periodici> periodiciList = null;
	
	private LeftMenuPanel leftMenuPanel = null;
	private TopMenuPanel topMenuPanel = null;
	private SimplePanel contentPanel = null;
	private MessagePanel messagePanel = null;
	
	private String apgTitle = null;
	private String apgStatus = null;
	private String apgMenuImage = null;
	private String apgLoginImage = null;
	
	private LoggingServiceAsync loggingService = GWT.create(LoggingService.class);
	
	
	private UiSingleton() {
		loadPeriodiciList();
	}
	
	public static UiSingleton get() {
		if (instance == null) {
			instance = new UiSingleton();
		}
		return instance;
	}
	
	public void drawUi() {
		getApgTitle();
		getApgStatus();
		loadLayoutType();
		RootPanel.get("header").clear();
		RootPanel.get("horizontal-menu").clear();
		RootPanel.get("right-column").clear();
		RootPanel.get("left-column").clear();
		RootPanel.get("message-panel").clear();
		if (layoutType == LAYOUT_TOP_MENU) {
			initTopMenuLayout();
		} else {
			initLeftMenuLayout();
		}
	}
	
	private void initTopMenuLayout() {
		// Panels
		topPanel = new TopPanel();
		RootPanel.get("header").add(topPanel);
		setTopPanelBackground();
		
		// top menu
		topMenuPanel = new TopMenuPanel();
		RootPanel.get("horizontal-menu").add(topMenuPanel);
		
		// bottomPanel items
		FlowPanel verticalPanelGroup = new FlowPanel();
		contentPanel = new SimplePanel();
		contentPanel.setWidth("100%");
		verticalPanelGroup.add(contentPanel);
		RootPanel.get("right-column").add(verticalPanelGroup);
		
		// message panel
		messagePanel = new MessagePanel();
		RootPanel.get("message-panel").add(messagePanel);
		
		// navigation functionality
		initHistorySupport();
	}
	
	private void initLeftMenuLayout() {
		// Panels
		topPanel = new TopPanel();
		RootPanel.get("header").add(topPanel);
		setTopPanelBackground();
		
		// bottomPanel items
		FlowPanel centralPanelGroup = new FlowPanel();
		contentPanel = new SimplePanel();
		contentPanel.setWidth("100%");
		centralPanelGroup.add(contentPanel);
		RootPanel.get("right-column").add(centralPanelGroup);
		
		// structure
		leftMenuPanel = new LeftMenuPanel();
		RootPanel.get("left-column").add(leftMenuPanel);
		//bottomPanelGroup.setCellWidth(menuPanel, menuPanel.getOffsetWidth()+"px"); errore su IE
		
		// message-panel
		messagePanel = new MessagePanel();
		RootPanel.get("message-panel").add(messagePanel);
		
		// navigation functionality
		initHistorySupport();
	}
	
	private void loadLayoutType() {
		this.layoutType=LAYOUT_LEFT_MENU;
		String layoutTypeString = CookieSingleton.get().getCookie(ClientConstants.COOKIE_LAYOUT);
		if (layoutTypeString != null) {
			try {
				this.layoutType = Integer.parseInt(layoutTypeString);
			} catch (NumberFormatException e) { }
		}
	}
	
	private void saveLayoutType(int layoutType) {
		CookieSingleton.get().setCookie(ClientConstants.COOKIE_LAYOUT, layoutType+"");
	}
	
	private void setTopPanelBackground() {
		Date now = new Date();
		String dayInMonth$ = DateTimeFormat.getFormat("d").format(now);
		String monthInYear$ = DateTimeFormat.getFormat("M").format(now);
		Integer dayInMonth = Integer.parseInt(dayInMonth$);
		Integer monthInYear = Integer.parseInt(monthInYear$);
		int num = Math.abs((dayInMonth+monthInYear)%BANNER_IMAGE_COUNT);
		String imgNum = NumberFormat.getFormat("00").format(num);
		String styleName = BANNER_STYLE_PREFIX+imgNum;
		topPanel.setStyleName(styleName);
	}
	
	//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
	// history mgmt
	//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

	private void initHistorySupport() {
		// add the MainPanel as a history listener
		History.addValueChangeHandler(this);//addHistoryListener(this);
		// check to see if there are any tokens passed at startup via the browser's URI
		String token = History.getToken();
		if (token.length() == 0) {
			UriManager.loadContent(UriManager.INDEX);
		}
		else {
			UriManager.loadContent(token);
		}
	}

	/**
	 * this method is called when the fwd/back buttons are invoked on the browser.
	 * this is also invoked when hyperlinks (generated by the app) are clicked.
	 */
	public void onValueChange(ValueChangeEvent<String> event) {
		// This method is called whenever the application's history changes. Set
		// the label to reflect the current history token.
		UriManager.loadContent(event.getValue());
	}


	//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
	// getters & setters
	//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
	
	public Integer getDefaultIdPeriodico(Utenti utente) {
		Integer idPeriodico=AppConstants.DEFAULT_ID_PERIODICO;
		if (utente != null) {
			if (periodiciList != null) {
				idPeriodico = periodiciList.get(0).getId();
				if (utente != null) {
					if (utente.getPeriodiciUidRestriction() != null) {
						if (utente.getPeriodiciUidRestriction().length() > 0) {
							String firstUid = utente.getPeriodiciUidRestriction().substring(0, 1);
							for (Periodici pdc:periodiciList) {
								if (pdc.getUid().equals(firstUid))
									idPeriodico = pdc.getId();
							}
						}
					}
				}
			}
		}
		return idPeriodico;
	}
	
	//Apg title
	public String getApgTitle() {
		if (apgTitle == null) {
			AsyncCallback<String> callback = new AsyncCallback<String>() {
				@Override
				public void onFailure(Throwable e) {
					apgTitle = ClientConstants.APG_DEFAULT_TITLE;
				}
				@Override
				public void onSuccess(String value) {
					apgTitle = value;
					UiSingleton.get().setApplicationTitle(apgTitle);
				}
			};
			LookupServiceAsync lookupService = GWT.create(LookupService.class);
			lookupService.getApgTitle(callback);
		}
		return apgTitle;
	}
	
	public String getApgStatus() {
		if (apgStatus == null) {
			AsyncCallback<String> callback = new AsyncCallback<String>() {
				@Override
				public void onFailure(Throwable e) {
					apgStatus = "TEST";
				}
				@Override
				public void onSuccess(String value) {
					apgStatus = value;
				}
			};
			LookupServiceAsync lookupService = GWT.create(LookupService.class);
			lookupService.getApgStatus(callback);
		}
		return apgTitle;
	}
	
	//Apg logo
	public Image getApgMenuImage(Image logoImage) {
		if (apgMenuImage != null) {
			logoImage.setUrl(apgMenuImage);
		} else{
			final Image fLogoImage = logoImage;
			AsyncCallback<String> callback = new AsyncCallback<String>() {
				@Override
				public void onFailure(Throwable e) {
					UiSingleton.get().addWarning("Impossibile caricare l'immagine");
				}
				@Override
				public void onSuccess(String value) {
					apgMenuImage = value;
					fLogoImage.setUrl(apgMenuImage);
				}
			};
			LookupServiceAsync lookupService = GWT.create(LookupService.class);
			lookupService.getApgMenuImage(callback);
		}
		return logoImage;
	}
	
	//Apg logo
	public Image getApgLoginImage(Image logoImage) {
		if (apgLoginImage != null) {
			logoImage.setUrl(apgLoginImage);
		} else{
			final Image fLogoImage = logoImage;
			AsyncCallback<String> callback = new AsyncCallback<String>() {
				@Override
				public void onFailure(Throwable e) {
					UiSingleton.get().addWarning("Impossibile caricare l'immagine");
				}
				@Override
				public void onSuccess(String value) {
					apgLoginImage = value;
					fLogoImage.setUrl(apgLoginImage);
				}
			};
			LookupServiceAsync lookupService = GWT.create(LookupService.class);
			lookupService.getApgLoginImage(callback);
		}
		return logoImage;
	}
	
	public int getLayoutType() {
		return layoutType;
	}
	
	public void setLayoutType(int layoutType) {
		this.layoutType=layoutType;
		saveLayoutType(layoutType);
		drawUi();
	}
	
	public void addError(Throwable e) {
		messagePanel.addError(e);
	}
	
	public void addWarning(String warning) {
		messagePanel.addWarning(warning);
	}
	
	public void addInfo(String info) {
		messagePanel.addInfo(info);
	}

	public SimplePanel getContentPanel() {
		return contentPanel;
	}
	
	public void setApplicationTitle(String title) {
		Window.setTitle(title);
	}
	
	
	//Async methods
	
	private void loadPeriodiciList() {
		LookupServiceAsync lookupService = GWT.create(LookupService.class);
		AsyncCallback<List<Periodici>> callback = new AsyncCallback<List<Periodici>>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
			}
			@Override
			public void onSuccess(List<Periodici> result) {
				periodiciList = result;
			}
		};
		lookupService.findPeriodici(callback);
	}
	
	public void checkMaintenance() {
		AsyncCallback<Avvisi> callback = new AsyncCallback<Avvisi>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
			}
			@Override
			public void onSuccess(Avvisi avviso) {
				if (avviso != null) {
					Integer id = 0;
					String idS = CookieSingleton.get().getCookie(ClientConstants.COOKIE_LAST_ID_MAINTENANCE);
					if (idS != null) {
						try {
							id = Integer.parseInt(idS);
						} catch (NumberFormatException e) {}
					}
					if (avviso.getId() > id) {
						new MaintenancePopUp(avviso);
						CookieSingleton.get().setCookie(ClientConstants.COOKIE_LAST_ID_MAINTENANCE,
								avviso.getId().toString());
					}
				}
			}
		};
		loggingService.checkMaintenence(callback);
	}
}
