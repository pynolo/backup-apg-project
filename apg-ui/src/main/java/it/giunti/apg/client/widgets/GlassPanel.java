package it.giunti.apg.client.widgets;

import it.giunti.apg.client.ClientConstants;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * This panel is positioned absolutely and covers the whole client
 * area of the browser. It can be used to disable everything
 * underneath it with appropriate z-index.
 * <p/>
 * For instance a dialog can attach it to the root panel during
 * opening, and remove it during closing.
 * <p/>
 * CSS:<br>
 * .glassPanel
 * <p/>
 * Example:<br>
 * Add and remove this widget whenever you want, and style it as
 * follows:
 * <pre>
 * .glassPanel{
 *   background-color: black;
 *   filter:alpha(opacity=50);
 *   opacity:.50;
 * }
 * </pre>
 */
public class GlassPanel extends Composite implements ResizeHandler {
    public static final String STYLE = "glass-panel";

    private AbsolutePanel basePanel = new AbsolutePanel();
    private SimplePanel screenPanel = new SimplePanel();
    private InlineHTML waitImg = new InlineHTML(ClientConstants.ICON_LOADING_BIG);
    private SimplePanel imgPanel = new SimplePanel();
    
    public GlassPanel() {
    	basePanel.add(screenPanel);
        imgPanel.add(waitImg);
        basePanel.add(imgPanel);
        initWidget(basePanel);
        screenPanel.setStylePrimaryName(STYLE);
        Window.addResizeHandler(this);
        resize();
    }

    public void onResize(ResizeEvent event) {
        resize();
    }
    
    public void addClickHandler(ClickHandler handler) {
    	waitImg.addClickHandler(handler);
    }
    
    public void show() {
        // Override the styles explicitly, because it's needed
        // every time the widget is detached
        Element panelElem = screenPanel.getElement();
        panelElem.getStyle().setProperty( "left", "0px");
        panelElem.getStyle().setProperty( "top", "0px");
        //DOM.setStyleAttribute(panelElem, "left", "0px");
        //DOM.setStyleAttribute(panelElem, "top", "0px");
        //DOM.setStyleAttribute(panelElem, "position", "absolute");
        RootPanel rp = RootPanel.get();
        rp.add(this);
    }
    
    public void hide() {
        RootPanel.get().remove(this);
    }

    private void resize() {
    	//Schermo
        screenPanel.setSize(Window.getClientWidth() + "px",
                Window.getClientHeight() + "px");
        //Immagine
        Element imgElem = imgPanel.getElement();
        Double imgX = (Window.getClientWidth()-ClientConstants.ICON_LOADING_WIDTH)/2D;
        Double imgY = (Window.getClientHeight()-ClientConstants.ICON_LOADING_HEIGHT)/2D;
        imgElem.getStyle().setProperty("left", imgX.intValue()+"px");
        imgElem.getStyle().setProperty("top", imgY.intValue()+"px");
        imgElem.getStyle().setProperty("position", "absolute");
		//DOM.setStyleAttribute(imgElem, "left", imgX.intValue()+"px");
		//DOM.setStyleAttribute(imgElem, "top", imgY.intValue()+"px");
		//DOM.setStyleAttribute(imgElem, "position", "absolute");
    }

}