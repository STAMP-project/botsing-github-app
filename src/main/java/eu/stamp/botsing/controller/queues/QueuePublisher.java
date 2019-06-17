package eu.stamp.botsing.controller.queues;

import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import eu.stamp.botsing.controller.ActionObject;
import eu.stamp.botsing.controller.event.Action;
import eu.stamp.botsing.controller.event.EventFactory;
import eu.stamp.botsing.controller.event.ResponseBean;
import eu.stamp.botsing.controller.event.filter.FilteredActionException;

public class QueuePublisher implements Action {

	private static final Logger log = LoggerFactory.getLogger(QueuePublisher.class);;
	private MessageProducer messageProducer;
	private Session session;
	private String 	event,
					action,
					qualifiedName,
					toolName;
	
	
	QueuePublisher(MessageProducer messageProducer, Session session,String toolName,String event, String action) 
	{
		this.messageProducer = messageProducer;
		this.session = session;
		this.event = event;
		this.action = action;
		this.qualifiedName=event+"."+action;
	}
	



	@Override
	public ResponseBean execute(ActionObject actionObject) throws Exception {
		log.debug("Adding the request in queue");
		TextMessage messageObject = this.session.createTextMessage(actionObject.getJsonString());
		messageObject.setStringProperty(EventFactory.EVENT, this.event);
		messageObject.setStringProperty(EventFactory.ACTION, this.action);	
		messageObject.setStringProperty(EventFactory.TOOL, this.toolName);	
		this.messageProducer.send(messageObject);
		return new ResponseBean(202,"Request forwarded to botsing");	
	}

	@Override
	public String getActionName() 
	{
		return this.action;
	}




	@Override
	public String getQualifiedActionName() 
	{
		return this.qualifiedName;
	}




	@Override
	public void applyFilter(JsonObject jsonObject) throws FilteredActionException {
		// TODO Auto-generated method stub
		
	}

}
