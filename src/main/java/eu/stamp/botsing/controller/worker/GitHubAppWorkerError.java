package eu.stamp.botsing.controller.worker;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;

public class GitHubAppWorkerError  implements GitHubAppWorker{


	private String actionName;
	

	@Override
	public Map<String, String> getPullRequest ()
	{
		HashMap<String, String> response = new HashMap<>();

		response.put("message", "Issue action '"+this.actionName+"' is not supported.");

		return response;
	}

	
	@Override
	public void setParameters(JsonObject jsonObject, String bodyString) {
		this.actionName = jsonObject.get("action").getAsString();
	}


	
	
}
