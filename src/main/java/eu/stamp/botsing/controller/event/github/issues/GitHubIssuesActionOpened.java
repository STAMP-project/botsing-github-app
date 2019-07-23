package eu.stamp.botsing.controller.event.github.issues;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

import eu.stamp.botsing.controller.ActionObject;
import eu.stamp.botsing.controller.event.ResponseBean;
import eu.stamp.botsing.controller.event.actions.BotsingExecutor;
import eu.stamp.botsing.controller.event.actions.BotsingExecutor.BotsingResult;
import eu.stamp.botsing.controller.event.filter.ActionFilter;
import eu.stamp.botsing.controller.event.filter.FilteredActionException;
import eu.stamp.botsing.controller.event.github.GitHubJSonManager;
import eu.stamp.botsing.controller.event.github.GitHubServiceClient;
import eu.stamp.botsing.service.BotsingParameters;
import eu.stamp.botsing.service.IssueParameters;

@Configurable
@Component
public class GitHubIssuesActionOpened  extends GitHubJSonManager implements GitHubIssuesAction{

	Logger log = LoggerFactory.getLogger(GitHubIssuesActionOpened.class);

	@Autowired
	@Qualifier ("github.configuration")
	private ActionFilter actionFilter;
	
	private final String 	ACTION_OPENED ="opened",
							QUALIFIED_ACTION_NAME = EVENT_NAME+"."+ACTION_OPENED;
	
	private GitHubServiceClient gitHubServiceClient;
	
	@Autowired
	public GitHubIssuesActionOpened(GitHubServiceClient gitHubServiceClient) {
		super (gitHubServiceClient);
		this.gitHubServiceClient = gitHubServiceClient;
	}
	
	
	@Override
	public ResponseBean execute (ActionObject actionObject) throws Exception
	{
		JsonObject jsonObject = actionObject.getJsonObject();
		IssueParameters issueParameters = getIssueParameters(jsonObject);
		BotsingParameters botsingParameters = getBotsingParameters(issueParameters);
		this.gitHubServiceClient.sendDataString(issueParameters,"Start Botsing on artifact " + botsingParameters.getGroupId() + ":"
		+ botsingParameters.getArtifactId() + ":" + botsingParameters.getVersion() + " to reproduce the stacktrace in the issue.");
		BotsingExecutor botsingExecutor = new BotsingExecutor(botsingParameters, issueParameters.getIssueBody());
		BotsingResult botsingExecutionResult = botsingExecutor.runBotsing();
		
		ResponseBean result = null;
		
		switch (botsingExecutionResult)
		{
		case OK:
			result = new ResponseBean(200,"Botsing executed succesfully with reproduction test.");
			this.gitHubServiceClient.sendDataString(issueParameters,"Botsing generate the following reproduction test.");
			this.gitHubServiceClient.sendDataFile(issueParameters, botsingExecutor.getTestFile());
			this.gitHubServiceClient.sendDataFile(issueParameters, botsingExecutor.getScaffoldingTestFile());
			break;
		case FAIL:
			result = new ResponseBean(500,"Error executing Botsing");
			this.gitHubServiceClient.sendDataString(issueParameters, "Error executing Botsing");
			break;
		case NO_FILES:
			result = new ResponseBean(304, "Botsing did not generate any reproduction test.");
			this.gitHubServiceClient.sendDataString(issueParameters, "Botsing did not generate any reproduction test.");
			break;
		}
		
		return result;
	}

	
	@Override
	public String getActionName() 
	{
		return ACTION_OPENED;
	}

	@Override
	public String getQualifiedActionName() 
	{
		return  QUALIFIED_ACTION_NAME;
	}

	@Override
	public void applyFilter(JsonObject jsonObject) throws FilteredActionException 
	{
		this.actionFilter.apply(GitHubIssuesAction.EVENT_NAME, ACTION_OPENED,jsonObject);	
	}


	
}
