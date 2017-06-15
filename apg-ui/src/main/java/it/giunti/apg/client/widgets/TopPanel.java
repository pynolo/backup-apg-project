package it.giunti.apg.client.widgets;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.UriManager;
import it.giunti.apg.client.UriParameters;
import it.giunti.apg.shared.AppConstants;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;

public class TopPanel extends AbsolutePanel implements ResizeHandler {

	public static final int HEIGHT = 50;
	public static final int LOGO_WIDTH = 200;
	private static final int SEARCH_TOP = 10;
	
	private QuickSearchPanel qsPanel = null;
	private Image logoImage = new Image();
	
	public TopPanel() {
		Window.addResizeHandler(this);
		this.setHeight(HEIGHT+"px");
		this.setStyleName("top-banner");
		// topPanel items
		UiSingleton.get().getApgMenuImage(logoImage);
		logoImage.setHeight(HEIGHT+"px");
		logoImage.setWidth(LOGO_WIDTH+"px");
		logoImage.addLoadHandler(new LoadHandler() {
			@Override
			public void onLoad(LoadEvent event) {
				resize();
			}
		});
		this.add(logoImage,0,0);
		
		qsPanel = new QuickSearchPanel();
		this.add(qsPanel,SEARCH_TOP,100);
		resize();
	}
	
	@Override
    public void onResize(ResizeEvent event) {
        resize();
    }
	private void resize() {
		this.setWidth(Window.getClientWidth() + "px");
		resizeQsPanel();
    }

	private void resizeQsPanel() {
		//qsPanel
		Double left = LOGO_WIDTH + ((Window.getClientWidth()-LOGO_WIDTH)-qsPanel.getOffsetWidth())/2D;
		this.setWidgetPosition(qsPanel, left.intValue(), SEARCH_TOP);
	}
	
	
	
	
	//Inner classes
	
	
	

	class QuickSearchPanel extends HorizontalPanel {
		
		final private FormPanel abboForm;
		final private FormPanel anagForm;
		final private TextBox abboSearchText;
		final private TextBox anagSearchText;

		public QuickSearchPanel() {
			abboForm = new FormPanel();
			HorizontalPanel abboHolder = new HorizontalPanel();
			abboSearchText = new LabelledTextBox("Abbonamenti");
			abboSearchText.setTitle("Ricerca per codice abbonamento");
			abboSearchText.setWidth("7em");
			//abboSearchText.setHeight("20px");
			Anchor abboButton = new Anchor();
			abboButton.setHTML("&nbsp;"+ClientConstants.ICON_MAGNIFIER+"&nbsp;<b>Cerca</b>");
			abboButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					abboForm.submit();
				}
			});
			abboHolder.add(abboSearchText);
			abboHolder.add(abboButton);
			abboHolder.add(new HTML("&nbsp;&nbsp;&nbsp;"));
			abboForm.addSubmitHandler(new SubmitHandler() {
				@Override
				public void onSubmit(SubmitEvent event) {
					doAbboSearch();
				}
			});
			abboForm.add(abboHolder);
			this.add(abboForm);
			
			anagForm = new FormPanel();
			HorizontalPanel anagHolder = new HorizontalPanel();
			anagSearchText = new LabelledTextBox("Anagrafiche");
			anagSearchText.setTitle("Ricerca per nome*, cognome*, ragione sociale*, presso*, localita'*, codice anagrafica e cap");
			anagSearchText.setWidth("11em");
			//anagSearchText.setHeight("20px");
			Anchor anagButton = new Anchor();
			anagButton.setHTML("&nbsp;"+ClientConstants.ICON_MAGNIFIER+"&nbsp;<b>Cerca</b>");
			anagButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					anagForm.submit();
				}
			});
			anagHolder.add(anagSearchText);
			anagHolder.add(anagButton);
			anagForm.addSubmitHandler(new SubmitHandler() {
				@Override
				public void onSubmit(SubmitEvent event) {
					doAnagSearch();
				}
			});
			anagForm.add(anagHolder);
			this.add(anagForm);
			
			HorizontalPanel iconHolder = new HorizontalPanel();
			iconHolder.add(new HTML("&nbsp;&nbsp;&nbsp;"));
			iconHolder.add(new LayoutIcon());
			this.add(iconHolder);
		}
		
		private void doAbboSearch() {
			String searchString = abboSearchText.getValue();
			if (searchString != null) {
				if (searchString.length()>1) {
					abboSearchText.setValue("");
					UriParameters params = new UriParameters();
					params.add(AppConstants.PARAM_QUICKSEARCH, searchString);
					params.triggerUri(UriManager.QUICK_SEARCH_ABBONAMENTI);
				}
			}
		}
		
		private void doAnagSearch() {
			String searchString = anagSearchText.getValue();
			if (searchString != null) {
				if (searchString.length()>1) {
					anagSearchText.setValue("");
					UriParameters params = new UriParameters();
					params.add(AppConstants.PARAM_QUICKSEARCH, searchString);
					params.triggerUri(UriManager.QUICK_SEARCH_ANAGRAFICHE);
				}
			}
		}
	}

	
	//Inner classes
	
	
	class LayoutIcon extends SimplePanel {
		
		public LayoutIcon() {
			int layoutType = UiSingleton.get().getLayoutType();
			final SimplePanel fPanel = this;
			final InlineHTML layoutPcIcon = new InlineHTML(
					"<span title='Passa alla vista per schermi grandi' style='font-size:2em'><i class='fa fa-laptop'></i></span>");
			final InlineHTML layoutMobileIcon = new InlineHTML(
					"<span title='Passa alla vista per schermi piccoli' style='font-size:2em'><i class='fa fa-mobile'></i></span>");
			layoutPcIcon.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					fPanel.clear();
					fPanel.add(layoutMobileIcon);
					UiSingleton.get().setLayoutType(UiSingleton.LAYOUT_LEFT_MENU);
				}
			});
			layoutMobileIcon.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					fPanel.clear();
					fPanel.add(layoutPcIcon);
					UiSingleton.get().setLayoutType(UiSingleton.LAYOUT_TOP_MENU);
				}
			});
			if (layoutType == UiSingleton.LAYOUT_LEFT_MENU) {
				this.add(layoutMobileIcon);
			} else {
				this.add(layoutPcIcon);
			}
		}
	}
	
	class LabelledTextBox extends TextBox implements FocusHandler, BlurHandler {
		private String label = null;
		private String STYLE_FOCUSED = "search-box-focused";
		private String STYLE_BLURRED = "search-box-blurred";
		
		public LabelledTextBox(String label) {
			super();
			this.label=label;
			this.setValue("");
			this.addFocusHandler(this);
			this.addBlurHandler(this);
			onBlur(null);
		}

		@Override
		public void onBlur(BlurEvent event) {
			String value = super.getValue();
			if (value.equals("")) {
				this.setStyleName(STYLE_BLURRED);
				this.setValue(label);
			}
		}

		@Override
		public void onFocus(FocusEvent event) {
			this.setStyleName(STYLE_FOCUSED);
			String value = super.getValue();
			if (label.equals(value)) {
				this.setValue("");
			}
		}

		@Override
		public String getValue() {
			String value = super.getValue();
			if (label.equals(value)) return "";
			return value;
		}

	}
}
