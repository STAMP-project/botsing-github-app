package eu.stamp.botsing.controller.worker;

import com.google.gson.JsonObject;

public interface GitHubAppWorker {

	
	ResponseBean getPullRequest (JsonObject jsonObject, String bodyString) throws Exception;

}
