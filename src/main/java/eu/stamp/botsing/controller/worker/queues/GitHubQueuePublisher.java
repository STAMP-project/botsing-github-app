package eu.stamp.botsing.controller.worker.queues;

import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import eu.stamp.botsing.controller.event.GitHubAction;
import eu.stamp.botsing.controller.event.GitHubEventFactory;
import eu.stamp.botsing.controller.event.ResponseBean;

public class GitHubQueuePublisher implements GitHubAction {

	private static final Logger log = LoggerFactory.getLogger(GitHubQueuePublisher.class);;
	private MessageProducer messageProducer;
	private Session session;
	private String 	event,
					action;
	
	GitHubQueuePublisher(MessageProducer messageProducer, Session session,String event, String action) 
	{
		this.messageProducer = messageProducer;
		this.session = session;
		this.event = event;
		this.action = action;
	}
	



	@Override
	public ResponseBean execute(JsonObject jsonObject, String bodyString) throws Exception {
		log.debug("Adding the request in queue");
		TextMessage messageObject = this.session.createTextMessage(bodyString);
		messageObject.setStringProperty(GitHubEventFactory.EVENT, this.event);
		messageObject.setStringProperty(GitHubEventFactory.ACTION, this.action);		
		this.messageProducer.send(messageObject);
		return new ResponseBean(202,"Request forwarded to botsing");	
	}

	@Override
	public String actionName() 
	{
		return this.action;
	}

}
