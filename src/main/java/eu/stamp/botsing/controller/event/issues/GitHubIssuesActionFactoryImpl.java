package eu.stamp.botsing.controller.event.issues;

import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

import eu.stamp.botsing.controller.event.GitHubAction;
import eu.stamp.botsing.controller.event.GitHubEventFactory;
import eu.stamp.botsing.controller.event.filter.FilteredActionException;



@Component 
public class GitHubIssuesActionFactoryImpl extends GitHubIssuesActionFactoryAbstractImpl implements GitHubIssuesActionFactory{

	@Override
	public GitHubAction getAction(JsonObject jsonObject)
			throws InvalidActionException, FilteredActionException { 
	 
		String actionName = jsonObject.get(GitHubEventFactory.ACTION).getAsString();
		GitHubAction action = this.getActionMap().get(EVENT_NAME+"."+actionName);
		
		if (action == null) throw new InvalidActionException(EVENT_NAME, actionName);
		else
		{
			action.applyFilter(jsonObject);
			return action;
		}
	}


}
