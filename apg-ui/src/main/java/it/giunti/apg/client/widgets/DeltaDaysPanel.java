package it.giunti.apg.client.widgets;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

public class DeltaDaysPanel extends HorizontalPanel {

	private TextBox daysText = null;
	private ListBox directionList = null;
	
	public DeltaDaysPanel(Integer deltaDays, String referenceDescription) {
		Boolean isAfter = false;
		//DRAW
		//days
		daysText = new TextBox();
		if (deltaDays != null) {
			isAfter = (deltaDays > 0);
			Integer days = Math.abs(deltaDays);
			daysText.setValue(days.toString());
		}
		this.add(daysText);
		//label
		this.add(new InlineHTML("&nbsp;giorni&nbsp;"));
		//direction
		directionList = new ListBox();
		directionList.addItem("prima", Boolean.FALSE.toString());//index 0
		directionList.addItem("dopo", Boolean.TRUE.toString());//index 1
		if (isAfter) {
			directionList.setSelectedIndex(1);
		} else {
			directionList.setSelectedIndex(0);
		}
		this.add(directionList);
		//reference label
		this.add(new InlineHTML("&nbsp;"+referenceDescription));
	}
	
	public Integer getDeltaDays() {
		Integer result = null;
		Integer days = null;
		if (daysText.getValue() != null) {
			try {
				days = Integer.parseInt(daysText.getValue());
			} catch (NumberFormatException e) {
				//Do nothing
			}
		}
		if (days != null) {
			boolean isAfter = false;
			if (directionList.getSelectedIndex() > 0) isAfter = true;
			if (isAfter) {
				result = days;
			} else {
				result = (-1)*days;
			}
		}
		return result;
	}
	
	
}
