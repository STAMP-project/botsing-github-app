package eu.stamp.botsing.controller;

import com.google.gson.JsonObject;

public class ActionObject {

	private JsonObject jsonObject;
	private String jsonString;
	
	public ActionObject(JsonObject jsonObject, String jsonString) {
		this.jsonObject = jsonObject;
		this.jsonString = jsonString;
	}

	public JsonObject getJsonObject() {
		return jsonObject;
	}

	public String getJsonString() {
		return jsonString;
	}
	
	
	
}
