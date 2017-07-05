package it.giunti.apg.client.widgets;

import it.giunti.apg.client.ClientConstants;
import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.services.LoggingService;
import it.giunti.apg.client.services.LoggingServiceAsync;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.model.LogEditing;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class VersioningPanel extends VerticalPanel {

	private String classSimpleName;
	private Integer entityId;
	private String defaultIdUtente;
	private Date defaultDate;
	
	public VersioningPanel(String classSimpleName, Integer entityId, String defaultIdUtente, Date defaultDate) {
		this.classSimpleName = classSimpleName;
		this.entityId = entityId;
		this.defaultIdUtente = defaultIdUtente;
		this.defaultDate = defaultDate;
		loadEditLog();
	}
	
	private void draw(List<LogEditing> logList) {
		this.clear();
		this.add(new InlineHTML("<br/><i>Ultime modifiche:</i>"));
		for (LogEditing el:logList) {
			InlineHTML modifiedInfo = new InlineHTML(
					ClientConstants.FORMAT_DATETIME.format(el.getLogDatetime()) +
					" - "+el.getIdUtente());
			this.add(modifiedInfo);
		}
	}
	
	private void loadEditLog() {
		add(new InlineHTML(ClientConstants.LABEL_LOADING));
		LoggingServiceAsync loggingService = GWT.create(LoggingService.class);
		AsyncCallback<List<LogEditing>> callback = new AsyncCallback<List<LogEditing>>() {
			@Override
			public void onFailure(Throwable caught) {
				if (caught instanceof EmptyResultException) {
					clear();
					if ((defaultIdUtente != null) && (defaultDate != null)) {
						add(new InlineHTML(ClientConstants.FORMAT_DATETIME.format(defaultDate) +
								" - "+defaultIdUtente));
					} else {
						add(new InlineHTML("<i>"+AppConstants.MSG_EMPTY_LOG+"</i>"));
					}
				} else {
					UiSingleton.get().addError(caught);
				}
			}
			@Override
			public void onSuccess(List<LogEditing> result) {
				draw(result);
			}
		};
		loggingService.findEditLogs(classSimpleName, entityId, callback);
	}
	
}
