package com.crunii.android.fxpt.business;

import java.io.Serializable;

import org.json.JSONObject;

public class Param implements Serializable{
	private static final long serialVersionUID = 9122670675862720164L;
	
	private String name;
	private String value;
	
	public Param(JSONObject json) {
		name = json.optString("name");
		value = json.optString("value");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
