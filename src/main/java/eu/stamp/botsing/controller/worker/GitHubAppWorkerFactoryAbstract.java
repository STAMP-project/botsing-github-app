package eu.stamp.botsing.controller.worker;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public abstract class GitHubAppWorkerFactoryAbstract 
{

	protected final String 	OPENED ="opened",
							EDIT = "edit";
	


	protected JsonObject getJSonObjectFromBodyString(String body) throws IOException {
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
