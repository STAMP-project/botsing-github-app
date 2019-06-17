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
import eu.stamp.botsing.controller.event.filter.ActionFilter;
import eu.stamp.botsing.controller.event.filter.FilteredActionException;
import eu.stamp.botsing.service.GitHubService;

@Configurable
@Component
public class GitHubIssuesActionOpened  extends BotsingExecutor implements GitHubIssuesAction{

	Logger log = LoggerFactory.getLogger(GitHubIssuesActionOpened.class);

	@Autowired
	@Qualifier ("configuration")
	private ActionFilter actionFilter;
	
	private final String 	ACTION_OPENED ="opened",
							QUALIFIED_ACTION_NAME = GitHubIssuesActionFactory.EVENT_NAME+"."+ACTION_OPENED;
		
	
	@Override
	public ResponseBean execute (ActionObject actionObject) throws Exception
	{
		JsonObject jsonObject = actionObject.getJsonObject();
		BotsingParameters botsingParameters = extractFromJson(jsonObject);
		return super.executeBotsing(botsingParameters);
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
		this.actionFilter.apply(GitHubIssuesActionFactory.EVENT_NAME, ACTION_OPENED,jsonObject);	
	}


	private BotsingParameters extractFromJson(JsonObject jsonObject) 
	{
		BotsingParameters response = new BotsingParameters();
		response.repositoryName = jsonObject.get("repository").getAsJsonObject().get("name").getAsString();
		response.repositoryURL  = jsonObject.get("repository").getAsJsonObject().get("html_url").getAsString();
		response.repositoryOwner = jsonObject.get("repository").getAsJsonObject().get("owner").getAsJsonObject().get("login").getAsString();
		response.issueNumber = jsonObject.get("issue").getAsJsonObject().get("number").getAsString();
		response.issueBody = jsonObject.get("issue").getAsJsonObject().get("body").getAsString();		
		return response;
	}


	@Autowired
	public void setGitHubService (GitHubService service)
	{
		super.setService(service);
		
	}
	
}
