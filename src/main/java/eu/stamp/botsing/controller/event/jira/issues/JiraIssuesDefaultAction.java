package eu.stamp.botsing.controller.event.jira.issues;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

import eu.stamp.botsing.controller.ActionObject;
import eu.stamp.botsing.controller.event.ResponseBean;
import eu.stamp.botsing.controller.event.actions.BotsingExecutor;
import eu.stamp.botsing.controller.event.actions.BotsingExecutor.BotsingResult;
import eu.stamp.botsing.controller.event.filter.FilteredActionException;
import eu.stamp.botsing.controller.event.jira.JiraJSonManager;
import eu.stamp.botsing.controller.event.jira.JiraServiceClient;
import eu.stamp.botsing.service.BotsingParameters;
import eu.stamp.botsing.service.IssueParameters;

@Configurable
@Component
public class JiraIssuesDefaultAction  extends JiraJSonManager implements JiraIssuesAction{

	Logger log = LoggerFactory.getLogger(JiraIssuesDefaultAction.class);



	private final String 	DEFAULT_ACTION ="default",
							QUALIFIED_ACTION_NAME = EVENT_NAME+"."+DEFAULT_ACTION;


	public JiraIssuesDefaultAction() {

	}

	@Override
	public ResponseBean execute (ActionObject actionObject) throws Exception
	{
		JsonObject jsonObject = actionObject.getJsonObject();
		BotsingParameters botsingParameters = getBotsingParameters(jsonObject);
		IssueParameters issueParameters = getIssueParameters(jsonObject);
		BotsingExecutor botsingExecutor = new BotsingExecutor (botsingParameters, issueParameters.getIssueBody());
		BotsingResult botsingExecutorResponse = botsingExecutor.runBotsing();

		ResponseBean result = null;

		switch (botsingExecutorResponse)
		{
		case OK:
			JiraServiceClient client = new JiraServiceClient(this.getJiraServiceEndpoint(jsonObject));
			// TODO find a better way to get the user credential to access to jira services
			boolean clientResponse = client.sendData("user", "password", issueParameters.getIssueNumber(), botsingExecutor.getTestFile(), botsingExecutor.getScaffoldingTestFile());

			result = clientResponse ?  new ResponseBean(200,"Botsing executed succesfully with reproduction test."):
				new ResponseBean(502,"Botsing executed succesfully but invalid response from jira");

			break;
		case FAIL:
			result = new ResponseBean(500,"Error executing Botsing");
			break;
		case NO_FILES:
			result = new ResponseBean(304, "Botsing did not generate any reproduction test.");
			break;
		}

		return result;
	}



	@Override
	public String getActionName()
	{
		return DEFAULT_ACTION;
	}

	@Override
	public String getQualifiedActionName()
	{
		return  QUALIFIED_ACTION_NAME;
	}

	@Override
	public void applyFilter(JsonObject jsonObject) throws FilteredActionException
	{

	}




}
