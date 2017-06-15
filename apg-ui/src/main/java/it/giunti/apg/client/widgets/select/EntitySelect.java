package it.giunti.apg.client.widgets.select;

import java.util.List;

public abstract class EntitySelect<T> extends Select {

	private List<T> entityList = null;
	
	//COSTRUTTORI
	public EntitySelect(String selectedId) {
		super(selectedId);
	}
	
	public EntitySelect(Integer selectedId) {
		super(selectedId);
	}
	
	//METODI ASTRATTI	
	protected abstract void drawListBox(List<T> entityList);
	
	protected abstract void loadEntityList();
	
	
	//GETTERS AND SETTERS
	protected List<T> getEntityList() {
		return entityList;
	}
	protected void setEntityList(List<T> entityList) {
		this.entityList = entityList;
	}
	
}
