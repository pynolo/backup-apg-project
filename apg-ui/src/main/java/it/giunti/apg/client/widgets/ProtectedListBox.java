package it.giunti.apg.client.widgets;

import it.giunti.apg.client.ClientConstants;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;

public class ProtectedListBox extends SimplePanel {

	private final static String NO_SELECTION_LABEL = "&nbsp;(nessuno selezionato)&nbsp;";
	private ListBox listBox = null;
	private FlowPanel listPanel = null;
	private List<String> items = null;
	private List<String> values = null;
	private boolean enabled = true;
	
	public ProtectedListBox() {
		listBox = new ListBox();
		listPanel = new FlowPanel();
		listPanel.add(new InlineHTML(NO_SELECTION_LABEL));
		items = new ArrayList<String>();
		values = new ArrayList<String>();
		draw();
	}
	
	private void draw() {
		final SimplePanel fThis = this;
		final HorizontalPanel holder = new HorizontalPanel();
		holder.add(listPanel);
		if (enabled) {
			InlineHTML editImg = new InlineHTML(ClientConstants.ICON_EDIT);
			editImg.setTitle("Modifica");
			editImg.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent arg0) {
					fThis.remove(holder);
					fThis.add(listBox);
				}
			});
			holder.add(editImg);
		}
		this.add(holder);
	}

	public void addItem(String item, String value) {
		items.add(item);
		values.add(value);
		listBox.addItem(item, value);
	}

	public int getSelectedIndex() {
		return listBox.getSelectedIndex();
	}

	public void setSelectedIndex(int index) {
		listBox.setSelectedIndex(index);
		listPanel.clear();
		listPanel.add(new InlineHTML("<b>"+items.get(index)+"</b>"));
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled=enabled;
	}
	public boolean getEnabled() {
		return this.enabled;
	}
	
	public String getValue(int index) {
		return values.get(index);
	}
	
	public void clear() {
		listPanel.clear();
		listPanel.add(new InlineHTML(NO_SELECTION_LABEL));
		listBox.clear();
		items.clear();
		values.clear();
		super.clear();
		draw();
	}
}
