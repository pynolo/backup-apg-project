package it.giunti.apg.client.widgets.tables;

import it.giunti.apg.client.ClientConstants;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Label;

public abstract class PagingTable<T> extends FlowPanel {

	private int page = 0;
	private int pageSize;
	private boolean empty = false;
	
	private DataModel<T> model = null;
	private FlexTable table = null;
	private HTML messageHtml = null;
	private PagingLinkPanel pagingPanel = null;
	
	public PagingTable(DataModel<T> model, int pageSize) {
		this.model=model;
		this.pageSize=pageSize;
		clearInnerTable();
		//this.add(pagingPanel); ora è nella tabella
	}
	
	public void clearInnerTable() {
		if (table != null) {
			this.remove(table);
			table = null;
			this.clear();
		}
		table = new FlexTable();
		table.setStyleName("apg-table");
		this.add(table);
	}
	
	public FlexTable getInnerTable() {
		return table;
	}
	
	public final void goPreviousPage() {
		if (page > 0) {
			page--;
			drawPage(page);
		}
	}
	
	public final void goNextPage() {
		page++;
		drawPage(page);
	}
	
	public void setTableRows(List<T> list) {
		pagingPanel = new PagingLinkPanel(page);
		//getInnerTable().removeAllRows();
		empty = (list == null);
		if (!empty) empty = (list.size() == 0);
		if (empty) {
			drawEmptyMessage();
		} else {
			drawTableRows(list);
		}
	}
	
	private void drawEmptyMessage() {
		if (table != null) {
			this.remove(table);
			table = null;
			this.clear();
		}
		if (messageHtml != null) {
			this.remove(messageHtml);
			messageHtml = null;
		}
		messageHtml = new HTML(ClientConstants.LABEL_EMPTY_RESULT);
		this.add(messageHtml);
	}
	
	private void drawTableRows(List<T> list) {
		getInnerTable().setVisible(true);
		if (messageHtml != null) {
			this.remove(messageHtml);
			messageHtml = null;
		}
		clearInnerTable();
		//Fill with data
		addHeader();
		for (int i = 0; i<list.size(); i++) {
			addTableRow(i+1, list.get(i));
		}
		if (list.size() < pageSize) {
			pagingPanel.switchNextButton(false);
		} else {
			pagingPanel.switchNextButton(true);
		}
		getInnerTable().setWidget(list.size()+1, 0, pagingPanel);
		getInnerTable().getFlexCellFormatter().setColSpan(list.size()+1, 0, getInnerTable().getCellCount(0));
		getInnerTable().getFlexCellFormatter().setStyleName(list.size()+1, 0, "center-panel");
		showButtons();
		applyDataRowStyles();
	}
	

	public abstract void drawPage(int page);
	
	protected abstract void addHeader();
	
	protected abstract void addTableRow(int rowNum, T rowObj);

	protected abstract void onEmptyResult();
	
	public DataModel<T> getModel() {
		return model;
	}

	public void setModel(DataModel<T> model) {
		this.model=model;
		page = 0;
		drawPage(page);
	}
	
	private void applyDataRowStyles() {
		HTMLTable.RowFormatter rf = table.getRowFormatter();

		rf.addStyleName(0, "apg-row-top");
		for (int row = 1; row < table.getRowCount()-1; ++row) {
			if ((row % 2) != 0) {
				rf.addStyleName(row, "apg-row-odd");
			} else {
				rf.addStyleName(row, "apg-row-even");
			}
		}
		rf.addStyleName(table.getRowCount()-1, "apg-row-bot");
	}

	public void hideButtons() {
		if (pagingPanel != null && getInnerTable() != null) {
			pagingPanel.setVisible(false);
			int lastRow = getInnerTable().getRowCount();
			getInnerTable().getRowFormatter().setVisible(lastRow, false);
		}
	}
	public void showButtons() {
		if (pagingPanel != null && getInnerTable() != null) {
			pagingPanel.setVisible(true);
			int lastRow = getInnerTable().getRowCount();
			getInnerTable().getRowFormatter().setVisible(lastRow, true);
		}
	}
	
	public boolean isEmpty() {
		return empty;
	}
	
	//Inner classes
	
	
	private class PagingLinkPanel extends FlexTable {
		private static final String PREV = "<< precedenti ";
		private static final String NEXT = " successivi >>";
		private boolean prev = false;
		private boolean next = false;
		private Label prevLabel;
		private Label nextLabel;
		private Anchor prevButton;
		private Anchor nextButton;
		private int page;
		
		public PagingLinkPanel(int page) {
			this.page=page;
			prevLabel = new Label(PREV);
			prevButton = new Anchor(PREV);
			prevButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent arg0) {
					goPreviousPage();
				}
			});
			this.setWidget(0, 0, prevLabel);
			this.setWidget(0, 1, prevButton);
			nextLabel = new Label(NEXT);
			nextButton = new Anchor(NEXT);
			nextButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent arg0) {
					goNextPage();
				}
			});
			this.setWidget(0, 2, nextLabel);
			this.setWidget(0, 3, nextButton);
			refreshPrevButton();
		}
		
		private void refreshPrevButton() {
			boolean isFirstPage = (page == 0);
			prev = !isFirstPage;
			verifyPanel();
		}
		private void switchNextButton(boolean isClickable) {
			next = isClickable;
			verifyPanel();
		}
		private void verifyPanel() {
			boolean panelVisible = prev || next;
			if (panelVisible) {
				showButtons();
			} else {
				hideButtons();
			}
			prevLabel.setVisible(!prev && panelVisible);
			prevButton.setVisible(prev && panelVisible);
			nextLabel.setVisible(!next && panelVisible);
			nextButton.setVisible(next && panelVisible);
		}
	}
}
