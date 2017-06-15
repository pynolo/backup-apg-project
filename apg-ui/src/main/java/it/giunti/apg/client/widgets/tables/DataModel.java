package it.giunti.apg.client.widgets.tables;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DataModel<T> {

	public void find(int offset, int pageSize, AsyncCallback<List<T>> callback);
	
}
