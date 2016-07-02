package com.crunii.android.fxpt.business;

public class Item {
	private String id;
	private String name;
	private String upperId;

	public Item(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public Item(String id, String name, String upperId) {
		this.id = id;
		this.name = name;
		this.upperId = upperId;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

	public String getUpperId() {
		return upperId;
	}

	public void setUpperId(String upperId) {
		this.upperId = upperId;
	}

}
