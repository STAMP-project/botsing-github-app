package eu.stamp.botsing.controller.worker.queues;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

import eu.stamp.botsing.controller.event.GitHubAction;
import eu.stamp.botsing.controller.event.GitHubEventFactory;
import eu.stamp.botsing.controller.utils.JsonMethods;

@Component
public class GitHubQueueSubscriber implements Runnable, ExceptionListener{

	private MessageConsumer messageConsumer;
	Logger log = LoggerFactory.getLogger(GitHubQueueSubscriber.class);
	private boolean started;
	
	@Autowired
	@Qualifier ("basicEventFactory")
	private GitHubEventFactory eventFactory;

	public GitHubQueueSubscriber() {
		this.started = false;
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
					
					processMessage(textMessage.getStringProperty(GitHubEventFactory.EVENT), textMessage.getStringProperty(GitHubEventFactory.ACTION), 
							textMessage.getText());
					
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
	
	private void processMessage (String eventName, String actionName,String message)
	{
		try
		{
			JsonObject jsonObject = JsonMethods.getJSonObjectFromBodyString(message);
			GitHubAction action = this.eventFactory.getActionFactory(eventName).getAction(actionName);
			action.execute(jsonObject,message);
			
		} catch (Exception e)
		{
			this.log.error("Unable to process the message",e);
		}
		
		
	}
	
}
