package it.giunti.apg.client.widgets.select;

import it.giunti.apg.client.UiSingleton;
import it.giunti.apg.client.WaitSingleton;
import it.giunti.apg.client.services.LookupService;
import it.giunti.apg.client.services.LookupServiceAsync;
import it.giunti.apg.shared.model.FileResources;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FileResourcesSelect extends EntitySelect<FileResources> {
	
	private String relativePath = null;
	private String fileType = null;
	
	public FileResourcesSelect(String selectedPath, String relativePath, String fileType) {
		super(selectedPath);
		this.relativePath=relativePath;
		this.fileType=fileType;
		reload(selectedPath);
	}

	public void reload(String selectedPath) {
		this.setSelectedId(selectedPath);
		loadEntityList();
	}
	
	@Override
	protected void drawListBox(List<FileResources> entityList) {
		this.clear();
		//selected is found?
		boolean found = false;
		//this.setVisibleItemCount(1);
		//Disegna la lista delle Macroaree selezionando quello con selectedId
		for (int i=0; i<entityList.size(); i++) {
			FileResources p = entityList.get(i);
			String path = relativePath+p.getPath();
			this.addItem(path);
			if (path.equals(getSelectedId())) found=true;
		}
		if (!found) this.addItem(getSelectedId()+" (non trovato)", getSelectedId());
		showSelectedValue();
	}
	
	protected void loadEntityList() {
		LookupServiceAsync lookupService = GWT.create(LookupService.class);
		AsyncCallback<List<FileResources>> callback = new AsyncCallback<List<FileResources>>() {
			@Override
			public void onFailure(Throwable caught) {
				UiSingleton.get().addError(caught);
				WaitSingleton.get().stop();
			}
			@Override
			public void onSuccess(List<FileResources> result) {
				setEntityList(result);
				try {
					drawListBox(getEntityList());
				} catch (Exception e) {
					UiSingleton.get().addWarning(e.getMessage());
				}
				WaitSingleton.get().stop();
			}
		};
		WaitSingleton.get().start();
		lookupService.findFileResources(fileType, callback);
	}
	
}
