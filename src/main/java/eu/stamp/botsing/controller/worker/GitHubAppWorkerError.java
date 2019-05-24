package eu.stamp.botsing.controller.worker;

import com.google.gson.JsonObject;

public class GitHubAppWorkerError  implements GitHubAppWorker{

	

	@Override
	public ResponseBean getPullRequest (JsonObject jsonObject, String bodyString)
	{
		return new ResponseBean(400, "Issue action '"+jsonObject.get("action").getAsString()+"' is not supported.");
	}

	



	
	
}
