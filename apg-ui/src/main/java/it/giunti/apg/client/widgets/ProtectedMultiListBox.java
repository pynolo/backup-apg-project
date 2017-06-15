package it.giunti.apg.client.widgets;

import it.giunti.apg.client.ClientConstants;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ProtectedMultiListBox extends SimplePanel {

	private ListBox listBox = null;
	private VerticalPanel listPanel = null;
	private List<String> items = null;
	private List<String> values = null;
	private boolean enabled = true;
	
	public ProtectedMultiListBox() {
		listBox = new ListBox();
		listBox.setMultipleSelect(true);
		listPanel = new VerticalPanel();
		items = new ArrayList<String>();
		values = new ArrayList<String>();
		draw();
	}
	
	private void draw() {
		final SimplePanel fThis = this;
		final VerticalPanel holder = new VerticalPanel();
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

	public boolean isItemSelected(int index) {
		return listBox.isItemSelected(index);
	}

	public void setItemSelected(int index, boolean selected) {
		listBox.setItemSelected(index, selected);
		if (selected) {
			listPanel.add(new HTML("<b>"+items.get(index)+"</b>"));
		}
	}
	
	public void setSelectedIndex(int index) {
		listBox.setSelectedIndex(index);
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled=enabled;
	}
	public boolean getEnabled() {
		return this.enabled;
	}
	
	public void setVisibleItemCount(int visibleItems) {
		listBox.setVisibleItemCount(visibleItems);
	}
	
	public int getItemCount() {
		return listBox.getItemCount();
	}
	
	public String getValue(int index) {
		return values.get(index);
	}
	
	public void clear() {
		listPanel.clear();
		listBox.clear();
		items.clear();
		values.clear();
		super.clear();
		draw();
	}
}
