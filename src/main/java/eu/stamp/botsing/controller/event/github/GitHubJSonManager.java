package eu.stamp.botsing.controller.event.github;

import java.io.IOException;

import com.google.gson.JsonObject;

import eu.stamp.botsing.service.BotsingParameters;
import eu.stamp.botsing.service.IssueParameters;

public abstract class GitHubJSonManager {

	private GitHubServiceClient client;
	
	public GitHubJSonManager(GitHubServiceClient client) {
		this.client = client;
	}
	
	
	protected IssueParameters getIssueParameters (JsonObject jsonObject)
	{
		IssueParameters response = new IssueParameters(jsonObject.get("issue").getAsJsonObject().get("number").getAsString(), 
				jsonObject.get("repository").getAsJsonObject().get("name").getAsString(),jsonObject.get("repository").getAsJsonObject().get("html_url").getAsString(),
				jsonObject.get("repository").getAsJsonObject().get("owner").getAsJsonObject().get("login").getAsString(), 
				jsonObject.get("issue").getAsJsonObject().get("body").getAsString());
		
		return response;
	}
	
	protected BotsingParameters getBotsingParameters (IssueParameters issueParameters) throws IOException
	{
		return this.client.getBotsingParameters(issueParameters);
	}
	



}
