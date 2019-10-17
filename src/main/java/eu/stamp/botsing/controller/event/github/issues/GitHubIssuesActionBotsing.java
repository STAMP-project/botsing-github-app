package eu.stamp.botsing.controller.event.github.issues;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import eu.stamp.botsing.controller.ActionObject;
import eu.stamp.botsing.controller.event.ResponseBean;
import eu.stamp.botsing.controller.event.actions.BotsingResultManager;
import eu.stamp.botsing.controller.event.filter.ActionFilter;
import eu.stamp.botsing.controller.event.filter.FilteredActionException;
import eu.stamp.botsing.controller.event.github.GitHubBotsingExecutor;
import eu.stamp.botsing.controller.event.github.GitHubClientManager;
import eu.stamp.botsing.controller.event.github.GitHubCommentManager;
import eu.stamp.botsing.controller.event.github.GitHubDataManager;
import eu.stamp.botsing.service.BotsingParameters;
import eu.stamp.botsing.service.IssueParameters;

public class GitHubIssuesActionBotsing implements GitHubIssuesAction
{

	private Logger log = LoggerFactory.getLogger(GitHubIssuesActionBotsing.class);
	private ActionFilter actionFilter;
	private final String 	QUALIFIED_ACTION_NAME, ACTION_NAME;
	private GitHubClientManager gitHubClientManager;
	private GitHubCommentManager commentManager;


	public GitHubIssuesActionBotsing(GitHubClientManager gitHubClientManager, String actionName) {
		this.gitHubClientManager = gitHubClientManager;
		this.commentManager = new GitHubCommentManager(gitHubClientManager);
		this.QUALIFIED_ACTION_NAME = EVENT_NAME+"."+actionName;
		this.ACTION_NAME = actionName;
	}
	
	
	@Override
	public ResponseBean execute (ActionObject actionObject) throws Exception
	{
		JsonObject jsonObject = actionObject.getJsonObject();
		GitHubDataManager dataManager = new GitHubDataManager(this.gitHubClientManager, jsonObject);
		IssueParameters issueParameters = dataManager.getIssueParameters();
		BotsingParameters botsingParameters = dataManager.getBotsingParameters();
		this.commentManager.sendComment(issueParameters,"Start Botsing on artifact " + botsingParameters.getGroupId() + ":"
		+ botsingParameters.getArtifactId() + ":" + botsingParameters.getVersion() + " to reproduce the stacktrace in the issue.");
		GitHubBotsingExecutor botsingExecutor = new GitHubBotsingExecutor (botsingParameters, issueParameters.getIssueBody(), this.gitHubClientManager);
		BotsingResultManager botsingResultManager = botsingExecutor.runBotsing();
		this.log.debug("Botsing executed with result "+botsingResultManager.getBotsingResult());
		return botsingResultManager.notifyToServer(dataManager);
		
	}

	protected void setActionFilter (ActionFilter actionFilter)
	{
		this.actionFilter = actionFilter;
	}
	

	@Override
	public String getActionName() {
		return ACTION_NAME;
	}

	@Override
	public String getQualifiedActionName() 
	{
		return  QUALIFIED_ACTION_NAME;
	}

	@Override
	public void applyFilter(JsonObject jsonObject) throws FilteredActionException 
	{
		if (actionFilter != null) this.actionFilter.apply(GitHubIssuesAction.EVENT_NAME, this.ACTION_NAME,jsonObject);	
	}


	
}
