package it.giunti.apg.shared;

import java.io.Serializable;

public class StatData<T extends Serializable> implements Serializable {

	private T name = null;
	private Integer value = null;
	
	public StatData() {
	}
	
	public StatData(T name, Integer value) {
		this.name = name;
		this.value = value;
	}


	public T getName() {
		return name;
	}


	public void setName(T name) {
		this.name = name;
	}


	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	@Override
	public String toString() {
		String s = name.toString() + " ("+value+")";
		return s;
	}

}
