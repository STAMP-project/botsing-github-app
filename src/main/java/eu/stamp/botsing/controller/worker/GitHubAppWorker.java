package eu.stamp.botsing.controller.worker;

import java.util.Map;

import com.google.gson.JsonObject;

public interface GitHubAppWorker {

	void setParameters (JsonObject jsonObject, String bodyString);
	
	Map<String, String> getPullRequest () throws Exception;

}
