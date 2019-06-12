package eu.stamp.botsing.controller.queues;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import eu.stamp.botsing.controller.ActionObject;
import eu.stamp.botsing.controller.event.Action;
import eu.stamp.botsing.controller.event.EventFactory;
import eu.stamp.botsing.controller.utils.JsonMethods;


public class GitHubQueueSubscriber implements Runnable, ExceptionListener{

	private MessageConsumer messageConsumer;
	private Logger log = LoggerFactory.getLogger(GitHubQueueSubscriber.class);
	private boolean started;
	

	private EventFactory eventFactory;

	public GitHubQueueSubscriber(EventFactory eventFactory) {
		this.started = false;
		this.eventFactory = eventFactory;
	}


	@Override
	public void onException(JMSException exception) {
		this.log.error("Fatal exception: "+exception.getMessage(),exception);
		
	}

	void setMessageConsumer (MessageConsumer messageConsumer)
	{
		this.messageConsumer = messageConsumer;
	}
	
	void stopProcessor () throws Exception
	{
		this.started = false;
		this.messageConsumer.close();
	}
	
	@Override
	public void run() {
		
		this.started = true;

		try
		{

			while (this.started)
			{
				Message message = this.messageConsumer.receive();

				if (message   instanceof TextMessage) {
					
					TextMessage textMessage = (TextMessage) message;
					
					processMessage(textMessage.getStringProperty(EventFactory.EVENT), textMessage.getText());
					
				}
				else
				{
					this.log.warn("Received unexpected message type "+message.getClass());
				}
				
			}


			
			
		} catch (Exception e)
		{
			this.log.error("Fatal error inside the cycle",e);
			this.started = false;
		}

	}
	
	private void processMessage (String eventName,String message)
	{
		try
		{
			JsonObject jsonObject = JsonMethods.getJSonObjectFromBodyString(message);
			Action action = this.eventFactory.getActionFactory(eventName).getAction(jsonObject);
			action.execute(new ActionObject(jsonObject, message));
			
		} catch (Exception e)
		{
			this.log.error("Unable to process the message",e);
		}
		
		
	}
	
	
}
