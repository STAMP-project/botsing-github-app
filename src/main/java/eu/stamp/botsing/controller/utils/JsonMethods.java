package eu.stamp.botsing.controller.utils;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonMethods {
	public static JsonObject getJSonObjectFromBodyString(String body) throws IOException {
		JsonObject jsonObject = null;


		Gson gson = new Gson();
		if (body.startsWith("[")) {

			JsonArray entries = (JsonArray) new JsonParser().parse(body);
			jsonObject = ((JsonObject)entries.get(0));

		} else {
			jsonObject = gson.fromJson(body, JsonElement.class).getAsJsonObject();
		}

		return jsonObject;
	}
}
