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
import eu.stamp.botsing.controller.event.actions.BotsingResultManager;
import eu.stamp.botsing.controller.event.filter.ActionFilter;
import eu.stamp.botsing.controller.event.filter.FilteredActionException;
import eu.stamp.botsing.controller.event.github.GitHubBotsingExecutor;
import eu.stamp.botsing.controller.event.github.GitHubClientManager;
import eu.stamp.botsing.controller.event.github.GitHubCommentManager;
import eu.stamp.botsing.controller.event.github.GitHubDataManager;
import eu.stamp.botsing.service.BotsingParameters;
import eu.stamp.botsing.service.IssueParameters;

@Configurable
@Component
public class GitHubIssuesActionOpened implements GitHubIssuesAction{

	Logger log = LoggerFactory.getLogger(GitHubIssuesActionOpened.class);

	@Autowired
	@Qualifier ("github.configuration")
	private ActionFilter actionFilter;
	
	private final String 	ACTION_OPENED ="opened",
							QUALIFIED_ACTION_NAME = EVENT_NAME+"."+ACTION_OPENED;
	
	private GitHubClientManager gitHubClientManager;
	private GitHubCommentManager commentManager;

	
	@Autowired
	public GitHubIssuesActionOpened(GitHubClientManager gitHubClientManager) {
		this.gitHubClientManager = gitHubClientManager;
		this.commentManager = new GitHubCommentManager(gitHubClientManager);
		
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
		BotsingExecutor botsingExecutor = new GitHubBotsingExecutor (botsingParameters, issueParameters.getIssueBody(), this.gitHubClientManager);
		BotsingResultManager botsingResultManager = botsingExecutor.runBotsing();
		this.log.debug("Botsing executed with result "+botsingResultManager.getBotsingResult());
		return botsingResultManager.notifyToServer(dataManager);
		
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
