package it.giunti.apg.client.widgets.select;

import it.giunti.apg.shared.AppConstants;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.FlowPanel;

public class TagSelectPanel extends FlowPanel {
	
	private boolean enabled = true;
	private String tagString = "";
	private List<TagSelect> selectList = new ArrayList<TagSelect>();
	
	public TagSelectPanel(String tagString) {
		if (tagString != null) this.tagString = tagString;
		draw();
	}
	
	private void draw() {
		this.clear();
		if (tagString.length() > 0) {
			String[] tags = tagString.split(AppConstants.STRING_SEPARATOR);
			for (String tag:tags) {
				TagSelect select = new TagSelect(tag, this);
				select.setEnabled(enabled);
				this.add(select);
				selectList.add(select);
			}
		}
		pack();
	}
	
	public String getTagValues() {
		String result = "";
		for (TagSelect select:selectList) {
			if (select.getSelectedValueString().length() > 0) {
				if (result.length() > 0) result += AppConstants.STRING_SEPARATOR;
				result += select.getSelectedValueString();
			}
		}
		return result;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		for (TagSelect select:selectList) {
			select.setEnabled(enabled);
		}
	}
	
	/**
	 * Verifica l'elenco dei select box e fa in modo che ce ne sia
	 * uno solo vuoto, in fondo alla lista.
	 */
	protected void pack() {
		List<TagSelect> checkList = new ArrayList<TagSelect>();
		checkList.addAll(selectList);
		for (TagSelect select:checkList) {
			if (select.getSelectedValueString() == null) {
				this.remove(select);
				selectList.remove(select);
			} else if (select.getSelectedValueString().equals("")) {
				this.remove(select);
				selectList.remove(select);
			}
		}
		//Opzione vuota in coda
		TagSelect select = new TagSelect("", this);
		select.setEnabled(enabled);
		this.add(select);
		selectList.add(select);
	}
	
}


