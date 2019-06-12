package eu.stamp.botsing.controller.github;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.stamp.botsing.controller.ActionManager;
import eu.stamp.botsing.controller.AppController;
import eu.stamp.botsing.controller.event.Action;
import eu.stamp.botsing.controller.event.EventFactory;
import eu.stamp.botsing.controller.event.InvalidEventException;
import eu.stamp.botsing.controller.event.filter.FilteredActionException;
import eu.stamp.botsing.controller.event.github.issues.InvalidActionException;
import eu.stamp.botsing.controller.queues.GitHubQueueSubscriber;
import eu.stamp.botsing.controller.queues.QueueManager;

public abstract class GitHubQueuedAppController extends AppController {

	private Logger log = LoggerFactory.getLogger(GitHubQueuedAppController.class);
	private QueueManager queueManager;
	
	@Override
	protected ActionManager getAction(HttpServletRequest request, String eventType)
			throws InvalidEventException, IOException, InvalidActionException, FilteredActionException 
	{
		ActionManager basicActionManager = super.getAction(request, eventType);
		this.log.debug("Action found: publishing on the queue");
		Action queueAction =  this.queueManager.createPublisher(eventType, basicActionManager.getActionName());
		return new ActionManager(queueAction, basicActionManager);
	}
	
	@Override
	public void setEventFactory(EventFactory eventFactory) throws Exception
	{

		this.queueManager = new QueueManager(new GitHubQueueSubscriber(eventFactory));
		this.queueManager.startSubscriber();
		super.setEventFactory(eventFactory);
	}
	
}
