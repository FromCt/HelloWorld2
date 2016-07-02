package com.crunii.android.fxpt.business;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AdBanner implements Serializable {
	private static final long serialVersionUID = -9073074986856429019L;

	private String image;
	private String bgPicPath;
	private String advDesc;


	public AdBanner(JSONObject jsonObject) {
		this.image = jsonObject.optString("image");
		this.bgPicPath=jsonObject.optString("bgPicPath");
		this.advDesc=jsonObject.optString("advDesc");
	}
	public AdBanner(String image){
		this.image = image;
	}


	public String getBgPicPath() {
		return bgPicPath;
	}

	public void setBgPicPath(String bgPicPath) {
		this.bgPicPath = bgPicPath;
	}

	public String getAdvDesc() {
		return advDesc;
	}

	public void setAdvDesc(String advDesc) {
		this.advDesc = advDesc;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}



}