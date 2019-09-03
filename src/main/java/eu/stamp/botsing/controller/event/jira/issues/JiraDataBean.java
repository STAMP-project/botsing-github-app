package eu.stamp.botsing.controller.event.jira.issues;

import com.google.gson.JsonObject;

import eu.stamp.botsing.controller.event.actions.NotificationDataBean;
import eu.stamp.botsing.controller.event.jira.JiraEndpointOwner;
import eu.stamp.botsing.service.BotsingParameters;
import eu.stamp.botsing.service.IssueParameters;

public class JiraDataBean implements NotificationDataBean, JiraEndpointOwner{

	private JsonObject jsonObject;
	private IssueParameters issueParameters;
	private BotsingParameters botsingParameters;
	private String serviceEndpoint;

	
	JiraDataBean(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
		this.issueParameters = getIssueParameters(jsonObject);
		this.botsingParameters = getBotsingParameters(jsonObject);
		this.serviceEndpoint = getJiraServiceEndpoint(jsonObject);
	}
	
	
	private IssueParameters getIssueParameters (JsonObject jsonObject)
	{
		JsonObject issueJsonObject = jsonObject.get("botsingIssueConfig").getAsJsonObject();
		return new IssueParameters(issueJsonObject.get("issueKey").getAsString(), null, null, null, issueJsonObject.get("testFileBody").getAsString());
	}
	
	
	private BotsingParameters getBotsingParameters (JsonObject jsonObject)
	{
		JsonObject botsingJsonObject = jsonObject.get("botsingProjectConfig").getAsJsonObject();
		return new BotsingParameters (botsingJsonObject.get("groupId").getAsString(),botsingJsonObject.get("artifactId").getAsString(),
				botsingJsonObject.get("version").getAsString(),botsingJsonObject.get("searchBudget").getAsString(),
				botsingJsonObject.get("globalTimeout").getAsString(),botsingJsonObject.get("population").getAsString(),botsingJsonObject.get("packageFilter").getAsString());
				
				
	}
	
	private String getJiraServiceEndpoint (JsonObject jsonObject)
	{
		return jsonObject.get("jiraCallbackUrl").getAsString();
	}


	@Override
	public JsonObject getJsonObject() {
		return jsonObject;
	}


	@Override
	public IssueParameters getIssueParameters() {
		return issueParameters;
	}


	@Override
	public BotsingParameters getBotsingParameters() {
		return botsingParameters;
	}



	@Override
	public String getServiceEndpoint() {
		return serviceEndpoint;
	}



	
	
	
}
