package eu.stamp.botsing.controller.event.issues;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.JsonObject;

import eu.stamp.botsing.controller.event.GitHubAction;
import eu.stamp.botsing.controller.event.GitHubEventFactory;

public abstract class GitHubIssuesActionFactoryAbstractImpl {

	protected final String EVENT_NAME ="issues";
	
	private Map<String, GitHubAction> actionMap;
	
	@Autowired
	private List<GitHubIssuesAction> actions;
	
	@PostConstruct
	public void init ()
	{
		this.actionMap = new HashMap<String, GitHubAction>();
		
		for (GitHubAction action: this.actions)
		{
			this.actionMap.put(action.actionName(), action);
		}
	}
	
	protected void setActions (List<GitHubIssuesAction> actions)
	{
		this.actions = actions;
	}
	
	public GitHubAction getAction (JsonObject jsonObject) throws IOException, InvalidActionException
	{

		String action = jsonObject.get(GitHubEventFactory.ACTION).getAsString();
	
		return this.getAction(action);
		
	}
	
	protected Map<String, GitHubAction> getActionMap ()
	{
		return this.actionMap;
	}
	
	public abstract GitHubAction getAction (String actionName) throws InvalidActionException;
}
