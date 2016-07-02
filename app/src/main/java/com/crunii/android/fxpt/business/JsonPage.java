package com.crunii.android.fxpt.business;

import java.util.List;

import org.json.JSONObject;

public class JsonPage {
	List<JSONObject> jsonList;
	int pageIndex;
	boolean hasPrevPage;
	boolean hasNextPage;

	public JsonPage(List<JSONObject> jsonList, int pageIndex, int prePage, int nextPage) {
		this.jsonList = jsonList;
		this.pageIndex = pageIndex;

		if (pageIndex == prePage) {
			hasPrevPage = false;
		} else {
			hasPrevPage = true;
		}
		if (pageIndex == nextPage) {
			hasNextPage = false;
		} else {
			hasNextPage = true;
		}
	}

	public List<JSONObject> getJsonList() {
		return jsonList;
	}

	public void setOrderList(List<JSONObject> jsonList) {
		this.jsonList = jsonList;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public boolean isHasPrevPage() {
		return hasPrevPage;
	}

	public void setHasPrevPage(boolean hasPrevPage) {
		this.hasPrevPage = hasPrevPage;
	}

	public boolean isHasNextPage() {
		return hasNextPage;
	}

	public void setHasNextPage(boolean hasNextPage) {
		this.hasNextPage = hasNextPage;
	}

}
