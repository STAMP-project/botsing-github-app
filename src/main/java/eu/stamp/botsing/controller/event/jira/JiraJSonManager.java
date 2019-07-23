package eu.stamp.botsing.controller.event.jira;

import com.google.gson.JsonObject;

import eu.stamp.botsing.service.BotsingParameters;
import eu.stamp.botsing.service.IssueParameters;

public abstract class JiraJSonManager {

	protected IssueParameters getIssueParameters (JsonObject jsonObject)
	{
		JsonObject issueJsonObject = jsonObject.get("botsingIssueConfig").getAsJsonObject();
		return new IssueParameters(issueJsonObject.get("issueKey").getAsString(), null, null, null, issueJsonObject.get("testFileBody").getAsString());
	}
	
	
	protected BotsingParameters getBotsingParameters (JsonObject jsonObject)
	{
		JsonObject botsingJsonObject = jsonObject.get("botsingProjectConfig").getAsJsonObject();
		return new BotsingParameters (botsingJsonObject.get("groupId").getAsString(),botsingJsonObject.get("artifactId").getAsString(),
				botsingJsonObject.get("version").getAsString(),botsingJsonObject.get("searchBudget").getAsString(),
				botsingJsonObject.get("globalTimeout").getAsString(),botsingJsonObject.get("population").getAsString(),botsingJsonObject.get("packageFilter").getAsString());
				
				
	}
	
	protected String getJiraServiceEndpoint (JsonObject jsonObject)
	{
		return jsonObject.get("jiraCallbackUrl").getAsString();
	}
	
	
}
