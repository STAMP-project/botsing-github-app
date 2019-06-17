package eu.stamp.botsing.controller;

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
import eu.stamp.botsing.controller.queues.QueueManager;
import eu.stamp.botsing.controller.queues.QueueSubscriber;

public abstract class QueuedAppController extends AppController {

	private Logger log = LoggerFactory.getLogger(QueuedAppController.class);
	private QueueManager queueManager;
	
	@Override
	protected ActionManager getAction(HttpServletRequest request, String toolName,String eventType)
			throws InvalidEventException, IOException, InvalidActionException, FilteredActionException 
	{
		ActionManager basicActionManager = super.getAction(request, toolName,eventType);
		this.log.debug("Action found: publishing on the queue");
		Action queueAction =  this.queueManager.createPublisher(toolName,eventType, basicActionManager.getActionName());
		return new ActionManager(queueAction, basicActionManager);
	}
	
	@Override
	public void setEventFactory(EventFactory eventFactory) throws Exception
	{

		this.queueManager = new QueueManager(new QueueSubscriber(eventFactory));
		this.queueManager.startSubscriber();
		super.setEventFactory(eventFactory);
	}
	
}
