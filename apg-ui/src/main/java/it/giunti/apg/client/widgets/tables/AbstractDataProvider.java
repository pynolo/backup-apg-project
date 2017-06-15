package it.giunti.apg.client.widgets.tables;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class AbstractDataProvider<T> {
	
	public static final int DEFAULT_PAGE_SIZE = 50;
	
	private AsyncCallback<List<T>> callback;
	private int pageSize=DEFAULT_PAGE_SIZE;
	
	public AbstractDataProvider(AsyncCallback<List<T>> callback) {
		this.callback=callback;
	}
	
	public abstract void getRows(int page);
	
	public AsyncCallback<List<T>> getCallback() {
		return callback;
	}
	
	public void setPageSize(int pageSize) {
		this.pageSize=pageSize;
	}
	
	public int getPageSize() {
		return pageSize;
	}
	
}
