package eu.stamp.botsing.controller.worker.queues;

import java.util.List;

import com.google.gson.JsonObject;

import eu.stamp.botsing.controller.event.GitHubAction;
import eu.stamp.botsing.controller.event.GitHubActionFactory;
import eu.stamp.botsing.controller.event.GitHubEventFactory;
import eu.stamp.botsing.controller.event.filter.FilteredActionException;
import eu.stamp.botsing.controller.event.issues.GitHubIssuesAction;
import eu.stamp.botsing.controller.event.issues.GitHubIssuesActionFactoryAbstractImpl;
import eu.stamp.botsing.controller.event.issues.InvalidActionException;


public class GitHubIssuesQueuedActionFactory extends GitHubIssuesActionFactoryAbstractImpl implements GitHubActionFactory {

	
	private QueueManager queueManager;

	public GitHubIssuesQueuedActionFactory(QueueManager queueManager, List<GitHubIssuesAction> actions) throws Exception {
		this.queueManager = queueManager;
		this.queueManager.startSubscriber();

		setActions(actions);
	}

	
	@Override
	public GitHubAction getAction (JsonObject jsonObject) throws InvalidActionException, FilteredActionException
	{ 
		String actionName = jsonObject.get(GitHubEventFactory.ACTION).getAsString();
		GitHubAction action = this.getActionMap().get(EVENT_NAME+"."+actionName);
		
		if (action!= null)
		{

			action.applyFilter(jsonObject);
			// If there is a basic action manager associated to this action
			// publish the data
			
			return queueManager.createPublisher(EVENT_NAME, actionName);
		}
		else
		{
			throw new InvalidActionException(EVENT_NAME,actionName);
		}		
	}
}
