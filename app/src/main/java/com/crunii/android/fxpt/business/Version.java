package com.crunii.android.fxpt.business;

import org.json.JSONException;
import org.json.JSONObject;

public class Version {
	String name;
	int number;
	String desc;
	String url;
    boolean force;
	
	public Version(JSONObject jsonObject) {
		try {
			name = jsonObject.getString("name");
			number = jsonObject.getInt("number");			
			desc = jsonObject.getString("desc");			
			url = jsonObject.getString("url");
            force = jsonObject.optBoolean("force");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }
}
