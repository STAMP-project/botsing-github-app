package eu.stamp.botsing.service.bean;

import com.google.gson.JsonObject;

import eu.stamp.botsing.controller.event.actions.NotificationDataBean;
import eu.stamp.botsing.controller.event.jira.JiraEndpointOwner;
import eu.stamp.botsing.service.BotsingParameters;
import eu.stamp.botsing.service.IssueParameters;

public class TestJiraDataBean implements NotificationDataBean, JiraEndpointOwner {

	private final String ADDRESS = "http://localhost:8080/jira/rest/botsing-config/1.0/reproduction/PL-23/add";
	private final String TEST_ID = "testID";
	
	@Override
	public JsonObject getJsonObject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IssueParameters getIssueParameters() {
		IssueParameters issueParameters = new IssueParameters(TEST_ID, null, null, null, null);
		return issueParameters;
	}

	@Override
	public BotsingParameters getBotsingParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServiceEndpoint() {

		return ADDRESS;
	}

}
