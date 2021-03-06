package eu.stamp.botsing.controller.event.github.issues;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

import eu.stamp.botsing.controller.event.Action;
import eu.stamp.botsing.controller.event.ActionFactory;
import eu.stamp.botsing.controller.event.InvalidActionException;
import eu.stamp.botsing.controller.event.filter.FilteredActionException;
import eu.stamp.botsing.controller.event.issues.IssuesActionFactoryImpl;
import eu.stamp.botsing.controller.github.GitHubAppController;



@Component 
public class GitHubIssuesActionFactoryImpl extends IssuesActionFactoryImpl implements ActionFactory{

	
	public GitHubIssuesActionFactoryImpl() {
		super ();
	}


	@Override
	public Action getAction(JsonObject jsonObject)
			throws InvalidActionException, FilteredActionException { 
	 
		return super.getAction(jsonObject);
	}

	
	@Autowired
	public void setGitHubIssuesActions (List<GitHubIssuesAction> actions)
	{
		for (GitHubIssuesAction action : actions)
		{
			this.addAction(action);
		}
	}

	@Override
	public String getEventName() {
		return GitHubIssuesAction.EVENT_NAME;
	}

	@Override
	public String getQualifiedEventName() 
	{
		return GitHubAppController.TOOL_NAME+"."+GitHubIssuesAction.EVENT_NAME;
	}
}
