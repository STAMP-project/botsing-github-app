package eu.stamp.botsing.controller.worker.queues;

import java.util.List;

import eu.stamp.botsing.controller.event.GitHubAction;
import eu.stamp.botsing.controller.event.GitHubActionFactory;
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
	public GitHubAction getAction (String actionName) throws InvalidActionException
	{

		
		if ( this.getActionMap().get(actionName)!= null)
		{
			// If there is a basic action manager associated to this action
			// publish the data
			return queueManager.createPublisher(EVENT_NAME, actionName);
		}
		else
		{
			throw new InvalidActionException(EVENT_NAME,actionName);
		}

		
	}


	@Override
	public String eventName() {
		return EVENT_NAME;
	}



}
