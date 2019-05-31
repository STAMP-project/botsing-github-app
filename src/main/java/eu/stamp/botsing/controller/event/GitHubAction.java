package eu.stamp.botsing.controller.event;

import com.google.gson.JsonObject;

public interface GitHubAction {

	String actionName ();
	
	ResponseBean execute (JsonObject jsonObject, String bodyString) throws Exception;

}
