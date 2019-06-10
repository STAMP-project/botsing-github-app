package eu.stamp.botsing.controller.event.issues;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import eu.stamp.botsing.controller.event.GitHubAction;

public abstract class GitHubIssuesActionFactoryAbstractImpl implements GitHubIssuesActionFactory{

	private Map<String, GitHubAction> actionMap;
	
	@Autowired
	private List<GitHubIssuesAction> actions;
	
	@PostConstruct
	public void init ()
	{
		this.actionMap = new HashMap<String, GitHubAction>();
		
		for (GitHubAction action: this.actions)
		{
			this.actionMap.put(action.getQualifiedActionName(), action);
		}
	}
	
	protected void setActions (List<GitHubIssuesAction> actions)
	{
		this.actions = actions;
	}
	

	protected Map<String, GitHubAction> getActionMap ()
	{
		return this.actionMap;
	}
	
	public String eventName() {
		return EVENT_NAME;
	}


}
