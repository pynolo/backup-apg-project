package it.giunti.apg.client.widgets.select;

import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.ValueUtil;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.user.client.ui.ListBox;

public class Select extends ListBox {

	private String selectedId = null;
	
	public Select(String selectedId) {
		setSelectedId(selectedId);
	}
	
	public Select(Integer selectedId) {
		setSelectedId(selectedId);
	}
	
	//GETTERS AND SETTERS
	protected String getSelectedId() {
		return selectedId;
	}
	public void setSelectedId(String selectedId) {
		this.selectedId = selectedId;
	}
	public void setSelectedId(Integer selectedId) {
		this.selectedId = "";
		if (selectedId != null) this.selectedId = selectedId.toString();
	}
	
	
	//VALUE METHODS
	/** Se la lista non è caricata ritorna null
	 */
	public Integer getSelectedValueInt() {
		return ValueUtil.stoi(getSelectedValueString());
	}
	
	/** Se la lista non è caricata ritorna null
	 */
	public String getSelectedValueString() {
		String result = null;
		if (getItemCount() > 0) {
			result = getValue(getSelectedIndex());
			if (result != null) {
				if (result.equals(AppConstants.SELECT_EMPTY_LABEL))
						result = null;
			}
		}
		return result;
	}

	public String getSelectedValueDescription() {
		return this.getItemText(this.getSelectedIndex());
	}
	
	public void showSelectedValue() {
		if (selectedId != null) {
			if (selectedId.length() > 0) {
				int count = this.getItemCount();
				if (count > 0) {
					for (int i = 0; i < count; i++) {
						String currentId = this.getValue(i);
						if (currentId.equals(selectedId)) {
							this.setSelectedIndex(i);
						}
					}
				} else {
					UiSingleton.get().addWarning("Il ListBox non ha potuto caricare l'id "+selectedId);
				}
			}
		}
	}
	
	public void createChangeEvent() {
		NativeEvent evt = Document.get().createChangeEvent();
		DomEvent.fireNativeEvent(evt, this);
	}
	
}
