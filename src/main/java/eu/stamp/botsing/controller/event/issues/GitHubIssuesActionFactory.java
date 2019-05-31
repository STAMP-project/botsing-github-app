package eu.stamp.botsing.controller.event.issues;

import org.springframework.stereotype.Component;

import eu.stamp.botsing.controller.event.GitHubAction;
import eu.stamp.botsing.controller.event.GitHubActionFactory;


@Component 
public class GitHubIssuesActionFactory extends GitHubIssuesActionFactoryAbstractImpl implements GitHubActionFactory{

	@Override
	public GitHubAction getAction(String actionName) throws InvalidActionException{
		GitHubAction action = this.getActionMap().get(actionName);

		if (action == null) throw new InvalidActionException(EVENT_NAME, actionName);
		else return action;
	}



	@Override
	public String eventName() {
		return EVENT_NAME;
	}










	
}
