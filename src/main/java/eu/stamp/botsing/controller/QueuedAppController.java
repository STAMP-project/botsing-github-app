package eu.stamp.botsing.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.stamp.botsing.controller.event.Action;
import eu.stamp.botsing.controller.event.InvalidActionException;
import eu.stamp.botsing.controller.event.InvalidEventException;
import eu.stamp.botsing.controller.event.filter.FilteredActionException;
import eu.stamp.botsing.controller.queues.QueueEventFactory;
import eu.stamp.botsing.controller.queues.QueuePublisherGenerator;

public abstract class QueuedAppController extends AppController {

	private Logger log = LoggerFactory.getLogger(QueuedAppController.class);
	private QueuePublisherGenerator queuePublisherGenerator;
	
	@Override
	protected ActionManager getAction(HttpServletRequest request, String toolName,String eventType)
			throws InvalidEventException, IOException, InvalidActionException, FilteredActionException 
	{
		ActionManager basicActionManager = super.getAction(request, toolName,eventType);
		this.log.debug("Action found: publishing on the queue");
		Action queueAction =  this.queuePublisherGenerator.createPublisher(toolName,eventType, basicActionManager.getActionName());
		return new ActionManager(queueAction, basicActionManager);
	}
	

	public void setEventFactory(QueueEventFactory eventFactory) throws Exception
	{
		this.queuePublisherGenerator = new QueuePublisherGenerator();
		eventFactory.startSubscriber();
		super.setEventFactory(eventFactory);
	}
	
}
